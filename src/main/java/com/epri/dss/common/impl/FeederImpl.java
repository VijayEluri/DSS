package com.epri.dss.common.impl;

import com.epri.dss.common.Circuit;
import com.epri.dss.common.Feeder;
import com.epri.dss.common.FeederObj;
import com.epri.dss.conversion.impl.PCClassImpl;
import com.epri.dss.parser.impl.Parser;
import com.epri.dss.shared.impl.CommandListImpl;

public class FeederImpl extends PCClassImpl implements Feeder {

	public static int NumPropsThisClass = 0;

	public static FeederObj activeFeederObj;

	public FeederImpl() {
		super();
		className = "Feeder";
		classType = DSSClassDefs.FEEDER_ELEMENT; /*+ PC_ELEMENT; */ // add to PCElement list

		activeElement = -1;

		defineProperties();

		String[] commands = new String[numProperties];
		System.arraycopy(propertyName, 0, commands, 0, numProperties);
		commandList = new CommandListImpl(commands);
		commandList.setAbbrevAllowed(true);
	}

	protected void defineProperties() {
		NumPropsThisClass = 0;

		numProperties = NumPropsThisClass;
		countProperties();   // get inherited property count
		allocatePropertyArrays();

// Can't think of any properties we want the user to be able to set

		// Define Property names
//		PropertyName[0] = "bus1";

		// define Property help values
//		PropertyHelp[0] = "Name of bus to which source is connected."+DSSGlobals.CRLF+"bus1=busname"+DSSGlobals.CRLF+"bus1=busname.1.2.3";


		activeProperty = NumPropsThisClass - 1;
		super.defineProperties();  // Add defs of inherited properties to bottom of list
	}

	/**
	 * Called from EnergyMeter.
	 */
	@Override
	public int newObject(String objName) {
		int result;

		// make a new feeder object
		// first see if this one already exists; if so, just reinitialize
		FeederObj obj = (FeederObj) find(objName);

		Circuit ckt = DSSGlobals.activeCircuit;

		if (obj != null) {
			ckt.setActiveCktElement((DSSCktElement) obj);
			result = 0;
		} else {
			ckt.setActiveCktElement(new FeederObjImpl(this, objName));
			result = addObjectToList(DSSGlobals.activeDSSObject);
			ckt.addCktElement(result);
			// done here because feeder objects are instantiated from energy meters
		}

		return result;
	}

	@Override
	public int edit() {
		// continue parsing with contents of parser
		activeFeederObj = (FeederObj) elementList.getActive();
		DSSGlobals.activeCircuit.setActiveCktElement((DSSCktElement) activeFeederObj);

		int result = 0;

		int paramPointer = 0;
		String paramName = Parser.getInstance().getNextParam();
		String param = Parser.getInstance().makeString();
		while (param.length() > 0) {
			if (paramName.length() == 0) {
				paramPointer += 1;
			} else {
				paramPointer = commandList.getCommand(paramName);
			}

			if ((paramPointer > 0) && (paramPointer <= numProperties))
				activeFeederObj.setPropertyValue(paramPointer, param);

				switch (paramPointer) {
				case 0:
					DSSGlobals.doSimpleMsg("Unknown parameter \"" + paramName + "\" for object \"" + getName() +"."+ activeFeederObj.getName() + "\"", 630);
					break;
				default:
					classEdit(activeFeederObj, paramPointer - NumPropsThisClass);
					break;
				}

				paramName = Parser.getInstance().getNextParam();
				param     = Parser.getInstance().makeString();
		}

		activeFeederObj.recalcElementData();
		activeFeederObj.setYPrimInvalid(true);

		return result;
	}

	@Override
	protected int makeLike(String otherFeederName) {
		int result = 0;

		/* See if we can find this name in the present collection */
		FeederObj otherFeeder = (FeederObj) find(otherFeederName);
		if (otherFeeder != null) {
			if (activeFeederObj.getNPhases() != otherFeeder.getNPhases()) {
				activeFeederObj.setNPhases(otherFeeder.getNPhases());
				activeFeederObj.setNConds(activeFeederObj.getNPhases());  // forces reallocation of terminal stuff

				activeFeederObj.setYorder(activeFeederObj.getNConds() * activeFeederObj.getNTerms());
				activeFeederObj.setYPrimInvalid(true);
			}

			// put properties to copy here

			classMakeLike(otherFeeder);  // set spectrum, base frequency

			for (int i = 0; i < activeFeederObj.getParentClass().getNumProperties(); i++) {
				activeFeederObj.setPropertyValue(i, otherFeeder.getPropertyValue(i));
			}
			result = 1;
		} else {
			DSSGlobals.doSimpleMsg("Error in Feeder makeLike: \"" + otherFeederName + "\" not found.", 631);
		}

		return result;
	}

	@Override
	public int init(int handle) {
		DSSGlobals.doSimpleMsg("Need to implement Feeder.init()", -1);
		return 0;
	}

}
