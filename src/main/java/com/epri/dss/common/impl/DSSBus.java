package com.epri.dss.common.impl;

import com.epri.dss.shared.impl.CMatrixImpl;
import org.apache.commons.math.complex.Complex;

import com.epri.dss.common.Bus;
import com.epri.dss.common.Circuit;
import com.epri.dss.general.impl.NamedObjectImpl;
import com.epri.dss.shared.CMatrix;

public class DSSBus extends NamedObjectImpl implements Bus {

	public class NodeBus {
		/* Ref to bus in circuit's bus list */
		public int busRef;
		public int nodeNum;
	}

	private int numNodesThisBus;
	private int[] nodes;
	private int allocation;
	private int[] refNo;

	protected Complex[] VBus, busCurrent;
	protected CMatrix Zsc, Ysc;

	/* Coordinates */
	protected double x, y;
	protected double kVBase;
	/* Base kV for each node to ground (0) */
	protected double distFromMeter;

	protected boolean coordDefined, busChecked, keep, isRadialBus;

	public DSSBus() {
		super("Bus");
		allocation = 3;
		nodes = new int[allocation];
		refNo = new int[allocation];
		numNodesThisBus = 0;
		Ysc              = null;
		Zsc              = null;
		VBus             = null;
		busCurrent       = null;
		kVBase           = 0.0;  // signify that it has not been set
		x                = 0.0;
		y                = 0.0;
		distFromMeter    = 0.0;
		coordDefined     = false;
		keep             = false;
		isRadialBus      = false;
	}

	private void addANode() {
		numNodesThisBus += 1;
		if (numNodesThisBus > allocation) {
			allocation = allocation + 1;
			nodes = (int[]) Utilities.resizeArray(nodes, allocation);
			refNo = (int[]) Utilities.resizeArray(refNo, allocation);
		}
	}

	public int add(int nodeNum) {
		int result;

		if (nodeNum == 0) {
			result = 0;
		} else {
			result = find(nodeNum);
			if (result == 0) {
				// add a node to the bus
				addANode();
				nodes[numNodesThisBus] = nodeNum;

				Circuit ckt = DSSGlobals.getInstance().getActiveCircuit();

				ckt.setNumNodes(ckt.getNumNodes() + 1);  // global node number for circuit
				refNo[numNodesThisBus] = ckt.getNumNodes();
				result = ckt.getNumNodes();  // return global node number
			}
		}

		return result;
	}

	/**
	 * Returns reference num for node by node number.
	 */
	public int find(int nodeNum) {
		for (int i = 0; i < numNodesThisBus; i++) {
			if (nodes[i] == nodeNum)
				return refNo[i];
		}
		return 0;
	}

	/**
	 * Returns reference num for node by node index.
	 */
	public int getRef(int nodeIndex) {  // FIXME Check zero based indexing
		if ((nodeIndex > 0) && (nodeIndex <= numNodesThisBus)) {
			return refNo[nodeIndex];
		} else {
			return 0;
		}
	}

	/**
	 * Returns ith node number designation.
	 */
	public int getNum(int nodeIndex) {
		if ((nodeIndex > 0) && (nodeIndex <= numNodesThisBus)) {
			return nodes[nodeIndex];
		} else {
			return 0;
		}
	}

	public void allocateBusQuantities() {
		// have to perform a short circuit study to get this allocated
		Ysc = new CMatrixImpl(numNodesThisBus);
		Zsc = new CMatrixImpl(numNodesThisBus);
		allocateBusVoltages();
		allocateBusCurrents();
	}

	/**
	 * = Zs + 2 Zm
	 */
	public Complex getZsc0() {
		if (Zsc != null) {
			return Zsc.avgDiagonal().add( Zsc.avgOffDiagonal().multiply(2.0) );
		} else {
			return Complex.ZERO;
		}
	}

	/**
	 * = Zs - Zm
	 */
	public Complex getZsc1() {
		if (Zsc != null) {
			return Zsc.avgDiagonal().subtract( Zsc.avgOffDiagonal() );
		} else {
			return Complex.ZERO;
		}
	}

	/**
	 * Returns index of node by node number.
	 */
	public int findIdx(int nodeNum) {
		for (int i = 0; i < numNodesThisBus; i++) {
			if (nodes[i] == nodeNum)
				return i;
		}
		return 0;  // TODO Check zero based indexing
	}

	public void allocateBusVoltages() {
		VBus = (Complex[]) Utilities.resizeArray(VBus, numNodesThisBus);
		for (int i = 0; i < numNodesThisBus; i++)
			VBus[i] = Complex.ZERO;
	}

	public void allocateBusCurrents() {
		busCurrent = (Complex[]) Utilities.resizeArray(busCurrent, numNodesThisBus);
		for (int i = 0; i < numNodesThisBus; i++)
			busCurrent[i] = Complex.ZERO;
	}

	public Complex[] getVBus() {
		return VBus;
	}

	public void setVBus(Complex[] vBus) {
		VBus = vBus;
	}

	public Complex[] getBusCurrent() {
		return busCurrent;
	}

	public void setBusCurrent(Complex[] buscurrent) {
		busCurrent = buscurrent;
	}

	public CMatrix getZsc() {
		return Zsc;
	}

	public void setZsc(CMatrix zsc) {
		Zsc = zsc;
	}

	public CMatrix getYsc() {
		return Ysc;
	}

	public void setYsc(CMatrix ysc) {
		Ysc = ysc;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getKVBase() {
		return kVBase;
	}

	public void setKVBase(double kVBase) {
		this.kVBase = kVBase;
	}

	public double getDistFromMeter() {
		return distFromMeter;
	}

	public void setDistFromMeter(double distFromMeter) {
		this.distFromMeter = distFromMeter;
	}

	public boolean isCoordDefined() {
		return coordDefined;
	}

	public void setCoordDefined(boolean defined) {
		coordDefined = defined;
	}

	public boolean isBusChecked() {
		return busChecked;
	}

	public void setBusChecked(boolean checked) {
		busChecked = checked;
	}

	public boolean isKeep() {
		return keep;
	}

	public void setKeep(boolean keep) {
		this.keep = keep;
	}

	public boolean isRadialBus() {
		return isRadialBus;
	}

	public void setRadialBus(boolean isRadial) {
		isRadialBus = isRadial;
	}

	public int getNumNodesThisBus() {
		return numNodesThisBus;
	}

}
