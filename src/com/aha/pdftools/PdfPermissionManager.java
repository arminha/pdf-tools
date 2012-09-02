package com.aha.pdftools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.List;

import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.util.encoders.Base64;

import com.aha.pdftools.model.PdfPermissions;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfEncryption;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

public class PdfPermissionManager {

	public static final String PDF_EXTENSION = ".pdf";
	public static final String PASSWORD = "changeit";

	private PdfPermissionManager() {
	}

	public static int getPermissions(File inputFile) throws IOException {
		PdfReader reader = new PdfReader(inputFile.getAbsolutePath());
		int permissions;
		if (!reader.isEncrypted()) {
			permissions = 0xFFFFFFFF;
		} else {
			permissions = reader.getPermissions();
		}
		reader.close();
		return permissions;
	}

	private static void changePermissions(PdfReader reader, OutputStream os,
			PdfPermissions permissions, String password)
					throws DocumentException, IOException {
		try {
			Class<? extends PdfReader> readerClass = reader.getClass();
			Field pwField = readerClass.getDeclaredField("ownerPasswordUsed");
			pwField.setAccessible(true);
			pwField.set(reader, new Boolean(true));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		PdfStamper stp = new PdfStamper(reader, os, '\0');
		int perms = permissions.getPermissions();
		stp.setEncryption(null, password.getBytes(), perms,
				PdfEncryption.STANDARD_ENCRYPTION_40);
		stp.close();
	}

	public static void processFile(File inputFile, File output, PdfPermissions permissions, String password)
			throws IOException, DocumentException {
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
		progress.startTask("Combine", inputFiles.size(), true);
		FileOutputStream outputStream = null;
		PdfCopy copy = null;
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
					for (int i = 1; i <= reader.getNumberOfPages(); i++) {
						if (progress.isCanceled()) {
							break;
						}
						document.newPage();
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
			if (copy != null) {
				copy.close();
			}
			if (outputStream != null) {
				outputStream.close();
			}
			progress.endTask();
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
		return new String(Base64.encode(output));
	}

	public static void main(String[] args) {
		try {
			File input = new File(args[0]);
			File output = new File(args[1]);
			processFile(input, output, new PdfPermissions(), PASSWORD);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

}
