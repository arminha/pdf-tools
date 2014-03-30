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

package com.aha.pdftools.gui;

import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Rectangle;

import javax.swing.DropMode;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.UIManager;

import com.aha.pdftools.FileUtils;
import com.aha.pdftools.Messages;
import com.aha.pdftools.PdfPermissionManager;
import com.aha.pdftools.ProgressDisplay;
import com.aha.pdftools.model.PdfFileTableModel;
import com.aha.pdftools.model.PdfFile;
import com.jgoodies.binding.list.SelectionInList;

import java.awt.BorderLayout;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

public class PermissionManager implements FileSelection {

    // XXX table headers line wrap

    private JFrame frame;
    private JTable table;
    private SelectionInList<PdfFile> openFiles;
    private HashSet<String> openFileSet = new HashSet<String>();
    private final Action openFilesAction = new OpenFilesAction();
    private final Action openFolderAction = new OpenFolderAction();
    private final Action saveAction = new SaveAction();
    private final Action deleteAction = new DeleteAction();
    private final Action allPermissionsAction = new AllPermissionsAction();
    private final Action mergeAction = new MergeAction();
    private final Action mergePagesAction = new MergePagesAction();
    private StatusPanel statusPanel;
    private JFileChooser fileChooser;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    String os = System.getProperty("os.name").toLowerCase(); //$NON-NLS-1$
                    if (os.equals("linux")) { //$NON-NLS-1$
                        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                    }

                    PermissionManager window = new PermissionManager();
                    window.frame.setVisible(true);
                    new DropTarget(window.frame, new PermissionManagerDropTarget(window));
                } catch (Exception e) {
                    Logger.getLogger(PermissionManager.class.getName()).log(Level.SEVERE, e.getMessage(), e);
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public PermissionManager() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setTitle(Messages.getString("PermissionManager.Title")); //$NON-NLS-1$
        // SUPPRESS CHECKSTYLE MagicNumber
        frame.setBounds(100, 100, 622, 410);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        JMenu mnFile = new JMenu(Messages.getString("PermissionManager.FileMenu")); //$NON-NLS-1$
        mnFile.setMnemonic('F');
        menuBar.add(mnFile);

        JMenuItem mntmOpenFile = new JMenuItem(openFilesAction);
        mntmOpenFile.setMnemonic(KeyEvent.VK_O);
        mntmOpenFile.setText(Messages.getString("PermissionManager.OpenFiles")); //$NON-NLS-1$
        mnFile.add(mntmOpenFile);

        JMenuItem mntmOpenFolder = new JMenuItem(openFolderAction);
        mntmOpenFolder.setText(Messages.getString("PermissionManager.OpenFolder")); //$NON-NLS-1$
        mntmOpenFolder.setMnemonic(KeyEvent.VK_F);
        mnFile.add(mntmOpenFolder);

        JMenuItem mntmSave = new JMenuItem(saveAction);
        mntmSave.setText(Messages.getString("PermissionManager.Save..")); //$NON-NLS-1$
        mntmSave.setMnemonic(KeyEvent.VK_S);

        JSeparator separator1 = new JSeparator();
        mnFile.add(separator1);
        mnFile.add(mntmSave);

        JMenuItem mntmSaveAll = new JMenuItem(Messages.getString("PermissionManager.SaveAll")); //$NON-NLS-1$
        mntmSaveAll.setMnemonic(KeyEvent.VK_A);
        mntmSaveAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
        mntmSaveAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAll();
            }
        });
        mnFile.add(mntmSaveAll);

        JMenuItem mntmQuit = new JMenuItem(Messages.getString("PermissionManager.Quit")); //$NON-NLS-1$
        mntmQuit.setMnemonic(KeyEvent.VK_Q);
        mntmQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));
        mntmQuit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });

        JMenuItem mntmMergeFiles = new JMenuItem(mergeAction);
        mntmMergeFiles.setText(Messages.getString("PermissionManager.CombineFiles")); //$NON-NLS-1$
        mntmMergeFiles.setMnemonic(KeyEvent.VK_C);
        mnFile.add(mntmMergeFiles);

        JMenuItem mntmMergePages = new JMenuItem(mergePagesAction);
        mntmMergePages.setMnemonic(KeyEvent.VK_P);
        mntmMergePages.setText(Messages.getString("PermissionManager.CombinePages")); //$NON-NLS-1$
        mnFile.add(mntmMergePages);

        JSeparator separator = new JSeparator();
        mnFile.add(separator);

        JMenuItem mntmAbout = new JMenuItem(Messages.getString("PermissionManager.About")); //$NON-NLS-1$
        mntmAbout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new AboutDialog().setVisible(true);
            }
        });
        mnFile.add(mntmAbout);
        mnFile.add(mntmQuit);

        JMenu mnEdit = new JMenu(Messages.getString("PermissionManager.EditMenu")); //$NON-NLS-1$
        mnEdit.setMnemonic('E');
        menuBar.add(mnEdit);

        JMenuItem mntmDelete = new JMenuItem(deleteAction);
        mntmDelete.setText(Messages.getString("PermissionManager.Delete")); //$NON-NLS-1$
        mntmDelete.setMnemonic(KeyEvent.VK_D);
        mnEdit.add(mntmDelete);

        JMenuItem mntmSelectAll = new JMenuItem(Messages.getString("PermissionManager.SelectAll")); //$NON-NLS-1$
        mntmSelectAll.setMnemonic(KeyEvent.VK_A);
        mntmSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
        mntmSelectAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                table.selectAll();
            }
        });
        mnEdit.add(mntmSelectAll);

        JSeparator separator2 = new JSeparator();
        mnEdit.add(separator2);

        JMenuItem mntmClearList = new JMenuItem(Messages.getString("PermissionManager.ClearList")); //$NON-NLS-1$
        mntmClearList.setMnemonic(KeyEvent.VK_C);
        mntmClearList.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, InputEvent.SHIFT_MASK));
        mntmClearList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFiles();
            }
        });
        mnEdit.add(mntmClearList);

        JMenuItem mntmAllPermissions = new JMenuItem(allPermissionsAction);
        mntmAllPermissions.setText(Messages.getString("PermissionManager.SetAllPermissions")); //$NON-NLS-1$
        mntmAllPermissions.setMnemonic(KeyEvent.VK_P);
        mnEdit.add(mntmAllPermissions);

        openFiles = new SelectionInList<PdfFile>();
        table = new JTable();
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setModel(new PdfFileTableModel(openFiles));
        table.setDragEnabled(true);
        table.setDropMode(DropMode.INSERT_ROWS);
        table.setTransferHandler(new TableMultiRowTransferHandler(table));
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    JTable target = (JTable) e.getSource();
                    int row = target.rowAtPoint(e.getPoint());
                    int column = target.columnAtPoint(e.getPoint());
                    if (row >= 0 && column >= 0 && column <= 1) {
                        openPdf(openFiles.getElementAt(row));
                    }
                }
            }
        });
        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JTable table = ((JTableHeader) e.getSource()).getTable();
                TableColumnModel colModel = table.getColumnModel();

                // The index of the column whose header was clicked
                int vColIndex = colModel.getColumnIndexAtX(e.getX());

                // Return if not clicked on any column header
                if (vColIndex == -1) {
                    return;
                }

                // Determine if mouse was clicked between column heads
                Rectangle headerRect = table.getTableHeader().getHeaderRect(vColIndex);
                if (vColIndex == 0) {
                    headerRect.width -= 3;    // Hard-coded constant
                } else {
                    headerRect.grow(-3, 0);   // Hard-coded constant
                }
                if (headerRect.contains(e.getX(), e.getY())) {
                    // toggle column values
                    int mColIndex = table.convertColumnIndexToModel(vColIndex);
                    ((PdfFileTableModel) table.getModel()).toggleColumn(mColIndex);
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(table);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        frame.getContentPane().add(toolBar, BorderLayout.NORTH);

        JButton btnOpenFileButton = new JButton(openFilesAction);
        toolBar.add(btnOpenFileButton);

        JButton btnSave = new JButton(saveAction);
        toolBar.add(btnSave);

        JButton btnMerge = new JButton(mergeAction);
        toolBar.add(btnMerge);

        JButton btnDelete = new JButton(deleteAction);
        toolBar.add(btnDelete);

        JButton btnAllPermissions = new JButton(allPermissionsAction);
        toolBar.add(btnAllPermissions);

        statusPanel = new StatusPanel();
        frame.getContentPane().add(statusPanel, BorderLayout.SOUTH);
    }

    private void openFiles() {
        JFileChooser chooser = getFileChooser();
        chooser.setFileFilter(new PdfFileFilter());
        chooser.setMultiSelectionEnabled(true);
        int result = chooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            insertFiles(Arrays.asList(chooser.getSelectedFiles()));
        }
    }

    private void openPdf(PdfFile pdfFile) {
        try {
            Desktop.getDesktop().open(pdfFile.getSourceFile());
        } catch (IOException e) {
            Logger.getLogger(PermissionManager.class.getName())
                .log(Level.WARNING, "Failed to open Pdf file", e); //$NON-NLS-1$
        }
    }

    private void openFolder() {
        JFileChooser chooser = getFileChooser();
        chooser.setDialogTitle(Messages.getString("PermissionManager.ChooseFolder")); //$NON-NLS-1$
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = chooser.showOpenDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            List<File> files = filesInFolder(chooser.getSelectedFile());
            insertFiles(files);
        }
    }

    private void saveSelected() {
        List<PdfFile> selected = new ArrayList<PdfFile>();
        for (int row : table.getSelectedRows()) {
            selected.add(openFiles.getList().get(row));
        }
        save(selected);
    }

    private void saveAll() {
        save(openFiles.getList());
    }

    private void mergeSelected() {
        List<File> sourceFiles = new ArrayList<File>();
        for (int row : table.getSelectedRows()) {
            sourceFiles.add(openFiles.getList().get(row).getSourceFile());
        }
        if (!sourceFiles.isEmpty()) {
            File file = chooseSaveFile(null, true);
            if (file != null && checkOverwriteFile(file)) {
                new MergeFilesTask(file, sourceFiles, statusPanel).execute();
            }
        }
    }

    @Override
    public boolean checkOverwriteFile(File f) {
        if (f.exists()) {
            // ask if the file should be overwritten
            String msg = MessageFormat.
                    format(Messages.getString("PermissionManager.AskOverwriteFile"), f.getAbsolutePath()); //$NON-NLS-1$
            int resultVal = JOptionPane.showConfirmDialog(
                    frame,
                    msg,
                    Messages.getString("PermissionManager.SaveAs"), //$NON-NLS-1$
                    JOptionPane.YES_NO_OPTION);
            if (resultVal == JOptionPane.NO_OPTION) {
                return false;
            }
        }
        return true;
    }

    private void save(List<PdfFile> files) {
        List<SaveUnit> saveUnits = new ArrayList<SaveUnit>();
        if (files.size() == 1) {
            File f = chooseSaveFile(null, true);
            if (f != null && checkOverwriteFile(f)) {
                saveUnits.add(new SaveUnit(files.get(0), f));
            }
        } else if (files.size() > 1) {
            File f = chooseSaveFile(Messages.getString("PermissionManager.FileNameWillBeIgnored"), false); //$NON-NLS-1$
            if (f != null) {
                File targetDirectory = f.getParentFile();
                List<File> overwrittenFiles = new ArrayList<File>();
                for (PdfFile pdfFile : files) {
                    String name = pdfFile.getName();
                    File target = new File(targetDirectory.getAbsolutePath() + File.separator + name);
                    if (target.exists()) {
                        overwrittenFiles.add(target);
                    }
                    saveUnits.add(new SaveUnit(pdfFile, target));
                }
                if (!overwrittenFiles.isEmpty()) {
                    String format = Messages.getString("PermissionManager.AskOverwriteFiles"); //$NON-NLS-1$
                    String msg = MessageFormat
                            .format(format, overwrittenFiles.size(), targetDirectory.getAbsolutePath());
                    int result = JOptionPane.showConfirmDialog(
                            frame,
                            msg,
                            Messages.getString("PermissionManager.SaveAs"), //$NON-NLS-1$
                            JOptionPane.YES_NO_OPTION);
                    if (result == JOptionPane.NO_OPTION) {
                        return;
                    }
                }
            }
        }

        if (!saveUnits.isEmpty()) {
            new SaveFileTask(saveUnits, statusPanel).execute();
        }
    }

    static List<File> filesInFolder(File folder) {
        return FileUtils.listFiles(folder, true, new PdfFileFilter());
    }

    void insertFiles(List<File> files) {
        new OpenFileTask(files, statusPanel).execute();
    }

    private synchronized void insertFile(File file) {
        try {
            String path = file.getAbsolutePath();
            if (openFileSet.contains(path)) {
                return;
            }
            openFiles.getList().add(PdfFile.openFile(file));
            openFileSet.add(path);
            // add file to selection
            int index = openFiles.getSize() - 1;
            table.getSelectionModel().addSelectionInterval(index, index);
        } catch (IOException e) {
            Logger.getLogger(PermissionManager.class.getName()).log(Level.WARNING, e.getMessage(), e);
        }
    }

    private void removeSelected() {
        int[] selectedRows = table.getSelectedRows();
        Arrays.sort(selectedRows);
        for (int i = selectedRows.length - 1; i >= 0; i--) {
            removeFile(selectedRows[i]);
        }
    }

    private synchronized void removeFile(int index) {
        PdfFile pdfFile = openFiles.getList().remove(index);
        if (pdfFile != null) {
            openFileSet.remove(pdfFile.getSourcePath());
        }
    }

    private void clearFiles() {
        openFileSet.clear();
        openFiles.getList().clear();
    }

    private void setAllPermissions() {
        boolean allowAll = true;
        for (PdfFile pdfFile : openFiles.getList()) {
            if (!allowAll) {
                break;
            }
            allowAll = allowAll && pdfFile.isAllowAll();
        }

        for (PdfFile pdfFile : openFiles.getList()) {
            pdfFile.setAllowAll(!allowAll);
        }
        // update table
        ((PdfFileTableModel) table.getModel()).firePermissionsUpdated();
    }

    private JFileChooser getFileChooser() {
        JFileChooser chooser = new JFileChooser();
        if (fileChooser != null) {
            chooser.setCurrentDirectory(fileChooser.getCurrentDirectory());
        }
        fileChooser = chooser;
        return chooser;
    }

    @Override
    public File chooseSaveFile(String initalName, boolean addExtension) {
        JFileChooser chooser = getFileChooser();
        chooser.setFileFilter(new PdfFileFilter());
        if (initalName != null) {
            File f = new File(chooser.getCurrentDirectory().getAbsolutePath() + File.separator + initalName);
            chooser.setSelectedFile(f);
        }
        int result = chooser.showSaveDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (addExtension) {
                String filename = file.getAbsolutePath();
                if (!filename.endsWith(PdfFileFilter.PDF_EXTENSION)) {
                    filename += PdfFileFilter.PDF_EXTENSION;
                    file = new File(filename);
                }
            }
            return file;
        }
        return null;
    }

    private void mergePages() {
        int[] selectedRows = table.getSelectedRows();
        List<PdfFile> files = new ArrayList<PdfFile>();
        for (int i = 0; i < selectedRows.length; i++) {
            files.add(openFiles.getElementAt(selectedRows[i]));
        }
        if (!files.isEmpty()) {
            CombineDialog dialog = new CombineDialog(frame, this, statusPanel);
            dialog.show(files);
        }
    }

    private class OpenFileTask extends ReportingWorker<Void, Void> {
        private final List<File> files;
        private final ProgressDisplay progress;

        public OpenFileTask(List<File> files, ProgressDisplay progress) {
            super(frame);
            this.files = files;
            this.progress = progress;
        }

        @Override
        protected Void doInBackground() throws Exception {
            synchronized (progress) {
                progress.startTask(Messages.getString("PermissionManager.Loading"), files.size(), true); //$NON-NLS-1$
                int i = 0;
                try {
                    for (File file : files) {
                        if (progress.isCanceled()) {
                            break;
                        }
                        progress.setNote(file.getName());
                        insertFile(file);
                        progress.setProgress(++i);
                    }
                } finally {
                    progress.endTask();
                }
            }
            return null;
        }
    }

    private static class SaveUnit {
        final PdfFile pdfFile;
        final File target;

        public SaveUnit(PdfFile pdfFile, File target) {
            this.pdfFile = pdfFile;
            this.target = target;
        }
    }

    private class SaveFileTask extends ReportingWorker<Void, Void> {
        private final List<SaveUnit> files;
        private final ProgressDisplay progress;

        public SaveFileTask(List<SaveUnit> files, ProgressDisplay progress) {
            super(frame);
            this.files = files;
            this.progress = progress;
        }

        protected Void doInBackground() throws Exception {
            synchronized (progress) {
                progress.startTask(Messages.getString("PermissionManager.Saving"), files.size(), true); //$NON-NLS-1$
                int i = 0;
                try {
                    for (SaveUnit unit : files) {
                        if (progress.isCanceled()) {
                            break;
                        }
                        progress.setNote(unit.pdfFile.getName());
                        File source = unit.pdfFile.getSourceFile();
                        File target = unit.target;
                        PdfPermissionManager.processFile(source, target, unit.pdfFile, PdfPermissionManager.PASSWORD);
                        progress.setProgress(++i);
                    }
                } finally {
                    progress.endTask();
                }
            }
            return null;
        }
    }

    private class MergeFilesTask extends ReportingWorker<Void, Void> {
        private final File outputFile;
        private final List<File> sourceFiles;
        private final ProgressDisplay progress;

        public MergeFilesTask(File outputFile, List<File> sourceFiles,
                ProgressDisplay progress) {
            super(frame);
            this.outputFile = outputFile;
            this.sourceFiles = sourceFiles;
            this.progress = progress;
        }

        @Override
        protected Void doInBackground() throws Exception {
            synchronized (progress) {
                PdfPermissionManager.merge(outputFile, sourceFiles, progress);
            }
            return null;
        }
    }

    @SuppressWarnings("serial")
    private class OpenFilesAction extends AbstractAction {
        public OpenFilesAction() {
            putValue(LARGE_ICON_KEY, new ImageIcon(
                    PermissionManager.class.getResource("/com/aha/pdftools/icons/document-open.png"))); //$NON-NLS-1$
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
            putValue(SHORT_DESCRIPTION, Messages.getString("PermissionManager.OpenFilesDesc")); //$NON-NLS-1$
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            openFiles();
        }
    }

    @SuppressWarnings("serial")
    private class OpenFolderAction extends AbstractAction {
        public OpenFolderAction() {
            putValue(ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
            putValue(SHORT_DESCRIPTION, Messages.getString("PermissionManager.OpenFolderDesc")); //$NON-NLS-1$
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            openFolder();
        }
    }

    @SuppressWarnings("serial")
    private class SaveAction extends AbstractAction {
        public SaveAction() {
            putValue(LARGE_ICON_KEY, new ImageIcon(
                    PermissionManager.class.getResource("/com/aha/pdftools/icons/document-save.png"))); //$NON-NLS-1$
            putValue(SHORT_DESCRIPTION, Messages.getString("PermissionManager.SaveDesc")); //$NON-NLS-1$
            putValue(NAME, Messages.getString("PermissionManager.Save")); //$NON-NLS-1$
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            saveSelected();
        }
    }

    @SuppressWarnings("serial")
    private class DeleteAction extends AbstractAction {
        public DeleteAction() {
            putValue(LARGE_ICON_KEY, new ImageIcon(
                    PermissionManager.class.getResource("/com/aha/pdftools/icons/edit-delete.png"))); //$NON-NLS-1$
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
            putValue(SHORT_DESCRIPTION, Messages.getString("PermissionManager.DeleteDesc")); //$NON-NLS-1$
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            removeSelected();
        }
    }

    @SuppressWarnings("serial")
    private class AllPermissionsAction extends AbstractAction {
        public AllPermissionsAction() {
            putValue(LARGE_ICON_KEY, new ImageIcon(PermissionManager
                    .class.getResource("/com/aha/pdftools/icons/stock_calc-accept.png"))); //$NON-NLS-1$
            putValue(SHORT_DESCRIPTION, Messages.getString("PermissionManager.SetAllPermissionsDesc")); //$NON-NLS-1$
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            setAllPermissions();
        }
    }

    @SuppressWarnings("serial")
    private class MergeAction extends AbstractAction {
        public MergeAction() {
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_MASK));
            putValue(LARGE_ICON_KEY, new ImageIcon(
                    PermissionManager.class.getResource("/com/aha/pdftools/icons/stock_save-pdf.png"))); //$NON-NLS-1$
            putValue(SHORT_DESCRIPTION, Messages.getString("PermissionManager.CombineFilesDesc")); //$NON-NLS-1$
            putValue(NAME, Messages.getString("PermissionManager.Combine")); //$NON-NLS-1$
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            mergeSelected();
        }
    }

    @SuppressWarnings("serial")
    private class MergePagesAction extends AbstractAction {
        public MergePagesAction() {
            putValue(ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
            putValue(SHORT_DESCRIPTION, Messages.getString("PermissionManager.CombinePagesDesc")); //$NON-NLS-1$
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            mergePages();
        }
    }
}
