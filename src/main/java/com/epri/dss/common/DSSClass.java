package com.epri.dss.common;

import com.epri.dss.general.DSSObject;
import com.epri.dss.shared.ObjectList;

public interface DSSClass<T extends DSSObject> {

	int getNumProperties();

	void setNumProperties(int numProperties);

	String[] getPropertyName();

	void setPropertyName(String[] propertyName);

	String[] getPropertyHelp();

	void setPropertyHelp(String[] propertyHelp);

	int[] getPropertyIdxMap();

	void setPropertyIdxMap(int[] propertyIdxMap);

	int[] getRevPropertyIdxMap();

	void setRevPropertyIdxMap(int[] revPropertyIdxMap);

	int getDSSClassType();

	void setDSSClassType(int DSSClassType);

	ObjectList<T> getElementList();

	void setElementList(ObjectList<T> elementList);

	boolean isElementNamesOutOfSynch();

	void setElementNamesOutOfSynch(boolean elementNamesOutOfSynch);

	boolean isSaved();

	void setSaved(boolean saved);

	int getActiveElement();

	void setActiveElement(int value);

	int getElementCount();

	int getFirst();

	int getNext();

	String getName();

	String getFirstPropertyName();

	String getNextPropertyName();

	/** Helper routine for building property strings */
	void addProperty(String propName, int cmdMapIndex, String helpString);

	void reallocateElementNameList();

	/** Uses global parser */
	int edit();

	int init(int Handle);

	int newObject(String objName);

	boolean setActive(String value);

	/** Get address of active object of this class */
	Object getActiveObj();

	/** Find an obj of this class by name */
	Object find(String objName);

	/** Find property value by string */
	int propertyIndex(String prop);

}
