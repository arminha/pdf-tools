package com.aha.pdftools.model;

import com.jgoodies.binding.adapter.AbstractTableAdapter;
import com.jgoodies.binding.list.SelectionInList;

@SuppressWarnings("serial")
public class PdfFileTableModel extends AbstractTableAdapter<PdfFile> {

	SelectionInList<PdfFile> listModel = new SelectionInList<PdfFile>();

	public PdfFileTableModel(SelectionInList<PdfFile> listModel) {
		// TODO translate
		super(listModel, new String [] {
				"Name",
				"Path",
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
		if (columnIndex >= 2 && columnIndex <= 9) {
			return true;
		}
		return super.isCellEditable(rowIndex, columnIndex);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == 0) {
			return String.class;
		} else if (columnIndex >= 2 && columnIndex <= 9) {
			return Boolean.class;
		}
		return super.getColumnClass(columnIndex);
	}
}
