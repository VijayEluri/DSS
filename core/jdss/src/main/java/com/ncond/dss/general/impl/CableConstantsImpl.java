package com.ncond.dss.general.impl;

import com.ncond.dss.general.CableConstants;
import com.ncond.dss.shared.CMatrix;
import com.ncond.dss.shared.impl.CMatrixImpl;
import com.ncond.dss.shared.impl.LineUnits;
import com.ncond.dss.shared.impl.MathUtil;

public class CableConstantsImpl extends LineConstantsImpl implements CableConstants {

	protected double[] epsR;
	protected double[] insLayer;
	protected double[] diaIns;
	protected double[] diaCable;

	public CableConstantsImpl(int numConductors) {
		super(numConductors);
		epsR = new double[getNumConds()];
		insLayer = new double[getNumConds()];
		diaIns = new double[getNumConds()];
		diaCable = new double[getNumConds()];
	}

	/**
	 * Don't reduce Y, it has zero neutral capacitance.
	 */
	@Override
	public void kron(int norder) {
		CMatrix ZTemp;
		int i, j;

		ZTemp = ZMatrix;
		if (frequency >= 0.0 && norder > 0 && norder < getNumConds()) {
			while (ZTemp.order() > norder) {
				ZReduced = ZTemp.kron(ZTemp.order());  // eliminate last row
				ZTemp = ZReduced;
			}
			// now copy part of YcMatrix to YcReduced
			YcReduced = new CMatrixImpl(norder);
			for (i = 0; i < norder; i++)
				for (j = 0; j < norder; j++)
					YcReduced.set(i, j, YcMatrix.get(i, j));
		}
	}

	@Override
	public boolean conductorsInSameSpace(StringBuffer errorMessage) {
		int i, j;
		double Dij;
		double Ri, Rj;

		boolean result = false;

		for (i = 0; i < getNumConds(); i++)
			if (Y[i] >= 0.0) {
				result = true;
				errorMessage.append(String.format("Cable %d height must be < 0. ", i));
				return result;
			}

		for (i = 0; i < getNumConds(); i++) {
			if (i <= getNPhases()) {
				Ri = radius[i];
			} else {
				Ri = 0.5 * diaCable[i];
			}
			for (j = i + 1; j < getNumConds(); j++) {
				if (j < getNPhases()) {
					Rj = radius[j];
				} else {
					Rj = 0.5 * diaCable[j];
				}
				Dij = Math.sqrt( MathUtil.sqr(X[i] - X[j]) + MathUtil.sqr(Y[i] - Y[j]) );
				if (Dij < (Ri + Rj)) {
					result = true;
					errorMessage.append(String.format("Cable conductors %d and %d occupy the same space.", i, j));
					return result;
				}
			}
		}
		return result;
	}

	public double getEpsR(int i) {
		return epsR[i];
	}

	public void setEpsR(int i, double epsr) {
		epsR[i] = epsr;
	}

	public double getInsLayer(int i, int units) {
		return insLayer[i] * LineUnits.fromMeters(units);
	}

	public void setInsLayer(int i, int units, double inslayer) {
		if (i >= 0 && i < getNumConds())
			insLayer[i] = inslayer * LineUnits.toMeters(units);
	}

	public double getDiaIns(int i, int units) {
		return diaIns[i] * LineUnits.fromMeters(units);
	}

	public void setDiaIns(int i, int units, double diains) {
		if (i >= 0 && i < getNumConds())
			diaIns[i] = diains * LineUnits.toMeters(units);
	}

	public double getDiaCable(int i, int units) {
		return diaCable[i] * LineUnits.fromMeters(units);
	}

	public void setDiaCable(int i, int units, double diacable) {
		if (i >= 0 && i < getNumConds())
			diaCable[i] = diacable * LineUnits.toMeters(units);
	}

}
