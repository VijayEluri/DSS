# Copyright (C) 2009 Richard W. Lincoln
# All rights reserved.

from cim14.iec61968.common import Document
from cim14.iec61970.core import IdentifiedObject
from cim14.iec61968.assets import AssetContainer
from cim14.iec61968.informative.inf_common import Role
from cim14.iec61968.assets import Asset
from cim14.iec61968.assets import ElectricalInfo
from cim14.iec61970.core import Curve
from cim14.iec61968.common import ActivityRecord
from cim14 import Element

# <<< imports
# @generated
# >>> imports

ns_prefix = "cim.IEC61968.Informative.InfAssets"

ns_uri = "http://iec.ch/TC57/2009/CIM-schema-cim14#IEC61968.Informative.InfAssets"

class ProcedureDataSet(Document):
    """ A data set recorded each time a procedure is executed. Observed results are captured in associated measurement values and/or values for properties relevant to the type of procedure performed.
    """
    # <<< procedure_data_set
    # @generated
    def __init__(self, measurement_values=[], transformer_observations=[], procedure=None, properties=[], **kw_args):
        """ Initialises a new 'ProcedureDataSet' instance.
        """
        
        self._measurement_values = []
        self.measurement_values = measurement_values
        self._transformer_observations = []
        self.transformer_observations = transformer_observations
        self._procedure = None
        self.procedure = procedure
        self._properties = []
        self.properties = properties

        super(ProcedureDataSet, self).__init__(**kw_args)
    # >>> procedure_data_set
        
    # <<< measurement_values
    # @generated
    def get_measurement_values(self):
        """ 
        """
        return self._measurement_values

    def set_measurement_values(self, value):
        for p in self._measurement_values:
            filtered = [q for q in p.procedure_data_sets if q != self]
            self._measurement_values._procedure_data_sets = filtered
        for r in value:
            if self not in r._procedure_data_sets:
                r._procedure_data_sets.append(self)
        self._measurement_values = value
            
    measurement_values = property(get_measurement_values, set_measurement_values)
    
    def add_measurement_values(self, *measurement_values):
        for obj in measurement_values:
            if self not in obj._procedure_data_sets:
                obj._procedure_data_sets.append(self)
            self._measurement_values.append(obj)
        
    def remove_measurement_values(self, *measurement_values):
        for obj in measurement_values:
            if self in obj._procedure_data_sets:
                obj._procedure_data_sets.remove(self)
            self._measurement_values.remove(obj)
    # >>> measurement_values

    # <<< transformer_observations
    # @generated
    def get_transformer_observations(self):
        """ 
        """
        return self._transformer_observations

    def set_transformer_observations(self, value):
        for p in self._transformer_observations:
            filtered = [q for q in p.procedure_data_sets if q != self]
            self._transformer_observations._procedure_data_sets = filtered
        for r in value:
            if self not in r._procedure_data_sets:
                r._procedure_data_sets.append(self)
        self._transformer_observations = value
            
    transformer_observations = property(get_transformer_observations, set_transformer_observations)
    
    def add_transformer_observations(self, *transformer_observations):
        for obj in transformer_observations:
            if self not in obj._procedure_data_sets:
                obj._procedure_data_sets.append(self)
            self._transformer_observations.append(obj)
        
    def remove_transformer_observations(self, *transformer_observations):
        for obj in transformer_observations:
            if self in obj._procedure_data_sets:
                obj._procedure_data_sets.remove(self)
            self._transformer_observations.remove(obj)
    # >>> transformer_observations

    # <<< procedure
    # @generated
    def get_procedure(self):
        """ 
        """
        return self._procedure

    def set_procedure(self, value):
        if self._procedure is not None:
            filtered = [x for x in self.procedure.procedure_data_sets if x != self]
            self._procedure._procedure_data_sets = filtered
            
        self._procedure = value
        if self._procedure is not None:
            self._procedure._procedure_data_sets.append(self)

    procedure = property(get_procedure, set_procedure)
    # >>> procedure

    # <<< properties
    # @generated
    def get_properties(self):
        """ UserAttributes used to specify further properties of this procedure data set. Use 'name' to specify what kind of property it is, and 'value.value' attribute for the actual value.
        """
        return self._properties

    def set_properties(self, value):
        for p in self._properties:
            filtered = [q for q in p.procedure_data_sets if q != self]
            self._properties._procedure_data_sets = filtered
        for r in value:
            if self not in r._procedure_data_sets:
                r._procedure_data_sets.append(self)
        self._properties = value
            
    properties = property(get_properties, set_properties)
    
    def add_properties(self, *properties):
        for obj in properties:
            if self not in obj._procedure_data_sets:
                obj._procedure_data_sets.append(self)
            self._properties.append(obj)
        
    def remove_properties(self, *properties):
        for obj in properties:
            if self in obj._procedure_data_sets:
                obj._procedure_data_sets.remove(self)
            self._properties.remove(obj)
    # >>> properties



class PowerRating(IdentifiedObject):
    """ There are often stages of power which are associated with stages of cooling. For instance, a transformer may be rated 121kV on the primary, 15kV on the secondary and 4kV on the tertiary winding. These are voltage ratings and the power ratings are generally the same for all three windings and independent of the voltage ratings, there are instances where the tertiary may have a lower power rating. For example, for three stages, the power rating may be 15/20/25 MVA and the cooling is OA/FA/FOA. The 15 MVA rating goes with the OA cooling (Oil and Air cooling). This is called the self cooled rating as there are no external cooling enhancements. The 20 MVA rating goes with the FA cooling (Forced Air cooling), this means that when the fans are running and thus enhancing the cooling characteristics, the transformer can operate at a power level of 20 MVA. The 25 MVA rating goes with the FOA cooling (Forced Oil and Air cooling), this means that when the fans and pumps are running and thus enhancing the cooling characteristics even more than before, the transformer can operate at a power level of 25 MVA. This 15/20/25 MVA does not state how the power is split between the various windings. It may be 25 MVA input on the primary, 25 MVA output on the secondary and 0 MVA output on the tertiary. It may also operate at 25 MVA input on the primary, 17 MVA output on the secondary and 8 MVA output on the tertiary.
    """
    # <<< power_rating
    # @generated
    def __init__(self, cooling_kind='self_cooling', stage=0, power_rating='', transformer_assets=[], **kw_args):
        """ Initialises a new 'PowerRating' instance.
        """
        # Kind of cooling system. Values are: "self_cooling", "forced_oil_and_air", "forced_air", "other"
        self.cooling_kind = 'self_cooling'
        # Stage of cooling and associated power rating. 
        self.stage = 0
        # The power rating associated with type of cooling specified for this stage. 
        self.power_rating = ''
        
        self._transformer_assets = []
        self.transformer_assets = transformer_assets

        super(PowerRating, self).__init__(**kw_args)
    # >>> power_rating
        
    # <<< transformer_assets
    # @generated
    def get_transformer_assets(self):
        """ 
        """
        return self._transformer_assets

    def set_transformer_assets(self, value):
        for p in self._transformer_assets:
            filtered = [q for q in p.power_ratings if q != self]
            self._transformer_assets._power_ratings = filtered
        for r in value:
            if self not in r._power_ratings:
                r._power_ratings.append(self)
        self._transformer_assets = value
            
    transformer_assets = property(get_transformer_assets, set_transformer_assets)
    
    def add_transformer_assets(self, *transformer_assets):
        for obj in transformer_assets:
            if self not in obj._power_ratings:
                obj._power_ratings.append(self)
            self._transformer_assets.append(obj)
        
    def remove_transformer_assets(self, *transformer_assets):
        for obj in transformer_assets:
            if self in obj._power_ratings:
                obj._power_ratings.remove(self)
            self._transformer_assets.remove(obj)
    # >>> transformer_assets



class Facility(AssetContainer):
    """ A facility may contain buildings, storage facilities, switching facilities, power generation, manufacturing facilities, maintenance facilities, etc.
    """
    # <<< facility
    # @generated
    def __init__(self, kind='', **kw_args):
        """ Initialises a new 'Facility' instance.
        """
        # Kind of this facility. 
        self.kind = ''
        

        super(Facility, self).__init__(**kw_args)
    # >>> facility
        


class Specification(Document):
    """ Specification can be used for various purposes relative to an asset, a logical device (PowerSystemResource), location, etc. Examples include documents supplied by manufacturers such as asset installation instructions, asset maintenance instructions, etc.
    """
    # <<< specification
    # @generated
    def __init__(self, asset_properites=[], mediums=[], qualification_requirements=[], asset_property_curves=[], dimensions_infos=[], ratings=[], reliability_infos=[], **kw_args):
        """ Initialises a new 'Specification' instance.
        """
        
        self._asset_properites = []
        self.asset_properites = asset_properites
        self._mediums = []
        self.mediums = mediums
        self._qualification_requirements = []
        self.qualification_requirements = qualification_requirements
        self._asset_property_curves = []
        self.asset_property_curves = asset_property_curves
        self._dimensions_infos = []
        self.dimensions_infos = dimensions_infos
        self._ratings = []
        self.ratings = ratings
        self._reliability_infos = []
        self.reliability_infos = reliability_infos

        super(Specification, self).__init__(**kw_args)
    # >>> specification
        
    # <<< asset_properites
    # @generated
    def get_asset_properites(self):
        """ UserAttributes used to specify further properties of the asset covered with this specification. Use 'name' to specify what kind of property it is, and 'value.value' attribute for the actual value.
        """
        return self._asset_properites

    def set_asset_properites(self, value):
        for x in self._asset_properites:
            x._property_specification = None
        for y in value:
            y._property_specification = self
        self._asset_properites = value
            
    asset_properites = property(get_asset_properites, set_asset_properites)
    
    def add_asset_properites(self, *asset_properites):
        for obj in asset_properites:
            obj._property_specification = self
            self._asset_properites.append(obj)
        
    def remove_asset_properites(self, *asset_properites):
        for obj in asset_properites:
            obj._property_specification = None
            self._asset_properites.remove(obj)
    # >>> asset_properites

    # <<< mediums
    # @generated
    def get_mediums(self):
        """ 
        """
        return self._mediums

    def set_mediums(self, value):
        for x in self._mediums:
            x._specification = None
        for y in value:
            y._specification = self
        self._mediums = value
            
    mediums = property(get_mediums, set_mediums)
    
    def add_mediums(self, *mediums):
        for obj in mediums:
            obj._specification = self
            self._mediums.append(obj)
        
    def remove_mediums(self, *mediums):
        for obj in mediums:
            obj._specification = None
            self._mediums.remove(obj)
    # >>> mediums

    # <<< qualification_requirements
    # @generated
    def get_qualification_requirements(self):
        """ 
        """
        return self._qualification_requirements

    def set_qualification_requirements(self, value):
        for p in self._qualification_requirements:
            filtered = [q for q in p.specifications if q != self]
            self._qualification_requirements._specifications = filtered
        for r in value:
            if self not in r._specifications:
                r._specifications.append(self)
        self._qualification_requirements = value
            
    qualification_requirements = property(get_qualification_requirements, set_qualification_requirements)
    
    def add_qualification_requirements(self, *qualification_requirements):
        for obj in qualification_requirements:
            if self not in obj._specifications:
                obj._specifications.append(self)
            self._qualification_requirements.append(obj)
        
    def remove_qualification_requirements(self, *qualification_requirements):
        for obj in qualification_requirements:
            if self in obj._specifications:
                obj._specifications.remove(self)
            self._qualification_requirements.remove(obj)
    # >>> qualification_requirements

    # <<< asset_property_curves
    # @generated
    def get_asset_property_curves(self):
        """ 
        """
        return self._asset_property_curves

    def set_asset_property_curves(self, value):
        for x in self._asset_property_curves:
            x._specification = None
        for y in value:
            y._specification = self
        self._asset_property_curves = value
            
    asset_property_curves = property(get_asset_property_curves, set_asset_property_curves)
    
    def add_asset_property_curves(self, *asset_property_curves):
        for obj in asset_property_curves:
            obj._specification = self
            self._asset_property_curves.append(obj)
        
    def remove_asset_property_curves(self, *asset_property_curves):
        for obj in asset_property_curves:
            obj._specification = None
            self._asset_property_curves.remove(obj)
    # >>> asset_property_curves

    # <<< dimensions_infos
    # @generated
    def get_dimensions_infos(self):
        """ 
        """
        return self._dimensions_infos

    def set_dimensions_infos(self, value):
        for p in self._dimensions_infos:
            filtered = [q for q in p.specifications if q != self]
            self._dimensions_infos._specifications = filtered
        for r in value:
            if self not in r._specifications:
                r._specifications.append(self)
        self._dimensions_infos = value
            
    dimensions_infos = property(get_dimensions_infos, set_dimensions_infos)
    
    def add_dimensions_infos(self, *dimensions_infos):
        for obj in dimensions_infos:
            if self not in obj._specifications:
                obj._specifications.append(self)
            self._dimensions_infos.append(obj)
        
    def remove_dimensions_infos(self, *dimensions_infos):
        for obj in dimensions_infos:
            if self in obj._specifications:
                obj._specifications.remove(self)
            self._dimensions_infos.remove(obj)
    # >>> dimensions_infos

    # <<< ratings
    # @generated
    def get_ratings(self):
        """ UserAttributes used to specify ratings of the asset covered by this specification. Ratings also can be used to set the initial value of operational measurement limits. Use 'name' to specify what kind of rating it is (e.g., voltage, current), and 'value' attribute for the actual value and unit information of the rating.
        """
        return self._ratings

    def set_ratings(self, value):
        for x in self._ratings:
            x._rating_specification = None
        for y in value:
            y._rating_specification = self
        self._ratings = value
            
    ratings = property(get_ratings, set_ratings)
    
    def add_ratings(self, *ratings):
        for obj in ratings:
            obj._rating_specification = self
            self._ratings.append(obj)
        
    def remove_ratings(self, *ratings):
        for obj in ratings:
            obj._rating_specification = None
            self._ratings.remove(obj)
    # >>> ratings

    # <<< reliability_infos
    # @generated
    def get_reliability_infos(self):
        """ 
        """
        return self._reliability_infos

    def set_reliability_infos(self, value):
        for x in self._reliability_infos:
            x._specification = None
        for y in value:
            y._specification = self
        self._reliability_infos = value
            
    reliability_infos = property(get_reliability_infos, set_reliability_infos)
    
    def add_reliability_infos(self, *reliability_infos):
        for obj in reliability_infos:
            obj._specification = self
            self._reliability_infos.append(obj)
        
    def remove_reliability_infos(self, *reliability_infos):
        for obj in reliability_infos:
            obj._specification = None
            self._reliability_infos.remove(obj)
    # >>> reliability_infos



class SubstationAsset(AssetContainer):
    """ A grouping of assets such as conductors, transformers, switchgear, etc. When located on the ground surface, it is usually surrounded by some kind of fence with a locked gate. It may also be located inside buildings, in underground vaults, and on structures. Use 'category' for utility-specific categorisation (such as Air Cooled, Gas Insultated, etc.).
    """
    # <<< substation_asset
    # @generated
    def __init__(self, function='distribution', substation=None, **kw_args):
        """ Initialises a new 'SubstationAsset' instance.
        """
        # Function of this substation asset. Values are: "distribution", "transmission", "sub_transmission", "industrial", "other", "generation"
        self.function = 'distribution'
        
        self._substation = None
        self.substation = substation

        super(SubstationAsset, self).__init__(**kw_args)
    # >>> substation_asset
        
    # <<< substation
    # @generated
    def get_substation(self):
        """ 
        """
        return self._substation

    def set_substation(self, value):
        if self._substation is not None:
            self._substation._substation_asset = None
            
        self._substation = value
        if self._substation is not None:
            self._substation._substation_asset = self
            
    substation = property(get_substation, set_substation)
    # >>> substation



class DocAssetRole(Role):
    """ Roles played between Documents and Assets.
    """
    # <<< doc_asset_role
    # @generated
    def __init__(self, asset=None, document=None, **kw_args):
        """ Initialises a new 'DocAssetRole' instance.
        """
        
        self._asset = None
        self.asset = asset
        self._document = None
        self.document = document

        super(DocAssetRole, self).__init__(**kw_args)
    # >>> doc_asset_role
        
    # <<< asset
    # @generated
    def get_asset(self):
        """ 
        """
        return self._asset

    def set_asset(self, value):
        if self._asset is not None:
            filtered = [x for x in self.asset.document_roles if x != self]
            self._asset._document_roles = filtered
            
        self._asset = value
        if self._asset is not None:
            self._asset._document_roles.append(self)

    asset = property(get_asset, set_asset)
    # >>> asset

    # <<< document
    # @generated
    def get_document(self):
        """ 
        """
        return self._document

    def set_document(self, value):
        if self._document is not None:
            filtered = [x for x in self.document.asset_roles if x != self]
            self._document._asset_roles = filtered
            
        self._document = value
        if self._document is not None:
            self._document._asset_roles.append(self)

    document = property(get_document, set_document)
    # >>> document



class Procedure(Document):
    """ A documented procedure for various types of Work or Work Tasks. One or more procedures guide a compatible unit, a standard way of performing a unit of work. The type of procedure is defined in Procedure.type. For example, when type=Inspection, this procedure coupled with Schedule and other information provides the key items of an inspection plan. Another type of Procedure is a Diagnosis. Note that each specific values and settings to be used in a procedure is intended to be described in an instance of ProcedureValue. A maintenance ticket, a type of Work, is generated whenever maintenance is determined to be needed as a result of an inspection or diagnosis.
    """
    # <<< procedure
    # @generated
    def __init__(self, instruction='', kind='test', corporate_code='', sequence_number='', compatible_units=[], limits=[], procedure_data_sets=[], procedure_values=[], **kw_args):
        """ Initialises a new 'Procedure' instance.
        """
        # The textual description of the procedure, which references instances of ProcedureValue as appropriate. 
        self.instruction = ''
        # Kind of this procedure. Values are: "test", "diagnosis", "inspection", "other", "maintenance"
        self.kind = 'test'
        # Code for this kind of procedure. 
        self.corporate_code = ''
        # Sequence number in a sequence of procedures being performed. 
        self.sequence_number = ''
        
        self._compatible_units = []
        self.compatible_units = compatible_units
        self._limits = []
        self.limits = limits
        self._procedure_data_sets = []
        self.procedure_data_sets = procedure_data_sets
        self._procedure_values = []
        self.procedure_values = procedure_values

        super(Procedure, self).__init__(**kw_args)
    # >>> procedure
        
    # <<< compatible_units
    # @generated
    def get_compatible_units(self):
        """ 
        """
        return self._compatible_units

    def set_compatible_units(self, value):
        for p in self._compatible_units:
            filtered = [q for q in p.procedures if q != self]
            self._compatible_units._procedures = filtered
        for r in value:
            if self not in r._procedures:
                r._procedures.append(self)
        self._compatible_units = value
            
    compatible_units = property(get_compatible_units, set_compatible_units)
    
    def add_compatible_units(self, *compatible_units):
        for obj in compatible_units:
            if self not in obj._procedures:
                obj._procedures.append(self)
            self._compatible_units.append(obj)
        
    def remove_compatible_units(self, *compatible_units):
        for obj in compatible_units:
            if self in obj._procedures:
                obj._procedures.remove(self)
            self._compatible_units.remove(obj)
    # >>> compatible_units

    # <<< limits
    # @generated
    def get_limits(self):
        """ 
        """
        return self._limits

    def set_limits(self, value):
        for p in self._limits:
            filtered = [q for q in p.procedures if q != self]
            self._limits._procedures = filtered
        for r in value:
            if self not in r._procedures:
                r._procedures.append(self)
        self._limits = value
            
    limits = property(get_limits, set_limits)
    
    def add_limits(self, *limits):
        for obj in limits:
            if self not in obj._procedures:
                obj._procedures.append(self)
            self._limits.append(obj)
        
    def remove_limits(self, *limits):
        for obj in limits:
            if self in obj._procedures:
                obj._procedures.remove(self)
            self._limits.remove(obj)
    # >>> limits

    # <<< procedure_data_sets
    # @generated
    def get_procedure_data_sets(self):
        """ 
        """
        return self._procedure_data_sets

    def set_procedure_data_sets(self, value):
        for x in self._procedure_data_sets:
            x._procedure = None
        for y in value:
            y._procedure = self
        self._procedure_data_sets = value
            
    procedure_data_sets = property(get_procedure_data_sets, set_procedure_data_sets)
    
    def add_procedure_data_sets(self, *procedure_data_sets):
        for obj in procedure_data_sets:
            obj._procedure = self
            self._procedure_data_sets.append(obj)
        
    def remove_procedure_data_sets(self, *procedure_data_sets):
        for obj in procedure_data_sets:
            obj._procedure = None
            self._procedure_data_sets.remove(obj)
    # >>> procedure_data_sets

    # <<< procedure_values
    # @generated
    def get_procedure_values(self):
        """ UserAttributes used to specify procedure values. An example is to have an instance for each of the following settings when conducting a test: voltage, current, frequency, temperature specified in 'name' attribute, and the corresponding value and units in 'value' attribute.
        """
        return self._procedure_values

    def set_procedure_values(self, value):
        for x in self._procedure_values:
            x._procedure = None
        for y in value:
            y._procedure = self
        self._procedure_values = value
            
    procedure_values = property(get_procedure_values, set_procedure_values)
    
    def add_procedure_values(self, *procedure_values):
        for obj in procedure_values:
            obj._procedure = self
            self._procedure_values.append(obj)
        
    def remove_procedure_values(self, *procedure_values):
        for obj in procedure_values:
            obj._procedure = None
            self._procedure_values.remove(obj)
    # >>> procedure_values



class Cabinet(AssetContainer):
    """ Enclosure that offers protection to the equipment it contains and/or safety to people/animals outside it.
    """
    # <<< cabinet
    # @generated
    def __init__(self, cabinet_model=None, **kw_args):
        """ Initialises a new 'Cabinet' instance.
        """
        
        self._cabinet_model = None
        self.cabinet_model = cabinet_model

        super(Cabinet, self).__init__(**kw_args)
    # >>> cabinet
        
    # <<< cabinet_model
    # @generated
    def get_cabinet_model(self):
        """ 
        """
        return self._cabinet_model

    def set_cabinet_model(self, value):
        if self._cabinet_model is not None:
            filtered = [x for x in self.cabinet_model.cabinets if x != self]
            self._cabinet_model._cabinets = filtered
            
        self._cabinet_model = value
        if self._cabinet_model is not None:
            self._cabinet_model._cabinets.append(self)

    cabinet_model = property(get_cabinet_model, set_cabinet_model)
    # >>> cabinet_model



class DimensionsInfo(IdentifiedObject):
    """ As applicable, the basic linear, area, or volume dimensions of an asset, asset type (AssetModel) or other type of object (such as land area). Units and multipliers are specified per dimension.
    """
    # <<< dimensions_info
    # @generated
    def __init__(self, orientation='', size_length='', size_depth='', size_width='', size_diameter='', assets=[], locations=[], specifications=[], **kw_args):
        """ Initialises a new 'DimensionsInfo' instance.
        """
        # A description of the orientation of the object relative to the dimensions. As an example, a vault may have north-south orientation for the sizeLength measurement and sizeDepth may be the height of the vault. 
        self.orientation = ''
        # Length measurement. 
        self.size_length = ''
        # Depth measurement. 
        self.size_depth = ''
        # Width measurement. 
        self.size_width = ''
        # Diameter measurement. 
        self.size_diameter = ''
        
        self._assets = []
        self.assets = assets
        self._locations = []
        self.locations = locations
        self._specifications = []
        self.specifications = specifications

        super(DimensionsInfo, self).__init__(**kw_args)
    # >>> dimensions_info
        
    # <<< assets
    # @generated
    def get_assets(self):
        """ 
        """
        return self._assets

    def set_assets(self, value):
        for x in self._assets:
            x._dimensions_info = None
        for y in value:
            y._dimensions_info = self
        self._assets = value
            
    assets = property(get_assets, set_assets)
    
    def add_assets(self, *assets):
        for obj in assets:
            obj._dimensions_info = self
            self._assets.append(obj)
        
    def remove_assets(self, *assets):
        for obj in assets:
            obj._dimensions_info = None
            self._assets.remove(obj)
    # >>> assets

    # <<< locations
    # @generated
    def get_locations(self):
        """ 
        """
        return self._locations

    def set_locations(self, value):
        for x in self._locations:
            x._dimensions_info = None
        for y in value:
            y._dimensions_info = self
        self._locations = value
            
    locations = property(get_locations, set_locations)
    
    def add_locations(self, *locations):
        for obj in locations:
            obj._dimensions_info = self
            self._locations.append(obj)
        
    def remove_locations(self, *locations):
        for obj in locations:
            obj._dimensions_info = None
            self._locations.remove(obj)
    # >>> locations

    # <<< specifications
    # @generated
    def get_specifications(self):
        """ 
        """
        return self._specifications

    def set_specifications(self, value):
        for p in self._specifications:
            filtered = [q for q in p.dimensions_infos if q != self]
            self._specifications._dimensions_infos = filtered
        for r in value:
            if self not in r._dimensions_infos:
                r._dimensions_infos.append(self)
        self._specifications = value
            
    specifications = property(get_specifications, set_specifications)
    
    def add_specifications(self, *specifications):
        for obj in specifications:
            if self not in obj._dimensions_infos:
                obj._dimensions_infos.append(self)
            self._specifications.append(obj)
        
    def remove_specifications(self, *specifications):
        for obj in specifications:
            if self in obj._dimensions_infos:
                obj._dimensions_infos.remove(self)
            self._specifications.remove(obj)
    # >>> specifications



class TapChangerAsset(Asset):
    """ Physical asset performing TapChanger function.
    """
    # <<< tap_changer_asset
    # @generated
    def __init__(self, tap_changer_model=None, **kw_args):
        """ Initialises a new 'TapChangerAsset' instance.
        """
        
        self._tap_changer_model = None
        self.tap_changer_model = tap_changer_model

        super(TapChangerAsset, self).__init__(**kw_args)
    # >>> tap_changer_asset
        
    # <<< tap_changer_model
    # @generated
    def get_tap_changer_model(self):
        """ 
        """
        return self._tap_changer_model

    def set_tap_changer_model(self, value):
        if self._tap_changer_model is not None:
            filtered = [x for x in self.tap_changer_model.tap_changer_assets if x != self]
            self._tap_changer_model._tap_changer_assets = filtered
            
        self._tap_changer_model = value
        if self._tap_changer_model is not None:
            self._tap_changer_model._tap_changer_assets.append(self)

    tap_changer_model = property(get_tap_changer_model, set_tap_changer_model)
    # >>> tap_changer_model



class Structure(AssetContainer):
    """ Construction holding assets such as conductors, transformers, switchgear, etc.
    """
    # <<< structure
    # @generated
    def __init__(self, remove_weed=False, material_kind='other', weed_removed_date='', fumigant_name='', fumigant_applied_date='', height='', structure_supports=[], **kw_args):
        """ Initialises a new 'Structure' instance.
        """
        # True if weeds are to be removed around asset. 
        self.remove_weed = False
        # Material this structure is made of. Values are: "other", "steel", "concrete", "wood"
        self.material_kind = 'other'
        # Date weed were last removed. 
        self.weed_removed_date = ''
        # Name of fumigant. 
        self.fumigant_name = ''
        # Date fumigant was last applied. 
        self.fumigant_applied_date = ''
        # Visible height of structure above ground level for overhead construction (e.g., Pole or Tower) or below ground level for an underground vault, manhole, etc. Refer to associated DimensionPropertiesInfo for other types of dimensions. 
        self.height = ''
        
        self._structure_supports = []
        self.structure_supports = structure_supports

        super(Structure, self).__init__(**kw_args)
    # >>> structure
        
    # <<< structure_supports
    # @generated
    def get_structure_supports(self):
        """ 
        """
        return self._structure_supports

    def set_structure_supports(self, value):
        for x in self._structure_supports:
            x._secured_structure = None
        for y in value:
            y._secured_structure = self
        self._structure_supports = value
            
    structure_supports = property(get_structure_supports, set_structure_supports)
    
    def add_structure_supports(self, *structure_supports):
        for obj in structure_supports:
            obj._secured_structure = self
            self._structure_supports.append(obj)
        
    def remove_structure_supports(self, *structure_supports):
        for obj in structure_supports:
            obj._secured_structure = None
            self._structure_supports.remove(obj)
    # >>> structure_supports



class StructureSupport(Asset):
    """ Support for Structures.
    """
    # <<< structure_support
    # @generated
    def __init__(self, length='', rod_count=0, rod_length='', size='', direction='', secured_structure=None, **kw_args):
        """ Initialises a new 'StructureSupport' instance.
        """
        # Length of anchor lead or guy. 
        self.length = ''
        # Number of rods used for an anchor. 
        self.rod_count = 0
        # Length of rod used for an anchor. 
        self.rod_length = ''
        # Size of anchor or guy. 
        self.size = ''
        # Direction of supporting anchor or guy. 
        self.direction = ''
        
        self._secured_structure = None
        self.secured_structure = secured_structure

        super(StructureSupport, self).__init__(**kw_args)
    # >>> structure_support
        
    # <<< secured_structure
    # @generated
    def get_secured_structure(self):
        """ 
        """
        return self._secured_structure

    def set_secured_structure(self, value):
        if self._secured_structure is not None:
            filtered = [x for x in self.secured_structure.structure_supports if x != self]
            self._secured_structure._structure_supports = filtered
            
        self._secured_structure = value
        if self._secured_structure is not None:
            self._secured_structure._structure_supports.append(self)

    secured_structure = property(get_secured_structure, set_secured_structure)
    # >>> secured_structure



class SwitchInfo(ElectricalInfo):
    """ Properties of a switch.
    """
    # <<< switch_info
    # @generated
    def __init__(self, remote=False, minimum_current='', dielectric_strength='', interrupting_rating='', load_break=False, pole_count=0, gang=False, withstand_current='', making_capacity='', switch_type_asset=None, switch_asset_model=None, switch_assets=[], **kw_args):
        """ Initialises a new 'SwitchInfo' instance.
        """
        # True if device is capable of being operated by remote control. 
        self.remote = False
        # The lowest value of current that the switch can make, carry and break in uninterrupted duty at the rated voltage under specified operating conditions without suffering significant deterioration of its performance. 
        self.minimum_current = ''
        # The maximum rms voltage that may be applied across an open contact without breaking down the dielectric properties of the switch in the open position. 
        self.dielectric_strength = ''
        # Breaking capacity, or short circuit rating, is the maximum rated current which the device can safely interrupt at the rated voltage. 
        self.interrupting_rating = ''
        # True if switch has load breaking capabiity. Unless specified false, this is always assumed to be true for breakers and reclosers. 
        self.load_break = False
        # Number of poles (i.e. of current carrying conductors that are switched). 
        self.pole_count = 0
        # True if multi-phase switch controls all phases concurrently. 
        self.gang = False
        # The highest value of current the switch can carry in the closed position at the rated voltage under specified operating conditions without suffering significant deterioration of its performance. 
        self.withstand_current = ''
        # The highest value of current the switch can make at the rated voltage under specified operating conditions without suffering significant deterioration of its performance. 
        self.making_capacity = ''
        
        self._switch_type_asset = None
        self.switch_type_asset = switch_type_asset
        self._switch_asset_model = None
        self.switch_asset_model = switch_asset_model
        self._switch_assets = []
        self.switch_assets = switch_assets

        super(SwitchInfo, self).__init__(**kw_args)
    # >>> switch_info
        
    # <<< switch_type_asset
    # @generated
    def get_switch_type_asset(self):
        """ 
        """
        return self._switch_type_asset

    def set_switch_type_asset(self, value):
        if self._switch_type_asset is not None:
            self._switch_type_asset._switch_info = None
            
        self._switch_type_asset = value
        if self._switch_type_asset is not None:
            self._switch_type_asset._switch_info = self
            
    switch_type_asset = property(get_switch_type_asset, set_switch_type_asset)
    # >>> switch_type_asset

    # <<< switch_asset_model
    # @generated
    def get_switch_asset_model(self):
        """ 
        """
        return self._switch_asset_model

    def set_switch_asset_model(self, value):
        if self._switch_asset_model is not None:
            self._switch_asset_model._switch_info = None
            
        self._switch_asset_model = value
        if self._switch_asset_model is not None:
            self._switch_asset_model._switch_info = self
            
    switch_asset_model = property(get_switch_asset_model, set_switch_asset_model)
    # >>> switch_asset_model

    # <<< switch_assets
    # @generated
    def get_switch_assets(self):
        """ 
        """
        return self._switch_assets

    def set_switch_assets(self, value):
        for x in self._switch_assets:
            x._switch_info = None
        for y in value:
            y._switch_info = self
        self._switch_assets = value
            
    switch_assets = property(get_switch_assets, set_switch_assets)
    
    def add_switch_assets(self, *switch_assets):
        for obj in switch_assets:
            obj._switch_info = self
            self._switch_assets.append(obj)
        
    def remove_switch_assets(self, *switch_assets):
        for obj in switch_assets:
            obj._switch_info = None
            self._switch_assets.remove(obj)
    # >>> switch_assets



class ElectricalAsset(Asset):
    """ An asset that has (or can have) a role in the electrical network.
    """
    # <<< electrical_asset
    # @generated
    def __init__(self, is_connected=False, phase_code='abn', conducting_equipment=None, electrical_infos=[], **kw_args):
        """ Initialises a new 'ElectricalAsset' instance.
        """
        # True if the asset is physically connected to electrical network (as opposed to being in a warehouse, being refurbished, etc.). Note that this attribute is not intended to imply energization status and/or whether the asset is actually being used. 
        self.is_connected = False
        # If 'isConnected' is true, then this is the as-built phase(s) that the asset is associatied with. Values are: "abn", "bc", "acn", "bn", "ac", "abc", "an", "ab", "c", "b", "abcn", "a", "cn", "n", "bcn"
        self.phase_code = 'abn'
        
        self._conducting_equipment = None
        self.conducting_equipment = conducting_equipment
        self._electrical_infos = []
        self.electrical_infos = electrical_infos

        super(ElectricalAsset, self).__init__(**kw_args)
    # >>> electrical_asset
        
    # <<< conducting_equipment
    # @generated
    def get_conducting_equipment(self):
        """ 
        """
        return self._conducting_equipment

    def set_conducting_equipment(self, value):
        if self._conducting_equipment is not None:
            filtered = [x for x in self.conducting_equipment.electrical_assets if x != self]
            self._conducting_equipment._electrical_assets = filtered
            
        self._conducting_equipment = value
        if self._conducting_equipment is not None:
            self._conducting_equipment._electrical_assets.append(self)

    conducting_equipment = property(get_conducting_equipment, set_conducting_equipment)
    # >>> conducting_equipment

    # <<< electrical_infos
    # @generated
    def get_electrical_infos(self):
        """ 
        """
        return self._electrical_infos

    def set_electrical_infos(self, value):
        for p in self._electrical_infos:
            filtered = [q for q in p.electrical_assets if q != self]
            self._electrical_infos._electrical_assets = filtered
        for r in value:
            if self not in r._electrical_assets:
                r._electrical_assets.append(self)
        self._electrical_infos = value
            
    electrical_infos = property(get_electrical_infos, set_electrical_infos)
    
    def add_electrical_infos(self, *electrical_infos):
        for obj in electrical_infos:
            if self not in obj._electrical_assets:
                obj._electrical_assets.append(self)
            self._electrical_infos.append(obj)
        
    def remove_electrical_infos(self, *electrical_infos):
        for obj in electrical_infos:
            if self in obj._electrical_assets:
                obj._electrical_assets.remove(self)
            self._electrical_infos.remove(obj)
    # >>> electrical_infos



class ShuntImpedanceInfo(ElectricalInfo):
    """ Properties of a shunt impedance.
    """
    # <<< shunt_impedance_info
    # @generated
    def __init__(self, local_on_level='', local_override=False, cell_size='', local_control_kind='none', high_voltage_override='', reg_branch='', control_kind='remote_with_local_override', normal_open=False, local_off_level='', reg_branch_kind='breaker', low_voltage_override='', max_switch_operation_count=0, reg_branch_end=0, switch_operation_cycle='', branch_direct=0, sensing_phase_code='abn', v_reg_line_line=False, shunt_compensator_type_asset=None, shunt_compensator_asset_model=None, shunt_compensator_assets=[], **kw_args):
        """ Initialises a new 'ShuntImpedanceInfo' instance.
        """
        # Lower control setting. 
        self.local_on_level = ''
        # True if the locally controlled capacitor has voltage override capability. 
        self.local_override = False
        # The size of the individual units that make up the bank. 
        self.cell_size = ''
        # Kind of local controller. Values are: "none", "voltage", "power_factor", "time", "reactive_power", "temperature", "current"
        self.local_control_kind = 'none'
        # For locally controlled shunt impedances which have a voltage override feature, the high voltage override value. If the voltage is above this value, the shunt impedance will be turned off regardless of the other local controller settings. 
        self.high_voltage_override = ''
        # For VAR, amp, or power factor locally controlled shunt impedances, the index of the regulation branch. 
        self.reg_branch = ''
        # Kind of control (if any). Values are: "remote_with_local_override", "local_only", "fixed", "remote_only"
        self.control_kind = 'remote_with_local_override'
        # True if open is normal status for a fixed capacitor bank, otherwise normal status is closed. 
        self.normal_open = False
        # Upper control setting. 
        self.local_off_level = ''
        # (For VAR, amp, or power factor locally controlled shunt impedances) Kind of regulation branch. Values are: "breaker", "recloser", "sectionner", "line", "transformer", "switch", "other", "fuse"
        self.reg_branch_kind = 'breaker'
        # For locally controlled shunt impedances which have a voltage override feature, the low voltage override value. If the voltage is below this value, the shunt impedance will be turned on regardless of the other local controller settings. 
        self.low_voltage_override = ''
        # IdmsShuntImpedanceData.maxNumSwitchOps 
        self.max_switch_operation_count = 0
        # For VAR, amp, or power factor locally controlled shunt impedances, the end of the branch that is regulated. The field has the following values: from side, to side, and tertiary (only if the branch is a transformer). 
        self.reg_branch_end = 0
        # Time interval between consecutive switching operations. 
        self.switch_operation_cycle = ''
        # For VAR, amp, or power factor locally controlled shunt impedances, the flow direction: in, out. 
        self.branch_direct = 0
        # Phases that are measured for controlling the device. Values are: "abn", "bc", "acn", "bn", "ac", "abc", "an", "ab", "c", "b", "abcn", "a", "cn", "n", "bcn"
        self.sensing_phase_code = 'abn'
        # True if regulated voltages are measured line to line, otherwise they are measured line to ground. 
        self.v_reg_line_line = False
        
        self._shunt_compensator_type_asset = None
        self.shunt_compensator_type_asset = shunt_compensator_type_asset
        self._shunt_compensator_asset_model = None
        self.shunt_compensator_asset_model = shunt_compensator_asset_model
        self._shunt_compensator_assets = []
        self.shunt_compensator_assets = shunt_compensator_assets

        super(ShuntImpedanceInfo, self).__init__(**kw_args)
    # >>> shunt_impedance_info
        
    # <<< shunt_compensator_type_asset
    # @generated
    def get_shunt_compensator_type_asset(self):
        """ 
        """
        return self._shunt_compensator_type_asset

    def set_shunt_compensator_type_asset(self, value):
        if self._shunt_compensator_type_asset is not None:
            self._shunt_compensator_type_asset._shunt_impedance_info = None
            
        self._shunt_compensator_type_asset = value
        if self._shunt_compensator_type_asset is not None:
            self._shunt_compensator_type_asset._shunt_impedance_info = self
            
    shunt_compensator_type_asset = property(get_shunt_compensator_type_asset, set_shunt_compensator_type_asset)
    # >>> shunt_compensator_type_asset

    # <<< shunt_compensator_asset_model
    # @generated
    def get_shunt_compensator_asset_model(self):
        """ 
        """
        return self._shunt_compensator_asset_model

    def set_shunt_compensator_asset_model(self, value):
        if self._shunt_compensator_asset_model is not None:
            self._shunt_compensator_asset_model._shunt_impedance_info = None
            
        self._shunt_compensator_asset_model = value
        if self._shunt_compensator_asset_model is not None:
            self._shunt_compensator_asset_model._shunt_impedance_info = self
            
    shunt_compensator_asset_model = property(get_shunt_compensator_asset_model, set_shunt_compensator_asset_model)
    # >>> shunt_compensator_asset_model

    # <<< shunt_compensator_assets
    # @generated
    def get_shunt_compensator_assets(self):
        """ 
        """
        return self._shunt_compensator_assets

    def set_shunt_compensator_assets(self, value):
        for x in self._shunt_compensator_assets:
            x._shunt_impedance_info = None
        for y in value:
            y._shunt_impedance_info = self
        self._shunt_compensator_assets = value
            
    shunt_compensator_assets = property(get_shunt_compensator_assets, set_shunt_compensator_assets)
    
    def add_shunt_compensator_assets(self, *shunt_compensator_assets):
        for obj in shunt_compensator_assets:
            obj._shunt_impedance_info = self
            self._shunt_compensator_assets.append(obj)
        
    def remove_shunt_compensator_assets(self, *shunt_compensator_assets):
        for obj in shunt_compensator_assets:
            obj._shunt_impedance_info = None
            self._shunt_compensator_assets.remove(obj)
    # >>> shunt_compensator_assets



class WindingInsulation(IdentifiedObject):
    """ Winding insulation condition as a result of a test.
    """
    # <<< winding_insulation
    # @generated
    def __init__(self, leakage_reactance='', insulation_resistance='', insulation_pfstatus='', status=None, from_transformer_winding=None, ground=None, transformer_observation=None, to_transformer_winding=None, **kw_args):
        """ Initialises a new 'WindingInsulation' instance.
        """
        # As of statusDate, the leakage reactance measured at the 'from' winding with the 'to' winding short-circuited and all other windings open-circuited. 
        self.leakage_reactance = ''
        # For testType, status of Winding Insulation Resistance as of statusDate. Typical values are: Acceptable, Questionable, Failed. 
        self.insulation_resistance = ''
        # Status of Winding Insulation Power Factor as of statusDate: Acceptable, Minor Deterioration or Moisture Absorption, Major Deterioration or Moisture Absorption, Failed. 
        self.insulation_pfstatus = ''
        
        self.status = status
        self._from_transformer_winding = None
        self.from_transformer_winding = from_transformer_winding
        self._ground = None
        self.ground = ground
        self._transformer_observation = None
        self.transformer_observation = transformer_observation
        self._to_transformer_winding = None
        self.to_transformer_winding = to_transformer_winding

        super(WindingInsulation, self).__init__(**kw_args)
    # >>> winding_insulation
        
    # <<< status
    # @generated
    status = None
    # >>> status

    # <<< from_transformer_winding
    # @generated
    def get_from_transformer_winding(self):
        """ 
        """
        return self._from_transformer_winding

    def set_from_transformer_winding(self, value):
        if self._from_transformer_winding is not None:
            filtered = [x for x in self.from_transformer_winding.from_winding_insulations if x != self]
            self._from_transformer_winding._from_winding_insulations = filtered
            
        self._from_transformer_winding = value
        if self._from_transformer_winding is not None:
            self._from_transformer_winding._from_winding_insulations.append(self)

    from_transformer_winding = property(get_from_transformer_winding, set_from_transformer_winding)
    # >>> from_transformer_winding

    # <<< ground
    # @generated
    def get_ground(self):
        """ 
        """
        return self._ground

    def set_ground(self, value):
        if self._ground is not None:
            filtered = [x for x in self.ground.winding_insulations if x != self]
            self._ground._winding_insulations = filtered
            
        self._ground = value
        if self._ground is not None:
            self._ground._winding_insulations.append(self)

    ground = property(get_ground, set_ground)
    # >>> ground

    # <<< transformer_observation
    # @generated
    def get_transformer_observation(self):
        """ 
        """
        return self._transformer_observation

    def set_transformer_observation(self, value):
        if self._transformer_observation is not None:
            filtered = [x for x in self.transformer_observation.winding_insulation_pfs if x != self]
            self._transformer_observation._winding_insulation_pfs = filtered
            
        self._transformer_observation = value
        if self._transformer_observation is not None:
            self._transformer_observation._winding_insulation_pfs.append(self)

    transformer_observation = property(get_transformer_observation, set_transformer_observation)
    # >>> transformer_observation

    # <<< to_transformer_winding
    # @generated
    def get_to_transformer_winding(self):
        """ 
        """
        return self._to_transformer_winding

    def set_to_transformer_winding(self, value):
        if self._to_transformer_winding is not None:
            filtered = [x for x in self.to_transformer_winding.to_winding_insulations if x != self]
            self._to_transformer_winding._to_winding_insulations = filtered
            
        self._to_transformer_winding = value
        if self._to_transformer_winding is not None:
            self._to_transformer_winding._to_winding_insulations.append(self)

    to_transformer_winding = property(get_to_transformer_winding, set_to_transformer_winding)
    # >>> to_transformer_winding



class PotentialTransformerInfo(ElectricalInfo):
    """ Used to define either the required additional electrical properties of a type of a Potential Transformer (PT), or a PT Model.
    """
    # <<< potential_transformer_info
    # @generated
    def __init__(self, secondary_ratio=None, primary_ratio=None, tertiary_ratio=None, potential_transformer_assets=[], potential_transformer_type_asset=None, potential_transformer_asset_models=[], **kw_args):
        """ Initialises a new 'PotentialTransformerInfo' instance.
        """
        
        self.secondary_ratio = secondary_ratio
        self.primary_ratio = primary_ratio
        self.tertiary_ratio = tertiary_ratio
        self._potential_transformer_assets = []
        self.potential_transformer_assets = potential_transformer_assets
        self._potential_transformer_type_asset = None
        self.potential_transformer_type_asset = potential_transformer_type_asset
        self._potential_transformer_asset_models = []
        self.potential_transformer_asset_models = potential_transformer_asset_models

        super(PotentialTransformerInfo, self).__init__(**kw_args)
    # >>> potential_transformer_info
        
    # <<< secondary_ratio
    # @generated
    # Ratio for the secondary winding tap changer.
    secondary_ratio = None
    # >>> secondary_ratio

    # <<< primary_ratio
    # @generated
    # Ratio for the primary winding tap changer.
    primary_ratio = None
    # >>> primary_ratio

    # <<< tertiary_ratio
    # @generated
    # Ratio for the tertiary winding tap changer.
    tertiary_ratio = None
    # >>> tertiary_ratio

    # <<< potential_transformer_assets
    # @generated
    def get_potential_transformer_assets(self):
        """ 
        """
        return self._potential_transformer_assets

    def set_potential_transformer_assets(self, value):
        for x in self._potential_transformer_assets:
            x._potential_transformer_info = None
        for y in value:
            y._potential_transformer_info = self
        self._potential_transformer_assets = value
            
    potential_transformer_assets = property(get_potential_transformer_assets, set_potential_transformer_assets)
    
    def add_potential_transformer_assets(self, *potential_transformer_assets):
        for obj in potential_transformer_assets:
            obj._potential_transformer_info = self
            self._potential_transformer_assets.append(obj)
        
    def remove_potential_transformer_assets(self, *potential_transformer_assets):
        for obj in potential_transformer_assets:
            obj._potential_transformer_info = None
            self._potential_transformer_assets.remove(obj)
    # >>> potential_transformer_assets

    # <<< potential_transformer_type_asset
    # @generated
    def get_potential_transformer_type_asset(self):
        """ 
        """
        return self._potential_transformer_type_asset

    def set_potential_transformer_type_asset(self, value):
        if self._potential_transformer_type_asset is not None:
            self._potential_transformer_type_asset._potential_transformer_info = None
            
        self._potential_transformer_type_asset = value
        if self._potential_transformer_type_asset is not None:
            self._potential_transformer_type_asset._potential_transformer_info = self
            
    potential_transformer_type_asset = property(get_potential_transformer_type_asset, set_potential_transformer_type_asset)
    # >>> potential_transformer_type_asset

    # <<< potential_transformer_asset_models
    # @generated
    def get_potential_transformer_asset_models(self):
        """ 
        """
        return self._potential_transformer_asset_models

    def set_potential_transformer_asset_models(self, value):
        for x in self._potential_transformer_asset_models:
            x._potential_transformer_info = None
        for y in value:
            y._potential_transformer_info = self
        self._potential_transformer_asset_models = value
            
    potential_transformer_asset_models = property(get_potential_transformer_asset_models, set_potential_transformer_asset_models)
    
    def add_potential_transformer_asset_models(self, *potential_transformer_asset_models):
        for obj in potential_transformer_asset_models:
            obj._potential_transformer_info = self
            self._potential_transformer_asset_models.append(obj)
        
    def remove_potential_transformer_asset_models(self, *potential_transformer_asset_models):
        for obj in potential_transformer_asset_models:
            obj._potential_transformer_info = None
            self._potential_transformer_asset_models.remove(obj)
    # >>> potential_transformer_asset_models



class SVCInfo(ElectricalInfo):
    """ Properties for an SVC, allowing the capacitive and inductive ratings for each phase to be specified individually if required.
    """
    # <<< svcinfo
    # @generated
    def __init__(self, inductive_rating='', capacitive_rating='', svcasset_model=None, svcasset=None, svctype_assets=[], **kw_args):
        """ Initialises a new 'SVCInfo' instance.
        """
        # Maximum inductive reactive power 
        self.inductive_rating = ''
        # Maximum capacitive reactive power 
        self.capacitive_rating = ''
        
        self._svcasset_model = None
        self.svcasset_model = svcasset_model
        self._svcasset = None
        self.svcasset = svcasset
        self._svctype_assets = []
        self.svctype_assets = svctype_assets

        super(SVCInfo, self).__init__(**kw_args)
    # >>> svcinfo
        
    # <<< svcasset_model
    # @generated
    def get_svcasset_model(self):
        """ 
        """
        return self._svcasset_model

    def set_svcasset_model(self, value):
        if self._svcasset_model is not None:
            self._svcasset_model._svc_info = None
            
        self._svcasset_model = value
        if self._svcasset_model is not None:
            self._svcasset_model._svc_info = self
            
    svcasset_model = property(get_svcasset_model, set_svcasset_model)
    # >>> svcasset_model

    # <<< svcasset
    # @generated
    def get_svcasset(self):
        """ 
        """
        return self._svcasset

    def set_svcasset(self, value):
        if self._svcasset is not None:
            self._svcasset._svc_info = None
            
        self._svcasset = value
        if self._svcasset is not None:
            self._svcasset._svc_info = self
            
    svcasset = property(get_svcasset, set_svcasset)
    # >>> svcasset

    # <<< svctype_assets
    # @generated
    def get_svctype_assets(self):
        """ 
        """
        return self._svctype_assets

    def set_svctype_assets(self, value):
        for p in self._svctype_assets:
            filtered = [q for q in p.svc_infos if q != self]
            self._svctype_assets._svc_infos = filtered
        for r in value:
            if self not in r._svc_infos:
                r._svc_infos.append(self)
        self._svctype_assets = value
            
    svctype_assets = property(get_svctype_assets, set_svctype_assets)
    
    def add_svctype_assets(self, *svctype_assets):
        for obj in svctype_assets:
            if self not in obj._svc_infos:
                obj._svc_infos.append(self)
            self._svctype_assets.append(obj)
        
    def remove_svctype_assets(self, *svctype_assets):
        for obj in svctype_assets:
            if self in obj._svc_infos:
                obj._svc_infos.remove(self)
            self._svctype_assets.remove(obj)
    # >>> svctype_assets



class CompositeSwitchAsset(Asset):
    """ Physical asset that performs a given CompositeSwitch's role.
    """
    # <<< composite_switch_asset
    # @generated
    def __init__(self, kind='other', composite_switch_info=None, composite_switch_asset_model=None, **kw_args):
        """ Initialises a new 'CompositeSwitchAsset' instance.
        """
        # Kind of composite switch. Values are: "other", "throw_over", "ug_multi_switch", "esco_throw_over", "gral", "ral", "regulator_bypass"
        self.kind = 'other'
        
        self._composite_switch_info = None
        self.composite_switch_info = composite_switch_info
        self._composite_switch_asset_model = None
        self.composite_switch_asset_model = composite_switch_asset_model

        super(CompositeSwitchAsset, self).__init__(**kw_args)
    # >>> composite_switch_asset
        
    # <<< composite_switch_info
    # @generated
    def get_composite_switch_info(self):
        """ 
        """
        return self._composite_switch_info

    def set_composite_switch_info(self, value):
        if self._composite_switch_info is not None:
            filtered = [x for x in self.composite_switch_info.composite_switch_assets if x != self]
            self._composite_switch_info._composite_switch_assets = filtered
            
        self._composite_switch_info = value
        if self._composite_switch_info is not None:
            self._composite_switch_info._composite_switch_assets.append(self)

    composite_switch_info = property(get_composite_switch_info, set_composite_switch_info)
    # >>> composite_switch_info

    # <<< composite_switch_asset_model
    # @generated
    def get_composite_switch_asset_model(self):
        """ 
        """
        return self._composite_switch_asset_model

    def set_composite_switch_asset_model(self, value):
        if self._composite_switch_asset_model is not None:
            filtered = [x for x in self.composite_switch_asset_model.composite_switch_assets if x != self]
            self._composite_switch_asset_model._composite_switch_assets = filtered
            
        self._composite_switch_asset_model = value
        if self._composite_switch_asset_model is not None:
            self._composite_switch_asset_model._composite_switch_assets.append(self)

    composite_switch_asset_model = property(get_composite_switch_asset_model, set_composite_switch_asset_model)
    # >>> composite_switch_asset_model



class Medium(IdentifiedObject):
    """ A substance that either (1) provides the means of transmission of a force or effect, such as hydraulic fluid, or (2) is used for a surrounding or enveloping substance, such as oil in a transformer or circuit breaker.
    """
    # <<< medium
    # @generated
    def __init__(self, volume_spec='', kind='gas', assets=[], specification=None, **kw_args):
        """ Initialises a new 'Medium' instance.
        """
        # The volume of the medium specified for this application. Note that the actual volume is a type of measurement associated witht the asset. 
        self.volume_spec = ''
        # Kind of this medium. Values are: "gas", "solid", "liquid"
        self.kind = 'gas'
        
        self._assets = []
        self.assets = assets
        self._specification = None
        self.specification = specification

        super(Medium, self).__init__(**kw_args)
    # >>> medium
        
    # <<< assets
    # @generated
    def get_assets(self):
        """ 
        """
        return self._assets

    def set_assets(self, value):
        for p in self._assets:
            filtered = [q for q in p.mediums if q != self]
            self._assets._mediums = filtered
        for r in value:
            if self not in r._mediums:
                r._mediums.append(self)
        self._assets = value
            
    assets = property(get_assets, set_assets)
    
    def add_assets(self, *assets):
        for obj in assets:
            if self not in obj._mediums:
                obj._mediums.append(self)
            self._assets.append(obj)
        
    def remove_assets(self, *assets):
        for obj in assets:
            if self in obj._mediums:
                obj._mediums.remove(self)
            self._assets.remove(obj)
    # >>> assets

    # <<< specification
    # @generated
    def get_specification(self):
        """ 
        """
        return self._specification

    def set_specification(self, value):
        if self._specification is not None:
            filtered = [x for x in self.specification.mediums if x != self]
            self._specification._mediums = filtered
            
        self._specification = value
        if self._specification is not None:
            self._specification._mediums.append(self)

    specification = property(get_specification, set_specification)
    # >>> specification



class AssetAssetRole(Role):
    """ Roles played between Assets and other Assets.
    """
    # <<< asset_asset_role
    # @generated
    def __init__(self, from_asset=None, to_asset=None, **kw_args):
        """ Initialises a new 'AssetAssetRole' instance.
        """
        
        self._from_asset = None
        self.from_asset = from_asset
        self._to_asset = None
        self.to_asset = to_asset

        super(AssetAssetRole, self).__init__(**kw_args)
    # >>> asset_asset_role
        
    # <<< from_asset
    # @generated
    def get_from_asset(self):
        """ 
        """
        return self._from_asset

    def set_from_asset(self, value):
        if self._from_asset is not None:
            filtered = [x for x in self.from_asset.to_asset_roles if x != self]
            self._from_asset._to_asset_roles = filtered
            
        self._from_asset = value
        if self._from_asset is not None:
            self._from_asset._to_asset_roles.append(self)

    from_asset = property(get_from_asset, set_from_asset)
    # >>> from_asset

    # <<< to_asset
    # @generated
    def get_to_asset(self):
        """ 
        """
        return self._to_asset

    def set_to_asset(self, value):
        if self._to_asset is not None:
            filtered = [x for x in self.to_asset.from_asset_roles if x != self]
            self._to_asset._from_asset_roles = filtered
            
        self._to_asset = value
        if self._to_asset is not None:
            self._to_asset._from_asset_roles.append(self)

    to_asset = property(get_to_asset, set_to_asset)
    # >>> to_asset



class WireArrangement(IdentifiedObject):
    """ Identification, spacing and configuration of the wires of a ConductorType, with reference to their type.
    """
    # <<< wire_arrangement
    # @generated
    def __init__(self, mounting_point_y=0, mounting_point_x=0, wire_type=None, conductor_type=None, **kw_args):
        """ Initialises a new 'WireArrangement' instance.
        """
        # Mounting point where wire One is mounted. 
        self.mounting_point_y = 0
        # Mounting point where wire One is mounted. 
        self.mounting_point_x = 0
        
        self._wire_type = None
        self.wire_type = wire_type
        self._conductor_type = None
        self.conductor_type = conductor_type

        super(WireArrangement, self).__init__(**kw_args)
    # >>> wire_arrangement
        
    # <<< wire_type
    # @generated
    def get_wire_type(self):
        """ Wire type mounted at a specified place in this wire arrangement.
        """
        return self._wire_type

    def set_wire_type(self, value):
        if self._wire_type is not None:
            filtered = [x for x in self.wire_type.wire_arrangements if x != self]
            self._wire_type._wire_arrangements = filtered
            
        self._wire_type = value
        if self._wire_type is not None:
            self._wire_type._wire_arrangements.append(self)

    wire_type = property(get_wire_type, set_wire_type)
    # >>> wire_type

    # <<< conductor_type
    # @generated
    def get_conductor_type(self):
        """ Conductor type owning this wire arangement.
        """
        return self._conductor_type

    def set_conductor_type(self, value):
        if self._conductor_type is not None:
            filtered = [x for x in self.conductor_type.wire_arrangements if x != self]
            self._conductor_type._wire_arrangements = filtered
            
        self._conductor_type = value
        if self._conductor_type is not None:
            self._conductor_type._wire_arrangements.append(self)

    conductor_type = property(get_conductor_type, set_conductor_type)
    # >>> conductor_type



class ReliabilityInfo(IdentifiedObject):
    """ Information regarding the experienced and expected reliability of a specific asset, type of asset, or asset model.
    """
    # <<< reliability_info
    # @generated
    def __init__(self, mom_failure_rate='', m_ttr='', specification=None, assets=[], **kw_args):
        """ Initialises a new 'ReliabilityInfo' instance.
        """
        # Momentary failure rate (temporary failures/kft-year). 
        self.mom_failure_rate = ''
        # Mean time to repair (MTTR - hours). 
        self.m_ttr = ''
        
        self._specification = None
        self.specification = specification
        self._assets = []
        self.assets = assets

        super(ReliabilityInfo, self).__init__(**kw_args)
    # >>> reliability_info
        
    # <<< specification
    # @generated
    def get_specification(self):
        """ 
        """
        return self._specification

    def set_specification(self, value):
        if self._specification is not None:
            filtered = [x for x in self.specification.reliability_infos if x != self]
            self._specification._reliability_infos = filtered
            
        self._specification = value
        if self._specification is not None:
            self._specification._reliability_infos.append(self)

    specification = property(get_specification, set_specification)
    # >>> specification

    # <<< assets
    # @generated
    def get_assets(self):
        """ 
        """
        return self._assets

    def set_assets(self, value):
        for p in self._assets:
            filtered = [q for q in p.reliability_infos if q != self]
            self._assets._reliability_infos = filtered
        for r in value:
            if self not in r._reliability_infos:
                r._reliability_infos.append(self)
        self._assets = value
            
    assets = property(get_assets, set_assets)
    
    def add_assets(self, *assets):
        for obj in assets:
            if self not in obj._reliability_infos:
                obj._reliability_infos.append(self)
            self._assets.append(obj)
        
    def remove_assets(self, *assets):
        for obj in assets:
            if self in obj._reliability_infos:
                obj._reliability_infos.remove(self)
            self._assets.remove(obj)
    # >>> assets



class TransformerInfo(ElectricalInfo):
    """ Additional electrical properties of a type of transformer, of a transformer model, or the actual ones of a particular transformer asset.
    """
    # <<< transformer_info
    # @generated
    def __init__(self, line_drp_rs='', line_drp_xs='', rev_reg_s=False, impedance_xy='', grounded=False, r_ground='', line_drp_rp='', mag_pf=0.0, line_drp_xp='', emergency_apparent_power='', impedance_hy='', x_ground='', rev_reg_p=False, line_drp_rt='', line_drp_xt='', rev_reg_t=False, impedance_hx='', winding_code=None, transformer_type_assets=[], transformer_assets=[], transformer_asset_models=[], **kw_args):
        """ Initialises a new 'TransformerInfo' instance.
        """
        # Secondary winding line drop compensation resistance. 
        self.line_drp_rs = ''
        # Secondary winding line drop compensation reactance. 
        self.line_drp_xs = ''
        # True if secondary winding tap changer has reverse regulation capability. 
        self.rev_reg_s = False
        # Impedance Secondary to Tertiary. 
        self.impedance_xy = ''
        # True if transformer is grounded. 
        self.grounded = False
        # Ground resistance path through connected grounding transformer. 
        self.r_ground = ''
        # Primary winding line drop compensation resistance. 
        self.line_drp_rp = ''
        # Magnetization power factor. 
        self.mag_pf = 0.0
        # Primary winding line drop compensation reactance. 
        self.line_drp_xp = ''
        # Apparent power that the winding can carry under emergency conditions. 
        self.emergency_apparent_power = ''
        # Impedance Primary to Tertiary. 
        self.impedance_hy = ''
        # Ground reactance path through connected grounding transformer. 
        self.x_ground = ''
        # True if primary winding tap changer has reverse regulation capability. 
        self.rev_reg_p = False
        # Tertiary winding line drop compensation resistance. 
        self.line_drp_rt = ''
        # Tertiary winding line drop compensation reactance. 
        self.line_drp_xt = ''
        # True if tertiary winding tap changer has reverse regulation capability. 
        self.rev_reg_t = False
        # Impedance Primary to Secondary. 
        self.impedance_hx = ''
        
        self.winding_code = winding_code
        self._transformer_type_assets = []
        self.transformer_type_assets = transformer_type_assets
        self._transformer_assets = []
        self.transformer_assets = transformer_assets
        self._transformer_asset_models = []
        self.transformer_asset_models = transformer_asset_models

        super(TransformerInfo, self).__init__(**kw_args)
    # >>> transformer_info
        
    # <<< winding_code
    # @generated
    # Details on winding, allowing to specify winding code such as DYn11, DYn1 or DY11.
    winding_code = None
    # >>> winding_code

    # <<< transformer_type_assets
    # @generated
    def get_transformer_type_assets(self):
        """ 
        """
        return self._transformer_type_assets

    def set_transformer_type_assets(self, value):
        for x in self._transformer_type_assets:
            x._transformer_info = None
        for y in value:
            y._transformer_info = self
        self._transformer_type_assets = value
            
    transformer_type_assets = property(get_transformer_type_assets, set_transformer_type_assets)
    
    def add_transformer_type_assets(self, *transformer_type_assets):
        for obj in transformer_type_assets:
            obj._transformer_info = self
            self._transformer_type_assets.append(obj)
        
    def remove_transformer_type_assets(self, *transformer_type_assets):
        for obj in transformer_type_assets:
            obj._transformer_info = None
            self._transformer_type_assets.remove(obj)
    # >>> transformer_type_assets

    # <<< transformer_assets
    # @generated
    def get_transformer_assets(self):
        """ 
        """
        return self._transformer_assets

    def set_transformer_assets(self, value):
        for x in self._transformer_assets:
            x._transformer_info = None
        for y in value:
            y._transformer_info = self
        self._transformer_assets = value
            
    transformer_assets = property(get_transformer_assets, set_transformer_assets)
    
    def add_transformer_assets(self, *transformer_assets):
        for obj in transformer_assets:
            obj._transformer_info = self
            self._transformer_assets.append(obj)
        
    def remove_transformer_assets(self, *transformer_assets):
        for obj in transformer_assets:
            obj._transformer_info = None
            self._transformer_assets.remove(obj)
    # >>> transformer_assets

    # <<< transformer_asset_models
    # @generated
    def get_transformer_asset_models(self):
        """ 
        """
        return self._transformer_asset_models

    def set_transformer_asset_models(self, value):
        for x in self._transformer_asset_models:
            x._transformer_info = None
        for y in value:
            y._transformer_info = self
        self._transformer_asset_models = value
            
    transformer_asset_models = property(get_transformer_asset_models, set_transformer_asset_models)
    
    def add_transformer_asset_models(self, *transformer_asset_models):
        for obj in transformer_asset_models:
            obj._transformer_info = self
            self._transformer_asset_models.append(obj)
        
    def remove_transformer_asset_models(self, *transformer_asset_models):
        for obj in transformer_asset_models:
            obj._transformer_info = None
            self._transformer_asset_models.remove(obj)
    # >>> transformer_asset_models



class ComEquipmentAsset(AssetContainer):
    """ Communicaiton equipment, other than media, such as gateways, routers, controllers, etc.
    """
    # <<< com_equipment_asset
    # @generated
    def __init__(self, device_functions=[], **kw_args):
        """ Initialises a new 'ComEquipmentAsset' instance.
        """
        
        self._device_functions = []
        self.device_functions = device_functions

        super(ComEquipmentAsset, self).__init__(**kw_args)
    # >>> com_equipment_asset
        
    # <<< device_functions
    # @generated
    def get_device_functions(self):
        """ All device functions of this communication equipment asset.
        """
        return self._device_functions

    def set_device_functions(self, value):
        for x in self._device_functions:
            x._com_equipment_asset = None
        for y in value:
            y._com_equipment_asset = self
        self._device_functions = value
            
    device_functions = property(get_device_functions, set_device_functions)
    
    def add_device_functions(self, *device_functions):
        for obj in device_functions:
            obj._com_equipment_asset = self
            self._device_functions.append(obj)
        
    def remove_device_functions(self, *device_functions):
        for obj in device_functions:
            obj._com_equipment_asset = None
            self._device_functions.remove(obj)
    # >>> device_functions



class ConductorType(IdentifiedObject):
    """ Wire or cable conductor (per IEEE specs). A specific type of wire or combination of wires not insulated from one another, suitable for carrying electric current. It may be bare or insulated.
    """
    # <<< conductor_type
    # @generated
    def __init__(self, sheath_reactance='', sheath_resistance='', wire_arrangements=[], linear_conductor_assets=[], conductors=[], linear_conductro_type_asset=None, **kw_args):
        """ Initialises a new 'ConductorType' instance.
        """
        # Reactance of the sheath for cable conductors. 
        self.sheath_reactance = ''
        # Resistance of the sheath for cable conductors. 
        self.sheath_resistance = ''
        
        self._wire_arrangements = []
        self.wire_arrangements = wire_arrangements
        self._linear_conductor_assets = []
        self.linear_conductor_assets = linear_conductor_assets
        self._conductors = []
        self.conductors = conductors
        self._linear_conductro_type_asset = None
        self.linear_conductro_type_asset = linear_conductro_type_asset

        super(ConductorType, self).__init__(**kw_args)
    # >>> conductor_type
        
    # <<< wire_arrangements
    # @generated
    def get_wire_arrangements(self):
        """ All wire arrangements for this conductor type.
        """
        return self._wire_arrangements

    def set_wire_arrangements(self, value):
        for x in self._wire_arrangements:
            x._conductor_type = None
        for y in value:
            y._conductor_type = self
        self._wire_arrangements = value
            
    wire_arrangements = property(get_wire_arrangements, set_wire_arrangements)
    
    def add_wire_arrangements(self, *wire_arrangements):
        for obj in wire_arrangements:
            obj._conductor_type = self
            self._wire_arrangements.append(obj)
        
    def remove_wire_arrangements(self, *wire_arrangements):
        for obj in wire_arrangements:
            obj._conductor_type = None
            self._wire_arrangements.remove(obj)
    # >>> wire_arrangements

    # <<< linear_conductor_assets
    # @generated
    def get_linear_conductor_assets(self):
        """ 
        """
        return self._linear_conductor_assets

    def set_linear_conductor_assets(self, value):
        for x in self._linear_conductor_assets:
            x._conductor_type = None
        for y in value:
            y._conductor_type = self
        self._linear_conductor_assets = value
            
    linear_conductor_assets = property(get_linear_conductor_assets, set_linear_conductor_assets)
    
    def add_linear_conductor_assets(self, *linear_conductor_assets):
        for obj in linear_conductor_assets:
            obj._conductor_type = self
            self._linear_conductor_assets.append(obj)
        
    def remove_linear_conductor_assets(self, *linear_conductor_assets):
        for obj in linear_conductor_assets:
            obj._conductor_type = None
            self._linear_conductor_assets.remove(obj)
    # >>> linear_conductor_assets

    # <<< conductors
    # @generated
    def get_conductors(self):
        """ All conductors this conductor type physically describes.
        """
        return self._conductors

    def set_conductors(self, value):
        for x in self._conductors:
            x._conductor_type = None
        for y in value:
            y._conductor_type = self
        self._conductors = value
            
    conductors = property(get_conductors, set_conductors)
    
    def add_conductors(self, *conductors):
        for obj in conductors:
            obj._conductor_type = self
            self._conductors.append(obj)
        
    def remove_conductors(self, *conductors):
        for obj in conductors:
            obj._conductor_type = None
            self._conductors.remove(obj)
    # >>> conductors

    # <<< linear_conductro_type_asset
    # @generated
    def get_linear_conductro_type_asset(self):
        """ 
        """
        return self._linear_conductro_type_asset

    def set_linear_conductro_type_asset(self, value):
        if self._linear_conductro_type_asset is not None:
            self._linear_conductro_type_asset._conductor_type = None
            
        self._linear_conductro_type_asset = value
        if self._linear_conductro_type_asset is not None:
            self._linear_conductro_type_asset._conductor_type = self
            
    linear_conductro_type_asset = property(get_linear_conductro_type_asset, set_linear_conductro_type_asset)
    # >>> linear_conductro_type_asset



class WireType(IdentifiedObject):
    """ Wire conductor (per IEEE specs). A specific type of wire or combination of wires, not insulated from each other, suitable for carrying electrical current.
    """
    # <<< wire_type
    # @generated
    def __init__(self, g_mr='', resistance='', rated_current='', phase_conductor_spacing='', radius='', phase_conductor_count=0, wire_arrangements=[], **kw_args):
        """ Initialises a new 'WireType' instance.
        """
        # Geometric Mean Radius. If we replace the conductor by a thin walled tube of radius GMR, then its reactance is identical to the reactance of the actual conductor. 
        self.g_mr = ''
        # The resistance per unit length of the conductor 
        self.resistance = ''
        # Current carrying capacity of a wire or cable under stated thermal conditions 
        self.rated_current = ''
        # Distance between conductor strands in a (symmetrical) bundle. 
        self.phase_conductor_spacing = ''
        # The radius of the conductor 
        self.radius = ''
        # Number of conductor strands in the (symmetrical) bundle (1-12) 
        self.phase_conductor_count = 0
        
        self._wire_arrangements = []
        self.wire_arrangements = wire_arrangements

        super(WireType, self).__init__(**kw_args)
    # >>> wire_type
        
    # <<< wire_arrangements
    # @generated
    def get_wire_arrangements(self):
        """ All wire arrangements this wire type is mounted in.
        """
        return self._wire_arrangements

    def set_wire_arrangements(self, value):
        for x in self._wire_arrangements:
            x._wire_type = None
        for y in value:
            y._wire_type = self
        self._wire_arrangements = value
            
    wire_arrangements = property(get_wire_arrangements, set_wire_arrangements)
    
    def add_wire_arrangements(self, *wire_arrangements):
        for obj in wire_arrangements:
            obj._wire_type = self
            self._wire_arrangements.append(obj)
        
    def remove_wire_arrangements(self, *wire_arrangements):
        for obj in wire_arrangements:
            obj._wire_type = None
            self._wire_arrangements.remove(obj)
    # >>> wire_arrangements



class BushingInsulationPF(IdentifiedObject):
    """ Bushing insulation power factor condition as a result of a test. Typical status values are: Acceptable, Minor Deterioration or Moisture Absorption, Major Deterioration or Moisture Absorption, Failed.
    """
    # <<< bushing_insulation_pf
    # @generated
    def __init__(self, test_kind='c1', status=None, transformer_observation=None, bushing_asset=None, **kw_args):
        """ Initialises a new 'BushingInsulationPF' instance.
        """
        # Kind of test for this bushing. Values are: "c1", "c2"
        self.test_kind = 'c1'
        
        self.status = status
        self._transformer_observation = None
        self.transformer_observation = transformer_observation
        self._bushing_asset = None
        self.bushing_asset = bushing_asset

        super(BushingInsulationPF, self).__init__(**kw_args)
    # >>> bushing_insulation_pf
        
    # <<< status
    # @generated
    status = None
    # >>> status

    # <<< transformer_observation
    # @generated
    def get_transformer_observation(self):
        """ 
        """
        return self._transformer_observation

    def set_transformer_observation(self, value):
        if self._transformer_observation is not None:
            filtered = [x for x in self.transformer_observation.bushing_insultation_pfs if x != self]
            self._transformer_observation._bushing_insultation_pfs = filtered
            
        self._transformer_observation = value
        if self._transformer_observation is not None:
            self._transformer_observation._bushing_insultation_pfs.append(self)

    transformer_observation = property(get_transformer_observation, set_transformer_observation)
    # >>> transformer_observation

    # <<< bushing_asset
    # @generated
    def get_bushing_asset(self):
        """ 
        """
        return self._bushing_asset

    def set_bushing_asset(self, value):
        if self._bushing_asset is not None:
            filtered = [x for x in self.bushing_asset.bushing_insulation_pfs if x != self]
            self._bushing_asset._bushing_insulation_pfs = filtered
            
        self._bushing_asset = value
        if self._bushing_asset is not None:
            self._bushing_asset._bushing_insulation_pfs.append(self)

    bushing_asset = property(get_bushing_asset, set_bushing_asset)
    # >>> bushing_asset



class CurrentTransformerInfo(ElectricalInfo):
    """ Used to define either the required additional electrical properties of a type of Current Transformer (CT) or a CT Model.
    """
    # <<< current_transformer_info
    # @generated
    def __init__(self, tertiary_fls_rating='', secondary_fls_rating='', primary_fls_rating='', secondary_ratio=None, tertiary_ratio=None, primary_ratio=None, current_transformer_assert_models=[], current_transformer_type_asset=None, current_transformer_assets=[], **kw_args):
        """ Initialises a new 'CurrentTransformerInfo' instance.
        """
        # Full load secondary (FLS) rating for tertiary winding. 
        self.tertiary_fls_rating = ''
        # Full load secondary (FLS) rating for secondary winding. 
        self.secondary_fls_rating = ''
        # Full load secondary (FLS) rating for primary winding. 
        self.primary_fls_rating = ''
        
        self.secondary_ratio = secondary_ratio
        self.tertiary_ratio = tertiary_ratio
        self.primary_ratio = primary_ratio
        self._current_transformer_assert_models = []
        self.current_transformer_assert_models = current_transformer_assert_models
        self._current_transformer_type_asset = None
        self.current_transformer_type_asset = current_transformer_type_asset
        self._current_transformer_assets = []
        self.current_transformer_assets = current_transformer_assets

        super(CurrentTransformerInfo, self).__init__(**kw_args)
    # >>> current_transformer_info
        
    # <<< secondary_ratio
    # @generated
    # Ratio for the secondary winding tap changer.
    secondary_ratio = None
    # >>> secondary_ratio

    # <<< tertiary_ratio
    # @generated
    # Ratio for the tertiary winding tap changer.
    tertiary_ratio = None
    # >>> tertiary_ratio

    # <<< primary_ratio
    # @generated
    # Ratio for the primary winding tap changer.
    primary_ratio = None
    # >>> primary_ratio

    # <<< current_transformer_assert_models
    # @generated
    def get_current_transformer_assert_models(self):
        """ 
        """
        return self._current_transformer_assert_models

    def set_current_transformer_assert_models(self, value):
        for x in self._current_transformer_assert_models:
            x._current_transformer_info = None
        for y in value:
            y._current_transformer_info = self
        self._current_transformer_assert_models = value
            
    current_transformer_assert_models = property(get_current_transformer_assert_models, set_current_transformer_assert_models)
    
    def add_current_transformer_assert_models(self, *current_transformer_assert_models):
        for obj in current_transformer_assert_models:
            obj._current_transformer_info = self
            self._current_transformer_assert_models.append(obj)
        
    def remove_current_transformer_assert_models(self, *current_transformer_assert_models):
        for obj in current_transformer_assert_models:
            obj._current_transformer_info = None
            self._current_transformer_assert_models.remove(obj)
    # >>> current_transformer_assert_models

    # <<< current_transformer_type_asset
    # @generated
    def get_current_transformer_type_asset(self):
        """ 
        """
        return self._current_transformer_type_asset

    def set_current_transformer_type_asset(self, value):
        if self._current_transformer_type_asset is not None:
            self._current_transformer_type_asset._current_transformer_info = None
            
        self._current_transformer_type_asset = value
        if self._current_transformer_type_asset is not None:
            self._current_transformer_type_asset._current_transformer_info = self
            
    current_transformer_type_asset = property(get_current_transformer_type_asset, set_current_transformer_type_asset)
    # >>> current_transformer_type_asset

    # <<< current_transformer_assets
    # @generated
    def get_current_transformer_assets(self):
        """ 
        """
        return self._current_transformer_assets

    def set_current_transformer_assets(self, value):
        for x in self._current_transformer_assets:
            x._current_transformer_info = None
        for y in value:
            y._current_transformer_info = self
        self._current_transformer_assets = value
            
    current_transformer_assets = property(get_current_transformer_assets, set_current_transformer_assets)
    
    def add_current_transformer_assets(self, *current_transformer_assets):
        for obj in current_transformer_assets:
            obj._current_transformer_info = self
            self._current_transformer_assets.append(obj)
        
    def remove_current_transformer_assets(self, *current_transformer_assets):
        for obj in current_transformer_assets:
            obj._current_transformer_info = None
            self._current_transformer_assets.remove(obj)
    # >>> current_transformer_assets



class OrgAssetRole(Role):
    """ The roles played between an Organisations and an Asset.
    """
    # <<< org_asset_role
    # @generated
    def __init__(self, percent_ownership=0.0, erp_organisation=None, asset=None, **kw_args):
        """ Initialises a new 'OrgAssetRole' instance.
        """
        # If the role type is 'owner,' this indicate the percentage of ownership. 
        self.percent_ownership = 0.0
        
        self._erp_organisation = None
        self.erp_organisation = erp_organisation
        self._asset = None
        self.asset = asset

        super(OrgAssetRole, self).__init__(**kw_args)
    # >>> org_asset_role
        
    # <<< erp_organisation
    # @generated
    def get_erp_organisation(self):
        """ 
        """
        return self._erp_organisation

    def set_erp_organisation(self, value):
        if self._erp_organisation is not None:
            filtered = [x for x in self.erp_organisation.asset_roles if x != self]
            self._erp_organisation._asset_roles = filtered
            
        self._erp_organisation = value
        if self._erp_organisation is not None:
            self._erp_organisation._asset_roles.append(self)

    erp_organisation = property(get_erp_organisation, set_erp_organisation)
    # >>> erp_organisation

    # <<< asset
    # @generated
    def get_asset(self):
        """ 
        """
        return self._asset

    def set_asset(self, value):
        if self._asset is not None:
            filtered = [x for x in self.asset.erp_organisation_roles if x != self]
            self._asset._erp_organisation_roles = filtered
            
        self._asset = value
        if self._asset is not None:
            self._asset._erp_organisation_roles.append(self)

    asset = property(get_asset, set_asset)
    # >>> asset



class Vehicle(Asset):
    """ A vehicle is a type of utility asset.
    """
    # <<< vehicle
    # @generated
    def __init__(self, odometer_reading='', odometer_read_date_time='', usage_kind='crew', vehicle_asset_model=None, crew=None, **kw_args):
        """ Initialises a new 'Vehicle' instance.
        """
        # Odometer reading of this vehicle as of the 'odometerReadingDateTime'. Refer to associated ActivityRecords for earlier readings. 
        self.odometer_reading = ''
        # Date and time the last odometer reading was recorded. 
        self.odometer_read_date_time = ''
        # The general categorization type of vehicle as categorized by the utility's asset management standards and practices. Note: (1) Vehicle model is defined by VehicleAssetModel, and (2) Specific people and organizations and their roles relative to this vehicle may be determined by the inherited Asset-ErpPerson and Asset-Organization associations. Values are: "crew", "other", "user", "contractor"
        self.usage_kind = 'crew'
        
        self._vehicle_asset_model = None
        self.vehicle_asset_model = vehicle_asset_model
        self._crew = None
        self.crew = crew

        super(Vehicle, self).__init__(**kw_args)
    # >>> vehicle
        
    # <<< vehicle_asset_model
    # @generated
    def get_vehicle_asset_model(self):
        """ 
        """
        return self._vehicle_asset_model

    def set_vehicle_asset_model(self, value):
        if self._vehicle_asset_model is not None:
            filtered = [x for x in self.vehicle_asset_model.vehicles if x != self]
            self._vehicle_asset_model._vehicles = filtered
            
        self._vehicle_asset_model = value
        if self._vehicle_asset_model is not None:
            self._vehicle_asset_model._vehicles.append(self)

    vehicle_asset_model = property(get_vehicle_asset_model, set_vehicle_asset_model)
    # >>> vehicle_asset_model

    # <<< crew
    # @generated
    def get_crew(self):
        """ 
        """
        return self._crew

    def set_crew(self, value):
        if self._crew is not None:
            filtered = [x for x in self.crew.vehicles if x != self]
            self._crew._vehicles = filtered
            
        self._crew = value
        if self._crew is not None:
            self._crew._vehicles.append(self)

    crew = property(get_crew, set_crew)
    # >>> crew



class AssetPropertyCurve(Curve):
    """ An Asset Property that is described through curves rather than as a data point. The relationship is to be defined between an independent variable (X-axis) and one or two dependent variables (Y1-axis and Y2-axis).
    """
    # <<< asset_property_curve
    # @generated
    def __init__(self, assets=[], specification=None, **kw_args):
        """ Initialises a new 'AssetPropertyCurve' instance.
        """
        
        self._assets = []
        self.assets = assets
        self._specification = None
        self.specification = specification

        super(AssetPropertyCurve, self).__init__(**kw_args)
    # >>> asset_property_curve
        
    # <<< assets
    # @generated
    def get_assets(self):
        """ 
        """
        return self._assets

    def set_assets(self, value):
        for p in self._assets:
            filtered = [q for q in p.asset_property_curves if q != self]
            self._assets._asset_property_curves = filtered
        for r in value:
            if self not in r._asset_property_curves:
                r._asset_property_curves.append(self)
        self._assets = value
            
    assets = property(get_assets, set_assets)
    
    def add_assets(self, *assets):
        for obj in assets:
            if self not in obj._asset_property_curves:
                obj._asset_property_curves.append(self)
            self._assets.append(obj)
        
    def remove_assets(self, *assets):
        for obj in assets:
            if self in obj._asset_property_curves:
                obj._asset_property_curves.remove(self)
            self._assets.remove(obj)
    # >>> assets

    # <<< specification
    # @generated
    def get_specification(self):
        """ 
        """
        return self._specification

    def set_specification(self, value):
        if self._specification is not None:
            filtered = [x for x in self.specification.asset_property_curves if x != self]
            self._specification._asset_property_curves = filtered
            
        self._specification = value
        if self._specification is not None:
            self._specification._asset_property_curves.append(self)

    specification = property(get_specification, set_specification)
    # >>> specification



class Tool(Asset):
    """ Utility asset typically used by utility resources like crews and persons. As is the case for other assets, tools must be maintained.
    """
    # <<< tool
    # @generated
    def __init__(self, last_calibration_date='', crew=None, tool_asset_model=None, **kw_args):
        """ Initialises a new 'Tool' instance.
        """
        # Date the tool was last caibrated, if applicable. 
        self.last_calibration_date = ''
        
        self._crew = None
        self.crew = crew
        self._tool_asset_model = None
        self.tool_asset_model = tool_asset_model

        super(Tool, self).__init__(**kw_args)
    # >>> tool
        
    # <<< crew
    # @generated
    def get_crew(self):
        """ 
        """
        return self._crew

    def set_crew(self, value):
        if self._crew is not None:
            filtered = [x for x in self.crew.tools if x != self]
            self._crew._tools = filtered
            
        self._crew = value
        if self._crew is not None:
            self._crew._tools.append(self)

    crew = property(get_crew, set_crew)
    # >>> crew

    # <<< tool_asset_model
    # @generated
    def get_tool_asset_model(self):
        """ 
        """
        return self._tool_asset_model

    def set_tool_asset_model(self, value):
        if self._tool_asset_model is not None:
            filtered = [x for x in self.tool_asset_model.tools if x != self]
            self._tool_asset_model._tools = filtered
            
        self._tool_asset_model = value
        if self._tool_asset_model is not None:
            self._tool_asset_model._tools.append(self)

    tool_asset_model = property(get_tool_asset_model, set_tool_asset_model)
    # >>> tool_asset_model



class FailureEvent(ActivityRecord):
    """ An event where an asset has failed to perform its functions within specified parameters.
    """
    # <<< failure_event
    # @generated
    def __init__(self, fault_locating_method='', failure_isolation_method='manually_isolated', corporate_code='', location='', **kw_args):
        """ Initialises a new 'FailureEvent' instance.
        """
        # The method used for locating the faulted part of the asset. For example, cable options include: Cap Discharge-Thumping, Bridge Method, Visual Inspection, Other. 
        self.fault_locating_method = ''
        # How the asset failure was isolated from the system. Values are: "manually_isolated", "other", "breaker_operation", "fuse", "burned_in_the_clear"
        self.failure_isolation_method = 'manually_isolated'
        # Code for asset failure. 
        self.corporate_code = ''
        # Failure location on an object. 
        self.location = ''
        

        super(FailureEvent, self).__init__(**kw_args)
    # >>> failure_event
        


class TransformerObservation(IdentifiedObject):
    """ Common information captured during transformer inspections and/or diagrnotics. Note that some properties may be measured through other means and therefore have measurement values in addition to the observed values recorded here.
    """
    # <<< transformer_observation
    # @generated
    def __init__(self, freq_resp='', top_oil_temp='', bushing_temp='', hot_spot_temp='', oil_dielectric_strength='', dga='', pump_vibration='', furfural_dp='', water_content='', oil_neutralization_number='', oil_level='', oil_color='', oil_ift='', status=None, procedure_data_sets=[], winding_insulation_pfs=[], bushing_insultation_pfs=[], transformer_asset=None, winding_tests=[], **kw_args):
        """ Initialises a new 'TransformerObservation' instance.
        """
        # Frequency Response Analysis. Typical values are: acceptable, slight movement, significant movement, failed, near failure. A graphic of the response diagram, which is a type of document, may be associated with this analysis through the recursive document relationship of the ProcedureDataSet. 
        self.freq_resp = ''
        # Top oil temperature. 
        self.top_oil_temp = ''
        # Bushing temperature. 
        self.bushing_temp = ''
        # Hotspot oil temperature. 
        self.hot_spot_temp = ''
        # Oil Quality Analysis-Dielectric Strength. 
        self.oil_dielectric_strength = ''
        # Dissolved Gas Analysis. Typical values are: Acceptable, Overheating, Corona, Sparking, Arcing. 
        self.dga = ''
        # Pump vibration, with typical values being: nominal, high. 
        self.pump_vibration = ''
        # Overall measure of furfural in oil and mechanical strength of paper. DP, the degree of polymerization, is the strength of the paper. Furfural is a measure of furfural compounds, often expressed in parts per million. 
        self.furfural_dp = ''
        # Water Content expressed in parts per million. 
        self.water_content = ''
        # Oil Quality Analysis-Neutralization Number - Number - Mg KOH. 
        self.oil_neutralization_number = ''
        # The level of oil in the transformer. 
        self.oil_level = ''
        # Oil Quality Analysis-Color. 
        self.oil_color = ''
        # Oil Quality Analysis- inter facial tension (IFT) - number-Dynes/CM. 
        self.oil_ift = ''
        
        self.status = status
        self._procedure_data_sets = []
        self.procedure_data_sets = procedure_data_sets
        self._winding_insulation_pfs = []
        self.winding_insulation_pfs = winding_insulation_pfs
        self._bushing_insultation_pfs = []
        self.bushing_insultation_pfs = bushing_insultation_pfs
        self._transformer_asset = None
        self.transformer_asset = transformer_asset
        self._winding_tests = []
        self.winding_tests = winding_tests

        super(TransformerObservation, self).__init__(**kw_args)
    # >>> transformer_observation
        
    # <<< status
    # @generated
    status = None
    # >>> status

    # <<< procedure_data_sets
    # @generated
    def get_procedure_data_sets(self):
        """ 
        """
        return self._procedure_data_sets

    def set_procedure_data_sets(self, value):
        for p in self._procedure_data_sets:
            filtered = [q for q in p.transformer_observations if q != self]
            self._procedure_data_sets._transformer_observations = filtered
        for r in value:
            if self not in r._transformer_observations:
                r._transformer_observations.append(self)
        self._procedure_data_sets = value
            
    procedure_data_sets = property(get_procedure_data_sets, set_procedure_data_sets)
    
    def add_procedure_data_sets(self, *procedure_data_sets):
        for obj in procedure_data_sets:
            if self not in obj._transformer_observations:
                obj._transformer_observations.append(self)
            self._procedure_data_sets.append(obj)
        
    def remove_procedure_data_sets(self, *procedure_data_sets):
        for obj in procedure_data_sets:
            if self in obj._transformer_observations:
                obj._transformer_observations.remove(self)
            self._procedure_data_sets.remove(obj)
    # >>> procedure_data_sets

    # <<< winding_insulation_pfs
    # @generated
    def get_winding_insulation_pfs(self):
        """ 
        """
        return self._winding_insulation_pfs

    def set_winding_insulation_pfs(self, value):
        for x in self._winding_insulation_pfs:
            x._transformer_observation = None
        for y in value:
            y._transformer_observation = self
        self._winding_insulation_pfs = value
            
    winding_insulation_pfs = property(get_winding_insulation_pfs, set_winding_insulation_pfs)
    
    def add_winding_insulation_pfs(self, *winding_insulation_pfs):
        for obj in winding_insulation_pfs:
            obj._transformer_observation = self
            self._winding_insulation_pfs.append(obj)
        
    def remove_winding_insulation_pfs(self, *winding_insulation_pfs):
        for obj in winding_insulation_pfs:
            obj._transformer_observation = None
            self._winding_insulation_pfs.remove(obj)
    # >>> winding_insulation_pfs

    # <<< bushing_insultation_pfs
    # @generated
    def get_bushing_insultation_pfs(self):
        """ 
        """
        return self._bushing_insultation_pfs

    def set_bushing_insultation_pfs(self, value):
        for x in self._bushing_insultation_pfs:
            x._transformer_observation = None
        for y in value:
            y._transformer_observation = self
        self._bushing_insultation_pfs = value
            
    bushing_insultation_pfs = property(get_bushing_insultation_pfs, set_bushing_insultation_pfs)
    
    def add_bushing_insultation_pfs(self, *bushing_insultation_pfs):
        for obj in bushing_insultation_pfs:
            obj._transformer_observation = self
            self._bushing_insultation_pfs.append(obj)
        
    def remove_bushing_insultation_pfs(self, *bushing_insultation_pfs):
        for obj in bushing_insultation_pfs:
            obj._transformer_observation = None
            self._bushing_insultation_pfs.remove(obj)
    # >>> bushing_insultation_pfs

    # <<< transformer_asset
    # @generated
    def get_transformer_asset(self):
        """ 
        """
        return self._transformer_asset

    def set_transformer_asset(self, value):
        if self._transformer_asset is not None:
            filtered = [x for x in self.transformer_asset.transformer_observations if x != self]
            self._transformer_asset._transformer_observations = filtered
            
        self._transformer_asset = value
        if self._transformer_asset is not None:
            self._transformer_asset._transformer_observations.append(self)

    transformer_asset = property(get_transformer_asset, set_transformer_asset)
    # >>> transformer_asset

    # <<< winding_tests
    # @generated
    def get_winding_tests(self):
        """ 
        """
        return self._winding_tests

    def set_winding_tests(self, value):
        for p in self._winding_tests:
            filtered = [q for q in p.transformer_observations if q != self]
            self._winding_tests._transformer_observations = filtered
        for r in value:
            if self not in r._transformer_observations:
                r._transformer_observations.append(self)
        self._winding_tests = value
            
    winding_tests = property(get_winding_tests, set_winding_tests)
    
    def add_winding_tests(self, *winding_tests):
        for obj in winding_tests:
            if self not in obj._transformer_observations:
                obj._transformer_observations.append(self)
            self._winding_tests.append(obj)
        
    def remove_winding_tests(self, *winding_tests):
        for obj in winding_tests:
            if self in obj._transformer_observations:
                obj._transformer_observations.remove(self)
            self._winding_tests.remove(obj)
    # >>> winding_tests



class CompositeSwitchInfo(IdentifiedObject):
    """ Properties of a composite switch.
    """
    # <<< composite_switch_info
    # @generated
    def __init__(self, phase_count=0, gang=False, remote=False, rated_voltage='', interrupting_rating='', phase_code='abn', switch_state_count=0, init_op_mode='', composite_switch_type_asset=None, composite_switch_asset_model=None, composite_switch_assets=[], **kw_args):
        """ Initialises a new 'CompositeSwitchInfo' instance.
        """
        # Supported number of phases, typically 0, 1 or 3. 
        self.phase_count = 0
        # True if multi-phase switch controls all phases concurrently. 
        self.gang = False
        # True if device is capable of being operated by remote control. 
        self.remote = False
        # Rated voltage. 
        self.rated_voltage = ''
        # Breaking capacity, or short circuit rating, is the maximum rated current which the device can safely interrupt at the rated voltage. 
        self.interrupting_rating = ''
        # Phases carried, if applicable. Values are: "abn", "bc", "acn", "bn", "ac", "abc", "an", "ab", "c", "b", "abcn", "a", "cn", "n", "bcn"
        self.phase_code = 'abn'
        # Number of switch states represented by the composite switch. 
        self.switch_state_count = 0
        # Initial operating mode, with the following values: Automatic, Manual. 
        self.init_op_mode = ''
        
        self._composite_switch_type_asset = None
        self.composite_switch_type_asset = composite_switch_type_asset
        self._composite_switch_asset_model = None
        self.composite_switch_asset_model = composite_switch_asset_model
        self._composite_switch_assets = []
        self.composite_switch_assets = composite_switch_assets

        super(CompositeSwitchInfo, self).__init__(**kw_args)
    # >>> composite_switch_info
        
    # <<< composite_switch_type_asset
    # @generated
    def get_composite_switch_type_asset(self):
        """ 
        """
        return self._composite_switch_type_asset

    def set_composite_switch_type_asset(self, value):
        if self._composite_switch_type_asset is not None:
            self._composite_switch_type_asset._composite_switch_info = None
            
        self._composite_switch_type_asset = value
        if self._composite_switch_type_asset is not None:
            self._composite_switch_type_asset._composite_switch_info = self
            
    composite_switch_type_asset = property(get_composite_switch_type_asset, set_composite_switch_type_asset)
    # >>> composite_switch_type_asset

    # <<< composite_switch_asset_model
    # @generated
    def get_composite_switch_asset_model(self):
        """ 
        """
        return self._composite_switch_asset_model

    def set_composite_switch_asset_model(self, value):
        if self._composite_switch_asset_model is not None:
            self._composite_switch_asset_model._composite_switch_info = None
            
        self._composite_switch_asset_model = value
        if self._composite_switch_asset_model is not None:
            self._composite_switch_asset_model._composite_switch_info = self
            
    composite_switch_asset_model = property(get_composite_switch_asset_model, set_composite_switch_asset_model)
    # >>> composite_switch_asset_model

    # <<< composite_switch_assets
    # @generated
    def get_composite_switch_assets(self):
        """ 
        """
        return self._composite_switch_assets

    def set_composite_switch_assets(self, value):
        for x in self._composite_switch_assets:
            x._composite_switch_info = None
        for y in value:
            y._composite_switch_info = self
        self._composite_switch_assets = value
            
    composite_switch_assets = property(get_composite_switch_assets, set_composite_switch_assets)
    
    def add_composite_switch_assets(self, *composite_switch_assets):
        for obj in composite_switch_assets:
            obj._composite_switch_info = self
            self._composite_switch_assets.append(obj)
        
    def remove_composite_switch_assets(self, *composite_switch_assets):
        for obj in composite_switch_assets:
            obj._composite_switch_info = None
            self._composite_switch_assets.remove(obj)
    # >>> composite_switch_assets



class AssetPsrRole(Role):
    """ Roles played between Assets and Power System Resources.
    """
    # <<< asset_psr_role
    # @generated
    def __init__(self, asset=None, power_system_resource=None, **kw_args):
        """ Initialises a new 'AssetPsrRole' instance.
        """
        
        self._asset = None
        self.asset = asset
        self._power_system_resource = None
        self.power_system_resource = power_system_resource

        super(AssetPsrRole, self).__init__(**kw_args)
    # >>> asset_psr_role
        
    # <<< asset
    # @generated
    def get_asset(self):
        """ 
        """
        return self._asset

    def set_asset(self, value):
        if self._asset is not None:
            filtered = [x for x in self.asset.power_system_resource_roles if x != self]
            self._asset._power_system_resource_roles = filtered
            
        self._asset = value
        if self._asset is not None:
            self._asset._power_system_resource_roles.append(self)

    asset = property(get_asset, set_asset)
    # >>> asset

    # <<< power_system_resource
    # @generated
    def get_power_system_resource(self):
        """ 
        """
        return self._power_system_resource

    def set_power_system_resource(self, value):
        if self._power_system_resource is not None:
            filtered = [x for x in self.power_system_resource.asset_roles if x != self]
            self._power_system_resource._asset_roles = filtered
            
        self._power_system_resource = value
        if self._power_system_resource is not None:
            self._power_system_resource._asset_roles.append(self)

    power_system_resource = property(get_power_system_resource, set_power_system_resource)
    # >>> power_system_resource



class PSRStatus(IdentifiedObject):
    """ The current status of the PowerSystemResource. History of the status is tracked through instances of ActivityRecord.
    """
    # <<< psrstatus
    # @generated
    def __init__(self, status=None, power_system_resource=None, **kw_args):
        """ Initialises a new 'PSRStatus' instance.
        """
        
        self.status = status
        self._power_system_resource = None
        self.power_system_resource = power_system_resource

        super(PSRStatus, self).__init__(**kw_args)
    # >>> psrstatus
        
    # <<< status
    # @generated
    status = None
    # >>> status

    # <<< power_system_resource
    # @generated
    def get_power_system_resource(self):
        """ 
        """
        return self._power_system_resource

    def set_power_system_resource(self, value):
        if self._power_system_resource is not None:
            self._power_system_resource._psrstatus = None
            
        self._power_system_resource = value
        if self._power_system_resource is not None:
            self._power_system_resource._psrstatus = self
            
    power_system_resource = property(get_power_system_resource, set_power_system_resource)
    # >>> power_system_resource



class WorkEquipmentAsset(Asset):
    """ Various equipment used to perform units of work by crews, office staff, etc.
    """
    # <<< work_equipment_asset
    # @generated
    def __init__(self, crew=None, work_equipment_asset_model=None, usages=[], **kw_args):
        """ Initialises a new 'WorkEquipmentAsset' instance.
        """
        
        self._crew = None
        self.crew = crew
        self._work_equipment_asset_model = None
        self.work_equipment_asset_model = work_equipment_asset_model
        self._usages = []
        self.usages = usages

        super(WorkEquipmentAsset, self).__init__(**kw_args)
    # >>> work_equipment_asset
        
    # <<< crew
    # @generated
    def get_crew(self):
        """ 
        """
        return self._crew

    def set_crew(self, value):
        if self._crew is not None:
            filtered = [x for x in self.crew.work_equipment_assets if x != self]
            self._crew._work_equipment_assets = filtered
            
        self._crew = value
        if self._crew is not None:
            self._crew._work_equipment_assets.append(self)

    crew = property(get_crew, set_crew)
    # >>> crew

    # <<< work_equipment_asset_model
    # @generated
    def get_work_equipment_asset_model(self):
        """ 
        """
        return self._work_equipment_asset_model

    def set_work_equipment_asset_model(self, value):
        if self._work_equipment_asset_model is not None:
            filtered = [x for x in self.work_equipment_asset_model.work_equipment_assets if x != self]
            self._work_equipment_asset_model._work_equipment_assets = filtered
            
        self._work_equipment_asset_model = value
        if self._work_equipment_asset_model is not None:
            self._work_equipment_asset_model._work_equipment_assets.append(self)

    work_equipment_asset_model = property(get_work_equipment_asset_model, set_work_equipment_asset_model)
    # >>> work_equipment_asset_model

    # <<< usages
    # @generated
    def get_usages(self):
        """ 
        """
        return self._usages

    def set_usages(self, value):
        for x in self._usages:
            x._work_equipment_asset = None
        for y in value:
            y._work_equipment_asset = self
        self._usages = value
            
    usages = property(get_usages, set_usages)
    
    def add_usages(self, *usages):
        for obj in usages:
            obj._work_equipment_asset = self
            self._usages.append(obj)
        
    def remove_usages(self, *usages):
        for obj in usages:
            obj._work_equipment_asset = None
            self._usages.remove(obj)
    # >>> usages



class DuctBank(Asset):
    """ A duct bank may contain many ducts. Each duct contains individual lines that are expressed as linear assets (thereby describing each line's physical asset characteristics), which are each associated with ACLineSegments and other classes describing their electrical characteristics.
    """
    # <<< duct_bank
    # @generated
    def __init__(self, duct_count=0, circuit_count=0, duct_band_type_asset=None, cable_assets=[], **kw_args):
        """ Initialises a new 'DuctBank' instance.
        """
        # Number of ducts in duct bank. 
        self.duct_count = 0
        # Number of circuits in duct bank. Refer to associations between a duct (LinearAsset) and an ACLineSegment to understand which circuits are in which ducts. 
        self.circuit_count = 0
        
        self._duct_band_type_asset = None
        self.duct_band_type_asset = duct_band_type_asset
        self._cable_assets = []
        self.cable_assets = cable_assets

        super(DuctBank, self).__init__(**kw_args)
    # >>> duct_bank
        
    # <<< duct_band_type_asset
    # @generated
    def get_duct_band_type_asset(self):
        """ 
        """
        return self._duct_band_type_asset

    def set_duct_band_type_asset(self, value):
        if self._duct_band_type_asset is not None:
            filtered = [x for x in self.duct_band_type_asset.duct_banks if x != self]
            self._duct_band_type_asset._duct_banks = filtered
            
        self._duct_band_type_asset = value
        if self._duct_band_type_asset is not None:
            self._duct_band_type_asset._duct_banks.append(self)

    duct_band_type_asset = property(get_duct_band_type_asset, set_duct_band_type_asset)
    # >>> duct_band_type_asset

    # <<< cable_assets
    # @generated
    def get_cable_assets(self):
        """ 
        """
        return self._cable_assets

    def set_cable_assets(self, value):
        for p in self._cable_assets:
            filtered = [q for q in p.duct_banks if q != self]
            self._cable_assets._duct_banks = filtered
        for r in value:
            if self not in r._duct_banks:
                r._duct_banks.append(self)
        self._cable_assets = value
            
    cable_assets = property(get_cable_assets, set_cable_assets)
    
    def add_cable_assets(self, *cable_assets):
        for obj in cable_assets:
            if self not in obj._duct_banks:
                obj._duct_banks.append(self)
            self._cable_assets.append(obj)
        
    def remove_cable_assets(self, *cable_assets):
        for obj in cable_assets:
            if self in obj._duct_banks:
                obj._duct_banks.remove(self)
            self._cable_assets.remove(obj)
    # >>> cable_assets



class WindingInfo(Element):
    """ Details on winding. For example, to express winding code 'DYn11', set attributes as follows: 'windingConnectionKind' = Yn and 'phaseAngle' = 11.
    """
    # <<< winding_info
    # @generated
    def __init__(self, winding_connection_kind='zn', phase_angle=0, **kw_args):
        """ Initialises a new 'WindingInfo' instance.
        """
        # Kind of winding connection. Values are: "zn", "y", "d", "z", "yn"
        self.winding_connection_kind = 'zn'
        # Winding phase angle where 360 degrees are represented with clock hours, so the valid values are {0, ..., 11}. 
        self.phase_angle = 0
        

        super(WindingInfo, self).__init__(**kw_args)
    # >>> winding_info
        


class FinancialInfo(IdentifiedObject):
    """ Various current financial properties associated with a particular asset. Historical properties may be determined by ActivityRecords associated with the asset.
    """
    # <<< financial_info
    # @generated
    def __init__(self, quantity='', plant_transfer_date='', warranty_date='', purchase_order_number='', cost_description='', actual_purchase_cost='', financial_value='', cost_type='', account='', value_date='', purchase_date='', asset=None, **kw_args):
        """ Initialises a new 'FinancialInfo' instance.
        """
        # The quantity of the asset if per unit length, for example conductor. 
        self.quantity = ''
        # Date asset's financial value was put in plant for regulatory accounting purposes (e.g., for rate base calculations). This is sometime referred to as the 'in-service date.' 
        self.plant_transfer_date = ''
        # Date warranty on asset expires. 
        self.warranty_date = ''
        # Purchase order identifier. 
        self.purchase_order_number = ''
        # Description of the cost. 
        self.cost_description = ''
        # The actual purchase cost of this particular asset. 
        self.actual_purchase_cost = ''
        # Value of asset as of 'valueDate'. 
        self.financial_value = ''
        # The category of cost to which this Material Item belongs. 
        self.cost_type = ''
        # The account to which this actual material item is charged. 
        self.account = ''
        # The date and time at which the financial value was last established. 
        self.value_date = ''
        # Date asset was purchased. 
        self.purchase_date = ''
        
        self._asset = None
        self.asset = asset

        super(FinancialInfo, self).__init__(**kw_args)
    # >>> financial_info
        
    # <<< asset
    # @generated
    def get_asset(self):
        """ 
        """
        return self._asset

    def set_asset(self, value):
        if self._asset is not None:
            self._asset._financial_info = None
            
        self._asset = value
        if self._asset is not None:
            self._asset._financial_info = self
            
    asset = property(get_asset, set_asset)
    # >>> asset



class Anchor(StructureSupport):
    """ A type of support for structures, used to hold poles secure.
    """
    # <<< anchor
    # @generated
    def __init__(self, kind='unknown', **kw_args):
        """ Initialises a new 'Anchor' instance.
        """
        # Kind of this anchor. Values are: "unknown", "multi_helix", "other", "rod", "concrete", "helix", "screw"
        self.kind = 'unknown'
        

        super(Anchor, self).__init__(**kw_args)
    # >>> anchor
        


class PotentialTransformerAsset(ElectricalAsset):
    """ Physical asset performing Potential Transformer (PT) function.
    """
    # <<< potential_transformer_asset
    # @generated
    def __init__(self, potential_transformer_info=None, potential_transformer=None, potential_transformer_asset_model=None, **kw_args):
        """ Initialises a new 'PotentialTransformerAsset' instance.
        """
        
        self._potential_transformer_info = None
        self.potential_transformer_info = potential_transformer_info
        self._potential_transformer = None
        self.potential_transformer = potential_transformer
        self._potential_transformer_asset_model = None
        self.potential_transformer_asset_model = potential_transformer_asset_model

        super(PotentialTransformerAsset, self).__init__(**kw_args)
    # >>> potential_transformer_asset
        
    # <<< potential_transformer_info
    # @generated
    def get_potential_transformer_info(self):
        """ 
        """
        return self._potential_transformer_info

    def set_potential_transformer_info(self, value):
        if self._potential_transformer_info is not None:
            filtered = [x for x in self.potential_transformer_info.potential_transformer_assets if x != self]
            self._potential_transformer_info._potential_transformer_assets = filtered
            
        self._potential_transformer_info = value
        if self._potential_transformer_info is not None:
            self._potential_transformer_info._potential_transformer_assets.append(self)

    potential_transformer_info = property(get_potential_transformer_info, set_potential_transformer_info)
    # >>> potential_transformer_info

    # <<< potential_transformer
    # @generated
    def get_potential_transformer(self):
        """ 
        """
        return self._potential_transformer

    def set_potential_transformer(self, value):
        if self._potential_transformer is not None:
            self._potential_transformer._potential_transformer_asset = None
            
        self._potential_transformer = value
        if self._potential_transformer is not None:
            self._potential_transformer._potential_transformer_asset = self
            
    potential_transformer = property(get_potential_transformer, set_potential_transformer)
    # >>> potential_transformer

    # <<< potential_transformer_asset_model
    # @generated
    def get_potential_transformer_asset_model(self):
        """ 
        """
        return self._potential_transformer_asset_model

    def set_potential_transformer_asset_model(self, value):
        if self._potential_transformer_asset_model is not None:
            filtered = [x for x in self.potential_transformer_asset_model.potential_transformer_assets if x != self]
            self._potential_transformer_asset_model._potential_transformer_assets = filtered
            
        self._potential_transformer_asset_model = value
        if self._potential_transformer_asset_model is not None:
            self._potential_transformer_asset_model._potential_transformer_assets.append(self)

    potential_transformer_asset_model = property(get_potential_transformer_asset_model, set_potential_transformer_asset_model)
    # >>> potential_transformer_asset_model



class RecloserAsset(ElectricalAsset):
    """ Physical recloser performing a reclosing function, which is modeled through Breaker.
    """
    # <<< recloser_asset
    # @generated
    def __init__(self, recloser_asset_model=None, recloser_info=None, **kw_args):
        """ Initialises a new 'RecloserAsset' instance.
        """
        
        self._recloser_asset_model = None
        self.recloser_asset_model = recloser_asset_model
        self._recloser_info = None
        self.recloser_info = recloser_info

        super(RecloserAsset, self).__init__(**kw_args)
    # >>> recloser_asset
        
    # <<< recloser_asset_model
    # @generated
    def get_recloser_asset_model(self):
        """ 
        """
        return self._recloser_asset_model

    def set_recloser_asset_model(self, value):
        if self._recloser_asset_model is not None:
            filtered = [x for x in self.recloser_asset_model.recloser_assets if x != self]
            self._recloser_asset_model._recloser_assets = filtered
            
        self._recloser_asset_model = value
        if self._recloser_asset_model is not None:
            self._recloser_asset_model._recloser_assets.append(self)

    recloser_asset_model = property(get_recloser_asset_model, set_recloser_asset_model)
    # >>> recloser_asset_model

    # <<< recloser_info
    # @generated
    def get_recloser_info(self):
        """ 
        """
        return self._recloser_info

    def set_recloser_info(self, value):
        if self._recloser_info is not None:
            filtered = [x for x in self.recloser_info.recloser_assets if x != self]
            self._recloser_info._recloser_assets = filtered
            
        self._recloser_info = value
        if self._recloser_info is not None:
            self._recloser_info._recloser_assets.append(self)

    recloser_info = property(get_recloser_info, set_recloser_info)
    # >>> recloser_info



class CurrentTransformerAsset(ElectricalAsset):
    """ Physical asset performing Current Transformer (CT) function.
    """
    # <<< current_transformer_asset
    # @generated
    def __init__(self, type_ct='', current_transformer=None, current_transformer_info=None, current_transformer_asset_model=None, **kw_args):
        """ Initialises a new 'CurrentTransformerAsset' instance.
        """
        # Type of CT as categorized by the utility's asset management standards and practices. 
        self.type_ct = ''
        
        self._current_transformer = None
        self.current_transformer = current_transformer
        self._current_transformer_info = None
        self.current_transformer_info = current_transformer_info
        self._current_transformer_asset_model = None
        self.current_transformer_asset_model = current_transformer_asset_model

        super(CurrentTransformerAsset, self).__init__(**kw_args)
    # >>> current_transformer_asset
        
    # <<< current_transformer
    # @generated
    def get_current_transformer(self):
        """ 
        """
        return self._current_transformer

    def set_current_transformer(self, value):
        if self._current_transformer is not None:
            self._current_transformer._current_transformer_asset = None
            
        self._current_transformer = value
        if self._current_transformer is not None:
            self._current_transformer._current_transformer_asset = self
            
    current_transformer = property(get_current_transformer, set_current_transformer)
    # >>> current_transformer

    # <<< current_transformer_info
    # @generated
    def get_current_transformer_info(self):
        """ 
        """
        return self._current_transformer_info

    def set_current_transformer_info(self, value):
        if self._current_transformer_info is not None:
            filtered = [x for x in self.current_transformer_info.current_transformer_assets if x != self]
            self._current_transformer_info._current_transformer_assets = filtered
            
        self._current_transformer_info = value
        if self._current_transformer_info is not None:
            self._current_transformer_info._current_transformer_assets.append(self)

    current_transformer_info = property(get_current_transformer_info, set_current_transformer_info)
    # >>> current_transformer_info

    # <<< current_transformer_asset_model
    # @generated
    def get_current_transformer_asset_model(self):
        """ 
        """
        return self._current_transformer_asset_model

    def set_current_transformer_asset_model(self, value):
        if self._current_transformer_asset_model is not None:
            filtered = [x for x in self.current_transformer_asset_model.current_transformer_assets if x != self]
            self._current_transformer_asset_model._current_transformer_assets = filtered
            
        self._current_transformer_asset_model = value
        if self._current_transformer_asset_model is not None:
            self._current_transformer_asset_model._current_transformer_assets.append(self)

    current_transformer_asset_model = property(get_current_transformer_asset_model, set_current_transformer_asset_model)
    # >>> current_transformer_asset_model



class Streetlight(ElectricalAsset):
    """ Streetlight asset.
    """
    # <<< streetlight
    # @generated
    def __init__(self, arm_length='', lamp_kind='high_pressure_sodium', light_rating='', attached_to_pole=None, streetlight_asset_model=None, **kw_args):
        """ Initialises a new 'Streetlight' instance.
        """
        # Length of arm of this specific asset. Note that a new light may be placed on an existing arm. 
        self.arm_length = ''
        # Lamp kind currently installed. Values are: "high_pressure_sodium", "other", "mercury_vapor", "metal_halide"
        self.lamp_kind = 'high_pressure_sodium'
        # Actual power rating of light. 
        self.light_rating = ''
        
        self._attached_to_pole = None
        self.attached_to_pole = attached_to_pole
        self._streetlight_asset_model = None
        self.streetlight_asset_model = streetlight_asset_model

        super(Streetlight, self).__init__(**kw_args)
    # >>> streetlight
        
    # <<< attached_to_pole
    # @generated
    def get_attached_to_pole(self):
        """ Streetlight(s) may be attached to a pole.
        """
        return self._attached_to_pole

    def set_attached_to_pole(self, value):
        if self._attached_to_pole is not None:
            filtered = [x for x in self.attached_to_pole.support_streetlights if x != self]
            self._attached_to_pole._support_streetlights = filtered
            
        self._attached_to_pole = value
        if self._attached_to_pole is not None:
            self._attached_to_pole._support_streetlights.append(self)

    attached_to_pole = property(get_attached_to_pole, set_attached_to_pole)
    # >>> attached_to_pole

    # <<< streetlight_asset_model
    # @generated
    def get_streetlight_asset_model(self):
        """ 
        """
        return self._streetlight_asset_model

    def set_streetlight_asset_model(self, value):
        if self._streetlight_asset_model is not None:
            filtered = [x for x in self.streetlight_asset_model.streetlights if x != self]
            self._streetlight_asset_model._streetlights = filtered
            
        self._streetlight_asset_model = value
        if self._streetlight_asset_model is not None:
            self._streetlight_asset_model._streetlights.append(self)

    streetlight_asset_model = property(get_streetlight_asset_model, set_streetlight_asset_model)
    # >>> streetlight_asset_model



class LinearConductorAsset(ElectricalAsset):
    """ Physical asset used to perform the conductor's role.
    """
    # <<< linear_conductor_asset
    # @generated
    def __init__(self, is_horizontal=False, insulated=False, grounding_method='', circuit_section=None, conductor_type=None, linear_conductor_type_asset=None, conductors=[], linear_conductor_asset_model=None, **kw_args):
        """ Initialises a new 'LinearConductorAsset' instance.
        """
        # True when orientation is horizontal (e.g., transmission and distribution lines), false if vertical (e.g. a riser for underground to overhead service). 
        self.is_horizontal = False
        # True if linear asset has an insulator around the core material. 
        self.insulated = False
        # Description of the method used for grounding the linear conductor. For a cable, the grounding/bonding shield may be multi-point, single-point, cross cable, or other. 
        self.grounding_method = ''
        
        self._circuit_section = None
        self.circuit_section = circuit_section
        self._conductor_type = None
        self.conductor_type = conductor_type
        self._linear_conductor_type_asset = None
        self.linear_conductor_type_asset = linear_conductor_type_asset
        self._conductors = []
        self.conductors = conductors
        self._linear_conductor_asset_model = None
        self.linear_conductor_asset_model = linear_conductor_asset_model

        super(LinearConductorAsset, self).__init__(**kw_args)
    # >>> linear_conductor_asset
        
    # <<< circuit_section
    # @generated
    def get_circuit_section(self):
        """ 
        """
        return self._circuit_section

    def set_circuit_section(self, value):
        if self._circuit_section is not None:
            filtered = [x for x in self.circuit_section.linear_conductors if x != self]
            self._circuit_section._linear_conductors = filtered
            
        self._circuit_section = value
        if self._circuit_section is not None:
            self._circuit_section._linear_conductors.append(self)

    circuit_section = property(get_circuit_section, set_circuit_section)
    # >>> circuit_section

    # <<< conductor_type
    # @generated
    def get_conductor_type(self):
        """ 
        """
        return self._conductor_type

    def set_conductor_type(self, value):
        if self._conductor_type is not None:
            filtered = [x for x in self.conductor_type.linear_conductor_assets if x != self]
            self._conductor_type._linear_conductor_assets = filtered
            
        self._conductor_type = value
        if self._conductor_type is not None:
            self._conductor_type._linear_conductor_assets.append(self)

    conductor_type = property(get_conductor_type, set_conductor_type)
    # >>> conductor_type

    # <<< linear_conductor_type_asset
    # @generated
    def get_linear_conductor_type_asset(self):
        """ 
        """
        return self._linear_conductor_type_asset

    def set_linear_conductor_type_asset(self, value):
        if self._linear_conductor_type_asset is not None:
            filtered = [x for x in self.linear_conductor_type_asset.linear_conductor_assets if x != self]
            self._linear_conductor_type_asset._linear_conductor_assets = filtered
            
        self._linear_conductor_type_asset = value
        if self._linear_conductor_type_asset is not None:
            self._linear_conductor_type_asset._linear_conductor_assets.append(self)

    linear_conductor_type_asset = property(get_linear_conductor_type_asset, set_linear_conductor_type_asset)
    # >>> linear_conductor_type_asset

    # <<< conductors
    # @generated
    def get_conductors(self):
        """ 
        """
        return self._conductors

    def set_conductors(self, value):
        for p in self._conductors:
            filtered = [q for q in p.linear_conductor_assets if q != self]
            self._conductors._linear_conductor_assets = filtered
        for r in value:
            if self not in r._linear_conductor_assets:
                r._linear_conductor_assets.append(self)
        self._conductors = value
            
    conductors = property(get_conductors, set_conductors)
    
    def add_conductors(self, *conductors):
        for obj in conductors:
            if self not in obj._linear_conductor_assets:
                obj._linear_conductor_assets.append(self)
            self._conductors.append(obj)
        
    def remove_conductors(self, *conductors):
        for obj in conductors:
            if self in obj._linear_conductor_assets:
                obj._linear_conductor_assets.remove(self)
            self._conductors.remove(obj)
    # >>> conductors

    # <<< linear_conductor_asset_model
    # @generated
    def get_linear_conductor_asset_model(self):
        """ 
        """
        return self._linear_conductor_asset_model

    def set_linear_conductor_asset_model(self, value):
        if self._linear_conductor_asset_model is not None:
            filtered = [x for x in self.linear_conductor_asset_model.linear_conductor_assets if x != self]
            self._linear_conductor_asset_model._linear_conductor_assets = filtered
            
        self._linear_conductor_asset_model = value
        if self._linear_conductor_asset_model is not None:
            self._linear_conductor_asset_model._linear_conductor_assets.append(self)

    linear_conductor_asset_model = property(get_linear_conductor_asset_model, set_linear_conductor_asset_model)
    # >>> linear_conductor_asset_model



class ProtectionEquipmentAsset(ElectricalAsset):
    """ Physical asset performing ProtectionEquipment function.
    """
    # <<< protection_equipment_asset
    # @generated
    def __init__(self, phase_trip='', ground_trip='', protection_equipment_asset_model=None, **kw_args):
        """ Initialises a new 'ProtectionEquipmentAsset' instance.
        """
        # Actual phase trip for this type of relay, if applicable. 
        self.phase_trip = ''
        # Actual ground trip for this type of relay, if applicable. 
        self.ground_trip = ''
        
        self._protection_equipment_asset_model = None
        self.protection_equipment_asset_model = protection_equipment_asset_model

        super(ProtectionEquipmentAsset, self).__init__(**kw_args)
    # >>> protection_equipment_asset
        
    # <<< protection_equipment_asset_model
    # @generated
    def get_protection_equipment_asset_model(self):
        """ 
        """
        return self._protection_equipment_asset_model

    def set_protection_equipment_asset_model(self, value):
        if self._protection_equipment_asset_model is not None:
            filtered = [x for x in self.protection_equipment_asset_model.protection_equipment_assets if x != self]
            self._protection_equipment_asset_model._protection_equipment_assets = filtered
            
        self._protection_equipment_asset_model = value
        if self._protection_equipment_asset_model is not None:
            self._protection_equipment_asset_model._protection_equipment_assets.append(self)

    protection_equipment_asset_model = property(get_protection_equipment_asset_model, set_protection_equipment_asset_model)
    # >>> protection_equipment_asset_model



class Pole(Structure):
    """ A long, slender piece of wood, metal, etc. usually rounded that stands vertically from the ground and is used for mounting various types of overhead equipment. Dimensions of Pole are specified in associated DimensionsInfo.
    """
    # <<< pole
    # @generated
    def __init__(self, base_kind='dirt', treatment_kind='green_stain', breast_block=False, jpa_reference='', treated_date='', preservative_kind='penta', construction='', support_streetlights=[], pole_model=None, **kw_args):
        """ Initialises a new 'Pole' instance.
        """
        # Kind of base for this pole. Values are: "dirt", "cement", "other", "asphalt", "unknown"
        self.base_kind = 'dirt'
        # Kind of treatment for this pole. Values are: "green_stain", "gray_stain", "other", "natural", "butt", "full", "unknown", "penta"
        self.treatment_kind = 'green_stain'
        # True if a block of material has been attached to base of pole in ground for stability. This technique is used primarily when anchors can not be used. 
        self.breast_block = False
        # Joint pole agreement reference number. 
        self.jpa_reference = ''
        # Date pole was last treated with preservative. 
        self.treated_date = ''
        # Kind of preservative for this pole. Values are: "penta", "other", "creosote", "chemonite", "cellon", "unknown", "naphthena"
        self.preservative_kind = 'penta'
        # The framing structure mounted on the pole. 
        self.construction = ''
        
        self._support_streetlights = []
        self.support_streetlights = support_streetlights
        self._pole_model = None
        self.pole_model = pole_model

        super(Pole, self).__init__(**kw_args)
    # >>> pole
        
    # <<< support_streetlights
    # @generated
    def get_support_streetlights(self):
        """ Streetlight(s) may be attached to a pole.
        """
        return self._support_streetlights

    def set_support_streetlights(self, value):
        for x in self._support_streetlights:
            x._attached_to_pole = None
        for y in value:
            y._attached_to_pole = self
        self._support_streetlights = value
            
    support_streetlights = property(get_support_streetlights, set_support_streetlights)
    
    def add_support_streetlights(self, *support_streetlights):
        for obj in support_streetlights:
            obj._attached_to_pole = self
            self._support_streetlights.append(obj)
        
    def remove_support_streetlights(self, *support_streetlights):
        for obj in support_streetlights:
            obj._attached_to_pole = None
            self._support_streetlights.remove(obj)
    # >>> support_streetlights

    # <<< pole_model
    # @generated
    def get_pole_model(self):
        """ 
        """
        return self._pole_model

    def set_pole_model(self, value):
        if self._pole_model is not None:
            filtered = [x for x in self.pole_model.poles if x != self]
            self._pole_model._poles = filtered
            
        self._pole_model = value
        if self._pole_model is not None:
            self._pole_model._poles.append(self)

    pole_model = property(get_pole_model, set_pole_model)
    # >>> pole_model



class CableAsset(LinearConductorAsset):
    """ Insultated physical cable for performing the Conductor role used in undergrond and other applications..
    """
    # <<< cable_asset
    # @generated
    def __init__(self, duct_bank_type_asset=None, duct_banks=[], **kw_args):
        """ Initialises a new 'CableAsset' instance.
        """
        
        self._duct_bank_type_asset = None
        self.duct_bank_type_asset = duct_bank_type_asset
        self._duct_banks = []
        self.duct_banks = duct_banks

        super(CableAsset, self).__init__(**kw_args)
    # >>> cable_asset
        
    # <<< duct_bank_type_asset
    # @generated
    def get_duct_bank_type_asset(self):
        """ 
        """
        return self._duct_bank_type_asset

    def set_duct_bank_type_asset(self, value):
        if self._duct_bank_type_asset is not None:
            filtered = [x for x in self.duct_bank_type_asset.cable_assets if x != self]
            self._duct_bank_type_asset._cable_assets = filtered
            
        self._duct_bank_type_asset = value
        if self._duct_bank_type_asset is not None:
            self._duct_bank_type_asset._cable_assets.append(self)

    duct_bank_type_asset = property(get_duct_bank_type_asset, set_duct_bank_type_asset)
    # >>> duct_bank_type_asset

    # <<< duct_banks
    # @generated
    def get_duct_banks(self):
        """ 
        """
        return self._duct_banks

    def set_duct_banks(self, value):
        for p in self._duct_banks:
            filtered = [q for q in p.cable_assets if q != self]
            self._duct_banks._cable_assets = filtered
        for r in value:
            if self not in r._cable_assets:
                r._cable_assets.append(self)
        self._duct_banks = value
            
    duct_banks = property(get_duct_banks, set_duct_banks)
    
    def add_duct_banks(self, *duct_banks):
        for obj in duct_banks:
            if self not in obj._cable_assets:
                obj._cable_assets.append(self)
            self._duct_banks.append(obj)
        
    def remove_duct_banks(self, *duct_banks):
        for obj in duct_banks:
            if self in obj._cable_assets:
                obj._cable_assets.remove(self)
            self._duct_banks.remove(obj)
    # >>> duct_banks



class FACTSDeviceAsset(ElectricalAsset):
    """ Physical asset used to perform the FACTSDevice's role.
    """
    # <<< factsdevice_asset
    # @generated
    def __init__(self, kind='tsbr', factsdevice_asset_model=None, **kw_args):
        """ Initialises a new 'FACTSDeviceAsset' instance.
        """
        # Kind of FACTS device. Values are: "tsbr", "tssc", "svc", "statcom", "tcvl", "tcpar", "tcsc", "upfc"
        self.kind = 'tsbr'
        
        self._factsdevice_asset_model = None
        self.factsdevice_asset_model = factsdevice_asset_model

        super(FACTSDeviceAsset, self).__init__(**kw_args)
    # >>> factsdevice_asset
        
    # <<< factsdevice_asset_model
    # @generated
    def get_factsdevice_asset_model(self):
        """ 
        """
        return self._factsdevice_asset_model

    def set_factsdevice_asset_model(self, value):
        if self._factsdevice_asset_model is not None:
            filtered = [x for x in self.factsdevice_asset_model.factsdevice_assets if x != self]
            self._factsdevice_asset_model._factsdevice_assets = filtered
            
        self._factsdevice_asset_model = value
        if self._factsdevice_asset_model is not None:
            self._factsdevice_asset_model._factsdevice_assets.append(self)

    factsdevice_asset_model = property(get_factsdevice_asset_model, set_factsdevice_asset_model)
    # >>> factsdevice_asset_model



class TestDataSet(ProcedureDataSet):
    """ Test results, usually obtained by a lab or other independent organisation.
    """
    # <<< test_data_set
    # @generated
    def __init__(self, conclusion='', spec_to_lab_date='', specimen_id='', conclusion_date='', **kw_args):
        """ Initialises a new 'TestDataSet' instance.
        """
        # Conclusion drawn from test results. 
        self.conclusion = ''
        # The date the specimen was received by the lab. 
        self.spec_to_lab_date = ''
        # Identifier of specimen used in inspection or test. 
        self.specimen_id = ''
        # The date the conclusion form the test was issued by the lab. 
        self.conclusion_date = ''
        

        super(TestDataSet, self).__init__(**kw_args)
    # >>> test_data_set
        


class FaultIndicatorAsset(ElectricalAsset):
    """ Physical asset performing FaultIndicator function.
    """
    # <<< fault_indicator_asset
    # @generated
    def __init__(self, fault_indicator_asset_model=None, fault_indicator=None, **kw_args):
        """ Initialises a new 'FaultIndicatorAsset' instance.
        """
        
        self._fault_indicator_asset_model = None
        self.fault_indicator_asset_model = fault_indicator_asset_model
        self._fault_indicator = None
        self.fault_indicator = fault_indicator

        super(FaultIndicatorAsset, self).__init__(**kw_args)
    # >>> fault_indicator_asset
        
    # <<< fault_indicator_asset_model
    # @generated
    def get_fault_indicator_asset_model(self):
        """ 
        """
        return self._fault_indicator_asset_model

    def set_fault_indicator_asset_model(self, value):
        if self._fault_indicator_asset_model is not None:
            filtered = [x for x in self.fault_indicator_asset_model.fault_indicator_assets if x != self]
            self._fault_indicator_asset_model._fault_indicator_assets = filtered
            
        self._fault_indicator_asset_model = value
        if self._fault_indicator_asset_model is not None:
            self._fault_indicator_asset_model._fault_indicator_assets.append(self)

    fault_indicator_asset_model = property(get_fault_indicator_asset_model, set_fault_indicator_asset_model)
    # >>> fault_indicator_asset_model

    # <<< fault_indicator
    # @generated
    def get_fault_indicator(self):
        """ 
        """
        return self._fault_indicator

    def set_fault_indicator(self, value):
        if self._fault_indicator is not None:
            filtered = [x for x in self.fault_indicator.fault_indicator_assets if x != self]
            self._fault_indicator._fault_indicator_assets = filtered
            
        self._fault_indicator = value
        if self._fault_indicator is not None:
            self._fault_indicator._fault_indicator_assets.append(self)

    fault_indicator = property(get_fault_indicator, set_fault_indicator)
    # >>> fault_indicator



class TransformerAsset(ElectricalAsset):
    """ A specific physical (vs. logical) transformer.
    """
    # <<< transformer_asset
    # @generated
    def __init__(self, day_over_load_rating='', alt_secondary_nom_voltage='', function='autotransformer', reconfig_winding=False, alt_primary_nom_voltage='', construction_kind='padmount_dead_front', hour_over_load_rating='', reconditioned_date_time='', transformer_observations=[], transformer_asset_model=None, power_ratings=[], transformer_info=None, **kw_args):
        """ Initialises a new 'TransformerAsset' instance.
        """
        # 24-hour overload rating. 
        self.day_over_load_rating = ''
        # Nominal voltage rating for alternate configuration for secondary winding. 
        self.alt_secondary_nom_voltage = ''
        # Function of this transformer. Values are: "autotransformer", "other", "secondary_transformer", "voltage_regulator", "power_transformer"
        self.function = 'autotransformer'
        # True if windings can be re-configured to result in a different input or output voltage. 
        self.reconfig_winding = False
        # Nominal voltage rating for alternate configuration for primary winding. 
        self.alt_primary_nom_voltage = ''
        # Kind of construction for this transformer. Values are: "padmount_dead_front", "padmounted", "valut", "one_phase", "network", "underground", "subway", "overhead", "three_phase", "padmount_live_front", "padmount_loop_through", "padmount_feed_through", "dry_type", "aerial", "vault_three_phase", "unknown"
        self.construction_kind = 'padmount_dead_front'
        # 1-hour overload rating. 
        self.hour_over_load_rating = ''
        # Date and time this asset was last reconditioned or had a major overhaul. 
        self.reconditioned_date_time = ''
        
        self._transformer_observations = []
        self.transformer_observations = transformer_observations
        self._transformer_asset_model = None
        self.transformer_asset_model = transformer_asset_model
        self._power_ratings = []
        self.power_ratings = power_ratings
        self._transformer_info = None
        self.transformer_info = transformer_info

        super(TransformerAsset, self).__init__(**kw_args)
    # >>> transformer_asset
        
    # <<< transformer_observations
    # @generated
    def get_transformer_observations(self):
        """ 
        """
        return self._transformer_observations

    def set_transformer_observations(self, value):
        for x in self._transformer_observations:
            x._transformer_asset = None
        for y in value:
            y._transformer_asset = self
        self._transformer_observations = value
            
    transformer_observations = property(get_transformer_observations, set_transformer_observations)
    
    def add_transformer_observations(self, *transformer_observations):
        for obj in transformer_observations:
            obj._transformer_asset = self
            self._transformer_observations.append(obj)
        
    def remove_transformer_observations(self, *transformer_observations):
        for obj in transformer_observations:
            obj._transformer_asset = None
            self._transformer_observations.remove(obj)
    # >>> transformer_observations

    # <<< transformer_asset_model
    # @generated
    def get_transformer_asset_model(self):
        """ 
        """
        return self._transformer_asset_model

    def set_transformer_asset_model(self, value):
        if self._transformer_asset_model is not None:
            filtered = [x for x in self.transformer_asset_model.transformer_assets if x != self]
            self._transformer_asset_model._transformer_assets = filtered
            
        self._transformer_asset_model = value
        if self._transformer_asset_model is not None:
            self._transformer_asset_model._transformer_assets.append(self)

    transformer_asset_model = property(get_transformer_asset_model, set_transformer_asset_model)
    # >>> transformer_asset_model

    # <<< power_ratings
    # @generated
    def get_power_ratings(self):
        """ 
        """
        return self._power_ratings

    def set_power_ratings(self, value):
        for p in self._power_ratings:
            filtered = [q for q in p.transformer_assets if q != self]
            self._power_ratings._transformer_assets = filtered
        for r in value:
            if self not in r._transformer_assets:
                r._transformer_assets.append(self)
        self._power_ratings = value
            
    power_ratings = property(get_power_ratings, set_power_ratings)
    
    def add_power_ratings(self, *power_ratings):
        for obj in power_ratings:
            if self not in obj._transformer_assets:
                obj._transformer_assets.append(self)
            self._power_ratings.append(obj)
        
    def remove_power_ratings(self, *power_ratings):
        for obj in power_ratings:
            if self in obj._transformer_assets:
                obj._transformer_assets.remove(self)
            self._power_ratings.remove(obj)
    # >>> power_ratings

    # <<< transformer_info
    # @generated
    def get_transformer_info(self):
        """ 
        """
        return self._transformer_info

    def set_transformer_info(self, value):
        if self._transformer_info is not None:
            filtered = [x for x in self.transformer_info.transformer_assets if x != self]
            self._transformer_info._transformer_assets = filtered
            
        self._transformer_info = value
        if self._transformer_info is not None:
            self._transformer_info._transformer_assets.append(self)

    transformer_info = property(get_transformer_info, set_transformer_info)
    # >>> transformer_info



class OverheadConductorAsset(LinearConductorAsset):
    """ Physical conductor performing the Conductor role that is used in overhead applications.
    """
    # <<< overhead_conductor_asset
    # @generated
    def __init__(self, mounting_point=None, **kw_args):
        """ Initialises a new 'OverheadConductorAsset' instance.
        """
        
        self._mounting_point = None
        self.mounting_point = mounting_point

        super(OverheadConductorAsset, self).__init__(**kw_args)
    # >>> overhead_conductor_asset
        
    # <<< mounting_point
    # @generated
    def get_mounting_point(self):
        """ 
        """
        return self._mounting_point

    def set_mounting_point(self, value):
        if self._mounting_point is not None:
            filtered = [x for x in self.mounting_point.overhead_conductors if x != self]
            self._mounting_point._overhead_conductors = filtered
            
        self._mounting_point = value
        if self._mounting_point is not None:
            self._mounting_point._overhead_conductors.append(self)

    mounting_point = property(get_mounting_point, set_mounting_point)
    # >>> mounting_point



class BusbarAsset(ElectricalAsset):
    """ Physical asset used to perform the BusbarSection's role.
    """
    # <<< busbar_asset
    # @generated
    def __init__(self, busbar_asset_model=None, **kw_args):
        """ Initialises a new 'BusbarAsset' instance.
        """
        
        self._busbar_asset_model = None
        self.busbar_asset_model = busbar_asset_model

        super(BusbarAsset, self).__init__(**kw_args)
    # >>> busbar_asset
        
    # <<< busbar_asset_model
    # @generated
    def get_busbar_asset_model(self):
        """ 
        """
        return self._busbar_asset_model

    def set_busbar_asset_model(self, value):
        if self._busbar_asset_model is not None:
            filtered = [x for x in self.busbar_asset_model.busbar_assets if x != self]
            self._busbar_asset_model._busbar_assets = filtered
            
        self._busbar_asset_model = value
        if self._busbar_asset_model is not None:
            self._busbar_asset_model._busbar_assets.append(self)

    busbar_asset_model = property(get_busbar_asset_model, set_busbar_asset_model)
    # >>> busbar_asset_model



class SeriesCompensatorAsset(ElectricalAsset):
    """ For a a series capacitor or reactor, this is the physical asset performing the SeriesCompensator role (PSR).
    """
    # <<< series_compensator_asset
    # @generated
    def __init__(self, series_compensator_asset_model=None, **kw_args):
        """ Initialises a new 'SeriesCompensatorAsset' instance.
        """
        
        self._series_compensator_asset_model = None
        self.series_compensator_asset_model = series_compensator_asset_model

        super(SeriesCompensatorAsset, self).__init__(**kw_args)
    # >>> series_compensator_asset
        
    # <<< series_compensator_asset_model
    # @generated
    def get_series_compensator_asset_model(self):
        """ 
        """
        return self._series_compensator_asset_model

    def set_series_compensator_asset_model(self, value):
        if self._series_compensator_asset_model is not None:
            self._series_compensator_asset_model._series_compensator_asset = None
            
        self._series_compensator_asset_model = value
        if self._series_compensator_asset_model is not None:
            self._series_compensator_asset_model._series_compensator_asset = self
            
    series_compensator_asset_model = property(get_series_compensator_asset_model, set_series_compensator_asset_model)
    # >>> series_compensator_asset_model



class SurgeProtectorAsset(ElectricalAsset):
    """ Physical asset performing SurgeProtector function.
    """
    # <<< surge_protector_asset
    # @generated
    def __init__(self, surge_protector=None, surge_protector_asset_model=None, **kw_args):
        """ Initialises a new 'SurgeProtectorAsset' instance.
        """
        
        self._surge_protector = None
        self.surge_protector = surge_protector
        self._surge_protector_asset_model = None
        self.surge_protector_asset_model = surge_protector_asset_model

        super(SurgeProtectorAsset, self).__init__(**kw_args)
    # >>> surge_protector_asset
        
    # <<< surge_protector
    # @generated
    def get_surge_protector(self):
        """ 
        """
        return self._surge_protector

    def set_surge_protector(self, value):
        if self._surge_protector is not None:
            self._surge_protector._surge_protector_asset = None
            
        self._surge_protector = value
        if self._surge_protector is not None:
            self._surge_protector._surge_protector_asset = self
            
    surge_protector = property(get_surge_protector, set_surge_protector)
    # >>> surge_protector

    # <<< surge_protector_asset_model
    # @generated
    def get_surge_protector_asset_model(self):
        """ 
        """
        return self._surge_protector_asset_model

    def set_surge_protector_asset_model(self, value):
        if self._surge_protector_asset_model is not None:
            filtered = [x for x in self.surge_protector_asset_model.surge_protector_assets if x != self]
            self._surge_protector_asset_model._surge_protector_assets = filtered
            
        self._surge_protector_asset_model = value
        if self._surge_protector_asset_model is not None:
            self._surge_protector_asset_model._surge_protector_assets.append(self)

    surge_protector_asset_model = property(get_surge_protector_asset_model, set_surge_protector_asset_model)
    # >>> surge_protector_asset_model



class UndergroundStructure(Structure):
    """ Abstract class for underground structures. Typical structure types are: BURD, Enclosure, Hand Hole, Manhole, Pad/Slab, Subsurface Enclosure, Trench, Tunnel, Vault, Pull/Splice Box.
    """
    # <<< underground_structure
    # @generated
    def __init__(self, sealing_date='', sealing_warranty_expires='', material='', ventilation=False, **kw_args):
        """ Initialises a new 'UndergroundStructure' instance.
        """
        # Date vault was sealed. 
        self.sealing_date = ''
        # Date sealing warranty expires. 
        self.sealing_warranty_expires = ''
        # Primary material of underground structure. 
        self.material = ''
        # True if vault is ventilating. 
        self.ventilation = False
        

        super(UndergroundStructure, self).__init__(**kw_args)
    # >>> underground_structure
        


class GeneratorAsset(ElectricalAsset):
    """ Physical asset performing the Generator role.
    """
    # <<< generator_asset
    # @generated
    def __init__(self, generator_asset_model=None, **kw_args):
        """ Initialises a new 'GeneratorAsset' instance.
        """
        
        self._generator_asset_model = None
        self.generator_asset_model = generator_asset_model

        super(GeneratorAsset, self).__init__(**kw_args)
    # >>> generator_asset
        
    # <<< generator_asset_model
    # @generated
    def get_generator_asset_model(self):
        """ 
        """
        return self._generator_asset_model

    def set_generator_asset_model(self, value):
        if self._generator_asset_model is not None:
            filtered = [x for x in self.generator_asset_model.generator_assets if x != self]
            self._generator_asset_model._generator_assets = filtered
            
        self._generator_asset_model = value
        if self._generator_asset_model is not None:
            self._generator_asset_model._generator_assets.append(self)

    generator_asset_model = property(get_generator_asset_model, set_generator_asset_model)
    # >>> generator_asset_model



class BreakerInfo(SwitchInfo):
    """ Properties of breakers.
    """
    # <<< breaker_info
    # @generated
    def __init__(self, phase_trip='', breaker_asset_models=[], breaker_assets=[], breaker_type_asset=None, **kw_args):
        """ Initialises a new 'BreakerInfo' instance.
        """
        # Phase trip rating. 
        self.phase_trip = ''
        
        self._breaker_asset_models = []
        self.breaker_asset_models = breaker_asset_models
        self._breaker_assets = []
        self.breaker_assets = breaker_assets
        self._breaker_type_asset = None
        self.breaker_type_asset = breaker_type_asset

        super(BreakerInfo, self).__init__(**kw_args)
    # >>> breaker_info
        
    # <<< breaker_asset_models
    # @generated
    def get_breaker_asset_models(self):
        """ 
        """
        return self._breaker_asset_models

    def set_breaker_asset_models(self, value):
        for x in self._breaker_asset_models:
            x._breaker_info = None
        for y in value:
            y._breaker_info = self
        self._breaker_asset_models = value
            
    breaker_asset_models = property(get_breaker_asset_models, set_breaker_asset_models)
    
    def add_breaker_asset_models(self, *breaker_asset_models):
        for obj in breaker_asset_models:
            obj._breaker_info = self
            self._breaker_asset_models.append(obj)
        
    def remove_breaker_asset_models(self, *breaker_asset_models):
        for obj in breaker_asset_models:
            obj._breaker_info = None
            self._breaker_asset_models.remove(obj)
    # >>> breaker_asset_models

    # <<< breaker_assets
    # @generated
    def get_breaker_assets(self):
        """ 
        """
        return self._breaker_assets

    def set_breaker_assets(self, value):
        for x in self._breaker_assets:
            x._breaker_info = None
        for y in value:
            y._breaker_info = self
        self._breaker_assets = value
            
    breaker_assets = property(get_breaker_assets, set_breaker_assets)
    
    def add_breaker_assets(self, *breaker_assets):
        for obj in breaker_assets:
            obj._breaker_info = self
            self._breaker_assets.append(obj)
        
    def remove_breaker_assets(self, *breaker_assets):
        for obj in breaker_assets:
            obj._breaker_info = None
            self._breaker_assets.remove(obj)
    # >>> breaker_assets

    # <<< breaker_type_asset
    # @generated
    def get_breaker_type_asset(self):
        """ 
        """
        return self._breaker_type_asset

    def set_breaker_type_asset(self, value):
        if self._breaker_type_asset is not None:
            self._breaker_type_asset._breaker_info = None
            
        self._breaker_type_asset = value
        if self._breaker_type_asset is not None:
            self._breaker_type_asset._breaker_info = self
            
    breaker_type_asset = property(get_breaker_type_asset, set_breaker_type_asset)
    # >>> breaker_type_asset



class RecloserInfo(SwitchInfo):
    """ Properties of reclosers.
    """
    # <<< recloser_info
    # @generated
    def __init__(self, ground_trip_normal_enabled=False, phase_trip_rating='', ground_trip_capable=False, ground_trip_rating='', reclose_lockout_count=0, recloser_assets=[], recloser_asset_models=[], recloser_type_asset=None, **kw_args):
        """ Initialises a new 'RecloserInfo' instance.
        """
        # True if normal status of ground trip is enabled. 
        self.ground_trip_normal_enabled = False
        # Phase trip rating. 
        self.phase_trip_rating = ''
        # True if device has ground trip capability. 
        self.ground_trip_capable = False
        # Ground trip rating. 
        self.ground_trip_rating = ''
        # Total number of phase reclose operations. 
        self.reclose_lockout_count = 0
        
        self._recloser_assets = []
        self.recloser_assets = recloser_assets
        self._recloser_asset_models = []
        self.recloser_asset_models = recloser_asset_models
        self._recloser_type_asset = None
        self.recloser_type_asset = recloser_type_asset

        super(RecloserInfo, self).__init__(**kw_args)
    # >>> recloser_info
        
    # <<< recloser_assets
    # @generated
    def get_recloser_assets(self):
        """ 
        """
        return self._recloser_assets

    def set_recloser_assets(self, value):
        for x in self._recloser_assets:
            x._recloser_info = None
        for y in value:
            y._recloser_info = self
        self._recloser_assets = value
            
    recloser_assets = property(get_recloser_assets, set_recloser_assets)
    
    def add_recloser_assets(self, *recloser_assets):
        for obj in recloser_assets:
            obj._recloser_info = self
            self._recloser_assets.append(obj)
        
    def remove_recloser_assets(self, *recloser_assets):
        for obj in recloser_assets:
            obj._recloser_info = None
            self._recloser_assets.remove(obj)
    # >>> recloser_assets

    # <<< recloser_asset_models
    # @generated
    def get_recloser_asset_models(self):
        """ 
        """
        return self._recloser_asset_models

    def set_recloser_asset_models(self, value):
        for x in self._recloser_asset_models:
            x._recloser_info = None
        for y in value:
            y._recloser_info = self
        self._recloser_asset_models = value
            
    recloser_asset_models = property(get_recloser_asset_models, set_recloser_asset_models)
    
    def add_recloser_asset_models(self, *recloser_asset_models):
        for obj in recloser_asset_models:
            obj._recloser_info = self
            self._recloser_asset_models.append(obj)
        
    def remove_recloser_asset_models(self, *recloser_asset_models):
        for obj in recloser_asset_models:
            obj._recloser_info = None
            self._recloser_asset_models.remove(obj)
    # >>> recloser_asset_models

    # <<< recloser_type_asset
    # @generated
    def get_recloser_type_asset(self):
        """ 
        """
        return self._recloser_type_asset

    def set_recloser_type_asset(self, value):
        if self._recloser_type_asset is not None:
            self._recloser_type_asset._recloser_info = None
            
        self._recloser_type_asset = value
        if self._recloser_type_asset is not None:
            self._recloser_type_asset._recloser_info = self
            
    recloser_type_asset = property(get_recloser_type_asset, set_recloser_type_asset)
    # >>> recloser_type_asset



class Tower(Structure):
    """ Large structure used to carry transmission lines, subtransmission lines, and/or other equipment/lines (e.g., communication). Dimensions of the Tower are specified in associated DimensionsInfo class.
    """
    # <<< tower
    # @generated
    def __init__(self, construction_kind='tension', tower_asset_model=None, **kw_args):
        """ Initialises a new 'Tower' instance.
        """
        # Construction structure on the tower. Values are: "tension", "suspension"
        self.construction_kind = 'tension'
        
        self._tower_asset_model = None
        self.tower_asset_model = tower_asset_model

        super(Tower, self).__init__(**kw_args)
    # >>> tower
        
    # <<< tower_asset_model
    # @generated
    def get_tower_asset_model(self):
        """ 
        """
        return self._tower_asset_model

    def set_tower_asset_model(self, value):
        if self._tower_asset_model is not None:
            filtered = [x for x in self.tower_asset_model.towers if x != self]
            self._tower_asset_model._towers = filtered
            
        self._tower_asset_model = value
        if self._tower_asset_model is not None:
            self._tower_asset_model._towers.append(self)

    tower_asset_model = property(get_tower_asset_model, set_tower_asset_model)
    # >>> tower_asset_model



class JointAsset(ElectricalAsset):
    """ Physical asset connecting two or more cable assets. It includes the portion of cable under wipes, welds, or other seals.
    """
    # <<< joint_asset
    # @generated
    def __init__(self, configuration_kind='wires3to1', fill_kind='no_void', insulation='', **kw_args):
        """ Initialises a new 'JointAsset' instance.
        """
        # Configuration of joint. Values are: "wires3to1", "wires1to1", "other", "wires2to1"
        self.configuration_kind = 'wires3to1'
        # Material used to fill the joint. Values are: "no_void", "no_fill_prefab", "other", "petrolatum", "insoluseal", "oil", "air_no_filling", "epoxy", "asphaltic", "bluefill254"
        self.fill_kind = 'no_void'
        # The type of insulation around the joint, classified according to the utility's asset management standards and practices. 
        self.insulation = ''
        

        super(JointAsset, self).__init__(**kw_args)
    # >>> joint_asset
        


class SwitchAsset(ElectricalAsset):
    """ Physical asset performing Switch function.
    """
    # <<< switch_asset
    # @generated
    def __init__(self, switch_info=None, switch_asset_model=None, **kw_args):
        """ Initialises a new 'SwitchAsset' instance.
        """
        
        self._switch_info = None
        self.switch_info = switch_info
        self._switch_asset_model = None
        self.switch_asset_model = switch_asset_model

        super(SwitchAsset, self).__init__(**kw_args)
    # >>> switch_asset
        
    # <<< switch_info
    # @generated
    def get_switch_info(self):
        """ 
        """
        return self._switch_info

    def set_switch_info(self, value):
        if self._switch_info is not None:
            filtered = [x for x in self.switch_info.switch_assets if x != self]
            self._switch_info._switch_assets = filtered
            
        self._switch_info = value
        if self._switch_info is not None:
            self._switch_info._switch_assets.append(self)

    switch_info = property(get_switch_info, set_switch_info)
    # >>> switch_info

    # <<< switch_asset_model
    # @generated
    def get_switch_asset_model(self):
        """ 
        """
        return self._switch_asset_model

    def set_switch_asset_model(self, value):
        if self._switch_asset_model is not None:
            filtered = [x for x in self.switch_asset_model.switch_assets if x != self]
            self._switch_asset_model._switch_assets = filtered
            
        self._switch_asset_model = value
        if self._switch_asset_model is not None:
            self._switch_asset_model._switch_assets.append(self)

    switch_asset_model = property(get_switch_asset_model, set_switch_asset_model)
    # >>> switch_asset_model



class BushingAsset(ElectricalAsset):
    """ Physical bushing that insulates and protects from abrasion a conductor that passes through it. It is associated with a specific Terminal, which is in turn associated with a ConductingEquipment.
    """
    # <<< bushing_asset
    # @generated
    def __init__(self, c1_power_factor=0.0, c1_capacitance='', c2_capacitance='', c2_power_factor=0.0, bushing_insulation_pfs=[], terminal=None, bushing_model=None, **kw_args):
        """ Initialises a new 'BushingAsset' instance.
        """
        # Factory Measured Insulation Power Factor measured between the Power Factor tap and the bushing conductor. 
        self.c1_power_factor = 0.0
        # Factory Measured Capacitance measured between the Power Factor tap and the bushing conductor. 
        self.c1_capacitance = ''
        # Factory Measured Capacitance measured between the Power Factor tap and ground. 
        self.c2_capacitance = ''
        # Factory Measured Insulation Power Factor measured between the Power Factor tap and ground. 
        self.c2_power_factor = 0.0
        
        self._bushing_insulation_pfs = []
        self.bushing_insulation_pfs = bushing_insulation_pfs
        self._terminal = None
        self.terminal = terminal
        self._bushing_model = None
        self.bushing_model = bushing_model

        super(BushingAsset, self).__init__(**kw_args)
    # >>> bushing_asset
        
    # <<< bushing_insulation_pfs
    # @generated
    def get_bushing_insulation_pfs(self):
        """ 
        """
        return self._bushing_insulation_pfs

    def set_bushing_insulation_pfs(self, value):
        for x in self._bushing_insulation_pfs:
            x._bushing_asset = None
        for y in value:
            y._bushing_asset = self
        self._bushing_insulation_pfs = value
            
    bushing_insulation_pfs = property(get_bushing_insulation_pfs, set_bushing_insulation_pfs)
    
    def add_bushing_insulation_pfs(self, *bushing_insulation_pfs):
        for obj in bushing_insulation_pfs:
            obj._bushing_asset = self
            self._bushing_insulation_pfs.append(obj)
        
    def remove_bushing_insulation_pfs(self, *bushing_insulation_pfs):
        for obj in bushing_insulation_pfs:
            obj._bushing_asset = None
            self._bushing_insulation_pfs.remove(obj)
    # >>> bushing_insulation_pfs

    # <<< terminal
    # @generated
    def get_terminal(self):
        """ 
        """
        return self._terminal

    def set_terminal(self, value):
        if self._terminal is not None:
            self._terminal._bushing_asset = None
            
        self._terminal = value
        if self._terminal is not None:
            self._terminal._bushing_asset = self
            
    terminal = property(get_terminal, set_terminal)
    # >>> terminal

    # <<< bushing_model
    # @generated
    def get_bushing_model(self):
        """ 
        """
        return self._bushing_model

    def set_bushing_model(self, value):
        if self._bushing_model is not None:
            self._bushing_model._bushing_asset = None
            
        self._bushing_model = value
        if self._bushing_model is not None:
            self._bushing_model._bushing_asset = self
            
    bushing_model = property(get_bushing_model, set_bushing_model)
    # >>> bushing_model



class BreakerAsset(ElectricalAsset):
    """ Physical asset performing Breaker role.
    """
    # <<< breaker_asset
    # @generated
    def __init__(self, breaker_asset_model=None, breaker_info=None, **kw_args):
        """ Initialises a new 'BreakerAsset' instance.
        """
        
        self._breaker_asset_model = None
        self.breaker_asset_model = breaker_asset_model
        self._breaker_info = None
        self.breaker_info = breaker_info

        super(BreakerAsset, self).__init__(**kw_args)
    # >>> breaker_asset
        
    # <<< breaker_asset_model
    # @generated
    def get_breaker_asset_model(self):
        """ 
        """
        return self._breaker_asset_model

    def set_breaker_asset_model(self, value):
        if self._breaker_asset_model is not None:
            filtered = [x for x in self.breaker_asset_model.breaker_assets if x != self]
            self._breaker_asset_model._breaker_assets = filtered
            
        self._breaker_asset_model = value
        if self._breaker_asset_model is not None:
            self._breaker_asset_model._breaker_assets.append(self)

    breaker_asset_model = property(get_breaker_asset_model, set_breaker_asset_model)
    # >>> breaker_asset_model

    # <<< breaker_info
    # @generated
    def get_breaker_info(self):
        """ 
        """
        return self._breaker_info

    def set_breaker_info(self, value):
        if self._breaker_info is not None:
            filtered = [x for x in self.breaker_info.breaker_assets if x != self]
            self._breaker_info._breaker_assets = filtered
            
        self._breaker_info = value
        if self._breaker_info is not None:
            self._breaker_info._breaker_assets.append(self)

    breaker_info = property(get_breaker_info, set_breaker_info)
    # >>> breaker_info



class ShuntCompensatorAsset(ElectricalAsset):
    """ For a shunt capacitor or reactor or switchable bank of shunt capacitors or reactors, this is the physical asset performing the ShuntCompensator role (PSR).
    """
    # <<< shunt_compensator_asset
    # @generated
    def __init__(self, shunt_compensator_asset_model=None, shunt_impedance_info=None, **kw_args):
        """ Initialises a new 'ShuntCompensatorAsset' instance.
        """
        
        self._shunt_compensator_asset_model = None
        self.shunt_compensator_asset_model = shunt_compensator_asset_model
        self._shunt_impedance_info = None
        self.shunt_impedance_info = shunt_impedance_info

        super(ShuntCompensatorAsset, self).__init__(**kw_args)
    # >>> shunt_compensator_asset
        
    # <<< shunt_compensator_asset_model
    # @generated
    def get_shunt_compensator_asset_model(self):
        """ 
        """
        return self._shunt_compensator_asset_model

    def set_shunt_compensator_asset_model(self, value):
        if self._shunt_compensator_asset_model is not None:
            filtered = [x for x in self.shunt_compensator_asset_model.shunt_compensator_assets if x != self]
            self._shunt_compensator_asset_model._shunt_compensator_assets = filtered
            
        self._shunt_compensator_asset_model = value
        if self._shunt_compensator_asset_model is not None:
            self._shunt_compensator_asset_model._shunt_compensator_assets.append(self)

    shunt_compensator_asset_model = property(get_shunt_compensator_asset_model, set_shunt_compensator_asset_model)
    # >>> shunt_compensator_asset_model

    # <<< shunt_impedance_info
    # @generated
    def get_shunt_impedance_info(self):
        """ 
        """
        return self._shunt_impedance_info

    def set_shunt_impedance_info(self, value):
        if self._shunt_impedance_info is not None:
            filtered = [x for x in self.shunt_impedance_info.shunt_compensator_assets if x != self]
            self._shunt_impedance_info._shunt_compensator_assets = filtered
            
        self._shunt_impedance_info = value
        if self._shunt_impedance_info is not None:
            self._shunt_impedance_info._shunt_compensator_assets.append(self)

    shunt_impedance_info = property(get_shunt_impedance_info, set_shunt_impedance_info)
    # >>> shunt_impedance_info



class SVCAsset(FACTSDeviceAsset):
    """ Physical asset performing StaticVarCompensator function.
    """
    # <<< svcasset
    # @generated
    def __init__(self, svc_info=None, svcasset_model=None, **kw_args):
        """ Initialises a new 'SVCAsset' instance.
        """
        
        self._svc_info = None
        self.svc_info = svc_info
        self._svcasset_model = None
        self.svcasset_model = svcasset_model

        super(SVCAsset, self).__init__(**kw_args)
    # >>> svcasset
        
    # <<< svc_info
    # @generated
    def get_svc_info(self):
        """ 
        """
        return self._svc_info

    def set_svc_info(self, value):
        if self._svc_info is not None:
            self._svc_info._svcasset = None
            
        self._svc_info = value
        if self._svc_info is not None:
            self._svc_info._svcasset = self
            
    svc_info = property(get_svc_info, set_svc_info)
    # >>> svc_info

    # <<< svcasset_model
    # @generated
    def get_svcasset_model(self):
        """ 
        """
        return self._svcasset_model

    def set_svcasset_model(self, value):
        if self._svcasset_model is not None:
            filtered = [x for x in self.svcasset_model.svcassets if x != self]
            self._svcasset_model._svcassets = filtered
            
        self._svcasset_model = value
        if self._svcasset_model is not None:
            self._svcasset_model._svcassets.append(self)

    svcasset_model = property(get_svcasset_model, set_svcasset_model)
    # >>> svcasset_model



class ResistorAsset(ElectricalAsset):
    """ Physical asset performing Resistor function.
    """
    # <<< resistor_asset
    # @generated
    def __init__(self, resistor=None, resistor_asset_model=None, **kw_args):
        """ Initialises a new 'ResistorAsset' instance.
        """
        
        self._resistor = None
        self.resistor = resistor
        self._resistor_asset_model = None
        self.resistor_asset_model = resistor_asset_model

        super(ResistorAsset, self).__init__(**kw_args)
    # >>> resistor_asset
        
    # <<< resistor
    # @generated
    def get_resistor(self):
        """ 
        """
        return self._resistor

    def set_resistor(self, value):
        if self._resistor is not None:
            self._resistor._resistor_asset = None
            
        self._resistor = value
        if self._resistor is not None:
            self._resistor._resistor_asset = self
            
    resistor = property(get_resistor, set_resistor)
    # >>> resistor

    # <<< resistor_asset_model
    # @generated
    def get_resistor_asset_model(self):
        """ 
        """
        return self._resistor_asset_model

    def set_resistor_asset_model(self, value):
        if self._resistor_asset_model is not None:
            filtered = [x for x in self.resistor_asset_model.resistor_assets if x != self]
            self._resistor_asset_model._resistor_assets = filtered
            
        self._resistor_asset_model = value
        if self._resistor_asset_model is not None:
            self._resistor_asset_model._resistor_assets.append(self)

    resistor_asset_model = property(get_resistor_asset_model, set_resistor_asset_model)
    # >>> resistor_asset_model



class Guy(StructureSupport):
    """ A type of support for structures.
    """
    pass
    # <<< guy
    # @generated
    def __init__(self, **kw_args):
        """ Initialises a new 'Guy' instance.
        """
        

        super(Guy, self).__init__(**kw_args)
    # >>> guy
        


class Manhole(UndergroundStructure):
    """ Provides access at key locations to underground cables, equipment, etc. housed inside a protective vault.
    """
    pass
    # <<< manhole
    # @generated
    def __init__(self, **kw_args):
        """ Initialises a new 'Manhole' instance.
        """
        

        super(Manhole, self).__init__(**kw_args)
    # >>> manhole
        


# <<< inf_assets
# @generated
# >>> inf_assets
