package com.epri.dss.control.impl;

import java.io.PrintStream;

import org.apache.commons.math.complex.Complex;
import com.epri.dss.shared.impl.MathUtil;

import com.epri.dss.common.Circuit;
import com.epri.dss.common.CktElement;
import com.epri.dss.common.impl.DSSClassDefs;
import com.epri.dss.common.impl.DSSClassImpl;
import com.epri.dss.common.impl.DSSGlobals;
import com.epri.dss.common.impl.Utilities;
import com.epri.dss.control.Relay;
import com.epri.dss.control.RelayObj;
import com.epri.dss.conversion.PCElement;
import com.epri.dss.general.TCC_CurveObj;

public class RelayObjImpl extends ControlElemImpl implements RelayObj {

	private int controlType;

	/* Over current relay */
	private TCC_CurveObj phaseCurve, groundCurve;

	private double phaseTrip, groundTrip, phaseInst, groundInst;

	private double[] recloseIntervals;
	private int numReclose;

	private double resetTime,
		delayTime,
		breakerTime,
		TDPhase, TDGround;

	private String relayTarget;

	/* Over/under voltage relay */
	// Curves assumed in per unit of base voltage
	private TCC_CurveObj OVCurve, UVCurve;

	private double VBase,   // line-neut volts base
		kVBase;

	/* 46 relay neg seq current */
	private double pickupAmps46,
		pctPickup46,
		baseAmps46,
		isqt46;

	/* 47 relay */
	private double pickupVolts47,
		pctPickup47;

	/* Generic relay */
	private double overTrip,
		underTrip;

	private String monitoredElementName;
	private int monitoredElementTerminal;
	private CktElement monitoredElement;

	private ControlAction presentState;

	private int operationCount;

	private boolean lockedOut,
		armedForClose,
		armedForOpen,
		phaseTarget, groundTarget;

	private double nextTripTime;
	private int lastEventHandle;

	private int condOffset;  // Offset for monitored terminal

	private Complex[] cBuffer;

	public RelayObjImpl(DSSClassImpl parClass, String relayName) {
		super(parClass);
		setName(relayName.toLowerCase());
		objType = parClass.getDSSClassType();

		setNPhases(3);  // directly set conds and phases
		nConds = 3;
		setNTerms(1);   // this forces allocation of terminals and conductors in base class


		elementName   = "";
		setControlledElement(null);
		elementTerminal = 0;  // TODO Check zero based indexing

		monitoredElementName = "";
		monitoredElementTerminal = 0;
		monitoredElement = null;

		relayTarget = "";

		phaseCurve     = null;
		groundCurve    = null;
		OVCurve        = null;
		UVCurve        = null;
		phaseTrip      = 1.0;
		groundTrip     = 1.0;
		TDPhase        = 1.0;
		TDGround       = 1.0;
		phaseInst      = 0.0;
		groundInst     = 0.0;
		resetTime      = 15.0;
		numReclose     = 3;
		recloseIntervals = null;

		recloseIntervals = (double[]) Utilities.resizeArray(recloseIntervals, 4);  // fixed allocation of 4
		recloseIntervals[0] = 0.5;
		recloseIntervals[1] = 2.0;
		recloseIntervals[2] = 2.0;

		presentState = ControlAction.CLOSE;

		isqt46 = 1.0;
		baseAmps46 = 100.0;
		pctPickup46 = 20.0;
		pickupAmps46 = baseAmps46 * pctPickup46 * 0.01;

		pctPickup47 = 2.0;

		overTrip  = 1.2;
		underTrip = 0.8;

		operationCount   = 1;
		lockedOut        = false;
		armedForOpen     = false;
		armedForClose    = false;
		phaseTarget      = false;
		groundTarget     = false;

		nextTripTime     = -1.0;  // not set to trip

		cBuffer = null;

		objType = parClass.getDSSClassType();  // CAP_CONTROL;

		initPropertyValues(0);

		//recalcElementData();
	}

	@Override
	public void recalcElementData() {
		DSSGlobals globals = DSSGlobals.getInstance();

		int devIndex = Utilities.getCktElementIndex(monitoredElementName);
		if (devIndex >= 0) {
			monitoredElement = globals.getActiveCircuit().getCktElements().get(devIndex);
			setNPhases( monitoredElement.getNPhases() );  // force number of phases to be same
			if (monitoredElementTerminal > monitoredElement.getNTerms()) {
				globals.doErrorMsg("Relay: \"" + getName() + "\"",
						"Terminal no. \"" +"\" does not exist.",
						"Re-specify terminal no.", 384);
			} else {
				// sets name of i-th terminal's connected bus in Relay's bus list
				setBus(1, monitoredElement.getBus(monitoredElementTerminal));
				// allocate a buffer big enough to hold everything from the monitored element
				cBuffer = (Complex[]) Utilities.resizeArray(cBuffer, monitoredElement.getYorder());
				condOffset = (monitoredElementTerminal - 1) * monitoredElement.getNConds();  // for speedy sampling

				switch (controlType) {
				case Relay.GENERIC:
					if ((monitoredElement.getDSSObjType() & DSSClassDefs.BASECLASSMASK) != DSSClassDefs.PC_ELEMENT) {
						globals.doSimpleMsg("Relay "+getName()+": Monitored element for generic relay is not a PC element.", 385);
					} else {
						PCElement pElem = (PCElement) monitoredElement;
						monitorVarIndex = pElem.lookupVariable(monitorVariable);
						if (monitorVarIndex < 0)
							globals.doSimpleMsg("Relay "+getName()+": Monitor variable \""+monitorVariable+"\" does not exist.", 386);
					}
					break;
				}
			}
		}

		/* Check for existence of controlled element */
		devIndex = Utilities.getCktElementIndex(elementName);
		if (devIndex >= 0) {
			// both CktElement and monitored element must already exist
			setControlledElement(globals.getActiveCircuit().getCktElements().get(devIndex));
			getControlledElement().setActiveTerminalIdx(elementTerminal);  // make the 1 st terminal active
			if (getControlledElement().getConductorClosed(0)) {  // check state of phases of active terminal
				presentState = ControlAction.CLOSE;
				lockedOut = false;
				operationCount = 1;
				armedForOpen = false;
			} else {
				presentState = ControlAction.OPEN;
				lockedOut = true;
				operationCount = numReclose + 1;
				armedForClose = false;
			}
		} else {
			setControlledElement(null);  // element not found
			globals.doErrorMsg("Relay: \"" + getName() + "\"", "CktElement element \""+ elementName + "\" not found.",
					" Element must be defined previously.", 387);
		}

		/* Misc stuff */

		pickupAmps46 = baseAmps46 * pctPickup46 * 0.01;

		switch (nPhases) {
		case 1:
			VBase = kVBase * 1000.0;
			break;
		default:
			VBase = kVBase / DSSGlobals.SQRT3 * 1000.0;
			break;
		}

		pickupVolts47 = VBase * pctPickup47 * 0.01;
	}

	/**
	 * Make a positive sequence model.
	 */
	@Override
	public void makePosSequence() {
		if (monitoredElement != null) {
			setNPhases( monitoredElement.getNPhases() );
			setNConds(nPhases);
			setBus(1, monitoredElement.getBus(elementTerminal));
			// allocate a buffer big enough to hold everything from the monitored element
			cBuffer = (Complex[]) Utilities.resizeArray(cBuffer, monitoredElement.getYorder());
			condOffset = (elementTerminal - 1) * monitoredElement.getNConds();  // for speedy sampling
		}

		switch (nPhases) {
		case 1:
			VBase = kVBase * 1000.0;
			break;
		default:
			VBase = kVBase / DSSGlobals.SQRT3 * 1000.0;
			break;
		}

		pickupVolts47 = VBase * pctPickup47 * 0.01;

		super.makePosSequence();
	}

	@Override
	public void calcYPrim() {
		// leave YPrims as null and they will be ignored
	}

	@Override
	public void getCurrents(Complex[] curr) {
		for (int i = 0; i < nConds; i++)
			curr[i] = Complex.ZERO;
	}

	@Override
	public void getInjCurrents(Complex[] curr) {
		for (int i = 0; i < nConds; i++)
			curr[i] = Complex.ZERO;
	}

	/**
	 * Do the action that is pending from last sample.
	 */
	@Override
	public void doPendingAction(int code, int proxyHdl) {

		getControlledElement().setActiveTerminalIdx(elementTerminal);  // set active terminal of CktElement to terminal 1

		if (code == ControlAction.OPEN.code()) {
			switch (presentState) {
			case CLOSE:
				if (armedForOpen) {
					// ignore if we became disarmed in meantime
					getControlledElement().setConductorClosed(0, false);  // open all phases of active terminal
					if (operationCount > numReclose) {
						lockedOut = true;
						Utilities.appendToEventLog("Relay."+getName(), "Opened on "+relayTarget+" & Locked Out ");
					}
				} else {
					Utilities.appendToEventLog("Relay."+getName(), "Opened");
				}
				if (phaseTarget) Utilities.appendToEventLog(" ", "Phase Target");
				if (groundTarget) Utilities.appendToEventLog(" ", "Ground Target");
				armedForOpen = false;
				break;
			}
		} else if (code == ControlAction.CLOSE.code()) {
			switch (presentState) {
			case OPEN:
				if (armedForClose && !lockedOut) {
					getControlledElement().setConductorClosed(0, true);  // close all phases of active terminal
					operationCount += 1;
					Utilities.appendToEventLog("Relay."+getName(), "Closed");
					armedForClose = false;
				}
				break;
			}
		} else if (code == ControlAction.CTRL_RESET.code()) {
			switch (presentState) {
			case CLOSE:
				if (!armedForOpen)
					operationCount = 1;  // don't reset if we just rearmed
				break;
			}
		}
	}

	// FIXME Private method in OpenDSS
	public void interpretRelayAction(String action) {

		if (getControlledElement() != null) {
			getControlledElement().setActiveTerminalIdx(elementTerminal);  // set active terminal
			switch (action.toLowerCase().charAt(0)) {
			case 'o':
				getControlledElement().setConductorClosed(0, false);  // open all phases of active terminal
				lockedOut = true;
				operationCount = numReclose + 1;
				break;
			case 't':
				getControlledElement().setConductorClosed(0, false);  // open all phases of active terminal
				lockedOut = true;
				operationCount = numReclose + 1;
				break;
			case 'c':
				getControlledElement().setConductorClosed(0, true);  // close all phases of active terminal
				lockedOut = false;
				operationCount = 1;
				break;
			}
		}
	}

	/**
	 * Sample control quantities and set action times in control queue.
	 */
	@Override
	public void sample() {

		getControlledElement().setActiveTerminalIdx(elementTerminal);
		if (getControlledElement().getConductorClosed(0)) {  // check state of phases of active terminal
			presentState = ControlAction.CLOSE;
		} else {
			presentState = ControlAction.OPEN;

			switch (controlType) {
			case Relay.CURRENT:
				overcurrentLogic();  /* Current */
				break;
			case Relay.VOLTAGE:
				voltageLogic();   /* Reclosing voltage relay - definite time */
				break;
			case Relay.REVPOWER:
				revPowerLogic();  /* One shot to lockout */
				break;
			case Relay.NEGCURRENT:
				negSeq46Logic();  /* One shot to lockout */
				break;
			case Relay.NEGVOLTAGE:
				negSeq47Logic();  /* One shot to lockout */
				break;
			case Relay.GENERIC:
				genericLogic();   /* One shot to lockout */
				break;
			}
		}
	}

	@Override
	public void dumpProperties(PrintStream f, boolean complete) {
		super.dumpProperties(f, complete);

		for (int i = 0; i < getParentClass().getNumProperties(); i++)
			f.println("~ " + getParentClass().getPropertyName()[i] + "=" + getPropertyValue( getParentClass().getPropertyIdxMap()[i] ));

		if (complete)
		f.println();
	}

	@Override
	public String getPropertyValue(int index) {
		String result = "";

		switch (getParentClass().getPropertyIdxMap()[index]) {
		case 13:
			result = "(";
			if (numReclose == 0) {
				result = result + "NONE";
			} else {
				for (int i = 0; i < numReclose; i++)
					result = result + String.format("%-g, " , recloseIntervals[i]);
				result = result + ")";
			}
			break;
		default:
			result = super.getPropertyValue(index);
			break;
		}
		return result;
	}

	/**
	 * Reset to initial defined state.
	 */
	@Override
	public void reset() {

		presentState   = ControlAction.CLOSE;
		operationCount = 1;
		lockedOut      = false;
		armedForOpen   = false;
		armedForClose  = false;
		phaseTarget    = false;
		groundTarget   = false;

		nextTripTime   = -1.0;  // not set to trip

		if (getControlledElement() != null) {
			getControlledElement().setActiveTerminalIdx(elementTerminal);  // set active terminal
			getControlledElement().setConductorClosed(0, true);  // close all phases of active terminal
		}
	}

	@Override
	public void initPropertyValues(int arrayOffset) {

		propertyValue[0] = "";   // "element";
		propertyValue[1] = "1";  // "terminal";
		propertyValue[2] = "";
		propertyValue[3] = "1";  // "terminal";
		propertyValue[4] = "current";
		propertyValue[5] = "";
		propertyValue[6] = "";
		propertyValue[7] = "1.0";
		propertyValue[8] = "1.0";
		propertyValue[9] = "0.0";
		propertyValue[10] = "0.0";
		propertyValue[11] = "15";
		propertyValue[12] = "4";
		propertyValue[13] = "(0.5, 2.0, 2.0)";
		propertyValue[14] = "";
		propertyValue[15] = "";
		propertyValue[16] = "0.0";
		propertyValue[17] = "0.0";
		propertyValue[18] = "";
		propertyValue[19] = "";
		propertyValue[20] = "20";
		propertyValue[21] = "1";
		propertyValue[22] = "100";
		propertyValue[23] = "0";
		propertyValue[24] = "2";
		propertyValue[25] = "1.2";
		propertyValue[26] = "0.8";
		propertyValue[27] = "1.0";
		propertyValue[28] = "1.0";

		super.initPropertyValues(Relay.NumPropsThisClass);
	}

	// FIXME Private method in OpenDSS
	public void interpretRelayType(String s) {

		switch (s.toLowerCase().charAt(0)) {
		case 'c':
			controlType = Relay.CURRENT;
			break;
		case'v':
			controlType = Relay.VOLTAGE;
			break;
		case'r':
			controlType = Relay.REVPOWER;
			break;
		case'4':
			switch (s.charAt(1)) {
			case '6':
				controlType = Relay.NEGCURRENT;
				break;
			case '7':
				controlType = Relay.NEGVOLTAGE;
				break;
			}
		case '8':
			controlType = Relay.GENERIC;
			break;
		default:
			controlType = Relay.CURRENT;
			break;
		}

		/* Set definite time defaults */
		switch (s.toLowerCase().charAt(0)) {
		case 'c':
			delayTime = 0.0;
			break;
		case 'v':
			delayTime = 0.0;
			break;
		case 'r':
			delayTime = 0.1;
			break;
		case '4':
			delayTime = 0.1;
			break;
		case '8':
			delayTime = 0.1;
			break;
		default:
			delayTime = 0.0;
			break;
		}

		propertyValue[23] = String.format("%-.g", delayTime);
	}

	/**
	 * Generic relays only work on PC elements with control terminals.
	 */
	private void genericLogic() {
		PCElement pElem = (PCElement) monitoredElement;
		double varValue = pElem.getVariable(monitorVarIndex);

		/* Check for trip */
		if ((varValue >  overTrip) || (varValue < underTrip)) {
			if (!armedForOpen) {  // push the trip operation and arm to trip
				Circuit ckt = DSSGlobals.getInstance().getActiveCircuit();
				relayTarget = pElem.variableName(monitorVarIndex);
				lastEventHandle = ckt.getControlQueue().push(ckt.getSolution().getIntHour(), ckt.getSolution().getDynaVars().t + delayTime + breakerTime, ControlAction.OPEN, 0, this);
				operationCount = numReclose + 1;  // force a lockout
				armedForOpen = true;
			}
		} else {
			/* Within bounds */
			/* Less than pickup value: reset if armed */
			if (armedForOpen) {  // we became unarmed, so reset and disarm
				Circuit ckt = DSSGlobals.getInstance().getActiveCircuit();
				lastEventHandle = ckt.getControlQueue().push(ckt.getSolution().getIntHour(), ckt.getSolution().getDynaVars().t + resetTime, ControlAction.CTRL_RESET, 0, this);
				armedForOpen = false;
			}
		}
	}

	/**
	 * Negative sequence current relay. Patterned after Basler relay.
	 */
	private void negSeq46Logic() {
		double negSeqCurrentMag, tripTime;
		int iOffset;
		Complex[] I012 = new Complex[3];

		monitoredElement.setActiveTerminalIdx(monitoredElementTerminal);
		monitoredElement.getCurrents(cBuffer);
		iOffset = (monitoredElementTerminal - 1) * monitoredElement.getNConds();  // offset for active terminal
		MathUtil.phase2SymComp(cBuffer[iOffset + 1], I012);
		negSeqCurrentMag = I012[2].abs();
		if (negSeqCurrentMag >= pickupAmps46) {
			if (!armedForOpen) {  // push the trip operation and arm to trip
				Circuit ckt = DSSGlobals.getInstance().getActiveCircuit();
				relayTarget = "-Seq Curr";
				/* Simple estimate of trip time assuming current will be constant */
				if (delayTime > 0.0) {
					tripTime = delayTime;
				} else {
					tripTime = isqt46 / Math.pow(negSeqCurrentMag / baseAmps46, 2);  // sec
				}
				lastEventHandle = ckt.getControlQueue().push(ckt.getSolution().getIntHour(), ckt.getSolution().getDynaVars().t + tripTime + breakerTime, ControlAction.OPEN, 0, this);
				operationCount = numReclose + 1;  // force a lockout
				armedForOpen = true;
			}
		} else {
			/* Less than pickup value: reset if armed */
			if (armedForOpen) {  // we became unarmed, so reset and disarm
				Circuit ckt = DSSGlobals.getInstance().getActiveCircuit();
				lastEventHandle = ckt.getControlQueue().push(ckt.getSolution().getIntHour(), ckt.getSolution().getDynaVars().t + resetTime, ControlAction.CTRL_RESET, 0, this);
				armedForOpen = false;
			}
		}
	}

	private void overcurrentLogic() {
		int i;
		double CMag;
		Complex CSum;

		double groundTime, phaseTime, tripTime, timeTest;

		if (presentState == ControlAction.CLOSE) {
			tripTime = -1.0;
			groundTime = -1.0;
			phaseTime = -1.0;  /* No trip */

			// check largest current of all phases of monitored element
			monitoredElement.getCurrents(cBuffer);

			/* Check ground trip, if any */
			if (((groundCurve != null) || (delayTime > 0.0)) && (groundTrip > 0.0)) {
				CSum = Complex.ZERO;
				for (i = (1 + condOffset); i < (nPhases + condOffset); i++)  // TODO Check zero based indexing
					CSum = CSum.add(cBuffer[i]);
				CMag  = CSum.abs();
				if ((groundInst > 0.0) && (CMag >= groundInst) && (operationCount == 1)) {
					groundTime = 0.01 + breakerTime;  // inst trip on first operation
				} else {
					if (delayTime > 0.0) {  // definite time ground relay
						if (CMag >= groundTrip) {
							groundTime = delayTime;
						} else {
							groundTime = -1.0;
						}
					} else {
						groundTime = TDGround * groundCurve.getTCCTime(CMag / groundTrip);
					}
				}
			}

			if (groundTime > 0.0) {
				tripTime = groundTime;
				groundTarget = true;
			}

			// if groundTime > 0 then we have a ground trip

			/* Check phase trip, if any */

			if (((phaseCurve != null) || (delayTime > 0.0)) && (phaseTrip > 0.0)) {
				for (i = (1 + condOffset); i < (nPhases + condOffset); i++) {  // TODO Check zero based indexing
					CMag = cBuffer[i].abs();
					if ((phaseInst > 0.0) && (CMag >= phaseInst) && (operationCount == 1)) {
						phaseTime = 0.01 + breakerTime;  // inst trip on first operation
						break;  /* if inst, no sense checking other phases */
					} else {
						if (delayTime > 0.0) {  // definite time phase relay
							if (CMag >= phaseTrip) {
								timeTest = delayTime;
							} else {
								timeTest = -1.0;
							}
						} else {
							timeTest = TDPhase * phaseCurve.getTCCTime(CMag / phaseTrip);
						}
						if (timeTest > 0.0) {
							if (phaseTime < 0.0) {
								phaseTime = timeTest;
							} else {
								phaseTime = Math.min(phaseTime, timeTest);
							}
						}
					}
				}
			}

			// if phaseTime > 0 then we have a phase trip
			if (phaseTime > 0.0) {
				phaseTarget = true;
				if (tripTime > 0.0) {
					tripTime = Math.min(tripTime, phaseTime);
				} else {
					tripTime = phaseTime;
				}
			}

			if (tripTime > 0.0) {
				if (!armedForOpen) {
					Circuit ckt = DSSGlobals.getInstance().getActiveCircuit();
					// then arm for an open operation
					relayTarget = "";
					if (phaseTime > 0.0)
						relayTarget = relayTarget + "Ph";
					if (groundTime > 0.0)
						relayTarget = relayTarget + " Gnd";
					lastEventHandle = ckt.getControlQueue().push(ckt.getSolution().getIntHour(), ckt.getSolution().getDynaVars().t + tripTime + breakerTime, ControlAction.OPEN, 0, this);
					if (operationCount <= numReclose)
						lastEventHandle = ckt.getControlQueue().push(ckt.getSolution().getIntHour(), ckt.getSolution().getDynaVars().t + tripTime + breakerTime + recloseIntervals[operationCount], ControlAction.CLOSE, 0, this);
					armedForOpen = true;
					armedForClose = true;
				}
			} else {
				if (armedForOpen) {
					Circuit ckt = DSSGlobals.getInstance().getActiveCircuit();
					// if current dropped below pickup, disarm trip and set for reset
					lastEventHandle = ckt.getControlQueue().push(ckt.getSolution().getIntHour(), ckt.getSolution().getDynaVars().t + resetTime, ControlAction.CTRL_RESET, 0, this);
					armedForOpen  = false;
					armedForClose = false;
					phaseTarget   = false;
					groundTarget  = false;
				}
			}
		}
	}

	private void revPowerLogic() {

		//MonitoredElement.ActiveTerminalIdx = MonitoredElementTerminal;
		Complex S = monitoredElement.getPower(monitoredElementTerminal);
		if (S.getReal() < 0.0) {
			if (Math.abs(S.getReal()) > phaseInst * 1000.0) {
				if (!armedForOpen) {  // push the trip operation and arm to trip
					Circuit ckt = DSSGlobals.getInstance().getActiveCircuit();
					relayTarget = "Rev P";
					lastEventHandle = ckt.getControlQueue().push(ckt.getSolution().getIntHour(), ckt.getSolution().getDynaVars().t + delayTime +  breakerTime, ControlAction.OPEN, 0, this);
					operationCount = numReclose + 1;  // force a lockout
					armedForOpen = true;
				}
			} else {
				if (armedForOpen) {  // we became unarmed, so reset and disarm
					Circuit ckt = DSSGlobals.getInstance().getActiveCircuit();
					lastEventHandle = ckt.getControlQueue().push(ckt.getSolution().getIntHour(), ckt.getSolution().getDynaVars().t + resetTime, ControlAction.CTRL_RESET, 0, this);
					armedForOpen = false;
				}
			}
		}
	}

	private void voltageLogic() {
		int i;
		double VMax, VMin, VMag = 0;
		double OVTime, UVTime, tripTime;

		if (!lockedOut) {
			/* *** Fix so that fastest trip time applies *** */
			monitoredElement.getTermVoltages(monitoredElementTerminal, cBuffer);

			VMin = 1.e50;
			VMax = 0.0;
			for (i = 0; i < monitoredElement.getNPhases(); i++)
				VMag = cBuffer[i].abs();
			if (VMag > VMax) VMax = VMag;
			if (VMag < VMin) VMin = VMag;

			/* Convert to per unit */
			VMax = VMax / VBase;
			VMin = VMin / VBase;

			if (presentState == ControlAction.CLOSE) {
				tripTime = -1.0;
				OVTime = -1.0;
				UVTime = -1.0;

				/* Check over voltage trip, if any */
				if (OVCurve != null) OVTime = OVCurve.getOVTime(VMax);

				if (OVTime > 0.0)
					tripTime = OVTime;
				// if OVTime > 0 then we have a OV trip

				/* Check UV trip, if any */
				if (UVCurve != null)
					UVTime = UVCurve.getUVTime(VMin);

				// If UVTime > 0 then we have a UV trip
				if (UVTime > 0.0) {
					if (tripTime > 0.0) {
						tripTime = Math.min(tripTime, UVTime);  // min of UV or OV time
					} else {
						tripTime = UVTime;
					}
				}

				if (tripTime > 0.0) {
					Circuit ckt = DSSGlobals.getInstance().getActiveCircuit();

					if (armedForOpen && ((ckt.getSolution().getDynaVars().t + tripTime + breakerTime) < nextTripTime)) {
						ckt.getControlQueue().delete(lastEventHandle);  // delete last event from queue
						armedForOpen = false;  // force it to go through next if
					}

					if (!armedForOpen) {
						// then arm for an open operation
						if (tripTime == UVTime) {
							if (tripTime == OVTime) {
								relayTarget = "UV + OV";
							} else {
								relayTarget = "UV";
							}
						} else {
							relayTarget = "OV";
						}

						nextTripTime = ckt.getSolution().getDynaVars().t + tripTime + breakerTime;
						lastEventHandle = ckt.getControlQueue().push(ckt.getSolution().getIntHour(), nextTripTime, ControlAction.OPEN, 0, this);
						armedForOpen = true;
					}
				} else {
					if (armedForOpen) {
						Circuit ckt = DSSGlobals.getInstance().getActiveCircuit();
						// if voltage dropped below pickup, disarm trip and set for reset
						ckt.getControlQueue().delete(lastEventHandle);  // delete last event from queue
						nextTripTime = -1.0;
						lastEventHandle = ckt.getControlQueue().push(ckt.getSolution().getIntHour(), ckt.getSolution().getDynaVars().t + resetTime, ControlAction.CTRL_RESET, 0, this);
						armedForOpen = false;
					}
				}
			} else {
				/* Present state is open, check for voltage and then set reclose interval */
				if (operationCount <= numReclose) {
					if (!armedForClose) {
						if (VMax > 0.9) {
							Circuit ckt = DSSGlobals.getInstance().getActiveCircuit();
							// OK if voltage > 90%
							lastEventHandle = ckt.getControlQueue().push(ckt.getSolution().getIntHour(), ckt.getSolution().getDynaVars().t + recloseIntervals[operationCount], ControlAction.CLOSE, 0, this);
							armedForClose = true;
						}
					} else {
						/* Armed, but check to see if voltage dropped before it reclosed and cancel action */
						if (VMax < 0.9)
							armedForClose = false;
					}
				}
			}
		}
	}

	/**
	 * Neg seq voltage relay.
	 */
	private void negSeq47Logic() {
		double negSeqVoltageMag;
		Complex[] V012 = new Complex[3];

		monitoredElement.getTermVoltages(monitoredElementTerminal, cBuffer);
		MathUtil.phase2SymComp(cBuffer, V012);  // phase to symmetrical components
		negSeqVoltageMag = V012[2].abs();
		if (negSeqVoltageMag >= pickupVolts47) {
			if (!armedForOpen) {  // push the trip operation and arm to trip
				Circuit ckt = DSSGlobals.getInstance().getActiveCircuit();
				relayTarget = "-Seq V";
				lastEventHandle = ckt.getControlQueue().push(ckt.getSolution().getIntHour(), ckt.getSolution().getDynaVars().t + delayTime + breakerTime, ControlAction.OPEN, 0, this);
				operationCount = numReclose + 1;  // force a lockout
				armedForOpen = true;
			}
		} else {
			/* Less than pickup value: reset if armed */
			if (armedForOpen) {  // we became unarmed, so reset and disarm
				Circuit ckt = DSSGlobals.getInstance().getActiveCircuit();
				lastEventHandle = ckt.getControlQueue().push(ckt.getSolution().getIntHour(), ckt.getSolution().getDynaVars().t + resetTime, ControlAction.CTRL_RESET, 0, this);
				armedForOpen = false;
			}
		}
	}

	// FIXME Private members in OpenDSS

	public int getControlType() {
		return controlType;
	}

	public void setControlType(int type) {
		controlType = type;
	}

	public TCC_CurveObj getPhaseCurve() {
		return phaseCurve;
	}

	public void setPhaseCurve(TCC_CurveObj curve) {
		phaseCurve = curve;
	}

	public TCC_CurveObj getGroundCurve() {
		return groundCurve;
	}

	public void setGroundCurve(TCC_CurveObj curve) {
		groundCurve = curve;
	}

	public double getPhaseTrip() {
		return phaseTrip;
	}

	public void setPhaseTrip(double trip) {
		phaseTrip = trip;
	}

	public double getGroundTrip() {
		return groundTrip;
	}

	public void setGroundTrip(double trip) {
		groundTrip = trip;
	}

	public double getPhaseInst() {
		return phaseInst;
	}

	public void setPhaseInst(double value) {
		phaseInst = value;
	}

	public double getGroundInst() {
		return groundInst;
	}

	public void setGroundInst(double value) {
		groundInst = value;
	}

	public double[] getRecloseIntervals() {
		return recloseIntervals;
	}

	public void setRecloseIntervals(double[] intervals) {
		recloseIntervals = intervals;
	}

	public int getNumReclose() {
		return numReclose;
	}

	public void setNumReclose(int num) {
		numReclose = num;
	}

	public double getResetTime() {
		return resetTime;
	}

	public void setResetTime(double time) {
		resetTime = time;
	}

	public double getDelayTime() {
		return delayTime;
	}

	public void setDelayTime(double time) {
		delayTime = time;
	}

	public double getBreakerTime() {
		return breakerTime;
	}

	public void setBreakerTime(double time) {
		breakerTime = time;
	}

	public double getTDPhase() {
		return TDPhase;
	}

	public void setTDPhase(double phase) {
		TDPhase = phase;
	}

	public double getTDGround() {
		return TDGround;
	}

	public void setTDGround(double ground) {
		TDGround = ground;
	}

	public String getRelayTarget() {
		return relayTarget;
	}

	public void setRelayTarget(String target) {
		relayTarget = target;
	}

	public TCC_CurveObj getOVCurve() {
		return OVCurve;
	}

	public void setOVCurve(TCC_CurveObj curve) {
		OVCurve = curve;
	}

	public TCC_CurveObj getUVCurve() {
		return UVCurve;
	}

	public void setUVCurve(TCC_CurveObj curve) {
		UVCurve = curve;
	}

	public double getVBase() {
		return VBase;
	}

	public void setVBase(double base) {
		VBase = base;
	}

	public double getKVBase() {
		return kVBase;
	}

	public void setKVBase(double base) {
		this.kVBase = base;
	}

	public double getPickupAmps46() {
		return pickupAmps46;
	}

	public void setPickupAmps46(double value) {
		pickupAmps46 = value;
	}

	public double getPctPickup46() {
		return pctPickup46;
	}

	public void setPctPickup46(double value) {
		pctPickup46 = value;
	}

	public double getBaseAmps46() {
		return baseAmps46;
	}

	public void setBaseAmps46(double value) {
		baseAmps46 = value;
	}

	public double getIsqt46() {
		return isqt46;
	}

	public void setIsqt46(double value) {
		isqt46 = value;
	}

	public double getPickupVolts47() {
		return pickupVolts47;
	}

	public void setPickupVolts47(double value) {
		pickupVolts47 = value;
	}

	public double getPctPickup47() {
		return pctPickup47;
	}

	public void setPctPickup47(double value) {
		pctPickup47 = value;
	}

	public double getOverTrip() {
		return overTrip;
	}

	public void setOverTrip(double trip) {
		overTrip = trip;
	}

	public double getUnderTrip() {
		return underTrip;
	}

	public void setUnderTrip(double trip) {
		underTrip = trip;
	}

	public String getMonitoredElementName() {
		return monitoredElementName;
	}

	public void setMonitoredElementName(String name) {
		monitoredElementName = name;
	}

	public int getMonitoredElementTerminal() {
		return monitoredElementTerminal;
	}

	public void setMonitoredElementTerminal(int terminal) {
		monitoredElementTerminal = terminal;
	}

	public CktElement getMonitoredElement() {
		return monitoredElement;
	}

	public void setMonitoredElement(CktElement element) {
		monitoredElement = element;
	}

	public ControlAction getPresentState() {
		return presentState;
	}

	public void setPresentState(ControlAction state) {
		presentState = state;
	}

	public int getOperationCount() {
		return operationCount;
	}

	public void setOperationCount(int count) {
		operationCount = count;
	}

	public boolean isLockedOut() {
		return lockedOut;
	}

	public void setLockedOut(boolean value) {
		lockedOut = value;
	}

	public boolean isArmedForClose() {
		return armedForClose;
	}

	public void setArmedForClose(boolean armed) {
		armedForClose = armed;
	}

	public boolean isArmedForOpen() {
		return armedForOpen;
	}

	public void setArmedForOpen(boolean armed) {
		armedForOpen = armed;
	}

	public boolean isPhaseTarget() {
		return phaseTarget;
	}

	public void setPhaseTarget(boolean target) {
		phaseTarget = target;
	}

	public boolean isGroundTarget() {
		return groundTarget;
	}

	public void setGroundTarget(boolean target) {
		groundTarget = target;
	}

	public double getNextTripTime() {
		return nextTripTime;
	}

	public void setNextTripTime(double time) {
		nextTripTime = time;
	}

	public int getLastEventHandle() {
		return lastEventHandle;
	}

	public void setLastEventHandle(int handle) {
		lastEventHandle = handle;
	}

	public int getCondOffset() {
		return condOffset;
	}

	public void setCondOffset(int offset) {
		condOffset = offset;
	}

	public Complex[] getCBuffer() {
		return cBuffer;
	}

	public void setCBuffer(Complex[] buffer) {
		this.cBuffer = buffer;
	}

}
