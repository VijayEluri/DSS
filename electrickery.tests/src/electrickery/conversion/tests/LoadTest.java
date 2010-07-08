/**
 * Copyright (C) 2010 Richard Lincoln
 */
package electrickery.conversion.tests;

import electrickery.conversion.ConversionFactory;
import electrickery.conversion.Load;

import junit.textui.TestRunner;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Load</b></em>'.
 * <!-- end-user-doc -->
 * @generated
 */
public class LoadTest extends PowerConversionElementTest {

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static void main(String[] args) {
		TestRunner.run(LoadTest.class);
	}

	/**
	 * Constructs a new Load test case with the given name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LoadTest(String name) {
		super(name);
	}

	/**
	 * Returns the fixture for this Load test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected Load getFixture() {
		return (Load)fixture;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see junit.framework.TestCase#setUp()
	 * @generated
	 */
	@Override
	protected void setUp() throws Exception {
		setFixture(ConversionFactory.eINSTANCE.createLoad());
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

} //LoadTest