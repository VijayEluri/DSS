package com.ncond.dss.general;

import com.ncond.dss.common.DSSClass;

/* Superstructure for all Spectrum objects */
public interface Spectrum extends DSSClass {

	static final int NumPropsThisClass = 5;

	/**
	 * Returns active spectrum code string.
	 */
	String getCode();

	/**
	 * Sets the active spectrum.
	 */
	void setCode(String value);

	int edit();

	int newObject(String objName);

}
