package com.aha.pdftools;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.itextpdf.text.pdf.PdfName;

public class PdfInfoHelperTest extends PdfReaderTestBase {

    private PdfInfoHelper helper;

    @Before
    public void setup() {
        helper = new PdfInfoHelper();
    }

    // @formatter:off
    private static final String STREAM_15 = "Stream #15 {\n"
            + "  /Type = /XObject\n"
            + "  /Subtype = /Image\n"
            + "  /ColorSpace = /DeviceRGB\n"
            + "  /Width = 512\n"
            + "  /BitsPerComponent = 8\n"
            + "  /Length = 733935\n"
            + "  /Height = 512\n"
            + "  /Filter = /FlateDecode\n" + "}";
    // @formatter:on

    @Test
    public void dumpStreamInfo() throws Exception {
        setupReader(EXAMPLE_PDF_WITH_PNG_IMAGE);

        String streamInfo = helper.dumpStreamInfo(getReader(), null, null);
        assertThat(streamInfo, containsString(STREAM_15));

        streamInfo = helper.dumpStreamInfo(getReader(), PdfName.XOBJECT, null);
        assertThat(streamInfo, containsString(STREAM_15));

        streamInfo = helper.dumpStreamInfo(getReader(), PdfName.XOBJECT, PdfName.IMAGE);
        assertThat(streamInfo, containsString(STREAM_15));

        streamInfo = helper.dumpStreamInfo(getReader(), PdfName.XOBJECT, PdfName.FILEATTACHMENT);
        assertThat(streamInfo, not(containsString(STREAM_15)));
    }

}
