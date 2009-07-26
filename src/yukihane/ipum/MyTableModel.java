/* $Id$ */
package yukihane.ipum;

import java.awt.Component;
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
                ret = Integer.valueOf(rowItem.getProgress());
                break;
            default:
        }
        return ret;
    }

    @Override
    public String getColumnName(int modelIndex) {
        return COLUMN_NAMES[modelIndex];
    }

    public static class ProgressCellRenderer extends DefaultTableCellRenderer {

        private static final String DONE = "完了";
        private static final String NOT_START = "待機中";
        private final JProgressBar b = new JProgressBar(0, 100);

        public ProgressCellRenderer() {
            super();
            setOpaque(true);
//            b.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
            b.setStringPainted(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus,
                int row, int column) {
            Integer i = (Integer) value;
            String text = DONE;
            if (i < 0) {
                text = NOT_START;
            } else if (i < 100) {
                b.setValue(i);
                return b;
            }
            super.getTableCellRendererComponent(table, text, isSelected, hasFocus, row, column);
            return this;
        }
    }
}

