package com.epri.common;

import java.io.PrintStream;

import com.epri.dss.general.NamedObject;

public class DSSCircuit extends NamedObject {

	private class CktElementDef {
		public int CktElementClass;
		public int devHandle;
	}

	private int[] NodeBuffer;
	private int NodeBufferMax;
	private boolean BusNameRedefined;
	private DSSCktElement ActiveCktElement;

    // Temp arrays for when the bus swap takes place
    private Bus[] SavedBuses;
    private String[] SavedBusNames;
    private int SavedNumBuses;

    /* global multiplier for every load */
    private double LoadMultiplier;

    private boolean AbortBusProcess;

    /* topology from the first source, lazy evaluation */
    private CktTree Branch_List;
    /* bus adjacency lists of PD and PC elements */
    private AdjArray BusAdjPC, BusAdjPD;

    public String CaseName;

    public int ActiveBusIndex;
    /* fundamental and default base frequency */
    public double Fundamental;

    /* Flag for use by control elements to detect redefinition of buses */
    public boolean Control_BusNameRedefined;

    public HashList BusList, AutoAddBusList, DeviceList;
    public CktElementDef[] DeviceRef;  //Type and handle of device

    // lists of pointers to different elements by class
    public PointerList Faults, CktElements, PDElements, PCElements, DSSControls,
    	Sources, MeterElements, Sensors, Monitors, EnergyMeters, Generators,
    	StorageElements, Substations, Transformers, CapControls, RegControls,
    	Lines, Loads, ShuntCapacitors, Feeders, SwtControls;

    public ControlQueue ControlQueue;

    public SolutionObj Solution;
    public AutoAdd AutoAddObj;

    // For AutoAdd stuff
    public double UEWeight, LossWeight;

    public int NumUEregs, NumLossRegs;
    public int[] UEregs, LossRegs;

    public double CapacityStart, CapacityIncrement;

    public boolean TrapezoidalIntegration,  // flag for trapezoidal integration
    LogEvents;

    public String LoadDurCurve;
    public LoadShapeObj LoadDurCurveObj;
    public String PriceCurve;
    public LoadShapeObj PriceCurveObj;

    public int NumDevices, NumBuses, NumNodes;
    public int MaxDevices, MaxBuses, MaxNodes;
    public int IncDevices, IncBuses, IncNodes;

    // Bus and Node stuff
    public Bus[] Buses;
    public NodeBus[] MapNodeToBus;

    // Flags
    public boolean IsSolved;
    public boolean DuplicatesAllowed;
    public boolean ZonesLocked;
    public boolean MeterZonesComputed;
    public boolean PositiveSequence;  // Model is to be interpreted as Pos seq

    // Voltage limits
    /* per unit voltage restraints for this circuit */
    public double NormalMinVolts, NormalMaxVolts, EmergMaxVolts, EmergMinVolts;
    public double[] LegalVoltageBases;

    // Global circuit multipliers
    public double GeneratorDispatchReference,
    DefaultGrowthFactor,
    DefaultGrowthRate,
    GenMultiplier,   // global multiplier for every generator
    HarmMult;
    public double[] DefaultHourMult; // complex

    public double PriceSignal; // price signal for entire circuit

    // EnergyMeter Totals
    public double[] RegisterTotals;

    public LoadShapeObj DefaultDailyShapeObj, DefaultYearlyShapeObj;

    public String CurrentDirectory;

    public ReductionStrategy ReductionStrategy;
    public double ReductionMaxAngle, ReductionZmag;
    public String ReductionStrategyString;

    public double PctNormalFactor;

    /* Plot Markers */
    public int NodeMarkerCode, NodeMarkerWidth, SwitchMarkerCode;
    public int TransMarkerSize, TransMarkerCode;

    public boolean MarkSwitches;
    public boolean MarkTransformers;

	public DSSCircuit(String aName) {
		super("Circuit");

		this.IsSolved = false;
	    /*Retval   = */ SolutionClass.NewObject(Get_Name());
	    this.Solution = ActiveSolutionObj;

	    this.LocalName = aName.toLowerCase();

	    this.CaseName = aName;  // Default case name to circuitname
	                            // Sets CircuitName_

	    this.Fundamental = DSSGlobals.getInstance().DefaultBaseFreq;
	    this.ActiveCktElement = null;
	    this.ActiveBusIndex = 1;    // Always a bus

	    // initial allocations increased from 100 to 1000 to speed things up

	    this.MaxBuses   = 1000;  // good sized allocation to start
	    this.MaxDevices = 1000;
	    this.MaxNodes   = 3 * MaxBuses;
	    this.IncDevices = 1000;
	    this.IncBuses   = 1000;
	    this.IncNodes   = 3000;

	    // Allocate some nominal sizes
	    this.BusList    = new HashList(900);  // Bus name list Nominal size to start; gets reallocated
	    this.DeviceList = new HashList(900);
	    this.AutoAddBusList = new HashList(100);

	    this.NumBuses   = 0;  // Eventually allocate a single source
	    this.NumDevices = 0;
	    this.NumNodes   = 0;

	    this.Faults       = PointerList(2);
	    this.CktElements  = PointerList(1000);
	    this.PDElements   = PointerList(1000);
	    this.PCElements   = PointerList(1000);
	    this.DSSControls  = PointerList(10);
	    this.Sources      = PointerList(10);
	    this.MeterElements= PointerList(20);
	    this.Monitors     = PointerList(20);
	    this.EnergyMeters = PointerList(5);
	    this.Sensors      = PointerList(5);
	    this.Generators   = PointerList(5);
	    this.StorageElements = PointerList(5);
	    this.Feeders      = PointerList(10);
	    this.Substations  = PointerList(5);
	    this.Transformers = PointerList(10);
	    this.CapControls  = PointerList(10);
	    this.SwtControls  = PointerList(50);
	    this.RegControls  = PointerList(5);
	    this.Lines        = PointerList(1000);
	    this.Loads        = PointerList(1000);
	    this.ShuntCapacitors = PointerList(20);

	    this.Buses     = new Bus[this.MaxBuses];
	    this.MapNodeToBus = NodeBus[this.MaxNodes];
	    this.DeviceRef = new CktElementDef[MaxDevices];

	    this.ControlQueue = new ControlQueue();

	    this.LegalVoltageBases = new double[8];
	     // Default Voltage Bases
	    this.LegalVoltageBases[0] = 0.208;
	    this.LegalVoltageBases[1] = 0.480;
	    this.LegalVoltageBases[2] = 12.47;
	    this.LegalVoltageBases[3] = 24.9;
	    this.LegalVoltageBases[4] = 34.5;
	    this.LegalVoltageBases[5] = 115.0;
	    this.LegalVoltageBases[6] = 230.0;
	    this.LegalVoltageBases[7] = 0.0;  // terminates array

	    this.NodeBufferMax = 20;
	    this.NodeBuffer    = new int[this.NodeBufferMax]; // A place to hold the nodes

	     // Init global circuit load and harmonic source multipliers
	    this.LoadMultiplier = 1.0;
	    this.GenMultiplier = 1.0;
	    this.HarmMult = 1.0;

	    this.PriceSignal = 25.0;   // $25/MWH

	     // Factors for Autoadd stuff
	    this.UEWeight   = 1.0;  // Default to weighting UE same as losses
	    this.LossWeight = 1.0;
	    this.NumUEregs  = 1;
	    this.NumLossRegs = 1;
	    this.UEregs = new int[NumUEregs];
	    this.LossRegs = new int[NumLossregs];
	    this.UEregs[0]      = 10;   // Overload UE
	    this.LossRegs[0]    = 13;   // Zone Losses

	    this.CapacityStart = 0.9;     // for Capacity search
	    this.CapacityIncrement = 0.005;

	    this.LoadDurCurve    = "";
	    this.LoadDurCurveObj = null;
	    this.PriceCurve    = "";
	    this.PriceCurveObj = null;

        // Flags
	    this.DuplicatesAllowed   = false;
	    this.ZonesLocked         = false;   // Meter zones recomputed after each change
	    this.MeterZonesComputed  = false;
	    this.PositiveSequence    = false;

	    this.NormalMinVolts = 0.95;
	    this.NormalMaxVolts = 1.05;
	    this.EmergMaxVolts  = 1.08;
	    this.EmergMinVolts  = 0.90;

	    this.NodeMarkerCode = 16;
	    this.NodeMarkerWidth = 1;
	    this.MarkSwitches     = false;
	    this.MarkTransformers = false;
	    this.SwitchMarkerCode = 5;
	    this.TransMarkerCode  = 35;
	    this.TransMarkerSize  = 1;


	    this.TrapezoidalIntegration = false;  // Default to Euler method
	    this.LogEvents = false;

	    this.GeneratorDispatchReference = 0.0;
	    this.DefaultGrowthRate = 1.025;
	    this.DefaultGrowthFactor = 1.0;

	    this.DefaultDailyShapeObj  = LoadShapeClass.Find("default");
	    this.DefaultYearlyShapeObj = LoadShapeClass.Find("default");

	    this.CurrentDirectory = "";

	    this.BusNameRedefined = true;  // set to force rebuild of buslists, nodelists

	    this.SavedBuses = null;
	    this.SavedBusNames = null;

	    this.ReductionStrategy = rsDefault;
	    this.ReductionMaxAngle = 15.0;
	    this.ReductionZmag = 0.02;

	    /* Misc objects */
	    this.AutoAddObj = new AutoAdd();

	    this.Branch_List = null;
	    this.BusAdjPC = null;
	    this.BusAdjPD = null;
	}

	private void AddDeviceHandle(int Handle) {
		if (NumDevices > MaxDevices) {
			MaxDevices = MaxDevices + IncDevices;
			// FIXME: Set min capacity of array list
			DeviceRef = new CktElementDef[MaxDevices];
		}
		DeviceRef[NumDevices].devHandle = Handle;    // Index into CktElements
		DeviceRef[NumDevices].CktElementClass = DSSGlobals.getInstance().LastClassReferenced;
	}

	private void AddABus() {
	    if (NumBuses > MaxBuses) {
	    	MaxBuses += IncBuses;
			// FIXME: Set min capacity of array list
	    	Buses = new Bus[MaxBuses];
	    }
	}

	private void AddANodeBus() {
		if (NumNodes > MaxNodes) {
			MaxNodes += IncNodes;
			// FIXME: Set min capacity of array list
			MapNodeToBus = new NodeBus[MaxNodes];
		}
	}

	private int AddBus(String BusName, int NNodes) {
		// Trap error in bus name
	    if (BusName.length() == 0) {  // Error in busname
	       DSSGlobals.getInstance().DoErrorMsg("TDSSCircuit.AddBus", "BusName for Object \"" + ActiveCktElement.Name + "\" is null.",
	                  "Error in definition of object.", 424);
	       for (int i = 0; i < ActiveCktElement.NConds; i++) {
	    	   NodeBuffer[i] = 0;
	       }
	       return 0;
	    }

	    int Result = BusList.Find(BusName);
	    if (Result == 0) {
	         Result = BusList.Add(BusName);    // Result is index of bus
	         NumBuses += 1;
	         AddABus();   // Allocates more memory if necessary
	         Buses[NumBuses] = new DSSBus();
	    }

	    /* Define nodes belonging to the bus */
	    /* Replace Nodebuffer values with global reference number */
	    int NodeRef;
	    for (int i = 0; i < NNodes; i++) {
	         NodeRef = Buses[Result].Add(NodeBuffer[i]);
	         if (NodeRef == NumNodes) { // This was a new node so Add a NodeToBus element ????
	             AddANodeBus();   // Allocates more memory if necessary
	             MapNodeToBus[NumNodes].BusRef  = Result;
	             MapNodeToBus[NumNodes].NodeNum = NodeBuffer[i];
	         }
	         NodeBuffer[i] = NodeRef;  //  Swap out in preparation to setnoderef call
	    }
		return Result;
	}

	public void Set_ActiveCktElement(DSSCktElement Value) {
		ActiveCktElement = Value;
		DSSGlobals.getInstance().ActiveDSSObject = Value;
	}

	public DSSCktElement Get_ActiveCktElement() {
		return null;
	}

	public void Set_BusNameRedefined(boolean Value) {

	}

	public boolean Is_BusNameRedefined() {

	}

	/* Total Circuit PD Element losses */
	public double[] Get_Losses() {
		return new double[] {0.0, 0.0};
	}

	public void Set_LoadMultiplier(double Value) {

	}

	public double Get_LoadMultiplier() {
		return 0.0;
	}

	private void SaveBusInfo() {

	}

	private void RestoreBusInfo() {

	}

	private boolean SaveMasterFile() {
		return false;
	}

	private boolean SaveDSSObjects() {
		return false;
	}

	private boolean SaveFeeders() {
		return false;
	}

	private boolean SaveBusCoords() {
		return false;
	}

	private void ReallocDeviceList() {

	}

	public void Set_CaseName(String Value) {

	}

	public String Get_CaseName() {
		return null;
	}

	public String Get_Name() {
		return null;
	}

	/* Adds last DSS object created to circuit */
	public void AddCktElement(int Handle) {

	}

	public void TotalizeMeters() {

	}

	public boolean ComputeCapacity() {

	}

	public boolean Save(String Dir) {

	}

	public void ProcessBusDefs() {
	    int np = ActiveCktElement.NPhases;
	    int NCond = ActiveCktElement.NConds;

	    Parser.Token = ActiveCktElement.FirstBus;     // use parser functions to decode
	    for (int iTerm = 0; iTerm < ActiveCktElement.Nterms; iTerm++) {
	    	boolean NodesOK = true;
	        // Assume normal phase rotation  for default
	    	for (int i = 0; i < np; i++)
				NodeBuffer[i] = i; // set up buffer with defaults

	        // Default all other conductors to a ground connection
	        // If user wants them ungrounded, must be specified explicitly!
	    	for (int i = np + 1; i < NCond; i++)
				NodeBuffer[i] = 0;

			// Parser will override bus connection if any specified
	        BusName = Parser.ParseAsBusName(NNodes, NodeBuffer);

	    	// Check for error in node specification
	    	for (int j = 0; j < NNodes; j++) {
				if (NodeBuffer[j] < 0) {
					int retval = DSSMessageDlg("Error in Node specification for Element: \""
	                     + ActiveCktElement.ParentClass.Name + "." + Name + "\"" + CRLF +
	                     "Bus Spec: \"" + Parser.Token + "\"", false);
					NodesOK = false;
	                if (retval == -1) {
	                    AbortBusProcess = true;
	                    DSSGlobals.getInstance().AppendGlobalResult("Aborted bus process.");
	                    return;
	                }
	                break;
				}
	    	}


	        // Node -Terminal Connnections
	        // Caution: Magic -- AddBus replaces values in nodeBuffer to correspond
	        // with global node reference number.
	        if (NodesOK) {
	        	ActiveCktElement.ActiveTerminalIdx = iTerm;
	        	ActiveCktElement.ActiveTerminal.BusRef = AddBus(BusName, Ncond);
	            SetNodeRef(iTerm, NodeBuffer);  // for active circuit
	        }
	        Parser.Token = NextBus;
	    }
	}

	public void ReProcessBusDefs() {

	}

	public void DoResetMeterZones() {

	}

	public int SetElementActive(String FullObjectName) {
		int Result = 0;

		Utilities.ParseObjectClassandName(FullObjectName, DevType, DevName);
		DevClassIndex = ClassNames.Find(DevType);
		if (DevClassIndex == 0)
			DevClassIndex = DSSGlobals.getInstance().LastClassReferenced;
		Devindex = DeviceList.Find(DevName);
		while (DevIndex >= 0) {
			if (DeviceRef[Devindex].CktElementClass == DevClassIndex) {  // we got a match
				DSSGlobals.getInstance().ActiveDSSClass = DSSGlobals.getInstance().DSSClassList.Get(DevClassIndex);
				DSSGlobals.getInstance().LastClassReferenced = DevClassIndex;
				Result = DeviceRef[Devindex].devHandle;
				// ActiveDSSClass.Active := Result;
				//  ActiveCktElement := ActiveDSSClass.GetActiveObj;
				ActiveCktElement = CktElements.Get(Result);
				break;
			}
			DevIndex = Devicelist.FindNext();   // Could be duplicates
		}

		DSSGlobals.getInstance().CmdResult = Result;

		return Result;
	}

	public void InvalidateAllPCElements() {

	}

	public void DebugDump(PrintStream F) {

	}

	/* Access to topology from the first source */
	public CktTree GetTopology() {
		return null;
	}

	public void FreeTopology() {

	}

	public AdjArray GetBusAdjacentPDLists() {
		return null;
	}

	public AdjArray GetBusAdjacentPCLists() {
		return null;
	}


}
