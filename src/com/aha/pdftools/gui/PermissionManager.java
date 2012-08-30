package com.aha.pdftools.gui;

import java.awt.EventQueue;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;

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
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import javax.swing.JSeparator;

public class PermissionManager {

	private JFrame frame;
	private JTable table;
	private SelectionInList<PdfFile> openFiles;
	private HashSet<String> openFileSet = new HashSet<String>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
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
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		mnFile.setMnemonic('F');
		menuBar.add(mnFile);

		JMenuItem mntmOpenFile = new JMenuItem("Open File(s)..");
		mntmOpenFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		mntmOpenFile.setMnemonic(KeyEvent.VK_O);
		mntmOpenFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFiles();
			}
		});
		mnFile.add(mntmOpenFile);

		JMenuItem mntmOpenFolder = new JMenuItem("Open Folder..");
		mntmOpenFolder.setMnemonic(KeyEvent.VK_F);
		mntmOpenFolder.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		mntmOpenFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFolder();
			}
		});
		mnFile.add(mntmOpenFolder);

		JMenuItem mntmSave = new JMenuItem("Save..");
		mntmSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		mntmSave.setMnemonic(KeyEvent.VK_S);
		mntmSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveSelected();
			}
		});

		JSeparator separator_1 = new JSeparator();
		mnFile.add(separator_1);
		mnFile.add(mntmSave);

		JMenuItem mntmSaveAll = new JMenuItem("Save All..");
		mntmSaveAll.setMnemonic(KeyEvent.VK_A);
		mntmSaveAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		mntmSaveAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveAll();
			}
		});
		mnFile.add(mntmSaveAll);

		JMenuItem mntmQuit = new JMenuItem("Quit");
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

		JMenu mnEdit = new JMenu("Edit");
		mnEdit.setMnemonic('E');
		menuBar.add(mnEdit);

		JMenuItem mntmDelete = new JMenuItem("Delete");
		mntmDelete.setMnemonic(KeyEvent.VK_D);
		mntmDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeSelected();
			}
		});
		mntmDelete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		mnEdit.add(mntmDelete);

		JMenuItem mntmSelectAll = new JMenuItem("Select All");
		mntmSelectAll.setMnemonic(KeyEvent.VK_A);
		mntmSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
		mnEdit.add(mntmSelectAll);

		JSeparator separator_2 = new JSeparator();
		mnEdit.add(separator_2);

		JMenuItem mntmClearList = new JMenuItem("Clear List");
		mntmClearList.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, InputEvent.SHIFT_MASK));
		mnEdit.add(mntmClearList);
		mntmClearList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearFiles();
			}
		});

		openFiles = new SelectionInList<PdfFile>();
		table = new JTable();
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setModel(new PdfFileTableModel(openFiles));
		JScrollPane scrollPane = new JScrollPane(table);
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
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
					String msg = MessageFormat.format(Messages.getString("PdfPermissionManagerGui.AskOverwriteFile"), f.getAbsolutePath()); //$NON-NLS-1$
					int resultVal = JOptionPane.showConfirmDialog(
							frame,
							msg,
							Messages.getString("PdfPermissionManagerGui.SaveAs"), //$NON-NLS-1$
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
			new SaveFileTask(saveUnits).execute();
		}
	}

	static List<File> filesInFolder(File folder) {
		return FileUtils.listFiles(folder, true, new PdfFileFilter());
	}

	void insertFiles(List<File> files) {
		new OpenFileTask(files).execute();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		// TODO disable/enable ui
		// TODO allow cancel
		private final List<File> files;

		public OpenFileTask(List<File> files) {
			this.files = files;
		}

		@Override
		protected Void doInBackground() throws Exception {
			for (File file : files) {
				insertFile(file);
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
		// TODO progess & cancel
		// TODO disable ui
		private final List<SaveUnit> files;

		public SaveFileTask(List<SaveUnit> files) {
			this.files = files;
		}

		protected Void doInBackground() throws Exception {
			for (SaveUnit unit : files) {
				File source = unit.pdfFile.getSourceFile();
				File target = unit.target;
				PdfPermissionManager.processFile(source, target, unit.pdfFile, PdfPermissionManager.PASSWORD);
			}
			return null;
		}
	}
}
