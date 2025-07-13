package com.aha.pdftools.transform;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.aha.pdftools.PdfReaderTestBase;
import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

public class ShrinkImagesTest extends PdfReaderTestBase {

    private ShrinkImages transformation;

    @BeforeEach
    public void setup() {
        transformation = new ShrinkImages();
    }

    @Test
    public void reducedPdfSize() throws Exception {
        PdfReader reader = setupReader(EXAMPLE_PDF_WITH_PNG_IMAGE);
        transformation.setScaleFactor(0.5);
        transformation.transform(reader);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfStamper stamper = new PdfStamper(reader, outputStream);
        stamper.close();
        assertThat(outputStream.size()).isLessThan(62000);
    }

    @Test
    public void shrinkPngImage() throws Exception {
        setupReader(EXAMPLE_PDF_WITH_PNG_IMAGE);
        PRStream stream = (PRStream) getReader().getPdfObject(15);

        int originalLength = stream.getLength();
        transformation.transform(getReader());
        assertThat(stream.getLength()).isLessThan(originalLength)
            .isLessThan(40000);
        assertThat(stream.get(PdfName.BITSPERCOMPONENT)).isInstanceOfSatisfying(PdfNumber.class, n -> assertThat(n.intValue()).isEqualTo(8));
        assertThat(stream.get(PdfName.COLORSPACE)).isEqualTo(PdfName.DEVICERGB);
        assertThat(stream.get(PdfName.WIDTH)).isInstanceOfSatisfying(PdfNumber.class, n -> assertThat(n.intValue()).isEqualTo(512));
        assertThat(stream.get(PdfName.HEIGHT)).isInstanceOfSatisfying(PdfNumber.class, n -> assertThat(n.intValue()).isEqualTo(512));
    }

    @Test
    public void shrinkAndResizePngImage() throws Exception {
        setupReader(EXAMPLE_PDF_WITH_PNG_IMAGE);
        PRStream stream = (PRStream) getReader().getPdfObject(15);

        int originalLength = stream.getLength();
        transformation.setScaleFactor(0.5);
        transformation.transform(getReader());
        assertThat(stream.getLength()).isLessThan(originalLength)
            .isLessThan(14000);
        assertThat(stream.get(PdfName.BITSPERCOMPONENT)).isInstanceOfSatisfying(PdfNumber.class, n -> assertThat(n.intValue()).isEqualTo(8));
        assertThat(stream.get(PdfName.COLORSPACE)).isEqualTo(PdfName.DEVICERGB);
        assertThat(stream.get(PdfName.WIDTH)).isInstanceOfSatisfying(PdfNumber.class, n -> assertThat(n.intValue()).isEqualTo(256));
        assertThat(stream.get(PdfName.HEIGHT)).isInstanceOfSatisfying(PdfNumber.class, n -> assertThat(n.intValue()).isEqualTo(256));
    }

}
