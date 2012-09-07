package com.aha.pdftools.gui;

import java.io.File;

public interface FileSelection {
	File chooseSaveFile(String initalName, boolean addExtension);
	boolean checkOverwriteFile(File f);
}
