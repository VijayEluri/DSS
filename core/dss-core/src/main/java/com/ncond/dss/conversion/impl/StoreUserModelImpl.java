package com.ncond.dss.conversion.impl;

import org.apache.commons.math.complex.Complex;

import com.ncond.dss.common.impl.DSSCallBacks;
import com.ncond.dss.conversion.StoreUserModel;
import com.ncond.dss.shared.impl.GeneratorVars;

public class StoreUserModelImpl implements StoreUserModel {

	private int handle;  // handle to DLL containing user model
	private int id;      // id of this instance of the user model
	private String name; // name of the DLL file containing user model
	private boolean funcError;

	public StoreUserModelImpl() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Make a new instance.
	 */
	private int makeNew(GeneratorVars genVars, DSSCallBacks callBacks) {
		return 0;
	}

	/**
	 * Deletes specified instance.
	 */
	private void delete(int x) {

	}

	/**
	 * Select active instance.
	 */
	@Override
	public int select(int x) {
		return 0;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String Value) {
	}

	private Object checkFuncError(Object addr, String funcName) {
		return null;
	}

	@Override
	public void edit(String value) {

	}

	@Override
	public boolean exists() {
		return false;
	}

	/**
	 * Send string to user model to handle.
	 */
	@Override
	public void edit(int s, int maxlen) {
	}

	/**
	 * For dynamics.
	 */
	@Override
	public void init(Complex[] V, Complex[] I) {

	}

	/**
	 * Returns currents or sets pShaft.
	 */
	@Override
	public void calc(Complex[] V, Complex[] I) {

	}

	/**
	 * Integrates any state vars.
	 */
	@Override
	public void integrate() {

	}

	/**
	 * Called when props of generator updated.
	 */
	@Override
	public void updateModel() {

	}

	@Override
	public void save() {

	}

	@Override
	public void restore() {

	}

	/* Monitoring functions */

	@Override
	public int numVars() {
		return 0;
	}

	@Override
	public void getAllVars(double[] vars) {

	}

	@Override
	public void getAllVars(double var) {

	}

	@Override
	public double getVariable(int i) {
		return 0.0;
	}

	@Override
	public void setVariable(int i, double value) {

	}

	@Override
	public void getVarName(int varNum, int varName, int maxlen) {

	}

}
