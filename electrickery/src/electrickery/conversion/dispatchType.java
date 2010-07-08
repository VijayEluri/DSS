/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package electrickery.conversion;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>dispatch Type</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see electrickery.conversion.ConversionPackage#getdispatchType()
 * @model
 * @generated
 */
public enum dispatchType implements Enumerator {
	/**
	 * The '<em><b>Load Mode</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #LOAD_MODE_VALUE
	 * @generated
	 * @ordered
	 */
	LOAD_MODE(0, "LoadMode", "LoadMode"), /**
	 * The '<em><b>Price Mode</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #PRICE_MODE_VALUE
	 * @generated
	 * @ordered
	 */
	PRICE_MODE(1, "PriceMode", "PriceMode");

	/**
	 * The '<em><b>Load Mode</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Load Mode</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #LOAD_MODE
	 * @model name="LoadMode"
	 * @generated
	 * @ordered
	 */
	public static final int LOAD_MODE_VALUE = 0;

	/**
	 * The '<em><b>Price Mode</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Price Mode</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #PRICE_MODE
	 * @model name="PriceMode"
	 * @generated
	 * @ordered
	 */
	public static final int PRICE_MODE_VALUE = 1;

	/**
	 * An array of all the '<em><b>dispatch Type</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final dispatchType[] VALUES_ARRAY =
		new dispatchType[] {
			LOAD_MODE,
			PRICE_MODE,
		};

	/**
	 * A public read-only list of all the '<em><b>dispatch Type</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List<dispatchType> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>dispatch Type</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static dispatchType get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			dispatchType result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>dispatch Type</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static dispatchType getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			dispatchType result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>dispatch Type</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static dispatchType get(int value) {
		switch (value) {
			case LOAD_MODE_VALUE: return LOAD_MODE;
			case PRICE_MODE_VALUE: return PRICE_MODE;
		}
		return null;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private final int value;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private final String name;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private final String literal;

	/**
	 * Only this class can construct instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private dispatchType(int value, String name, String literal) {
		this.value = value;
		this.name = name;
		this.literal = literal;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getValue() {
	  return value;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getName() {
	  return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getLiteral() {
	  return literal;
	}

	/**
	 * Returns the literal value of the enumerator, which is its string representation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		return literal;
	}
	
} //dispatchType
