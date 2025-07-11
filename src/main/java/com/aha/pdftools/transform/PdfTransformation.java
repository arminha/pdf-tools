package com.aha.pdftools.transform;

import java.io.IOException;
import java.util.Objects;

import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStream;

public abstract class PdfTransformation {

    public void transform(PdfReader reader) throws IOException {
        Objects.requireNonNull(reader);
        PdfObject obj;
        for (int i = 1; i < reader.getXrefSize(); i++) {
            obj = reader.getPdfObject(i);
            if (obj == null) {
                continue;
            }
            if (obj.isDictionary()) {
                transformDictionary((PdfDictionary) obj);
            }
            if (obj.isStream()) {
                transformStream((PdfStream) obj);
            }
        }
        postTransform(reader);
    }

    protected void transformDictionary(PdfDictionary dict) {
    }

    protected void transformStream(PdfStream stream) throws IOException {
    }

    protected void postTransform(PdfReader reader) {
    }

}
