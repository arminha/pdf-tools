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
