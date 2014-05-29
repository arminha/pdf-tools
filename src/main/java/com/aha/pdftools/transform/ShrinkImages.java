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

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfStream;
import com.itextpdf.text.pdf.parser.PdfImageObject;

public class ShrinkImages extends PdfTransformation {

    private static final int BITS_PER_COMPONENT = 8;

    private double scaleFactor = 1.0;

    public double getScaleFactor() {
        return scaleFactor;
    }

    public void setScaleFactor(double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    @Override
    protected void transformStream(PdfStream stream) throws IOException {
        if (isImage(stream)) {
            shrinkImage((PRStream) stream);
        }
    }

    private void shrinkImage(PRStream stream) throws IOException {
        PdfObject colorSpace = stream.get(PdfName.COLORSPACE);

        PdfImageObject image = new PdfImageObject(stream);
        BufferedImage origImg = image.getBufferedImage();
        BufferedImage img = resize(origImg);

        byte[] imgData = writeAsJpeg(img);

        replaceImage(stream, imgData, img.getWidth(), img.getHeight(), colorSpace);
    }

    private void replaceImage(PRStream stream, byte[] imgData, int width, int height, PdfObject colorSpace) {
        stream.clear();
        stream.setData(imgData);
        stream.put(PdfName.TYPE, PdfName.XOBJECT);
        stream.put(PdfName.TYPE, PdfName.XOBJECT);
        stream.put(PdfName.SUBTYPE, PdfName.IMAGE);
        stream.put(PdfName.FILTER, PdfName.DCTDECODE);
        stream.put(PdfName.WIDTH, new PdfNumber(width));
        stream.put(PdfName.HEIGHT, new PdfNumber(height));
        stream.put(PdfName.BITSPERCOMPONENT, new PdfNumber(BITS_PER_COMPONENT));
        stream.put(PdfName.COLORSPACE, colorSpace);
    }

    private byte[] writeAsJpeg(BufferedImage img) throws IOException {
        ByteArrayOutputStream imgBytes = new ByteArrayOutputStream();
        ImageIO.write(img, "JPG", imgBytes);
        return imgBytes.toByteArray();
    }

    private BufferedImage resize(BufferedImage origImg) {
        int width = (int) (origImg.getWidth() * scaleFactor);
        int height = (int) (origImg.getHeight() * scaleFactor);
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        AffineTransform at = AffineTransform.getScaleInstance(scaleFactor, scaleFactor);
        g.drawRenderedImage(origImg, at);
        return img;
    }

    private boolean isImage(PdfStream stream) {
        return PdfName.IMAGE.equals(stream.get(PdfName.SUBTYPE));
    }
}
