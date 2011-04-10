package com.epri.dss.conversion;

import com.epri.dss.shared.impl.Complex;

public interface Generator extends PCClass {
	
	/* Number of energy meter registers */
	static final int NumGenRegisters = 6;    
	static final int NumGenVariables = 6;
	
	static final int NumPropsThisClass = 36;
	/* Dispatch modes */
	static final int DEFAULT = 0;
	static final int LOADMODE = 1;
	static final int PRICEMODE = 2;
	
	String[] getRegisterNames();

	void setRegisterNames(String[] registerNames);
	
	void resetRegistersAll();
	
	void sampleAll();

}
