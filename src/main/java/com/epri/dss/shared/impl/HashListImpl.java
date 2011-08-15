package com.epri.dss.shared.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import com.epri.dss.common.impl.Utilities;
import com.epri.dss.shared.HashList;

public class HashListImpl implements HashList {

	private class SubList {
		public int nElem;
		public int nAllocated;
		public String[] str;
		public int[] idx;
	}

	private int numElementsAllocated;

	private int numLists;

	private int numElements;

	private String[] stringArray;

	private SubList[] listArray;

	private int allocationInc;

	private int lastFind;

	private int lastHash;

	private String lastSearchString;

	protected int initialAllocation;

	public HashListImpl(int nelements) {
		super();
		numElements = 0;
		initialAllocation = nelements;
		stringArray = new String[numElements];

		numLists = (int) Math.round(Math.sqrt(nelements));
		int ElementsPerList = nelements / numLists + 1;
		allocationInc = ElementsPerList;
		if (numLists < 1) numLists = 1;  // make sure at least one list
		listArray = new SubList[numLists];
		for (int i = 0; i < numLists; i++) {
			listArray[i] = new SubList();
			/* Allocate initial sublists to zero; allocated on demand */
			listArray[i].str = new String[0];
			listArray[i].idx = new int[0];
			listArray[i].nAllocated = 0;
			listArray[i].nElem = 0;
		}
		numElementsAllocated = 0;
		lastFind = 0;
		lastHash = 0;
		lastSearchString = "";
	}

	public int getInitialAllocation() {
		return initialAllocation;
	}

	public void setInitialAllocation(int allocation) {
		initialAllocation = allocation;
	}

	public int listSize() {
		return numElements;
	}

	private void resizeSubList(SubList subList) {
		// resize by reasonable amount
		int oldAllocation = subList.nAllocated;
		subList.nAllocated = oldAllocation + allocationInc;
		subList.str = (String[]) Utilities.resizeArray(subList.str, subList.nAllocated);
		subList.idx = (int[]) Utilities.resizeArray(subList.idx, subList.nAllocated);
	}

	private int hash(String s) {
//		int HashValue = 0;//S.hashCode();
//		for (int i = 0; i < S.length(); i++) {
//			HashValue = ((HashValue << 5) | (HashValue >> 27)) ^ S.charAt(i);
//		}

		int HashValue = s.hashCode();
		return Math.abs(HashValue % numLists);  // FIXME: negative modulus
	}

	/** Makes the linear string list larger. */
	private void resizeStrArray() {
		numElementsAllocated += allocationInc * numLists;
		stringArray = (String[]) Utilities.resizeArray(stringArray, numElementsAllocated);
	}

	public int add(String s) {
		int hashNum;
		String ss;

		ss = s.toLowerCase();
		hashNum = hash(ss);

		numElements += 1;
		if (numElements > numElementsAllocated)
			resizeStrArray();

		listArray[hashNum].nElem += 1;
		if (listArray[hashNum].nElem > listArray[hashNum].nAllocated)
			resizeSubList(listArray[hashNum]);

		// make copy of whole string, lower case
		listArray[hashNum].str[listArray[hashNum].nElem - 1] = ss;
		// increments count to string
		stringArray[numElements - 1] = ss;

		listArray[hashNum].idx[listArray[hashNum].nElem - 1] = numElements - 1;

		return numElements;
	}

	/**
	 * Repeat find for duplicate string in same hash list.
	 */
	public int find(String s) {

		lastSearchString = s.toLowerCase();
		lastHash = hash(lastSearchString);
		int result = -1;
		lastFind = -1;

		for (int i = 0; i < listArray[lastHash].nElem; i++)
			if (lastSearchString.equalsIgnoreCase(listArray[lastHash].str[i])) {
				result = listArray[lastHash].idx[i];
				lastFind = i;
				break;
			}

		return result;
	}

	/**
	 * Begins search in same list as last.
	 */
	public int findNext() {
		// TODO: Check zero indexing.
		int result = -1;  // default return
		lastFind += 1;    // start with next item in hash list

		if ((lastHash > 0) && (lastHash <= numLists)) {
			for (int i = lastFind; i < listArray[lastHash].nElem; i++)
				if (lastSearchString.equalsIgnoreCase(listArray[lastHash].str[i])) {
					result = listArray[lastHash].idx[i];
					lastFind = i;
					break;
				}
		}

		return result;
	}

	/**
	 * Makes a linear search and tests each string until a string is found
	 * that matches all the characters entered in s.
	 */
	public int findAbbrev(String s) {
		String test1, test2;

		int result = 0;
		if (s.length() > 0) {
			test1 = s.toLowerCase();
			for (int i = 0; i < numElements; i++) {
				test2 = stringArray[i].substring(0, test1.length());
				if (test1.equalsIgnoreCase(test2)) {
					result = i;
					break;
				}
			}
		}

		return result;
	}

	public String get(int i) {
		// TODO: Check zero indexing
		return ((i > 0) && (i <= numElements)) ? stringArray[i] : "";
	}

	/**
	 * Expands number of elements.
	 *
	 * Creates a new set of string lists and copies the old strings
	 * into the new, hashing for the new number of lists.
	 */
	public void expand(int newSize) {

		String[] newStringArray;
		int newNumLists;
		int elementsPerList;
		SubList[] newListArray;
		int hashNum;
		String s;
//		int OldNumLists;

		if (newSize > numElementsAllocated) {

//			OldNumLists = NumLists;

			newStringArray = new String[newSize];
			newNumLists = (int) Math.sqrt(newSize);
			elementsPerList = newSize / newNumLists + 1;
			if (newNumLists < 1) newNumLists = 1;  // make sure at least one list
			newListArray = new SubList[newNumLists];
			for (int i = 0; i < numLists; i++) {  // TODO: Check zero indexing.
				/* Allocate initial sublists */
				newListArray[i].str = new String[elementsPerList];
				newListArray[i].idx = new int[elementsPerList];
				newListArray[i].nAllocated = elementsPerList;
				newListArray[i].nElem = 0;
			}

			numLists = newNumLists;  // has to be set so hash function will work

			/* Add elements from old hash list to new hash list */

			for (int i = 0; i < numElements; i++) {  // TODO: Check zero indexing
				s = stringArray[i];
				hashNum = hash(s);
				newListArray[hashNum].nElem += 1;
				if (newListArray[hashNum].nElem > newListArray[hashNum].nAllocated) {
					resizeSubList(newListArray[hashNum]);
				}

				newListArray[hashNum].str[newListArray[hashNum].nElem] = s;
				newStringArray[numElements] = newListArray[hashNum].str[newListArray[hashNum].nElem];
				newListArray[hashNum].idx[newListArray[hashNum].nElem] = i;
			}

			/* Assign new string and list pointers */

			stringArray = newStringArray;
			listArray = newListArray;
			numElementsAllocated = newSize;

		}
	}

	public void dumpToFile(String fname) {
		File f = new File(fname);
		try {
			PrintWriter pw = new PrintWriter(f);

			pw.println(String.format("Number of Hash Lists = %d, Number of Elements = %d", numLists, numElements));

			pw.println();
			pw.println("Hash List Distribution");
			for (int i = 0; i < numLists; i++)
				pw.println(String.format("List = %d, Number of elements = %d", i, listArray[i].nElem));
			pw.println();

			for (int i = 0; i < numLists; i++) {
				pw.println(String.format("List = %d, Number of elements = %d", i, listArray[i].nElem));
				for (int j = 0; j < listArray[i].nElem; j++)
					pw.println("\"" + listArray[i].str[j] + "\"  Idx= " + listArray[i].idx[j]);
				pw.println();
			}

			pw.println("LINEAR LISTING...");
			for (int i = 0; i < numElements; i++)
				pw.println(i + " = \"" + stringArray[i] + "\"");

			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
		}
	}

	public void clear() {
		for (int i = 0; i < numLists; i++) {
			listArray[i].nElem = 0;
			for (int j = 0; j < listArray[i].nAllocated; j++)
				listArray[i].str[j] = "";
		}

		for (int i = 0; i < numElementsAllocated; i++)
			stringArray[i] = "";

		numElements = 0;
	}

}
