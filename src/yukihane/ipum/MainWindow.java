/* $Id$ */
package yukihane.ipum;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.UIManager;

/**
 *
 * @author yuki
 */
public class MainWindow extends javax.swing.JFrame {

    private final MyTableModel model = new MyTableModel();

    /** Creates new form MainWindow */
    public MainWindow() {
        initComponents();

        DropTargetListener dtl = new DropTargetAdapter() {

            @Override
            public void dragOver(DropTargetDragEvent dtde) {
                if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    dtde.acceptDrag(DnDConstants.ACTION_COPY);
                    return;
                }
                dtde.rejectDrag();
            }

            public void drop(DropTargetDropEvent dtde) {
                try {
                    if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                        dtde.acceptDrop(DnDConstants.ACTION_COPY);
                        Transferable transferable = dtde.getTransferable();
                        List list = (List) transferable.getTransferData(DataFlavor.javaFileListFlavor);
                        for (Object o : list) {
                            if (o instanceof File) {
                                File file = (File) o;
                                model.addItem(new Item(file.getName(), file.getAbsolutePath()));
                            }
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnsupportedFlavorException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
//                dtde.rejectDrop();
            }
        };

        new DropTarget(mainTable, DnDConstants.ACTION_COPY, dtl, true);
        new DropTarget(scrollPane.getViewport(), DnDConstants.ACTION_COPY, dtl, true);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        propertyButton = new javax.swing.JButton();
        scrollPane = new javax.swing.JScrollPane();
        mainTable = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        propertyButton.setText("Property");

        mainTable.setModel(model);
        scrollPane.setViewportView(mainTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
                    .addComponent(propertyButton))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(propertyButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        setLookAndFeel();
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                JFrame frame = new MainWindow();
                frame.setLocationByPlatform(true);
                frame.setVisible(true);
            }
        });
    }

    private static void setLookAndFeel() {
        UIManager.LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();
        // Nimbusが使用可能であればそれを優先的に使用する。
        // 無ければシステムデフォルトを使用する。
        String lafClassName = UIManager.getSystemLookAndFeelClassName();
        for (UIManager.LookAndFeelInfo laf : lafs) {
            if ("Nimbus".equals(laf.getName())) {
                lafClassName = laf.getClassName();
                break;
            }
        }
        try {
            Logger.getLogger(MainWindow.class.getName()).
                    log(Level.FINEST, lafClassName);
            UIManager.setLookAndFeel(lafClassName);
        } catch (Exception ex) {
            Logger.getLogger(MainWindow.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable mainTable;
    private javax.swing.JButton propertyButton;
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables
}
