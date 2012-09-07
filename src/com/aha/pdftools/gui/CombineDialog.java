package com.aha.pdftools.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.ImageIcon;

import com.aha.pdftools.model.PdfPages;
import com.aha.pdftools.model.PdfPagesTableModel;
import com.jgoodies.binding.list.SelectionInList;

@SuppressWarnings("serial")
public class CombineDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTable table;
	private final SelectionInList<PdfPages> sourcePages = new SelectionInList<PdfPages>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			CombineDialog dialog = new CombineDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public CombineDialog() {
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
				table.setModel(new PdfPagesTableModel(sourcePages));
				table.getColumnModel().getColumn(0).setPreferredWidth(120);
				table.getColumnModel().getColumn(1).setPreferredWidth(125);
				scrollPane.setViewportView(table);
			}
		}
		{
			Box verticalBox = Box.createVerticalBox();
			contentPanel.add(verticalBox);
			{
				JButton upButton = new JButton();
				upButton.setIcon(new ImageIcon(CombineDialog.class.getResource("/com/aha/pdftools/icons/go-up.png")));
				verticalBox.add(upButton);
			}
			{
				JButton downButton = new JButton();
				downButton.setIcon(new ImageIcon(CombineDialog.class.getResource("/com/aha/pdftools/icons/go-down.png")));
				verticalBox.add(downButton);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton saveButton = new JButton("Save");
				buttonPane.add(saveButton);
				getRootPane().setDefaultButton(saveButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
}
