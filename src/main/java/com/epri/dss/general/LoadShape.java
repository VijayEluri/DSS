package com.epri.dss.general;

import com.epri.dss.common.DSSClass;

public interface LoadShape extends DSSClass {

	static final int NumPropsThisClass = 16;

	/**
	 * Returns active LoadShape string.
	 */
	String getCode();

	/**
	 * Sets the active LoadShape.
	 */
	void setCode(String Value);

	int edit();

	int init(int Handle);

	int newObject(String ObjName);

	/**
	 * Find an obj of this class by name.
	 */
	Object find(String ObjName);

	void TOPExport(String ObjName);

}
