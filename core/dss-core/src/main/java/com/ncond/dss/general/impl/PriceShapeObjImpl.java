package com.ncond.dss.general.impl;

import java.io.OutputStream;
import java.io.PrintWriter;

import com.ncond.dss.common.DSSClass;
import com.ncond.dss.general.PriceShape;
import com.ncond.dss.general.PriceShapeObj;
import com.ncond.dss.shared.impl.MathUtil;

public class PriceShapeObjImpl extends DSSObjectImpl implements PriceShapeObj {

	private int lastValueAccessed, numPoints;  // number of points in curve
	private int arrayPropertyIndex;

	private boolean stdDevCalculated;
	private double[] mean = new double[1];
	private double[] stdDev = new double[1];

	protected double interval;  // =0.0 then random interval (hr)
	protected double[] hours;   // time values (hr) if interval > 0.0 else nil
	protected double[] priceValues;  // prices

	public PriceShapeObjImpl(DSSClass parClass, String priceShapeName) {
		super(parClass);
		setName(priceShapeName.toLowerCase());
		objType = parClass.getDSSClassType();

		lastValueAccessed = 0;

		numPoints   = 0;
		interval    = 1.0;  // hr
		hours       = null;
		priceValues = null;
		stdDevCalculated = false;  // calculate on demand

		arrayPropertyIndex = -1;

		initPropertyValues(0);
	}

	/**
	 * Get prices at specified time, hr.
	 *
	 * This method returns the Price for the given hour.
	 * If no points exist in the curve, the result is 0.0
	 * If there are fewer points than requested, the curve is simply assumed to repeat
	 * Thus a daily load curve can suffice for a yearly load curve: You just get the
	 * same day over and over again.
	 * The value returned is the nearest to the interval requested. Thus if you request
	 * hour=12.25 and the interval is 1.0, you will get interval 12.
	 */
	@Override
	public double getPrice(double hr) {
		int index, i;

		double result = 0.0;  // default return value if no points in curve

		if (numPoints > 0)  // handle exceptional cases
			if (numPoints == 1) {
				result = priceValues[0];
			} else {
				if (interval > 0.0) {
					index = (int) Math.round(hr / interval);
					if (index >= numPoints)
						index = index % numPoints;  // wrap around using remainder
					if (index == -1)
						index = numPoints;
					result = priceValues[index];
				} else {
					// for random interval

					/* Start with previous value accessed under the assumption that most
					 * of the time, this function will be called sequentially.
					 */

					/* Normalize Hr to max hour in curve to get wraparound */
					if (hr > hours[numPoints])
						hr = hr - (int) (hr / hours[numPoints]) * hours[numPoints];

					if (hours[lastValueAccessed] > hr)
						lastValueAccessed = 0;  // start over from beginning
					for (i = lastValueAccessed + 1; i < numPoints; i++) {
						if (Math.abs(hours[i] - hr) < 0.00001) {  // if close to an actual point, just use it.
							result = priceValues[i];
							lastValueAccessed = i;
							return result;
						} else if (hours[i] > hr) {  // interpolate for price
							lastValueAccessed = i - 1;
							result = priceValues[lastValueAccessed] +
									(hr - hours[lastValueAccessed]) / (hours[i] - hours[lastValueAccessed]) *
									(priceValues[i] - priceValues[lastValueAccessed]);
							return result;
						}
					}

					// if we fall through the loop, just use last value
					lastValueAccessed = numPoints - 1;
					result            = priceValues[numPoints];
				}
			}

		return result;
	}

	private void calcMeanandStdDev() {
		if (numPoints > 0) {
			if (interval > 0.0) {
				MathUtil.meanandStdDev(priceValues, numPoints, mean, stdDev);
			} else {
				MathUtil.curveMeanAndStdDev(priceValues, hours, numPoints, mean, stdDev);
			}
		}

		setPropertyValue(4, String.format("%.8g", mean[0]));
		setPropertyValue(5, String.format("%.8g", stdDev[0]));

		stdDevCalculated = true;
	}

	@Override
	public double getMean() {
		if (!stdDevCalculated)
			calcMeanandStdDev();
		return mean[0];
	}

	@Override
	public double getStdDev() {
		if (!stdDevCalculated)
			calcMeanandStdDev();
		return stdDev[0];
	}

	/**
	 * Get prices by index.
	 */
	@Override
	public double getPrice(int i) {
		if (i < numPoints && i >= 0) {
			lastValueAccessed = i;
			return priceValues[i];
		} else {
			return 0.0;
		}
	}

	/**
	 * Get hour corresponding to point index.
	 */
	@Override
	public double getHour(int i) {
		if (interval == 0) {
			if (i < numPoints && i >= 0) {
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

	@Override
	public void dumpProperties(OutputStream out, boolean complete) {
		super.dumpProperties(out, complete);

		PrintWriter pw = new PrintWriter(out);

		for (int i = 0; i < getParentClass().getNumProperties(); i++) {
			switch (i) {
			case 2:
				pw.println("~ " + getParentClass().getPropertyName(i) +
					"=(" + getPropertyValue(i) + ")");
				break;
			case 3:
				pw.println("~ " + getParentClass().getPropertyName(i) +
					"=(" + getPropertyValue(i) + ")");
				break;
			default:
				pw.println("~ " + getParentClass().getPropertyName(i) +
					"=" + getPropertyValue(i));
				break;
			}
		}

		pw.close();
	}

	@Override
	public String getPropertyValue(int index) {
		String result;
		switch (index) {
		case 2:
			result = "[";
			break;
		case 3:
			result = "[";
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
				result = result + String.format("%-g, " , priceValues[i]);
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
		case 9:
			result = String.format("%.8g", interval * 3600.0);
			break;
		case 10:
			result = String.format("%.8g", interval * 60.0);
			break;
		default:
			result = super.getPropertyValue(index);
			break;
		}

		switch (index) {
		case 2:
			result = result + "]";
			break;
		case 3:
			result = result + "]";
			break;
		}

		return result;
	}

	@Override
	public void initPropertyValues(int arrayOffset) {

		setPropertyValue(0, "0");  // number of points to expect
		setPropertyValue(1, "1");  // default = 1.0 hr;
		setPropertyValue(2, "");   // vector of multiplier values
		setPropertyValue(3, "");   // vextor of hour values
		setPropertyValue(4, "0");  // set the mean (otherwise computed)
		setPropertyValue(5, "0");  // set the std dev (otherwise computed)
		setPropertyValue(6, "");   // switch input to a csvfile
		setPropertyValue(7, "");   // switch input to a binary file of singles
		setPropertyValue(8, "");   // switch input to a binary file of singles
		setPropertyValue(9, "3600");  // seconds
		setPropertyValue(10, "60");   // minutes
		setPropertyValue(11, "");     // action option

		super.initPropertyValues(PriceShape.NumPropsThisClass);
	}

	// FIXME Private method in OpenDSS
	@Override
	public void saveToDblFile() {
		throw new UnsupportedOperationException();
	}

	// FIXME Private method in OpenDSS
	@Override
	public void saveToSngFile() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setMean(double value) {
		stdDevCalculated = true;
		mean[0] = value;
	}

	@Override
	public void setNumPoints(int num) {
		setPropertyValue(0, String.valueOf(num));  // update property list variable

		// reset array property values to keep them in proper order in save

		if (arrayPropertyIndex >= 0)
			setPropertyValue(arrayPropertyIndex, getPropertyValue(arrayPropertyIndex));

		numPoints = num;
	}

	@Override
	public void setStdDev(double stddev) {
		stdDevCalculated = true;
		stdDev[0] = stddev;
	}

	@Override
	public int getNumPoints() {
		return 0;
	}

	@Override
	public double getInterval() {
		return interval;
	}

	@Override
	public double[] getHours() {
		return hours;
	}

	@Override
	public void setHours(double[] values) {
		hours = values;
	}

	@Override
	public double[] getPriceValues() {
		return priceValues;
	}

	@Override
	public void setPriceValues(double[] values) {
		priceValues = values;
	}

	// FIXME Private members in OpenDSS

	@Override
	public int getLastValueAccessed() {
		return lastValueAccessed;
	}

	@Override
	public void setLastValueAccessed(int lastValue) {
		lastValueAccessed = lastValue;
	}

	@Override
	public int getArrayPropertyIndex() {
		return arrayPropertyIndex;
	}

	@Override
	public void setArrayPropertyIndex(int index) {
		arrayPropertyIndex = index;
	}

	@Override
	public boolean isStdDevCalculated() {
		return stdDevCalculated;
	}

	@Override
	public void setStdDevCalculated(boolean calculated) {
		stdDevCalculated = calculated;
	}

	@Override
	public void setInterval(double value) {
		interval = value;
	}

}