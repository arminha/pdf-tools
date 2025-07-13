package com.aha.pdftools.gui;

import java.io.File;

public interface FileSelection {
    File chooseSaveFile(String initialName, boolean addExtension);

    boolean checkOverwriteFile(File f);
}
