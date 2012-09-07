package com.aha.pdftools.model;

import java.io.File;

public class PdfPages extends AbstractModelObject {

	private final File mSourceFile;

	public PdfPages(File sourceFile) {
		mSourceFile = sourceFile;
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
		// TODO
		return new int[] { 1 };
	}

	public int getPageCount() {
		// TODO
		return 1;
	}

}
