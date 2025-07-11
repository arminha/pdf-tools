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
    protected void transformStream(PdfStream stream) {
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
