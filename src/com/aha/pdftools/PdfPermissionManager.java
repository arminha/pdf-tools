package com.aha.pdftools;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfEncryption;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

public class PdfPermissionManager {

    public PdfPermissionManager() {
    }

    public PdfPermissions getPdfPermissions(PdfReader reader) {
        if (reader.isEncrypted()) {
            return new PdfPermissions(reader.getPermissions());
        }
        return new PdfPermissions();
    }

    public void changePermissions(PdfReader reader, OutputStream os,
            PdfPermissions permissions) throws DocumentException, IOException {
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
        stp.setEncryption(null, "changeit".getBytes(), perms,
                PdfEncryption.STANDARD_ENCRYPTION_40);
        stp.close();
    }

    public static void main(String[] args) {
        try {
            PdfPermissionManager pm = new PdfPermissionManager();
            pm.changePermissions(new PdfReader(args[0]), new FileOutputStream(
                    args[1]), new PdfPermissions());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
