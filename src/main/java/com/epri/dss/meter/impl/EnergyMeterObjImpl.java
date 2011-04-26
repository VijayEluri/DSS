package com.epri.dss.meter.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.List;

import com.epri.dss.parser.impl.Parser;
import com.epri.dss.shared.impl.CktTreeImpl;
import com.epri.dss.shared.impl.Complex;
import com.epri.dss.shared.impl.LineUnits;

import com.epri.dss.common.impl.DSSCktElement;
import com.epri.dss.common.impl.DSSClassDefs;
import com.epri.dss.common.impl.DSSClassImpl;
import com.epri.dss.common.impl.DSSGlobals;
import com.epri.dss.common.impl.Utilities;
import com.epri.dss.common.Bus;
import com.epri.dss.common.Circuit;
import com.epri.dss.common.CktElement;
import com.epri.dss.common.FeederObj;
import com.epri.dss.conversion.GeneratorObj;
import com.epri.dss.conversion.LoadObj;
import com.epri.dss.conversion.PCElement;
import com.epri.dss.delivery.LineObj;
import com.epri.dss.delivery.PDElement;
import com.epri.dss.meter.EnergyMeter;
import com.epri.dss.meter.EnergyMeterObj;
import com.epri.dss.shared.CktTree;
import com.epri.dss.shared.CktTreeNode;

public class EnergyMeterObjImpl extends MeterElementImpl implements EnergyMeterObj {

	private static double Delta_Hrs;

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

	protected CktTree BranchList;  // Pointers to all circuit elements in meter"s zone

	protected double[] Registers   = new double[NumEMRegisters];
	protected double[] Derivatives = new double[NumEMRegisters];
	protected double[] TotalsMask  = new double[NumEMRegisters];

	public EnergyMeterObjImpl(DSSClassImpl ParClass, String EnergyMeterName) {
		super(ParClass);

		int i;
		Circuit ckt = DSSGlobals.getInstance().getActiveCircuit();

		setName(EnergyMeterName.toLowerCase());
		this.DSSObjType = ParClass.getDSSClassType();  // ENERGY_METER;

		this.nPhases        = 3;  // Directly set conds and phases
		this.nConds         = 3;
		this.nTerms         = 1;  // this forces allocation of terminals and conductors in base class
		this.ExcessFlag     = true;  // Default to Excess energy FOR UE
		this.ElementName    = "Vsource." + ckt.getCktElements().get(0).getName(); // Default to first circuit element (source)
		this.MeteredElement = null;
		this.BranchList     = null;  // initialize to null, set later when inited

		this.This_Meter_DIFileIsOpen = false;
		this.VPhaseReportFileIsOpen  = false;

		initPropertyValues(0);

		// Max zone kW limits ignored unless the user provides a rating
		this.MaxZonekVA_Norm     = 0.0;
		this.MaxZonekVA_Emerg    = 0.0;

		this.ZoneIsRadial        = true;
		this.HasFeeder           = false;
		this.FeederObj           = null;  // Initialise to not assigned
		this.DefinedZoneList     = null;
		this.DefinedZoneListSize = 0;
		this.Losses             = true;  /* Loss reporting switches */
		this.LineLosses         = true;
		this.XfmrLosses         = true;
		this.SeqLosses          = true;
		this.ThreePhaseLosses   = true;
		this.VBaseLosses        = true;
		this.PhaseVoltageReport = false;
		this.VBaseList          = null;
		this.VBaseTotalLosses   = null;
		this.VBaseLineLosses    = null;
		this.VBaseLoadLosses    = null;
		this.VBaseNoLoadLosses  = null;
		this.VBaseLoad          = null;
		this.VBaseCount         = 0;
		this.MaxVBaseCount      = (EnergyMeter.NumEMRegisters - EnergyMeter.Reg_VBaseStart) / 5;
		this.VBaseList          = new double[this.MaxVBaseCount];
		this.VBaseTotalLosses   = new double[this.MaxVBaseCount];
		this.VBaseLineLosses    = new double[this.MaxVBaseCount];
		this.VBaseLoadLosses    = new double[this.MaxVBaseCount];
		this.VBaseNoLoadLosses  = new double[this.MaxVBaseCount];
		this.VBaseLoad          = new double[this.MaxVBaseCount];

		// Arrays for phase voltage report
		this.VphaseMax   = new double[this.MaxVBaseCount * 3];
		this.VPhaseMin   = new double[this.MaxVBaseCount * 3];
		this.VPhaseAccum = new double[this.MaxVBaseCount * 3];
		this.VPhaseAccumCount = new int[this.MaxVBaseCount * 3];

		LocalOnly     = false;
		VoltageUEOnly = false;

		// Set register names  that correspond to the register quantities
		RegisterNames[0]  = "kWh";
		RegisterNames[1]  = "kvarh";
		RegisterNames[2]  = "Max kW";
		RegisterNames[3]  = "Max kVA";
		RegisterNames[4]  = "Zone kWh";
		RegisterNames[5]  = "Zone kvarh";
		RegisterNames[6]  = "Zone Max kW";
		RegisterNames[7]  = "Zone Max kVA";
		RegisterNames[8]  = "Overload kWh Normal";
		RegisterNames[9]  = "Overload kWh Emerg";
		RegisterNames[10] = "Load EEN";
		RegisterNames[11] = "Load UE";
		RegisterNames[12] = "Zone Losses kWh";
		RegisterNames[13] = "Zone Losses kvarh";
		RegisterNames[14] = "Zone Max kW Losses";
		RegisterNames[15] = "Zone Max kvar Losses";
		RegisterNames[16] = "Load Losses kWh";
		RegisterNames[17] = "Load Losses kvarh";
		RegisterNames[18] = "No Load Losses kWh";
		RegisterNames[19] = "No Load Losses kvarh";
		RegisterNames[20] = "Max kW Load Losses";
		RegisterNames[21] = "Max kW No Load Losses";
		RegisterNames[22] = "Line Losses";
		RegisterNames[23] = "Transformer Losses";

		RegisterNames[24] = "Line Mode Line Losses";
		RegisterNames[25] = "Zero Mode Line Losses";

		RegisterNames[26] = "3-phase Line Losses";
		RegisterNames[27] = "1- and 2-phase Line Losses";

		RegisterNames[28] = "Gen kWh";
		RegisterNames[29] = "Gen kvarh";
		RegisterNames[30] = "Gen Max kW";
		RegisterNames[31] = "Gen Max kVA";

		/* Registers for capturing losses by base voltage, names assigned later */
		for (i = EnergyMeter.Reg_VBaseStart + 1; i < EnergyMeter.NumEMRegisters; i++)
		RegisterNames[i] = "";

		resetRegisters();
		for (i = 0; i < EnergyMeter.NumEMRegisters; i++)
		TotalsMask[i] = 1.0;

		allocateSensorArrays();

		for (i = 0; i < this.nPhases; i++)
		SensorCurrent[i] = 400.0;

		//recalcElementData();
	}

	private static int jiIndex(int i, int j) {
		return (j - 1) * 3 + i;
	}

	@Override
	public void recalcElementData() {
		DSSGlobals Globals = DSSGlobals.getInstance();

		int DevIndex = Utilities.getCktElementIndex(ElementName);   // Global function

		if (DevIndex >= 0) {  // Monitored element must already exist
		MeteredElement = (DSSCktElement) Globals.getActiveCircuit().getCktElements().get(DevIndex); // Get pointer to metered element
		/* MeteredElement must be a PDElement */
		if (!(MeteredElement instanceof PDElement)) {
			MeteredElement = null;  // element not found
			Globals.doErrorMsg("EnergyMeter: \"" + getName() + "\"", "Circuit Element \""+ ElementName + "\" is not a Power Delivery (PD) element.",
				" Element must be a PD element.", 525);
			return;
		}

		if (MeteredTerminal >= MeteredElement.getNTerms()) {  // TODO Check zero based indexing
			Globals.doErrorMsg("EnergyMeter: \"" + getName() + "\"",
				"Terminal no. \"" + String.valueOf(MeteredTerminal)+"\" does not exist.",
				"Respecify terminal no.", 524);
		} else {

			if (MeteredElementChanged) {
				// Sets name of i-th terminal's connected bus in monitor's buslist
				// This value will be used to set the NodeRef array (see TakeSample)
				setBus(1, MeteredElement.getBus(MeteredTerminal));
				nPhases = MeteredElement.getNPhases();
				nConds  = MeteredElement.getNConds();
				allocateSensorArrays();

				// If we come through here, throw branchlist away
				BranchList = null;
			}

			if (HasFeeder) makeFeederObj();  // OK to call multiple times
		}
		} else {
		MeteredElement = null;  // element not found
		Globals.doErrorMsg("EnergyMeter: \"" + getName() + "\"", "Circuit Element \""+ ElementName + "\" Not Found.",
				" Element must be defined previously.", 525);
		}
	}

	/**
	 * Make a positive sequence model.
	 */
	@Override
	public void makePosSequence() {

		if (MeteredElement != null) {
		setBus(1, MeteredElement.getBus(MeteredTerminal));
		nPhases = MeteredElement.getNPhases();
		nConds = MeteredElement.getNConds();
		allocateSensorArrays();
		BranchList = null;
		}

		if (HasFeeder) makeFeederObj();

		super.makePosSequence();
	}

	private String makeVPhaseReportFileName() {
		return DSSGlobals.getInstance().getEnergyMeterClass().getDI_Dir() + "/" + getName() + "_PhaseVoltageReport.csv";
	}

	public void resetRegisters() {
		int i;

		for (i = 0; i < EnergyMeter.NumEMRegisters; i++)
		Registers[i] = 0.0;
		for (i = 0; i < EnergyMeter.NumEMRegisters; i++)
		Derivatives[i] = 0.0;

		/* Initialise drag hand registers to some big negative number */
		Registers[EnergyMeter.Reg_MaxkW]           = -1.0e50;
		Registers[EnergyMeter.Reg_MaxkVA]          = -1.0e50;
		Registers[EnergyMeter.Reg_ZoneMaxkW]       = -1.0e50;
		Registers[EnergyMeter.Reg_ZoneMaxkVA]      = -1.0e50;
		Registers[EnergyMeter.Reg_MaxLoadLosses]   = -1.0e50;
		Registers[EnergyMeter.Reg_MaxNoLoadLosses] = -1.0e50;
		Registers[EnergyMeter.Reg_LossesMaxkW]     = -1.0e50;
		Registers[EnergyMeter.Reg_LossesMaxkvar]   = -1.0e50;

		Registers[EnergyMeter.Reg_GenMaxkW]        = -1.0e50;
		Registers[EnergyMeter.Reg_GenMaxkVA]       = -1.0e50;

		FirstSampleAfterReset = true;  // initialise for trapezoidal integration
		// Removed .. open in solution loop See Solve Yearly if (EnergyMeterClass.SaveDemandInterval) openDemandIntervalFile();
	}

	@Override
	public void calcYPrim() {
		// YPrim is all zeros. Just leave as nil so it is ignored.
	}

	public void saveRegisters() {
		File F;
		FileWriter FStream;
		BufferedWriter FBuffer;

		DSSGlobals Globals = DSSGlobals.getInstance();

		String CSVName = "MTR_" + getName() + ".csv";

		try {
			F = new File(Globals.getDSSDataDirectory() + CSVName);
			FStream = new FileWriter(F, false);
			FBuffer = new BufferedWriter(FStream);

			Globals.setGlobalResult(CSVName);

			FBuffer.write("Year, " + Globals.getActiveCircuit().getSolution().getYear() + ",");
			FBuffer.newLine();

			for (int i = 0; i < EnergyMeter.NumEMRegisters; i++) {
				FBuffer.write("\"" + RegisterNames[i] + "\"," + Registers[i]);
				FBuffer.newLine();
			}

			FBuffer.close();
			FStream.close();
		} catch (Exception e) {
			Globals.doSimpleMsg("Error opening Meter File \"" + DSSGlobals.CRLF + CSVName + "\": " + e.getMessage(), 526);
			return;
		}
	}

	private void integrate(int Reg, double Deriv, double Interval) {
		Circuit ckt = DSSGlobals.getInstance().getActiveCircuit();

		if (ckt.isTrapezoidalIntegration()) {
		/* Trapezoidal Rule Integration */
		if (!FirstSampleAfterReset)
			Registers[Reg] = Registers[Reg] + 0.5 * Interval * (Deriv + Derivatives[Reg]);
		} else {
		/* Plain Euler integration */
		Registers[Reg] = Registers[Reg] + Interval * Deriv;
		}

		/* Set the derivatives so that the proper value shows up in Demand Interval Files
		 * and prepare for next time step in Trapezoidal integration.
		 */
		Derivatives[Reg] = Deriv;
	}

	/**
	 * Update registers from metered zone.
	 * Assumes one time period has taken place since last sample.
	 */
	@Override
	public void takeSample() {
		int i, j;

		Complex S_Local,
		S_Totallosses = null,
		S_LoadLosses = null,
		S_NoLoadLosses = null,
		TotalLoadLosses,
		TotalNoLoadLosses,
		TotalLineLosses,
		TotalTransformerLosses,
		TotalLineModeLosses,    // Lines only  for now
		TotalZeroModeLosses,
		Total3phaseLosses,
		Total1phaseLosses,
		TotalLosses;

		PDElement CktElem, ParenElem;
		PCElement PCelem;
		LoadObj pLoad;
		GeneratorObj pGen;

		double MaxExcesskWNorm,
		MaxExcesskWEmerg,
		EEN = 0,
		UE = 0,
		ZonekW,
		TotalZonekw,
		TotalZonekvar,
		TotalLoad_EEN,
		TotalLoad_UE,
		TotalGenkW,
		TotalGenkVAr,
		LoadkVA,
		GenkVA,
		S_Local_kVA,
		Load_kW;
		Complex S_PosSeqLosses = null;
		Complex S_ZeroSeqLosses = null;
		Complex S_NegSeqLosses = null;

		double puV;

		DSSGlobals Globals = DSSGlobals.getInstance();

		// Compute energy in branch to which meter is connected

		//MeteredElement.setActiveTerminalIdx(MeteredTerminal);  // needed for Excess kVA calcs
		S_Local     = MeteredElement.getPower(MeteredTerminal).multiply(0.001);
		S_Local_kVA = S_Local.abs();
		Delta_Hrs   = Globals.getActiveCircuit().getSolution().getIntervalHrs();
		integrate(EnergyMeter.Reg_kWh,   S_Local.getReal(), Delta_Hrs);  // Accumulate the power
		integrate(EnergyMeter.Reg_kvarh, S_Local.getImaginary(), Delta_Hrs);
		setDragHandRegister(EnergyMeter.Reg_MaxkW,  S_Local.getReal());   // 3-10-04 removed abs()
		setDragHandRegister(EnergyMeter.Reg_MaxkVA, S_Local_kVA);

		// Compute maximum overload energy in all branches in zone
		// and mark all load downline from an overloaded branch as unserved
		// If localonly, check only metered element

		TotalLosses         = Complex.ZERO;     // Initialize loss accumulators
		TotalLoadLosses     = Complex.ZERO;
		TotalNoLoadLosses   = Complex.ZERO;
		TotalLineLosses     = Complex.ZERO;
		TotalLineModeLosses = Complex.ZERO;
		TotalZeroModeLosses = Complex.ZERO;
		Total3phaseLosses   = Complex.ZERO;
		Total1phaseLosses   = Complex.ZERO;
		TotalTransformerLosses   = Complex.ZERO;

		// Init all voltage base loss accumulators
		for (i = 0; i < MaxVBaseCount; i++) {
			VBaseTotalLosses[i]  = 0.0;
			VBaseLineLosses[i]   = 0.0;
			VBaseLoadLosses[i]   = 0.0;
			VBaseNoLoadLosses[i] = 0.0;
			VBaseLoad[i]         = 0.0;
		}

		// Phase voltage arrays
		if (PhaseVoltageReport) {
			for (i = 0; i < MaxVBaseCount; i++) {
				if (VBaseList[i] > 0.0) {
					for (j = 0; j < 3; j++) {
						VphaseMax[jiIndex(j, i)] = 0.0;
						VPhaseMin[jiIndex(j, i)] = 9999.0;
						VPhaseAccum[jiIndex(j, i)] = 0.0;
						VPhaseAccumCount[jiIndex(j, i)] = 0;  // Keep track of counts for average
					}
				}
			}
		}

		CktElem           = (PDElement) BranchList.getFirst();
		MaxExcesskWNorm   = 0.0;
		MaxExcesskWEmerg  = 0.0;

		/* -------------------------------------------------------------------------- */
		/* ------------------------ Local Zone Only --------------------------------- */
		/* -------------------------------------------------------------------------- */
		if (LocalOnly) {
			CktElem = (PDElement) MeteredElement;
			MaxExcesskWNorm  = Math.abs(CktElem.getExcessKVANorm(MeteredTerminal).getReal());
			MaxExcesskWEmerg = Math.abs(CktElem.getExcessKVAEmerg(MeteredTerminal).getReal());
		} else {
			/* -------------------------------------------------------------------------- */
			/* --------Cyle Through Entire Zone Setting EEN/UE -------------------------- */
			/* -------------------------------------------------------------------------- */
			while (CktElem != null) {
				// loop thru all ckt elements on zone

				CktElem.setActiveTerminalIdx( BranchList.getPresentBranch().getFromTerminal() );
				// Invoking this property sets the Overload_UE flag in the PD Element
				EEN = Math.abs(CktElem.getExcessKVANorm(CktElem.getActiveTerminalIdx()).getReal());
				UE  = Math.abs(CktElem.getExcessKVAEmerg(CktElem.getActiveTerminalIdx()).getReal());
			}

			/* For radial circuits just keep the maximum overload; for mesh, add 'em up */
			if (ZoneIsRadial) {
				if (UE  > MaxExcesskWEmerg) MaxExcesskWEmerg = UE;
				if (EEN > MaxExcesskWNorm)  MaxExcesskWNorm  = EEN;
			} else {
				MaxExcesskWEmerg = MaxExcesskWEmerg + UE;
				MaxExcesskWNorm  = MaxExcesskWNorm  + EEN;
			}

			// Even if this branch is not overloaded, if the parent element is overloaded
			// mark load on this branch as unserved also
			// Use the larger of the two factors
			ParenElem = (PDElement) BranchList.getParent();
			if (ParenElem != null) {
				CktElem.setOverLoad_EEN( Math.max(CktElem.getOverLoad_EEN(), ParenElem.getOverLoad_EEN()) );
				CktElem.setOverload_UE( Math.max(CktElem.getOverload_UE(), ParenElem.getOverload_UE()) );
			}

			// Mark loads (not generators) by the degree of overload if the meter's zone is to be considered radial
			// This overrides and supercedes the load's own determination of unserved based on voltage
			// If voltage only is to be used for Load UE/EEN, don't mark (set to 0.0 and load will calc UE based on voltage)
			PCelem = (PCElement) BranchList.getFirstObject();
			while (PCelem != null) {
				if ((PCelem.getDSSObjType() & DSSClassDefs.CLASSMASK) == DSSClassDefs.LOAD_ELEMENT) {
					pLoad = (LoadObj) PCelem;
					if ((CktElem.getOverLoad_EEN() > 0.0) && (ZoneIsRadial) && !VoltageUEOnly) {
						pLoad.setEEN_Factor(CktElem.getOverLoad_EEN());
					} else {
						pLoad.setEEN_Factor(0.0);
					}

					if ((CktElem.getOverload_UE() > 0.0) && ZoneIsRadial && !VoltageUEOnly) {
						pLoad.setUE_Factor(CktElem.getOverload_UE());
					} else {
						pLoad.setUE_Factor(0.0);
					}
				}
				PCelem = (PCElement) BranchList.getNextObject();
			}

			CktElem = (PDElement) BranchList.GoForward();
		}

		// Get the losses, and unserved bus energies
		TotalZonekw   = 0.0;
		TotalZonekvar = 0.0;
		TotalLoad_EEN = 0.0;
		TotalLoad_UE  = 0.0;
		TotalGenkW    = 0.0;
		TotalGenkVAr  = 0.0;

		/* -------------------------------------------------------------------------- */
		/* --------       Cycle Through Zone Accumulating Load and Losses    -------- */
		/* -------------------------------------------------------------------------- */
		CktElem = (PDElement) BranchList.getFirst();
		while (CktElem != null) {
			PCelem = (PCElement) BranchList.getFirstObject();
			while (PCelem != null) {
				switch (PCelem.getDSSObjType() & DSSClassDefs.CLASSMASK) {
				case DSSClassDefs.LOAD_ELEMENT:
					if (!LocalOnly) {  // Dont check for load EEN/UE if Local only
						pLoad = (LoadObj) PCelem;
						Load_kW = accumulateLoad(pLoad, TotalZonekw, TotalZonekvar, TotalLoad_EEN, TotalLoad_UE);
						if (VBaseLosses) {
							int vbi = BranchList.getPresentBranch().getVoltBaseIndex();
							if (vbi > 0)
								VBaseLoad[vbi] = VBaseLoad[vbi] + Load_kW;
						}
					}
				case DSSClassDefs.GEN_ELEMENT:
					pGen = (GeneratorObj) PCelem;
					accumulateGen(pGen, TotalGenkW, TotalGenkVAr);
				}
				PCelem = (PCElement) BranchList.getNextObject();
			}

			if (Losses) {  // Compute and report losses

				/* Get losses from the present circuit element */
				CktElem.getLosses(S_Totallosses, S_LoadLosses, S_NoLoadLosses);  // returns watts, vars
				/* Convert to kW */
				S_Totallosses = S_Totallosses.multiply(0.001);
				S_LoadLosses = S_LoadLosses.multiply(0.001);
				S_NoLoadLosses = S_NoLoadLosses.multiply(0.001);
				/* Update accumulators */
				TotalLosses = TotalLosses.add(S_Totallosses);  // Accumulate total losses in meter zone
				TotalLoadLosses = TotalLoadLosses.add(S_LoadLosses);  // Accumulate total load losses in meter zone
				TotalNoLoadLosses = TotalNoLoadLosses.add(S_NoLoadLosses);  // Accumulate total no load losses in meter zone

				/* Line and Transformer Elements */
				if (Utilities.isLineElement(CktElem) && LineLosses) {
					TotalLineLosses = TotalLineLosses.add(S_Totallosses);  // Accumulate total losses in meter zone
					if (SeqLosses) {
						CktElem.getSeqLosses(S_PosSeqLosses, S_NegSeqLosses, S_ZeroSeqLosses);
						S_PosSeqLosses = S_PosSeqLosses.add(S_NegSeqLosses);  // add line modes together
						S_PosSeqLosses = S_PosSeqLosses.multiply(0.001);  // convert to kW
						S_ZeroSeqLosses = S_ZeroSeqLosses.multiply(0.001);
						TotalLineModeLosses = TotalLineModeLosses.add(S_PosSeqLosses);
						TotalZeroModeLosses = TotalZeroModeLosses.add(S_ZeroSeqLosses);
					}
					/* Separate Line losses into 3- and "1-phase" losses */
					if (ThreePhaseLosses) {
						if (CktElem.getNPhases() == 3) {
							Total3phaseLosses = Total3phaseLosses.add(S_Totallosses);
						} else {
							Total1phaseLosses = Total1phaseLosses.add(S_Totallosses);
						}
					}
				} else if (Utilities.isTransformerElement(CktElem) && XfmrLosses) {
					TotalTransformerLosses = TotalTransformerLosses.add(S_Totallosses); // Accumulate total losses in meter zone
				}

				if (VBaseLosses) {
					int vbi = BranchList.getPresentBranch().getVoltBaseIndex();
					if (vbi >= 0) {
						VBaseTotalLosses[vbi] = VBaseTotalLosses[vbi]  + S_Totallosses.getReal();
						if (Utilities.isLineElement(CktElem)) {
							VBaseLineLosses[vbi] = VBaseLineLosses[vbi] + S_Totallosses.getReal();
						} else if (Utilities.isTransformerElement(CktElem)) {
							VBaseLoadLosses[vbi]   = VBaseLoadLosses[vbi]   + S_LoadLosses.getReal();
							VBaseNoLoadLosses[vbi] = VBaseNoLoadLosses[vbi] + S_NoLoadLosses.getReal();
						}
					}
				}

				// Compute min, max, and average pu voltages for 1st 3 phases  (nodes designated 1, 2, or 3)
				if (PhaseVoltageReport) {
					int vbi = BranchList.getPresentBranch().getVoltBaseIndex();
					int fbr = BranchList.getPresentBranch().getFromBusReference();
					if (vbi >= 0) {
						Circuit ckt = Globals.getActiveCircuit();
						if (ckt.getBuses()[fbr].getkVBase() > 0.0) {
							for (i = 0; i < ckt.getBuses()[fbr].getNumNodesThisBus(); i++) {
								j = ckt.getBuses()[fbr].getNum(i);
								if ((j >= 0) && (j < 3)) {  // TODO Check zero based indexing
									puV = ckt.getSolution().getNodeV()[ ckt.getBuses()[fbr].getRef(i) ].abs() / ckt.getBuses()[fbr].getkVBase();
									VphaseMax[jiIndex(j, vbi)] = Math.max(VphaseMax[jiIndex(j, vbi)], puV);
									VPhaseMin[jiIndex(j, vbi)] = Math.min(VPhaseMin[jiIndex(j, vbi)], puV);
									VPhaseAccum[jiIndex(j, vbi)] = VPhaseAccum[jiIndex(j, vbi)] + puV;
									VPhaseAccumCount[jiIndex(j, vbi)] += 1;  // Keep track of counts for average
								}
							}
						}
					}
				}
			}  // if (Losses)

			CktElem = (PDElement) BranchList.GoForward();
		}

		/* NOTE: integrate proc automatically sets derivatives array */
		integrate(EnergyMeter.Reg_LoadEEN, TotalLoad_EEN, Delta_Hrs);
		integrate(EnergyMeter.Reg_LoadUE , TotalLoad_UE,  Delta_Hrs);

		/* Accumulate losses in appropriate registers */
		integrate(EnergyMeter.Reg_ZoneLosseskWh,     TotalLosses.getReal(),          Delta_Hrs);
		integrate(EnergyMeter.Reg_ZoneLosseskvarh,   TotalLosses.getImaginary(),     Delta_Hrs);
		integrate(EnergyMeter.Reg_LoadLosseskWh,     TotalLoadLosses.getReal(),      Delta_Hrs);
		integrate(EnergyMeter.Reg_LoadLosseskvarh,   TotalLoadLosses.getImaginary(), Delta_Hrs);
		integrate(EnergyMeter.Reg_NoLoadLosseskWh,   TotalNoLoadLosses.getReal(),    Delta_Hrs);
		integrate(EnergyMeter.Reg_NoLoadLosseskvarh, TotalNoLoadLosses.getImaginary(),    Delta_Hrs);
		integrate(EnergyMeter.Reg_LineLosseskWh,     TotalLineLosses.getReal(),      Delta_Hrs);
		integrate(EnergyMeter.Reg_LineModeLineLoss,  TotalLineModeLosses.getReal(),  Delta_Hrs);
		integrate(EnergyMeter.Reg_ZeroModeLineLoss,  TotalZeroModeLosses.getReal(),  Delta_Hrs);
		integrate(EnergyMeter.Reg_3_phaseLineLoss,   Total3phaseLosses.getReal(),    Delta_Hrs);
		integrate(EnergyMeter.Reg_1_phaseLineLoss,   Total1phaseLosses.getReal(),    Delta_Hrs);
		integrate(EnergyMeter.Reg_TransformerLosseskWh,  TotalTransformerLosses.getReal(),  Delta_Hrs);

		for (i = 0; i < MaxVBaseCount; i++) {
			integrate(EnergyMeter.Reg_VBaseStart + i, VBaseTotalLosses[i],  Delta_Hrs);
			integrate(EnergyMeter.Reg_VBaseStart + 1 * MaxVBaseCount + i, VBaseLineLosses[i],    Delta_Hrs);
			integrate(EnergyMeter.Reg_VBaseStart + 2 * MaxVBaseCount + i, VBaseLoadLosses[i],    Delta_Hrs);
			integrate(EnergyMeter.Reg_VBaseStart + 3 * MaxVBaseCount + i, VBaseNoLoadLosses[i],  Delta_Hrs);
			integrate(EnergyMeter.Reg_VBaseStart + 4 * MaxVBaseCount + i, VBaseLoad[i],          Delta_Hrs);
		}


		/* -------------------------------------------------------------------------- */
		/* ---------------   Total Zone Load and Generation ------------------------- */
		/* -------------------------------------------------------------------------- */

		integrate(EnergyMeter.Reg_ZonekWh,   TotalZonekw,   Delta_Hrs);
		integrate(EnergyMeter.Reg_Zonekvarh, TotalZonekvar, Delta_Hrs);
		integrate(EnergyMeter.Reg_GenkWh,    TotalGenkW,    Delta_Hrs);
		integrate(EnergyMeter.Reg_Genkvarh,  TotalGenkVAr,  Delta_Hrs);
		GenkVA  = Math.sqrt(Math.pow(TotalGenkVAr, 2)  + Math.pow(TotalGenkW, 2));
		LoadkVA = Math.sqrt(Math.pow(TotalZonekvar, 2) + Math.pow(TotalZonekw, 2));

		/* -------------------------------------------------------------------------- */
		/* ---------------   Set Drag Hand Registers  ------------------------------- */
		/* -------------------------------------------------------------------------- */

		setDragHandRegister(EnergyMeter.Reg_LossesMaxkW,     Math.abs(TotalLosses.getReal()));
		setDragHandRegister(EnergyMeter.Reg_LossesMaxkvar,   Math.abs(TotalLosses.getImaginary()));
		setDragHandRegister(EnergyMeter.Reg_MaxLoadLosses,   Math.abs(TotalLoadLosses.getReal()));
		setDragHandRegister(EnergyMeter.Reg_MaxNoLoadLosses, Math.abs(TotalNoLoadLosses.getReal()));
		setDragHandRegister(EnergyMeter.Reg_ZoneMaxkW,       TotalZonekw);  // Removed abs()  3-10-04
		setDragHandRegister(EnergyMeter.Reg_ZoneMaxkVA,      LoadkVA);
		/* Max total generator registers */
		setDragHandRegister(EnergyMeter.Reg_GenMaxkW,        TotalGenkW); // Removed abs()  3-10-04
		setDragHandRegister(EnergyMeter.Reg_GenMaxkVA,       GenkVA);

		/* -------------------------------------------------------------------------- */
		/* ---------------------   Overload Energy  --------------------------------- */
		/* -------------------------------------------------------------------------- */
		/* Overload energy for the entire zone */
		if (LocalOnly) {
			ZonekW = S_Local.getReal();
		} else {
			ZonekW = TotalZonekw;
		}

		/* Either the max excess kW of any PD element or the excess over zone limits */

		/* regs 9 and 10 */
		/* Fixed these formulas 2-7-07 per discussions with Daniel Brooks */
		if (MaxZonekVA_Norm > 0.0) {
			if (S_Local_kVA == 0.0) S_Local_kVA = MaxZonekVA_Norm;
			integrate(EnergyMeter.Reg_OverloadkWhNorm, Math.max(0.0, (ZonekW * (1.0 - MaxZonekVA_Norm / S_Local_kVA))), Delta_Hrs);
		} else {
			integrate(EnergyMeter.Reg_OverloadkWhNorm, MaxExcesskWNorm, Delta_Hrs);
		}

		if (MaxZonekVA_Emerg > 0.0) {
			if (S_Local_kVA == 0.0) S_Local_kVA = MaxZonekVA_Emerg;
			integrate(EnergyMeter.Reg_OverloadkWhEmerg, Math.max(0.0, (ZonekW * (1.0 - MaxZonekVA_Emerg / S_Local_kVA))), Delta_Hrs);
		} else {
			integrate(EnergyMeter.Reg_OverloadkWhEmerg, MaxExcesskWEmerg,  Delta_Hrs);
		}

		FirstSampleAfterReset = false;
		if (Globals.getEnergyMeterClass().isSaveDemandInterval())
			writeDemandIntervalData();
	}

	private void totalUpDownstreamCustomers() {
		int i, Accumulator;
		CktTreeNode PresentNode = null;
		PDElement CktElem;

		if (BranchList == null) {
			DSSGlobals.getInstance().doSimpleMsg("Meter Zone Lists need to be built. Do Solve or makeBusList first!", 529);
			return;
		}

		/* Init totsls and checked flag */
		CktElem = (PDElement) BranchList.getFirst();
		while (CktElem != null) {
			CktElem.setChecked(false);
			CktElem.setTotalCustomers(0);
			CktElem = (PDElement) BranchList.GoForward();
		}

		/* This algorithm could be made more efficient with a sequence list */

		for (i = 0; i < BranchList.getZoneEndsList().getNumEnds(); i++) {
			/*Busref = */BranchList.getZoneEndsList().Get(i, PresentNode);
			if (PresentNode != null) {
				CktElem = (PDElement) PresentNode.getCktObject();
				if (!CktElem.isChecked()) {  // don't do a zone end element more than once
					CktElem.setChecked(true);
					Accumulator = CktElem.getNumCustomers();
					while (true) {  /* Go back to the source */
						CktElem.setTotalCustomers( CktElem.getTotalCustomers() + Accumulator );
						PresentNode = PresentNode.getParent();
						if (PresentNode == null) break;
						CktElem = (PDElement) PresentNode.getCktObject();
						Accumulator = Accumulator + CktElem.getNumCustomers();
					}
				}
			}
		}
	}

	/**
	 * This gets fired off whenever the buslists are rebuilt.
	 * Must be updated whenever there is a change in the circuit.
	 */
	public void makeMeterZoneLists() {
		DSSGlobals Globals = DSSGlobals.getInstance();
		Circuit ckt = Globals.getActiveCircuit();

		int TestBusNum, ZoneListCounter;
		int j, iTerm, iPC, iPD;
		DSSCktElement ActiveBranch;
		PDElement TestElement;
		PCElement pC;
		LoadObj pLoad;
		boolean IsFeederEnd;
		String S;
		List<String> adjLst;

		ZoneListCounter = 0;
		VBaseCount      = 0;  /* Build the voltage base list over in case a base added or deleted */
		for (j = 0; j < MaxVBaseCount; j++)
			VBaseList[j] = 0.0;


		BranchList = new CktTreeImpl();  /* Instantiates ZoneEndsList, too */

		// Get Started
		if (MeteredElement != null) {
			BranchList.setNew(MeteredElement);
		} else {
			Globals.doSimpleMsg("Metered Element for EnergyMeter "+getName()+" not defined.", 527);
			return;
		}

		/* Initialize SensorObj property of the first branch to this MeterElement object.
		 * Before starting, all sensorObj definitions are cleared in PCElements and PDElements. The
		 * SensorObj property is passed down to the Load objects for LoadAllocation and state estimation.
		 */
		if (MeteredElement instanceof PDElement) {
			((PDElement) MeteredElement).setSensorObj(this);
		} else if (MeteredElement instanceof PCElement) {
			((PCElement) MeteredElement).setSensorObj(this);
		}

		if (MeteredElement instanceof PDElement) {
			((PDElement) MeteredElement).setMeterObj(this);
		} else if (MeteredElement instanceof PCElement) {
			((PCElement) MeteredElement).setMeterObj(this);
		}

		MeteredElement.getTerminals()[MeteredTerminal].setChecked(true);
		CktTreeNode pb = BranchList.getPresentBranch();
		// This bus is the head of the feeder; do not mark as radial bus
		pb.setFromBusReference( MeteredElement.getTerminals()[MeteredTerminal].getBusRef() );
		Globals.getActiveCircuit().getBuses()[pb.getFromBusReference()].setDistFromMeter(0.0);
		pb.setVoltBaseIndex( addToVoltBaseList(pb.getFromBusReference()) );
		pb.setFromTerminal(MeteredTerminal);
		if (MeteredElement instanceof PDElement)
			((PDElement) MeteredElement).setFromTerminal(MeteredTerminal);

		// Check off this element so we don't use it again
		MeteredElement.setChecked(true);
		MeteredElement.setIsIsolated(false);

		// Now start looking for other branches
		// Finds any branch connected to the TestBranch and adds it to the list
		// Goes until end of circuit, another energy meter, an open terminal, or disabled device.
		ActiveBranch = MeteredElement;
		while (ActiveBranch != null) {
			pb = BranchList.getPresentBranch();
			pb.setIsLoopedHere(false);
			pb.setIsParallel(false);
			pb.setIsDangling(true);  // Unless we find something connected to it
			pb.setVoltBaseIndex( addToVoltBaseList(pb.getFromBusReference()) );

			((PDElement) ActiveBranch).setNumCustomers(0);  // Init counter

			for (iTerm = 0; iTerm < ActiveBranch.getNTerms(); iTerm++) {
				if (!ActiveBranch.getTerminals()[iTerm].isChecked()) {
					// Now find all loads and generators connected to the bus on this end of branch
					// attach them as generic objects to cktTree node.
					TestBusNum = ActiveBranch.getTerminals()[iTerm].getBusRef();
					BranchList.getPresentBranch().setToBusReference(TestBusNum);  // Add this as a "to" bus reference
					if (Utilities.isLineElement(ActiveBranch)) {  // Convert to consistent units (km)
						ckt.getBuses()[TestBusNum].setDistFromMeter( ckt.getBuses()[ BranchList.getPresentBranch().getFromBusReference() ].getDistFromMeter()
								+ ((LineObj) ActiveBranch).getLen() * LineUnits.convertLineUnits( ((LineObj) ActiveBranch).getLengthUnits(), LineUnits.UNITS_KM) );
					} else {
						ckt.getBuses()[TestBusNum].setDistFromMeter( ckt.getBuses()[BranchList.getPresentBranch().getFromBusReference()].getDistFromMeter() );
					}

					adjLst = EnergyMeterImpl.BusAdjPC[TestBusNum];
					for (iPC = 0; iPC < adjLst.size(); iPC++) {
						pC = adjLst[iPC];
						if (pC.isChecked()) continue;  // skip ones we already checked
						BranchList.getPresentBranch().setIsDangling(false);  // Something is connected here
						// Is this a load or a generator or a Capacitor or reactor?
						if (((pC.getDSSObjType() & DSSClassDefs.CLASSMASK) == DSSClassDefs.LOAD_ELEMENT)
								|| ((pC.getDSSObjType() & DSSClassDefs.CLASSMASK) == DSSClassDefs.GEN_ELEMENT)
								|| ((pC.getDSSObjType() & DSSClassDefs.CLASSMASK) == DSSClassDefs.CAP_ELEMENT)
								|| ((pC.getDSSObjType() & DSSClassDefs.CLASSMASK) == DSSClassDefs.REACTOR_ELEMENT)) {

							BranchList.setNewObject(pC);
							pC.setChecked(true);  // So we don't pick this element up again
							pC.setIsIsolated(false);
							pC.setActiveTerminalIdx(1);  // TODO Check zero based indexing
							/* Totalize number of customers if load type */
							if (pC instanceof LoadObj) {
								pLoad = (LoadObj) pC;
								((PDElement) ActiveBranch).setNumCustomers( ((PDElement) ActiveBranch).getNumCustomers() + pLoad.getNumCustomers() );
							}
							/* If object does not have a sensor attached, it acquires the sensor of its parent branch */
							if (!pC.hasSensorObj())
								pC.setSensorObj( ((PDElement) ActiveBranch).getSensorObj() );
							pC.setMeterObj(this);
						}
					}

					// Now find all branches connected to this bus that we havent found already
					// Do not include in this zone if branch has open terminals or has another meter

					if (DefinedZoneListSize == 0) {  // Search tree for connected branches (default)
						IsFeederEnd = true;
						adjLst = EnergyMeterImpl.BusAdjPD[TestBusNum];
						for (iPD = 0; iPD < adjLst.size(); iPD++) {
							TestElement = adjLst[iPD];  // Only enabled objects are in this list
							// See resetMeterZonesAll()
							if (!(TestElement == ActiveBranch)) {  // Skip self
								if (!TestElement.hasEnergyMeter()) {  // Stop at other meters  so zones don't interfere
									for (j = 0; j < TestElement.getNTerms(); j++) {  // Check each terminal
										if (TestBusNum == TestElement.getTerminals()[j].getBusRef()) {
											BranchList.getPresentBranch().setIsDangling(false);  // We found something it was connected to
											/* Check for loops and parallel branches and mark them */
											if (TestElement.isChecked()) {  /* This branch is on some meter's list already */
												BranchList.getPresentBranch().setIsLoopedHere(true);  /* It's a loop */
												BranchList.getPresentBranch().setLoopLineObj(TestElement);
												if (Utilities.isLineElement(ActiveBranch) && Utilities.isLineElement(TestElement))
													if (Utilities.checkParallel(ActiveBranch, TestElement))
														BranchList.getPresentBranch().setIsParallel(true);  /* It's paralleled with another line */
											} else {  // Push testElement onto stack and set properties
												IsFeederEnd = false;  // for interpolation
												BranchList.addNewChild(TestElement, TestBusNum, j);  // Add new child to the branchlist
												TestElement.getTerminals()[j].setChecked(true);
												TestElement.setFromTerminal(j);
												TestElement.setChecked(true);
												TestElement.setIsIsolated(false);
												/* Branch inherits sensor of upline branch if it doesn't have its own */
												if (!HasSensorObj)
													TestElement.setSensorObj( ((PDElement) ActiveBranch).getSensorObj() );
												TestElement.setMeterObj(this);   // Set meterobj to this meter
												TestElement.setParentPDElement( (PDElement) ActiveBranch );  // record the parent so we can easily back up for reconductoring, etc.
												break;
											}
										}  /* if TestBusNum */
									}  /* for terminals */
								}
							}
						}  /* for iPD */
						if (IsFeederEnd)
							BranchList.getZoneEndsList().Add(BranchList.getPresentBranch(), TestBusNum);
						/* This is an end of the feeder and testbusnum is the end bus */
					} else {  // Zone is manually specified; Just add next element in list as a child
						ZoneListCounter += 1;
						while (ZoneListCounter <= DefinedZoneListSize) {
							if (ckt.setElementActive(DefinedZoneList[ZoneListCounter]) == 0) {
								ZoneListCounter += 1;  // Not found. Let's search for another
							} else {
								TestElement = (PDElement) ckt.getActiveCktElement();
								if (!TestElement.isEnabled()) {
									ZoneListCounter += 1;  // Lets ignore disabled devices
								} else {
									if ((TestElement.getDSSObjType() & DSSClassDefs.BASECLASSMASK) != DSSClassDefs.PD_ELEMENT) {
										ZoneListCounter += 1;  // Lets ignore non-PD elements
									} else {
										BranchList.addNewChild(TestElement, 0, 0);  // Add it as a child to the previous element
									}
									break;  // Can't do reductions if manually spec'd
								}
							}
						}  // while
					}
				}
			}  /* for iTerm */
			ActiveBranch = (DSSCktElement) BranchList.GoForward();  // Sets present node
		}

		totalUpDownstreamCustomers();

		if (HasFeeder)
			FeederObj.initializeFeeder(BranchList);   // Synchronise the feeder definition

		assignVoltBaseRegisterNames();
	}

	@Override
	public void getCurrents(Complex[] Curr) {
		for (int i = 0; i < nConds; i++)
			Curr[i] = Complex.ZERO;
	}

	@Override
	public void getInjCurrents(Complex[] Curr) {
		for (int i = 0; i < nConds; i++)
			Curr[i] = Complex.ZERO;
	}

	public void zoneDump() {
		File F;
		FileWriter FStream;
		BufferedWriter FBuffer;
		PDElement PDElem;
		CktElement LoadElem;

		DSSGlobals Globals = DSSGlobals.getInstance();
		Circuit ckt = Globals.getActiveCircuit();

		String CSVName = "Zone_" + getName() + ".csv";

		try {
			F = new File(Globals.getDSSDataDirectory(), CSVName);
			FStream = new FileWriter(F, false);
			FBuffer = new BufferedWriter(FStream);

			Globals.setGlobalResult(CSVName);

			FBuffer.write("Level, Branch, Bus1, Bus2, Distance");
			FBuffer.newLine();

			if (BranchList != null) {
				PDElem = (PDElement) BranchList.getFirst();
				while (PDElem != null) {
					FBuffer.write(String.format("%d, %s.%s, %s, %s, %10.4f",
							BranchList.getLevel(), PDElem.getParentClass().getName(), PDElem.getName(),
							PDElem.getFirstBus(), PDElem.getNextBus(),
							/*BusList.get(BranchList.getPresentBranch().getToBusReference()),*/
							ckt.getBuses()[BranchList.getPresentBranch().getToBusReference()].getDistFromMeter()));
					FBuffer.newLine();
					LoadElem = (CktElement) BranchList.getFirstObject();
					while (LoadElem != null) {
						FBuffer.write("-1, " + String.format("%s.%s, %s", LoadElem.getParentClass().getName(), LoadElem.getName(), LoadElem.getFirstBus()/*ckt.getBusList().get(BranchList.getPresentBranch().getToBusReference())*/));
						FBuffer.newLine();
						LoadElem = (CktElement) BranchList.getNextObject();
					}
					PDElem = (PDElement) BranchList.GoForward();
				}
			}

			FBuffer.close();
			FStream.close();
		} catch (Exception e) {
			Globals.doSimpleMsg("Error opening File \"" + CSVName + "\": " + e.getMessage(), 528);
		}
	}

	@Override
	public void dumpProperties(PrintStream F, boolean Complete) {
		int i;
		PDElement PDElem;
		CktElement LoadElem;

		super.dumpProperties(F, Complete);

		for (i = 0; i < getParentClass().getNumProperties(); i++) {
			switch (i) {
			case 3:  // option
				F.print("~ " + getParentClass().getPropertyName()[i] + "=(");
				if (ExcessFlag) {
					F.print("E,");
				} else {
					F.print("T,");
				}
				if (ZoneIsRadial) {
					F.print(" R,");
				} else {
					F.print(" M,");
				}
				if (VoltageUEOnly) {
					F.print(" V");
				} else {
					F.print(" C");
				}
				F.println(")");
			case 6:
				F.println("~ " + getParentClass().getPropertyName()[i] + "=(" + getPropertyValue(i) + ")");
			default:
				F.println("~ " + getParentClass().getPropertyName()[i] + "=" + getPropertyValue(i));
			}
		}

		if (Complete) {
			F.println("Registers");
			for (i = 0; i < EnergyMeter.NumEMRegisters; i++)
				F.println("\"" + RegisterNames[i] + "\" = " + Registers[i]);
			F.println();

			F.println("Branch List:");
			if (BranchList != null) {
				PDElem = (PDElement) BranchList.getFirst();
				while (PDElem != null) {
					F.println("Circuit Element = " + PDElem.getName());
					LoadElem = (CktElement) BranchList.getFirstObject();
					while (LoadElem != null) {
						F.println("   Shunt Element = " + LoadElem.getParentClass().getName() + "." + LoadElem.getName());
						LoadElem = (CktElement) BranchList.getNextObject();
					}
					PDElem = (PDElement) BranchList.GoForward();
				}
			}
		}
	}

	/**
	 * Add to VoltBase list if not already there and return index.
	 */
	private int addToVoltBaseList(int BusRef) {
		Bus bus = DSSGlobals.getInstance().getActiveCircuit().getBuses()[BusRef];

		for (int i = 0; i < VBaseCount; i++) {
			if (Math.abs(1.0 - bus.getkVBase() / VBaseList[i]) < 0.01)  // < 1% difference
				return i;
		}

		if ((bus.getkVBase() > 0.0) && (VBaseCount < MaxVBaseCount)) {
			VBaseCount += 1;
			VBaseList[VBaseCount] = bus.getkVBase();
			return VBaseCount;
		} else {
			return 0;
		}
	}

	public void allocateLoad() {
		int ConnectedPhase;
		PDElement CktElem;
		LoadObj LoadElem;

		/* Now go through the meter's zone and adjust the loads.
		 *
		 * While the AllocationFactor property is adjusted for all loads, it will only
		 * have an effect on loads defined with either the XFKVA property or the
		 * kWh property.
		 *
		 * Loads have a SensorObj property that points to its upstream sensor that has the adjustments for
		 * the allocation factors.  This is established in the MakeMeterZoneLists proc in this Unit.
		 *
		 * Sensors consist of EnergyMeters, which drive the load allocation process and Sensor objects that
		 * are simply voltage and current measuring points.  A Sensor may be attached to a line or transformer
		 * or it may be connected directly to a load.
		 */

		CktElem = (PDElement) BranchList.getFirst();
		while (CktElem != null) {
			LoadElem = (LoadObj) BranchList.getFirstObject();
			while (LoadElem != null) {
				if ((LoadElem.getDSSObjType() & DSSClassDefs.CLASSMASK) == DSSClassDefs.LOAD_ELEMENT) {  // only for loads not other shunts
					switch (LoadElem.getNPhases()) {
					/* For single phase loads, allocate based on phase factor, else average factor */
					case 1:
						ConnectedPhase = DSSGlobals.getInstance().getActiveCircuit().getMapNodeToBus()[NodeRef[0]].NodeNum;
						if ((ConnectedPhase > 0) && (ConnectedPhase < 4))  // Restrict to phases 1..3
							LoadElem.setAllocationFactor( LoadElem.getAllocationFactor() * LoadElem.getSensorObj().getPhsAllocationFactor()[ConnectedPhase] );
					default:
						LoadElem.setAllocationFactor( LoadElem.getAllocationFactor() * AvgAllocFactor);
					}
				}
				LoadElem = (LoadObj) BranchList.getNextObject();  /* Next load at this bus */
			}
			CktElem = (PDElement) BranchList.GoForward();  /* Go on down the tree */
		}
	}

	@Override
	public void initPropertyValues(int ArrayOffset) {
		String S;

		setPropertyValue(0, ""); // "element";
		setPropertyValue(1, "1"); // "terminal";
		setPropertyValue(2, "clear"); // "action";
		setPropertyValue(3, "(E, R, C)"); // "option";
		setPropertyValue(4, "0.0"); // "kWnormal";
		setPropertyValue(5, "0.0"); // "kwEmerg";
		setPropertyValue(6, "(400, 400, 400)"); // "PeakCurrent";
		setPropertyValue(7, ""); // ZoneList
		setPropertyValue(8, "No");
		/* Define mask as 1 for all registers */
		S = "[";
		for (int i = 0; i < EnergyMeter.NumEMRegisters; i++)
			S = S + "1 ";
		setPropertyValue(9, S + "]");
		setPropertyValue(10, "Yes");
		setPropertyValue(11, "Yes");
		setPropertyValue(12, "Yes");
		setPropertyValue(13, "Yes");
		setPropertyValue(14, "Yes");  // segregate losses by voltage base
		setPropertyValue(15, "Yes");
		setPropertyValue(16, "No");

		super.initPropertyValues(EnergyMeter.NumPropsThisClass);
	}

	private void accumulateGen(GeneratorObj pGen, double TotalZonekW, double TotalZonekvar) {
		//pGen.setActiveTerminalIdx(1);
		Complex S = pGen.getPower(1).multiply(0.001).negate();  // TODO Check zero based indexing
		TotalZonekW   = TotalZonekW   + S.getReal();
		TotalZonekvar = TotalZonekvar + S.getImaginary();
	}

	private double accumulateLoad(LoadObj pLoad, double TotalZonekW, double TotalZonekvar, double TotalLoad_EEN, double TotalLoad_UE) {

		Complex S_Load;
		double kW_Load, Result;
		double Load_EEN, Load_UE;

		//pLoad.setActiveTerminalIdx(1);
		S_Load  = pLoad.getPower(1).multiply(0.001);   // Get Power in Terminal 1   TODO Check zero based indexing
		kW_Load = S_Load.getReal();
		Result  = kW_Load;

		/* Accumulate load in zone */
		TotalZonekW   = TotalZonekW   + kW_Load;
		TotalZonekvar = TotalZonekvar + S_Load.getImaginary();

		/* always integrate even if the value is 0.0
		 * otherwise the Integrate function is not correct
		 */
		/* Invoking the ExceedsNormal and Unserved Properties causes the factors to be computed */
		if (ExcessFlag) {  // Return Excess load as EEN/UE
			if (pLoad.getExceedsNormal()) {
				Load_EEN = kW_Load * pLoad.getEEN_Factor();
			} else {
				Load_EEN = 0.0;
			}
			if (pLoad.getUnserved()) {
				Load_UE  = kW_Load * pLoad.getUE_Factor();
			} else {
				Load_UE = 0.0;
			}
		} else {  // Return TOTAL load as EEN/UE
			if (pLoad.getExceedsNormal()) {
				Load_EEN = kW_Load;
			} else {
				Load_EEN = 0.0;
			}
			if (pLoad.getUnserved()) {
				Load_UE = kW_Load;
			} else {
				Load_UE = 0.0;
			}
		}

		TotalLoad_EEN = TotalLoad_EEN + Load_EEN;
		TotalLoad_UE  = TotalLoad_UE  + Load_UE;

		return Result;
	}

	/**
	 * Reduce Zone by eliminating buses and merging lines.
	 */
	public void reduceZone() {
		// Make  sure zone list is built
		if (BranchList == null) makeMeterZoneLists();

		switch (DSSGlobals.getInstance().getActiveCircuit().getReductionStrategy()) {
		case rsStubs:         ReduceAlgs.doReduceStubs(BranchList);
		case rsTapEnds:       ReduceAlgs.doReduceTapEnds (BranchList);
		case rsMergeParallel: ReduceAlgs.doMergeParallelLines(BranchList);
		case rsDangling:      ReduceAlgs.doReduceDangling(BranchList);
		case rsBreakLoop:     ReduceAlgs.doBreakLoops(BranchList);
		case rsSwitches:      ReduceAlgs.doReduceSwitches(BranchList);
		default:
			ReduceAlgs.doReduceDefault(BranchList);
		}

		// Resynchronize with Feeders
		if (HasFeeder) FeederObj.initializeFeeder(BranchList);
	}

	/**
	 * Start at the ends of the zone and work toward the start
	 * interpolating between known coordinates.
	 */
	public void interpolateCoordinates() {
		int i, BusRef, FirstCoordRef, SecondCoordRef, LineCount;
		CktTreeNode StartNode, PresentNode = null;
		CktElement CktElem;

		DSSGlobals Globals = DSSGlobals.getInstance();
		Circuit ckt = Globals.getActiveCircuit();

		if (BranchList == null) {
			Globals.doSimpleMsg("Meter Zone Lists need to be built. Do Solve or Makebuslist first!", 529);
			return;
		}

		for (i = 0; i < BranchList.getZoneEndsList().getNumEnds(); i++) {
			BusRef = BranchList.getZoneEndsList().Get(i, PresentNode);

			FirstCoordRef = BusRef;
			SecondCoordRef = FirstCoordRef;  /* so compiler won't issue warning */
			/* Find a bus with a coordinate */
			if (!ckt.getBuses()[BusRef].isCoordDefined()) {
				while (!ckt.getBuses()[PresentNode.getFromBusReference()].isCoordDefined()) {
					PresentNode = PresentNode.getParent();
					if (PresentNode == null) break;
				}
				if (PresentNode != null) FirstCoordRef = PresentNode.getFromBusReference();
			}

			while (PresentNode != null) {
				/* Back up until we find another Coord defined */
				LineCount = 0;  /* number of line segments in this segment */
				StartNode = PresentNode;
				CktElem   = (CktElement) PresentNode.getCktObject();
				if (FirstCoordRef != PresentNode.getFromBusReference()) {
					/* Handle special case for end branch */
					if (ckt.getBuses()[PresentNode.getFromBusReference()].isCoordDefined()) {
						FirstCoordRef = PresentNode.getFromBusReference();
					} else {
						LineCount += 1;
					}
				}

				while (!ckt.getBuses()[SecondCoordRef].isCoordDefined() && !CktElem.isChecked()) {
					CktElem.setChecked(true);
					PresentNode = PresentNode.getParent();
					if (PresentNode == null) break;
					CktElem = (CktElement) PresentNode.getCktObject();
					SecondCoordRef = PresentNode.getFromBusReference();
					LineCount += 1;
				}

				if ((PresentNode != null) && (LineCount > 1)) {
					if (ckt.getBuses()[SecondCoordRef].isCoordDefined()) {
						calcBusCoordinates(StartNode,  FirstCoordRef, SecondCoordRef, LineCount);
					} else {
						break;  /* While - went as far as we could go this way */
					}
				}

				FirstCoordRef = SecondCoordRef;
			}

		}  /* for */
	}

	private void calcBusCoordinates(CktTreeNode StartBranch, int FirstCoordRef, int SecondCoordRef, int LineCount) {

		double X, Y, Xinc, Yinc;

		if (LineCount == 1) return;  /* Nothing to do! */

		Circuit ckt = DSSGlobals.getInstance().getActiveCircuit();

		Xinc = (ckt.getBuses()[FirstCoordRef].getX() - ckt.getBuses()[SecondCoordRef].getX()) / LineCount;
		Yinc = (ckt.getBuses()[FirstCoordRef].getY() - ckt.getBuses()[SecondCoordRef].getY()) / LineCount;

		X = ckt.getBuses()[FirstCoordRef].getX();
		Y = ckt.getBuses()[FirstCoordRef].getY();

		/*if (((X < 10.0) && (y < 10.0)) || ((ckt.getBuses()[SecondCoordRef].getX() < 10.0) && (ckt.getBuses()[SecondCoordRef].getY() < 10.0)))
			X = Y;*/  // Stopping point

		/* Either start with the "to" end of StartNode or the "from" end; */
		if (FirstCoordRef != StartBranch.getFromBusReference()) {
			// Start with "to" end
			X = X - Xinc;
			Y = Y - Yinc;
			ckt.getBuses()[StartBranch.getFromBusReference()].setX(X);
			ckt.getBuses()[StartBranch.getFromBusReference()].setY(Y);
			ckt.getBuses()[StartBranch.getFromBusReference()].setCoordDefined(true);
			LineCount -= 1;
		}

		while (LineCount > 1) {
			X = X - Xinc;
			Y = Y - Yinc;
			StartBranch = StartBranch.getParent();  // back up the tree
			ckt.getBuses()[StartBranch.getFromBusReference()].setX(X);
			ckt.getBuses()[StartBranch.getFromBusReference()].setY(Y);
			ckt.getBuses()[StartBranch.getFromBusReference()].setCoordDefined(true);
			LineCount -= 1;
		}
	}

	@Override
	public String getPropertyValue(int Index) {
		String Result;

		switch (Index) {
		case 3:
			Result = "(";
		case 6:
			Result = "(";
		default:
			Result = "";
		}

		switch (Index) {
		case 4:  // option
			if (ExcessFlag) {
				Result = Result + "E,";
			} else {
				Result = Result + "T,";
			}
			if (ZoneIsRadial) {
				Result = Result + " R,";
			} else {
				Result = Result + " M,";
			}
			if (VoltageUEOnly) {
				Result = Result + " V";
			} else {
				Result = Result + " C";
			}
		default:
			Result = Result + super.getPropertyValue(Index);
		}

		switch (Index) {
		case 3:
			Result = Result + ")";
		case 6:
			Result = Result + ")";
		}

		return Result;
	}

	public void saveZone(String dirname) {
		CktElement cktElem, shuntElement;
		LoadObj LoadElement;
		File FBranches, FShunts, FLoads, FGens, FCaps;
		FileWriter FBranchesStream, FShuntsStream, FLoadsStream, FGensStream, FCapsStream;
		PrintWriter FBranchesBuffer, FShuntsBuffer, FLoadsBuffer, FGensBuffer, FCapsBuffer;
		int NBranches, NShunts, NLoads, NGens, NCaps;

		DSSGlobals Globals = DSSGlobals.getInstance();
		Circuit ckt = Globals.getActiveCircuit();

		/* We are in the directory indicated by dirname */

		/* Run down the zone and write each element into a file */

		if (BranchList != null) {
			/* Open some files: */

			try {
				FBranches = new File("Branches.dss");  // Both lines and transformers
				FBranchesStream = new FileWriter(FBranches, false);
				FBranchesBuffer = new PrintWriter(FBranchesStream);

				NBranches = 0;
			} catch (Exception e) {
				Globals.doSimpleMsg("Error creating Branches.dss for Energymeter: " + getName()+". " + e.getMessage(), 530);
				return;
			}

			try {
				FShunts = new File("Shunts.dss");
				FShuntsStream = new FileWriter(FShunts, false);
				FShuntsBuffer = new PrintWriter(FShuntsStream);

				NShunts = 0;
			} catch (Exception e) {
				Globals.doSimpleMsg("Error creating Shunts.dss for Energymeter: " + getName() + ". " + e.getMessage(), 531);
				return;
			}

			try {
				FLoads = new File("Loads.dss");
				FLoadsStream = new FileWriter(FLoads, false);
				FLoadsBuffer = new PrintWriter(FLoadsStream);

				NLoads = 0;
			} catch (Exception e) {
				Globals.doSimpleMsg("Error creating Loads.dss for Energymeter: " + getName() + ". " + e.getMessage(), 532);
				return;
			}

			try {
				FGens = new File("Generators.dss");
				FGensStream = new FileWriter(FGens, false);
				FGensBuffer = new PrintWriter(FGensStream);

				NGens = 0;
			} catch (Exception e) {
				Globals.doSimpleMsg("Error creating Generators.dss for Energymeter: " + getName() + ". " + e.getMessage(), 533);
				return;
			}

			try {
				FCaps = new File("Capacitors.dss");
				FCapsStream = new FileWriter(FCaps, false);
				FCapsBuffer = new PrintWriter(FCapsStream);
				NCaps = 0;
			} catch (Exception e) {
				Globals.doSimpleMsg("Error creating Generators.dss for Energymeter: " + getName() + ". " + e.getMessage(), 534);
				return;
			}


			cktElem = (CktElement) BranchList.getFirst();
			while (cktElem != null) {
				if (cktElem.isEnabled()) {
					ckt.setActiveCktElement(cktElem);
					NBranches += 1;
					Utilities.writeActiveDSSObject(FBranchesBuffer, "New");     // sets hasBeenSaved(true)
					if (ckt.getActiveCktElement().hasControl()) {
						ckt.setActiveCktElement( ckt.getActiveCktElement().getControlElement() );
						Utilities.writeActiveDSSObject(FBranchesBuffer, "New");  //  regulator control ... also, relays, switch controls
					}

					shuntElement = (CktElement) BranchList.getFirstObject();
					while (shuntElement != null) {
						ckt.setActiveCktElement(shuntElement);
						if ((shuntElement.getDSSObjType() & DSSClassDefs.CLASSMASK) == DSSClassDefs.LOAD_ELEMENT) {
							LoadElement = (LoadObj) shuntElement;
							if (LoadElement.getHasBeenAllocated()) {
								/* Manually set the allocation factor so it shows up */
								Parser.getInstance().setCmdString( "allocationfactor=" + String.format("%-.4g", LoadElement.getAllocationFactor()) );
								LoadElement.edit();
							}
							ckt.setActiveCktElement(shuntElement);  // reset in case edit mangles it
							NLoads += 1;
							Utilities.writeActiveDSSObject(FLoadsBuffer, "New");
						} else if ((shuntElement.getDSSObjType() & DSSClassDefs.CLASSMASK) == DSSClassDefs.GEN_ELEMENT) {
							NGens += 1;
							Utilities.writeActiveDSSObject(FGensBuffer, "New");
							if (ckt.getActiveCktElement().hasControl()) {
								ckt.setActiveCktElement(ckt.getActiveCktElement().getControlElement());
								Utilities.writeActiveDSSObject(FGensBuffer, "New");
							}
						} else if ((shuntElement.getDSSObjType() & DSSClassDefs.CLASSMASK) == DSSClassDefs.CAP_ELEMENT) {
							NCaps += 1;
							Utilities.writeActiveDSSObject(FCapsBuffer, "New");
							if (ckt.getActiveCktElement().hasControl()) {
								ckt.setActiveCktElement(ckt.getActiveCktElement().getControlElement());
								Utilities.writeActiveDSSObject(FCapsBuffer, "New");
							}
						} else {
							NShunts += 1;
							Utilities.writeActiveDSSObject(FShuntsBuffer, "New");
						}
						shuntElement = (CktElement) BranchList.getNextObject();
					}
				}  /* if enabled */
				cktElem = (CktElement) BranchList.GoForward();
			}

			FBranchesBuffer.close();
			FShuntsBuffer.close();
			FLoadsBuffer.close();
			FGensBuffer.close();
			FCapsBuffer.close();

			try {
				FBranchesStream.close();
				FShuntsStream.close();
				FLoadsStream.close();
				FGensStream.close();
				FCapsStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			/* If any records were written to the file, record their relative names */
			if (NBranches > 0) {
				Globals.getSavedFileList().add(dirname + "/Branches.dss");
			} else {
				FBranches.delete();
			}
			if (NShunts > 0) {
				Globals.getSavedFileList().add(dirname + "/Shunts.dss");
			} else {
				FShunts.delete();
			}
			if (NLoads > 0) {
				Globals.getSavedFileList().add(dirname + "/Loads.dss");
			} else {
				FLoads.delete();
			}
			if (NGens > 0) {
				Globals.getSavedFileList().add(dirname + "/Generators.dss");
			} else {
				FGens.delete();
			}
			if (NCaps > 0) {
				Globals.getSavedFileList().add(dirname + "/Capacitors.dss");
			} else {
				FCaps.delete();
			}
		}
	}

	private void setDragHandRegister(int Reg, double Value) {
		if (Value > Registers[Reg]) {
			Registers[Reg]   = Value;
			Derivatives[Reg] = Value;  // Use this for demand interval data;
		}
	}

	public void enableFeeder() {

	}

	private String makeDIFileName() {
		return null;
	}

	private void assignVoltBaseRegisterNames() {

	}

	private void makeFeederObj() {

	}

	private void removeFeederObj() {

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
