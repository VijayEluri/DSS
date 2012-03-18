/**
 * Copyright (C) 2008-2012 Electric Power Research Institute, Inc.
 * Copyright (C) 2009-2012 Richard Lincoln
 * All rights reserved.
 */
package com.ncond.dss.common;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.math.complex.Complex;

import com.ncond.dss.common.exceptions.ControlProblem;
import com.ncond.dss.common.exceptions.SolverError;
import com.ncond.dss.common.exceptions.SolverProblem;
import com.ncond.dss.common.types.Algorithm;
import com.ncond.dss.common.types.AutoAddType;
import com.ncond.dss.common.types.ControlMode;
import com.ncond.dss.common.types.SolutionLoadModel;
import com.ncond.dss.conversion.Generator;
import com.ncond.dss.delivery.Capacitor;
import com.ncond.dss.delivery.PDElement;
import com.ncond.dss.executive.Executive;
import com.ncond.dss.meter.EnergyMeterObj;
import com.ncond.dss.shared.HashList;

/**
 * Unit for processing the AutoAdd solution functions.
 *
 * Note: Make sure this class in instantiated after EnergyMeter class.
 *
 * There is one of these per circuit.
 *
 */
@Getter @Setter
public class AutoAdd {

	private Generator generatorClass;
	private Capacitor capacitorClass;

	private int[] busIdxList;
	private int busIdxListSize;
	private boolean busIdxListCreated;
	private int lastAddedGenerator;
	private int lastAddedCapacitor;

	private int busIndex;
	private int phases;

	private double Ycap;
	private Complex genVA;

	private double kWLosses, baseLosses, puLossImprovement;
	private double kWEEN , baseEEN, puEENImprovement;

	private File logFile;  // log file

	private int progressCount;


	/* AutoAdd mode variables */
	protected double genKW, genPF, genKVAr, capKVAr;
	protected AutoAddType addType;

	protected boolean modeChanged;

	public AutoAdd() {

		busIdxListCreated = false;
		generatorClass = (Generator) DSS.DSSClassList.get(DSS.classNames.find("generator"));
		capacitorClass = (Capacitor) DSS.DSSClassList.get(DSS.classNames.find("capacitor"));

		// autoAdd defaults
		genKW   = 1000.0;
		genPF   = 1.0;
		capKVAr = 600.0;
		addType = AutoAddType.GEN;
		lastAddedGenerator = 0;
		lastAddedCapacitor = 0;

		modeChanged = true;
	}

	private static double sumSelectedRegisters(EnergyMeterObj mtr, int[] regs, int count) {
		double result = 0.0;
		for (int i = 0; i < count; i++)
			result += mtr.getRegister(regs[i]) * mtr.getTotalsMask(regs[i]);
		return result;
	}

	/**
	 * Makes a list of unique bus names.
	 *
	 * If autoAddBusList in activeCircuit is not null, use this list.
	 * Otherwise, use the element lists in energy meters.
	 * If no energy meters, use all the buses in the active circuit.
	 */
	public void makeBusList() {
		int retval;
		String bName;
		PDElement PDElem;
		HashList busList;

		Circuit ckt = DSS.activeCircuit;

		if (busIdxListCreated) busIdxList = new int[0];

		busIdxListCreated = false;

		// autoAddBusList exists in active circuit, use it (see set autoBusList)
		if (ckt.getAutoAddBusList().listSize() > 0) {
			busList = ckt.getAutoAddBusList();
		} else  {
			if (ckt.getEnergyMeters().size() == 0) {
				// no energy meters in circuit
				// include all buses in the circuit
				busIdxListSize = ckt.getBusList().listSize();
				busIdxList = Util.resizeArray(busIdxList, busIdxListSize);

				for (int i = 0; i < busIdxListSize; i++)
					busIdxList[i] = i;

				busIdxListCreated = true;
				return;
			} else {
				/* Construct bus list from energy meters zone lists */

				// include only buses in energy meter lists
				// consider all meters
				busList = new HashList(ckt.getNumBuses());

				for (EnergyMeterObj pMeter : ckt.getEnergyMeters()) {
					if (pMeter.getBranchList() != null) {
						PDElem = (PDElement) pMeter.getBranchList().getFirst();  // FIXME: generic tree
						while (PDElem != null) {  // add only unique bus names
							for (int i = 0; i < PDElem.getNumTerms(); i++) {
								bName = Util.stripExtension(PDElem.getBus(i));

								retval = busList.find(bName);
								if (retval == -1)
									busList.add(bName);  // return value is index of bus
							}
							PDElem = (PDElement) pMeter.getBranchList().goForward();
						}
					}
				}
			}
		}

		// make busIdxList from busList
		busIdxListSize = busList.listSize();
		busIdxList = Util.resizeArray(busIdxList, busIdxListSize);

		for (int i = 0; i < busIdxListSize; i++)
			busIdxList[i] = ckt.getBusList().find(busList.get(i));

		busIdxListCreated = true;
	}

	/**
	 * Returns losses in metered part of circuit + weighted EEN values.
	 *
	 * If no meters, returns just total losses in circuit.
	 * Base everything on gen kW.
	 */
	public double getWeightedLosses() {
		double losses;
		Circuit ckt = DSS.activeCircuit;

		computekWLossesEEN();

		if (ckt.getEnergyMeters().size() == 0) {
			// no energymeters in circuit
			// just go by total system losses
			puLossImprovement = (baseLosses - kWLosses) / genKW;
			puEENImprovement = 0.0;
			losses = puLossImprovement;
		} else {
			puLossImprovement = (baseLosses - kWLosses) / genKW;
			puEENImprovement = (baseEEN - kWEEN) / genKW;
			losses = ckt.getLossWeight() * puLossImprovement + ckt.getUEWeight() * puEENImprovement;
		}
		return losses;
	}

	public void appendToFile(String fileName, String s) {
		String fName = DSS.dataDirectory + DSS.circuitName_ + "AutoAdded" + fileName + ".txt";

		try {
			FileWriter fw = new FileWriter(fName, true);
			PrintWriter pw = new PrintWriter(fw);

			pw.println(s);

			fw.close();
			pw.close();
		} catch (IOException e) {
			DSS.doSimpleMsg("Error trying to append to " + fName +
					DSS.CRLF + e.getMessage(), 438);
		}
	}

	private String getUniqueGenName() {
		String trialName = "";
		boolean done = false;

		while (!done) {
			done = true;
			lastAddedGenerator += 1;
			trialName = "Gadd" + String.valueOf(lastAddedGenerator);
			if (generatorClass.find(trialName) != null) done = false;
		}

		return trialName;
	}

	private String getUniqueCapName() {
		String trialName = "";
		boolean done = false;

		while (!done) {
			done = true;
			lastAddedCapacitor += 1;
			trialName = "Cadd" + String.valueOf(lastAddedCapacitor);
			if (capacitorClass.find(trialName) != null) done = false;
		}

		return trialName;
	}

	/**
	 * Automatically add caps or generators.
	 *
	 * Automatically add a specified size of generator or capacitor at the location
	 * that results in the lowest losses in either metered part of circuit or
	 * total circuit, if no meters.
	 *
	 * If metered, EEN is also added in with a selected weighting factor (see
	 * set ueweight= ... command).
	 *
	 * Thus, this algorithm placed generators and capacitors to minimize losses and
	 * potential unserved energy.
	 *
	 * @throws ControlProblem
	 * @throws SolverError
	 * @throws SolverProblem
	 */
	public int solve() throws SolverError, ControlProblem, SolverProblem {

		double lossImproveFactor, maxLossImproveFactor;
		int minLossBus, minBusPhases;
		String testBus;

		String commandString;

		double kVrat, testGenKW, testCapKVAr;
		int progressMax;

		Executive exec;
		FileWriter fw;
		PrintWriter pw;

		/* Algorithm:
		 *     1) makes a list of buses to check, either
		 *         a. Previously defined list
		 *         b. Meter zone lists
		 *         c. All buses, if neither of the above
		 *     2) Inject a current corresponding to the generator
		 *     3) Check test criteria
		 *     4) Save result
		 *     5) Add generator/capacitor to circuit
		 */
		int result = 0;

		Circuit ckt = DSS.activeCircuit;
		SolutionObj sol = ckt.getSolution();

		if (sol.getLoadModel() == SolutionLoadModel.ADMITTANCE) {
			sol.setLoadModel(SolutionLoadModel.POWERFLOW);
			sol.setSystemYChanged(true);  // force rebuild of system Y without loads
		}

		/* Do a preliminary snapshot solution to force definition of meter zones
		 * and set bus lists
		 */
		DSS.energyMeterClass.resetAll();
		if (sol.isSystemYChanged() || ckt.isBusNameRedefined()) {
			sol.solveSnap();
			modeChanged = true;
		}

		DSS.energyMeterClass.sampleAll();

		/* Check to see if bus base voltages have been defined. */
		if (ckt.getBus(ckt.getNumBuses()).getKVBase() == 0.0)
			sol.setVoltageBases();

		if (modeChanged) {
			makeBusList();  // make list of buses to check
			modeChanged = false;  /* Keep same busIdxList if no changes */
		}

		sol.setIntervalHrs(1.0);

		/* Start up Log File */
		logFile = new File(DSS.dataDirectory + DSS.circuitName_ + "AutoAddLog.csv");
		try {
			fw = new FileWriter(logFile, false);
			pw = new PrintWriter(fw);
			pw.println("\"Bus\", \"Base kV\", \"kW Losses\", \"% Improvement\", \"kW UE\", \"% Improvement\", \"Weighted Total\", \"Iterations\"");
			fw.close();  // close it now after clearing it out
			pw.close();
		} catch (IOException e1) {
			DSS.doSimpleMsg("Error writing AutoAdd log file: " + e1.getMessage(), 0);
		}


		// for this solution mode, only the peak load condition is taken into account
		// load is adjusted for growth by year.
		sol.setGeneratorDispRef();

		/* Turn regulators and caps off while we are searching */
		sol.setControlMode(ControlMode.CONTROLSOFF);

		setBaseLosses();  /* Establish base values */

		switch (addType) {
		case GEN:
			if (ckt.isPositiveSequence()) {
				testGenKW = genKW / 3.0;
			} else {
				testGenKW = genKW;
			}

			if (genPF != 0.0) {
				genKVAr = testGenKW * Math.sqrt(1.0 / Math.pow(genPF, 2) - 1.0);
				if (genPF < 0.0)
					genKVAr = -genKVAr;
			} else {  // Someone specified 0.0 PF
				genPF = 1.0;
				genKVAr = 0.0;
			}

			minLossBus = 0;  // null string
			maxLossImproveFactor = -1.0e50;  // very large negative number
			minBusPhases = 3;

			/* Progress meter */
			DSS.forms.progressCaption("AutoAdding Generators");
			progressMax = busIdxListSize;
			progressCount = 0;

			DSS.forms.progressFormCaption(String.format("Testing %d buses. Please Wait... ", busIdxListSize));
			DSS.forms.showPctProgress(0);

			for (int i = 0; i < busIdxListSize; i++) {
				progressCount += 1;
				busIndex = busIdxList[i];

				if (busIndex >= 0) {
					testBus = ckt.getBusList().get(busIndex);

					//DSSGlobals.forms.progressFormCaption("Testing bus" + TestBus);
					if ((progressCount % 20 == 0) || (i == busIdxListSize)) {
						DSS.forms.progressFormCaption(String.format("Testing bus %d/%d. ", i, busIdxListSize));
						DSS.forms.showPctProgress((100 * progressCount) / progressMax);
					}

					DSS.energyMeterClass.resetAll();

					/* Get the number of phases at this bus and the node ref and add into the aux current array */

					/* Assume either a 3-phase or 1-phase generator */
					phases = (ckt.getBus(busIndex).getNumNodesThisBus() < 3) ? 1 : 3;

					genVA = new Complex(1000.0 * testGenKW/phases, 1000.0 * genKVAr/phases) ;

					/* - - - - - - - - - Solution - - - - - - - - - - - - - - - */
					ckt.setSolved(false);

					sol.setUseAuxCurrents(true);  // calls injCurrents on callback
					sol.solveSnap();

					if (ckt.isSolved()) {
						/* Only do this if solution converged else something might break
						 * in meter sampling.
						 */
						DSS.energyMeterClass.sampleAll();

						lossImproveFactor = getWeightedLosses();

						try {
							fw = new FileWriter(logFile, true);  // append
							pw = new PrintWriter(fw);

							pw.printf("\"%s\", %-g", testBus, ckt.getBus(busIndex).getKVBase() * DSS.SQRT3);
							pw.printf(", %-g, %-g", kWLosses, puLossImprovement * 100.0);
							pw.printf(", %-g, %-g", kWEEN, puEENImprovement * 100.0);
							pw.printf(", %-g, %d", lossImproveFactor, sol.getIteration());
							pw.println();

							fw.close();
							pw.close();
						} catch (IOException e2) {
							DSS.doSimpleMsg("Error writing AutoAdd log file: " + e2.getMessage(), 0);
						}

						if (lossImproveFactor > maxLossImproveFactor) {
							maxLossImproveFactor = lossImproveFactor;
							minLossBus = busIndex;
							minBusPhases = phases;
						}
					}
				}

				if (DSS.solutionAbort) break;
			}

			/* Put control mode back to default before inserting generator for real */
			sol.setControlMode(ControlMode.CTRLSTATIC);
			sol.setUseAuxCurrents(false);

			if (minLossBus > 0) {
				exec = Executive.getInstance();

				if (minBusPhases >= 3) {
					kVrat = ckt.getBus(minLossBus).getKVBase() * DSS.SQRT3;
				} else {
					kVrat = ckt.getBus(minLossBus).getKVBase();
				}

				commandString = "new, generator." + getUniqueGenName() +
						", bus1=\"" + ckt.getBusList().get(minLossBus) +
						"\", phases=" + minBusPhases +
						", kv="+ String.format("%-g", kVrat) +
						", kw=" + String.format("%-g", testGenKW) +
						", " + String.format("%5.2f", genPF) +
						String.format("! Factor =  %-g (%-.3g, %-.3g)",
								maxLossImproveFactor,
								ckt.getLossWeight(),
								ckt.getUEWeight());
				exec.setCommand(commandString);  // defines generator

				// append this command to '...AutoAddedGenerators.txt'
				appendToFile("Generators", commandString);

				sol.solveSnap();  // force rebuilding of lists
			}

			// return location of added generator
			DSS.globalResult = ckt.getBusList().get(minLossBus) +
					String.format(", %-g", maxLossImproveFactor);

			DSS.forms.progressHide();

			break;
		case CAP:
			minLossBus = 0;  // null string
			maxLossImproveFactor = -1.0e50;  // very large negative number
			minBusPhases = 3;

			testCapKVAr = ckt.isPositiveSequence() ? capKVAr / 3.0 : capKVAr;

			/* Progress meter */
			DSS.forms.progressCaption("AutoAdding Capacitors");
			progressMax = busIdxListSize;
			progressCount = 0;

			for (int i = 0; i < busIdxListSize; i++) {
				progressCount += 1;
				/* Make sure test bus is actually in the circuit */
				busIndex = busIdxList[i];
				if (busIndex >= 0) {
					testBus = ckt.getBusList().get(busIndex);
					DSS.forms.progressFormCaption("Testing bus " + testBus);
					DSS.forms.showPctProgress((100 * progressCount) / progressMax);

					DSS.energyMeterClass.resetAll();

					/* Get the number of phases at this bus and the node ref and add
					 * into the aux current array */

					/* Assume either a 3-phase or 1-phase capacitor */
					phases = (ckt.getBus(busIndex).getNumNodesThisBus() < 3) ? 1 : 3;

					// apply the capacitor at the bus rating

					kVrat = ckt.getBus(busIndex).getKVBase();  // L-N Base kV
					Ycap = (testCapKVAr * 0.001 / phases ) / (kVrat * kVrat) ;


					/* - - - - - - - - - Solution - - - - - - - - - - - - - - - */
					ckt.setSolved(false);

					sol.setUseAuxCurrents(true);  // calls injCurrents on callback
					sol.solveSnap();

					if (ckt.isSolved()) {
						/* Only do this if solution converged else something might
						 * break in meter sampling */

						DSS.energyMeterClass.sampleAll();

						lossImproveFactor = getWeightedLosses();

						try {
							fw = new FileWriter(logFile, true);  // append
							pw = new PrintWriter(fw);
							pw.printf("\"%s\", %-g", testBus, ckt.getBus(busIndex).getKVBase() * DSS.SQRT3);
							pw.printf(", %-g, %-g", kWLosses, puLossImprovement * 100.0);
							pw.printf(", %-g, %-g", kWEEN, puEENImprovement * 100.0);
							pw.printf(", %-g, %d", lossImproveFactor, sol.getIteration());
							pw.println();
							fw.close();
							pw.close();
						} catch (IOException e3) {
							DSS.doSimpleMsg("Error writing AutoAdd log file: " + e3.getMessage(), 0);
						}

						if (lossImproveFactor > maxLossImproveFactor) {
							maxLossImproveFactor = lossImproveFactor;
							minLossBus = busIndex;
							minBusPhases = phases;
						}
					}
				}

				if (DSS.solutionAbort) break;
			}

			/* Put control mode back to default before inserting capacitor for real */
			sol.setControlMode(ControlMode.CTRLSTATIC);
			sol.setUseAuxCurrents(false);

			if (minLossBus > 0) {
				exec = Executive.getInstance();

				if (minBusPhases >= 3) {
					kVrat = ckt.getBus(minLossBus).getKVBase() * DSS.SQRT3;
				} else {
					kVrat = ckt.getBus(minLossBus).getKVBase();
				}

				commandString = "new, capacitor." + getUniqueCapName() +
						", bus1=\"" + ckt.getBusList().get(minLossBus) +
						"\", phases=" + minBusPhases +
						", kvar=" + String.format("%-g", testCapKVAr) +
						", kv=" + String.format("%-g", kVrat);
				exec.setCommand(commandString);  // defines capacitor

				// append this command to 'DSSAutoAddedCapacitors.txt'
				appendToFile("Capacitors", commandString);

				sol.solveSnap();  // for rebuilding of lists, etc.
			}
			// return location of added generator
			DSS.globalResult = ckt.getBusList().get(minLossBus);

			break;
		}

		return result;
	}

	/**
	 * Compute injection currents for generator or capacitor and add into
	 * system currents array.
	 */
	public void addCurrents(Algorithm solveType) {
		Complex busV, current;
		int nodeRef;

		Circuit ckt = DSS.activeCircuit;
		SolutionObj sol = ckt.getSolution();

		switch (addType) {
		case GEN:
			/* For buses with voltage != 0, add into aux current array */
			for (int i = 0; i < phases; i++) {
				nodeRef = ckt.getBus(busIndex).getRef(i);
				if (nodeRef > 0) {  // add in only non-ground currents
					busV = sol.getNodeV(nodeRef);
					if ((busV.getReal() != 0.0) || (busV.getImaginary() != 0.0)) {
						/* Current into the system network */
						switch (solveType) {
						case NEWTON:
							current = genVA.divide(busV).conjugate().negate();  // terminal current
							sol.setCurrent(nodeRef, sol.getCurrent(nodeRef).add(current));
							break;
						case NORMAL:
							current = genVA.divide(busV).conjugate();  // injection current
							sol.setCurrent(nodeRef, sol.getCurrent(nodeRef).add(current));
							break;
						}
					}
				}
			}
			break;
		case CAP:
			/* For buses with voltage != 0, add into aux current array */
			for (int i = 0; i < phases; i++) {
				nodeRef = ckt.getBus(busIndex).getRef(i);
				if (nodeRef > 0) {
					busV = sol.getNodeV(nodeRef);
					if (busV.getReal() != 0.0 || busV.getImaginary() != 0.0) {
						/* Current into the system network */
						switch (solveType) {
						case NEWTON:
							current = new Complex(0.0, Ycap).multiply(busV);  // terminal current
							sol.setCurrent(nodeRef, sol.getCurrent(nodeRef).add(current));
							break;
						case NORMAL:
							current = new Complex(0.0, -Ycap).multiply(busV);  // injection current
							sol.setCurrent(nodeRef, sol.getCurrent(nodeRef).add(current));
							break;
						}
					}  // constant Y model
				}
			}
			break;
		}
	}

	private void computekWLossesEEN() {
		Circuit ckt = DSS.activeCircuit;

		if (ckt.getEnergyMeters().size() == 0) {
			// no energymeters in circuit, just go by total system losses
			kWLosses = ckt.getLosses().getReal() * 0.001;
			kWEEN = 0.0;
		} else {
			// sum losses in energy meters and add EEN
			kWLosses = 0.0;
			kWEEN = 0.0;

			for (EnergyMeterObj pMeter : ckt.getEnergyMeters()) {
				kWLosses = kWLosses + sumSelectedRegisters(pMeter, ckt.getLossRegs(), ckt.getNumLossRegs());
				kWEEN = kWEEN + sumSelectedRegisters(pMeter, ckt.getUERegs(), ckt.getNumUERegs());
			}
		}
	}

	private void setBaseLosses() {
		computekWLossesEEN();
		baseLosses = kWLosses;
		baseEEN = kWEEN;
	}

}
