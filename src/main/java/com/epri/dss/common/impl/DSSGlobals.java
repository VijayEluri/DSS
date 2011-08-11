package com.epri.dss.common.impl;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import com.epri.dss.shared.impl.Complex;

import com.epri.dss.common.Circuit;
import com.epri.dss.common.DSSClass;
import com.epri.dss.common.DSSForms;
import com.epri.dss.common.Feeder;
import com.epri.dss.conversion.PVSystem;
import com.epri.dss.conversion.Storage;
import com.epri.dss.executive.impl.DSSExecutive;
import com.epri.dss.general.CNData;
import com.epri.dss.general.DSSObject;
import com.epri.dss.general.GrowthShape;
import com.epri.dss.general.LineSpacing;
import com.epri.dss.general.LoadShape;
import com.epri.dss.general.PriceShape;
import com.epri.dss.general.Spectrum;
import com.epri.dss.general.TCC_Curve;
import com.epri.dss.general.TSData;
import com.epri.dss.general.TShape;
import com.epri.dss.general.WireData;
import com.epri.dss.general.XYCurve;
import com.epri.dss.meter.EnergyMeter;
import com.epri.dss.meter.Monitor;
import com.epri.dss.meter.Sensor;
import com.epri.dss.parser.impl.Parser;
import com.epri.dss.shared.HashList;

public class DSSGlobals {

	public static final String CRLF = System.getProperty("line.separator");
	public static final double PI = 3.14159265359;
	public static final double TwoPi = 2.0 * PI;
	public static final double RadiansToDegrees = 57.29577951;
	public static final double EPSILON = 1.0e-12;   // default tiny floating point
	public static final double EPSILON2 = 1.0e-3;   // default for real number mismatch testing

	/* Load model types for solution */
	public static final int POWERFLOW  = 1;
	public static final int ADMITTANCE = 2;

	/* For YPrim matrices */
	public static final int ALL_YPRIM = 0;
	public static final int SERIES = 1;
	public static final int SHUNT  = 2;

	/* Control modes */
	public static final int CONTROLSOFF = -1;
	public static final int EVENTDRIVEN =  1;
	public static final int TIMEDRIVEN  =  2;
	public static final int CTRLSTATIC  =  0;

	/* Randomization constants */
	public static final int GAUSSIAN  = 1;
	public static final int UNIFORM   = 2;
	public static final int LOGNORMAL = 3;

	/* AutoAdd constants */
	public static final int GENADD = 1;
	public static final int CAPADD = 2;

	/* Errors */
	public static final int SOLUTION_ABORT = 99;

	/* For general sequential time simulations */
	public static final int USEDAILY  = 0;
	public static final int USEYEARLY = 1;
	public static final int USEDUTY   = 2;
	public static final int USENONE   =-1;

	/* Earth model */
	public static final int SIMPLECARSON  = 1;
	public static final int FULLCARSON    = 2;
	public static final int DERI          = 3;

    /* Profile plot constants */
	public static final int PROFILE3PH    = 9999;  // some big number > likely no. of phases
	public static final int PROFILEALL    = 9998;
	public static final int PROFILEALLPRI = 9997;
	public static final int PROFILELLALL  = 9996;
	public static final int PROFILELLPRI  = 9995;
	public static final int PROFILELL     = 9994;

	/* 120-degree shift constant */
	public static final Complex CALPHA = new Complex(-0.5, -0.866025);
	public static final double SQRT2 = Math.sqrt(2.0);
	public static final double SQRT3 = Math.sqrt(3.0);
	public static final double InvSQRT3 = 1.0 / SQRT3;
	public static final double InvSQRT3x1000 = InvSQRT3 * 1000.0;

	/* DSS Forms */
	DSSForms Forms = CommandLineDSSForms.getInstance();

	/* Variables */
	private boolean DLLFirstTime = true;
	private PrintStream DLLDebugFile;
	private String DSS_IniFileName = "OpenDSSPanel.ini";
	// Registry   (See Executive)
//	private IniRegSave DSS_Registry = IniRegSave("\\Software\\OpenDSS");

	private boolean IsDLL = false;
	private boolean NoFormsAllowed = false;

	private Circuit ActiveCircuit;
	private DSSClass ActiveDSSClass;
	private int LastClassReferenced;  // index of class of last thing edited
	private DSSObject ActiveDSSObject;
	private int NumCircuits = 0;
	private int MaxCircuits = 1;
	private int MaxBusLimit = 0;  // set in validation
	private int MaxAllocationIterations = 2;
	private ArrayList<Circuit> Circuits;
	private ArrayList<DSSObject> DSSObjs;

	// auxiliary parser for use by anybody for reparsing values
	private Parser AuxParser = Parser.getInstance();

	private boolean ErrorPending = false;
	private int CmdResult = 0;
	private int ErrorNumber = 0;
	private String LastErrorMessage = "";

	private int DefaultEarthModel = DERI;
	private int ActiveEarthModel = DefaultEarthModel;

	private String LastFileCompiled = "";
	private boolean LastCommandWasCompile = false;

	private boolean SolutionAbort = false;
	private boolean InShowResults = false;
	private boolean Redirect_Abort = false;
	private boolean In_Redirect = false;
	private boolean DIFilesAreOpen = false;
	private boolean AutoShowExport = false;
	private boolean SolutionWasAttempted = false;

	private String GlobalHelpString = "";
	private String GlobalPropertyValue = "";
	private String GlobalResult = "";
	private String VersionString = "Version " + getDSSVersion();

	private String DefaultEditor = "NotePad";
	private String DSSFileName;// = GetDSSExeFile();  // name of current exe or DLL
	private String DSSDirectory;// = new File(DSSFileName).getParent();  // where the current exe resides
	private String StartupDirectory = System.getProperty("user.dir") + "/";  // starting point
	private String DSSDataDirectory = StartupDirectory;
	private String CircuitName_;  // name of circuit with a "_" appended
	private String CurrentDirectory = StartupDirectory;  // current working directory

	private double DefaultBaseFreq = 60.0;
	private double DaisySize = 1.0;

	// commonly used classes
	private LoadShape LoadShapeClass;
	private TShape TShapeClass;
	private PriceShape PriceShapeClass;
	private XYCurve XYCurveClass;
	private GrowthShape GrowthShapeClass;
	private Spectrum SpectrumClass;
	private DSSClass SolutionClass;
	private EnergyMeter EnergyMeterClass;
	//private Feeder FeederClass;
	private Monitor MonitorClass;
	private Sensor SensorClass;
	private TCC_Curve TCC_CurveClass;
	private WireData WireDataClass;
	private CNData CNDataClass;
	private TSData TSDataClass;
	private LineSpacing LineSpacingClass;
	private Storage StorageClass;
	private PVSystem PVSystemClass;

	private List<String> EventStrings;
	private List<String> SavedFileList;

	private List<DSSClass> DSSClassList;  // base class types
	private HashList ClassNames;

	// private constructor prevents instantiation from other classes
	private DSSGlobals() {
	}

	/**
	 * DSSGlobalsHolder is loaded on the first execution of
	 * DSSGlobals.getInstance() or the first access to
	 * DSSGlobalsHolder.INSTANCE, not before.
	 */
	private static class DSSGlobalsHolder {
		public static final DSSGlobals INSTANCE = new DSSGlobals();
	}

	public static DSSGlobals getInstance() {
		return DSSGlobalsHolder.INSTANCE;
	}

	public void doErrorMsg(String S, String Emsg, String ProbCause, int ErrNum) {
		String Msg = String.format("Error %d reported from DSS function: ", ErrNum) + S
		+ CRLF + "Error description: " + CRLF + Emsg
		+ CRLF + "Probable cause: " + CRLF + ProbCause;

		if (!NoFormsAllowed) {
			if (In_Redirect) {
				int RetVal = Forms.messageDlg(Msg, false);
				if (RetVal == -1)
					Redirect_Abort = true;
			} else {
				Forms.messageDlg(Msg, true);
			}
		}

		LastErrorMessage = Msg;
		ErrorNumber = ErrNum;
		appendGlobalResult(Msg);
	}

	public void doSimpleMsg(String S, int ErrNum) {
		if (!NoFormsAllowed) {
			if (In_Redirect) {
				int RetVal = Forms.messageDlg(String.format("(%d) %s%s", ErrNum, CRLF, S), false);
				if (RetVal == -1)
					Redirect_Abort = true;
			} else {
				Forms.infoMessageDlg(String.format("(%d) %s%s", ErrNum, CRLF, S));
			}
		}

		LastErrorMessage = S;
		ErrorNumber = ErrNum;
		appendGlobalResult(S);
	}

	public void clearAllCircuits() {
		ActiveCircuit = null;
		Circuits = new ArrayList<Circuit>(2);  // make a new list of circuits
		NumCircuits = 0;
	}

	/**
	 * Set object active by name.
	 */
	public void setObject(String Param) {
		String ObjName, ObjClass = null;

		// Split off obj class and name
		int dotpos = Param.indexOf(".");
		switch (dotpos) {
		case 0:
			// assume it is all name; class defaults
			ObjName = Param;
			break;
		default:
			ObjClass = Param.substring(0, dotpos - 1);
			ObjName = Param.substring(dotpos + 1, Param.length());
			break;
		}

		if (ObjClass.length() > 0)
			DSSClassDefs.setObjectClass(ObjClass);

		ActiveDSSClass = DSSClassList.get(LastClassReferenced);
		if (ActiveDSSClass != null) {
			if (!ActiveDSSClass.setActive(ObjName)) {
				// scroll through list of objects until a match
				doSimpleMsg("Error: Object \"" + ObjName + "\" not found." + CRLF + Parser.getInstance().getCmdString(), 904);
			} else {
				switch (ActiveDSSObject.getDSSObjType()) {
				case DSSClassDefs.DSS_OBJECT:
					// do nothing for general DSS object
					break;
				default:
					// for circuit types, set active circuit element too
					ActiveCircuit.setActiveCktElement((DSSCktElement) ActiveDSSClass.getActiveObj());
					break;
				}
			}
		} else {
			doSimpleMsg("Error: Active object type/class is not set.", 905);
		}
	}

	/** Finds the bus and sets it active. */
	public int setActiveBus(String BusName) {
		int Result = 0;

		if (ActiveCircuit.getBusList().listSize() == 0)
			return Result;   // bus list not yet built

		ActiveCircuit.setActiveBusIndex(ActiveCircuit.getBusList().find(BusName));

		if (ActiveCircuit.getActiveBusIndex() == 0) {
			Result = 1;
			appendGlobalResult("SetActiveBus: Bus " + BusName + " not found.");
		}

		return Result;
	}

	/** Pathname may be null */
	public void setDataPath(String PathName) {
		File F = new File(PathName);

		if ((PathName.length() > 0) && !F.exists()) {

			// try to create the directory
			if (F.mkdir()) {
				doSimpleMsg("Cannot create " + PathName + " directory.", 907);
				System.exit(0);
			}

		}

		DSSDataDirectory = PathName;

		// Put a \ on the end if not supplied. Allow a null specification.
		if (DSSDataDirectory.length() > 0) {
	    	setCurrentDirectory(DSSDataDirectory);   // change to specified directory
//			if (DSSDataDirectory.charAt(DSSDataDirectory.length()) != '\\') {
//					DSSDataDirectory = DSSDataDirectory + "\\";
//			}
		}
	}


	public void makeNewCircuit(String Name) {
		if (NumCircuits < MaxCircuits) {
			ActiveCircuit = new DSSCircuit(Name);
			ActiveDSSObject = SolutionImpl.getActiveSolutionObj();
			/*Handle = */ Circuits.add(ActiveCircuit);
			NumCircuits += 1;
			// pass remainder of string on to VSource
			String S = Parser.getInstance().getRemainder();

			/* create a default circuit */
			SolutionAbort = false;
			/* Voltage source named "source" connected to "SourceBus" */
			// load up the parser as if it were read in
			DSSExecutive.getInstance().setCommand("new object=vsource.source bus1=SourceBus " + S);
		} else {
			doErrorMsg("makeNewCircuit", "Cannot create new circuit.",
					"Max. circuits exceeded." + CRLF +
					"(Max no. of circuits=" + String.valueOf(MaxCircuits) + ")", 906);
		}
	}

	/**
	 * Append a string to global result, separated by commas.
	 */
	public void appendGlobalResult(String S) {
		if (GlobalResult.length() == 0) {
			GlobalResult = S;
		} else {
			GlobalResult = GlobalResult + ", " + S;
		}
	}

	/**
	 * Separate by CRLF.
	 */
	public void appendGlobalResultCRLF(String S) {
		if (GlobalResult.length() > 0) {
			GlobalResult += CRLF + S;
		} else {
			GlobalResult = S;
		}
	}

	public void WriteDLLDebugFile(String S) {
		boolean Append;
		if (DLLFirstTime) {
			Append = false;
			DLLFirstTime = false;
		} else {
			Append = true;
		}
		FileWriter Writer;
		try {
			Writer = new FileWriter(DSSDataDirectory + "DSSDLLDebug.txt", Append);
			BufferedWriter DLLDebugFile = new BufferedWriter(Writer);
			DLLDebugFile.write(S + CRLF);
			DLLDebugFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
	}

	public boolean isDLLFirstTime() {
		return DLLFirstTime;
	}

	public void setDLLFirstTime(boolean dLLFirstTime) {
		DLLFirstTime = dLLFirstTime;
	}

	public PrintStream getDLLDebugFile() {
		return DLLDebugFile;
	}

	public void setDLLDebugFile(PrintStream dLLDebugFile) {
		DLLDebugFile = dLLDebugFile;
	}

	public String getDSS_IniFileName() {
		return DSS_IniFileName;
	}

	public void setDSS_IniFileName(String dSS_IniFileName) {
		DSS_IniFileName = dSS_IniFileName;
	}

//	public IniRegSave getDSS_Registry() {
//		return DSS_Registry;
//	}

//	public void setDSS_Registry(IniRegSave dSS_Registry) {
//		DSS_Registry = dSS_Registry;
//	}

	public boolean isDLL() {
		return IsDLL;
	}

	public void setDLL(boolean isDLL) {
		IsDLL = isDLL;
	}

	public boolean isNoFormsAllowed() {
		return NoFormsAllowed;
	}

	public void setNoFormsAllowed(boolean noFormsAllowed) {
		NoFormsAllowed = noFormsAllowed;
	}

	public Circuit getActiveCircuit() {
		return ActiveCircuit;
	}

	public void setActiveCircuit(Circuit activeCircuit) {
		ActiveCircuit = activeCircuit;
	}

	public DSSClass getActiveDSSClass() {
		return ActiveDSSClass;
	}

	public void setActiveDSSClass(DSSClass activeDSSClass) {
		ActiveDSSClass = activeDSSClass;
	}

	public int getLastClassReferenced() {
		return LastClassReferenced;
	}

	public void setLastClassReferenced(int lastClassReferenced) {
		LastClassReferenced = lastClassReferenced;
	}

	public DSSObject getActiveDSSObject() {
		return ActiveDSSObject;
	}

	public void setActiveDSSObject(DSSObject activeDSSObject) {
		ActiveDSSObject = activeDSSObject;
	}

	public int getNumCircuits() {
		return NumCircuits;
	}

	public void setNumCircuits(int numCircuits) {
		NumCircuits = numCircuits;
	}

	public int getMaxCircuits() {
		return MaxCircuits;
	}

	public void setMaxCircuits(int maxCircuits) {
		MaxCircuits = maxCircuits;
	}

	public int getMaxBusLimit() {
		return MaxBusLimit;
	}

	public void setMaxBusLimit(int maxBusLimit) {
		MaxBusLimit = maxBusLimit;
	}

	public int getMaxAllocationIterations() {
		return MaxAllocationIterations;
	}

	public void setMaxAllocationIterations(int maxAllocationIterations) {
		MaxAllocationIterations = maxAllocationIterations;
	}

	public ArrayList<Circuit> getCircuits() {
		return Circuits;
	}

	public void setCircuits(ArrayList<Circuit> circuits) {
		Circuits = circuits;
	}

	public ArrayList<DSSObject> getDSSObjs() {
		return DSSObjs;
	}

	public void setDSSObjs(ArrayList<DSSObject> dSSObjs) {
		DSSObjs = dSSObjs;
	}

	public Parser getAuxParser() {
		return AuxParser;
	}

	public void setAuxParser(Parser auxParser) {
		AuxParser = auxParser;
	}

	public boolean isErrorPending() {
		return ErrorPending;
	}

	public void setErrorPending(boolean errorPending) {
		ErrorPending = errorPending;
	}

	public int getCmdResult() {
		return CmdResult;
	}

	public void setCmdResult(int cmdResult) {
		CmdResult = cmdResult;
	}

	public int getErrorNumber() {
		return ErrorNumber;
	}

	public void setErrorNumber(int errorNumber) {
		ErrorNumber = errorNumber;
	}

	public String getLastErrorMessage() {
		return LastErrorMessage;
	}

	public void setLastErrorMessage(String lastErrorMessage) {
		LastErrorMessage = lastErrorMessage;
	}

	public int getDefaultEarthModel() {
		return DefaultEarthModel;
	}

	public void setDefaultEarthModel(int defaultEarthModel) {
		DefaultEarthModel = defaultEarthModel;
	}

	public int getActiveEarthModel() {
		return ActiveEarthModel;
	}

	public void setActiveEarthModel(int activeEarthModel) {
		ActiveEarthModel = activeEarthModel;
	}

	public String getLastFileCompiled() {
		return LastFileCompiled;
	}

	public void setLastFileCompiled(String lastFileCompiled) {
		LastFileCompiled = lastFileCompiled;
	}

	public boolean isLastCommandWasCompile() {
		return LastCommandWasCompile;
	}

	public void setLastCommandWasCompile(boolean lastCommandWasCompile) {
		LastCommandWasCompile = lastCommandWasCompile;
	}

	public boolean isSolutionAbort() {
		return SolutionAbort;
	}

	public void setSolutionAbort(boolean solutionAbort) {
		SolutionAbort = solutionAbort;
	}

	public boolean isInShowResults() {
		return InShowResults;
	}

	public void setInShowResults(boolean inShowResults) {
		InShowResults = inShowResults;
	}

	public boolean isRedirect_Abort() {
		return Redirect_Abort;
	}

	public void setRedirect_Abort(boolean redirect_Abort) {
		Redirect_Abort = redirect_Abort;
	}

	public boolean isIn_Redirect() {
		return In_Redirect;
	}

	public void setIn_Redirect(boolean in_Redirect) {
		In_Redirect = in_Redirect;
	}

	public boolean isDIFilesAreOpen() {
		return DIFilesAreOpen;
	}

	public void setDIFilesAreOpen(boolean dIFilesAreOpen) {
		DIFilesAreOpen = dIFilesAreOpen;
	}

	public boolean isAutoShowExport() {
		return AutoShowExport;
	}

	public void setAutoShowExport(boolean autoShowExport) {
		AutoShowExport = autoShowExport;
	}

	public boolean isSolutionWasAttempted() {
		return SolutionWasAttempted;
	}

	public void setSolutionWasAttempted(boolean solutionWasAttempted) {
		SolutionWasAttempted = solutionWasAttempted;
	}

	public String getGlobalHelpString() {
		return GlobalHelpString;
	}

	public void setGlobalHelpString(String globalHelpString) {
		GlobalHelpString = globalHelpString;
	}

	public String getGlobalPropertyValue() {
		return GlobalPropertyValue;
	}

	public void setGlobalPropertyValue(String globalPropertyValue) {
		GlobalPropertyValue = globalPropertyValue;
	}

	public String getGlobalResult() {
		return GlobalResult;
	}

	public void setGlobalResult(String globalResult) {
		GlobalResult = globalResult;
	}

	public String getVersionString() {
		return VersionString;
	}

	public void setVersionString(String versionString) {
		VersionString = versionString;
	}

	public String getDefaultEditor() {
		return DefaultEditor;
	}

	public void setDefaultEditor(String defaultEditor) {
		DefaultEditor = defaultEditor;
	}

	public String getDSSFileName() {
		return DSSFileName;
	}

	public void setDSSFileName(String dSSFileName) {
		DSSFileName = dSSFileName;
	}

	public String getDSSDirectory() {
		return DSSDirectory;
	}

	public void setDSSDirectory(String dSSDirectory) {
		DSSDirectory = dSSDirectory;
	}

	public String getStartupDirectory() {
		return StartupDirectory;
	}

	public void setStartupDirectory(String startupDirectory) {
		StartupDirectory = startupDirectory;
	}

	public String getDSSDataDirectory() {
		return DSSDataDirectory;
	}

	public void setDSSDataDirectory(String dSSDataDirectory) {
		DSSDataDirectory = dSSDataDirectory;
	}

	public String getCircuitName_() {
		return CircuitName_;
	}

	public void setCircuitName_(String circuitName_) {
		CircuitName_ = circuitName_;
	}

	public double getDefaultBaseFreq() {
		return DefaultBaseFreq;
	}

	public void setDefaultBaseFreq(double defaultBaseFreq) {
		DefaultBaseFreq = defaultBaseFreq;
	}

	public double getDaisySize() {
		return DaisySize;
	}

	public void setDaisySize(double daisySize) {
		DaisySize = daisySize;
	}

	public LoadShape getLoadShapeClass() {
		return LoadShapeClass;
	}

	public void setLoadShapeClass(LoadShape loadShapeClass) {
		LoadShapeClass = loadShapeClass;
	}

	public GrowthShape getGrowthShapeClass() {
		return GrowthShapeClass;
	}

	public void setGrowthShapeClass(GrowthShape growthShapeClass) {
		GrowthShapeClass = growthShapeClass;
	}

	public Spectrum getSpectrumClass() {
		return SpectrumClass;
	}

	public void setSpectrumClass(Spectrum spectrumClass) {
		SpectrumClass = spectrumClass;
	}

	public DSSClass getSolutionClass() {
		return SolutionClass;
	}

	public void setSolutionClass(DSSClass solutionClass) {
		SolutionClass = solutionClass;
	}

	public EnergyMeter getEnergyMeterClass() {
		return EnergyMeterClass;
	}

	public void setEnergyMeterClass(EnergyMeter energyMeterClass) {
		EnergyMeterClass = energyMeterClass;
	}

//	public Feeder getFeederClass() {
//		return FeederClass;
//	}
//
//	public void setFeederClass(Feeder feederClass) {
//		FeederClass = feederClass;
//	}

	public Monitor getMonitorClass() {
		return MonitorClass;
	}

	public void setMonitorClass(Monitor monitorClass) {
		MonitorClass = monitorClass;
	}

	public Sensor getSensorClass() {
		return SensorClass;
	}

	public void setSensorClass(Sensor sensorClass) {
		SensorClass = sensorClass;
	}

	public TCC_Curve getTCC_CurveClass() {
		return TCC_CurveClass;
	}

	public void setTCC_CurveClass(TCC_Curve tCC_CurveClass) {
		TCC_CurveClass = tCC_CurveClass;
	}

	public WireData getWireDataClass() {
		return WireDataClass;
	}

	public void setWireDataClass(WireData wireDataClass) {
		WireDataClass = wireDataClass;
	}

	public LineSpacing getLineSpacingClass() {
		return LineSpacingClass;
	}

	public void setLineSpacingClass(LineSpacing lineSpacingClass) {
		LineSpacingClass = lineSpacingClass;
	}

	public Storage getStorageClass() {
		return StorageClass;
	}

	public void setStorageClass(Storage storageClass) {
		StorageClass = storageClass;
	}

	public TShape getTShapeClass() {
		return TShapeClass;
	}

	public void setTShapeClass(TShape tShapeClass) {
		TShapeClass = tShapeClass;
	}

	public PriceShape getPriceShapeClass() {
		return PriceShapeClass;
	}

	public void setPriceShapeClass(PriceShape priceShapeClass) {
		PriceShapeClass = priceShapeClass;
	}

	public XYCurve getXYCurveClass() {
		return XYCurveClass;
	}

	public void setXYCurveClass(XYCurve xYCurveClass) {
		XYCurveClass = xYCurveClass;
	}

	public CNData getCNDataClass() {
		return CNDataClass;
	}

	public void setCNDataClass(CNData cNDataClass) {
		CNDataClass = cNDataClass;
	}

	public TSData getTSDataClass() {
		return TSDataClass;
	}

	public void setTSDataClass(TSData tSDataClass) {
		TSDataClass = tSDataClass;
	}

	public PVSystem getPVSystemClass() {
		return PVSystemClass;
	}

	public void setPVSystemClass(PVSystem pVSystemClass) {
		PVSystemClass = pVSystemClass;
	}

	public List<String> getEventStrings() {
		return EventStrings;
	}

	public void setEventStrings(List<String> eventStrings) {
		EventStrings = eventStrings;
	}

	public List<String> getSavedFileList() {
		return SavedFileList;
	}

	public void setSavedFileList(List<String> savedFileList) {
		SavedFileList = savedFileList;
	}

	public List<DSSClass> getDSSClassList() {
		return DSSClassList;
	}

	public void setDSSClassList(List<DSSClass> dSSClassList) {
		DSSClassList = dSSClassList;
	}

	public HashList getClassNames() {
		return ClassNames;
	}

	public void setClassNames(HashList classNames) {
		ClassNames = classNames;
	}

	public void readDSS_Registry() {
		throw new UnsupportedOperationException();
	}

	public void writeDSS_Registry() {
		throw new UnsupportedOperationException();
	}

	public boolean isDSSDLL(String Fname) {
		throw new UnsupportedOperationException();
	}

	public String getDSSVersion() {
		// TODO: Implement GetDSSVersion()
		return "Unknown.";
	}

	public DSSForms getDSSForms() {
		return Forms;
	}

	public void setDSSForms(DSSForms forms) {
		Forms = forms;
	}

	public String getCurrentDirectory() {
		return CurrentDirectory;
	}

	public void setCurrentDirectory(String currentDirectory) {
		CurrentDirectory = currentDirectory;
	}

}