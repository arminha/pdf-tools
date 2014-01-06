/*
 * Copyright (C) 2012  Armin Häberling
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

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileUtils {

	public static void deleteFile(File file) {
		if (!file.delete()) {
			file.deleteOnExit();
		}
	}

	public static void copyFile(File sourceFile, File destFile) throws IOException {
		if (!destFile.exists() && !destFile.createNewFile()) {
			Logger.getLogger(FileUtils.class.getName()).log(Level.WARNING, "Could not create file " + destFile.getAbsolutePath());
		}

		FileInputStream sourceStream = null;
		FileOutputStream destinationStream = null;
		try {
			sourceStream = new FileInputStream(sourceFile);
			FileChannel source = sourceStream.getChannel();
			destinationStream = new FileOutputStream(destFile);
			FileChannel destination = destinationStream.getChannel();
			long amount = destination.transferFrom(source, 0, source.size());
			if (amount != source.size()) {
				// XXX transfer rest of file
				Logger.getLogger(FileUtils.class.getName()).log(Level.WARNING, "Could not transfer whole file " + sourceFile.getAbsolutePath());
			}
		} finally {
			if (sourceStream != null) {
				sourceStream.close();
			}
			if(destinationStream != null) {
				destinationStream.close();
			}
		}
	}

	public static void moveFile(File source, File target) throws IOException {
		if (!source.renameTo(target)) {
			// copy and delete file
			copyFile(source, target);
			deleteFile(source);
		}
	}

	public static List<File> listFiles(File dir, boolean recursive, FileFilter filter) {
		ArrayList<File> files = new ArrayList<File>();
		if (dir.isDirectory()) {
			Queue<File> dirsToProcess = new ArrayDeque<File>();
			Set<String> processedDirs = new HashSet<String>();
			dirsToProcess.add(dir);
			while (!dirsToProcess.isEmpty()) {
				File currentDir = dirsToProcess.remove();
				for (File file : currentDir.listFiles(filter)) {
					if (file.isDirectory()) {
						if (recursive) {
							try {
								String path = file.getCanonicalPath();
								if (!processedDirs.contains(path)) {
									dirsToProcess.add(file);
									processedDirs.add(path);
								}
							} catch (IOException e) {
								Logger.getLogger(FileUtils.class.getName()).log(Level.WARNING, e.getMessage(), e);
							}
						}
					} else {
						files.add(file);
					}
				}
			}
		}
		return files;
	}

}
