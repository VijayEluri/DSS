/* ------------------------------------------------------------------------- */
/* Copyright (C) 2008, EnerNex Corporation. All rights reserved.             */
/* Copyright (C) 2012, Richard Lincoln. All rights reserved.                 */
/* Licensed under the GNU Lesser General Public License (LGPL) v 2.1         */
/* ------------------------------------------------------------------------- */

package net.sourceforge.klusolve;

import edu.emory.mathcs.csparsej.tdcomplex.DZcs_common.DZcsa;

import static edu.ufl.cise.klu.tdouble.Dklu_defaults.klu_defaults;
import static edu.ufl.cise.klu.tdouble.Dklu_analyze.klu_analyze;
import static edu.ufl.cise.klu.tdouble.Dklu_factor.klu_factor;
import static edu.ufl.cise.klu.tdouble.Dklu_solve.klu_solve;
import static edu.ufl.cise.klu.tdouble.Dklu_diagnostics.klu_condest;
import static edu.ufl.cise.klu.tdouble.Dklu_diagnostics.klu_flops;
import static edu.ufl.cise.klu.tdouble.Dklu_diagnostics.klu_rcond;
import static edu.ufl.cise.klu.tdouble.Dklu_diagnostics.klu_rgrowth;

import static edu.ufl.cise.klu.tdouble.Dklu_version.KLU_OK;
import static edu.ufl.cise.klu.tdouble.Dklu_version.KLU_SINGULAR;

import edu.ufl.cise.klu.common.KLU_common;
import edu.ufl.cise.klu.common.KLU_numeric;
import edu.ufl.cise.klu.common.KLU_symbolic;


public class KLUSolver extends CSparseSolver {

	private static KLU_common common;
	private static boolean common_init = false;

	private KLU_symbolic symbolic;
	private KLU_numeric numeric;

	protected int nnz_post;  // number of non-zero entries after factoring
	protected int fltbus;    // row number of a bus causing singularity

	protected void zero_indices() {
		super.zero_indices();
		nnz_post = 0;
		fltbus = 0;
	}

	@Override
	public void initDefaults() {
		super.initDefaults();
		if (!common_init) {
			klu_defaults(common);
			common.halt_if_singular = 0;
			common_init = true;
		}
	}

	// metrics

	@Override
	public int getSparseNNZ() {
		return nnz_post;
	}

	@Override
	public double getRCond() {
		klu_rcond (symbolic, numeric, common);
		return common.rcond;
	}

	@Override
	public double getRGrowth() {
		if (Y22 == null) return 0.0;
		klu_rgrowth (Y22.p, Y22.i, Y22.x, symbolic, numeric, common);
		return common.rgrowth;
	}

	@Override
	public double getCondEst() {
		if (Y22 == null) return 0.0;
		if (Y22.n > 1) klu_condest (Y22.p, Y22.x, symbolic, numeric, common);
		return common.condest;
	}

	@Override
	public double getFlops() {
		klu_flops(symbolic, numeric, common);
		return common.flops;
	}

	public int getSingularCol() {
		return fltbus;
	}

	/**
	 * @return 1 for success, -1 for a singular matrix, 0 for another
	 * error, most likely the matrix is too large for int32
	 */
	@Override
	public int factor() {
		// first convert the triplets to column-compressed form, and prep the columns
		if (T22 != null) {
			compress_partitions();
		} else {  // otherwise, compression and factoring has already been done
			if (fltbus != 0) return -1;  // was found singular before
			return 1;  // was found okay before
		}

		// then factor Y22
		if (numeric != null) numeric = null;
		if (symbolic != null) symbolic = null;

		if (Y22 != null) {
			symbolic = klu_analyze (Y22.n, Y22.p, Y22.i, common);
			numeric = klu_factor (Y22.p, Y22.i, Y22.x, symbolic, common);

			fltbus = common.singular_col;
			if (common.singular_col < Y22.n) {
				++fltbus; // FIXME for 1-based NexHarm row numbers
				fltbus += 0; // skip over the voltage source buses
			} else {
				fltbus = 0;  // no singular submatrix was found
			}
			if (common.status == KLU_OK) {
				// compute size of the factorization
				nnz_post += (numeric.lnz + numeric.unz - numeric.n +
					((numeric.Offp != null) ? (numeric.Offp[numeric.n]) : 0));
				return 1;
			} else if (common.status == KLU_SINGULAR) {
				return -1;
			} else {  // KLU_OUT_OF_MEMORY, KLU_INVALID, or KLU_TOO_LARGE
				if (fltbus == 0) {
					fltbus = 1;  // this is the flag for unsuccessful factorization
				}
				return 0;
			}
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
	@Override
	public void solve(DZcsa acxVbus) {
		double[] rhs = null;
		int i, i1, offset;

		if (nx < 1) return;  // nothing to do

		// load current injections into RHS
		rhs = new double[2 * nx];
		offset = 1;
		for (i = 0; i < nx; i++) {
			i1 = 2 * i;
			rhs[i1] = acxVbus.get(i + offset)[0];
			rhs[i1 + 1] = acxVbus.get(i + offset)[1];
		}

		// solve and copy voltages into the output vector
		// relying on Y22.n == m_nX from T22 creation by csz_spalloc
		klu_solve (symbolic, numeric, Y22.n, 1, rhs, common);

		offset = 1;
		for (i = 0; i < nx; i++) {
			i1 = 2 * i;
			acxVbus.get(i+offset)[0] = rhs[i1];
			acxVbus.get(i+offset)[1] = rhs[i1+1];
		}

		rhs = null;
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

		return fltbus;
	}

}
