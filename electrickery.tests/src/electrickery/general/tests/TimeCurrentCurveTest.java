/**
 * Copyright (C) 2010 Richard Lincoln
 */
package electrickery.general.tests;

import electrickery.general.GeneralFactory;
import electrickery.general.TimeCurrentCurve;

import junit.framework.TestCase;

import junit.textui.TestRunner;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Time Current Curve</b></em>'.
 * <!-- end-user-doc -->
 * @generated
 */
public class TimeCurrentCurveTest extends TestCase {

	/**
	 * The fixture for this Time Current Curve test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected TimeCurrentCurve fixture = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static void main(String[] args) {
		TestRunner.run(TimeCurrentCurveTest.class);
	}

	/**
	 * Constructs a new Time Current Curve test case with the given name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TimeCurrentCurveTest(String name) {
		super(name);
	}

	/**
	 * Sets the fixture for this Time Current Curve test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void setFixture(TimeCurrentCurve fixture) {
		this.fixture = fixture;
	}

	/**
	 * Returns the fixture for this Time Current Curve test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected TimeCurrentCurve getFixture() {
		return fixture;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see junit.framework.TestCase#setUp()
	 * @generated
	 */
	@Override
	protected void setUp() throws Exception {
		setFixture(GeneralFactory.eINSTANCE.createTimeCurrentCurve());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see junit.framework.TestCase#tearDown()
	 * @generated
	 */
	@Override
	protected void tearDown() throws Exception {
		setFixture(null);
	}

} //TimeCurrentCurveTest