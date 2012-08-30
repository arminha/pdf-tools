package com.aha.pdftools.gui;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JProgressBar;
import javax.swing.JLabel;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class StatusPanel extends JPanel implements ProgressDisplay {

	boolean canceled = false;
	private JButton btnCancel;
	private JProgressBar progressBar;
	private JLabel lblStatus;

	/**
	 * Create the panel.
	 */
	public StatusPanel() {
		// TODO improve layout
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		lblStatus = new JLabel();
		add(lblStatus);

		Component horizontalGlue = Box.createHorizontalGlue();
		add(horizontalGlue);

		progressBar = new JProgressBar();
		progressBar.setEnabled(false);
		add(progressBar);

		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				canceled = true;
			}
		});
		btnCancel.setEnabled(false);
		add(btnCancel);
	}

	@Override
	public void startTask(String message, int max, boolean cancelable) {
		canceled = false;
		btnCancel.setEnabled(cancelable);
		progressBar.setEnabled(true);
		progressBar.setMaximum(max);
		progressBar.setValue(0);
		
		// TODO show message
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
		lblStatus.setText(note);
	}

	@Override
	public void setProgress(int nv) {
		progressBar.setValue(nv);
	}

}
