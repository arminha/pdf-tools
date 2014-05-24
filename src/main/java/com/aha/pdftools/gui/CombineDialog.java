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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DropMode;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aha.pdftools.Messages;
import com.aha.pdftools.PdfPermissionManager;
import com.aha.pdftools.ProgressDisplay;
import com.aha.pdftools.model.PdfFile;
import com.aha.pdftools.model.PdfPages;
import com.aha.pdftools.model.PdfPagesTableModel;
import com.jgoodies.binding.list.SelectionInList;

@SuppressWarnings("serial")
public class CombineDialog extends JDialog {

    private static final Logger LOGGER = LoggerFactory.getLogger(CombineDialog.class);

    private final JPanel contentPanel = new JPanel();
    private JTable table;
    private final SelectionInList<PdfPages> sourcePages = new SelectionInList<PdfPages>();
    private final FileSelection fileSelection;
    private final ProgressDisplay progress;

    /**
     * Create the dialog.
     * 
     * @param frame
     */
    public CombineDialog(Frame frame, FileSelection fileSelection, ProgressDisplay progress) {
        super(frame, true);
        this.fileSelection = fileSelection;
        this.progress = progress;
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setTitle(Messages.getString("PermissionManager.Combine")); //$NON-NLS-1$
        // SUPPRESS CHECKSTYLE MagicNumber
        setBounds(100, 100, 450, 300);
        getContentPane().setLayout(new BorderLayout());
        // SUPPRESS CHECKSTYLE MagicNumber
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));
        // CHECKSTYLE IGNORE AvoidNestedBlocks
        {
            JScrollPane scrollPane = new JScrollPane();
            contentPanel.add(scrollPane);
            {
                table = new JTable();
                table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                table.setModel(new PdfPagesTableModel(sourcePages));
                table.setDragEnabled(true);
                table.setDropMode(DropMode.INSERT_ROWS);
                table.setTransferHandler(new TableRowTransferHandler(table));
                table.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (e.getClickCount() == 2) {
                            JTable target = (JTable) e.getSource();
                            int row = target.rowAtPoint(e.getPoint());
                            int column = target.columnAtPoint(e.getPoint());
                            if (row >= 0 && column == 0) {
                                openPdf(sourcePages.getElementAt(row));
                            }
                        }
                    }
                });
                scrollPane.setViewportView(table);
            }
        }
        {
            Box verticalBox = Box.createVerticalBox();
            contentPanel.add(verticalBox);
            {
                JButton upButton = new JButton();
                upButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        moveUp();
                    }
                });
                upButton.setIcon(new ImageIcon(
                        CombineDialog.class.getResource("/com/aha/pdftools/icons/go-up.png"))); //$NON-NLS-1$
                verticalBox.add(upButton);
            }
            {
                JButton downButton = new JButton();
                downButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        moveDown();
                    }
                });
                downButton.setIcon(new ImageIcon(
                        CombineDialog.class.getResource("/com/aha/pdftools/icons/go-down.png"))); //$NON-NLS-1$
                verticalBox.add(downButton);
            }
        }
        {
            JPanel buttonPane = new JPanel();
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            JButton saveButton = new JButton(Messages.getString("PermissionManager.Save")); //$NON-NLS-1$
            saveButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    save();
                }
            });
            getRootPane().setDefaultButton(saveButton);
            JButton cancelButton = new JButton(Messages.getString("PermissionManager.Cancel")); //$NON-NLS-1$
            cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setVisible(false);
                }
            });
            GroupLayout glButtonPane = new GroupLayout(buttonPane);
            final int containerGap = 6;
            glButtonPane.setAutoCreateGaps(true);
            glButtonPane.linkSize(saveButton, cancelButton);
            glButtonPane.setHorizontalGroup(
                    glButtonPane.createSequentialGroup()
                    .addGap(containerGap)
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(saveButton)
                    .addComponent(cancelButton)
                    .addGap(containerGap)
                    );
            glButtonPane.setVerticalGroup(
                    glButtonPane.createSequentialGroup()
                    .addGroup(glButtonPane.createParallelGroup(Alignment.LEADING)
                            .addComponent(saveButton)
                            .addComponent(cancelButton))
                            .addGap(containerGap)
                    );
            buttonPane.setLayout(glButtonPane);
        }
        // CHECKSTYLE END IGNORE AvoidNestedBlocks
    }

    private void moveDown() {
        int selectionIdx = table.getSelectedRow();
        if (selectionIdx >= 0 && selectionIdx < sourcePages.getSize() - 1) {
            List<PdfPages> list = sourcePages.getList();
            PdfPages next = list.get(selectionIdx + 1);
            PdfPages current = list.get(selectionIdx);
            list.set(selectionIdx + 1, current);
            list.set(selectionIdx, next);
            sourcePages.fireContentsChanged(selectionIdx, selectionIdx + 1);
            table.getSelectionModel().setSelectionInterval(selectionIdx + 1, selectionIdx + 1);
        }
    }

    public void show(List<PdfFile> files) {
        ArrayList<PdfPages> pages = new ArrayList<PdfPages>(files.size());
        for (PdfFile pdfFile : files) {
            pages.add(new PdfPages(pdfFile.getSourceFile(), pdfFile.getPageCount()));
        }
        sourcePages.setList(pages);
        setVisible(true);
    }

    private void moveUp() {
        int selectionIdx = table.getSelectedRow();
        if (selectionIdx >= 1 && selectionIdx < sourcePages.getSize()) {
            List<PdfPages> list = sourcePages.getList();
            PdfPages previous = list.get(selectionIdx - 1);
            PdfPages current = list.get(selectionIdx);
            list.set(selectionIdx - 1, current);
            list.set(selectionIdx, previous);
            sourcePages.fireContentsChanged(selectionIdx - 1, selectionIdx);
            table.getSelectionModel().setSelectionInterval(selectionIdx - 1, selectionIdx - 1);
        }
    }

    private void save() {
        if (table.getCellEditor() != null) {
            table.getCellEditor().stopCellEditing();
        }
        setVisible(false);
        File file = fileSelection.chooseSaveFile(null, true);
        if (file != null && fileSelection.checkOverwriteFile(file)) {
            new MergePagesTask(this.getParent(), file, sourcePages.getList(), progress).execute();
        }
    }

    private void openPdf(PdfPages pdfPages) {
        try {
            Desktop.getDesktop().open(pdfPages.getSourceFile());
        } catch (IOException e) {
            LOGGER.warn("Failed to open PDF file", e); //$NON-NLS-1$
        }
    }

    private static class MergePagesTask extends ReportingWorker<Void, Void> {
        private final File outputFile;
        private final List<PdfPages> pages;
        private final ProgressDisplay progress;

        public MergePagesTask(Component parentComponent, File outputFile, List<PdfPages> pages,
                ProgressDisplay progress) {
            super(parentComponent);
            this.outputFile = outputFile;
            this.pages = pages;
            this.progress = progress;
        }

        @Override
        protected Void doInBackground() throws Exception {
            synchronized (progress) {
                PdfPermissionManager.mergePages(outputFile, pages, progress);
            }
            return null;
        }
    }
}
