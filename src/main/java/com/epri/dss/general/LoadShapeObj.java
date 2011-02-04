package com.epri.dss.general;

import java.io.PrintStream;

import com.epri.dss.shared.impl.Complex;

/*
 * The LoadShape object is a general DSS object used by all circuits
 * as a reference for obtaining yearly, daily, and other load shapes.
 *
 * The values are set by the normal New and Edit procedures for any DSS object.
 *
 * The values are retrieved by setting the Code Property in the LoadShape
 * Class. This sets the active LoadShape object to be the one referenced by the
 * Code Property;
 *
 * Then the values of that code can be retrieved via the public variables.  Or
 * you can pick up the ActiveLoadShapeObj object and save the direct reference
 * to the object.
 *
 * Loadshapes default to fixed interval data.  If the Interval is specified to
 * be 0.0, then both time and multiplier data are expected.  If the Interval is
 * greater than 0.0, the user specifies only the multipliers.  The Hour command
 * is ignored and the files are assumed to contain only the multiplier data.
 *
 * The user may place the data in CSV or binary files as well as passing
 * through the command interface. Obviously, for large amounts of data such as
 * 8760 load curves, the command interface is cumbersome.  CSV files are text
 * separated by commas, one interval to a line. There are two binary formats
 * permitted: 1) a file of Singles; 2) a file of Doubles.
 *
 * For fixed interval data, only the multiplier is expected.  Therefore, the
 * CSV format would contain only one number per line.  The two binary formats
 * are packed.
 *
 * For variable interval data, (hour, multiplier) pairs are expected in both
 * formats.
 *
 * The Mean and Std Deviation are automatically computed when a new series of
 * points is entered.
 *
 * The data may also be entered in unnormalized form.  The normalize=Yes
 * command will force normalization.  That is, the multipliers are scaled so
 * that the maximum value is 1.0.
 *
 */
public interface LoadShapeObj extends DSSObject {

	double getInterval();

	void setNumPoints(int Value);

	int getNumPoints();

	/* Get multiplier at specified time */
	Complex getMult(double hr);

	/* get multiplier by index */
	double mult(int i);

	/* get hour corresponding to point index */
	double hour(int i);

	/* Normalize the curve presently in memory */
	void normalize();

	void calcMeanandStdDev();

	String getPropertyValue(int Index);

	void initPropertyValues(int ArrayOffset);

	void dumpProperties(PrintStream F, boolean Complete);

}
