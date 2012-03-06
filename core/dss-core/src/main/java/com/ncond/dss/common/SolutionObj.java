package com.ncond.dss.common;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.UUID;

import org.apache.commons.math.complex.Complex;

import com.ncond.dss.common.Bus.NodeBus;
import com.ncond.dss.control.ControlElem;
import com.ncond.dss.conversion.GeneratorObj;
import com.ncond.dss.conversion.PCElement;
import com.ncond.dss.delivery.FaultObj;
import com.ncond.dss.general.DSSObject;
import com.ncond.dss.shared.ComplexUtil;
import com.ncond.dss.shared.Dynamics;
import com.ncond.dss.shared.DynamicsRec;

public class SolutionObj extends DSSObject {

	/* Array of delta V for Newton iteration */
	private Complex[] dV;
	private double frequency;

	protected int algorithm;           // NORMALSOLVE or NEWTONSOLVE
	protected Complex[] auxCurrents;   // for injections like AutoAdd
	protected boolean controlActionsDone;
	protected int controlIteration;
	protected int controlMode;         // EVENTDRIVEN, TIMEDRIVEN
	protected double convergenceTolerance;
	protected boolean convergedFlag;
	protected int defaultControlMode;  // EVENTDRIVEN, TIMEDRIVEN
	protected int defaultLoadModel;    // 1=POWERFLOW  2=ADMITTANCE
	protected boolean doAllHarmonics;
	protected boolean dynamicsAllowed;
	protected DynamicsRec dynaVars = new DynamicsRec();
	protected double[] errorSaved;
	protected boolean firstIteration;
	/* Flag set to true if something has altered the frequency */
	protected boolean frequencyChanged;
	protected int year;
	protected double harmonic;
	protected double[] harmonicList;
	protected int harmonicListSize;
	protected int intHour;
	protected double dblHour;
	/* Main (system) Y matrix */
	protected UUID YSystem;
	/* Series Y matrix */
	protected UUID YSeries;
	/* Either Ysystem or Yseries */
	protected UUID Y;
	protected double intervalHrs;   // solution interval since last solution, hrs.
	protected boolean isDynamicModel;
	protected boolean isHarmonicModel;
	protected int iteration;
	protected int loadModel;        // 1=POWERFLOW  2=ADMITTANCE
	protected boolean lastSolutionWasDirect;
	protected boolean loadsNeedUpdating;
	protected int maxControlIterations;
	protected double maxError;
	protected int maxIterations;
	protected int mostIterationsDone;
	protected double[] nodeVBase;
	protected int numberOfTimes;    // number of times to solve
	protected boolean preserveNodeVoltages;
	protected int randomType;       // 0 = NONE; 1 = GAUSSIAN; 2 = UNIFORM
	protected boolean seriesYInvalid;
	protected int solutionCount;    // counter incremented for each solution
	protected boolean solutionInitialized;
	protected boolean systemYChanged;
	protected boolean useAuxCurrents;
	protected double[] VMagSaved;
	protected boolean voltageBaseChanged;

	/* Main system voltage array */
	protected Complex[] nodeV;
	/* Main system currents array */
	protected Complex[] currents;

	public SolutionObj(DSSClass parClass, String solutionName) {
		super(parClass);
		setName(solutionName.toLowerCase());

		year = 0;
		intHour = 0;
		dynaVars.t = 0.0;
		dblHour = 0.0;
		dynaVars.tstart = 0.0;
		dynaVars.tstop = 0.0;
		//duration = 0.0;
		dynaVars.h = 0.001;  // default for dynasolve

		loadsNeedUpdating = true;
		voltageBaseChanged = true;  // forces building of convergence check arrays

		maxIterations = 15;
		maxControlIterations = 10;
		convergenceTolerance = 0.0001;
		convergedFlag = false;

		isDynamicModel  = false;
		isHarmonicModel = false;

		setFrequency( DSS.defaultBaseFreq );
		/*Fundamental = 60.0; moved to circuit and used as default base frequency*/
		harmonic = 1.0;

		frequencyChanged = true;  // force building of YPrim matrices
		doAllHarmonics   = true;
		firstIteration   = true;
		dynamicsAllowed  = false;
		systemYChanged   = true;
		seriesYInvalid   = true;

		/* Define default harmonic list */
		harmonicListSize = 5;
		harmonicList = new double[harmonicListSize];
		harmonicList[0] = 1.0;
		harmonicList[1] = 5.0;
		harmonicList[2] = 7.0;
		harmonicList[3] = 11.0;
		harmonicList[4] = 13.0;

		solutionInitialized = false;
		loadModel = DSS.POWERFLOW;
		defaultLoadModel = loadModel;
		lastSolutionWasDirect = false;

		YSeries = null;
		YSystem = null;
		Y = null;

		nodeV      = null;
		dV         = null;
		currents   = null;
		auxCurrents= null;
		VMagSaved  = null;
		errorSaved = null;
		nodeVBase  = null;

		useAuxCurrents = false;

		solutionCount = 0;

		dynaVars.solutionMode = Dynamics.SNAPSHOT;
		controlMode           = DSS.CTRLSTATIC;
		defaultControlMode    = controlMode;
		algorithm             = Solution.NORMALSOLVE;

		randomType    = DSS.GAUSSIAN;  // default to Gaussian
		numberOfTimes = 100;
		intervalHrs   = 1.0;

		initPropertyValues(0);
	}

	/**
	 * Main solution dispatch.
	 */
	public void solve() {

		DSS.activeCircuit.setIsSolved(false);
		DSS.solutionWasAttempted = true;

		DSS.forms.initProgressForm();  // initialize progress form;

		/* Check of some special conditions that must be met before executing solutions */

		if (DSS.activeCircuit.getEmergMinVolts() >= DSS.activeCircuit.getNormalMinVolts()) {
			DSS.doSimpleMsg("Error: Emergency Min Voltage Must Be Less Than Normal Min Voltage!" +
					DSS.CRLF + "Solution Not Executed.", 480);
			return;
		}

		if (DSS.solutionAbort) {
			DSS.globalResult = "Solution aborted.";
			DSS.cmdResult = DSS.SOLUTION_ABORT;
			DSS.errorNumber = DSS.cmdResult;
			return;
		}

		try {

			/* Main solution algorithm dispatcher */
			Circuit ckt = DSS.activeCircuit;

			switch (year) {
			case 0:
				ckt.setDefaultGrowthFactor(1.0);
				break;
			default:
				ckt.setDefaultGrowthFactor(Math.pow(ckt.getDefaultGrowthRate(), (year - 1)));
				break;
			}

//			fireInitControls();

			/* checkFaultStatus;  ???? needed here?? */

			switch (dynaVars.solutionMode) {
			case Dynamics.SNAPSHOT:
				solveSnap();
				break;
			case Dynamics.YEARLYMODE:
				SolutionAlgs.solveYearly();
				break;
			case Dynamics.DAILYMODE:
				SolutionAlgs.solveDaily();
				break;
			case Dynamics.DUTYCYCLE:
				SolutionAlgs.solveDuty();
				break;
			case Dynamics.DYNAMICMODE:
				SolutionAlgs.solveDynamic();
				break;
			case Dynamics.MONTECARLO1:
				SolutionAlgs.solveMonte1();
				break;
			case Dynamics.MONTECARLO2:
				SolutionAlgs.solveMonte2();
				break;
			case Dynamics.MONTECARLO3:
				SolutionAlgs.solveMonte3();
				break;
			case Dynamics.PEAKDAY:
				SolutionAlgs.solvePeakDay();
				break;
			case Dynamics.LOADDURATION1:
				SolutionAlgs.solveLD1();
				break;
			case Dynamics.LOADDURATION2:
				SolutionAlgs.solveLD2();
				break;
			case Dynamics.DIRECT:
				solveDirect();
				break;
			case Dynamics.MONTEFAULT:
				SolutionAlgs.solveMonteFault();  // Monte Carlo fault cases
				break;
			case Dynamics.FAULTSTUDY:
				SolutionAlgs.solveFaultStudy();
				break;
			case Dynamics.AUTOADDFLAG:
				ckt.getAutoAddObj().solve();
				break;
			case Dynamics.HARMONICMODE:
				SolutionAlgs.solveHarmonic();
				break;
			case Dynamics.GENERALTIME:
				SolutionAlgs.solveGeneralTime();
				break;
			default:
				DSS.doSimpleMsg("Unknown solution mode.", 481);
				break;
			}
		} catch (SolveProblem e) {
			DSS.doSimpleMsg("Error encountered in Solve: " + e.getMessage(), 482);
			DSS.solutionAbort = true;
		} catch (SolverError e) {
			// TODO Handle solver error
		} catch (ControlProblem e) {
			// TODO Handle control problem
		}
	}

	private boolean converged() {
		boolean result;
		double VMag;
		Circuit ckt = DSS.activeCircuit;

		// base convergence on voltage magnitude

		maxError = 0.0;
		for (int i = 0; i < ckt.getNumNodes(); i++) {
			VMag = nodeV[i].abs();

			/* If base specified, use it; otherwise go on present magnitude */
			if (nodeVBase[i] > 0.0) {
				errorSaved[i] = Math.abs(VMag - VMagSaved[i]) / nodeVBase[i];
			} else {
				if (VMag != 0.0)
					errorSaved[i] = Math.abs(1.0 - VMagSaved[i] / VMag);
			}

			VMagSaved[i] = VMag;  // for next go-'round

			maxError = Math.max(maxError, errorSaved[i]);  // update max error
		}

		/* $IFDEF debugtrace */
//		FileWriter F = new FileWriter("DebugTrace.csv", true);
//		BufferedWriter FDebug = new BufferedWriter(F);
//		if (Iteration == 1) {
//			FDebug.write("Iter");
//			for (int i = 0; i < ckt.getNumNodes(); i++)
//				FDebug.write(", " + ckt.getBusList().get(ckt.getMapNodeToBus()[i].BusRef) + "." + ckt.getMapNodeToBus()[i].NodeNum);  // TODO Implement colon syntax
//			FDebug.newLine();
//		}
//		/* ***** */  // FIXME Format number widths
//		FDebug.write(Iteration);
//		for (int i = 0; i < ckt.getNumNodes(); i++)
//			FDebug.write(", " + VmagSaved[i]);
//		FDebug.newLine();
//		FDebug.write("Err");
//		for (int i = 0; i < ckt.getNumNodes(); i++)
//			FDebug.write(", " + String.format("%-.5g", ErrorSaved[i]));
//		FDebug.newLine();
//		FDebug.write("Curr");
//		for (int i = 0; i < ckt.getNumNodes(); i++)
//			FDebug.write(", " + Currents[i].abs());
//		FDebug.newLine();
//		/* ***** */
//		FDebug.close();
		/* $ENDIF */

		if (maxError <= convergenceTolerance) {
			result = true;
		} else {
			result = false;
		}

		convergedFlag = result;

		return result;
	}

	/**
	 * Add in the contributions of all source type elements to the global
	 * solution vector InjCurr.
	 */
	private void getSourceInjCurrents() {
		Circuit ckt = DSS.activeCircuit;

		for (CktElement pElem : ckt.getSources())
			if (pElem.isEnabled())
				pElem.injCurrents();  // uses nodeRef to add current into injCurr Array;
	}

	/**
	 * Set the global generator dispatch reference.
	 */
	public void setGeneratorDispRef() {
		Circuit ckt = DSS.activeCircuit;

		switch (dynaVars.solutionMode) {
		case Dynamics.SNAPSHOT:
			ckt.setGeneratorDispatchReference(ckt.getLoadMultiplier() * ckt.getDefaultGrowthFactor());
			break;
		case Dynamics.YEARLYMODE:
			ckt.setGeneratorDispatchReference(ckt.getDefaultGrowthFactor() * ckt.getDefaultHourMult().getReal());
			break;
		case Dynamics.DAILYMODE:
			ckt.setGeneratorDispatchReference(ckt.getLoadMultiplier() * ckt.getDefaultGrowthFactor() * ckt.getDefaultHourMult().getReal());
			break;
		case Dynamics.DUTYCYCLE:
			ckt.setGeneratorDispatchReference(ckt.getLoadMultiplier() * ckt.getDefaultGrowthFactor() * ckt.getDefaultHourMult().getReal());
			break;
		case Dynamics.GENERALTIME:
			ckt.setGeneratorDispatchReference(ckt.getLoadMultiplier() * ckt.getDefaultGrowthFactor() * ckt.getDefaultHourMult().getReal());
			break;
		case Dynamics.DYNAMICMODE:
			ckt.setGeneratorDispatchReference(ckt.getLoadMultiplier() * ckt.getDefaultGrowthFactor());
			break;
		case Dynamics.HARMONICMODE:
			ckt.setGeneratorDispatchReference(ckt.getLoadMultiplier() * ckt.getDefaultGrowthFactor());
			break;
		case Dynamics.MONTECARLO1:
			ckt.setGeneratorDispatchReference(ckt.getLoadMultiplier() * ckt.getDefaultGrowthFactor());
			break;
		case Dynamics.MONTECARLO2:
			ckt.setGeneratorDispatchReference(ckt.getLoadMultiplier() * ckt.getDefaultGrowthFactor() * ckt.getDefaultHourMult().getReal());
			break;
		case Dynamics.MONTECARLO3:
			ckt.setGeneratorDispatchReference(ckt.getLoadMultiplier() * ckt.getDefaultGrowthFactor() * ckt.getDefaultHourMult().getReal());
			break;
		case Dynamics.PEAKDAY:
			ckt.setGeneratorDispatchReference(ckt.getLoadMultiplier() * ckt.getDefaultGrowthFactor() * ckt.getDefaultHourMult().getReal());
			break;
		case Dynamics.LOADDURATION1:
			ckt.setGeneratorDispatchReference(ckt.getLoadMultiplier() * ckt.getDefaultGrowthFactor() * ckt.getDefaultHourMult().getReal());
			break;
		case Dynamics.LOADDURATION2:
			ckt.setGeneratorDispatchReference(ckt.getLoadMultiplier() * ckt.getDefaultGrowthFactor() * ckt.getDefaultHourMult().getReal());
			break;
		case Dynamics.DIRECT:
			ckt.setGeneratorDispatchReference(ckt.getLoadMultiplier() * ckt.getDefaultGrowthFactor());
			break;
		case Dynamics.MONTEFAULT:
			ckt.setGeneratorDispatchReference(1.0);  // Monte Carlo fault cases solve at peak load only base case
			break;
		case Dynamics.FAULTSTUDY:
			ckt.setGeneratorDispatchReference(1.0);
			break;
		case Dynamics.AUTOADDFLAG:
			ckt.setGeneratorDispatchReference(ckt.getDefaultGrowthFactor());  // peak load only
			break;
		default:
			DSS.doSimpleMsg("Unknown solution mode.", 483);
			break;
		}
	}

	private void setGenerator_dQdV() throws SolverError, SolveProblem {
		boolean didOne = false;
		double genDispSave;

		Circuit ckt = DSS.activeCircuit;

		// save the generator dispatch level and set on high enough to turn all generators on
		genDispSave = ckt.getGeneratorDispatchReference();
		ckt.setGeneratorDispatchReference(1000.0);

		for (GeneratorObj pGen : ckt.getGenerators()) {
			if (pGen.isEnabled()) {

				// for PV generator models only ...
				if (pGen.getGenModel() == 3) {

					pGen.initDQDVCalc();

					// solve at base var setting
					iteration = 0;
					while (!converged() && (iteration < maxIterations)) {
						iteration += 1;
						zeroInjCurr();
						getSourceInjCurrents();
						pGen.injCurrents();  // get generator currents with nominal vars
						solveSystem(nodeV);
					}

					pGen.rememberQV();  // remember Q and V
					pGen.bumpUpQ();

					// solve after changing vars
					iteration = 0;
					while (!converged() && iteration < maxIterations) {
						iteration += 1;
						zeroInjCurr();
						getSourceInjCurrents();
						pGen.injCurrents();  // get generator currents with nominal vars
						solveSystem(nodeV);
					}

					pGen.calcDQDV(); // bssed on remembered Q and V and present values of same
					pGen.resetStartPoint();

					didOne = true;
				}
			}
		}

		// restore generator dispatch reference
		ckt.setGeneratorDispatchReference(genDispSave);
		try {
			if (didOne)  // reset initial solution
				solveZeroLoadSnapShot();
		} catch (SolveProblem e) {
			DSS.doSimpleMsg("From setGenerator dQdV, solveZeroLoadSnapShot: " + DSS.CRLF + e.getMessage()  + YMatrix.checkYMatrixforZeroes(), 7071);
			throw new SolverError("Aborting");
		}
	}

	/**
	 * Normal fixed-point solution.
	 *
	 *   Vn+1 = [Y]-1 Injcurr
	 *
	 * Where Injcurr includes only PC elements (loads, generators, etc.)
	 * i.e., the shunt elements.
	 *
	 * InjCurr are the current injected into the node (need to reverse
	 * current direction for loads).
	 *
	 * @throws SolveProblem
	 */
	private void doNormalSolution() throws SolveProblem {
		iteration = 0;

		Circuit ckt = DSS.activeCircuit;

		/* **** Main iteration loop **** */
		while ((!converged() || iteration <= 1) && (iteration < maxIterations)) {
			iteration += 1;

			if (ckt.isLogEvents())
				Util.logThisEvent("Solution Iteration " + String.valueOf(iteration));

			/* Get injcurrents for all PC devices */
			zeroInjCurr();
			getSourceInjCurrents();  // sources
			getPCInjCurr();  // get the injection currents from all the power conversion devices and feeders

			// the above call could change the primitive Y matrix, so have to check
			if (systemYChanged)
				YMatrix.buildYMatrix(YMatrix.WHOLEMATRIX, false);  // does not realloc V, I

			if (useAuxCurrents)
				addInAuxCurrents(Solution.NORMALSOLVE);

			// solve for voltages      /* Note: nodeV[0] = 0 + j0 always */
			if (ckt.isLogEvents())
				Util.logThisEvent("Solve sparse set doNormalSolution ...");
			solveSystem(nodeV);
			loadsNeedUpdating = false;
		}
	}

	/**
	 * Newton iteration.
	 *
	 *   Vn+1 =  Vn - [Y]-1 Termcurr
	 *
	 * Where termCurr includes currents from all elements and we are
	 * attempting to get the  currents to sum to zero at all nodes.
	 *
	 * TermCurr is the sum of all currents going into the terminals of
	 * the elements.
	 *
	 * For PD Elements, termCurr = Yprim*V
	 *
	 * For Loads, termCurr = (Sload / V)*
	 * For Generators, termCurr = -(Sgen / V)
	 *
	 * @throws SolveProblem *
	 */
	private void doNewtonSolution() throws SolveProblem {
		Circuit ckt = DSS.activeCircuit;

		dV = Util.resizeArray(dV, ckt.getNumNodes() + 1);  // make sure this is always big enough

		if (controlIteration == 1)
			getPCInjCurr();  // update the load multipliers for this solution

		iteration = 0;
		while ((!converged() || (iteration <= 1)) && (iteration < maxIterations)) {
			iteration += 1;
			solutionCount += 1;  // sumAllCurrents uses ITerminal, so must force a recalc

			// get sum of currents at all nodes for all devices
			zeroInjCurr();
			sumAllCurrents();

			// call to current calc could change YPrim for some devices
			if (systemYChanged)
				YMatrix.buildYMatrix(YMatrix.WHOLEMATRIX, false);  // does not realloc V, I

			if (useAuxCurrents)
				addInAuxCurrents(Solution.NEWTONSOLVE);

			// solve for change in voltages
			solveSystem(dV);

			loadsNeedUpdating = false;

			// compute new guess at voltages
			for (int i = 0; i < ckt.getNumNodes(); i++) {  // 0 node is always 0
				nodeV[i] = new Complex(nodeV[i].getReal() - dV[i].getReal(),
						nodeV[i].getImaginary() - dV[i].getImaginary());
			}
		}
	}

	public void doPFlowSolution() throws SolverError, SolveProblem {

		solutionCount += 1;  // unique number for this solution

		if (voltageBaseChanged)
			YMatrix.initializeNodeVbase();  // for convergence test

		if (!solutionInitialized) {

			if (DSS.activeCircuit.isLogEvents())
				Util.logThisEvent("Initializing Solution");
			try {
				//solveZeroLoadSnapShot();
				solveYDirect();  // 8-14-06 this should give a better answer than zero load snapshot
			} catch (SolveProblem e) {
				DSS.doSimpleMsg("From doPFlowSolution().solveYDirect(): " + DSS.CRLF + e.getMessage() + YMatrix.checkYMatrixforZeroes(), 7072);
				throw new SolverError("Aborting");
			}
			if (DSS.solutionAbort)
				return;  // initialization can result in abort

			try {
				setGenerator_dQdV();  // set dQdV for model 3 generators
			} catch (SolveProblem e) {
				DSS.doSimpleMsg("From doPFlowSolution.setGeneratordQdV(): " + DSS.CRLF + e.getMessage() + YMatrix.checkYMatrixforZeroes(), 7073);
				throw new SolverError("Aborting");
			}

			/* The above resets the active sparse set to hY */
			solutionInitialized = true;
		}

		switch (algorithm) {
		case Solution.NORMALSOLVE:
			doNormalSolution();
			break;
		case Solution.NEWTONSOLVE:
			doNewtonSolution();
			break;
		}

		DSS.activeCircuit.setIsSolved(convergedFlag);
		lastSolutionWasDirect = false;
	}

	/**
	 * Solve without load for initialization purposes.
	 *
	 * @throws SolveProblem
	 */
	public int solveZeroLoadSnapShot() throws SolveProblem {

		if (systemYChanged || seriesYInvalid)
			YMatrix.buildYMatrix(YMatrix.SERIESONLY, true);  // side effect: allocates V

		solutionCount += 1;  // unique number for this solution

		zeroInjCurr();  // Side effect: allocates InjCurr
		getSourceInjCurrents();  // Vsource and Isource only

		/* Make the series Y matrix the active matrix */
		if (YSeries == null)
			throw new SolveProblem("Series Y matrix not built yet in solveZeroLoadSnapshot().");
		Y = YSeries;

		if (DSS.activeCircuit.isLogEvents())
			Util.logThisEvent("Solve Sparse Set ZeroLoadSnapshot ...");

		solveSystem(nodeV);  // also sets voltages in radial part of the circuit if radial solution

		/* Reset the main system Y as the solution matrix */
		if ((YSystem != null) && !DSS.solutionAbort)
			Y = YSystem;

		return 0;
	}

	/**
	 * Set voltage bases using voltage at first node (phase) of a bus.
	 *
	 * @throws SolverError
	 */
	public void setVoltageBases() throws SolverError {
		boolean bZoneCalc, bZoneLock;
		Circuit ckt = DSS.activeCircuit;

		try {
			// don't allow the meter zones to auto-build in this load flow
			// solution, because the voltage bases are not available yet

			bZoneCalc = ckt.isMeterZonesComputed();
			bZoneLock = ckt.isZonesLocked();
			ckt.setMeterZonesComputed(true);
			ckt.setZonesLocked(true);

			solveZeroLoadSnapShot();

			for (int i = 0; i < ckt.getNumBuses(); i++) {
				Bus bus = ckt.getBus(i);
				bus.setKVBase( Util.nearestBasekV( nodeV[ bus.getRef(0) ].abs() * 0.001732 ) / DSS.SQRT3);  // l-n base kV
			}

			YMatrix.initializeNodeVbase();  // for convergence test

			ckt.setIsSolved(true);

			// now build the meter zones
			ckt.setMeterZonesComputed(bZoneCalc);
			ckt.setZonesLocked(bZoneLock);
			ckt.doResetMeterZones();

		} catch (SolveProblem e) {
			DSS.doSimpleMsg("From setVoltageBases().solveZeroLoadSnapShot(): " + DSS.CRLF + e.getMessage() + YMatrix.checkYMatrixforZeroes(), 7075);
			throw new SolverError("Aborting");
		}
	}

	public void snapShotInit() {
		setGeneratorDispRef();
		controlIteration   = 0;
		controlActionsDone = false;
		mostIterationsDone = 0;
		loadsNeedUpdating  = true;  // force the loads to update at least once
	}

	/**
	 * Snapshot checks with matrix rebuild.
	 *
	 * @throws ControlProblem
	 * @throws SolveProblem
	 */
	public void checkControls() throws ControlProblem, SolveProblem {
		if (controlIteration < maxControlIterations) {
			if (convergedFlag) {
				if (DSS.activeCircuit.isLogEvents())
					Util.logThisEvent("Control Iteration " + String.valueOf(controlIteration));
				sampleDoControlActions();
				checkFaultStatus();
			} else {
				controlActionsDone = true;  // stop solution process if failure to converge
			}
		}

		if (systemYChanged)
			YMatrix.buildYMatrix(YMatrix.WHOLEMATRIX, false);  // rebuild Y matrix, but V stays same
	}

	/**
	 * Solve for now once.
	 *
	 * @throws SolverError
	 * @throws ControlProblem
	 * @throws SolveProblem
	 */
	public int solveSnap() throws SolverError, ControlProblem, SolveProblem {
		int result = 0;
		int totalIterations = 0;

		snapShotInit();

		while (!controlActionsDone && (controlIteration < maxControlIterations)) {
			controlIteration += 1;

			result = solveCircuit();  // do circuit solution w/o checking controls

			/* Now check controls */
			/* TODO $IFDEF DLL_ENGINE */
//			fire_checkControls();
			/* TODO $ENDIF */
			checkControls();

			/* For reporting max iterations per control iteration */
			if (iteration > mostIterationsDone)
				mostIterationsDone = iteration;

			totalIterations = totalIterations + iteration;

		}

		if (!controlActionsDone && (controlIteration >= maxControlIterations)) {
			DSS.doSimpleMsg("Warning: max control iterations exceeded. " + DSS.CRLF + "Tip: Show eventLog to debug control settings.", 485);
			DSS.solutionAbort = true;  // this will stop this message in dynamic power flow modes
		}

		if (DSS.activeCircuit.isLogEvents())
			Util.logThisEvent("Solution done");

		/* TODO $IFDEF DLL_ENGINE */
//		fire_StepControls();
		/* $ENDIF */

		iteration = totalIterations;  /* so that it reports a more interesting number */

		return result;
	}

	/**
	 * Solve for now once, direct solution.
	 *
	 * @throws SolveProblem
	 */
	public int solveDirect() throws SolveProblem {
		loadsNeedUpdating = true;  // force possible update of loads and generators

		if (systemYChanged)
			YMatrix.buildYMatrix(YMatrix.WHOLEMATRIX, true);  // side effect: allocates V

		solutionCount += 1;  // unique number for this solution

		zeroInjCurr();  // side effect: allocates injCurr
		getSourceInjCurrents();

		// Pick up PCElement injections for Harmonics mode and Dynamics mode
		// Ignore these injections for powerflow; Use only admittance in Y matrix
		if (isDynamicModel || isHarmonicModel)
			getPCInjCurr();

		if (solveSystem(nodeV) == 1) {  // solve with zero injection current
			DSS.activeCircuit.setIsSolved(true);
			convergedFlag = true;
		}

		iteration = 1;
		lastSolutionWasDirect = true;

		return 0;
	}

	/**
	 * SolveSnap sans control iteration.
	 *
	 * @throws SolverError
	 */
	public int solveCircuit() throws SolverError {
		if (loadModel == DSS.ADMITTANCE) {
			try {
				solveDirect();  // no sense horsing around when it's all admittance
			} catch (SolveProblem e) {
				DSS.doSimpleMsg("From solveSnap().solveDirect(): " + DSS.CRLF + e.getMessage() + YMatrix.checkYMatrixforZeroes(), 7075);
				throw new SolverError("Aborting");
			}
		} else {
			try {
				if (systemYChanged)
					YMatrix.buildYMatrix(YMatrix.WHOLEMATRIX, true);  // Side effect: allocates V
				doPFlowSolution();
			} catch (SolveProblem e) {
				DSS.doSimpleMsg("From solveSnap().doPFlowSolution(): " + DSS.CRLF + e.getMessage() + YMatrix.checkYMatrixforZeroes(), 7074);
				throw new SolverError("Aborting");
			}
		}
		return 0;
	}

	private void zeroInjCurr() {
		for (int i = 0; i < DSS.activeCircuit.getNumNodes(); i++)
			currents[i] = Complex.ZERO;
	}

	/**
	 * Get inj currents from all enabled PC devices.
	 */
	private void getPCInjCurr() {
		Circuit ckt = DSS.activeCircuit;
		for (PCElement pElem : ckt.getPCElements())
			if (pElem.isEnabled())
				pElem.injCurrents();  // uses nodeRef to add current into injCurr array
	}

	@Override
	public void dumpProperties(OutputStream out, boolean complete) {
		// TODO Translate this method
		throw new UnsupportedOperationException();
	}

	/**
	 * Difference between two node voltages.
	 */
	public Complex vDiff(int i, int j) {
		return nodeV[i].subtract(nodeV[j]);  // V1-V2;
	}

	public void writeConvergenceReport(String fileName) {
		FileWriter fw;
		PrintWriter f;

		try {
			fw = new FileWriter(fileName);
			f = new PrintWriter(fw);

			f.println();
			f.println("-------------------");
			f.println("Convergence Report:");
			f.println("-------------------");
			f.println("\"Bus.Node\", \"Error\", \"|V|\",\"Vbase\"");

			Circuit ckt = DSS.activeCircuit;

			for (int i = 0; i < ckt.getNumNodes(); i++) {
				NodeBus nb = ckt.getMapNodeToBus()[i];
				f.print("\"" + Util.pad((ckt.getBusList().get(nb.busRef)+"."+String.valueOf(nb.nodeNum)+"\""), 18) );
				f.printf(", %10.5s", errorSaved[i]);
				f.printf(", %14s", VMagSaved[i]);
				f.printf(", %14s", nodeVBase[i]);  // TODO Check text padding
				f.println();
			}

			f.println();
			f.printf("Max Error = %10.5s", maxError);
			f.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			Util.fireOffEditor(fileName);
		}
	}

	private void sumAllCurrents() {
		Circuit ckt = DSS.activeCircuit;
		for (CktElement pElem : ckt.getCktElements())
			pElem.sumCurrents();  // sum terminal currents into system currents array
	}

	public void doControlActions() {
		boolean succ;
		int[] mHour, xHour = new int[1];
		double[] mSec, xSec = new double[1];

		Circuit ckt = DSS.activeCircuit;

		switch (controlMode) {
		// execute the nearest set of control actions time-wise.
		case DSS.CTRLSTATIC:
			if (ckt.getControlQueue().isEmpty()) {
				controlActionsDone = true;
			} else {
				ckt.getControlQueue().doNearestActions(xHour, xSec);  // ignore time advancement
			}
			break;
		case DSS.EVENTDRIVEN:
			mHour = new int[1];
			mSec = new double[1];
			succ = ckt.getControlQueue().doNearestActions(mHour, mSec);  // advances time
			intHour = mHour[1];
			dynaVars.t = mSec[0];
			if (!succ)
				controlActionsDone = true;
			break;
		case DSS.TIMEDRIVEN:
			if (!ckt.getControlQueue().doActions(intHour, dynaVars.t))
				controlActionsDone = true;
			break;
		}
	}

	public void sampleControlDevices() throws ControlProblem {
		ControlElem controlDevice = null;
		Circuit ckt = DSS.activeCircuit;
		try {
			// sample all controls and set action times in control queue.
			for (int i = 0; i < ckt.getDSSControls().size(); i++) {
				controlDevice = ckt.getDSSControls().get(i);
				if (controlDevice.isEnabled())
					controlDevice.sample();
			}
		} catch (Exception e) {
			DSS.doSimpleMsg("Error sampling control device \""+controlDevice.getName()+"\""+DSS.CRLF+"Error = "+e.getMessage(), 484);
			throw new ControlProblem("Solution aborted.");
		}
	}

	/**
	 * Sample and do.
	 *
	 * @throws ControlProblem
	 */
	public void sampleDoControlActions() throws ControlProblem {
		if (controlMode == DSS.CONTROLSOFF) {
			controlActionsDone = true;
		} else {
			sampleControlDevices();
			doControlActions();

			/* This variable lets control devices know the bus list has changed */
			DSS.activeCircuit.setControlBusNameRedefined(false);  // reset until next change
		}
	}

	public void setMode(int value) {
		Circuit ckt = DSS.activeCircuit;

		intHour    = 0;
		dynaVars.t = 0.0;
		updateDblHour();

		ckt.setTrapezoidalIntegration(false);

		if (!okForDynamics(value))
			return;
		if (!okForHarmonics(value))
			return;

		dynaVars.solutionMode = value;

		controlMode = defaultControlMode;  // revert to default mode
		loadModel   = defaultLoadModel;

		isDynamicModel  = false;
		isHarmonicModel = false;

		solutionInitialized  = false;  // reinitialize solution when mode set (except dynamics)
		preserveNodeVoltages = false;  // don't do this unless we have to

		// reset defaults for solution modes
		switch (dynaVars.solutionMode) {
		case Dynamics.PEAKDAY:
			dynaVars.h    = 3600.0;
			numberOfTimes = 24;
			break;
		case Dynamics.DAILYMODE:
			dynaVars.h    = 3600.0;
			numberOfTimes = 24;
			break;
		case Dynamics.SNAPSHOT:
			intervalHrs   = 1.0;
			numberOfTimes = 1;
			break;
		case Dynamics.YEARLYMODE:
			intervalHrs   = 1.0;
			dynaVars.h    = 3600.0;
			numberOfTimes = 8760;
			break;
		case Dynamics.DUTYCYCLE:
			dynaVars.h  = 1.0;
			controlMode = DSS.TIMEDRIVEN;
			break;
		case Dynamics.DYNAMICMODE:
			dynaVars.h     = 0.001;
			controlMode    = DSS.TIMEDRIVEN;
			isDynamicModel = true;
			preserveNodeVoltages = true;  // need to do this in case Y changes during this mode
			break;
		case Dynamics.GENERALTIME:
			intervalHrs   = 1.0;
			dynaVars.h    = 3600.0;
			numberOfTimes = 1;  // just one time step per solve call expected
			break;
		case Dynamics.MONTECARLO1:
			intervalHrs    = 1.0;
			break;
		case Dynamics.MONTECARLO2:
			dynaVars.h     = 3600.0;
			break;
		case Dynamics.MONTECARLO3:
			intervalHrs    = 1.0;
			break;
		case Dynamics.MONTEFAULT:
			isDynamicModel = true;
			break;
		case Dynamics.FAULTSTUDY:
			isDynamicModel = true;
			break;
		case Dynamics.LOADDURATION1:
			dynaVars.h = 3600.0;
			ckt.setTrapezoidalIntegration(true);
			break;
		case Dynamics.LOADDURATION2:
			intHour = 1;
			ckt.setTrapezoidalIntegration(true);
			break;
		case Dynamics.AUTOADDFLAG:
			intervalHrs = 1.0;
			ckt.getAutoAddObj().setModeChanged(true);
			break;
		case Dynamics.HARMONICMODE:
			controlMode     = DSS.CONTROLSOFF;
			isHarmonicModel = true;
			loadModel       = DSS.ADMITTANCE;
			preserveNodeVoltages = true;  // need to do this in case Y changes during this mode
			break;
		}

		/* Moved here 9-8-2007 so that mode is changed before reseting monitors, etc. */

		// reset meters and monitors
		DSS.monitorClass.resetAll();
		DSS.energyMeterClass.resetAll();
		Util.doResetFaults();
		Util.doResetControls();
	}

	private void addInAuxCurrents(int solveType) {
		Circuit ckt = DSS.activeCircuit;

		//for (int i = 0; i < ckt.getNumNodes(); i++)
		//	currents[i] = currents[i].add(auxCurrents[i]);
		// for now, only AutoAddObj uses this.

		if (dynaVars.solutionMode == Dynamics.AUTOADDFLAG)
			ckt.getAutoAddObj().addCurrents(solveType);
	}

	public void zeroAuxCurrents() {
		Circuit ckt = DSS.activeCircuit;

		for (int i = 0; i < ckt.getNumNodes(); i++)
			auxCurrents[i] = Complex.ZERO;
	}

	public void checkFaultStatus() {
		Circuit ckt = DSS.activeCircuit;

		for (FaultObj pFault : ckt.getFaults())
			pFault.checkStatus(controlMode);
	}

	/**
	 * This procedure is called for solveDirect and any other solution method
	 * that does not get the injection currents for PC elements normally. In
	 * dynamics mode, generators are voltage sources ...
	 */
	private void getMachineInjCurrents() {
		// do machines in dynamics mode
		if (isDynamicModel) {
			Circuit ckt = DSS.activeCircuit;
			for (GeneratorObj pElem : ckt.getGenerators())
				if (pElem.isEnabled())
					pElem.injCurrents();  // uses nodeRef to add current into injCurr array
		}
	}

	private boolean okForDynamics(int value) {
		boolean valueIsDynamic;
		boolean result = true;

		switch (value) {
		case Dynamics.MONTEFAULT:
			valueIsDynamic = true;
			break;
		case Dynamics.DYNAMICMODE:
			valueIsDynamic = true;
			break;
		case Dynamics.FAULTSTUDY:
			valueIsDynamic = true;
			break;
		default:
			valueIsDynamic = false;
			break;
		}

		/* When we go in and out of dynamics mode, we have to do some special things */
		if (isDynamicModel && !valueIsDynamic)
			Util.invalidateAllPCElements();  // force recomp of YPrims when we leave dynamics mode

		if (!isDynamicModel && valueIsDynamic) {  // see if conditions right for going into dynamics

			if (DSS.activeCircuit.isSolved()) {
				Util.calcInitialMachineStates();  // set state variables for machines (loads and generators)
			} else {
				/* Raise error message if not solved */
				DSS.doSimpleMsg("Circuit must be solved in a non-dynamic mode before entering dynamics or fault study modes!" + DSS.CRLF +
						"If you attempted to solve, then the solution has not yet converged.", 486);
				if (DSS.inRedirect)
					DSS.redirectAbort = true;
				result = false;
			}
		}
		return result;
	}

	/**
	 * When we go in and out of harmonics mode, we have to do some special things.
	 */
	private boolean okForHarmonics(int value) {
		boolean result = true;
		Circuit ckt = DSS.activeCircuit;

		if (isHarmonicModel && !(value == Dynamics.HARMONICMODE)) {
			Util.invalidateAllPCElements();  // force recomp of YPrims when we leave harmonics mode
			frequency = ckt.getFundamental();   // resets everything to norm
		}

		if (!isHarmonicModel && (value == Dynamics.HARMONICMODE)) {  // see if conditions right for going into harmonics

			if (ckt.isSolved() && (frequency == ckt.getFundamental())) {
				if (!Util.initializeForHarmonics()) {  // set state variables for machines (loads and generators) and sources
					result = false;
					if (DSS.inRedirect)
						DSS.redirectAbort = true;
				}
			} else {
				DSS.doSimpleMsg("Circuit must be solved in a fundamental frequency power flow or direct mode before entering harmonics mode!", 487);
				if (DSS.inRedirect)
					DSS.redirectAbort = true;
				result = false;
			}
		}
		return result;
	}

	public void setFrequency(double value) {
		if (frequency != value) {
			frequencyChanged = true;  // force rebuild of all Y primitives
			systemYChanged = true;    // force rebuild of system Y
		}

		frequency = value;

		Circuit ckt = DSS.activeCircuit;
		if (ckt != null)
			harmonic = frequency / ckt.getFundamental();  // make sure harmonic stays in synch
	}

	public void incrementTime() {
		dynaVars.t = dynaVars.t + dynaVars.h;
		while (dynaVars.t >= 3600.0) {
			intHour += 1;
			dynaVars.t = dynaVars.t - 3600.0;
		}
		updateDblHour();
	}

	@Override
	public void initPropertyValues(int arrayOffset) {
		setPropertyValue(0, "");

		super.initPropertyValues(Solution.NumPropsThisClass - 1);
	}

	public void setYear(int value) {

		if (DSS.DIFilesAreOpen)
			DSS.energyMeterClass.closeAllDIFiles();

		year = value;
		intHour = 0;  /* Change year, start over */
		dynaVars.t = 0.0;
		updateDblHour();

		DSS.energyMeterClass.resetAll();  // force any previous year data to complete
	}

	public void saveVoltages() {
		FileWriter fd;
		PrintWriter f;
		Complex volts;
		String busName;

		try {
			fd = new FileWriter(DSS.circuitName_ + "SavedVoltages.txt");
			f = new PrintWriter(fd);

			Circuit ckt = DSS.activeCircuit;

			for (int i = 0; i < ckt.getNumBuses(); i++) {
				busName = ckt.getBusList().get(i);
				for (int j = 0; j < ckt.getBus(i).getNumNodesThisBus(); j++) {
					volts = nodeV[ckt.getBus(i).getRef(j)];
					f.println(busName + ", " + ckt.getBus(i).getNum(j) + String.format(", %-.7g, %-.7g", volts.abs(), ComplexUtil.degArg(volts)));
				}
			}

			f.close();
			DSS.globalResult = DSS.circuitName_ + "SavedVoltages.txt";

		} catch (Exception e) {
			DSS.doSimpleMsg("Error opening saved voltages file: "+e.getMessage(), 488);
			return;
		}
	}

	/**
	 * *************  MAIN SOLVER CALL *************
	 *
	 * @throws SolveProblem
	 */
	private int solveSystem(Complex[] V) throws SolveProblem {
		int retCode;
		long iRes = 0;
		double dRes = 0;
		double[] dp = new double[1];
		int[] ip = new int[1];

		/* Note: NodeV[0] = 0 + j0 always. Therefore, pass the address of the element 1 of the array. */
		try {
			/* log KLUSolve function calls */
			YMatrix.setLogFile("KLU_Log.txt", 1);

			retCode = YMatrix.solveSparseSet(Y, V, 1, currents, 1);  // solve for present injCurr

			/* information functions */
			//YMatrix.getFlops(Y, dRes);
			//YMatrix.getRGrowth(Y, dRes);
			YMatrix.getRCond(Y, dp);
			dRes = dp[0];
			//YMatrix.getCondEst(Y, dRes); // this can be expensive
			//YMatrix.getSize(Y, iRes);
			YMatrix.getNNZ(Y, ip);
			iRes = ip[0];
			YMatrix.getSparseNNZ(Y, ip);
			iRes = ip[0];
			//YMatrix.getSingularCol(Y, iRes);
		} catch (Exception e) {
			throw new SolveProblem("Error solving system Y matrix. Sparse matrix solver reports numerical error: " + e.getMessage());
		}

		return retCode;
	}

	public void updateDblHour() {
		dblHour = intHour + dynaVars.t / 3600.0;
	}

	/**
	 * Updates voltages for each bus from NodeV.
	 */
	public void updateVBus() {
		Bus bus;
		Circuit ckt = DSS.activeCircuit;

		for (int i = 0; i < ckt.getNumBuses(); i++) {
			bus = ckt.getBus(i);
			if (bus.getVBus() != null)
				for (int j = 0; j < bus.getNumNodesThisBus(); j++)
					bus.getVBus()[j] = nodeV[bus.getRef(j)];
		}
	}

	/**
	 * Opposite of updateVBus().
	 */
	public void restoreNodeVFromVbus() {
		Bus bus;
		Circuit ckt = DSS.activeCircuit;

		for (int i = 0; i < ckt.getNumBuses(); i++) {
			bus = ckt.getBus(i);
			if (bus.getVBus() != null)
				for (int j = 0; j < bus.getNumNodesThisBus(); j++)
					nodeV[bus.getRef(j)] = bus.getVBus()[j];
		}
	}

	/**
	 * Similar to solveDirect(); used for initialization.
	 * Solves present Y matrix with no injection sources except voltage and current sources.
	 *
	 * @throws SolveProblem
	 */
	public int solveYDirect() throws SolveProblem {
		zeroInjCurr();  // side effect: allocates injCurr
		getSourceInjCurrents();
		if (isDynamicModel) getPCInjCurr();  // Need this in dynamics mode to pick up additional injections

		solveSystem(nodeV);  // solve with zero injection current
		return 0;
	}

	public Complex getNodeV(int idx) {
		return nodeV[idx];
	}

	public Complex getCurrent(int idx) {
		return currents[idx];
	}

	public void setCurrent(int idx, Complex current) {
		currents[idx] = current;
	}

	public double getFrequency() {
		return frequency;
	}

	public int getMode() {
		return dynaVars.solutionMode;
	}

	public int getYear() {
		return year;
	}

	public int getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(int alg) {
		algorithm = alg;
	}

	public Complex[] getAuxCurrents() {
		return auxCurrents;
	}

	public void setAuxCurrents(Complex[] value) {
		auxCurrents = value;
	}

	public boolean isControlActionsDone() {
		return controlActionsDone;
	}

	public void setControlActionsDone(boolean value) {
		controlActionsDone = value;
	}

	public int getControlIteration() {
		return controlIteration;
	}

	public void setControlIteration(int iteration) {
		controlIteration = iteration;
	}

	public int getControlMode() {
		return controlMode;
	}

	public void setControlMode(int mode) {
		controlMode = mode;
	}

	public double getConvergenceTolerance() {
		return convergenceTolerance;
	}

	public void setConvergenceTolerance(double tolerance) {
		convergenceTolerance = tolerance;
	}

	public boolean isConvergedFlag() {
		return convergedFlag;
	}

	public void setConvergedFlag(boolean flag) {
		convergedFlag = flag;
	}

	public int getDefaultControlMode() {
		return defaultControlMode;
	}

	public void setDefaultControlMode(int controlMode) {
		defaultControlMode = controlMode;
	}

	public int getDefaultLoadModel() {
		return defaultLoadModel;
	}

	public void setDefaultLoadModel(int loadModel) {
		defaultLoadModel = loadModel;
	}

	public boolean isDoAllHarmonics() {
		return doAllHarmonics;
	}

	public void setDoAllHarmonics(boolean value) {
		doAllHarmonics = value;
	}

	public boolean isDynamicsAllowed() {
		return dynamicsAllowed;
	}

	public void setDynamicsAllowed(boolean allowed) {
		dynamicsAllowed = allowed;
	}

	public DynamicsRec getDynaVars() {
		return dynaVars;
	}

	public void setDynaVars(DynamicsRec vars) {
		dynaVars = vars;
	}

	public double[] getErrorSaved() {
		return errorSaved;
	}

	public void setErrorSaved(double[] value) {
		errorSaved = value;
	}

	public boolean isFirstIteration() {
		return firstIteration;
	}

	public void setFirstIteration(boolean iteration) {
		firstIteration = iteration;
	}

	public boolean isFrequencyChanged() {
		return frequencyChanged;
	}

	public void setFrequencyChanged(boolean value) {
		frequencyChanged = value;
	}

	public double getHarmonic() {
		return harmonic;
	}

	public void setHarmonic(double value) {
		harmonic = value;
	}

	public double[] getHarmonicList() {
		return harmonicList;
	}

	public void setHarmonicList(double[] value) {
		harmonicList = value;
	}

	public int getHarmonicListSize() {
		return harmonicListSize;
	}

	public void setHarmonicListSize(int size) {
		harmonicListSize = size;
	}

	public int getIntHour() {
		return intHour;
	}

	public void setIntHour(int hour) {
		this.intHour = hour;
	}

	public double getDblHour() {
		return dblHour;
	}

	public void setDblHour(double hour) {
		this.dblHour = hour;
	}

	public UUID getYSystem() {
		return YSystem;
	}

	public void setYSystem(UUID value) {
		YSystem = value;
	}

	public UUID getYSeries() {
		return YSeries;
	}

	public void setYSeries(UUID value) {
		YSeries = value;
	}

	public UUID getY() {
		return Y;
	}

	public void setY(UUID y) {
		Y = y;
	}

	public double getIntervalHrs() {
		return intervalHrs;
	}

	public void setIntervalHrs(double interval) {
		intervalHrs = interval;
	}

	public boolean isDynamicModel() {
		return isDynamicModel;
	}

	public void setDynamicModel(boolean isDynamic) {
		isDynamicModel = isDynamic;
	}

	public boolean isHarmonicModel() {
		return isHarmonicModel;
	}

	public void setHarmonicModel(boolean isHarmonic) {
		isHarmonicModel = isHarmonic;
	}

	public int getIteration() {
		return iteration;
	}

	public void setIteration(int iter) {
		iteration = iter;
	}

	public int getLoadModel() {
		return loadModel;
	}

	public void setLoadModel(int model) {
		loadModel = model;
	}

	public boolean lastSolutionWasDirect() {
		return lastSolutionWasDirect;
	}

	public void setLastSolutionWasDirect(boolean value) {
		lastSolutionWasDirect = value;
	}

	public boolean loadsNeedUpdating() {
		return loadsNeedUpdating;
	}

	public void setLoadsNeedUpdating(boolean value) {
		loadsNeedUpdating = value;
	}

	public int getMaxControlIterations() {
		return maxControlIterations;
	}

	public void setMaxControlIterations(int iterations) {
		maxControlIterations = iterations;
	}

	public double getMaxError() {
		return maxError;
	}

	public void setMaxError(double error) {
		maxError = error;
	}

	public int getMaxIterations() {
		return maxIterations;
	}

	public void setMaxIterations(int iterations) {
		maxIterations = iterations;
	}

	public int getMostIterationsDone() {
		return mostIterationsDone;
	}

	public void setMostIterationsDone(int value) {
		mostIterationsDone = value;
	}

	public double[] getNodeVBase() {
		return nodeVBase;
	}

	public void setNodeVBase(double[] base) {
		nodeVBase = base;
	}

	public int getNumberOfTimes() {
		return numberOfTimes;
	}

	public void setNumberOfTimes(int number) {
		numberOfTimes = number;
	}

	public boolean isPreserveNodeVoltages() {
		return preserveNodeVoltages;
	}

	public void setPreserveNodeVoltages(boolean preserve) {
		preserveNodeVoltages = preserve;
	}

	public int getRandomType() {
		return randomType;
	}

	public void setRandomType(int type) {
		randomType = type;
	}

	public boolean isSeriesYInvalid() {
		return seriesYInvalid;
	}

	public void setSeriesYInvalid(boolean invalid) {
		seriesYInvalid = invalid;
	}

	public int getSolutionCount() {
		return solutionCount;
	}

	public void setSolutionCount(int count) {
		solutionCount = count;
	}

	public boolean isSolutionInitialized() {
		return solutionInitialized;
	}

	public void setSolutionInitialized(boolean value) {
		solutionInitialized = value;
	}

	public boolean isSystemYChanged() {
		return systemYChanged;
	}

	public void setSystemYChanged(boolean value) {
		systemYChanged = value;
	}

	public boolean useAuxCurrents() {
		return useAuxCurrents;
	}

	public void setUseAuxCurrents(boolean value) {
		useAuxCurrents = value;
	}

	public double[] getVMagSaved() {
		return VMagSaved;
	}

	public void setVMagSaved(double[] value) {
		VMagSaved = value;
	}

	public boolean isVoltageBaseChanged() {
		return voltageBaseChanged;
	}

	public void setVoltageBaseChanged(boolean value) {
		voltageBaseChanged = value;
	}

	public Complex[] getNodeV() {
		return nodeV;
	}

	public void setNodeV(Complex[] value) {
		nodeV = value;
	}

	public Complex[] getCurrents() {
		return currents;
	}

	public void setCurrents(Complex[] value) {
		currents = value;
	}

}
