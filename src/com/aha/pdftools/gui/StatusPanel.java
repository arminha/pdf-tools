package com.aha.pdftools.gui;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

@SuppressWarnings("serial")
public class StatusPanel extends JPanel implements ProgressDisplay {

	boolean canceled = false;
	private JButton btnCancel;
	private JProgressBar progressBar;
	private JLabel lblStatus;
	private String message;

	/**
	 * Create the panel.
	 */
	public StatusPanel() {
		lblStatus = new JLabel();

		progressBar = new JProgressBar();
		progressBar.setEnabled(false);

		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				canceled = true;
			}
		});
		btnCancel.setEnabled(false);
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.linkSize(SwingConstants.VERTICAL, progressBar, btnCancel, lblStatus);
		groupLayout.setHorizontalGroup(
			groupLayout.createSequentialGroup()
				.addGap(8)
				.addComponent(lblStatus)
				.addPreferredGap(ComponentPlacement.RELATED, 0, Short.MAX_VALUE)
				.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
				.addComponent(btnCancel)
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.CENTER)
				.addComponent(lblStatus)
				.addComponent(progressBar)
				.addComponent(btnCancel)
		);
		setLayout(groupLayout);
	}

	@Override
	public void startTask(String message, int max, boolean cancelable) {
		canceled = false;
		btnCancel.setEnabled(cancelable);
		progressBar.setEnabled(true);
		progressBar.setMaximum(max);
		progressBar.setValue(0);
		this.message = message;
		lblStatus.setText(message);
	}

	@Override
	public void endTask() {
		btnCancel.setEnabled(false);
		progressBar.setEnabled(false);
		progressBar.setValue(0);
		lblStatus.setText("");
	}

	@Override
	public boolean isCanceled() {
		return canceled;
	}

	@Override
	public void setNote(String note) {
		lblStatus.setText(message + " " + note);
	}

	@Override
	public void setProgress(int nv) {
		progressBar.setValue(nv);
	}

}
