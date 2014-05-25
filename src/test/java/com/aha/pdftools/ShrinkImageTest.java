package com.aha.pdftools;

import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import org.junit.Test;

import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.parser.PdfImageObject;
import com.itextpdf.text.pdf.parser.PdfImageObject.ImageBytesType;

public class ShrinkImageTest extends PdfReaderTestBase {

    @Test
    public void shrinkImage() throws Exception {
        PdfReader reader = setupReader(EXAMPLE_PDF_WITH_PNG_IMAGE);
        int n = reader.getXrefSize();
        PdfObject object;
        PRStream stream;
        for (int i = 0; i < n; i++) {
            object = reader.getPdfObject(i);
            if (object == null || !object.isStream()) {
                continue;
            }
            stream = (PRStream) object;
            if (stream.checkType(PdfName.XOBJECT) && PdfName.IMAGE.equals(stream.get(PdfName.SUBTYPE))) {
                PdfImageObject image = new PdfImageObject(stream);
                ImageBytesType type = image.getImageBytesType();
                byte[] data = image.getImageAsBytes();
                BufferedImage bufferedImage = image.getBufferedImage();
                System.out.println(type);
                System.out.println(data.length);
                System.out.println(bufferedImage);

                double factor = 0.5;
                int width = (int) (bufferedImage.getWidth() * factor);
                int height = (int) (bufferedImage.getHeight() * factor);
                BufferedImage img = new BufferedImage(width, height, bufferedImage.getType());
                Graphics2D g = img.createGraphics();
                AffineTransform at = AffineTransform.getScaleInstance(factor, factor);
                g.drawRenderedImage(bufferedImage, at);
                ByteArrayOutputStream imgBytes = new ByteArrayOutputStream();
                ImageIO.write(img, "JPG", imgBytes);
                System.out.println(imgBytes.size());

                stream.clear();
                stream.setData(imgBytes.toByteArray(), false, PRStream.NO_COMPRESSION);
                stream.put(PdfName.TYPE, PdfName.XOBJECT);
                stream.put(PdfName.SUBTYPE, PdfName.IMAGE);
                stream.put(PdfName.FILTER, PdfName.DCTDECODE);
                stream.put(PdfName.WIDTH, new PdfNumber(width));
                stream.put(PdfName.HEIGHT, new PdfNumber(height));
                stream.put(PdfName.BITSPERCOMPONENT, new PdfNumber(8));
                stream.put(PdfName.COLORSPACE, PdfName.DEVICERGB);
            }
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfStamper stamper = new PdfStamper(reader, outputStream);
        stamper.close();
        assertThat(outputStream.size(), lessThan(62000));
    }

}
