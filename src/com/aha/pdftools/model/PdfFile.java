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

	public void setAllowAll(boolean allow) {
		setAssembly(allow);
		setCopy(allow);
		setDegradedPrinting(allow);
		setFillIn(allow);
		setModifyAnnotations(allow);
		setModifyContents(allow);
		setPrinting(allow);
		setScreenReaders(allow);
	}

	public boolean isAllowAll() {
		return isAssembly()
				&& isCopy()
				&& isDegradedPrinting()
				&& isFillIn()
				&& isModifyAnnotations()
				&& isModifyContents()
				&& isPrinting()
				&& isScreenReaders();
	}
}
