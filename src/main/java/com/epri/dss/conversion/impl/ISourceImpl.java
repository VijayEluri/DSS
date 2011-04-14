package com.epri.dss.conversion.impl;

import com.epri.dss.common.impl.DSSCktElement;
import com.epri.dss.common.impl.DSSClassDefs;
import com.epri.dss.common.impl.DSSGlobals;
import com.epri.dss.conversion.ISource;
import com.epri.dss.conversion.ISourceObj;
import com.epri.dss.parser.impl.Parser;
import com.epri.dss.shared.impl.CommandListImpl;

public class ISourceImpl extends PCClassImpl implements ISource {
	
	private static ISourceObj ActiveISourceObj;

	public ISourceImpl() {
		super();
		this.Class_Name = "Isource";
		this.DSSClassType = DSSClassDefs.SOURCE + DSSClassDefs.NON_PCPD_ELEM;  // Don"t want this in PC Element List

		this.ActiveElement = -1;

		defineProperties();

		String[] Commands = new String[0];
		System.arraycopy(this.PropertyName, 0, Commands, 0, this.NumProperties);
		this.CommandList = new CommandListImpl(Commands);
		this.CommandList.setAbbrevAllowed(true);
	}
	
	protected void defineProperties() {
		NumProperties = ISource.NumPropsThisClass;
		
		countProperties();   // Get inherited property count
		allocatePropertyArrays();


		// Define property names
		PropertyName[0] = "bus1";
		PropertyName[1] = "amps";
		PropertyName[2] = "angle";
		PropertyName[3] = "frequency";
		PropertyName[4] = "phases";
		PropertyName[5] = "scantype";
		//PropertyName[5] = "spectrum";

		// Define property help values
		PropertyHelp[0] = "Name of bus to which source is connected."+DSSGlobals.CRLF+"bus1=busname"+DSSGlobals.CRLF+"bus1=busname.1.2.3";
		PropertyHelp[1] = "Magnitude of current source, each phase, in Amps.";
		PropertyHelp[2] = "Phase angle in degrees of first phase: e.g.,Angle=10.3."+DSSGlobals.CRLF+
				"Phase shift between phases is assumed 120 degrees when "+
				"number of phases <= 3";
		PropertyHelp[3] = "Source frequency.  Defaults to  circuit fundamental frequency.";
		PropertyHelp[4] = "Number of phases.  Defaults to 3. For 3 or less, phase shift is 120 degrees.";
		PropertyHelp[5] = "{pos*| zero | none} Maintain specified sequence for harmonic solution. Default is positive sequence. "+
				"Otherwise, angle between phases rotates with harmonic.";


		ActiveProperty = ISource.NumPropsThisClass - 1;  // TODO Check zero based indexing
		super.defineProperties();  // Add defs of inherited properties to bottom of list

		// Override help string
		PropertyHelp[ISource.NumPropsThisClass] = "Harmonic spectrum assumed for this source.  Default is \"default\".";  // TODO Check zero based indexing
	}
	
	@Override
	public int newObject(String ObjName) {
		DSSGlobals Globals = DSSGlobals.getInstance();

		Globals.getActiveCircuit().setActiveCktElement(new ISourceObjImpl(this, ObjName));
		return addObjectToList(Globals.getActiveDSSObject());
	}
	
	@Override
	public int edit() {
		DSSGlobals Globals = DSSGlobals.getInstance();
		Parser parser = Parser.getInstance();
		
		// continue parsing with contents of Parser
		setActiveISourceObj(ElementList.getActive());
		Globals.getActiveCircuit().setActiveCktElement((DSSCktElement) getActiveISourceObj());

		int Result = 0;

		ISourceObj ais  = getActiveISourceObj();

		int ParamPointer = 0;
		String ParamName = parser.getNextParam();
		String Param     = parser.makeString();
		while (Param.length() > 0) {
			if (ParamName.length() == 0) {
				ParamPointer += 1;
			} else {
				ParamPointer = CommandList.getCommand(ParamName);
			}

			if ((ParamPointer >= 0) && (ParamPointer < NumProperties))
				ais.setPropertyValue(ParamPointer, Param);

			switch (ParamPointer) {
			case -1:
				Globals.doSimpleMsg("Unknown parameter \"" + ParamName + "\" for Object \"" + Class_Name +"."+ ais.getName() + "\"", 330);
			case 0:
				ais.setBus(1, Param);  // TODO Check zero based indexing
			case 1:
				ais.setAmps(parser.makeDouble());
			case 2:
				ais.setAngle(parser.makeDouble());  // Ang
			case 3:
				ais.setSrcFrequency(parser.makeDouble()); // freq
			case 4:
				ais.setNPhases(parser.makeInteger()); // num phases
				switch (ais.getNPhases()) {
				case 1:
					ais.setPhaseShift(0.0);
				case 2:
					ais.setPhaseShift(120.0);
				case 3:
					ais.setPhaseShift(120.0);
				default:  // higher order systems
					ais.setPhaseShift(360.0 / ais.getNPhases());
				}
				ais.setNConds(ais.getNPhases());  // Force Reallocation of terminal info
			case 5:
				switch (Param.toUpperCase().charAt(0)) {
				case 'P':
					ais.setScanType(1);
				case 'Z':
					ais.setScanType(0);
				case 'N':
					ais.setScanType(-1);
				default:
					Globals.doSimpleMsg("Unknown Scan Type for \"" + Class_Name +"."+ ais.getName() + "\": "+Param, 331);
				}
			default:
				classEdit(getActiveISourceObj(), ParamPointer - ISource.NumPropsThisClass);
			}

			ParamName = parser.getNextParam();
			Param     = parser.makeString();
		}
		
		ais.recalcElementData();
		ais.setYprimInvalid(true);
		
		return Result;
	}
	
	@Override
	protected int makeLike(String OtherSource) {
		int Result = 0;

		/* See if we can find this line name in the present collection */
		ISourceObj OtherIsource = (ISourceObj) find(OtherSource);
		
		if (OtherIsource != null) {
			ISourceObj ais  = getActiveISourceObj();

			if (ais.getNPhases() != OtherIsource.getNPhases()) {
				ais.setNPhases(OtherIsource.getNPhases());
				ais.setNConds(ais.getNPhases());  // Forces reallocation of terminal stuff

				ais.setYorder(ais.getNConds() * ais.getNTerms());
				ais.setYprimInvalid(true);
			}

			ais.setAmps(OtherIsource.getAmps());
			ais.setAngle(OtherIsource.getAngle());
			ais.setSrcFrequency(OtherIsource.getSrcFrequency());

			classMakeLike(OtherIsource); // set spectrum,  base frequency

			for (int i = 0; i < ais.getParentClass().getNumProperties(); i++)
				ais.setPropertyValue(i, OtherIsource.getPropertyValue(i));
			
			Result = 1;
		} else {
			DSSGlobals.getInstance().doSimpleMsg("Error in Isource makeLike: \"" + OtherSource + "\" Not Found.", 332);
		}

		return Result;
	}
	
	@Override
	public int init(int Handle) {
		DSSGlobals.getInstance().doSimpleMsg("Need to implement Isource.init", -1);
		return 0;
	}

	public static ISourceObj getActiveISourceObj() {
		return ActiveISourceObj;
	}

	public static void setActiveISourceObj(ISourceObj activeISourceObj) {
		ActiveISourceObj = activeISourceObj;
	}

}
