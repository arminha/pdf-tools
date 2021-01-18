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

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;

import com.aha.pdftools.Messages;
import com.aha.pdftools.ProgressDisplay;

public class StatusPanel extends JPanel implements ProgressDisplay {

    private boolean canceled = false;
    private final JButton btnCancel;
    private final JProgressBar progressBar;
    private final JLabel lblStatus;
    private String message;
    private boolean taskStarted;

    /**
     * Create the panel.
     */
    public StatusPanel() {
        lblStatus = new JLabel();

        progressBar = new JProgressBar();
        progressBar.setEnabled(false);

        btnCancel = new JButton(Messages.getString("PermissionManager.Cancel")); //$NON-NLS-1$
        btnCancel.addActionListener(e -> canceled = true);
        btnCancel.setEnabled(false);

        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.linkSize(SwingConstants.VERTICAL, progressBar, btnCancel, lblStatus);
        groupLayout.setHorizontalGroup(
                groupLayout.createSequentialGroup()
                .addGap(8) // SUPPRESS CHECKSTYLE MagicNumber
                .addComponent(lblStatus)
                .addPreferredGap(ComponentPlacement.RELATED, 0, Short.MAX_VALUE)
                // SUPPRESS CHECKSTYLE MagicNumber
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
        if (taskStarted) {
            throw new IllegalStateException("already running a task"); //$NON-NLS-1$
        }
        taskStarted = true;
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
        lblStatus.setText(""); //$NON-NLS-1$
        taskStarted = false;
    }

    @Override
    public boolean isCanceled() {
        return canceled;
    }

    @Override
    public void setNote(String note) {
        lblStatus.setText(message + " " + note); //$NON-NLS-1$
    }

    @Override
    public void setProgress(int nv) {
        progressBar.setValue(nv);
    }

}
