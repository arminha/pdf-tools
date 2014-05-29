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

import java.io.IOException;
import java.io.InputStream;

import org.junit.After;

import com.itextpdf.text.pdf.PdfReader;

public class PdfReaderTestBase {

    public static final String EXAMPLE_PDF_WITH_PNG_IMAGE = "image_example.pdf";

    private InputStream pdfIn;
    private PdfReader reader;

    @After
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
