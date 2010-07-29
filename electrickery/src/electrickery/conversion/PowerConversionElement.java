/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package electrickery.conversion;

import cern.colt.matrix.tdcomplex.DComplexMatrix1D;
import electrickery.common.CircuitElement;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Power Conversion Element</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Power conversion elements convert power from electrical form to some
 * other form, or vice-versa.  Some may temporarily store energy and then give
 * it back, as is the case for reactive elements.  Most will have only one
 * connection to the power system and, therefore, only one multiphase
 * terminal.  The description of the mechanical or thermal side of the power
 * conversion is contained within the "Black box" model.  The description may
 * be a simple impedance or a complicated set of differential equations
 * yielding a current injection equation of the form:
 * 
 *         ITerm(t)  = F(VTerm, [State], t)
 * 
 * The function F will vary according to the type of simulation being
 * performed.  The power conversion element must also be capable of reporting
 * the partials matrix when necessary: dF/dV
 * 
 * In simple cases, this will simply be the primitive y (admittance) matrix;
 * that is, the y matrix for this element alone.
 * 
 * This concept may easily be extended to multi-terminal devices, which would
 * allow the representation of complex series elements such as fault current
 * limiters.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link electrickery.conversion.PowerConversionElement#getInjCurrent <em>Inj Current</em>}</li>
 * </ul>
 * </p>
 *
 * @see electrickery.conversion.ConversionPackage#getPowerConversionElement()
 * @model abstract="true"
 * @generated
 */
public interface PowerConversionElement extends CircuitElement {
    /**
	 * Returns the value of the '<em><b>Inj Current</b></em>' attribute.
	 * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Inj Current</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
	 * @return the value of the '<em>Inj Current</em>' attribute.
	 * @see #setInjCurrent(double)
	 * @see electrickery.conversion.ConversionPackage#getPowerConversionElement_InjCurrent()
	 * @model
	 * @generated
	 */
    double getInjCurrent();

    /**
	 * Sets the value of the '{@link electrickery.conversion.PowerConversionElement#getInjCurrent <em>Inj Current</em>}' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Inj Current</em>' attribute.
	 * @see #getInjCurrent()
	 * @generated
	 */
    void setInjCurrent(double value);

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @model
	 * @generated
	 */
    void recalcElementData();

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @model
	 * @generated
	 */
    int injCurrents();

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @model currType="electrickery.DComplexMatrix1D"
	 * @generated
	 */
    void getCurrents(DComplexMatrix1D curr);

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @model currType="electrickery.DComplexMatrix1D"
	 * @generated
	 */
    void getInjCurrents(DComplexMatrix1D curr);

} // PowerConversionElement
