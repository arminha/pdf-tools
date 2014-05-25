package com.aha.pdftools;

import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;

import org.junit.Test;

import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

public class ShrinkImageTest extends PdfReaderTestBase {

    @Test
    public void shrinkImage() throws Exception {
        PdfReader reader = setupReader(EXAMPLE_PDF_WITH_PNG_IMAGE);
        PdfShrinker shrinker = new PdfShrinker();
        shrinker.setScaleFactor(0.5);
        int n = reader.getXrefSize();
        PdfObject object;
        PRStream stream;
        for (int i = 0; i < n; i++) {
            object = reader.getPdfObject(i);
            if (object == null || !object.isStream()) {
                continue;
            }
            stream = (PRStream) object;
            if (stream.checkType(PdfName.XOBJECT) && PdfName.IMAGE.equals(stream.get(PdfName.SUBTYPE))) {
                shrinker.shrinkImage(stream);
            }
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfStamper stamper = new PdfStamper(reader, outputStream);
        stamper.close();
        assertThat(outputStream.size(), lessThan(62000));
    }

}
