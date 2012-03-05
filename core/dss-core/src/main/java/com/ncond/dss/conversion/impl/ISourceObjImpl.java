package com.ncond.dss.conversion.impl;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;


import org.apache.commons.math.complex.Complex;

import com.ncond.dss.common.SolutionObj;
import com.ncond.dss.common.impl.DSSClassImpl;
import com.ncond.dss.common.impl.DSS;
import com.ncond.dss.common.impl.Util;
import com.ncond.dss.conversion.ISource;
import com.ncond.dss.conversion.ISourceObj;
import com.ncond.dss.parser.impl.Parser;
import com.ncond.dss.shared.impl.CMatrixImpl;
import com.ncond.dss.shared.impl.ComplexUtil;

public class ISourceObjImpl extends PCElementImpl implements ISourceObj {

	private double amps;

	private double angle;

	private double phaseShift;

	private int scanType;

	private int sequenceType;

	protected double srcFrequency;

	public ISourceObjImpl(DSSClassImpl parClass, String sourceName) {
		super(parClass);

		setName(sourceName.toLowerCase());
		objType = parClass.getDSSClassType(); // SOURCE + NON_PCPD_ELEM;  // don't want this in PC Element List

		setNumPhases(3);
		ncond = 3;
		setNumTerms(1);

		amps = 0.0;
		angle = 0.0;
		srcFrequency = baseFrequency;
		phaseShift = 120.0;
		scanType = 1;  // pos sequence
		sequenceType = 1;

		initPropertyValues(0);

		YOrder = nterm * ncond;
		recalcElementData();
	}

	@Override
	public void recalcElementData() {
		setSpectrumObj( (com.ncond.dss.general.SpectrumObj) DSS.spectrumClass.find(getSpectrum()) );

		if (getSpectrumObj() == null)
			DSS.doSimpleMsg("Spectrum object \"" + getSpectrum() + "\" for device ISource."+getName()+" not found.", 333);

		setInjCurrent( Util.resizeArray(getInjCurrent(), YOrder) );
	}

	@Override
	public void calcYPrim() {

		// build only YPrim_Series
		if (isYprimInvalid()) {
			YPrimSeries = new CMatrixImpl(YOrder);
			YPrim = new CMatrixImpl(YOrder);
		} else {
			YPrimSeries.clear();
			YPrim.clear();
		}


		/* Yprim = 0 for ideal current source; just leave it zeroed */

		/* Now account for open conductors.
		 * For any conductor that is open, zero out row and column.
		 */
		super.calcYPrim();

		setYPrimInvalid(false);
	}

	private Complex getBaseCurr() {
		double srcHarmonic;
		Complex result = null;
		SolutionObj sol;

		try {
			sol = DSS.activeCircuit.getSolution();

			/* Get first phase current */
			if (sol.isHarmonicModel()) {
				srcHarmonic = sol.getFrequency() / srcFrequency;
				result = getSpectrumObj().getMult(srcHarmonic).multiply(amps);  // base current for this harmonic
				result = Util.rotatePhasorDeg(result, srcHarmonic, angle);
			} else {
				if (Math.abs(sol.getFrequency() - srcFrequency) < DSS.EPSILON2) {
					result = ComplexUtil.polarDeg2Complex(amps, angle);
				} else {
					result = Complex.ZERO;
				}
			}
		} catch (Exception e) {
			DSS.doSimpleMsg("Error computing current for ISource."+getName()+". Check specification. Aborting.", 334);
			if (DSS.inRedirect)
				DSS.redirectAbort = true;
		}

		return result;
	}

	/**
	 * Sum currents directly into solution array.
	 */
	@Override
	public int injCurrents() {
		getInjCurrents(getInjCurrent());

		return super.injCurrents();  // adds into system array
	}

	/**
	 * Total currents into a device.
	 */
	@Override
	public void getCurrents(Complex[] curr) {
		try {
			getInjCurrents(complexBuffer);  // get present value of inj currents
			// add together with YPrim currents
			for (int i = 0; i < YOrder; i++)
				curr[i] = complexBuffer[i].negate();
		} catch (Exception e) {
			DSS.doErrorMsg(("GetCurrents for ISource element: " + getName() + "."), e.getMessage(),
					"Inadequate storage allotted for circuit element.", 335);
		}
	}

	/**
	 * Fill up an array of injection currents.
	 */
	@Override
	public void getInjCurrents(Complex[] curr) {
		SolutionObj sol = DSS.activeCircuit.getSolution();

		Complex baseCurr = getBaseCurr();  // this func applies spectrum if needed

		for (int i = 0; i < getNumPhases(); i++) {
			curr[i] = baseCurr;
			if (i < getNumPhases())
				if (sol.isHarmonicModel()) {
					switch (scanType) {
					case 1:
						baseCurr = Util.rotatePhasorDeg(baseCurr, 1.0, -getPhaseShift());  // maintain positive sequence for ISource
						break;
					case 0:
						// do not rotate for zero sequence
						break;
					default:
						baseCurr = Util.rotatePhasorDeg(baseCurr, sol.getHarmonic(), -getPhaseShift());  // rotate by frequency
						/* Harmonic 1 will be pos; 2 is neg; 3 is zero, and so on. */
						break;
					}
				} else {
					switch (sequenceType) {
					case -1:
						baseCurr = Util.rotatePhasorDeg(baseCurr, 1.0, phaseShift);  // neg seq
						break;
					case 0:
						// do not rotate for zero sequence
						break;
					default:
						baseCurr = Util.rotatePhasorDeg(baseCurr, 1.0, -phaseShift);  // maintain pos seq
						break;
					}
				}
		}
	}

	@Override
	public void dumpProperties(OutputStream out, boolean complete) {
		super.dumpProperties(out, complete);

		PrintWriter pw = new PrintWriter(out);

		for (int i = 0; i < getParentClass().getNumProperties(); i++)
			pw.println("~ " + getParentClass().getPropertyName(i) + "=" + getPropertyValue(i));

		if (complete) {
			pw.println();
			pw.println();
		}

		pw.close();
	}

	@Override
	public void initPropertyValues(int arrayOffset) {

		setPropertyValue(0, getBus(0));
		setPropertyValue(1, "0");
		setPropertyValue(2, "0");
		setPropertyValue(3, String.format("%-.6g", srcFrequency));
		setPropertyValue(4, "3");
		setPropertyValue(5, "pos");
		setPropertyValue(6, "pos");

		super.initPropertyValues(ISource.NumPropsThisClass - 1);
	}

	/**
	 * Make a positive sequence model.
	 */
	@Override
	public void makePosSequence() {
		if (getNumPhases() > 1) {
			Parser.getInstance().setCmdString("phases=1");
			edit();
		}
		super.makePosSequence();
	}

	public double getSrcFrequency() {
		return srcFrequency;
	}

	public void setSrcFrequency(double frequency) {
		srcFrequency = frequency;
	}

	// FIXME Private members in OpenDSS

	public double getAmps() {
		return amps;
	}

	public void setAmps(double value) {
		amps = value;
	}

	public double getAngle() {
		return angle;
	}

	public void setAngle(double ang) {
		angle = ang;
	}

	public double getPhaseShift() {
		return phaseShift;
	}

	public void setPhaseShift(double shift) {
		phaseShift = shift;
	}

	public int getScanType() {
		return scanType;
	}

	public void setScanType(int type) {
		scanType = type;
	}

	public int getSequenceType() {
		return sequenceType;
	}

	public void setSequenceType(int type) {
		sequenceType = type;
	}

}
