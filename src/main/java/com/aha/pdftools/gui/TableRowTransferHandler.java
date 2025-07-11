package com.aha.pdftools.gui;

import java.awt.Cursor;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aha.pdftools.model.Reorderable;

/**
 * Handles drag & drop row reordering.
 */
public class TableRowTransferHandler extends AbstractTableRowTransferHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TableMultiRowTransferHandler.class);

    public TableRowTransferHandler(JTable table) {
        super(table,
                new ActivationDataFlavor(Integer.class, DataFlavor.javaJVMLocalObjectMimeType, "Integer Row Index"));
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        assert c == getTable();
        return new DataHandler(getTable().getSelectedRow(), getLocalObjectFlavor().getMimeType());
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport info) {
        JTable target = (JTable) info.getComponent();
        JTable.DropLocation dl = (JTable.DropLocation) info.getDropLocation();
        int index = dl.getRow();
        int max = getTable().getModel().getRowCount();
        if (index < 0 || index > max) {
            index = max;
        }
        target.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        try {
            int rowFrom = (Integer) info.getTransferable().getTransferData(getLocalObjectFlavor());
            if (rowFrom != -1 && rowFrom != index) {
                ((Reorderable) getTable().getModel()).reorder(rowFrom, index);
                if (index > rowFrom) {
                    index--;
                }
                target.getSelectionModel().addSelectionInterval(index, index);
                return true;
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return false;
    }
}
