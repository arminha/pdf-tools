package com.aha.pdftools.model;

import java.io.File;

public class PdfFile extends PdfPermissions {

	private final File mSourceFile;

	public PdfFile(File sourceFile, int perm) {
		super(perm);
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
}
