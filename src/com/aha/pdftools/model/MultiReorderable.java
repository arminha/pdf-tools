package com.aha.pdftools.model;

public interface MultiReorderable {
	/**
	 * Move a list of items given in {@code fromIndices} and insert them at {@code toIndex}.
	 * The method return the new index of the first item.
	 * @param fromIndices
	 * @param toIndex
	 * @return
	 */
	public int reorder(int[] fromIndices, int toIndex);
}
