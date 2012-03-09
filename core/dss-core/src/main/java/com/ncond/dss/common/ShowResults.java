package com.ncond.dss.common;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

import org.apache.commons.math.complex.Complex;

import com.ncond.dss.control.RegControlObj;
import com.ncond.dss.conversion.Generator;
import com.ncond.dss.conversion.GeneratorObj;
import com.ncond.dss.conversion.LoadObj;
import com.ncond.dss.conversion.PCElement;
import com.ncond.dss.delivery.Line;
import com.ncond.dss.delivery.PDElement;
import com.ncond.dss.delivery.TransformerObj;
import com.ncond.dss.general.LineGeometry;
import com.ncond.dss.general.LineGeometryObj;
import com.ncond.dss.meter.EnergyMeter;
import com.ncond.dss.meter.EnergyMeterObj;
import com.ncond.dss.parser.Parser;
import com.ncond.dss.shared.CMatrix;
import com.ncond.dss.shared.CktTree;
import com.ncond.dss.shared.CktTreeNode;
import com.ncond.dss.shared.ComplexUtil;
import com.ncond.dss.shared.LineUnits;
import com.ncond.dss.shared.MathUtil;

public abstract class ShowResults {

	private static final char TABCHAR = '\u0009';

	private static int maxBusNameLength = 12;
	private static int maxDeviceNameLength = 30;

	private static void setMaxBusNameLength() {
		Circuit ckt = DSS.activeCircuit;
		maxBusNameLength = 4;
		for (int i = 0; i < ckt.getNumBuses(); i++)
			maxBusNameLength = Math.max(maxBusNameLength, ckt.getBusList().get(i).length());
	}

	private static void setMaxDeviceNameLength() {
		String devName, devClassName;
		Circuit ckt = DSS.activeCircuit;

		maxDeviceNameLength = 0;
		for (int i = 0; i < ckt.getNumDevices(); i++) {
			devName = ckt.getDeviceList().get(i);
			devClassName = ((DSSClass) DSS.DSSClassList.get( ckt.getDeviceRef()[i].cktElementClass )).getClassName();
			maxDeviceNameLength = Math.max(maxDeviceNameLength, devName.length() + devClassName.length() + 1);
		}
	}

	private static void writeSeqVoltages(PrintWriter pw, int i, boolean LL) {
		int j, k;
		Complex[] Vph = new Complex[3];
		Complex[] VLL = new Complex[3];
		Complex[] V012 = new Complex[3];
		double V0, V1, V2, Vpu, V2V1, V0V1;

		Circuit ckt = DSS.activeCircuit;

		if (ckt.getBus(i).getNumNodesThisBus() >= 3) {

			for (j = 0; j < 3; j++)
				Vph[j] = ckt.getSolution().getNodeV( ckt.getBus(i).getRef(j) );

			if (LL) {
				for (j = 0; j < 3; j++) {
					k = j + 1;
					if (k >= 3) k = 0;
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
			Vph[0] = ckt.getSolution().getNodeV( ckt.getBus(i).getRef(0) );
			V0 = 0.0;
			V1 = Vph[0].abs();  // use first phase value for non-three phase buses
			V2 = 0.0;
		}

		V1 = V1 / 1000.0;  // convert to kV
		V2 = V2 / 1000.0;
		V0 = V0 / 1000.0;

		// calc per unit value
		if (ckt.getBus(i).getKVBase() != 0.0) {
			Vpu = V1 / ckt.getBus(i).getKVBase();
		} else {
			Vpu = 0.0;
		}
		if (LL) Vpu = Vpu / DSS.SQRT3;

		if (V1 > 0.0) {
			V2V1 = 100.0 * V2 / V1;
			V0V1 = 100.0 * V0 / V1;
		} else {
			V2V1 = 0.0;
			V0V1 = 0.0;
		}

		pw.printf("%s %9.4g  %9.4g  %9.4g  %9.4g %9.4g %9.4g",
			Util.pad(ckt.getBusList().get(i), maxBusNameLength),
			V1, Vpu, V2, V2V1, V0, V0V1);
		pw.println();
	}

	private static void writeBusVoltages(PrintWriter pw, int i, boolean LL) {
		int nref, j, k;
		Complex V;
		double Vmag, Vpu;
		String bName;

		Circuit ckt = DSS.activeCircuit;

		for (j = 0; j < ckt.getBus(i).getNumNodesThisBus(); j++) {
			nref = ckt.getBus(i).getRef(j);
			V = ckt.getSolution().getNodeV(nref);

			if (LL && (j < 3)) {
				// convert to line-line assuming no more than 3 phases
				k = j + 1;
				if (k >= 3) k = 0;
				if (k < ckt.getBus(i).getNumNodesThisBus()) {
					nref = ckt.getBus(i).getRef(k);
					V = V.subtract( ckt.getSolution().getNodeV(nref) );
				}
			}
			Vmag = V.abs() * 0.001;
			if (ckt.getBus(i).getKVBase() != 0.0) {
				Vpu = Vmag / ckt.getBus(i).getKVBase();
			} else {
				Vpu = 0.0;
			}
			if (LL) Vpu = Vpu / DSS.SQRT3;
			if (j == 0) {
				bName = Util.padDots(ckt.getBusList().get(i), maxBusNameLength);
			} else {
				bName = Util.pad("   -", maxBusNameLength);
			}
			pw.printf("%s %2d %10.5g /_ %6.1f %9.5g %9.3f",
				bName.toUpperCase(), ckt.getBus(i).getNum(j), Vmag,
				ComplexUtil.degArg(V), Vpu, ckt.getBus(i).getKVBase() * DSS.SQRT3);
			pw.println();
		}
	}

	private static void writeElementVoltages(PrintWriter pw, CktElement elem, boolean LL) {
		int nCond, nTerm, i, j, k, nref, bref;
		String busName;
		Complex V;
		double Vpu, Vmag;

		Circuit ckt = DSS.activeCircuit;

		nCond = elem.getNumConds();
		nTerm = elem.getNumTerms();
		k = -1;
		busName = Util.pad(Util.stripExtension(elem.getFirstBus()), maxBusNameLength);
		pw.println("ELEMENT = \"" + elem.getDSSClassName() + "." + elem.getName().toUpperCase() + "\"");
		for (j = 0; j < nTerm; j++) {
			for (i = 0; i < nCond; i++) {
				k++;
				nref = elem.getNodeRef(k);
				V = ckt.getSolution().getNodeV(nref);
				Vmag  = V.abs() * 0.001;
				if (nref == 0) {
					Vpu = 0.0;
				} else {
					bref = ckt.getMapNodeToBus(nref).busRef;
					if (ckt.getBus(bref - 1).getKVBase() != 0.0) {
						Vpu = Vmag / ckt.getBus(bref - 1).getKVBase();
					} else {
						Vpu = 0.0;
					}
				}
				if (LL) Vpu = Vpu / DSS.SQRT3;
				pw.printf("%s  (%3d) %4d    %13.5g (%8.4g) /_ %6.1f", busName.toUpperCase(),
						nref, i,Vmag, Vpu, ComplexUtil.degArg(V));
				pw.println();
			}
			if (j < nTerm - 1)
				pw.println("------------");
			busName = Util.pad(Util.stripExtension(elem.getNextBus()), maxBusNameLength);
		}
	}

	private static void writeElementDeltaVoltages(PrintWriter pw, CktElement elem) {
		int nCond, nref1, nref2, bref1, bref2, i;
		double Vmag;
		Complex V1, V2;
		String elemName;

		Circuit ckt = DSS.activeCircuit;

		nCond = elem.getNumConds();
		elemName = Util.pad(elem.getDSSClassName() + "." + elem.getName().toUpperCase(), maxDeviceNameLength);

		for (i = 0; i < nCond; i++) {
			nref1 = elem.getNodeRef(i);
			nref2 = elem.getNodeRef(i + nCond);

			bref1 = nref1 > 0 ? ckt.getMapNodeToBus(nref1).busRef : 0;
			bref2 = nref2 > 0 ? ckt.getMapNodeToBus(nref2).busRef : 0;

			if ((bref1 > 0) && (bref2 > 0)) {
				V1 = ckt.getSolution().getNodeV(nref1);  // OK if nref1 or nref2 = 0
				V2 = ckt.getSolution().getNodeV(nref2);
				V1 = V1.subtract(V2);  // diff voltage

				if (ckt.getBus(bref1 - 1).getKVBase() != ckt.getBus(bref2 - 1).getKVBase()) {
					Vmag = 0.0;
				} else {
					if (ckt.getBus(bref1 - 1).getKVBase() > 0.0) {
						Vmag = V1.abs() / (1000.0 * ckt.getBus(bref1 - 1).getKVBase()) * 100.0;
					} else {
						Vmag = 0.0;
					}
				}
				pw.printf("%s,  %4d,    %12.5g, %12.5g, %12.5g, %6.1f",
					elemName, i, V1.abs(), Vmag, ckt.getBus(bref1).getKVBase(), ComplexUtil.degArg(V1));
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
		Circuit ckt = DSS.activeCircuit;

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
				pw.println(Util.pad("Bus", maxBusNameLength) +
					"  Mag:   V1 (kV)    p.u.     V2 (kV)   %V2/V1    V0 (kV)    %V0/V1");
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
				pw.println(Util.pad("Bus", maxBusNameLength) +
					" Node    V (kV)    Angle      p.u.   Base kV");
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
				pw.println(Util.pad("Bus", maxBusNameLength) +
					" (node ref)  Phase    Magnitude, kV (pu)    Angle");
				pw.println();

				// sources first
				for (CktElement elem : ckt.getSources()) {
					if (elem.isEnabled())
						writeElementVoltages(pw, elem, LL);
					pw.println();
				}

				// PD elements first
				for (CktElement elem : ckt.getPDElements()) {
					if (elem.isEnabled())
						writeElementVoltages(pw, elem, LL);
					pw.println();
				}

				pw.println("= = = = = = = = = = = = = = = = = = =  = = = = = = = = = = =  = =");
				pw.println();
				pw.println("Power Conversion Elements");
				pw.println();
				pw.println(Util.pad("Bus", maxBusNameLength) +
					" (node ref)  Phase    Magnitude, kV (pu)    Angle");
				pw.println();

				// PC elements next
				for (CktElement elem : ckt.getPCElements()) {
					if (elem.isEnabled())
						writeElementVoltages(pw, elem, LL);
					pw.println();
				}
				break;
			}

			pw.close();
			fw.close();

			Util.fireOffEditor(fileName);
		} catch (IOException e) {
			DSS.doSimpleMsg("Error encountered showing bus voltages: " + e.getMessage(), -1);
		}
	}

	private static void getI0I1I2(double[] I0, double[] I1, double[] I2, double[] Cmax, int nPhases, int koffset, Complex[] cBuffer) {
		double Cmag;
		int i;
		Complex[] Iph  = new Complex[3];
		Complex[] I012 = new Complex[3];

		if (nPhases >= 3) {
			Cmax[0] = 0.0;
			for (i = 0; i < 3; i++) {
				Iph[i] = cBuffer[koffset + i];
				Cmag = Iph[i].abs();
				if (Cmag > Cmax[0]) Cmax[0] = Cmag;
			}
			MathUtil.phase2SymComp(Iph, I012);
			I0[0] = I012[0].abs();
			I1[0] = I012[1].abs();
			I2[0] = I012[2].abs();
		} else {
			I0[0] = 0.0;
			I1[0] = cBuffer[1 + koffset].abs();
			I2[0] = 0.0;
			Cmax[0] = I1[0];
		}
	}

	private static void writeSeqCurrents(PrintWriter pw, final String paddedBrName,
			double I0, double I1, double I2, double CMax, double normAmps, double emergAmps,
			int j, int DSSObjType) {

		double Inormal, Iemerg, I2I1, I0I1;
		String name;

		Inormal = 0.0;
		Iemerg = 0.0;

		name = (j == 0) ? paddedBrName : Util.pad("   -", paddedBrName.length());

		I2I1 = (I1 > 0.0) ? 100.0 * I2 / I1 : 0.0;
		I0I1 = (I1 > 0.0) ? 100.0 * I0 / I1 : 0.0;

		if (((DSSClassDefs.CLASSMASK & DSSObjType) != DSSClassDefs.CAP_ELEMENT) && (j == 0)) {
			// only write overloads for non-capacitors and terminal 1
			if (normAmps > 0.0) Inormal = CMax / normAmps * 100.0;
			if (emergAmps > 0.0) Iemerg = CMax / emergAmps * 100.0;
		}

		pw.printf("%s %3d  %10.5g   %10.5g %8.2f  %10.5g %8.2f  %8.2f %8.2f",
				name.toUpperCase(), j + 1, I1, I2, I2I1, I0, I0I1, Inormal, Iemerg);
		pw.println();
	}

	private static void writeTerminalCurrents(PrintWriter pw, CktElement elem, boolean showResidual) {
		int j, i, k, nCond, nTerm;
		Complex[] cBuffer;
		String fromBus;
		Complex Ctotal;
		double[] residPolar;

		nCond = elem.getNumConds();
		nTerm = elem.getNumTerms();

		cBuffer = new Complex[nCond * nTerm];
		elem.getCurrents(cBuffer);
		k = -1;
		fromBus = Util.pad(Util.stripExtension(elem.getFirstBus()), maxBusNameLength);
		pw.println("ELEMENT = " + Util.fullName(elem));
		for (j = 0; j < nTerm; j++) {
			Ctotal = Complex.ZERO;
			for (i = 0; i < nCond; i++) {
				k++;
				if (showResidual)
					Ctotal = Ctotal.add(cBuffer[k]);
				pw.printf("%s  %4d    %13.5g /_ %6.1f",
					fromBus.toUpperCase(), Util.getNodeNum(elem.getNodeRef(k)),
					cBuffer[k].abs(), ComplexUtil.degArg(cBuffer[k]));
				pw.println();
			}
			if (showResidual && (elem.getNumPhases() > 1)) {
				residPolar = ComplexUtil.complexToPolarDeg(Ctotal.negate());
				pw.printf("%s Resid    %13.5g /_ %6.1f",
					fromBus.toUpperCase(), residPolar[0], residPolar[1]);
				pw.println();
			}
			if (j < nTerm - 1) pw.println("------------");
			fromBus = Util.pad(Util.stripExtension(elem.getNextBus()), maxBusNameLength);
		}
		pw.println();
	}


	public static void showCurrents(String fileName, boolean showResidual, int showOptionCode) {
		FileWriter fw;
		PrintWriter pw;
		Complex[] cBuffer;
		int nCond, nTerm, j;
		double[] I0 = new double[1];
		double[] I1 = new double[1];
		double[] I2 = new double[1];
		double[] Cmax = new double[1];;

		Circuit ckt = DSS.activeCircuit;

		setMaxDeviceNameLength();
		setMaxBusNameLength();

		try {
			fw = new FileWriter(fileName);
			pw = new PrintWriter(fw);

			switch (showOptionCode) {
			case 0:  /* Sequence currents */
				pw.println();
				pw.println("SYMMETRICAL COMPONENT CURRENTS BY CIRCUIT ELEMENT (first 3 phases)");
				pw.println();
				pw.println(Util.pad("Element", maxDeviceNameLength + 2) +
					" Term      I1         I2         %I2/I1    I0         %I0/I1   %Normal %Emergency");
				pw.println();

				// sources first
				for (CktElement elem : ckt.getSources()) {
					if (elem.isEnabled()) {
						nCond = elem.getNumConds();
						nTerm = elem.getNumTerms();
						cBuffer = new Complex[nCond * nTerm];
						elem.getCurrents(cBuffer);

						for (j = 0; j < nTerm; j++) {
							getI0I1I2(I0, I1, I2, Cmax, elem.getNumPhases(), j * nCond, cBuffer);
							writeSeqCurrents(pw, Util.padDots(Util.fullName(elem), maxDeviceNameLength + 2),
									I0[0], I1[0], I2[0], Cmax[0], 0.0, 0.0, j, elem.getObjType());
						}
					}
				}

				// PD elements next
				for (PDElement elem : ckt.getPDElements()) {
					if (elem.isEnabled()) {
						nCond = elem.getNumConds();
						nTerm = elem.getNumTerms();
						cBuffer = new Complex[nCond * nTerm];
						elem.getCurrents(cBuffer);

						for (j = 0; j < nTerm; j++) {
							getI0I1I2(I0, I1, I2, Cmax, elem.getNumPhases(), j * nCond, cBuffer);
							writeSeqCurrents(pw, Util.padDots(Util.fullName(elem), maxDeviceNameLength + 2),
									I0[0], I1[0], I2[0], Cmax[0], elem.getNormAmps(), elem.getEmergAmps(),
									j, elem.getObjType());
						}
					}
				}

				// PC elements next
				for (PCElement elem : ckt.getPCElements()) {
					if (elem.isEnabled()) {
						nCond = elem.getNumConds();
						nTerm = elem.getNumTerms();
						cBuffer = new Complex[nCond * nTerm];
						elem.getCurrents(cBuffer);

						for (j = 0; j < nTerm; j++) {
							getI0I1I2(I0, I1, I2, Cmax, elem.getNumPhases(), j * nCond, cBuffer);
							writeSeqCurrents(pw, Util.padDots( Util.fullName(elem), maxDeviceNameLength + 2 ),
									I0[0], I1[0], I2[0], Cmax[0], 0.0, 0.0, j, elem.getObjType());
						}
					}
				}

				// faults next
				for (CktElement elem : ckt.getFaults()) {
					if (elem.isEnabled()) {
						nCond = elem.getNumConds();
						nTerm = elem.getNumTerms();
						cBuffer = new Complex[nCond * nTerm];
						elem.getCurrents(cBuffer);

						for (j = 0; j < nTerm; j++) {
							getI0I1I2(I0, I1, I2, Cmax, elem.getNumPhases(), j * nCond, cBuffer);
							writeSeqCurrents(pw, Util.padDots(Util.fullName(elem), maxDeviceNameLength + 2),
									I0[0], I1[0], I2[0], Cmax[0], 0.0, 0.0, j, elem.getObjType());
						}
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
				pw.println(Util.pad("  Bus", maxBusNameLength) + " Phase    Magnitude, A     Angle");
				pw.println();

				// sources first
				for (CktElement elem : ckt.getSources())
					if (elem.isEnabled())
						writeTerminalCurrents(pw, elem, false);

				// PD elements next
				for (CktElement elem : ckt.getPDElements())
					if (elem.isEnabled())
						writeTerminalCurrents(pw, elem, showResidual);

				// faults
				for (CktElement elem : ckt.getFaults())
					if (elem.isEnabled())
						writeTerminalCurrents(pw, elem, false);

				pw.println("= = = = = = = = = = = = = = = = = = =  = = = = = = = = = = =  = =");
				pw.println();
				pw.println("Power Conversion Elements");
				pw.println();
				pw.println(Util.pad("  Bus", maxBusNameLength) + " Phase    Magnitude, A     Angle");
				pw.println();

				// PC elements next
				for (CktElement elem : ckt.getPCElements())
					if (elem.isEnabled())
						writeTerminalCurrents(pw, elem, false);
				break;
			}
			pw.close();
			fw.close();

			Util.fireOffEditor(fileName);
		} catch (IOException e) {
			DSS.doSimpleMsg("Error encountered showing currents: " + e.getMessage(), 2190);
		}
	}

	/**
	 * @param fileName
	 * @param opt 0: kVA, 1: MVA
	 * @param showOptionCode
	 */
	public static void showPowers(String fileName, int opt, int showOptionCode) {
		String fromBus;
		FileWriter fw;
		PrintWriter pw;
		Complex[] cBuffer;
		int nCond, nTerm, i, j, k;
		Complex V;
		Complex S, Saccum;
		int nref;
		Complex[] Vph = new Complex[3];
		Complex[] V012 = new Complex[3];
		Complex[] Iph = new Complex[3];
		Complex[] I012 = new Complex[3];

		Circuit ckt = DSS.activeCircuit;

		setMaxDeviceNameLength();
		setMaxBusNameLength();

		try {
			fw = new FileWriter(fileName);
			pw = new PrintWriter(fw);

			/* Allocate cBuffer big enough for largest circuit element */
			cBuffer = new Complex[Util.getMaxCktElementSize()];

			switch (showOptionCode) {
			case 0: /* Sequence currents */
				pw.println();
				pw.println("SYMMETRICAL COMPONENT POWERS BY CIRCUIT ELEMENT (first 3 phases)                                     Excess Power");
				pw.println();
				switch (opt) {
				case 1:
					pw.println(Util.pad("Element", maxDeviceNameLength + 2) +
						" Term    P1(MW)   Q1(Mvar)       P2         Q2      P0      Q0       P_Norm      Q_Norm     P_Emerg    Q_Emerg");
					break;
				default:
					pw.println(Util.pad("Element", maxDeviceNameLength + 2) +
						" Term    P1(kW)   Q1(kvar)       P2         Q2      P0      Q0       P_Norm      Q_Norm     P_Emerg    Q_Emerg");
					break;
				}
				pw.println();

				// sources first
				for (CktElement elem : ckt.getSources()) {
					if (elem.isEnabled()) {
						nCond = elem.getNumConds();
						nTerm = elem.getNumTerms();
						elem.getCurrents(cBuffer);

						for (j = 0; j < nTerm; j++) {
							pw.print(Util.pad(Util.fullName(elem), maxDeviceNameLength + 2) + j + 1);
							for (i = 0; i < Math.min(3, elem.getNumPhases()); i++) {
								k = j * nCond + i;
								nref = elem.getNodeRef(k);
								V = ckt.getSolution().getNodeV(nref);
								Iph[i] = cBuffer[k];
								Vph[i] = V;
							}
							if (elem.getNumPhases() >= 3) {
								MathUtil.phase2SymComp(Iph, I012);
								MathUtil.phase2SymComp(Vph, V012);
							} else {
								// handle single phase and pos seq models
								V012[0] = Complex.ZERO;
								I012[0] = Complex.ZERO;
								V012[1] = ckt.isPositiveSequence() ? Vph[0] : Complex.ZERO;
								I012[1] = ckt.isPositiveSequence() ? Iph[0] : Complex.ZERO;
								V012[2] = Complex.ZERO;
								I012[2] = Complex.ZERO;
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
				for (PDElement elem : ckt.getPDElements()) {
					if (elem.isEnabled()) {
						nCond = elem.getNumConds();
						nTerm = elem.getNumTerms();
						elem.getCurrents(cBuffer);

						for (j = 0; j < nTerm; j++) {
							pw.print(Util.pad( Util.fullName(elem), maxDeviceNameLength + 2) + j + 1);
							for (i = 0; i < Math.min(3, elem.getNumPhases()); i++) {
								k = j * nCond + i;
								nref = elem.getNodeRef(k);
								V = ckt.getSolution().getNodeV(nref);
								Iph[i] = cBuffer[k];
								Vph[i] = V;
							}

							if (elem.getNumPhases() >= 3) {
								MathUtil.phase2SymComp(Iph, I012);
								MathUtil.phase2SymComp(Vph, V012);
							} else {  // handle single phase and pos seq models
								V012[0] = Complex.ZERO;
								I012[0] = Complex.ZERO;
								V012[1] = ckt.isPositiveSequence() ? Vph[0] : Complex.ZERO;
								I012[1] = ckt.isPositiveSequence() ? Iph[0] : Complex.ZERO;
								V012[2] = Complex.ZERO;
								I012[2] = Complex.ZERO;
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

							if (j == 0) {
								S = elem.getExcessKVANorm(0);
								if (opt == 1) S = S.multiply(0.001);
								pw.print(S.getReal());
								pw.print(S.getImaginary());

								S = elem.getExcessKVAEmerg(0);
								if (opt == 1) S = S.multiply(0.001);
								pw.print(S.getReal());
								pw.print(S.getImaginary());
							}
							pw.println();

						}
					}
				}

				// PC elements next
				for (PCElement elem : ckt.getPCElements()) {
					if (elem.isEnabled()) {
						nCond = elem.getNumConds();
						nTerm = elem.getNumTerms();
						elem.getCurrents(cBuffer);

						for (j = 0; j < nTerm; j++) {
							pw.print(Util.pad(Util.fullName(elem), maxDeviceNameLength + 2) + j + 1);
							for (i = 0; i < Math.min(3, elem.getNumPhases()); i++) {
								k = j * nCond + i;
								nref = elem.getNodeRef(k);
								V = ckt.getSolution().getNodeV(nref);
								Iph[i] = cBuffer[k];
								Vph[i] = V;
							}

							if (elem.getNumPhases() >= 3) {
								MathUtil.phase2SymComp(Iph, I012);
								MathUtil.phase2SymComp(Vph, V012);
							} else {  // handle single phase and pos seq models
								V012[0] = Complex.ZERO;
								I012[0] = Complex.ZERO;
								V012[1] = ckt.isPositiveSequence() ? Vph[0] : Complex.ZERO;
								I012[1] = ckt.isPositiveSequence() ? Iph[0] : Complex.ZERO;
								V012[2] = Complex.ZERO;
								I012[2] = Complex.ZERO;
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
			case 1:  /* Branch powers */
				pw.println();
				pw.println("CIRCUIT ELEMENT POWER FLOW");
				pw.println();
				pw.println("(Power Flow into element from indicated Bus)");
				pw.println();
				pw.println("Power Delivery Elements");
				pw.println();
				switch (opt) {
				case 1:
					pw.println(Util.pad("  Bus", maxBusNameLength) +
						" Phase     MW     +j   Mvar         MVA         PF");
					break;
				default:
					pw.println(Util.pad("  Bus", maxBusNameLength) +
						" Phase     kW     +j   kvar         kVA         PF");
					break;
				}
				pw.println();

				for (CktElement elem : ckt.getSources()) {
					if (elem.isEnabled()) {
						nCond = elem.getNumConds();
						nTerm = elem.getNumTerms();
						elem.getCurrents(cBuffer);
						k = -1;
						fromBus = Util.pad(Util.stripExtension(elem.getFirstBus()), maxBusNameLength);
						pw.println("ELEMENT = " + Util.fullName(elem));
						for (j = 0; j < nTerm; j++) {
							Saccum = Complex.ZERO;
							for (i = 0; i < nCond; i++) {
								k++;
								nref = elem.getNodeRef(k);
								V = ckt.getSolution().getNodeV(nref);
								S = V.multiply(cBuffer[k].conjugate());
								if (ckt.isPositiveSequence()) // && elem.getNumPhases() == 1)
									S = S.multiply(3.0);
								if (opt == 1) S = S.multiply(0.001);
								Saccum = Saccum.add(S);
								pw.print(fromBus.toUpperCase() + "  " + Util.getNodeNum(elem.getNodeRef(k)) +
									"    " + S.getReal() / 1000.0 + " +j " + S.getImaginary() / 1000.0);
								pw.println("   " + S.abs() / 1000.0 + "     " + Util.powerFactor(S));
							}
							pw.print(Util.padDots("   TERMINAL TOTAL", maxBusNameLength + 10) +
								Saccum.getReal() / 1000.0 + " +j " + Saccum.getImaginary() / 1000.0);
							pw.println("   " + Saccum.abs() / 1000.0 + "     " + Util.powerFactor(Saccum));
							fromBus = Util.pad( Util.stripExtension(elem.getNextBus()), maxBusNameLength );
						}
					}
					pw.println();
				}

				// PD elements next
				for (CktElement elem : ckt.getPDElements()) {
					if (elem.isEnabled()) {
						nCond = elem.getNumConds();
						nTerm = elem.getNumTerms();
						elem.getCurrents(cBuffer);
						k = -1;
						fromBus = Util.pad( Util.stripExtension(elem.getFirstBus()), maxBusNameLength );
						pw.println("ELEMENT = " + Util.fullName(elem));
						for (j = 0; j < nTerm; j++) {
							Saccum = Complex.ZERO;
							for (i = 0; i < nCond; i++) {
								k++;
								nref = elem.getNodeRef(k);
								V = ckt.getSolution().getNodeV(nref);
								S = V.multiply(cBuffer[k].conjugate());
								if (ckt.isPositiveSequence())  // && (pElem.getNPhases() == 1)
									S = S.multiply(3.0);
								if (opt == 1) S = S.multiply(0.001);
								Saccum = Saccum.add(S);
 								pw.print(fromBus.toUpperCase() + "  " + Util.getNodeNum(elem.getNodeRef(k)) +
 									"    " + S.getReal() / 1000.0 + " +j " + S.getImaginary() / 1000.0);
								pw.println("   " + S.abs() / 1000.0 + "     " + Util.powerFactor(S));
							}
							pw.print(Util.padDots("   TERMINAL TOTAL", maxBusNameLength + 10) +
								Saccum.getReal() / 1000.0 + " +j " + Saccum.getImaginary() / 1000.0);
							pw.println("   " + Saccum.abs() / 1000.0 + "     " + Util.powerFactor(Saccum));
							fromBus = Util.pad( Util.stripExtension(elem.getNextBus()), maxBusNameLength );
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
					pw.println(Util.pad("  Bus", maxBusNameLength) + " Phase     MW   +j  Mvar         MVA         PF");
					break;
				default:
					pw.println(Util.pad("  Bus", maxBusNameLength) + " Phase     kW   +j  kvar         kVA         PF");
					break;
				}
				pw.println();

				// PC elements next
				for (CktElement elem : ckt.getPCElements()) {
					if (elem.isEnabled()) {
						nCond = elem.getNumConds();
						nTerm = elem.getNumTerms();
						elem.getCurrents(cBuffer);
						k = -1;
						fromBus = Util.pad(Util.stripExtension(elem.getFirstBus()), maxBusNameLength);
						pw.println("ELEMENT = " + Util.fullName(elem));
						for (j = 0; j < nTerm; j++) {
							Saccum = Complex.ZERO;
							for (i = 0; i < nCond; i++) {
								k++;
								nref = elem.getNodeRef(k);
								V = ckt.getSolution().getNodeV(nref);
								S = V.multiply(cBuffer[k].conjugate());
								if (ckt.isPositiveSequence())  // && elem.getNumPhases() == 1
									S = S.multiply(3.0);
								if (opt == 1) S = S.multiply(0.001);
								Saccum = Saccum.add(S);
								pw.print(fromBus.toUpperCase() + "  " + Util.getNodeNum(elem.getNodeRef(k)) +
									"    " + S.getReal() / 1000.0 + " +j " + S.getImaginary() / 1000.0);
								pw.println("   " + S.abs() / 1000.0 + "     " + Util.powerFactor(S));
							}
							pw.print(Util.padDots("   TERMINAL TOTAL", maxBusNameLength + 10) +
								Saccum.getReal() / 1000.0 + " +j " + Saccum.getImaginary() / 1000.0);
							pw.println("   " + Saccum.abs() / 1000.0 + "     " + Util.powerFactor(Saccum));
							fromBus = Util.pad( Util.stripExtension(elem.getNextBus()), maxBusNameLength );
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

			pw.close();
			fw.close();

			Util.fireOffEditor(fileName);
		} catch (IOException e) {
			DSS.doSimpleMsg("Error encountered showing powers: " + e.getMessage(), 2190);
		}
	}

	/**
	 * Check all terminals of cktElement to see if bus connected to busreference.
	 */
	private static boolean checkBusReference(CktElement cktElem, int busReference, int[] terminalIndex) {
		for (int i = 0; i < cktElem.getNumTerms(); i++) {
			if (cktElem.getTerminal(i).getBusRef() == busReference) {
				terminalIndex[0] = i;
				return true;
			}
		}
		return false;
	}

	private static void writeTerminalPowerSeq(PrintWriter pw, CktElement cktElem, int j, int opt) {
		int i, k, nCond, nref;
		Complex V, S;
		Complex[] Vph = new Complex[3];
		Complex[] V012 = new Complex[3];
		Complex[] Iph = new Complex[3];
		Complex[] I012 = new Complex[3];
		Complex[] cBuffer;  // Allocate to max total conductors

		Circuit ckt = DSS.activeCircuit;

		/* Allocate cBuffer big enough for this circuit element */
		cBuffer = new Complex[cktElem.getYOrder()];

		nCond = cktElem.getNumConds();
		cktElem.getCurrents(cBuffer);

		pw.print(Util.pad(Util.fullName(cktElem), maxDeviceNameLength + 2) + j + 1);
		for (i = 0; i < Math.min(cktElem.getNumPhases(), 3); i++) {
			k = j * nCond + i;
			nref = cktElem.getNodeRef(k);
			V = ckt.getSolution().getNodeV(nref);
			Iph[i] = cBuffer[k];
			Vph[i] = V;
		}

		if (cktElem.getNumPhases() >= 3) {
			MathUtil.phase2SymComp(Iph, I012);
			MathUtil.phase2SymComp(Vph, V012);
		} else {  // handle single phase and pos seq models
			V012[0] = Complex.ZERO;
			I012[0] = Complex.ZERO;
			V012[1] = ckt.isPositiveSequence() ? Vph[0] : Complex.ZERO;
			I012[1] = ckt.isPositiveSequence() ? Iph[0] : Complex.ZERO;
			V012[2] = Complex.ZERO;
			I012[2] = Complex.ZERO;
		}

		switch (cktElem.getNumPhases()) {  // pos seq or single phase
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
	}

	private static void writeTerminalPower(PrintWriter pw, CktElement cktElem, int jTerm, int opt) {
		int i, k, nCond, nref;
		Complex V, S;
		Complex Saccum;
		Complex[] cBuffer;
		String fromBus;

		Circuit ckt = DSS.activeCircuit;

		cBuffer = new Complex[cktElem.getYOrder()];  // allocate to max total conductors

		nCond = cktElem.getNumConds();
		cktElem.getCurrents(cBuffer);
		fromBus = Util.pad(Util.stripExtension(cktElem.getBus(jTerm)), 12);
		pw.println("ELEMENT = " + Util.pad(Util.fullName(cktElem), maxDeviceNameLength + 2));

		Saccum = Complex.ZERO;
		for (i = 0; i < nCond; i++) {
			k = jTerm * nCond + i;
			nref = cktElem.getNodeRef(k);
			V = ckt.getSolution().getNodeV(nref);
			S = V.multiply( cBuffer[k].conjugate() );
			if (ckt.isPositiveSequence())  // && cktElem.getNumPhases() == 1
				S = S.multiply(3.0);
			if (opt == 1) S = S.multiply(0.001);
			Saccum = Saccum.add(S);
			pw.printf("%s %4d %10.5g +j %10.5g    %10.5g    %8.4f",
					fromBus.toUpperCase(), Util.getNodeNum(cktElem.getNodeRef(k)),
					S.getReal() / 1000.0, S.getImaginary() / 1000.0,
					S.abs() / 1000.0 , Util.powerFactor(S));
			pw.println();
		}
		pw.printf(" TERMINAL TOTAL   %10.5g +j %10.5g    %10.5g    %8.4f",
				Saccum.getReal() / 1000.0,
				Saccum.getImaginary() / 1000.0, Saccum.abs() / 1000.0,
				Util.powerFactor(Saccum));
		pw.println();
	}

	/**
	 * Report power flow around a specified bus.
	 *
	 * @param opt 0: kVA 1: MVA
	 */
	public static void showBusPowers(String fileName, String busName, int opt, int showOptionCode) {
		FileWriter fw;
		PrintWriter pw;
		int[] j = new int[1];
		int nCond, nTerm;
		double[] I0 = new double[1];
		double[] I1 = new double[1];
		double[] I2 = new double[1];
		double[] CMax = new double[1];
		Complex[] cBuffer;  // allocate to max total conductors
		int busRef;
		int[] jTerm = new int[1];
		Circuit ckt = DSS.activeCircuit;

		setMaxDeviceNameLength();

		/* Get bus reference */
		busRef = ckt.getBusList().find(busName) + 1;
		if (busRef == 0) {
			DSS.doSimpleMsg("Bus \"" + busName.toUpperCase() + "\" not found.", 219);
			return;
		}
		try {
			fw = new FileWriter(fileName);
			pw = new PrintWriter(fw);

			cBuffer = new Complex[Util.getMaxCktElementSize()];

			switch (showOptionCode) {
			case 0:
				/* Write bus voltage */
				pw.println();
				pw.println("  Bus   Mag:    V1 (kV)  p.u.    V2 (kV)  %V2/V1  V0 (kV)  %V0/V1");
				pw.println();

				writeSeqVoltages(pw, busRef, false);

				/* Sequence currents */
				pw.println();
				pw.println("SYMMETRICAL COMPONENT CURRENTS BY CIRCUIT ELEMENT (first 3 phases)");
				pw.println();
				pw.println("Element                      Term      I1         I2         %I2/I1    I0         %I0/I1   %Normal %Emergency");
				pw.println();

				// sources first
				for (CktElement elem : ckt.getSources()) {
					if (elem.isEnabled()) {
						if (checkBusReference(elem, busRef, j)) {
							/* FIXME: Use j set by checkBusReference */
							nCond = elem.getNumConds();
							nTerm = elem.getNumTerms();
							elem.getCurrents(cBuffer);

							for (j[0] = 0; j[0] < nTerm; j[0]++) {
								getI0I1I2(I0, I1, I2, CMax, elem.getNumPhases(), j[0] * nCond, cBuffer);
								writeSeqCurrents(pw, Util.padDots(Util.fullName(elem), maxDeviceNameLength + 2),
										I0[0], I1[0], I2[0], CMax[0], 0.0, 0.0, j[0], elem.getObjType());
							}
						}
					}
				}

				// PD elements next
				for (PDElement elem : ckt.getPDElements()) {
					if (elem.isEnabled()) {
						if (checkBusReference(elem, busRef, j)) {  // is this connected to the bus
							/* Use j set by CheckBusReference */
							nCond = elem.getNumConds();
							nTerm = elem.getNumTerms();
							elem.getCurrents(cBuffer);

							for (j[0] = 0; j[0] < nTerm; j[0]++) {
								getI0I1I2(I0, I1, I2, CMax, elem.getNumPhases(), j[0] * nCond, cBuffer);
								writeSeqCurrents(pw, Util.padDots(Util.fullName(elem), maxDeviceNameLength + 2),
										I0[0], I1[0], I2[0], CMax[0], 0.0, 0.0, j[0], elem.getObjType());
							}
						}
					}
				}

				// PC elements next
				for (PCElement elem : ckt.getPCElements()) {
					if (elem.isEnabled()) {
						if (checkBusReference(elem, busRef, j)) {
							nCond = elem.getNumConds();
							nTerm = elem.getNumTerms();
							elem.getCurrents(cBuffer);

							for (j[0] = 0; j[0] < nTerm; j[0]++) {
								getI0I1I2(I0, I1, I2, CMax, elem.getNumPhases(), j[0] * nCond, cBuffer);
								writeSeqCurrents(pw, Util.padDots(Util.fullName(elem), maxDeviceNameLength + 2),
										I0[0], I1[0], I2[0], CMax[0], 0.0, 0.0, j[0], elem.getObjType());
							}
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
				for (CktElement elem : ckt.getSources()) {
					if (elem.isEnabled()) {
						if (checkBusReference(elem, busRef, j)) {
							/* Use j set by checkBusReference */
							writeTerminalPowerSeq(pw, elem, j[0], opt);
						}
					}
				}

				// PD elements next
				for (PDElement elem : ckt.getPDElements()) {
					if (elem.isEnabled()) {
						if (checkBusReference(elem, busRef, j)) {  // is this connected to the bus
							writeTerminalPowerSeq(pw, elem, j[0], opt);
						}
					}
				}

				// PC elements next
				for (PCElement elem : ckt.getPCElements()) {
					if (elem.isEnabled()) {
						if (checkBusReference(elem, busRef, j)) {
							writeTerminalPowerSeq(pw, elem, j[0], opt);
						}
					}
				}
				break;
			case 1:
				/* Write bus voltage */
				pw.println();
				pw.println("  Bus   (node ref)  Node       V (kV)    Angle    p.u.   Base kV");
				pw.println();
				writeBusVoltages(pw, busRef - 1, false);

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
				for (CktElement elem : ckt.getSources()) {
					if (elem.isEnabled()) {
						if (checkBusReference(elem, busRef, j)) {
							writeTerminalCurrents(pw, elem, false);
							pw.println();
						}
					}
				}

				// PD elements first
				for (CktElement elem : ckt.getPDElements()) {
					if (elem.isEnabled()) {
						if (checkBusReference(elem, busRef, j)) {
							writeTerminalCurrents(pw, elem, true);
							pw.println();
						}
					}
				}

				pw.println("= = = = = = = = = = = = = = = = = = =  = = = = = = = = = = =  = =");
				pw.println();
				pw.println("Power Conversion Elements");
				pw.println();
				pw.println("  Bus         Phase    Magnitude, A     Angle");
				pw.println();

				// PC elements next
				for (CktElement elem : ckt.getPCElements()) {
					if (elem.isEnabled()) {
						if (checkBusReference(elem, busRef, j)) {
							writeTerminalCurrents(pw, elem, false);
							pw.println();
						}
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
				for (CktElement elem : ckt.getSources()) {
					if (elem.isEnabled()) {
						if (checkBusReference(elem, busRef, j)) {
							writeTerminalPower(pw, elem, j[0], opt);
							pw.println();
						}
					}
				}

				// PD elements first
				for (CktElement elem : ckt.getPDElements()) {
					if (elem.isEnabled()) {
						if (checkBusReference(elem, busRef, jTerm)) {
							writeTerminalPower(pw, elem, jTerm[0], opt);
							/* Get the other bus for the report */
							if (jTerm[0] == 0) {
								jTerm[0] = 1;
							} else {
								jTerm[0] = 0;  // may sometimes give wrong terminal if more than 2 terminals
							}
							writeTerminalPower(pw, elem, jTerm[0], opt);
							pw.println();
						}
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
				for (PCElement elem : ckt.getPCElements()) {
					if (elem.isEnabled()) {
						if (checkBusReference(elem, busRef, jTerm)) {
							writeTerminalPower(pw, elem, jTerm[0], opt);
							pw.println();
						}
					}
				}
				break;
			}
			pw.close();
			fw.close();

			Util.fireOffEditor(fileName);
		} catch (IOException e) {
			DSS.doSimpleMsg("Error encountered showing bus powers: " + e.getMessage(), 2190);
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

			Circuit ckt = DSS.activeCircuit;
			SolutionObj sol = ckt.getSolution();

			/* Set source voltage injection currents */

			/* All phase faults */
			pw.println("FAULT STUDY REPORT");
			pw.println();
			pw.println("ALL-Node Fault Currents");
			pw.println();
			pw.println(Util.pad("Bus", maxBusNameLength) + "       Node 1  X/R        Node 2  X/R        Node 3  X/R   ...  (Amps)");
			pw.println();
			for (iBus = 0; iBus < ckt.getNumBuses(); iBus++) {
				/* Bus Norton equivalent current, Isc has been previously computed */
				bus = ckt.getBus(iBus);
				pw.print(Util.pad(Util.encloseQuotes(ckt.getBusList().get(iBus).toUpperCase()) + ",", maxBusNameLength + 2));
				for (i = 0; i < bus.getNumNodesThisBus(); i++) {
					currMag = bus.getBusCurrent(i).abs();
					if (i > 1)
						pw.print(", ");
						pw.print(currMag);
						if (currMag > 0.0) {
							pw.print(", " + MathUtil.getXR( bus.getVBus()[i].divide( bus.getBusCurrent(i) ) ));
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
			pw.println(Util.pad("Bus", maxBusNameLength) + "   Node      Amps         Node 1     Node 2     Node 3    ...");
			pw.println();

			/* Solve for fault injection currents */
			for (iBus = 0; iBus < ckt.getNumBuses(); iBus++) {
				/* Bus Norton equivalent current, Isc has been previously computed */
				bus = ckt.getBus(iBus);
				ZFault = new CMatrix(bus.getNumNodesThisBus());
				ZFault.copyFrom(bus.getZsc());

				for (iphs = 0; iphs < bus.getNumNodesThisBus(); iphs++) {
					IFault = bus.getVBus()[iphs].divide( bus.getZsc().get(iphs, iphs) );
					pw.print(Util.pad( Util.encloseQuotes(ckt.getBusList().get(iBus).toUpperCase()), maxBusNameLength + 2) + iphs + IFault.abs() + "   ");
					for (i = 0; i < bus.getNumNodesThisBus(); i++) {
						Vphs = bus.getVBus()[i].subtract( bus.getZsc().get(i, iphs).multiply(IFault) ).abs();
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
				bus = ckt.getBus(iBus);
				YFault = new CMatrix(bus.getNumNodesThisBus());
				VFault = new Complex[bus.getNumNodesThisBus()];

				GFault = new Complex(10000.0, 0.0);

				for (iphs = 0; iphs < bus.getNumNodesThisBus(); iphs++) {
					YFault.copyFrom(bus.getYsc());
					YFault.add(iphs, iphs, GFault);
					YFault.add(iphs + 1, iphs + 1, GFault);
					YFault.addSym(iphs, iphs + 1, GFault.negate());

					/* Solve for injection currents */
					YFault.invert();
					YFault.vMult(VFault, bus.getBusCurrent());  /* Gets voltage appearing at fault */

					pw.print(Util.pad(Util.encloseQuotes(ckt.getBusList().get(iBus)), maxBusNameLength + 2) + iphs + (iphs + 1) + VFault[iphs].subtract(VFault[iphs + 1]).multiply(GFault).abs() + "   ");
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

			Util.fireOffEditor(fileName);
		} catch (IOException e) {
			// TODO: handle exception
		}
	}

	private static void writeElementRecord(PrintWriter pw, CktElement pElem) {
		int nTerm, j;
		String busName;

		nTerm = pElem.getNumTerms();
		busName = Util.pad(Util.stripExtension(pElem.getFirstBus()), maxBusNameLength);
		pw.print(Util.pad(Util.fullName(pElem), maxDeviceNameLength + 2) + " ");
		for (j = 0; j < nTerm; j++) {
			pw.print(busName.toUpperCase() + " ");
			busName = Util.pad(Util.stripExtension(pElem.getNextBus()), maxBusNameLength);
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

		Circuit ckt = DSS.activeCircuit;

		setMaxBusNameLength();
		setMaxDeviceNameLength();

		try {
			fw = new FileWriter(fileName);
			pw = new PrintWriter(fw);

			disabledFileName = Util.stripExtension(fileName) + "_Disabled.txt";
			fwDisabled = new FileWriter(disabledFileName);
			pwDisabled = new PrintWriter(fw);

			if (className.length() > 0) {
				// Just give a list of active elements of a particular class
				if (DSSClassDefs.setObjectClass(className)) {
					pw.println("All Elements in Class \"" + className + "\"");
					pw.println();
					pwDisabled.println("All DISABLED Elements in Class \"" + className + "\"");
					pwDisabled.println();
					DSS.activeDSSClass = DSS.DSSClassList.get(DSS.lastClassReferenced);
					for (i = 0; i < DSS.activeDSSClass.getElementCount(); i++) {
						DSS.activeDSSClass.setActiveElement(i);
						if ((DSS.activeDSSClass.getClassType() & DSSClassDefs.BASECLASSMASK) > 0) {
							if (((CktElement) DSS.activeDSSObject).isEnabled()) {
								pw.println(DSS.activeDSSObject.getName().toUpperCase());
							} else {
								pwDisabled.println(DSS.activeDSSObject.getName().toUpperCase());
							}
						} else {
							pw.println(DSS.activeDSSObject.getName().toUpperCase());  // non cktelements
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
				pw.println(Util.pad("Element", maxDeviceNameLength + 2) + Util.pad(" Bus1", maxBusNameLength) + Util.pad(" Bus2", maxBusNameLength) + Util.pad(" Bus3", maxBusNameLength) + " ...");
				pw.println();


				pwDisabled.println();
				pwDisabled.println("DISABLED Elements in Active Circuit: " + ckt.getName());
				pwDisabled.println();
				pwDisabled.println("DISABLED Power Delivery Elements");
				pwDisabled.println();
				pwDisabled.println(Util.pad("DISABLED Element", maxDeviceNameLength + 2) + Util.pad(" Bus1", maxBusNameLength) + Util.pad(" Bus2", maxBusNameLength) + Util.pad(" Bus3", maxBusNameLength) + " ...");
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
				pw.println(Util.pad("Element", maxDeviceNameLength + 2) + Util.pad(" Bus1", maxBusNameLength) + Util.pad(" Bus2", maxBusNameLength) + Util.pad(" Bus3", maxBusNameLength) + " ...");
				pw.println();

				pwDisabled.println();
				pwDisabled.println("DISABLED Power Conversion Elements");
				pwDisabled.println();
				pwDisabled.println(Util.pad("DISABLED Element", maxDeviceNameLength + 2) + Util.pad(" Bus1", maxBusNameLength) + Util.pad(" Bus2", maxBusNameLength) + Util.pad(" Bus3", maxBusNameLength) + " ...");
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

			Util.fireOffEditor(disabledFileName);
			Util.fireOffEditor(fileName);
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

		Circuit ckt = DSS.activeCircuit;

		try {
			setMaxBusNameLength();
			maxBusNameLength += 2;

			fw = new FileWriter(fileName);
			pw = new PrintWriter(fw);

			pw.println();
			pw.println("BUSES AND NODES IN ACTIVE CIRCUIT: " + ckt.getName());
			pw.println();
			pw.println(Util.pad("     ", maxBusNameLength) + "                         Coord                        Number of     Nodes ");
			pw.println(Util.pad("  Bus", maxBusNameLength) + "    Base kV             (x, y)            Keep?       Nodes      connected ...");
			pw.println();
			for (i = 0; i < ckt.getNumBuses(); i++) {
				pw.print(Util.pad(Util.encloseQuotes(ckt.getBusList().get(i)), maxBusNameLength) + " ");
				pBus = ckt.getBus(i);
				if (pBus.getKVBase() > 0.0) {
					pw.print(pBus.getKVBase() * DSS.SQRT3);
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

			Util.fireOffEditor(fileName);
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

		Circuit ckt = DSS.activeCircuit;

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
						pw.print(Util.pad("   Reg " + String.valueOf(i), 11));
					pw.println();
					pw.println();
				}

				for (EnergyMeterObj pElem : ckt.getEnergyMeters()) {
					if (pElem != null) {
						if (pElem.isEnabled()) {
							pw.print(Util.pad(pElem.getName(), 12));
							for (j = 0; j < EnergyMeter.NUM_EM_REGISTERS; j++)
								pw.print(pElem.getRegister(j) + " ");
						}
					}
					pw.println();
				}
			}

			pw.close();
			fw.close();

			Util.fireOffEditor(fileName);
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

		Circuit ckt = DSS.activeCircuit;

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
					pw.print(Util.pad(generatorClass.getRegisterNames()[i], 11));
				pw.println();
				pw.println();
			}

			for (GeneratorObj pElem : ckt.getGenerators()) {
				if (pElem != null) {
					if (pElem.isEnabled()) {
						pw.print(Util.pad(pElem.getName(), 12));
						for (j = 0; j < Generator.NumGenRegisters; j++) {
							pw.print(pElem.getRegisters()[j] + " ");
						}
					}
				}
				pw.println();
			}

			pw.close();
			fw.close();

			Util.fireOffEditor(fileName);
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

			Circuit ckt = DSS.activeCircuit;

			for (RegControlObj pReg : ckt.getRegControls()) {
				TransformerObj t = pReg.getTransformer();
				iWind = pReg.getWinding();
				pw.print(Util.pad(t.getName(), 12) + " ");
				pw.printf("%8.5f %8.5f %8.5f %8.5f     %d", t.getPresentTap(iWind), t.getMinTap(iWind), t.getMaxTap(iWind), t.getTapIncrement(iWind), tapPosition(pReg.getTransformer(), iWind));
				pw.println();
			};

			pw.close();
			fw.close();

			Util.fireOffEditor(fileName);
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


		Parser parser = Parser.getInstance();

		try {
			fileName = Util.stripExtension(fileName);
			paramName = parser.getNextParam();
			param = parser.makeString();

			fileName = fileName+"_"+param+".txt";

			fw = new FileWriter(fileName);
			pw = new PrintWriter(fw);

			DSS.globalResult = fileName;

			pMtrClass = (EnergyMeter) DSS.DSSClassList.get(DSS.classNames.find("energymeter"));

			if (param.length() > 0) {
				pMtr = (EnergyMeterObj) pMtrClass.find(param);  // FIXME make generic
				if (pMtr == null) {
					DSS.doSimpleMsg("EnergyMeter \"" + param + "\" not found.", 220);
				} else {
					if (pMtr.getBranchList() != null) {
						pw.println("Branches and Load in Zone for EnergyMeter " + param);
						pw.println();

						PDElem = (PDElement) pMtr.getBranchList().getFirst();
						while (PDElem != null) {
							for (i = 0; i < pMtr.getBranchList().getLevel(); i++)
								pw.print(TABCHAR);
							//F.print(pMtr.getBranchList().getLevel() +" ");
							pw.print(PDElem.getParentClass().getClassName() + "." + PDElem.getName());
							CktTreeNode pb = pMtr.getBranchList().getPresentBranch();
							if (pb.isParallel())
								pw.print("(PARALLEL:" + ((CktElement) pb.getLoopLineObj()).getName()+")");
							if (pb.isLoopedHere())
								pw.print("(LOOP:" + ((CktElement) pb.getLoopLineObj()).getParentClass().getClassName()+"."+((CktElement) pb.getLoopLineObj()).getName()+")");

							if (PDElem.getSensorObj() != null) {
								pw.printf(" (Sensor: %s.%s) ", PDElem.getSensorObj().getParentClass().getClassName(), PDElem.getSensorObj().getName());
							} else {
								pw.print(" (Sensor: NIL)");
							}
							pw.println();
							loadElem = (LoadObj) pMtr.getBranchList().getFirstObject();
							while (loadElem != null) {
								for (i = 0; i < pMtr.getBranchList().getLevel() + 1; i++)
									pw.print(TABCHAR);
								pw.print(loadElem.getParentClass().getClassName() + "." + loadElem.getName());
								if (loadElem.getSensorObj() != null) {
									pw.printf(" (Sensor: %s.%s) ", loadElem.getSensorObj().getParentClass().getClassName(), loadElem.getSensorObj().getName());
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
				DSS.doSimpleMsg("Meter name not specified."+ DSS.CRLF + parser.getCmdString(), 221);
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
				Util.fireOffEditor(fileName);
				break;
			default:
				DSS.forms.showTreeView(fileName);
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

		Circuit ckt = DSS.activeCircuit;

		setMaxDeviceNameLength();

		try {
			fw = new FileWriter(fileName);
			pw = new PrintWriter(fw);

			/* Allocate cBuffer big enough for largest circuit element */
			cBuffer = new Complex[Util.getMaxCktElementSize()];

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
					if ((DSSClassDefs.CLASSMASK & PDElem.getObjType()) != DSSClassDefs.CAP_ELEMENT) {  // ignore capacitors
						NCond = PDElem.getNumConds();
						PDElem.getCurrents(cBuffer);

						for (j = 0; j < 1; j++) {  // check only terminal 1 for overloads
							if (PDElem.getNumPhases() >= 3) {
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
									pw.print(Util.pad(Util.fullName(PDElem), maxDeviceNameLength + 2) + j);
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

			Util.fireOffEditor(fileName);
		} catch (IOException e) {
			// TODO: handle exception
		}
	}

	public static void showUnserved(String fileName, boolean UE_Only) {
		FileWriter fw;
		PrintWriter pw;
//		LoadObj PLoad;
		boolean doIt;

		Circuit ckt = DSS.activeCircuit;

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
						pw.print(Util.pad(pLoad.getName(), 20));
						pw.print(Util.pad(pLoad.getBus(0), 10));
						pw.print(pLoad.getKWBase());
						pw.print(pLoad.getEEN_Factor());
						pw.print(pLoad.getUE_Factor());
						pw.println();
					}
				}
			}

			pw.close();
			fw.close();

			Util.fireOffEditor(fileName);
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

		Circuit ckt = DSS.activeCircuit;

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

					if ((DSSClassDefs.CLASSMASK & PDElem.getObjType()) == DSSClassDefs.XFMR_ELEMENT)
						transLosses = transLosses.add(kLosses);
					if ((DSSClassDefs.CLASSMASK & PDElem.getObjType()) == DSSClassDefs.LINE_ELEMENT)
						lineLosses = lineLosses.add(kLosses);

					pw.print(Util.pad(Util.fullName(PDElem), maxDeviceNameLength + 2));
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
			pw.println(Util.pad("LINE LOSSES=", 30) + lineLosses.getReal() + " kW");
			pw.println(Util.pad("TRANSFORMER LOSSES=", 30) + transLosses.getReal() + " kW");
			pw.println();
			pw.println(Util.pad("TOTAL LOSSES=", 30) + totalLosses.getReal() + " kW");

			loadPower = Complex.ZERO;
			// sum the total load kW being served in the circuit model
			for (PCElement PCElem : ckt.getLoads()) {
				if (PCElem.isEnabled()) {
					loadPower = loadPower.add(PCElem.getPower(1));
				}
			}
			loadPower = loadPower.multiply(0.001);

			pw.println();
			pw.println(Util.pad("TOTAL LOAD POWER = ", 30) + Math.abs(loadPower.getReal()) + " kW");
			pw.print(Util.pad("Percent Losses for Circuit = ", 30));
			if (loadPower.getReal() != 0.0)
				pw.println(Math.abs(totalLosses.getReal() / loadPower.getReal()) * 100.0 + " %");

			pw.close();
			fw.close();

			Util.fireOffEditor(fileName);
		} catch (IOException e) {
			// TODO: handle exception
		}
	}

	public static void showVariables(String fileName) {
		FileWriter fw;
		PrintWriter pw;
		int i;

		Circuit ckt = DSS.activeCircuit;

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
					pw.println("ELEMENT: " + PCElem.getParentClass().getClassName() + "." + PCElem.getName());
					pw.println("No. of variables: " + PCElem.numVariables());
					for (i = 0; i < PCElem.numVariables(); i++)
						pw.println("  " + PCElem.variableName(i) + " = " + String.format("%-.6g", PCElem.getVariable(i)));
					pw.println();
				}
			}
			pw.close();
			fw.close();

			Util.fireOffEditor(fileName);
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

		Circuit ckt = DSS.activeCircuit;

		// make sure bus list is built
		if (ckt.isBusNameRedefined()) ckt.reProcessBusDefs();
		/* Initialize all circuit elements to not checked */
		for (CktElement TestElement : ckt.getCktElements()) {
			TestElement.setChecked(false);
			for (i = 0; i < TestElement.getNumTerms(); i++) {
				TestElement.getTerminal(i).setChecked(false);
			}
		}

		// initialize the checked flag for all buses
		for (j = 0; j < ckt.getNumBuses(); j++)
			ckt.getBus(j).setBusChecked(false);

		// get started at main voltage source
		testElem = ckt.getSources().get(0);
		branchList = CktTree.getIsolatedSubArea(testElem);

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
				if (!ckt.getBus(j).isBusChecked())
					pw.println(Util.encloseQuotes(ckt.getBusList().get(j)));
			}

			pw.println();
			pw.println("***********  THE FOLLOWING SUB NETWORKS ARE ISOLATED ************");
			pw.println();

			for (CktElement TestElement : ckt.getCktElements()) {
				if (TestElement.isEnabled())
					if (!TestElement.isChecked())
						if ((TestElement.getObjType() & DSSClassDefs.BASECLASSMASK) == DSSClassDefs.PD_ELEMENT) {
							subArea = CktTree.getIsolatedSubArea(TestElement);
							pw.println("*** START SUBAREA ***");

							testBranch = (CktElement) subArea.getFirst();  // TODO Implement
							while (testBranch != null) {
								pw.println("(" + subArea.getLevel() + ") " + testBranch.getParentClass().getClassName() + "." + testBranch.getName());
								pElem = (CktElement) subArea.getFirstObject();
								while (pElem != null) {
									pw.println("[SHUNT], " + pElem.getParentClass().getClassName() + "." + pElem.getName());
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

			for (i = 0; i < ckt.getControls().size(); i++)
				((CktElement) ckt.getControls().get(i)).setChecked(true);
			for (i = 0; i < ckt.getMeterElements().size(); i++)
				((CktElement) ckt.getMeterElements().get(i)).setChecked(true);

			for (CktElement TestElement : ckt.getCktElements()) {
				if (TestElement.isEnabled()) {
					if (!TestElement.isChecked()) {
						pw.print("\"" + TestElement.getParentClass().getClassName() + "." + TestElement.getName() + "\"");
						pw.print("  Buses:");
						for (j = 0; j < TestElement.getNumTerms(); j++)
							pw.print("  \"" + TestElement.getBus(j) + "\"");
						pw.println();
					}
				}
			}

			pw.println();
			pw.println("***  THE FOLLOWING BUSES ARE NOT CONNECTED TO ANY POWER DELIVERY ELEMENT ***");
			pw.println();

			for (j = 0; j < ckt.getNumBuses(); j++) {
				if (!ckt.getBus(j).isBusChecked())
					pw.println(Util.encloseQuotes(ckt.getBusList().get(j)));
			}

			pw.println();
			pw.println("***********  CONNECTED CIRCUIT ELEMENT TREE ************");
			pw.println();
			pw.println("(Lexical Level) Element name");
			pw.println();

			testBranch = (CktElement) branchList.getFirst();  // FIXME Make generic
			while (testBranch != null) {
				pw.println("(" + branchList.getLevel() + ") " + testBranch.getParentClass().getClassName() + "." + testBranch.getName());
				testElem = (CktElement) branchList.getFirstObject();
				while (testElem != null) {
					pw.println("[SHUNT], " + testElem.getParentClass().getClassName() + "." + testElem.getName());
					testElem = (CktElement) branchList.getNextObject();
				}
				testBranch = (CktElement) branchList.goForward();
			}

			branchList = null;
			pw.close();
			fw.close();

			Util.fireOffEditor(fileName);
		} catch (IOException e) {
			// TODO: handle exception
		}
	}

	public static void showRatings(String fileName) {
		FileWriter fw;
		PrintWriter pw;

		Circuit ckt = DSS.activeCircuit;

		try {
			fw = new FileWriter(fileName);
			pw = new PrintWriter(fw);

			pw.println("Power Delivery Elements Normal and Emergency (max) Ratings");
			pw.println();

			for (PDElement PDElem : ckt.getPDElements()) {
				pw.print("\"" + PDElem.getParentClass().getClassName() + "." + PDElem.getName() + "\" normamps=");
				pw.printf("%-.4g,  %-.4g  !Amps", PDElem.getNormAmps(), PDElem.getEmergAmps());
				pw.println();

			}
			pw.close();
			fw.close();

			Util.fireOffEditor(fileName);
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


		try {
			fw = new FileWriter(fileName);
			pw = new PrintWriter(fw);

			pw.println("Loops and Paralleled Lines in all EnergyMeter Zones");
			pw.println();

			hMeter = DSS.energyMeterClass.getFirst();

			while (hMeter > 0) {

				pMtr = (EnergyMeterObj) DSS.activeDSSObject;

				if (pMtr.getBranchList() != null) {

					PDElem = (PDElement) pMtr.getBranchList().getFirst();
					while (PDElem != null) {

						CktTreeNode pb = pMtr.getBranchList().getPresentBranch();
						if (pb.isParallel())
							pw.println("(" + pMtr.getName() + ") " + PDElem.getParentClass().getClassName() + "." + PDElem.getName().toUpperCase() +": PARALLEL WITH " + ((CktElement) pb.getLoopLineObj()).getParentClass().getClassName() + "." + ((CktElement) pb.getLoopLineObj()).getName());
						if (pb.isLoopedHere())
							pw.println("(" + pMtr.getName() + ") " + PDElem.getParentClass().getClassName() + "." + PDElem.getName().toUpperCase() + ": LOOPED TO     " + ((CktElement) pb.getLoopLineObj()).getParentClass().getClassName() + "." + ((CktElement) pb.getLoopLineObj()).getName());

						PDElem = (PDElement) pMtr.getBranchList().goForward();
					}
				}

				hMeter = DSS.energyMeterClass.getNext();
			}
			pw.close();
			fw.close();

			Util.fireOffEditor(fileName);
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

		Circuit ckt = DSS.activeCircuit;

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
				pwTree.print(PDElem.getParentClass().getClassName() + "." + PDElem.getName());
				CktTreeNode pb = topo.getPresentBranch();
				if (pb.isParallel()) {
					nParallel += 1;
					pwTree.print("(PARALLEL:" + ((CktElement) pb.getLoopLineObj()).getName() + ")");
				}
				if (pb.isLoopedHere()) {
					nLoops++;
					pwTree.print("(LOOP:" + ((CktElement) pb.getLoopLineObj()).getParentClass().getClassName()
					+"."+((CktElement) pb.getLoopLineObj()).getName()+")");
				}
				if (PDElem.hasSensorObj()) {
					pwTree.printf(" (Sensor: %s.%s) ",
							PDElem.getSensorObj().getParentClass().getClassName(), PDElem.getSensorObj().getName());
				}
				if (PDElem.hasControl()) {
					pwTree.printf(" (Control: %s.%s) ",
							PDElem.getControlElement().getParentClass().getClassName(), PDElem.getControlElement().getName());
					if ((PDElem.getControlElement().getObjType() & DSSClassDefs.CLASSMASK) == DSSClassDefs.SWT_CONTROL)
						nSwitches++;
				}
				if (PDElem.hasEnergyMeter())
					pwTree.printf(" (Meter: %s) ", PDElem.getMeterObj().getName());
				pwTree.println();

				loadElem = (LoadObj) topo.getFirstObject();
				while (loadElem != null) {
					topoLevelTabs(pwTree, topo.getLevel() + 1);
					pwTree.print(loadElem.getParentClass().getClassName() + "." + loadElem.getName());
					if (loadElem.hasSensorObj())
						pwTree.printf(" (Sensor: %s.%s) ",
								loadElem.getSensorObj().getParentClass().getClassName(), loadElem.getSensorObj().getName());
					if (loadElem.hasControl()) {
						pwTree.printf(" (Control: %s.%s) ",
								loadElem.getControlElement().getParentClass().getClassName(), loadElem.getControlElement().getName());
						if ((loadElem.getControlElement().getObjType() & DSSClassDefs.CLASSMASK) == DSSClassDefs.SWT_CONTROL)
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
					pwTree.printf("Isolated: %s.%s", PDElemt.getParentClass().getClassName(), PDElemt.getName());
					if (PDElemt.hasSensorObj()) {
						pwTree.printf(" (Sensor: %s.%s) ",
								PDElemt.getSensorObj().getParentClass().getClassName(), PDElemt.getSensorObj().getName());
					}
					if (PDElemt.hasControl()) {
						pwTree.printf(" (Control: %s.%s) ",
								PDElemt.getControlElement().getParentClass().getClassName(), PDElemt.getControlElement().getName());

						if ((PDElemt.getControlElement().getObjType() & DSSClassDefs.CLASSMASK) == DSSClassDefs.SWT_CONTROL)
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

			Util.fireOffEditor(fileName);
			DSS.forms.showTreeView(treeName);
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


		try {
			fw = new FileWriter(fileNm);
			pw = new PrintWriter(fw);

			pw.println("LINE CONSTANTS");
			pw.println(String.format("Frequency = %.6g Hz, Earth resistivity = %.6g ohm-m", freq, rho));
			pw.println("Earth Model = " + Util.getEarthModel(DSS.defaultEarthModel));
			pw.println();

			lineCodesFileNm = "LineConstantsCode.dss";
			fw2 = new FileWriter(lineCodesFileNm);
			pw2 = new PrintWriter(fw2);

			pw2.println("!--- OpenDSS Linecodes file generated from Show LINECONSTANTS command");
			pw2.println(String.format("!--- Frequency = %.6g Hz, Earth resistivity = %.6g ohm-m", freq, rho));
			pw2.println("!--- Earth Model = " + Util.getEarthModel(DSS.defaultEarthModel));

			Line.lineGeometryClass = (LineGeometry) DSS.DSSClassList.get(DSS.classNames.find("LineGeometry"));
			Z = null;
			Yc = null;

			DSS.activeEarthModel = DSS.defaultEarthModel;

			p = Line.lineGeometryClass.getFirst();
			while (p > 0) {
				pElem = (LineGeometryObj) Line.lineGeometryClass.getActiveObj();
				Z = null;
				Yc = null;

				try {
					// get impedances per unit length
					pElem.setRhoEarth(rho);
					Z  = pElem.getZMatrix(freq, 1.0, units);
					Yc = pElem.getYcMatrix(freq, 1.0, units);
				} catch (Exception e) {
					DSS.doSimpleMsg("Error computing line constants for LineGeometry." + pElem.getName() +
							"; Error message: " + e.getMessage(), 9934);
				}

				pw.println();
				pw.println("--------------------------------------------------");
				pw.println("Geometry Code = " + pElem.getName());
				pw.println();
				pw.println("R MATRIX, ohms per " + LineUnits.lineUnitsStr(units));
				for (i = 0; i < Z.order(); i++) {
					for (j = 0; j < i; j++)
						pw.printf("%.6g, ", Z.get(i, j).getReal());
					pw.println();
				}

				pw.println();
				pw.println("jX MATRIX, ohms per " + LineUnits.lineUnitsStr(units));
				for (i = 0; i < Z.order(); i++) {
					for (j = 0; j < i; j++)
						pw.printf("%.6g, ", Z.get(i, j).getImaginary());
					pw.println();
				}

				pw.println();
				pw.println("Susceptance (jB) MATRIX, S per " + LineUnits.lineUnitsStr(units));
				for (i = 0; i < Yc.order(); i++) {
					for (j = 0; j < i; j++)
						pw.printf("%.6g, ", Yc.get(i, j).getImaginary());
					pw.println();
				}

				w = freq * DSS.TWO_PI / 1.e3;
				pw.println();
				pw.println("L MATRIX, mH per " + LineUnits.lineUnitsStr(units));
				for (i = 0; i < Z.order(); i++) {
					for (j = 0; j < i; j++)
						pw.printf("%.6g, ", Z.get(i, j).getImaginary() / w);
					pw.println();
				}

				w = freq * DSS.TWO_PI / 1.e9;
				pw.println();
				pw.println("C MATRIX, nF per " + LineUnits.lineUnitsStr(units));
				for (i = 0; i < Yc.order(); i++) {
					for (j = 0; j < i; j++)
						pw.printf("%.6g, ", Yc.get(i, j).getImaginary() / w);
					pw.println();
				}

				/* Write DSS line code record */
				//F.println();
				//F.println(,"-------------------------------------------------------------------");
				//F.println(,"-------------------DSS LineCode Definition-------------------------");
				//F.println(,"-------------------------------------------------------------------");
				pw2.println();

				pw2.println(String.format("new lineCode.%s nphases=%d  Units=%s", pElem.getName(), Z.order(), LineUnits.lineUnitsStr(units)));

				pw2.print("~ Rmatrix=[");
				for (i = 0; i < Z.order(); i++) {
					for (j = 0; j < i; j++)
						pw2.printf("%.6g  ", Z.get(i, j).getReal());
					if (i < Z.order()) pw2.print("|");
				}
				pw2.println("]");

				pw2.print("~ Xmatrix=[");
				for (i = 0; i < Z.order(); i++) {
					for (j = 0; j < i; j++)
						pw2.printf("%.6g  ", Z.get(i, j).getImaginary());
					if (i < Z.order()) pw2.print("|");
				}
				pw2.println("]");

				w = freq * DSS.TWO_PI /1.e9;
				pw2.print("~ Cmatrix=[");
				for (i = 0; i < Yc.order(); i++) {
					for (j = 0; j < i; j++)
						pw2.printf("%.6g  ", Yc.get(i, j).getImaginary() / w);
					if (i < Yc.order()) pw2.print("|");
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

				if (Z.order() == 3) {
					pw.println();
					pw.println("-------------------------------------------------------------------");
					pw.println("-------------------Equiv Symmetrical Component --------------------");
					pw.println("-------------------------------------------------------------------");
					pw.println();
					for (i = 0; i < 3; i++)
						Zs = Zs.add( Z.get(i, i) );
					for (i = 0; i < 3; i++)
						for (j = 0; j < i-1; j++)  // TODO Check zero based indexing
							Zm = Zm.add( Z.get(i, j) );

					Z1 = ComplexUtil.divide(Zs.subtract(Zm), 3.0);
					Z0 = ComplexUtil.divide(Zm.multiply(2.0).add(Zs), 3.0);
					w = freq * DSS.TWO_PI / 1000.0;
					pw.println();
					pw.println("Z1, ohms per " + LineUnits.lineUnitsStr(units) + String.format(" = %.6g + j %.6g (L1 = %.6g mH) ", Z1.getReal(), Z1.getImaginary(), Z1.getImaginary() / w));
					pw.println("Z0, ohms per " + LineUnits.lineUnitsStr(units) + String.format(" = %.6g + j %.6g (L0 = %.6g mH) ", Z0.getReal(), Z0.getImaginary(), Z0.getImaginary() / w));
					pw.println();

					/* Compute common mode series impedance */
					Z.invert();
					Ycm = Complex.ZERO;
					for (i = 0; i < 3; i++)
						for (j = 0; j < 3; j++)
							Ycm = Ycm.add(Z.get(i, j));
					Xcm = ComplexUtil.invert(Ycm).getImaginary();

					w = freq * DSS.TWO_PI /1.e9;
					/* Capacitance */
					for (i = 0; i < 3; i++)
						Cs = Cs + Yc.get(i, i).getImaginary();
					for (i = 0; i < 3; i++)
						for (j = 0; j < i - 1; j++)
							Cm = Cm + Yc.get(i, j).getImaginary();

					C1 = (Cs - Cm) / 3.0 / w;   // nF
					C0 = (Cs + 2.0 * Cm) / 3.0 / w;

					/* Compute common mode shunt capacitance */
					Ycm = Complex.ZERO;
					for (i = 0; i < 3; i++)  // add up all elements of Z inverse
						for (j = 0; j < 3; j++)
							Ycm = Ycm.add(Yc.get(i, j));
					Ccm = Ycm.getImaginary() / w;

					pw.println("C1, nF per " + LineUnits.lineUnitsStr(units) + String.format(" = %.6g", C1));
					pw.println("C0, nF per " + LineUnits.lineUnitsStr(units) + String.format(" = %.6g", C0));
					pw.println();

					w = freq * DSS.TWO_PI;
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

				p = Line.lineGeometryClass.getNext();
			}
			pw.close();
			fw.close();
			pw2.close();
			fw2.close();

			Util.fireOffEditor(fileNm);
			Util.fireOffEditor(lineCodesFileNm);
		} catch (IOException e) {
			// TODO: handle exception
		}
	}

	public static void showYPrim(String fileName) {
		FileWriter fw;
		PrintWriter pw;
		Complex[] cValues;
		int i, j;

		Circuit ckt = DSS.activeCircuit;

		if (ckt != null) {
			if (ckt.getActiveCktElement() != null) {
				try {
					fw = new FileWriter(fileName);
					pw = new PrintWriter(fw);

					CktElement ace = ckt.getActiveCktElement();

					pw.println("Yprim of active circuit element: " + ace.getParentClass().getClassName() + "." + ace.getName());
					pw.println();

					cValues = ace.getYPrimValues(DSS.ALL_YPRIM);
					if (cValues != null) {
						pw.println();
						pw.println("G matrix (conductance), S");
						pw.println();

						for (i = 0; i < ace.getYOrder(); i++) {
							for (j = 0; j < i; j++)
								pw.printf("%13.10g ", cValues[i + (j - 1) * ace.getYOrder()].getReal());
							pw.println();
						}

						pw.println();
						pw.println("jB matrix (Susceptance), S") ;
						pw.println();

						for (i = 0; i < ace.getYOrder(); i++) {
							for (j = 0; j < i; j++)
								pw.printf("%13.10g ", cValues[i + (j - 1) * ace.getYOrder()].getImaginary());
							pw.println();
						}
					} else {
						pw.println("Yprim matrix is nil");
					}
					pw.close();
					fw.close();

					Util.fireOffEditor(fileName);
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
		UUID hY;
		int nnz, nBus;
		int i, row, col;
		double re, im;
		int[] colIdx, rowIdx;
		Complex[] cVals;
		int[] ip = new int[1];

		Circuit ckt = DSS.activeCircuit;

		if (ckt == null)
			return;

		hY = ckt.getSolution().getY();
		if (hY == null) {
			DSS.doSimpleMsg("Y Matrix not Built.", 222);
			return;
		}

		// print lower triangle of G and B using new functions
		// this compresses the entries if necessary - no extra work if already solved
		YMatrix.factorSparseMatrix(hY);
		YMatrix.getNNZ(hY, ip);
		nnz = ip[0];
		YMatrix.getSize(hY, ip);  // we should already know this
		nBus = ip[0];

		try {
			colIdx = new int[nnz];
			rowIdx = new int[nnz];
			cVals = new Complex[nnz];
			YMatrix.getTripletMatrix(hY, nnz, rowIdx, colIdx, cVals);

			fw = new FileWriter(fileName);
			pw = new PrintWriter(fw);

			pw.println("System Y Matrix (Lower Triangle by Columns)");
			pw.println();
			pw.println("  Row  Col               G               B");
			pw.println();

			// shows how to easily traverse the triplet format
			for (i = 0; i < nnz; i++) {  // TODO Check zero based indexing
				col = colIdx[i];
				row = rowIdx[i];
				if (row >= col) {
					re = cVals[i].getReal();
					im = cVals[i].getImaginary();
					pw.printf("[%4d,%4d] = %13.10g + j%13.10g", row, col, re, im);
					pw.println();
				}
			}
			pw.close();
			fw.close();

			Util.fireOffEditor(fileName);
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

		Circuit ckt = DSS.activeCircuit;
		SolutionObj sol = ckt.getSolution();

		maxNodeCurrent = null;
		try {
			fw = new FileWriter(fileName);
			pw = new PrintWriter(fw);

			// zero out the nodal current array
			for (i = 0; i < ckt.getNumNodes(); i++)
				sol.setCurrent(i, Complex.ZERO);
			// make temp storage for max current at node
			maxNodeCurrent = new double[ckt.getNumNodes() + 1];
			for (i = 0; i < ckt.getNumNodes(); i++)
				maxNodeCurrent[i] = 0.0;
			// now sum in each device current, keep track of the largest current at a node.
			for (CktElement pCktElement : ckt.getCktElements()) {
				if (pCktElement.isEnabled()) {
					pCktElement.computeITerminal();
					for (i = 0; i < pCktElement.getYOrder(); i++) {
						CTemp =  pCktElement.getITerminal(i);
						nRef  =  pCktElement.getNodeRef(i);
						sol.setCurrent(nRef, sol.getCurrent(nRef).add(CTemp));  // nodeRef = 0 is OK  TODO Check
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
			pw.println(Util.pad("Bus,", maxBusNameLength) + " Node, \"Current Sum (A)" + "%error" + "Max Current (A)\"");

			// ground bus
			nRef = 0;
			dTemp = sol.getCurrent(nRef).abs();
			if ((maxNodeCurrent[nRef] == 0.0) || (maxNodeCurrent[nRef] == dTemp)) {
				pctError = String.format("%10.1f", 0.0);
			} else {
				pctError = String.format("%10.6f", dTemp / maxNodeCurrent[nRef] * 100.0);
			}
			bName = Util.pad("\"System Ground\"", maxBusNameLength);
			pw.println(String.format("%s, %2d, %10.5f,       %s, %10.5f", bName, nRef, dTemp, pctError, maxNodeCurrent[nRef]));

			for (i = 0; i < ckt.getNumBuses(); i++) {
				for (j = 0; j < ckt.getBus(i).getNumNodesThisBus(); j++) {
					nRef = ckt.getBus(i).getRef(j);
					dTemp = sol.getCurrent(nRef).abs();
					if ((maxNodeCurrent[nRef] == 0.0) || (maxNodeCurrent[nRef] == dTemp)) {
						pctError = String.format("%10.1f", 0.0);
					} else {
						pctError = String.format("%10.6f", dTemp / maxNodeCurrent[nRef] * 100.0);
					}
					if (j == 0) {
						bName = Util.padDots(Util.encloseQuotes(ckt.getBusList().get(i)), maxBusNameLength);
					} else {
						bName = Util.pad("\"   -\"", maxBusNameLength);
					}
					pw.println(String.format("%s, %2d, %10.5f,       %s, %10.5f", bName, ckt.getBus(i).getNum(j), dTemp, pctError, maxNodeCurrent[nRef]));
				}
			}
			pw.close();
			fw.close();

			Util.fireOffEditor(fileName);

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

		Circuit ckt = DSS.activeCircuit;

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
				pBus = ckt.getBus( pLoad.getTerminal(0).getBusRef() );
				busName = ckt.getBusList().get( pLoad.getTerminal(0).getBusRef() );
				if (pBus.getKVBase() != 0.0) {
					if ((pLoad.getNumPhases() == 1) && (pLoad.getConnection() == 0)) {
						if (Math.abs(pLoad.getKVLoadBase() - pBus.getKVBase()) > 0.10 * pBus.getKVBase()) {
							pw.println(String.format("!!!!! Voltage Base Mismatch, Load.%s.kV=%.6g, Bus %s LN kvBase = %.6g", pLoad.getName(), pLoad.getKVLoadBase(), pLoad.getBus(0), pBus.getKVBase()));
							pw.println(String.format("!setkvbase %s kVLN=%.6g", busName, pLoad.getKVLoadBase()));
							pw.println(String.format("!Load.%s.kV=%.6g", pLoad.getName(), pBus.getKVBase()));
						}
					} else {
						busKV = pBus.getKVBase() * DSS.SQRT3;
						if (Math.abs(pLoad.getKVLoadBase() - busKV) > 0.10 * busKV) {
							pw.println(String.format("!!!!! Voltage Base Mismatch, Load.%s.kV=%.6g, Bus %s kvBase = %.6g", pLoad.getName(), pLoad.getKVLoadBase(), pLoad.getBus(0), busKV));
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
				pBus = ckt.getBus( pGen.getTerminal(0).getBusRef() );
				busName = ckt.getBusList().get( pGen.getTerminal(0).getBusRef() );
				if (pBus.getKVBase() != 0.0) {
					if ((pGen.getNumPhases() == 1) && (pGen.getConnection() == 0)) {
						if (Math.abs(pGen.getGenVars().kVGeneratorBase - pBus.getKVBase()) > 0.10 * pBus.getKVBase()) {
							pw.println(String.format("!!! Voltage Base Mismatch, Generator.%s.kV=%.6g, Bus %s LN kvBase = %.6g", pGen.getName(), pGen.getGenVars().kVGeneratorBase, pGen.getBus(1), pBus.getKVBase()));
							pw.println(String.format("!setkvbase %s kVLN=%.6g", busName, pGen.getGenVars().kVGeneratorBase));
							pw.println(String.format("!Generator.%s.kV=%.6g", pGen.getName(), pBus.getKVBase()));
						}
					} else {
						busKV = pBus.getKVBase() * DSS.SQRT3;
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

			Util.fireOffEditor(fileName);
		} catch (IOException e) {
			// TODO: handle exception
		}
	}

	public static void showDeltaV(String fileName) {
		FileWriter fw;
		PrintWriter pw;

		Circuit ckt = DSS.activeCircuit;

		try {
			fw = new FileWriter(fileName);
			pw = new PrintWriter(fw);

			setMaxDeviceNameLength();

			pw.println();
			pw.println("VOLTAGES ACROSS CIRCUIT ELEMENTS WITH 2 TERMINALS");
			pw.println();
			pw.println("Source Elements");
			pw.println();
			pw.println(Util.pad("Element,", maxDeviceNameLength) + " Conductor,     Volts,   Percent,           kVBase,  Angle");
			pw.println();

			// sources first
			for (CktElement pElem : ckt.getSources()) {
				if (pElem.isEnabled() && (pElem.getNumTerms() == 2)) {
					writeElementDeltaVoltages(pw, pElem);
					pw.println();
				}
			}

			pw.println();
			pw.println("Power Delivery Elements");
			pw.println();
			pw.println(Util.pad("Element,", maxDeviceNameLength) + " Conductor,     Volts,   Percent,           kVBase,  Angle");
			pw.println();


			// PD elements next
			for (CktElement pElem : ckt.getPDElements()) {
				if (pElem.isEnabled() && (pElem.getNumTerms() == 2)) {
					writeElementDeltaVoltages(pw, pElem);
					pw.println();
				}
			}

			pw.println("= = = = = = = = = = = = = = = = = = =  = = = = = = = = = = =  = =");
			pw.println();
			pw.println("Power Conversion Elements");
			pw.println();
			pw.println(Util.pad("Element,", maxDeviceNameLength) + " Conductor,     Volts,   Percent,           kVBase,  Angle");
			pw.println();

			// PC elements next
			for (CktElement pElem : ckt.getPCElements()) {
				if (pElem.isEnabled() && (pElem.getNumTerms() == 2)) {
					writeElementDeltaVoltages(pw, pElem);
					pw.println();
				}
			}
			pw.close();
			fw.close();

			Util.fireOffEditor(fileName);
		} catch (IOException e) {
			// TODO: handle exception
		}
	}

}
