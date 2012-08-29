package com.aha.pdftools.model;

import com.jgoodies.binding.adapter.AbstractTableAdapter;
import com.jgoodies.binding.list.SelectionInList;

@SuppressWarnings("serial")
public class PdfFileTableModel extends AbstractTableAdapter<PdfFile> {

	SelectionInList<PdfFile> listModel = new SelectionInList<PdfFile>();

	public PdfFileTableModel(SelectionInList<PdfFile> listModel) {
		super(listModel, new String [] {
				"Source Path",
				"Assembly",
				"Copy",
				"Degraded Printing",
				"Printing",
				"Screen Readers",
				"Fill In",
				"Modify Annotations",
				"Modify Contents",
				});
	    this.listModel = listModel;
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		PdfFile pdfFile = getRow(rowIndex);
		switch (columnIndex) {
		case 0:
			return pdfFile.getSourcePath();
		case 1:
			return pdfFile.isAssembly();
		case 2:
			return pdfFile.isCopy();
		case 3:
			return pdfFile.isDegradedPrinting();
		case 4:
			return pdfFile.isPrinting();
		case 5:
			return pdfFile.isScreenReaders();
		case 6:
			return pdfFile.isFillIn();
		case 7:
			return pdfFile.isModifyAnnotations();
		case 8:
			return pdfFile.isModifyContents();
		default:
			return null;
		}
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		PdfFile pdfFile = getRow(rowIndex);
		switch (columnIndex) {
		case 1:
			pdfFile.setAssembly((Boolean)aValue);
			break;
		case 2:
			pdfFile.setCopy((Boolean)aValue);
			break;
		case 3:
			pdfFile.setDegradedPrinting((Boolean)aValue);
			break;
		case 4:
			pdfFile.setPrinting((Boolean)aValue);
			break;
		case 5:
			pdfFile.setScreenReaders((Boolean)aValue);
			break;
		case 6:
			pdfFile.setFillIn((Boolean)aValue);
			break;
		case 7:
			pdfFile.setModifyAnnotations((Boolean)aValue);
			break;
		case 8:
			pdfFile.setModifyContents((Boolean)aValue);
			break;
		default:
			super.setValueAt(aValue, rowIndex, columnIndex);
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex >= 1 && columnIndex <= 8) {
			return true;
		}
		return super.isCellEditable(rowIndex, columnIndex);
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == 0) {
			return String.class;
		} else if (columnIndex >= 1 && columnIndex <= 8) {
			return Boolean.class;
		}
		return super.getColumnClass(columnIndex);
	}
}
