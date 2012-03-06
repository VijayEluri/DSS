/**
 * Copyright (C) 2009 Electric Power Research Institute, Inc.
 * Copyright (C) 2011 Richard Lincoln
 *
 * All rights reserved.
 */

package com.ncond.dss.general.impl;

import java.util.UUID;

import com.ncond.dss.general.NamedObject;

public class NamedObjectImpl implements NamedObject {

	/**
	 * Path name, or class name for DSS.
	 */
	private String pathName;

	/**
	 * LocalName is unique within a class.
	 */
	private String localName;

	/**
	 * For optional display, does not have to be unique.
	 */
	private String displayName;

	private UUID uuid;


	public NamedObjectImpl(String className) {
		super();
		pathName = className;
		localName = "";
		displayName = "";
		uuid = null;
	}

	@Override
	public String getDisplayName() {
		if (this.displayName == "") {
			return pathName + "_" + localName;
		} else {
			return displayName;
		}
	}

	@Override
	public void setDisplayName(String value) {
		displayName = value;
	}

	@Override
	public String getQualifiedName() {
		return pathName + "." + localName;
	}

	private UUID getUUID() {
		if (uuid == null)
			uuid = UUID.randomUUID();
		return uuid;
	}

	@Override
	public void setUUID(UUID value) {
		//if (pGuid == null) {}
		uuid = value;
	}

	@Override
	public String getID() {
		return getUUID().toString();
	}

	@Override
	public String getCIM_ID() {
		return UUIDToCIMString(getUUID());
	}


	@Override
	public String getDSSClassName() {
		return pathName;
	}

	@Override
	public void setDSSClassName(String value) {
		pathName = value;
	}

	@Override
	public String getLocalName() {
		return localName;
	}

	@Override
	public void setLocalName(String value) {
		localName = value;
	}


	@Override
	public String UUIDToCIMString(UUID uUID) {
		String s;
		s = uUID.toString();
		return s.substring(1, s.length() - 2);
	}

}