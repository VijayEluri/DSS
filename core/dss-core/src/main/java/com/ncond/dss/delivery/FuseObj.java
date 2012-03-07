package com.ncond.dss.delivery;

import java.io.OutputStream;
import java.io.PrintWriter;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.apache.commons.math.complex.Complex;

import com.ncond.dss.common.Circuit;
import com.ncond.dss.common.CktElement;
import com.ncond.dss.common.DSS;
import com.ncond.dss.common.DSSClass;
import com.ncond.dss.common.Util;
import com.ncond.dss.control.ControlAction;
import com.ncond.dss.control.ControlElem;
import com.ncond.dss.general.TCC_CurveObj;

/**
 * A control element that is connected to a terminal of a
 * circuit element and controls the switches in the same or another terminal.
 *
 * The control is usually placed in the terminal of a line or transformer,
 * but it could be any element.
 *
 * CktElement to be controlled must already exist.
 *
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class FuseObj extends ControlElem {

	private TCC_CurveObj fuseCurve;

	private double ratedCurrent;

	private double delayTime;

	private String monitoredElementName;
	private int monitoredElementTerminal;
	private CktElement monitoredElement;

	/* Handle to control queue actions */
	private int[] hAction = new int[Fuse.FUSEMAXDIM];
	/* 0 = open 1 = close */
	private ControlAction[] presentState = new ControlAction[Fuse.FUSEMAXDIM];
	private boolean[] readyToBlow = new boolean[Fuse.FUSEMAXDIM];

	/* Offset for monitored terminal */
	private int condOffset;
	private Complex[] cBuffer;

	public FuseObj(DSSClass parClass, String fuseName) {
		super(parClass);

		int i;

		setName(fuseName.toLowerCase());
		objType = parClass.getClassType();

		setNumPhases(3);  // directly set conds and phases
		nConds = 3;
		setNumTerms(1);  // this forces allocation of terminals and conductors in base class

		elementName = "";
		setControlledElement(null);
		elementTerminal = 0;

		monitoredElementName = "";
		monitoredElementTerminal = 0;
		monitoredElement = null;

		fuseCurve = Fuse.getTccCurve("tlink");

		ratedCurrent = 1.0;

		for (i = 0; i < Math.min(Fuse.FUSEMAXDIM, getNumPhases()); i++)
			presentState[i] = ControlAction.CLOSE;
		for (i = 0; i < Math.min(Fuse.FUSEMAXDIM, getNumPhases()); i++)
			readyToBlow[i] = false;
		for (i = 0; i < Math.min(Fuse.FUSEMAXDIM, getNumPhases()); i++)
			hAction[i] = 0;

		cBuffer = null;  // complex buffer

		objType = parClass.getClassType(); //CAP_CONTROL;

		initPropertyValues(0);

		//recalcElementData();
	}

	@Override
	public void recalcElementData() {
		int i;

		int devIndex = Util.getCktElementIndex(monitoredElementName);
		if (devIndex >= 0) {
			monitoredElement = (CktElement) DSS.activeCircuit.getCktElements().get(devIndex);
			setNumPhases( monitoredElement.getNumPhases() );  // force number of phases to be same
			if (getNumPhases() > Fuse.FUSEMAXDIM)
				DSS.doSimpleMsg("Warning: Fuse "+getName()+": Number of phases > max fuse dimension.", 404);
			if (monitoredElementTerminal >= monitoredElement.getNumTerms()) {
				DSS.doErrorMsg("Fuse: \"" + getName() + "\"",
						"Terminal no. \"" +"\" does not exist.",
						"Re-specify terminal no.", 404);
			} else {
				// sets name of i-th terminal's connected bus in fuse's bus list
				setBus(0, monitoredElement.getBus(monitoredElementTerminal));
				// allocate a buffer big enough to hold everything from the monitored element
				cBuffer = Util.resizeArray(cBuffer, monitoredElement.getYOrder());
				condOffset = monitoredElementTerminal * monitoredElement.getNumConds();  // for speedy sampling
			}
		}

		/* Check for existence of controlled element */

		devIndex = Util.getCktElementIndex(elementName);
		if (devIndex >= 0) {  // both CktElement and monitored element must already exist
			setControlledElement( DSS.activeCircuit.getCktElements().get(devIndex) );
			getControlledElement().setActiveTerminalIdx(elementTerminal);  // make the 1st terminal active

			for (i = 0; i < Math.min(Fuse.FUSEMAXDIM, getControlledElement().getNumPhases()); i++) {
				if (getControlledElement().isConductorClosed(i)) {  // check state of i-th phase of active terminal
					presentState[i] = ControlAction.CLOSE;
				} else {
					presentState[i] = ControlAction.OPEN;
				}
			}
			for (i = 0; i < getControlledElement().getNumPhases(); i++)
				hAction[i] = 0;
			for (i = 0; i < Math.min(Fuse.FUSEMAXDIM, getControlledElement().getNumPhases()); i++)
				readyToBlow[i] = false;
		} else {
			setControlledElement(null);  // element not found
			DSS.doErrorMsg("Fuse: \"" + getName() + "\"", "CktElement Element \""+ elementName + "\" not found.",
					" Element must be defined previously.", 405);
		}
	}

	/**
	 * Always zero for a fuse.
	 */
	@Override
	public void calcYPrim() {
		// leave YPrims as null and they will be ignored
	}

	/**
	 * Get present value of terminal current.
	 */
	@Override
	public void getCurrents(Complex[] curr) {
		for (int i = 0; i < getNumConds(); i++)
			curr[i] = Complex.ZERO;
	}

	/**
	 * Returns injection currents.
	 */
	@Override
	public void getInjCurrents(Complex[] curr) {
		for (int i = 0; i < getNumConds(); i++)
			curr[i] = Complex.ZERO;
	}

	/**
	 * Do the action that is pending from last sample.
	 *
	 * Theoretically, there shouldn't be anything on the queue unless we have to do something.
	 *
	 * Only legal action is to open one phase.
	 */
	@Override
	public void doPendingAction(int phs, int proxyHdl) {
		if (phs <= Fuse.FUSEMAXDIM) {
			getControlledElement().setActiveTerminalIdx(elementTerminal);  // set active terminal of CktElement to terminal 1
			switch (presentState[phs]) {
			case CLOSE:
				if (readyToBlow[phs]) {  // ignore if we became disarmed in meantime
					getControlledElement().setConductorClosed(phs, false);  // open all phases of active terminal
					Util.appendToEventLog("Fuse." + getName(), "Phase "+String.valueOf(phs)+" Blown");
					hAction[phs] = 0;
				}
				break;
			default:
				// do nothing
				break;
			}
		}
	}

	public void interpretFuseAction(String action) {
		if (getControlElement() != null) {
			getControlledElement().setActiveTerminalIdx(elementTerminal);  // set active terminal
			switch (action.toLowerCase().charAt(0)) {
			case 'o':
				getControlledElement().setConductorClosed(0, false);  // open all phases of active terminal   TODO Check zero based indexing
				break;
			case 't':
				getControlledElement().setConductorClosed(0, false);
				break;
			case 'c':
				getControlledElement().setConductorClosed(0, true);  // close all phases of active terminal
				break;
			}
		}
	}

	/**
	 * Sample control quantities and set action times in control queue.
	 */
	@Override
	public void sample() {
		double CMag;
		double tripTime;

		getControlledElement().setActiveTerminalIdx(elementTerminal);
		monitoredElement.getCurrents(cBuffer);

		for (int i = 0; i < Math.min(Fuse.FUSEMAXDIM, monitoredElement.getNumPhases()); i++) {
			if (getControlledElement().isConductorClosed(i)) {  // check state of phases of active terminal
				presentState[i] = ControlAction.CLOSE;
			} else {
				presentState[i] = ControlAction.OPEN;
			}

			if (presentState[i] == ControlAction.CLOSE) {
				tripTime = -1.0;

				/* Check phase trip, if any */

				if (fuseCurve != null) {
					CMag = cBuffer[i].abs();
					tripTime = fuseCurve.getTCCTime(CMag / ratedCurrent);
				}

				if (tripTime > 0.0) {
					if (!readyToBlow[i]) {
						Circuit ckt = DSS.activeCircuit;
						// then arm for an open operation
						hAction[i] = ckt.getControlQueue().push(ckt.getSolution().getIntHour(),
								ckt.getSolution().getDynaVars().t + tripTime + delayTime, i, 0, this);
						readyToBlow[i] = true;
					}
				} else {
					if (readyToBlow[i]) {
						// current has dropped below pickup and it hasn't blown yet
						DSS.activeCircuit.getControlQueue().delete(hAction[i]);  // delete the fuse blow action
						readyToBlow[i] = false;
					}
				}
			}
		}
	}

	@Override
	public void dumpProperties(OutputStream out, boolean complete) {
		super.dumpProperties(out, complete);

		PrintWriter pw = new PrintWriter(out);

		for (int i = 0; i < getParentClass().getNumProperties(); i++)
			pw.println("~ " + getParentClass().getPropertyName(i) +
				"=" + getPropertyValue(i));

		if (complete) pw.println();

		pw.close();
	}

	@Override
	public String getPropertyValue(int index) {
		return super.getPropertyValue(index);
	}

	/**
	 * Reset to initial defined state.
	 */
	@Override
	public void reset() {
		int i;

		if (getControlledElement() != null) {
			for (i = 0; i < Math.min(Fuse.FUSEMAXDIM, getControlledElement().getNumPhases()); i++)
				presentState[i] = ControlAction.CLOSE;
			for (i = 0; i < Math.min(Fuse.FUSEMAXDIM, getControlledElement().getNumPhases()); i++)
				readyToBlow[i] = false;
			for (i = 0; i < Math.min(Fuse.FUSEMAXDIM, getControlledElement().getNumPhases()); i++)
				hAction[i] = 0;
			getControlledElement().setActiveTerminalIdx(elementTerminal);  // set active terminal
			getControlledElement().setConductorClosed(0, true);            // close all phases of active terminal
		}
	}

	@Override
	public void initPropertyValues(int arrayOffset) {
		setPropertyValue(0, "");   // "element";
		setPropertyValue(1, "1");  // "terminal";
		setPropertyValue(2, "");
		setPropertyValue(3, "1");  // "terminal";
		setPropertyValue(4, "Tlink");
		setPropertyValue(5, "1.0");
		setPropertyValue(6, "0");
		setPropertyValue(7, "");

		super.initPropertyValues(Fuse.NumPropsThisClass - 1);
	}

	@Override
	public int injCurrents() {
		throw new UnsupportedOperationException();
	}

}
