/**
 * Copyright (C) 2008-2012 Electric Power Research Institute, Inc.
 * Copyright (C) 2009-2012 Richard Lincoln
 * All rights reserved.
 */
package com.ncond.dss.meter;

import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.commons.math.complex.Complex;

import com.ncond.dss.common.DSS;
import com.ncond.dss.common.DSSClass;
import com.ncond.dss.common.types.Connection;

import static com.ncond.dss.common.Util.getCktElementIndex;
import static com.ncond.dss.common.Util.resizeArray;

import static java.lang.Math.pow;


/**
 * Sensor compares voltages and currents. Power quantities are converted to
 * current quantities based on rated kVBase, or actual voltage if voltage
 * measurement specified.
 */
public class SensorObj extends MeterElement {

	private boolean validSensor;
	private double[] sensorKW;
	private double[] sensorKVAr;
	private double kVBase;  // value specified
	private double VBase;   // in volts

	private Connection conn;

	private boolean VSpecified, ISpecified, PSpecified, QSpecified;

	private boolean clearSpecified;
	private int deltaDirection;

	protected double pctError, weight;

	public SensorObj(DSSClass parClass, String sensorName) {
		super(parClass);
		setName(sensorName.toLowerCase());

		setNumPhases(3);  // directly set conds and phases
		nConds = 3;
		setNumTerms(1);   // this forces allocation of terminals and conductors in base class

		sensorKW   = null;
		sensorKVAr = null;

		kVBase = 12.47;  // default 3-phase voltage
		weight = 1.0;
		pctError = 1.0;

		setConn(Connection.WYE);

		clearSensor();

		objType = parClass.getClassType();  // SENSOR_ELEMENT;

		initPropertyValues(0);

		//recalcElementData();
	}

	@Override
	public void recalcElementData() {

		validSensor = false;
		int devIndex = getCktElementIndex(elementName);
		if (devIndex >= 0) {  // sensored element must already exist
			meteredElement = DSS.activeCircuit.getCktElements().get(devIndex);

			if (meteredTerminalIdx >= meteredElement.getNumTerms()) {
				DSS.doErrorMsg("Sensor: \"" + getName() + "\"",
						"Terminal no. \"" +"\" does not exist.",
						"Respecify terminal no.", 665);
			} else {
				setNumPhases( meteredElement.getNumPhases() );
				setNumConds( meteredElement.getNumConds() );

				// sets name of i-th terminal's connected bus in Sensor's bus list
				// this value will be used to set the nodeRef array (see takeSample)
				setBus(0, meteredElement.getBus(meteredTerminalIdx));

				clearSensor();

				validSensor = true;

				allocateSensorObjArrays();
				zeroSensorArrays();
				recalcVbase();
			}
		} else {
			meteredElement = null;   // element not found
			DSS.doErrorMsg("Sensor: \"" + getName() + "\"", "Circuit Element \""+ elementName + "\" not found.",
					" Element must be defined previously.", 666);
		}
	}

	/**
	 * Make a positive sequence model.
	 */
	@Override
	public void makePosSequence() {
		if (meteredElement != null) {
			setBus(0, meteredElement.getBus(meteredTerminalIdx));
			setNumPhases( meteredElement.getNumPhases() );
			setNumConds( meteredElement.getNumConds() );
			clearSensor();
			validSensor = true;
			allocateSensorObjArrays();
			zeroSensorArrays();
			recalcVbase();
		}
		super.makePosSequence();
	}

	private void recalcVbase() {
		switch (conn) {
		case WYE:
			if (nPhases == 1) {
				VBase = kVBase * 1000.0;
			} else {
				VBase = kVBase * 1000.0 / DSS.SQRT3;
			}
			break;
		case DELTA:
			VBase = kVBase * 1000.0;
			break;
		}
	}

	@Override
	public void calcYPrim() {
		// leave YPrims as nil and they will be ignored
	}

	public void resetIt() {
		clearSensor();
	}

	/**
	 * For delta connections or line-line voltages.
	 */
	private int rotatePhases(int j) {
		int result = j + deltaDirection;

		// make sure result is within limits
		if (nPhases > 2) {
			// assumes 2 phase delta is open delta
			if (result >= nPhases)
				result = 0;
			if (result < 0)
				result = nPhases - 1;
		} else {
			if (result < 0)
				result = 2;  // for 2-phase delta, next phase will be 3rd phase
		}

		return result;
	}

	@Override
	public void takeSample() {
		if ( !(validSensor && isEnabled()) )
			return;

		meteredElement.getCurrents(calculatedCurrent);
		computeVTerminal();
		switch (conn) {
		case DELTA:
			for (int i = 0; i < nPhases; i++)
				calculatedVoltage[i] = VTerminal[i].subtract( VTerminal[rotatePhases(i)] );
			break;
		default:
			for (int i = 0; i < nPhases; i++)
				calculatedVoltage[i] = VTerminal[i];
			break;
		}
	}

	@Override
	public void getCurrents(Complex[] curr) {
		for (int i = 0; i < nConds; i++)
			curr[i] = Complex.ZERO;
	}

	@Override
	public void getInjCurrents(Complex[] curr) {
		for (int i = 0; i < nConds; i++)
			curr[i] = Complex.ZERO;
	}

	/**
	 * Return the WLS Error for currents.
	 * Get square error and weight it.
	 */
	public double getWLSCurrentError() {
		double kVA;
		int i;

		double result = 0.0;

		/* Convert P and Q specification to currents */
		if (PSpecified) {  // compute currents assuming vbase
			if (QSpecified) {
				for (i = 0; i < nPhases; i++) {
					kVA = new Complex(sensorKW[i], sensorKVAr[i]).abs();
					sensorCurrent[i] = kVA * 1000.0 / VBase;
				}
			} else {  // no Q just use P
				for (i = 0; i < nPhases; i++)
					sensorCurrent[i] = sensorKW[i] * 1000.0 / VBase;
			}
			ISpecified = true;  // overrides current specification
		}

		if (ISpecified)
			for (i = 0; i < nPhases; i++)
				result = result + pow(calculatedCurrent[i].getReal(), 2) + pow(calculatedCurrent[i].getImaginary(), 2) - pow(sensorCurrent[i], 2);

		result = result * weight;

		return result;
	}

	/**
	 * Get square error and weight it.
	 */
	public double getWLSVoltageError() {
		int i;
		double result = 0.0;

		if (VSpecified)
			for (i = 0; i < nPhases; i++)
				result = result + pow(calculatedVoltage[i].getReal(), 2) + pow(calculatedVoltage[i].getImaginary(), 2) - pow(sensorVoltage[i], 2);

		result = result * weight;

		return result;
	}

	@Override
	public void dumpProperties(OutputStream out, boolean complete) {
		super.dumpProperties(out, complete);

		PrintWriter pw = new PrintWriter(out);

		for (int i = 0; i < getParentClass().getNumProperties(); i++) {
			pw.println("~ " + getParentClass().getPropertyName(i) +
				"=" + getPropertyValue(i));
		}

		if (complete) pw.println();

		pw.close();
	}

	// FIXME Private method in OpenDSS
	public void clearSensor() {
		VSpecified = false;
		ISpecified = false;
		PSpecified = false;
		QSpecified = false;
		clearSpecified = false;
	}

	private void allocateSensorObjArrays() {
		sensorKW = resizeArray(sensorKW, nPhases);
		sensorKVAr = resizeArray(sensorKVAr, nPhases);
		allocateSensorArrays();
	}

	private void zeroSensorArrays() {
		for (int i = 0; i < nPhases; i++) {
			sensorCurrent[i] = 0.0;
			sensorVoltage[i] = 0.0;
			sensorKW[i]      = 0.0;
			sensorKVAr[i]    = 0.0;
		}
	}

	@Override
	public void initPropertyValues(int arrayOffset) {
		setPropertyValue(0, "");   // 'element';
		setPropertyValue(1, "1");  // 'terminal';
		setPropertyValue(2, "12.47");  // 'kVBase';
		setPropertyValue(3, "No");  // must be set to yes to clear before setting quantities
		setPropertyValue(4, "[7.2, 7.2, 7.2]");
		setPropertyValue(5, "[0.0, 0.0, 0.0]");  // currents
		setPropertyValue(6, "[0.0, 0.0, 0.0]");  // P kW
		setPropertyValue(7, "[0.0, 0.0, 0.0]");  // Q kvar
		setPropertyValue(8, "wye");
		setPropertyValue(9, "1");
		setPropertyValue(10, "1");  // %Error
		setPropertyValue(11, "1");  // %Error
		setPropertyValue(12, "");   // Action

		super.initPropertyValues(Sensor.NumPropsThisClass);
	}

	@Override
	public int injCurrents() {
		throw new UnsupportedOperationException();
	}

	// FIXME Private method in OpenDSS
	public int limitToPlusMinusOne(int i) {
		if (i >= 0) {
			return 1;
		} else {
			return -1;
		}
	}

	/**
	 * Saves present buffer to file.
	 */
	public void save() {

	}

	/**
	 * Connection code.
	 */
	public void setConn(Connection value) {
		conn = value;
		recalcVbase();
	}

	public void setAction(String value) {

	}

	public boolean isClearSpecified() {
		return clearSpecified;
	}

	public void setClearSpecified(boolean clearSpecified) {
		this.clearSpecified = clearSpecified;
	}

	public double[] getSensorKW() {
		return sensorKW;
	}

	public double[] getSensorKVAr() {
		return sensorKVAr;
	}

	public void setKVBase(double kVBase) {
		this.kVBase = kVBase;
	}

	public void setISpecified(boolean iSpecified) {
		ISpecified = iSpecified;
	}

	public void setPSpecified(boolean pSpecified) {
		PSpecified = pSpecified;
	}

	public void setQSpecified(boolean qSpecified) {
		QSpecified = qSpecified;
	}

	public void setDeltaDirection(int deltaDirection) {
		this.deltaDirection = deltaDirection;
	}

	public void setPctError(double pctError) {
		this.pctError = pctError;
	}

	public void setVSpecified(boolean vSpecified) {
		VSpecified = vSpecified;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

}
