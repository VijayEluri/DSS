/**
 * Copyright (C) 2010 Richard Lincoln
 */
package electrickery.tests;

import electrickery.common.tests.CommonTests;

import electrickery.conversion.tests.ConversionTests;

import electrickery.executive.tests.ExecutiveTests;

import junit.framework.Test;
import junit.framework.TestSuite;

import junit.textui.TestRunner;

/**
 * <!-- begin-user-doc -->
 * A test suite for the '<em><b>Electrickery</b></em>' model.
 * <!-- end-user-doc -->
 * @generated
 */
public class ElectrickeryAllTests extends TestSuite {

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static Test suite() {
		TestSuite suite = new ElectrickeryAllTests("Electrickery Tests");
		suite.addTest(CommonTests.suite());
		suite.addTest(ConversionTests.suite());
		suite.addTest(ExecutiveTests.suite());
		return suite;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ElectrickeryAllTests(String name) {
		super(name);
	}

} //ElectrickeryAllTests
