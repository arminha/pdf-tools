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

import java.util.ArrayList;
import java.util.List;

import com.aha.pdftools.Messages;
import com.jgoodies.binding.adapter.AbstractTableAdapter;
import com.jgoodies.binding.list.SelectionInList;

@SuppressWarnings("serial")
public class PdfFileTableModel extends AbstractTableAdapter<PdfFile> implements Reorderable, MultiReorderable {

	private final int FIRST_PERMISSION_COLUMN = 2;
	private final int LAST_PERMISSION_COLUMN = 9;

	SelectionInList<PdfFile> listModel = new SelectionInList<PdfFile>();

	public PdfFileTableModel(SelectionInList<PdfFile> listModel) {
		super(listModel, new String [] {
				Messages.getString("PdfFileTableModel.Name"), //$NON-NLS-1$
				Messages.getString("PdfFileTableModel.Path"), //$NON-NLS-1$
				Messages.getString("PdfFileTableModel.Assembly"), //$NON-NLS-1$
				Messages.getString("PdfFileTableModel.Copy"), //$NON-NLS-1$
				Messages.getString("PdfFileTableModel.DegradedPrinting"), //$NON-NLS-1$
				Messages.getString("PdfFileTableModel.Printing"), //$NON-NLS-1$
				Messages.getString("PdfFileTableModel.ScreenReaders"), //$NON-NLS-1$
				Messages.getString("PdfFileTableModel.FillIn"), //$NON-NLS-1$
				Messages.getString("PdfFileTableModel.ModifyAnnotations"), //$NON-NLS-1$
				Messages.getString("PdfFileTableModel.ModifyContents"), //$NON-NLS-1$
		});
		this.listModel = listModel;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		PdfFile pdfFile = getRow(rowIndex);
		switch (columnIndex) {
		case 0:
			return pdfFile.getName();
		case 1:
			return pdfFile.getSourcePath();
		case 2:
			return pdfFile.isAssembly();
		case 3:
			return pdfFile.isCopy();
		case 4:
			return pdfFile.isDegradedPrinting();
		case 5:
			return pdfFile.isPrinting();
		case 6:
			return pdfFile.isScreenReaders();
		case 7:
			return pdfFile.isFillIn();
		case 8:
			return pdfFile.isModifyAnnotations();
		case 9:
			return pdfFile.isModifyContents();
		default:
			return null;
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		PdfFile pdfFile = getRow(rowIndex);
		switch (columnIndex) {
		case 2:
			pdfFile.setAssembly((Boolean)aValue);
			break;
		case 3:
			pdfFile.setCopy((Boolean)aValue);
			break;
		case 4:
			pdfFile.setDegradedPrinting((Boolean)aValue);
			break;
		case 5:
			pdfFile.setPrinting((Boolean)aValue);
			break;
		case 6:
			pdfFile.setScreenReaders((Boolean)aValue);
			break;
		case 7:
			pdfFile.setFillIn((Boolean)aValue);
			break;
		case 8:
			pdfFile.setModifyAnnotations((Boolean)aValue);
			break;
		case 9:
			pdfFile.setModifyContents((Boolean)aValue);
			break;
		default:
			super.setValueAt(aValue, rowIndex, columnIndex);
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex >= FIRST_PERMISSION_COLUMN && columnIndex <= LAST_PERMISSION_COLUMN) {
			return true;
		}
		return super.isCellEditable(rowIndex, columnIndex);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex < FIRST_PERMISSION_COLUMN) {
			return String.class;
		} else if (columnIndex >= FIRST_PERMISSION_COLUMN && columnIndex <= LAST_PERMISSION_COLUMN) {
			return Boolean.class;
		}
		return super.getColumnClass(columnIndex);
	}

	/**
	 * 
	 * @param columnIndex Index of the column
	 * @return The new value of the column
	 */
	public void toggleColumn(int columnIndex) {
		if (getColumnClass(columnIndex) != Boolean.class) {
			return;
		}

		int n = getRowCount();
		boolean value = true;
		for (int i = 0; i < n; i++) {
			if (!value) {
				break;
			}
			value = value && (Boolean)getValueAt(i, columnIndex);
		}
		for (int i = 0; i < n; i++) {
			setValueAt(!value, i, columnIndex);
		}
	}

	public void firePermissionsUpdated() {
		int n = getRowCount();
		for (int row = 0; row < n; row++) {
			for (int column = FIRST_PERMISSION_COLUMN; column <= LAST_PERMISSION_COLUMN; column++) {
				fireTableCellUpdated(row, column);
			}
		}
	}

	@Override
	public void reorder(int fromIndex, int toIndex) {
		PdfFile row = getRow(fromIndex);
		List<PdfFile> list = listModel.getList();
		if (toIndex > fromIndex) {
			toIndex = toIndex - 1;
		}
		list.remove(fromIndex);
		list.add(toIndex, row);
	}

	@Override
	public int reorder(int[] fromIndices, int toIndex) {
		List<PdfFile> insert = new ArrayList<PdfFile>();
		List<PdfFile> list = listModel.getList();
		for (int i = fromIndices.length - 1; i >= 0; i--) {
			int index = fromIndices[i];
			PdfFile row = getRow(index);
			insert.add(0, row);
			list.remove(index);
			if (index < toIndex) {
				toIndex--;
			}
		}
		int nextIndex = toIndex;
		for (PdfFile pdfFile : insert) {
			list.add(nextIndex, pdfFile);
			nextIndex++;
		}
		return toIndex;
	}
}
