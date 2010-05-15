/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package dss.common;

import dss.general.Spectrum;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Feeder</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * User cannot instantiate this object.  Feeders are created on the fly
 * when a radial system is specified.  Feeders are created from Energymeters
 * and are given the same name.
 * 
 * Feeders get created from energy meters if Radial is set to yes and meter
 * zones are already computed.  If Radial=Yes and the meterzones are reset,
 * then the feeders are redefined.  If Radial is subsequently set to NO or a
 * solution mode is used that doesn't utilize feeders, the get currents
 * routines will not do anything.
 * 
 * Feeders cannot be re-enabled unless the energymeter object allows them to
 * be.
 * 
 * Feeders are not saved.  This is implicit with the Energymeter saving.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link dss.common.Feeder#getSpectrum <em>Spectrum</em>}</li>
 *   <li>{@link dss.common.Feeder#getBaseFreq <em>Base Freq</em>}</li>
 *   <li>{@link dss.common.Feeder#isEnabled <em>Enabled</em>}</li>
 * </ul>
 * </p>
 *
 * @see dss.common.CommonPackage#getFeeder()
 * @model
 * @generated
 */
public interface Feeder extends EObject {
	/**
	 * Returns the value of the '<em><b>Spectrum</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Harmonic spectrum for this device.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Spectrum</em>' reference.
	 * @see #setSpectrum(Spectrum)
	 * @see dss.common.CommonPackage#getFeeder_Spectrum()
	 * @model
	 * @generated
	 */
	Spectrum getSpectrum();

	/**
	 * Sets the value of the '{@link dss.common.Feeder#getSpectrum <em>Spectrum</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Spectrum</em>' reference.
	 * @see #getSpectrum()
	 * @generated
	 */
	void setSpectrum(Spectrum value);

	/**
	 * Returns the value of the '<em><b>Base Freq</b></em>' attribute.
	 * The default value is <code>"60.0"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Base Frequency for ratings.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Base Freq</em>' attribute.
	 * @see #setBaseFreq(double)
	 * @see dss.common.CommonPackage#getFeeder_BaseFreq()
	 * @model default="60.0"
	 * @generated
	 */
	double getBaseFreq();

	/**
	 * Sets the value of the '{@link dss.common.Feeder#getBaseFreq <em>Base Freq</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Base Freq</em>' attribute.
	 * @see #getBaseFreq()
	 * @generated
	 */
	void setBaseFreq(double value);

	/**
	 * Returns the value of the '<em><b>Enabled</b></em>' attribute.
	 * The default value is <code>"true"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Indicates whether this element is enabled.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Enabled</em>' attribute.
	 * @see #setEnabled(boolean)
	 * @see dss.common.CommonPackage#getFeeder_Enabled()
	 * @model default="true"
	 * @generated
	 */
	boolean isEnabled();

	/**
	 * Sets the value of the '{@link dss.common.Feeder#isEnabled <em>Enabled</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Enabled</em>' attribute.
	 * @see #isEnabled()
	 * @generated
	 */
	void setEnabled(boolean value);

} // Feeder
