package com.aha.pdftools;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.InputStream;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;

public class PdfShrinkerTest {

    private static final String EXAMPLE_PDF_WITH_PNG_IMAGE = "image_example.pdf";
    private PdfShrinker shrinker;
    private InputStream pdfIn;
    private PdfReader reader;

    @Before
    public void setup() throws Exception {
        shrinker = new PdfShrinker();
    }

    @After
    public void tearDown() throws Exception {
        if (reader != null) {
            reader.close();
        }
        if (pdfIn != null) {
            pdfIn.close();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotShrinkNonImage() throws Exception {
        setupReader(EXAMPLE_PDF_WITH_PNG_IMAGE);
        PRStream stream = (PRStream) reader.getPdfObject(30);

        shrinker.shrinkImage(stream);
    }

    @Test
    public void shrinkPngImage() throws Exception {
        setupReader(EXAMPLE_PDF_WITH_PNG_IMAGE);
        PRStream stream = (PRStream) reader.getPdfObject(15);

        int originalLength = stream.getLength();
        shrinker.shrinkImage(stream);
        assertThat(stream.getLength(), lessThan(originalLength));
        assertThat(stream.getLength(), lessThan(40000));
        assertThat(stream.get(PdfName.BITSPERCOMPONENT), isPdfNumber(8));
        assertThat(stream.get(PdfName.COLORSPACE), is((PdfObject) PdfName.DEVICERGB));
        assertThat(stream.get(PdfName.WIDTH), isPdfNumber(512));
        assertThat(stream.get(PdfName.HEIGHT), isPdfNumber(512));
    }

    @Test
    public void shrinkAndResizePngImage() throws Exception {
        setupReader(EXAMPLE_PDF_WITH_PNG_IMAGE);
        PRStream stream = (PRStream) reader.getPdfObject(15);

        int originalLength = stream.getLength();
        shrinker.setScaleFactor(0.5);
        shrinker.shrinkImage(stream);
        assertThat(stream.getLength(), lessThan(originalLength));
        assertThat(stream.getLength(), lessThan(14000));
        assertThat(stream.get(PdfName.BITSPERCOMPONENT), isPdfNumber(8));
        assertThat(stream.get(PdfName.COLORSPACE), is((PdfObject) PdfName.DEVICERGB));
        assertThat(stream.get(PdfName.WIDTH), isPdfNumber(256));
        assertThat(stream.get(PdfName.HEIGHT), isPdfNumber(256));
    }

    private void setupReader(String example) throws IOException {
        pdfIn = getClass().getClassLoader().getResourceAsStream(example);
        reader = new PdfReader(pdfIn);
    }

    private static class IsPdfNumber extends TypeSafeMatcher<PdfObject> {

        private final int expected;

        public IsPdfNumber(int expected) {
            this.expected = expected;
        }

        @Override
        protected boolean matchesSafely(PdfObject item) {
            return item instanceof PdfNumber && ((PdfNumber) item).intValue() == expected;
        }

        @Override
        public void describeTo(Description description) {
            description.appendValue(new PdfNumber(expected));
        }
    }

    private static Matcher<PdfObject> isPdfNumber(int value) {
        return allOf(instanceOf(PdfNumber.class), new IsPdfNumber(value));
    }
}
