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

import java.io.IOException;
import java.util.Objects;

import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStream;

public abstract class PdfTransformation {

    public void transform(PdfReader reader) throws IOException {
        Objects.requireNonNull(reader);
        PdfObject obj;
        for (int i = 1; i < reader.getXrefSize(); i++) {
            obj = reader.getPdfObject(i);
            if (obj == null) {
                continue;
            }
            if (obj.isDictionary()) {
                transformDictionary((PdfDictionary) obj);
            }
            if (obj.isStream()) {
                transformStream((PdfStream) obj);
            }
        }
        postTransform(reader);
    }

    protected void transformDictionary(PdfDictionary dict) {
    }

    protected void transformStream(PdfStream stream) throws IOException {
    }

    protected void postTransform(PdfReader reader) {
    }

}
