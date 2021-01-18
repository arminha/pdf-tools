/*
 * Copyright (C) 2019  Armin Häberling
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

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aha.pdftools.Messages;

public class AboutDialog extends JDialog implements ActionListener, HyperlinkListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AboutDialog.class);

    private static final int WIDTH = 450;
    private static final int HEIGHT = 300;

    /**
     * Create the dialog.
     */
    public AboutDialog() {
        setTitle(Messages.getString("PermissionManager.AboutTitle"));
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        // SUPPRESS CHECKSTYLE MagicNumber
        WindowUtil.setBoundsScaled(this, 100, 100, WIDTH, HEIGHT);
        getContentPane().setLayout(new BorderLayout());
        // SUPPRESS CHECKSTYLE MagicNumber
        JPanel contentPanel = new JPanel();
        contentPanel.setBorder(new EmptyBorder(5, 5, 0, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BorderLayout(0, 0));
        // CHECKSTYLE IGNORE AvoidNestedBlocks
        {
            JEditorPane editorPane = new JEditorPane();
            editorPane.setEditable(false);
            editorPane.setContentType("text/html");
            try {
                editorPane.setPage(AboutDialog.class.getResource("/com/aha/pdftools/gui/about.html"));
            } catch (IOException e) {
                LOGGER.warn(e.getMessage(), e);
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
        // CHECKSTYLE END IGNORE AvoidNestedBlocks
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("Close".equals(e.getActionCommand())) {
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
            } catch (IOException | URISyntaxException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
    }
}
