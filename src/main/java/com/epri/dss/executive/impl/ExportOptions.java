package com.epri.dss.executive.impl;

import com.epri.dss.common.impl.CIMProfileChoice;
import com.epri.dss.common.impl.DSSGlobals;
import com.epri.dss.common.impl.ExportResults;
import com.epri.dss.common.impl.Utilities;
import com.epri.dss.meter.MonitorObj;
import com.epri.dss.parser.impl.Parser;
import com.epri.dss.shared.CommandList;
import com.epri.dss.shared.impl.CommandListImpl;

public class ExportOptions {

	private static final int NumExportOptions = 32;

	private String[] ExportOption;
	private String[] ExportHelp;

	private static CommandList ExportCommands;

	// private constructor prevents instantiation from other classes
	private ExportOptions() {
		defineOptions();

		ExportCommands = new CommandListImpl(ExportOption);
		ExportCommands.setAbbrevAllowed(true);
	}

	/**
	 * ExportOptionsHolder is loaded on the first execution of
	 * ExportOptions.getInstance() or the first access to
	 * ExportOptionsHolder.INSTANCE, not before.
	 */
	private static class ExportOptionsHolder {
		public static final ExportOptions INSTANCE = new ExportOptions();
	}

	public static ExportOptions getInstance() {
		return ExportOptionsHolder.INSTANCE;
	}

	private void defineOptions() {

		ExportOption = new String[NumExportOptions];

		ExportOption[ 0] = "Voltages";
		ExportOption[ 1] = "SeqVoltages";
		ExportOption[ 2] = "Currents";
		ExportOption[ 3] = "SeqCurrents";
		ExportOption[ 4] = "Estimation";
		ExportOption[ 5] = "Capacity";
		ExportOption[ 6] = "Overloads";
		ExportOption[ 7] = "Unserved";
		ExportOption[ 8] = "Powers";
		ExportOption[ 9] = "SeqPowers";
		ExportOption[10] = "Faultstudy";
		ExportOption[11] = "Generators";
		ExportOption[12] = "Loads";
		ExportOption[13] = "Meters";
		ExportOption[14] = "Monitors";
		ExportOption[15] = "Yprims";
		ExportOption[16] = "Y";
		ExportOption[17] = "seqz";
		ExportOption[18] = "P_byphase";
		ExportOption[19] = "CDPSMCombined";
		ExportOption[20] = "CDPSMFunc";
		ExportOption[21] = "CDPSMAsset";
		ExportOption[22] = "Buscoords";
		ExportOption[23] = "Losses";
		ExportOption[24] = "Guids";
		ExportOption[25] = "Counts";
		ExportOption[26] = "Summary";
		ExportOption[27] = "CDPSMElec";
		ExportOption[28] = "CDPSMGeo";
		ExportOption[29] = "CDPSMTopo";
		ExportOption[30] = "CDPSMStateVar";
		ExportOption[31] = "Profile";

		ExportHelp[ 0] = "(Default file = EXP_VOLTAGES.CSV) Voltages to ground by bus/node.";
		ExportHelp[ 1] = "(Default file = EXP_SEQVOLTAGES.CSV) Sequence voltages.";
		ExportHelp[ 2] = "(Default file = EXP_CURRENTS.CSV) Currents in each conductor of each element.";
		ExportHelp[ 3] = "(Default file = EXP_SEQCURRENTS.CSV) Sequence currents in each terminal of 3-phase elements.";
		ExportHelp[ 4] = "(Default file = EXP_ESTIMATION.CSV) Results of last estimation.";
		ExportHelp[ 5] = "(Default file = EXP_CAPACITY.CSV) Capacity report.";
		ExportHelp[ 6] = "(Default file = EXP_OVERLOADS.CSV) Overloaded elements report.";
		ExportHelp[ 7] = "(Default file = EXP_UNSERVED.CSV) Report on elements that are served in violation of ratings.";
		ExportHelp[ 8] = "(Default file = EXP_POWERS.CSV) Powers into each terminal of each element.";
		ExportHelp[ 9] = "(Default file = EXP_SEQPOWERS.CSV) Sequence powers into each terminal of 3-phase elements.";
		ExportHelp[10] = "(Default file = EXP_FAULTS.CSV) results of a fault study.";
		ExportHelp[11] = "(Default file = EXP_GENMETERS.CSV) Present values of generator meters. Adding the switch \"/multiple\" or \"/m\" will " +
							" cause a separate file to be written for each generator.";
		ExportHelp[12] = "(Default file = EXP_LOADS.CSV) Report on loads from most recent solution.";
		ExportHelp[13] = "(Default file = EXP_METERS.CSV) Energy meter exports. Adding the switch \"/multiple\" or \"/m\" will " +
							" cause a separate file to be written for each meter.";
		ExportHelp[14] = "(file name is assigned by Monitor export) Monitor values.";
		ExportHelp[15] = "(Default file = EXP_YPRIMS.CSV) All primitive Y matrices.";
		ExportHelp[16] = "(Default file = EXP_Y.CSV) System Y matrix.";
		ExportHelp[17] = "(Default file = EXP_SEQZ.CSV) Equivalent sequence Z1, Z0 to each bus.";
		ExportHelp[18] = "(Default file = EXP_P_BYPHASE.CSV) Power by phase.";
		ExportHelp[19] = "(Default file = CDPSM.XML) (IEC 61968-13, CDPSM Unbalanced load flow profile)";
		ExportHelp[20] = "(Default file = CDPSM_Connect.XML) (IEC 61968-13, CDPSM Unbalanced connectivity profile)";
		ExportHelp[21] = "(Default file = CDPSM_Balanced.XML) (IEC 61968-13, CDPSM Balanced profile)";
		ExportHelp[22] = "[Default file = EXP_BUSCOORDS.CSV] Bus coordinates in csv form.";
		ExportHelp[23] = "[Default file = EXP_LOSSES.CSV] Losses for each element.";
		ExportHelp[24] = "[Default file = EXP_GUIDS.CSV] Guids for each element.";
		ExportHelp[25] = "[Default file = EXP_Counts.CSV] (instance counts for each class)";
		ExportHelp[26] = "[Default file = EXP_Summary.CSV] Solution summary.";
		ExportHelp[27] = "(Default file = CDPSM_ElectricalProperties.XML) (IEC 61968-13, CDPSM Electrical Properties profile)";
		ExportHelp[28] = "(Default file = CDPSM_Geographical.XML) (IEC 61968-13, CDPSM Geographical profile)";
		ExportHelp[29] = "(Default file = CDPSM_Topology.XML) (IEC 61968-13, CDPSM Topology profile)";
		ExportHelp[30] = "(Default file = CDPSM_StateVariables.XML) (IEC 61968-13, CDPSM State Variables profile)";
		ExportHelp[31] = "[Default file = EXP_Profile.CSV] Coordinates, color of each line section in Profile plot. Same options as Plot Profile Phases property." + DSSGlobals.CRLF + DSSGlobals.CRLF +
        		"Example:  Export Profile Phases=All [optional file name]";

	}

	public int doExportCmd() {
		String Parm2 = "", FileName;
		MonitorObj pMon;
		int PhasesToPlot;

		Parser parser = Parser.getInstance();
		DSSGlobals Globals = DSSGlobals.getInstance();

		Parser.getInstance().getNextParam();
		String Parm1 = Parser.getInstance().makeString().toLowerCase();
		int ParamPointer = ExportCommands.getCommand(Parm1);

		int MVAOpt = 0;
		boolean UEonlyOpt = false;
		PhasesToPlot = DSSGlobals.PROFILE3PH;  // init this to get rid of compiler warning

		switch (ParamPointer) {
		case 8:  // trap export powers command and look for MVA/kVA option
			parser.getNextParam();
			Parm2 = Parser.getInstance().makeString().toLowerCase();
			MVAOpt = 0;
			if (Parm2.length() > 0)
				if (Parm2.charAt(0) == 'm')
					MVAOpt = 1;
			break;
		case 18:
			parser.getNextParam();
			Parm2 = Parser.getInstance().makeString().toLowerCase();
			MVAOpt = 0;
			if (Parm2.length() > 0)
				if (Parm2.charAt(0) == 'm')
					MVAOpt = 1;
			break;
		case 7:  // trap UE only flag
			parser.getNextParam();
			Parm2 = parser.makeString().toLowerCase();
			UEonlyOpt = false;
			if (Parm2.length() > 0)
				if (Parm2.charAt(0) == 'u')
					UEonlyOpt = true;
			break;
		case 14:  // get monitor name for export monitors command
			parser.getNextParam();
			Parm2 = parser.makeString();
			break;
		case 31:  /* Get phases to plot */
			parser.getNextParam();
			Parm2 = parser.makeString();
			PhasesToPlot = DSSGlobals.PROFILE3PH;  // the default
			if (Utilities.compareTextShortest(Parm2, "default") == 0) {
				PhasesToPlot = DSSGlobals.PROFILE3PH;
			} else if (Utilities.compareTextShortest(Parm2, "all") == 0) {
				PhasesToPlot = DSSGlobals.PROFILEALL;
			} else if (Utilities.compareTextShortest(Parm2, "primary") == 0) {
				PhasesToPlot = DSSGlobals.PROFILEALLPRI;
			} else if (Utilities.compareTextShortest(Parm2, "ll3ph") == 0) {
				PhasesToPlot = DSSGlobals.PROFILELL;
			} else if (Utilities.compareTextShortest(Parm2, "llall") == 0) {
				PhasesToPlot = DSSGlobals.PROFILELLALL;
			} else if (Utilities.compareTextShortest(Parm2, "llprimary") == 0) {
				PhasesToPlot = DSSGlobals.PROFILELLPRI;
			} else if (Parm2.length() == 1) {
				PhasesToPlot = parser.makeInteger();
			}
			break;
		}

		/* Pick up last parameter on line, alternate file name, if any */
		parser.getNextParam();
		FileName = parser.makeString().toLowerCase();  // should be full path name to work universally

		Globals.setInShowResults(true);

		/* Assign default file name if alternate not specified */
		if (FileName.length() == 0) {
			switch (ParamPointer) {
			case 0: FileName = "EXP_VOLTAGES.csv"; break;
			case 1: FileName = "EXP_SEQVOLTAGES.csv"; break;
			case 2: FileName = "EXP_CURRENTS.csv"; break;
			case 3: FileName = "EXP_SEQCURRENTS.csv"; break;
			case 4: FileName = "EXP_ESTIMATION.csv"; break;  // estimation error
			case 5: FileName = "EXP_CAPACITY.csv"; break;
			case 6: FileName = "EXP_OVERLOADS.csv"; break;
			case 7: FileName = "EXP_UNSERVED.csv"; break;
			case 8: FileName = "EXP_POWERS.csv"; break;
			case 9: FileName = "EXP_SEQPOWERS.csv"; break;
			case 10: FileName = "EXP_FAULTS.csv"; break;
			case 11: FileName = "EXP_GENMETERS.csv"; break;
			case 12: FileName = "EXP_LOADS.csv"; break;
			case 13: FileName = "EXP_METERS.csv"; break;
			//case 14: FileName is assigned
			case 15: FileName = "EXP_YPRIM.csv"; break;
			case 16: FileName = "EXP_Y.csv"; break;
			case 17: FileName = "EXP_SEQZ.csv"; break;
			case 18: FileName = "EXP_P_BYPHASE.csv"; break;
			case 19: FileName = "CDPSM_Combined.xml"; break;
			case 20: FileName = "CDPSM_Functional.xml"; break;
			case 21: FileName = "CDPSM_Asset.xml"; break;
			case 22: FileName = "EXP_BUSCOORDS.csv"; break;
			case 23: FileName = "EXP_LOSSES.csv"; break;
			case 24: FileName = "EXP_GUIDS.csv"; break;
			case 25: FileName = "EXP_Counts.csv"; break;
			case 26: FileName = "EXP_Summary.csv"; break;
			case 27: FileName = "CDPSM_ElectricalProperties.xml"; break;
			case 28: FileName = "CDPSM_Geographical.xml"; break;
			case 29: FileName = "CDPSM_Topology.xml"; break;
			case 30: FileName = "CDPSM_StateVariables.xml"; break;
			case 31: FileName = "EXP_Profile.csv"; break;
			default:
				FileName = "EXP_VOLTAGES.csv"; break;
			}
			FileName = Globals.getDSSDataDirectory() + Globals.getCircuitName_() + FileName;  // explicitly define directory
		}

		switch (ParamPointer) {
		case 0: ExportResults.exportVoltages(FileName); break;
		case 1: ExportResults.exportSeqVoltages(FileName); break;
		case 2: ExportResults.exportCurrents(FileName); break;
		case 3: ExportResults.exportSeqCurrents(FileName); break;
		case 4: ExportResults.exportEstimation(FileName); break;  // estimation error
		case 5: ExportResults.exportCapacity(FileName); break;
		case 6: ExportResults.exportOverloads(FileName); break;
		case 7: ExportResults.exportUnserved(FileName, UEonlyOpt); break;
		case 8: ExportResults.exportPowers(FileName, MVAOpt); break;
		case 9: ExportResults.exportSeqPowers(FileName, MVAOpt); break;
		case 10: ExportResults.exportFaultStudy(FileName); break;
		case 11: ExportResults.exportGenMeters(FileName); break;
		case 12: ExportResults.exportLoads(FileName); break;
		case 13: ExportResults.exportMeters(FileName); break;
		case 14:
			if (Parm2.length() > 0) {
				pMon = (MonitorObj) Globals.getMonitorClass().find(Parm2);
				if (pMon != null) {
					pMon.translateToCSV(false);
					FileName = Globals.getGlobalResult();
				} else {
					Globals.doSimpleMsg("Monitor \""+Parm2+"\" not found."+ DSSGlobals.CRLF + parser.getCmdString(), 250);
				}
			} else {
				Globals.doSimpleMsg("Monitor name not specified."+ DSSGlobals.CRLF + parser.getCmdString(), 251);
			}
			break;
		case 15: ExportResults.exportYprim(FileName); break;
		case 16: ExportResults.exportY(FileName); break;
		case 17: ExportResults.exportSeqZ(FileName); break;
		case 18: ExportResults.exportPbyphase(FileName, MVAOpt); break;
		case 19: ExportResults.exportCDPSM(FileName, CIMProfileChoice.Combined); break;  // defaults to a load-flow model
		case 20: ExportResults.exportCDPSM(FileName, CIMProfileChoice.Functional); break;
		case 21: ExportResults.exportCDPSM(FileName, CIMProfileChoice.Asset); break;
		case 22: ExportResults.exportBusCoords(FileName); break;
		case 23: ExportResults.exportLosses(FileName); break;
		case 24: ExportResults.exportUUIDs(FileName); break;
		case 25: ExportResults.exportCounts(FileName); break;
		case 26: ExportResults.exportSummary(FileName); break;
		case 27: ExportResults.exportCDPSM(FileName, CIMProfileChoice.ElectricalProperties); break;
		case 28: ExportResults.exportCDPSM(FileName, CIMProfileChoice.Geographical); break;
		case 29: ExportResults.exportCDPSM(FileName, CIMProfileChoice.Topology); break;
		case 30: ExportResults.exportCDPSM(FileName, CIMProfileChoice.StateVariables); break;
		case 31: ExportResults.exportProfile(FileName, PhasesToPlot); break;
		default:
			ExportResults.exportVoltages(FileName);
			break;
		}

		int Result = 0;
		Globals.setInShowResults(false);

		if (Globals.isAutoShowExport())
			Utilities.fireOffEditor(FileName);

		return Result;
	}

}
