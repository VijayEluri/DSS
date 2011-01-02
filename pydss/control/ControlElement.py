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

from pydss.common.CircuitElement import CircuitElement

class ControlElement(CircuitElement):
    """Base for control classes.
    """

    def __init__(self, elementName='', elementTerminal=0, controlledBusName='',
            monitoredVariable='', monitoredVarIndex=0, timeDelay=0.0,
            dblTraceParam=0.0, controlledBus=None, *args, **kw_args):
        """Initialises a new 'ControlElement' instance.
        """
        self.elementName = elementName

        self.elementTerminal = elementTerminal

        self.controlledBusName = controlledBusName

        self.monitoredVariable = monitoredVariable

        self.monitoredVarIndex = monitoredVarIndex

        self.timeDelay = timeDelay

        self.dblTraceParam = dblTraceParam

        self.controlledBus = controlledBus

        super(ControlElement, self).__init__(*args, **kw_args)
