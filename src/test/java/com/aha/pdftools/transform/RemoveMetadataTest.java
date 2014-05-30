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

package com.aha.pdftools.transform;

import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;

import org.junit.Before;
import org.junit.Test;

import com.aha.pdftools.PdfReaderTestBase;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

public class RemoveMetadataTest extends PdfReaderTestBase {

    private RemoveMetadata transformation;

    @Before
    public void setup() throws Exception {
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
