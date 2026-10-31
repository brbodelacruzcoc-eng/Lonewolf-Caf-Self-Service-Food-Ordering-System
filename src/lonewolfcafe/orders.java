/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package lonewolfcafe;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
/**
 *
 * @author ysabel
 */
public class orders extends javax.swing.JFrame {
    
    private DefaultTableModel model;
    
    private String DB_URL = "jdbc:mysql://localhost:3306/lonewolf_cafe_db";
    private String DB_USER = "root";
    private String DB_PASS = "";
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(orders.class.getName());

    /**
     * Creates new form orders
     */
    public orders() {
        initComponents();
        this.setExtendedState(JFrame.MAXIMIZED_BOTH); 
        detailsArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 16));
 
        setupOrdersTable();
 
        // CHANGED: default view is "All Orders" so completed orders stay visible
        jRadioButton4.setSelected(true);
        loadAllOrders();
 
        ordersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) showOrderDetails();
        });
    }
    
    private void setupOrdersTable() {
 
    model = new DefaultTableModel(
        new Object[]{
            "Select",
            "Order ID",
            "Total",
            "Type",
            "Payment",
            "Date",
            "Status"
        }, 0) {
 
        @Override
        public Class<?> getColumnClass(int column) {
 
            if (column == 0) {
                return Boolean.class;
            }
 
            if (column == 1) {
                return Integer.class;
            }
 
            return String.class;
        }
 
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 0;
        }
    };
 
    ordersTable.setModel(model);
}
    
    // NEW: refreshes the table based on which radio button is selected
    private void refreshOrders() {
        if (jRadioButton3.isSelected()) {
            loadOrders();        // Pending Only
        } else {
            loadAllOrders();     // All Orders (default)
        }
    }
    
            // ========== ENCAPSULATION ==========
    private void loadOrders() {
    DefaultTableModel model = (DefaultTableModel) ordersTable.getModel();
    model.setRowCount(0);
 
    // CHANGED: also shows 'Preparing' orders in the Pending Only view
    String sql = "SELECT order_id, total_amount, order_type, " +
                 "payment_method, order_date, status " +
                 "FROM tbl_orders " +
                 "WHERE status IN ('Pending', 'Preparing') " +
                 "ORDER BY order_date DESC";
 
    try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
 
        while (rs.next()) {
            model.addRow(new Object[]{
                false,
                rs.getInt("order_id"),
                "P" + (int) rs.getDouble("total_amount"),
                rs.getString("order_type"),
                rs.getString("payment_method"),
                rs.getString("order_date"),
                rs.getString("status")
            });
        }
 
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(
            this,
            "DB Error: " + ex.getMessage()
        );
    }
}
    private void loadAllOrders() {
    DefaultTableModel model = (DefaultTableModel) ordersTable.getModel();
    model.setRowCount(0);
 
    String sql = "SELECT order_id, total_amount, order_type, " +
                 "payment_method, order_date, status " +
                 "FROM tbl_orders " +
                 "ORDER BY order_date DESC";
 
    try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
 
        while (rs.next()) {
            model.addRow(new Object[]{
                false,
                rs.getInt("order_id"),
                "P" + (int) rs.getDouble("total_amount"),
                rs.getString("order_type"),
                rs.getString("payment_method"),
                rs.getString("order_date"),
                rs.getString("status")
            });
        }
 
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(
            this,
            "DB Error: " + ex.getMessage()
        );
    }
}
    
    private void showOrderDetails() {
        int row = ordersTable.getSelectedRow(); if(row<0) return;
        int orderId = (int) ordersTable.getValueAt(row, 1);
        bigOrderIdLabel.setText("#" + orderId);
        try {
            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            PreparedStatement ps = con.prepareStatement("SELECT mi.item_name, oi.quantity, oi.subtotal FROM tbl_order_items oi JOIN tbl_menu_items mi ON oi.item_id = mi.item_id WHERE oi.order_id = ?");
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            StringBuilder sb = new StringBuilder(); sb.append("Order ID: ").append(orderId).append("\n");
            while(rs.next()){ sb.append(rs.getInt("quantity")).append("x ").append(rs.getString("item_name")).append(" - P").append((int)rs.getDouble("subtotal")).append("\n"); }
            con.close(); detailsArea.setText(sb.toString());
        } catch(Exception ex){ detailsArea.setText("Error: "+ex.getMessage()); }
    }
    
    private void completeSelectedOrder() {
        int row = ordersTable.getSelectedRow(); if(row<0){ JOptionPane.showMessageDialog(this, "Select order!"); return; }
        int id = (int) ordersTable.getValueAt(row, 1);
        try{
            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            // CHANGED: also save the cashier who clicked Completed
               PreparedStatement ps = con.prepareStatement("UPDATE tbl_orders SET status='Completed', user_id=? WHERE order_id=?");
                ps.setInt(1, UserSession.getUserId());
                ps.setInt(2, id);
                ps.executeUpdate(); con.close();
            // CHANGED: refreshOrders() instead of loadOrders()
            JOptionPane.showMessageDialog(this, "Order #"+id+" COMPLETED!"); refreshOrders();
        }catch(Exception ex){ JOptionPane.showMessageDialog(this, "Error: "+ex.getMessage()); }
    }
    
    private void markPendingAgain() {
        int row = ordersTable.getSelectedRow(); if(row<0){ JOptionPane.showMessageDialog(this, "Select order!"); return; }
        int id = (int) ordersTable.getValueAt(row, 1);
        try{
            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            PreparedStatement ps = con.prepareStatement("UPDATE tbl_orders SET status='Pending' WHERE order_id=?");
            // CHANGED: refreshOrders() instead of loadAllOrders()
            ps.setInt(1,id); ps.executeUpdate(); con.close(); refreshOrders();
        }catch(Exception ex){ JOptionPane.showMessageDialog(this, "Error: "+ex.getMessage()); }
    }
    
    private void markPreparingAgain() {
        int row = ordersTable.getSelectedRow(); if(row<0){ JOptionPane.showMessageDialog(this, "Select order!"); return; }
        int id = (int) ordersTable.getValueAt(row, 1);
        try{
            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            // CHANGED: was 'Pending' (bug), now 'Preparing'
            PreparedStatement ps = con.prepareStatement("UPDATE tbl_orders SET status='Preparing' WHERE order_id=?");
            // CHANGED: refreshOrders() instead of loadAllOrders()
            ps.setInt(1,id); ps.executeUpdate(); con.close(); refreshOrders();
        }catch(Exception ex){ JOptionPane.showMessageDialog(this, "Error: "+ex.getMessage()); }
    }
    
private void removeSelected() {
 
    DefaultTableModel model =
            (DefaultTableModel) ordersTable.getModel();
 
    // Save the checkbox value before checking it
    if (ordersTable.isEditing()) {
        ordersTable.getCellEditor().stopCellEditing();
    }
 
    int count = 0;
 
    // Check the rows from bottom to top
    for (int i = model.getRowCount() - 1; i >= 0; i--) {
 
        Boolean selected = (Boolean) model.getValueAt(i, 0);
 
        if (Boolean.TRUE.equals(selected)) {
            model.removeRow(i);
            count++;
        }
    }
 
    if (count > 0) {
        JOptionPane.showMessageDialog(
                this,
                count + " order(s) removed from display."
        );
    } else {
        JOptionPane.showMessageDialog(
                this,
                "Please select at least one order."
        );
    }
}
 
 
private void archiveOrder(int orderId) {
 
    Connection con = null;
 
    try {
 
        con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
 
        con.setAutoCommit(false);
 
        // Copy order to archive table
        PreparedStatement archive = con.prepareStatement(
            "INSERT INTO tbl_archived_orders " +
            "(order_id, user_id, total_amount, order_date, status, order_type, payment_method, archived_date) " +
            "SELECT order_id, user_id, total_amount, order_date, status, order_type, payment_method, NOW() " +
            "FROM tbl_orders WHERE order_id = ?"
        );
 
        archive.setInt(1, orderId);
        archive.executeUpdate();
        archive.close();
 
        // Delete order items first
        PreparedStatement deleteItems = con.prepareStatement(
            "DELETE FROM tbl_order_items WHERE order_id = ?"
        );
 
        deleteItems.setInt(1, orderId);
        deleteItems.executeUpdate();
        deleteItems.close();
 
        // Delete order
        PreparedStatement deleteOrder = con.prepareStatement(
            "DELETE FROM tbl_orders WHERE order_id = ?"
        );
 
        deleteOrder.setInt(1, orderId);
        deleteOrder.executeUpdate();
        deleteOrder.close();
 
        con.commit();
 
    } catch (Exception ex) {
 
        try {
            if (con != null) {
                con.rollback();
            }
        } catch (Exception rollbackEx) {
            // ignore rollback error
        }
 
        JOptionPane.showMessageDialog(this,
            "Archive Error: " + ex.getMessage());
 
    } finally {
 
        try {
            if (con != null) {
                con.setAutoCommit(true);
                con.close();
            }
        } catch (Exception ex) {
            // ignore close error
        }
    }
}

    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jSplitPane2 = new javax.swing.JSplitPane();
        jPanel3 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jRadioButton3 = new javax.swing.JRadioButton();
        jRadioButton4 = new javax.swing.JRadioButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        ordersTable = new javax.swing.JTable();
        jButton3 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        detailsArea = new javax.swing.JTextArea();
        bigOrderIdLabel = new javax.swing.JLabel();
        btnComplete = new javax.swing.JButton();
        btnPending1 = new javax.swing.JButton();
        btnPending = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(204, 255, 255));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel2.setBackground(new java.awt.Color(245, 243, 238));

        jLabel1.setBackground(new java.awt.Color(66, 43, 9));
        jLabel1.setFont(new java.awt.Font("Georgia", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(66, 43, 9));
        jLabel1.setText("PENDING ORDERS");

        jButton1.setBackground(new java.awt.Color(66, 43, 9));
        jButton1.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Logout");
        jButton1.addActionListener(this::jButton1ActionPerformed);

        jButton2.setBackground(new java.awt.Color(66, 43, 9));
        jButton2.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Refresh");
        jButton2.addActionListener(this::jButton2ActionPerformed);

        jPanel3.setBackground(new java.awt.Color(245, 243, 238));

        jPanel5.setBackground(new java.awt.Color(245, 243, 238));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 330, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        buttonGroup1.add(jRadioButton3);
        jRadioButton3.setFont(new java.awt.Font("Georgia", 1, 20)); // NOI18N
        jRadioButton3.setText("Pending Only");
        jRadioButton3.addActionListener(this::jRadioButton3ActionPerformed);

        buttonGroup1.add(jRadioButton4);
        jRadioButton4.setFont(new java.awt.Font("Georgia", 1, 20)); // NOI18N
        jRadioButton4.setText("All Orders");
        jRadioButton4.addActionListener(this::jRadioButton4ActionPerformed);

        ordersTable.setFont(new java.awt.Font("Serif", 0, 14)); // NOI18N
        ordersTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Select", "Order ID", "Total", "Type", "Payment", "Date", "Status"
            }
        ));
        ordersTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ordersTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(ordersTable);

        jButton3.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        jButton3.setText("REMOVE");
        jButton3.addActionListener(this::jButton3ActionPerformed);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(225, 225, 225))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jRadioButton4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jRadioButton3))
                            .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRadioButton3)
                    .addComponent(jRadioButton4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 421, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(701, 701, 701)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(106, 106, 106))
        );

        jButton3.getAccessibleContext().setAccessibleName("REMOVE ");

        jSplitPane2.setLeftComponent(jPanel3);

        jPanel4.setBackground(new java.awt.Color(245, 243, 238));

        detailsArea.setColumns(20);
        detailsArea.setRows(5);
        jScrollPane2.setViewportView(detailsArea);

        bigOrderIdLabel.setBackground(new java.awt.Color(245, 243, 238));
        bigOrderIdLabel.setFont(new java.awt.Font("Serif", 1, 70)); // NOI18N
        bigOrderIdLabel.setForeground(new java.awt.Color(66, 43, 9));
        bigOrderIdLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        bigOrderIdLabel.setText("#000");
        bigOrderIdLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        btnComplete.setBackground(new java.awt.Color(34, 139, 34));
        btnComplete.setFont(new java.awt.Font("Georgia", 1, 18)); // NOI18N
        btnComplete.setForeground(new java.awt.Color(255, 255, 255));
        btnComplete.setText("MARK AS COMPLETED");
        btnComplete.addActionListener(this::btnCompleteActionPerformed);

        btnPending1.setBackground(new java.awt.Color(243, 206, 2));
        btnPending1.setFont(new java.awt.Font("Georgia", 1, 18)); // NOI18N
        btnPending1.setForeground(new java.awt.Color(255, 255, 255));
        btnPending1.setText("MARK AS PREPARING");
        btnPending1.addActionListener(this::btnPending1ActionPerformed);

        btnPending.setBackground(new java.awt.Color(200, 100, 0));
        btnPending.setFont(new java.awt.Font("Georgia", 1, 18)); // NOI18N
        btnPending.setForeground(new java.awt.Color(255, 255, 255));
        btnPending.setText("MARK AS PENDING");
        btnPending.addActionListener(this::btnPendingActionPerformed);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(bigOrderIdLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 743, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 372, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(btnPending1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnComplete, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 365, Short.MAX_VALUE)
                            .addComponent(btnPending, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(15, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(bigOrderIdLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(0, 91, Short.MAX_VALUE)
                        .addComponent(btnComplete, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPending1, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPending, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28))
                    .addComponent(jScrollPane2))
                .addGap(161, 161, 161))
        );

        jSplitPane2.setRightComponent(jPanel4);

        jSeparator1.setForeground(new java.awt.Color(66, 43, 9));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lonewolfcafe/lonewolf-circle.png"))); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(52, 52, 52))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jSplitPane2))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSeparator1)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel3))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jSplitPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 620, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(158, 158, 158))
        );

        jPanel1.add(jPanel2, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
    refreshOrders();    // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        int confirm = JOptionPane.showConfirmDialog(  //confirmation dialog
        this,
        "Are you sure you want to logout?",
        "Confirm Logout",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE
    );
    // If YES, then logout
    if (confirm == JOptionPane.YES_OPTION) {
        new login().setVisible(true); 
        this.dispose();
    }
    // If NO, stay on this page // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnPendingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPendingActionPerformed
        markPendingAgain();        // TODO add your handling code here:
    }//GEN-LAST:event_btnPendingActionPerformed

    private void btnCompleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCompleteActionPerformed
        completeSelectedOrder();        // TODO add your handling code here:
    }//GEN-LAST:event_btnCompleteActionPerformed

    private void jRadioButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton4ActionPerformed
        loadAllOrders();        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton4ActionPerformed

    private void jRadioButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton3ActionPerformed
        loadOrders();        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton3ActionPerformed

    private void ordersTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ordersTableMouseClicked
        showOrderDetails();    // TODO add your handling code here:
    }//GEN-LAST:event_ordersTableMouseClicked

    private void btnPending1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPending1ActionPerformed
        markPreparingAgain();         // TODO add your handling code here:
    }//GEN-LAST:event_btnPending1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
    removeSelected();       // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new orders().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel bigOrderIdLabel;
    private javax.swing.JButton btnComplete;
    private javax.swing.JButton btnPending;
    private javax.swing.JButton btnPending1;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JTextArea detailsArea;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JTable ordersTable;
    // End of variables declaration//GEN-END:variables
}
