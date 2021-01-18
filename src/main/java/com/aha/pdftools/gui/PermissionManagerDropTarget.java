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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class PermissionManagerDropTarget extends DropTargetAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionManagerDropTarget.class);

    private static final DataFlavor uriListFlavor;
    static {
        try {
            uriListFlavor = new DataFlavor("text/uri-list; class=java.lang.String");
        } catch (ClassNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private final PermissionManager manager;

    public PermissionManagerDropTarget(PermissionManager manager) {
        this.manager = manager;
    }

    private boolean openFiles(List<File> files) {
        List<File> finalFiles = new ArrayList<>();
        for (File file : files) {
            if (!file.canRead()) {
                continue;
            }
            if (file.isDirectory()) {
                finalFiles.addAll(PermissionManager.filesInFolder(file));
            } else {
                finalFiles.add(file);
            }
        }

        if (finalFiles.isEmpty()) {
            return false;
        } else {
            manager.insertFiles(finalFiles);
            return true;
        }
    }

    @Override
    public void drop(DropTargetDropEvent dtde) {
        if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            dtde.acceptDrop(DnDConstants.ACTION_COPY);
            Transferable trans = dtde.getTransferable();
            try {
                @SuppressWarnings("unchecked")
                List<File> filelist = (List<File>) trans.getTransferData(DataFlavor.javaFileListFlavor);
                dtde.dropComplete(openFiles(filelist));
            } catch (Exception e) {
                dtde.dropComplete(false);
            }
        } else if (dtde.isDataFlavorSupported(uriListFlavor)) {
            dtde.acceptDrop(DnDConstants.ACTION_COPY);
            Transferable trans = dtde.getTransferable();
            try {
                String uris = (String) trans.getTransferData(uriListFlavor);
                StringTokenizer tokenizer = new StringTokenizer(uris);
                List<File> files = new ArrayList<>();
                while (tokenizer.hasMoreTokens()) {
                    URI uri = new URI(tokenizer.nextToken());
                    files.add(new File(uri));
                }
                dtde.dropComplete(openFiles(files));
            } catch (Exception e) {
                dtde.dropComplete(false);
            }
        } else {
            dtde.rejectDrop();
        }
    }
}
