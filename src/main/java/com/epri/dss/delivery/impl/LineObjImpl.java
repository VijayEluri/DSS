package com.epri.dss.delivery.impl;

import java.io.PrintStream;

import com.epri.dss.shared.impl.Complex;

import com.epri.dss.common.DSSClass;
import com.epri.dss.delivery.LineObj;
import com.epri.dss.general.LineSpacingObj;
import com.epri.dss.general.LineGeometryObj;
import com.epri.dss.general.WireDataObj;
import com.epri.dss.shared.CMatrix;

public class LineObjImpl extends PDElementImpl implements LineObj {
	
	/* keep track of last frequency computed for geometry */
	private double ZFrequency; 
	private int LineCodeUnits;
	private double UnitsConvert; // conversion factor
	private LineGeometryObj LineGeometryObj;
	private LineSpacingObj LineSpacingObj;
	private WireDataObj[] WireData;
	private boolean rhoSpecified;
	private boolean LineCodeSpecified;
	private int EarthModel;
	
	protected CMatrix Zinv;
	
	
	/* Base Frequency Series Z matrix  per unit length */
	protected CMatrix Z;   
	protected CMatrix Yc;

	protected double R1;
	protected double X1;
	protected double R0;
	protected double X0;
	protected double C1;
	protected double C0;
	protected double Len;
	protected int LengthUnits;
	protected double Rg, Xg, KXg, rho;
	protected double GeneralPlotQuantity;  // For general circuit plotting
	protected String CondCode;
	protected String GeometryCode;
	protected String SpacingCode;
	protected boolean GeometrySpecified;
	protected boolean SpacingSpecified;
	protected boolean SymComponentsChanged;
	protected boolean SymComponentsModel;
	protected boolean IsSwitch;

	public LineObjImpl(DSSClass ParClass, String LineName) {
		super(ParClass);
		// TODO Auto-generated constructor stub
	}
	
	/* make new Z, Zinv, Yc, etc */
	private void makeZFromGeometry(double f) {
		
	}
	
	private void killGeometrySpecified() {
		
	}
	
	/* make new Z, Zinv, Yc, etc */
	private void makeZFromSpacing(double f) {
		
	}
	
	private void killSpacingSpecified() {
		
	}
	
	private void clearYPrim() {
		
	}
	
	private void resetLengthUnits() {
		
	}
	
	private void updatePDProperties() {
		
	}

	public CMatrix getZ() {
		return Z;
	}

	public void setZ(CMatrix z) {
		Z = z;
	}

	public CMatrix getYc() {
		return Yc;
	}

	public void setYc(CMatrix yc) {
		Yc = yc;
	}

	public double getR1() {
		return R1;
	}

	public void setR1(double r1) {
		R1 = r1;
	}

	public double getX1() {
		return X1;
	}

	public void setX1(double x1) {
		X1 = x1;
	}

	public double getR0() {
		return R0;
	}

	public void setR0(double r0) {
		R0 = r0;
	}

	public double getX0() {
		return X0;
	}

	public void setX0(double x0) {
		X0 = x0;
	}

	public double getC1() {
		return C1;
	}

	public void setC1(double c1) {
		C1 = c1;
	}

	public double getC0() {
		return C0;
	}

	public void setC0(double c0) {
		C0 = c0;
	}

	public double getLen() {
		return Len;
	}

	public void setLen(double len) {
		Len = len;
	}

	public int getLengthUnits() {
		return LengthUnits;
	}

	public void setLengthUnits(int lengthUnits) {
		LengthUnits = lengthUnits;
	}

	public double getRg() {
		return Rg;
	}

	public void setRg(double rg) {
		Rg = rg;
	}

	public double getXg() {
		return Xg;
	}

	public void setXg(double xg) {
		Xg = xg;
	}

	public double getKXg() {
		return KXg;
	}

	public void setKXg(double kXg) {
		KXg = kXg;
	}

	public double getRho() {
		return rho;
	}

	public void setRho(double rho) {
		this.rho = rho;
	}

	public double getGeneralPlotQuantity() {
		return GeneralPlotQuantity;
	}

	public void setGeneralPlotQuantity(double generalPlotQuantity) {
		GeneralPlotQuantity = generalPlotQuantity;
	}

	public String getCondCode() {
		return CondCode;
	}

	public void setCondCode(String condCode) {
		CondCode = condCode;
	}

	public String getGeometryCode() {
		return GeometryCode;
	}

	public void setGeometryCode(String geometryCode) {
		GeometryCode = geometryCode;
	}

	public String getSpacingCode() {
		return SpacingCode;
	}

	public void setSpacingCode(String spacingCode) {
		SpacingCode = spacingCode;
	}

	public boolean isGeometrySpecified() {
		return GeometrySpecified;
	}

	public void setGeometrySpecified(boolean geometrySpecified) {
		GeometrySpecified = geometrySpecified;
	}

	public boolean isSpacingSpecified() {
		return SpacingSpecified;
	}

	public void setSpacingSpecified(boolean spacingSpecified) {
		SpacingSpecified = spacingSpecified;
	}

	public boolean isSymComponentsChanged() {
		return SymComponentsChanged;
	}

	public void setSymComponentsChanged(boolean symComponentsChanged) {
		SymComponentsChanged = symComponentsChanged;
	}

	public boolean isSymComponentsModel() {
		return SymComponentsModel;
	}

	public void setSymComponentsModel(boolean symComponentsModel) {
		SymComponentsModel = symComponentsModel;
	}

	public boolean isIsSwitch() {
		return IsSwitch;
	}

	public void setIsSwitch(boolean isSwitch) {
		IsSwitch = isSwitch;
	}
	
	@Override
	public void getLosses(Complex TotalLosses, Complex LoadLosses,
			Complex NoLoadLosses) {
		
	}
	
	public void getSeqLosses(Complex PosSeqLosses, Complex NegSeqLosses,
			Complex ZeroSeqLosses) {
		
	}

	@Override
	public void recalcElementData() {
		
	}
	
	@Override
	public void calcYPrim() {
		
	}
	
	@Override
	public void makePosSequence() {
		
	}
	
	public boolean mergeWith(LineObj OtherLine, boolean Series) {
		return false;
	}
	
	public void updateControlElements(String NewName, String OldName) {
		
	}
	
	@Override
	public void initPropertyValues(int ArrayOffset) {
		
	}
	
	@Override
	public void dumpProperties(PrintStream F, boolean Complete) {
		
	}
	
	@Override
	public String getPropertyValue(int Index) {
		return null;
	}
	
	public void fetchLineCode(String Code) {
		
	}
	
	public void fetchGeometryCode(String Code) {
		
	}
	
	public void fetchLineSpacing(String Code) {
		
	}
	
	public void fetchWireList(String Code) {
		
	}

	public boolean isLineCodeSpecified() {
		return LineCodeSpecified;
	}
	
	// FIXME Private members in OpenDSS

	public double getZFrequency() {
		return ZFrequency;
	}

	public void setZFrequency(double zFrequency) {
		ZFrequency = zFrequency;
	}

	public int getLineCodeUnits() {
		return LineCodeUnits;
	}

	public void setLineCodeUnits(int lineCodeUnits) {
		LineCodeUnits = lineCodeUnits;
	}

	public double getUnitsConvert() {
		return UnitsConvert;
	}

	public void setUnitsConvert(double unitsConvert) {
		UnitsConvert = unitsConvert;
	}

	public LineGeometryObj getLineGeometryObj() {
		return LineGeometryObj;
	}

	public void setLineGeometryObj(LineGeometryObj lineGeometryObj) {
		LineGeometryObj = lineGeometryObj;
	}

	public LineSpacingObj getLineSpacingObj() {
		return LineSpacingObj;
	}

	public void setLineSpacingObj(LineSpacingObj lineSpacingObj) {
		LineSpacingObj = lineSpacingObj;
	}

	public WireDataObj[] getWireData() {
		return WireData;
	}

	public void setWireData(WireDataObj[] wireData) {
		WireData = wireData;
	}

	public boolean getRhoSpecified() {
		return rhoSpecified;
	}

	public void setRhoSpecified(boolean rhoSpecified) {
		this.rhoSpecified = rhoSpecified;
	}

	public int getEarthModel() {
		return EarthModel;
	}

	public void setEarthModel(int earthModel) {
		EarthModel = earthModel;
	}

	public void setLineCodeSpecified(boolean lineCodeSpecified) {
		LineCodeSpecified = lineCodeSpecified;
	}

	public CMatrix getZinv() {
		return Zinv;
	}

	public void setZinv(CMatrix zinv) {
		Zinv = zinv;
	}

}
