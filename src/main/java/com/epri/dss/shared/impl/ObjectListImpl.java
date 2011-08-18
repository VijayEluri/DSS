package com.epri.dss.shared.impl;

import com.epri.dss.shared.ObjectList;

/**
 * Iterable list of objects.
 *
 * TODO: Replace with java.util.List and Iterator
 *
 * @param <T>
 */
public class ObjectListImpl<T> implements ObjectList<T> {

	private int numInList;

	private int maxAllocated;

	private int activeItem;

	private T[] list;

	private int incrementSize;

	public ObjectListImpl(int size) {
		super();
		maxAllocated = size;
		// default size & increment
		if (maxAllocated <= 0) maxAllocated = 10;
		list = (T[]) new Object[maxAllocated];
		numInList = 0;
		activeItem = 0;
		// increment is equal to original allocation
		incrementSize = maxAllocated;
	}

	public T getFirst() {
		if (numInList > 0) {
		activeItem = 0;
		return list[activeItem];
		} else {
			activeItem = -1;
			return null;
		}
	}

	public T getNext() {
		if (numInList > 0) {
		activeItem += 1;
		if (activeItem > numInList) {
			activeItem = numInList;
			return null;
		} else {
			return list[activeItem];
		}
		} else {
			activeItem = -1;
			return null;
		}
	}

	public T getActive() {
		if ((activeItem > 0) && (activeItem <= numInList)) {
			return get(activeItem);
		} else {
			return null;
		}
	}

	public void setNew(T value) {
		add(value);
	}

	public void clear() {
		activeItem = -1;
		numInList = 0;
	}

	/** Returns index of item */
	public int add(T p) {
		T[] newList;
		int size, l;

		numInList += 1;
		if (numInList > maxAllocated) {
			// resize array
			maxAllocated = maxAllocated + incrementSize;
			newList = (T[]) new Object[maxAllocated];
			size = list.length;
			l = Math.min(size, maxAllocated);
			if (l > 0)
				System.arraycopy(list, 0, newList, 0, l);
			list = newList;
		}
		list[numInList] = p;
		activeItem = numInList;
		return numInList;
	}

	public T get(int i) {
		if ((i < 1) || (i > numInList)) {
			return null;
		} else {
			activeItem = i;
			return list[i];
		}
	}

	public int size() {
		return numInList;
	}

	public int getActiveIndex() {
		return activeItem;
	}

}
