/* $Id$ */
package yukihane.ipum.gui;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

public class MyTableModel extends AbstractTableModel {

    private final ArrayList<Item> tableData = new ArrayList<Item>();
    private final String[] COLUMN_NAMES = {"ファイル名", "ステータス"};

    public void addItem(Item item) {
        tableData.add(item);
        final int insertedRow = tableData.size() - 1;
        fireTableRowsInserted(insertedRow, insertedRow);
    }

    public void updateItem(File file, Status.State state) {
        for (Item i : tableData) {
            if (i.getInput() == file) {
                i.setStatus(state);
                fireTableDataChanged();
                break;
            }
        }
    }

    public int getRowCount() {
        return tableData.size();
    }

    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        final Item rowItem = tableData.get(rowIndex);
        Object ret = null;
        switch (columnIndex) {
            case 0:
                ret = rowItem.getInput().getName();
                break;
            case 1:
                ret = rowItem.getStatus();
                break;
            default:
        }
        return ret;
    }

    @Override
    public String getColumnName(int modelIndex) {
        return COLUMN_NAMES[modelIndex];
    }

    public static class StatusCellRenderer extends DefaultTableCellRenderer {

        private final JProgressBar b = new JProgressBar(0, 100);

        public StatusCellRenderer() {
            super();
            setOpaque(true);
//            b.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
            b.setStringPainted(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus,
                int row, int column) {
            final Status status = (Status) value;
            final Status.State state = status.getState();
            if (state == Status.State.CONVERTING) {
                b.setValue(status.getProgress());
                return b;
            } else if (state == Status.State.DONE) {
                b.setValue(100);
                return b;
            }

            String text = "";
            if (state == Status.State.NOT_STARTED) {
                text = "待機中";
            } else if (state == Status.State.FAIL) {
                text = "変換失敗";
            }

            return super.getTableCellRendererComponent(table, text, isSelected, hasFocus, row, column);
        }
    }
}

