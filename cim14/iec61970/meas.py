# Copyright (C) 2009 Richard W. Lincoln
# All rights reserved.

from cim14.iec61970.core import IdentifiedObject
from cim14 import Element
from cim14.iec61970.core import Equipment

# <<< imports
# @generated
# >>> imports

ns_prefix = "cim.IEC61970.Meas"

ns_uri = "http://iec.ch/TC57/2009/CIM-schema-cim14#IEC61970.Meas"

class ValueAliasSet(IdentifiedObject):
    """ Describes the translation of a set of values into a name and is intendend to facilitate cusom translations. Each ValueAliasSet has a name, description etc. A specific Measurement may represent a discrete state like Open, Closed, Intermediate etc. This requires a translation from the MeasurementValue.value number to a string, e.g. 0->'Invalid', 1->'Open', 2->'Closed', 3->'Intermediate'. Each ValueToAlias member in ValueAliasSet.Value describe a mapping for one particular value to a name.
    """
    # The ValueAliasSet used for translation of a Control value to a name.
    commands = []

    # The Measurements using the set for translation
    discretes = []

    # The ValueToAlias mappings included in the set
    values = []

    # <<< value_alias_set
    # @generated
    def __init__(self, commands=[], discretes=[], values=[], **kw_args):
        """ Initialises a new 'ValueAliasSet' instance.
        """
        self.commands = commands
        self.discretes = discretes
        self.values = values

        super(ValueAliasSet, self).__init__(**kw_args)
    # >>> value_alias_set


class LimitSet(IdentifiedObject):
    """ Specifies a set of Limits that are associated with a Measurement. A Measurement may have several LimitSets corresponding to seasonal or other changing conditions. The condition is captured in the name and description attributes. The same LimitSet may be used for several Measurements. In particular percentage limits are used this way.
    """
    # Tells if the limit values are in percentage of normalValue or the specified Unit for Measurements and Controls. 
    is_percentage_limits = False

    # <<< limit_set
    # @generated
    def __init__(self, is_percentage_limits=False, **kw_args):
        """ Initialises a new 'LimitSet' instance.
        """
        self.is_percentage_limits = is_percentage_limits

        super(LimitSet, self).__init__(**kw_args)
    # >>> limit_set


class Measurement(IdentifiedObject):
    """ A Measurement represents any measured, calculated or non-measured non-calculated quantity. Any piece of equipment may contain Measurements, e.g. a substation may have temperature measurements and door open indications, a transformer may have oil temperature and tank pressure measurements, a bay may contain a number of power flow measurements and a Breaker may contain a switch status measurement.  The PSR - Measurement association is intended to capture this use of Measurement and is included in the naming hierarchy based on EquipmentContainer. The naming hierarchy typically has Measurements as leafs, e.g. Substation-VoltageLevel-Bay-Switch-Measurement. Some Measurements represent quantities related to a particular sensor location in the network, e.g. a voltage transformer (PT) at a busbar or a current transformer (CT) at the bar between a breaker and an isolator. The sensing position is not captured in the PSR - Measurement association. Instead it is captured by the Measurement - Terminal association that is used to define the sensing location in the network topology. The location is defined by the connection of the Terminal to ConductingEquipment.  Two possible paths exist: 1) Measurement-Terminal- ConnectivityNode-Terminal-ConductingEquipment 2) Measurement-Terminal-ConductingEquipment Alternative 2 is the only allowed use.  When the sensor location is needed both Measurement-PSR and Measurement-Terminal are used. The Measurement-Terminal association is never used alone.
    """
    # Specifies the type of Measurement, e.g. IndoorTemperature, OutDoorTemperature, BusVoltage, GeneratorVoltage, LineFlow etc. 
    measurement_type = ''

    asset = None

    # The Unit for the Measurement
    unit = None

    # Measurements are specified in types of documents, such as procedures.
    documents = []

    # The PowerSystemResource that contains the Measurement in the naming hierarchy
    power_system_resource = None

    # One or more measurements may be associated with a terminal in the network
    terminal = None

    locations = []

    # A measurement is made on the A side of a tie point
    for_tie_point = None

    # A measurement is a data source for dynamic interchange schedules
    dynamic_schedules = []

    pnode = None

    # A measurement is made on the B side of a tie point
    by_tie_point = None

    violation_limits = []

    change_items = []

    # <<< measurement
    # @generated
    def __init__(self, measurement_type='', asset=None, unit=None, documents=[], power_system_resource=None, terminal=None, locations=[], for_tie_point=None, dynamic_schedules=[], pnode=None, by_tie_point=None, violation_limits=[], change_items=[], **kw_args):
        """ Initialises a new 'Measurement' instance.
        """
        self.measurement_type = measurement_type
        self.asset = asset
        self.unit = unit
        self.documents = documents
        self.power_system_resource = power_system_resource
        self.terminal = terminal
        self.locations = locations
        self.for_tie_point = for_tie_point
        self.dynamic_schedules = dynamic_schedules
        self.pnode = pnode
        self.by_tie_point = by_tie_point
        self.violation_limits = violation_limits
        self.change_items = change_items

        super(Measurement, self).__init__(**kw_args)
    # >>> measurement


class Quality61850(Element):
    """ Quality flags in this class are as defined in IEC 61850, except for estimatorReplaced, which has been included in this class for convenience.
    """
    # A correlation function has detected that the value is not consitent with other values. Typically set by a network State Estimator. 
    suspect = False

    # Measurement value is old and possibly invalid, as it has not been successfully updated during a specified time interval. 
    old_data = False

    # Measurement value is transmitted for test purposes. 
    test = False

    # Measurement value is beyond a predefined range of value. 
    out_of_range = False

    # Measurement value is blocked and hence unavailable for transmission. 
    operator_blocked = False

    # Validity of the measurement value. Values are: "good", "questionable", "invalid"
    validity = 'good'

    # Measurement value may be incorrect due to a reference being out of calibration. 
    bad_reference = False

    # Value has been replaced by State Estimator. estimatorReplaced is not an IEC61850 quality bit but has been put in this class for convenience. 
    estimator_replaced = False

    # Source gives information related to the origin of a value. The value may be acquired from the process, defaulted or substituted. Values are: "substituted", "process", "defaulted"
    source = 'substituted'

    # To prevent some overload of the communication it is sensible to detect and suppress oscillating (fast changing) binary inputs. If a signal changes in a defined time (tosc) twice in the same direction (from 0 to 1 or from 1 to 0) then oscillation is detected and the detail quality identifier 'oscillatory' is set. If it is detected a configured numbers of transient changes could be passed by. In this time the validity status 'questionable' is set. If after this defined numbers of changes the signal is still in the oscillating state the value shall be set either to the opposite state of the previous stable value or to a defined default value. In this case the validity status 'questionable' is reset and 'invalid' is set as long as the signal is oscillating. If it is configured such that no transient changes should be passed by then the validity status 'invalid' is set immediately in addition to the detail quality identifier 'oscillatory' (used for status information only). 
    oscillatory = False

    # Measurement value is beyond the capability of being  represented properly. For example, a counter value overflows from maximum count back to a value of zero. 
    over_flow = False

    # This identifier indicates that a supervision function has detected an internal or external failure, e.g. communication failure. 
    failure = False

    # <<< quality61850
    # @generated
    def __init__(self, suspect=False, old_data=False, test=False, out_of_range=False, operator_blocked=False, validity='good', bad_reference=False, estimator_replaced=False, source='substituted', oscillatory=False, over_flow=False, failure=False, **kw_args):
        """ Initialises a new 'Quality61850' instance.
        """
        self.suspect = suspect
        self.old_data = old_data
        self.test = test
        self.out_of_range = out_of_range
        self.operator_blocked = operator_blocked
        self.validity = validity
        self.bad_reference = bad_reference
        self.estimator_replaced = estimator_replaced
        self.source = source
        self.oscillatory = oscillatory
        self.over_flow = over_flow
        self.failure = failure

        super(Quality61850, self).__init__(**kw_args)
    # >>> quality61850


class CurrentTransformer(Equipment):
    """ Instrument transformer used to measure electrical qualities of the circuit that is being protected and/or monitored. Typically used as current transducer for the purpose of metering or protection. A typical secondary current rating would be 5A.
    """
    # CT classification; i.e. class 10P. 
    ct_class = ''

    # Percent of rated current for which the CT remains accurate within specified limits. 
    accuracy_limit = ''

    # For multi-ratio CT's, the maximum permissable ratio attainable. 
    max_ratio = 0.0

    # Number of cores. 
    core_count = 0

    # Intended usage of the CT; i.e. metering, protection. 
    usage = ''

    # CT accuracy classification. 
    accuracy_class = ''

    current_transformer_type_asset = None

    current_transformer_asset = None

    # <<< current_transformer
    # @generated
    def __init__(self, ct_class='', accuracy_limit='', max_ratio=0.0, core_count=0, usage='', accuracy_class='', current_transformer_type_asset=None, current_transformer_asset=None, **kw_args):
        """ Initialises a new 'CurrentTransformer' instance.
        """
        self.ct_class = ct_class
        self.accuracy_limit = accuracy_limit
        self.max_ratio = max_ratio
        self.core_count = core_count
        self.usage = usage
        self.accuracy_class = accuracy_class
        self.current_transformer_type_asset = current_transformer_type_asset
        self.current_transformer_asset = current_transformer_asset

        super(CurrentTransformer, self).__init__(**kw_args)
    # >>> current_transformer


class Control(IdentifiedObject):
    """ Control is used for supervisory/device control. It represents control outputs that are used to change the state in a process, e.g. close or open breaker, a set point value or a raise lower command.
    """
    # Indicates that a client is currently sending control commands that has not completed 
    operation_in_progress = False

    # The last time a control output was sent 
    time_stamp = ''

    # The type of Control
    control_type = None

    # The remote point controlling the physical actuator.
    remote_control = None

    # The Unit for the Control.
    unit = None

    # Regulating device governed by this control output.
    regulating_cond_eq = None

    # <<< control
    # @generated
    def __init__(self, operation_in_progress=False, time_stamp='', control_type=None, remote_control=None, unit=None, regulating_cond_eq=None, **kw_args):
        """ Initialises a new 'Control' instance.
        """
        self.operation_in_progress = operation_in_progress
        self.time_stamp = time_stamp
        self.control_type = control_type
        self.remote_control = remote_control
        self.unit = unit
        self.regulating_cond_eq = regulating_cond_eq

        super(Control, self).__init__(**kw_args)
    # >>> control


class ControlType(IdentifiedObject):
    """ Specifies the type of Control, e.g. BreakerOn/Off, GeneratorVoltageSetPoint, TieLineFlow etc. The ControlType.name shall be unique among all specified types and describe the type. The ControlType.aliasName is meant to be used for localization.
    """
    # The Controls having the ControlType
    controls = []

    # <<< control_type
    # @generated
    def __init__(self, controls=[], **kw_args):
        """ Initialises a new 'ControlType' instance.
        """
        self.controls = controls

        super(ControlType, self).__init__(**kw_args)
    # >>> control_type


class MeasurementValue(IdentifiedObject):
    """ The current state for a measurement. A state value is an instance of a measurement from a specific source. Measurements can be associated with many state values, each representing a different source for the measurement.
    """
    # The time when the value was last updated 
    time_stamp = ''

    # The limit, expressed as a percentage of the sensor maximum, that errors will not exceed when the sensor is used under  reference conditions. 
    sensor_accuracy = ''

    gml_values = []

    # A reference to the type of source that updates the MeasurementValue, e.g. SCADA, CCLink, manual, etc. User conventions for the names of sources are contained in the introduction to IEC 61970-301.
    measurement_value_source = None

    erp_person = None

    # A MeasurementValue has a MeasurementValueQuality associated with it.
    measurement_value_quality = None

    procedure_data_sets = []

    # Link to the physical telemetered point associated with this measurement.
    remote_source = None

    # <<< measurement_value
    # @generated
    def __init__(self, time_stamp='', sensor_accuracy='', gml_values=[], measurement_value_source=None, erp_person=None, measurement_value_quality=None, procedure_data_sets=[], remote_source=None, **kw_args):
        """ Initialises a new 'MeasurementValue' instance.
        """
        self.time_stamp = time_stamp
        self.sensor_accuracy = sensor_accuracy
        self.gml_values = gml_values
        self.measurement_value_source = measurement_value_source
        self.erp_person = erp_person
        self.measurement_value_quality = measurement_value_quality
        self.procedure_data_sets = procedure_data_sets
        self.remote_source = remote_source

        super(MeasurementValue, self).__init__(**kw_args)
    # >>> measurement_value


class ValueToAlias(IdentifiedObject):
    """ Describes the translation of one particular value into a name, e.g. 1->'Open'
    """
    # The value that is mapped 
    value = 0

    # The ValueAliasSet having the ValueToAlias mappings
    value_alias_set = None

    # <<< value_to_alias
    # @generated
    def __init__(self, value=0, value_alias_set=None, **kw_args):
        """ Initialises a new 'ValueToAlias' instance.
        """
        self.value = value
        self.value_alias_set = value_alias_set

        super(ValueToAlias, self).__init__(**kw_args)
    # >>> value_to_alias


class MeasurementValueSource(IdentifiedObject):
    """ MeasurementValueSource describes the alternative sources updating a MeasurementValue. User conventions for how to use the MeasurementValueSource attributes are described in the introduction to IEC 61970-301.
    """
    # The MeasurementValues updated by the source
    measurement_values = []

    # <<< measurement_value_source
    # @generated
    def __init__(self, measurement_values=[], **kw_args):
        """ Initialises a new 'MeasurementValueSource' instance.
        """
        self.measurement_values = measurement_values

        super(MeasurementValueSource, self).__init__(**kw_args)
    # >>> measurement_value_source


class PotentialTransformer(Equipment):
    """ Instrument transformer (also known as Voltage Transformer) used to measure electrical qualities of the circuit that is being protected and/or monitored. Typically used as voltage transducer for the purpose of metering, protection, or sometimes auxiliary substation supply. A typical secondary voltage rating would be 120V.
    """
    # Nominal ratio between the primary and secondary voltage. 
    nominal_ratio = 0.0

    # PT classification. 
    pt_class = ''

    # PT accuracy classification. 
    accuracy_class = ''

    potential_transformer_asset = None

    potential_transformer_type_asset = None

    # <<< potential_transformer
    # @generated
    def __init__(self, nominal_ratio=0.0, pt_class='', accuracy_class='', potential_transformer_asset=None, potential_transformer_type_asset=None, **kw_args):
        """ Initialises a new 'PotentialTransformer' instance.
        """
        self.nominal_ratio = nominal_ratio
        self.pt_class = pt_class
        self.accuracy_class = accuracy_class
        self.potential_transformer_asset = potential_transformer_asset
        self.potential_transformer_type_asset = potential_transformer_type_asset

        super(PotentialTransformer, self).__init__(**kw_args)
    # >>> potential_transformer


class Limit(IdentifiedObject):
    """ Specifies one limit value for a Measurement. A Measurement typically has several limits that are kept together by the LimitSet class. The actual meaning and use of a Limit instance (i.e., if it is an alarm or warning limit or if it is a high or low limit) is not captured in the Limit class. However the name of a Limit instance may indicate both meaning and use.
    """
    procedures = []

    # <<< limit
    # @generated
    def __init__(self, procedures=[], **kw_args):
        """ Initialises a new 'Limit' instance.
        """
        self.procedures = procedures

        super(Limit, self).__init__(**kw_args)
    # >>> limit


class AccumulatorLimit(Limit):
    """ Limit values for Accumulator measurements
    """
    # The value to supervise against. The value is positive. 
    value = 0

    # The set of limits.
    limit_set = None

    # <<< accumulator_limit
    # @generated
    def __init__(self, value=0, limit_set=None, **kw_args):
        """ Initialises a new 'AccumulatorLimit' instance.
        """
        self.value = value
        self.limit_set = limit_set

        super(AccumulatorLimit, self).__init__(**kw_args)
    # >>> accumulator_limit


class AccumulatorLimitSet(LimitSet):
    """ An AccumulatorLimitSet specifies a set of Limits that are associated with an Accumulator measurement.
    """
    # The Measurements using the LimitSet.
    measurements = []

    # The limit values used for supervision of Measurements.
    limits = []

    # <<< accumulator_limit_set
    # @generated
    def __init__(self, measurements=[], limits=[], **kw_args):
        """ Initialises a new 'AccumulatorLimitSet' instance.
        """
        self.measurements = measurements
        self.limits = limits

        super(AccumulatorLimitSet, self).__init__(**kw_args)
    # >>> accumulator_limit_set


class SetPoint(Control):
    """ A SetPoint is an analog control used for supervisory control.
    """
    # Normal value for Control.value e.g. used for percentage scaling 
    normal_value = 0.0

    # Normal value range minimum for any of the Control.value. Used for scaling, e.g. in bar graphs. 
    min_value = 0.0

    # Normal value range maximum for any of the Control.value. Used for scaling, e.g. in bar graphs. 
    max_value = 0.0

    # The value representing the actuator output 
    value = 0.0

    # The Measurement variable used for control
    analog = None

    # <<< set_point
    # @generated
    def __init__(self, normal_value=0.0, min_value=0.0, max_value=0.0, value=0.0, analog=None, **kw_args):
        """ Initialises a new 'SetPoint' instance.
        """
        self.normal_value = normal_value
        self.min_value = min_value
        self.max_value = max_value
        self.value = value
        self.analog = analog

        super(SetPoint, self).__init__(**kw_args)
    # >>> set_point


class AccumulatorValue(MeasurementValue):
    """ AccumulatorValue represents a accumulated (counted) MeasurementValue.
    """
    # The value to supervise. The value is positive. 
    value = 0

    # Measurement to which this value is connected.
    accumulator = None

    # <<< accumulator_value
    # @generated
    def __init__(self, value=0, accumulator=None, **kw_args):
        """ Initialises a new 'AccumulatorValue' instance.
        """
        self.value = value
        self.accumulator = accumulator

        super(AccumulatorValue, self).__init__(**kw_args)
    # >>> accumulator_value


class AnalogValue(MeasurementValue):
    """ AnalogValue represents an analog MeasurementValue.
    """
    # The value to supervise. 
    value = 0.0

    # The alternate generating unit for which this measurement value applies.
    alt_generating_unit = []

    # Measurement to which this value is connected.
    analog = None

    # The usage of the measurement within the control area specification.
    alt_tie_meas = []

    # <<< analog_value
    # @generated
    def __init__(self, value=0.0, alt_generating_unit=[], analog=None, alt_tie_meas=[], **kw_args):
        """ Initialises a new 'AnalogValue' instance.
        """
        self.value = value
        self.alt_generating_unit = alt_generating_unit
        self.analog = analog
        self.alt_tie_meas = alt_tie_meas

        super(AnalogValue, self).__init__(**kw_args)
    # >>> analog_value


class DiscreteValue(MeasurementValue):
    """ DiscreteValue represents a discrete MeasurementValue.
    """
    # The value to supervise. 
    value = 0

    # Measurement to which this value is connected.
    discrete = None

    # <<< discrete_value
    # @generated
    def __init__(self, value=0, discrete=None, **kw_args):
        """ Initialises a new 'DiscreteValue' instance.
        """
        self.value = value
        self.discrete = discrete

        super(DiscreteValue, self).__init__(**kw_args)
    # >>> discrete_value


class Accumulator(Measurement):
    """ Accumulator represents a accumulated (counted) Measurement, e.g. an energy value.
    """
    # Normal value range maximum for any of the MeasurementValue.values. Used for scaling, e.g. in bar graphs or of telemetered raw values. 
    max_value = 0

    # The values connected to this measurement.
    accumulator_values = []

    # A measurement may have zero or more limit ranges defined for it.
    limit_sets = []

    # <<< accumulator
    # @generated
    def __init__(self, max_value=0, accumulator_values=[], limit_sets=[], **kw_args):
        """ Initialises a new 'Accumulator' instance.
        """
        self.max_value = max_value
        self.accumulator_values = accumulator_values
        self.limit_sets = limit_sets

        super(Accumulator, self).__init__(**kw_args)
    # >>> accumulator


class AnalogLimitSet(LimitSet):
    """ An AnalogLimitSet specifies a set of Limits that are associated with an Analog measurement.
    """
    # The Measurements using the LimitSet.
    measurements = []

    # The limit values used for supervision of Measurements.
    limits = []

    # <<< analog_limit_set
    # @generated
    def __init__(self, measurements=[], limits=[], **kw_args):
        """ Initialises a new 'AnalogLimitSet' instance.
        """
        self.measurements = measurements
        self.limits = limits

        super(AnalogLimitSet, self).__init__(**kw_args)
    # >>> analog_limit_set


class MeasurementValueQuality(Quality61850):
    """ Measurement quality flags. Bits 0-10 are defined for substation automation in draft IEC 61850 part 7-3. Bits 11-15 are reserved for future expansion by that document. Bits 16-31 are reserved for EMS applications.
    """
    # A MeasurementValue has a MeasurementValueQuality associated with it.
    measurement_value = None

    # <<< measurement_value_quality
    # @generated
    def __init__(self, measurement_value=None, **kw_args):
        """ Initialises a new 'MeasurementValueQuality' instance.
        """
        self.measurement_value = measurement_value

        super(MeasurementValueQuality, self).__init__(**kw_args)
    # >>> measurement_value_quality


class StringMeasurementValue(MeasurementValue):
    """ StringMeasurementValue represents a measurement value of type string.
    """
    # The value to supervise. 
    value = ''

    # Measurement to which this value is connected.
    string_measurement = None

    # <<< string_measurement_value
    # @generated
    def __init__(self, value='', string_measurement=None, **kw_args):
        """ Initialises a new 'StringMeasurementValue' instance.
        """
        self.value = value
        self.string_measurement = string_measurement

        super(StringMeasurementValue, self).__init__(**kw_args)
    # >>> string_measurement_value


class StringMeasurement(Measurement):
    """ StringMeasurement represents a measurement with values of type string.
    """
    # The values connected to this measurement.
    string_measurement_values = []

    # <<< string_measurement
    # @generated
    def __init__(self, string_measurement_values=[], **kw_args):
        """ Initialises a new 'StringMeasurement' instance.
        """
        self.string_measurement_values = string_measurement_values

        super(StringMeasurement, self).__init__(**kw_args)
    # >>> string_measurement


class Command(Control):
    """ A Command is a discrete control used for supervisory control.
    """
    # The value representing the actuator output 
    value = 0

    # Normal value for Control.value e.g. used for percentage scaling 
    normal_value = 0

    # The Commands using the set for translation.
    value_alias_set = None

    # The Measurement variable used for control.
    discrete = None

    # <<< command
    # @generated
    def __init__(self, value=0, normal_value=0, value_alias_set=None, discrete=None, **kw_args):
        """ Initialises a new 'Command' instance.
        """
        self.value = value
        self.normal_value = normal_value
        self.value_alias_set = value_alias_set
        self.discrete = discrete

        super(Command, self).__init__(**kw_args)
    # >>> command


class AnalogLimit(Limit):
    """ Limit values for Analog measurements
    """
    # The value to supervise against. 
    value = 0.0

    # The set of limits.
    limit_set = None

    # <<< analog_limit
    # @generated
    def __init__(self, value=0.0, limit_set=None, **kw_args):
        """ Initialises a new 'AnalogLimit' instance.
        """
        self.value = value
        self.limit_set = limit_set

        super(AnalogLimit, self).__init__(**kw_args)
    # >>> analog_limit


class Discrete(Measurement):
    """ Discrete represents a discrete Measurement, i.e. a Measurement reprsenting discrete values, e.g. a Breaker position.
    """
    # Normal measurement value, e.g., used for percentage calculations. 
    normal_value = 0

    # Normal value range maximum for any of the MeasurementValue.values. Used for scaling, e.g. in bar graphs or of telemetered raw values. 
    max_value = 0

    # Normal value range minimum for any of the MeasurementValue.values. Used for scaling, e.g. in bar graphs or of telemetered raw values 
    min_value = 0

    # The Control variable associated with the Measurement.
    command = None

    # The values connected to this measurement.
    discrete_values = []

    # The ValueAliasSet used for translation of a MeasurementValue.value to a name
    value_alias_set = None

    # <<< discrete
    # @generated
    def __init__(self, normal_value=0, max_value=0, min_value=0, command=None, discrete_values=[], value_alias_set=None, **kw_args):
        """ Initialises a new 'Discrete' instance.
        """
        self.normal_value = normal_value
        self.max_value = max_value
        self.min_value = min_value
        self.command = command
        self.discrete_values = discrete_values
        self.value_alias_set = value_alias_set

        super(Discrete, self).__init__(**kw_args)
    # >>> discrete


class Analog(Measurement):
    """ Analog represents an analog Measurement.
    """
    # Normal value range minimum for any of the MeasurementValue.values. Used for scaling, e.g. in bar graphs or of telemetered raw values 
    min_value = 0.0

    # If true then this measurement is an active power, reactive power or current with the convention that a positive value measured at the Terminal means power is flowing into the related PowerSystemResource. 
    positive_flow_in = False

    # Normal measurement value, e.g., used for percentage calculations. 
    normal_value = 0.0

    # Normal value range maximum for any of the MeasurementValue.values. Used for scaling, e.g. in bar graphs or of telemetered raw values. 
    max_value = 0.0

    # A measurement may have zero or more limit ranges defined for it.
    limit_sets = []

    # The values connected to this measurement.
    analog_values = []

    # The Control variable associated with the Measurement
    set_point = None

    # <<< analog
    # @generated
    def __init__(self, min_value=0.0, positive_flow_in=False, normal_value=0.0, max_value=0.0, limit_sets=[], analog_values=[], set_point=None, **kw_args):
        """ Initialises a new 'Analog' instance.
        """
        self.min_value = min_value
        self.positive_flow_in = positive_flow_in
        self.normal_value = normal_value
        self.max_value = max_value
        self.limit_sets = limit_sets
        self.analog_values = analog_values
        self.set_point = set_point

        super(Analog, self).__init__(**kw_args)
    # >>> analog


# <<< meas
# @generated
# >>> meas
