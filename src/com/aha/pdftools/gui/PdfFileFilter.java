package com.aha.pdftools.gui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import com.aha.pdftools.Messages;

public class PdfFileFilter extends FileFilter implements java.io.FileFilter {

	public static final String PDF_EXTENSION = ".pdf"; //$NON-NLS-1$

	@Override
	public boolean accept(File f) {
		if (f.isFile()) {
			return (f.getName().endsWith(PDF_EXTENSION));
		}
		return f.isDirectory();
	}

	@Override
	public String getDescription() {
		return Messages.getString("PdfFileFilter.PdfFiles"); //$NON-NLS-1$
	}

}
