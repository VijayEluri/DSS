/**
 * Copyright (C) 2008-2012 Electric Power Research Institute, Inc.
 * Copyright (C) 2009-2012 Richard Lincoln
 * All rights reserved.
 */
package com.ncond.dss.shared;

import com.ncond.dss.shared.Complex;


public abstract class MathUtil {

	/* Symmetrical component conversion matrices */
	private static final CMatrix As2p = getAs2p(3);
	private static final CMatrix Ap2s = getAp2s(3);
//	private static final CMatrix ClarkeF = getClarkeF(3);
//	private static final CMatrix ClarkeR = getClarkeR(3);

//	private static final double SIN2PI3 = Math.sin(2.0 * Math.PI / 3.0);

	private static CMatrix getAMatrix(int order) {
		CMatrix Amat = new CMatrix(order);

		Complex a  = new Complex(-0.5, 0.866025403);
		Complex aa = new Complex(-0.5,-0.866025403);

		for (int i = 0; i < 3; i++)
			Amat.setSym(0, i, Complex.ONE);
		Amat.set(1, 1, aa);
		Amat.set(2, 2, aa);
		Amat.setSym(1, 2, a);

		return Amat;
	}

	private static CMatrix getAs2p(int order) {
		return getAMatrix(order);
	}

	private static CMatrix getAp2s(int order) {
		CMatrix Ap2s = getAMatrix(order);
		Ap2s.invert();
		return Ap2s;
	}

//	/**
//	 * Forward Clarke.
//	 */
//	private static CMatrix getClarkeF(int order) {
//
//		CMatrix ClarkeF = new CMatrixImpl(order);
//		ClarkeF.setElement(0, 0, new Complex(1.0, 0.0) );
//		ClarkeF.setElement(0, 1, new Complex(-0.5,0.0) );
//		ClarkeF.setElement(0, 2, new Complex(-0.5,0.0) );
//
//		ClarkeF.setElement(1, 1, new Complex(SIN2PI3, 0.0) );
//		ClarkeF.setElement(1, 2, new Complex(-SIN2PI3,0.0) );
//
//		ClarkeF.setElement(2, 0, new Complex(0.5, 0.0) );
//		ClarkeF.setElement(2, 1, new Complex(0.5, 0.0) );
//		ClarkeF.setElement(2, 2, new Complex(0.5, 0.0) );
//
//		ClarkeF.multByConst(2.0 / 3.0);  // multiply all elements by a const 2/3
//
//		return ClarkeF;
//	}

//	/**
//	 * Reverse Clarke.
//	 */
//	private static CMatrix getClarkeR(int order) {
//
//		CMatrix ClarkeR = new CMatrixImpl(order);
//		ClarkeR.setElement(0, 0, new Complex(1.0, 0.0) );
//		ClarkeR.setElement(1, 0, new Complex(-0.5,0.0) );
//		ClarkeR.setElement(2, 0, new Complex(-0.5,0.0) );
//
//		ClarkeR.setElement(1, 1, new Complex(SIN2PI3, 0.0) );
//		ClarkeR.setElement(2, 1, new Complex(-SIN2PI3,0.0) );
//
//		ClarkeR.setElement(0, 2, new Complex(1.0, 0.0) );
//		ClarkeR.setElement(1, 2, new Complex(1.0, 0.0) );
//		ClarkeR.setElement(2, 3, new Complex(1.0, 0.0) );
//
//		return null;
//	}

	private static int L;

	private static int index(int i, int j) {
		return j * L + i;
	}

	/**
	 * A = reference to matrix of doubles
	 * norder = order of matrix  (assumed square)
	 * error = 0 if no error;
	 *       = 1 if not enough heap to alloc temp array
	 *       = 2 if matrix can't be inverted
	 *
	 * This routine will invert a non-symmetric matrix.  Index is assumed to
	 * follow the Fortran standard, not the Pascal standard.  That is the data
	 * are ordered by first subscript first, then second subscript.  This routine
	 * computes its own indexing.
	 *
	 * It assumes that the matrix is dimensioned to exactly the number of elements
	 * needed.  Apologies to Fortran users who are accustomed to over dimensioning
	 * stuff.
	 *
	 */
	public static void ETKInvert(double[] A, int norder, int[] error) {
		int j, k, LL, M, i;
		int[] LT;
		double RMY, T1;

		L = norder;
		error[0] = 0;

		/* Allocate LT */
		LT = new int[L];
		if (LT.length == 0) {
			error[0] = 1;
			return;
		}

		/* Zero LT */
		for (j = 0; j < L; j++) LT[j] = 0;

		T1 = 0.0;

		/* M Loop */
		// initialize a safe value of k
		k = 1;
		for (M = 0; M < L; M++) {
			for (LL = 0; LL < L; LL++) {
				if (LT[LL] != 1) {
					RMY = Math.abs(A[index(LL, LL)]) - Math.abs(T1);
					if (RMY > 0.0) {
						T1 = A[index(LL, LL)];
						k = LL;
					}
				}
			}

			/* Error Check. If RMY ends up zero, matrix is non-inversible */
			RMY = Math.abs(T1);
			if (RMY == 0.0) {
				error[0] = 2;
				return;
			}

			T1 = 0.0;
			LT[k] = 1;
			for (i = 0; i < L; i++) {
				if (i != k) {
					for (j = 0; j < L; j++) {
						if (j != k) A[index(i, j)] = A[index(i, j)] - A[index(i, k)] * A[index(k, j)] / A[index(k, k)];
					}
				}
			}

			A[index(k, k)] = -1.0 / A[index(k, k)];

			for (i = 0; i < L; i++) {
				if (i != k) {
					A[index(i, k)] = A[index(i, k)] * A[index(k, k)];
					A[index(k, i)] = A[index(k, i)] * A[index(k, k)];
				}
			}
		}

		for (j = 0; j < L; j++)
			for (k = 0; k < L; k++)
				A[index(j, k)] = -A[index(j, k)];

		LT = null;
	}

	public static void phase2SymComp(Complex[] Vph, Complex[] V012) {
		Ap2s.vMult(V012, Vph);
	}

	public static void phase2SymComp(Complex Vph, Complex[] V012) {
		phase2SymComp(new Complex[] {Vph}, V012);
	}

	public static void symComp2Phase(Complex[] Vph, Complex[] V012) {
		As2p.vMult(Vph, V012);
	}

	/**
	 * Computes total complex power given terminal voltages and currents.
	 */
	public static Complex terminalPowerIn(Complex[] V, Complex[] I, int nPhases) {
		Complex total = Complex.ZERO;
		for (int j = 0; j < nPhases; j++)
			total = total.add(V[j].mult(I[j].conj()));
		return total;
	}

	/**
	 * Compute complex power in kW and kvar in each phase.
	 */
	public static void calcKPowers(Complex[] kWkVAr, Complex[] V, Complex[] I, int n) {
		for (int j = 0; j < n; j++)
			kWkVAr[j] = V[j].mult(I[j].conj()).mult(0.001);
	}

	public static void calcKPowers(Complex[] kWkVAr, Complex[] V, Complex I, int n) {
		calcKPowers(kWkVAr, V, new Complex[] {I}, n);
	}

	/**
	 * Returns a normally distributed random variable.
	 */
	public static double gauss(double mean, double stdDev) {
		double A = 0.0;

		for (int i = 0; i < 12; i++) A += Math.random();

		return (A - 6.0) * stdDev + mean;
	}

	/**
	 * Generates a quasi-lognormal distribution with approx 50% of values
	 * from 0 to Mean and the remainder from Mean to infinity.
	 */
	public static double quasiLognormal(double mean) {
		return Math.exp(gauss(0.0, 1.0)) * mean;
	}

	public static double sum(double[] data, int count) {
		double sum = 0;
		for (int i = 0; i < count; i++) sum += data[i];
		return sum;
	}

	public static void meanAndStdDev(double[] pData, int nData, double[] mean, double[] stdDev) {
		double[] data = new double[100];
		double s;

		data = pData;  // make a double pointer
		if (nData == 1) {
			mean[0] = data[0];
			stdDev[0] = data[0];
			return;
		}
		mean[0] = sum(data, (nData)) / nData;
		s = 0;  // sum differences from the mean, for greater accuracy
		for (int i = 0; i < nData; i++)
			s = s + Math.pow(mean[0] - data[i], 2);
		stdDev[0] = Math.sqrt(s / (nData - 1));
	}

	public static void curveMeanAndStdDev(double[] pY, double[] pX, int N, double[] mean, double[] stdDev) {
		double s, dy1, dy2;
		int i;

		if (N == 1) {
			mean[0] = pY[0];
			stdDev[0] = pY[0];
			return;
		}
		s = 0;
		for (i = 0; i < N - 1; i++) {
			s += 0.5 * (pY[i] + pY[i + 1]) * (pX[i + 1] - pX[i]);
		}
		mean[0] = s / (pX[N] - pX[0]);

		s = 0;  // sum differences from the mean, for greater accuracy
		for (i = 0; i < N - 1; i++) {
			dy1 = (pY[i] - mean[0]);
			dy2 = (pY[i + 1] - mean[0]);
			s += 0.5 * (dy1 * dy1 + dy2 * dy2) * (pX[i + 1] - pX[i]);
		}

		stdDev[0] = Math.sqrt(s / (pX[N] - pX[0]));
	}

	/**
	 * Parallel two complex impedances.
	 */
	public static Complex parallelZ(Complex Z1, Complex Z2) {
		Complex denom = Z1.add(Z2) ;
		if ((Math.abs(denom.real()) > 0.0) || (Math.abs(denom.imag()) > 0.0)) {
			return Z1.mult(Z2).div(denom);
		} else {  /* Error */
			return Complex.ZERO;
		}
	}

	/**
	 * z = I0(a)
	 */
	public static Complex besselI0(Complex a) {
		int maxTerm = 1000;
		double epsilonSqr = 1.0E-20;

		int i;
		double sizeSqr = 1;
		Complex term;
		Complex zSQR25;

		Complex result = Complex.ONE;  // term 0
		zSQR25 = a.mult(a).mult(0.25);
		term = zSQR25;
		result = result.add(zSQR25);  // term 1

		i = 0;
		while (i <= maxTerm && sizeSqr > epsilonSqr) {
			term = zSQR25.mult(term);
			i += 1;
			term = term.div(Math.pow(i, 2));
			result = result.add(term);  // sum = sum + term
			sizeSqr = Math.pow(term.real(), 2) + Math.pow(term.imag(), 2);
		}

		return result;
	}

	/**
	 *
	 * @return Nema unbalance
	 */
	public static double pctNemaUnbalance(Complex[] Vph) {
		int i;
		double Vavg;
		double maxDiff;
		double[] Vmag = new double[3];

		for (i = 0; i < 3; i++) Vmag[i] = Vph[i].abs();

		Vavg = 0.0;
		for (i = 0; i < 3; i++) Vavg += Vmag[i];
		Vavg = Vavg / 3.0;

		maxDiff = 0.0;
		for (i = 0; i < 3; i++) {
			maxDiff = Math.max(maxDiff, Math.abs(Vmag[i] - Vavg));
		}

		if (Vavg != 0.0) {
			return maxDiff / Vavg * 100.0;  // pct difference
		} else {
			return 0;
		}
	}

	public static double getXR(Complex a) {
		double xr;
		if (a.real() != 0.0) {
			xr = a.imag() / a.real();
			if (Math.abs(xr) > 9999.0) xr = 9999.0;
		} else{
			xr = 9999.0;;
		}
		return xr;
	}

	public static double sqr(double a) {
		return Math.pow(a, 2);
	}

}
