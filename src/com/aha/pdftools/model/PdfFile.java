package com.aha.pdftools.model;

public class PdfFile extends PdfPermissions {

	private String mSourcePath;
	
	public PdfFile(String sourcePath, int perm) {
		super(perm);
		mSourcePath = sourcePath;
	}
	
	public String getSourcePath() {
		return mSourcePath;
	}
}
