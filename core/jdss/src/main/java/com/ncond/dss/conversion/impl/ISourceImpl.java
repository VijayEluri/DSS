package com.ncond.dss.conversion.impl;

import com.ncond.dss.common.impl.DSSClassDefs;
import com.ncond.dss.common.impl.DSSGlobals;
import com.ncond.dss.conversion.ISource;
import com.ncond.dss.conversion.ISourceObj;
import com.ncond.dss.parser.impl.Parser;
import com.ncond.dss.shared.impl.CommandListImpl;

public class ISourceImpl extends PCClassImpl implements ISource {

	public static ISourceObj activeISourceObj;

	public ISourceImpl() {
		super();
		className = "Isource";
		classType = DSSClassDefs.SOURCE + DSSClassDefs.NON_PCPD_ELEM;  // don"t want this in PC element list

		activeElement = -1;

		defineProperties();

		String[] commands = new String[numProperties];
		System.arraycopy(propertyName, 0, commands, 0, numProperties);
		commandList = new CommandListImpl(commands);
		commandList.setAbbrevAllowed(true);
	}

	protected void defineProperties() {
		numProperties = ISource.NumPropsThisClass;

		countProperties();  // get inherited property count
		allocatePropertyArrays();

		// define property names
		propertyName[0] = "bus1";
		propertyName[1] = "amps";
		propertyName[2] = "angle";
		propertyName[3] = "frequency";
		propertyName[4] = "phases";
		propertyName[5] = "scantype";
		propertyName[6] = "sequence";

		// define property help values
		propertyHelp[0] = "Name of bus to which source is connected."+DSSGlobals.CRLF+"bus1=busname"+DSSGlobals.CRLF+"bus1=busname.1.2.3";
		propertyHelp[1] = "Magnitude of current source, each phase, in Amps.";
		propertyHelp[2] = "Phase angle in degrees of first phase: e.g.,Angle=10.3."+DSSGlobals.CRLF+
				"Phase shift between phases is assumed 120 degrees when "+
				"number of phases <= 3";
		propertyHelp[3] = "Source frequency.  Defaults to  circuit fundamental frequency.";
		propertyHelp[4] = "Number of phases.  Defaults to 3. For 3 or less, phase shift is 120 degrees.";
		propertyHelp[5] = "{pos*| zero | none} Maintain specified sequence for harmonic solution. Default is positive sequence. "+
				"Otherwise, angle between phases rotates with harmonic.";
		propertyHelp[6] = "{pos*| neg | zero} Set the phase angles for the specified symmetrical component sequence for non-harmonic solution modes. "+
				"Default is positive sequence.";


		activeProperty = ISource.NumPropsThisClass - 1;
		super.defineProperties();  // add defs of inherited properties to bottom of list

		// override help string
		propertyHelp[ISource.NumPropsThisClass] = "Harmonic spectrum assumed for this source.  Default is \"default\".";
	}

	@Override
	public int newObject(String objName) {
		DSSGlobals.activeCircuit.setActiveCktElement(new ISourceObjImpl(this, objName));
		return addObjectToList(DSSGlobals.activeDSSObject);
	}

	@Override
	public int edit() {
		Parser parser = Parser.getInstance();

		// continue parsing with contents of parser
		activeISourceObj = (ISourceObj) elementList.getActive();
		DSSGlobals.activeCircuit.setActiveCktElement(activeISourceObj);

		int result = 0;

		ISourceObj ais  = activeISourceObj;

		int paramPointer = -1;
		String paramName = parser.getNextParam();
		String param = parser.makeString();
		while (param.length() > 0) {
			if (paramName.length() == 0) {
				paramPointer += 1;
			} else {
				paramPointer = commandList.getCommand(paramName);
			}

			if (paramPointer >= 0 && paramPointer < numProperties)
				ais.setPropertyValue(paramPointer, param);

			switch (paramPointer) {
			case -1:
				DSSGlobals.doSimpleMsg("Unknown parameter \"" + paramName + "\" for object \"" + getName() +"."+ ais.getName() + "\"", 330);
				break;
			case 0:
				ais.setBus(0, param);
				break;
			case 1:
				ais.setAmps(parser.makeDouble());
				break;
			case 2:
				ais.setAngle(parser.makeDouble());  // ang
				break;
			case 3:
				ais.setSrcFrequency(parser.makeDouble()); // freq
				break;
			case 4:
				ais.setNPhases(parser.makeInteger()); // num phases
				switch (ais.getNPhases()) {
				case 1:
					ais.setPhaseShift(0.0);
					break;
				case 2:
					ais.setPhaseShift(120.0);
					break;
				case 3:
					ais.setPhaseShift(120.0);
					break;
				default:  // higher order systems
					ais.setPhaseShift(360.0 / ais.getNPhases());
					break;
				}
				ais.setNConds(ais.getNPhases());  // force reallocation of terminal info
				break;
			case 5:
				switch (param.toUpperCase().charAt(0)) {
				case 'P':
					ais.setScanType(1);
					break;
				case 'Z':
					ais.setScanType(0);
					break;
				case 'N':
					ais.setScanType(-1);
					break;
				default:
					DSSGlobals.doSimpleMsg("Unknown scan type for \"" + getName() +"."+ ais.getName() + "\": "+param, 331);
					break;
				}
				break;
			case 6:
				switch (param.toUpperCase().charAt(0)) {
				case 'P':
					ais.setSequenceType(1);
					break;
				case 'Z':
					ais.setSequenceType(0);
					break;
				case 'N':
					ais.setSequenceType(-1);
					break;
				default:
					DSSGlobals.doSimpleMsg("Unknown sequence type for \"" + getName() +"."+ ais.getName() + "\": "+param, 331);
					break;
				}
				break;
			default:
				classEdit(activeISourceObj, paramPointer - ISource.NumPropsThisClass);
				break;
			}

			paramName = parser.getNextParam();
			param     = parser.makeString();
		}

		ais.recalcElementData();
		ais.setYPrimInvalid(true);

		return result;
	}

	@Override
	protected int makeLike(String otherSource) {
		int result = 0;

		/* See if we can find this line name in the present collection */
		ISourceObj otherISource = (ISourceObj) find(otherSource);

		if (otherISource != null) {
			ISourceObj ais  = activeISourceObj;

			if (ais.getNPhases() != otherISource.getNPhases()) {
				ais.setNPhases(otherISource.getNPhases());
				ais.setNConds(ais.getNPhases());  // forces reallocation of terminal stuff

				ais.setYOrder(ais.getNConds() * ais.getNTerms());
				ais.setYPrimInvalid(true);
			}

			ais.setAmps(otherISource.getAmps());
			ais.setAngle(otherISource.getAngle());
			ais.setSrcFrequency(otherISource.getSrcFrequency());
			ais.setScanType(otherISource.getScanType());
			ais.setSequenceType(otherISource.getSequenceType());

			classMakeLike(otherISource); // set spectrum, base frequency

			for (int i = 0; i < ais.getParentClass().getNumProperties(); i++)
				ais.setPropertyValue(i, otherISource.getPropertyValue(i));

			result = 1;
		} else {
			DSSGlobals.doSimpleMsg("Error in ISource makeLike: \"" + otherSource + "\" not found.", 332);
		}

		return result;
	}

	@Override
	public int init(int handle) {
		DSSGlobals.doSimpleMsg("Need to implement ISource.init", -1);
		return 0;
	}

}
