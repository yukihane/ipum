/* $Id$ */
package yukihane.ipum;

import javax.swing.table.DefaultTableModel;

public class MyTableModel extends DefaultTableModel {

    private static final ColumnContext[] columnArray = {
        new ColumnContext("Name", String.class, true),};
    private int number = 0;

    public void addItem(Item t) {
        Object[] obj = {number, t.getName(), t.getComment()};
        super.addRow(obj);
        number++;
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

