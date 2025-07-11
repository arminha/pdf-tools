package com.aha.pdftools.transform;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;

import java.io.ByteArrayOutputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.aha.pdftools.PdfReaderTestBase;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

public class RemoveMetadataTest extends PdfReaderTestBase {

    private RemoveMetadata transformation;

    @BeforeEach
    public void setup() {
        transformation = new RemoveMetadata();
    }

    @Test
    public void removeMetaData() throws Exception {
        PdfReader reader = setupReader(EXAMPLE_PDF_WITH_EMBEDDED_ODT);

        transformation.transform(reader);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfStamper stamper = new PdfStamper(reader, outputStream);
        stamper.close();
        assertThat(outputStream.size(), lessThan(780000));
    }

}
