package com.epri.dss.conversion.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import com.epri.dss.common.impl.DSSClassDefs;
import com.epri.dss.common.impl.DSSGlobals;
import com.epri.dss.common.impl.Utilities;
import com.epri.dss.conversion.Generator;
import com.epri.dss.conversion.GeneratorObj;
import com.epri.dss.general.LoadShapeObj;
import com.epri.dss.parser.impl.Parser;
import com.epri.dss.shared.impl.CommandListImpl;

public class GeneratorImpl extends PCClassImpl implements Generator {

	private static GeneratorObj ActiveGeneratorObj;

	private String[] RegisterNames = new String[Generator.NumGenRegisters];

	public GeneratorImpl() {
		super();
		this.Class_Name = "Generator";
		this.DSSClassType = this.DSSClassType + DSSClassDefs.GEN_ELEMENT;  // in both PCElement and GenElement list

		this.ActiveElement = -1;

		// set register names
		this.RegisterNames[0]  = "kWh";
		this.RegisterNames[1]  = "kvarh";
		this.RegisterNames[2]  = "Max kW";
		this.RegisterNames[3]  = "Max kVA";
		this.RegisterNames[4]  = "Hours";
		this.RegisterNames[5]  = "$";

		defineProperties();

		String[] Commands = new String[this.NumProperties];
		System.arraycopy(this.PropertyName, 0, Commands, 0, this.NumProperties);
		this.CommandList = new CommandListImpl(Commands);
		this.CommandList.setAbbrevAllowed(true);
	}

	protected void defineProperties() {

		NumProperties = Generator.NumPropsThisClass;
		countProperties();  // get inherited property count
		allocatePropertyArrays();   /* see DSSClass */

		// define property names
		addProperty("phases", 0, "Number of Phases, this Generator.  Power is evenly divided among phases.");
		addProperty("bus1", 1, "Bus to which the Generator is connected.  May include specific node specification.");
		addProperty("kv",  2,  "Nominal rated (1.0 per unit) voltage, kV, for Generator. For 2- and 3-phase Generators, specify phase-phase kV. "+
						"Otherwise, specify actual kV across each branch of the Generator. "+
						"If wye (star), specify phase-neutral kV. "+
						"If delta or phase-phase connected, specify phase-phase kV.");  // line-neutral voltage//  base voltage
		addProperty("kW", 3, "Total base kW for the Generator.  A positive value denotes power coming OUT of the element, "+DSSGlobals.CRLF+
						"which is the opposite of a load. This value is modified depending on the dispatch mode. " +
						"Unaffected by the global load multiplier and growth curves. " +
						"If you want there to be more generation, you must add more generators or change this value.");
		addProperty("pf", 4, "Generator power factor. Default is 0.80. Enter negative for leading powerfactor "+
						"(when kW and kvar have opposite signs.)"+DSSGlobals.CRLF+
						"A positive power factor for a generator signifies that the generator produces vars " + DSSGlobals.CRLF +
						"as is typical for a synchronous generator.  Induction machines would be " +DSSGlobals.CRLF+
						"specified with a negative power factor.");
		addProperty("kvar", 12,   "Specify the base kvar.  Alternative to specifying the power factor.  Side effect: "+
							" the power factor value is altered to agree based on present value of kW.");
		addProperty("model", 5, "Integer code for the model to use for generation variation with voltage. "+
						"Valid values are:" +DSSGlobals.CRLF+DSSGlobals.CRLF+
						"1:Generator injects a constant kW at specified power factor."+DSSGlobals.CRLF+
						"2:Generator is modeled as a constant admittance."  +DSSGlobals.CRLF+
						"3:Const kW, constant kV.  Somewhat like a conventional transmission power flow P-V generator."+DSSGlobals.CRLF+
						"4:Const kW, Fixed Q (Q never varies)"+DSSGlobals.CRLF+
						"5:Const kW, Fixed Q(as a constant reactance)"+DSSGlobals.CRLF+
						"6:Compute load injection from User-written Model.(see usage of Xd, Xdp)"+DSSGlobals.CRLF+
						"7:Constant kW, kvar, but current limited below Vminpu");
		addProperty("Vminpu", 22,   "Default = 0.90.  Minimum per unit voltage for which the Model is assumed to apply. " +
							"Below this value, the load model reverts to a constant impedance model.");
		addProperty("Vmaxpu", 23, "Default = 1.10.  Maximum per unit voltage for which the Model is assumed to apply. " +
							"Above this value, the load model reverts to a constant impedance model.");
		addProperty("yearly", 6,  "Dispatch shape to use for yearly simulations.  Must be previously defined "+
						"as a Loadshape object. If this is not specified, a constant value is assumed (no variation). "+
						"If the generator is assumed to be ON continuously, specify Status=FIXED, or "+
						"designate a curve that is 1.0 per unit at all times. "+
						"Set to NONE to reset to no loadahape. " +
						"Nominally for 8760 simulations.  If there are fewer points in the designated shape than "+
						"the number of points in the solution, the curve is repeated.");
		addProperty("daily", 7,  "Dispatch shape to use for daily simulations.  Must be previously defined "+
						"as a Loadshape object of 24 hrs, typically.  If generator is assumed to be "+
						"ON continuously, specify Status=FIXED, or designate a Loadshape object"+
						"that is 1.0 perunit for all hours. " +
						"Set to NONE to reset to no loadahape. "       ); // daily dispatch (hourly)
		addProperty("duty", 8,  "Load shape to use for duty cycle dispatch simulations such as for wind generation. " +
						"Must be previously defined as a Loadshape object. "+
						"Typically would have time intervals less than 1 hr -- perhaps, in seconds. "+
						"Set Status=Fixed to ignore Loadshape designation. " +
						"Set to NONE to reset to no loadahape. " +
						"Designate the number of points to solve using the Set Number=xxxx command. "+
						"If there are fewer points in the actual shape, the shape is assumed to repeat.");  // as for wind generation
		addProperty("dispmode", 9,   "{Default* | Loadlevel | Price } Default = Default. Dispatch mode. "+
						"In default mode, gen is either always on or follows dispatch curve as specified. "+
						"Otherwise, the gen comes on when either the global default load level (Loadshape \"default\") or the price level "+
						"exceeds the dispatch value."); // = 0 | >0
		addProperty("dispvalue", 10,  "Dispatch value. "+DSSGlobals.CRLF+
						"If = 0.0 (default) then Generator follow dispatch curves, if any. " +DSSGlobals.CRLF+
						"If > 0  then Generator is ON only when either the price signal (in Price dispatch mode) "+
						"exceeds this value or the active circuit load multiplier * \"default\" loadshape value * the default yearly growth factor " +
						"exceeds this value.  Then the generator follows dispatch curves (duty, daily, or yearly), if any (see also Status).");  // = 0 | >0
		addProperty("conn",  11,  "={wye|LN|delta|LL}.  Default is wye.");
		addProperty("Rneut", 13, "Removed due to causing confusion - Add neutral impedance externally.");
		addProperty("Xneut", 14, "Removed due to causing confusion - Add neutral impedance externally.");
		addProperty("status", 15,  "={Fixed | Variable*}.  If Fixed, then dispatch multipliers do not apply. "+
							"The generator is alway at full power when it is ON. "+
							" Default is Variable  (follows curves).");  // fixed or variable
		addProperty("class", 16,   "An arbitrary integer number representing the class of Generator so that Generator values may "+
							"be segregated by class."); // integer
		addProperty("Vpu", 17,  "Per Unit voltage set point for Model = 3  (typical power flow model).  Default is 1.0. "); // per unit set point voltage for power flow model
		addProperty("maxkvar", 18,  "Maximum kvar limit for Model = 3.  Defaults to twice the specified load kvar.  "+
							"Always reset this if you change PF or kvar properties.");
		addProperty("minkvar", 19,  "Minimum kvar limit for Model = 3. Enter a negative number if generator can absorb vars."+
							" Defaults to negative of Maxkvar.  Always reset this if you change PF or kvar properties.");
		addProperty("pvfactor", 20,  "Deceleration factor for P-V generator model (Model=3).  Default is 0.1. " +
							"If the circuit converges easily, you may want to use a higher number such as 1.0. " +
							"Use a lower number if solution diverges. Use Debugtrace=yes to create a file that will " +
							"trace the convergence of a generator model.");
		addProperty("forceon",  24, "{Yes | No}  Forces generator ON despite requirements of other dispatch modes. " +
							"Stays ON until this property is set to NO, or an internal algorithm cancels the forced ON state.");
		addProperty("kVA", 25, "kVA rating of electrical machine. Defaults to 1.2* kW if not specified. Applied to machine or inverter definition for Dynamics mode solutions. ");
		addProperty("MVA", 26, "MVA rating of electrical machine.  Alternative to using kVA=.");
		addProperty("Xd", 27,  "Per unit synchronous reactance of machine. Presently used only for Thevinen impedance for power flow calcs of user models (model=6). " +
								"Typically use a value 0.4 to 1.0. Default is 1.0");
		addProperty("Xdp",  28, "Per unit transient reactance of the machine.  Used for Dynamics mode and Fault studies.  Default is 0.27." +
								"For user models, this value is used for the Thevinen/Norton impedance for Dynamics Mode.");
		addProperty("Xdpp",  29, "Per unit subtransient reactance of the machine.  Used for Harmonics. Default is 0.20.");
		addProperty("H", 30,  "Per unit mass constant of the machine.  MW-sec/MVA.  Default is 1.0.");
		addProperty("D",  31, "Damping constant.  Usual range is 0 to 4. Default is 1.0.  Adjust to get damping");
		addProperty("UserModel", 32, "Name of DLL containing user-written model, which computes the terminal currents for Dynamics studies, " +
									"overriding the default model.  Set to \"none\" to negate previous setting.");
		addProperty("UserData", 33, "String (in quotes or parentheses) that gets passed to user-written model for defining the data required for that model.");
		addProperty("ShaftModel",  34, "Name of user-written DLL containing a Shaft model, which models the prime mover and determines the power on the shaft for Dynamics studies. "+
										"Models additional mass elements other than the single-mass model in the DSS default model. Set to \"none\" to negate previous setting.");
		addProperty("ShaftData", 35,  "String (in quotes or parentheses) that gets passed to user-written shaft dynamic model for defining the data for that model.");
		addProperty("debugtrace", 21,  "{Yes | No }  Default is no.  Turn this on to capture the progress of the generator model " +
							"for each iteration.  Creates a separate file for each generator named \"GEN_name.CSV\"." );


		ActiveProperty = NumPropsThisClass - 1;
		super.defineProperties();  // add defs of inherited properties to bottom of list

		// override default help string
		PropertyHelp[Generator.NumPropsThisClass] = "Name of harmonic voltage or current spectrum for this generator. " +
							"Voltage behind Xd' for machine - default. Current injection for inverter. " +
							"Default value is \"default\", which is defined when the DSS starts.";
	}

	@Override
	public int newObject(String ObjName) {
		DSSGlobals Globals = DSSGlobals.getInstance();

		Globals.getActiveCircuit().setActiveCktElement(new GeneratorObjImpl(this, ObjName));
		return addObjectToList(Globals.getActiveDSSObject());
	}

	private void setNcondsForConnection() {
		GeneratorObj ag = getActiveGeneratorObj();

		switch (ag.getConnection()) {
		case 0:
			ag.setNConds(ag.getNPhases() + 1);
			break;
		case 1:
			switch (ag.getNPhases()) {
			case 1:
				ag.setNConds(ag.getNPhases() + 1);  // L-L
				break;
			case 2:
				ag.setNConds(ag.getNPhases() + 1);  // open-delta
				break;
			default:
				ag.setNConds(ag.getNPhases());
				break;
			}
			break;
		}
	}

	/**
	 * Accepts (case insensitive):
	 * 		delta or LL
	 * 		Y, wye, or LN
	 */
	private void interpretConnection(String S) {
		String TestS;
		GeneratorObj ag = getActiveGeneratorObj();

		TestS = S.toLowerCase();
		switch (TestS.charAt(0)) {
		case 'y':
			ag.setConnection(0);  /* Wye */
			break;
		case 'w':
			ag.setConnection(0);  /* Wye */
			break;
		case 'd':
			ag.setConnection(1);  /* Delta or line-Line */
			break;
		case 'l':
			switch (TestS.charAt(1)) {
			case 'n':
				ag.setConnection(0);
				break;
			case 'l':
				ag.setConnection(1);
				break;
			}
			break;
		}

		setNcondsForConnection();

		/* VBase is always L-N voltage unless 1-phase device or more than 3 phases */
		switch (ag.getNPhases()) {
		case 2:
			ag.setVBase(ag.getGenVars().kVGeneratorBase * DSSGlobals.InvSQRT3x1000);  // L-N Volts
			break;
		case 3:
			ag.setVBase(ag.getGenVars().kVGeneratorBase * DSSGlobals.InvSQRT3x1000);  // L-N Volts
			break;
		default:
			ag.setVBase(ag.getGenVars().kVGeneratorBase * 1000.0);   // just use what is supplied
			break;
		}
		ag.setVBase95(ag.getVMinPU() * ag.getVBase());
		ag.setVBase105(ag.getVMaxPU() * ag.getVBase());

		ag.setYorder(ag.getNConds() * ag.getNTerms());
		ag.setYprimInvalid(true);
	}

	private static int interpretDispMode(String S) {
		switch (S.toLowerCase().charAt(0)) {
		case 'l':
			return Generator.LOADMODE;
		case 'p':
			return Generator.PRICEMODE;
		default:
			return Generator.DEFAULT;
		}
	}

	@Override
	public int edit() {
		DSSGlobals Globals = DSSGlobals.getInstance();
		Parser parser = Parser.getInstance();

		// continue parsing with contents of parser
		setActiveGeneratorObj((GeneratorObj) ElementList.getActive());
		Globals.getActiveCircuit().setActiveCktElement(getActiveGeneratorObj());

		int Result = 0;
		GeneratorObj ag = getActiveGeneratorObj();

		int ParamPointer = 0;
		String ParamName = parser.getNextParam();
		String Param = parser.makeString();
		while (Param.length() > 0) {
			if (ParamName.length() == 0) {
				ParamPointer += 1;
			} else {
				ParamPointer = CommandList.getCommand(ParamName);
			}

			if ((ParamPointer >= 0) && (ParamPointer <= NumProperties)) {
				ag.setPropertyValue(PropertyIdxMap[ParamPointer], Param);
			} else {
				Globals.doSimpleMsg("Unknown parameter \""+ParamName+"\" for generator \""+ag.getName()+"\"", 560);
			}

			if (ParamPointer >= 0) {
				switch (PropertyIdxMap[ParamPointer]) {
				case -1:
					Globals.doSimpleMsg("Unknown parameter \"" + ParamName + "\" for object \"" + getName() +"."+ ag.getName() + "\"", 561);
					break;
				case 0:
					ag.setNPhases(parser.makeInteger());  // num phases
					break;
				case 1:
					ag.setBus(1, Param);  // TODO Check zero based indexing
					break;
				case 2:
					ag.setPresentKV(parser.makeDouble());
					break;
				case 3:
					ag.setkWBase(parser.makeDouble());
					break;
				case 4:
					ag.setPowerFactor(parser.makeDouble());
					break;
				case 5:
					ag.setGenModel(parser.makeInteger());
					break;
				case 6:
					ag.setYearlyShape(Param);
					break;
				case 7:
					ag.setDailyDispShape(Param);
					break;
				case 8:
					ag.setDutyShape(Param);
					break;
				case 9:
					ag.setDispatchMode(interpretDispMode(Param));
					break;
				case 10:
					ag.setDispatchValue(parser.makeDouble());
					break;
				case 11:
					interpretConnection(Param);
					break;
				case 12:
					ag.setPresentKVar(parser.makeDouble());
					break;
				case 13:
					Globals.doSimpleMsg("Rneut property has been deleted. Use external impedance.", 5611);
					break;
				case 14:
					Globals.doSimpleMsg("Xneut property has been deleted. Use external impedance.", 5612);
					break;
				case 15:
					if (Param.toLowerCase().charAt(0) == 'f') {
						ag.setFixed(true);
					} else {
						ag.setFixed(false);
					}
					break;
				case 16:
					ag.setGenClass(parser.makeInteger());
					break;
				case 17:
					ag.setVpu(parser.makeDouble());
					break;
				case 18:
					ag.setKvarMax(parser.makeDouble());
					break;
				case 19:
					ag.setKvarMin(parser.makeDouble());
					break;
				case 20:
					ag.setPVFactor(parser.makeDouble());  // declaration factor
					break;
				case 21:
					ag.setDebugTrace(Utilities.interpretYesNo(Param));
					break;
				case 22:
					ag.setVMinPU(parser.makeDouble());
					break;
				case 23:
					ag.setVMaxPU(parser.makeDouble());
					break;
				case 24:
					ag.setForcedON(Utilities.interpretYesNo(Param));
					break;
				case 25:
					ag.getGenVars().kVArating = parser.makeDouble();
					break;
				case 26:
					ag.getGenVars().kVArating = parser.makeDouble() * 1000.0;  // "MVA";
					break;
				case 27:
					ag.getGenVars().puXd = parser.makeDouble();
					break;
				case 28:
					ag.getGenVars().puXdp = parser.makeDouble();
					break;
				case 29:
					ag.getGenVars().puXdpp = parser.makeDouble();
					break;
				case 30:
					ag.getGenVars().Hmass = parser.makeDouble();
					break;
				case 31:
					ag.getGenVars().Dpu = parser.makeDouble();
					break;
				case 32:
					ag.getUserModel().setName(parser.makeString());  // connect to user written models
					break;
				case 33:
					ag.getUserModel().edit(parser.makeString());  // send edit string to user model
					break;
				case 34:
					ag.getShaftModel().setName(parser.makeString());
					break;
				case 35:
					ag.getShaftModel().edit(parser.makeString());
					break;
				default:
					// inherited parameters
					classEdit(getActiveGeneratorObj(), ParamPointer - Generator.NumPropsThisClass);
					break;
				}
			}

			if (ParamPointer >= 0) {
				switch (PropertyIdxMap[ParamPointer]) {
				case 0:
					setNcondsForConnection();  // force reallocation of terminal info
					break;
				case 3:
					// keep kvar nominal up to date with kW and PF
					ag.syncUpPowerQuantities();
					break;
				case 4:
					// keep kvar nominal up to date with kW and PF
					ag.syncUpPowerQuantities();
					break;
				case 6:
					/* Set shape objects; returns nil if not valid */
					/* Sets the kW and kvar properties to match the peak kW demand from the LoadShape */
					ag.setYearlyShapeObj( (LoadShapeObj) Globals.getLoadShapeClass().find(ag.getYearlyShape()) );
					if (ag.getYearlyShape() != null) {
						if (ag.getYearlyShapeObj().isUseActual())
							ag.setKwKVar(ag.getYearlyShapeObj().getMaxP(), ag.getYearlyShapeObj().getMaxQ());
					}
					break;
				case 7:
					ag.setDailyDispShapeObj( (LoadShapeObj) Globals.getLoadShapeClass().find(ag.getDailyDispShape()) );
					if (ag.getDailyDispShapeObj() != null) {
						if (ag.getDailyDispShapeObj().isUseActual())
							ag.setKwKVar(ag.getDailyDispShapeObj().getMaxP(), ag.getDailyDispShapeObj().getMaxQ());
					}
					break;
				case 8:
					ag.setDutyShapeObj( (LoadShapeObj) Globals.getLoadShapeClass().find(ag.getDutyShape()) );
					if (ag.getDutyShapeObj() != null) {
						if (ag.getDutyShapeObj().isUseActual())
							ag.setKwKVar(ag.getDutyShapeObj().getMaxP(), ag.getDutyShapeObj().getMaxQ());
					}
					break;
				case 21:
					if (ag.isDebugTrace()) {
						try {
							File TraceFile = new File(Globals.getDSSDataDirectory() + "GEN_"+ag.getName()+".csv");
							FileWriter TraceStream = new FileWriter(TraceFile, false);
							BufferedWriter TraceBuffer = new BufferedWriter(TraceStream);

							TraceBuffer.write("t, Iteration, LoadMultiplier, Mode, LoadModel, GenModel, dQdV, Avg_Vpu, Vdiff, MQnominalperphase, MPnominalperphase, CurrentType");

							for (int i = 0; i < ag.getNPhases(); i++)
								TraceBuffer.write(", |Iinj" + String.valueOf(i) + "|");
							for (int i = 0; i < ag.getNPhases(); i++)
								TraceBuffer.write(", |Iterm" + String.valueOf(i) + "|");
							for (int i = 0; i < ag.getNPhases(); i++)
								TraceBuffer.write(", |Vterm" + String.valueOf(i) + "|");

							TraceBuffer.write(",Vthev, Theta");
							TraceBuffer.newLine();

							TraceBuffer.close();
							TraceStream.close();
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
					break;
				case 25:
					ag.setkVANotSet(false);
					break;
				case 26:
					ag.setkVANotSet(false);
					break;
				}
			}

			ParamName = parser.getNextParam();
			Param     = parser.makeString();
		}

		ag.recalcElementData();
		ag.setYprimInvalid(true);

		return Result;
	}

	@Override
	protected int makeLike(String OtherGeneratorName) {
		int i;

		int Result = 0;
		/* See if we can find this line name in the present collection */
		GeneratorObj OtherGenerator = (GeneratorObj) find(OtherGeneratorName);
		if (OtherGenerator != null) {
			GeneratorObj ag = getActiveGeneratorObj();

			if (ag.getNPhases() != OtherGenerator.getNPhases()) {
				ag.setNPhases(OtherGenerator.getNPhases());
				ag.setNConds(ag.getNPhases());  // forces reallocation of terminal stuff

				ag.setYorder(ag.getNConds() * ag.getNTerms());
				ag.setYprimInvalid(true);
			}

			ag.getGenVars().kVGeneratorBase = OtherGenerator.getGenVars().kVGeneratorBase;
			ag.setVBase(OtherGenerator.getVBase());
			ag.setVMinPU(OtherGenerator.getVMinPU());
			ag.setVMaxPU(OtherGenerator.getVMaxPU());
			ag.setVBase95(OtherGenerator.getVBase95());
			ag.setVBase105(OtherGenerator.getVBase105());
			ag.setkWBase(OtherGenerator.getkWBase());
			ag.setKvarBase(OtherGenerator.getKvarBase());
			ag.getGenVars().Pnominalperphase = OtherGenerator.getGenVars().Pnominalperphase;
			ag.setPowerFactor(OtherGenerator.getPowerFactor());
			ag.getGenVars().Qnominalperphase = OtherGenerator.getGenVars().Qnominalperphase;
			ag.setVarMin(OtherGenerator.getVarMin());
			ag.setVarMax(OtherGenerator.getVarMax());
			ag.setConnection(OtherGenerator.getConnection());
			//ag.setRneut(OtherGenerator.getRneut());
			//ag.setXneut(OtherGenerator.getXneut());
			ag.setYearlyShape(OtherGenerator.getYearlyShape());
			ag.setYearlyShapeObj(OtherGenerator.getYearlyShapeObj());
			ag.setDailyDispShape(OtherGenerator.getDailyDispShape());
			ag.setDailyDispShapeObj(OtherGenerator.getDailyDispShapeObj());
			ag.setDutyShape(OtherGenerator.getDutyShape());
			ag.setDutyShapeObj(OtherGenerator.getDutyShapeObj());
			ag.setDispatchMode(OtherGenerator.getDispatchMode());
			ag.setDispatchValue(OtherGenerator.getDispatchValue());
			ag.setGenClass(OtherGenerator.getGenClass());
			ag.setGenModel(OtherGenerator.getGenModel());
			ag.setFixed(OtherGenerator.isFixed());
			ag.setVTarget(OtherGenerator.getVTarget());
			ag.setVpu(OtherGenerator.getVpu());
			ag.setKvarMax(OtherGenerator.getKvarMax());
			ag.setKvarMin(OtherGenerator.getKvarMin());
			ag.setForcedON(OtherGenerator.isForcedON());
			ag.setkVANotSet(OtherGenerator.iskVANotSet());

			ag.getGenVars().kVArating      = OtherGenerator.getGenVars().kVArating;
			ag.getGenVars().puXd           = OtherGenerator.getGenVars().puXd;
			ag.getGenVars().puXdp          = OtherGenerator.getGenVars().puXdp;
			ag.getGenVars().puXdpp         = OtherGenerator.getGenVars().puXdpp;
			ag.getGenVars().Hmass          = OtherGenerator.getGenVars().Hmass;
			ag.getGenVars().Theta          = OtherGenerator.getGenVars().Theta;
			ag.getGenVars().Speed          = OtherGenerator.getGenVars().Speed;
			ag.getGenVars().w0             = OtherGenerator.getGenVars().w0;
			ag.getGenVars().dSpeed         = OtherGenerator.getGenVars().dSpeed;
			ag.getGenVars().D              = OtherGenerator.getGenVars().D;
			ag.getGenVars().Dpu            = OtherGenerator.getGenVars().Dpu;

			ag.getUserModel().setName(OtherGenerator.getUserModel().getName());  // connect to user written models
			ag.getShaftModel().setName(OtherGenerator.getShaftModel().getName());

			classMakeLike(OtherGenerator);

			for (i = 0; i < ag.getParentClass().getNumProperties(); i++)
				ag.setPropertyValue(i, OtherGenerator.getPropertyValue(i));

			Result = 1;
		} else {
			DSSGlobals.getInstance().doSimpleMsg("Error in Load makeLike: \"" + OtherGeneratorName + "\" not found.", 562);
		}

		return Result;
	}

	@Override
	public int init(int Handle) {
		GeneratorObj p;

		if (Handle == 0) {  // init all
			for (int i = 0; i < ElementList.size(); i++) {
				p = (GeneratorObj) ElementList.get(i);
				p.randomize(0);
			}
		} else {
			setActiveElement(Handle);
			p = (GeneratorObj) getActiveObj();
			p.randomize(0);
		}

		DSSGlobals.getInstance().doSimpleMsg("Need to implement Generator.init()", -1);
		return 0;
	}

	/**
	 * Force all EnergyMeters in the circuit to reset.
	 */
	public void resetRegistersAll() {
		for (GeneratorObj pGen : DSSGlobals.getInstance().getActiveCircuit().getGenerators())
			pGen.resetRegisters();
	}

	/**
	 * Force all EnergyMeters in the circuit to take a sample.
	 */
	public void sampleAll() {
		for (GeneratorObj pGen : DSSGlobals.getInstance().getActiveCircuit().getGenerators())
			pGen.takeSample();
	}

	public String[] getRegisterNames() {
		return RegisterNames;
	}

	public void setRegisterNames(String[] registerNames) {
		RegisterNames = registerNames;
	}

	public static GeneratorObj getActiveGeneratorObj() {
		return ActiveGeneratorObj;
	}

	public static void setActiveGeneratorObj(GeneratorObj activeGeneratorObj) {
		ActiveGeneratorObj = activeGeneratorObj;
	}

}
