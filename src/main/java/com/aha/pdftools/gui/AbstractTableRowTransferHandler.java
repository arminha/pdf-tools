package com.aha.pdftools.gui;

import java.awt.Cursor;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragSource;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;

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
