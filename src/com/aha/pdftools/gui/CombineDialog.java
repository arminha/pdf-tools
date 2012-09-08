package com.aha.pdftools.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Frame;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.ImageIcon;

import com.aha.pdftools.PdfPermissionManager;
import com.aha.pdftools.ProgressDisplay;
import com.aha.pdftools.model.PdfFile;
import com.aha.pdftools.model.PdfPages;
import com.aha.pdftools.model.PdfPagesTableModel;
import com.jgoodies.binding.list.SelectionInList;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ListSelectionModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

@SuppressWarnings("serial")
public class CombineDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTable table;
	private final SelectionInList<PdfPages> sourcePages = new SelectionInList<PdfPages>();
	private final FileSelection fileSelection;
	private final ProgressDisplay progress;

	/**
	 * Create the dialog.
	 * @param frame 
	 */
	public CombineDialog(Frame frame, FileSelection fileSelection, ProgressDisplay progress) {
		super(frame, true);
		this.fileSelection = fileSelection;
		this.progress = progress;
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("Combine");
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));
		{
			JScrollPane scrollPane = new JScrollPane();
			contentPanel.add(scrollPane); 
			{
				table = new JTable();
				table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				table.setModel(new PdfPagesTableModel(sourcePages));
				table.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						if (e.getClickCount() == 2) {
							JTable target = (JTable)e.getSource();
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
				upButton.setIcon(new ImageIcon(CombineDialog.class.getResource("/com/aha/pdftools/icons/go-up.png")));
				verticalBox.add(upButton);
			}
			{
				JButton downButton = new JButton();
				downButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						moveDown();
					}
				});
				downButton.setIcon(new ImageIcon(CombineDialog.class.getResource("/com/aha/pdftools/icons/go-down.png")));
				verticalBox.add(downButton);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			JButton saveButton = new JButton("Save");
			saveButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					save();
				}
			});
			getRootPane().setDefaultButton(saveButton);
			JButton cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setVisible(false);
				}
			});
			GroupLayout gl_buttonPane = new GroupLayout(buttonPane);
			int containerGap = 6;
			gl_buttonPane.setAutoCreateGaps(true);
			gl_buttonPane.linkSize(saveButton, cancelButton);
			gl_buttonPane.setHorizontalGroup(
					gl_buttonPane.createSequentialGroup()
					.addGap(containerGap)
					.addGap(0, 0, Short.MAX_VALUE)
					.addComponent(saveButton)
					.addComponent(cancelButton)
					.addGap(containerGap)
					);
			gl_buttonPane.setVerticalGroup(
					gl_buttonPane.createSequentialGroup()
					.addGroup(gl_buttonPane.createParallelGroup(Alignment.LEADING)
							.addComponent(saveButton)
							.addComponent(cancelButton))
							.addGap(containerGap)
					);
			buttonPane.setLayout(gl_buttonPane);
		}
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
			Logger.getLogger(CombineDialog.class.getName()).log(Level.WARNING, "Failed to open Pdf file", e); //$NON-NLS-1$
		}
	}

	private static class MergePagesTask extends ReportingWorker<Void, Void> {
		private final File outputFile;
		private final List<PdfPages> pages;
		private final ProgressDisplay progress;

		public MergePagesTask(Component parentComponent, File outputFile,
				List<PdfPages> pages, ProgressDisplay progress) {
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
