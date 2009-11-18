/* $Id$ */
package yukihane.ipum.gui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.TableColumn;
import yukihane.ipum.Config;
import yukihane.ipum.Event;
import yukihane.ipum.gui.MyTableModel.StatusCellRenderer;

/**
 *
 * @author yuki
 */
public class MainWindow extends javax.swing.JFrame {

    private final MyTableModel model = new MyTableModel();
    private final Controller controller;
    private final Thread controllerThread;
    private final DataFlavor uriFlavor;

    /** Creates new form MainWindow */
    public MainWindow() throws ClassNotFoundException {
        initComponents();

        uriFlavor = new DataFlavor("text/uri-list;class=java.lang.String");

        Config config = Config.getInstance();
        controller = new Controller(config.getThreadNum()) {

            @Override
            protected void notifyEvent(final Event event) {
                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        model.updateItem(event.getFile(), event.getStatus().getState());
                    }
                });
            }
        };
        controllerThread = new Thread(controller);
        controllerThread.start();

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                controller.stop();
                controllerThread.interrupt();
            }
        });

        final TableColumn column = mainTable.getColumnModel().getColumn(1);
        column.setCellRenderer(new StatusCellRenderer());

        DropTargetListener dtl = new DropTargetAdapter() {

            @Override
            public void dragOver(DropTargetDragEvent dtde) {
                if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor) || dtde.isDataFlavorSupported(uriFlavor)) {
                    dtde.acceptDrag(DnDConstants.ACTION_COPY);
                    return;
                }
                dtde.rejectDrag();
            }

            public void drop(DropTargetDropEvent dtde) {
                try {
                    List<File> list = new ArrayList<File>();
                    if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                        // Windows(Macも？)の場合
                        dtde.acceptDrop(DnDConstants.ACTION_COPY);
                        Transferable transferable = dtde.getTransferable();
                        list = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
                    } else if (dtde.isDataFlavorSupported(uriFlavor)) {
                        // Linuxの場合
                        dtde.acceptDrop(DnDConstants.ACTION_COPY);
                        String str = (String) dtde.getTransferable().getTransferData(uriFlavor);
                        StringTokenizer st = new StringTokenizer(str);

                        java.net.URI uri;
                        while (st.hasMoreTokens()) {
                            try {
                                uri = new java.net.URI(st.nextToken());
                                if (uri.getScheme().equals("file")) {
                                    list.add(new File(uri.getPath()));
                                }
                            } catch (java.net.URISyntaxException ex) {
                                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                    for (File file : list) {
                        if (file.isFile()) {
                            System.out.println(file.toString());
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnsupportedFlavorException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ipum - music picker ver.0.3");

        propertyButton.setText("Property");
        propertyButton.setEnabled(false);

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
                JFrame frame;
                try {
                    frame = new MainWindow();
                    frame.setLocationByPlatform(true);
                    frame.setVisible(true);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                    System.exit(-1);
                }
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
