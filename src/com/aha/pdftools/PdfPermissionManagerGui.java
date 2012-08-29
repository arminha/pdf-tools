package com.aha.pdftools;

import com.itextpdf.text.DocumentException;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.StringTokenizer;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import org.jdesktop.layout.GroupLayout;

@SuppressWarnings("serial")//$NON-NLS-1$
public class PdfPermissionManagerGui extends JFrame {

    private static DataFlavor uriListFlavor = null;
    static {
        try {
            uriListFlavor = new DataFlavor("text/uri-list; class=java.lang.String");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PdfPermissionManagerGui.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static final String PDF_EXTENSION = ".pdf"; //$NON-NLS-1$
    
    JButton openButton;
    JButton saveButton;
    JButton batchButton;
    JButton saveBatchButton;
    JButton exitButton;
    PermPanel permPanel;
    JTextArea openFileLabel;

    private File source;

    public PdfPermissionManagerGui() {
        super(Messages.getString("PdfPermissionManagerGui.FrameTitle")); //$NON-NLS-1$

        Container content = getContentPane();

        // create widgets
        openButton = new JButton(Messages
                .getString("PdfPermissionManagerGui.OpenButton")); //$NON-NLS-1$
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                if (source != null) {
                    chooser.setCurrentDirectory(source.isDirectory() ?
                        source : source.getParentFile());
                }
                chooser.setFileFilter(new PdfFileFilter());
                int returnVal = chooser
                        .showOpenDialog(PdfPermissionManagerGui.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File f = chooser.getSelectedFile();
                    PdfPermissionManagerGui.this.loadFile(f);
                }
            }
        });

        batchButton = new JButton("Open Folder..");
        batchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                JFileChooser chooser = new JFileChooser();
                if (source != null) {
                    chooser.setCurrentDirectory(source.isDirectory() ?
                        source : source.getParentFile());
                }
                chooser.setDialogTitle("Choose folder");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = chooser.showOpenDialog(PdfPermissionManagerGui.this);
                if (returnVal != JFileChooser.APPROVE_OPTION) return;

                loadBatch(chooser.getSelectedFile());
            }
        });

        saveBatchButton = new JButton("Save batch..");
        saveBatchButton.setEnabled(false);
        saveBatchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (isBatch()) {
                    saveBatch();
                }
            }
        });

        saveButton = new JButton(Messages
                .getString("PdfPermissionManagerGui.SaveButton")); //$NON-NLS-1$
        saveButton.setEnabled(false);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isBatch()) {
                    saveSingle();
                }
            }
        });

        exitButton = new JButton(Messages
                .getString("PdfPermissionManagerGui.ExitButton")); //$NON-NLS-1$
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        permPanel = new PermPanel();
        permPanel.setEnabled(false);
        openFileLabel = new JTextArea();
        openFileLabel.setEditable(false);

        // layout
        final GroupLayout layout = new GroupLayout(content);
        layout.setAutocreateGaps(true);
        layout.setAutocreateContainerGaps(true);
        content.setLayout(layout);

        content.add(openButton);
        content.add(batchButton);
        content.add(saveBatchButton);
        content.add(saveButton);
        content.add(exitButton);
        content.add(permPanel);
        content.add(openFileLabel);

        layout.linkSize(new Component[] { openButton, saveButton, exitButton,
            batchButton, saveBatchButton });
        layout.linkSize(new Component[] { openButton, openFileLabel },
                GroupLayout.VERTICAL);

        // horizontal group
        final GroupLayout.SequentialGroup horizontal = layout
                .createSequentialGroup();
        horizontal.add(layout.createParallelGroup().add(openFileLabel, 100,
                GroupLayout.PREFERRED_SIZE, 4000).add(permPanel,
                GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
                GroupLayout.PREFERRED_SIZE));
        horizontal.add(layout.createParallelGroup().add(openButton)
                .add(batchButton).add(saveBatchButton)
                .add(saveButton).add(exitButton));
        layout.setHorizontalGroup(horizontal);

        // vertical group
        final GroupLayout.SequentialGroup vertical = layout
                .createSequentialGroup();
        vertical.add(layout.createParallelGroup().add(openFileLabel).add(
                openButton));
        vertical.add(layout.createParallelGroup(GroupLayout.TRAILING).add(
                layout.createSequentialGroup().add(permPanel).add(0, 0, 4000))
                .add(layout.createSequentialGroup().add(batchButton)
                .add(saveBatchButton).add(saveButton).add(exitButton)));
        layout.setVerticalGroup(vertical);
    }

    private boolean isBatch() {
        return source != null && source.isDirectory();
    }

    private List<File> getBatchFiles() {
        ArrayList<File> files = new ArrayList<File>();
        if (source.isDirectory()) {
            Queue<File> dirsToProcess = new ArrayDeque<File>();
            Set<String> processedDirs = new HashSet<String>();
            dirsToProcess.add(source);
            java.io.FileFilter filter = new PdfFileFilter();
            while (!dirsToProcess.isEmpty()) {
                File dir = dirsToProcess.remove();
                for (File file : dir.listFiles(filter)) {
                    if (file.isDirectory()) {
                        try {
                            String path = file.getCanonicalPath();
                            if (!processedDirs.contains(path)) {
                                dirsToProcess.add(file);
                                processedDirs.add(path);
                            }
                        } catch (IOException e) {
                            Logger.getLogger(PdfPermissionManagerGui.class.getName())
                                    .log(Level.WARNING, null, e);
                        }
                    } else {
                        files.add(file);
                    }
                }
            }
        }
        return files;
    }

    private void saveSingle() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new PdfFileFilter());
        int returnVal = chooser
                .showSaveDialog(PdfPermissionManagerGui.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String filename = chooser.getSelectedFile()
                    .getAbsolutePath();
            if (!filename.endsWith(PDF_EXTENSION)) {
                filename += PDF_EXTENSION;
            }
            File f = new File(filename);
            if (f.exists()) {
                // ask if the file should be overwritten
                String msg = MessageFormat.format(Messages.getString("PdfPermissionManagerGui.AskOverwriteFile"), f.getAbsolutePath()); //$NON-NLS-1$
                int resultVal = JOptionPane
                        .showConfirmDialog(
                                PdfPermissionManagerGui.this,
                                msg,
                                Messages.getString("PdfPermissionManagerGui.SaveAs"), //$NON-NLS-1$
                                JOptionPane.YES_NO_OPTION);
                if (resultVal == JOptionPane.NO_OPTION) {
                    return;
                }
            }
            String password = askNewOwnerPassword();
            try {
                PdfPermissionManager.processFile(source, f, permPanel.getPermissions(), password);
            } catch (IOException ioe) {
                String errMsg = MessageFormat.format(Messages.getString("PdfPermissionManagerGui.CannotSaveAs"), f.getAbsolutePath()); //$NON-NLS-1$
                JOptionPane
                        .showMessageDialog(
                                PdfPermissionManagerGui.this,
                                new String[] { errMsg, ioe.getMessage() },
                                Messages
                                        .getString("PdfPermissionManagerGui.ErrorSaving"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
            } catch (DocumentException de) {
                String errMsg = MessageFormat.format(Messages.getString("PdfPermissionManagerGui.CannotSaveAs"), f.getAbsolutePath()); //$NON-NLS-1$
                JOptionPane
                        .showMessageDialog(
                                PdfPermissionManagerGui.this,
                                new String[] { errMsg, de.getMessage() },
                                Messages
                                        .getString("PdfPermissionManagerGui.ErrorSaving"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
            }
        }
    }

    private void saveBatch() {
        String password = askNewOwnerPassword();
        if (password == null) return;
        int result = JOptionPane.showConfirmDialog(this, "Keep backup copies of the PDFs?", "Backup?", JOptionPane.YES_NO_CANCEL_OPTION);
        if (result == JOptionPane.CANCEL_OPTION) return;
        boolean keepBackup = result != JOptionPane.NO_OPTION;

        List<File> files = getBatchFiles();
        ProgressMonitor pm = new ProgressMonitor(this, "Save batch..", "", 0, files.size());
        pm.setMillisToPopup(500);
        pm.setProgress(0);
        BatchSaveTask task = new BatchSaveTask(keepBackup, files , password, permPanel.getPermissions(), pm);
        task.execute();
    }

    private boolean load(File f) {
        if (f.isDirectory()) {
            return loadBatch(f);
        } else {
            return loadFile(f);
        }
    }

    private boolean loadBatch(File dir) {
        if (dir.isDirectory() && dir.canRead()) {
            source = dir;
            int number = getBatchFiles().size();
            openFileLabel.setText(source.getAbsolutePath() + "\n" + number + " PDF files");

            permPanel.setEnabled(true);
            permPanel.setPermissions(new PdfPermissions());
            saveButton.setEnabled(false);
            saveBatchButton.setEnabled(true);
            return true;
        } else {
            return false;
        }
    }

    private boolean loadFile(File f) {
        if (f.isFile() && f.canRead()) {
            // TODO do this in a special thread
            try {
                PdfPermissions perms = new PdfPermissions(PdfPermissionManager.getPermissions(f));

                permPanel.setPermissions(perms);
                permPanel.setEnabled(true);
                saveButton.setEnabled(true);
                saveBatchButton.setEnabled(false);
                source = f;
                openFileLabel.setText(source.getAbsolutePath());
                return true;
            } catch (IOException ioe) {
                String errMsg = MessageFormat.format(
                        Messages.getString("PdfPermissionManagerGui.CannotReadFile"), //$NON-NLS-1$
                        f.getAbsolutePath());
                JOptionPane.showMessageDialog(PdfPermissionManagerGui.this,
                        new String[] { errMsg, ioe.getMessage() }, Messages
                                .getString("PdfPermissionManagerGui.ErrorOpening"), //$NON-NLS-1$
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } else {
            String errMsg = MessageFormat.format(
                    Messages.getString("PdfPermissionManagerGui.CannotReadFile"), //$NON-NLS-1$
                    f.getAbsolutePath());
            JOptionPane.showMessageDialog(PdfPermissionManagerGui.this, errMsg,
                    Messages.getString("PdfPermissionManagerGui.ErrorOpening"), //$NON-NLS-1$
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private String askNewOwnerPassword() {
//        String password = JOptionPane.showInputDialog(PdfPermissionManagerGui.this, Messages.getString("PdfPermissionManagerGui.EnterOwnerPassword"), "changeit"); //$NON-NLS-2$
//        return password;
        return "changeit";
    }

    private static class PermPanel extends JPanel {

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

    private static class PdfFileFilter extends FileFilter implements java.io.FileFilter {

        public PdfFileFilter() {
        }

        @Override
        public boolean accept(File f) {
            if (f.isFile()) {
                return (f.getName().endsWith(PDF_EXTENSION));
            }
            return f.isDirectory();
        }

        @Override
        public String getDescription() {
            return Messages.getString("PdfPermissionManagerGui.PdfFiles"); //$NON-NLS-1$
        }

    }

    private static class BatchSaveTask extends SwingWorker<Void, Void> {

        private boolean keepBackup;
        private List<File> sourceFiles;
        private String password;
        private PdfPermissions permissions;
        private ProgressMonitor pm;

        public BatchSaveTask(boolean keepBackup, List<File> sourceFiles, String password, PdfPermissions permissions, ProgressMonitor pm) {
            this.keepBackup = keepBackup;
            this.sourceFiles = sourceFiles;
            this.password = password;
            this.permissions = permissions;
            this.pm = pm;
        }

        @Override
        protected Void doInBackground() throws Exception {
            int count = 0;
            for (File file : sourceFiles) {
                if (pm.isCanceled()) break;

                count++;
                pm.setNote(file.getAbsolutePath());
                File backupFile = createBackup(file);
                PdfPermissionManager.processFile(backupFile, file, permissions, password);
                if (!keepBackup) {
                    deleteBackup(backupFile);
                }
                setProgress(count);
                pm.setProgress(count);
            }
            return null;
        }

        @Override
        protected void done() {
            super.done();
            pm.close();
        }

        private File createBackup(File file) {
            File backupFile = new File(file.getAbsolutePath() + ".bak");
            boolean success = file.renameTo(backupFile);
            // TODO check return value
            return backupFile;
        }

        private void deleteBackup(File backupFile) {
            if (backupFile.exists() && backupFile.canWrite()) {
                backupFile.delete();
            }
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        final PdfPermissionManagerGui gui = new PdfPermissionManagerGui();
        gui.pack();
        Dimension s = gui.getPreferredSize();
        gui.setMinimumSize(s);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setVisible(true);
        // add drag and drop
        new DropTarget(gui, new DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent dtde) {
                if (dtde
                        .isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY);
                    Transferable trans = dtde.getTransferable();
                    try {
                        @SuppressWarnings("rawtypes")
						List filelist = (List) trans
                                .getTransferData(DataFlavor.javaFileListFlavor);
                        File f = (File) filelist.get(0);
                        dtde.dropComplete(gui.load(f));
                    } catch (Exception e) {
                        dtde.dropComplete(false);
                    }
                } else if (dtde.isDataFlavorSupported(uriListFlavor)) {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY);
                    Transferable trans = dtde.getTransferable();
                    try {
                        String uris = (String) trans.getTransferData(uriListFlavor);
                        StringTokenizer tokenizer = new StringTokenizer(uris);
                        URI uri = new URI(tokenizer.nextToken());
                        File f = new File(uri);
                        dtde.dropComplete(gui.load(f));
                    } catch (Exception e) {
                        dtde.dropComplete(false);
                    }
                } else {
                    dtde.rejectDrop();
                }
            }
        });
    }

}
