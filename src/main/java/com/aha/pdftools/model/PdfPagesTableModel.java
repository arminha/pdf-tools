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

package com.aha.pdftools.model;

import java.util.List;

import com.jgoodies.binding.adapter.AbstractTableAdapter;
import com.jgoodies.binding.list.SelectionInList;

@SuppressWarnings("serial")
public class PdfPagesTableModel extends AbstractTableAdapter<PdfPages> implements Reorderable {

    private final SelectionInList<PdfPages> listModel;

    public PdfPagesTableModel(SelectionInList<PdfPages> listModel) {
        super(listModel, "Name", "Pages");
        this.listModel = listModel;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        PdfPages pdfPages = getRow(rowIndex);
        switch (columnIndex) {
        case 0:
            return pdfPages.getName();
        case 1:
            return pdfPages.getPagesString();
        default:
            return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0 || columnIndex == 1) {
            return String.class;
        }
        return super.getColumnClass(columnIndex);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (columnIndex == 1) {
            return true;
        }
        return super.isCellEditable(rowIndex, columnIndex);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        switch (columnIndex) {
        case 1:
            PdfPages pdfPages = getRow(rowIndex);
            pdfPages.setPagesString((String) aValue);
            break;
        default:
            super.setValueAt(aValue, rowIndex, columnIndex);
            break;
        }
    }

    @Override
    public void reorder(int fromIndex, int toIndex) {
        PdfPages row = getRow(fromIndex);
        List<PdfPages> list = listModel.getList();
        if (toIndex > fromIndex) {
            toIndex = toIndex - 1;
        }
        list.remove(fromIndex);
        list.add(toIndex, row);
    }
}
