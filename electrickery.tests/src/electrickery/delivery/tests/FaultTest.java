/**
 * Copyright (C) 2010 Richard Lincoln
 */
package electrickery.delivery.tests;

import electrickery.delivery.DeliveryFactory;
import electrickery.delivery.Fault;

import junit.textui.TestRunner;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Fault</b></em>'.
 * <!-- end-user-doc -->
 * @generated
 */
public class FaultTest extends PowerDeliveryElementTest {

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static void main(String[] args) {
		TestRunner.run(FaultTest.class);
	}

	/**
	 * Constructs a new Fault test case with the given name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FaultTest(String name) {
		super(name);
	}

	/**
	 * Returns the fixture for this Fault test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected Fault getFixture() {
		return (Fault)fixture;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see junit.framework.TestCase#setUp()
	 * @generated
	 */
	@Override
	protected void setUp() throws Exception {
		setFixture(DeliveryFactory.eINSTANCE.createFault());
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

} //FaultTest
