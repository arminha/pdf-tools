/*
 * Copyright (C) 2012  Armin Häberling
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.aha.pdftools.model;

import java.io.File;
import java.io.IOException;

import com.aha.pdftools.PdfPermissionManager;
import com.itextpdf.text.pdf.PdfReader;

public class PdfFile extends PdfPermissions {

	private final File mSourceFile;
	private final int mPageCount;

	public static final PdfFile openFile(File file) throws IOException {
		PdfReader reader = new PdfReader(file.getAbsolutePath());
		int perm = PdfPermissionManager.getPermissions(reader);
		int pageCount = reader.getNumberOfPages();
		reader.close();
		return new PdfFile(file, perm, pageCount);
	}

	public PdfFile(File sourceFile, int perm, int pageCount) {
		super(perm);
		mSourceFile = sourceFile;
		mPageCount = pageCount;
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

	public int getPageCount() {
		return mPageCount;
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
