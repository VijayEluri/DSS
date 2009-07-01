# Copyright (C) 2009 Richard W. Lincoln
# All rights reserved.

from cim14.iec61968.common import Document

# <<< imports
# @generated
# >>> imports

ns_prefix = "cim.IEC61968.Work"

ns_uri = "http://iec.ch/TC57/2009/CIM-schema-cim14#IEC61968.Work"

class Work(Document):
    """ Document used to request, initiate, track and record work. This is synonymous with Work Breakdown Structure (WBS), which is traversed through the (currently informative) recursive association of Work. Note that the work name is equal to the WBS name, which is given in the inherited 'name' attribute.
    """
    # Kind of work. Values are: "disconnect", "service", "other", "maintenance", "meter", "inspection", "reconnect", "construction"
    kind = 'disconnect'

    # Priority of work. 
    priority = ''

    # Date and time work was requested. 
    request_date_time = ''

    def get_work_billing_info(self):
        """ 
        """
        return self._work_billing_info

    def set_work_billing_info(self, value):
        if self._work_billing_info is not None:
            filtered = [x for x in self.work_billing_info.works if x != self]
            self._work_billing_info._works = filtered
            
        self._work_billing_info = value
        if self._work_billing_info is not None:
            self._work_billing_info._works.append(self)

    work_billing_info = property(get_work_billing_info, set_work_billing_info)

    def get_project(self):
        """ 
        """
        return self._project

    def set_project(self, value):
        if self._project is not None:
            filtered = [x for x in self.project.works if x != self]
            self._project._works = filtered
            
        self._project = value
        if self._project is not None:
            self._project._works.append(self)

    project = property(get_project, set_project)

    def get_designs(self):
        """ 
        """
        return self._designs

    def set_designs(self, value):
        for x in self._designs:
            x._work = None
        for y in value:
            y._work = self
        self._designs = value
            
    designs = property(get_designs, set_designs)
    
    def add_designs(self, *designs):
        for obj in designs:
            obj._work = self
            self._designs.append(obj)
        
    def remove_designs(self, *designs):
        for obj in designs:
            obj._work = None
            self._designs.remove(obj)

    customers = []
    
    def add_customers(self, *customers):
        for obj in customers:
	        self._customers.append(obj)
        
    def remove_customers(self, *customers):
        for obj in customers:
	        self._customers.remove(obj)

    def get_work_flow_steps(self):
        """ 
        """
        return self._work_flow_steps

    def set_work_flow_steps(self, value):
        for x in self._work_flow_steps:
            x._work = None
        for y in value:
            y._work = self
        self._work_flow_steps = value
            
    work_flow_steps = property(get_work_flow_steps, set_work_flow_steps)
    
    def add_work_flow_steps(self, *work_flow_steps):
        for obj in work_flow_steps:
            obj._work = self
            self._work_flow_steps.append(obj)
        
    def remove_work_flow_steps(self, *work_flow_steps):
        for obj in work_flow_steps:
            obj._work = None
            self._work_flow_steps.remove(obj)

    def get_business_case(self):
        """ 
        """
        return self._business_case

    def set_business_case(self, value):
        if self._business_case is not None:
            filtered = [x for x in self.business_case.works if x != self]
            self._business_case._works = filtered
            
        self._business_case = value
        if self._business_case is not None:
            self._business_case._works.append(self)

    business_case = property(get_business_case, set_business_case)

    work_cost_details = []
    
    def add_work_cost_details(self, *work_cost_details):
        for obj in work_cost_details:
	        self._work_cost_details.append(obj)
        
    def remove_work_cost_details(self, *work_cost_details):
        for obj in work_cost_details:
	        self._work_cost_details.remove(obj)

    def get_request(self):
        """ 
        """
        return self._request

    def set_request(self, value):
        if self._request is not None:
            filtered = [x for x in self.request.works if x != self]
            self._request._works = filtered
            
        self._request = value
        if self._request is not None:
            self._request._works.append(self)

    request = property(get_request, set_request)

    def get_work_tasks(self):
        """ 
        """
        return self._work_tasks

    def set_work_tasks(self, value):
        for x in self._work_tasks:
            x._work = None
        for y in value:
            y._work = self
        self._work_tasks = value
            
    work_tasks = property(get_work_tasks, set_work_tasks)
    
    def add_work_tasks(self, *work_tasks):
        for obj in work_tasks:
            obj._work = self
            self._work_tasks.append(obj)
        
    def remove_work_tasks(self, *work_tasks):
        for obj in work_tasks:
            obj._work = None
            self._work_tasks.remove(obj)

    def get_erp_project_accounting(self):
        """ 
        """
        return self._erp_project_accounting

    def set_erp_project_accounting(self, value):
        if self._erp_project_accounting is not None:
            filtered = [x for x in self.erp_project_accounting.works if x != self]
            self._erp_project_accounting._works = filtered
            
        self._erp_project_accounting = value
        if self._erp_project_accounting is not None:
            self._erp_project_accounting._works.append(self)

    erp_project_accounting = property(get_erp_project_accounting, set_erp_project_accounting)

    # <<< work
    # @generated
    def __init__(self, kind='disconnect', priority='', request_date_time='', work_billing_info=None, project=None, designs=[], customers=[], work_flow_steps=[], business_case=None, work_cost_details=[], request=None, work_tasks=[], erp_project_accounting=None, **kw_args):
        """ Initialises a new 'Work' instance.
        """
        self.kind = kind
        self.priority = priority
        self.request_date_time = request_date_time
        self._work_billing_info = None
        self.work_billing_info = work_billing_info
        self._project = None
        self.project = project
        self._designs = []
        self.designs = designs
        self._customers = []
        self.customers = customers
        self._work_flow_steps = []
        self.work_flow_steps = work_flow_steps
        self._business_case = None
        self.business_case = business_case
        self._work_cost_details = []
        self.work_cost_details = work_cost_details
        self._request = None
        self.request = request
        self._work_tasks = []
        self.work_tasks = work_tasks
        self._erp_project_accounting = None
        self.erp_project_accounting = erp_project_accounting

        super(Work, self).__init__(**kw_args)
    # >>> work


# <<< work
# @generated
# >>> work
