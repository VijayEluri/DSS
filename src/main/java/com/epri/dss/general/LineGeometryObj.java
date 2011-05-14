package com.epri.dss.general;

import com.epri.dss.general.impl.ConductorChoice;
import com.epri.dss.general.impl.LineGeometryObjImpl.LineGeometryProblem;
import com.epri.dss.shared.CMatrix;

/**
 * The LineGeometry object is a general DSS object used by all circuits
 * as a reference for obtaining line impedances.
 *
 * The values are set by the normal New and Edit procedures for any DSS object.
 *
 * The values are retrieved by setting the Code Property in the LineGeometry
 * Class. This sets the active LineGeometry object to be the one referenced by
 * the Code Property;
 *
 * Then the values of that code can be retrieved via the public variables.
 *
 */
public interface LineGeometryObj extends DSSObject {

	void changeLineConstantsType(ConductorChoice newPhaseChoice);

	void setNconds(int Value);

	void setNphases(int Value);

	int getNphases();

	void setActiveCond(int Value);

	int getActiveCond();

	CMatrix getYCmatrix(double f, double Length, int Units);

	CMatrix getZmatrix(double f, double Length, int Units);

	double getRhoEarth();

	void setRhoEarth(double Value);

	int getNconds();

	double getXcoord(int i);

	double getYcoord(int i);

	String getWireName(int i);

	WireDataObj getWireData (int i);

	int getNWires();

	/**
	 * called from a Line object that has its own Spacing and Wires input
	 * automatically sets reduce=y if the spacing has more wires than phases
	 */
	void LoadSpacingAndWires(LineSpacingObj Spc, WireDataObj[] Wires);

	double getNormAmps();

	void setNormAmps(double normAmps);

	double getEmergAmps();

	void setEmergAmps(double emergAmps);

	ConductorChoice getPhaseChoice();

	// FIXME Private method in OpenDSS
	void updateLineGeometryData(double f) throws LineGeometryProblem;


	// FIXME Private members in OpenDSS.

	String[] getCondName();

	void setCondName(String[] condName);

	ConductorDataObj[] getWireData();

	void setWireData(ConductorDataObj[] wireData);

	double[] getX();

	void setX(double[] x);

	double[] getY();

	void setY(double[] y);

	int[] getUnits();

	void setUnits(int[] units);

	int getLastUnit();

	void setLastUnit(int lastUnit);

	boolean isDataChanged();

	void setDataChanged(boolean dataChanged);

	boolean isReduce();

	void setReduce(boolean reduce);

	String getSpacingType();

	void setSpacingType(String spacingType);

	LineConstants getLineData();

	void setLineData(LineConstants lineData);

	void setPhaseChoice(ConductorChoice phaseChoice);

}
