/**
 * Copyright (C) 2008-2012 Electric Power Research Institute, Inc.
 * Copyright (C) 2009-2012 Richard Lincoln
 * All rights reserved.
 */
package com.ncond.dss.meter;

import java.io.BufferedWriter;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.commons.math.complex.Complex;

import com.ncond.dss.common.CktElement;
import com.ncond.dss.common.DSS;
import com.ncond.dss.common.DSSClass;
import com.ncond.dss.common.DSSClassDefs;
import com.ncond.dss.common.SolutionObj;
import com.ncond.dss.common.Util;
import com.ncond.dss.conversion.PCElement;
import com.ncond.dss.delivery.TransformerObj;
import com.ncond.dss.shared.ComplexUtil;
import com.ncond.dss.shared.MathUtil;

/**
 * A monitor is a circuit element that is connected to a terminal of another
 * circuit element.  It records the voltages and currents at that terminal as
 * a function of time and can report those values upon demand.
 *
 * A Monitor is defined by a New commands:
 *
 * New Type=Monitor Name=myname Element=elemname Terminal=[1,2,...] Buffer=clear|save
 *
 * Upon creation, the monitor buffer is established.  There is a file associated
 * with the buffer.  It is named "Mon_elemnameN.mon"  where N is the terminal no.
 * The file is truncated to zero at creation or buffer clearing.
 *
 * The Monitor keeps results in the in-memory buffer until it is filled.  Then it
 * appends the buffer to the associated file and resets the in-memory buffer.
 *
 * For buffer=save, the present in-memory buffer is appended to the disk file so
 * that it is saved for later reference.
 *
 * The Monitor is a passive device that takes a sample whenever its "TakeSample"
 * method is invoked.  The SampleAll method of the Monitor ckt element class will
 * force all monitors elements to take a sample.  If the present time (for the most
 * recent solution is greater than the last time entered in to the monitor buffer,
 * the sample is appended to the buffer.  Otherwise, it replaces the last entry.
 *
 * Monitor Files are simple binary files of doubles.  The first record
 * contains the number of conductors per terminal (NCond). (always use 'round' function
 * when converting this to an integer). Then subsequent records consist of time and
 * voltage and current samples for each terminal (all complex doubles) in the order
 * shown below:
 *
 * <NCond>
 *
 *          <--- All voltages first ---------------->|<--- All currents ----->|
 * <hour 1> <sec 1> <V1.re>  <V1.im>  <V2.re>  <V2.im>  .... <I1.re>  <I1.im> ...
 * <hour 2> <sec 1> <V1.re>  <V1.im>  <V2.re>  <V2.im>  .... <I1.re>  <I1.im> ...
 * <hour 3> <sec 1> <V1.re>  <V1.im>  <V2.re>  <V2.im>  .... <I1.re>  <I1.im> ...
 *
 * The time values will not necessarily be in a uniform time step;  they will
 * be at times samples or solutions were taken.  This could vary from several
 * hours down to a few milliseconds.
 *
 * The monitor ID can be determined from the file name.  Thus, these values can
 * be post-processed at any later time, provided that the monitors are not reset.
 *
 * Modes are:
 *    0: Standard mode - V and I,each phase, Mag and Angle
 *    1: Power each phase, complex (kw and kvars)
 *    2: Transformer Tap
 *    3: State Variables
 *  +16: Sequence components: V012, I012
 *  +32: Magnitude Only
 *  +64: Pos Seq only or average of phases
 *
 */
@SuppressWarnings("unused")
public class MonitorObj extends MeterElement {

	private static StringBuffer strBuffer = new StringBuffer();

	private int bufferSize;
	private int hour;

	/** Last time entered in the buffer */
	private double sec;
	private float[] monBuffer;

	/** Point to present (last) element in buffer must be incremented to add */
	private int bufPtr;

	private Complex[] currentBuffer;
	private Complex[] voltageBuffer;

	private int numStateVars;
	private double[] stateBuffer;

	private boolean includeResidual;
	private boolean VIpolar;
	private boolean Ppolar;

	private int fileSignature;
	private int fileVersion;

	/** Name of file for catching buffer overflow */
	private String bufferFile;

	private boolean isFileOpen;
	private boolean validMonitor;

	protected int mode;
	protected CharArrayWriter monitorStream;
	protected int sampleCount;  // this is the number of samples taken

	public MonitorObj(DSSClass parClass, String monitorName) {
		super(parClass);
		setName(monitorName.toLowerCase());

		setNumPhases(3);  // directly set conds and phases
		nConds = 3;
		setNumTerms(1);   // this forces allocation of terminals and conductors in base class

		/** Current buffer has to be big enough to hold all terminals */
		currentBuffer = null;
		voltageBuffer = null;
		stateBuffer = null;

		baseFrequency = 60.0;
		hour = 0;
		sec = 0.0;

		mode = 0;  // standard mode: V & I, complex values

		bufferSize = 1024;  // makes a 4K buffer
		monBuffer = new float[bufferSize];
		bufPtr = 0;

		// default to first circuit element (source)
		elementName = ((CktElement) DSS.activeCircuit.getCktElements().get(0)).getName();
		meteredElement = null;
		bufferFile = "";

		monitorStream = new CharArrayWriter();  // create memory stream

		isFileOpen = false;
		meteredTerminalIdx = 0;
		includeResidual = false;
		VIpolar = true;
		Ppolar = true;
		fileSignature = 43756;
		fileVersion = 1;
		sampleCount = 0;

		objType = parClass.getClassType();  // MON_ELEMENT;

		initPropertyValues(0);
	}

	@Override
	public void recalcElementData() {
		validMonitor = false;

		int idx = Util.getCktElementIndex(elementName);

		if (idx >= 0) {  // monitored element must already exist
			meteredElement = (CktElement) DSS.activeCircuit.getCktElements().get(idx);

			switch (mode & Monitor.MODEMASK) {
			case 2:  // must be transformer
				if ((meteredElement.getObjType() & DSSClassDefs.CLASSMASK) != DSSClassDefs.XFMR_ELEMENT) {
					DSS.doSimpleMsg(meteredElement.getName() + " is not a transformer!", 663);
					return;
				}
				break;
			case 3:  // must be PC element
				if ((meteredElement.getObjType() & DSSClassDefs.BASECLASSMASK) != DSSClassDefs.PC_ELEMENT) {
					DSS.doSimpleMsg(meteredElement.getName() + " must be a power conversion element!", 664);
					return;
				}
				break;
			}

			if (meteredTerminalIdx >= meteredElement.getNumTerms()) {
				DSS.doErrorMsg("Monitor: \"" + getName() + "\"",
					"Terminal no. \"" + (meteredTerminalIdx+1) + "\" does not exist.",
					"Respecify terminal no.", 665);
			} else {
				setNumPhases(meteredElement.getNumPhases());
				setNumConds(meteredElement.getNumConds());

				// sets name of i-th terminal's connected bus in monitor's bus list
				// this value will be used to set the NodeRef array (see takeSample)
				setBus(0, meteredElement.getBus(meteredTerminalIdx));

				// make a name for the buffer file
				bufferFile = DSS.circuitName_ + "Mon_" + getName() + ".mon";

				/* Allocate buffers */
				switch (mode & Monitor.MODEMASK) {
				case 3:
					numStateVars = ((PCElement) meteredElement).numVariables();
					stateBuffer = Util.resizeArray(stateBuffer, numStateVars);
					break;
				default:
					currentBuffer = Util.resizeArray(currentBuffer, meteredElement.getYOrder());
					voltageBuffer = Util.resizeArray(voltageBuffer, meteredElement.getNumConds());
					break;
				}

				clearMonitorStream();

				validMonitor = true;
			}
		} else {
			meteredElement = null;  // element not found
			DSS.doErrorMsg("Monitor: \"" + getName() + "\"",
				"Circuit element \"" + elementName + "\" not found.",
				" Element must be defined previously.", 666);
		}
	}

	/**
	 * Make a positive sequence model.
	 */
	@Override
	public void makePosSequence() {
		if (meteredElement != null) {
			setBus(0, meteredElement.getBus(meteredTerminalIdx));
			setNumPhases(meteredElement.getNumPhases());
			setNumConds(meteredElement.getNumConds());
			switch (mode & Monitor.MODEMASK) {
			case 3:
				numStateVars = ((PCElement) meteredElement).numVariables();
				stateBuffer = (double[]) Util.resizeArray(stateBuffer, numStateVars);
				break;
			default:
				currentBuffer = Util.resizeArray(currentBuffer, meteredElement.getYOrder());
				voltageBuffer = Util.resizeArray(voltageBuffer, meteredElement.getNumConds());
				break;
			}
			clearMonitorStream();
			validMonitor = true;
		}
		super.makePosSequence();
	}

	@Override
	public void calcYPrim() {
		/* A monitor is a zero current source; Yprim is always zero. */
	}

	public void clearMonitorStream() {
		int i;
		int iMax;
		int iMin;
		boolean isPosSeq;
		boolean isPower;
		String nameOfState;
		int numVI;
		//int RecordSize;
		//int strPtr;


		try {
			//if (!isFileOpen) openMonitorFile();  // always opens for appending
			//buffer.seek(0);  // back to BOF
			//buffer.truncate();  // get rid of anything remaining

			monitorStream.reset();
			sampleCount = 0;
			isPosSeq = false;
			strBuffer.delete(0, strBuffer.length());  /* clear buffer */
			//strPtr = 0;  // init string
			if (DSS.activeCircuit.getSolution().isHarmonicModel()) {
				strBuffer.append("Freq, Harmonic, ");
			} else {
				strBuffer.append("hour, t(sec), ");
			}

			switch (mode & Monitor.MODEMASK) {
			case 2:
				//recordSize = 1;  // transformer taps
				strBuffer.append("Tap (pu)");
				break;
			case 3:
				//recordSize = numStateVars;   // state variables
				for (i = 0; i < numStateVars; i++) {
					nameOfState = String.valueOf( ((PCElement) meteredElement).getVariable(i) );
					strBuffer.append(nameOfState);
					if (i < numStateVars)
						strBuffer.append(", ");
				}
				break;
			default:
				/* Compute recordSize */
				// use same logic as in takeSample method

				if (((mode & Monitor.SEQUENCEMASK) > 0) && nPhases == 3) {  // convert to symmetrical components
					isPosSeq = true;
					numVI = 3;
				} else {
					numVI = nConds;
				}
				// convert voltage buffer to power kW, kVAr
				if ((mode & Monitor.MODEMASK) == 1) {
					isPower = true;
				} else {
					isPower = false;
				}

				switch (mode & (Monitor.MAGNITUDEMASK + Monitor.POSSEQONLYMASK)) {
				case 32:  // save magnitudes only
					//recordSize = 0;
					//for (i = 0; i < numVI; i++) recordSize += 1;
					if (!isPower) {
						//for (i = 0; i < numVI; i++) recordSize += 1;
						//if (includeResidual) recordSize += 2;
						for (i = 0; i < numVI; i++) {
							strBuffer.append(String.format("|V|%d (volts)", i));
							strBuffer.append(", ");
						}
						if (includeResidual) {
							strBuffer.append("|VN| (volts)");
							strBuffer.append(", ");
						}
						for (i = 0; i < numVI; i++) {
							strBuffer.append("|I|"+String.valueOf(i)+" (amps)");
							if (i < numVI)
								strBuffer.append(", ");
						}
						if (includeResidual)
							strBuffer.append(",|IN| (volts)");
					} else {
						for (i = 0; i < numVI; i++) {
							strBuffer.append("S"+String.valueOf(i)+" (kVA)");
							if (i < numVI)
								strBuffer.append(", ");
						}
					}
					break;
				case 64:  // save pos seq or total of all phases or total power (Complex)
					//recordSize = 2;
					if (!isPower) {
						//recordSize = recordSize + 2;
						if (VIpolar) {
							strBuffer.append("V1, V1ang, I1, I1ang");
						} else {
							strBuffer.append("V1.re, V1.im, I1.re, I1.im");
						}
					} else {
						if (Ppolar) {
							strBuffer.append("S1 (kVA), Ang ");
						} else {
							strBuffer.append("P1 (kW), Q1 (kvar)");
						}
					}
					break;
				case 96:  // save pos seq or aver magnitude of all phases of total kVA (magnitude)
					//recordSize = 1;
					if (!isPower) {
						//recordSize = recordSize + 1;
						strBuffer.append("V, I ");
					} else {
						if (Ppolar) {
							strBuffer.append("S1 (kVA)");
						} else {
							strBuffer.append("P1 (kW)");
						}
					}
					break;

				default:  // save V and I in mag and angle or complex kW, kVAr
					//recordSize = numVI * 2;
					if (!isPower) {
						if (isPosSeq) {
							iMin = 0;
							iMax = numVI - 1;
						} else {
							iMin = 1;
							iMax = numVI;
						}
						//recordSize = recordSize + numVI * 2;
						//if (includeResidual) recordSize += 4;
						for (i = iMin; i < iMax; i++) {
							if (VIpolar) {
								strBuffer.append("V"+String.valueOf(i)+", VAngle"+String.valueOf(i));
							} else {
								strBuffer.append("V"+String.valueOf(i)+".re, V"+String.valueOf(i)+".im");
							}
							strBuffer.append(", ");
						}
						if (includeResidual) {
							if (VIpolar) {
								strBuffer.append("VN, VNAngle");
							} else {
								strBuffer.append("VN.re, VN.im");
							}
							strBuffer.append(", ");
						}
						for (i = iMin; i < iMax; i++) {
							if (VIpolar) {
								strBuffer.append("I"+String.valueOf(i)+", IAngle"+String.valueOf(i));
							} else {
								strBuffer.append("I"+String.valueOf(i)+".re, I"+String.valueOf(i)+".im");
							}
							if (i < numVI)
								strBuffer.append(", ");
						}
						if (includeResidual) {
							if (VIpolar) {
								strBuffer.append(", IN, INAngle");
							} else {
								strBuffer.append(", IN.re, IN.im");
							}
						}
					} else {
						if (isPosSeq) {
							iMin = 0;
							iMax = numVI - 1;
						} else {
							iMin = 1;
							iMax = numVI;
						}
						for (i = iMin; i < iMax; i++) {
							if (Ppolar) {
								strBuffer.append("S"+String.valueOf(i)+" (kVA), Ang"+String.valueOf(i));
							} else {
								strBuffer.append("P"+String.valueOf(i)+" (kW), Q"+String.valueOf(i)+" (kvar)");
							}
							if (i < numVI)
								strBuffer.append(", ");
						}
						break;
					}
				}
				break;
			}  /* switch */

			// recordSize is the number of singles in the sample (after the hour and sec)

			// write header to monitor stream
			// write id so we know it is a DSS monitor file and which version in case we
			// change it down the road

			//MonitorStream.write(FileSignature, Sizeof(FileSignature) );
			//MonitorStream.write(FileVersion,   Sizeof(FileVersion) );
			//MonitorStream.write(RecordSize,    Sizeof(RecordSize) );
			//MonitorStream.write(Mode,          Sizeof(Mode)       );
			monitorStream.write(strBuffer.toString().toCharArray());

			/*
			 * So the file now looks like:
			 *   FileSignature (4 bytes)    32-bit Integers
			 *   FileVersion   (4)
			 *   RecordSize    (4)
			 *   Mode          (4)
			 *   String        (256)
			 *
			 *   hr   (4)       all singles
			 *   Sec  (4)
			 *   Sample  (4*RecordSize)
			 *   ...
			 */

			//closeMonitorFile();  // ready now for appending

		} catch (Exception e) {
			DSS.doErrorMsg("Cannot open Monitor file.", e.getMessage(), "Monitor: \"" + getName() + "\"", 670);
		}
	}

	public void openMonitorStream() {
		if (!isFileOpen) {
			//monitorStream.seek(-1);  // positioned at end of stream
			isFileOpen = true;
		}
	}

	public void closeMonitorStream() {
		try {
			if (isFileOpen) {  // only close open files
				//monitorStream.seek(0);  // just move stream position to the beginning
				isFileOpen = false;
			}
		} catch (Exception e) {
			DSS.doErrorMsg("Cannot open monitor stream.",
				e.getMessage(),
				"Monitor: \"" + getName() + "\"", 671);
		}
	}

	/**
	 * Saves present buffer to monitor file.
	 */
	public void save() {
		if (!isFileOpen) openMonitorStream();  // position to end of stream

		/* Write present monitor buffer to monitor stream */
		//monitorStream.write(monBuffer);

		bufPtr = 0; // reset buffer for next
	}

	public void resetIt() {
		bufPtr = 0;
		clearMonitorStream();
	}

	@Override
	public void takeSample() {
		double dHour;
		double dSum;
		int i;
		boolean isPower;
		boolean isSequence;
		int numVI;
		int offset;
		Complex residualCurr = null;
		Complex residualVolt = null;
		Complex sum;
		Complex[] V012 = new Complex[3];
		Complex[] I012 = new Complex[3];

		SolutionObj sol = DSS.activeCircuit.getSolution();

		if (!(validMonitor && isEnabled())) return;

		sampleCount += 1;

		hour = sol.getIntHour();
		sec = sol.getDynaVars().t;

		offset = meteredTerminalIdx * meteredElement.getNumConds();

		// save time unless harmonics mode and then save frequency and harmonic
		if (sol.isHarmonicModel()) {
			addDblsToBuffer(sol.getFrequency(), 1);  // put freq in hour slot as a double
			addDblsToBuffer(sol.getHarmonic(), 1);  // put harmonic in time slot in buffer
		} else {
			dHour = hour;  // convert to double
			addDblsToBuffer(dHour, 1);  // put hours in buffer as a double
			addDblsToBuffer(sec, 1);  // stick time in sec in buffer
		}

		switch (mode & Monitor.MODEMASK) {
		case 0:  // voltage, current, powers
			//meteredElement.getCurrents(currentBuffer);
			// to save some time, call computeITerminal
			meteredElement.computeITerminal();  // only does calc if needed

			for (i = 0; i < meteredElement.getYOrder(); i++)
				currentBuffer[i] = meteredElement.getITerminal(i);

			try {
				for (i = 0; i < nConds; i++) {
					// nodeRef is set by the main circuit object
					// it is the index of the terminal into the system node list
					voltageBuffer[i] = sol.getNodeV(nodeRef[i]);
				}
			} catch (Exception e) {
				DSS.doSimpleMsg(e.getMessage() + DSS.CRLF + "NodeRef is invalid. " +
					"Try solving a snapshot or direct before solving in a mode that takes a monitor sample.", 672);
			}
			break;

		case 1:
			//meteredElement.getCurrents(currentBuffer);
			// to save some time, call computeITerminal
			meteredElement.computeITerminal();  // only does calc if needed

			for (i = 0; i < meteredElement.getYOrder(); i++)
				currentBuffer[i] = meteredElement.getITerminal(i);

			try {
				for (i = 0; i < nConds; i++) {
					// nodeRef is set by the main circuit object
					// it is the index of the terminal into the system node list
					voltageBuffer[i] = sol.getNodeV(nodeRef[i]);
				}
			} catch (Exception e) {
				DSS.doSimpleMsg(e.getMessage() + DSS.CRLF + "NodeRef is invalid. " +
					"Try solving a snapshot or direct before solving in a mode that takes a monitor sample.", 672);
			}
			break;

		case 2:  // monitor transformer tap position
			addDblToBuffer(((TransformerObj) meteredElement).getPresentTap(meteredTerminalIdx));
			return;  // done with this mode now

		case 3:  // pick up device state variables
			((PCElement) meteredElement).getAllVariables(stateBuffer);
			addDblsToBuffer(stateBuffer, numStateVars);
			return;  // done with this mode now
		default:
			return;  // ignore invalid mask
		}

		if (((mode & Monitor.SEQUENCEMASK) > 0) && nPhases == 3) {
			// convert to symmetrical components
			MathUtil.phase2SymComp(voltageBuffer, V012);
			MathUtil.phase2SymComp(currentBuffer[offset + 1], I012);

			numVI = 3;
			isSequence = true;

			// replace voltage and current buffer with sequence quantities
			for (i = 0; i < 3; i++) voltageBuffer[i] = V012[i];
			for (i = 0; i < 3; i++) currentBuffer[offset + i] = I012[i];
		} else {
			numVI = nConds;
			isSequence = false;
		}

		isPower = false;

		switch (mode & Monitor.MODEMASK) {
		case 0:  // convert to mag, angle and compute residual if required
			isPower = false;
			if (includeResidual) {
				if (VIpolar) {
					residualVolt = Util.residualPolar(voltageBuffer[0], nPhases);
					residualCurr = Util.residualPolar(currentBuffer[offset + 1], nPhases);  // TODO Check zero based indexing
				} else {
					residualVolt = Util.residual(voltageBuffer[0], nPhases);
					residualCurr = Util.residual(currentBuffer[offset + 1], nPhases);  // TODO Check zero based indexing
				}
			}
			if (VIpolar) {
				Util.convertComplexArrayToPolar(voltageBuffer, numVI);
				Util.convertComplexArrayToPolar(currentBuffer, numVI * meteredElement.getNumTerms());  // get all of current buffer
			}
			break;

		case 1:  // convert voltage buffer to power kW, kVAr or mag/angle
			MathUtil.calcKPowers(voltageBuffer, voltageBuffer, currentBuffer[offset + 1], numVI);
			if (isSequence || DSS.activeCircuit.isPositiveSequence())
				Util.mulArray(voltageBuffer, 3.0, numVI);  // convert to total power
			if (Ppolar)
				Util.convertComplexArrayToPolar(voltageBuffer, numVI);
			isPower = true;
			break;
		}

		// now check to see what to write to disk
		switch (mode & (Monitor.MAGNITUDEMASK + Monitor.POSSEQONLYMASK)) {
		case 32:  // save magnitudes only
			for (i = 0; i < numVI; i++)
				addDblToBuffer(voltageBuffer[i].getReal() /*VoltageBuffer[i].abs()*/);
			if (includeResidual)
				addDblToBuffer(residualVolt.getReal());
			if (!isPower) {
				for (i = 0; i < numVI; i++)
					addDblToBuffer(currentBuffer[offset + i].getReal());
				if (includeResidual)
					addDblToBuffer(residualCurr.getReal());
			}
			break;

		case 64:  // save pos seq or avg of all phases or total power (Complex)
			if (isSequence) {
				addDblsToBuffer(voltageBuffer[1].getReal(), 2);
				if (!isPower)
					addDblsToBuffer(currentBuffer[offset + 2].getReal(), 2);
			} else {
				if (!isPower) {
					sum = Complex.ZERO;
					for (i = 0; i < nPhases; i++)
						sum = sum.add(voltageBuffer[i]);
					addDblsToBuffer(sum.getReal(), 2);
				} else {
					// average the phase magnitudes and sum angles
					sum = Complex.ZERO;
					for (i = 0; i < nPhases; i++)
						sum = sum.add(voltageBuffer[i]);
					sum = new Complex(sum.getReal() / nPhases, sum.getImaginary());
					addDblsToBuffer(sum.getReal(), 2);
					sum = Complex.ZERO;
					for (i = 0; i < nPhases; i++)
						sum = sum.add(currentBuffer[i]);
					sum = new Complex(sum.getReal() / nPhases, sum.getImaginary());
					addDblsToBuffer(sum.getReal(), 2);
				}
			}
			break;

		case 96:  // save pos seq or aver magnitude of all phases of total kVA (magnitude)
			if (isSequence) {
				addDblToBuffer(voltageBuffer[1].getReal());  // first double is magnitude
				if (!isPower)
					addDblToBuffer(currentBuffer[offset + 2].getReal());
			} else {
				dSum = 0.0;
				for (i = 0; i < nPhases; i++)
					dSum = dSum + voltageBuffer[i].getReal();
				if (!isPower)
					dSum = dSum / nPhases;
				addDblToBuffer(dSum);
				if (!isPower) {
					dSum = 0.0;
					for (i = 0; i < nPhases; i++)
						dSum = dSum + currentBuffer[offset + i].getReal();
					dSum = dSum / nPhases;
					addDblToBuffer(dSum);
				}
			}
			break;

		default:
			addDblsToBuffer(voltageBuffer[1].getReal(), numVI * 2);
			if (!isPower) {
				if (includeResidual)
					addDblsToBuffer(ComplexUtil.asArray( residualVolt ), 2);
				addDblsToBuffer(currentBuffer[offset + 1].getReal(), numVI * 2);
				if (includeResidual)
					addDblsToBuffer(ComplexUtil.asArray( residualCurr ), 2);
			}
			break;
		}
	}

	private void addDblsToBuffer(double dbl, int nDoubles) {
		addDblToBuffer(dbl);
	}

	private void addDblsToBuffer(double[] dbl, int nDoubles) {
		for (int i = 0; i < nDoubles; i++)
			addDblToBuffer(dbl[i]);
	}

	private void addDblToBuffer(double dbl) {
		// first check to see if there's enough room
		// if not, save to monitorStream first
		if (bufPtr == bufferSize - 1)
			save();
		bufPtr += 1;
		monBuffer[bufPtr] = (float) dbl;
	}

	public void translateToCSV(boolean show) {
		String csvName;
		File f;
		FileWriter fw;
		BufferedWriter bw;
		int i;
//		int fSignature;
//		int fVersion;
//		float hr;
//		int mode;
//		int nRead = 0;
//		int pStr;
//		int recordBytes;
		int recordSize = 0;
		float s = 0;
		float[] sngBuffer = new float[100];


		save();  // save present buffer
		closeMonitorStream();  // position at beginning

		csvName = getFileName();

		try {
			f = new File(csvName);  // make CSV file
			fw = new FileWriter(f, false);
			bw = new BufferedWriter(fw);
		} catch (Exception e) {
			DSS.doSimpleMsg("Error opening CSVFile \""+csvName+"\" for writing" +DSS.CRLF + e.getMessage(), 672);
			return;
		}

//		MonitorStream.seek(0);  // start at the beginning of the stream
//		MonitorStream.read(Fsignature);
//		MonitorStream.read(Fversion);
//		MonitorStream.read(RecordSize);
//		MonitorStream.read(Mode);
//		MonitorStream.read(StrBuffer);

//		pStr = strBuffer.length();
//		FBuffer.write(pStr);
//		FBuffer.newLine();
//		recordBytes = recordSize;

		try {

//			while (!(MonitorStream.position() >= MonitorStream.size())) {
//				MonitorStream.read(hr);
//				MonitorStream.read(s);
//				Nread = MonitorStream.read(sngBuffer, RecordBytes);
//			}
//			if (Nread < RecordBytes) break;
//			FBuffer.write(hr);  // hours
			bw.write(", " + s);  // sec
			for (i = 0; i < recordSize; i++)
				bw.write(", " + String.format("%-.6g", sngBuffer[i]));
			bw.newLine();

			closeMonitorStream();
			bw.close();
			fw.close();
		} catch (Exception e) {
			DSS.doSimpleMsg("Error writing CSV file \""+csvName+"\" " +DSS.CRLF + e.getMessage(), 673);
		}

		if (show) Util.fireOffEditor(csvName);

		DSS.globalResult = csvName;
	}

	@Override
	public void getCurrents(Complex[] curr) {
		for (int i = 0; i < nConds; i++) curr[i] = Complex.ZERO;
	}

	@Override
	public void getInjCurrents(Complex[] curr) {
		for (int i = 0; i < nConds; i++) curr[i] = Complex.ZERO;
	}

	@Override
	public void dumpProperties(OutputStream out, boolean complete) {
		super.dumpProperties(out, complete);

		PrintWriter pw = new PrintWriter(out);

		for (int i = 0; i < getParentClass().getNumProperties(); i++) {
			pw.println("~ " + getParentClass().getPropertyName(i) +
				"=" + getPropertyValue(i));
		}

		if (complete) {
			pw.println();
			pw.println("// bufferSize=" + bufferSize);
			pw.println("// hour=" + hour);
			pw.println("// sec=" + sec);
			pw.println("// baseFrequency=" + baseFrequency);
			pw.println("// bufptr=" + bufPtr);
			pw.println("// buffer=");
			int k = 0;
			for (int i = 0; i < bufPtr; i++) {
				pw.print(monBuffer[i] + ", ");
				k += 1;
				if (k == (2 + nConds * 4)) {
					pw.println();
					k = 0;
				}
			}
			pw.println();
		}
		pw.close();
	}

	@Override
	public void initPropertyValues(int arrayOffset) {
		setPropertyValue(0, "");  // "element";
		setPropertyValue(1, "1"); // "terminal";
		setPropertyValue(2, "0"); // "mode";
		setPropertyValue(3, "");  // "action";  // buffer=clear|save
		setPropertyValue(4, "bo");
		setPropertyValue(5, "yes");
		setPropertyValue(6, "yes");

		super.initPropertyValues(Monitor.NumPropsThisClass - 1);
	}

	public String getFileName() {
		return DSS.dataDirectory + DSS.circuitName_ + "Mon_" + getName() + ".csv";
	}

	public void TOPExport(String objName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int injCurrents() {
		throw new UnsupportedOperationException();
	}

	public boolean isIncludeResidual() {
		return includeResidual;
	}

	public void setIncludeResidual(boolean includeResidual) {
		this.includeResidual = includeResidual;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public void setVIpolar(boolean vIpolar) {
		VIpolar = vIpolar;
	}

	public void setPpolar(boolean ppolar) {
		Ppolar = ppolar;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

}
