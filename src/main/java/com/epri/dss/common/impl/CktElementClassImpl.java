package com.epri.dss.common.impl;

import com.epri.dss.common.CktElement;
import com.epri.dss.common.CktElementClass;
import com.epri.dss.parser.impl.Parser;

public class CktElementClassImpl extends DSSClassImpl implements
		CktElementClass {

	private int NumCktElemClassProps;

	public CktElementClassImpl() {
		super();
		this.NumCktElemClassProps = 2;
	}

	protected int classEdit(Object ActiveCktElemObj, int ParamPointer) {
		// continue parsing with contents of Parser
		if (ParamPointer >= 0) {  // TODO Check zero based indexing
			CktElement cktElem = DSSCktElement(ActiveCktElemObj); // FIXME Translate this
			switch (ParamPointer) {
			case 0:
				cktElem.setBaseFrequency(Parser.getInstance().makeDouble());
			case 2:
				cktElem.setEnabled(Utilities.interpretYesNo(Parser.getInstance().makeString()));
			default:
				super.classEdit(ActiveCktElemObj, ParamPointer - NumCktElemClassProps);
			}
		}
		return 0;
	}

	protected void classMakeLike(Object OtherObj) {
		CktElement OtherCktObj = DSSCktElement(OtherObj);  // TODO Translate this

		CktElement cktElem = DSSCktElement(DSSGlobals.getInstance().getActiveDSSObject());  // TODO Translate this
		cktElem.setBaseFrequency(OtherCktObj.getBaseFrequency());
		cktElem.setEnabled(true);
	}

	/**
	 * Add no. of intrinsic properties.
	 */
	protected void countProperties() {
		NumProperties = NumProperties + NumCktElemClassProps;
		super.countProperties();
	}

	/**
	 * Define the properties for the base power delivery element class.
	 */
	protected void defineProperties() {
		PropertyName[ActiveProperty + 1] = "basefreq";  // TODO Check zero based indexing
		PropertyName[ActiveProperty + 2] = "enabled";

		PropertyHelp[ActiveProperty + 1] = "Base Frequency for ratings.";
		PropertyHelp[ActiveProperty + 2] = "{Yes|No or True|False} Indicates whether this element is enabled.";

		ActiveProperty = ActiveProperty + NumCktElemClassProps;

		super.defineProperties();
	}

	public int getNumCktElemClassProps() {
		return NumCktElemClassProps;
	}

}
