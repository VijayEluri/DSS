package com.ncond.dss.common.impl;

import java.io.PrintStream;

import org.apache.commons.math.complex.Complex;

import com.ncond.dss.common.CktElement;
import com.ncond.dss.common.DSSClass;
import com.ncond.dss.common.SolutionObj;
import com.ncond.dss.general.impl.DSSObjectImpl;
import com.ncond.dss.shared.CMatrix;

public class DSSCktElement extends DSSObjectImpl implements CktElement {

	private String[] busNames;
	private boolean enabled;
	private int enabledProperty;
	private int activeTerminalIdx;
	private boolean YPrimInvalid;
	private int handle;

	protected int nTerms;
	/* No. conductors per terminal */
	protected int nConds;
	protected int nPhases;

	protected Complex[] complexBuffer;

	protected int ITerminalSolutionCount;

	protected int busIndex;

	protected CMatrix YPrimSeries;
	protected CMatrix YPrimShunt;
	protected CMatrix YPrim;  // order will be nTerms * nCond

	/* Frequency at which YPrim has been computed */
	protected double YPrimFreq;

	/* Total nodeRef array for element */
	protected int[] nodeRef;
	protected int YOrder;
	/* Flag used in tree searches */
	protected int lastTerminalChecked;

	protected boolean checked, hasEnergyMeter, hasSensorObj, isIsolated,
		hasControl, isPartOfFeeder;

	protected DSSCktElement controlElement;

	protected Complex[] ITerminal;
	protected Complex[] VTerminal;

	protected double baseFrequency;

	protected PowerTerminal[] terminals;

	protected PowerTerminal activeTerminal;

	public DSSCktElement(DSSClass parClass) {
		super(parClass);

		nodeRef     = null;
		YPrimSeries = null;
		YPrimShunt  = null;
		YPrim       = null;
		terminals   = null;
		busNames    = null;
		VTerminal   = null;
		ITerminal   = null;  // present value of terminal current
		complexBuffer = null;

		handle      = -1;
		busIndex    = -1;
		nTerms      = 0;
		nConds      = 0;
		nPhases     = 0;
		objType     = 0;
		YOrder      = 0;

		YPrimInvalid   = true;
		enabled        = true;
		hasEnergyMeter = false;
		hasSensorObj   = false;
		isPartOfFeeder = false;
		isIsolated     = false;

		controlElement = null;  // init to no control on this element
		hasControl     = false;

		activeTerminalIdx   = 0;
		lastTerminalChecked = -1;

		/* Indicates which solution ITemp is computed for */
		ITerminalSolutionCount = -1;

		baseFrequency = DSSGlobals.activeCircuit.getFundamental();
	}

	public void setYPrimInvalid(boolean value) {
		YPrimInvalid = value;
		if (value) {
			// if this device is in the circuit, then we have to rebuild Y on a change in Yprim
			if (enabled)
				DSSGlobals.activeCircuit.getSolution().setSystemYChanged(true);
		}
	}

	public boolean isYprimInvalid() {
		return YPrimInvalid;
	}

	public void setActiveTerminalIdx(int value) {
		if (value >= 0 && value < nTerms) {
			activeTerminalIdx = value;
			activeTerminal = terminals[value] ;
		}
	}

	public int getActiveTerminalIdx() {
		return activeTerminalIdx;
	}

	public void setHandle(int value) {
		handle = value;
	}

	/**
	 * Returns the state of selected conductor.
	 * If index=-1 return true if all phases closed, else false.
	 */
	public boolean getConductorClosed(int index) {
		boolean result;
		if (index == -1) {  // all phases
			result = true;
			for (int i = 0; i < nPhases; i++) {
				if (!terminals[activeTerminalIdx].getConductors()[i].isClosed()) {
					result = false;
					break;
				}
			}
		} else {
			if (index >= 0 && index < nConds) {
				result = terminals[activeTerminalIdx].getConductors()[index].isClosed();
			} else {
				result = false;
			}
		}
		return result;
	}

	public void setConductorClosed(int index, boolean value) {
		if (index == -1) {  // do all conductors
			for (int i = 0; i < nPhases; i++)
				terminals[activeTerminalIdx].getConductors()[i].setClosed(value);
			DSSGlobals.activeCircuit.getSolution().setSystemYChanged(true);  // force Y matrix rebuild
			YPrimInvalid = true;
		} else {
			if (index >= 0 && index < nConds) {
				terminals[activeTerminalIdx].getConductors()[index].setClosed(value);
				DSSGlobals.activeCircuit.getSolution().setSystemYChanged(true);
				YPrimInvalid = true;
			}
		}
	}

	public void setNConds(int value) {
		// check for an almost certain programming error
		if (value <= 0) {
			DSSGlobals.doSimpleMsg(String.format("Invalid number of terminals (%d) for \"%s.%s\"",
					value, parentClass.getName(), getName()), 749);
			return;
		}

		if (value != nConds)
			DSSGlobals.activeCircuit.setBusNameRedefined(true);
		nConds = value;
		setNTerms(this.nTerms);  // realloc terminals; need more efficient way to do this
	}

	public int getNConds() {
		return nConds;
	}

	public void setNPhases(int value) {
		if (value > 0)
			nPhases = value;
	}

	public int getNPhases() {
		return nPhases;
	}

	public void setNTerms(int value) {
		int i;
		String[] newBusNames;

		// check for an almost certain programming error
		if (value <= 0) {
			DSSGlobals.doSimpleMsg(String.format("Invalid number of terminals (%d) for \"%s.%s\"",
								value, parentClass.getName(), getName()), 749);
			return;
		}

		// if value is same as present value, no reallocation necessary;
		// if either nTerms or nConds has changed then reallocate
		if (value != nTerms || value * nConds != YOrder) {

			/* Sanity check */
			if (nConds > 101) {
				DSSGlobals.doSimpleMsg(String.format("Warning: Number of conductors is very large (%d) for circuit element: \"%s.%s." +
						"Possible error in specifying the number of phases for element.",
						nConds, parentClass.getName(), getName()), 750);
			}

			/* Reallocate bus names */
			// because they are strings, we have to do it differently

			if (value < nTerms) {
				busNames = Utilities.resizeArray(busNames, value);  // keeps old values; truncates storage
			} else {
				if (busNames == null) {
					// first allocation
					busNames = new String[value];  // fill with zeros or strings will crash
					for (i = 0; i < value; i++)
						busNames[i] = getName()+'_'+String.valueOf(i);  // make up a bus name to stick in
					// this is so devices like transformers which may be defined on multiple commands
					// will have something in the busNames array
				} else {
					newBusNames = new String[value];  // make some new space
					for (i = 0; i < nTerms; i++)
						newBusNames[i] = busNames[i];  // copy old into new
					for (i = 0; i < nTerms; i++)
						busNames[i] = "";  // decrement usage counts by setting to empty string
					for (i = 0; i < nTerms + 1; i++)
						newBusNames[i] = getName()+'_'+String.valueOf(i);  // make up a bus name to stick in
					busNames = newBusNames;
				}
			}

			/* Reallocate terminals if nConds or nTerms changed */
			if (terminals != null)
				for (i = 0; i < nTerms; i++)
					terminals[i] = null;  // clean up old storage

			terminals = Utilities.resizeArray(terminals, value);

			nTerms = value;  // set new number of terminals
			YOrder = nTerms * nConds;
			VTerminal = Utilities.resizeArray(VTerminal, YOrder);
			ITerminal = Utilities.resizeArray(ITerminal, YOrder);
			complexBuffer = Utilities.resizeArray(complexBuffer, YOrder);  // used by both PD and PC elements

			for (i = 0; i < value; i++)
				terminals[i] = new PowerTerminal(nConds);
		}
	}

	public int getNTerms() {
		return nTerms;
	}

	public void setEnabled(boolean value) {
		if (value != enabled) {  // don't change unless this represents a change
			enabled = value;
			// force rebuilding of Y matrix and bus lists
			DSSGlobals.activeCircuit.setBusNameRedefined(true);
		}
	}

	public boolean isEnabled() {
		return enabled;
	}

	public CMatrix getYPrim(int opt) {
		CMatrix YMatrix = null;

		switch (opt) {
		case DSSGlobals.ALL_YPRIM:
			YMatrix = YPrim;
			break;
		case DSSGlobals.SERIES:
			YMatrix = YPrimSeries;
			break;
		case DSSGlobals.SHUNT:
			YMatrix = YPrimShunt;
			break;
		}

		return YMatrix;
	}

	/**
	 * Returns the storage arrays for fast access.
	 */
	public Complex[] getYPrimValues(int opt) {
		Complex[] result = null;

		switch (opt) {
		case DSSGlobals.ALL_YPRIM:
			if (YPrim != null)
				result = YPrim.asArray();
			break;
		case DSSGlobals.SERIES:
			if (YPrimSeries != null)
				result = YPrimSeries.asArray();
			break;
		case DSSGlobals.SHUNT:
			if (YPrimShunt != null)
				result = YPrimShunt.asArray();
			break;
		}

		return result;
	}

	/**
	 * Get present value of terminal current for reports.
	 */
	public void getCurrents(Complex[] curr) {
		DSSGlobals.doErrorMsg("Something is wrong. Got to base CktElement getCurrents for object:"+DSSGlobals.CRLF+getDSSClassName()+"."+getName(),
				"N/A",
				"Should not be able to get here. Probable programming error.", 751);
	}

	/**
	 * Returns injection currents
	 */
	public void getInjCurrents(Complex[] curr) {
		DSSGlobals.doErrorMsg("Something is wrong. Got to base CktElement getInjCurrents for object:"+DSSGlobals.CRLF+getDSSClassName()+"."+getName(), "****",
				"Should not be able to get here. Probable programming error.", 752);
	}

	public void getLosses(double[] totalLosses, double[] loadLosses,
			double[] NoLoadLosses) {
		Complex totLosses = getLosses();  // watts, vars

		totalLosses[0] = totLosses.getReal();
		totalLosses[1] = totLosses.getImaginary();

		loadLosses[0] = totLosses.getReal();
		loadLosses[1] = totLosses.getImaginary();

		NoLoadLosses[0] = 0;
		NoLoadLosses[1] = 0;
	}

	/**
	 * Applies to PC Elements Puts straight into solution array
	 */
	public int injCurrents() {
		DSSGlobals.doErrorMsg(("Improper call to injCurrents for element: " + getName() + "."), "****",
				"Called CktElement class base function instead of actual.", 753);
		return 0;
	}

	/**
	 * Set NodeRef array for fast solution with intrinsics.
	 *
	 * Also allocates VTemp & ITemp.
	 */
	public void setNodeRef(int iTerm, int[] nodeRefArray) {
		int size, size2;

		// allocate nodeRef and move new values into it
		size = YOrder;
		size2 = nConds;  // size for one terminal
		nodeRef = Utilities.resizeArray(nodeRef, size);  // doesn't do anything if already properly allocated
		System.arraycopy(nodeRefArray[0], 0, nodeRef[iTerm * nConds], 0, size2);
		System.arraycopy(nodeRefArray[0], 0, terminals[iTerm].termNodeRef[0], 0, size2);  // copy in terminal as well

		// allocate temp array used to hold voltages and currents for calcs
		VTerminal = Utilities.resizeArray(VTerminal, YOrder);
		ITerminal = Utilities.resizeArray(ITerminal, YOrder);
		complexBuffer = Utilities.resizeArray(complexBuffer, YOrder);
	}

	public void setNodeRef(int iTerm, int nodeRefArray) {
		setNodeRef(iTerm, new int[] {nodeRefArray});
	}

	public String getFirstBus() {
		if (nTerms > 0) {
			busIndex = 0;
			return busNames[busIndex];
		} else {
			return "";
		}
	}

	public String getNextBus() {
		String result = "";
		if (nTerms > 0) {
			busIndex += 1;
			if (busIndex < nTerms) {
				result = busNames[busIndex];
			} else {
				busIndex = nTerms;
			}
		}
		return result;
	}

	/**
	 * Get bus name by index.
	 */
	public String getBus(int i) {
		if (i < nTerms) {
			return busNames[i];
		} else {
			return "";
		}
	}

	/**
	 * Set bus name by index.
	 */
	public void setBus(int i, String s) {
		if (i < nTerms) {
			busNames[i] = s.toLowerCase();
			// set global flag to signal circuit to rebuild bus defs
			DSSGlobals.activeCircuit.setBusNameRedefined(true);
		} else {
			DSSGlobals.doSimpleMsg(String.format("Attempt to set bus name for non-existent circuit element terminal(%d): \"%s\"", i, s), 7541);
		}
	}

	/**
	 * Set freq and recompute YPrim.
	 */
	private void setFreq(double value) {
		if (value > 0.0)
			YPrimFreq = value;
	}

	public void recalcElementData() {
		DSSGlobals.doSimpleMsg("recalcElementData in base CktElement class called for device = \"" + getName() +"\"", 754);
	}

	public void calcYPrim() {
		if (YPrimSeries != null)
			doYPrimCalcs(YPrimSeries);
		if (YPrimShunt != null)
			doYPrimCalcs(YPrimShunt);
		if (YPrim != null)
			doYPrimCalcs(YPrim);
	}

	/**
	 * Computes ITerminal for this device.
	 */
	public void computeITerminal() {
		// to save time, only recompute if a different solution than last time it was computed
		if (ITerminalSolutionCount != DSSGlobals.activeCircuit.getSolution().getSolutionCount()) {
			getCurrents(ITerminal);
			ITerminalSolutionCount = DSSGlobals.activeCircuit.getSolution().getSolutionCount();
		}
	}

	/**
	 * Max of ITerminal 1 phase currents.
	 */
	public double maxTerminalOneIMag() {
		double result = 0.0;
		if (enabled)
			for (int i = 0; i < nPhases; i++)
				result = Math.max(result, Math.pow(ITerminal[i].getReal(), 2) + Math.pow(ITerminal[i].getImaginary(), 2));
		return Math.sqrt(result);  // just do the sqrt once and save a little time
	}

	/**
	 * Get total complex power in active terminal.
	 */
	public Complex getPower(int idxTerm) {
		Complex cPower = Complex.ZERO;
		int i, k, n;
		SolutionObj sol;

		activeTerminalIdx = idxTerm;

		if (enabled)
			computeITerminal();

		// sum complex power going into phase conductors of active terminal
		sol = DSSGlobals.activeCircuit.getSolution();
		k = idxTerm * nConds;
		for (i = 0; i < nConds; i++) {  // 11-7-08 changed from nPhases - was not accounting for all conductors
			n = activeTerminal.getTermNodeRef()[i];  // don't bother for grounded node
			if (n >= 0)
				cPower = cPower.add( sol.getNodeV()[n].multiply( ITerminal[k + i].conjugate() ) );
		}

		/* If this is a positive sequence circuit, then we need to multiply by 3 to get the 3-phase power */
		if (DSSGlobals.activeCircuit.isPositiveSequence())
			cPower = cPower.multiply(3.0);

		return cPower;
	}

	/**
	 * Get total losses in circuit element, all phases, all terminals.
	 * Returns complex losses (watts, vars).
	 */
	public Complex getLosses() {
		Complex cLoss = Complex.ZERO;
		int k, n;
		SolutionObj sol;

		if (enabled) {
			computeITerminal();

			// sum complex power going into all conductors of all terminals
			sol = DSSGlobals.activeCircuit.getSolution();

			for (k = 0; k < YOrder; k++) {
				n = nodeRef[k];
				if (n >= 0)
					if (DSSGlobals.activeCircuit.isPositiveSequence()) {
						cLoss = cLoss.add( sol.getNodeV()[n].multiply(ITerminal[k].conjugate()).multiply(3.0) );
					} else {
						cLoss = cLoss.add( sol.getNodeV()[n].multiply(ITerminal[k].conjugate()) );
					}
			}
		}

		return cLoss;
	}

	/**
	 * Get the power in each phase (complex losses) of active terminal
	 * neutral conductors are ignored by this routine.
	 */
	public void getPhasePower(Complex[] powerBuffer) {
		int i, n;
		SolutionObj sol;

		if (enabled) {
			computeITerminal();

			sol = DSSGlobals.activeCircuit.getSolution();

			for (i = 0; i < YOrder; i++) {
				n = nodeRef[i];  // increment through terminals
				if (n >= 0) {
					if (DSSGlobals.activeCircuit.isPositiveSequence()) {
						powerBuffer[i] = sol.getNodeV()[n].multiply( ITerminal[i].conjugate() ).multiply(3.0);
					} else {
						powerBuffer[i] = sol.getNodeV()[n].multiply( ITerminal[i].conjugate() );
					}
				}
			}
		} else {
			for (i = 0; i < YOrder; i++)
				powerBuffer[i] = Complex.ZERO;
		}
	}

	/**
	 * Get the losses in each phase (complex losses);  Power difference coming out
	 * each phase. Note: This can be misleading if the nodeV voltage is greatly unbalanced.
	 *
	 * Neutral conductors are ignored by this routine.
	 */
	public void getPhaseLosses(int numPhases, Complex[] lossBuffer) {
		int i, j, k, n;
		Complex cLoss;
		SolutionObj sol;

		numPhases = nPhases;
		if (enabled) {
			computeITerminal();

			sol = DSSGlobals.activeCircuit.getSolution();

			for (i = 0; i < numPhases; i++) {
				cLoss = Complex.ZERO;
				for (j = 0; j < nTerms; j++) {
					k = j * nConds + i;
					n = nodeRef[k];  // increment through terminals
					if (n >= 0) {
						if (DSSGlobals.activeCircuit.isPositiveSequence()) {
							cLoss = cLoss.add( sol.getNodeV()[n].multiply( ITerminal[k].conjugate() ).multiply(3.0) );
						} else {
							cLoss = cLoss.add( sol.getNodeV()[n].multiply( ITerminal[k].conjugate() ) );
						}
					}
				}
				lossBuffer[i] = cLoss;
			}
		} else {
			for (i = 0; i < numPhases; i++)
				lossBuffer[i] = Complex.ZERO;
		}
	}

	public void dumpProperties(PrintStream f, boolean complete) {
		// FIXME Implement this method
		throw new UnsupportedOperationException();
	}

	private void doYPrimCalcs(CMatrix YMatrix) {
		int i, j, k = 0, ii, jj, elimRow;
		Complex Ynn, Yij, Yin, Ynj;
		int[] rowEliminated = null;
		boolean elementOpen = false;

		/* Now account for open conductors */
		/* For any conductor that is open, zero out row and column */
		k = 0;
		for (i = 0; i < nTerms; i++) {
			for (j = 0; j < nConds; j++) {
				if (!terminals[i].getConductors()[j].isClosed()) {
					if (!elementOpen) {
						rowEliminated = new int[YOrder];
						elementOpen = true;
					}
					// first do Kron reduction
					elimRow = j + k;
					Ynn = YMatrix.get(elimRow, elimRow);
					if (Ynn.abs() == 0.0)
						Ynn = new Complex(DSSGlobals.EPSILON, Ynn.getImaginary());
					rowEliminated[elimRow] = 1;
					for (ii = 0; ii < YOrder; ii++) {
						if (rowEliminated[ii] == 0) {
							Yin = YMatrix.get(ii, elimRow);
							for (jj = 0; jj < YOrder; jj++)
								if (rowEliminated[jj] == 0) {
									Yij = YMatrix.get(ii, jj);
									Ynj = YMatrix.get(elimRow, jj);
									YMatrix.setSym(ii, jj, Yij.subtract( Yin.multiply(Ynj).divide(Ynn) ));
								}
						}
					}
					// now zero out row and column
					YMatrix.zeroRow(elimRow);
					YMatrix.zeroCol(elimRow);
					YMatrix.set(elimRow, elimRow, new Complex(DSSGlobals.EPSILON, 0.0));  // in case node gets isolated
				}
			}
			k = k + nConds;
		}
		if (elementOpen)
			rowEliminated = new int[0];
	}

	/**
	 * Sum terminal currents into system currents array.
	 *
	 * Primarily for Newton iteration.
	 */
	public void sumCurrents() {
		if (enabled) {
			computeITerminal();

			SolutionObj sol = DSSGlobals.activeCircuit.getSolution();

			for (int i = 0; i < YOrder; i++)
				sol.setCurrent(nodeRef[i], sol.getCurrent(nodeRef[i]).add(ITerminal[i]));
		}
	}

	/**
	 * Bus voltages at indicated terminal.
	 *
	 * Fills VBuffer array which must be adequately allocated by calling routine.
	 */
	public void getTermVoltages(int iTerm, Complex[] VBuffer) {
		int i;
		SolutionObj sol;

		try {
			int ncond = nConds;

			/* Return zero if terminal number improperly specified */
			if (iTerm <= 0 || iTerm >= nTerms) {
				for (i = 0; i < ncond; i++)
					VBuffer[i] = Complex.ZERO;
				return;
			}

			sol = DSSGlobals.activeCircuit.getSolution();

			for (i = 0; i < ncond; i++)
				VBuffer[i] = sol.getNodeV()[ terminals[iTerm].getTermNodeRef()[i] ];
		} catch (Exception e) {
			DSSGlobals.doSimpleMsg("Error filling voltage buffer in getTermVoltages for circuit element:"+getDSSClassName()+"."+getName()+DSSGlobals.CRLF+
					"Probable cause: Invalid definition of element."+DSSGlobals.CRLF+
					"System error message: "+e.getMessage(), 755);
		}
	}

	public void initPropertyValues(int arrayOffset) {
		setPropertyValue(arrayOffset + 1, String.format("%g", baseFrequency));  // base freq
		setPropertyValue(arrayOffset + 2, "true");  // enabled

		enabledProperty = arrayOffset + 2;  // keep track of this

		super.initPropertyValues(arrayOffset + 2);
	}

	public String getPropertyValue(int index) {
		String result;
		if (index == enabledProperty - 1) {
			if (enabled) {
				result = "true";
			} else {
				result = "false";
			}
		} else {
			result = super.getPropertyValue(index);
		}
		return result;
	}

	/**
	 * For the base class, just return complex zero.
	 *
	 * Derived classes have to supply appropriate function.
	 */
	public void getSeqLosses(double[] posSeqLosses, double[] negSeqLosses,
			double[] zeroModeLosses) {
		posSeqLosses[0] = 0;
		posSeqLosses[1] = 0;
		negSeqLosses[0] = 0;
		negSeqLosses[1] = 0;
		zeroModeLosses[0] = 0;
		zeroModeLosses[1] = 0;
	}

	private boolean isGroundBus(String s) {
		boolean result = true;
		int i = s.indexOf(".1");
		if (i >= 0)
			result = false;
		i = s.indexOf(".2");
		if (i >= 0)
			result = false;
		i = s.indexOf(".3");
		if (i >= 0)
			result = false;
		i = s.indexOf('.');
		if (i == -1)
			result = false;
		return result;
	}

	/**
	 * Make a positive sequence model.
	 */
	public void makePosSequence() {
		boolean grnd;
		for (int i = 0; i < nTerms; i++) {
			grnd = isGroundBus(busNames[i]);
			busNames[i] = Utilities.stripExtension(busNames[i]);
			if (grnd)
				busNames[i] = busNames[i] + ".0";
		}
	}

	/**
	 * Put terminal voltages in an array.
	 */
	public void computeVTerminal() {
		SolutionObj sol = DSSGlobals.activeCircuit.getSolution();
		for (int i = 0; i < YOrder; i++)
			VTerminal[i] = sol.getNodeV()[nodeRef[i]];
	}

	public void zeroITerminal() {
		for (int i = 0; i < YOrder; i++)
			ITerminal[i] = Complex.ZERO;
	}

	public PowerTerminal getTerminal(int idx) {
		return terminals[idx];
	}

	public int[] getNodeRef() {
		return nodeRef;
	}

	public void setNodeRef(int[] ref) {
		nodeRef = ref;
	}

	public int getYorder() {
		return YOrder;
	}

	public void setYOrder(int order) {
		YOrder = order;
	}

	public int getLastTerminalChecked() {
		return lastTerminalChecked;
	}

	public void setLastTerminalChecked(int checked) {
		lastTerminalChecked = checked;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean value) {
		checked = value;
	}

	public boolean hasEnergyMeter() {
		return hasEnergyMeter;
	}

	public void setHasEnergyMeter(boolean hasMeter) {
		hasEnergyMeter = hasMeter;
	}

	public boolean hasSensorObj() {
		return hasSensorObj;
	}

	public void setHasSensorObj(boolean value) {
		hasSensorObj = value;
	}

	public boolean isIsolated() {
		return isIsolated;
	}

	public void setIsolated(boolean value) {
		isIsolated = value;
	}

	public boolean hasControl() {
		return hasControl;
	}

	public void setHasControl(boolean value) {
		hasControl = value;
	}

	public boolean isPartofFeeder() {
		return isPartOfFeeder;
	}

	public void setPartofFeeder(boolean isPart) {
		isPartOfFeeder = isPart;
	}

	public DSSCktElement getControlElement() {
		return controlElement;
	}

	public void setControlElement(DSSCktElement element) {
		controlElement = element;
	}

	public Complex[] getITerminal() {
		return ITerminal;
	}

	public void setITerminal(Complex[] terminal) {
		ITerminal = terminal;
	}

	public Complex[] getVTerminal() {
		return VTerminal;
	}

	public void setVTerminal(Complex[] terminal) {
		VTerminal = terminal;
	}

	public double getBaseFrequency() {
		return baseFrequency;
	}

	public void setBaseFrequency(double frequency) {
		baseFrequency = frequency;
	}

	public PowerTerminal[] getTerminals() {
		return terminals;
	}

	public void setTerminals(PowerTerminal[] value) {
		terminals = value;
	}

	public void setActiveTerminal(PowerTerminal terminal) {
		activeTerminal = terminal;
	}

	public PowerTerminal getActiveTerminal() {
		return activeTerminal;
	}

	public void setYPrimFreq(double Value) {
		setFreq(Value);
	}

	public double getYPrimFreq() {
		return this.YPrimFreq;
	}

	public int getHandle() {
		return 0;
	}

}
