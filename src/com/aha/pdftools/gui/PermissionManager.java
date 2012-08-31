package com.aha.pdftools.gui;

import java.awt.Desktop;
import java.awt.EventQueue;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

import com.aha.pdftools.FileUtils;
import com.aha.pdftools.Messages;
import com.aha.pdftools.PdfPermissionManager;
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

public class PermissionManager {

	// TODO table headers line wrap

	private JFrame frame;
	private JTable table;
	private SelectionInList<PdfFile> openFiles;
	private HashSet<String> openFileSet = new HashSet<String>();
	private final Action openFilesAction = new OpenFilesAction();
	private final Action openFolderAction = new OpenFolderAction();
	private final Action saveAction = new SaveAction();
	private final Action deleteAction = new DeleteAction();
	private final Action allPermissionsAction = new AllPermissionsAction();
	private StatusPanel statusPanel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					String os = System.getProperty("os.name"); //$NON-NLS-1$
					if (os.equals("Linux")) { //$NON-NLS-1$
						UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
					} else if (os.equals("Windows")) { //$NON-NLS-1$
						UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					}

					PermissionManager window = new PermissionManager();
					window.frame.setVisible(true);
					new DropTarget(window.frame, new PermissionManagerDropTarget(window));
				} catch (Exception e) {
					e.printStackTrace();
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

		JSeparator separator_1 = new JSeparator();
		mnFile.add(separator_1);
		mnFile.add(mntmSave);

		JMenuItem mntmSaveAll = new JMenuItem(Messages.getString("PermissionManager.SaveAll")); //$NON-NLS-1$
		mntmSaveAll.setMnemonic(KeyEvent.VK_A);
		mntmSaveAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		mntmSaveAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveAll();
			}
		});
		mnFile.add(mntmSaveAll);

		JMenuItem mntmQuit = new JMenuItem(Messages.getString("PermissionManager.Quit")); //$NON-NLS-1$
		mntmQuit.setMnemonic(KeyEvent.VK_Q);
		mntmQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));
		mntmQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		JSeparator separator = new JSeparator();
		mnFile.add(separator);
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

		JSeparator separator_2 = new JSeparator();
		mnEdit.add(separator_2);

		JMenuItem mntmClearList = new JMenuItem(Messages.getString("PermissionManager.ClearList")); //$NON-NLS-1$
		mntmClearList.setMnemonic(KeyEvent.VK_C);
		mntmClearList.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, InputEvent.SHIFT_MASK));
		mntmClearList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearFiles();
			}
		});
		mnEdit.add(mntmClearList);

		JMenuItem mntmAllPermissions = new JMenuItem(allPermissionsAction);
		mntmAllPermissions.setText("Set All Permissions");
		mntmAllPermissions.setMnemonic(KeyEvent.VK_P);
		mnEdit.add(mntmAllPermissions);

		openFiles = new SelectionInList<PdfFile>();
		table = new JTable();
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setModel(new PdfFileTableModel(openFiles));
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() == 2) {
					JTable target = (JTable)e.getSource();
					int row = target.rowAtPoint(e.getPoint());
					int column = target.columnAtPoint(e.getPoint());
					if (row >= 0 && column >= 0 && column <= 1) {
						openPdf(openFiles.getElementAt(row));
					}
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
			Logger.getLogger(PermissionManager.class.getName()).log(Level.WARNING, "Failed to open Pdf file", e); //$NON-NLS-1$
		}
	}

	private void openFolder() {
		JFileChooser chooser = getFileChooser();
		chooser.setDialogTitle("Choose folder");
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

	private void save(List<PdfFile> files) {
		List<SaveUnit> saveUnits = new ArrayList<SaveUnit>();
		if (files.size() == 1) {
			File f = chooseSaveFile(null, true);
			if (f != null) {
				if (f.exists()) {
					// ask if the file should be overwritten
					String msg = MessageFormat.format(Messages.getString("PermissionManager.AskOverwriteFile"), f.getAbsolutePath()); //$NON-NLS-1$
					int resultVal = JOptionPane.showConfirmDialog(
							frame,
							msg,
							Messages.getString("PermissionManager.SaveAs"), //$NON-NLS-1$
							JOptionPane.YES_NO_OPTION);
					if (resultVal == JOptionPane.NO_OPTION) {
						return;
					}
				}
				saveUnits.add(new SaveUnit(files.get(0), f));
			}
		} else if (files.size() > 1) {
			File f = chooseSaveFile("file name will be ignored", false);
			if (f != null) {
				File targetDirectory = f.getParentFile();
				for (PdfFile pdfFile : files) {
					// TODO check source exists and target is not overwriting
					String name = pdfFile.getName();
					File target = new File(targetDirectory.getAbsolutePath() + File.separator + name);
					saveUnits.add(new SaveUnit(pdfFile, target));
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
			int perm = PdfPermissionManager.getPermissions(file);
			PdfFile pdfFile = new PdfFile(file, perm);
			openFiles.getList().add(pdfFile);
			openFileSet.add(path);
		} catch (IOException e) {
			Logger.getLogger(PermissionManager.class.getName()).log(Level.WARNING, null, e);
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
		for (PdfFile pdfFile : openFiles.getList()) {
			pdfFile.setAllowAll(true);
		}
		// update table
		((PdfFileTableModel)table.getModel()).fireTableDataChanged();
	}

	private JFileChooser getFileChooser() {
		// TODO remember directory
		return new JFileChooser();
	}

	private File chooseSaveFile(String initalName, boolean addExtension) {
		JFileChooser chooser = getFileChooser();
		chooser.setFileFilter(new PdfFileFilter());
		if (initalName != null) {
			chooser.setName(initalName);
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

	private class OpenFileTask extends SwingWorker<Void, Void> {
		private final List<File> files;
		private final ProgressDisplay progress;

		public OpenFileTask(List<File> files, ProgressDisplay progress) {
			this.files = files;
			this.progress = progress;
		}

		@Override
		protected Void doInBackground() throws Exception {
			synchronized (progress) {
				progress.startTask(Messages.getString("PermissionManager.Loading"), files.size(), true); //$NON-NLS-1$
				int i = 0;
				for (File file : files) {
					if (progress.isCanceled()) {
						break;
					}
					progress.setNote(file.getName());
					insertFile(file);
					progress.setProgress(++i);
				}
				progress.endTask();
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

	private class SaveFileTask extends SwingWorker<Void, Void> {
		private final List<SaveUnit> files;
		private final ProgressDisplay progress;

		public SaveFileTask(List<SaveUnit> files, ProgressDisplay progress) {
			this.files = files;
			this.progress = progress;
		}

		protected Void doInBackground() throws Exception {
			synchronized (progress) {
				progress.startTask(Messages.getString("PermissionManager.Saving"), files.size(), true); //$NON-NLS-1$
				int i = 0;
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
				progress.endTask();
			}
			return null;
		}
	}

	@SuppressWarnings("serial")
	private class OpenFilesAction extends AbstractAction {
		public OpenFilesAction() {
			putValue(LARGE_ICON_KEY, new ImageIcon(PermissionManager.class.getResource("/com/aha/pdftools/icons/document-open.png"))); //$NON-NLS-1$
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
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
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
			putValue(LARGE_ICON_KEY, new ImageIcon(PermissionManager.class.getResource("/com/aha/pdftools/icons/document-save.png"))); //$NON-NLS-1$
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
			putValue(LARGE_ICON_KEY, new ImageIcon(PermissionManager.class.getResource("/com/aha/pdftools/icons/edit-delete.png"))); //$NON-NLS-1$
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
			putValue(LARGE_ICON_KEY, new ImageIcon(PermissionManager.class.getResource("/com/aha/pdftools/icons/stock_calc-accept.png"))); //$NON-NLS-1$
			putValue(SHORT_DESCRIPTION, "Set all permissions on all files");
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			setAllPermissions();
		}
	}
}
