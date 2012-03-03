package net.sourceforge.klusolve;

import edu.emory.mathcs.csparsej.tdcomplex.DZcs_common.DZcsa;

public interface ISystem {

	void initDefaults();

	void clear();

	int factorSystem();

	int solveSystem(DZcsa acxX, DZcsa acxB);

	/**
	 * This resets and reinitializes the sparse matrix, nI = nBus
	 *
	 * @param nBus
	 * @param nV
	 * @param nI
	 * @return
	 */
	int initialize(int nBus, int nV, int nI);

	int initialize(int nBus);

	int getSize();

	// metrics
	int getSparseNNZ();

	int getNNZ();

	double getRCond();

	double getRGrowth();

	double getCondEst();

	double getFlops();

	int getSingularCol();

//	void addMatrix(int[] aidBus, MatrixComplex pcxm, int bSum);

	/**
	 * returns 1 for success, -1 for a singular matrix
	 * returns 0 for another error, most likely the matrix is too large for int
	 *
	 * @return
	 */
	int factor();

	/**
	 * input: acxVbus[0] is ground voltage
	 * acxVbus[1..nBus] are current injections
	 * output: acxVbus[1..nBus] are solved voltages
	 *
	 * @param acxVbus
	 */
	void solve(DZcsa acxVbus);

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
	int findIslands(int[] idClique);

	/**
	 * returns the row > 0 if a zero appears on the diagonal
	 * calls Factor if necessary
	 * note: the EMTP terminology is "floating subnetwork"
	 *
	 * @return
	 */
	int findDisconnectedSubnetwork();

	/**
	 * Maintains allocations, zeros matrix values
	 */
	void zero();

	void addElement(int iRow, int iCol, double[] cpxVal, boolean bSum);

	/**
	 * Return the sum of elements at FIXME: 1-based [iRow, iCol]
	 *
	 * @param iRow
	 * @param iCol
	 * @param cpxVal
	 */
	void getElement(int iRow, int iCol, double[] cpxVal);

	/**
	 * For OpenDSS, return 1 for success
	 *
	 * @param nOrder
	 * @param pNodes
	 * @param pMat
	 * @return
	 */
	int addPrimitiveMatrix(int nOrder, int[] pNodes, int node_offset, DZcsa pMat);

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
	int getCompressedMatrix(int nColP, int nNZ, int[] pColP, int[] pRowIdx, DZcsa pMat);

	int getTripletMatrix(int nNZ, int[] pRows, int[] pCols, DZcsa pMat);

	boolean isFactored();

	void setFactored(boolean factored);

}