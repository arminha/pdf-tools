/*
 * Copyright (C) 2014  Armin Häberling
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.aha.pdftools;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;

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
        assertThat(streamInfo, containsString(STREAM_15));

        streamInfo = helper.dumpStreamInfo(getReader(), PdfName.XOBJECT, null);
        assertThat(streamInfo, containsString(STREAM_15));

        streamInfo = helper.dumpStreamInfo(getReader(), PdfName.XOBJECT, PdfName.IMAGE);
        assertThat(streamInfo, containsString(STREAM_15));

        streamInfo = helper.dumpStreamInfo(getReader(), PdfName.XOBJECT, PdfName.FILEATTACHMENT);
        assertThat(streamInfo, not(containsString(STREAM_15)));
    }

    @Test
    public void getStreamSizeByType() throws Exception {
        setupReader(EXAMPLE_PDF_WITH_PNG_IMAGE);

        Map<PdfName, Integer> sizeByType = helper.getStreamSizeByType(getReader());

        assertThat(sizeByType, hasEntry(null, 38930));
        assertThat(sizeByType, hasEntry(PdfName.METADATA, 3058));
        assertThat(sizeByType, hasEntry(PdfName.IMAGE, 733935));
    }

    @Test
    public void getStreamSizeByTypeEmbeddedOdt() throws Exception {
        setupReader(EXAMPLE_PDF_WITH_EMBEDDED_ODT);

        Map<PdfName, Integer> sizeByType = helper.getStreamSizeByType(getReader());

        assertThat(sizeByType, hasEntry(null, 570325));
        assertThat(sizeByType, hasEntry(PdfName.METADATA, 3058));
        assertThat(sizeByType, hasEntry(PdfName.IMAGE, 733935));
    }

    @Test
    public void getStreamSizeByTypeJpeg() throws Exception {
        setupReader(EXAMPLE_PDF_WITH_JPEG_IMAGE);

        Map<PdfName, Integer> sizeByType = helper.getStreamSizeByType(getReader());

        assertThat(sizeByType, hasEntry(null, 36127));
        assertThat(sizeByType, hasEntry(PdfName.IMAGE, 64562));
    }

}
