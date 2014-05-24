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

import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aha.pdftools.model.MultiReorderable;

@SuppressWarnings("serial")
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
