package com.aha.pdftools.model;

import java.io.File;
import java.io.IOException;

import com.aha.pdftools.PdfPermissionManager;
import com.itextpdf.text.pdf.PdfReader;

public class PdfFile extends PdfPermissions {

    private final File mSourceFile;
    private final int mPageCount;

    public static PdfFile openFile(File file) throws IOException {
        PdfReader reader = new PdfReader(file.getAbsolutePath());
        long perm = PdfPermissionManager.getPermissions(reader);
        int pageCount = reader.getNumberOfPages();
        reader.close();
        return new PdfFile(file, perm, pageCount);
    }

    public PdfFile(File sourceFile, long perm, int pageCount) {
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
