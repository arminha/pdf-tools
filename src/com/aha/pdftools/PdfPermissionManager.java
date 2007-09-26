package com.aha.pdftools;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;

import com.lowagie.text.pdf.PdfEncryption;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

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
            PdfPermissions permissions) {
        try {
            Class readerClass = reader.getClass();
            Field pwField = readerClass.getDeclaredField("ownerPasswordUsed");
            pwField.setAccessible(true);
            pwField.set(reader, new Boolean(true));
        } catch (Exception e) {
            // TODO hack failed
            e.printStackTrace();
        }

        try {
            PdfStamper stp = new PdfStamper(reader, os, '\0');
            int perms = permissions.getPermissions();
            stp.setEncryption(null, "changeit".getBytes(), perms,
                    PdfEncryption.STANDARD_ENCRYPTION_40);
            stp.close();
        } catch (Exception e) {
            // TODO error occured
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            PdfPermissionManager pm = new PdfPermissionManager();
            pm.changePermissions(new PdfReader(args[0]), new FileOutputStream(
                    args[1]), new PdfPermissions());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
