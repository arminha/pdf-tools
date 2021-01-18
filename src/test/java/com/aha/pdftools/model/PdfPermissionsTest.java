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

package com.aha.pdftools.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.itextpdf.text.pdf.PdfWriter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class PdfPermissionsTest {

    private static final int ALL_PERMISSIONS = 0xfffffffc;
    private static final int NO_PERMISSIONS = 0xfffff0c0;
    private static final String ASSEMBLY = "assembly";
    private static final String COPY = "copy";
    private static final String PRINT = "printing";
    private static final String FILL_IN = "fillIn";
    private static final String DEGRADED_PRINT = "degradedPrinting";
    private static final String ANNOTATION = "modifyAnnotation";
    private static final String CONTENT = "modifyContents";
    private static final String SCREEN = "screenReaders";

    @Test
    public void checkInitialPermissionValues() {
        PdfPermissions perm = new PdfPermissions();
        assertTrue(perm.isAssembly());
        assertTrue(perm.isCopy());
        assertTrue(perm.isDegradedPrinting());
        assertTrue(perm.isFillIn());
        assertTrue(perm.isModifyAnnotations());
        assertTrue(perm.isModifyContents());
        assertTrue(perm.isPrinting());
        assertTrue(perm.isScreenReaders());
        assertEquals(ALL_PERMISSIONS, perm.getPermissionFlags());
    }

    @ParameterizedTest
    @MethodSource("permissionValues")
    public void permissions(int pdfPerm, Set<String> permissions) {
        PdfPermissions perm = new PdfPermissions(pdfPerm);
        assertPermission(perm.isAssembly(), ASSEMBLY, permissions);
        assertPermission(perm.isCopy(), COPY, permissions);
        assertPermission(perm.isDegradedPrinting(), DEGRADED_PRINT, permissions);
        assertPermission(perm.isPrinting(), PRINT, permissions);
        assertPermission(perm.isFillIn(), FILL_IN, permissions);
        assertPermission(perm.isModifyAnnotations(), ANNOTATION, permissions);
        assertPermission(perm.isModifyContents(), CONTENT, permissions);
        assertPermission(perm.isScreenReaders(), SCREEN, permissions);
        assertEquals(pdfPerm, perm.getPermissionFlags());
    }

    private void assertPermission(boolean actual, String name, Set<String> permissions) {
        assertEquals(permissions.contains(name), actual, name);
    }

    private static Set<String> $$(String... permissions) {
        Set<String> set = new HashSet<>();
        Collections.addAll(set, permissions);
        return set;
    }

    private static Object[] $(Object... elements) {
        return elements;
    }

    static Object[] permissionValues() {
        return $(
                $(ALL_PERMISSIONS, $$(ASSEMBLY, COPY, PRINT, DEGRADED_PRINT, FILL_IN, ANNOTATION, CONTENT, SCREEN)),
                $((ALL_PERMISSIONS ^ PdfWriter.ALLOW_PRINTING) & ALL_PERMISSIONS,
                        $$(ASSEMBLY, COPY, FILL_IN, ANNOTATION, CONTENT, SCREEN)),
                $(NO_PERMISSIONS | PdfWriter.ALLOW_ASSEMBLY, $$(ASSEMBLY)),
                $(NO_PERMISSIONS | PdfWriter.ALLOW_COPY, $$(COPY)),
                $(NO_PERMISSIONS | PdfWriter.ALLOW_DEGRADED_PRINTING, $$(DEGRADED_PRINT)),
                $(NO_PERMISSIONS | PdfWriter.ALLOW_FILL_IN, $$(FILL_IN)),
                $(NO_PERMISSIONS | PdfWriter.ALLOW_MODIFY_ANNOTATIONS, $$(ANNOTATION)),
                $(NO_PERMISSIONS | PdfWriter.ALLOW_MODIFY_CONTENTS, $$(CONTENT)),
                $(NO_PERMISSIONS | PdfWriter.ALLOW_PRINTING, $$(PRINT, DEGRADED_PRINT)),
                $(NO_PERMISSIONS | PdfWriter.ALLOW_SCREENREADERS, $$(SCREEN)));
    }
}
