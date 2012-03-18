/**
 * Copyright (C) 2008-2012 Electric Power Research Institute, Inc.
 * Copyright (C) 2009-2012 Richard Lincoln
 * All rights reserved.
 */
package com.ncond.dss.shared;

import org.apache.commons.math.complex.Complex;


public class CMatrix {

	private int nOrder;
	private Complex[] values;

	protected int invertError;

	public CMatrix(int n) {
		nOrder = n;
		invertError = 0;
		values = new Complex[n * n];
		for (int i = 0; i < n * n; i++)
			values[i] = Complex.ZERO;
	}

	public int order() {
		return nOrder;
	}

	public void set(int i, int j, Complex value) {
		values[(j * nOrder + i)] = value;
	}

	public void setSym(int i, int j, Complex value) {
		values[j * nOrder + i] = value;
		if (i != j) values[i * nOrder + j] = value;  // ensure symmetry
	}

	public void add(int i, int j, Complex value) {
		values[j * nOrder + i] = values[j * nOrder + i].add(value);
	}

	public void addSym(int i, int j, Complex value) {
		values[j * nOrder + i] = values[j * nOrder + i].add(value);
		if (i != j) values[i * nOrder + j] = values[i * nOrder + j].add(value);  // ensure symmetry
	}

	public Complex get(int i, int j) {
		return values[j * nOrder + i];
	}

	public int getErrorCode() {
		return invertError;
	}

	/**
	 * Zero out matrix
	 */
	public void clear() {
		for (int i = 0; i < nOrder * nOrder; i++)
			values[i] = Complex.ZERO;
	}

	/**
	 * b = Ax
	 */
	public void vMult(Complex[] b, Complex[] x) {
		Complex sum;
		for (int i = 0; i < nOrder; i++) {
			sum = Complex.ZERO;
			for (int j = 0; j < nOrder; j++)
				sum = sum.add( values[j * nOrder + i].multiply( x[j] ) );
			b[i] = sum;
		}
	}

	/**
	 * b = Ax
	 *
	 * Same as MVMult except accumulates b.
	 */
	public void vMultAccum(Complex[] b, Complex[] x) {
		Complex sum;
		for (int i = 0; i < nOrder; i++) {
			sum = Complex.ZERO;
			for (int j = 0; j < nOrder; j++)
				sum = sum.add(values[j * nOrder + i].multiply(x[j]));
			b[i] = b[i].add(sum);
		}
	}

	public void addFrom(CMatrix otherMatrix) {
		if (nOrder == otherMatrix.order()) {
			for (int i = 0; i < nOrder; i++) {
				for (int j = 0; j < nOrder; j++) {
					add(i, j, otherMatrix.get(i, j));
				}
			}
		}
	}

	public void copyFrom(CMatrix otherMatrix) {
		if (nOrder == otherMatrix.order()) {
			for (int i = 0; i < nOrder; i++) {
				for (int j = 0; j < nOrder; j++) {
					set(i, j, otherMatrix.get(i, j));
				}
			}
		}
	}

	/**
	 * Sum all elements in a given block of the matrix.
	 */
	public Complex sumBlock(int row1, int row2, int col1, int col2) {
		int rowStart;
		Complex sum = Complex.ZERO;

		for (int j = col1; j <= col2; j++) {
			rowStart = j * nOrder;
			for (int i = rowStart + row1; i <= rowStart + row2; i++) {
				sum = sum.add(values[i]);
			}
		}

		return sum;
	}

	public Complex[] asArray(int[] order) {
		order[0] = nOrder;
		return values;
	}

	public Complex[] asArray() {
		return values;
	}

	public void zeroRow(int iRow) {
		int j = iRow;
		for (int i = 0; i < nOrder; i++) {
			values[j] = Complex.ZERO;
			j += nOrder;
		}
	}

	public void zeroCol(int iCol) {
		for (int i = iCol * nOrder; i < (iCol + 1) * nOrder; i++) {
			values[i] = Complex.ZERO;
		}
	}

	/**
	 * Average of diagonal elements
	 */
	public Complex avgDiag() {
		Complex avg = Complex.ZERO;
		for (int i = 0; i < nOrder; i++)
			avg = avg.add(values[i * nOrder + i]);

		if (nOrder > 0) {
			avg = ComplexUtil.divide(avg, nOrder);
		}

		return avg;
	}

	/**
	 * Average the upper triangle off diagonals.
	 */
	public Complex avgOffDiag() {
		Complex avg = Complex.ZERO;
		int nTimes = 0;
		for (int i = 0; i < nOrder; i++) {
			for (int j = i+1; j < nOrder; j++) {
				nTimes += 1;
				avg = avg.add(values[j * nOrder + i]);
			}
		}

		if (nTimes > 0) {
			avg = ComplexUtil.divide(avg, nTimes);
		}

		return avg;
	}

	/**
	 * Multiply all elements by a constant
	 */
	public void mult(double x) {
		for (int i = 0; i < nOrder * nOrder; i++)
			values[i] = values[i].multiply(x);
	}

	/**
	 * Inverts the matrix
	 */
	public void invert() {
		int j, K, L, LL, m, i;
		int[] LT;
		double RMY;
		Complex T1;
		Complex[] A;

		L = nOrder;
		invertError = 0;

		A = values;  /* Assign pointer to something we can use */

		/* Allocate LT */
		try {
			LT = new int[L];
		} catch (OutOfMemoryError e) {
			invertError = 1;
			return;
		}

		/* Zero LT */
		for (j = 0; j < L; j++) LT[j] = 0;

		T1 = Complex.ZERO;
		K = 0;

		/* M Loop */
		for (m = 0; m < L; m++) {
			for (LL = 0; LL < L; LL++) {
				if (LT[LL] != 1) {
					RMY = A[ index(LL, LL) ].abs() - T1.abs();  // Will this work??
					if (RMY > 0) {
						T1 = A[ index(LL, LL) ];
						K = LL;
					}
				}
			}

			/* Error check. If RMY ends up zero, matrix is non-inversible */
			RMY = T1.abs();
			if (RMY == 0.0) {
				invertError = 2;
				return;
			}

			T1 = Complex.ZERO;
			LT[K] = 1;
			for (i = 0; i < L; i++) {
				if (i != K) {
					for (j = 0; j < L; j++) {
						if (j != K) {
							A[index(i, j)] = A[index(i, j)].subtract(A[index(i, K)].multiply(A[index(K, j)]).divide(A[index(K, K)]));
						}
					}
				}
			}

			// Invert and negate k, k element
			A[index(K, K)] = Complex.ONE.divide(A[index(K, K)]).negate();

			for (i = 0; i < L; i++) {
				if (i != K) {
					A[index(i, K)] = A[index(i, K)].multiply(A[index(K, K)]);
					A[index(K, i)] = A[index(K, i)].multiply(A[index(K, K)]);
				}
			}
		}  // m loop

		for (j = 0; j < L; j++)
			for (K = 0; K < L; K++)
				A[ index(j, K) ] = A[ index(j, K) ].negate();

		LT = null;
	}

	private int index(int i, int j) {
		return j * nOrder + i;
	}

	/**
	 * Perform Kron reduction on last row/col and return new matrix
	 */
	public CMatrix kron(int eliminationRow) {
		int ii, jj;
		CMatrix mtx = null;   // null result on failure
		if ((nOrder > 1) && (eliminationRow < nOrder) && (eliminationRow >= 0)) {
			mtx = new CMatrix(nOrder - 1);
			int irow = eliminationRow;
			Complex x = get(irow, irow);

			ii = 0;
			for (int i = 0; i < nOrder; i++) {
				if (i != irow) {  // skip elimination row
					jj = 0;
					for (int j = 0; j < nOrder; j++) {
						if (j != irow) {
							mtx.set(ii, jj, get(i, j).subtract(get(i, irow).multiply(get(irow, j)).divide(x)));
							jj += 1;
						}
					}
					ii += 1;
				}
			}
		}
		return mtx;
	}

}
