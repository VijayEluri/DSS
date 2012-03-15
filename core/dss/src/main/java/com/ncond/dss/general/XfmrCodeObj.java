package com.ncond.dss.general;

import java.io.OutputStream;
import java.io.PrintWriter;

import lombok.Getter;
import lombok.Setter;

import com.ncond.dss.common.DSS;
import com.ncond.dss.common.DSSClass;
import com.ncond.dss.common.Util;
import com.ncond.dss.delivery.Transformer;
import com.ncond.dss.delivery.TransformerObj;
import com.ncond.dss.delivery.Winding;

@Getter @Setter
public class XfmrCodeObj extends DSSObject {

	private int nPhases;
	private int activeWinding;
	private int numWindings;
	private int maxWindings;
	private double XHL, XHT, XLT;  // per unit
	private double[] XSC;     // per unit SC measurements
	private double VABase;    // for impedances
	private double normMaxHKVA;
	private double emergMaxHKVA;
	private double thermalTimeConst;  // hr
	private double nThermal;
	private double mThermal;  // exponents
	private double LRise;
	private double HSRise;
	private double pctLoadLoss;
	private double pctNoLoadLoss;
	private double ppmFloatFactor;  // parts per million winding float factor
	private double pctImag;
	private Winding[] windings;

	public XfmrCodeObj(DSSClass parClass, String xfmrCodeName) {
		super(parClass);
		setName(xfmrCodeName.toLowerCase());
		objType = parClass.getClassType();

		// default values and sizes
		nPhases       = 3;
		numWindings   = 2;
		maxWindings   = 2;
		activeWinding = 0;
		windings = new Winding[maxWindings];
		for (int i = 0; i < maxWindings; i++)
			windings[i] = new Winding();
		XHL = 0.07;
		XHT = 0.35;
		XLT = 0.30;
		XSC = new double[(numWindings - 1) * numWindings / 2];
		VABase           = windings[0].getKVA() * 1000.0;
		thermalTimeConst = 2.0;
		nThermal         = 0.8;
		mThermal         = 0.8;
		LRise            = 65.0;
		HSRise           = 15.0;  // hot spot rise
		normMaxHKVA      = 1.1 * windings[0].getKVA();
		emergMaxHKVA     = 1.5 * windings[0].getKVA();
		pctLoadLoss      = 2.0 * windings[0].getRpu() * 100.0;  // assume two windings
		ppmFloatFactor   = 0.000001;
		/* Compute antifloat added for each winding */
		for (int i = 0; i < numWindings; i++)
			windings[i].computeAntiFloatAdder(ppmFloatFactor, VABase / nPhases);
		pctNoLoadLoss = 0.0;
		pctImag = 0.0;

		initPropertyValues(0);
	}

	public void setNumWindings(int n) {
		int oldWdgSize;
		int newWdgSize;

		if (n > 1) {
			for (int i = 0; i < numWindings; i++)
				windings[i] = null;  // free old winding objects
			oldWdgSize = (numWindings - 1) * numWindings / 2;
			numWindings = n;
			maxWindings = n;
		        newWdgSize = (numWindings - 1) * numWindings / 2;
			windings = Util.resizeArray(windings, maxWindings);  // reallocate collector array
			for (int i = 0; i < maxWindings; i++)
				windings[i] = new Winding();
			XSC = Util.resizeArray(XSC, newWdgSize);
			for (int i = oldWdgSize; i < newWdgSize; i++)
				XSC[i] = 0.30;   // default to something
		} else {
			DSS.doSimpleMsg("Invalid number of windings: " + String.valueOf(n) + " for Transformer " +
					Transformer.activeTransfObj.getName(), 111);
		}
	}

	public void pullFromTransformer(TransformerObj obj) {
		int i;

		setNumWindings(obj.getNumWindings());
		nPhases = obj.getNumPhases();
		XHL = obj.getXHL();
		XHT = obj.getXHT();
		XLT = obj.getXLT();
		VABase = obj.getVABase();
		normMaxHKVA = obj.getNormMaxHKVA();
		emergMaxHKVA = obj.getEmergMaxHKVA();
		thermalTimeConst = obj.getThTau();
		nThermal = obj.getThN();
		mThermal = obj.getThM();
		LRise = obj.getFLRise();
		HSRise = obj.getHSRise();
		pctLoadLoss = obj.getPctLoadLoss();
		pctNoLoadLoss = obj.getPctNoLoadLoss();
		ppmFloatFactor = obj.getPpmFloatFactor();
		pctImag = obj.getPctImag();
		for (i = 0; i < (numWindings - 1) * numWindings / 2; i++)
			XSC[i] = obj.getXSC(i);
		for (i = 0; i < numWindings; i++) {
			windings[i].setConnection(obj.getWdgConnection(i));
			windings[i].setKVLL(obj.getBasekVLL(i));
			windings[i].setVbase(obj.getBaseVoltage(i));
			windings[i].setKVA(obj.getWdgKVA(i));
			windings[i].setPuTap(obj.getPresentTap(i));
			windings[i].setRpu(obj.getWdgResistance(i));
			windings[i].setRNeut(obj.getWdgRNeutral(i));
			windings[i].setXNeut(obj.getWdgXNeutral(i));
			windings[i].setY_PPM(obj.getWdgYPPM(i));
			windings[i].setTapIncrement(obj.getTapIncrement(i));
			windings[i].setMinTap(obj.getMinTap(i));
			windings[i].setMaxTap(obj.getMaxTap(i));
			windings[i].setNumTaps(obj.getNumTaps(i));
		}
	}

	@Override
	public void dumpProperties(OutputStream out, boolean complete) {
		super.dumpProperties(out, complete);

		PrintWriter pw = new PrintWriter(out);

		/* Basic property dump */
		pw.println("~ " + "numWindings=" + numWindings);
		pw.println("~ " + "phases=" + nPhases);

		for (int i = 0; i < numWindings; i++) {
			Winding wdg = windings[i];
			if (i == 0) {
				pw.println("~ " + "Wdg=" + i);
			} else {
				pw.println("~ " + "Wdg=" + i);
			}
			switch (wdg.getConnection()) {
			case WYE:
				pw.println("~ conn=wye");
				break;
			case DELTA:
				pw.println("~ conn=delta");
				break;
			}
			pw.println("~ kv=" + wdg.getKVLL());
			pw.println("~ kva=" + wdg.getKVA());
			pw.println("~ tap=" + wdg.getPuTap());
			pw.println("~ %r=" + wdg.getRpu() * 100.0);
			pw.println("~ rneut=" + wdg.getRNeut());
			pw.println("~ xneut=" + wdg.getXNeut());
		}

		pw.println("~ " + "xhl=" + XHL * 100.0);
		pw.println("~ " + "xht=" + XHT * 100.0);
		pw.println("~ " + "xlt=" + XLT * 100.0);
		pw.print("~ Xscmatrix= \"");
		for (int i = 0; i < (numWindings - 1) * numWindings / 2; i++)
			pw.print(XSC[i] * 100.0 + " ");
		pw.println("\"");
		pw.println("~ " + "NormMAxHkVA=" + normMaxHKVA);
		pw.println("~ " + "EmergMAxHkVA=" + emergMaxHKVA);
		pw.println("~ " + "thermal=" + thermalTimeConst);
		pw.println("~ " + "n=" + nThermal);
		pw.println("~ " + "m=" + mThermal);
		pw.println("~ " + "flrise=" + LRise);
		pw.println("~ " + "hsrise=" + HSRise);
		pw.println("~ " + "%loadloss=" + pctLoadLoss);
		pw.println("~ " + "%noloadloss=" + pctNoLoadLoss);

		for (int i = 27; i < XfmrCode.NumPropsThisClass; i++)
			pw.println("~ " + parentClass.getPropertyName(i) + "=" + getPropertyValue(i));

		for (int i = XfmrCode.NumPropsThisClass; i < parentClass.getNumProperties(); i++)
			pw.println("~ " + parentClass.getPropertyName(i) + "=" + getPropertyValue(i));

		pw.close();
	}

	/**
	 * Gets the property for the active winding.
	 * Set the active winding before calling.
	 */
	@Override
	public String getPropertyValue(int index) {
		String result;
		switch (index) {
		case 10:
			result = "[";
			break;
		case 11:
			result = "[";
			break;
		case 12:
			result = "[";
			break;
		case 13:
			result = "[";
			break;
		case 17:
			result = "[";
			break;
		case 32:
			result = "[";
			break;
		default:
			result = "";
			break;
		}

		switch (index) {
		case 2:
			result = String.valueOf(activeWinding);  // return active winding
			break;
		case 3:
			switch (windings[activeWinding].getConnection()) {
			case WYE:
				result = "wye ";
				break;
			case DELTA:
				result = "delta ";
				break;
			}
			break;
		case 4:
			result = String.format("%.7g", windings[activeWinding].getKVLL());
			break;
		case 5:
			result = String.format("%.7g", windings[activeWinding].getKVA());
			break;
		case 6:
			result = String.format("%.7g", windings[activeWinding].getPuTap());
			break;
		case 7:
			result = String.format("%.7g", windings[activeWinding].getRpu() * 100.0);   // %R
			break;
		case 8:
			result = String.format("%.7g", windings[activeWinding].getRNeut());
			break;
		case 9:
			result = String.format("%.7g", windings[activeWinding].getXNeut());
			break;
		case 10:
			for (int i = 0; i < numWindings; i++)
				switch (windings[i].getConnection()) {
				case WYE:
					result = result + "wye, ";
					break;
				case DELTA:
					result = result + "delta, ";
					break;
				}
			break;
		case 11:
			for (int i = 0; i < numWindings; i++)
				result = result + String.format("%.7g, ", windings[i].getKVLL());
			break;
		case 12:
			for (int i = 0; i < numWindings; i++)
				result = result + String.format("%.7g, ", windings[i].getKVA());
			break;
		case 13:
			for (int i = 0; i < numWindings; i++)
				result = result + String.format("%.7g, ", windings[i].getPuTap());
			break;
		case 17:
			for (int i = 0; i < (numWindings - 1) * numWindings / 2; i++)
				result = result + String.format("%-g, ", XSC[i] * 100.0);
		case 23:
			result = String.format("%.7g", pctLoadLoss);
			break;
		case 24:
			result = String.format("%.7g", pctNoLoadLoss);
			break;
		case 27:
			result = String.format("%.7g", windings[activeWinding].getMaxTap());
			break;
		case 28:
			result = String.format("%.7g", windings[activeWinding].getMinTap());
			break;
		case 29:
			result = String.format("%-d", windings[activeWinding].getNumTaps());
			break;
		case 32:
			for (int i = 0; i < numWindings; i++)
				result = result + String.format("%.7g, ", windings[i].getRpu() * 100.0);
			break;
		default:
			result = super.getPropertyValue(index);
			break;
		}


		switch (index) {
		case 10:
			result = "]";
			break;
		case 11:
			result = "]";
			break;
		case 12:
			result = "]";
			break;
		case 13:
			result = "]";
			break;
		case 17:
			result = "]";
			break;
		case 32:
			result = "]";
			break;
		default:
			result = "";
			break;
		}

		return result;
	}

	@Override
	public void initPropertyValues(int arrayOffset) {

		setPropertyValue(0, "3"); // "phases");
		setPropertyValue(1, "2"); // "windings");
		setPropertyValue(2, "1"); // "wdg");
		setPropertyValue(3, "wye");    // "conn");
		setPropertyValue(4, "12.47");  // if 2or 3-phase: phase-phase else actual winding
		setPropertyValue(5, "1000");
		setPropertyValue(6, "1.0");
		setPropertyValue(7, "0.2");
		setPropertyValue(8, "-1");
		setPropertyValue(9, "0");
		setPropertyValue(10, "");
		setPropertyValue(11, "");  // if 1-phase: actual winding rating; else phase-phase
		setPropertyValue(12, "");  // if 1-phase: actual winding rating; else phase-phase
		setPropertyValue(13, "");
		setPropertyValue(14, "7");
		setPropertyValue(15, "35");
		setPropertyValue(16, "30");
		setPropertyValue(17, "");  // x12 13 14... 23 24.. 34 ..
		setPropertyValue(18, "2");
		setPropertyValue(19, ".8");
		setPropertyValue(20, ".8");
		setPropertyValue(21, "65");
		setPropertyValue(22, "15");
		setPropertyValue(23, "0");
		setPropertyValue(24, "0");
		setPropertyValue(25, "");
		setPropertyValue(26, "");
		setPropertyValue(27, "1.10");
		setPropertyValue(28, "0.90");
		setPropertyValue(29, "32");
		setPropertyValue(30, "0");
		setPropertyValue(31, "1");
		setPropertyValue(32, "");

		super.initPropertyValues(XfmrCode.NumPropsThisClass - 1);
	}

	public Winding getWinding(int idx) {
		return windings[idx];
	}

}
