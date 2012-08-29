package com.aha.pdftools.gui;

import java.awt.EventQueue;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTable;

import com.aha.pdftools.PdfPermissionManager;
import com.aha.pdftools.PdfPermissionManagerGui;
import com.aha.pdftools.model.PdfFileTableModel;
import com.aha.pdftools.model.PdfFile;
import com.jgoodies.binding.list.SelectionInList;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

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
		
		JMenuItem mntmAddFile = new JMenuItem("Add File..");
		mntmAddFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addFile();
			}
		});
		mnFile.add(mntmAddFile);
		
		JMenuItem mntmAddFolder = new JMenuItem("Add Folder..");
		mntmAddFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addFolder();
			}
		});
		mnFile.add(mntmAddFolder);
		
		JMenuItem mntmSave = new JMenuItem("Save..");
		mntmSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveSelected();
			}
		});
		mnFile.add(mntmSave);
		
		JMenuItem mntmSaveAll = new JMenuItem("Save All..");
		mntmSaveAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveAll();
			}
		});
		mnFile.add(mntmSaveAll);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		JMenuItem mntmClearList = new JMenuItem("Clear List");
		mntmClearList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearFiles();
			}
		});
		mnFile.add(mntmClearList);
		mnFile.add(mntmExit);
		
		openFiles = new SelectionInList<PdfFile>();
		table = new JTable();
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setModel(new PdfFileTableModel(openFiles));
		JScrollPane scrollPane = new JScrollPane(table);
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
	}

	private void addFile() {
		JFileChooser chooser = getFileChooser();
		chooser.setFileFilter(new PdfFileFilter());
		chooser.setMultiSelectionEnabled(true);
		int result = chooser.showOpenDialog(frame);
		if (result == JFileChooser.APPROVE_OPTION) {
			insertFiles(chooser.getSelectedFiles());
		}
	}
	
	private void addFolder() {
		JFileChooser chooser = getFileChooser();
		chooser.setDialogTitle("Choose folder");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = chooser.showOpenDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
        	File[] files = filesInFolder(chooser.getSelectedFile());
        	insertFiles(files);
        }
	}
	
	private void saveSelected() {
		// TODO
	}
	
	private void saveAll() {
		save(openFiles.getList());
	}
	
	private void save(List<PdfFile> files) {
		if (files.size() == 1) {
			// TODO choose target file
		} else if (files.size() > 1) {
			// TODO choose target folder
		}
	}
	
	private File[] filesInFolder(File folder) {
		ArrayList<File> files = new ArrayList<File>();
        if (folder.isDirectory()) {
            Queue<File> dirsToProcess = new ArrayDeque<File>();
            Set<String> processedDirs = new HashSet<String>();
            dirsToProcess.add(folder);
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
        return files.toArray(new File[0]);
	}
	
	private void insertFiles(File[] files) {
		// TODO do in background
		for (File file : files) {
			try {
				String path = file.getAbsolutePath();
				if (openFileSet.contains(path)) {
					continue;
				}
				int perm = PdfPermissionManager.getPermissions(file);
				PdfFile pdfFile = new PdfFile(path, perm);
				openFiles.getList().add(pdfFile);
				openFileSet.add(path);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void removeFile(int index) {
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
}
