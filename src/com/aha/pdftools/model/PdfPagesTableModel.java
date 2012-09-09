package com.aha.pdftools.model;

import java.util.List;

import com.jgoodies.binding.adapter.AbstractTableAdapter;
import com.jgoodies.binding.list.SelectionInList;

@SuppressWarnings("serial")
public class PdfPagesTableModel extends AbstractTableAdapter<PdfPages> implements Reorderable {

	SelectionInList<PdfPages> listModel = new SelectionInList<PdfPages>();

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
			pdfPages.setPagesString((String)aValue);
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
