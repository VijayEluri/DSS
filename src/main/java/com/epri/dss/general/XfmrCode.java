package com.epri.dss.general;

import com.epri.dss.common.DSSClass;

public interface XfmrCode extends DSSClass {
	
	static final int NumPropsThisClass = 33;
	
	String getCode();
	
	void setCode(String Value);

}
