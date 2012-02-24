package com.ncond.dss.general.impl;

import java.io.PrintStream;

import org.apache.commons.math.complex.Complex;

import com.ncond.dss.common.DSSClass;
import com.ncond.dss.common.impl.Utilities;
import com.ncond.dss.general.LoadShape;
import com.ncond.dss.general.LoadShapeObj;
import com.ncond.dss.shared.impl.MathUtil;

public class LoadShapeObjImpl extends DSSObjectImpl implements LoadShapeObj {

	private int lastValueAccessed;
	/** Number of points in curve */
	private int numPoints;
	private int arrayPropertyIndex;

	/** =0.0 then random interval (hr) */
	protected double interval;
	/** Time values (hr) if Interval > 0.0 */
	protected double[] hours;
	protected double[] PMultipliers, QMultipliers;

	protected double maxP, maxQ, baseP, baseQ;

	protected boolean useActual;

	protected int iMaxP;

	protected boolean stdDevCalculated;
	protected double[] mean = new double[1];
	protected double[] stdDev = new double[1];

	public LoadShapeObjImpl(DSSClass parClass, String loadShapeName) {
		super(parClass);
		setName(loadShapeName.toLowerCase());
		objType = parClass.getDSSClassType();

		lastValueAccessed = 0;

		numPoints    = 0;
		interval     = 1.0;  // hr
		hours        = null;
		PMultipliers = null;
		QMultipliers = null;
		maxP         = 1.0;
		maxQ         = 0.0;
		baseP        = 0.0;
		baseQ        = 0.0;
		useActual    = false;
		stdDevCalculated = false;  // calculate on demand

		arrayPropertyIndex = 0;

		initPropertyValues(0);
	}

	/** Set imaginary part of result when QMultipliers not defined. */
	private double setResultIm(double realpart) {
		// if actual, assume zero, same as real otherwise
		return useActual ? 0.0 : realpart;
	}

	/**
	 * Get multiplier at specified time.
	 *
	 * This function returns a multiplier for the given hour.
	 * If no points exist in the curve, the result is 1.0.
	 * If there are fewer points than requested, the curve is simply assumed to repeat
	 * Thus a daily load curve can suffice for a yearly load curve:  You just get the
	 * same day over and over again.
	 * The value returned is the nearest to the interval requested.  Thus if you request
	 * hour=12.25 and the interval is 1.0, you will get interval 12.
	 */
	public Complex getMult(double hr) {
		int index;

		//Complex Result = new Complex(1.0, 1.0);  // default return value if no points in curve
		double[] result = new double[] {1.0, 1.0};

		if (numPoints > 0) {  // handle exceptional cases
			if (numPoints == 1) {
				result[0] = PMultipliers[0];
				if (QMultipliers != null) {
					result[1] = QMultipliers[0];
				} else {
					result[1] = setResultIm(result[0]);
				}
			} else {
				if (interval > 0.0) {
					index = (int) Math.round(hr / interval);
					if (index > numPoints)
						index = index % numPoints;  // wrap around using remainder
					if (index == 0)
						index = numPoints;
					result[0] = PMultipliers[index];
					if (QMultipliers != null) {
						result[1] = QMultipliers[index];
					} else {
						result[1] = setResultIm(result[0]);
					}
				} else {
					// for random interval

					/* Start with previous value accessed under the assumption that most
					 * of the time, this function will be called sequentially
					 */

					/* Normalize Hr to max hour in curve to get wraparound */
					if (hr > hours[numPoints])
						hr = hr - (hr / hours[numPoints]) * hours[numPoints];

					if (hours[lastValueAccessed] > hr)
						lastValueAccessed = 1;  // start over from beginning
					for (int i = lastValueAccessed + 1; i < numPoints; i++) {
						if (Math.abs(hours[i] - hr) < 0.00001) {  // if close to an actual point, just use it.
							result[0] = PMultipliers[i];
							if (QMultipliers != null) {
								result[1] = QMultipliers[i];
							} else {
								result[1] = setResultIm(result[0]);
							}
							lastValueAccessed = i;
							return new Complex(result[0], result[1]);
						} else if (hours[i] > hr) {  // interpolate for multiplier
							lastValueAccessed = i - 1;
							result[0] = PMultipliers[lastValueAccessed] +
								(hr - hours[lastValueAccessed]) / (hours[i] - hours[lastValueAccessed]) *
									(PMultipliers[i] -PMultipliers[lastValueAccessed]);
							if (QMultipliers != null) {
								result[1] = QMultipliers[lastValueAccessed] +
									(hr - hours[lastValueAccessed]) / (hours[i] - hours[lastValueAccessed]) *
									(QMultipliers[i] -QMultipliers[lastValueAccessed]);
							} else {
								result[1] = setResultIm(result[0]);
							}
							return new Complex(result[0], result[1]);
						}
					}

					// if we fall through the loop, just use last value
					lastValueAccessed = numPoints - 1;
					result[0] = PMultipliers[numPoints];
					if (QMultipliers != null) {
						result[1] = QMultipliers[numPoints];
					} else {
						result[1] = setResultIm(result[0]);
					}
				}
			}
		}

		return new Complex(result[0], result[1]);
	}

	private double maxMult;

	private void doNormalize(double[] multipliers) {
		int i;
		if (numPoints > 0) {
			if (maxMult <= 0.0) {
				maxMult = Math.abs(multipliers[0]);
				for (i = 1; i < numPoints; i++)
					maxMult = Math.max(maxMult, Math.abs(multipliers[i]));
			}

			if (maxMult == 0.0)
				maxMult = 1.0;  // avoid divide by zero
			for (i = 0; i < numPoints; i++)
				multipliers[i] = multipliers[i] / maxMult;
		}
	}

	/**
	 * Normalize the curve presently in memory.
	 */
	public void normalize() {
		doNormalize(PMultipliers);
		if (QMultipliers != null) {
			maxMult = baseQ;
			doNormalize(QMultipliers);
		}
		useActual = false;  // not likely that you would want to use the actual if you normalized it
	}

	public void calcMeanAndStdDev() {
		if (numPoints > 0) {
			if (interval > 0.0) {
				MathUtil.meanandStdDev(PMultipliers, numPoints, mean, stdDev);
			} else {
				MathUtil.curveMeanAndStdDev(PMultipliers, hours, numPoints, mean, stdDev);
			}
		}

		setPropertyValue(4, String.format("%.8g", mean[0]));
		setPropertyValue(5, String.format("%.8g", stdDev[0]));

		stdDevCalculated = true;
		/* No action is taken on Q multipliers */
	}

	public double getInterval() {
		if (interval > 0.0) {
			return interval;
		} else {
			if (lastValueAccessed > 0) {
				return hours[lastValueAccessed] - hours[lastValueAccessed - 1];
			} else {
				return 0.0;
			}
		}
	}

	public double getMean() {
		if (!stdDevCalculated)
			calcMeanAndStdDev();
		return mean[0];
	}

	public double getStdDev() {
		if (!stdDevCalculated)
			calcMeanAndStdDev();
		return stdDev[0];
	}

	/**
	 * Get multiplier by index.
	 */
	public double mult(int i) {
		if (i < numPoints && i >= 0) {
			lastValueAccessed = i;
			return PMultipliers[i];
		} else {
			return 0.0;
		}
	}

	/**
	 * Get hour corresponding to point index.
	 */
	public double hour(int i) {
		if (interval == 0) {
			if (i < numPoints & i >= 0) {
				lastValueAccessed = i;
				return hours[i];
			} else {
				return 0.0;
			}
		} else {
			lastValueAccessed = i;
			return hours[i] * interval;
		}
	}

	public void dumpProperties(PrintStream f, boolean complete) {
		super.dumpProperties(f, complete);

		for (int i = 0; i < parentClass.getNumProperties(); i++) {
			switch (i) {
			case 2:
				f.println("~ " + parentClass.getPropertyName()[i] + "=(" + propertyValue[i] + ")");
				break;
			case 3:
				f.println("~ " + parentClass.getPropertyName()[i] + "=(" + propertyValue[i] + ")");
				break;
			default:
				f.println("~ " + parentClass.getPropertyName()[i] + "=" + propertyValue[i]);
				break;
			}
		}
	}

	public String getPropertyValue(int index) {
		String result;

		switch (index) {
		case 2:
			result = "(";
			break;
		case 3:
			result = "(";
			break;
		default:
			result = "";
			break;
		}

		switch (index) {
		case 1:
			result = String.format("%.8g", interval);
			break;
		case 2:
			for (int i = 0; i < numPoints; i++)
				result = result + String.format("%-g, ", PMultipliers[i]);
			break;
		case 3:
			if (hours != null)
				for (int i = 0; i < numPoints; i++)
					result = result + String.format("%-g, ", hours[i]);
			break;
		case 4:
			result = String.format("%.8g", mean[0]);
			break;
		case 5:
			result = String.format("%.8g", stdDev[0]);
			break;
		case 10:
			if (QMultipliers != null) {
				result = "(";
				for (int i = 0; i < numPoints; i++)
					result = result + String.format("%-g,", QMultipliers[i]);
				result = result + ")";
			}
			break;
		case 11:
			if (useActual) {
				result = "Yes";
			} else {
				result = "No";
			}
			break;
		case 12:
			result = String.format("%.8g", maxP);
			break;
		case 13:
			result = String.format("%.8g", maxQ);
			break;
		case 14:
			result = String.format("%.8g", interval * 3600.0);
			break;
		case 15:
			result = String.format("%.8g", interval * 60.0);
			break;
		case 17:
			result = String.format("%.8g", baseP);
			break;
		case 18:
			result = String.format("%.8g", baseQ);
			break;
		default:
			result = super.getPropertyValue(index);
			break;
		}

		switch (index) {
		case 3:
			result = result + ")";
			break;
		case 4:
			result = result + ")";
			break;
		}

		return result;
	}

	public void initPropertyValues(int arrayOffset) {

		setPropertyValue(0, "0");  // number of points to expect
		setPropertyValue(1, "1");  // default = 1.0 hr;
		setPropertyValue(2, "");   // vector of multiplier values
		setPropertyValue(3, "");   // vector of hour values
		setPropertyValue(4, "0");  // set the mean (otherwise computed)
		setPropertyValue(5, "0");  // set the std dev (otherwise computed)
		setPropertyValue(6, "");   // switch input to a csvfile
		setPropertyValue(7, "");   // switch input to a binary file of singles
		setPropertyValue(8, "");   // switch input to a binary file of singles
		setPropertyValue(9, "");   // action option.
		setPropertyValue(10, "");  // Qmult.
		setPropertyValue(11, "No");
		setPropertyValue(12, "0");
		setPropertyValue(13, "0");
		setPropertyValue(14, "3600");  // seconds
		setPropertyValue(15, "60");    // minutes

		super.initPropertyValues(LoadShape.NumPropsThisClass - 1);
	}

	// TODO Implement TOPExport method

	// FIXME Private method in OpenDSS
	public void saveToDblFile() {
		// FIXME Implement this method
		throw new UnsupportedOperationException();
	}

	// FIXME Private method in OpenDSS
	public void saveToSngFile() {
		// FIXME Implement this method
		throw new UnsupportedOperationException();
	}

	// FIXME Private method in OpenDSS
	public void setMaxPandQ() {
		iMaxP = Utilities.iMaxAbsdblArrayValue(numPoints, PMultipliers);

		if (iMaxP >= 0) {
			maxP = PMultipliers[iMaxP];
			if (QMultipliers != null) {
				maxQ = QMultipliers[iMaxP];
			} else {
				maxQ = 0.0;
			}
		}
	}

	public void setMean(double value) {
		stdDevCalculated = true;
		mean[0] = value;
	}

	public void setNumPoints(int value) {

		setPropertyValue(0, String.valueOf(value));  // update property list variable

		// reset array property values to keep them in proper order in save

		if (arrayPropertyIndex >= 0)
			propertyValue[arrayPropertyIndex] = propertyValue[arrayPropertyIndex];
		if (QMultipliers != null)
			propertyValue[10] = propertyValue[10];

		numPoints = value;  // now assign the value
	}

	public void setStdDev(double stddev) {
		stdDevCalculated = true;
		stdDev[0] = stddev;
	}

	public int getNumPoints() {
		return numPoints;
	}

	public double[] getHours() {
		return hours;
	}

	public void setHours(double[] values) {
		hours = values;
	}

	public double[] getPMultipliers() {
		return PMultipliers;
	}

	public void setPMultipliers(double[] values) {
		PMultipliers = values;
	}

	public double[] getQMultipliers() {
		return QMultipliers;
	}

	public void setQMultipliers(double[] values) {
		QMultipliers = values;
	}

	public void setInterval(double value) {
		interval = value;
	}

	public double getMaxP() {
		return maxP;
	}

	public void setMaxP(double max) {
		maxP = max;
	}

	public double getMaxQ() {
		return maxQ;
	}

	public void setMaxQ(double max) {
		maxQ = max;
	}

	public boolean isUseActual() {
		return useActual;
	}

	public void setUseActual(boolean value) {
		useActual = value;
	}

	// Protected member in OpenDSS.

	public boolean isStdDevCalculated() {
		return stdDevCalculated;
	}

	public void setStdDevCalculated(boolean calculated) {
		stdDevCalculated = calculated;
	}

	// Private member in OpenDSS.
	public void setArrayPropertyIndex(int i) {
		arrayPropertyIndex = i;
	}

	public double getBaseP() {
		return baseP;
	}

	public void setBaseP(double baseP) {
		this.baseP = baseP;
	}

	public double getBaseQ() {
		return baseQ;
	}

	public void setBaseQ(double baseQ) {
		this.baseQ = baseQ;
	}

}
