package com.aha.pdftools;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfObject;

public class IsPdfNumber extends TypeSafeMatcher<PdfObject> {

    public static Matcher<PdfObject> isPdfNumber(int value) {
        return allOf(instanceOf(PdfNumber.class), new IsPdfNumber(value));
    }

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
