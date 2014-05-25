package com.aha.pdftools;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class PdfInfoHelperTest extends PdfReaderTestBase {

    @Test
    public void dumpStreamInfo() throws Exception {
        setupReader(EXAMPLE_PDF_WITH_PNG_IMAGE);

        PdfInfoHelper helper = new PdfInfoHelper();
        String streamInfo = helper.dumpStreamInfo(getReader());

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
