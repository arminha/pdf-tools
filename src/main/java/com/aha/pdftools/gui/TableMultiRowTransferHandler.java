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

import com.aha.pdftools.model.MultiReorderable;

public class TableMultiRowTransferHandler extends AbstractTableRowTransferHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TableMultiRowTransferHandler.class);

    public TableMultiRowTransferHandler(JTable table) {
        super(table,
                new ActivationDataFlavor(int[].class, DataFlavor.javaJVMLocalObjectMimeType, "Integer Row Indices"));
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        assert c == getTable();
        return new DataHandler(getTable().getSelectedRows(), getLocalObjectFlavor().getMimeType());
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
            int[] rowsFrom = (int[]) info.getTransferable().getTransferData(getLocalObjectFlavor());
            if (rowsFrom.length > 0) {
                index = ((MultiReorderable) getTable().getModel()).reorder(rowsFrom, index);
                target.getSelectionModel().addSelectionInterval(index, index + rowsFrom.length - 1);
                return true;
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return false;
    }
}
