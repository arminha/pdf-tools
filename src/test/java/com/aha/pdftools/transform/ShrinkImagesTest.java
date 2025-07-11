package com.aha.pdftools.transform;

import static com.aha.pdftools.IsPdfNumber.isPdfNumber;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

import java.io.ByteArrayOutputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.aha.pdftools.PdfReaderTestBase;
import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfName;
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
        assertThat(outputStream.size(), lessThan(62000));
    }

    @Test
    public void shrinkPngImage() throws Exception {
        setupReader(EXAMPLE_PDF_WITH_PNG_IMAGE);
        PRStream stream = (PRStream) getReader().getPdfObject(15);

        int originalLength = stream.getLength();
        transformation.transform(getReader());
        assertThat(stream.getLength(), lessThan(originalLength));
        assertThat(stream.getLength(), lessThan(40000));
        assertThat(stream.get(PdfName.BITSPERCOMPONENT), isPdfNumber(8));
        assertThat(stream.get(PdfName.COLORSPACE), is(PdfName.DEVICERGB));
        assertThat(stream.get(PdfName.WIDTH), isPdfNumber(512));
        assertThat(stream.get(PdfName.HEIGHT), isPdfNumber(512));
    }

    @Test
    public void shrinkAndResizePngImage() throws Exception {
        setupReader(EXAMPLE_PDF_WITH_PNG_IMAGE);
        PRStream stream = (PRStream) getReader().getPdfObject(15);

        int originalLength = stream.getLength();
        transformation.setScaleFactor(0.5);
        transformation.transform(getReader());
        assertThat(stream.getLength(), lessThan(originalLength));
        assertThat(stream.getLength(), lessThan(14000));
        assertThat(stream.get(PdfName.BITSPERCOMPONENT), isPdfNumber(8));
        assertThat(stream.get(PdfName.COLORSPACE), is(PdfName.DEVICERGB));
        assertThat(stream.get(PdfName.WIDTH), isPdfNumber(256));
        assertThat(stream.get(PdfName.HEIGHT), isPdfNumber(256));
    }

}
