package com.epri.dss.meter.impl;

import java.io.File;
import java.io.PrintStream;

import com.epri.dss.shared.impl.Complex;

import com.epri.dss.common.impl.DSSClassImpl;
import com.epri.dss.common.FeederObj;
import com.epri.dss.conversion.GeneratorObj;
import com.epri.dss.conversion.LoadObj;
import com.epri.dss.meter.EnergyMeterObj;
import com.epri.dss.shared.CktTree;
import com.epri.dss.shared.impl.CktTreeImpl.CktTreeNode;

public class EnergyMeterObjImpl extends MeterElementImpl implements EnergyMeterObj {

	private boolean FirstSampleAfterReset;
	private boolean ExcessFlag;
	private boolean ZoneIsRadial;
	private boolean VoltageUEOnly;
	private boolean LocalOnly;
	private boolean HasFeeder;

	private boolean Losses;
	private boolean LineLosses;
	private boolean XfmrLosses;
	private boolean SeqLosses;
	private boolean ThreePhaseLosses;
	private boolean VBaseLosses;
	private boolean PhaseVoltageReport;

	private FeederObj FeederObj;   // not used at present
	private String[] DefinedZoneList;
	private int DefinedZoneListSize;

	/* Limits on the entire load in the zone for networks where UE cannot be
	 * determined by the individual branches.
	 */
	private double MaxZonekVA_Norm;
	private double MaxZonekVA_Emerg;

	/* Voltage bases in the Meter Zone */
	private double[] VBaseTotalLosses;    // allocated array
	private double[] VBaseLineLosses;
	private double[] VBaseLoadLosses;
	private double[] VBaseNoLoadLosses;
	private double[] VBaseLoad;
	private double[] VBaseList;
	private int VBaseCount;
	private int MaxVBaseCount;

	/* Arrays for phase voltage report */
	private double[] VphaseMax;
	private double[] VPhaseMin;
	private double[] VPhaseAccum;
	private int[] VPhaseAccumCount;
	private File VPhase_File;
	private boolean VPhaseReportFileIsOpen;

	/* Demand Interval File variables */
	private File DI_File;
	private boolean This_Meter_DIFileIsOpen;


	protected String[] RegisterNames = new String[NumEMRegisters];

	protected CktTree BranchList;  // Pointers to all circuit elements in meter's zone

	protected double[] Registers   = new double[NumEMRegisters];
	protected double[] Derivatives = new double[NumEMRegisters];
	protected double[] TotalsMask  = new double[NumEMRegisters];

	public EnergyMeterObjImpl(DSSClassImpl ParClass, String EnergyMeterName) {
		super(ParClass);
		// TODO Auto-generated constructor stub
	}

	private static int jiIndex(int i, int j) {
		return (j - 1) * 3 + i;
	}

	private void integrate(int Reg, double Deriv, double Interval) {

	}

	private void setDragHandRegister(int Reg, double Value) {

	}

	private double accumulateLoad(LoadObj Load, double TotalZonekW,
			double TotalZonekvar, double TotalLoad_EEN, double TotalLoad_UE) {
		return 0.0;
	}

	private void accumulateGen(GeneratorObj Gen, double TotalZonekW, double TotalZonekvar) {

	}

	private void calcBusCoordinates(CktTreeNode StartBranch, int FirstCoordRef,
			int SecondCoordRef, int LineCount) {

	}

	private int addToVoltBaseList(int BusRef) {
		return 0;
	}

	private String makeDIFileName() {
		return null;
	}

	private String makeVPhaseReportFileName() {
		return null;
	}

	private void assignVoltBaseRegisterNames() {

	}

	private void makeFeederObj() {

	}

	private void removeFeederObj() {

	}

	private void totalUpDownstreamCustomers() {

	}

	protected void openDemandIntervalFile() {

	}

	protected void writeDemandIntervalData() {

	}

	protected void closeDemandIntervalFile() {

	}

	protected void appendDemandIntervalFile() {

	}

	public String[] getRegisterNames() {
		return RegisterNames;
	}

	public void setRegisterNames(String[] registerNames) {
		RegisterNames = registerNames;
	}

	public CktTree getBranchList() {
		return BranchList;
	}

	public void setBranchList(CktTree branchList) {
		BranchList = branchList;
	}

	public double[] getRegisters() {
		return Registers;
	}

	public void setRegisters(double[] registers) {
		Registers = registers;
	}

	public double[] getDerivatives() {
		return Derivatives;
	}

	public void setDerivatives(double[] derivatives) {
		Derivatives = derivatives;
	}

	public double[] getTotalsMask() {
		return TotalsMask;
	}

	public void setTotalsMask(double[] totalsMask) {
		TotalsMask = totalsMask;
	}

	/* Make a positive Sequence Model */
	@Override
	public void makePosSequence() {

	}

	@Override
	public void recalcElementData() {

	}

	@Override
	public void calcYPrim() {

	}

	@Override
	public void getInjCurrents(Complex[] Curr) {

	}

	@Override
	public void getCurrents(Complex[] Curr) {

	}

	public void resetRegisters() {

	}

	@Override
	public void takeSample() {

	}

	public void saveRegisters() {

	}

	public void makeMeterZoneLists() {

	}

	public void zoneDump() {

	}

	public void interpolateCoordinates() {

	}

	public void enableFeeder() {

	}

	public void allocateLoad() {

	}

	/* Reduce Zone by eliminating buses and merging lines */
	public void reduceZone() {

	}

	public void saveZone(String dirname) {

	}

	@Override
	public String getPropertyValue(int Index) {
		return null;
	}

	@Override
	public void initPropertyValues(int ArrayOffset) {

	}

	@Override
	public void dumpProperties(PrintStream F, boolean Complete) {

	}

	// FIXME Private members in OpenDSS

	public boolean isFirstSampleAfterReset() {
		return FirstSampleAfterReset;
	}

	public void setFirstSampleAfterReset(boolean firstSampleAfterReset) {
		FirstSampleAfterReset = firstSampleAfterReset;
	}

	public boolean isExcessFlag() {
		return ExcessFlag;
	}

	public void setExcessFlag(boolean excessFlag) {
		ExcessFlag = excessFlag;
	}

	public boolean isZoneIsRadial() {
		return ZoneIsRadial;
	}

	public void setZoneIsRadial(boolean zoneIsRadial) {
		ZoneIsRadial = zoneIsRadial;
	}

	public boolean isVoltageUEOnly() {
		return VoltageUEOnly;
	}

	public void setVoltageUEOnly(boolean voltageUEOnly) {
		VoltageUEOnly = voltageUEOnly;
	}

	public boolean isLocalOnly() {
		return LocalOnly;
	}

	public void setLocalOnly(boolean localOnly) {
		LocalOnly = localOnly;
	}

	public boolean isHasFeeder() {
		return HasFeeder;
	}

	public void setHasFeeder(boolean hasFeeder) {
		HasFeeder = hasFeeder;
	}

	public boolean isLosses() {
		return Losses;
	}

	public void setLosses(boolean losses) {
		Losses = losses;
	}

	public boolean isLineLosses() {
		return LineLosses;
	}

	public void setLineLosses(boolean lineLosses) {
		LineLosses = lineLosses;
	}

	public boolean isXfmrLosses() {
		return XfmrLosses;
	}

	public void setXfmrLosses(boolean xfmrLosses) {
		XfmrLosses = xfmrLosses;
	}

	public boolean isSeqLosses() {
		return SeqLosses;
	}

	public void setSeqLosses(boolean seqLosses) {
		SeqLosses = seqLosses;
	}

	public boolean isThreePhaseLosses() {
		return ThreePhaseLosses;
	}

	public void setThreePhaseLosses(boolean threePhaseLosses) {
		ThreePhaseLosses = threePhaseLosses;
	}

	public boolean isVBaseLosses() {
		return VBaseLosses;
	}

	public void setVBaseLosses(boolean vBaseLosses) {
		VBaseLosses = vBaseLosses;
	}

	public boolean isPhaseVoltageReport() {
		return PhaseVoltageReport;
	}

	public void setPhaseVoltageReport(boolean phaseVoltageReport) {
		PhaseVoltageReport = phaseVoltageReport;
	}

	public FeederObj getFeederObj() {
		return FeederObj;
	}

	public void setFeederObj(FeederObj feederObj) {
		FeederObj = feederObj;
	}

	public String[] getDefinedZoneList() {
		return DefinedZoneList;
	}

	public void setDefinedZoneList(String[] definedZoneList) {
		DefinedZoneList = definedZoneList;
	}

	public int getDefinedZoneListSize() {
		return DefinedZoneListSize;
	}

	public void setDefinedZoneListSize(int definedZoneListSize) {
		DefinedZoneListSize = definedZoneListSize;
	}

	public double getMaxZonekVA_Norm() {
		return MaxZonekVA_Norm;
	}

	public void setMaxZonekVA_Norm(double maxZonekVA_Norm) {
		MaxZonekVA_Norm = maxZonekVA_Norm;
	}

	public double getMaxZonekVA_Emerg() {
		return MaxZonekVA_Emerg;
	}

	public void setMaxZonekVA_Emerg(double maxZonekVA_Emerg) {
		MaxZonekVA_Emerg = maxZonekVA_Emerg;
	}

	public double[] getVBaseTotalLosses() {
		return VBaseTotalLosses;
	}

	public void setVBaseTotalLosses(double[] vBaseTotalLosses) {
		VBaseTotalLosses = vBaseTotalLosses;
	}

	public double[] getVBaseLineLosses() {
		return VBaseLineLosses;
	}

	public void setVBaseLineLosses(double[] vBaseLineLosses) {
		VBaseLineLosses = vBaseLineLosses;
	}

	public double[] getVBaseLoadLosses() {
		return VBaseLoadLosses;
	}

	public void setVBaseLoadLosses(double[] vBaseLoadLosses) {
		VBaseLoadLosses = vBaseLoadLosses;
	}

	public double[] getVBaseNoLoadLosses() {
		return VBaseNoLoadLosses;
	}

	public void setVBaseNoLoadLosses(double[] vBaseNoLoadLosses) {
		VBaseNoLoadLosses = vBaseNoLoadLosses;
	}

	public double[] getVBaseLoad() {
		return VBaseLoad;
	}

	public void setVBaseLoad(double[] vBaseLoad) {
		VBaseLoad = vBaseLoad;
	}

	public double[] getVBaseList() {
		return VBaseList;
	}

	public void setVBaseList(double[] vBaseList) {
		VBaseList = vBaseList;
	}

	public int getVBaseCount() {
		return VBaseCount;
	}

	public void setVBaseCount(int vBaseCount) {
		VBaseCount = vBaseCount;
	}

	public int getMaxVBaseCount() {
		return MaxVBaseCount;
	}

	public void setMaxVBaseCount(int maxVBaseCount) {
		MaxVBaseCount = maxVBaseCount;
	}

	public double[] getVphaseMax() {
		return VphaseMax;
	}

	public void setVphaseMax(double[] vphaseMax) {
		VphaseMax = vphaseMax;
	}

	public double[] getVPhaseMin() {
		return VPhaseMin;
	}

	public void setVPhaseMin(double[] vPhaseMin) {
		VPhaseMin = vPhaseMin;
	}

	public double[] getVPhaseAccum() {
		return VPhaseAccum;
	}

	public void setVPhaseAccum(double[] vPhaseAccum) {
		VPhaseAccum = vPhaseAccum;
	}

	public int[] getVPhaseAccumCount() {
		return VPhaseAccumCount;
	}

	public void setVPhaseAccumCount(int[] vPhaseAccumCount) {
		VPhaseAccumCount = vPhaseAccumCount;
	}

	public File getVPhase_File() {
		return VPhase_File;
	}

	public void setVPhase_File(File vPhase_File) {
		VPhase_File = vPhase_File;
	}

	public boolean isVPhaseReportFileIsOpen() {
		return VPhaseReportFileIsOpen;
	}

	public void setVPhaseReportFileIsOpen(boolean vPhaseReportFileIsOpen) {
		VPhaseReportFileIsOpen = vPhaseReportFileIsOpen;
	}

	public File getDI_File() {
		return DI_File;
	}

	public void setDI_File(File dI_File) {
		DI_File = dI_File;
	}

	public boolean isThis_Meter_DIFileIsOpen() {
		return This_Meter_DIFileIsOpen;
	}

	public void setThis_Meter_DIFileIsOpen(boolean this_Meter_DIFileIsOpen) {
		This_Meter_DIFileIsOpen = this_Meter_DIFileIsOpen;
	}

}
