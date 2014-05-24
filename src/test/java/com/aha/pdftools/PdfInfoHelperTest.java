package com.aha.pdftools;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

import java.io.InputStream;

import org.junit.Test;

import com.itextpdf.text.pdf.PdfReader;

public class PdfInfoHelperTest {

    private static final String EXAMPLE_PDF = "image_example.pdf";

    @Test
    public void dumpStreamInfo() throws Exception {
        InputStream pdfIn = getClass().getClassLoader().getResourceAsStream(EXAMPLE_PDF);
        PdfReader reader = new PdfReader(pdfIn);

        PdfInfoHelper helper = new PdfInfoHelper();
        String streamInfo = helper.dumpStreamInfo(reader);

        System.out.println(streamInfo);
        // @formatter:off
        assertThat(streamInfo, containsString("Stream #15 {\n"
                + "  /Type = /XObject\n"
                + "  /Subtype = /Image\n"
                + "  /ColorSpace = /DeviceRGB\n"
                + "  /Width = 512\n"
                + "  /BitsPerComponent = 8\n"
                + "  /Length = 733935\n"
                + "  /Height = 512\n"
                + "  /Filter = /FlateDecode\n" + "}"));
        // @formatter:on
    }

}
