/* $Id$ */
package yukihane.ipum;

import javax.swing.JProgressBar;
import javax.swing.table.DefaultTableModel;

public class MyTableModel extends DefaultTableModel {

    private static final ColumnContext[] columnArray = {
        new ColumnContext("ファイル名", String.class, true),
        new ColumnContext("ステータス", JProgressBar.class, true),};

    public void addItem(Item t) {
        Object[] obj = {t.getName()};
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
}

