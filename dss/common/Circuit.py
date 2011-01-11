# Copyright (C) 2010 Richard Lincoln
#
# This library is free software; you can redistribute it and/or
# modify it under the terms of the GNU Lesser General Public
# License as published by the Free Software Foundation; either
# version 2.1 of the License, or (at your option) any later version.
#
# This library is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
# Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public
# License along with this library; if not, write to the Free Software
# Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA, USA

import sys

import logging

from numpy import array, zeros

from dss.common.Solution import Solution
from dss.common.Named import Named
from dss.common.ControlQueue import ControlQueue
from dss.common.Bus import Bus
from dss.common.Utilities import ParseObjectClassandName
from dss.general.LoadShape import LoadShape

global DefaultBaseFreq, USENONE, AppendGlobalresult, LastClassReferenced, \
    ClassNames, DSSClassList, ActiveDSSClass, CmdResult

logger = logging.getLogger(__name__)

class Circuit(Named):
    """Defines a container of circuit elements.
    """

    def __init__(self, aName):
        """Initialises a new 'Circuit' instance.
        """
        super(Circuit, self).__init__("Circuit")

        self.LocalName = aName.lower()

        # Default case name to circuitname.
        # Sets CircuitName_
        self.CaseName = aName

        self.ActiveCktElement = None

        self.ActiveBusIndex = 1  # Always a bus

        #: Fundamental and default base frequency.
        self.Fundamental = DefaultBaseFreq

        #: Flag for use by control elements to detect redefinition of buses.
        self.Control_BusNameRedefined = False

        self.BusList = {}
        self.AutoAddBusList = {}
        self.DeviceList = {}

        #: Type and handle of device
        self.DeviceRef = []

        # lists of pointers to different elements by class
        self.faults = []
        self.CktElements = []
        self.PDElements = []
        self.PCElements = []
        self.DSSControls = []
        self.Sources = []
        self.MeterElements = []
        self.voltageSources = []
        self.currentSources = []
        self.sensors = []
        self.monitors = []
        self.energyMeters = []
        self._generators = []
        self.generators = []
        self.StorageElements = []
        self.Substations = []
        self.transformers = []
        self.capControls = []
        self.regControls = []
        self.lines = []
        self.loads = []
        self.shuntCapacitors = []
        self.feeder = []
        self.SwtControls = []

        self.controlQueue = ControlQueue

        self._solution = None
        self.Solution = Solution(self.Name)
#        self.AutoAddObj = AutoAdd()

        # For AutoAdd stuff
        self.UEWeight = 1.0  # Default to weighting UE same as losses
        self.LossWeight = 1.0

        self.NumUEregs = 1
        self.NumLossRegs = 1
        self.Ueregs = [10] # Overload UE
        self.LossRegs = [13] # Zone Losses

        self.CapacityStart = 0.9  # for Capacity search
        self.CapacityIncrement = 0.005

        #: Flag for trapezoidal integration.
        self.TrapezoidalIntegration = False  # Default to Euler method
        self.LogEvents = False

        self.LoadDurCurve = ""
        self.LoadDurCurveObj = None
        self.PriceCurve = ""
        self.PriceCurveObj = None

        self.MaxDevices = 1000 # good sized allocation to start
        self.MaxBuses = 1000
        self.MaxNodes = 3 * self.MaxBuses
        self.IncDevices = 1000
        self.IncBuses = 1000
        self.IncNodes = 3 * 1000
        # Eventually allocate a single source
        self.NumDevices, self.NumBuses, self.NumNodes = 0

        # Bus and Node stuff
        self.Buses = []
        self.MapNodeToBus = []

        # Flags
        self.Issolved = False
        self.DuplicatesAllowed = False
        self.ZonesLocked = False  # Meter zones recomputed after each change
        self.MeterZonesComputed = False
        #: Model is to be interpreted as Pos seq?
        self.PositiveSequence = False

        # Voltage limits
        # per unit voltage restraints for this circuit
        self.NormalMinVolts = 0.95
        self.NormalMaxVolts = 1.05
        self.EmergMaxVolts = 1.08
        self.EmergMinVolts = 0.90
        # Default Voltage Bases
        self.LegalVoltageBases = [0.208, 0.480, 12.47, 24.9, 34.5, 115.0,230.0]

        # Global circuit multipliers
        self.GeneratorDispatchReference = 0.0

        self.defaultGrowthFactor = 1.0
        self.DefaultGrowthRate = 1.025
        #: Global multiplier for every generator.
        self.genMultiplier = 1.0
        # Init global circuit harmonic source multiplier.
        self.HarmMult = 1.0
        self.defaultHourMult = complex(0, 0)

        #: price signal for entire circuit
        self.priceSignal = 25.0  # $25/MWH

        # EnergyMeter Totals
        self.RegisterTotals = []

        self.DefaultDailyShapeObj = LoadShape.Find('default')
        self.DefaultYearlyShapeObj = LoadShape.Find('default')

        self.CurrentDirectory = ""

        self.ReductionStrategy = 0
        self.ReductionMaxAngle = 15.0
        self.ReductionZmag = 0.02
        self.ReductionStrategyString = ""

        self.PctNormalFactor = 100.0

        ## Plot Markers
        self.NodeMarkerCode = 16
        self.NodeMarkerWidth = 1
        self.SwitchMarkerCode = 5
        self.TransMarkerSize = 1
        self.TransMarkerCode = 35

        self.MarkSwitches = False
        self.MarkTransformers = False

        self.ActiveLoadShapeClass = USENONE

        #: Lets control devices know the bus list has changed.
#        self.control_busNameRedefined = control_busNameRedefined

        ## Private
        self._NodeBufferMax = 50
        # A place to hold the nodes.
        self._NodeBuffer = zeros(self._NodeBufferMax, dtype=int)
        self._BusNameRedefined = False
        # set to force rebuild of buslists, nodelists
        self.BusNameRedefined = True
        self._ActiveCktElement = None

        # Temp arrays for when the bus swap takes place
        self._SavedBuses = None #[]
        self._SavedBusNames = None #[]
        self._SavedNumBuses = 0

        self._LoadMultiplier = 1.0
        # Init global circuit load multiplier.
        self.LoadMultiplier = 1.0

        self._AbortBusProcess = False

        self._Branch_List = None
        self._BusAdjPC = None #array([])
        self._BusAdjPD = None #array([])


    def ProcessBusDefs(self):
        BusName = ""
        NNodes, Ncond, i, j, iTerm, RetVal = 0
        NodesOK = False

        ace = self.ActiveCktElement
        np = ace.NPhases
        NCond = ace.NConds

        # use parser functions to decode
#        Parser.Token = ace.FirstBus

        for iTerm in range(ace.Nterms):
            NodesOK = True
            # Assume normal phase rotation  for default
            for i in range(np):
                self._NodeBuffer[i] = i # set up buffer with defaults

            # Default all other conductors to a ground connection
            # If user wants them ungrounded, must be specified explicitly!
            for i in range(np + 1, NCond):
                self._NodeBuffer[i] = 0

            # Parser will override bus connection if any specified
#            BusName = Parser.ParseAsBusName(NNodes, self.NodeBuffer)

            # Check for error in node specification
            for j in range(NNodes):
                if self._NodeBuffer[j] < 0:
#                    logger.error('Error in Node specification for Element: "',
#                     self.__class__.__name__ + '.' + self.Name + '"\n' +
#                     'Bus Spec: "' + Parser.Token + '"')
                    RetVal = -1
                    NodesOK = False
                    if RetVal == -1:
                        self._AbortBusProcess = True
                        AppendGlobalresult('Aborted bus process.')
                    sys.exit(RetVal)
                break

            # Node-Terminal Connections
            # Caution: Magic -- AddBus replaces values in nodeBuffer to
            # correspond with global node reference number.
            if NodesOK:
                ace.ActiveTerminalIdx = iTerm
                ace.ActiveTerminal.BusRef = self.AddBus(BusName, Ncond)
                ace.SetNodeRef(iTerm, self._NodeBuffer) # for active circuit
#            Parser.Token = NextBus

        return


    def _AddABus(self):
        """Allocates more memory if necessary."""
        if self.NumBuses > self.MaxBuses:
            self.MaxBuses += self.IncBuses


    def _AddANodeBus(self):
        if self.NumNodes > self.MaxNodes:
            self.MaxNodes += self.IncNodes


    def _AddBus(self, BusName="", NNodes=0):
        NodeRef, i = 0

        # Trap error in bus name
        if not BusName:
            # Error in busname
            logger.error('Circuit.AddBus: BusName for Object "' +
                         self.ActiveCktElement.Name + '" is null.' +
                         'Error in definition of object.')
            for i in range(self.ActiveCktElement.NConds):
                self._NodeBuffer[i] = 0

            sys.exit()
            return 0

        Result = self.BusList.Find(BusName)
        if Result == 0:
            self.NumBuses += 1
            self._AddABus() # Allocates more memory if necessary
            self.Buses[self.NumBuses] = Bus()

        # Define nodes belonging to the bus}
        # Replace Nodebuffer values with global reference number
        Bus = self.Buses[Result]
        for i in range(NNodes):
            NodeRef = Bus.add(self._NodeBuffer[i])
            if NodeRef == self.NumNodes:
                # This was a new node so Add a NodeToBus element ????
                self.AddANodeBus() # Allocates more memory if necessary
                self.MapNodeToBus[self.NumNodes].BusRef = Result
                self.MapNodeToBus[self.NumNodes].NodeNum = self._NodeBuffer[i]
            # Swap out in preparation to setnoderef call
            self._NodeBuffer[i] = NodeRef

        return Result


    def _AddDeviceHandle(self, Handle=0):
        if self.NumDevices > self.MaxDevices:
            self.MaxDevices = self.MaxDevices + self.IncDevices

        # Index into CktElements
        self.DeviceRef[self.NumDevices].devHandle = Handle
        self.DeviceRef[self.NumDevices].CktElementClass = LastClassReferenced


    def SetElementActive(self, FullObjectName=""):
        """Fast way to set a cktelement active."""
        DevType = ""
        DevName = ""

        Result = 0

        ParseObjectClassandName(FullObjectName, DevType, DevName)
        DevClassIndex = ClassNames.index(DevType)
        if DevClassIndex == 0:
            DevClassIndex = LastClassReferenced
        DevIndex = self.DeviceList.index(DevName)
        while DevIndex > 0:
            if self.DeviceRef[DevIndex].CktElementClass == DevClassIndex:
                # we got a match
                ActiveDSSClass = DSSClassList[DevClassIndex]
                LastClassReferenced = DevClassIndex
                Result = self.DeviceRef[DevIndex].devHandle
#                ActiveDSSClass.Active := Result;
#                ActiveCktElement := ActiveDSSClass.GetActiveObj;
                self.ActiveCktElement = self.CktElements[Result]
                break
            DevIndex = self.DeviceList.next()   # Could be duplicates

        CmdResult = Result

        return Result



    def AddCktElement(self, Handle=0):
        """Adds last DSS object created to circuit.
        """
        pass

    def TotalizeMeters(self):
        pass
    def ComputeCapacity(self):
        return False

    def Save(self, Dir=""):
        return False
    def reProcessBusDefs(self):
        pass
    def DoResetMeterZones(self):
        pass
    def InvalidateAllPCElements(self):
        pass

    def DebugDump(self, F=file):
        pass


    def GetTopology(self):
        """Access to topology from the first source.
        """
        return None
    def FreeTopology(self):
        pass
    def GetBusAdjacentPDLists(self):
        return array([])
    def GetBusAdjacentPCLists(self):
        return array([])

    def Get_Name(self):
        return ""
    Name = property(Get_Name)

    def Get_CaseName(self):
        return ""
    def Set_CaseName(self, value):
        self._CaseName = value
    CaseName = property(Get_CaseName, Set_CaseName)

    def Get_ActiveCktElement(self):
        return self._ActiveCktElement
    def Set_ActiveCktElement(self, value):
        self._ActiveCktElement = value
    ActiveCktElement = property(Get_ActiveCktElement, Set_ActiveCktElement)

    def Get_Losses(self):
        """Total Circuit PD Element losses.
        """
        return complex(0.0, 0.0)
    Losses = property(Get_Losses)

    def Get_BusNameRedefined(self):
        return self._BusNameRedefined
    def Set_BusNameRedefined(self, value):
        self._BusNameRedefined = value
    BusNameRedefined = property(Get_BusNameRedefined, Set_BusNameRedefined)

    def Get_LoadMultiplier(self):
        return self._LoadMultiplier
    def Set_LoadMultiplier(self, value):
        self._LoadMultiplier = value
    LoadMultiplier = property(Get_LoadMultiplier, Set_LoadMultiplier)


    def _Set_ActiveCktElement(self, Value=None):
        pass
    def _Set_BusNameRedefined(self, Value=False):
        pass
    def _Get_Losses(self):
        """Total Circuit losses
        """
        return complex
    def _Set_LoadMultiplier(self, Value=0.0):
        pass
    def _SaveBusInfo(self):
        pass
    def _RestoreBusInfo(self):
        pass
    def _SaveMasterFile(self):
        return False
    def _SaveDSSObjects(self):
        return False
    def _SaveFeeders(self):
        return False
    def _SaveBusCoords(self):
        pass
    def _ReallocDeviceList(self):
        pass
    def _Set_CaseName(self, Value=""):
        pass
    def _Get_Name(self):
        return ""


#    def add(self, element, uid=None):
#        """ Adds an elements to the circuit model.
#        """
##        if uid is None:
##            if element.m_rid == "":
##                uid = uuid.uuid4().hex
##            else:
##                uid = element.mrid
##
##        self.elements[uid] = element
#        element.model = self
#
#
#    def remove(self, uid):
#        """Removes the element corresponding the the specified UID.
#        """
#        del self.elements[uid]
#
#
#    def topologicalAnalysis(self):
#        """Returns a list of TopologicalNode objects that contain connectivity
#        nodes that, in the current state, connect all non-primary elements
#        between primary elements (Transformers, ACLineSegments etc.).
#        """
##        nodes = [e for e in self.elements.values() \
##                 if isinstance(e, ConnectivityNode)]
##
##        tn = TopologicalNode()
##
##        for element in delivery:
##            t1 = element.terminals[0]
##            t2 = element.terminals[1]
##
##            cn1 = t1.connectivity_node
##
##            if cn1 in nodes:
##                for terminal in cn1.terminals:
##                    c_eq = terminal.conducting_equipment
##
##                    if type(c_eu) in primary_types:
##                        nodes.remove(cn1)
##                        tn = TopologicalNode() # Start a new node.
##                    else:
##                        tn.connectivity_nodes.append(cn1)
##                        nodes.remove(cn1)
##
##            cn2 = t2.connectivity_node
##
##        for element in conversion:
##            pass
#
#
#    def loadElements(self, file_name):
#        """Loads model elements from a CIM RDF/XML file and replaces the
#        existing elements.
#        """
##        reader = CIMReader()
##        uri_element_map = reader(file_name)
##        self.elements = uri_element_map.values()
#
#
#    def addElements(self, file_name):
#        """Loads model elements from a CIM RDF/XML file and adds them to the
#        existing list of elements.
#        """
##        element_map = read_cim(file_name)
##        self.elements.update(element_map)
