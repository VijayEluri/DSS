package com.ncond.dss.general.impl;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

import com.ncond.dss.common.DSSClass;
import com.ncond.dss.general.LineSpacing;
import com.ncond.dss.general.LineSpacingObj;
import com.ncond.dss.shared.impl.LineUnits;

public class LineSpacingObjImpl extends DSSObjectImpl implements LineSpacingObj {

	private int nConds;
	private int nPhases;
	private double[] X;
	private double[] Y;
	private int units;
	private boolean dataChanged;

	public LineSpacingObjImpl(DSSClass parClass, String lineSpacingName) {
		super(parClass);

		setName(lineSpacingName.toLowerCase());
		objType = parClass.getDSSClassType();

		dataChanged = true;
		X           = null;
		Y           = null;
		units       = LineUnits.UNITS_FT;

		setNWires(3);  // allocates terminals
		nPhases = 3;

		initPropertyValues(0);
	}

	@Override
	public void dumpProperties(OutputStream out, boolean complete) {
		super.dumpProperties(out, complete);

		PrintWriter pw = new PrintWriter(out);

		for (int i = 0; i < 5; i++)
			pw.println("~ " + parentClass.getPropertyName(i) + "=" + getPropertyValue(i));

		pw.close();
	}

	private String arrayString(double[] pf, int n) {
		// FIXME use StringBuffer
		String r = "[";
		if (n > 0)
			r = r + String.format("%-g", pf[0]);
		for (int i = 1; i < n; i++)
			r = r + String.format(",%-g", pf[i]);
		return r + "]";
	}

	@Override
	public String getPropertyValue(int index) {
		switch (index) {
		case 2:
			return arrayString(X, nConds);
		case 3:
			return arrayString(Y, nConds);
		case 4:
			LineUnits.lineUnitsStr(units);
		default:
			// inherited parameters
			return super.getPropertyValue(index);
		}
	}

	public double getXCoord(int i) {
		return i < nConds ? X[i] : 0.0;
	}

	public double getYCoord(int i) {
		return i < nConds ? Y[i] : 0.0;
	}

	@Override
	public void initPropertyValues(int arrayOffset) {
		setPropertyValue(0, "3");
		setPropertyValue(1, "3");
		setPropertyValue(2, "0");
		setPropertyValue(3, "32");
		setPropertyValue(4, "ft");

		super.initPropertyValues(LineSpacing.NumPropsThisClass - 1);
	}

	public void setNWires(int value) {
		nConds = value;
		X = new double[nConds];
		Y = new double[nConds];
		units = LineUnits.UNITS_FT;
	}

	public int getNWires() {
		return nConds;
	}

	public int getNPhases() {
		return nPhases;
	}

	public int getUnits() {
		return units;
	}

	// FIXME Private members in OpenDSS.

	public int getNConds() {
		return nConds;
	}

	public void setNConds(int num) {
		nConds = num;
	}

	public double[] getX() {
		return X;
	}

	public void setX(double[] x) {
		X = x;
	}

	public double[] getY() {
		return Y;
	}

	public void setY(double[] y) {
		Y = y;
	}

	public boolean isDataChanged() {
		return dataChanged;
	}

	public void setDataChanged(boolean changed) {
		dataChanged = changed;
	}

	public void setNPhases(int num) {
		nPhases = num;
	}

	public void setUnits(int value) {
		units = value;
	}

}
