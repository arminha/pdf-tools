package com.aha.pdftools.gui;

import org.jspecify.annotations.Nullable;

import java.io.File;

public interface FileSelection {
    @Nullable File chooseSaveFile(@Nullable String initialName, boolean addExtension);

    boolean checkOverwriteFile(File f);
}
