package com.epri.dss.common.impl;

import com.epri.dss.common.Conductor;
import com.epri.dss.common.Terminal;

public class PowerTerminal implements Terminal {

	private int NumCond;
	private int ActiveConductor;

	protected int BusRef;

	protected int[] TermNodeRef;
	protected Conductor[] Conductors;
	protected boolean Checked;

	public PowerTerminal(int Ncond) {

	}

	public int getBusRef() {
		return BusRef;
	}

	public void setBusRef(int busRef) {
		BusRef = busRef;
	}

	public int[] getTermNodeRef() {
		return TermNodeRef;
	}

	public void setTermNodeRef(int[] termNodeRef) {
		TermNodeRef = termNodeRef;
	}

	public Conductor[] getConductors() {
		return Conductors;
	}

	public void setConductors(Conductor[] conductors) {
		Conductors = conductors;
	}

	public boolean isChecked() {
		return Checked;
	}

	public void setChecked(boolean checked) {
		Checked = checked;
	}

	public void setActiveConductor(int Value) {

	}

	public int getActiveConductor() {
		return 0;
	}
}
