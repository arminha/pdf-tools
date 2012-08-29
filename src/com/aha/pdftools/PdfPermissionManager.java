package com.aha.pdftools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;

import com.aha.pdftools.model.PdfPermissions;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfEncryption;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

public class PdfPermissionManager {

    public PdfPermissionManager() {
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

    public void changePermissions(PdfReader reader, OutputStream os,
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
        PdfReader reader = new PdfReader(inputFile.getAbsolutePath());
        FileOutputStream fout = new FileOutputStream(output);
        PdfPermissionManager ppm = new PdfPermissionManager();
        ppm.changePermissions(reader, fout, permissions, password);
    }

    public static void main(String[] args) {
        try {
            PdfPermissionManager pm = new PdfPermissionManager();
            pm.changePermissions(new PdfReader(args[0]), new FileOutputStream(
                    args[1]), new PdfPermissions(), "changeit");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
