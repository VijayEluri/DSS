package com.epri.dss.executive.impl;

import com.epri.dss.common.CktElement;
import com.epri.dss.common.impl.DSSGlobals;
import com.epri.dss.common.impl.ShowResults;
import com.epri.dss.common.impl.Utilities;
import com.epri.dss.meter.MonitorObj;
import com.epri.dss.parser.impl.Parser;
import com.epri.dss.shared.CommandList;
import com.epri.dss.shared.impl.CommandListImpl;
import com.epri.dss.shared.impl.LineUnits;

public class ShowOptions {

	private static final String CRLF = DSSGlobals.CRLF;

	private static final int NumShowOptions = 30;

	private String[] showOption;
	private String[] showHelp;

	private CommandList showCommands;

	// private constructor prevents instantiation from other classes
	private ShowOptions() {
		defineOptions();

		showCommands = new CommandListImpl(showOption);
		showCommands.setAbbrevAllowed(true);
	}

	/**
	 * ShowOptionsHolder is loaded on the first execution of
	 * ShowOptions.getInstance() or the first access to
	 * ShowOptionsHolder.INSTANCE, not before.
	 */
	private static class ShowOptionsHolder {
		public static final ShowOptions INSTANCE = new ShowOptions();
	}

	public static ShowOptions getInstance() {
		return ShowOptionsHolder.INSTANCE;
	}

	private void defineOptions() {

		showOption = new String[NumShowOptions];

		showOption[0]  = "autoadded";
		showOption[1]  = "buses";
		showOption[2]  = "currents";
		showOption[3]  = "convergence";
		showOption[4]  = "elements";
		showOption[5]  = "faults";
		showOption[6]  = "isolated";
		showOption[7]  = "generators";
		showOption[8]  = "meters";
		showOption[9]  = "monitor";
		showOption[10] = "panel";
		showOption[11] = "powers";
		showOption[12] = "voltages";
		showOption[13] = "zone";
		showOption[14] = "taps";
		showOption[15] = "overloads";
		showOption[16] = "unserved";
		showOption[17] = "eventlog";
		showOption[18] = "variables";
		showOption[19] = "ratings";
		showOption[20] = "loops";
		showOption[21] = "losses";
		showOption[22] = "busflow";
		showOption[23] = "lineconstants";
		showOption[24] = "yprim";
		showOption[25] = "y";
		showOption[26] = "controlqueue";
		showOption[27] = "topology";
		showOption[28] = "mismatch";
		showOption[29] = "kvbasemismatch";


		showHelp = new String[NumShowOptions];

		showHelp[ 0] = "Shows auto added capacitors or generators. See AutoAdd solution mode.";
		showHelp[ 1] = "Report showing all buses and nodes currently defined.";
		showHelp[ 2] = "Report showing currents from most recent solution. syntax: " + CRLF +  CRLF +
								"Show Currents  [[residual=]yes|no*] [Seq* | Elements]" + CRLF + CRLF +
							"If \"residual\" flag is yes, the sum of currents in all conductors is reported. " +
							"Default is to report Sequence currents; otherwise currents in all conductors are reported.";
		showHelp[ 3] = "Report on the convergence of each node voltage.";
		showHelp[ 4] = "Shows names of all elements in circuit or all elements of a specified class. Syntax: " +CRLF + CRLF +
							"Show ELements [Classname] " +CRLF + CRLF +
							"Useful for creating scripts that act on selected classes of elements. ";
		showHelp[ 5] = "After fault study solution, shows fault currents.";
		showHelp[ 6] = "Report showing buses and elements that are isolated from the main source.";
		showHelp[ 7] = "Report showing generator elements currently defined and the values of the energy meters " +CRLF +
							"associated with each generator.";
		showHelp[ 8] = "Shows the present values of the registers in the EnergyMeter elements.";
		showHelp[ 9] = "Shows the contents of a selected monitor. Syntax: " + CRLF + CRLF +
							" Show Monitor monitorname";
		showHelp[10] = "Shows control panel. (not necessary for standalone version)";
		showHelp[11] = "Report on powers flowing in circuit from most recent solution. "+CRLF+
							"Powers may be reported in kVA or MVA and in sequence quantities or in every " +
							"conductor of each element. Syntax:" +CRLF+CRLF+
							"Show Powers [MVA|kVA*] [Seq* | Elements]"+CRLF+CRLF+
							"Sequence powers in kVA is the default. Examples:"+CRLF+CRLF+
							"Show powers"+CRLF+
							"Show power kva element" +CRLF+
							"Show power mva elem";
		showHelp[12] = "Reports voltages from most recent solution. Voltages are reported with respect to "+CRLF+
							"system reference (Node 0) by default (LN option), but may also be reported Line-Line (LL option)."+CRLF+
							"The voltages are normally reported by bus/node, but may also be reported by circuit element. Syntax:"+CRLF+CRLF+
							"Show Voltages [LL |LN*]  [Seq* | Nodes | Elements]" +CRLF +CRLF +
							"Show Voltages" +CRLF+
							"Show Voltage LN Nodes"+CRLF+
							"Show Voltages LL Nodes" +CRLF+
							"Show Voltage LN Elem";
		showHelp[13] = "Shows the zone for a selected EnergyMeter element. Shows zone either in " +
							"a text file or in a graphical tree view." +CRLF + CRLF +
							"Show Zone  energymetername [Treeview]";
		showHelp[14] = "Shows the regulator/LTC taps from the most recent solution.";
		showHelp[15] = "Shows overloaded power delivery elements.";
		showHelp[16] = "Shows loads that are \"unserved\". That is, loads for which the voltage is too low, "+
							"or a branch on the source side is overloaded. If UEonly is specified, shows only those loads " +
							"in which the emergency rating has been exceeded. Syntax:" +CRLF + CRLF+
							"Show Unserved [UEonly] (unserved loads)";
		showHelp[17] = "Shows the present event log. (Regulator tap changes, capacitor switching, etc.)";
		showHelp[18] = "Shows internal state variables of devices (Power conversion elements) that report them.";
		showHelp[19] = "Shows ratings of power delivery elements.";
		showHelp[20] = "Shows closed loops detected by EnergyMeter elements that are possibly unwanted. Otherwise, loops are OK.";
		showHelp[21] = "Reports losses in each element and in the entire circuit.";
		showHelp[22] = "Creates a report showing power and current flows as well as voltages around a selected bus. Syntax:" +CRLF+CRLF+
							"Show BUSFlow busname [MVA|kVA*] [Seq* | Elements]" +CRLF+CRLF+
							"Show busflow busxxx kVA elem" +CRLF+
							"Show busflow busxxx MVA seq" +CRLF+CRLF+
							"NOTE: The Show menu will prompt you for these values.";
		showHelp[23] = "Creates two report files for the line constants (impedances) of every LINEGEOMETRY element currently defined. "+
							"One file shows the main report with the matrices. The other file contains corresponding LINECODE " +
							"definitions that you may use in subsequent simulations.  Syntax:" + CRLF + CRLF +
							"Show LIneConstants [frequency] [none|mi|km|kft|m|me|ft|in|cm] [rho]" + CRLF + CRLF +
							"Specify the frequency, length units and earth resistivity (meter-ohms). Examples:" + CRLF + CRLF +
							"Show Lineconstants 60 kft 100" + CRLF +
							"Show Linecon 50 km 1000";
		showHelp[24] = "Show the primitive admittance (y) matrix for the active element.";
		showHelp[25] = "Show the system Y matrix. Could be a large file!";
		showHelp[26] = "Shows the present contents of the control queue.";
		showHelp[27] = "Shows the topology as seen by the SwtControl elements.";
		showHelp[28] = "Shows the current mismatches at each node in amperes and percent of max currents at node.";
		showHelp[29] = "Creates a report of Load and Generator elements for which the base voltage does not match the Bus base voltage. " +
							"Scripts for correcting the voltage base are suggested.";

	}

	public int doShowCmd() {
		String fileName = "";
		MonitorObj pMon;

		Parser parser = Parser.getInstance();
		DSSGlobals globals = DSSGlobals.getInstance();

		int MVAOpt;
		boolean LLOpt;
		boolean showResid;
		int showOptionCode;
		String busName;
		double freq;
		int units;
		double rhoLine;

		parser.getNextParam();
		String param = parser.makeString().toLowerCase();
		int paramPointer = showCommands.getCommand(param);

		if (paramPointer == 0)
			paramPointer = 13;  // voltages

		globals.setInShowResults(true);

		switch (paramPointer) {
		case 0:  // autoadded
			Utilities.fireOffEditor(globals.getDSSDataDirectory() + globals.getCircuitName_() + "AutoAddedGenerators.txt");
			Utilities.fireOffEditor(globals.getDSSDataDirectory() + globals.getCircuitName_() + "AutoAddedCapacitors.txt");
			break;
		case 1:
			ShowResults.showBuses(globals.getDSSDataDirectory() + globals.getCircuitName_() + "Buses.txt");
			break;
		case 2:
			showOptionCode = 0;
			showResid = false;
			parser.getNextParam();  // look for residual
			param = parser.makeString().toUpperCase();
			// logic handles show curr y|n|t elements or show curr elements
			if (param.length() > 0) {
				switch (param.charAt(0)) {
				case 'Y':
					showResid = true;
					break;
				case 'T':
					showResid = true;
					break;
				case 'N':
					showResid = false;
					break;
				case 'E':
					showOptionCode = 1;
					break;
				}
				parser.getNextParam();  // look for another param
				param = parser.makeString().toUpperCase();
				if (param.length() > 0) {
					switch (param.charAt(0)) {
					case 'E':
						showOptionCode = 1;
						break;
					}
				}
				switch (showOptionCode) {
				case 0:
					fileName = "Curr_Seq";
					break;
				case 1:
					fileName = "Curr_Elem";
					break;
				}
				ShowResults.showCurrents(globals.getDSSDataDirectory() + globals.getCircuitName_() + fileName + ".txt", showResid, showOptionCode);
			}
			break;
		case 3:
			globals.getActiveCircuit().getSolution().writeConvergenceReport(globals.getDSSDataDirectory() + globals.getCircuitName_() + "Convergence.txt");
			break;
		case 4:
			parser.getNextParam();  // Look for another param
			param = parser.makeString().toLowerCase();
			ShowResults.showElements(globals.getDSSDataDirectory() + globals.getCircuitName_() + "Elements.txt", param);
			break;
		case 5:
			ShowResults.showFaultStudy(globals.getDSSDataDirectory() + globals.getCircuitName_() + "FaultStudy.txt");
			break;
		case 6:
			ShowResults.showIsolated(globals.getDSSDataDirectory() + globals.getCircuitName_() + "Isolated.txt");
			break;
		case 7:
			ShowResults.showGenMeters(globals.getDSSDataDirectory() + globals.getCircuitName_() + "GenMeterOut.txt");
			break;
		case 8:
			ShowResults.showMeters(globals.getDSSDataDirectory() + globals.getCircuitName_() + "EMout.txt");
			break;
		case 9:  // Show Monitor
			parser.getNextParam();
			param = parser.makeString();
			if (param.length() > 0) {
				pMon = (MonitorObj) globals.getMonitorClass().find(param);
				if (pMon != null) {
					pMon.translateToCSV(true);
				} else {
					globals.doSimpleMsg("Monitor \""+param+"\" not found."+ DSSGlobals.CRLF + parser.getCmdString(), 248);
				}
			} else {
				globals.doSimpleMsg("Monitor name not specified." + DSSGlobals.CRLF + parser.getCmdString(), 249);
			}
			break;
		case 10:
			globals.getDSSForms().showControlPanel();
			break;
		case 11:
			showOptionCode = 0;
			MVAOpt = 0;
			fileName = "Power";
			parser.getNextParam();
			param = parser.makeString().toLowerCase();
			if (param.length() > 0) {
				switch (param.charAt(0)) {
				case 'm':
					MVAOpt = 1;
					break;
				case 'e':
					showOptionCode = 1;
					break;
				}
			}
			parser.getNextParam();
			param = parser.makeString().toLowerCase();
			if (param.length() > 0)
				if (param.charAt(0) == 'e')
					showOptionCode = 1;
			if (showOptionCode == 1) {
				fileName = fileName + "_elem";
			} else {
				fileName = fileName + "_seq";
			}
			if (MVAOpt == 1) {
				fileName = fileName + "_MVA";
			} else {
				fileName = fileName + "_kVA";
			}

			ShowResults.showPowers(globals.getDSSDataDirectory() + globals.getCircuitName_() + fileName + ".txt", MVAOpt, showOptionCode);
			break;
		case 12:
			LLOpt = false;  // line-line voltage option
			showOptionCode = 0;
			/* Check for LL or LN option */
			parser.getNextParam();
			param = parser.makeString();

			fileName = "VLN";
			if (param.length() > 0) {
				if (param.equalsIgnoreCase("LL")) {
					LLOpt = true;
					fileName = "VLL";
				}
			}
			/* Check for seq | nodes | elements */
			parser.getNextParam();
			param = parser.makeString().toUpperCase();
			if (param.length() > 0) {
				switch (param.charAt(0)) {
				case 'N':
					showOptionCode = 1;
					fileName = fileName + "_node";
					break;
				case 'E':
					showOptionCode = 2;
					fileName = fileName + "_elem";
					break;
				default:
					fileName = fileName + "_seq";
					break;
				}
			}
			ShowResults.showVoltages(globals.getDSSDataDirectory() + globals.getCircuitName_() + fileName + ".txt", LLOpt, showOptionCode);
			break;
		case 13:
			ShowResults.showMeterZone(globals.getDSSDataDirectory() + globals.getCircuitName_() + "ZoneOut.txt");
			break;
		case 14:
			ShowResults.showRegulatorTaps(globals.getDSSDataDirectory() + globals.getCircuitName_() + "RegTaps.txt");
			break;
		case 15:
			ShowResults.showOverloads(globals.getDSSDataDirectory() + globals.getCircuitName_() + "Overload.txt");
			break;
		case 16:
			parser.getNextParam();
			param = parser.makeString();
			if (param.length() > 0) {
				ShowResults.showUnserved(globals.getDSSDataDirectory() + globals.getCircuitName_() + "Unserved.txt", true);
			} else {
				ShowResults.showUnserved(globals.getDSSDataDirectory() + globals.getCircuitName_() + "Unserved.txt", false);
			}
			break;
		case 17:
			globals.getDSSForms().showMessageForm(globals.getEventStrings());
			break;
		case 18:
			ShowResults.showVariables(globals.getDSSDataDirectory() + globals.getCircuitName_() + "Variables.txt");
			break;
		case 19:
			ShowResults.showRatings(globals.getDSSDataDirectory() + globals.getCircuitName_() + "RatingsOut.txt");
			break;
		case 20:
			ShowResults.showLoops(globals.getDSSDataDirectory() + globals.getCircuitName_() + "Loops.txt");
			break;
		case 21:
			ShowResults.showLosses(globals.getDSSDataDirectory() + globals.getCircuitName_() + "Losses.txt");
			break;
		case 22:  // show bus power report
			showOptionCode = 0;
			MVAOpt = 0;
			parser.getNextParam();  // get bus name
			busName = parser.makeString();
			if (busName.length() > 0) {
				fileName = busName;
			} else {
				fileName = "BusPower";
			}
			parser.getNextParam();
			param = parser.makeString().toLowerCase();
			if (param.length() > 0) {
				switch (param.charAt(0)) {
				case 'm':
					MVAOpt = 1;
					break;
				case 'e':
					showOptionCode = 1;
					break;
				}
			}
			parser.getNextParam();
			param = parser.makeString().toLowerCase();
			if (param.length() > 0)
				if (param.charAt(0) == 'e')
					showOptionCode = 1;
			if (showOptionCode == 1) {
				fileName = fileName + "_elem";
			} else {
				fileName = fileName + "_seq";
			}
			if (MVAOpt == 1) {
				fileName = fileName + "_MVA";
			} else {
				fileName = fileName + "_kVA";
			}

			ShowResults.showBusPowers(globals.getDSSDataDirectory() + globals.getCircuitName_() + fileName + ".txt", busName, MVAOpt, showOptionCode);
			break;
		case 23:  // showLineConstants  show lineConstants 60 mi
			freq = globals.getDefaultBaseFreq();  // default
			units = LineUnits.UNITS_KFT; // 'kft'; // default
			rhoLine = 100.0;
			parser.getNextParam();
			if (parser.makeString().length() > 0)
				freq = parser.makeDouble();
			parser.getNextParam();
			if (parser.makeString().length() > 0)
				units = LineUnits.getUnitsCode(parser.makeString());
			parser.getNextParam();
			if (parser.makeString().length() > 0)
				rhoLine = parser.makeDouble();
			ShowResults.showLineConstants(globals.getDSSDataDirectory() + globals.getCircuitName_() + "LineConstants.txt", freq, units, rhoLine);
			break;
		case 24:
			if (globals.getActiveCircuit() != null) {  // Yprim
				CktElement cktElem = globals.getActiveCircuit().getActiveCktElement();
				ShowResults.showYPrim(globals.getDSSDataDirectory() + cktElem.getParentClass().getName() + '_' + cktElem.getName() + "_Yprim.txt");
			}
			break;
		case 25:  // Y
			ShowResults.showY(globals.getDSSDataDirectory() + globals.getCircuitName_() + "SystemY.txt");
			break;
		case 26:
			if (globals.getActiveCircuit() != null)
				globals.getActiveCircuit().getControlQueue().showQueue(globals.getDSSDataDirectory() + globals.getCircuitName_()  + "ControlQueue.csv");
			break;
		case 27:
			ShowResults.showTopology(globals.getDSSDataDirectory() + globals.getCircuitName_());
			break;
		case 28:
			ShowResults.showNodeCurrentSum(globals.getDSSDataDirectory() + globals.getCircuitName_() + "NodeMismatch.txt");
			break;
		case 29:
			ShowResults.showkVBaseMismatch(globals.getDSSDataDirectory() + globals.getCircuitName_() + "kVBaseMismatch.txt");
			break;
		case 30:
			ShowResults.showDeltaV(globals.getDSSDataDirectory() + globals.getCircuitName_() + "DeltaV.txt");
			break;
		}

		globals.setInShowResults(false);
		return 0;
	}

}
