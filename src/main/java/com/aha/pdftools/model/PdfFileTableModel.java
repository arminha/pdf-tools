package com.aha.pdftools.model;

import java.util.ArrayList;
import java.util.List;

import com.aha.pdftools.Messages;
import com.jgoodies.binding.adapter.AbstractTableAdapter;
import com.jgoodies.binding.list.SelectionInList;

public class PdfFileTableModel extends AbstractTableAdapter<PdfFile> implements Reorderable, MultiReorderable {

    private static final int FIRST_PERMISSION_COLUMN = 2;
    private static final int LAST_PERMISSION_COLUMN = 9;

    private final SelectionInList<PdfFile> listModel;

    public PdfFileTableModel(SelectionInList<PdfFile> listModel) {
        super(listModel, Messages.getString("PdfFileTableModel.Name"), //$NON-NLS-1$
            Messages.getString("PdfFileTableModel.Path"), //$NON-NLS-1$
            Messages.getString("PdfFileTableModel.Assembly"), //$NON-NLS-1$
            Messages.getString("PdfFileTableModel.Copy"), //$NON-NLS-1$
            Messages.getString("PdfFileTableModel.DegradedPrinting"), //$NON-NLS-1$
            Messages.getString("PdfFileTableModel.Printing"), //$NON-NLS-1$
            Messages.getString("PdfFileTableModel.ScreenReaders"), //$NON-NLS-1$
            Messages.getString("PdfFileTableModel.FillIn"), //$NON-NLS-1$
            Messages.getString("PdfFileTableModel.ModifyAnnotations"), //$NON-NLS-1$
            Messages.getString("PdfFileTableModel.ModifyContents") //$NON-NLS-1$
        );
        this.listModel = listModel;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        PdfFile pdfFile = getRow(rowIndex);
        // CHECKSTYLE IGNORE MagicNumber
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
        // CHECKSTYLE END IGNORE MagicNumber
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        PdfFile pdfFile = getRow(rowIndex);
        // CHECKSTYLE IGNORE MagicNumber
        switch (columnIndex) {
        case 2:
            pdfFile.setAssembly((Boolean) aValue);
            break;
        case 3:
            pdfFile.setCopy((Boolean) aValue);
            break;
        case 4:
            pdfFile.setDegradedPrinting((Boolean) aValue);
            break;
        case 5:
            pdfFile.setPrinting((Boolean) aValue);
            break;
        case 6:
            pdfFile.setScreenReaders((Boolean) aValue);
            break;
        case 7:
            pdfFile.setFillIn((Boolean) aValue);
            break;
        case 8:
            pdfFile.setModifyAnnotations((Boolean) aValue);
            break;
        case 9:
            pdfFile.setModifyContents((Boolean) aValue);
            break;
        default:
            super.setValueAt(aValue, rowIndex, columnIndex);
        }
        // CHECKSTYLE END IGNORE MagicNumber
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
        } else if (columnIndex <= LAST_PERMISSION_COLUMN) {
            return Boolean.class;
        }
        return super.getColumnClass(columnIndex);
    }

    /**
     * 
     * @param columnIndex
     *            Index of the column
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
            value = (Boolean) getValueAt(i, columnIndex);
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
        List<PdfFile> insert = new ArrayList<>();
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
