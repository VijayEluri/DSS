package com.ncond.dss.delivery;

import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.commons.math.complex.Complex;

import com.ncond.dss.common.DSS;
import com.ncond.dss.common.DSSClass;
import com.ncond.dss.common.SolutionObj;
import com.ncond.dss.common.Util;
import com.ncond.dss.parser.Parser;
import com.ncond.dss.shared.CMatrix;
import com.ncond.dss.shared.ComplexUtil;
import com.ncond.dss.shared.MathUtil;

/**
 * Uses same rules as Capacitor and Fault for connections
 *
 * Implemented as a two-terminal constant impedance (power delivery element)
 * Defaults to a shunt reactor but can be connected as a two-terminal series
 * reactor
 *
 * If parallel=Yes, then the R and X components are treated as being in
 * parallel
 *
 * bus2 connection defaults to 0 node of bus1 (if bus2 has the default bus
 * connection at the time bus1 is defined.  Therefore, if only bus1 is
 * specified, a shunt reactor results.
 * If delta connected, bus2 is set to node zero of bus1 and nothing is returned
 * in the lower half of YPrim - all zeroes.
 *
 * If an ungrounded wye is desired, explicitly set bus2= and set all nodes the
 * same,
 *   e.g. bus1.4.4.4   (uses 4th node of bus1 as neutral point)
 *     or busNew.1.1.1  (makes a new bus for the neutral point)
 * You must specify the nodes or you will get a series Reactor!
 *
 * A series reactor is specified simply by setting bus2 and declaring the
 * connection to be wye.  If the connection is specified as delta, nothing will
 * be connected to bus2. In fact the number of terminals is set to 1.
 *
 * Reactance may be specified as:
 *
 *   1.  kVAr and kV ratings at base frequency.  impedance.  Specify kVAr as
 *       total for all phases. For 1-phase, kV = reactor coil kV rating.
 *       For 2 or 3-phase, kV is line-line three phase. For more than 3 phases,
 *       specify kV as actual coil voltage.
 *   2.  Series resistance and reactance in ohns at base frequency to be used
 *       in each phase.  If specified in this manner, the given value is always
 *       used whether wye or delta.
 *   3.  A, R and X matrices.
 *       If conn=wye then 2-terminal through device
 *       If conn=delta then 1-terminal.
 *       Ohms at base frequency
 *       Note that Rmatrix may be in parallel with Xmatrix (set parallel = Yes)
 *
 */
public class ReactorObj extends PDElement {

	private double R, Rp, Gp, X, kVArRating, kVRating;
	/* If not null then overrides C */
	private double[] RMatrix, GMatrix, XMatrix, BMatrix;

	private int connection;  // 0 or 1 for wye (default) or delta, respectively
	private int specType;    // 1=kVAr, 2=r+jx, 3=r and x matrices

	private boolean isParallel;
	private boolean RpSpecified;

	public ReactorObj(DSSClass parClass, String reactorName) {
		super(parClass);
		setName(reactorName.toLowerCase());
		objType = parClass.getClassType();

		setNumPhases(3);  // directly set conds and phases
		nConds = 3;
		setNumTerms(2);   // force allocation of terminals and conductors

		setBus(1, (getBus(0) + ".0.0.0"));  // default to grounded wye

		isShunt = true;

		RMatrix = null;
		XMatrix = null;
		GMatrix = null;
		BMatrix = null;

		kVArRating = 100.0;
		kVRating   = 12.47;
		X          = Math.pow(kVRating, 2) * 1000.0 / kVArRating;
		R          = 0.0;
		Rp         = 0.0;  // indicates it has not been set to a proper value
		isParallel  = false;
		RpSpecified = false;
		connection  = 0;  // 0 or 1 for wye (default) or delta, respectively
		specType    = 1;  // 1=kVAr, 2=Cuf, 3=CMatrix
		normAmps    = kVArRating * DSS.SQRT3 / kVRating;
		emergAmps   = getNormAmps() * 1.35;
		faultRate   = 0.0005;
		pctPerm     = 100.0;
		hrsToRepair = 3.0;
		YOrder      = nTerms * nConds;
		recalcElementData();

		initPropertyValues(0);
	}

	@Override
	public void recalcElementData() {
		double kVArPerPhase, phaseKV;
		int i;
		int[] checkError = new int[1];

		switch (specType) {
		case 1:  // kVAr
			kVArPerPhase = kVArRating / nPhases;
			switch (connection) {
			case 1:  // line-to-line
				phaseKV = kVRating;
				break;
			default:  //  line-to-neutral
				switch (nPhases) {
				case 2:
					phaseKV = kVRating / DSS.SQRT3;  // assume three phase system
					break;
				case 3:
					phaseKV = kVRating / DSS.SQRT3;
					break;
				default:
					phaseKV = kVRating;
					break;
				}
				break;
			}
			X = Math.pow(phaseKV, 2) * 1000.0 / kVArPerPhase;
			/* Leave r as specified */
			setNormAmps(kVArPerPhase / phaseKV);
			setEmergAmps(getNormAmps() * 1.35);
			break;
		case 2:  // r + jx
			// nothing to do
			break;
		case 3:  // matrices
			break;
		}

		if (RpSpecified && Rp != 0.0) {
			Gp = 1.0 / Rp;
		} else {
			Gp = 0.0;  // default to 0,0 if Rp=0;
		}

		if (isParallel && specType == 3) {

			GMatrix = Util.resizeArray(GMatrix, nPhases * nPhases);
			BMatrix = Util.resizeArray(BMatrix, nPhases * nPhases);

			/* Copy rMatrix to gMatrix and invert */
			for (i = 0; i < nPhases * nPhases; i++)
				GMatrix[i] = RMatrix[i];
			MathUtil.ETKInvert(RMatrix, nPhases, checkError);
			if (checkError[0] > 0) {
				DSS.doSimpleMsg("Error inverting R matrix for Reactor."+getName()+" - G is zeroed.", 232);
				for (i = 0; i < nPhases * nPhases; i++)
					GMatrix[i] = 0.0;
			}

			/* Copy xMatrix to bMatrix and invert */
			for (i = 0; i < nPhases * nPhases; i++) {
				BMatrix[i] = -XMatrix[i];
				MathUtil.ETKInvert(BMatrix, nPhases, checkError);
				if (checkError[0] > 0) {
					DSS.doSimpleMsg("Error inverting X matrix for Reactor."+getName()+" - B is zeroed.", 233);
					for (i = 0; i < nPhases * nPhases; i++)
						GMatrix[i] = 0.0;
				}
			}
		}
	}

	@Override
	public void calcYPrim() {

		Complex value, value2;
		int i, j, idx;
		double freqMultiplier;
		Complex[] ZValues;
		CMatrix YPrimTemp, ZMatrix;

		// normally build only Yprim_Shunt, but if there are 2 terminals and
		// bus1 != bus2

		if (isYprimInvalid()) {  // reallocate YPrim if something has invalidated old allocation
			YPrimShunt = new CMatrix(YOrder);
			YPrimSeries = new CMatrix(YOrder);
			YPrim = new CMatrix(YOrder);
		} else {
			YPrimSeries.clear(); // zero out YPrim
			YPrimShunt.clear();  // zero out YPrim
			YPrim.clear();
		}

		if (isShunt()) {
			YPrimTemp = YPrimShunt;
		} else {
			YPrimTemp = YPrimSeries;
		}


		YPrimFreq = DSS.activeCircuit.getSolution().getFrequency();
		freqMultiplier = YPrimFreq / baseFrequency;

		/* Now, put in Yprim matrix */

		switch (specType) {
		case 1:  /* Some form of r and x specified */
			// adjust for frequency
			value = ComplexUtil.invert(new Complex(R, X * freqMultiplier));
			// add in rP value if specified
			if (RpSpecified)
				value = value.add(new Complex(Gp, 0.0));

			switch (connection) {
			case 1:  // line-line
				value2 = value.multiply(2.0);
				value = value.negate();
				for (i = 0; i < nPhases; i++) {
					YPrimTemp.set(i, i, value2);
					for (j = 0; j < i; j++)
						YPrimTemp.setSym(i, j, value);
					// remainder of the matrix is all zero
				}
				break;
			default:  // wye
				for (i = 0; i < nPhases; i++) {
					YPrimTemp.set(i, i, value);  // elements are only on the diagonals
					YPrimTemp.set(i + nPhases, i + nPhases, value);
					YPrimTemp.setSym(i, i + nPhases, value.negate());
				}
				break;
			}
			break;
		case 2:  /* Some form of r and x specified */
			// adjust for frequency
			value = ComplexUtil.invert(new Complex(R, X * freqMultiplier));
			// add in rP value if specified
			if (RpSpecified)
				value = value.add(new Complex(Gp, 0.0));

			switch (connection) {
			case 1:  // line-line
				value2 = value.multiply(2.0);
				value = value.negate();
				for (i = 0; i < nPhases; i++) {
					YPrimTemp.set(i, i, value2);
					for (j = 0; j < i; j++)
						YPrimTemp.setSym(i, j, value);
					// remainder of the matrix is all zero
				}
				break;
			default:  // wye
				for (i = 0; i < nPhases; i++) {
					YPrimTemp.set(i, i, value);  // elements are only on the diagonals
					YPrimTemp.set(i + nPhases, i + nPhases, value);
					YPrimTemp.setSym(i, i + nPhases, value.negate());
				}
				break;
			}
			break;
		case 3:  // Z matrix specified
			/* Compute Z matrix */

			/* Put in parallel r & l */
			if (isParallel) {  // build Z as a Y matrix

				for (i = 0; i < nPhases; i++) {
					for (j = 0; j < nPhases; j++) {
						idx = j * nPhases + i;
						value = new Complex(GMatrix[idx], BMatrix[idx] / freqMultiplier);
						YPrimTemp.set(i, j, value);
						YPrimTemp.set(i + nPhases, j + nPhases, value);
						YPrimTemp.setSym(i, j + nPhases, value.negate());
					}
				}
			} else {  // for series r and x
				ZMatrix = new CMatrix(nPhases);
				ZValues = ZMatrix.asArray();  // so we can populate array fast
				nPhases = ZMatrix.order();
				/* Put in series r & l */
				for (i = 0; i < nPhases * nPhases; i++)
					// correct the impedances for frequency
					ZValues[i] = new Complex(RMatrix[i], XMatrix[i] * freqMultiplier);

				ZMatrix.invert();  /* Invert in place - is now Y matrix */
				if (ZMatrix.getErrorCode() > 0) {  /* If error, put in tiny series conductance */
					DSS.doErrorMsg("ReactorObj.calcYPrim()", "Matrix inversion error for reactor \"" + getName() + "\"",
									"Invalid impedance specified. Replaced with tiny conductance.", 234);
					ZMatrix.clear();
					for (i = 0; i < nPhases; i++)
						ZMatrix.set(i, i, new Complex(DSS.EPSILON, 0.0));

					for (i = 0; i < nPhases; i++)
						for (j = 0; j < nPhases; j++) {
							value = ZMatrix.get(i, j);
							YPrimTemp.set(i, j, value);
							YPrimTemp.set(i + nPhases, j + nPhases, value);
							YPrimTemp.setSym(i, j + nPhases, value.negate());
						}

					ZMatrix = null;
				}
			}
			break;
		}

		// set YPrim_Series based on diagonals of YPrim_Shunt so that calcVoltages doesn't fail
		if (isShunt()) {
			if (nPhases == 1 && !DSS.activeCircuit.isPositiveSequence()) {  // assume a neutral or grounding reactor; leave diagonal in the circuit
				for (i = 0; i < YOrder; i++)
					YPrimSeries.set(i, i, YPrimShunt.get(i, i));
			} else {
				for (i = 0; i < YOrder; i++)
					YPrimSeries.set(i, i, YPrimShunt.get(i, i).multiply(1.0e-10));
			}
		}

		YPrim.copyFrom(YPrimTemp);

		calcYPrim();

		setYPrimInvalid(false);
	}

	@Override
	public void dumpProperties(OutputStream out, boolean complete) {
		int i, j, k;

		super.dumpProperties(out, complete);

		PrintWriter pw = new PrintWriter(out);

		for (k = 0; k < parentClass.getNumProperties(); k++) {
			switch (k) {
			case 6:
				if (RMatrix != null) {
					pw.print(parentClass.getPropertyName(k) + "= (");
					for (i = 0; i < nPhases; i++) {
						for (j = 0; j < i; j++)
							pw.printf("%-.5g", RMatrix[i * nPhases + j] + " ");
						if (i != nPhases - 1)
							pw.print("|");
					}
					pw.println(")");
				}
				break;
			case 7:
				if (XMatrix != null) {
					pw.print(parentClass.getPropertyName(k) + "= (");
					for (i = 0; i < nPhases; i++) {
						for (j = 0; j < i; j++)
							pw.printf("%-.5g", XMatrix[i * nPhases + j] + " ");
						if (i != nPhases - 1)
							pw.print("|");
					}
					pw.println(")");
				} else {
					pw.println("~ " + parentClass.getPropertyName(k) + "=" + getPropertyValue(k));
				}
				break;
			}
		}

		pw.close();
	}

	@Override
	public void getLosses(Complex[] totalLosses, Complex[] loadLosses, Complex[] noLoadLosses) {
		SolutionObj sol;
		Complex tot, noload, load;

		/* Only report no load losses if Rp defined and reactor is a shunt device;
		 * else do default behavior.
		 */

		if (RpSpecified && isShunt() && Rp != 0.0) {

			tot = getLosses();  // side effect: computes iTerminal and vTerminal
			/* Compute losses in Rp branch from voltages across shunt element -- node to ground */
			noload = Complex.ZERO;
			sol = DSS.activeCircuit.getSolution();
			for (int i = 0; i < nPhases; i++) {
				Complex V = sol.getNodeV(nodeRef[i]);
				noload = noload.add(new Complex((Math.pow(V.getReal(), 2) + Math.pow(V.getImaginary(), 2)) / Rp, 0.0));  // V^2/Rp
			}

			if (DSS.activeCircuit.isPositiveSequence())
				noload = noload.multiply(3.0);
			load = tot.subtract(noload);  // subtract no load losses from total losses

			/* handle pass by reference */
			totalLosses[0] = tot;
			loadLosses[0] = load;
			noLoadLosses[0] = noload;
		} else {
			/* do the default CktElement behaviour */
			super.getLosses(totalLosses, loadLosses, noLoadLosses);
		}
	}

	@Override
	public void initPropertyValues(int arrayOffset) {

		setPropertyValue(0, getBus(0));
		setPropertyValue(1, getBus(1));
		setPropertyValue(2, "3");
		setPropertyValue(3, "1200");
		setPropertyValue(4, "12.47");
		setPropertyValue(5, "wye");
		setPropertyValue(6, "");
		setPropertyValue(7, "");
		setPropertyValue(8, "NO");  // parallel
		setPropertyValue(9, "0");  // r series
		setPropertyValue(10, String.format("%-.6g", X));  // X
		setPropertyValue(11, "0");  // Rp

		super.initPropertyValues(Reactor.NumPropsThisClass - 1);

		// override inherited properties
		setPropertyValue(Reactor.NumPropsThisClass + 0, String.valueOf(getNormAmps()));
		setPropertyValue(Reactor.NumPropsThisClass + 1, String.valueOf(getEmergAmps()));
		setPropertyValue(Reactor.NumPropsThisClass + 2, String.valueOf(getFaultRate()));
		setPropertyValue(Reactor.NumPropsThisClass + 3, String.valueOf(getPctPerm()));
		setPropertyValue(Reactor.NumPropsThisClass + 4, String.valueOf(getHrsToRepair()));

		clearPropSeqArray();
	}

	@Override
	public void makePosSequence() {
		String s = "";
		double kVArPerPhase, phaseKV, Rs, Rm;
		int i, j;

		if (nPhases > 1) {
			switch (specType) {
			case 1:  // kVAr
				kVArPerPhase = kVArRating / nPhases;
				if (nPhases > 1 || connection != 0) {
					phaseKV = kVRating / DSS.SQRT3;
				} else {
					phaseKV = kVRating;
				}

				s = "phases=1 " + String.format(" kV=%-.5g kvar=%-.5g", phaseKV, kVArPerPhase);
				/* Leave r as specified */
				break;
			case 2:  // r + jx
				s = "phases=1 ";
				break;
			case 3:  // matrices
				s = "phases=1 ";
				// r1
				Rs = 0.0;   // avg self
				for (i = 0; i < nPhases; i++)
					Rs = Rs + RMatrix[i * nPhases + i];
				Rs = Rs / nPhases;
				Rm = 0.0;   // avg mutual
				for (i = 1; i < nPhases; i++)
					for (j = i; j < nPhases; j++)
						Rm = Rm + RMatrix[i * nPhases + j];
				Rm = Rm / (nPhases * (nPhases - 1.0) / 2.0);

				s = s + String.format(" R=%-.5g", (Rs - Rm));

				// x1
				Rs = 0.0;   // avg self
				for (i = 0; i < nPhases; i++)
					Rs = Rs + XMatrix[i * nPhases + i];
				Rs = Rs / nPhases;
				Rm = 0.0;   // avg mutual
				for (i = 1; i < nPhases; i++)
					for (j = i; j < nPhases; j++)
						Rm = Rm + XMatrix[i * nPhases + j];
				Rm = Rm / (nPhases * (nPhases - 1.0) / 2.0);

				s = s + String.format(" X=%-.5g", (Rs - Rm));
				break;
			}

			Parser.getInstance().setCmdString(s);
			edit();
		}

		super.makePosSequence();
	}

	@Override
	public void getInjCurrents(Complex[] curr) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int injCurrents() {
		throw new UnsupportedOperationException();
	}

	public double getR() {
		return R;
	}

	public void setR(double r) {
		R = r;
	}

	public double getRp() {
		return Rp;
	}

	public void setRp(double rp) {
		Rp = rp;
	}

	public double getGp() {
		return Gp;
	}

	public void setGp(double gp) {
		Gp = gp;
	}

	public double getX() {
		return X;
	}

	public void setX(double x) {
		X = x;
	}

	public double getKVArRating() {
		return kVArRating;
	}

	public void setKVArRating(double kvarrating) {
		this.kVArRating = kvarrating;
	}

	public double getKVRating() {
		return kVRating;
	}

	public void setKVRating(double kvrating) {
		this.kVRating = kvrating;
	}

	public double[] getRMatrix() {
		return RMatrix;
	}

	public void setRMatrix(double[] rmatrix) {
		RMatrix = rmatrix;
	}

	public double[] getGMatrix() {
		return GMatrix;
	}

	public void setGMatrix(double[] gmatrix) {
		GMatrix = gmatrix;
	}

	public double[] getXMatrix() {
		return XMatrix;
	}

	public void setXMatrix(double[] xmatrix) {
		XMatrix = xmatrix;
	}

	public double[] getBMatrix() {
		return BMatrix;
	}

	public void setBMatrix(double[] bmatrix) {
		BMatrix = bmatrix;
	}

	public int getConnection() {
		return connection;
	}

	public void setConnection(int conn) {
		connection = conn;
	}

	public int getSpecType() {
		return specType;
	}

	public void setSpecType(int type) {
		specType = type;
	}

	public boolean isParallel() {
		return isParallel;
	}

	public void setParallel(boolean parallel) {
		isParallel = parallel;
	}

	public boolean isRpSpecified() {
		return RpSpecified;
	}

	public void setRpSpecified(boolean specified) {
		RpSpecified = specified;
	}

}
