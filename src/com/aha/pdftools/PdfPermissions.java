package com.aha.pdftools;

import com.itextpdf.text.pdf.PdfWriter;

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
        allowAssembly = (perms & PdfWriter.ALLOW_ASSEMBLY) != 0;
        allowCopy = (perms & PdfWriter.ALLOW_COPY) != 0;
        allowDegradedPrinting = (perms & PdfWriter.ALLOW_DEGRADED_PRINTING) != 0;
        allowPrinting = (perms & PdfWriter.ALLOW_PRINTING) != 0;
        allowScreenReaders = (perms & PdfWriter.ALLOW_SCREENREADERS) != 0;
        allowFillIn = (perms & PdfWriter.ALLOW_FILL_IN) != 0;
        allowModifyContents = (perms & PdfWriter.ALLOW_MODIFY_CONTENTS) != 0;
        allowModifyAnnotations = (perms & PdfWriter.ALLOW_MODIFY_ANNOTATIONS) != 0;
    }

    public int getPermissions() {
        // as per User access permissions table in the pdf specification
        int perms = 0xFFFFF0C0;
        if (allowAssembly)
            perms = perms | PdfWriter.ALLOW_ASSEMBLY;
        if (allowCopy)
            perms = perms | PdfWriter.ALLOW_COPY;
        if (allowDegradedPrinting)
            perms = perms | PdfWriter.ALLOW_DEGRADED_PRINTING;
        if (allowPrinting)
            perms = perms | PdfWriter.ALLOW_PRINTING;
        if (allowScreenReaders)
            perms = perms | PdfWriter.ALLOW_SCREENREADERS;
        if (allowFillIn)
            perms = perms | PdfWriter.ALLOW_FILL_IN;
        if (allowModifyContents)
            perms = perms | PdfWriter.ALLOW_MODIFY_CONTENTS;
        if (allowModifyAnnotations)
            perms = perms | PdfWriter.ALLOW_MODIFY_ANNOTATIONS;
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
