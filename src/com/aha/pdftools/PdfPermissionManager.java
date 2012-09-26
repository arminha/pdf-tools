package com.aha.pdftools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.util.encoders.Hex;

import com.aha.pdftools.model.PdfFile;
import com.aha.pdftools.model.PdfPages;
import com.aha.pdftools.model.PdfPermissions;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfEncryption;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

public class PdfPermissionManager {

	public static final String PDF_EXTENSION = ".pdf"; //$NON-NLS-1$
	public static final String PASSWORD = "changeit"; //$NON-NLS-1$

	private PdfPermissionManager() {
	}

	public static int getPermissions(PdfReader reader) {
		int permissions;
		if (!reader.isEncrypted()) {
			permissions = 0xFFFFFFFF;
		} else {
			permissions = reader.getPermissions();
		}
		return permissions;
	}

	private static void changePermissions(PdfReader reader, OutputStream os,
			PdfPermissions permissions, String password)
					throws DocumentException, IOException {
		unlockReader(reader);
		PdfStamper stp = new PdfStamper(reader, os, '\0');
		int perms = permissions.getPermissions();
		stp.setEncryption(null, password.getBytes(), perms,
				PdfEncryption.STANDARD_ENCRYPTION_40);
		stp.close();
	}

	private static void unlockReader(PdfReader reader) {
		try {
			Class<? extends PdfReader> readerClass = reader.getClass();
			Field pwField = readerClass.getDeclaredField("ownerPasswordUsed"); //$NON-NLS-1$
			pwField.setAccessible(true);
			pwField.set(reader, new Boolean(true));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void processFile(File inputFile, File output, PdfPermissions permissions, String password)
			throws IOException, DocumentException {
		if (permissions instanceof PdfFile && ((PdfFile)permissions).isAllowAll()) {
			ArrayList<File> inputFiles = new ArrayList<File>();
			inputFiles.add(inputFile);
			merge(output, inputFiles, new NullProgressDisplay());
			return;
		}
		boolean createTempFile = inputFile.equals(output);
		File outputFile = createTempFile ? createTempFile(output.getAbsolutePath()) : output;
		PdfReader reader = new PdfReader(inputFile.getAbsolutePath());
		FileOutputStream fout = new FileOutputStream(outputFile);
		changePermissions(reader, fout, permissions, password);
		if (createTempFile) {
			FileUtils.moveFile(outputFile, output);
		}
	}

	public static void merge(File output, List<File> inputFiles, ProgressDisplay progress) throws IOException, DocumentException {
		progress.startTask(Messages.getString("PdfPermissionManager.Combine"), inputFiles.size(), true); //$NON-NLS-1$
		FileOutputStream outputStream = null;
		PdfCopy copy = null;
		int pageCount = 0;
		try {
			Document document = new Document();
			outputStream = new FileOutputStream(output);
			copy = new PdfCopy(document, outputStream);
			document.open();

			int n = 0;
			for (File file : inputFiles) {
				if (progress.isCanceled()) {
					break;
				}
				progress.setNote(file.getName());
				FileInputStream inputStream = null;
				PdfReader reader = null;
				try {
					inputStream = new FileInputStream(file);
					reader = new PdfReader(inputStream);
					unlockReader(reader);
					for (int i = 1; i <= reader.getNumberOfPages(); i++) {
						if (progress.isCanceled()) {
							break;
						}
						document.newPage();
						pageCount++;
						// import the page from source pdf
						PdfImportedPage page = copy.getImportedPage(reader, i);
						copy.addPage(page);
					}
				} finally {
					if (reader != null) {
						reader.close();
					}
					if (inputStream != null) {
						inputStream.close();
					}
					progress.setProgress(++n);
				}
			}
			outputStream.flush();
			document.close();
		} finally {
			progress.endTask();
			// check if any page was written.
			if (copy != null && pageCount > 0) {
				copy.close();
			}
			if (outputStream != null) {
				outputStream.close();
			}
		}
	}

	public static void mergePages(File output, List<PdfPages> pages, ProgressDisplay progress) throws IOException, DocumentException {
		int totalPageCount = 0;
		for (PdfPages pdfPages : pages) {
			totalPageCount += pdfPages.getPageCount();
		}

		progress.startTask(Messages.getString("PdfPermissionManager.Combine"), totalPageCount, true); //$NON-NLS-1$
		FileOutputStream outputStream = null;
		PdfCopy copy = null;
		int pageCount = 0;
		try {
			Document document = new Document();
			outputStream = new FileOutputStream(output);
			copy = new PdfCopy(document, outputStream);
			document.open();
			for (PdfPages pdfPages : pages) {
				if (progress.isCanceled()) {
					break;
				}
				progress.setNote(pdfPages.getName());
				FileInputStream inputStream = null;
				PdfReader reader = null;
				try {
					inputStream = new FileInputStream(pdfPages.getSourceFile());
					reader = new PdfReader(inputStream);
					unlockReader(reader);
					int[] pageIndices = pdfPages.getPages();
					for (int i = 0; i < pageIndices.length; i++) {
						if (progress.isCanceled()) {
							break;
						}
						document.newPage();
						pageCount++;
						// import the page from source pdf
						PdfImportedPage page = copy.getImportedPage(reader, pageIndices[i]);
						copy.addPage(page);
						progress.setProgress(pageCount);
					}
				} finally {
					if (reader != null) {
						reader.close();
					}
					if (inputStream != null) {
						inputStream.close();
					}
				}
			}
			outputStream.flush();
			document.close();
		} finally {
			progress.endTask();
			// check if any page was written.
			if (copy != null && pageCount > 0) {
				copy.close();
			}
			if (outputStream != null) {
				outputStream.close();
			}
		}
	}

	private static File createTempFile(String name) throws IOException {
		return File.createTempFile(hash(name), PDF_EXTENSION);
	}

	private static String hash(String name) {
		MD5Digest digest = new MD5Digest();
		byte[] output = new byte[digest.getDigestSize()];
		byte[] input = name.getBytes();
		digest.update(input, 0, input.length);
		digest.doFinal(output, 0);
		return new String(Hex.encode(output));
	}
}
