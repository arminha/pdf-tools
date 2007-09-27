package com.aha.pdftools;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import org.jdesktop.layout.GroupLayout;

import com.lowagie.text.pdf.PdfReader;

@SuppressWarnings("serial")//$NON-NLS-1$
public class PdfPermissionManagerGui extends JFrame {

    JButton openButton;
    JButton saveButton;
    JButton exitButton;
    PermPanel permPanel;
    JTextField openFileLabel;

    private String currentPdf;
    private PdfPermissionManager permManager = new PdfPermissionManager();

    public PdfPermissionManagerGui() {
        super(Messages.getString("PdfPermissionManagerGui.1")); //$NON-NLS-1$

        Container content = getContentPane();

        // create widgets
        openButton = new JButton(Messages
                .getString("PdfPermissionManagerGui.2")); //$NON-NLS-1$
        openButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileFilter(new PdfFileFilter());
                int returnVal = chooser
                        .showOpenDialog(PdfPermissionManagerGui.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File f = chooser.getSelectedFile();
                    PdfPermissionManagerGui.this.loadFile(f);
                }
            }
        });

        saveButton = new JButton(Messages
                .getString("PdfPermissionManagerGui.3")); //$NON-NLS-1$
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
                    if (!filename.endsWith(".pdf")) { //$NON-NLS-1$
                        filename += ".pdf"; //$NON-NLS-1$
                    }
                    File f = new File(filename);
                    if (f.exists()) {
                        // ask if the file should be overwritten
                        String msg = Messages
                                .getString("PdfPermissionManagerGui.26") + " \"" //$NON-NLS-1$ //$NON-NLS-2$
                                + f.getAbsolutePath()
                                + "\" " + Messages.getString("PdfPermissionManagerGui.34") + "?"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        int resultVal = JOptionPane
                                .showConfirmDialog(
                                        PdfPermissionManagerGui.this,
                                        msg,
                                        Messages
                                                .getString("PdfPermissionManagerGui.29"), JOptionPane.YES_NO_OPTION); //$NON-NLS-1$
                        if (resultVal == JOptionPane.NO_OPTION) {
                            return;
                        }
                    }
                    try {
                        PdfReader reader = new PdfReader(currentPdf);
                        FileOutputStream fout = new FileOutputStream(f);
                        permManager.changePermissions(reader, fout, permPanel
                                .getPermissions());
                    } catch (IOException ioe) {
                        String errMsg = Messages
                                .getString("PdfPermissionManagerGui.30") + " \"" //$NON-NLS-1$ //$NON-NLS-2$
                                + f.getAbsolutePath() + "\":"; //$NON-NLS-1$
                        JOptionPane
                                .showMessageDialog(
                                        PdfPermissionManagerGui.this,
                                        new String[] { errMsg, ioe.getMessage() },
                                        Messages
                                                .getString("PdfPermissionManagerGui.33"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
                    }
                }
            }
        });

        exitButton = new JButton(Messages
                .getString("PdfPermissionManagerGui.6")); //$NON-NLS-1$
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        permPanel = new PermPanel();
        permPanel.setEnabled(false);
        openFileLabel = new JTextField();
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

    protected boolean loadFile(File f) {
        if (f.isFile() && f.canRead()) {
            // TODO do this in a special thread
            try {
                PdfReader reader = new PdfReader(f.getAbsolutePath());
                PdfPermissions perms = permManager.getPdfPermissions(reader);

                permPanel.setPermissions(perms);
                permPanel.setEnabled(true);
                saveButton.setEnabled(true);
                currentPdf = f.getAbsolutePath();
                openFileLabel.setText(currentPdf);
                return true;
            } catch (IOException ioe) {
                String errMsg = Messages
                        .getString("PdfPermissionManagerGui.18") + " \"" //$NON-NLS-1$ //$NON-NLS-2$
                        + f.getAbsolutePath() + "\":"; //$NON-NLS-1$
                JOptionPane.showMessageDialog(PdfPermissionManagerGui.this,
                        new String[] { errMsg, ioe.getMessage() }, Messages
                                .getString("PdfPermissionManagerGui.21"), //$NON-NLS-1$
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } else {
            String errMsg = Messages.getString("PdfPermissionManagerGui.18") + " \"" //$NON-NLS-1$ //$NON-NLS-2$
                    + f.getAbsolutePath() + "\""; //$NON-NLS-1$
            JOptionPane.showMessageDialog(PdfPermissionManagerGui.this, errMsg,
                    Messages.getString("PdfPermissionManagerGui.21"), //$NON-NLS-1$
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
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

            this.setBorder(BorderFactory.createTitledBorder(Messages
                    .getString("PdfPermissionManagerGui.0"))); //$NON-NLS-1$

            assemblyBox = new JCheckBox(Messages
                    .getString("PdfPermissionManagerGui.8")); //$NON-NLS-1$
            copyBox = new JCheckBox(Messages
                    .getString("PdfPermissionManagerGui.9")); //$NON-NLS-1$
            degradedPrintingBox = new JCheckBox(Messages
                    .getString("PdfPermissionManagerGui.10")); //$NON-NLS-1$
            printingBox = new JCheckBox(Messages
                    .getString("PdfPermissionManagerGui.11")); //$NON-NLS-1$
            screenReadersBox = new JCheckBox(Messages
                    .getString("PdfPermissionManagerGui.12")); //$NON-NLS-1$
            fillInBox = new JCheckBox(Messages
                    .getString("PdfPermissionManagerGui.13")); //$NON-NLS-1$
            modifyContentsBox = new JCheckBox(Messages
                    .getString("PdfPermissionManagerGui.14")); //$NON-NLS-1$
            modifyAnnotationsBox = new JCheckBox(Messages
                    .getString("PdfPermissionManagerGui.15")); //$NON-NLS-1$

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
                return (f.getName().endsWith(".pdf")); //$NON-NLS-1$
            }
            return f.isDirectory();
        }

        @Override
        public String getDescription() {
            return Messages.getString("PdfPermissionManagerGui.17"); //$NON-NLS-1$
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
        gui.setMinimumSize(s);
        if (!isJava6OrHigher()) {
            enforceMinimalSize(s, gui);
        }
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setVisible(true);
    }

    public static boolean isJava6OrHigher() {
        try {
            String[] rawVersion = System.getProperty("java.version").split(".");
            int first = Integer.parseInt(rawVersion[0]);
            int second = Integer.parseInt(rawVersion[1]);
            if (first > 1) {
                return true;
            } else if (first < 1) {
                return false;
            }
            if (second >= 6) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public static void enforceMinimalSize(final Dimension d, final JFrame frame) {
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Dimension s = frame.getSize();
                boolean resize = false;
                if (s.width < d.width) {
                    s.width = d.width;
                    resize = true;
                }
                if (s.height < d.height) {
                    s.height = d.height;
                    resize = true;
                }
                if (resize) {
                    frame.setSize(s);
                }
            }
        });
    }

}
