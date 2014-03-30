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

import java.awt.Cursor;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragSource;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;

@SuppressWarnings("serial")
public abstract class AbstractTableRowTransferHandler extends TransferHandler {
    private final DataFlavor localObjectFlavor;
    private final JTable table;

    protected AbstractTableRowTransferHandler(JTable table, DataFlavor localObjectFlavor) {
        this.table = table;
        this.localObjectFlavor = localObjectFlavor;
    }

    public abstract boolean importData(TransferHandler.TransferSupport info);

    protected abstract Transferable createTransferable(JComponent c);

    protected JTable getTable() {
        return table;
    }

    protected DataFlavor getLocalObjectFlavor() {
        return localObjectFlavor;
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport info) {
        boolean b = info.getComponent() == table && info.isDrop() && info.isDataFlavorSupported(localObjectFlavor);
        table.setCursor(b ? DragSource.DefaultMoveDrop : DragSource.DefaultMoveNoDrop);
        return b;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY_OR_MOVE;
    }

    @Override
    protected void exportDone(JComponent c, Transferable t, int act) {
        if (act == TransferHandler.MOVE) {
            table.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }
}
