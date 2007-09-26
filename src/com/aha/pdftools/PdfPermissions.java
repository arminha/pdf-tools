package com.aha.pdftools;

import com.lowagie.text.pdf.PdfWriter;

public class PdfPermissions {

    public boolean allowAssembly = true;
    public boolean allowCopy = true;
    public boolean allowDegradedPrinting = true;
    public boolean allowPrinting = true;
    public boolean allowScreenReaders = true;
    public boolean allowFillIn = true;
    public boolean allowModifyContents = true;
    public boolean allowModifyAnnotations = true;

    public PdfPermissions() {
    }

    public PdfPermissions(int perms) {
        allowAssembly = (perms & PdfWriter.AllowAssembly) != 0;
        allowCopy = (perms & PdfWriter.AllowCopy) != 0;
        allowDegradedPrinting = (perms & PdfWriter.AllowDegradedPrinting) != 0;
        allowPrinting = (perms & PdfWriter.AllowPrinting) != 0;
        allowScreenReaders = (perms & PdfWriter.AllowScreenReaders) != 0;
        allowFillIn = (perms & PdfWriter.AllowFillIn) != 0;
        allowModifyContents = (perms & PdfWriter.AllowModifyContents) != 0;
        allowModifyAnnotations = (perms & PdfWriter.AllowModifyAnnotations) != 0;
    }

    public int getPermissions() {
        int perms = 0;
        if (allowAssembly)
            perms = perms | PdfWriter.AllowAssembly;
        if (allowCopy)
            perms = perms | PdfWriter.AllowCopy;
        if (allowDegradedPrinting)
            perms = perms | PdfWriter.AllowDegradedPrinting;
        if (allowPrinting)
            perms = perms | PdfWriter.AllowPrinting;
        if (allowScreenReaders)
            perms = perms | PdfWriter.AllowScreenReaders;
        if (allowFillIn)
            perms = perms | PdfWriter.AllowFillIn;
        if (allowModifyContents)
            perms = perms | PdfWriter.AllowModifyContents;
        if (allowModifyAnnotations)
            perms = perms | PdfWriter.AllowModifyAnnotations;
        return perms;
    }

    public String getPermissionsAsString() {
        String permissions = "";
        if (allowPrinting) {
            permissions += "AllowPrinting ";
        }
        if (allowAssembly) {
            permissions += "AllowAssembly";
        }
        if (allowCopy) {
            permissions += "AllowCopy";
        }
        if (allowDegradedPrinting) {
            permissions += "AllowDegradedPrinting";
        }
        if (allowScreenReaders) {
            permissions += "AllowScreenReaders";
        }
        if (allowFillIn) {
            permissions += "AllowFillIn";
        }
        if (allowModifyContents) {
            permissions += "AllowModifyContents";
        }
        if (allowModifyAnnotations) {
            permissions += "AllowModifyAnnotations";
        }
        return permissions;
    }

}
