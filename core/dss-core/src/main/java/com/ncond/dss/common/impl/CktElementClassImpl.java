package com.ncond.dss.common.impl;

import com.ncond.dss.common.CktElement;
import com.ncond.dss.common.CktElementClass;
import com.ncond.dss.parser.impl.Parser;

public class CktElementClassImpl extends DSSClassImpl implements
		CktElementClass {

	private int numCktElemClassProps;

	public CktElementClassImpl() {
		super();
		numCktElemClassProps = 2;
	}

	protected int classEdit(Object activeCktElemObj, int paramPointer) {
		// continue parsing with contents of parser
		if (paramPointer >= 0) {
			CktElement cktElem = (CktElement) activeCktElemObj;
			switch (paramPointer) {
			case 0:
				cktElem.setBaseFrequency(Parser.getInstance().makeDouble());
				break;
			case 1:
				cktElem.setEnabled(Utilities.interpretYesNo(Parser.getInstance().makeString()));
				break;
			default:
				super.classEdit(activeCktElemObj, paramPointer - numCktElemClassProps);
				break;
			}
		}
		return 0;
	}

	protected void classMakeLike(Object otherObj) {
		CktElement otherCktObj = (CktElement) otherObj;

		CktElement cktElem = (CktElement) DSSGlobals.activeDSSObject;
		cktElem.setBaseFrequency(otherCktObj.getBaseFrequency());
		cktElem.setEnabled(true);
	}

	/**
	 * Add no. of intrinsic properties.
	 */
	protected void countProperties() {
		numProperties = numProperties + numCktElemClassProps;
		super.countProperties();
	}

	/**
	 * Define the properties for the base power delivery element class.
	 */
	protected void defineProperties() {
		propertyName[activeProperty + 1] = "basefreq";
		propertyName[activeProperty + 2] = "enabled";

		propertyHelp[activeProperty + 1] = "Base frequency for ratings.";
		propertyHelp[activeProperty + 2] = "{Yes|No or True|False} Indicates whether this element is enabled.";

		activeProperty = activeProperty + numCktElemClassProps;

		super.defineProperties();
	}

	public int getNumCktElemClassProps() {
		return numCktElemClassProps;
	}

}
