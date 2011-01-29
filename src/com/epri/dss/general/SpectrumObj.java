package com.epri.dss.general;

/**
 * Harmonic Spectrum specified as Harmonic, pct magnitude and angle.
 *
 * Spectrum is shifted by the fundamental angle and stored in MultArray
 * so that the fundamental is at zero degrees phase shift.
 */
public interface SpectrumObj extends DSSObject {

	int getNumHarm();

	void setNumHarm(int numHarm);

	double[] getHarmArray();

	void setHarmArray(double[] harmArray);

	double[] getMult(double h);

}
