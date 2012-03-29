/**
 * Copyright (C) 2008-2012 Electric Power Research Institute, Inc.
 * Copyright (C) 2009-2012 Richard Lincoln
 * All rights reserved.
 */
package com.ncond.dss.control;

import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.commons.math.complex.Complex;

import com.ncond.dss.common.Circuit;
import com.ncond.dss.common.CktElement;
import com.ncond.dss.common.DSS;
import com.ncond.dss.common.DSSClass;
import com.ncond.dss.common.DSSClassDefs;
import com.ncond.dss.conversion.PCElement;
import com.ncond.dss.general.TCC_CurveObj;

import static com.ncond.dss.shared.MathUtil.phase2SymComp;

import static com.ncond.dss.common.Util.appendToEventLog;
import static com.ncond.dss.common.Util.getCktElementIndex;
import static com.ncond.dss.common.Util.resizeArray;

import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.pow;

import static java.lang.String.format;


/**
 * A control element that is connected to a terminal of a
 * circuit element and controls the switches in the same or another terminal.
 *
 * The control is usually placed in the terminal of a line or transformer,
 * but it could be any element.
 *
 *   new relay.name=myName element=devClass.name terminal=[ 1|2|...] switch=devClass.name terminal=[ 1|2|...]
 *   type = [current | voltage]
 *   phase = TCCCurve
 *   ground = TCCCurve
 *   overVolt = TCCcurve
 *   underVolt = TCCCurve
 *   phaseTrip =  Multipliers times curve
 *   groundTrip =
 *   phaseInst  =
 *   groundInst =
 *   recloseIntervals = (array of times, sec);
 *   resetTime =
 *
 * CktElement to be controlled must already exist.
 *
 * Voltage relay is a definite time relay that operates after the voltage
 * stays out of bounds for a fixed time interval.  It will then reclose a
 * set time after the voltage comes back in the normal range.
 *
 */
public class RelayObj extends ControlElem {

	private RelayControlType controlType;

	/* Over current relay */
	private TCC_CurveObj phaseCurve, groundCurve;

	private double phaseTrip, groundTrip, phaseInst, groundInst;

	private double[] recloseIntervals;
	private int numReclose;

	private double resetTime, delayTime, breakerTime;
	private double TDPhase, TDGround;

	private String relayTarget;

	/* Over/under voltage relay */
	// Curves assumed in per unit of base voltage
	private TCC_CurveObj OVCurve, UVCurve;

	private double VBase,   // line-neut volts base
		kVBase;

	/* 46 relay neg seq current */
	private double pickupAmps46, pctPickup46, baseAmps46, isqt46;

	/* 47 relay */
	private double pickupVolts47, pctPickup47;

	/* Generic relay */
	private double overTrip, underTrip;

	private String monitoredElementName;
	private int monitoredElementTerminalIdx;
	private CktElement monitoredElement;

	private ControlAction presentState;

	private int operationCount;

	private boolean lockedOut, armedForClose, armedForOpen, phaseTarget, groundTarget;

	private double nextTripTime;
	private int lastEventHandle;

	private int condOffset;  // Offset for monitored terminal

	private Complex[] cBuffer;

	public RelayObj(DSSClass parClass, String relayName) {
		super(parClass);

		setName(relayName.toLowerCase());
		objType = parClass.getClassType();

		setNumPhases(3);  // directly set conds and phases
		nConds = 3;
		setNumTerms(1);   // this forces allocation of terminals and conductors in base class

		elementName = "";
		setControlledElement(null);
		elementTerminalIdx = 0;

		monitoredElementName = "";
		monitoredElementTerminalIdx = 0;
		monitoredElement = null;

		relayTarget = "";

		phaseCurve = null;
		groundCurve = null;
		OVCurve = null;
		UVCurve = null;
		phaseTrip = 1.0;
		groundTrip = 1.0;
		TDPhase = 1.0;
		TDGround = 1.0;
		phaseInst = 0.0;
		groundInst = 0.0;
		resetTime = 15.0;
		numReclose = 3;
		recloseIntervals = null;

		recloseIntervals = resizeArray(recloseIntervals, 4);  // fixed allocation of 4
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

		operationCount = 1;
		lockedOut = false;
		armedForOpen = false;
		armedForClose = false;
		phaseTarget = false;
		groundTarget = false;

		nextTripTime = -1.0;  // not set to trip

		cBuffer = null;

		objType = parClass.getClassType();  // CAP_CONTROL;

		initPropertyValues(0);

		//recalcElementData();
	}

	@Override
	public void recalcElementData() {
		int devIndex = getCktElementIndex(monitoredElementName);

		if (devIndex >= 0) {
			monitoredElement = DSS.activeCircuit.getCktElements().get(devIndex);
			setNumPhases(monitoredElement.getNumPhases());  // force number of phases to be same
			if (monitoredElementTerminalIdx >= monitoredElement.getNumTerms()) {
				DSS.doErrorMsg("Relay: \"" + getName() + "\"",
						"Terminal no. \"" + (monitoredElementTerminalIdx+1) + "\" does not exist.",
						"Re-specify terminal no.", 384);
			} else {
				// sets name of i-th terminal's connected bus in Relay's bus list
				setBus(0, monitoredElement.getBus(monitoredElementTerminalIdx));

				// allocate a buffer big enough to hold everything from the monitored element
				cBuffer = resizeArray(cBuffer, monitoredElement.getYOrder());
				condOffset = (monitoredElementTerminalIdx + 1) * monitoredElement.getNumConds();  // for speedy sampling

				switch (controlType) {
				case GENERIC:
					if ((monitoredElement.getObjType() & DSSClassDefs.BASECLASSMASK) != DSSClassDefs.PC_ELEMENT) {
						DSS.doSimpleMsg("Relay " + getName() + ": Monitored element for generic relay is not a PC element.", 385);
					} else {
						PCElement elem = (PCElement) monitoredElement;
						monitorVarIdx = elem.lookupVariable(monitorVariable);
						if (monitorVarIdx < 0)
							DSS.doSimpleMsg("Relay " + getName() + ": Monitor variable \"" +
									monitorVariable + "\" does not exist.", 386);
					}
					break;
				}
			}
		}

		/* Check for existence of controlled element */
		devIndex = getCktElementIndex(elementName);

		if (devIndex >= 0) {
			// both CktElement and monitored element must already exist
			setControlledElement( DSS.activeCircuit.getCktElements().get(devIndex) );
			getControlledElement().setActiveTerminalIdx(elementTerminalIdx);  // make the 1st terminal active
			if (getControlledElement().isConductorClosed(-1)) {  // check state of phases of active terminal
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
			DSS.doErrorMsg("Relay: \"" + getName() + "\"",
					"CktElement element \"" + elementName + "\" not found.",
					"Element must be defined previously.", 387);
		}

		/* Misc stuff */

		pickupAmps46 = baseAmps46 * pctPickup46 * 0.01;

		switch (nPhases) {
		case 1:
			VBase = kVBase * 1000.0;
			break;
		default:
			VBase = kVBase / DSS.SQRT3 * 1000.0;
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
			setNumPhases(monitoredElement.getNumPhases());
			setNumConds(nPhases);
			setBus(0, monitoredElement.getBus(elementTerminalIdx));
			// allocate a buffer big enough to hold everything from the monitored element
			cBuffer = resizeArray(cBuffer, monitoredElement.getYOrder());
			condOffset = elementTerminalIdx * monitoredElement.getNumConds();  // for speedy sampling
		}

		switch (nPhases) {
		case 1:
			VBase = kVBase * 1000.0;
			break;
		default:
			VBase = kVBase / DSS.SQRT3 * 1000.0;
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
		for (int i = 0; i < nConds; i++) curr[i] = Complex.ZERO;
	}

	@Override
	public void getInjCurrents(Complex[] curr) {
		for (int i = 0; i < nConds; i++) curr[i] = Complex.ZERO;
	}

	/**
	 * Do the action that is pending from last sample.
	 */
	@Override
	public void doPendingAction(int code, int proxyHdl) {
		// set active terminal of CktElement to terminal 1
		getControlledElement().setActiveTerminalIdx(elementTerminalIdx);

		if (code == ControlAction.OPEN.code()) {
			switch (presentState) {
			case CLOSE:
				if (armedForOpen) {
					// ignore if we became disarmed in meantime
					getControlledElement().setConductorClosed(-1, false);  // open all phases of active terminal

					if (operationCount > numReclose) {
						lockedOut = true;
						appendToEventLog("Relay." + getName(),
							"Opened on " + relayTarget + " & Locked Out ");
					}
				} else {
					appendToEventLog("Relay." + getName(), "Opened");
				}
				if (phaseTarget) appendToEventLog(" ", "Phase Target");
				if (groundTarget) appendToEventLog(" ", "Ground Target");
				armedForOpen = false;
				break;
			}
		} else if (code == ControlAction.CLOSE.code()) {
			switch (presentState) {
			case OPEN:
				if (armedForClose && !lockedOut) {
					getControlledElement().setConductorClosed(-1, true);  // close all phases of active terminal
					operationCount += 1;
					appendToEventLog("Relay." + getName(), "Closed");
					armedForClose = false;
				}
				break;
			}
		} else if (code == ControlAction.CTRL_RESET.code()) {
			switch (presentState) {
			case CLOSE:
				if (!armedForOpen) operationCount = 1;  // don't reset if we just rearmed
				break;
			}
		}
	}

	public void interpretRelayAction(String action) {
		if (getControlledElement() != null) {
			getControlledElement().setActiveTerminalIdx(elementTerminalIdx);  // set active terminal

			switch (action.toLowerCase().charAt(0)) {
			case 'o':
				getControlledElement().setConductorClosed(-1, false);  // open all phases of active terminal
				lockedOut = true;
				operationCount = numReclose + 1;
				break;
			case 't':
				getControlledElement().setConductorClosed(-1, false);  // open all phases of active terminal
				lockedOut = true;
				operationCount = numReclose + 1;
				break;
			case 'c':
				getControlledElement().setConductorClosed(-1, true);  // close all phases of active terminal
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
		getControlledElement().setActiveTerminalIdx(elementTerminalIdx);

		if (getControlledElement().isConductorClosed(-1)) {  // check state of phases of active terminal
			presentState = ControlAction.CLOSE;
		} else {
			presentState = ControlAction.OPEN;

			switch (controlType) {
			case CURRENT:
				overcurrentLogic();  // current
				break;
			case VOLTAGE:
				voltageLogic();   // reclosing voltage relay - definite time
				break;
			case REVPOWER:
				revPowerLogic();  // one shot to lockout
				break;
			case NEGCURRENT:
				negSeq46Logic();  // one shot to lockout */
				break;
			case NEGVOLTAGE:
				negSeq47Logic();  // one shot to lockout */
				break;
			case GENERIC:
				genericLogic();   // one shot to lockout */
				break;
			}
		}
	}

	@Override
	public void dumpProperties(OutputStream out, boolean complete) {
		super.dumpProperties(out, complete);

		PrintWriter pw = new PrintWriter(out);

		for (int i = 0; i < getParentClass().getNumProperties(); i++)
			pw.println("~ " + getParentClass().getPropertyName(i) + "=" + getPropertyValue( getParentClass().getPropertyIdxMap(i) ));

		if (complete) pw.println();

		pw.close();
	}

	@Override
	public String getPropertyValue(int index) {
		String val = "";

		switch (getParentClass().getPropertyIdxMap(index)) {
		case 13:
			if (numReclose == 0) {
				val = "NONE";
			} else {
				StringBuffer sb = new StringBuffer("(");
				for (int i = 0; i < numReclose; i++)
					sb.append(format("%g, " , recloseIntervals[i]));
				sb.append(")");
				val = sb.toString();
			}
			break;
		default:
			val = super.getPropertyValue(index);
			break;
		}
		return val;
	}

	/**
	 * Reset to initial defined state.
	 */
	@Override
	public void reset() {
		presentState = ControlAction.CLOSE;
		operationCount = 1;
		lockedOut = false;
		armedForOpen = false;
		armedForClose = false;
		phaseTarget = false;
		groundTarget = false;

		nextTripTime = -1.0;  // not set to trip

		if (getControlledElement() != null) {
			getControlledElement().setActiveTerminalIdx(elementTerminalIdx);  // set active terminal
			getControlledElement().setConductorClosed(-1, true);  // close all phases of active terminal
		}
	}

	@Override
	public void initPropertyValues(int arrayOffset) {
		setPropertyValue(0, "");   // "element";
		setPropertyValue(1, "1");  // "terminal";
		setPropertyValue(2, "");
		setPropertyValue(3, "1");  // "terminal";
		setPropertyValue(4, "current");
		setPropertyValue(5, "");
		setPropertyValue(6, "");
		setPropertyValue(7, "1.0");
		setPropertyValue(8, "1.0");
		setPropertyValue(9, "0.0");
		setPropertyValue(10, "0.0");
		setPropertyValue(11, "15");
		setPropertyValue(12, "4");
		setPropertyValue(13, "(0.5, 2.0, 2.0)");
		setPropertyValue(14, "");
		setPropertyValue(15, "");
		setPropertyValue(16, "0.0");
		setPropertyValue(17, "0.0");
		setPropertyValue(18, "");
		setPropertyValue(19, "");
		setPropertyValue(20, "20");
		setPropertyValue(21, "1");
		setPropertyValue(22, "100");
		setPropertyValue(23, "0");
		setPropertyValue(24, "2");
		setPropertyValue(25, "1.2");
		setPropertyValue(26, "0.8");
		setPropertyValue(27, "1.0");
		setPropertyValue(28, "1.0");

		super.initPropertyValues(Relay.NumPropsThisClass - 1);
	}

	public void interpretRelayType(String s) {
		switch (s.toLowerCase().charAt(0)) {
		case 'c':
			controlType = RelayControlType.CURRENT;
			break;
		case'v':
			controlType = RelayControlType.VOLTAGE;
			break;
		case'r':
			controlType = RelayControlType.REVPOWER;
			break;
		case'4':
			switch (s.charAt(1)) {
			case '6':
				controlType = RelayControlType.NEGCURRENT;
				break;
			case '7':
				controlType = RelayControlType.NEGVOLTAGE;
				break;
			}
		case '8':
			controlType = RelayControlType.GENERIC;
			break;
		default:
			controlType = RelayControlType.CURRENT;
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

		setPropertyValue(23, format("%-.g", delayTime));
	}

	/**
	 * Generic relays only work on PC elements with control terminals.
	 */
	private void genericLogic() {
		Circuit ckt = DSS.activeCircuit;
		PCElement elem = (PCElement) monitoredElement;
		double varValue = elem.getVariable(monitorVarIdx);

		/* Check for trip */
		if (varValue > overTrip || varValue < underTrip) {
			if (!armedForOpen) {  // push the trip operation and arm to trip
				relayTarget = elem.variableName(monitorVarIdx);
				lastEventHandle = ckt.getControlQueue().push(ckt.getSolution().getIntHour(),
					ckt.getSolution().getDynaVars().t + delayTime + breakerTime,
					ControlAction.OPEN, 0, this);
				operationCount = numReclose + 1;  // force a lockout
				armedForOpen = true;
			}
		} else {
			/* Within bounds */
			/* Less than pickup value: reset if armed */
			if (armedForOpen) {  // we became unarmed, so reset and disarm
				lastEventHandle = ckt.getControlQueue().push(ckt.getSolution().getIntHour(),
						ckt.getSolution().getDynaVars().t + resetTime,
						ControlAction.CTRL_RESET, 0, this);
				armedForOpen = false;
			}
		}
	}

	/**
	 * Negative sequence current relay. Patterned after Basler relay.
	 */
	private void negSeq46Logic() {
		Circuit ckt = DSS.activeCircuit;;
		double negSeqCurrentMag, tripTime;
		int offset;
		Complex[] I012 = new Complex[3];

		monitoredElement.setActiveTerminalIdx(monitoredElementTerminalIdx);
		monitoredElement.getCurrents(cBuffer);
		offset = (monitoredElementTerminalIdx + 1) * monitoredElement.getNumConds();  // offset for active terminal
		phase2SymComp(cBuffer[offset + 1], I012);
		negSeqCurrentMag = I012[2].abs();
		if (negSeqCurrentMag >= pickupAmps46) {
			if (!armedForOpen) {  // push the trip operation and arm to trip
				relayTarget = "-Seq Curr";
				/* Simple estimate of trip time assuming current will be constant */
				if (delayTime > 0.0) {
					tripTime = delayTime;
				} else {
					tripTime = isqt46 / pow(negSeqCurrentMag / baseAmps46, 2);  // sec
				}
				lastEventHandle = ckt.getControlQueue().push(ckt.getSolution().getIntHour(),
						ckt.getSolution().getDynaVars().t + tripTime + breakerTime,
						ControlAction.OPEN, 0, this);
				operationCount = numReclose + 1;  // force a lockout
				armedForOpen = true;
			}
		} else {
			/* Less than pickup value: reset if armed */
			if (armedForOpen) {  // we became unarmed, so reset and disarm
				lastEventHandle = ckt.getControlQueue().push(ckt.getSolution().getIntHour(),
						ckt.getSolution().getDynaVars().t + resetTime,
						ControlAction.CTRL_RESET, 0, this);
				armedForOpen = false;
			}
		}
	}

	private void overcurrentLogic() {
		int i;
		double cMag;
		Complex cSum;
		Circuit ckt = DSS.activeCircuit;

		double groundTime, phaseTime, tripTime, timeTest;

		if (presentState == ControlAction.CLOSE) {
			tripTime = -1.0;
			groundTime = -1.0;
			phaseTime = -1.0;  /* No trip */

			// check largest current of all phases of monitored element
			monitoredElement.getCurrents(cBuffer);

			/* Check ground trip, if any */
			if ((groundCurve != null || delayTime > 0.0) && groundTrip > 0.0) {
				cSum = Complex.ZERO;
				for (i = condOffset; i < nPhases + condOffset; i++)
					cSum = cSum.add(cBuffer[i]);
				cMag  = cSum.abs();

				if (groundInst > 0.0 && cMag >= groundInst && operationCount == 1) {
					groundTime = 0.01 + breakerTime;  // inst trip on first operation
				} else {
					if (delayTime > 0.0) {  // definite time ground relay
						if (cMag >= groundTrip) {
							groundTime = delayTime;
						} else {
							groundTime = -1.0;
						}
					} else {
						groundTime = TDGround * groundCurve.getTCCTime(cMag / groundTrip);
					}
				}
			}

			if (groundTime > 0.0) {
				tripTime = groundTime;
				groundTarget = true;
			}

			// if groundTime > 0 then we have a ground trip

			/* Check phase trip, if any */

			if ((phaseCurve != null || delayTime > 0.0) && phaseTrip > 0.0) {
				for (i = condOffset; i < nPhases + condOffset; i++) {
					cMag = cBuffer[i].abs();

					if (phaseInst > 0.0 && cMag >= phaseInst && operationCount == 1) {
						phaseTime = 0.01 + breakerTime;  // inst trip on first operation
						break;  /* if inst, no sense checking other phases */
					} else {
						if (delayTime > 0.0) {  // definite time phase relay
							timeTest = (cMag >= phaseTrip) ? delayTime : -1.0;
						} else {
							timeTest = TDPhase * phaseCurve.getTCCTime(cMag / phaseTrip);
						}
						if (timeTest > 0.0) {
							phaseTime = (phaseTime < 0.0) ? timeTest : min(phaseTime, timeTest);
						}
					}
				}
			}

			// if phaseTime > 0 then we have a phase trip
			if (phaseTime > 0.0) {
				phaseTarget = true;
				tripTime = (tripTime > 0.0) ? min(tripTime, phaseTime) : phaseTime;
			}

			if (tripTime > 0.0) {
				if (!armedForOpen) {
					// then arm for an open operation
					relayTarget = "";
					if (phaseTime > 0.0) relayTarget = relayTarget + "Ph";
					if (groundTime > 0.0) relayTarget = relayTarget + " Gnd";
					lastEventHandle = ckt.getControlQueue().push(ckt.getSolution().getIntHour(),
							ckt.getSolution().getDynaVars().t + tripTime + breakerTime,
							ControlAction.OPEN, 0, this);
					if (operationCount <= numReclose) {
						lastEventHandle = ckt.getControlQueue().push(ckt.getSolution().getIntHour(),
								ckt.getSolution().getDynaVars().t + tripTime + breakerTime + recloseIntervals[operationCount - 1],
								ControlAction.CLOSE, 0, this);
					}
					armedForOpen = true;
					armedForClose = true;
				}
			} else {
				if (armedForOpen) {
					// if current dropped below pickup, disarm trip and set for reset
					lastEventHandle = ckt.getControlQueue().push(ckt.getSolution().getIntHour(),
							ckt.getSolution().getDynaVars().t + resetTime,
							ControlAction.CTRL_RESET, 0, this);
					armedForOpen = false;
					armedForClose = false;
					phaseTarget = false;
					groundTarget = false;
				}
			}
		}
	}

	private void revPowerLogic() {
		Circuit ckt = DSS.activeCircuit;
		Complex S;

		//MonitoredElement.ActiveTerminalIdx = MonitoredElementTerminal;
		S = monitoredElement.getPower(monitoredElementTerminalIdx);
		if (S.getReal() < 0.0) {
			if (abs(S.getReal()) > phaseInst * 1000.0) {
				if (!armedForOpen) {  // push the trip operation and arm to trip
					relayTarget = "Rev P";
					lastEventHandle = ckt.getControlQueue().push(ckt.getSolution().getIntHour(),
							ckt.getSolution().getDynaVars().t + delayTime +  breakerTime,
							ControlAction.OPEN, 0, this);
					operationCount = numReclose + 1;  // force a lockout
					armedForOpen = true;
				}
			} else {
				if (armedForOpen) {  // we became unarmed, so reset and disarm
					lastEventHandle = ckt.getControlQueue().push(ckt.getSolution().getIntHour(),
							ckt.getSolution().getDynaVars().t + resetTime,
							ControlAction.CTRL_RESET, 0, this);
					armedForOpen = false;
				}
			}
		}
	}

	private void voltageLogic() {
		int i;
		double Vmax, Vmin, Vmag = 0;
		double OVTime, UVTime, tripTime;
		Circuit ckt = DSS.activeCircuit;

		if (!lockedOut) {
			/* *** Fix so that fastest trip time applies *** */
			monitoredElement.getTermVoltages(monitoredElementTerminalIdx, cBuffer);

			Vmin = 1.e50;
			Vmax = 0.0;
			for (i = 0; i < monitoredElement.getNumPhases(); i++)
				Vmag = cBuffer[i].abs();

			if (Vmag > Vmax)
				Vmax = Vmag;
			if (Vmag < Vmin)
				Vmin = Vmag;

			/* Convert to per unit */
			Vmax = Vmax / VBase;
			Vmin = Vmin / VBase;

			if (presentState == ControlAction.CLOSE) {
				tripTime = -1.0;
				OVTime = -1.0;
				UVTime = -1.0;

				/* Check over voltage trip, if any */
				if (OVCurve != null)
					OVTime = OVCurve.getOVTime(Vmax);

				if (OVTime > 0.0)
					tripTime = OVTime;
				// if OVTime > 0 then we have a OV trip

				/* Check UV trip, if any */
				if (UVCurve != null)
					UVTime = UVCurve.getUVTime(Vmin);

				// If UVTime > 0 then we have a UV trip
				if (UVTime > 0.0) {
					if (tripTime > 0.0) {
						tripTime = min(tripTime, UVTime);  // min of UV or OV time
					} else {
						tripTime = UVTime;
					}
				}

				if (tripTime > 0.0) {
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
						lastEventHandle = ckt.getControlQueue().push(ckt.getSolution().getIntHour(),
								nextTripTime, ControlAction.OPEN, 0, this);
						armedForOpen = true;
					}
				} else {
					if (armedForOpen) {
						// if voltage dropped below pickup, disarm trip and set for reset
						ckt.getControlQueue().delete(lastEventHandle);  // delete last event from queue
						nextTripTime = -1.0;
						lastEventHandle = ckt.getControlQueue().push(ckt.getSolution().getIntHour(),
								ckt.getSolution().getDynaVars().t + resetTime,
								ControlAction.CTRL_RESET, 0, this);
						armedForOpen = false;
					}
				}
			} else {
				/* Present state is open, check for voltage and then set reclose interval */
				if (operationCount <= numReclose) {
					if (!armedForClose) {
						if (Vmax > 0.9) {
							// OK if voltage > 90%
							lastEventHandle = ckt.getControlQueue().push(ckt.getSolution().getIntHour(),
									ckt.getSolution().getDynaVars().t + recloseIntervals[operationCount],
									ControlAction.CLOSE, 0, this);
							armedForClose = true;
						}
					} else {
						/* Armed, but check to see if voltage dropped before it reclosed and cancel action */
						if (Vmax < 0.9) armedForClose = false;
					}
				}
			}
		}
	}

	/**
	 * Neg seq voltage relay.
	 */
	private void negSeq47Logic() {
		Circuit ckt = DSS.activeCircuit;
		double negSeqVoltageMag;
		Complex[] V012 = new Complex[3];

		monitoredElement.getTermVoltages(monitoredElementTerminalIdx, cBuffer);
		phase2SymComp(cBuffer, V012);  // phase to symmetrical components
		negSeqVoltageMag = V012[2].abs();

		if (negSeqVoltageMag >= pickupVolts47) {
			if (!armedForOpen) {  // push the trip operation and arm to trip
				relayTarget = "-Seq V";
				lastEventHandle = ckt.getControlQueue().push(ckt.getSolution().getIntHour(),
						ckt.getSolution().getDynaVars().t + delayTime + breakerTime,
						ControlAction.OPEN, 0, this);
				operationCount = numReclose + 1;  // force a lockout
				armedForOpen = true;
			}
		} else {
			/* Less than pickup value: reset if armed */
			if (armedForOpen) {  // we became unarmed, so reset and disarm
				lastEventHandle = ckt.getControlQueue().push(ckt.getSolution().getIntHour(),
						ckt.getSolution().getDynaVars().t + resetTime,
						ControlAction.CTRL_RESET, 0, this);
				armedForOpen = false;
			}
		}
	}

	@Override
	public int injCurrents() {
		throw new UnsupportedOperationException();
	}

	public RelayControlType getControlType() {
		return controlType;
	}

	public TCC_CurveObj getPhaseCurve() {
		return phaseCurve;
	}

	public TCC_CurveObj getGroundCurve() {
		return groundCurve;
	}

	public double getPhaseTrip() {
		return phaseTrip;
	}

	public double getGroundTrip() {
		return groundTrip;
	}

	public double getPhaseInst() {
		return phaseInst;
	}

	public double getGroundInst() {
		return groundInst;
	}

	public double[] getRecloseIntervals() {
		return recloseIntervals;
	}

	public int getNumReclose() {
		return numReclose;
	}

	public double getResetTime() {
		return resetTime;
	}

	public double getDelayTime() {
		return delayTime;
	}

	public double getTDPhase() {
		return TDPhase;
	}

	public double getTDGround() {
		return TDGround;
	}

	public TCC_CurveObj getOVCurve() {
		return OVCurve;
	}

	public TCC_CurveObj getUVCurve() {
		return UVCurve;
	}

	public double getKVBase() {
		return kVBase;
	}

	public double getPickupAmps46() {
		return pickupAmps46;
	}

	public double getPctPickup46() {
		return pctPickup46;
	}

	public double getBaseAmps46() {
		return baseAmps46;
	}

	public double getIsqt46() {
		return isqt46;
	}

	public double getPickupVolts47() {
		return pickupVolts47;
	}

	public double getPctPickup47() {
		return pctPickup47;
	}

	public double getOverTrip() {
		return overTrip;
	}

	public double getUnderTrip() {
		return underTrip;
	}

	public String getMonitoredElementName() {
		return monitoredElementName;
	}

	public int getMonitoredElementTerminalIdx() {
		return monitoredElementTerminalIdx;
	}

	public CktElement getMonitoredElement() {
		return monitoredElement;
	}

	public ControlAction getPresentState() {
		return presentState;
	}

	public boolean isLockedOut() {
		return lockedOut;
	}

	public int getCondOffset() {
		return condOffset;
	}

	public double getBreakerTime() {
		return breakerTime;
	}

	public void setBreakerTime(double breakerTime) {
		this.breakerTime = breakerTime;
	}

	public void setControlType(RelayControlType controlType) {
		this.controlType = controlType;
	}

	public void setPhaseCurve(TCC_CurveObj phaseCurve) {
		this.phaseCurve = phaseCurve;
	}

	public void setGroundCurve(TCC_CurveObj groundCurve) {
		this.groundCurve = groundCurve;
	}

	public void setPhaseTrip(double phaseTrip) {
		this.phaseTrip = phaseTrip;
	}

	public void setGroundTrip(double groundTrip) {
		this.groundTrip = groundTrip;
	}

	public void setPhaseInst(double phaseInst) {
		this.phaseInst = phaseInst;
	}

	public void setGroundInst(double groundInst) {
		this.groundInst = groundInst;
	}

	public void setNumReclose(int numReclose) {
		this.numReclose = numReclose;
	}

	public void setDelayTime(double delayTime) {
		this.delayTime = delayTime;
	}

	public void setOVCurve(TCC_CurveObj oVCurve) {
		OVCurve = oVCurve;
	}

	public void setKVBase(double kVBase) {
		this.kVBase = kVBase;
	}

	public void setPickupAmps46(double pickupAmps46) {
		this.pickupAmps46 = pickupAmps46;
	}

	public void setPctPickup46(double pctPickup46) {
		this.pctPickup46 = pctPickup46;
	}

	public void setBaseAmps46(double baseAmps46) {
		this.baseAmps46 = baseAmps46;
	}

	public void setIsqt46(double isqt46) {
		this.isqt46 = isqt46;
	}

	public void setPickupVolts47(double pickupVolts47) {
		this.pickupVolts47 = pickupVolts47;
	}

	public void setPctPickup47(double pctPickup47) {
		this.pctPickup47 = pctPickup47;
	}

	public void setOverTrip(double overTrip) {
		this.overTrip = overTrip;
	}

	public void setMonitoredElementName(String monitoredElementName) {
		this.monitoredElementName = monitoredElementName;
	}

	public void setMonitoredElementTerminalIdx(int monitoredElementTerminalIdx) {
		this.monitoredElementTerminalIdx = monitoredElementTerminalIdx;
	}

	public void setMonitoredElement(CktElement monitoredElement) {
		this.monitoredElement = monitoredElement;
	}

	public void setLockedOut(boolean lockedOut) {
		this.lockedOut = lockedOut;
	}

	public void setCondOffset(int condOffset) {
		this.condOffset = condOffset;
	}

	public void setRecloseIntervals(double[] recloseIntervals) {
		this.recloseIntervals = recloseIntervals;
	}

	public void setResetTime(double resetTime) {
		this.resetTime = resetTime;
	}

	public void setTDPhase(double tDPhase) {
		TDPhase = tDPhase;
	}

	public void setTDGround(double tDGround) {
		TDGround = tDGround;
	}

	public void setUVCurve(TCC_CurveObj uVCurve) {
		UVCurve = uVCurve;
	}

	public void setUnderTrip(double underTrip) {
		this.underTrip = underTrip;
	}

	public void setPresentState(ControlAction presentState) {
		this.presentState = presentState;
	}

}
