/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package electrickery.general;

import electrickery.common.CommonPackage;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see electrickery.general.GeneralFactory
 * @model kind="package"
 * @generated
 */
public interface GeneralPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "general";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://www.electrickery.com/general";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "gen";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	GeneralPackage eINSTANCE = electrickery.general.impl.GeneralPackageImpl.init();

	/**
	 * The meta object id for the '{@link electrickery.general.impl.DSSObjectImpl <em>DSS Object</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see electrickery.general.impl.DSSObjectImpl
	 * @see electrickery.general.impl.GeneralPackageImpl#getDSSObject()
	 * @generated
	 */
	int DSS_OBJECT = 0;

	/**
	 * The feature id for the '<em><b>DSS Obj Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DSS_OBJECT__DSS_OBJ_TYPE = 0;

	/**
	 * The feature id for the '<em><b>DSS Class Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DSS_OBJECT__DSS_CLASS_NAME = 1;

	/**
	 * The feature id for the '<em><b>Parent Class</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DSS_OBJECT__PARENT_CLASS = 2;

	/**
	 * The feature id for the '<em><b>Class Index</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DSS_OBJECT__CLASS_INDEX = 3;

	/**
	 * The feature id for the '<em><b>Dirty</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DSS_OBJECT__DIRTY = 4;

	/**
	 * The feature id for the '<em><b>Flag</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DSS_OBJECT__FLAG = 5;

	/**
	 * The feature id for the '<em><b>Prop Sequence</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DSS_OBJECT__PROP_SEQUENCE = 6;

	/**
	 * The number of structural features of the '<em>DSS Object</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DSS_OBJECT_FEATURE_COUNT = 7;

	/**
	 * The meta object id for the '{@link electrickery.general.impl.GrowthShapeImpl <em>Growth Shape</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see electrickery.general.impl.GrowthShapeImpl
	 * @see electrickery.general.impl.GeneralPackageImpl#getGrowthShape()
	 * @generated
	 */
	int GROWTH_SHAPE = 1;

	/**
	 * The feature id for the '<em><b>NPts</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GROWTH_SHAPE__NPTS = 0;

	/**
	 * The feature id for the '<em><b>Year</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GROWTH_SHAPE__YEAR = 1;

	/**
	 * The feature id for the '<em><b>Csv File</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GROWTH_SHAPE__CSV_FILE = 2;

	/**
	 * The feature id for the '<em><b>Sng File</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GROWTH_SHAPE__SNG_FILE = 3;

	/**
	 * The feature id for the '<em><b>Dbl File</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GROWTH_SHAPE__DBL_FILE = 4;

	/**
	 * The number of structural features of the '<em>Growth Shape</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GROWTH_SHAPE_FEATURE_COUNT = 5;

	/**
	 * The meta object id for the '{@link electrickery.general.impl.LineCodeImpl <em>Line Code</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see electrickery.general.impl.LineCodeImpl
	 * @see electrickery.general.impl.GeneralPackageImpl#getLineCode()
	 * @generated
	 */
	int LINE_CODE = 2;

	/**
	 * The feature id for the '<em><b>NPhases</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINE_CODE__NPHASES = 0;

	/**
	 * The feature id for the '<em><b>R1</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINE_CODE__R1 = 1;

	/**
	 * The feature id for the '<em><b>X1</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINE_CODE__X1 = 2;

	/**
	 * The feature id for the '<em><b>R0</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINE_CODE__R0 = 3;

	/**
	 * The feature id for the '<em><b>X0</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINE_CODE__X0 = 4;

	/**
	 * The feature id for the '<em><b>C1</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINE_CODE__C1 = 5;

	/**
	 * The feature id for the '<em><b>C0</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINE_CODE__C0 = 6;

	/**
	 * The feature id for the '<em><b>Units</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINE_CODE__UNITS = 7;

	/**
	 * The feature id for the '<em><b>RMatrix</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINE_CODE__RMATRIX = 8;

	/**
	 * The feature id for the '<em><b>XMatrix</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINE_CODE__XMATRIX = 9;

	/**
	 * The feature id for the '<em><b>CMatrix</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINE_CODE__CMATRIX = 10;

	/**
	 * The feature id for the '<em><b>Base Freq</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINE_CODE__BASE_FREQ = 11;

	/**
	 * The feature id for the '<em><b>Norm Amps</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINE_CODE__NORM_AMPS = 12;

	/**
	 * The feature id for the '<em><b>Emerg Amps</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINE_CODE__EMERG_AMPS = 13;

	/**
	 * The feature id for the '<em><b>Fault Rate</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINE_CODE__FAULT_RATE = 14;

	/**
	 * The feature id for the '<em><b>Pct Perm</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINE_CODE__PCT_PERM = 15;

	/**
	 * The feature id for the '<em><b>Repair</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINE_CODE__REPAIR = 16;

	/**
	 * The feature id for the '<em><b>Kron</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINE_CODE__KRON = 17;

	/**
	 * The feature id for the '<em><b>Rg</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINE_CODE__RG = 18;

	/**
	 * The feature id for the '<em><b>Xg</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINE_CODE__XG = 19;

	/**
	 * The feature id for the '<em><b>Rho</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINE_CODE__RHO = 20;

	/**
	 * The feature id for the '<em><b>Neutral</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINE_CODE__NEUTRAL = 21;

	/**
	 * The feature id for the '<em><b>Like</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINE_CODE__LIKE = 22;

	/**
	 * The number of structural features of the '<em>Line Code</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINE_CODE_FEATURE_COUNT = 23;

	/**
	 * The meta object id for the '{@link electrickery.general.impl.LineGeometryImpl <em>Line Geometry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see electrickery.general.impl.LineGeometryImpl
	 * @see electrickery.general.impl.GeneralPackageImpl#getLineGeometry()
	 * @generated
	 */
	int LINE_GEOMETRY = 3;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINE_GEOMETRY__NAME = CommonPackage.NAMED__NAME;

	/**
	 * The feature id for the '<em><b>NConds</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINE_GEOMETRY__NCONDS = CommonPackage.NAMED_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>NPhases</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINE_GEOMETRY__NPHASES = CommonPackage.NAMED_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Cond</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINE_GEOMETRY__COND = CommonPackage.NAMED_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Wire</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINE_GEOMETRY__WIRE = CommonPackage.NAMED_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Spacing</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINE_GEOMETRY__SPACING = CommonPackage.NAMED_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>X</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINE_GEOMETRY__X = CommonPackage.NAMED_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>H</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINE_GEOMETRY__H = CommonPackage.NAMED_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>Units</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINE_GEOMETRY__UNITS = CommonPackage.NAMED_FEATURE_COUNT + 7;

	/**
	 * The feature id for the '<em><b>Norm Amps</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINE_GEOMETRY__NORM_AMPS = CommonPackage.NAMED_FEATURE_COUNT + 8;

	/**
	 * The feature id for the '<em><b>Emerg Amps</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINE_GEOMETRY__EMERG_AMPS = CommonPackage.NAMED_FEATURE_COUNT + 9;

	/**
	 * The feature id for the '<em><b>Reduce</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINE_GEOMETRY__REDUCE = CommonPackage.NAMED_FEATURE_COUNT + 10;

	/**
	 * The feature id for the '<em><b>Wires</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINE_GEOMETRY__WIRES = CommonPackage.NAMED_FEATURE_COUNT + 11;

	/**
	 * The feature id for the '<em><b>Like</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINE_GEOMETRY__LIKE = CommonPackage.NAMED_FEATURE_COUNT + 12;

	/**
	 * The number of structural features of the '<em>Line Geometry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINE_GEOMETRY_FEATURE_COUNT = CommonPackage.NAMED_FEATURE_COUNT + 13;

	/**
	 * The meta object id for the '{@link electrickery.general.impl.LineSpacingImpl <em>Line Spacing</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see electrickery.general.impl.LineSpacingImpl
	 * @see electrickery.general.impl.GeneralPackageImpl#getLineSpacing()
	 * @generated
	 */
	int LINE_SPACING = 4;

	/**
	 * The feature id for the '<em><b>NConds</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINE_SPACING__NCONDS = 0;

	/**
	 * The feature id for the '<em><b>NPhases</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINE_SPACING__NPHASES = 1;

	/**
	 * The feature id for the '<em><b>X</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINE_SPACING__X = 2;

	/**
	 * The feature id for the '<em><b>H</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINE_SPACING__H = 3;

	/**
	 * The feature id for the '<em><b>Units</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINE_SPACING__UNITS = 4;

	/**
	 * The feature id for the '<em><b>Like</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINE_SPACING__LIKE = 5;

	/**
	 * The number of structural features of the '<em>Line Spacing</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINE_SPACING_FEATURE_COUNT = 6;

	/**
	 * The meta object id for the '{@link electrickery.general.impl.LoadShapeImpl <em>Load Shape</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see electrickery.general.impl.LoadShapeImpl
	 * @see electrickery.general.impl.GeneralPackageImpl#getLoadShape()
	 * @generated
	 */
	int LOAD_SHAPE = 5;

	/**
	 * The feature id for the '<em><b>NPts</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOAD_SHAPE__NPTS = 0;

	/**
	 * The feature id for the '<em><b>Interval</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOAD_SHAPE__INTERVAL = 1;

	/**
	 * The feature id for the '<em><b>Mult</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOAD_SHAPE__MULT = 2;

	/**
	 * The feature id for the '<em><b>Hour</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOAD_SHAPE__HOUR = 3;

	/**
	 * The feature id for the '<em><b>Mean</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOAD_SHAPE__MEAN = 4;

	/**
	 * The feature id for the '<em><b>Std Dev</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOAD_SHAPE__STD_DEV = 5;

	/**
	 * The feature id for the '<em><b>Csv File</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOAD_SHAPE__CSV_FILE = 6;

	/**
	 * The feature id for the '<em><b>Sng File</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOAD_SHAPE__SNG_FILE = 7;

	/**
	 * The feature id for the '<em><b>Dbl File</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOAD_SHAPE__DBL_FILE = 8;

	/**
	 * The feature id for the '<em><b>QMult</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOAD_SHAPE__QMULT = 9;

	/**
	 * The number of structural features of the '<em>Load Shape</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOAD_SHAPE_FEATURE_COUNT = 10;

	/**
	 * The meta object id for the '{@link electrickery.general.impl.SpectrumImpl <em>Spectrum</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see electrickery.general.impl.SpectrumImpl
	 * @see electrickery.general.impl.GeneralPackageImpl#getSpectrum()
	 * @generated
	 */
	int SPECTRUM = 6;

	/**
	 * The feature id for the '<em><b>NHarm</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SPECTRUM__NHARM = 0;

	/**
	 * The feature id for the '<em><b>Harmonic</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SPECTRUM__HARMONIC = 1;

	/**
	 * The feature id for the '<em><b>Pct Mag</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SPECTRUM__PCT_MAG = 2;

	/**
	 * The feature id for the '<em><b>Angle</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SPECTRUM__ANGLE = 3;

	/**
	 * The feature id for the '<em><b>Csv File</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SPECTRUM__CSV_FILE = 4;

	/**
	 * The number of structural features of the '<em>Spectrum</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SPECTRUM_FEATURE_COUNT = 5;

	/**
	 * The meta object id for the '{@link electrickery.general.impl.TimeCurrentCurveImpl <em>Time Current Curve</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see electrickery.general.impl.TimeCurrentCurveImpl
	 * @see electrickery.general.impl.GeneralPackageImpl#getTimeCurrentCurve()
	 * @generated
	 */
	int TIME_CURRENT_CURVE = 7;

	/**
	 * The feature id for the '<em><b>NPts</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIME_CURRENT_CURVE__NPTS = 0;

	/**
	 * The feature id for the '<em><b>CArray</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIME_CURRENT_CURVE__CARRAY = 1;

	/**
	 * The feature id for the '<em><b>TArray</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIME_CURRENT_CURVE__TARRAY = 2;

	/**
	 * The number of structural features of the '<em>Time Current Curve</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIME_CURRENT_CURVE_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link electrickery.general.impl.WireDataImpl <em>Wire Data</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see electrickery.general.impl.WireDataImpl
	 * @see electrickery.general.impl.GeneralPackageImpl#getWireData()
	 * @generated
	 */
	int WIRE_DATA = 8;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIRE_DATA__NAME = CommonPackage.NAMED__NAME;

	/**
	 * The feature id for the '<em><b>RDC</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIRE_DATA__RDC = CommonPackage.NAMED_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>RAC</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIRE_DATA__RAC = CommonPackage.NAMED_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>RUnits</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIRE_DATA__RUNITS = CommonPackage.NAMED_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Gmr AC</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIRE_DATA__GMR_AC = CommonPackage.NAMED_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Gmr Units</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIRE_DATA__GMR_UNITS = CommonPackage.NAMED_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Radius</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIRE_DATA__RADIUS = CommonPackage.NAMED_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Rad Units</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIRE_DATA__RAD_UNITS = CommonPackage.NAMED_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>Norm Amps</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIRE_DATA__NORM_AMPS = CommonPackage.NAMED_FEATURE_COUNT + 7;

	/**
	 * The feature id for the '<em><b>Emerg Amps</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIRE_DATA__EMERG_AMPS = CommonPackage.NAMED_FEATURE_COUNT + 8;

	/**
	 * The feature id for the '<em><b>Diameter</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIRE_DATA__DIAMETER = CommonPackage.NAMED_FEATURE_COUNT + 9;

	/**
	 * The number of structural features of the '<em>Wire Data</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIRE_DATA_FEATURE_COUNT = CommonPackage.NAMED_FEATURE_COUNT + 10;


	/**
	 * The meta object id for the '{@link electrickery.general.impl.TransformerCodeImpl <em>Transformer Code</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see electrickery.general.impl.TransformerCodeImpl
	 * @see electrickery.general.impl.GeneralPackageImpl#getTransformerCode()
	 * @generated
	 */
	int TRANSFORMER_CODE = 9;

	/**
	 * The feature id for the '<em><b>Phases</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSFORMER_CODE__PHASES = 0;

	/**
	 * The feature id for the '<em><b>Windings</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSFORMER_CODE__WINDINGS = 1;

	/**
	 * The feature id for the '<em><b>Wdg</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSFORMER_CODE__WDG = 2;

	/**
	 * The feature id for the '<em><b>Conn</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSFORMER_CODE__CONN = 3;

	/**
	 * The feature id for the '<em><b>KV</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSFORMER_CODE__KV = 4;

	/**
	 * The feature id for the '<em><b>KVA</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSFORMER_CODE__KVA = 5;

	/**
	 * The feature id for the '<em><b>Tap</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSFORMER_CODE__TAP = 6;

	/**
	 * The feature id for the '<em><b>Pct R</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSFORMER_CODE__PCT_R = 7;

	/**
	 * The feature id for the '<em><b>RNeut</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSFORMER_CODE__RNEUT = 8;

	/**
	 * The feature id for the '<em><b>XNeut</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSFORMER_CODE__XNEUT = 9;

	/**
	 * The feature id for the '<em><b>Conns</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSFORMER_CODE__CONNS = 10;

	/**
	 * The feature id for the '<em><b>KVs</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSFORMER_CODE__KVS = 11;

	/**
	 * The feature id for the '<em><b>KV As</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSFORMER_CODE__KV_AS = 12;

	/**
	 * The feature id for the '<em><b>Taps</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSFORMER_CODE__TAPS = 13;

	/**
	 * The feature id for the '<em><b>Xhl</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSFORMER_CODE__XHL = 14;

	/**
	 * The feature id for the '<em><b>Xht</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSFORMER_CODE__XHT = 15;

	/**
	 * The feature id for the '<em><b>Xlt</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSFORMER_CODE__XLT = 16;

	/**
	 * The feature id for the '<em><b>Xsc Array</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSFORMER_CODE__XSC_ARRAY = 17;

	/**
	 * The feature id for the '<em><b>Thermal</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSFORMER_CODE__THERMAL = 18;

	/**
	 * The feature id for the '<em><b>N</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSFORMER_CODE__N = 19;

	/**
	 * The feature id for the '<em><b>M</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSFORMER_CODE__M = 20;

	/**
	 * The feature id for the '<em><b>FL Rise</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSFORMER_CODE__FL_RISE = 21;

	/**
	 * The feature id for the '<em><b>HS Rise</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSFORMER_CODE__HS_RISE = 22;

	/**
	 * The feature id for the '<em><b>Pct Load Loss</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSFORMER_CODE__PCT_LOAD_LOSS = 23;

	/**
	 * The feature id for the '<em><b>Pct No Load Loss</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSFORMER_CODE__PCT_NO_LOAD_LOSS = 24;

	/**
	 * The feature id for the '<em><b>Norm HKVA</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSFORMER_CODE__NORM_HKVA = 25;

	/**
	 * The feature id for the '<em><b>Emerg HKVA</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSFORMER_CODE__EMERG_HKVA = 26;

	/**
	 * The feature id for the '<em><b>Max Tap</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSFORMER_CODE__MAX_TAP = 27;

	/**
	 * The feature id for the '<em><b>Min Tap</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSFORMER_CODE__MIN_TAP = 28;

	/**
	 * The feature id for the '<em><b>Num Taps</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSFORMER_CODE__NUM_TAPS = 29;

	/**
	 * The feature id for the '<em><b>Pct IMag</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSFORMER_CODE__PCT_IMAG = 30;

	/**
	 * The feature id for the '<em><b>Ppm Anti Float</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSFORMER_CODE__PPM_ANTI_FLOAT = 31;

	/**
	 * The feature id for the '<em><b>Pct RS</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSFORMER_CODE__PCT_RS = 32;

	/**
	 * The feature id for the '<em><b>Like</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSFORMER_CODE__LIKE = 33;

	/**
	 * The number of structural features of the '<em>Transformer Code</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRANSFORMER_CODE_FEATURE_COUNT = 34;


	/**
	 * Returns the meta object for class '{@link electrickery.general.DSSObject <em>DSS Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>DSS Object</em>'.
	 * @see electrickery.general.DSSObject
	 * @generated
	 */
	EClass getDSSObject();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.DSSObject#getDSSObjType <em>DSS Obj Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>DSS Obj Type</em>'.
	 * @see electrickery.general.DSSObject#getDSSObjType()
	 * @see #getDSSObject()
	 * @generated
	 */
	EAttribute getDSSObject_DSSObjType();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.DSSObject#getDSSClassName <em>DSS Class Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>DSS Class Name</em>'.
	 * @see electrickery.general.DSSObject#getDSSClassName()
	 * @see #getDSSObject()
	 * @generated
	 */
	EAttribute getDSSObject_DSSClassName();

	/**
	 * Returns the meta object for the reference list '{@link electrickery.general.DSSObject#getParentClass <em>Parent Class</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Parent Class</em>'.
	 * @see electrickery.general.DSSObject#getParentClass()
	 * @see #getDSSObject()
	 * @generated
	 */
	EReference getDSSObject_ParentClass();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.DSSObject#getClassIndex <em>Class Index</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Class Index</em>'.
	 * @see electrickery.general.DSSObject#getClassIndex()
	 * @see #getDSSObject()
	 * @generated
	 */
	EAttribute getDSSObject_ClassIndex();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.DSSObject#isDirty <em>Dirty</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Dirty</em>'.
	 * @see electrickery.general.DSSObject#isDirty()
	 * @see #getDSSObject()
	 * @generated
	 */
	EAttribute getDSSObject_Dirty();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.DSSObject#isFlag <em>Flag</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Flag</em>'.
	 * @see electrickery.general.DSSObject#isFlag()
	 * @see #getDSSObject()
	 * @generated
	 */
	EAttribute getDSSObject_Flag();

	/**
	 * Returns the meta object for the attribute list '{@link electrickery.general.DSSObject#getPropSequence <em>Prop Sequence</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Prop Sequence</em>'.
	 * @see electrickery.general.DSSObject#getPropSequence()
	 * @see #getDSSObject()
	 * @generated
	 */
	EAttribute getDSSObject_PropSequence();

	/**
	 * Returns the meta object for class '{@link electrickery.general.GrowthShape <em>Growth Shape</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Growth Shape</em>'.
	 * @see electrickery.general.GrowthShape
	 * @generated
	 */
	EClass getGrowthShape();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.GrowthShape#getNPts <em>NPts</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>NPts</em>'.
	 * @see electrickery.general.GrowthShape#getNPts()
	 * @see #getGrowthShape()
	 * @generated
	 */
	EAttribute getGrowthShape_NPts();

	/**
	 * Returns the meta object for the attribute list '{@link electrickery.general.GrowthShape#getYear <em>Year</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Year</em>'.
	 * @see electrickery.general.GrowthShape#getYear()
	 * @see #getGrowthShape()
	 * @generated
	 */
	EAttribute getGrowthShape_Year();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.GrowthShape#getCsvFile <em>Csv File</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Csv File</em>'.
	 * @see electrickery.general.GrowthShape#getCsvFile()
	 * @see #getGrowthShape()
	 * @generated
	 */
	EAttribute getGrowthShape_CsvFile();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.GrowthShape#getSngFile <em>Sng File</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Sng File</em>'.
	 * @see electrickery.general.GrowthShape#getSngFile()
	 * @see #getGrowthShape()
	 * @generated
	 */
	EAttribute getGrowthShape_SngFile();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.GrowthShape#getDblFile <em>Dbl File</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Dbl File</em>'.
	 * @see electrickery.general.GrowthShape#getDblFile()
	 * @see #getGrowthShape()
	 * @generated
	 */
	EAttribute getGrowthShape_DblFile();

	/**
	 * Returns the meta object for class '{@link electrickery.general.LineCode <em>Line Code</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Line Code</em>'.
	 * @see electrickery.general.LineCode
	 * @generated
	 */
	EClass getLineCode();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.LineCode#getNPhases <em>NPhases</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>NPhases</em>'.
	 * @see electrickery.general.LineCode#getNPhases()
	 * @see #getLineCode()
	 * @generated
	 */
	EAttribute getLineCode_NPhases();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.LineCode#getR1 <em>R1</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>R1</em>'.
	 * @see electrickery.general.LineCode#getR1()
	 * @see #getLineCode()
	 * @generated
	 */
	EAttribute getLineCode_R1();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.LineCode#getX1 <em>X1</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>X1</em>'.
	 * @see electrickery.general.LineCode#getX1()
	 * @see #getLineCode()
	 * @generated
	 */
	EAttribute getLineCode_X1();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.LineCode#getR0 <em>R0</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>R0</em>'.
	 * @see electrickery.general.LineCode#getR0()
	 * @see #getLineCode()
	 * @generated
	 */
	EAttribute getLineCode_R0();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.LineCode#getX0 <em>X0</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>X0</em>'.
	 * @see electrickery.general.LineCode#getX0()
	 * @see #getLineCode()
	 * @generated
	 */
	EAttribute getLineCode_X0();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.LineCode#getC1 <em>C1</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>C1</em>'.
	 * @see electrickery.general.LineCode#getC1()
	 * @see #getLineCode()
	 * @generated
	 */
	EAttribute getLineCode_C1();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.LineCode#getC0 <em>C0</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>C0</em>'.
	 * @see electrickery.general.LineCode#getC0()
	 * @see #getLineCode()
	 * @generated
	 */
	EAttribute getLineCode_C0();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.LineCode#getUnits <em>Units</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Units</em>'.
	 * @see electrickery.general.LineCode#getUnits()
	 * @see #getLineCode()
	 * @generated
	 */
	EAttribute getLineCode_Units();

	/**
	 * Returns the meta object for the reference '{@link electrickery.general.LineCode#getRMatrix <em>RMatrix</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>RMatrix</em>'.
	 * @see electrickery.general.LineCode#getRMatrix()
	 * @see #getLineCode()
	 * @generated
	 */
	EReference getLineCode_RMatrix();

	/**
	 * Returns the meta object for the reference '{@link electrickery.general.LineCode#getXMatrix <em>XMatrix</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>XMatrix</em>'.
	 * @see electrickery.general.LineCode#getXMatrix()
	 * @see #getLineCode()
	 * @generated
	 */
	EReference getLineCode_XMatrix();

	/**
	 * Returns the meta object for the reference '{@link electrickery.general.LineCode#getCMatrix <em>CMatrix</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>CMatrix</em>'.
	 * @see electrickery.general.LineCode#getCMatrix()
	 * @see #getLineCode()
	 * @generated
	 */
	EReference getLineCode_CMatrix();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.LineCode#getBaseFreq <em>Base Freq</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Base Freq</em>'.
	 * @see electrickery.general.LineCode#getBaseFreq()
	 * @see #getLineCode()
	 * @generated
	 */
	EAttribute getLineCode_BaseFreq();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.LineCode#getNormAmps <em>Norm Amps</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Norm Amps</em>'.
	 * @see electrickery.general.LineCode#getNormAmps()
	 * @see #getLineCode()
	 * @generated
	 */
	EAttribute getLineCode_NormAmps();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.LineCode#getEmergAmps <em>Emerg Amps</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Emerg Amps</em>'.
	 * @see electrickery.general.LineCode#getEmergAmps()
	 * @see #getLineCode()
	 * @generated
	 */
	EAttribute getLineCode_EmergAmps();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.LineCode#getFaultRate <em>Fault Rate</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Fault Rate</em>'.
	 * @see electrickery.general.LineCode#getFaultRate()
	 * @see #getLineCode()
	 * @generated
	 */
	EAttribute getLineCode_FaultRate();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.LineCode#getPctPerm <em>Pct Perm</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Pct Perm</em>'.
	 * @see electrickery.general.LineCode#getPctPerm()
	 * @see #getLineCode()
	 * @generated
	 */
	EAttribute getLineCode_PctPerm();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.LineCode#getRepair <em>Repair</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Repair</em>'.
	 * @see electrickery.general.LineCode#getRepair()
	 * @see #getLineCode()
	 * @generated
	 */
	EAttribute getLineCode_Repair();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.LineCode#isKron <em>Kron</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Kron</em>'.
	 * @see electrickery.general.LineCode#isKron()
	 * @see #getLineCode()
	 * @generated
	 */
	EAttribute getLineCode_Kron();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.LineCode#getRg <em>Rg</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Rg</em>'.
	 * @see electrickery.general.LineCode#getRg()
	 * @see #getLineCode()
	 * @generated
	 */
	EAttribute getLineCode_Rg();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.LineCode#getXg <em>Xg</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Xg</em>'.
	 * @see electrickery.general.LineCode#getXg()
	 * @see #getLineCode()
	 * @generated
	 */
	EAttribute getLineCode_Xg();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.LineCode#getRho <em>Rho</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Rho</em>'.
	 * @see electrickery.general.LineCode#getRho()
	 * @see #getLineCode()
	 * @generated
	 */
	EAttribute getLineCode_Rho();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.LineCode#getNeutral <em>Neutral</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Neutral</em>'.
	 * @see electrickery.general.LineCode#getNeutral()
	 * @see #getLineCode()
	 * @generated
	 */
	EAttribute getLineCode_Neutral();

	/**
	 * Returns the meta object for the reference '{@link electrickery.general.LineCode#getLike <em>Like</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Like</em>'.
	 * @see electrickery.general.LineCode#getLike()
	 * @see #getLineCode()
	 * @generated
	 */
	EReference getLineCode_Like();

	/**
	 * Returns the meta object for class '{@link electrickery.general.LineGeometry <em>Line Geometry</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Line Geometry</em>'.
	 * @see electrickery.general.LineGeometry
	 * @generated
	 */
	EClass getLineGeometry();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.LineGeometry#getNConds <em>NConds</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>NConds</em>'.
	 * @see electrickery.general.LineGeometry#getNConds()
	 * @see #getLineGeometry()
	 * @generated
	 */
	EAttribute getLineGeometry_NConds();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.LineGeometry#getNPhases <em>NPhases</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>NPhases</em>'.
	 * @see electrickery.general.LineGeometry#getNPhases()
	 * @see #getLineGeometry()
	 * @generated
	 */
	EAttribute getLineGeometry_NPhases();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.LineGeometry#getCond <em>Cond</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Cond</em>'.
	 * @see electrickery.general.LineGeometry#getCond()
	 * @see #getLineGeometry()
	 * @generated
	 */
	EAttribute getLineGeometry_Cond();

	/**
	 * Returns the meta object for the reference '{@link electrickery.general.LineGeometry#getWire <em>Wire</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Wire</em>'.
	 * @see electrickery.general.LineGeometry#getWire()
	 * @see #getLineGeometry()
	 * @generated
	 */
	EReference getLineGeometry_Wire();

	/**
	 * Returns the meta object for the reference list '{@link electrickery.general.LineGeometry#getWires <em>Wires</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Wires</em>'.
	 * @see electrickery.general.LineGeometry#getWires()
	 * @see #getLineGeometry()
	 * @generated
	 */
	EReference getLineGeometry_Wires();

	/**
	 * Returns the meta object for the reference '{@link electrickery.general.LineGeometry#getLike <em>Like</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Like</em>'.
	 * @see electrickery.general.LineGeometry#getLike()
	 * @see #getLineGeometry()
	 * @generated
	 */
	EReference getLineGeometry_Like();

	/**
	 * Returns the meta object for the reference '{@link electrickery.general.LineGeometry#getSpacing <em>Spacing</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Spacing</em>'.
	 * @see electrickery.general.LineGeometry#getSpacing()
	 * @see #getLineGeometry()
	 * @generated
	 */
	EReference getLineGeometry_Spacing();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.LineGeometry#getX <em>X</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>X</em>'.
	 * @see electrickery.general.LineGeometry#getX()
	 * @see #getLineGeometry()
	 * @generated
	 */
	EAttribute getLineGeometry_X();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.LineGeometry#getH <em>H</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>H</em>'.
	 * @see electrickery.general.LineGeometry#getH()
	 * @see #getLineGeometry()
	 * @generated
	 */
	EAttribute getLineGeometry_H();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.LineGeometry#getUnits <em>Units</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Units</em>'.
	 * @see electrickery.general.LineGeometry#getUnits()
	 * @see #getLineGeometry()
	 * @generated
	 */
	EAttribute getLineGeometry_Units();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.LineGeometry#getNormAmps <em>Norm Amps</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Norm Amps</em>'.
	 * @see electrickery.general.LineGeometry#getNormAmps()
	 * @see #getLineGeometry()
	 * @generated
	 */
	EAttribute getLineGeometry_NormAmps();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.LineGeometry#getEmergAmps <em>Emerg Amps</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Emerg Amps</em>'.
	 * @see electrickery.general.LineGeometry#getEmergAmps()
	 * @see #getLineGeometry()
	 * @generated
	 */
	EAttribute getLineGeometry_EmergAmps();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.LineGeometry#isReduce <em>Reduce</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Reduce</em>'.
	 * @see electrickery.general.LineGeometry#isReduce()
	 * @see #getLineGeometry()
	 * @generated
	 */
	EAttribute getLineGeometry_Reduce();

	/**
	 * Returns the meta object for class '{@link electrickery.general.LineSpacing <em>Line Spacing</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Line Spacing</em>'.
	 * @see electrickery.general.LineSpacing
	 * @generated
	 */
	EClass getLineSpacing();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.LineSpacing#getNConds <em>NConds</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>NConds</em>'.
	 * @see electrickery.general.LineSpacing#getNConds()
	 * @see #getLineSpacing()
	 * @generated
	 */
	EAttribute getLineSpacing_NConds();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.LineSpacing#getNPhases <em>NPhases</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>NPhases</em>'.
	 * @see electrickery.general.LineSpacing#getNPhases()
	 * @see #getLineSpacing()
	 * @generated
	 */
	EAttribute getLineSpacing_NPhases();

	/**
	 * Returns the meta object for the attribute list '{@link electrickery.general.LineSpacing#getX <em>X</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>X</em>'.
	 * @see electrickery.general.LineSpacing#getX()
	 * @see #getLineSpacing()
	 * @generated
	 */
	EAttribute getLineSpacing_X();

	/**
	 * Returns the meta object for the attribute list '{@link electrickery.general.LineSpacing#getH <em>H</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>H</em>'.
	 * @see electrickery.general.LineSpacing#getH()
	 * @see #getLineSpacing()
	 * @generated
	 */
	EAttribute getLineSpacing_H();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.LineSpacing#getUnits <em>Units</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Units</em>'.
	 * @see electrickery.general.LineSpacing#getUnits()
	 * @see #getLineSpacing()
	 * @generated
	 */
	EAttribute getLineSpacing_Units();

	/**
	 * Returns the meta object for the reference '{@link electrickery.general.LineSpacing#getLike <em>Like</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Like</em>'.
	 * @see electrickery.general.LineSpacing#getLike()
	 * @see #getLineSpacing()
	 * @generated
	 */
	EReference getLineSpacing_Like();

	/**
	 * Returns the meta object for class '{@link electrickery.general.LoadShape <em>Load Shape</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Load Shape</em>'.
	 * @see electrickery.general.LoadShape
	 * @generated
	 */
	EClass getLoadShape();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.LoadShape#getNPts <em>NPts</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>NPts</em>'.
	 * @see electrickery.general.LoadShape#getNPts()
	 * @see #getLoadShape()
	 * @generated
	 */
	EAttribute getLoadShape_NPts();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.LoadShape#getInterval <em>Interval</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Interval</em>'.
	 * @see electrickery.general.LoadShape#getInterval()
	 * @see #getLoadShape()
	 * @generated
	 */
	EAttribute getLoadShape_Interval();

	/**
	 * Returns the meta object for the attribute list '{@link electrickery.general.LoadShape#getMult <em>Mult</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Mult</em>'.
	 * @see electrickery.general.LoadShape#getMult()
	 * @see #getLoadShape()
	 * @generated
	 */
	EAttribute getLoadShape_Mult();

	/**
	 * Returns the meta object for the attribute list '{@link electrickery.general.LoadShape#getHour <em>Hour</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Hour</em>'.
	 * @see electrickery.general.LoadShape#getHour()
	 * @see #getLoadShape()
	 * @generated
	 */
	EAttribute getLoadShape_Hour();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.LoadShape#getMean <em>Mean</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Mean</em>'.
	 * @see electrickery.general.LoadShape#getMean()
	 * @see #getLoadShape()
	 * @generated
	 */
	EAttribute getLoadShape_Mean();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.LoadShape#getStdDev <em>Std Dev</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Std Dev</em>'.
	 * @see electrickery.general.LoadShape#getStdDev()
	 * @see #getLoadShape()
	 * @generated
	 */
	EAttribute getLoadShape_StdDev();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.LoadShape#getCsvFile <em>Csv File</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Csv File</em>'.
	 * @see electrickery.general.LoadShape#getCsvFile()
	 * @see #getLoadShape()
	 * @generated
	 */
	EAttribute getLoadShape_CsvFile();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.LoadShape#getSngFile <em>Sng File</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Sng File</em>'.
	 * @see electrickery.general.LoadShape#getSngFile()
	 * @see #getLoadShape()
	 * @generated
	 */
	EAttribute getLoadShape_SngFile();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.LoadShape#getDblFile <em>Dbl File</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Dbl File</em>'.
	 * @see electrickery.general.LoadShape#getDblFile()
	 * @see #getLoadShape()
	 * @generated
	 */
	EAttribute getLoadShape_DblFile();

	/**
	 * Returns the meta object for the attribute list '{@link electrickery.general.LoadShape#getQMult <em>QMult</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>QMult</em>'.
	 * @see electrickery.general.LoadShape#getQMult()
	 * @see #getLoadShape()
	 * @generated
	 */
	EAttribute getLoadShape_QMult();

	/**
	 * Returns the meta object for class '{@link electrickery.general.Spectrum <em>Spectrum</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Spectrum</em>'.
	 * @see electrickery.general.Spectrum
	 * @generated
	 */
	EClass getSpectrum();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.Spectrum#getNHarm <em>NHarm</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>NHarm</em>'.
	 * @see electrickery.general.Spectrum#getNHarm()
	 * @see #getSpectrum()
	 * @generated
	 */
	EAttribute getSpectrum_NHarm();

	/**
	 * Returns the meta object for the attribute list '{@link electrickery.general.Spectrum#getHarmonic <em>Harmonic</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Harmonic</em>'.
	 * @see electrickery.general.Spectrum#getHarmonic()
	 * @see #getSpectrum()
	 * @generated
	 */
	EAttribute getSpectrum_Harmonic();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.Spectrum#getPctMag <em>Pct Mag</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Pct Mag</em>'.
	 * @see electrickery.general.Spectrum#getPctMag()
	 * @see #getSpectrum()
	 * @generated
	 */
	EAttribute getSpectrum_PctMag();

	/**
	 * Returns the meta object for the attribute list '{@link electrickery.general.Spectrum#getAngle <em>Angle</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Angle</em>'.
	 * @see electrickery.general.Spectrum#getAngle()
	 * @see #getSpectrum()
	 * @generated
	 */
	EAttribute getSpectrum_Angle();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.Spectrum#getCsvFile <em>Csv File</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Csv File</em>'.
	 * @see electrickery.general.Spectrum#getCsvFile()
	 * @see #getSpectrum()
	 * @generated
	 */
	EAttribute getSpectrum_CsvFile();

	/**
	 * Returns the meta object for class '{@link electrickery.general.TimeCurrentCurve <em>Time Current Curve</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Time Current Curve</em>'.
	 * @see electrickery.general.TimeCurrentCurve
	 * @generated
	 */
	EClass getTimeCurrentCurve();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.TimeCurrentCurve#getNPts <em>NPts</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>NPts</em>'.
	 * @see electrickery.general.TimeCurrentCurve#getNPts()
	 * @see #getTimeCurrentCurve()
	 * @generated
	 */
	EAttribute getTimeCurrentCurve_NPts();

	/**
	 * Returns the meta object for the attribute list '{@link electrickery.general.TimeCurrentCurve#getCArray <em>CArray</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>CArray</em>'.
	 * @see electrickery.general.TimeCurrentCurve#getCArray()
	 * @see #getTimeCurrentCurve()
	 * @generated
	 */
	EAttribute getTimeCurrentCurve_CArray();

	/**
	 * Returns the meta object for the attribute list '{@link electrickery.general.TimeCurrentCurve#getTArray <em>TArray</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>TArray</em>'.
	 * @see electrickery.general.TimeCurrentCurve#getTArray()
	 * @see #getTimeCurrentCurve()
	 * @generated
	 */
	EAttribute getTimeCurrentCurve_TArray();

	/**
	 * Returns the meta object for class '{@link electrickery.general.WireData <em>Wire Data</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Wire Data</em>'.
	 * @see electrickery.general.WireData
	 * @generated
	 */
	EClass getWireData();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.WireData#getRDC <em>RDC</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>RDC</em>'.
	 * @see electrickery.general.WireData#getRDC()
	 * @see #getWireData()
	 * @generated
	 */
	EAttribute getWireData_RDC();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.WireData#getRAC <em>RAC</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>RAC</em>'.
	 * @see electrickery.general.WireData#getRAC()
	 * @see #getWireData()
	 * @generated
	 */
	EAttribute getWireData_RAC();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.WireData#getRUnits <em>RUnits</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>RUnits</em>'.
	 * @see electrickery.general.WireData#getRUnits()
	 * @see #getWireData()
	 * @generated
	 */
	EAttribute getWireData_RUnits();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.WireData#getGmrAC <em>Gmr AC</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Gmr AC</em>'.
	 * @see electrickery.general.WireData#getGmrAC()
	 * @see #getWireData()
	 * @generated
	 */
	EAttribute getWireData_GmrAC();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.WireData#getGmrUnits <em>Gmr Units</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Gmr Units</em>'.
	 * @see electrickery.general.WireData#getGmrUnits()
	 * @see #getWireData()
	 * @generated
	 */
	EAttribute getWireData_GmrUnits();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.WireData#getRadius <em>Radius</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Radius</em>'.
	 * @see electrickery.general.WireData#getRadius()
	 * @see #getWireData()
	 * @generated
	 */
	EAttribute getWireData_Radius();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.WireData#getRadUnits <em>Rad Units</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Rad Units</em>'.
	 * @see electrickery.general.WireData#getRadUnits()
	 * @see #getWireData()
	 * @generated
	 */
	EAttribute getWireData_RadUnits();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.WireData#getNormAmps <em>Norm Amps</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Norm Amps</em>'.
	 * @see electrickery.general.WireData#getNormAmps()
	 * @see #getWireData()
	 * @generated
	 */
	EAttribute getWireData_NormAmps();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.WireData#getEmergAmps <em>Emerg Amps</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Emerg Amps</em>'.
	 * @see electrickery.general.WireData#getEmergAmps()
	 * @see #getWireData()
	 * @generated
	 */
	EAttribute getWireData_EmergAmps();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.WireData#getDiameter <em>Diameter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Diameter</em>'.
	 * @see electrickery.general.WireData#getDiameter()
	 * @see #getWireData()
	 * @generated
	 */
	EAttribute getWireData_Diameter();

	/**
	 * Returns the meta object for class '{@link electrickery.general.TransformerCode <em>Transformer Code</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Transformer Code</em>'.
	 * @see electrickery.general.TransformerCode
	 * @generated
	 */
	EClass getTransformerCode();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.TransformerCode#getPhases <em>Phases</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Phases</em>'.
	 * @see electrickery.general.TransformerCode#getPhases()
	 * @see #getTransformerCode()
	 * @generated
	 */
	EAttribute getTransformerCode_Phases();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.TransformerCode#getWindings <em>Windings</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Windings</em>'.
	 * @see electrickery.general.TransformerCode#getWindings()
	 * @see #getTransformerCode()
	 * @generated
	 */
	EAttribute getTransformerCode_Windings();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.TransformerCode#getWdg <em>Wdg</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Wdg</em>'.
	 * @see electrickery.general.TransformerCode#getWdg()
	 * @see #getTransformerCode()
	 * @generated
	 */
	EAttribute getTransformerCode_Wdg();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.TransformerCode#getConn <em>Conn</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Conn</em>'.
	 * @see electrickery.general.TransformerCode#getConn()
	 * @see #getTransformerCode()
	 * @generated
	 */
	EAttribute getTransformerCode_Conn();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.TransformerCode#getKV <em>KV</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>KV</em>'.
	 * @see electrickery.general.TransformerCode#getKV()
	 * @see #getTransformerCode()
	 * @generated
	 */
	EAttribute getTransformerCode_KV();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.TransformerCode#getKVA <em>KVA</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>KVA</em>'.
	 * @see electrickery.general.TransformerCode#getKVA()
	 * @see #getTransformerCode()
	 * @generated
	 */
	EAttribute getTransformerCode_KVA();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.TransformerCode#getTap <em>Tap</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Tap</em>'.
	 * @see electrickery.general.TransformerCode#getTap()
	 * @see #getTransformerCode()
	 * @generated
	 */
	EAttribute getTransformerCode_Tap();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.TransformerCode#getPctR <em>Pct R</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Pct R</em>'.
	 * @see electrickery.general.TransformerCode#getPctR()
	 * @see #getTransformerCode()
	 * @generated
	 */
	EAttribute getTransformerCode_PctR();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.TransformerCode#getRNeut <em>RNeut</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>RNeut</em>'.
	 * @see electrickery.general.TransformerCode#getRNeut()
	 * @see #getTransformerCode()
	 * @generated
	 */
	EAttribute getTransformerCode_RNeut();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.TransformerCode#getXNeut <em>XNeut</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>XNeut</em>'.
	 * @see electrickery.general.TransformerCode#getXNeut()
	 * @see #getTransformerCode()
	 * @generated
	 */
	EAttribute getTransformerCode_XNeut();

	/**
	 * Returns the meta object for the attribute list '{@link electrickery.general.TransformerCode#getConns <em>Conns</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Conns</em>'.
	 * @see electrickery.general.TransformerCode#getConns()
	 * @see #getTransformerCode()
	 * @generated
	 */
	EAttribute getTransformerCode_Conns();

	/**
	 * Returns the meta object for the attribute list '{@link electrickery.general.TransformerCode#getKVs <em>KVs</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>KVs</em>'.
	 * @see electrickery.general.TransformerCode#getKVs()
	 * @see #getTransformerCode()
	 * @generated
	 */
	EAttribute getTransformerCode_KVs();

	/**
	 * Returns the meta object for the attribute list '{@link electrickery.general.TransformerCode#getKVAs <em>KV As</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>KV As</em>'.
	 * @see electrickery.general.TransformerCode#getKVAs()
	 * @see #getTransformerCode()
	 * @generated
	 */
	EAttribute getTransformerCode_KVAs();

	/**
	 * Returns the meta object for the attribute list '{@link electrickery.general.TransformerCode#getTaps <em>Taps</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Taps</em>'.
	 * @see electrickery.general.TransformerCode#getTaps()
	 * @see #getTransformerCode()
	 * @generated
	 */
	EAttribute getTransformerCode_Taps();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.TransformerCode#getXhl <em>Xhl</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Xhl</em>'.
	 * @see electrickery.general.TransformerCode#getXhl()
	 * @see #getTransformerCode()
	 * @generated
	 */
	EAttribute getTransformerCode_Xhl();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.TransformerCode#getXht <em>Xht</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Xht</em>'.
	 * @see electrickery.general.TransformerCode#getXht()
	 * @see #getTransformerCode()
	 * @generated
	 */
	EAttribute getTransformerCode_Xht();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.TransformerCode#getXlt <em>Xlt</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Xlt</em>'.
	 * @see electrickery.general.TransformerCode#getXlt()
	 * @see #getTransformerCode()
	 * @generated
	 */
	EAttribute getTransformerCode_Xlt();

	/**
	 * Returns the meta object for the attribute list '{@link electrickery.general.TransformerCode#getXscArray <em>Xsc Array</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Xsc Array</em>'.
	 * @see electrickery.general.TransformerCode#getXscArray()
	 * @see #getTransformerCode()
	 * @generated
	 */
	EAttribute getTransformerCode_XscArray();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.TransformerCode#getThermal <em>Thermal</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Thermal</em>'.
	 * @see electrickery.general.TransformerCode#getThermal()
	 * @see #getTransformerCode()
	 * @generated
	 */
	EAttribute getTransformerCode_Thermal();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.TransformerCode#getN <em>N</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>N</em>'.
	 * @see electrickery.general.TransformerCode#getN()
	 * @see #getTransformerCode()
	 * @generated
	 */
	EAttribute getTransformerCode_N();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.TransformerCode#getM <em>M</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>M</em>'.
	 * @see electrickery.general.TransformerCode#getM()
	 * @see #getTransformerCode()
	 * @generated
	 */
	EAttribute getTransformerCode_M();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.TransformerCode#getFLRise <em>FL Rise</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>FL Rise</em>'.
	 * @see electrickery.general.TransformerCode#getFLRise()
	 * @see #getTransformerCode()
	 * @generated
	 */
	EAttribute getTransformerCode_FLRise();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.TransformerCode#getHSRise <em>HS Rise</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>HS Rise</em>'.
	 * @see electrickery.general.TransformerCode#getHSRise()
	 * @see #getTransformerCode()
	 * @generated
	 */
	EAttribute getTransformerCode_HSRise();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.TransformerCode#getPctLoadLoss <em>Pct Load Loss</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Pct Load Loss</em>'.
	 * @see electrickery.general.TransformerCode#getPctLoadLoss()
	 * @see #getTransformerCode()
	 * @generated
	 */
	EAttribute getTransformerCode_PctLoadLoss();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.TransformerCode#getPctNoLoadLoss <em>Pct No Load Loss</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Pct No Load Loss</em>'.
	 * @see electrickery.general.TransformerCode#getPctNoLoadLoss()
	 * @see #getTransformerCode()
	 * @generated
	 */
	EAttribute getTransformerCode_PctNoLoadLoss();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.TransformerCode#getNormHKVA <em>Norm HKVA</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Norm HKVA</em>'.
	 * @see electrickery.general.TransformerCode#getNormHKVA()
	 * @see #getTransformerCode()
	 * @generated
	 */
	EAttribute getTransformerCode_NormHKVA();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.TransformerCode#getEmergHKVA <em>Emerg HKVA</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Emerg HKVA</em>'.
	 * @see electrickery.general.TransformerCode#getEmergHKVA()
	 * @see #getTransformerCode()
	 * @generated
	 */
	EAttribute getTransformerCode_EmergHKVA();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.TransformerCode#getMaxTap <em>Max Tap</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Max Tap</em>'.
	 * @see electrickery.general.TransformerCode#getMaxTap()
	 * @see #getTransformerCode()
	 * @generated
	 */
	EAttribute getTransformerCode_MaxTap();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.TransformerCode#getMinTap <em>Min Tap</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Min Tap</em>'.
	 * @see electrickery.general.TransformerCode#getMinTap()
	 * @see #getTransformerCode()
	 * @generated
	 */
	EAttribute getTransformerCode_MinTap();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.TransformerCode#getNumTaps <em>Num Taps</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Num Taps</em>'.
	 * @see electrickery.general.TransformerCode#getNumTaps()
	 * @see #getTransformerCode()
	 * @generated
	 */
	EAttribute getTransformerCode_NumTaps();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.TransformerCode#getPctIMag <em>Pct IMag</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Pct IMag</em>'.
	 * @see electrickery.general.TransformerCode#getPctIMag()
	 * @see #getTransformerCode()
	 * @generated
	 */
	EAttribute getTransformerCode_PctIMag();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.TransformerCode#getPpmAntiFloat <em>Ppm Anti Float</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ppm Anti Float</em>'.
	 * @see electrickery.general.TransformerCode#getPpmAntiFloat()
	 * @see #getTransformerCode()
	 * @generated
	 */
	EAttribute getTransformerCode_PpmAntiFloat();

	/**
	 * Returns the meta object for the attribute '{@link electrickery.general.TransformerCode#getPctRS <em>Pct RS</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Pct RS</em>'.
	 * @see electrickery.general.TransformerCode#getPctRS()
	 * @see #getTransformerCode()
	 * @generated
	 */
	EAttribute getTransformerCode_PctRS();

	/**
	 * Returns the meta object for the reference '{@link electrickery.general.TransformerCode#getLike <em>Like</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Like</em>'.
	 * @see electrickery.general.TransformerCode#getLike()
	 * @see #getTransformerCode()
	 * @generated
	 */
	EReference getTransformerCode_Like();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	GeneralFactory getGeneralFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link electrickery.general.impl.DSSObjectImpl <em>DSS Object</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see electrickery.general.impl.DSSObjectImpl
		 * @see electrickery.general.impl.GeneralPackageImpl#getDSSObject()
		 * @generated
		 */
		EClass DSS_OBJECT = eINSTANCE.getDSSObject();

		/**
		 * The meta object literal for the '<em><b>DSS Obj Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DSS_OBJECT__DSS_OBJ_TYPE = eINSTANCE.getDSSObject_DSSObjType();

		/**
		 * The meta object literal for the '<em><b>DSS Class Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DSS_OBJECT__DSS_CLASS_NAME = eINSTANCE.getDSSObject_DSSClassName();

		/**
		 * The meta object literal for the '<em><b>Parent Class</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DSS_OBJECT__PARENT_CLASS = eINSTANCE.getDSSObject_ParentClass();

		/**
		 * The meta object literal for the '<em><b>Class Index</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DSS_OBJECT__CLASS_INDEX = eINSTANCE.getDSSObject_ClassIndex();

		/**
		 * The meta object literal for the '<em><b>Dirty</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DSS_OBJECT__DIRTY = eINSTANCE.getDSSObject_Dirty();

		/**
		 * The meta object literal for the '<em><b>Flag</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DSS_OBJECT__FLAG = eINSTANCE.getDSSObject_Flag();

		/**
		 * The meta object literal for the '<em><b>Prop Sequence</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DSS_OBJECT__PROP_SEQUENCE = eINSTANCE.getDSSObject_PropSequence();

		/**
		 * The meta object literal for the '{@link electrickery.general.impl.GrowthShapeImpl <em>Growth Shape</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see electrickery.general.impl.GrowthShapeImpl
		 * @see electrickery.general.impl.GeneralPackageImpl#getGrowthShape()
		 * @generated
		 */
		EClass GROWTH_SHAPE = eINSTANCE.getGrowthShape();

		/**
		 * The meta object literal for the '<em><b>NPts</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GROWTH_SHAPE__NPTS = eINSTANCE.getGrowthShape_NPts();

		/**
		 * The meta object literal for the '<em><b>Year</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GROWTH_SHAPE__YEAR = eINSTANCE.getGrowthShape_Year();

		/**
		 * The meta object literal for the '<em><b>Csv File</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GROWTH_SHAPE__CSV_FILE = eINSTANCE.getGrowthShape_CsvFile();

		/**
		 * The meta object literal for the '<em><b>Sng File</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GROWTH_SHAPE__SNG_FILE = eINSTANCE.getGrowthShape_SngFile();

		/**
		 * The meta object literal for the '<em><b>Dbl File</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GROWTH_SHAPE__DBL_FILE = eINSTANCE.getGrowthShape_DblFile();

		/**
		 * The meta object literal for the '{@link electrickery.general.impl.LineCodeImpl <em>Line Code</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see electrickery.general.impl.LineCodeImpl
		 * @see electrickery.general.impl.GeneralPackageImpl#getLineCode()
		 * @generated
		 */
		EClass LINE_CODE = eINSTANCE.getLineCode();

		/**
		 * The meta object literal for the '<em><b>NPhases</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LINE_CODE__NPHASES = eINSTANCE.getLineCode_NPhases();

		/**
		 * The meta object literal for the '<em><b>R1</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LINE_CODE__R1 = eINSTANCE.getLineCode_R1();

		/**
		 * The meta object literal for the '<em><b>X1</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LINE_CODE__X1 = eINSTANCE.getLineCode_X1();

		/**
		 * The meta object literal for the '<em><b>R0</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LINE_CODE__R0 = eINSTANCE.getLineCode_R0();

		/**
		 * The meta object literal for the '<em><b>X0</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LINE_CODE__X0 = eINSTANCE.getLineCode_X0();

		/**
		 * The meta object literal for the '<em><b>C1</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LINE_CODE__C1 = eINSTANCE.getLineCode_C1();

		/**
		 * The meta object literal for the '<em><b>C0</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LINE_CODE__C0 = eINSTANCE.getLineCode_C0();

		/**
		 * The meta object literal for the '<em><b>Units</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LINE_CODE__UNITS = eINSTANCE.getLineCode_Units();

		/**
		 * The meta object literal for the '<em><b>RMatrix</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LINE_CODE__RMATRIX = eINSTANCE.getLineCode_RMatrix();

		/**
		 * The meta object literal for the '<em><b>XMatrix</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LINE_CODE__XMATRIX = eINSTANCE.getLineCode_XMatrix();

		/**
		 * The meta object literal for the '<em><b>CMatrix</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LINE_CODE__CMATRIX = eINSTANCE.getLineCode_CMatrix();

		/**
		 * The meta object literal for the '<em><b>Base Freq</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LINE_CODE__BASE_FREQ = eINSTANCE.getLineCode_BaseFreq();

		/**
		 * The meta object literal for the '<em><b>Norm Amps</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LINE_CODE__NORM_AMPS = eINSTANCE.getLineCode_NormAmps();

		/**
		 * The meta object literal for the '<em><b>Emerg Amps</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LINE_CODE__EMERG_AMPS = eINSTANCE.getLineCode_EmergAmps();

		/**
		 * The meta object literal for the '<em><b>Fault Rate</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LINE_CODE__FAULT_RATE = eINSTANCE.getLineCode_FaultRate();

		/**
		 * The meta object literal for the '<em><b>Pct Perm</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LINE_CODE__PCT_PERM = eINSTANCE.getLineCode_PctPerm();

		/**
		 * The meta object literal for the '<em><b>Repair</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LINE_CODE__REPAIR = eINSTANCE.getLineCode_Repair();

		/**
		 * The meta object literal for the '<em><b>Kron</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LINE_CODE__KRON = eINSTANCE.getLineCode_Kron();

		/**
		 * The meta object literal for the '<em><b>Rg</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LINE_CODE__RG = eINSTANCE.getLineCode_Rg();

		/**
		 * The meta object literal for the '<em><b>Xg</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LINE_CODE__XG = eINSTANCE.getLineCode_Xg();

		/**
		 * The meta object literal for the '<em><b>Rho</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LINE_CODE__RHO = eINSTANCE.getLineCode_Rho();

		/**
		 * The meta object literal for the '<em><b>Neutral</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LINE_CODE__NEUTRAL = eINSTANCE.getLineCode_Neutral();

		/**
		 * The meta object literal for the '<em><b>Like</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LINE_CODE__LIKE = eINSTANCE.getLineCode_Like();

		/**
		 * The meta object literal for the '{@link electrickery.general.impl.LineGeometryImpl <em>Line Geometry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see electrickery.general.impl.LineGeometryImpl
		 * @see electrickery.general.impl.GeneralPackageImpl#getLineGeometry()
		 * @generated
		 */
		EClass LINE_GEOMETRY = eINSTANCE.getLineGeometry();

		/**
		 * The meta object literal for the '<em><b>NConds</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LINE_GEOMETRY__NCONDS = eINSTANCE.getLineGeometry_NConds();

		/**
		 * The meta object literal for the '<em><b>NPhases</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LINE_GEOMETRY__NPHASES = eINSTANCE.getLineGeometry_NPhases();

		/**
		 * The meta object literal for the '<em><b>Cond</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LINE_GEOMETRY__COND = eINSTANCE.getLineGeometry_Cond();

		/**
		 * The meta object literal for the '<em><b>Wire</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LINE_GEOMETRY__WIRE = eINSTANCE.getLineGeometry_Wire();

		/**
		 * The meta object literal for the '<em><b>Wires</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LINE_GEOMETRY__WIRES = eINSTANCE.getLineGeometry_Wires();

		/**
		 * The meta object literal for the '<em><b>Like</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LINE_GEOMETRY__LIKE = eINSTANCE.getLineGeometry_Like();

		/**
		 * The meta object literal for the '<em><b>Spacing</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LINE_GEOMETRY__SPACING = eINSTANCE.getLineGeometry_Spacing();

		/**
		 * The meta object literal for the '<em><b>X</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LINE_GEOMETRY__X = eINSTANCE.getLineGeometry_X();

		/**
		 * The meta object literal for the '<em><b>H</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LINE_GEOMETRY__H = eINSTANCE.getLineGeometry_H();

		/**
		 * The meta object literal for the '<em><b>Units</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LINE_GEOMETRY__UNITS = eINSTANCE.getLineGeometry_Units();

		/**
		 * The meta object literal for the '<em><b>Norm Amps</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LINE_GEOMETRY__NORM_AMPS = eINSTANCE.getLineGeometry_NormAmps();

		/**
		 * The meta object literal for the '<em><b>Emerg Amps</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LINE_GEOMETRY__EMERG_AMPS = eINSTANCE.getLineGeometry_EmergAmps();

		/**
		 * The meta object literal for the '<em><b>Reduce</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LINE_GEOMETRY__REDUCE = eINSTANCE.getLineGeometry_Reduce();

		/**
		 * The meta object literal for the '{@link electrickery.general.impl.LineSpacingImpl <em>Line Spacing</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see electrickery.general.impl.LineSpacingImpl
		 * @see electrickery.general.impl.GeneralPackageImpl#getLineSpacing()
		 * @generated
		 */
		EClass LINE_SPACING = eINSTANCE.getLineSpacing();

		/**
		 * The meta object literal for the '<em><b>NConds</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LINE_SPACING__NCONDS = eINSTANCE.getLineSpacing_NConds();

		/**
		 * The meta object literal for the '<em><b>NPhases</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LINE_SPACING__NPHASES = eINSTANCE.getLineSpacing_NPhases();

		/**
		 * The meta object literal for the '<em><b>X</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LINE_SPACING__X = eINSTANCE.getLineSpacing_X();

		/**
		 * The meta object literal for the '<em><b>H</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LINE_SPACING__H = eINSTANCE.getLineSpacing_H();

		/**
		 * The meta object literal for the '<em><b>Units</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LINE_SPACING__UNITS = eINSTANCE.getLineSpacing_Units();

		/**
		 * The meta object literal for the '<em><b>Like</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LINE_SPACING__LIKE = eINSTANCE.getLineSpacing_Like();

		/**
		 * The meta object literal for the '{@link electrickery.general.impl.LoadShapeImpl <em>Load Shape</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see electrickery.general.impl.LoadShapeImpl
		 * @see electrickery.general.impl.GeneralPackageImpl#getLoadShape()
		 * @generated
		 */
		EClass LOAD_SHAPE = eINSTANCE.getLoadShape();

		/**
		 * The meta object literal for the '<em><b>NPts</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LOAD_SHAPE__NPTS = eINSTANCE.getLoadShape_NPts();

		/**
		 * The meta object literal for the '<em><b>Interval</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LOAD_SHAPE__INTERVAL = eINSTANCE.getLoadShape_Interval();

		/**
		 * The meta object literal for the '<em><b>Mult</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LOAD_SHAPE__MULT = eINSTANCE.getLoadShape_Mult();

		/**
		 * The meta object literal for the '<em><b>Hour</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LOAD_SHAPE__HOUR = eINSTANCE.getLoadShape_Hour();

		/**
		 * The meta object literal for the '<em><b>Mean</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LOAD_SHAPE__MEAN = eINSTANCE.getLoadShape_Mean();

		/**
		 * The meta object literal for the '<em><b>Std Dev</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LOAD_SHAPE__STD_DEV = eINSTANCE.getLoadShape_StdDev();

		/**
		 * The meta object literal for the '<em><b>Csv File</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LOAD_SHAPE__CSV_FILE = eINSTANCE.getLoadShape_CsvFile();

		/**
		 * The meta object literal for the '<em><b>Sng File</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LOAD_SHAPE__SNG_FILE = eINSTANCE.getLoadShape_SngFile();

		/**
		 * The meta object literal for the '<em><b>Dbl File</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LOAD_SHAPE__DBL_FILE = eINSTANCE.getLoadShape_DblFile();

		/**
		 * The meta object literal for the '<em><b>QMult</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LOAD_SHAPE__QMULT = eINSTANCE.getLoadShape_QMult();

		/**
		 * The meta object literal for the '{@link electrickery.general.impl.SpectrumImpl <em>Spectrum</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see electrickery.general.impl.SpectrumImpl
		 * @see electrickery.general.impl.GeneralPackageImpl#getSpectrum()
		 * @generated
		 */
		EClass SPECTRUM = eINSTANCE.getSpectrum();

		/**
		 * The meta object literal for the '<em><b>NHarm</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SPECTRUM__NHARM = eINSTANCE.getSpectrum_NHarm();

		/**
		 * The meta object literal for the '<em><b>Harmonic</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SPECTRUM__HARMONIC = eINSTANCE.getSpectrum_Harmonic();

		/**
		 * The meta object literal for the '<em><b>Pct Mag</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SPECTRUM__PCT_MAG = eINSTANCE.getSpectrum_PctMag();

		/**
		 * The meta object literal for the '<em><b>Angle</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SPECTRUM__ANGLE = eINSTANCE.getSpectrum_Angle();

		/**
		 * The meta object literal for the '<em><b>Csv File</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SPECTRUM__CSV_FILE = eINSTANCE.getSpectrum_CsvFile();

		/**
		 * The meta object literal for the '{@link electrickery.general.impl.TimeCurrentCurveImpl <em>Time Current Curve</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see electrickery.general.impl.TimeCurrentCurveImpl
		 * @see electrickery.general.impl.GeneralPackageImpl#getTimeCurrentCurve()
		 * @generated
		 */
		EClass TIME_CURRENT_CURVE = eINSTANCE.getTimeCurrentCurve();

		/**
		 * The meta object literal for the '<em><b>NPts</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TIME_CURRENT_CURVE__NPTS = eINSTANCE.getTimeCurrentCurve_NPts();

		/**
		 * The meta object literal for the '<em><b>CArray</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TIME_CURRENT_CURVE__CARRAY = eINSTANCE.getTimeCurrentCurve_CArray();

		/**
		 * The meta object literal for the '<em><b>TArray</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TIME_CURRENT_CURVE__TARRAY = eINSTANCE.getTimeCurrentCurve_TArray();

		/**
		 * The meta object literal for the '{@link electrickery.general.impl.WireDataImpl <em>Wire Data</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see electrickery.general.impl.WireDataImpl
		 * @see electrickery.general.impl.GeneralPackageImpl#getWireData()
		 * @generated
		 */
		EClass WIRE_DATA = eINSTANCE.getWireData();

		/**
		 * The meta object literal for the '<em><b>RDC</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WIRE_DATA__RDC = eINSTANCE.getWireData_RDC();

		/**
		 * The meta object literal for the '<em><b>RAC</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WIRE_DATA__RAC = eINSTANCE.getWireData_RAC();

		/**
		 * The meta object literal for the '<em><b>RUnits</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WIRE_DATA__RUNITS = eINSTANCE.getWireData_RUnits();

		/**
		 * The meta object literal for the '<em><b>Gmr AC</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WIRE_DATA__GMR_AC = eINSTANCE.getWireData_GmrAC();

		/**
		 * The meta object literal for the '<em><b>Gmr Units</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WIRE_DATA__GMR_UNITS = eINSTANCE.getWireData_GmrUnits();

		/**
		 * The meta object literal for the '<em><b>Radius</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WIRE_DATA__RADIUS = eINSTANCE.getWireData_Radius();

		/**
		 * The meta object literal for the '<em><b>Rad Units</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WIRE_DATA__RAD_UNITS = eINSTANCE.getWireData_RadUnits();

		/**
		 * The meta object literal for the '<em><b>Norm Amps</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WIRE_DATA__NORM_AMPS = eINSTANCE.getWireData_NormAmps();

		/**
		 * The meta object literal for the '<em><b>Emerg Amps</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WIRE_DATA__EMERG_AMPS = eINSTANCE.getWireData_EmergAmps();

		/**
		 * The meta object literal for the '<em><b>Diameter</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WIRE_DATA__DIAMETER = eINSTANCE.getWireData_Diameter();

		/**
		 * The meta object literal for the '{@link electrickery.general.impl.TransformerCodeImpl <em>Transformer Code</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see electrickery.general.impl.TransformerCodeImpl
		 * @see electrickery.general.impl.GeneralPackageImpl#getTransformerCode()
		 * @generated
		 */
		EClass TRANSFORMER_CODE = eINSTANCE.getTransformerCode();

		/**
		 * The meta object literal for the '<em><b>Phases</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TRANSFORMER_CODE__PHASES = eINSTANCE.getTransformerCode_Phases();

		/**
		 * The meta object literal for the '<em><b>Windings</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TRANSFORMER_CODE__WINDINGS = eINSTANCE.getTransformerCode_Windings();

		/**
		 * The meta object literal for the '<em><b>Wdg</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TRANSFORMER_CODE__WDG = eINSTANCE.getTransformerCode_Wdg();

		/**
		 * The meta object literal for the '<em><b>Conn</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TRANSFORMER_CODE__CONN = eINSTANCE.getTransformerCode_Conn();

		/**
		 * The meta object literal for the '<em><b>KV</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TRANSFORMER_CODE__KV = eINSTANCE.getTransformerCode_KV();

		/**
		 * The meta object literal for the '<em><b>KVA</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TRANSFORMER_CODE__KVA = eINSTANCE.getTransformerCode_KVA();

		/**
		 * The meta object literal for the '<em><b>Tap</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TRANSFORMER_CODE__TAP = eINSTANCE.getTransformerCode_Tap();

		/**
		 * The meta object literal for the '<em><b>Pct R</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TRANSFORMER_CODE__PCT_R = eINSTANCE.getTransformerCode_PctR();

		/**
		 * The meta object literal for the '<em><b>RNeut</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TRANSFORMER_CODE__RNEUT = eINSTANCE.getTransformerCode_RNeut();

		/**
		 * The meta object literal for the '<em><b>XNeut</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TRANSFORMER_CODE__XNEUT = eINSTANCE.getTransformerCode_XNeut();

		/**
		 * The meta object literal for the '<em><b>Conns</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TRANSFORMER_CODE__CONNS = eINSTANCE.getTransformerCode_Conns();

		/**
		 * The meta object literal for the '<em><b>KVs</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TRANSFORMER_CODE__KVS = eINSTANCE.getTransformerCode_KVs();

		/**
		 * The meta object literal for the '<em><b>KV As</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TRANSFORMER_CODE__KV_AS = eINSTANCE.getTransformerCode_KVAs();

		/**
		 * The meta object literal for the '<em><b>Taps</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TRANSFORMER_CODE__TAPS = eINSTANCE.getTransformerCode_Taps();

		/**
		 * The meta object literal for the '<em><b>Xhl</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TRANSFORMER_CODE__XHL = eINSTANCE.getTransformerCode_Xhl();

		/**
		 * The meta object literal for the '<em><b>Xht</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TRANSFORMER_CODE__XHT = eINSTANCE.getTransformerCode_Xht();

		/**
		 * The meta object literal for the '<em><b>Xlt</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TRANSFORMER_CODE__XLT = eINSTANCE.getTransformerCode_Xlt();

		/**
		 * The meta object literal for the '<em><b>Xsc Array</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TRANSFORMER_CODE__XSC_ARRAY = eINSTANCE.getTransformerCode_XscArray();

		/**
		 * The meta object literal for the '<em><b>Thermal</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TRANSFORMER_CODE__THERMAL = eINSTANCE.getTransformerCode_Thermal();

		/**
		 * The meta object literal for the '<em><b>N</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TRANSFORMER_CODE__N = eINSTANCE.getTransformerCode_N();

		/**
		 * The meta object literal for the '<em><b>M</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TRANSFORMER_CODE__M = eINSTANCE.getTransformerCode_M();

		/**
		 * The meta object literal for the '<em><b>FL Rise</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TRANSFORMER_CODE__FL_RISE = eINSTANCE.getTransformerCode_FLRise();

		/**
		 * The meta object literal for the '<em><b>HS Rise</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TRANSFORMER_CODE__HS_RISE = eINSTANCE.getTransformerCode_HSRise();

		/**
		 * The meta object literal for the '<em><b>Pct Load Loss</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TRANSFORMER_CODE__PCT_LOAD_LOSS = eINSTANCE.getTransformerCode_PctLoadLoss();

		/**
		 * The meta object literal for the '<em><b>Pct No Load Loss</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TRANSFORMER_CODE__PCT_NO_LOAD_LOSS = eINSTANCE.getTransformerCode_PctNoLoadLoss();

		/**
		 * The meta object literal for the '<em><b>Norm HKVA</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TRANSFORMER_CODE__NORM_HKVA = eINSTANCE.getTransformerCode_NormHKVA();

		/**
		 * The meta object literal for the '<em><b>Emerg HKVA</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TRANSFORMER_CODE__EMERG_HKVA = eINSTANCE.getTransformerCode_EmergHKVA();

		/**
		 * The meta object literal for the '<em><b>Max Tap</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TRANSFORMER_CODE__MAX_TAP = eINSTANCE.getTransformerCode_MaxTap();

		/**
		 * The meta object literal for the '<em><b>Min Tap</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TRANSFORMER_CODE__MIN_TAP = eINSTANCE.getTransformerCode_MinTap();

		/**
		 * The meta object literal for the '<em><b>Num Taps</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TRANSFORMER_CODE__NUM_TAPS = eINSTANCE.getTransformerCode_NumTaps();

		/**
		 * The meta object literal for the '<em><b>Pct IMag</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TRANSFORMER_CODE__PCT_IMAG = eINSTANCE.getTransformerCode_PctIMag();

		/**
		 * The meta object literal for the '<em><b>Ppm Anti Float</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TRANSFORMER_CODE__PPM_ANTI_FLOAT = eINSTANCE.getTransformerCode_PpmAntiFloat();

		/**
		 * The meta object literal for the '<em><b>Pct RS</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TRANSFORMER_CODE__PCT_RS = eINSTANCE.getTransformerCode_PctRS();

		/**
		 * The meta object literal for the '<em><b>Like</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference TRANSFORMER_CODE__LIKE = eINSTANCE.getTransformerCode_Like();

	}

} //GeneralPackage
