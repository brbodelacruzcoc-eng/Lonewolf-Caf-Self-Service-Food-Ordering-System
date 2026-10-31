/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package lonewolfcafe;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.JFrame;

/**
 *
 * @author ysabel
 */
public class transaction extends javax.swing.JFrame {
    
    DefaultTableModel model;
    TableRowSorter<DefaultTableModel> sorter;
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(transaction.class.getName());

    /**
     * Creates new form transaction
     */
    public transaction() {
        initComponents();
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        setupTable();
        loadTransactions();
    }

    private void setupTable() {

        model = new DefaultTableModel(
            new Object[]{
                "Select",
                "Order ID",
                "User ID",
                "Date",
                "Total",
                "Status",
                "Order Type",
                "Payment"
            }, 0) {

        @Override
public Class<?> getColumnClass(int column) {

    switch (column) {

        case 0:
            return Boolean.class;              // Select

        case 1:
            return Integer.class;              // Order ID

        case 2:
            return Integer.class;              // User ID

        case 3:
            return java.sql.Timestamp.class;  // Date

        case 4:
            return java.math.BigDecimal.class; // Total

        default:
            return String.class;
    }
}

        @Override
        public boolean isCellEditable(int row, int column) {

            return column == 0;
        }
    };

    tblTransactions.setModel(model);

    sorter = new TableRowSorter<>(model);
    tblTransactions.setRowSorter(sorter);
}
    
    private void loadTransactions() {

    model.setRowCount(0);

    String sql = "SELECT order_id, user_id, order_date, total_amount, "
        + "status, order_type, payment_method "
        + "FROM tbl_orders "
        + "ORDER BY order_date DESC";

    try (Connection conn = DBConnection.connect();
         PreparedStatement pst = conn.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {

        while (rs.next()) {

            model.addRow(new Object[]{
                false,
                rs.getInt("order_id"),
                rs.getInt("user_id"),
                rs.getTimestamp("order_date"),
                rs.getBigDecimal("total_amount"),
                rs.getString("status"),
                rs.getString("order_type"),
                rs.getString("payment_method")
            });
        }

    } catch (SQLException e) {

        JOptionPane.showMessageDialog(
                this,
                "Error loading transactions: " + e.getMessage()
        );
    }
}
    
    private boolean archiveOrder(int orderId) {

    String insertSQL =
        "INSERT INTO tbl_archived_orders " +
        "(order_id, user_id, total_amount, order_date, " +
        "status, order_type, payment_method, archived_date) " +
        "SELECT order_id, user_id, total_amount, order_date, " +
        "status, order_type, payment_method, NOW() " +
        "FROM tbl_orders " +
        "WHERE order_id = ?";

    String deleteItemsSQL =
        "DELETE FROM tbl_order_items WHERE order_id = ?";

    String deleteOrderSQL =
        "DELETE FROM tbl_orders WHERE order_id = ?";

    try (Connection conn = DBConnection.connect()) {

        conn.setAutoCommit(false);

        try (
            PreparedStatement insert =
                conn.prepareStatement(insertSQL);

            PreparedStatement deleteItems =
                conn.prepareStatement(deleteItemsSQL);

            PreparedStatement deleteOrder =
                conn.prepareStatement(deleteOrderSQL)
        ) {

            // 1. Copy order to archive
            insert.setInt(1, orderId);

            int inserted = insert.executeUpdate();

            if (inserted == 0) {
                conn.rollback();
                return false;
            }

            // 2. Delete order items first
            deleteItems.setInt(1, orderId);
            deleteItems.executeUpdate();

            // 3. Delete the actual order
            deleteOrder.setInt(1, orderId);
            deleteOrder.executeUpdate();

            conn.commit();

            return true;

        } catch (SQLException e) {

            conn.rollback();
            throw e;
        }

    } catch (SQLException e) {

        JOptionPane.showMessageDialog(
            this,
            "Archive error: " + e.getMessage()
        );

        return false;
    }
}
    private void archiveSelected() {

    // Make sure the checkbox value is saved before reading it
    if (tblTransactions.isEditing()) {
        tblTransactions.getCellEditor().stopCellEditing();
    }

    int count = 0;

    for (int i = model.getRowCount() - 1; i >= 0; i--) {

        Boolean selected = (Boolean) model.getValueAt(i, 0);

        if (Boolean.TRUE.equals(selected)) {

            int orderId = (Integer) model.getValueAt(i, 1);

            System.out.println("Selected Order ID: " + orderId);

            if (archiveOrder(orderId)) {
                count++;
            }
        }
    }

    if (count > 0) {

        JOptionPane.showMessageDialog(
                this,
                count + " transaction(s) archived successfully."
        );

        loadTransactions();

    } else {

        JOptionPane.showMessageDialog(
                this,
                "Please select at least one transaction."
        );
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
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        logout = new javax.swing.JButton();
        txtSearch = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        cmbSort = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblTransactions = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        btnArchivebtnArchive = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(245, 243, 238));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        logout.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        logout.setForeground(new java.awt.Color(66, 43, 9));
        logout.setText("Logout");
        logout.addActionListener(this::logoutActionPerformed);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 13;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipady = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(33, 6, 0, 0);
        jPanel1.add(logout, gridBagConstraints);

        txtSearch.addActionListener(this::txtSearchActionPerformed);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 142;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(52, 5, 0, 0);
        jPanel1.add(txtSearch, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(66, 43, 9));
        jLabel2.setText("Search:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(55, 10, 0, 0);
        jPanel1.add(jLabel2, gridBagConstraints);

        cmbSort.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        cmbSort.setForeground(new java.awt.Color(66, 43, 9));
        cmbSort.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Date - Newest", "Date - Oldest", "Amount - Highest", "Amount - Lowest" }));
        cmbSort.addActionListener(this::cmbSortActionPerformed);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(52, 36, 0, 0);
        jPanel1.add(cmbSort, gridBagConstraints);

        tblTransactions.setForeground(new java.awt.Color(66, 43, 9));
        tblTransactions.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Select ", "Order ID ", "User ID", "Date ", "Total ", "Status ", "Order Type ", "Payment"
            }
        ));
        jScrollPane1.setViewportView(tblTransactions);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 16;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1063;
        gridBagConstraints.ipady = 320;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 29, 0, 31);
        jPanel1.add(jScrollPane1, gridBagConstraints);

        jButton1.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(66, 43, 9));
        jButton1.setText("Archives");
        jButton1.addActionListener(this::jButton1ActionPerformed);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 12;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipady = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(33, 6, 0, 0);
        jPanel1.add(jButton1, gridBagConstraints);

        jButton2.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        jButton2.setForeground(new java.awt.Color(66, 43, 9));
        jButton2.setText("Create Account");
        jButton2.addActionListener(this::jButton2ActionPerformed);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipady = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(33, 198, 0, 0);
        jPanel1.add(jButton2, gridBagConstraints);

        btnArchivebtnArchive.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        btnArchivebtnArchive.setForeground(new java.awt.Color(66, 43, 9));
        btnArchivebtnArchive.setText("Archive Selected");
        btnArchivebtnArchive.addActionListener(this::btnArchivebtnArchiveActionPerformed);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipady = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 29, 72, 0);
        jPanel1.add(btnArchivebtnArchive, gridBagConstraints);

        jLabel3.setBackground(new java.awt.Color(66, 43, 9));
        jLabel3.setFont(new java.awt.Font("Georgia", 1, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(66, 43, 9));
        jLabel3.setText("Transaction History");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(40, 6, 0, 0);
        jPanel1.add(jLabel3, gridBagConstraints);

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lonewolfcafe/lonewolf-circle.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 30, 0, 0);
        jPanel1.add(jLabel4, gridBagConstraints);

        jSeparator1.setForeground(new java.awt.Color(66, 43, 9));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 15;
        gridBagConstraints.ipadx = 1095;
        gridBagConstraints.ipady = 18;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        jPanel1.add(jSeparator1, gridBagConstraints);

        jButton3.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        jButton3.setText("Menu management");
        jButton3.addActionListener(this::jButton3ActionPerformed);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 11;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipady = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(33, 6, 0, 0);
        jPanel1.add(jButton3, gridBagConstraints);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnArchivebtnArchiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnArchivebtnArchiveActionPerformed
    archiveSelected();          // TODO add your handling code here:
    }//GEN-LAST:event_btnArchivebtnArchiveActionPerformed

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
    String search = txtSearch.getText().trim(); // Trim right away to clean up spaces

    if (search.isEmpty()) {
        sorter.setRowFilter(null);
    } else {
        // Pattern.quote escapes characters like ( ) [ ] + ? so they won't crash your app
        String sanitizedSearch = java.util.regex.Pattern.quote(search);

        // Better way to handle case-insensitive (?i) matching safely across all columns
        sorter.setRowFilter(
            javax.swing.RowFilter.regexFilter("(?i)" + sanitizedSearch)
        );
    }
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchActionPerformed

    private void cmbSortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbSortActionPerformed
    int selected = cmbSort.getSelectedIndex();

    switch (selected) {
        case 0: // Date - Newest
            sorter.setSortKeys(java.util.List.of(
                new javax.swing.RowSorter.SortKey(3, javax.swing.SortOrder.DESCENDING) // Index 3 is Date
            ));
            break;

        case 1: // Date - Oldest
            sorter.setSortKeys(java.util.List.of(
                new javax.swing.RowSorter.SortKey(3, javax.swing.SortOrder.ASCENDING)  // Index 3 is Date
            ));
            break;

        case 2: // Amount - Highest
            sorter.setSortKeys(java.util.List.of(
                new javax.swing.RowSorter.SortKey(4, javax.swing.SortOrder.DESCENDING) // Index 4 is Total (Amount)
            ));
            break;

        case 3: // Amount - Lowest
            sorter.setSortKeys(java.util.List.of(
                new javax.swing.RowSorter.SortKey(4, javax.swing.SortOrder.ASCENDING)  // Index 4 is Total (Amount)
            ));
            break;

        case 4: // Status
            sorter.setSortKeys(java.util.List.of(
                new javax.swing.RowSorter.SortKey(5, javax.swing.SortOrder.ASCENDING)  // Index 5 is Status
            ));
            break;
    }        // TODO add your handling code here:
    }//GEN-LAST:event_cmbSortActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    ArchivedTransactions ArchivedTransactionsForm = new ArchivedTransactions();

    ArchivedTransactionsForm.setVisible(true);

    this.dispose();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
    createacc createaccForm = new createacc();

    createaccForm.setVisible(true);

    this.dispose();            // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void logoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutActionPerformed
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
    }        // TODO add your handling code here:
    }//GEN-LAST:event_logoutActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
    menumanagement menumanagementForm = new menumanagement();

    menumanagementForm.setVisible(true);

    this.dispose();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
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
        java.awt.EventQueue.invokeLater(() -> new transaction().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnArchivebtnArchive;
    private javax.swing.JComboBox<String> cmbSort;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JButton logout;
    private javax.swing.JTable tblTransactions;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
