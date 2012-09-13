package com.aha.pdftools.gui;

import java.awt.Cursor;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;

import com.aha.pdftools.model.MultiReorderable;

@SuppressWarnings("serial")
public class TableMultiRowTransferHandler extends AbstractTableRowTransferHandler {
	public TableMultiRowTransferHandler(JTable table) {
		super(table, new ActivationDataFlavor(int[].class, DataFlavor.javaJVMLocalObjectMimeType, "Integer Row Indices"));
	}

	@Override
	protected Transferable createTransferable(JComponent c) {
		assert (c == table);
		return new DataHandler(table.getSelectedRows(), localObjectFlavor.getMimeType());
	}

	@Override
	public boolean importData(TransferHandler.TransferSupport info) {
		JTable target = (JTable) info.getComponent();
		JTable.DropLocation dl = (JTable.DropLocation) info.getDropLocation();
		int index = dl.getRow();
		int max = table.getModel().getRowCount();
		if (index < 0 || index > max)
			index = max;
		target.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		try {
			int[] rowsFrom = (int[]) info.getTransferable().getTransferData(localObjectFlavor);
			if (rowsFrom.length > 0) {
				index = ((MultiReorderable)table.getModel()).reorder(rowsFrom, index);
				target.getSelectionModel().addSelectionInterval(index, index + rowsFrom.length - 1);
				return true;
			}
		} catch (Exception e) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING, e.getMessage(), e);
		}
		return false;
	}
}
