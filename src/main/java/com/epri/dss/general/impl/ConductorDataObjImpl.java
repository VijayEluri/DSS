package com.epri.dss.general.impl;

import java.io.PrintStream;

import com.epri.dss.common.DSSClass;
import com.epri.dss.general.ConductorDataObj;
import com.epri.dss.shared.impl.LineUnits;

public class ConductorDataObjImpl extends DSSObjectImpl implements ConductorDataObj {

	private double RDC;
	private double R60;
	private double GMR60;
	private double radius;
	private int GMRUnits;
	private int ResistanceUnits;
	private int RadiusUnits;

	protected double NormAmps;
	protected double EmergAmps;

	public ConductorDataObjImpl(DSSClass ParClass, String ConductorDataName) {
		super(ParClass);

		setName(ConductorDataName.toLowerCase());
		this.DSSObjType = ParClass.getDSSClassType();

		this.RDC             = -1.0;
		this.R60             = -1.0;
		this.GMR60           = -1.0;
		this.radius          = -1.0;
		this.GMRUnits        = 0;
		this.ResistanceUnits = 0;
		this.RadiusUnits     = 0;

		this.NormAmps  = -1.0;
		this.EmergAmps = -1.0;
	}

	@Override
	public void dumpProperties(PrintStream F, boolean Complete) {
		super.dumpProperties(F, Complete);

		for (int i = 0; i < getParentClass().getNumProperties(); i++) {
			F.print("~ " + getParentClass().getPropertyName()[i] + "=");
			switch (i) {
			case 0:
				F.println(String.format("%.6g", getRDC()));
				break;
			case 1:
				F.println(String.format("%.6g", getR60()));
				break;
			case 2:
				F.println(String.format("%s", LineUnits.lineUnitsStr(getResistanceUnits())));
				break;
			case 3:
				F.println(String.format("%.6g", getGMR60()));
				break;
			case 4:
				F.println(String.format("%s", LineUnits.lineUnitsStr(getGMRUnits())));
				break;
			case 5:
				F.println(String.format("%.6g", getRadius()));
				break;
			case 6:
				F.println(String.format("%s", LineUnits.lineUnitsStr(getRadiusUnits())));
				break;
			case 7:
				F.println(String.format("%.6g", getNormAmps()));
				break;
			case 8:
				F.println(String.format("%.6g", getEmergAmps()));
				break;
			case 9:
				F.println(String.format("%.6g", getRadius() * 2.0));
				break;
			}
		}
	}

	@Override
	public void initPropertyValues(int ArrayOffset) {
		setPropertyValue(ArrayOffset + 0, "-1");
		setPropertyValue(ArrayOffset + 1, "-1");
		setPropertyValue(ArrayOffset + 2, "none");
		setPropertyValue(ArrayOffset + 3, "-1");
		setPropertyValue(ArrayOffset + 4, "none");
		setPropertyValue(ArrayOffset + 5, "-1");
		setPropertyValue(ArrayOffset + 6, "none");
		setPropertyValue(ArrayOffset + 7, "-1");
		setPropertyValue(ArrayOffset + 8, "-1");
		setPropertyValue(ArrayOffset + 9, "-1");
		super.initPropertyValues(ArrayOffset + 10);
	}

	public double getNormAmps() {
		return NormAmps;
	}

	public void setNormAmps(double normAmps) {
		NormAmps = normAmps;
	}

	public double getEmergAmps() {
		return EmergAmps;
	}

	public void setEmergAmps(double emergAmps) {
		EmergAmps = emergAmps;
	}

	public double getRDC() {
		return RDC;
	}

	public double getR60() {
		return R60;
	}

	public double getGMR60() {
		return GMR60;
	}

	public double getRadius() {
		return radius;
	}

	public int getGMRUnits() {
		return GMRUnits;
	}

	public int getResistanceUnits() {
		return ResistanceUnits;
	}

	public int getRadiusUnits() {
		return RadiusUnits;
	}

	// FIXME Private members in OpenDSS.

	public void setRDC(double rDC) {
		RDC = rDC;
	}

	public void setR60(double r60) {
		R60 = r60;
	}

	public void setGMR60(double gMR60) {
		GMR60 = gMR60;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public void setGMRUnits(int gMRUnits) {
		GMRUnits = gMRUnits;
	}

	public void setResistanceUnits(int resistanceUnits) {
		ResistanceUnits = resistanceUnits;
	}

	public void setRadiusUnits(int radiusUnits) {
		RadiusUnits = radiusUnits;
	}

}
