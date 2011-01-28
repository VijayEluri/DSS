package com.epri.dss.conversion;

import java.io.PrintStream;

import com.epri.dss.general.GrowthShapeObj;
import com.epri.dss.general.LoadShapeObj;

import cern.colt.matrix.tdcomplex.DComplexMatrix1D;
import cern.colt.matrix.tdcomplex.DComplexMatrix2D;

public interface LoadObj extends PCElement {

	int getConnection();

	void setConnection(int connection);

	String getDailyShape();

	void setDailyShape(String dailyShape);

	LoadShapeObj getDailyShapeObj();

	void setDailyShapeObj(LoadShapeObj dailyShapeObj);

	String getDutyShape();

	void setDutyShape(String dutyShape);

	LoadShapeObj getDutyShapeObj();

	void setDutyShapeObj(LoadShapeObj dutyShapeObj);

	double getEEN_Factor();

	void setEEN_Factor(double eEN_Factor);

	String getGrowthShape();

	void setGrowthShape(String growthShape);

	GrowthShapeObj getGrowthShapeObj();

	void setGrowthShapeObj(GrowthShapeObj growthShapeObj);

	Boolean getHasBeenAllocated();

	void setHasBeenAllocated(Boolean hasBeenAllocated);

	double getkWBase();

	void setkWBase(double kWBase);

	double getkVABase();

	void setkVABase(double kVABase);

	double getKvarBase();

	void setKvarBase(double kvarBase);

	double getkVLoadBase();

	void setkVLoadBase(double kVLoadBase);

	int getLoadClass();

	void setLoadClass(int loadClass);

	int getNumCustomers();

	void setNumCustomers(int numCustomers);

	int getLoadSpecType();

	void setLoadSpecType(int loadSpecType);

	double getPFNominal();

	void setPFNominal(double pFNominal);

	double getRneut();

	void setRneut(double rneut);

	double getUE_Factor();

	void setUE_Factor(double uE_Factor);

	double getXneut();

	void setXneut(double xneut);

	String getYearlyShape();

	void setYearlyShape(String yearlyShape);

	LoadShapeObj getYearlyShapeObj();

	void setYearlyShapeObj(LoadShapeObj yearlyShapeObj);

	String getCVRshape();

	void setCVRshape(String cVRshape);

	LoadShapeObj getCVRShapeObj();

	void setCVRShapeObj(LoadShapeObj cVRShapeObj);

	int getLoadModel();

	void setLoadModel(int loadModel);

	double getPuMean();

	double getPuStdDev();

	double getCVRwattFactor();

	double getCVRvarFactor();

	double getVmaxpu();

	double getVminEmerg();

	double getVminNormal();

	double getVminpu();

	boolean isExemptFromLDCurve();

	boolean isFixed();

	boolean getUnserved();

	boolean getExceedsNormal();

	/* Allocate load from connected kva or kWh billing */
	void setkVAAllocationFactor(double Value);

	double getkVAAllocationFactor();

	void setConnectedkVA(double Value);

	double getConnectedkVA();

	/** Set kWh properties ... */

	void setCFactor(double Value);

	double getCFactor();

	void setKWh(double Value);

	double getKWh();

	void setKWhDays(double Value);

	double getKWhDays();

	/* AllocationFactor adjusts either connected kVA allocation factor
	 * or kWh CFactor
	 */
	void setAllocationFactor(double Value);

	double getAllocationFactor();

	void recalcElementData();

	void calcYPrim();

	int injCurrents();

	void getInjCurrents(DComplexMatrix1D Curr);

	void initHarmonics();

	/* Make a positive Sequence Model */
	void makePosSequence();

	void setNominalLoad();

	/*
	 * 0 = reset to 1.0
	 * 1 = Gaussian around mean and std Dev
	 * 2 = uniform
	 */
	void randomize(int Opt);

	String getPropertyValue(int Index);

	void initPropertyValues(int ArrayOffset);

	void dumpProperties(PrintStream F, boolean Complete);

	void updateVoltageBases();
}
