package com.ncond.dss.control;

import com.ncond.dss.common.CktElement;
import com.ncond.dss.control.impl.ControlAction;
import com.ncond.dss.general.TCC_CurveObj;

import org.apache.commons.math.complex.Complex;

/**
 * A control element that is connected to a terminal of a
 * circuit element and controls the switches in the same or another terminal.
 *
 * The control is usually placed in the terminal of a line or transformer, but
 * it could be any element.
 *
 * CktElement to be controlled must already exist.
 *
 */
public interface RecloserObj extends ControlElem {

	// FIXME Private members in Open DSS

	TCC_CurveObj getPhaseDelayed();

	void setPhaseDelayed(TCC_CurveObj phaseDelayed);

	TCC_CurveObj getGroundDelayed();

	void setGroundDelayed(TCC_CurveObj groundDelayed);

	TCC_CurveObj getPhaseFast();

	void setPhaseFast(TCC_CurveObj phaseFast);

	TCC_CurveObj getGroundFast();

	void setGroundFast(TCC_CurveObj groundFast);

	double getPhaseTrip();

	void setPhaseTrip(double phaseTrip);

	double getGroundTrip();

	void setGroundTrip(double groundTrip);

	double getPhaseInst();

	void setPhaseInst(double phaseInst);

	double getGroundInst();

	void setGroundInst(double groundInst);

	double[] getRecloseIntervals();

	void setRecloseIntervals(double[] recloseIntervals);

	int getNumFast();

	void setNumFast(int numFast);

	int getNumReclose();

	void setNumReclose(int numReclose);

	double getResetTime();

	void setResetTime(double resetTime);

	double getDelayTime();

	void setDelayTime(double delayTime);

	double getTDGrDelayed();

	void setTDGrDelayed(double TDGrDelayed);

	double getTDPhDelayed();

	void setTDPhDelayed(double TDPhDelayed);

	double getTDGrFast();

	void setTDGrFast(double TDGrFast);

	double getTDPhFast();

	void setTDPhFast(double TDPhFast);

	String getMonitoredElementName();

	void setMonitoredElementName(String monitoredElementName);

	int getMonitoredElementTerminal();

	void setMonitoredElementTerminal(int monitoredElementTerminal);

	CktElement getMonitoredElement();

	void setMonitoredElement(CktElement monitoredElement);

	ControlAction getPresentState();

	void setPresentState(ControlAction presentState);

	int getOperationCount();

	void setOperationCount(int operationCount);

	boolean isLockedOut();

	void setLockedOut(boolean lockedOut);

	boolean isArmedForClose();

	void setArmedForClose(boolean armedForClose);

	boolean isArmedForOpen();

	void setArmedForOpen(boolean armedForOpen);

	boolean isGroundTarget();

	void setGroundTarget(boolean groundTarget);

	boolean isPhaseTarget();

	void setPhaseTarget(boolean phaseTarget);

	int getCondOffset();

	void setCondOffset(int condOffset);

	Complex[] getCBuffer();

	void setCBuffer(Complex[] cBuffer);

	// FIXME Private method in OpenDSS
	void interpretRecloserAction(String param);

}
