package com.ncond.dss.common;

import com.ncond.dss.common.impl.ControlProblem;
import com.ncond.dss.common.impl.Esolv32Problem;
import com.ncond.dss.common.impl.SolverError;

/*
 * Unit for processing the AutoAdd solution functions
 *
 * Note: Make sure this class in instantiated after EnergyMeter class
 *
 * There is one of these per circuit.
 */
public interface AutoAdd {

	double getGenKW();

	void setGenKW(double genkW);

	double getGenPF();

	void setGenPF(double genPF);

	double getGenKVAr();

	void setGenKVAr(double genkvar);

	double getCapKVAr();

	void setCapKVAr(double capkvar);

	int getAddType();

	void setAddType(int addType);

	boolean isModeChanged();

	void setModeChanged(boolean modeChanged);

	void makeBusList();

	void appendToFile(String whichFile, String s);

	void addCurrents(int solveType);

	/**
	 * Automatically add capacitors or generators.
	 *
	 * @throws ControlProblem
	 * @throws SolverError
	 * @throws Esolv32Problem
	 */
	int solve() throws SolverError, ControlProblem, Esolv32Problem;

	double getWeightedLosses();

}
