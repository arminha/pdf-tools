package com.aha.pdftools;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.itextpdf.text.pdf.PdfName;

public class PdfInfoHelperTest extends PdfReaderTestBase {

    private PdfInfoHelper helper;

    @BeforeEach
    public void setup() {
        helper = new PdfInfoHelper();
    }

    // @formatter:off
    private static final String STREAM_15 = "Stream #15 {\n"
            + "  /Type = /XObject\n"
            + "  /Subtype = /Image\n"
            + "  /Width = 512\n"
            + "  /Height = 512\n"
            + "  /BitsPerComponent = 8\n"
            + "  /Length = 733935\n"
            + "  /Filter = /FlateDecode\n"
            + "  /ColorSpace = /DeviceRGB\n"
            + "}";
    // @formatter:on

    @Test
    public void dumpStreamInfo() throws Exception {
        setupReader(EXAMPLE_PDF_WITH_PNG_IMAGE);

        String streamInfo = helper.dumpStreamInfo(getReader(), null, null);
        assertThat(streamInfo).contains(STREAM_15);

        streamInfo = helper.dumpStreamInfo(getReader(), PdfName.XOBJECT, null);
        assertThat(streamInfo).contains(STREAM_15);

        streamInfo = helper.dumpStreamInfo(getReader(), PdfName.XOBJECT, PdfName.IMAGE);
        assertThat(streamInfo).contains(STREAM_15);

        streamInfo = helper.dumpStreamInfo(getReader(), PdfName.XOBJECT, PdfName.FILEATTACHMENT);
        assertThat(streamInfo).doesNotContain(STREAM_15);
    }

    @Test
    public void getStreamSizeByType() throws Exception {
        setupReader(EXAMPLE_PDF_WITH_PNG_IMAGE);

        Map<PdfName, Integer> sizeByType = helper.getStreamSizeByType(getReader());

        assertThat(sizeByType).containsEntry(null, 38930);
        assertThat(sizeByType).containsEntry(PdfName.METADATA, 3058);
        assertThat(sizeByType).containsEntry(PdfName.IMAGE, 733935);
    }

    @Test
    public void getStreamSizeByTypeEmbeddedOdt() throws Exception {
        setupReader(EXAMPLE_PDF_WITH_EMBEDDED_ODT);

        Map<PdfName, Integer> sizeByType = helper.getStreamSizeByType(getReader());

        assertThat(sizeByType).containsEntry(null, 570325);
        assertThat(sizeByType).containsEntry(PdfName.METADATA, 3058);
        assertThat(sizeByType).containsEntry(PdfName.IMAGE, 733935);
    }

    @Test
    public void getStreamSizeByTypeJpeg() throws Exception {
        setupReader(EXAMPLE_PDF_WITH_JPEG_IMAGE);

        Map<PdfName, Integer> sizeByType = helper.getStreamSizeByType(getReader());

        assertThat(sizeByType).containsEntry(null, 36127);
        assertThat(sizeByType).containsEntry(PdfName.IMAGE, 64562);
    }

}
