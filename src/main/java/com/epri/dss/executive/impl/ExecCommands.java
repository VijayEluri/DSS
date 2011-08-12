package com.epri.dss.executive.impl;

import java.io.File;

import com.epri.dss.common.impl.DSSGlobals;
import com.epri.dss.common.impl.Utilities;
import com.epri.dss.parser.impl.Parser;
import com.epri.dss.shared.CommandList;
import com.epri.dss.shared.impl.CommandListImpl;

public class ExecCommands {

	public static final int NumExecCommands = 93;

	private String[] ExecCommand = new String[NumExecCommands];
	private String[] CommandHelp = new String[NumExecCommands];

	private CommandList CommandList;

	/* Always has last command processed */
	private String LastCmdLine;
	private String RedirFile;

	/**
	 * Private constructor prevents instantiation from other classes.
	 */
	private ExecCommands() {
		defineCommands();

		CommandList = new CommandListImpl(ExecCommand);
		CommandList.setAbbrevAllowed(true);
	}

	/**
	 * ExecCommandsHolder is loaded on the first execution of
	 * ExecCommands.getInstance() or the first access to
	 * ExecCommandsHolder.INSTANCE, not before.
	 */
	private static class ExecCommandsHolder {
		public static final ExecCommands INSTANCE = new ExecCommands();
	}

	public static ExecCommands getInstance() {
		return ExecCommandsHolder.INSTANCE;
	}

	private void defineCommands() {
		String CRLF = DSSGlobals.CRLF;

		ExecCommand = new String[NumExecCommands];

		ExecCommand[0]  = "New";
		ExecCommand[1]  = "Edit";
		ExecCommand[2]  = "More";
		ExecCommand[3]  = "M";
		ExecCommand[4]  = "~";
		ExecCommand[5]  = "Select";
		ExecCommand[6]  = "Save";
		ExecCommand[7]  = "Show";
		ExecCommand[8]  = "Solve";
		ExecCommand[9]  = "Enable";
		ExecCommand[10] = "Disable";
		ExecCommand[11] = "Plot";
		ExecCommand[12] = "Reset";
		ExecCommand[13] = "Compile";
		ExecCommand[14] = "Set";    // set DSS options
		ExecCommand[15] = "Dump";   // debug dump
		ExecCommand[16] = "Open";   // open a device terminal conductor
		ExecCommand[17] = "Close";  // close a device terminal conductor
		ExecCommand[18] = "//";     // comment
		ExecCommand[19] = "Redirect";
		ExecCommand[20] = "Help";
		ExecCommand[21] = "Quit";
		ExecCommand[22] = "?";      // property value inquiry
		ExecCommand[23] = "Next";
		ExecCommand[24] = "Panel";
		ExecCommand[25] = "Sample";
		ExecCommand[26] = "Clear";
		ExecCommand[27] = "About";
		ExecCommand[28] = "Calcvoltagebases";  // computes voltage bases
		ExecCommand[29] = "SetkVBase";  // set kV base at a bus
		ExecCommand[30] = "BuildY";  // forces rebuild of Y matrix right now
		ExecCommand[31] = "Get";  // returns values set with set command
		ExecCommand[32] = "Init";
		ExecCommand[33] = "Export";
		ExecCommand[34] = "Fileedit";
		ExecCommand[35] = "Voltages";
		ExecCommand[36] = "Currents";
		ExecCommand[37] = "Powers";
		ExecCommand[38] = "Seqvoltages";
		ExecCommand[39] = "Seqcurrents";
		ExecCommand[40] = "Seqpowers";
		ExecCommand[41] = "Losses";
		ExecCommand[42] = "Phaselosses";
		ExecCommand[43] = "Cktlosses";
		ExecCommand[44] = "Allocateloads";
		ExecCommand[45] = "Formedit";
		ExecCommand[46] = "Totals";  // total all energyMeters
		ExecCommand[47] = "Capacity";  // find upper kW limit of system for present year
		ExecCommand[48] = "Classes";  // list of intrinsic classes
		ExecCommand[49] = "Userclasses";  // list of user-defined classes
		ExecCommand[50] = "Zsc";
		ExecCommand[51] = "Zsc10";
		ExecCommand[52] = "ZscRefresh";
		ExecCommand[53] = "Ysc";
		ExecCommand[54] = "puvoltages";
		ExecCommand[55] = "VarValues";
		ExecCommand[56] = "Varnames";
		ExecCommand[57] = "Buscoords";
		ExecCommand[58] = "MakeBusList";
		ExecCommand[59] = "MakePosSeq";
		ExecCommand[60] = "Reduce";
		ExecCommand[61] = "Interpolate";
		ExecCommand[62] = "AlignFile";
		ExecCommand[63] = "TOP";
		ExecCommand[64] = "Rotate";
		ExecCommand[65] = "Vdiff";
		ExecCommand[66] = "Summary";
		ExecCommand[67] = "Distribute";
		ExecCommand[68] = "DI_plot";
		ExecCommand[69] = "Comparecases";
		ExecCommand[70] = "YearlyCurves";
		ExecCommand[71] = "CD";
		ExecCommand[72] = "Visualize";
		ExecCommand[73] = "CloseDI";
		ExecCommand[74] = "DOScmd";
		ExecCommand[75] = "Estimate";
		ExecCommand[76] = "Reconductor";
		ExecCommand[77] = "_InitSnap";
		ExecCommand[78] = "_SolveNoControl";
		ExecCommand[79] = "_SampleControls";
		ExecCommand[80] = "_DoControlActions";
		ExecCommand[81] = "_ShowControlQueue";
		ExecCommand[82] = "_SolveDirect";
		ExecCommand[83] = "_SolvePFlow";
		ExecCommand[84] = "AddMarker";

		ExecCommand[85] = "Guids";
		ExecCommand[86] = "SetLoadAndGenKV";
		ExecCommand[87] = "CvrtLoadshapes";
		ExecCommand[88] = "NodeDiff";
		ExecCommand[89] = "Rephase";
		ExecCommand[90] = "SetBusXY";
		ExecCommand[91] = "UpdateStorage";
		ExecCommand[92] = "Obfuscate";


		CommandHelp = new String[NumExecCommands];

		CommandHelp[0]  = "Create a new object within the DSS. Object becomes the "+
							"active object" + CRLF +
							"Example: New Line.line1 ...";
		CommandHelp[1]  = "Edit an object. The object is selected and it then becomes the active object."+CRLF+CRLF+
							"Note that Edit is the default command.  You many change a property value simply by " +
							"giving the full property name and the new value, for example:"+CRLF+CRLF+
							"line.line1.r1=.04"+CRLF+
							"vsource.source.kvll=230";
		CommandHelp[2]  = "Continuation of editing on the active object.";
		CommandHelp[3]  = "Continuation of editing on the active object. An abbreviation for More";
		CommandHelp[4]  = "Continuation of editing on the active object. An abbreviation."+CRLF+
							CRLF+
							"Example:"+CRLF+
							"New Line.Line1 Bus1=aaa  bus2=bbb"+CRLF+
							"~ R1=.058" +CRLF+
							"~ X1=.1121";
		CommandHelp[5]  = "Selects an element and makes it the active element.  You can also specify the " +
							"active terminal (default = 1)."+CRLF+  CRLF+
							"Syntax:"+CRLF+
							"Select [element=]elementname  [terminal=]terminalnumber "+CRLF+CRLF+
							"Example:"+CRLF+
							"Select Line.Line1 "+CRLF+
							"~ R1=.1"+CRLF+"(continue editing)"+CRLF+CRLF+
							"Select Line.Line1 2 " +CRLF+
							"Voltages  (returns voltages at terminal 2 in Result)";
		CommandHelp[6]  = "{Save [class=]{Meters | Circuit | Voltages | (classname)} [file=]filename [dir=]directory " + CRLF +
							" Default class = Meters, which saves the present values in both monitors and energy meters in the active circuit. " +
							"\"Save Circuit\" saves the present enabled circuit elements to the specified subdirectory in standard DSS form " +
							"with a Master.txt file and separate files for each class of data. If Dir= not specified a unique name based on the circuit name is created "+
							"automatically.  If Dir= is specified, any existing files are overwritten. " + CRLF +
							"\"Save Voltages\" saves the present solution in a simple CSV format in a file called DSS_SavedVoltages. "+
							"Used for VDIFF command."+CRLF+
							"Any class can be saved to a file.  If no filename specified, the classname is used.";
		CommandHelp[7]  = "Writes selected results to a text file and brings "+
							"up the default text editor (see Set Editor=....) with the file for you to browse."+CRLF+  CRLF+
							"See separate help on Show command. "  +CRLF+  CRLF+
							"Default is \"show voltages LN Seq\".  ";
		CommandHelp[8]  = "Perform the solution of the present solution mode. You can set any option "+
							"that you can set with the Set command (see Set). "+
							"The Solve command is virtually synonymous with the Set command except that " +
							"a solution is performed after the options are processed.";
		CommandHelp[9]  = "Enables a circuit element or entire class.  Example:" +CRLF+
							"Enable load.loadxxx"+CRLF+
							"Enable generator.*  (enables all generators)";
		CommandHelp[10] = "Disables a circuit element or entire class.  Example:" +CRLF+
							"Disable load.loadxxx"+CRLF+
							"Disable generator.*  (Disables all generators)"+CRLF+ CRLF+
							"The item remains defined, but is not included in the solution.";
		CommandHelp[11] = "Plots circuits and results in a variety of manners.  See separate Plot command help.";
		CommandHelp[12] = "{MOnitors | MEters | Faults | Controls | Eventlog | Keeplist |(no argument) } Resets all Monitors, Energymeters, etc. " +
							"If no argument specified, resets all options listed.";
		CommandHelp[13] = "Reads the designated file name containing DSS commands " +
							"and processes them as if they were entered directly into the command line. "+
							"The file is said to be \"compiled.\" "  +
							"Similar to \"redirect\" except changes the default directory to the path of the specified file."+CRLF+CRLF+
							"Syntax:"+CRLF+
							"Compile filename";
		CommandHelp[14] = "Used to set various DSS solution modes and options.  You may also set the options with the Solve command. "+
							"See \"Options\" for help.";
		CommandHelp[15] = "Display the properties of either a specific DSS object or a complete dump "+
							"on all variables in the problem (Warning! Could be very large!)."+
							" Brings up the default text editor with the text file written by this command."+ CRLF+
							" Syntax: dump [class.obj] [debug]" + CRLF +
							" Examples:"+CRLF+CRLF+
							" Dump line.line1 "+CRLF+
							" Dump solution  (dumps all solution vars) "+CRLF+
							" Dump commands  (dumps all commands to a text file) "+CRLF+
							" Dump transformer.*  (dumps all transformers)"+CRLF+
							" Dump ALLOCationfactors  (load allocation factors)"+CRLF+
							" Dump Buslist    (bus name hash list)" + CRLF +
							" Dump Devicelist    (Device name hash list)" + CRLF +
							" Dump      (dumps all objects in circuit) ";
							//" Dump debug";   // Debug dump
		CommandHelp[16] = "Opens the specified terminal and conductor of the specified circuit element. " +
							"If the conductor is not specified, all phase conductors of the terminal are opened." +CRLF+CRLF+
							"Examples:"+CRLF+
							"Open line.line1 2 "+CRLF+
							"(opens all phases of terminal 2)"+CRLF+CRLF+
							"Open line.line1 2 3" +CRLF+
							"(opens the 3rd conductor of terminal 2)";
		CommandHelp[17] = "Opposite of the Open command.";   // Close a device terminal conductor
		CommandHelp[18] = "Comment.  Command line is ignored.";       // Comment
		CommandHelp[19] = "Reads the designated file name containing DSS commands " +
							"and processes them as if they were entered directly into the command line. "+
							"Similar to \"Compile\", but leaves current directory where it was when Redirect command is invoked." +
							"Can temporarily change to subdirectories if nested Redirect commands require."+CRLF+CRLF+
							"ex:  redirect filename";
		CommandHelp[20] = "Gives this display.";
		CommandHelp[21] = "Shuts down DSS unless this is the DLL version.  Then it does nothing;  DLL parent is responsible for shutting down the DLL.";
		CommandHelp[22] = "Inquiry for property value.  Result is put into GlobalReault and can be seen in the Result Window. "+
							"Specify the full property name."+CRLF+CRLF+
							"Example: ? Line.Line1.R1" + CRLF+CRLF+
							"Note you can set this property merely by saying:"+CRLF+
							"Line.line1.r1=.058";   // Property Value inquiry
		CommandHelp[23] = "{Year | Hour | t}  Increments year, hour, or time as specified.  If \"t\" is " +
							"specified, then increments time by current step size.";
		CommandHelp[24] = "Displays main control panel window.";
		CommandHelp[25] = "Force all monitors and meters to take a sample now.";
		CommandHelp[26] = "Clear all circuits currently in memory.";
		CommandHelp[27] = "Display \"About Box\".  (Result string set to Version string.)";
		CommandHelp[28] = "Calculates voltagebase for buses based on voltage bases defined "+
							"with Set voltagebases=... command.";
		CommandHelp[29] = "Command to explicitly set the base voltage for a bus. " +
							"Bus must be previously defined. Parameters in order are:"+CRLF+
							"Bus = {bus name}"   +CRLF+
							"kVLL = (line-to-line base kV)"      +CRLF+
							"kVLN = (line-to-neutral base kV)"   + CRLF+CRLF+
							"kV base is normally given in line-to-line kV (phase-phase). " +
							"However, it may also be specified by line-to-neutral kV."+CRLF+
							"The following exampes are equivalent:"+CRLF+CRLF+
							"setkvbase Bus=B9654 kVLL=13.2"   +CRLF+
							"setkvbase B9654 13.2"   +CRLF+
							"setkvbase B9654 kvln=7.62";
		CommandHelp[30] = "Forces rebuild of Y matrix upon next Solve command regardless of need. " +
							"The usual reason for doing this would be to reset the matrix for another " +
							"load level when using LoadModel=PowerFlow (the default) when the system is difficult to " +
							"solve when the load is far from its base value.  Works by invalidating the Y primitive " +
							"matrices for all the Power Conversion elements.";
		CommandHelp[31] = "Returns DSS property values set using the Set command. "  +
							"Result is returne in Result property of the Text interface. " +CRLF+CRLF+
							"VBA Example:" +CRLF+CRLF+
							"DSSText.Command = \"Get mode\"" +CRLF   +
							"Answer = DSSText.Result" +CRLF +CRLF +
							"Multiple properties may be requested on one get.  The results are appended "+
							"and the individual values separated by commas." +CRLF+CRLF+
							"See help on Set command for property names.";
		CommandHelp[32] = "This command forces reinitialization of the solution for the next Solve command. " +
							"To minimize iterations, most solutions start with the previous solution unless there " +
							"has been a circuit change.  However, if the previous solution is bad, it may be necessary " +
							"to re-initialize.  In most cases, a re-initiallization results in a zero-load power flow " +
							"solution with only the series power delivery elements considered.";
		CommandHelp[33] = "Export various solution values to CSV (or XML) files for import into other programs. " +
							"Creates a new file except for Energymeter and Generator objects, for which " +
							"the results for each device of this class are APPENDED to the CSV File. You may export to "+
							"a specific file by specifying the file name as the LAST parameter on the line. For example:"+ CRLF+CRLF+
							"  Export Voltage Myvoltagefile.CSV" +CRLF+CRLF+
							"Otherwise, the default file names shown in the Export help are used. " +
							"For Energymeter and Generator, specifying the switch \"/multiple\" (or /m) for the file name will cause " +
							"a separate file to be written for each meter or generator. " +
							"The default is for a single file containing all elements." +  CRLF + CRLF+
							"May be abreviated Export V, Export C, etc.  Default is \"V\" for voltages."+
							" If Set ShowExport=Yes, the output file will be automatically displayed in the default editor. "+
							"Otherwise, you must open the file separately. The name appears in the Result window.";
		CommandHelp[34] = "Edit specified file in default text file editor (see Set Editor= option)."+CRLF+CRLF+
							"Fileedit EXP_METERS.CSV (brings up the meters export file)" + CRLF+CRLF+
							"\"FileEdit\" may be abbreviated to a unique character string.";
		CommandHelp[35] = "Returns the voltages for the ACTIVE BUS in the Result string. " +
							"For setting the active Bus, use the Select command or the Set Bus= option. " +
							"Returned as magnitude and angle quantities, comma separated, one set per conductor of the terminal.";
		CommandHelp[36] = "Returns the currents for each conductor of ALL terminals of the active circuit element in the Result string. "+
							"(See Select command.)" +
							"Returned as comma-separated magnitude and angle.";
		CommandHelp[37] = "Returns the powers (complex) going into each conductors of ALL terminals of the active circuit element in the Result string. "+
							"(See Select command.)" +
							"Returned as comma-separated kW and kvar.";
		CommandHelp[38] = "Returns the sequence voltages at all terminals of the active circuit element (see Select command) in Result string.  Returned as comma-separated magnitude only values." +
							"Order of returned values: 0, 1, 2  (for each terminal).";
		CommandHelp[39] = "Returns the sequence currents into all terminals of the active circuit element (see Select command) in Result string.  Returned as comma-separated magnitude only values." +
							"Order of returned values: 0, 1, 2  (for each terminal).";
		CommandHelp[40] = "Returns the sequence powers into all terminals of the active circuit element (see Select command) in Result string.  Returned as comma-separated kw, kvar pairs." +
							"Order of returned values: 0, 1, 2  (for each terminal).";
		CommandHelp[41] = "Returns the total losses for the active circuit element (see Select command) " +
							"in the Result string in kW, kvar.";
		CommandHelp[42] = "Returns the losses for the active circuit element (see Select command) " +
							"for each PHASE in the Result string in comma-separated kW, kvar pairs.";
		CommandHelp[43] = "Returns the total losses for the active circuit in the Result string in kW, kvar.";
		CommandHelp[44] = "Estimates the allocation factors for loads that are defined using the XFKVA property. " +
							"Requires that energymeter objects be defined with the PEAKCURRENT property set. " +
							"Loads that are not in the zone of an energymeter cannot be allocated.";
		CommandHelp[45] = "FormEdit [class.object].  Brings up form editor on active DSS object.";
		CommandHelp[46] = "Totals all EnergyMeter objects in the circuit and reports register totals in the result string.";
		CommandHelp[47] = "Find the maximum load the active circuit can serve in the PRESENT YEAR. Uses the EnergyMeter objects with the registers "+
							"set with the SET UEREGS= (..) command for the AutoAdd functions.  Syntax (defaults shown):"+CRLF+CRLF+
							"capacity [start=]0.9 [increment=]0.005" + CRLF + CRLF +
							"Returns the metered kW (load + losses - generation) and per unit load multiplier for the loading level at which something in the system reports an overload or undervoltage. "+
							"If no violations, then it returns the metered kW for peak load for the year (1.0 multiplier). "+
							"Aborts and returns 0 if no energymeters.";
		CommandHelp[48] = "List of intrinsic DSS Classes. Returns comma-separated list in Result variable.";
		CommandHelp[49] = "List of user-defined DSS Classes. Returns comma-separated list in Result variable.";
		CommandHelp[50] = "Returns full Zsc matrix for the ACTIVE BUS in comma-separated complex number form.";
		CommandHelp[51] = "Returns symmetrical component impedances, Z1, Z0 for the ACTIVE BUS in comma-separated R+jX form.";
		CommandHelp[52] = "Refreshes Zsc matrix for the ACTIVE BUS.";
		CommandHelp[53] = "Returns full Ysc matrix for the ACTIVE BUS in comma-separated complex number form G + jB.";
		CommandHelp[54] = "Just like the Voltages command, except the voltages are in per unit if the kVbase at the bus is defined.";
		CommandHelp[55] = "Returns variable values for active element if PC element. Otherwise, returns null.";
		CommandHelp[56] = "Returns variable names for active element if PC element. Otherwise, returns null.";
		CommandHelp[57] = "Define x,y coordinates for buses.  Execute after Solve command performed so that bus lists are defined." +
							"Reads coordinates from a CSV file with records of the form: busname, x, y."+CRLF+CRLF+
							"Example: BusCoords [file=]xxxx.csv";
		CommandHelp[58] = "Updates the buslist using the currently enabled circuit elements.  (This happens automatically for Solve command.)";
		CommandHelp[59] = "Attempts to convert present circuit model to a positive sequence equivalent. " +
							"It is recommended to Save the circuit after this and edit the saved version to correct possible misinterpretations.";
		CommandHelp[60] = "{All | MeterName}  Default is \"All\".  Reduce the circuit according to reduction options. " +
							"See \"Set ReduceOptions\" and \"Set Keeplist\" options." +
							"Energymeter objects actually perform the reduction.  \"All\" causes all meters to reduce their zones.";
		CommandHelp[61] = "{All | MeterName}  Default is \"All\". Interpolates coordinates for missing bus coordinates in meter zone";
		CommandHelp[62] = "Alignfile [file=]filename.  Aligns DSS script files in columns for easier reading.";
		CommandHelp[63] = "[class=]{Loadshape | TShape | Monitor  } [object=]{ALL (Loadshapes only) | objectname}. " +
							"Send specified object to TOP.  Loadshapes and TShapes must be hourly fixed interval. ";
		CommandHelp[64] = "Usage: Rotate [angle=]nnn.  Rotate circuit plotting coordinates by specified angle (degrees). ";
		CommandHelp[65] = "Displays the difference between the present solution and the last on saved using the SAVE VOLTAGES command.";
		CommandHelp[66] = "Returns a power flow summary of the most recent solution in the global result string.";
		CommandHelp[67] = "kw=nn how={Proportional | Uniform |Random | Skip} skip=nn PF=nn file=filename MW=nn" +CRLF +
							"Distributes generators on the system in the manner specified by \"how\"." + CRLF +
							"kW = total generation to be distributed (default=1000) "+ CRLF +
							"how= process name as indicated (default=proportional to load)" + CRLF +
							"skip = no. of buses to skip for \"How=Skip\" (default=1)" + CRLF +
							"PF = power factor for new generators (default=1.0)"+ CRLF +
							"file = name of file to save (default=distgenerators.txt)"+ CRLF +
							"MW = alternate way to specify kW (default = 1)";
		CommandHelp[68] = "[case=]casename [year=]yr [registers=](reg1, reg2,...)  [peak=]y/n  [meter=]metername" +CRLF+
							"Plots demand interval (DI) results from yearly simulation cases.  "+
							"Plots selected registers from selected meter file (default = DI_Totals.CSV).  " +
							"Peak defaults to NO.  If YES, only daily peak of specified registers "+
							"is plotted. Example:"+CRLF+CRLF+
							" DI_Plot basecase year=5 registers=(9,11) no";
		CommandHelp[69] = "[Case1=]casename [case2=]casename [register=](register number) [meter=]{Totals* | SystemMeter | metername}. "+CRLF+
							"Compares yearly simulations of two specified cases with respect to the quantity in the designated register "+
							"from the designated meter file. "+
							"Defaults: Register=9 meter=Totals.  Example:"+CRLF+CRLF+
							"Comparecases base pvgens 10";
		CommandHelp[70] = "[cases=](case1, case2, ...) [registers=](reg1, reg2, ...)  [meter=]{Totals* | SystemMeter | metername}"+
							"Plots yearly curves for specified cases and registers. "+CRLF+
							"Default: meter=Totals. Example: "+CRLF+CRLF+
							"yearlycurves cases=(basecase, pvgens) registers=9";
		CommandHelp[71] = "Change default directory to specified directory" + CRLF +CRLF +
							"CD dirname";
		CommandHelp[72] = "[What=] one of {Currents* | Voltages | Powers} [element=]full_element_name  (class.name). " +
							"Shows the selected quantity for selected element on a multiphase line drawing in phasor values.";
		CommandHelp[73] = "Close all DI files ... useful at end of yearly solution where DI files are left open. " +
							"(Reset and Set Year=nnn will also close the DI files)";
		CommandHelp[74] = "Do a DOS command. Sends the command \"cmd ... \" to Windows. Execute the \"cmd /?\" command "+
							"in a DOS window to see the options. To do a DOS command and automatically exit, do " + CRLF+CRLF+
							"DOScmd /c ...command string ..."+CRLF+CRLF+
							"To keep the DOS window open, use /k switch.";
		CommandHelp[75] = "Execute state estimator on present circuit given present sensor values.";
		CommandHelp[76] = "Reconductor a line section. Must be in an EnergyMeter zone. " + CRLF +
							"Syntax: Reconductor Line1=... Line2=... [LineCode= | Geometry = ] " +CRLF+
							"Line1 and Line2 may be given in any order. All lines in the path between the two are redefined " +
							"with either the LineCode or Geometry.";
		CommandHelp[77] = "For step control of solution process: Intialize iteration counters, etc. that normally occurs at the " +
							"start of a snapshot solution process.";
		CommandHelp[78] = "For step control of solution process: Solves the circuit in present state but does not check for control actions.";
		CommandHelp[79] = "For step control of solution process: Sample the control elements, which push control action requests onto the control queue.";
		CommandHelp[80] = "For step control of solution process: Pops control actions off the control queue according to the present control mode rules. " +
							"Dispatches contol actions to proper control element \"DoPendingAction\" handlers.";
		CommandHelp[81] = "For step control of solution process: Show the present control queue contents.";
		CommandHelp[82] = "For step control of solution process: Invoke direct solution function in DSS. Non-iterative solution of Y matrix and active sources only.";
		CommandHelp[83] = "For step control of solution process: Invoke iterative power flow solution function of DSS directly.";
		CommandHelp[84] = "Add a marker to the active plot. Example: "+CRLF+CRLF+"AddMarker Bus=busname code=nn color=$00FF0000 size=3";
		CommandHelp[85] = "Read GUIDS for class names. Tab or comma-delimited file with full object name and GUID";
		CommandHelp[86] = "Set load and generator object kv from bus voltage base and connection type.";
		CommandHelp[87] = "Convert all Loadshapes presently loaded into either files of single or files of double. "+
							"Usually files of singles are adequate precision for loadshapes.  Syntax:"+CRLF+CRLF+
							"cvrtloadshapes type=sng  (this is the default)"+CRLF+
							"cvrtloadshapes type=dbl"+CRLF+CRLF+
							"A DSS script for loading the loadshapes from the created files is produced and displayed in the default editor. ";
		CommandHelp[88] = "Global result is set to voltage difference, volts and degrees, (Node1 - Node2) between any two nodes. Syntax:" +CRLF+CRLF+
                "   NodeDiff Node1=MyBus.1 Node2=MyOtherBus.1";
		CommandHelp[89] = "Generates a script to change the phase designation of all lines downstream from a start in line. Useful for such things as moving a single-phase " +
                "lateral from one phase to another and keep the phase designation consistent for reporting functions that need it to be " +
                "(not required for simply solving). "+CRLF+CRLF+
                "StartLine=... PhaseDesignation=\"...\"  EditString=\"...\" ScriptFileName=... StopAtTransformers=Y/N/T/F" +CRLF+CRLF+
                "Enclose the PhaseDesignation in quotes since it contains periods (dots)." + CRLF +
                "You may add and optional EditString to edit any other line properties."+CRLF+CRLF+
                "Rephase StartLine=Line.L100  PhaseDesignation=\".2\"  EditString=\"phases=1\" ScriptFile=Myphasechangefile.DSS  Stop=No";
		CommandHelp[90] = "Bus=...  X=...  Y=... Set the X, Y coordinates for a single bus. Prerequisite: Bus must exist as a result of a Solve, CalcVoltageBases, or MakeBusList command.";
		CommandHelp[91] = "Update Storage elements based on present solution and time interval. ";
		CommandHelp[92] = "Change Bus and circuit element names to generic values to remove identifying names. Generally, " +
                "you will follow this command immediately by a \"Save Circuit Dir=MyDirName\" command.";
	}

	public String getLastCmdLine() {
		return LastCmdLine;
	}

	public void setLastCmdLine(String lastCmdLine) {
		LastCmdLine = lastCmdLine;
	}

	public String getRedirFile() {
		return RedirFile;
	}

	public void setRedirFile(String redirFile) {
		RedirFile = redirFile;
	}

	public String[] getExecCommand() {
		return ExecCommand;
	}

	public void setExecCommand(String[] execCommand) {
		ExecCommand = execCommand;
	}

	public CommandList getCommandList() {
		return CommandList;
	}

	public void setCommandList(CommandList commandList) {
		CommandList = commandList;
	}

	public String getExecCommand(int i) {
		return ExecCommand[i];
	}

	public String getCommandHelp(int i) {
		return CommandHelp[i];
	}

	public void processCommand(String CmdLine) {
		int ParamPointer;
		String ParamName;
		String Param;
		StringBuffer ObjName = new StringBuffer();
		StringBuffer PropName = new StringBuffer();
		DSSGlobals Globals = DSSGlobals.getInstance();
		Parser Parser = com.epri.dss.parser.impl.Parser.getInstance();

		try {
			Globals.setCmdResult(0);
			Globals.setErrorNumber(0);  // reset error number
			Globals.setGlobalResult("");

			/* Load up the parser and process the first parameter only */
			LastCmdLine = CmdLine;
			Parser.setCmdString(LastCmdLine);  // load up command parser
			Globals.setLastCommandWasCompile(false);

			ParamPointer = 0;
			ParamName = Parser.getNextParam();
			Param = Parser.makeString();
			if (Param.length() == 0) return;  // skip blank line

			// check for command verb or property value
			// commands do not have equal signs so paramName must be zero
			if (ParamName.length() == 0)
				ParamPointer = CommandList.getCommand(Param);

			// check first for "compile" or "redirect" and return
			switch (ParamPointer) {
			case 13:
				if (DSSExecutive.getInstance().isRecorderOn())
					DSSExecutive.getInstance().writeToRecorderFile(DSSGlobals.CRLF+"!*********"+CmdLine);
				Globals.setCmdResult(ExecHelper.doRedirect(true));
				return;
			case 19:
				if (DSSExecutive.getInstance().isRecorderOn())
					DSSExecutive.getInstance().writeToRecorderFile(DSSGlobals.CRLF+"!*********"+CmdLine);
				Globals.setCmdResult(ExecHelper.doRedirect(false));
				return;
			default:  // write everything direct to recorder, if on
				if (DSSExecutive.getInstance().isRecorderOn())
					DSSExecutive.getInstance().writeToRecorderFile(CmdLine);
				break;
			}

			// things that are ok to do before a circuit is defined
			switch (ParamPointer) {
			case 0:
				Globals.setCmdResult(ExecHelper.doNewCmd());  // new
				break;
			case 14:
				if (Globals.getActiveCircuit() == null) {
					ExecOptions.getInstance().doSetCmd_NoCircuit();  // can only call this if no circuit active
					return;  // we exit with either a good outcome or bad
				}
				break;
			case 18:
				// do nothing - comment
				break;
			case 20:
				Globals.setCmdResult(ExecHelper.doHelpCmd());
				break;
			case 21:
				if (!Globals.isDLL())
					Globals.getDSSForms().exitControlPanel();
				break;
			case 24:
				Globals.getDSSForms().showControlPanel();
				break;
			case 26:
				ExecHelper.doClearCmd();
				break;
			case 27:
				ExecHelper.doAboutBox();
				break;
			case 34:
				Globals.setCmdResult(ExecHelper.doFileEditCmd());
				break;
			case 48:
				Globals.setCmdResult(ExecHelper.doClassesCmd());
				break;
			case 49:
				Globals.setCmdResult(ExecHelper.doUserClassesCmd());
				break;
			case 62:
				Globals.setCmdResult(ExecHelper.doAlignFileCmd());
				break;
			case 68:
				Globals.setCmdResult(ExecHelper.doDI_PlotCmd());
				break;
			case 69:
				Globals.setCmdResult(ExecHelper.doCompareCasesCmd());
				break;
			case 70:
				Globals.setCmdResult(ExecHelper.doYearlyCurvesCmd());
				break;
			case 71:
				ParamName = Parser.getNextParam();
				Param = Parser.makeString();
				if (new File(Globals.getCurrentDirectory()).exists()) {
					Globals.setCurrentDirectory(Param);
					Globals.setCmdResult(0);
					Globals.setDataPath(Param);  // change DSSDataDirectory
				} else {
					Globals.doSimpleMsg("Directory \""+Param+"\" not found.", 282);
				}
				break;
			case 74:
				ExecHelper.doADOScmd();
				break;
			case 87:
				ExecHelper.doCvrtLoadshapesCmd();
				break;
			default:
				if (Globals.getActiveCircuit() == null)
					Globals.doSimpleMsg("You must create a new circuit object first: \"new circuit.mycktname\" to execute this command.", 301);
				break;
			}

			// now check to see if this is a command or a property reference

			if (ParamPointer == -1) {
				/* If not a command or the command is unknown, then it could be a property of a circuit element */

				/* If a command or no text before the = sign, then error */
				if ((ParamName.length() == 0) || ParamName.equalsIgnoreCase("command")) {
					Globals.doSimpleMsg("Unknown Command: \"" + Param + "\" "+ DSSGlobals.CRLF + Parser.getCmdString(), 302);
					Globals.setCmdResult(1);
				} else {
					ExecHelper.parseObjName(ParamName, ObjName, PropName);
					if (ObjName.length() > 0)
						Globals.setObject(ObjName.toString());  // set active element
					if (Globals.getActiveDSSObject() != null) {
						// rebuild command line and pass to editor
						// use quotes to ensure first parameter is interpreted ok after rebuild
						Parser.setCmdString(PropName.toString() + "=\"" + Param + "\" " + Parser.getRemainder());
						Globals.getActiveDSSClass().edit();
					}
				}
				return;
			}

			// process the rest of the commands
			switch (ParamPointer) {
			case 1:
				Globals.setCmdResult(ExecHelper.doEditCmd());  // edit
				break;
			case 2:
				Globals.setCmdResult(ExecHelper.doMoreCmd()); // more
				break;
			case 3:
				Globals.setCmdResult(ExecHelper.doMoreCmd()); // m
				break;
			case 4:
				Globals.setCmdResult(ExecHelper.doMoreCmd()); // ~
				break;
			case 5:
				Globals.setCmdResult(ExecHelper.doSelectCmd());
				break;
			case 6:
				Globals.setCmdResult(ExecHelper.doSaveCmd()); // save
				break;
			case 7:
				Globals.setCmdResult(ShowOptions.getInstance().doShowCmd()); // show
				break;
			case 8:
				Globals.setCmdResult(ExecOptions.getInstance().doSetCmd(1)); // solve
				break;
			case 9:
				Globals.setCmdResult(ExecHelper.doEnableCmd());
				break;
			case 10:
				Globals.setCmdResult(ExecHelper.doDisableCmd());
				break;
			case 11:
				Globals.setCmdResult(PlotOptions.getInstance().doPlotCmd());
				break;
			case 12:
				Globals.setCmdResult(ExecHelper.doResetCmd());
				break;
			case 14:
				Globals.setCmdResult(ExecOptions.getInstance().doSetCmd(0));  // set with no solve
				break;
			case 15:
				Globals.setCmdResult(ExecHelper.doPropertyDump());
				break;
			case 17:
				Globals.setCmdResult(ExecHelper.doCloseCmd());
				break;
			case 22:
				Globals.setCmdResult(ExecHelper.doQueryCmd());
				break;
			case 23:
				Globals.setCmdResult(ExecHelper.doNextCmd());
				break;
//			case 24:
//				DSSForms.showControlPanel()
//				break;
			case 25:
				Globals.setCmdResult(ExecHelper.doSampleCmd());
				break;
//			case 26:
//				clearAllCircuits();
//				disposeDSSClasses();
//				createDSSClasses();
//				break;
//			case 27:
//				doAboutBox();
//				break;
			case 28:
				Globals.setCmdResult(ExecHelper.doSetVoltageBases());
				break;
			case 29:
				Globals.setCmdResult(ExecHelper.doSetkVBase());
				break;
			case 30:
				// Force rebuilding of Y
				Globals.getActiveCircuit().invalidateAllPCElements();
				break;
			case 31:
				Globals.setCmdResult(ExecOptions.getInstance().doGetCmd());
				break;
			case 32:
				Globals.getActiveCircuit().getSolution().setSolutionInitialized(false);
				break;
			case 33:
				Globals.setCmdResult(ExportOptions.getInstance().doExportCmd());
				break;
			case 34:
				Globals.setCmdResult(ExecHelper.doFileEditCmd());
				break;
			case 35:
				Globals.setCmdResult(ExecHelper.doVoltagesCmd(false));
				break;
			case 36:
				Globals.setCmdResult(ExecHelper.doCurrentsCmd());
				break;
			case 37:
				Globals.setCmdResult(ExecHelper.doPowersCmd());
				break;
			case 38:
				Globals.setCmdResult(ExecHelper.doSeqVoltagesCmd());
				break;
			case 39:
				Globals.setCmdResult(ExecHelper.doSeqCurrentsCmd());
				break;
			case 40:
				Globals.setCmdResult(ExecHelper.doSeqPowersCmd());
				break;
			case 42:
				Globals.setCmdResult(ExecHelper.doLossesCmd());
				break;
			case 43:
				Globals.setCmdResult(ExecHelper.doCktLossesCmd());
				break;
			case 44:
				Globals.setCmdResult(ExecHelper.doAllocateLoadsCmd());
				break;
			case 45:
				Globals.setCmdResult(ExecHelper.doFormEditCmd());
				break;
			case 46:
				Globals.setCmdResult(ExecHelper.doMeterTotals());
				break;
			case 47:
				Globals.setCmdResult(ExecHelper.doCapacityCmd());
				break;
//			case 48:
//				Globals.setCmdResult(ExecHelper.doClassesCmd());
//				break;
//			case 49:
//				Globals.setCmdResult(ExecHelper.doUserClassesCmd();
//				break;
			case 50:
				Globals.setCmdResult(ExecHelper.doZscCmd(true));
				break;
			case 51:
				Globals.setCmdResult(ExecHelper.doZsc10Cmd());
				break;
			case 52:
				Globals.setCmdResult(ExecHelper.doZscRefresh());
				break;
			case 53:
				Globals.setCmdResult(ExecHelper.doZscCmd(false));
				break;
			case 54:
				Globals.setCmdResult(ExecHelper.doVoltagesCmd(true));
				break;
			case 55:
				Globals.setCmdResult(ExecHelper.doVarValuesCmd());
				break;
			case 56:
				Globals.setCmdResult(ExecHelper.doVarNamesCmd());
				break;
			case 57:
				Globals.setCmdResult(ExecHelper.doBusCoordsCmd());
				break;
			case 58:
				if (Globals.getActiveCircuit().isBusNameRedefined())
					Globals.getActiveCircuit().reProcessBusDefs();
				break;
			case 59:
				Globals.setCmdResult(ExecHelper.doMakePosSeq());
				break;
			case 60:
				Globals.setCmdResult(ExecHelper.doReduceCmd());
				break;
			case 61:
				Globals.setCmdResult(ExecHelper.doInterpolateCmd());
				break;
			case 63:
				Globals.setCmdResult(ExecHelper.doTOPCmd());
				break;
			case 64:
				Globals.setCmdResult(ExecHelper.doRotateCmd());
				break;
			case 65:
				Globals.setCmdResult(ExecHelper.doVDiffCmd());
				break;
			case 66:
				Globals.setCmdResult(ExecHelper.doSummaryCmd());
				break;
			case 67:
				Globals.setCmdResult(ExecHelper.doDistributeCmd());
				break;
			case 68:
				break;
			case 69:
				break;
			case 70:
				break;
			case 71:
				break;
			case 72:
				Globals.setCmdResult(ExecHelper.doVisualizeCmd());
				break;
			case 73:
				Globals.setCmdResult(ExecHelper.doCloseDICmd());
				break;
			case 75:
				Globals.setCmdResult(ExecHelper.doEstimateCmd());
				break;
			case 76:
				Globals.setCmdResult(ExecHelper.doReconductorCmd());
				break;
			/* Step solution commands */
			case 77:
				Globals.getActiveCircuit().getSolution().snapShotInit();
				break;
			case 78:
				Globals.getActiveCircuit().getSolution().solveCircuit();
				break;
			case 79:
				Globals.getActiveCircuit().getSolution().sampleControlDevices();
				break;
			case 80:
				Globals.getActiveCircuit().getSolution().doControlActions();
				break;
			case 81:
				Globals.getActiveCircuit().getControlQueue().showQueue(Globals.getDSSDirectory() + Globals.getCircuitName_() + "ControlQueue.csv");
				break;
			case 82:
				Globals.getActiveCircuit().getSolution().solveDirect();
				break;
			case 83:
				Globals.getActiveCircuit().getSolution().doPFlowSolution();
				break;
			case 84:
				Globals.setCmdResult(ExecHelper.doAddMarkerCmd());
				break;
			case 85:
				Globals.setCmdResult(ExecHelper.doUUIDsCmd());
				break;
			case 86:
				Globals.setCmdResult(ExecHelper.doSetLoadAndGenKVCmd());
				break;
			case 87:
				break;
			case 88:
				Globals.setCmdResult(ExecHelper.doNodeDiffCmd());
				break;
			case 89:
				Globals.setCmdResult(ExecHelper.doRephaseCmd());
				break;
			case 90:
				Globals.setCmdResult(ExecHelper.doSetBusXYCmd());
				break;
			case 91:
				Globals.setCmdResult(ExecHelper.doUpdateStorageCmd());
				break;
			case 92:
				Utilities.obfuscate();
				break;
			default:
				// ignore excess parameters
				break;
			}
		} catch (Exception e) {
//			Globals.doErrorMsg("ProcessCommand"+DSSGlobals.CRLF+"Exception raised while processing DSS command:"+ DSSGlobals.CRLF + Parser.getCmdString(),
//					e.getMessage(),
//					"Error in command string or circuit data.", 303);
			e.printStackTrace();
		}
	}

}
