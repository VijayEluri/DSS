package com.epri.dss.general;

import com.epri.dss.common.DSSClass;

/**
 * The GrowthShape object is a general DSS object used by all circuits
 * as a reference for obtaining yearly growth curves.
 * 
 * The values are set by the normal New and Edit procedures as for any DSS object.
 * 
 * The values are retrieved by setting the Code Property in the GrowthShape Class.
 * This sets the active GrowthShapeObj object to be the one referenced by the Code Property;
 * 
 * Then the values of that code can be retrieved via the public variables.  Or you
 * can pick up the ActiveGrowthShapeObj object and save the direct reference to the object.
 * 
 * Growth shapes are entered as multipliers for the previous year's load.  If the
 * load grows by 2.5% in a year, the multiplier is entered as 1.025.  You do not need
 * to enter subsequent years if the multiplier remains the same.  You need only enter
 * the years in which the growth rate is assumed to have changed.
 * 
 * The user may place the data in CSV or binary files as well as passing through the
 * command interface. The rules are the same as for LoadShapes except that the year
 * is always entered.  CSV files are text separated by commas, one interval to a line.
 * There are two binary formats permitted: 1) a file of Singles; 2) a file of Doubles.
 * 
 * (Year, multiplier) pairs are expected in all formats.  Through the COM interface,
 * supply separate arrays of Year and Mult.
 * 
 * Edit growthshape.allisonsub npts=5
 * ~   year="1999 2000 2001 2005 2010"
 * ~   mult="1.10 1.07 1.05 1.025 1.01"
 * 
 * This example describes a growth curve that start off relatively fast (10%) and after
 * 10 years tapers off to 1%.
 *
 */
public interface GrowthShape extends DSSClass {
	
	public static int NumPropsThisClass = 6;

	/* Returns active GrowthShape string */
	String getCode();

	/* Sets the active GrowthShape */
	void setCode(String Value);

	int edit();

	int init(int Handle);

	int newObject(String ObjName);

}
