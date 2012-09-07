package com.aha.pdftools.model;

import java.io.File;

public class PdfPages extends AbstractModelObject {

	private final File mSourceFile;
	private final int mSourcePageCount;
	private int[] mPages;

	public PdfPages(File sourceFile, int sourcePageCount) {
		mSourceFile = sourceFile;
		mSourcePageCount = sourcePageCount;
		mPages = new int[sourcePageCount];
		for (int i = 0; i < sourcePageCount; i++) {
			mPages[i] = i + 1;
		}
	}

	public String getSourcePath() {
		return mSourceFile.getAbsolutePath();
	}

	public File getSourceFile() {
		return mSourceFile;
	}

	public String getName() {
		return mSourceFile.getName();
	}

	public int[] getPages() {
		return mPages;
	}

	public int getPageCount() {
		return mPages.length;
	}

	public int getSourcePageCount() {
		return mSourcePageCount;
	}

	public String getPagesString() {
		// TODO
		return "";
	}
	
	public void setPagesString(String pagesAsString) {
		// TODO
	}
}
