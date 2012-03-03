/* ------------------------------------------------------------------------- */
/* Copyright (C) 2008, EnerNex Corporation. All rights reserved.             */
/* Copyright (C) 2012, Richard Lincoln. All rights reserved.                 */
/* Licensed under the GNU Lesser General Public License (LGPL) v 2.1         */
/* ------------------------------------------------------------------------- */

package net.sourceforge.klusolve;

import edu.emory.mathcs.csparsej.tdcomplex.DZcs_common.DZcs;
import edu.emory.mathcs.csparsej.tdcomplex.DZcs_common.DZcsa;
import edu.emory.mathcs.csparsej.tdcomplex.DZcs_common.DZcsn;
import edu.emory.mathcs.csparsej.tdcomplex.DZcs_common.DZcss;

import static edu.emory.mathcs.csparsej.tdcomplex.DZcs_usolve.cs_usolve;
import static edu.emory.mathcs.csparsej.tdcomplex.DZcs_util.cs_spalloc;
import static edu.emory.mathcs.csparsej.tdcomplex.DZcs_complex.cs_czero;
import static edu.emory.mathcs.csparsej.tdcomplex.DZcs_entry.cs_entry;
import static edu.emory.mathcs.csparsej.tdcomplex.DZcs_ipvec.cs_ipvec;
import static edu.emory.mathcs.csparsej.tdcomplex.DZcs_lsolve.cs_lsolve;
import static edu.emory.mathcs.csparsej.tdcomplex.DZcs_lu.cs_lu;
import static edu.emory.mathcs.csparsej.tdcomplex.DZcs_compress.cs_compress;
import static edu.emory.mathcs.csparsej.tdcomplex.DZcs_dupl.cs_dupl;
import static edu.emory.mathcs.csparsej.tdcomplex.DZcs_dropzeros.cs_dropzeros;
import static edu.emory.mathcs.csparsej.tdcomplex.DZcs_sqr.cs_sqr;
import static edu.emory.mathcs.csparsej.tdcomplex.DZcs_transpose.cs_transpose;
//import static edu.emory.mathcs.csparsej.tdcomplex.DZcs_print.cs_print;

//import static edu.ufl.cise.klu.tdouble.Dklu_defaults.klu_defaults;
//import static edu.ufl.cise.klu.tdouble.Dklu_analyze.klu_analyze;
//import static edu.ufl.cise.klu.tdouble.Dklu_factor.klu_factor;
//import static edu.ufl.cise.klu.tdouble.Dklu_solve.klu_solve;
//import static edu.ufl.cise.klu.tdouble.Dklu_diagnostics.klu_condest;
//import static edu.ufl.cise.klu.tdouble.Dklu_diagnostics.klu_flops;
//import static edu.ufl.cise.klu.tdouble.Dklu_diagnostics.klu_rcond;
//import static edu.ufl.cise.klu.tdouble.Dklu_diagnostics.klu_rgrowth;
//
//import static edu.ufl.cise.klu.tdouble.Dklu_version.KLU_OK;
//import static edu.ufl.cise.klu.tdouble.Dklu_version.KLU_SINGULAR;
//
//import edu.ufl.cise.klu.common.KLU_common;
//import edu.ufl.cise.klu.common.KLU_numeric;
//import edu.ufl.cise.klu.common.KLU_symbolic;


public class KLUSystem {

//	private static KLU_common common;
//	private static boolean common_init = false;

	private DZcsa acx;

	// admittance matrix blocks in compressed-column storage
	private DZcs Y22;

	// admittance matrix blocks in triplet storage
	private DZcs T22;

//	KLU_symbolic symbolic;
//	KLU_numeric numeric;
	DZcss symbolic;
	DZcsn numeric;

	private int m_nBus;    // number of nodes
	private int m_nX;      // number of unknown voltages, hardwired to m_nBus
	private int m_NZpre;   // number of non-zero entries before factoring
	private int m_NZpost;  // number of non-zero entries after factoring
	private int m_fltBus;  // row number of a bus causing singularity

	public boolean bFactored;      // system has been factored

	public KLUSystem() {
		initDefaults();
	}

	public KLUSystem(int nBus, int nV, int nI) {
		initDefaults();
		initialize(nBus, nV, nI);
	}

	public void initDefaults() {
		m_nBus = 0;
		bFactored = false;
		acx = null;
		zero_indices();
		null_pointers();
//		if (!common_init) {
//			klu_defaults(common);
//			common.halt_if_singular = 0;
//			common_init = true;
//		}
	}

	public void clear() {
		if (Y22 != null) Y22 = null;

		if (T22 != null) T22 = null;

		if (acx != null) acx = null;

		if (numeric != null) numeric = null;
		if (symbolic != null) symbolic = null;

		zero_indices();
		null_pointers();
	}

	private void zero_indices() {
		m_nBus = m_nX = 0;
		m_NZpre = m_NZpost = 0;
		m_fltBus = 0;
	}

	private void null_pointers() {
		Y22 = null;
		T22 = null;
		numeric = null;
		symbolic = null;
		acx = null;
	}

	private DZcs process_triplet(DZcs T) {
		DZcs C, A = null;

		if (T == null) return null;

		if (T.nz > 0) {
			//cs_print(T, false);
			C = cs_compress(T);
			cs_dupl(C);
			cs_dropzeros(C);
			A = cs_transpose(C, true);
			C = null;
			m_NZpre += A.p[A.n];
			//cs_print(A, false);
		}

		T = null;
		return A;
	}

	private void compress_partitions() {
		Y22 = process_triplet(T22);
	}

	public int factorSystem() {
		bFactored = false;

		int rc = factor();

		if (rc == 1) {
			bFactored = true;
			return 0;
		}
		return 1;
	}

	public int solveSystem(DZcsa acxX, DZcsa acxB) {
		int rc = 0;
		int i;

		acx.set(0, cs_czero());

		for (i = 0; i < m_nBus; i++) {
			acx.set(i + 1, acxB.get(i));
		}

		solve(acx);

		for (i = 0; i < m_nBus; i++) {
			acxX.set(i, acx.get(i + 1));
		}

		return rc;
	}

	/**
	 * This resets and reinitializes the sparse matrix, nI = nBus
	 *
	 * @param nBus
	 * @param nV
	 * @param nI
	 * @return
	 */
	public int initialize(int nBus, int nV, int nI) {
		clear();

		m_nBus = m_nX = nBus;

		if (m_nX > 0) {
			T22 = cs_spalloc (m_nX, m_nX, 2 * m_nX, true, true);
		}
		if (acx != null) acx = null;
		acx = new DZcsa (m_nBus + 1);
		return 0;
	}

	public int initialize(int nBus) {
		return initialize(nBus, 0, 0);
	}

	public int getSize() {
		return m_nBus;
	}

	// metrics
	public int getSparseNNZ() {
		return m_NZpost;
	}

	public int getNNZ() {
		return m_NZpre;
	}

	public double getRCond() {
//		klu_rcond (symbolic, numeric, common);
//		return common.rcond;
		return 0.0;
	}

	public double getRGrowth() {
//		if (Y22 == null) return 0.0;
//		klu_rgrowth (Y22.p, Y22.i, Y22.x, symbolic, numeric, common);
//		return common.rgrowth;
		return 0.0;
	}

	public double getCondEst() {
//		if (Y22 == null) return 0.0;
//		if (Y22.n > 1) klu_condest (Y22.p, Y22.x, symbolic, numeric, common);
//		return common.condest;
		return 0.0;
	}

	public double getFlops() {
//		klu_flops(symbolic, numeric, common);
//		return common.flops;
		return 0.0;
	}

	public int getSingularCol() {
		return m_fltBus;
	}

	public void addMatrix(int[] aidBus, MatrixComplex pcxm, int bSum) {  // bSum is ignored
		int i, j;
		int idRow, idCol;
		int nDim = pcxm.nRow;
		double[] val;
		double re, im;

		// add the full primitive matrix, transposed, since csz_compress called later
		// bus id 0 is reference node, but KLU indices are zero-based
		for (i = 0; i < nDim; i++) {
			idRow = aidBus[i];
			if (idRow < 1) continue;
			--idRow;
		        for (j = 0; j < nDim; j++) {
		        	idCol = aidBus[j];
				if (idCol < 1) continue;
				--idCol;
				val = pcxm.get_acx(i, j);
				re = val[0];
				im = val[1];
				if (re == 0.0 && im == 0.0) {
					continue;
				}

				// stuff this value into the correct partition, transposed
				cs_entry (T22, idCol, idRow, val);
		        }
		}
	}

	/**
	 * returns 1 for success, -1 for a singular matrix
	 * returns 0 for another KLU error, most likely the matrix is too large for int32
	 *
	 * @return
	 */
	public int factor() {
		// first convert the triplets to column-compressed form, and prep the columns
		if (T22 != null) {
			compress_partitions();
		} else {  // otherwise, compression and factoring has already been done
			if (m_fltBus != 0) return -1;  // was found singular before
			return 1;  // was found okay before
		}

		int order = 2;  // ordering method, 0:natural, 1:Chol, 2:LU, 3:QR
		double tol = 1.0;//0.001;  // partial pivoting tolerance

		// then factor Y22
		if (numeric != null) numeric = null;
		if (symbolic != null) symbolic = null;

		if (Y22 != null) {
//			symbolic = klu_analyze (Y22.n, Y22.p, Y22.i, common);
//			numeric = klu_factor (Y22.p, Y22.i, Y22.x, symbolic, common);
			symbolic = cs_sqr(order, Y22, false);		/* ordering and symbolic analysis */
			numeric = cs_lu(Y22, symbolic, tol) ;		/* numeric LU factorization */
			if (symbolic == null || numeric == null) {
				return -1;
			}

//			m_fltBus = common.singular_col;
//			if (common.singular_col < Y22.n) {
//				++m_fltBus; // FIXME for 1-based NexHarm row numbers
//				m_fltBus += 0; // skip over the voltage source buses
//			} else {
//				m_fltBus = 0;  // no singular submatrix was found
//			}
//			if (common.status == KLU_OK) {
//				// compute size of the factorization
//				m_NZpost += (numeric.lnz + numeric.unz - numeric.n +
//					((numeric.Offp != null) ? (numeric.Offp[numeric.n]) : 0));
//				return 1;
//			} else if (common.status == KLU_SINGULAR) {
//				return -1;
//			} else { // KLU_OUT_OF_MEMORY, KLU_INVALID, or KLU_TOO_LARGE
//				if (m_fltBus == 0) {
//					m_fltBus = 1;  // this is the flag for unsuccessful factorization
//				}
//				return 0;
//			}
		}

		return 1;
	}

	/**
	 * input: acxVbus[0] is ground voltage
	 * acxVbus[1..nBus] are current injections
	 * output: acxVbus[1..nBus] are solved voltages
	 *
	 * @param acxVbus
	 */
	public void solve(DZcsa acxVbus) {
		double[] rhs = null;
		int i, i1, offset;

		if (m_nX < 1) return;  // nothing to do

		// load current injections into RHS
		rhs = new double[2 * m_nX];
		offset = 1;
		for (i = 0; i < m_nX; i++) {
			i1 = 2 * i;
			rhs[i1] = acxVbus.get(i + offset)[0];
			rhs[i1 + 1] = acxVbus.get(i + offset)[1];
		}
		int n = Y22.n;
		DZcsa b = new DZcsa(rhs);
		DZcsa x = new DZcsa(n);

		// solve and copy voltages into the output vector
		// relying on Y22.n == m_nX from T22 creation by csz_spalloc
//		klu_solve (symbolic, numeric, Y22.n, 1, rhs, common);
		cs_ipvec(numeric.pinv, b, x, n) ;		/* x = b(p) */
		cs_lsolve(numeric.L, x) ;			/* x = L\x */
		cs_usolve(numeric.U, x) ;			/* x = U\x */
		cs_ipvec(symbolic.q, x, b, n) ;		/* b(q) = x */

		offset = 1;
		for (i = 0; i < m_nX; i++) {
			i1 = 2 * i;
//			acxVbus.get(i+offset)[0] = rhs[i1];
//			acxVbus.get(i+offset)[1] = rhs[i1+1];
			acxVbus.set(i, b.get(i));
		}

		rhs = null;
	}

//	private static void augment_column(int[] Ai, DZcs y, int j, int offset, int[] p, int[] count) {
//		int[] yp = y.p;
//		int[] yi = y.i;
//		int k;
//
//		for (k = yp[j]; k < yp[j+1]; k++) {
//			Ai[p[0]] = yi[k] + offset;
//			count[0] += 1;
//			p[0] += 1;
//		}
//	}

	/* stack-based DFS from Sedgewick */

	private static int[] stack = null;
	private static int stk_p = 0;

	private static void push (int v) {
		stack[stk_p++] = v;
	}

	private static int pop() {
		return stack[--stk_p];
	}

	private static void stackfree() {
		stack = null;
	}

	private static boolean stackempty() {
		return stk_p == 0;
	}

	private static void stackinit (int size) {
		stackfree();
		stack = new int[size + 1];
		stk_p = 0;
	}

	private static void mark_dfs (int j, int cnt, int[] Ap, int[] Ai, int[] clique) {
		int i, k;

		push(j);
		while (!stackempty()) {
			j = pop();
			clique[j] = cnt;
			for (k = Ap[j]; k < Ap[j+1]; k++) {
				i = Ai[k];
				if (clique[i] == 0) {
					push (i);
					clique[i] = -1;  // to only push once
				}
			}
		}
	}

	/**
	 * Returns the number of connected components (cliques) in the whole system graph
	 * (i.e., considers Y11, Y12, and Y21 in addition to Y22)
	 * store the island number (1-based) for each node in idClique
	 *
	 * The KLU factorization might have some information about cliques in Y22 only,
	 * but we want to consider the whole system, so this function
	 * performs a new DFS on the compressed non-zero pattern
	 *
	 * This function could behave differently than before,
	 * since the compression process removes numerical zero elements
	 *
	 * @param idClique
	 * @return
	 */
	public int findIslands(int[] idClique) {
		factor();

		int[] clique = new int[m_nBus];
		int[] Ap = Y22.p;
		int[] Ai = Y22.i;
		int j;

		// DFS down the columns
		int cnt = 0;
		for (j = 0; j < m_nBus; j++) clique[j] = cnt; // use to mark the nodes with clique #
		stackinit(m_nBus);
		for (j = 0; j < m_nBus; j++) {
			if (clique[j] == 0) {  // have not visited this column yet
				++cnt;
				mark_dfs(j, cnt, Ap, Ai, clique);
			}
		}

		for (j = 0; j < m_nBus; j++) idClique[j] = clique[j];

		clique = null;
		return cnt;
	}

	/**
	 * returns the row > 0 if a zero appears on the diagonal
	 * calls Factor if necessary
	 * note: the EMTP terminology is "floating subnetwork"
	 *
	 * @return
	 */
	public int findDisconnectedSubnetwork() {
		factor();

		return m_fltBus;
	}

	/**
	 * Maintains allocations, zeros matrix values
	 */
	public void zero() {
		initialize(m_nBus, 0, m_nBus);
	}

	public void addElement(int iRow, int iCol, double[] cpxVal, boolean bSum) {  // bSum is ignored
		if (iRow > m_nBus || iCol > m_nBus) return;
		if (iRow <= 0 || iCol <= 0) return;
		--iRow;
		--iCol;

		double re = cpxVal[0];
		double im = cpxVal[1];

		if (re == 0.0 && im == 0.0) {
			return;
		}

		// stuff this value into the correct partition, transposed
		cs_entry (T22, iCol, iRow, cpxVal);
	}

	/**
	 * Return the sum of elements at FIXME: 1-based [iRow, iCol]
	 *
	 * @param iRow
	 * @param iCol
	 * @param cpxVal
	 */
	public void getElement(int iRow, int iCol, double[] cpxVal) {
		cpxVal[0] = 0; cpxVal[1] = 0;
		if (iRow > m_nBus || iCol > m_nBus) return;
		if (--iRow < 0) return;
		if (--iCol < 0) return;

		double[] Ax;
		int[] Ap, Ai;
		int i, p;

		if (T22 != null) {  // have to search the triplet storage, which is not sorted
			Ax = T22.x;
			Ap = T22.p;
			Ai = T22.i;
			for (i = 0; i < T22.nz; i++) {
				if (Ap[i] == iCol && Ai[i] == iRow) {
					cpxVal[0] += Ax[2*i];
					cpxVal[1] += Ax[2*i + 1];
				}
			}
		} else if (Y22 != null) {  // faster, duplicates already summed and elements are sorted
			Ax = Y22.x;
			Ap = Y22.p;
			Ai = Y22.i;
			for (p = Ap[iCol]; p < Ap[iCol+1]; ++p) {
				if (Ai[p] == iRow) {
					cpxVal[0] = Ax[2*p];
					cpxVal[1] = Ax[2*p + 1];
					return;
				}
			}
		}
	}

	/**
	 * For OpenDSS, return 1 for success
	 *
	 * @param nOrder
	 * @param pNodes
	 * @param pMat
	 * @return
	 */
	public int addPrimitiveMatrix(int nOrder, int[] pNodes, int node_offset, DZcsa pMat) {
		int i, j, idRow, idCol, idVal;
		double re, im;

		// check the node numbers
		for (i = 0; i < nOrder; i++) {
			if (pNodes[node_offset + i] > m_nBus) return 0;
		}

		// add the matrix transposed
		for (i = 0; i < nOrder; i++) {
			if (pNodes[node_offset + i] < 1) continue; // skip ground
			idVal = i;
			idRow = pNodes[node_offset + i] - 1;  // convert to zero-based
			for (j = 0; j < nOrder; j++) {
				if (pNodes[node_offset + j] != 0) {
					idCol = pNodes[node_offset + j] - 1;
					re = pMat.get(idVal)[0];
					im = pMat.get(idVal)[0];
					if (re != 0.0 || im != 0.0) {
						// stuff this value into the correct partition, transposed
						cs_entry (T22, idCol, idRow, pMat.get(idVal));
					}
				}
				// always step through values, even if we don't use them
				idVal += nOrder;
			}
		}
		return 1;
	}

	/**
	 * Return in compressed triplet form.
	 *
	 * @param nColP
	 * @param nNZ
	 * @param pColP
	 * @param pRowIdx
	 * @param pMat
	 * @return 1 for success, 0 for a size mismatch
	 */
	public int getCompressedMatrix(int nColP, int nNZ, int[] pColP,
			int[] pRowIdx, DZcsa pMat) {
		int rc = 0;

		if (T22 != null) factor();
		if (Y22 != null && nNZ >= m_NZpre && nColP > m_nBus) {
			rc = m_NZpre;
			if (rc > 0) {
				System.arraycopy(Y22.x, 0, pMat, 0, m_NZpre);
				System.arraycopy(Y22.p, 0, pColP, 0, (m_nBus + 1));
				System.arraycopy(Y22.i, 0, pRowIdx, 0, m_NZpre);
			}
		}
		return rc;
	}

	public int getTripletMatrix(int nNZ, int[] pRows, int[] pCols, DZcsa pMat) {
		int rc = 0;

		if (T22 != null) factor();
		if (Y22 != null && nNZ >= m_NZpre) {
			rc = m_NZpre;
			if (rc > 0) {
				System.arraycopy(Y22.x, 0, pMat, 0, m_NZpre);
				int[] Ap = Y22.p;
				int[] Ai = Y22.i;
				for (int j = 0; j < m_nBus; j++) {
					for (int p = Ap[j]; p < Ap[j+1]; p++) {
						pRows[p] = Ai[p];
						pCols[p] = j;
					}
				}
			}
		}
		return rc;
	}

}
