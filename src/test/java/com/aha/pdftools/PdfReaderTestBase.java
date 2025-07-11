package com.aha.pdftools;

import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.AfterEach;

import com.itextpdf.text.pdf.PdfReader;

public class PdfReaderTestBase {

    public static final String EXAMPLE_PDF_WITH_PNG_IMAGE = "image_example.pdf";
    public static final String EXAMPLE_PDF_WITH_JPEG_IMAGE = "image_jpeg_example.pdf";
    public static final String EXAMPLE_PDF_WITH_EMBEDDED_ODT = "doc_embedded_example.pdf";

    private InputStream pdfIn;
    private PdfReader reader;

    @AfterEach
    public void tearDown() throws Exception {
        if (reader != null) {
            reader.close();
        }
        if (pdfIn != null) {
            pdfIn.close();
        }
    }

    protected PdfReader setupReader(String example) throws IOException {
        pdfIn = getClass().getClassLoader().getResourceAsStream(example);
        reader = new PdfReader(pdfIn);
        return reader;
    }

    protected PdfReader getReader() {
        return reader;
    }

}
