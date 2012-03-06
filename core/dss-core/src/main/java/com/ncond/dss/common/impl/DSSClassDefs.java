package com.ncond.dss.common.impl;

import java.util.ArrayList;

import com.ncond.dss.common.DSSClass;
import com.ncond.dss.control.impl.CapControlImpl;
import com.ncond.dss.control.impl.GenDispatcherImpl;
import com.ncond.dss.control.impl.RecloserImpl;
import com.ncond.dss.control.impl.RegControlImpl;
import com.ncond.dss.control.impl.RelayImpl;
import com.ncond.dss.control.impl.StorageControllerImpl;
import com.ncond.dss.control.impl.SwtControlImpl;
import com.ncond.dss.control.impl.VVControlImpl;
import com.ncond.dss.conversion.impl.GeneratorImpl;
import com.ncond.dss.conversion.impl.ISourceImpl;
import com.ncond.dss.conversion.impl.LoadImpl;
import com.ncond.dss.conversion.impl.PVSystemImpl;
import com.ncond.dss.conversion.impl.StorageImpl;
import com.ncond.dss.conversion.impl.VSourceImpl;
import com.ncond.dss.delivery.impl.CapacitorImpl;
import com.ncond.dss.delivery.impl.FaultImpl;
import com.ncond.dss.delivery.impl.FuseImpl;
import com.ncond.dss.delivery.impl.LineImpl;
import com.ncond.dss.delivery.impl.ReactorImpl;
import com.ncond.dss.delivery.impl.TransformerImpl;
import com.ncond.dss.general.DSSObject;
import com.ncond.dss.general.impl.CNDataImpl;
import com.ncond.dss.general.impl.GrowthShapeImpl;
import com.ncond.dss.general.impl.LineCodeImpl;
import com.ncond.dss.general.impl.LineGeometryImpl;
import com.ncond.dss.general.impl.LineSpacingImpl;
import com.ncond.dss.general.impl.LoadShapeImpl;
import com.ncond.dss.general.impl.PriceShapeImpl;
import com.ncond.dss.general.impl.SpectrumImpl;
import com.ncond.dss.general.impl.TCC_CurveImpl;
import com.ncond.dss.general.impl.TSDataImpl;
import com.ncond.dss.general.impl.TShapeImpl;
import com.ncond.dss.general.impl.WireDataImpl;
import com.ncond.dss.general.impl.XYCurveImpl;
import com.ncond.dss.general.impl.XfmrCodeImpl;
import com.ncond.dss.meter.impl.EnergyMeterImpl;
import com.ncond.dss.meter.impl.MonitorImpl;
import com.ncond.dss.meter.impl.SensorImpl;
import com.ncond.dss.parser.impl.Parser;
import com.ncond.dss.shared.impl.HashListImpl;

public class DSSClassDefs {

	public static final int BASECLASSMASK = 0x00000007;
	public static final int CLASSMASK = 0xFFFFFFF8;

	/* Basic element types */
	public static final int NON_PCPD_ELEM = 1;  // a circuit element we don't want enumerated in PD and PC Elements
	public static final int PD_ELEMENT    = 2;
	public static final int PC_ELEMENT    = 3;
	public static final int CTRL_ELEMENT  = 4;
	public static final int METER_ELEMENT = 5;
	public static final int HIDDEN_ELEMENT= 6;

	/* Specific element types */
	public static final int MON_ELEMENT  =  1 * 8;
	public static final int DSS_OBJECT   =  2 * 8;  // just a general DSS object, accessible to all circuits
	public static final int SOURCE       =  3 * 8;
	public static final int XFMR_ELEMENT =  4 * 8;
	public static final int SUBSTATION   =  5 * 8;  // not used
	public static final int LINE_ELEMENT =  6 * 8;
	public static final int LOAD_ELEMENT =  7 * 8;
	public static final int FAULTOBJECT  =  8 * 8;
	public static final int ENERGY_METER =  9 * 8;
	public static final int GEN_ELEMENT  = 10 * 8;
	public static final int CAP_CONTROL  = 11 * 8;
	public static final int REG_CONTROL  = 12 * 8;
	public static final int CAP_ELEMENT  = 13 * 8;
	public static final int RELAY_CONTROL    = 14 * 8;
	public static final int RECLOSER_CONTROL = 15 * 8;
	public static final int FUSE_CONTROL     = 16 * 8;
	public static final int REACTOR_ELEMENT  = 17 * 8;
	public static final int FEEDER_ELEMENT   = 18 * 8;
	public static final int GEN_CONTROL      = 19 * 8;
	public static final int SENSOR_ELEMENT   = 20 * 8;
	public static final int STORAGE_ELEMENT  = 21 * 8;
	public static final int STORAGE_CONTROL  = 22 * 8;
	public static final int SWT_CONTROL      = 23 * 8;
	public static final int PVSYSTEM_ELEMENT = 24 * 8;
	public static final int VV_CONTROL       = 25 * 8;


	private static int numIntrinsicClasses;
	private static int numUserClasses;


	public static int getNumIntrinsicClasses() {
		return numIntrinsicClasses;
	}

	public static void setNumIntrinsicClasses(int num) {
		numIntrinsicClasses = num;
	}

	public static int getNumUserClasses() {
		return numUserClasses;
	}

	public static void setNumUserClasses(int num) {
		numUserClasses = num;
	}

	public static void createDSSClasses() {

		DSS.classNames = new HashListImpl(25);  // makes 5 sub lists
		DSS.DSSClassList = new ArrayList<DSSClass>(10);  // 10 is initial size and increment
		DSSClassImpl.setDSSClasses(new DSSClassesImpl());  // class to handle defining DSS classes

		/* General DSS objects, not circuit elements */
		DSS.DSSObjs = new ArrayList<DSSObject>(25);  // 25 is initial size and increment

		/* Instantiate all intrinsic object classes */

		/* Generic object classes first in case others refer to them */
		DSSClassImpl.getDSSClasses().setNew( new SolutionImpl() );
		DSS.solutionClass = DSS.activeDSSClass;  // this is a special class
		DSSClassImpl.getDSSClasses().setNew( new LineCodeImpl() );
		DSS.loadShapeClass = new LoadShapeImpl();
		DSSClassImpl.getDSSClasses().setNew( DSS.loadShapeClass );

		DSS.TShapeClass = new TShapeImpl();
		DSSClassImpl.getDSSClasses().setNew( DSS.TShapeClass );
		DSS.priceShapeClass = new PriceShapeImpl();
		DSSClassImpl.getDSSClasses().setNew( DSS.priceShapeClass );
		DSS.XYCurveClass = new XYCurveImpl();
		DSSClassImpl.getDSSClasses().setNew( DSS.XYCurveClass );

		DSS.growthShapeClass = new GrowthShapeImpl();
		DSSClassImpl.getDSSClasses().setNew( DSS.growthShapeClass );
		DSS.TCC_CurveClass = new TCC_CurveImpl();
		DSSClassImpl.getDSSClasses().setNew( DSS.TCC_CurveClass );
		DSS.spectrumClass = new SpectrumImpl();
		DSSClassImpl.getDSSClasses().setNew( DSS.spectrumClass );
		DSS.wireDataClass = new WireDataImpl();
		DSSClassImpl.getDSSClasses().setNew( DSS.wireDataClass );

		DSS.CNDataClass = new CNDataImpl();
		DSSClassImpl.getDSSClasses().setNew( DSS.CNDataClass );
		DSS.TSDataClass = new TSDataImpl();
		DSSClassImpl.getDSSClasses().setNew( DSS.TSDataClass );

		DSSClassImpl.getDSSClasses().setNew( new LineGeometryImpl() );
		DSS.lineSpacingClass = new LineSpacingImpl();
		DSSClassImpl.getDSSClasses().setNew( DSS.lineSpacingClass );
		DSSClassImpl.getDSSClasses().setNew( new XfmrCodeImpl() );

		/* Circuit element classes */
		DSSClassImpl.getDSSClasses().setNew( new LineImpl() );
		DSSClassImpl.getDSSClasses().setNew( new VSourceImpl() );
		DSSClassImpl.getDSSClasses().setNew( new ISourceImpl() );
		DSSClassImpl.getDSSClasses().setNew( new LoadImpl() );
		DSSClassImpl.getDSSClasses().setNew( new TransformerImpl() );
		DSSClassImpl.getDSSClasses().setNew( new RegControlImpl() );
		DSSClassImpl.getDSSClasses().setNew( new CapacitorImpl() );
		DSSClassImpl.getDSSClasses().setNew( new ReactorImpl() );
		DSSClassImpl.getDSSClasses().setNew( new CapControlImpl() );
		DSSClassImpl.getDSSClasses().setNew( new FaultImpl() );
		DSSClassImpl.getDSSClasses().setNew( new GeneratorImpl() );
		DSSClassImpl.getDSSClasses().setNew( new GenDispatcherImpl() );
		DSS.storageClass = new StorageImpl();
		DSSClassImpl.getDSSClasses().setNew( DSS.storageClass );
		DSSClassImpl.getDSSClasses().setNew( new StorageControllerImpl() );
		DSSClassImpl.getDSSClasses().setNew( new RelayImpl() );
		DSSClassImpl.getDSSClasses().setNew( new RecloserImpl() );
		DSSClassImpl.getDSSClasses().setNew( new FuseImpl() );
		//Globals.setFeederClass(new FeederImpl());
		//DSSClassImpl.getDSSClasses().setNew( Globals.getFeederClass() );
		DSSClassImpl.getDSSClasses().setNew( new SwtControlImpl() );
		DSS.PVSystemClass = new PVSystemImpl();
		DSSClassImpl.getDSSClasses().setNew( DSS.PVSystemClass );

		DSS.VVControlClass = new VVControlImpl();
		DSSClassImpl.getDSSClasses().setNew ( DSS.VVControlClass );

		DSS.monitorClass = new MonitorImpl();  // have to do this after Generator
		DSSClassImpl.getDSSClasses().setNew( DSS.monitorClass );
		DSS.energyMeterClass = new EnergyMeterImpl();  // have to do this after Generator
		DSSClassImpl.getDSSClasses().setNew( DSS.energyMeterClass );
		DSS.sensorClass = new SensorImpl();  // create state estimation sensors
		DSSClassImpl.getDSSClasses().setNew( DSS.sensorClass );


		/* Create classes for custom implementations */
		//MyClassDefs.createMyDSSClasses();

		numIntrinsicClasses = DSS.DSSClassList.size();
		numUserClasses = 0;

		/* Add user-defined objects */

	}

	public static void disposeDSSClasses() {
		DSSObject obj;
		String traceName = "";
		String successFree = "";

		try {
			successFree = "First Object";
			for (int i = 0; i < DSS.DSSObjs.size(); i++) {
				obj = DSS.DSSObjs.get(i);
				traceName = obj.getParentClass().getName() + "." + obj.getName();
				successFree = traceName;
			}
			traceName = "(DSSObjs Class)";
			DSS.DSSObjs = null;
		} catch (Exception e) {
			DSS.doSimpleMsg("Exception disposing of DSS obj \""+traceName+"\". "+DSS.CRLF+
					"Last successful dispose was for object \"" + successFree + "\" " +DSS.CRLF+
					e.getMessage(), 901);
		}

		try {
			for (int i = 0; i < DSS.DSSClassList.size(); i++)
				DSS.DSSClassList.set(i, null);
			traceName = "(DSS Class List)";
			DSS.DSSClassList = null;
			traceName = "(DSS Classes)";
			DSSClassImpl.setDSSClasses(null);
			traceName = "(ClassNames)";
			DSS.classNames = null;
		} catch (Exception e) {
			DSS.doSimpleMsg("Exception disposing of DSS class\"" + traceName +
					"\". " + DSS.CRLF + e.getMessage(), 902);
		}
	}

	/**
	 * Set lastClassReferenced variable by class name.
	 */
	public static boolean setObjectClass(String objType) {
		int classRef = DSS.classNames.find(objType);

		switch (classRef) {
		case 0:
			DSS.doSimpleMsg("Error: Object class \"" + objType + "\" not found." +
					DSS.CRLF + Parser.getInstance().getCmdString(), 903);
			return false;
		default:
			DSS.lastClassReferenced = classRef;
			break;
		}

		return true;
	}

	public static DSSClass getDSSClass(String className) {
		return DSS.DSSClassList.get( DSS.classNames.find(className.toLowerCase()) );
	}

//	public static void addUserClass() {
//		throw new UnsupportedOperationException();
//	}
//
//	public static void loadUserClasses() {
//		throw new UnsupportedOperationException();
//	}

}