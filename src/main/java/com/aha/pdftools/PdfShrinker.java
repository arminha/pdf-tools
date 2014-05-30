package com.aha.pdftools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.aha.pdftools.transform.PdfTransformation;
import com.aha.pdftools.transform.ShrinkImages;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

public class PdfShrinker {

    private final List<PdfTransformation> transformations;

    public PdfShrinker() {
        transformations = new ArrayList<>();
        transformations.add(new ShrinkImages());
    }

    public void process(File input, File output) throws IOException, DocumentException {
        boolean createTempFile = input.equals(output);
        File outputFile = createTempFile ? FileUtils.createTempFile(output.getAbsolutePath(),
                PdfPermissionManager.PDF_EXTENSION) : output;
        PdfReader reader = new PdfReader(input.getAbsolutePath());
        PdfPermissionManager.unlockReader(reader);

        applyTransformations(reader);

        FileOutputStream fout = new FileOutputStream(outputFile);
        // set at least version 1.5 to use full compression
        char version = reader.getPdfVersion() < PdfWriter.VERSION_1_5 ? PdfWriter.VERSION_1_5 : '\0';
        PdfStamper stp = new PdfStamper(reader, fout, version);
        stp.setFullCompression();
        stp.close();
        if (createTempFile) {
            FileUtils.moveFile(outputFile, output);
        }
    }

    private void applyTransformations(PdfReader reader) throws IOException {
        for (PdfTransformation transformation : transformations) {
            transformation.transform(reader);
        }
    }
}
