package com.aha.pdftools;

import java.util.HashMap;
import java.util.Map;

import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;

public class PdfInfoHelper {

    /**
     * Returns a String with information about stream objects in the given PDF. The parameters <code>filterType</code>
     * and <code>filterSubType</code> can be set to only streams of the given type.
     * 
     * @param reader
     *            a {@link PdfReader}
     * @param filterType
     *            a type to filter. If null all types are included.
     * @param filterSubType
     *            a subtype to filter. If null all subtypes are included.
     */
    public String dumpStreamInfo(PdfReader reader, PdfName filterType, PdfName filterSubType) {
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
            if (filterType != null && !filterType.equals(stream.get(PdfName.TYPE))) {
                continue;
            }
            if (filterSubType != null && !filterSubType.equals(stream.get(PdfName.SUBTYPE))) {
                continue;
            }
            appendStreamInfo(sb, stream, i);
            sb.append('\n');
        }

        return sb.toString();
    }

    private void appendStreamInfo(StringBuilder sb, PRStream stream, int index) {
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

    public Map<PdfName, Integer> getStreamSizeByType(PdfReader reader) {
        Map<PdfName, Integer> sizes = new HashMap<>();

        int n = reader.getXrefSize();
        PdfObject object;
        PRStream stream;

        for (int i = 0; i < n; i++) {
            object = reader.getPdfObject(i);
            if (object == null || !object.isStream()) {
                continue;
            }
            stream = (PRStream) object;
            int length = stream.getLength();
            PdfName type = (PdfName) stream.get(PdfName.TYPE);
            if (type == null || type.equals(PdfName.XOBJECT)) {
                type = (PdfName) stream.get(PdfName.SUBTYPE);
            }
            Integer total = sizes.get(type);
            if (total == null) {
                total = length;
            } else {
                total = total + length;
            }
            sizes.put(type, total);
        }

        return sizes;
    }
}
