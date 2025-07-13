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
        return switch (columnIndex) {
            case 0 -> pdfFile.getName();
            case 1 -> pdfFile.getSourcePath();
            case 2 -> pdfFile.isAssembly();
            case 3 -> pdfFile.isCopy();
            case 4 -> pdfFile.isDegradedPrinting();
            case 5 -> pdfFile.isPrinting();
            case 6 -> pdfFile.isScreenReaders();
            case 7 -> pdfFile.isFillIn();
            case 8 -> pdfFile.isModifyAnnotations();
            case 9 -> pdfFile.isModifyContents();
            default -> throw new IllegalArgumentException("Invalid column index: " + columnIndex);
        };
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        PdfFile pdfFile = getRow(rowIndex);
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
            throw new IllegalArgumentException("Invalid column index: " + columnIndex);
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
