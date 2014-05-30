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

import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStream;

/**
 * Removes Metadata from PDFs.
 */
public class RemoveMetadata extends PdfTransformation {

    @Override
    public void transform(PdfReader reader) throws IOException {
        transformTrailer(reader.getTrailer());
        super.transform(reader);
    }

    @Override
    protected void transformDictionary(PdfDictionary dict) {
        removeMetaData(dict);
    }

    @Override
    protected void transformStream(PdfStream stream) throws IOException {
        removeMetaData(stream);
    }

    @Override
    protected void postTransform(PdfReader reader) {
        reader.removeUnusedObjects();
    }

    private void transformTrailer(PdfDictionary trailer) {
        trailer.remove(new PdfName("AdditionalStreams"));
        trailer.remove(new PdfName("DocChecksum"));
    }

    private void removeMetaData(PdfDictionary dict) {
        dict.remove(PdfName.METADATA);
        if (dict.checkType(PdfName.PAGE)) {
            PdfDictionary resources = dict.getAsDict(PdfName.RESOURCES);
            if (resources != null) {
                // meta-data
                resources.remove(PdfName.PROPERTIES);
            }
        }
    }

}
