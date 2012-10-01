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

import com.itextpdf.text.pdf.PdfWriter;

public class PdfPermissions extends AbstractModelObject {

	private boolean mAssembly = true;
	private boolean mCopy = true;
	private boolean mDegradedPrinting = true;
	private boolean mPrinting = true;
	private boolean mScreenReaders = true;
	private boolean mFillIn = true;
	private boolean mModifyContents = true;
	private boolean mModifyAnnotations = true;

	public PdfPermissions() {
	}

	public PdfPermissions(int perms) {
		mAssembly = isFlagSet(perms, PdfWriter.ALLOW_ASSEMBLY);
		mCopy = isFlagSet(perms, PdfWriter.ALLOW_COPY);
		mDegradedPrinting = isFlagSet(perms, PdfWriter.ALLOW_DEGRADED_PRINTING);
		mPrinting = isFlagSet(perms, PdfWriter.ALLOW_PRINTING);
		mScreenReaders = isFlagSet(perms, PdfWriter.ALLOW_SCREENREADERS);
		mFillIn = isFlagSet(perms, PdfWriter.ALLOW_FILL_IN);
		mModifyContents = isFlagSet(perms, PdfWriter.ALLOW_MODIFY_CONTENTS);
		mModifyAnnotations = isFlagSet(perms, PdfWriter.ALLOW_MODIFY_ANNOTATIONS);
	}

	private static boolean isFlagSet(int value, int flag) {
		return (value & flag) == flag;
	}

	public boolean isAssembly() {
		return mAssembly;
	}

	public void setAssembly(boolean allowAssembly) {
		boolean oldValue = mAssembly;
		mAssembly = allowAssembly;
		firePropertyChange("assembly", oldValue, mAssembly);
	}

	public boolean isCopy() {
		return mCopy;
	}

	public void setCopy(boolean allowCopy) {
		boolean oldValue = mCopy;
		mCopy = allowCopy;
		firePropertyChange("copy", oldValue, mCopy);
	}

	public boolean isDegradedPrinting() {
		return mDegradedPrinting;
	}

	public void setDegradedPrinting(boolean allowDegradedPrinting) {
		boolean oldValue = mDegradedPrinting;
		mDegradedPrinting = allowDegradedPrinting;
		firePropertyChange("degradedPrinting", oldValue, mDegradedPrinting);
	}

	public boolean isPrinting() {
		return mPrinting;
	}

	public void setPrinting(boolean allowPrinting) {
		boolean oldValue = mPrinting;
		mPrinting = allowPrinting;
		firePropertyChange("printing", oldValue, mPrinting);
	}

	public boolean isScreenReaders() {
		return mScreenReaders;
	}

	public void setScreenReaders(boolean allowScreenReaders) {
		boolean oldValue = mScreenReaders;
		mScreenReaders = allowScreenReaders;
		firePropertyChange("screenReaders", oldValue, mScreenReaders);
	}

	public boolean isFillIn() {
		return mFillIn;
	}

	public void setFillIn(boolean allowFillIn) {
		boolean oldValue = mFillIn;
		mFillIn = allowFillIn;
		firePropertyChange("fillIn", oldValue, mFillIn);
	}

	public boolean isModifyContents() {
		return mModifyContents;
	}

	public void setModifyContents(boolean allowModifyContents) {
		boolean oldValue = mModifyContents;
		mModifyContents = allowModifyContents;
		firePropertyChange("modifyContents", oldValue, mModifyContents);
	}

	public boolean isModifyAnnotations() {
		return mModifyAnnotations;
	}

	public void setModifyAnnotations(boolean allowModifyAnnotations) {
		boolean oldValue = mModifyAnnotations;
		mModifyAnnotations = allowModifyAnnotations;
		firePropertyChange("modifyAnnotations", oldValue, mModifyAnnotations);
	}

	public int getPermissions() {
		// as per User access permissions table in the pdf specification
		int perms = 0xFFFFF0C0;
		if (mAssembly)
			perms = perms | PdfWriter.ALLOW_ASSEMBLY;
		if (mCopy)
			perms = perms | PdfWriter.ALLOW_COPY;
		if (mDegradedPrinting)
			perms = perms | PdfWriter.ALLOW_DEGRADED_PRINTING;
		if (mPrinting)
			perms = perms | PdfWriter.ALLOW_PRINTING;
		if (mScreenReaders)
			perms = perms | PdfWriter.ALLOW_SCREENREADERS;
		if (mFillIn)
			perms = perms | PdfWriter.ALLOW_FILL_IN;
		if (mModifyContents)
			perms = perms | PdfWriter.ALLOW_MODIFY_CONTENTS;
		if (mModifyAnnotations)
			perms = perms | PdfWriter.ALLOW_MODIFY_ANNOTATIONS;
		return perms;
	}

	public String getPermissionsAsString() {
		String permissions = "";
		if (mPrinting) {
			permissions += "AllowPrinting ";
		}
		if (mAssembly) {
			permissions += "AllowAssembly";
		}
		if (mCopy) {
			permissions += "AllowCopy";
		}
		if (mDegradedPrinting) {
			permissions += "AllowDegradedPrinting";
		}
		if (mScreenReaders) {
			permissions += "AllowScreenReaders";
		}
		if (mFillIn) {
			permissions += "AllowFillIn";
		}
		if (mModifyContents) {
			permissions += "AllowModifyContents";
		}
		if (mModifyAnnotations) {
			permissions += "AllowModifyAnnotations";
		}
		return permissions;
	}

}
