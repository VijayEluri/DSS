/**
 * Copyright (C) 2008-2012 Electric Power Research Institute, Inc.
 * Copyright (C) 2009-2012 Richard Lincoln
 * All rights reserved.
 */
package com.ncond.dss.common;

import com.ncond.dss.general.DSSObject;
import com.ncond.dss.parser.Parser;

abstract public class CktElementClass extends DSSClass {

	/*
	 * To add a property,
	 *   1) add a property constant to this list
	 *   2) add a handler to the case statement in the edit function
	 *   3) add a statement(s) to initPropertyValues function to initialize the string value
	 *   4) add any special handlers to dumpProperties and getPropertyValue, if needed
	 */

	private int numCktElemClassProps;

	public CktElementClass() {
		super();
		numCktElemClassProps = 2;
	}

	@Override
	protected int classEdit(DSSObject activeCktElemObj, int paramPointer) {
		Parser parser = Parser.getInstance();

		// continue parsing with contents of parser
		if (paramPointer >= 0) {
			CktElement cktElem = (CktElement) activeCktElemObj;
			switch (paramPointer) {
			case 0:
				cktElem.setBaseFrequency(parser.doubleValue());
				break;
			case 1:
				cktElem.setEnabled(Util.interpretYesNo(parser.stringValue()));
				break;
			default:
				super.classEdit(activeCktElemObj, paramPointer - numCktElemClassProps);
				break;
			}
		}
		return 0;
	}

	protected void classMakeLike(DSSObject otherObj) {
		CktElement otherCktObj = (CktElement) otherObj;

		CktElement cktElem = (CktElement) DSS.activeDSSObject;
		cktElem.setBaseFrequency(otherCktObj.getBaseFrequency());
		cktElem.setEnabled(true);
	}

	/**
	 * Add no. of intrinsic properties.
	 */
	@Override
	protected void countProperties() {
		numProperties = numProperties + numCktElemClassProps;
		super.countProperties();
	}

	/**
	 * Define the properties for the base power delivery element class.
	 */
	@Override
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
