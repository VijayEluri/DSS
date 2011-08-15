package com.epri.dss.meter.impl;

import com.epri.dss.common.DSSClass;
import com.epri.dss.common.impl.CktElementClassImpl;
import com.epri.dss.common.impl.DSSClassDefs;
import com.epri.dss.common.impl.DSSGlobals;
import com.epri.dss.meter.MeterClass;

public class MeterClassImpl extends CktElementClassImpl implements MeterClass {

	private int numMeterClassProps;

	public MeterClassImpl() {
		super();
		this.numMeterClassProps = 0;
		this.classType = DSSClassDefs.METER_ELEMENT;
	}

	protected void countProperties() {
		numProperties = numProperties + numMeterClassProps;
		super.countProperties();
	}

	protected void defineProperties() {
		activeProperty = activeProperty + numMeterClassProps;
		super.defineProperties();
	}

	protected int classEdit(Object activeMeterObj, int paramPointer) {

		if (paramPointer > 0)
			super.classEdit(activeMeterObj, paramPointer - numMeterClassProps);

		return 0;
	}

	protected void classMakeLike(Object otherObj) {
		new MeterElementImpl((DSSClass) otherObj);
	}

	public void resetAll() {
		DSSGlobals.getInstance().doSimpleMsg("Programming Error: Base MeterClass.resetAll reached for class: "+getName(), 760);
	}

	/**
	 * Force all monitors to take a sample.
	 */
	public void sampleAll() {
		DSSGlobals.getInstance().doSimpleMsg("Programming Error: Base MeterClass.sampleAll reached for class: "+getName(), 761);
	}

	/**
	 * Force all monitors to save their buffers to disk.
	 */
	public void saveAll() {
		DSSGlobals.getInstance().doSimpleMsg("Programming Error: Base MeterClass.saveAll reached for Class: "+getName(), 762);
	}

	public int getNumMeterClassProps() {
		return numMeterClassProps;
	}

	public void setNumMeterClassProps(int numProps) {
		this.numMeterClassProps = numProps;
	}

}
