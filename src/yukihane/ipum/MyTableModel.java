/* $Id$ */
package yukihane.ipum;

import java.awt.Component;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class MyTableModel extends DefaultTableModel {

    private static final ColumnContext[] columnArray = {
        new ColumnContext("ファイル名", String.class, true),
        new ColumnContext("ステータス", JProgressBar.class, true),};

    public void addItem(Item t) {
        Object[] obj = {t.getName(), Integer.valueOf(-1)};
        super.addRow(obj);
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return columnArray[col].isEditable;
    }

    @Override
    public Class<?> getColumnClass(int modelIndex) {
        return columnArray[modelIndex].columnClass;
    }

    @Override
    public int getColumnCount() {
        return columnArray.length;
    }

    @Override
    public String getColumnName(int modelIndex) {
        return columnArray[modelIndex].columnName;
    }

    private static class ColumnContext {

        public final String columnName;
        public final Class columnClass;
        public final boolean isEditable;

        public ColumnContext(String columnName, Class columnClass, boolean isEditable) {
            this.columnName = columnName;
            this.columnClass = columnClass;
            this.isEditable = isEditable;
        }
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

