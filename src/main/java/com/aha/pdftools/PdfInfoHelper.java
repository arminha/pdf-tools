package com.aha.pdftools;

import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;

public class PdfInfoHelper {

    public String dumpStreamInfo(PdfReader reader) {
        StringBuilder sb = new StringBuilder();

        int n = reader.getXrefSize();
        PdfObject object;
        PRStream stream;

        for (int i = 0; i < n; i++) {
            object = reader.getPdfObject(i);
            if (object == null || !object.isStream()) {
                continue;
            }
            stream = (PRStream) object;
            appendStreamInfo(sb, stream, i);
            sb.append('\n');
        }

        return sb.toString();
    }

    public void appendStreamInfo(StringBuilder sb, PRStream stream, int index) {
        sb.append("Stream #");
        sb.append(index);
        sb.append(" {\n");
        for (PdfName key : stream.getKeys()) {
            PdfObject object = stream.get(key);
            sb.append("  ");
            sb.append(key);
            sb.append(" = ");
            sb.append(object);
            sb.append('\n');
        }
        sb.append('}');
    }

}
