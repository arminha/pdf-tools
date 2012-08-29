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
			return pdfFile.isAllowAssembly();
		case 2:
			return pdfFile.isAllowCopy();
		case 3:
			return pdfFile.isAllowDegradedPrinting();
		case 4:
			return pdfFile.isAllowPrinting();
		case 5:
			return pdfFile.isAllowScreenReaders();
		case 6:
			return pdfFile.isAllowFillIn();
		case 7:
			return pdfFile.isAllowModifyAnnotations();
		case 8:
			return pdfFile.isAllowModifyContents();
		default:
			return null;
		}
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		PdfFile pdfFile = getRow(rowIndex);
		switch (columnIndex) {
		case 1:
			pdfFile.setAllowAssembly((Boolean)aValue);
			break;
		case 2:
			pdfFile.setAllowCopy((Boolean)aValue);
			break;
		case 3:
			pdfFile.setAllowDegradedPrinting((Boolean)aValue);
			break;
		case 4:
			pdfFile.setAllowPrinting((Boolean)aValue);
			break;
		case 5:
			pdfFile.setAllowScreenReaders((Boolean)aValue);
			break;
		case 6:
			pdfFile.setAllowFillIn((Boolean)aValue);
			break;
		case 7:
			pdfFile.setAllowModifyAnnotations((Boolean)aValue);
			break;
		case 8:
			pdfFile.setAllowModifyContents((Boolean)aValue);
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
