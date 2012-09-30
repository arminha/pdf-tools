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
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

import com.aha.pdftools.Messages;

@SuppressWarnings("serial")
public class AboutDialog extends JDialog implements ActionListener, HyperlinkListener {

	private final JPanel contentPanel = new JPanel();

	/**
	 * Create the dialog.
	 */
	public AboutDialog() {
		setTitle(Messages.getString("PermissionManager.AboutTitle"));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 0, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JEditorPane editorPane = new JEditorPane();
			editorPane.setEditable(false);
			editorPane.setContentType("text/html");
			try {
				editorPane.setPage(AboutDialog.class.getResource("/com/aha/pdftools/gui/about.html"));
			} catch (IOException e) {
				Logger.getLogger(AboutDialog.class.getName()).log(Level.WARNING, e.getMessage(), e);
			}
			editorPane.addHyperlinkListener(this);
			JScrollPane scrollPane = new JScrollPane(editorPane);
			contentPanel.add(scrollPane, BorderLayout.CENTER);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton closeButton = new JButton("Close");
				closeButton.setActionCommand("Close");
				closeButton.addActionListener(this);
				buttonPane.add(closeButton);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand() == "Close") {
			setVisible(false);
			dispose();
		}
	}

	@Override
	public void hyperlinkUpdate(HyperlinkEvent event) {
		if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			URL url = event.getURL();
			try {
				Desktop.getDesktop().browse(url.toURI());
			} catch (IOException e) {
				Logger.getLogger(AboutDialog.class.getName()).log(Level.WARNING, e.getMessage(), e);
			} catch (URISyntaxException e) {
				Logger.getLogger(AboutDialog.class.getName()).log(Level.WARNING, e.getMessage(), e);
			}
		}
	}
}
