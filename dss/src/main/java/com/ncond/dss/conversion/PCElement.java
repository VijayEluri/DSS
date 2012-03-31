/**
 * Copyright (C) 2008-2012 Electric Power Research Institute, Inc.
 * Copyright (C) 2009-2012 Richard Lincoln
 * All rights reserved.
 */
package com.ncond.dss.conversion;

import java.io.PrintStream;

import com.ncond.dss.shared.Complex;

import com.ncond.dss.common.CktElement;
import com.ncond.dss.common.DSS;
import com.ncond.dss.common.DSSClass;
import com.ncond.dss.common.DSSClassDefs;
import com.ncond.dss.common.SolutionObj;
import com.ncond.dss.general.SpectrumObj;
import com.ncond.dss.meter.MeterElement;

public abstract class PCElement extends CktElement {

	private boolean ITerminalUpdated;

	protected String spectrum;
	protected SpectrumObj spectrumObj;
	/** Upline energy meter */
	private MeterElement meterObj;
	/** Upline sensor for this element */
	private MeterElement sensorObj;

	protected Complex[] injCurrent;

	public PCElement(DSSClass ParClass) {
		super(ParClass);
		spectrum = "default";
		spectrumObj = null;  // have to allocate later because not guaranteed there will be one now
		sensorObj = null;
		meterObj = null;
		injCurrent = null;
		ITerminalUpdated = false;

		objType = DSSClassDefs.PC_ELEMENT;
	}

	/**
	 * Add injection currents into system currents array.
	 */
	@Override
	public int injCurrents() {
		SolutionObj sol = DSS.activeCircuit.getSolution();

		for (int i = 0; i < YOrder; i++) {
			sol.setCurrent(
				nodeRef[i],
				sol.getCurrent(nodeRef[i]).add(injCurrent[i])
			);
		}

		return 0;
	}

	/**
	 * This is called only if we need to compute the terminal currents from the inj currents.
	 *
	 * Such as for harmonic model.
	 */
	protected void getTerminalCurrents(Complex[] curr) {
		int i;

		if (isITerminalUpdated()) {  // just copy ITerminal unless ITerminal=curr
			if (curr != getITerminal()) {
				for (i = 0; i < YOrder; i++)
					curr[i] = getITerminal(i);
			}
		} else {
			YPrim.vMult(curr, getVTerminal());
			for (i = 0; i < YOrder; i++)
				curr[i] = curr[i].add( getInjCurrent(i).neg() );
			setITerminalUpdated(true);
		}

		ITerminalSolutionCount = DSS.activeCircuit.getSolution().getSolutionCount();
	}

	/**
	 * Get present values of terminal.
	 *
	 * Gets total currents going into a devices terminals.
	 */
	@Override
	public void getCurrents(Complex[] curr) {
		SolutionObj sol = DSS.activeCircuit.getSolution();

		try {
			if (isEnabled()) {
				if (sol.lastSolutionWasDirect() &&
					(! (sol.isDynamicModel() || sol.isHarmonicModel()) )) {

					// take a short cut and get currents from YPrim only
					// for case where model is entirely in Y matrix
					calcYPrimContribution(curr);
				} else {
					getTerminalCurrents(curr);
				}
			} else {  // not enabled
				for (int i = 0; i < YOrder; i++)
					curr[i] = Complex.ZERO;
			}
		} catch (Exception e) {
			DSS.doErrorMsg(("getCurrents for element: " + getName() + "."), e.getMessage(),
					"Inadequate storage allotted for circuit element.", 641);
		}
	}

	public void calcYPrimContribution(Complex[] curr) {
		computeVTerminal();
		// apply these voltages to Yprim
		YPrim.vMult(curr, VTerminal);
	}

	/**
	 * For harmonics mode
	 */
	public void initHarmonics() {
		// by default do nothing in the base class
	}

	@Override
	public void initPropertyValues(int arrayOffset) {
		setPropertyValue(arrayOffset + 1, spectrum);

		super.initPropertyValues(arrayOffset + 1);
	}

	/**
	 * For dynamics mode and control devices.
	 */
	public void initStateVars() {
		// by default do nothing
	}

	public void integrateStates() {
		// by default do nothing
	}

	public void getAllVariables(double[] states) {
		// by default do nothing
	}

	public int numVariables() {
		return 0;
	}

	public String variableName(int i) {
		return "";  // do nothing
	}

	/**
	 * Search through variable name list and return index if found.
	 * Compare up to length of S.
	 */
	public int lookupVariable(String s) {
		int idx = -1;  // returns -1 for error not found
		int testLength = s.length();
		for (int i = 0; i < numVariables(); i++) {
			if (variableName(i).substring(0, testLength).equalsIgnoreCase(s)) {
				idx = i;
				break;
			}
		}
		return idx;
	}

	public void dumpProperties(PrintStream f, boolean complete) {
		super.dumpProperties(f, complete);

		if (complete) {
			f.println("! Variables");
			for (int i = 0; i < numVariables(); i++) {
				f.println("! " + i + ": " + variableName(i) + " = " +
						String.format("%-.5g", getVariable(i)));
			}
		}
	}

	public double getVariable(int i) {
		/* Do nothing here -- up to override function */
		return -9999.99;
	}

	public void setVariable(int i, double value) {
		/* Do nothing */
	}

	@Override
	public void computeITerminal() {
		SolutionObj sol = DSS.activeCircuit.getSolution();

		if (ITerminalSolutionCount != sol.getSolutionCount()) {
			getCurrents(ITerminal);
			ITerminalSolutionCount = sol.getSolutionCount();
		}
	}

	public void zeroInjCurrent() {
		for (int i = 0; i < YOrder; i++)
			injCurrent[i] = Complex.ZERO;
	}

	public void setITerminalUpdated(boolean value) {
		ITerminalUpdated = value;
		if (value) {
			ITerminalSolutionCount = DSS.activeCircuit.getSolution().getSolutionCount();
		}
	}

	public Complex getInjCurrent(int idx) {
		return injCurrent[idx];
	}

	public Complex[] getInjCurrent() {
		return injCurrent;
	}

	public boolean isITerminalUpdated() {
		return ITerminalUpdated;
	}

	public String getSpectrum() {
		return spectrum;
	}

	public SpectrumObj getSpectrumObj() {
		return spectrumObj;
	}

	public MeterElement getMeterObj() {
		return meterObj;
	}

	public MeterElement getSensorObj() {
		return sensorObj;
	}

	public void setSpectrum(String spectrum) {
		this.spectrum = spectrum;
	}

	public void setSpectrumObj(SpectrumObj spectrumObj) {
		this.spectrumObj = spectrumObj;
	}

	public void setMeterObj(MeterElement meterObj) {
		this.meterObj = meterObj;
	}

	public void setSensorObj(MeterElement sensorObj) {
		this.sensorObj = sensorObj;
	}

	public void setInjCurrent(Complex[] injCurrent) {
		this.injCurrent = injCurrent;
	}

}
