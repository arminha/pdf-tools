package com.aha.pdftools;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import org.jdesktop.layout.GroupLayout;

import com.lowagie.text.pdf.PdfReader;

@SuppressWarnings("serial")
public class PdfPermissionManagerGui extends JFrame {

    // TODO swing gui

    JButton openButton; // Open..
    JButton saveButton; // Save as..
    JButton exitButton; // Exit / Quit
    PermPanel permPanel;
    JTextField openFileLabel;

    private String currentPdf;
    private PdfPermissionManager permManager = new PdfPermissionManager();

    public PdfPermissionManagerGui() {
        super("Pdf Permission Manager");

        Container content = getContentPane();

        // create widgets
        openButton = new JButton("Open..");
        openButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileFilter(new PdfFileFilter());
                int returnVal = chooser
                        .showOpenDialog(PdfPermissionManagerGui.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File f = chooser.getSelectedFile();
                    if (f.isFile() && f.canRead()) {
                        // TODO do this in a special thread
                        try {
                            PdfReader reader = new PdfReader(f
                                    .getAbsolutePath());
                            PdfPermissions perms = permManager
                                    .getPdfPermissions(reader);

                            permPanel.setPermissions(perms);
                            permPanel.setEnabled(true);
                            saveButton.setEnabled(true);
                            currentPdf = f.getAbsolutePath();
                            openFileLabel.setText(currentPdf);
                        } catch (IOException ioe) {
                            // TODO display error
                            ioe.printStackTrace();
                        }
                    } else {
                        // TODO display error
                    }
                }
            }
        });

        saveButton = new JButton("Save As..");
        saveButton.setEnabled(false);
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileFilter(new PdfFileFilter());
                int returnVal = chooser
                        .showSaveDialog(PdfPermissionManagerGui.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    String filename = chooser.getSelectedFile()
                            .getAbsolutePath();
                    if (!filename.endsWith(".pdf")) {
                        filename += ".pdf";
                    }
                    File f = new File(filename);
                    if (f.exists()) {
                        // TODO error
                        return;
                    }
                    try {
                        PdfReader reader = new PdfReader(currentPdf);
                        FileOutputStream fout = new FileOutputStream(f);
                        permManager.changePermissions(reader, fout, permPanel
                                .getPermissions());
                    } catch (IOException ioe) {
                        // TODO error
                        ioe.printStackTrace();
                    }
                }
            }
        });

        exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        permPanel = new PermPanel();
        permPanel.setEnabled(false);
        openFileLabel = new JTextField("No file opened");
        openFileLabel.setEditable(false);

        // layout
        final GroupLayout layout = new GroupLayout(content);
        layout.setAutocreateGaps(true);
        layout.setAutocreateContainerGaps(true);
        content.setLayout(layout);

        content.add(openButton);
        content.add(saveButton);
        content.add(exitButton);
        content.add(permPanel);
        content.add(openFileLabel);

        layout.linkSize(new Component[] { openButton, saveButton, exitButton });
        layout.linkSize(new Component[] { openButton, openFileLabel },
                GroupLayout.VERTICAL);

        // horizontal group
        final GroupLayout.SequentialGroup horizontal = layout
                .createSequentialGroup();
        horizontal.add(layout.createParallelGroup().add(openFileLabel, 100,
                GroupLayout.PREFERRED_SIZE, 4000).add(permPanel,
                GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
                GroupLayout.PREFERRED_SIZE));
        horizontal.add(layout.createParallelGroup().add(openButton).add(
                saveButton).add(exitButton));
        layout.setHorizontalGroup(horizontal);

        // vertical group
        final GroupLayout.SequentialGroup vertical = layout
                .createSequentialGroup();
        vertical.add(layout.createParallelGroup().add(openFileLabel).add(
                openButton));
        vertical.add(layout.createParallelGroup(GroupLayout.TRAILING).add(
                layout.createSequentialGroup().add(permPanel).add(0, 0, 4000))
                .add(
                        layout.createSequentialGroup().add(saveButton).add(
                                exitButton)));
        layout.setVerticalGroup(vertical);
    }

    class PermPanel extends JPanel {

        JCheckBox assemblyBox;
        JCheckBox copyBox;
        JCheckBox degradedPrintingBox;
        JCheckBox printingBox;
        JCheckBox screenReadersBox;
        JCheckBox fillInBox;
        JCheckBox modifyContentsBox;
        JCheckBox modifyAnnotationsBox;

        public PermPanel() {
            assemblyBox = new JCheckBox("Assembly");
            copyBox = new JCheckBox("Copy");
            degradedPrintingBox = new JCheckBox("Degraded Printing");
            printingBox = new JCheckBox("Printing");
            screenReadersBox = new JCheckBox("Screenreader");
            fillInBox = new JCheckBox("Fill In");
            modifyContentsBox = new JCheckBox("Modify Contents");
            modifyAnnotationsBox = new JCheckBox("Modify Annotations");

            // layout
            final GroupLayout layout = new GroupLayout(this);
            layout.setAutocreateGaps(true);
            this.setLayout(layout);

            add(assemblyBox);
            add(copyBox);
            add(degradedPrintingBox);
            add(printingBox);
            add(screenReadersBox);
            add(fillInBox);
            add(modifyContentsBox);
            add(modifyAnnotationsBox);

            layout.linkSize(new Component[] { assemblyBox, copyBox,
                    degradedPrintingBox, printingBox, screenReadersBox,
                    fillInBox, modifyContentsBox, modifyAnnotationsBox });

            // horizontal group
            final GroupLayout.ParallelGroup horizontal = layout
                    .createParallelGroup();
            horizontal.add(assemblyBox).add(copyBox).add(degradedPrintingBox)
                    .add(printingBox).add(screenReadersBox).add(fillInBox).add(
                            modifyContentsBox).add(modifyAnnotationsBox);
            layout.setHorizontalGroup(horizontal);

            // vertical group
            final GroupLayout.SequentialGroup vertical = layout
                    .createSequentialGroup();
            vertical.add(assemblyBox).add(copyBox).add(degradedPrintingBox)
                    .add(printingBox).add(screenReadersBox).add(fillInBox).add(
                            modifyContentsBox).add(modifyAnnotationsBox);
            layout.setVerticalGroup(vertical);
        }

        public void setPermissions(PdfPermissions perms) {
            assemblyBox.setSelected(perms.allowAssembly);
            copyBox.setSelected(perms.allowCopy);
            degradedPrintingBox.setSelected(perms.allowDegradedPrinting);
            printingBox.setSelected(perms.allowPrinting);
            screenReadersBox.setSelected(perms.allowScreenReaders);
            fillInBox.setSelected(perms.allowFillIn);
            modifyContentsBox.setSelected(perms.allowModifyContents);
            modifyAnnotationsBox.setSelected(perms.allowModifyAnnotations);
        }

        public PdfPermissions getPermissions() {
            PdfPermissions perms = new PdfPermissions();
            perms.allowAssembly = assemblyBox.isSelected();
            perms.allowCopy = copyBox.isSelected();
            perms.allowDegradedPrinting = degradedPrintingBox.isSelected();
            perms.allowFillIn = fillInBox.isSelected();
            perms.allowModifyAnnotations = modifyAnnotationsBox.isSelected();
            perms.allowModifyContents = modifyContentsBox.isSelected();
            perms.allowPrinting = printingBox.isSelected();
            perms.allowScreenReaders = screenReadersBox.isSelected();
            return perms;
        }

        @Override
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            assemblyBox.setEnabled(enabled);
            copyBox.setEnabled(enabled);
            degradedPrintingBox.setEnabled(enabled);
            printingBox.setEnabled(enabled);
            screenReadersBox.setEnabled(enabled);
            fillInBox.setEnabled(enabled);
            modifyContentsBox.setEnabled(enabled);
            modifyAnnotationsBox.setEnabled(enabled);
        }

    }

    class PdfFileFilter extends FileFilter {

        public PdfFileFilter() {
        }

        @Override
        public boolean accept(File f) {
            if (f.isFile()) {
                return (f.getName().endsWith(".pdf"));
            }
            return f.isDirectory();
        }

        @Override
        public String getDescription() {
            return "Pdf Files (.pdf)";
        }

    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        PdfPermissionManagerGui gui = new PdfPermissionManagerGui();
        gui.pack();
        Dimension s = gui.getPreferredSize();
        s.width += 50;
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setSize(s);
        gui.setVisible(true);
    }
}
