package com.epri.dss.common.impl;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.epri.dss.common.Bus;
import com.epri.dss.common.Circuit;
import com.epri.dss.common.CktElement;
import com.epri.dss.common.DSSClass;
import com.epri.dss.common.SolutionObj;
import com.epri.dss.control.RegControlObj;
import com.epri.dss.conversion.Generator;
import com.epri.dss.conversion.GeneratorObj;
import com.epri.dss.conversion.LoadObj;
import com.epri.dss.conversion.PCElement;
import com.epri.dss.delivery.PDElement;
import com.epri.dss.delivery.TransformerObj;
import com.epri.dss.delivery.impl.LineImpl;
import com.epri.dss.general.LineGeometry;
import com.epri.dss.general.LineGeometryObj;
import com.epri.dss.meter.EnergyMeter;
import com.epri.dss.meter.EnergyMeterObj;
import com.epri.dss.parser.impl.Parser;
import com.epri.dss.shared.CMatrix;
import com.epri.dss.shared.CktTree;
import com.epri.dss.shared.CktTreeNode;
import com.epri.dss.shared.Polar;
import com.epri.dss.shared.impl.CMatrixImpl;
import com.epri.dss.shared.impl.CktTreeImpl;
import com.epri.dss.shared.impl.Complex;
import com.epri.dss.shared.impl.ComplexUtil;
import com.epri.dss.shared.impl.LineUnits;
import com.epri.dss.shared.impl.MathUtil;

public abstract class ShowResults {

	private static final char TABCHAR = '\u0009';

	private static int maxBusNameLength = 12;
	private static int maxDeviceNameLength = 30;

	private static void setMaxBusNameLength() {
		maxBusNameLength = 4;
		Circuit ckt = DSSGlobals.getInstance().getActiveCircuit();
		for (int i = 0; i < ckt.getNumBuses(); i++)
			maxBusNameLength = Math.max(maxBusNameLength, ckt.getBusList().get(i).length());
	}

	private static void setMaxDeviceNameLength() {
		String devName, devClassName;

		DSSGlobals globals = DSSGlobals.getInstance();
		Circuit ckt = globals.getActiveCircuit();

		maxDeviceNameLength = 0;
		for (int i = 0; i < ckt.getNumDevices(); i++) {
			devName = ckt.getDeviceList().get(i);
			devClassName = ((DSSClass) globals.getDSSClassList().get( ckt.getDeviceRef()[i].cktElementClass )).getName();
			maxDeviceNameLength = Math.max(maxDeviceNameLength, devName.length() + devClassName.length() + 1);
		}
	}

	private static void writeSeqVoltages(PrintWriter pw, int i, boolean LL) {
		int j, k;
		Complex[] Vph = new Complex[3];
		Complex[] VLL = new Complex[3];
		Complex[] V012 = new Complex[3];
		double V0, V1, V2, Vpu, V2V1, V0V1;

		Circuit ckt = DSSGlobals.getInstance().getActiveCircuit();

		if (ckt.getBuses()[i].getNumNodesThisBus() >= 3) {

			for (j = 0; j < 3; j++)
				Vph[j] = ckt.getSolution().getNodeV()[ ckt.getBuses()[i].getRef(j) ];

			if (LL) {
				for (j = 0; j < 3; j++) {
					k = j + 1;
					if (k > 3) k = 1;  // TODO Check zero based indexing
					VLL[j] = Vph[j].subtract(Vph[k]);
				}
				MathUtil.phase2SymComp(VLL, V012);
			} else {
				MathUtil.phase2SymComp(Vph, V012);
			}

			V0 = V012[0].abs();
			V1 = V012[1].abs();
			V2 = V012[2].abs();
		} else {
			Vph[0] = ckt.getSolution().getNodeV()[ ckt.getBuses()[i].getRef(0) ];
			V0 = 0.0;
			V1 = Vph[0].abs();  // use first phase value for non-three phase buses
			V2 = 0.0;
		}

		V1 = V1 / 1000.0;  /* Convert to kV */
		V2 = V2 / 1000.0;
		V0 = V0 / 1000.0;

		// calc per unit value
		if (ckt.getBuses()[i].getKVBase() != 0.0) {
			Vpu = V1 / ckt.getBuses()[i].getKVBase();
		} else {
			Vpu = 0.0;
		}
		if (LL) Vpu = Vpu / DSSGlobals.SQRT3;

		if (V1 > 0.0) {
			V2V1 = 100.0 * V2 / V1;
			V0V1 = 100.0 * V0 / V1;
		} else {
			V2V1 = 0.0;
			V0V1 = 0.0;
		}

		pw.printf("%s %9.4g  %9.4g  %9.4g  %9.4g %9.4g %9.4g", Utilities.pad(ckt.getBusList().get(i), maxBusNameLength), V1, Vpu, V2, V2V1, V0, V0V1);
		pw.println();
	}

	private static void writeBusVoltages(PrintWriter pw, int i, boolean LL) {
		int nref, j, k;
		Complex volts;
		double Vmag, Vpu;
		String BName;

		Circuit ckt = DSSGlobals.getInstance().getActiveCircuit();

		for (j = 0; j < ckt.getBuses()[i].getNumNodesThisBus(); j++) {
			nref = ckt.getBuses()[i].getRef(j);
			volts = ckt.getSolution().getNodeV()[nref];

			if (LL && (j < 4)) {  // TODO Check zero based indexing
				// convert to line-line assuming no more than 3 phases
				k = j + 1;
				if (k > 3) k = 1;  // TODO Check zero based indexing
				if (k <= ckt.getBuses()[i].getNumNodesThisBus()) {
					nref = ckt.getBuses()[i].getRef(k);
					volts = volts.subtract( ckt.getSolution().getNodeV()[nref] );
				}
			}
			Vmag = volts.abs() * 0.001;
			if (ckt.getBuses()[i].getKVBase() != 0.0) {
				Vpu = Vmag / ckt.getBuses()[i].getKVBase();
			} else {
				Vpu = 0.0;
			}
			if (LL) Vpu = Vpu / DSSGlobals.SQRT3;
			if (j == 0) {  // TODO Check zero based indexing
				BName = Utilities.padDots(ckt.getBusList().get(i), maxBusNameLength);
			} else {
				BName = Utilities.pad("   -", maxBusNameLength);
			}
			pw.printf("%s %2d %10.5g /_ %6.1f %9.5g %9.3f", BName, ckt.getBuses()[i].getNum(j), Vmag, volts.degArg(), Vpu, ckt.getBuses()[i].getKVBase() * DSSGlobals.SQRT3);
			pw.println();
		}
	}

	private static void writeElementVoltages(PrintWriter pw, CktElement pElem, boolean LL) {
		int nCond, nTerm, i, j, k, nref, bref;
		String busName;
		Complex volts;
		double Vpu, Vmag;

		Circuit ckt = DSSGlobals.getInstance().getActiveCircuit();

		nCond = pElem.getNConds();
		nTerm = pElem.getNTerms();
		k = -1;
		busName = Utilities.pad( Utilities.stripExtension(pElem.getFirstBus()), maxBusNameLength );
		pw.println("ELEMENT = \"" + pElem.getDSSClassName() + "." + pElem.getName() + "\"");
		for (j = 0; j < nTerm; j++) {
			for (i = 0; i < nCond; i++) {
				k++;
				nref = pElem.getNodeRef()[k];
				volts = ckt.getSolution().getNodeV()[nref];
				Vmag  = volts.abs() * 0.001;
				if (nref == 0) {
					Vpu = 0.0;
				} else {
					bref = ckt.getMapNodeToBus()[nref].busRef;
					if (ckt.getBuses()[bref].getKVBase() != 0.0) {
						Vpu = Vmag / ckt.getBuses()[bref].getKVBase();
					} else {
						Vpu = 0.0;
					}
				}
				if (LL) Vpu = Vpu / DSSGlobals.SQRT3;
				pw.printf("%s  (%3d) %4d    %13.5g (%8.4g) /_ %6.1f", busName, nref, i,Vmag, Vpu, volts.degArg());
				pw.println();
			}
			if (j < nTerm)
				pw.println("------------");
			busName = Utilities.pad( Utilities.stripExtension(pElem.getNextBus()), maxBusNameLength );
		}
	}

	private static void writeElementDeltaVoltages(PrintWriter pw, CktElement pElem) {
		int nCond, node1, node2, bus1, bus2, i;
		double Vmag;
		Complex volts1, volts2;
		String elemName;

		Circuit ckt = DSSGlobals.getInstance().getActiveCircuit();

		nCond = pElem.getNConds();

		elemName = Utilities.pad(pElem.getDSSClassName() + "." + pElem.getName(), maxDeviceNameLength);
		for (i = 0; i < nCond; i++) {
			node1 = pElem.getNodeRef()[i];
			node2 = pElem.getNodeRef()[i + nCond];
			if (node1 > 0) {
				bus1  = ckt.getMapNodeToBus()[node1].busRef;
			} else {
				bus1 = 0;
			}
			if (node2 > 0) {
				bus2 = ckt.getMapNodeToBus()[node2].busRef;
			} else {
				bus2 = 0;
			}
			if ((bus1 > 0) && (bus2 > 0)) {  // TODO Check zero based indexing
				volts1 = ckt.getSolution().getNodeV()[node1];  // OK if Node1 or Node2 = 0
				volts2 = ckt.getSolution().getNodeV()[node2];
				volts1 = volts1.subtract(volts2);  // diff voltage

				if (ckt.getBuses()[bus1].getKVBase() != ckt.getBuses()[bus2].getKVBase()) {
					Vmag = 0.0;
				} else {
					if (ckt.getBuses()[bus1].getKVBase() > 0.0) {
						Vmag = volts1.abs() / (1000.0 * ckt.getBuses()[bus1].getKVBase()) * 100.0;
					} else {
						Vmag = 0.0;
					}
				}
				pw.printf("%s,  %4d,    %12.5g, %12.5g, %12.5g, %6.1f", elemName, i, volts1.abs(), Vmag, ckt.getBuses()[bus1].getKVBase(), volts1.degArg());
				pw.println();
			}
		}
	}

	/**
	 * Show bus voltages by circuit element terminal.
	 */
	public static void showVoltages(String fileName, boolean LL, int showOptionCode) {
		FileWriter fw;
		PrintWriter pw;
		int i;

		Circuit ckt = DSSGlobals.getInstance().getActiveCircuit();

		try {
			setMaxBusNameLength();

			fw = new FileWriter(fileName);
			pw = new PrintWriter(fw);

			switch (showOptionCode) {
			case 0:
				pw.println();
				if (LL) {
					pw.println("SYMMETRICAL COMPONENT PHASE-PHASE VOLTAGES BY BUS (for 3-phase buses)");
				} else {
					pw.println("SYMMETRICAL COMPONENT VOLTAGES BY BUS (for 3-phase buses)");
				}
				pw.println();
				pw.println(Utilities.pad("Bus", maxBusNameLength) + "  Mag:   V1 (kV)    p.u.     V2 (kV)   %V2/V1    V0 (kV)    %V0/V1");
				pw.println();
				for (i = 0; i < ckt.getNumBuses(); i++)
					writeSeqVoltages(pw, i, LL);

				break;
			case 1:
				pw.println();
				if (LL) {
					pw.println("PHASE-PHASE VOLTAGES BY BUS & NODE");
				} else {
					pw.println("NODE-GROUND VOLTAGES BY BUS & NODE");
				}
				pw.println();
				pw.println(Utilities.pad("Bus", maxBusNameLength) + " Node    V (kV)    Angle      p.u.   Base kV");
				pw.println();
				for (i = 0; i < ckt.getNumBuses(); i++)
					writeBusVoltages(pw, i, LL);

				break;
			case 2:
				pw.println();
				pw.println("NODE-GROUND VOLTAGES BY CIRCUIT ELEMENT");
				pw.println();
				pw.println("Power Delivery Elements");
				pw.println();
				pw.println(Utilities.pad("Bus", maxBusNameLength) + " (node ref)  Phase    Magnitude, kV (pu)    Angle");
				pw.println();


				// sources first
				for (CktElement pElem : ckt.getSources()) {
					if (pElem.isEnabled())
						writeElementVoltages(pw, pElem, LL);
					pw.println();
				}

				// PD elements first
				for (CktElement pElem : ckt.getPDElements()) {
					if (pElem.isEnabled())
						writeElementVoltages(pw, pElem, LL);
					pw.println();
				}

				pw.println("= = = = = = = = = = = = = = = = = = =  = = = = = = = = = = =  = =");
				pw.println();
				pw.println("Power Conversion Elements");
				pw.println();
				pw.println(Utilities.pad("Bus", maxBusNameLength) + " (node ref)  Phase    Magnitude, kV (pu)    Angle");
				pw.println();

				// PC elements next
				for (CktElement pElem : ckt.getPCElements()) {
					if (pElem.isEnabled())
						writeElementVoltages(pw, pElem, LL);
					pw.println();
				}
				break;
			}

			pw.close();
			fw.close();

			Utilities.fireOffEditor(fileName);
		} catch (IOException e) {
			// TODO: handle exception
		}
	}

	private static void getI0I1I2(double I0, double I1, double I2, double CMax, int nPhases, int kOffset, Complex[] cBuffer) {
		double CMag;
		int i;
		Complex[] Iph  = new Complex[3];
		Complex[] I012 = new Complex[3];

		if (nPhases >= 3) {
			CMax = 0.0;
			for (i = 0; i < 3; i++) {
				Iph[i] = cBuffer[kOffset + i];
				CMag = Iph[i].abs();
				if (CMag > CMax) CMax = CMag;
			}
			MathUtil.phase2SymComp(Iph, I012);
			I0 = I012[0].abs();
			I1 = I012[1].abs();
			I2 = I012[2].abs();
		} else {
			I0 = 0.0;
			I1 = cBuffer[1 + kOffset].abs();
			I2 = 0.0;
			CMax = I1;
		}
	}

	private static void writeSeqCurrents(PrintWriter pw, final String paddedBrName,
			double I0, double I1, double I2, double CMax, double normAmps, double emergAmps,
			int j, int DSSObjType) {

		double INormal, IEmerg, I2I1, I0I1;
		String name;

		INormal = 0.0;
		IEmerg = 0.0;
		if (j == 0) {  // TODO Check zero based indexing
			name = paddedBrName;
		} else {
			name = Utilities.pad("   -", paddedBrName.length());
		}

		if (I1 > 0.0) {
			I2I1 = 100.0 * I2 / I1;
		} else {
			I2I1 = 0.0;
		}

		if (I1 > 0.0) {
			I0I1 = 100.0 * I0 / I1;
		} else {
			I0I1 = 0.0;
		}
		if (((DSSClassDefs.CLASSMASK & DSSObjType) != DSSClassDefs.CAP_ELEMENT) && (j == 0)) {  // TODO Check zero based indexing
			// only write overloads for non-capacitors and terminal 1
			if (normAmps > 0.0) INormal = CMax / normAmps * 100.0;
			if (emergAmps > 0.0) IEmerg = CMax / emergAmps * 100.0;
		}

		pw.printf("%s %3d  %10.5g   %10.5g %8.2f  %10.5g %8.2f  %8.2f %8.2f",
				name, j, I1, I2, I2I1, I0, I0I1, INormal, IEmerg);
		pw.println();
	}

	private static void writeTerminalCurrents(PrintWriter pw, CktElement pElem, boolean showResidual) {
		int j, i, k, nCond, nTerm;
		Complex[] cBuffer;
		String fromBus;
		Complex CTotal;
		Polar residPolar;

		nCond = pElem.getNConds();
		nTerm = pElem.getNTerms();

		cBuffer = new Complex[nCond * nTerm];
		pElem.getCurrents(cBuffer);
		k = -1;
		fromBus = Utilities.pad( Utilities.stripExtension(pElem.getFirstBus()), maxBusNameLength );
		pw.println("ELEMENT = " + Utilities.fullName(pElem));
		for (j = 0; j < nTerm; j++) {
			CTotal = Complex.ZERO;
			for (i = 0; i < nCond; i++) {
				k++;
				if (showResidual)
					CTotal = CTotal.add(cBuffer[k]);
				pw.printf("%s  %4d    %13.5g /_ %6.1f", fromBus, Utilities.getNodeNum(pElem.getNodeRef()[k]), cBuffer[k].abs(), cBuffer[k].degArg());
				pw.println();
			}
			if (showResidual && (pElem.getNPhases() > 1)) {
				residPolar = ComplexUtil.complexToPolarDeg(CTotal.negate());
				pw.printf("%s Resid    %13.5g /_ %6.1f", fromBus, residPolar.getMag(), residPolar.getAng());
				pw.println();
			}
			if (j < nTerm) pw.println("------------");  // TODO Check zero based indexing
			fromBus = Utilities.pad( Utilities.stripExtension(pElem.getNextBus()), maxBusNameLength );
		}
		pw.println();
	}


	public static void showCurrents(String fileName, boolean showResidual, int showOptionCode) {
		FileWriter fw;
		PrintWriter pw;
		Complex[] cBuffer;
		int nCond, nTerm, j;
		double I0 = 0, I1 = 0, I2 = 0, CMax = 0;

		Circuit ckt = DSSGlobals.getInstance().getActiveCircuit();

		setMaxDeviceNameLength();
		setMaxBusNameLength();

		try {
			fw = new FileWriter(fileName);
			pw = new PrintWriter(fw);

			switch (showOptionCode) {
			case 0:  /* Sequence Currents */

				pw.println();
				pw.println("SYMMETRICAL COMPONENT CURRENTS BY CIRCUIT ELEMENT (first 3 phases)");
				pw.println();
				pw.println(Utilities.pad("Element", maxDeviceNameLength + 2) + " Term      I1         I2         %I2/I1    I0         %I0/I1   %Normal %Emergency");
				pw.println();

				// sources first
				for (CktElement pElem : ckt.getSources()) {
					if (pElem.isEnabled()) {
						nCond = pElem.getNConds();
						nTerm = pElem.getNTerms();
						cBuffer = new Complex[nCond * nTerm];
						pElem.getCurrents(cBuffer);

						for (j = 0; j < nTerm; j++) {
							getI0I1I2(I0, I1, I2, CMax, pElem.getNPhases(), (j - 1) * nCond, cBuffer);  // TODO Check pass be value
							writeSeqCurrents(pw, Utilities.padDots( Utilities.fullName(pElem), maxDeviceNameLength + 2 ), I0, I1, I2, CMax, 0.0, 0.0, j, pElem.getDSSObjType());
						}
						cBuffer = null;
					}
				}

				// PD elements next
				for (PDElement PDElem : ckt.getPDElements()) {
					if (PDElem.isEnabled()) {
						nCond = PDElem.getNConds();
						nTerm = PDElem.getNTerms();
						cBuffer = new Complex[nCond * nTerm];
						PDElem.getCurrents(cBuffer);

						for (j = 0; j < nTerm; j++) {
							getI0I1I2(I0, I1, I2, CMax, PDElem.getNPhases(), (j - 1) * nCond, cBuffer);
							writeSeqCurrents(pw, Utilities.padDots( Utilities.fullName(PDElem), maxDeviceNameLength + 2 ), I0, I1, I2, CMax, PDElem.getNormAmps(), PDElem.getEmergAmps(), j, PDElem.getDSSObjType());
						}
						cBuffer = null;
					}
				}

				// PC elements next
				for (PCElement PCElem : ckt.getPCElements()) {
					if (PCElem.isEnabled()) {
						nCond = PCElem.getNConds();
						nTerm = PCElem.getNTerms();
						cBuffer = new Complex[nCond * nTerm];
						PCElem.getCurrents(cBuffer);

						for (j = 0; j < nTerm; j++) {
							getI0I1I2(I0, I1, I2, CMax, PCElem.getNPhases(), (j - 1) * nCond, cBuffer);
							writeSeqCurrents(pw, Utilities.padDots( Utilities.fullName(PCElem), maxDeviceNameLength + 2 ), I0, I1, I2, CMax, 0.0, 0.0, j, PCElem.getDSSObjType());
						}
						cBuffer = null;
					}
				}

				// faults next
				for (CktElement pElem : ckt.getFaults()) {
					if (pElem.isEnabled()) {
						nCond = pElem.getNConds();
						nTerm = pElem.getNTerms();
						cBuffer = new Complex[nCond * nTerm];
						pElem.getCurrents(cBuffer);

						for (j = 0; j < nTerm; j++) {
							getI0I1I2(I0, I1, I2, CMax, pElem.getNPhases(), (j - 1) * nCond, cBuffer);
							writeSeqCurrents(pw, Utilities.padDots( Utilities.fullName(pElem), maxDeviceNameLength + 2 ), I0, I1, I2, CMax, 0.0, 0.0, j, pElem.getDSSObjType());
						}
						cBuffer = null;
					}
				}

				break;
			case 1:  // element branch currents

				pw.println();
				pw.println("CIRCUIT ELEMENT CURRENTS");
				pw.println();
				pw.println("(Currents into element from indicated bus)");
				pw.println();
				pw.println("Power Delivery Elements");
				pw.println();
				pw.println(Utilities.pad("  Bus", maxBusNameLength) + " Phase    Magnitude, A     Angle");
				pw.println();

				// sources first
				for (CktElement pElem : ckt.getSources())
					if (pElem.isEnabled())
						writeTerminalCurrents(pw, pElem, false);

				// PD elements next
				for (CktElement pElem : ckt.getPDElements())
					if (pElem.isEnabled())
						writeTerminalCurrents(pw, pElem, showResidual);

				// faults
				for (CktElement pElem : ckt.getFaults())
					if (pElem.isEnabled())
						writeTerminalCurrents(pw, pElem, false);


				pw.println("= = = = = = = = = = = = = = = = = = =  = = = = = = = = = = =  = =");
				pw.println();
				pw.println("Power Conversion Elements");
				pw.println();
				pw.println(Utilities.pad("  Bus", maxBusNameLength) + " Phase    Magnitude, A     Angle");
				pw.println();

				// PC elements next
				for (CktElement pElem : ckt.getPCElements())
					if (pElem.isEnabled())
						writeTerminalCurrents(pw, pElem, false);
				break;
			}
			pw.close();
			fw.close();

			Utilities.fireOffEditor(fileName);
		} catch (IOException e) {
			DSSGlobals.getInstance().doSimpleMsg("Exception raised in ShowCurrents: " + e.getMessage(), 2190);
		}
	}

	/**
	 * opt = 0: kVA
	 * opt = 1: MVA
	 */
	public static void showPowers(String fileName, int opt, int showOptionCode) {
		String fromBus;
		FileWriter fw;
		PrintWriter pw;
		Complex[] cBuffer;
		int nCond, nTerm, i, j, k;
		Complex volts;
		Complex S, Saccum;
		int nref;
		Complex[] Vph = new Complex[3];
		Complex[] V012 = new Complex[3];
		Complex[] Iph = new Complex[3];
		Complex[] I012 = new Complex[3];

		Circuit ckt = DSSGlobals.getInstance().getActiveCircuit();

		setMaxDeviceNameLength();
		setMaxBusNameLength();

		try {
			fw = new FileWriter(fileName);
			pw = new PrintWriter(fw);

			/* Allocate cBuffer big enough for largest circuit element */
			cBuffer = new Complex[Utilities.getMaxCktElementSize()];

			switch (showOptionCode) {
			case 0:
				/* Sequence Currents */
				pw.println();
				pw.println("SYMMETRICAL COMPONENT POWERS BY CIRCUIT ELEMENT (first 3 phases)                                     Excess Power");
				pw.println();
				switch (opt) {
				case 1:
					pw.println(Utilities.pad("Element", maxDeviceNameLength + 2) + " Term    P1(MW)   Q1(Mvar)       P2         Q2      P0      Q0       P_Norm      Q_Norm     P_Emerg    Q_Emerg");
					break;
				default:
					pw.println(Utilities.pad("Element", maxDeviceNameLength + 2) + " Term    P1(kW)   Q1(kvar)       P2         Q2      P0      Q0       P_Norm      Q_Norm     P_Emerg    Q_Emerg");
					break;
				}
				pw.println();

				// sources first
				for (CktElement pElem : ckt.getSources()) {
					if (pElem.isEnabled()) {
						nCond = pElem.getNConds();
						nTerm = pElem.getNTerms();
						pElem.getCurrents(cBuffer);

						for (j = 0; j < nTerm; j++) {
							pw.print(Utilities.pad( Utilities.fullName(pElem), maxDeviceNameLength + 2) + j);
							for (i = 0; i < Math.min(3, pElem.getNPhases()); i++) {
								k = (j - 1) * nCond + i;
								nref = pElem.getNodeRef()[k];
								volts = ckt.getSolution().getNodeV()[nref];
								Iph[i] = cBuffer[k];
								Vph[i] = volts;
							}
							if (pElem.getNPhases() >= 3) {
								MathUtil.phase2SymComp(Iph, I012);
								MathUtil.phase2SymComp(Vph, V012);
							} else {
								// Handle single phase and pos seq models
								V012[0] = Complex.ZERO;
								I012[0] = Complex.ZERO;
								V012[2] = Complex.ZERO;
								I012[2] = Complex.ZERO;
								if (ckt.isPositiveSequence()) {
									V012[1] = Vph[0];
									I012[1] = Iph[0];
								} else {
									V012[1] = Complex.ZERO;
									I012[1] = Complex.ZERO;
								}
							}

							S = V012[1].multiply( I012[1].conjugate() );
							if (opt == 1) S = S.multiply(0.001);
							pw.print(S.getReal() * 0.003);
							pw.print(S.getImaginary() * 0.003);
							S = V012[2].multiply( I012[2].conjugate() );
							if (opt == 1) S = S.multiply(0.001);
							pw.print(S.getReal() * 0.003);
							pw.print(S.getImaginary() * 0.003);
							S = V012[0].multiply( I012[0].conjugate() );
							if (opt == 1) S = S.multiply(0.001);
							pw.print(S.getReal() * 0.003);
							pw.print(S.getImaginary() * 0.003);
							pw.println();
						}
					}
				}

				// PD elements next
				for (PDElement PDElem : ckt.getPDElements()) {
					if (PDElem.isEnabled()) {
						nCond = PDElem.getNConds();
						nTerm = PDElem.getNTerms();
						PDElem.getCurrents(cBuffer);

						for (j = 0; j < nTerm; j++) {
							pw.print(Utilities.pad( Utilities.fullName(PDElem), maxDeviceNameLength + 2) + j);
							for (i = 0; i < Math.min(3, PDElem.getNPhases()); i++) {
								k = (j - 1) * nCond + i;
								nref = PDElem.getNodeRef()[k];
								volts = ckt.getSolution().getNodeV()[nref];
								Iph[i] = cBuffer[k];
								Vph[i] = volts;
							}
							if (PDElem.getNPhases() >= 3) {
								MathUtil.phase2SymComp(Iph, I012);
								MathUtil.phase2SymComp(Vph, V012);
							} else {  // Handle single phase and pos seq models
								V012[0] = Complex.ZERO;
								I012[0] = Complex.ZERO;
								V012[1] = Complex.ZERO;
								I012[1] = Complex.ZERO;
								if (ckt.isPositiveSequence()) {
									V012[1] = Vph[0];
									I012[1] = Iph[0];
								} else {
									V012[1] = Complex.ZERO;
									I012[1] = Complex.ZERO;
								}
							}

							S = V012[1].multiply( I012[1].conjugate() );
							if (opt == 1) S = S.multiply(0.001);
							pw.print(S.getReal() * 0.003);
							pw.print(S.getImaginary() * 0.003);
							S = V012[2].multiply( I012[2].conjugate() );
							if (opt == 1) S = S.multiply(0.001);
							pw.print(S.getReal() * 0.003);
							pw.print(S.getImaginary() * 0.003);
							S = V012[0].multiply( I012[0].conjugate() );
							if (opt == 1) S = S.multiply(0.001);
							pw.print(S.getReal() * 0.003);
							pw.print(S.getImaginary() * 0.003);

							if (j == 0) {  // TODO Check zero based indexing
								S = PDElem.getExcessKVANorm(0);
								if (opt == 1) S = S.multiply(0.001);
								pw.print(S.getReal());
								pw.print(S.getImaginary());
								S = PDElem.getExcessKVAEmerg(1);  // TODO Check zero based indexing
								if (opt == 1) S = S.multiply(0.001);
								pw.print(S.getReal());
								pw.print(S.getImaginary());
							}
							pw.println();

						}
					}
				}

				// PC elements next
				for (PCElement PCElem : ckt.getPCElements()) {
					if (PCElem.isEnabled()) {
						nCond = PCElem.getNConds();
						nTerm = PCElem.getNTerms();
						PCElem.getCurrents(cBuffer);

						for (j = 0; j < nTerm; j++) {
							pw.print(Utilities.pad( Utilities.fullName(PCElem), maxDeviceNameLength + 2) + j);
							for (i = 0; i < Math.min(3, PCElem.getNPhases()); i++) {
								k = (j - 1) * nCond + i;  // TODO Check zero based indexing
								nref = PCElem.getNodeRef()[k];
								volts = ckt.getSolution().getNodeV()[nref] ;
								Iph[i] = cBuffer[k];
								Vph[i] = volts;
							}

							if (PCElem.getNPhases() >= 3) {
								MathUtil.phase2SymComp(Iph, I012);
								MathUtil.phase2SymComp(Vph, V012);
							} else {  // Handle single phase and pos seq models
								V012[0] = Complex.ZERO;
								I012[0] = Complex.ZERO;
								V012[2] = Complex.ZERO;
								I012[3] = Complex.ZERO;
								if (ckt.isPositiveSequence()) {
									V012[1] = Vph[0];
									I012[1] = Iph[0];
								} else {
									V012[1] = Complex.ZERO;
									I012[2] = Complex.ZERO;
								}
							}

							S = V012[1].multiply( I012[1].conjugate() );
							if (opt == 1) S = S.multiply(0.001);
							pw.print(S.getReal() * 0.003);
							pw.print(S.getImaginary() * 0.003);
							S = V012[2].multiply( I012[2].conjugate() );
							if (opt == 1) S = S.multiply(0.001);
							pw.print(S.getReal() * 0.003);
							pw.print(S.getImaginary() * 0.003);
							S = V012[0].multiply( I012[0].conjugate() );
							if (opt == 1) S = S.multiply(0.001);
							pw.print(S.getReal() * 0.003);
							pw.print(S.getImaginary() * 0.003);

							pw.println();

						}
					}
				}
				break;
			case 1:  /* Branch Powers */
				pw.println();
				pw.println("CIRCUIT ELEMENT POWER FLOW");
				pw.println();
				pw.println("(Power Flow into element from indicated Bus)");
				pw.println();
				pw.println("Power Delivery Elements");
				pw.println();
				switch (opt) {
				case 1:
					pw.println(Utilities.pad("  Bus", maxBusNameLength) + " Phase     MW     +j   Mvar         MVA         PF");
					break;
				default:
					pw.println(Utilities.pad("  Bus", maxBusNameLength) + " Phase     kW     +j   kvar         kVA         PF");
					break;
				}
				pw.println();


				for (CktElement pElem : ckt.getSources()) {
					if (pElem.isEnabled()) {
						nCond = pElem.getNConds();
						nTerm = pElem.getNTerms();
						pElem.getCurrents(cBuffer);
						k = -1;
						fromBus = Utilities.pad( Utilities.stripExtension(pElem.getFirstBus()), maxBusNameLength );
						pw.println("ELEMENT = " + Utilities.fullName(pElem));
						for (j = 0; j < nTerm; j++) {
							Saccum = Complex.ZERO;
							for (i = 0; i < nCond; i++) {
								k++;
								nref = pElem.getNodeRef()[k];
								volts = ckt.getSolution().getNodeV()[nref];
								S = volts.multiply( cBuffer[k].conjugate() );
								if (/* (pElem.getNPhases() == 1) and */ ckt.isPositiveSequence())
									S = S.multiply(3.0);
								if (opt == 1) S = S.multiply(0.001);
								Saccum = Saccum.add(S);
								pw.print(fromBus + "  " + Utilities.getNodeNum(pElem.getNodeRef()[k]) + "    " + S.getReal() / 1000.0 + " +j " + S.getImaginary() / 1000.0);
								pw.println("   " + S.abs() / 1000.0 + "     " + Utilities.powerFactor(S));
							}
							pw.print(Utilities.padDots("   TERMINAL TOTAL", maxBusNameLength + 10) + Saccum.getReal() / 1000.0 + " +j " + Saccum.getImaginary() / 1000.0);
							pw.println("   " + Saccum.abs() / 1000.0 + "     " + Utilities.powerFactor(Saccum));
							fromBus = Utilities.pad( Utilities.stripExtension(pElem.getNextBus()), maxBusNameLength );
						}
					}
					pw.println();
				}


				// PD elements next
				for (CktElement pElem : ckt.getPDElements()) {
					if (pElem.isEnabled()) {
						nCond = pElem.getNConds();
						nTerm = pElem.getNTerms();
						pElem.getCurrents(cBuffer);
						k = 0;
						fromBus = Utilities.pad( Utilities.stripExtension(pElem.getFirstBus()), maxBusNameLength );
						pw.println("ELEMENT = " + Utilities.fullName(pElem));
						for (j = 0; j < nTerm; j++) {
							Saccum = Complex.ZERO;
							for (i = 0; i < nCond; i++) {
								k++;
								nref = pElem.getNodeRef()[k];
								volts = ckt.getSolution().getNodeV()[nref];
								S = volts.multiply(cBuffer[k].conjugate());
								if (/* (pElem.getNPhases() == 1) and */ ckt.isPositiveSequence())
									S = S.multiply(3.0);
								if (opt == 1) S = S.multiply(0.001);
								Saccum = Saccum.add(S);
								pw.print(fromBus + "  " + Utilities.getNodeNum(pElem.getNodeRef()[k]) + "    " + S.getReal() / 1000.0 + " +j " + S.getImaginary() / 1000.0);
								pw.println("   " + S.abs() / 1000.0 + "     " + Utilities.powerFactor(S));
							}
							pw.print(Utilities.padDots("   TERMINAL TOTAL", maxBusNameLength + 10) + Saccum.getReal() / 1000.0 + " +j " + Saccum.getImaginary() / 1000.0);
							pw.println("   " + Saccum.abs() / 1000.0 + "     " + Utilities.powerFactor(Saccum));
							fromBus = Utilities.pad( Utilities.stripExtension(pElem.getNextBus()), maxBusNameLength );
						}
					}
					pw.println();
				}

				pw.println("= = = = = = = = = = = = = = = = = = =  = = = = = = = = = = =  = =");
				pw.println();
				pw.println("Power Conversion Elements");
				pw.println();
				switch (opt) {
				case 1:
					pw.println(Utilities.pad("  Bus", maxBusNameLength) + " Phase     MW   +j  Mvar         MVA         PF");
					break;
				default:
					pw.println(Utilities.pad("  Bus", maxBusNameLength) + " Phase     kW   +j  kvar         kVA         PF");
					break;
				}
				pw.println();

				// PC elements next
				for (CktElement pElem : ckt.getPCElements()) {
					if (pElem.isEnabled()) {
						nCond = pElem.getNConds();
						nTerm = pElem.getNTerms();
						pElem.getCurrents(cBuffer);
						k = -1;
						fromBus = Utilities.pad( Utilities.stripExtension(pElem.getFirstBus()), maxBusNameLength );
						pw.println("ELEMENT = " + Utilities.fullName(pElem));
						for (j = 0; j < nTerm; j++) {
							Saccum = Complex.ZERO;
							for (i = 0; i < nCond; i++) {
								k++;
								nref = pElem.getNodeRef()[k];
								volts = ckt.getSolution().getNodeV()[nref];
								S = volts.multiply(cBuffer[k].conjugate());
								if (/* (pElem.getNPhases() == 1) and */ ckt.isPositiveSequence())
									S = S.multiply(3.0);
								if (opt == 1) S = S.multiply(0.001);
								Saccum = Saccum.add(S);
								pw.print(fromBus + "  " + Utilities.getNodeNum(pElem.getNodeRef()[k]) + "    " + S.getReal() / 1000.0 + " +j " + S.getImaginary() / 1000.0);
								pw.println("   " + S.abs() / 1000.0 + "     " + Utilities.powerFactor(S));
							}
							pw.print(Utilities.padDots("   TERMINAL TOTAL", maxBusNameLength + 10) + Saccum.getReal() / 1000.0 + " +j " + Saccum.getImaginary() / 1000.0);
							pw.println("   " + Saccum.abs() / 1000.0 + "     " + Utilities.powerFactor(Saccum));
							fromBus = Utilities.pad( Utilities.stripExtension(pElem.getNextBus()), maxBusNameLength );
						}
					}
					pw.println();
				}
				break;
			}

			pw.println();
			S = ckt.getLosses().multiply(0.001);
			if (opt == 1) S = S.multiply(0.001);
			pw.println("Total Circuit Losses = " + S.getReal() + " +j " + S.getImaginary());

			cBuffer = null;

			pw.close();
			fw.close();

			Utilities.fireOffEditor(fileName);
		} catch (IOException e) {
			// TODO: handle exception
		}
	}

	/**
	 * Check all terminals of cktelement to see if bus connected to busreference.
	 */
	private static boolean checkBusReference(CktElement cktElem, int busReference, int terminalIndex) {

		for (int i = 0; i < cktElem.getNTerms(); i++)
			if (cktElem.getTerminals()[i].busRef == busReference) {
				terminalIndex = i;
				return true;
			}

		return false;
	}

	private static void writeTerminalPowerSeq(PrintWriter pw, CktElement cktElem, int j, int opt) {
		int i, k, nCond, nref;
		Complex volts, S;
		Complex[] Vph = new Complex[3];
		Complex[] V012 = new Complex[3];
		Complex[] Iph = new Complex[3];
		Complex[] I012 = new Complex[3];
		Complex[] cBuffer;  // Allocate to max total conductors

		Circuit ckt = DSSGlobals.getInstance().getActiveCircuit();

		try {
			/* Allocate cBuffer big enough for this circuit element */
			cBuffer = new Complex[cktElem.getYorder()];

			nCond = cktElem.getNConds();
			cktElem.getCurrents(cBuffer);
			pw.print(Utilities.pad( Utilities.fullName(cktElem), maxDeviceNameLength + 2) + j);
			for (i = 0; i < Math.min(cktElem.getNPhases(), 3); i++) {
				k = (j - 1) * nCond + i;  // TODO Check zero based indexing
				nref = cktElem.getNodeRef()[k];
				volts = ckt.getSolution().getNodeV()[nref];
				Iph[i] = cBuffer[k];
				Vph[i] = volts;
			}

			if (cktElem.getNPhases() >= 3) {
				MathUtil.phase2SymComp(Iph, I012);
				MathUtil.phase2SymComp(Vph, V012);
			} else {  // handle single phase and pos seq models
				V012[0] = Complex.ZERO;
				I012[0] = Complex.ZERO;
				V012[2] = Complex.ZERO;
				I012[2] = Complex.ZERO;
				if (ckt.isPositiveSequence()) {
					V012[1] = Vph[0];
					I012[1] = Iph[0];
				} else {
					V012[1] = Complex.ZERO;
					I012[1] = Complex.ZERO;
				}
			}

			// pos seq or single phase
			switch (cktElem.getNPhases()) {
			case 1:
				S = Vph[0].multiply( Iph[0].conjugate() );
				break;
			case 2:
				S = Vph[0].multiply( Iph[0].conjugate() ).add(Vph[1].multiply( Iph[2].conjugate() ));
				break;
			default:
				S = V012[1].multiply( I012[1].conjugate() );
				break;
			}

			if (opt == 1) S = S.multiply(0.001);
			pw.print(S.getReal() * 0.003);
			pw.print(S.getImaginary() * 0.003);
			S = V012[2].multiply( I012[2].conjugate() );
			if (opt == 1) S = S.multiply(0.001);
			pw.print(S.getReal() * 0.003);
			pw.print(S.getImaginary() * 0.003);
			S = V012[0].multiply( I012[0].conjugate() );
			if (opt == 1) S = S.multiply(0.001);
			pw.print(S.getReal() * 0.003);
			pw.print(S.getImaginary() * 0.003);

			pw.println();
		} finally  {
			cBuffer = null;
		}
	}

	private static void writeTerminalPower(PrintWriter pw, CktElement cktElem, int jTerm, int opt) {
		int i, k, nCond, nref;
		Complex volts, S;
		Complex Saccum;
		Complex[] cBuffer;  // allocate to max total conductors
		String fromBus;

		Circuit ckt = DSSGlobals.getInstance().getActiveCircuit();

		try {
			cBuffer = new Complex[cktElem.getYorder()];

			nCond = cktElem.getNConds();
			cktElem.getCurrents(cBuffer);
			fromBus = Utilities.pad(Utilities.stripExtension(cktElem.getBus(jTerm)), 12);
			pw.println("ELEMENT = " + Utilities.pad(Utilities.fullName(cktElem), maxDeviceNameLength + 2));

			Saccum = Complex.ZERO;
			for (i = 0; i < nCond; i++) {
				k = (jTerm - 1) * nCond + i;  // TODO Check zero based indexing
				nref = cktElem.getNodeRef()[k];
				volts = ckt.getSolution().getNodeV()[nref];
				S = volts.multiply( cBuffer[k].conjugate() );
				if (/* (CktElem.getNPhases() == 1) and */ ckt.isPositiveSequence())
					S = S.multiply(3.0);
				if (opt == 1) S = S.multiply(0.001);
				Saccum = Saccum.add(S);
				pw.printf("%s %4d %10.5g +j %10.5g    %10.5g    %8.4f",
						fromBus, Utilities.getNodeNum(cktElem.getNodeRef()[k]), S.getReal() / 1000.0, S.getImaginary() / 1000.0,
						S.abs() / 1000.0 , Utilities.powerFactor(S));
				pw.println();
			}
			pw.printf(" TERMINAL TOTAL   %10.5g +j %10.5g    %10.5g    %8.4f",
					Saccum.getReal() / 1000.0, Saccum.getImaginary() / 1000.0, Saccum.abs() / 1000.0,
					Utilities.powerFactor(Saccum));
			pw.println();
		} finally {
			cBuffer = null;
		}
	}

	/**
	 * Report power flow around a specified bus.
	 *
	 * opt = 0: kVA
	 * opt = 1: MVA
	 */
	public static void showBusPowers(String fileName, String busName, int opt, int showOptionCode) {
		FileWriter fw;
		PrintWriter pw;
		int j = 0, nCond, nTerm;
		double I0 = 0, I1 = 0, I2 = 0, CMax = 0;
		Complex[] cBuffer;  // allocate to max total conductors
		int busReference;
		int jTerm = 0;

		Circuit ckt = DSSGlobals.getInstance().getActiveCircuit();

		setMaxDeviceNameLength();

		/* Get bus reference */
		busReference = ckt.getBusList().find(busName);
		if (busReference == 0) {
			DSSGlobals.getInstance().doSimpleMsg("Bus \""+busName+"\" not found.", 219);
			return;
		}
		try {
			fw = new FileWriter(fileName);
			pw = new PrintWriter(fw);

			cBuffer = new Complex[Utilities.getMaxCktElementSize()];

			switch (showOptionCode) {
			case 0:
				/* Write bus voltage */

				pw.println();
				pw.println("  Bus   Mag:    V1 (kV)  p.u.    V2 (kV)  %V2/V1  V0 (kV)  %V0/V1");
				pw.println();

				writeSeqVoltages(pw, busReference, false);

				/* Sequence currents */
				pw.println();
				pw.println("SYMMETRICAL COMPONENT CURRENTS BY CIRCUIT ELEMENT (first 3 phases)");
				pw.println();
				pw.println("Element                      Term      I1         I2         %I2/I1    I0         %I0/I1   %Normal %Emergency");
				pw.println();

				// sources first
				for (CktElement pElem : ckt.getSources()) {
					if (pElem.isEnabled())
						if (checkBusReference(pElem, busReference, j)) {

							/* Use j set by checkBusReference */
							nCond = pElem.getNConds();
							nTerm = pElem.getNTerms();
							pElem.getCurrents(cBuffer);

							for (j = 0; j < nTerm; j++) {
								getI0I1I2(I0, I1, I2, CMax, pElem.getNPhases(), (j - 1) * nCond, cBuffer);
								writeSeqCurrents(pw, Utilities.padDots(Utilities.fullName(pElem), maxDeviceNameLength + 2), I0, I1, I2, CMax, 0.0, 0.0, j, pElem.getDSSObjType());
							}
						}
				}

				// PD elements next
				for (PDElement PDElem : ckt.getPDElements()) {
					if (PDElem.isEnabled())
						if (checkBusReference(PDElem, busReference, j)) {  // Is this connected to the bus
							/* Use j set by CheckBusReference */
							nCond = PDElem.getNConds();
							nTerm = PDElem.getNTerms();
							PDElem.getCurrents(cBuffer);

							for (j = 0; j < nTerm; j++) {
								getI0I1I2(I0, I1, I2, CMax, PDElem.getNPhases(), (j - 1) * nCond, cBuffer);
								writeSeqCurrents(pw, Utilities.padDots(Utilities.fullName(PDElem), maxDeviceNameLength + 2), I0, I1, I2, CMax, 0.0, 0.0, j, PDElem.getDSSObjType());
							}
						}
				}

				// PC elements next
				for (PCElement PCElem : ckt.getPCElements()) {
					if (PCElem.isEnabled())
						if (checkBusReference(PCElem, busReference, j)) {
							nCond = PCElem.getNConds();
							nTerm = PCElem.getNTerms();
							PCElem.getCurrents(cBuffer);

							for (j = 0; j < nTerm; j++) {
								getI0I1I2(I0, I1, I2, CMax, PCElem.getNPhases(), (j - 1) * nCond, cBuffer);
								writeSeqCurrents(pw, Utilities.padDots(Utilities.fullName(PCElem), maxDeviceNameLength + 2), I0, I1, I2, CMax, 0.0, 0.0, j, PCElem.getDSSObjType());
							}
						}
				}

				/* Sequence powers */
				pw.println();
				pw.println("SYMMETRICAL COMPONENT POWERS BY CIRCUIT ELEMENT (first 3 phases)");
				pw.println();
				switch (opt) {
				case 1:
					pw.println("Element                      Term    P1(MW)   Q1(Mvar)       P2         Q2      P0      Q0   ");
					break;
				default:
					pw.println("Element                      Term    P1(kW)   Q1(kvar)         P2         Q2      P0      Q0  ");
					break;
				}
				pw.println();

				// sources first
				for (CktElement pElem : ckt.getSources()) {
					if (pElem.isEnabled())
						if (checkBusReference(pElem, busReference, j)) {
							/* Use j set by checkBusReference */
							writeTerminalPowerSeq(pw, pElem, j, opt);
						}
				}

				// PD elements next
				for (PDElement PDElem : ckt.getPDElements()) {
					if (PDElem.isEnabled())
						if (checkBusReference(PDElem, busReference, j)) {  // Is this connected to the bus
							writeTerminalPowerSeq(pw, PDElem, j, opt);
						}
				}

				// PC elements next
				for (PCElement PCElem : ckt.getPCElements()) {
					if (PCElem.isEnabled())
						if (checkBusReference(PCElem, busReference, j)) {
							writeTerminalPowerSeq(pw, PCElem, j, opt);
						}
				}

				break;
			case 1:
				/* Write bus voltage */
				pw.println();
				pw.println("  Bus   (node ref)  Node       V (kV)    Angle    p.u.   Base kV");
				pw.println();
				writeBusVoltages(pw, busReference, false);

				/* Element currents */
				pw.println();
				pw.println("CIRCUIT ELEMENT CURRENTS");
				pw.println();
				pw.println("(Currents into element from indicated bus)");
				pw.println();
				pw.println("Power Delivery Elements");
				pw.println();
				pw.println("  Bus         Phase    Magnitude, A     Angle");
				pw.println();

				// sources first
				for (CktElement pElem : ckt.getSources()) {
					if (pElem.isEnabled())
						if (checkBusReference(pElem, busReference, j)) {
							writeTerminalCurrents(pw, pElem, false);
							pw.println();
						}
				}

				// PD elements first
				for (CktElement pElem : ckt.getPDElements()) {
					if (pElem.isEnabled())
						if (checkBusReference(pElem, busReference, j)) {
							writeTerminalCurrents(pw, pElem, true);
							pw.println();
						}
				}

				pw.println("= = = = = = = = = = = = = = = = = = =  = = = = = = = = = = =  = =");
				pw.println();
				pw.println("Power Conversion Elements");
				pw.println();
				pw.println("  Bus         Phase    Magnitude, A     Angle");
				pw.println();

				// PC elements next
				for (CktElement pElem : ckt.getPCElements()) {
					if (pElem.isEnabled())
						if (checkBusReference(pElem, busReference, j)) {
							writeTerminalCurrents(pw, pElem, false);
							pw.println();
						}
				}

				/* Branch powers */
				pw.println();
				pw.println("CIRCUIT ELEMENT POWER FLOW");
				pw.println();
				pw.println("(Power Flow into element from indicated Bus)");
				pw.println();
				switch (opt) {
				case 1:
					pw.println("  Bus       Phase     MW     +j   Mvar           MVA           PF");
					break;
				default:
					pw.println("  Bus       Phase     kW     +j   kvar           kVA           PF");
					break;
				}
				pw.println();

				// sources first
				for (CktElement pElem : ckt.getSources()) {
					if (pElem.isEnabled())
						if (checkBusReference(pElem, busReference, j)) {
							writeTerminalPower(pw, pElem, j, opt);
							pw.println();
						}
				}

				// PD elements first
				for (CktElement pElem : ckt.getPDElements()) {
					if (pElem.isEnabled())
						if (checkBusReference(pElem, busReference, jTerm)) {
							writeTerminalPower(pw, pElem, jTerm, opt);
							/* Get the other bus for the report */
							if (jTerm == 1) {
								jTerm = 2;
							} else {
								jTerm = 1;  // may sometimes give wrong terminal if more than 2 terminals
							}
							writeTerminalPower(pw, pElem, jTerm, opt);
							pw.println();
						}
				}

				pw.println("= = = = = = = = = = = = = = = = = = =  = = = = = = = = = = =  = =");
				pw.println();
				pw.println("Power Conversion Elements");
				pw.println();
				switch (opt) {
				case 1:
					pw.println("  Bus         Phase     MW   +j  Mvar         MVA         PF");
				default:
					pw.println("  Bus         Phase     kW   +j  kvar         kVA         PF");
				}
				pw.println();

				// PC elements next
				for (PCElement pElem : ckt.getPCElements()) {
					if (pElem.isEnabled())
						if (checkBusReference(pElem, busReference, jTerm)) {
							writeTerminalPower(pw, pElem, jTerm, opt);
							pw.println();
						}
				}
				break;
			}

			cBuffer = null;

			pw.close();
			fw.close();

			Utilities.fireOffEditor(fileName);
		} catch (IOException e) {
			// TODO: handle exception
		}
	}

	public static void showFaultStudy(String fileName) {
		int i, iBus, iphs;
		CMatrix YFault, ZFault;
		Complex[] VFault;  /* Big temp array */
		FileWriter fw;
		PrintWriter pw;
		Complex GFault, IFault;
		double Vphs;
		double currMag;
		Bus bus;

		setMaxBusNameLength();

		try {
			fw = new FileWriter(fileName);
			pw = new PrintWriter(fw);

			Circuit ckt = DSSGlobals.getInstance().getActiveCircuit();
			SolutionObj sol = ckt.getSolution();

			/* Set source voltage injection currents */

			/* All phase faults */
			pw.println("FAULT STUDY REPORT");
			pw.println();
			pw.println("ALL-Node Fault Currents");
			pw.println();
			pw.println(Utilities.pad("Bus", maxBusNameLength) + "       Node 1  X/R        Node 2  X/R        Node 3  X/R   ...  (Amps)");
			pw.println();
			for (iBus = 0; iBus < ckt.getNumBuses(); iBus++) {
				/* Bus Norton equivalent current, Isc has been previously computed */
				bus = ckt.getBuses()[iBus];
				pw.print(Utilities.pad(Utilities.encloseQuotes(ckt.getBusList().get(iBus)) + ",", maxBusNameLength + 2));
				for (i = 0; i < bus.getNumNodesThisBus(); i++) {
					currMag = bus.getBusCurrent()[i].abs();
					if (i > 1)
						pw.print(", ");
						pw.print(currMag);
						if (currMag > 0.0) {
							pw.print(", " + MathUtil.getXR( bus.getVBus()[i].divide( bus.getBusCurrent()[i] ) ));
						} else {
							pw.print(",   N/A");
						}
				}
				pw.println();
			}

			pw.println();

			/* One phase faults */
			pw.println();
			pw.println("ONE-Node to ground Faults");
			pw.println();
			pw.println("                                      pu Node Voltages (L-N Volts if no base)");
			pw.println(Utilities.pad("Bus", maxBusNameLength) + "   Node      Amps         Node 1     Node 2     Node 3    ...");
			pw.println();

			/* Solve for fault injection currents */
			for (iBus = 0; iBus < ckt.getNumBuses(); iBus++) {
				/* Bus Norton equivalent current, Isc has been previously computed */
				bus = ckt.getBuses()[iBus];
				ZFault = new CMatrixImpl(bus.getNumNodesThisBus());
				ZFault.copyFrom(bus.getZsc());

				for (iphs = 0; iphs < bus.getNumNodesThisBus(); iphs++) {
					IFault = bus.getVBus()[iphs].divide( bus.getZsc().getElement(iphs, iphs) );
					pw.print(Utilities.pad( Utilities.encloseQuotes(ckt.getBusList().get(iBus)), maxBusNameLength + 2) + iphs + IFault.abs() + "   ");
					for (i = 0; i < bus.getNumNodesThisBus(); i++) {
						Vphs = bus.getVBus()[i].subtract( bus.getZsc().getElement(i, iphs).multiply(IFault) ).abs();
						if (bus.getKVBase() > 0.0) {
							Vphs = 0.001 * Vphs / bus.getKVBase();
							pw.print(" " + Vphs);
						} else {
							pw.print(" " + Vphs);
						}
					}
					pw.println();
				}
				/* Now, put it in the Css array where it belongs */

				ZFault = null;
			}

			/* Node-node faults */
			pw.println();
			pw.println("Adjacent Node-Node Faults");
			pw.println();
			pw.println("                                        pu Node Voltages (L-N Volts if no base)");
			pw.println("Bus          Node-Node      Amps        Node 1     Node 2     Node 3    ...");
			pw.println();

			/* Solve for fault injection currents */
			for (iBus = 0; iBus < ckt.getNumBuses(); iBus++) {
				/* Bus Norton equivalent current, Isc has been previously computed */
				bus = ckt.getBuses()[iBus];
				YFault = new CMatrixImpl(bus.getNumNodesThisBus());
				VFault = new Complex[bus.getNumNodesThisBus()];

				GFault = new Complex(10000.0, 0.0);

				for (iphs = 0; iphs < bus.getNumNodesThisBus(); iphs++) {
					YFault.copyFrom(bus.getYsc());
					YFault.addElement(iphs, iphs, GFault);
					YFault.addElement(iphs + 1, iphs + 1, GFault);
					YFault.addElemSym(iphs, iphs + 1, GFault.negate());

					/* Solve for injection currents */
					YFault.invert();
					YFault.MVMult(VFault, bus.getBusCurrent());  /* Gets voltage appearing at fault */

					pw.print(Utilities.pad(Utilities.encloseQuotes(ckt.getBusList().get(iBus)), maxBusNameLength + 2) + iphs + (iphs + 1) + VFault[iphs].subtract(VFault[iphs + 1]).multiply(GFault).abs() + "   ");
					for (i = 0; i < bus.getNumNodesThisBus(); i++) {
						Vphs = VFault[i].abs();
						if (bus.getKVBase() > 0.0) {
							Vphs = 0.001 * Vphs / bus.getKVBase();
							pw.print(" " + Vphs);
						} else {
							pw.print(" " + Vphs);
						}
					}
					pw.println();

				}
			}
			/* Now, put it in the Css array where it belongs */
			VFault = null;
			YFault = null;

			pw.close();
			fw.close();

			Utilities.fireOffEditor(fileName);
		} catch (IOException e) {
			// TODO: handle exception
		}
	}

	private static void writeElementRecord(PrintWriter pw, CktElement pElem) {
		int nTerm, j;
		String busName;

		nTerm = pElem.getNTerms();
		busName = Utilities.pad(Utilities.stripExtension(pElem.getFirstBus()), maxBusNameLength);
		pw.print(Utilities.pad(Utilities.fullName(pElem), maxDeviceNameLength + 2) + " ");
		for (j = 0; j < nTerm; j++) {
			pw.print(busName + " ");
			busName = Utilities.pad(Utilities.stripExtension(pElem.getNextBus()), maxBusNameLength);
		}
		pw.println();
	}

	/**
	 * Show elements and bus connections.
	 */
	public static void showElements(String fileName, String className) {
		FileWriter fw;
		PrintWriter pw;
		FileWriter fwDisabled;
		PrintWriter pwDisabled;
		int i;
		String disabledFileName;

		DSSGlobals globals = DSSGlobals.getInstance();
		Circuit ckt = globals.getActiveCircuit();

		setMaxBusNameLength();
		setMaxDeviceNameLength();

		try {
			fw = new FileWriter(fileName);
			pw = new PrintWriter(fw);

			disabledFileName = Utilities.stripExtension(fileName) + "_Disabled.txt";
			fwDisabled = new FileWriter(disabledFileName);
			pwDisabled = new PrintWriter(fw);

			if (className.length() > 0) {
				// Just give a list of active elements of a particular class
				if (DSSClassDefs.setObjectClass(className)) {
					pw.println("All Elements in Class \"" + className + "\"");
					pw.println();
					pwDisabled.println("All DISABLED Elements in Class \"" + className + "\"");
					pwDisabled.println();
					globals.setActiveDSSClass( globals.getDSSClassList().get(globals.getLastClassReferenced()) );
					for (i = 0; i < globals.getActiveDSSClass().getElementCount(); i++) {
						globals.getActiveDSSClass().setActiveElement(i);
						if ((globals.getActiveDSSClass().getDSSClassType() & DSSClassDefs.BASECLASSMASK) > 0) {
							if (((CktElement) globals.getActiveDSSObject()).isEnabled()) {
								pw.println(globals.getActiveDSSObject().getName());
							} else {
								pwDisabled.println(globals.getActiveDSSObject().getName());
							}
						} else {
							pw.println(globals.getActiveDSSObject().getName());  // non cktelements
						}
					}
				}
			} else {
				// Default - just do PD and PC Element in active circuit

				pw.println();
				pw.println("Elements in Active Circuit: " + ckt.getName());
				pw.println();
				pw.println("Power Delivery Elements");
				pw.println();
				pw.println(Utilities.pad("Element", maxDeviceNameLength + 2) + Utilities.pad(" Bus1", maxBusNameLength) + Utilities.pad(" Bus2", maxBusNameLength) + Utilities.pad(" Bus3", maxBusNameLength) + " ...");
				pw.println();


				pwDisabled.println();
				pwDisabled.println("DISABLED Elements in Active Circuit: " + ckt.getName());
				pwDisabled.println();
				pwDisabled.println("DISABLED Power Delivery Elements");
				pwDisabled.println();
				pwDisabled.println(Utilities.pad("DISABLED Element", maxDeviceNameLength + 2) + Utilities.pad(" Bus1", maxBusNameLength) + Utilities.pad(" Bus2", maxBusNameLength) + Utilities.pad(" Bus3", maxBusNameLength) + " ...");
				pwDisabled.println();

				// PD elements first
				for (CktElement pElem : ckt.getPDElements()) {
					if (pElem.isEnabled()) {
						writeElementRecord(pw, pElem);
					} else {
						writeElementRecord(pwDisabled, pElem);
					}
				}

				pw.println();
				pw.println("Power Conversion Elements");
				pw.println();
				pw.println(Utilities.pad("Element", maxDeviceNameLength + 2) + Utilities.pad(" Bus1", maxBusNameLength) + Utilities.pad(" Bus2", maxBusNameLength) + Utilities.pad(" Bus3", maxBusNameLength) + " ...");
				pw.println();

				pwDisabled.println();
				pwDisabled.println("DISABLED Power Conversion Elements");
				pwDisabled.println();
				pwDisabled.println(Utilities.pad("DISABLED Element", maxDeviceNameLength + 2) + Utilities.pad(" Bus1", maxBusNameLength) + Utilities.pad(" Bus2", maxBusNameLength) + Utilities.pad(" Bus3", maxBusNameLength) + " ...");
				pwDisabled.println();

				// PC elements next
				for (CktElement pElem : ckt.getPCElements()) {
					if (pElem.isEnabled()) {
						writeElementRecord(pw, pElem);
					} else {
						writeElementRecord(pwDisabled, pElem);
					}
				}
			}

			pw.close();
			fw.close();
			pwDisabled.close();
			fwDisabled.close();

			Utilities.fireOffEditor(disabledFileName);
			Utilities.fireOffEditor(fileName);
		} catch (IOException e) {
			// TODO: handle exception
		}
	}

	/**
	 * Show bus names and nodes in uses.
	 */
	public static void showBuses(String fileName) {
		FileWriter fw;
		PrintWriter pw;
		int i, j;
		Bus pBus;

		Circuit ckt = DSSGlobals.getInstance().getActiveCircuit();

		try {
			setMaxBusNameLength();
			maxBusNameLength += 2;

			fw = new FileWriter(fileName);
			pw = new PrintWriter(fw);

			pw.println();
			pw.println("BUSES AND NODES IN ACTIVE CIRCUIT: " + ckt.getName());
			pw.println();
			pw.println(Utilities.pad("     ", maxBusNameLength) + "                         Coord                        Number of     Nodes ");
			pw.println(Utilities.pad("  Bus", maxBusNameLength) + "    Base kV             (x, y)            Keep?       Nodes      connected ...");
			pw.println();
			for (i = 0; i < ckt.getNumBuses(); i++) {
				pw.print(Utilities.pad(Utilities.encloseQuotes(ckt.getBusList().get(i)), maxBusNameLength) + " ");
				pBus = ckt.getBuses()[i];
				if (pBus.getKVBase() > 0.0) {
					pw.print(pBus.getKVBase() * DSSGlobals.SQRT3);
				} else {
					pw.print("   NA ");
				}
				pw.print("          (");
				if (pBus.isCoordDefined()) {
					pw.printf(" %-13.11g, %-13.11g)", pBus.getX(), pBus.getY());
				} else {
					pw.print("           NA,            NA )");
				}
				if (pBus.isKeep()) {
					pw.print("     Yes  ");
				} else {
					pw.print("     No  ");
				}
				pw.print("     ");
				pw.print(pBus.getNumNodesThisBus());
				pw.print("       ");
				for (j = 0; j < pBus.getNumNodesThisBus(); j++) {
					pw.print(pBus.getNum(j) + " ");
				}
				pw.println();
			}

			pw.close();
			fw.close();

			Utilities.fireOffEditor(fileName);
		} catch (IOException e) {
			// TODO: handle exception
		}
	}

	/**
	 * Show values of meter elements.
	 */
	public static void showMeters(String fileName) {
		FileWriter fw;
		PrintWriter pw;
		int i, j;
		EnergyMeter meterClass;

		Circuit ckt = DSSGlobals.getInstance().getActiveCircuit();

		try {
			fw = new FileWriter(fileName);
			pw = new PrintWriter(fw);

			pw.println();
			pw.println("ENERGY METER VALUES");
			pw.println();
			pw.println("Registers:");
			meterClass = (EnergyMeter) DSSClassDefs.getDSSClass("Energymeter");
			if (meterClass == null)
				return;
			if (meterClass.getElementCount() == 0) {
				pw.println("No Energymeter Elements Defined.");
			} else {
				EnergyMeterObj pMeter = ckt.getEnergyMeters().get(0);  // write registernames for first meter only
				for (i = 0; i < EnergyMeter.NUM_EM_REGISTERS; i++)
					pw.println("Reg " + String.valueOf(i) + " = " + pMeter.getRegisterNames()[i]);
				pw.println();

				if (pMeter != null) {
					pw.print("Meter        ");
					for (i = 0; i < EnergyMeter.NUM_EM_REGISTERS; i++)
						pw.print(Utilities.pad("   Reg " + String.valueOf(i), 11));
					pw.println();
					pw.println();
				}

				for (EnergyMeterObj pElem : ckt.getEnergyMeters()) {
					if (pElem != null) {
						if (pElem.isEnabled()) {
							pw.print(Utilities.pad(pElem.getName(), 12));
							for (j = 0; j < EnergyMeter.NUM_EM_REGISTERS; j++)
								pw.print(pElem.getRegisters()[j] + " ");
						}
					}
					pw.println();
				}
			}

			pw.close();
			fw.close();

			Utilities.fireOffEditor(fileName);
		} catch (IOException e) {
			// TODO: handle exception
		}
	}

	/**
	 * Show values of generator meter elements
	 */
	public static void showGenMeters(String fileName) {
		FileWriter fw;
		PrintWriter pw;
		int i, j;
		Generator generatorClass;

		Circuit ckt = DSSGlobals.getInstance().getActiveCircuit();

		try {
			fw = new FileWriter(fileName);
			pw = new PrintWriter(fw);

			pw.println();
			pw.println("GENERATOR ENERGY METER VALUES");
			pw.println();

			GeneratorObj pGen = ckt.getGenerators().get(0);
			if (pGen != null) {
				generatorClass = (Generator) pGen.getParentClass();
				pw.print("Generator          ");
				for (i = 0; i < Generator.NumGenRegisters; i++)
					pw.print(Utilities.pad(generatorClass.getRegisterNames()[i], 11));
				pw.println();
				pw.println();
			}

			for (GeneratorObj pElem : ckt.getGenerators()) {
				if (pElem != null) {
					if (pElem.isEnabled()) {
						pw.print(Utilities.pad(pElem.getName(), 12));
						for (j = 0; j < Generator.NumGenRegisters; j++) {
							pw.print(pElem.getRegisters()[j] + " ");
						}
					}
				}
				pw.println();
			}

			pw.close();
			fw.close();

			Utilities.fireOffEditor(fileName);
		} catch (IOException e) {
			// TODO: handle exception
		}
	}

	/**
	 * Assumes 0 is 1.0 per unit tap.
	 */
	private static int tapPosition(TransformerObj xfmr, int iWind) {
		return (int) Math.round((xfmr.getPresentTap(iWind) - 1.0) / xfmr.getTapIncrement(iWind));
	}

	public static void showRegulatorTaps(String fileName) {
		FileWriter fw;
		PrintWriter pw;
		int iWind;

		try {
			fw = new FileWriter(fileName);
			pw = new PrintWriter(fw);

			pw.println();
			pw.println("CONTROLLED TRANSFORMER TAP SETTINGS");
			pw.println();
			pw.println("Name            Tap      Min       Max     Step  Position");
			pw.println();

			Circuit ckt = DSSGlobals.getInstance().getActiveCircuit();

			for (RegControlObj pReg : ckt.getRegControls()) {
				TransformerObj t = pReg.getTransformer();
				iWind = pReg.getWinding();
				pw.print(Utilities.pad(t.getName(), 12) + " ");
				pw.printf("%8.5f %8.5f %8.5f %8.5f     %d", t.getPresentTap(iWind), t.getMinTap(iWind), t.getMaxTap(iWind), t.getTapIncrement(iWind), tapPosition(pReg.getTransformer(), iWind));
				pw.println();
			};

			pw.close();
			fw.close();

			Utilities.fireOffEditor(fileName);
		} catch (IOException e) {
			// TODO: handle exception
		}
	}

	public static void showMeterZone(String fileName) {
		FileWriter fw;
		PrintWriter pw;
		int i;
		EnergyMeterObj pMtr;
		EnergyMeter pMtrClass;
		PDElement PDElem;
		LoadObj loadElem;
		String paramName;
		String param;

		DSSGlobals globals = DSSGlobals.getInstance();
		Parser parser = Parser.getInstance();

		try {
			fileName = Utilities.stripExtension(fileName);
			paramName = parser.getNextParam();
			param = parser.makeString();

			fileName = fileName+"_"+param+".txt";

			fw = new FileWriter(fileName);
			pw = new PrintWriter(fw);

			globals.setGlobalResult(fileName);

			pMtrClass = (EnergyMeter) globals.getDSSClassList().get(globals.getClassNames().find("energymeter"));

			if (param.length() > 0) {
				pMtr = (EnergyMeterObj) pMtrClass.find(param);  // FIXME make generic
				if (pMtr == null) {
					globals.doSimpleMsg("EnergyMeter \"" + param + "\" not found.", 220);
				} else {
					if (pMtr.getBranchList() != null) {
						pw.println("Branches and Load in Zone for EnergyMeter " + param);
						pw.println();

						PDElem = (PDElement) pMtr.getBranchList().getFirst();
						while (PDElem != null) {
							for (i = 0; i < pMtr.getBranchList().getLevel(); i++)
								pw.print(TABCHAR);
							//F.print(pMtr.getBranchList().getLevel() +" ");
							pw.print(PDElem.getParentClass().getName() + "." + PDElem.getName());
							CktTreeNode pb = pMtr.getBranchList().getPresentBranch();
							if (pb.isParallel())
								pw.print("(PARALLEL:" + ((CktElement) pb.getLoopLineObj()).getName()+")");
							if (pb.isLoopedHere())
								pw.print("(LOOP:" + ((CktElement) pb.getLoopLineObj()).getParentClass().getName()+"."+((CktElement) pb.getLoopLineObj()).getName()+")");

							if (PDElem.getSensorObj() != null) {
								pw.printf(" (Sensor: %s.%s) ", PDElem.getSensorObj().getParentClass().getName(), PDElem.getSensorObj().getName());
							} else {
								pw.print(" (Sensor: NIL)");
							}
							pw.println();
							loadElem = (LoadObj) pMtr.getBranchList().getFirstObject();
							while (loadElem != null) {
								for (i = 0; i < pMtr.getBranchList().getLevel() + 1; i++)
									pw.print(TABCHAR);
								pw.print(loadElem.getParentClass().getName() + "." + loadElem.getName());
								if (loadElem.getSensorObj() != null) {
									pw.printf(" (Sensor: %s.%s) ", loadElem.getSensorObj().getParentClass().getName(), loadElem.getSensorObj().getName());
								} else {
									pw.print(" (Sensor: NIL)");
								}
								pw.println();
								loadElem = (LoadObj) pMtr.getBranchList().getNextObject();
							}
							PDElem = (PDElement) pMtr.getBranchList().goForward();
						}
					}
				}
			} else {
				globals.doSimpleMsg("Meter name not specified."+ DSSGlobals.CRLF + parser.getCmdString(), 221);
			}

			pw.close();
			fw.close();
		} catch (IOException e) {
			// TODO: handle exception
		} finally {
			paramName = parser.getNextParam();
			param = parser.makeString();

			switch (param.length()) {
			case 0:
				Utilities.fireOffEditor(fileName);
				break;
			default:
				globals.getDSSForms().showTreeView(fileName);
				break;
			}
		}
	}

	public static void showOverloads(String fileName) {
		FileWriter fw;
		PrintWriter pw;
		Complex[] cBuffer;  // allocate to max total conductors
		int NCond, i, j, k;
		Complex[] Iph = new Complex[3];
		Complex[] I012 = new Complex[3];
		double I0, I1, I2, Cmag, Cmax;

		Circuit ckt = DSSGlobals.getInstance().getActiveCircuit();

		setMaxDeviceNameLength();

		try {
			fw = new FileWriter(fileName);
			pw = new PrintWriter(fw);

			/* Allocate cBuffer big enough for largest circuit element */
			cBuffer = new Complex[Utilities.getMaxCktElementSize()];

			/* Sequence currents */
			pw.println();
			pw.println("Power Delivery Element Overload Report");
			pw.println();
			pw.println("SYMMETRICAL COMPONENT CURRENTS BY CIRCUIT ELEMENT ");
			pw.println();
			pw.println("Element                      Term    I1      I2    %I2/I1    I0    %I0/I1 %Normal   %Emergency");
			pw.println();

			// PD elements
			for (PDElement PDElem : ckt.getPDElements()) {
				if (PDElem.isEnabled())
					if ((DSSClassDefs.CLASSMASK & PDElem.getDSSObjType()) != DSSClassDefs.CAP_ELEMENT) {  // ignore capacitors
						NCond = PDElem.getNConds();
						PDElem.getCurrents(cBuffer);

						for (j = 0; j < 1; j++) {  // check only terminal 1 for overloads
							if (PDElem.getNPhases() >= 3) {
								Cmax = 0.0;
								for (i = 0; i < 3; i++) {
									k = (j - 1) * NCond + i;
									Iph[i] = cBuffer[k];
									Cmag = Iph[i].abs();
									if (Cmag > Cmax)
										Cmax = Cmag;
								}
								MathUtil.phase2SymComp(Iph, I012);
								I0 = I012[0].abs();
								I1 = I012[1].abs();
								I2 = I012[2].abs();
							} else {
								I0 = 0.0;
								I1 = cBuffer[1 + (j - 1) * NCond].abs();
								I2 = 0.0;
								Cmax = I1;
							}

							if ((PDElem.getNormAmps() > 0.0) || (PDElem.getEmergAmps() > 0.0)) {
								if ((Cmax > PDElem.getNormAmps()) || (Cmax > PDElem.getEmergAmps())) {
									pw.print(Utilities.pad(Utilities.fullName(PDElem), maxDeviceNameLength + 2) + j);
									pw.print(I1);
									pw.print(I2);
									if (I1 > 0.0) {
										pw.print(100.0 * I2 / I1);
									} else {
										pw.print("     0.0");
									}
									pw.print(I0);
									if (I1 > 0.0) {
										pw.print(100.0 * I0 / I1);
									} else {
										pw.print("     0.0");
									}
									if (PDElem.getNormAmps() > 0.0) {
										pw.print(Cmax / PDElem.getNormAmps() * 100.0);
									} else {
										pw.print("     0.0");
									}
									if (PDElem.getEmergAmps() > 0.0) {
										pw.print(Cmax / PDElem.getEmergAmps() * 100.0);
									} else {
										pw.print("     0.0");
									}
									pw.println();
								}
							}
						}
					}
			}

			pw.close();
			fw.close();

			Utilities.fireOffEditor(fileName);
		} catch (IOException e) {
			// TODO: handle exception
		}
	}

	public static void showUnserved(String fileName, boolean UE_Only) {
		FileWriter fw;
		PrintWriter pw;
//		LoadObj PLoad;
		boolean doIt;

		Circuit ckt = DSSGlobals.getInstance().getActiveCircuit();

		try {
			fw = new FileWriter(fileName);
			pw = new PrintWriter(fw);

			pw.println();
			pw.println("UNSERVED  LOAD  REPORT");
			pw.println();
			pw.println("Load Element        Bus        Load kW  EEN Factor  UE Factor");
			pw.println();

			// load
			for (LoadObj pLoad : ckt.getLoads()) {
				if (pLoad.isEnabled()) {
					doIt = false;
					if (UE_Only) {
						if (pLoad.getUnserved())
							doIt = true;
					} else {
						if (pLoad.getExceedsNormal())
							doIt = true;
					}

					if (doIt) {
						pw.print(Utilities.pad(pLoad.getName(), 20));
						pw.print(Utilities.pad(pLoad.getBus(1), 10));
						pw.print(pLoad.getKWBase());
						pw.print(pLoad.getEEN_Factor());
						pw.print(pLoad.getUE_Factor());
						pw.println();
					}
				}
			}

			pw.close();
			fw.close();

			Utilities.fireOffEditor(fileName);
		} catch (IOException e) {
			// TODO: handle exception
		}
	}

	public static void showLosses(String fileName) {
		FileWriter fw;
		PrintWriter pw;

		Complex kLosses,
			totalLosses,
			lineLosses,
			transLosses,
			termPower,
			loadPower;

		Circuit ckt = DSSGlobals.getInstance().getActiveCircuit();

		setMaxDeviceNameLength();

		try {
			fw = new FileWriter(fileName);
			pw = new PrintWriter(fw);

			/* Sequence currents */
			pw.println();
			pw.println("LOSSES REPORT");
			pw.println();
			pw.println("Power Delivery Element Loss Report");
			pw.println();
			pw.println("Element                  kW Losses    % of Power   kvar Losses");
			pw.println();

			totalLosses = Complex.ZERO;
			lineLosses  = Complex.ZERO;
			transLosses = Complex.ZERO;

			// PD elements
			for (PDElement PDElem : ckt.getPDElements()) {
				if (PDElem.isEnabled()) {
					/*if ((DSSClassDefs.CLASSMASK & PDElem.getDSSObjType()) != DSSClassDefs.CAP_ELEMENT) {*/    // Ignore capacitors
					kLosses = PDElem.getLosses().multiply(0.001);   // kW Losses in element
					totalLosses = totalLosses.add(kLosses);
					termPower = PDElem.getPower(1).multiply(0.001);  // terminal 1 power  TODO Check zero based indexing

					if ((DSSClassDefs.CLASSMASK & PDElem.getDSSObjType()) == DSSClassDefs.XFMR_ELEMENT)
						transLosses = transLosses.add(kLosses);
					if ((DSSClassDefs.CLASSMASK & PDElem.getDSSObjType()) == DSSClassDefs.LINE_ELEMENT)
						lineLosses = lineLosses.add(kLosses);

					pw.print(Utilities.pad(Utilities.fullName(PDElem), maxDeviceNameLength + 2));
					pw.printf("%10.5f, ", kLosses.getReal());
					if ((termPower.getReal() > 0.0) && (kLosses.getReal() > 0.0009)) {
						pw.print(kLosses.getReal() / Math.abs(termPower.getReal()) * 100.0);
					} else {
						pw.print(Complex.ZERO.getReal());
					}
					pw.printf("     %.6g", kLosses.getImaginary());
					pw.println();
				}
			}

			pw.println();
			pw.println(Utilities.pad("LINE LOSSES=", 30) + lineLosses.getReal() + " kW");
			pw.println(Utilities.pad("TRANSFORMER LOSSES=", 30) + transLosses.getReal() + " kW");
			pw.println();
			pw.println(Utilities.pad("TOTAL LOSSES=", 30) + totalLosses.getReal() + " kW");

			loadPower = Complex.ZERO;
			// sum the total load kW being served in the circuit model
			for (PCElement PCElem : ckt.getLoads()) {
				if (PCElem.isEnabled()) {
					loadPower = loadPower.add(PCElem.getPower(1));
				}
			}
			loadPower = loadPower.multiply(0.001);

			pw.println();
			pw.println(Utilities.pad("TOTAL LOAD POWER = ", 30) + Math.abs(loadPower.getReal()) + " kW");
			pw.print(Utilities.pad("Percent Losses for Circuit = ", 30));
			if (loadPower.getReal() != 0.0)
				pw.println(Math.abs(totalLosses.getReal() / loadPower.getReal()) * 100.0 + " %");

			pw.close();
			fw.close();

			Utilities.fireOffEditor(fileName);
		} catch (IOException e) {
			// TODO: handle exception
		}
	}

	public static void showVariables(String fileName) {
		FileWriter fw;
		PrintWriter pw;
		int i;

		Circuit ckt = DSSGlobals.getInstance().getActiveCircuit();

		try {
			fw = new FileWriter(fileName);
			pw = new PrintWriter(fw);

			/* Sequence currents */
			pw.println();
			pw.println("VARIABLES REPORT");
			pw.println();
			pw.println("Present values of all variables in PC Elements in the circuit.");
			pw.println();

			for (PCElement PCElem : ckt.getPCElements()) {
				if (PCElem.isEnabled() && (PCElem.numVariables() > 0)) {
					pw.println("ELEMENT: " + PCElem.getParentClass().getName() + "." + PCElem.getName());
					pw.println("No. of variables: " + PCElem.numVariables());
					for (i = 0; i < PCElem.numVariables(); i++)
						pw.println("  " + PCElem.variableName(i) + " = " + String.format("%-.6g", PCElem.getVariable(i)));
					pw.println();
				}
			}
			pw.close();
			fw.close();

			Utilities.fireOffEditor(fileName);
		} catch (IOException e) {
			// TODO: handle exception
		}
	}

	/**
	 * Show isolated buses/branches in present circuit.
	 */
	public static void showIsolated(String fileName) {
		CktTree branchList, subArea;  // all circuit elements

		FileWriter fw;
		PrintWriter pw;
		CktElement testElem, testBranch, pElem;

		int i, j;

		Circuit ckt = DSSGlobals.getInstance().getActiveCircuit();

		// make sure bus list is built
		if (ckt.isBusNameRedefined()) ckt.reProcessBusDefs();
		/* Initialize all circuit elements to not checked */
		for (CktElement TestElement : ckt.getCktElements()) {
			TestElement.setChecked(false);
			for (i = 0; i < TestElement.getNTerms(); i++) {
				TestElement.getTerminals()[i].setChecked(false);
			}
		}

		// initialize the checked flag for all buses
		for (j = 0; j < ckt.getNumBuses(); j++)
			ckt.getBuses()[j].setBusChecked(false);

		// get started at main voltage source
		testElem = ckt.getSources().get(0);
		branchList = CktTreeImpl.getIsolatedSubArea(testElem);

		/* Show report of elements connected and not connected */
		try {
			fw = new FileWriter(fileName);
			pw = new PrintWriter(fw);

			pw.println();
			pw.println("ISOLATED CIRCUIT ELEMENT REPORT");
			pw.println();
			pw.println();
			pw.println("***  THE FOLLOWING BUSES HAVE NO CONNECTION TO THE SOURCE ***");
			pw.println();

			for (j = 0; j < ckt.getNumBuses(); j++) {
				if (!ckt.getBuses()[j].isBusChecked())
					pw.println(Utilities.encloseQuotes(ckt.getBusList().get(j)));
			}

			pw.println();
			pw.println("***********  THE FOLLOWING SUB NETWORKS ARE ISOLATED ************");
			pw.println();

			for (CktElement TestElement : ckt.getCktElements()) {
				if (TestElement.isEnabled())
					if (!TestElement.isChecked())
						if ((TestElement.getDSSObjType() & DSSClassDefs.BASECLASSMASK) == DSSClassDefs.PD_ELEMENT) {
							subArea = CktTreeImpl.getIsolatedSubArea(TestElement);
							pw.println("*** START SUBAREA ***");

							testBranch = (CktElement) subArea.getFirst();  // TODO Implement
							while (testBranch != null) {
								pw.println("(" + subArea.getLevel() + ") " + testBranch.getParentClass().getName() + "." + testBranch.getName());
								pElem = (CktElement) subArea.getFirstObject();
								while (pElem != null) {
									pw.println("[SHUNT], " + pElem.getParentClass().getName() + "." + pElem.getName());
									pElem = (CktElement) subArea.getNextObject();
								}
								testBranch = (CktElement) subArea.goForward();
							}
							subArea = null;
							pw.println();
						}
			}

			pw.println();
			pw.println("***********  THE FOLLOWING ENABLED ELEMENTS ARE ISOLATED ************");
			pw.println();

			/* Mark all controls, energy meters and monitors as checked so they don"t show up */

			for (i = 0; i < ckt.getDSSControls().size(); i++)
				((CktElement) ckt.getDSSControls().get(i)).setChecked(true);
			for (i = 0; i < ckt.getMeterElements().size(); i++)
				((CktElement) ckt.getMeterElements().get(i)).setChecked(true);

			for (CktElement TestElement : ckt.getCktElements()) {
				if (TestElement.isEnabled()) {
					if (!TestElement.isChecked()) {
						pw.print("\"" + TestElement.getParentClass().getName() + "." + TestElement.getName() + "\"");
						pw.print("  Buses:");
						for (j = 0; j < TestElement.getNTerms(); j++)
							pw.print("  \"" + TestElement.getBus(j) + "\"");
						pw.println();
					}
				}
			}

			pw.println();
			pw.println("***  THE FOLLOWING BUSES ARE NOT CONNECTED TO ANY POWER DELIVERY ELEMENT ***");
			pw.println();

			for (j = 0; j < ckt.getNumBuses(); j++) {
				if (!ckt.getBuses()[j].isBusChecked())
					pw.println(Utilities.encloseQuotes(ckt.getBusList().get(j)));
			}

			pw.println();
			pw.println("***********  CONNECTED CIRCUIT ELEMENT TREE ************");
			pw.println();
			pw.println("(Lexical Level) Element name");
			pw.println();

			testBranch = (CktElement) branchList.getFirst();  // FIXME Make generic
			while (testBranch != null) {
				pw.println("(" + branchList.getLevel() + ") " + testBranch.getParentClass().getName() + "." + testBranch.getName());
				testElem = (CktElement) branchList.getFirstObject();
				while (testElem != null) {
					pw.println("[SHUNT], " + testElem.getParentClass().getName() + "." + testElem.getName());
					testElem = (CktElement) branchList.getNextObject();
				}
				testBranch = (CktElement) branchList.goForward();
			}

			branchList = null;
			pw.close();
			fw.close();

			Utilities.fireOffEditor(fileName);
		} catch (IOException e) {
			// TODO: handle exception
		}
	}

	public static void showRatings(String fileName) {
		FileWriter fw;
		PrintWriter pw;

		Circuit ckt = DSSGlobals.getInstance().getActiveCircuit();

		try {
			fw = new FileWriter(fileName);
			pw = new PrintWriter(fw);

			pw.println("Power Delivery Elements Normal and Emergency (max) Ratings");
			pw.println();

			for (PDElement PDElem : ckt.getPDElements()) {
				pw.print("\"" + PDElem.getParentClass().getName() + "." + PDElem.getName() + "\" normamps=");
				pw.printf("%-.4g,  %-.4g  !Amps", PDElem.getNormAmps(), PDElem.getEmergAmps());
				pw.println();

			}
			pw.close();
			fw.close();

			Utilities.fireOffEditor(fileName);
		} catch (IOException e) {
			// TODO: handle exception
		}
	}

	/**
	 * Show loops and paralleled branches in meter zones.
	 */
	public static void showLoops(String fileName) {
		FileWriter fw;
		PrintWriter pw;
		PDElement PDElem;
		int hMeter;
		EnergyMeterObj pMtr;

		DSSGlobals globals = DSSGlobals.getInstance();

		try {
			fw = new FileWriter(fileName);
			pw = new PrintWriter(fw);

			pw.println("Loops and Paralleled Lines in all EnergyMeter Zones");
			pw.println();

			hMeter = globals.getEnergyMeterClass().getFirst();

			while (hMeter > 0) {

				pMtr = (EnergyMeterObj) globals.getActiveDSSObject();

				if (pMtr.getBranchList() != null) {

					PDElem = (PDElement) pMtr.getBranchList().getFirst();
					while (PDElem != null) {

						CktTreeNode pb = pMtr.getBranchList().getPresentBranch();
						if (pb.isParallel())
							pw.println("(" + pMtr.getName() + ") " + PDElem.getParentClass().getName() + "." + PDElem.getName() +": PARALLEL WITH " + ((CktElement) pb.getLoopLineObj()).getParentClass().getName() + "." + ((CktElement) pb.getLoopLineObj()).getName());
						if (pb.isLoopedHere())
							pw.println("(" + pMtr.getName() + ") " + PDElem.getParentClass().getName() + "." + PDElem.getName() + ": LOOPED TO     " + ((CktElement) pb.getLoopLineObj()).getParentClass().getName() + "." + ((CktElement) pb.getLoopLineObj()).getName());

						PDElem = (PDElement) pMtr.getBranchList().goForward();
					}
				}

				hMeter = globals.getEnergyMeterClass().getNext();
			}
			pw.close();
			fw.close();

			Utilities.fireOffEditor(fileName);
		} catch (IOException e) {
			// TODO: handle exception
		}
	}

	private static void topoLevelTabs(PrintWriter pw, int nLevel) {
		int nTabs, i;

		nTabs = 30;
		if (nLevel < nTabs) nTabs = nLevel;
		for (i = 0; i < nTabs; i++)
			pw.print(TABCHAR);
		if (nLevel > nTabs)
			pw.printf("(* %d *)", nLevel);
	}

	public static void showTopology(String fileRoot) {
		FileWriter fw;
		PrintWriter pw;
		FileWriter fwTree;
		PrintWriter pwTree;
		String fileName, treeName;
		PDElement PDElem;
		LoadObj loadElem;
		CktTree topo;
		int nLoops, nParallel, nLevels, nIsolated, nSwitches;

		Circuit ckt = DSSGlobals.getInstance().getActiveCircuit();

		try {
			fileName = fileRoot + "TopoSumm.txt";
			treeName = fileRoot + "TopoTree.txt";

			fw = new FileWriter(fileName);
			pw = new PrintWriter(fw);
			pw.println("Topology analysis for switch control algorithms");
			pw.println();

			fwTree = new FileWriter(treeName);
			pwTree = new PrintWriter(fwTree);
			pwTree.println("Branches and Loads in Circuit " + ckt.getName());
			pwTree.println();

			topo = ckt.getTopology();
			nLoops = 0;
			nParallel = 0;
			nLevels = 0;
			nIsolated = 0;
			nSwitches = 0;

			if (topo != null) {
				PDElem = (PDElement) topo.getFirst();
				if (topo.getLevel() > nLevels)
					nLevels = topo.getLevel();
				topoLevelTabs(pwTree, topo.getLevel());
				pwTree.print(PDElem.getParentClass().getName() + "." + PDElem.getName());
				CktTreeNode pb = topo.getPresentBranch();
				if (pb.isParallel()) {
					nParallel += 1;
					pwTree.print("(PARALLEL:" + ((CktElement) pb.getLoopLineObj()).getName() + ")");
				}
				if (pb.isLoopedHere()) {
					nLoops++;
					pwTree.print("(LOOP:" + ((CktElement) pb.getLoopLineObj()).getParentClass().getName()
					+"."+((CktElement) pb.getLoopLineObj()).getName()+")");
				}
				if (PDElem.hasSensorObj()) {
					pwTree.printf(" (Sensor: %s.%s) ",
							PDElem.getSensorObj().getParentClass().getName(), PDElem.getSensorObj().getName());
				}
				if (PDElem.hasControl()) {
					pwTree.printf(" (Control: %s.%s) ",
							PDElem.getControlElement().getParentClass().getName(), PDElem.getControlElement().getName());
					if ((PDElem.getControlElement().getDSSObjType() & DSSClassDefs.CLASSMASK) == DSSClassDefs.SWT_CONTROL)
						nSwitches++;
				}
				if (PDElem.hasEnergyMeter())
					pwTree.printf(" (Meter: %s) ", PDElem.getMeterObj().getName());
				pwTree.println();

				loadElem = (LoadObj) topo.getFirstObject();
				while (loadElem != null) {
					topoLevelTabs(pwTree, topo.getLevel() + 1);
					pwTree.print(loadElem.getParentClass().getName() + "." + loadElem.getName());
					if (loadElem.hasSensorObj())
						pwTree.printf(" (Sensor: %s.%s) ",
								loadElem.getSensorObj().getParentClass().getName(), loadElem.getSensorObj().getName());
					if (loadElem.hasControl()) {
						pwTree.printf(" (Control: %s.%s) ",
								loadElem.getControlElement().getParentClass().getName(), loadElem.getControlElement().getName());
						if ((loadElem.getControlElement().getDSSObjType() & DSSClassDefs.CLASSMASK) == DSSClassDefs.SWT_CONTROL)
							nSwitches++;
					}
					if (loadElem.hasEnergyMeter()) {
						pwTree.printf(" (Meter: %s) ", loadElem.getMeterObj().getName());
						pwTree.println();
						loadElem = (LoadObj) topo.getNextObject();
					}

					PDElem = (PDElement) topo.goForward();
				}
			}

			for (PDElement PDElemt : ckt.getPDElements()) {
				if (PDElemt.isIsolated()) {
					pwTree.printf("Isolated: %s.%s", PDElemt.getParentClass().getName(), PDElemt.getName());
					if (PDElemt.hasSensorObj()) {
						pwTree.printf(" (Sensor: %s.%s) ",
								PDElemt.getSensorObj().getParentClass().getName(), PDElemt.getSensorObj().getName());
					}
					if (PDElemt.hasControl()) {
						pwTree.printf(" (Control: %s.%s) ",
								PDElemt.getControlElement().getParentClass().getName(), PDElemt.getControlElement().getName());

						if ((PDElemt.getControlElement().getDSSObjType() & DSSClassDefs.CLASSMASK) == DSSClassDefs.SWT_CONTROL)
							nSwitches++;
					}
					if (PDElemt.hasEnergyMeter()) {
						pwTree.printf(" (Meter: %s) ", PDElemt.getMeterObj().getName());
						pwTree.println();
						nIsolated += 1;
					}
				}
			}

			nLoops = nLoops / 2;  // TODO, see if parallel lines also counted twice
			pw.println(String.format("%d Levels Deep", nLevels));
			pw.println(String.format("%d Loops", nLoops));
			pw.println(String.format("%d Parallel PD elements", nParallel));
			pw.println(String.format("%d Isolated PD components", nIsolated));
			pw.println(String.format("%d Controlled Switches", nSwitches));

			pw.close();
			fw.close();
			pwTree.close();
			fwTree.close();

			Utilities.fireOffEditor(fileName);
			DSSGlobals.getInstance().getDSSForms().showTreeView(treeName);
		} catch (IOException e) {
			// TODO: handle exception
		}
	}

	public static void showLineConstants(String fileNm, double freq, int units, double rho) {
		FileWriter fw;
		PrintWriter pw;
		FileWriter fw2;
		PrintWriter pw2;
		int p;
		LineGeometryObj pElem;
		CMatrix Z, Yc;
		int i, j;
		double w;
		Complex Zs, Zm,	Z1, Z0;
		double Cs, Cm;
		double C1, C0;
		Complex Ycm;
		double Xcm;
		double Ccm;  // common mode capacitance
		String lineCodesFileNm;

		DSSGlobals globals = DSSGlobals.getInstance();

		try {
			fw = new FileWriter(fileNm);
			pw = new PrintWriter(fw);

			pw.println("LINE CONSTANTS");
			pw.println(String.format("Frequency = %.6g Hz, Earth resistivity = %.6g ohm-m", freq, rho));
			pw.println("Earth Model = " + Utilities.getEarthModel(globals.getDefaultEarthModel()));
			pw.println();

			lineCodesFileNm = "LineConstantsCode.dss";
			fw2 = new FileWriter(lineCodesFileNm);
			pw2 = new PrintWriter(fw2);

			pw2.println("!--- OpenDSS Linecodes file generated from Show LINECONSTANTS command");
			pw2.println(String.format("!--- Frequency = %.6g Hz, Earth resistivity = %.6g ohm-m", freq, rho));
			pw2.println("!--- Earth Model = " + Utilities.getEarthModel(globals.getDefaultEarthModel()));

			LineImpl.lineGeometryClass = (LineGeometry) globals.getDSSClassList().get(globals.getClassNames().find("LineGeometry"));
			Z = null;
			Yc = null;

			globals.setActiveEarthModel(globals.getDefaultEarthModel());

			p = LineImpl.lineGeometryClass.getFirst();
			while (p > 0) {
				pElem = (LineGeometryObj) LineImpl.lineGeometryClass.getActiveObj();
				Z = null;
				Yc = null;

				try {
					// get impedances per unit length
					pElem.setRhoEarth(rho);
					Z  = pElem.getZMatrix(freq, 1.0, units);
					Yc = pElem.getYcMatrix(freq, 1.0, units);
				} catch (Exception e) {
					globals.doSimpleMsg("Error computing line constants for LineGeometry." + pElem.getName() +
							"; Error message: " + e.getMessage(), 9934);
				}

				pw.println();
				pw.println("--------------------------------------------------");
				pw.println("Geometry Code = " + pElem.getName());
				pw.println();
				pw.println("R MATRIX, ohms per " + LineUnits.lineUnitsStr(units));
				for (i = 0; i < Z.getNOrder(); i++) {
					for (j = 0; j < i; j++)
						pw.printf("%.6g, ", Z.getElement(i, j).getReal());
					pw.println();
				}

				pw.println();
				pw.println("jX MATRIX, ohms per " + LineUnits.lineUnitsStr(units));
				for (i = 0; i < Z.getNOrder(); i++) {
					for (j = 0; j < i; j++)
						pw.printf("%.6g, ", Z.getElement(i, j).getImaginary());
					pw.println();
				}

				pw.println();
				pw.println("Susceptance (jB) MATRIX, S per " + LineUnits.lineUnitsStr(units));
				for (i = 0; i < Yc.getNOrder(); i++) {
					for (j = 0; j < i; j++)
						pw.printf("%.6g, ", Yc.getElement(i, j).getImaginary());
					pw.println();
				}

				w = freq * DSSGlobals.TWO_PI / 1.e3;
				pw.println();
				pw.println("L MATRIX, mH per " + LineUnits.lineUnitsStr(units));
				for (i = 0; i < Z.getNOrder(); i++) {
					for (j = 0; j < i; j++)
						pw.printf("%.6g, ", Z.getElement(i, j).getImaginary() / w);
					pw.println();
				}

				w = freq * DSSGlobals.TWO_PI / 1.e9;
				pw.println();
				pw.println("C MATRIX, nF per " + LineUnits.lineUnitsStr(units));
				for (i = 0; i < Yc.getNOrder(); i++) {
					for (j = 0; j < i; j++)
						pw.printf("%.6g, ", Yc.getElement(i, j).getImaginary() / w);
					pw.println();
				}

				/* Write DSS line code record */
				//F.println();
				//F.println(,"-------------------------------------------------------------------");
				//F.println(,"-------------------DSS LineCode Definition-------------------------");
				//F.println(,"-------------------------------------------------------------------");
				pw2.println();

				pw2.println(String.format("new lineCode.%s nphases=%d  Units=%s", pElem.getName(), Z.getNOrder(), LineUnits.lineUnitsStr(units)));

				pw2.print("~ Rmatrix=[");
				for (i = 0; i < Z.getNOrder(); i++) {
					for (j = 0; j < i; j++)
						pw2.printf("%.6g  ", Z.getElement(i, j).getReal());
					if (i < Z.getNOrder()) pw2.print("|");
				}
				pw2.println("]");

				pw2.print("~ Xmatrix=[");
				for (i = 0; i < Z.getNOrder(); i++) {
					for (j = 0; j < i; j++)
						pw2.printf("%.6g  ", Z.getElement(i, j).getImaginary());
					if (i < Z.getNOrder()) pw2.print("|");
				}
				pw2.println("]");

				w = freq * DSSGlobals.TWO_PI /1.e9;
				pw2.print("~ Cmatrix=[");
				for (i = 0; i < Yc.getNOrder(); i++) {
					for (j = 0; j < i; j++)
						pw2.printf("%.6g  ", Yc.getElement(i, j).getImaginary() / w);
					if (i < Yc.getNOrder()) pw2.print("|");
				}
				pw2.println("]");

				/* Add pos- and zero-sequence approximation here
				 * Kron reduce to 3 phases first
				 * Average diagonals and off-diagonals
				 */

				Zs = Complex.ZERO;
				Zm = Complex.ZERO;
				Cs = 0.0;
				Cm = 0.0;

				if (Z.getNOrder() == 3) {
					pw.println();
					pw.println("-------------------------------------------------------------------");
					pw.println("-------------------Equiv Symmetrical Component --------------------");
					pw.println("-------------------------------------------------------------------");
					pw.println();
					for (i = 0; i < 3; i++)
						Zs = Zs.add( Z.getElement(i, i) );
					for (i = 0; i < 3; i++)
						for (j = 0; j < i-1; j++)  // TODO Check zero based indexing
							Zm = Zm.add( Z.getElement(i, j) );

					Z1 = Zs.subtract(Zm).divide(3.0);
					Z0 = Zm.multiply(2.0).add(Zs).divide(3.0);
					w = freq * DSSGlobals.TWO_PI / 1000.0;
					pw.println();
					pw.println("Z1, ohms per " + LineUnits.lineUnitsStr(units) + String.format(" = %.6g + j %.6g (L1 = %.6g mH) ", Z1.getReal(), Z1.getImaginary(), Z1.getImaginary() / w));
					pw.println("Z0, ohms per " + LineUnits.lineUnitsStr(units) + String.format(" = %.6g + j %.6g (L0 = %.6g mH) ", Z0.getReal(), Z0.getImaginary(), Z0.getImaginary() / w));
					pw.println();

					/* Compute common mode series impedance */
					Z.invert();
					Ycm = Complex.ZERO;
					for (i = 0; i < 3; i++)
						for (j = 0; j < 3; j++)
							Ycm = Ycm.add(Z.getElement(i, j));
					Xcm = Ycm.invert().getImaginary();

					w = freq * DSSGlobals.TWO_PI /1.e9;
					/* Capacitance */
					for (i = 0; i < 3; i++)
						Cs = Cs + Yc.getElement(i, i).getImaginary();
					for (i = 0; i < 3; i++)
						for (j = 0; j < i - 1; j++)
							Cm = Cm + Yc.getElement(i, j).getImaginary();

					C1 = (Cs - Cm) / 3.0 / w;   // nF
					C0 = (Cs + 2.0 * Cm) / 3.0 / w;

					/* Compute common mode shunt capacitance */
					Ycm = Complex.ZERO;
					for (i = 0; i < 3; i++)  // add up all elements of Z inverse
						for (j = 0; j < 3; j++)
							Ycm = Ycm.add(Yc.getElement(i, j));
					Ccm = Ycm.getImaginary() / w;

					pw.println("C1, nF per " + LineUnits.lineUnitsStr(units) + String.format(" = %.6g", C1));
					pw.println("C0, nF per " + LineUnits.lineUnitsStr(units) + String.format(" = %.6g", C0));
					pw.println();

					w = freq * DSSGlobals.TWO_PI;
					pw.println("Surge Impedance:");
					pw.println(String.format("  Positive sequence = %.6g ohms", Math.sqrt(Z1.getImaginary() / w / (C1 * 1.0e-9))));
					pw.println(String.format("  Zero sequence     = %.6g ohms", Math.sqrt(Z0.getImaginary() / w / (C0 * 1.0e-9))));
					pw.println(String.format("  Common Mode       = %.6g ohms", Math.sqrt(Xcm / w / (Ccm * 1.0e-9))));
					pw.println();

					pw.println("Propagation Velocity (Percent of speed of light):");
					pw.println(String.format("  Positive sequence = %.6g ", 1.0 / (Math.sqrt(Z1.getImaginary() / w * (C1 * 1.0e-9))) / 299792458.0 / LineUnits.toPerMeter(units) * 100.0));
					pw.println(String.format("  Zero sequence     = %.6g ", 1.0 / (Math.sqrt(Z0.getImaginary() / w * (C0 * 1.0e-9))) / 299792458.0 / LineUnits.toPerMeter(units) * 100.0));
					pw.println();
				}

				p = LineImpl.lineGeometryClass.getNext();
			}
			pw.close();
			fw.close();
			pw2.close();
			fw2.close();

			Utilities.fireOffEditor(fileNm);
			Utilities.fireOffEditor(lineCodesFileNm);
		} catch (IOException e) {
			// TODO: handle exception
		}
	}

	public static void showYPrim(String fileName) {
		FileWriter fw;
		PrintWriter pw;
		Complex[] cValues;
		int i, j;

		Circuit ckt = DSSGlobals.getInstance().getActiveCircuit();

		if (ckt != null) {
			if (ckt.getActiveCktElement() != null) {
				try {
					fw = new FileWriter(fileName);
					pw = new PrintWriter(fw);

					CktElement ace = ckt.getActiveCktElement();

					pw.println("Yprim of active circuit element: " + ace.getParentClass().getName() + "." + ace.getName());
					pw.println();

					cValues = ace.getYPrimValues(DSSGlobals.ALL_YPRIM);
					if (cValues != null) {
						pw.println();
						pw.println("G matrix (conductance), S");
						pw.println();

						for (i = 0; i < ace.getYorder(); i++) {
							for (j = 0; j < i; j++)
								pw.printf("%13.10g ", cValues[i + (j - 1) * ace.getYorder()].getReal());
							pw.println();
						}

						pw.println();
						pw.println("jB matrix (Susceptance), S") ;
						pw.println();

						for (i = 0; i < ace.getYorder(); i++) {
							for (j = 0; j < i; j++)
								pw.printf("%13.10g ", cValues[i + (j - 1) * ace.getYorder()].getImaginary());
							pw.println();
						}
					} else {
						pw.println("Yprim matrix is nil");
					}
					pw.close();
					fw.close();

					Utilities.fireOffEditor(fileName);
				} catch (IOException e) {
					// TODO: handle exception
				}
			}
		}
	}

	/**
	 * Shows how to retrieve the system Y in triplet form.
	 */
	public static void showY(String fileName) {
		FileWriter fw;
		PrintWriter pw;
		CMatrix hY;
		long nnz, nBus;
		long i, row, col;
		double re, im;
		long[] colIdx, rowIdx;
		Complex[] cVals;

		Circuit ckt = DSSGlobals.getInstance().getActiveCircuit();

		if (ckt == null)
			return;

		hY = ckt.getSolution().getY();
		if (hY == null) {
			DSSGlobals.getInstance().doSimpleMsg("Y Matrix not Built.", 222);
			return;
		}

		// print lower triangle of G and B using new functions
		// this compresses the entries if necessary - no extra work if already solved
//		KLU.factorSparseMatrix(hY);
//		KLU.getNNZ(hY, nNZ);
//		KLU.getSize(hY, nBus);  // we should already know this

		try {
//			ColIdx = new long[nNZ];
//			RowIdx = new long[nNZ];
//			cVals = new Complex[nNZ];
//			KLU.getTripletMatrix(hY, nNZ, RowIdx[0], ColIdx[0], cVals[0]);

			fw = new FileWriter(fileName);
			pw = new PrintWriter(fw);

			pw.println("System Y Matrix (Lower Triangle by Columns)");
			pw.println();
			pw.println("  Row  Col               G               B");
			pw.println();

			// shows how to easily traverse the triplet format
//			for (i = 0; i < nNZ - 1; i++) {  // TODO Check zero based indexing
//				col = ColIdx[i] + 1;
//				row = RowIdx[i] + 1;
//				if (row >= col) {
//					re = cVals[i].getReal();
//					im = cVals[i].getImaginary();
//					F.println(String.format("[%4d,%4d] = %13.10g + j%13.10g", row, col, re, im));
//				}
//			}
			pw.close();
			fw.close();

			Utilities.fireOffEditor(fileName);
		} catch (IOException e) {
			// TODO: handle exception
		}
	}

	/**
	 * Summary and tree-view to separate files.
	 */
	public static void showNodeCurrentSum(String fileName) {
		FileWriter fw;
		PrintWriter pw;
		int i, j;
		int nRef;
		String bName;

//		CktElement pCktElement;
		double[] maxNodeCurrent = new double[100];
		Complex CTemp;
		String pctError;
		double dTemp;

		Circuit ckt = DSSGlobals.getInstance().getActiveCircuit();
		SolutionObj sol = ckt.getSolution();

		maxNodeCurrent = null;
		try {
			fw = new FileWriter(fileName);
			pw = new PrintWriter(fw);

			// zero out the nodal current array
			for (i = 0; i < ckt.getNumNodes(); i++)
				sol.getCurrents()[i] = Complex.ZERO;
			// make temp storage for max current at node
			maxNodeCurrent = new double[ckt.getNumNodes() + 1];
			for (i = 0; i < ckt.getNumNodes(); i++)
				maxNodeCurrent[i] = 0.0;
			// now sum in each device current, keep track of the largest current at a node.
			for (CktElement pCktElement : ckt.getCktElements()) {
				if (pCktElement.isEnabled()) {
					pCktElement.computeITerminal();
					for (i = 0; i < pCktElement.getYorder(); i++) {
						CTemp =  pCktElement.getITerminal()[i];
						nRef  =  pCktElement.getNodeRef()[i];
						sol.getCurrents()[nRef] = sol.getCurrents()[nRef].add(CTemp);  // nodeRef = 0 is OK  TODO Check
						if (CTemp.abs() > maxNodeCurrent[nRef])
							maxNodeCurrent[nRef] = CTemp.abs();
					}
				}
			}

			// now write report

			setMaxBusNameLength();
			maxBusNameLength = maxBusNameLength + 2;
			pw.println();
			pw.println("Node Current Mismatch Report");
			pw.println();
			pw.println();
			pw.println(Utilities.pad("Bus,", maxBusNameLength) + " Node, \"Current Sum (A)" + "%error" + "Max Current (A)\"");

			// ground bus
			nRef = 0;
			dTemp = sol.getCurrents()[nRef].abs();
			if ((maxNodeCurrent[nRef] == 0.0) || (maxNodeCurrent[nRef] == dTemp)) {
				pctError = String.format("%10.1f", 0.0);
			} else {
				pctError = String.format("%10.6f", dTemp / maxNodeCurrent[nRef] * 100.0);
			}
			bName = Utilities.pad("\"System Ground\"", maxBusNameLength);
			pw.println(String.format("%s, %2d, %10.5f,       %s, %10.5f", bName, nRef, dTemp, pctError, maxNodeCurrent[nRef]));

			for (i = 0; i < ckt.getNumBuses(); i++) {
				for (j = 0; j < ckt.getBuses()[i].getNumNodesThisBus(); j++) {
					nRef = ckt.getBuses()[i].getRef(j);
					dTemp = sol.getCurrents()[nRef].abs();
					if ((maxNodeCurrent[nRef] == 0.0) || (maxNodeCurrent[nRef] == dTemp)) {
						pctError = String.format("%10.1f", 0.0);
					} else {
						pctError = String.format("%10.6f", dTemp / maxNodeCurrent[nRef] * 100.0);
					}
					if (j == 0) {
						bName = Utilities.padDots(Utilities.encloseQuotes(ckt.getBusList().get(i)), maxBusNameLength);
					} else {
						bName = Utilities.pad("\"   -\"", maxBusNameLength);
					}
					pw.println(String.format("%s, %2d, %10.5f,       %s, %10.5f", bName, ckt.getBuses()[i].getNum(j), dTemp, pctError, maxNodeCurrent[nRef]));
				}
			}
			pw.close();
			fw.close();

			Utilities.fireOffEditor(fileName);

			maxNodeCurrent = null;
		} catch (IOException e) {
			// TODO: handle exception
		}
	}

	public static void showkVBaseMismatch(String fileName) {
		FileWriter fw;
		PrintWriter pw;

		Bus pBus;
		double busKV;
		String busName;

		Circuit ckt = DSSGlobals.getInstance().getActiveCircuit();

		try {
			fw = new FileWriter(fileName);
			pw = new PrintWriter(fw);

			/* Check loads */
			if (ckt.getLoads().size() > 0) {
				pw.println();
				pw.println("!!!  LOAD VOLTAGE BASE MISMATCHES");
				pw.println();
			}

			for (LoadObj pLoad : ckt.getLoads()) {
				/* Find bus to which load connected */
				pBus = ckt.getBuses()[ pLoad.getTerminals()[0].busRef ];
				busName = ckt.getBusList().get( pLoad.getTerminals()[0].busRef );
				if (pBus.getKVBase() != 0.0) {
					if ((pLoad.getNPhases() == 1) && (pLoad.getConnection() == 0)) {
						if (Math.abs(pLoad.getKVLoadBase() - pBus.getKVBase()) > 0.10 * pBus.getKVBase()) {
							pw.println(String.format("!!!!! Voltage Base Mismatch, Load.%s.kV=%.6g, Bus %s LN kvBase = %.6g", pLoad.getName(), pLoad.getKVLoadBase(), pLoad.getBus(1), pBus.getKVBase()));
							pw.println(String.format("!setkvbase %s kVLN=%.6g", busName, pLoad.getKVLoadBase()));
							pw.println(String.format("!Load.%s.kV=%.6g", pLoad.getName(), pBus.getKVBase()));
						}
					} else {
						busKV = pBus.getKVBase() * DSSGlobals.SQRT3;
						if (Math.abs(pLoad.getKVLoadBase() - busKV) > 0.10 * busKV) {
							pw.println(String.format("!!!!! Voltage Base Mismatch, Load.%s.kV=%.6g, Bus %s kvBase = %.6g", pLoad.getName(), pLoad.getKVLoadBase(), pLoad.getBus(1), busKV));
							pw.println(String.format("!setkvbase %s kVLL=%.6g", busName, pLoad.getKVLoadBase()));
							pw.println(String.format("!Load.%s.kV=%.6g", pLoad.getName(), busKV));
						}
					}
				}
			}


			/* Check generators */

			if (ckt.getGenerators().size() > 0) {
				pw.println();
				pw.println("!!!  GENERATOR VOLTAGE BASE MISMATCHES");
				pw.println();
			}

			for (GeneratorObj pGen : ckt.getGenerators()) {
				/* Find bus to which generator connected */
				pBus = ckt.getBuses()[ pGen.getTerminals()[0].busRef ];
				busName = ckt.getBusList().get( pGen.getTerminals()[0].busRef );
				if (pBus.getKVBase() != 0.0) {
					if ((pGen.getNPhases() == 1) && (pGen.getConnection() == 0)) {
						if (Math.abs(pGen.getGenVars().kVGeneratorBase - pBus.getKVBase()) > 0.10 * pBus.getKVBase()) {
							pw.println(String.format("!!! Voltage Base Mismatch, Generator.%s.kV=%.6g, Bus %s LN kvBase = %.6g", pGen.getName(), pGen.getGenVars().kVGeneratorBase, pGen.getBus(1), pBus.getKVBase()));
							pw.println(String.format("!setkvbase %s kVLN=%.6g", busName, pGen.getGenVars().kVGeneratorBase));
							pw.println(String.format("!Generator.%s.kV=%.6g", pGen.getName(), pBus.getKVBase()));
						}
					} else {
						busKV = pBus.getKVBase() * DSSGlobals.SQRT3;
						if (Math.abs(pGen.getGenVars().kVGeneratorBase - busKV) > 0.10 * busKV) {
							pw.println(String.format("!!! Voltage Base Mismatch, Generator.%s.kV=%.6g, Bus %s kvBase = %.6g", pGen.getName(), pGen.getGenVars().kVGeneratorBase, pGen.getBus(1), busKV));
							pw.println(String.format("!setkvbase %s kVLL=%.6g", busName, pGen.getGenVars().kVGeneratorBase));
							pw.println(String.format("!Generator.%s.kV=%.6g", pGen.getName(), busKV));
						}
					}
				}
			}
			pw.close();
			fw.close();

			Utilities.fireOffEditor(fileName);
		} catch (IOException e) {
			// TODO: handle exception
		}
	}

	public static void showDeltaV(String fileName) {
		FileWriter fw;
		PrintWriter pw;

		Circuit ckt = DSSGlobals.getInstance().getActiveCircuit();

		try {
			fw = new FileWriter(fileName);
			pw = new PrintWriter(fw);

			setMaxDeviceNameLength();

			pw.println();
			pw.println("VOLTAGES ACROSS CIRCUIT ELEMENTS WITH 2 TERMINALS");
			pw.println();
			pw.println("Source Elements");
			pw.println();
			pw.println(Utilities.pad("Element,", maxDeviceNameLength) + " Conductor,     Volts,   Percent,           kVBase,  Angle");
			pw.println();

			// sources first
			for (CktElement pElem : ckt.getSources()) {
				if (pElem.isEnabled() && (pElem.getNTerms() == 2)) {
					writeElementDeltaVoltages(pw, pElem);
					pw.println();
				}
			}

			pw.println();
			pw.println("Power Delivery Elements");
			pw.println();
			pw.println(Utilities.pad("Element,", maxDeviceNameLength) + " Conductor,     Volts,   Percent,           kVBase,  Angle");
			pw.println();


			// PD elements next
			for (CktElement pElem : ckt.getPDElements()) {
				if (pElem.isEnabled() && (pElem.getNTerms() == 2)) {
					writeElementDeltaVoltages(pw, pElem);
					pw.println();
				}
			}

			pw.println("= = = = = = = = = = = = = = = = = = =  = = = = = = = = = = =  = =");
			pw.println();
			pw.println("Power Conversion Elements");
			pw.println();
			pw.println(Utilities.pad("Element,", maxDeviceNameLength) + " Conductor,     Volts,   Percent,           kVBase,  Angle");
			pw.println();

			// PC elements next
			for (CktElement pElem : ckt.getPCElements()) {
				if (pElem.isEnabled() && (pElem.getNTerms() == 2)) {
					writeElementDeltaVoltages(pw, pElem);
					pw.println();
				}
			}
			pw.close();
			fw.close();

			Utilities.fireOffEditor(fileName);
		} catch (IOException e) {
			// TODO: handle exception
		}
	}

}
