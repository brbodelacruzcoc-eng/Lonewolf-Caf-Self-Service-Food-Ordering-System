/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package lonewolfcafe;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.prefs.Preferences;
/**
 *
 * @author ysabel
 */
public class cashierdashboard extends javax.swing.JFrame {
    
    private final Set<Integer> hiddenOrderIds = new HashSet<>();
    private final javax.swing.ButtonGroup filterGroup = new javax.swing.ButtonGroup();
    private String statusFilter = null; 
    private final Preferences prefs =
            Preferences.userNodeForPackage(cashierdashboard.class);

    private void loadHiddenOrders() {
        String saved = prefs.get("hiddenOrders", "");
        if (!saved.isEmpty()) {
            for (String id : saved.split(",")) {
                try {
                    hiddenOrderIds.add(Integer.parseInt(id));
                } catch (NumberFormatException e) {
                    // Ignore invalid saved IDs.
                }
            }
        }
    }

    private void saveHiddenOrders() {
        StringBuilder saved = new StringBuilder();
        for (int id : hiddenOrderIds) {
            if (saved.length() > 0) saved.append(",");
            saved.append(id);
        }
        prefs.put("hiddenOrders", saved.toString());
    }
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(cashierdashboard.class.getName());

    /**
     * Creates new form cashierdashboard
     */
    private void confirmPayment() {
    int selectedRow = ordersTable.getSelectedRow();

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(
            this,
            "Please select an order first.",
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
        return;
    }

    int modelRow = ordersTable.convertRowIndexToModel(selectedRow);

    int orderId = ((Number) model.getValueAt(modelRow, 0)).intValue();

    double total;
    try {
        total = Double.parseDouble(
            model.getValueAt(modelRow, 2).toString()
                .replace("P", "").trim()
        );
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(
            this, "Invalid order total.",
            "Error", JOptionPane.ERROR_MESSAGE
        );
        return;
    }

    String paymentMethod = String.valueOf(
        model.getValueAt(modelRow, 4)
    ).trim();

    String displayedStatus = String.valueOf(
        model.getValueAt(modelRow, 6)
    );

    if (!"Pending".equalsIgnoreCase(displayedStatus)) {
        JOptionPane.showMessageDialog(
            this,
            "Only Pending orders can be paid.",
            "Payment Error",
            JOptionPane.ERROR_MESSAGE
        );
        return;
    }

    boolean isCash = paymentMethod.equalsIgnoreCase("cash");

    // CASH PAYMENT DIALOG
    javax.swing.JTextField receivedField = new javax.swing.JTextField();
    javax.swing.JLabel changeLabel = new javax.swing.JLabel("P 0.00");

    javax.swing.JPanel panel = new javax.swing.JPanel(
        new java.awt.GridLayout(0, 2, 10, 12)
    );

    javax.swing.JLabel title = new javax.swing.JLabel("CONFIRM PAYMENT");
    title.setFont(new java.awt.Font("Georgia", java.awt.Font.BOLD, 18));

    panel.add(title);
    panel.add(new javax.swing.JLabel(""));
    panel.add(new javax.swing.JLabel("Order No.:"));
    panel.add(new javax.swing.JLabel("#" + orderId));
    panel.add(new javax.swing.JLabel("Payment Method:"));
    panel.add(new javax.swing.JLabel(paymentMethod));
    panel.add(new javax.swing.JLabel("Order Total:"));
    panel.add(new javax.swing.JLabel(String.format("P %.2f", total)));

    if (isCash) {
        panel.add(new javax.swing.JLabel("Amount Received:"));
        panel.add(receivedField);
        panel.add(new javax.swing.JLabel("Change Due:"));
        panel.add(changeLabel);

        receivedField.getDocument().addDocumentListener(
            new javax.swing.event.DocumentListener() {
                private void updateChange() {
                    try {
                        String input = receivedField.getText().trim();
                        double received = input.isEmpty()
                            ? 0 : Double.parseDouble(input);

                        double change = received - total;

                        if (change < 0) {
                            changeLabel.setText(
                                "Short by P " +
                                String.format("%.2f", -change)
                            );
                        } else {
                            changeLabel.setText(
                                "P " + String.format("%.2f", change)
                            );
                        }
                    } catch (NumberFormatException e) {
                        changeLabel.setText("Invalid amount");
                    }
                }

                @Override
                public void insertUpdate(
                        javax.swing.event.DocumentEvent e) {
                    updateChange();
                }

                @Override
                public void removeUpdate(
                        javax.swing.event.DocumentEvent e) {
                    updateChange();
                }

                @Override
                public void changedUpdate(
                        javax.swing.event.DocumentEvent e) {
                    updateChange();
                }
            }
        );
    } else {
        panel.add(new javax.swing.JLabel("Card Payment:"));
        panel.add(new javax.swing.JLabel("Debit/Credit Card"));
        panel.add(new javax.swing.JLabel("Verification:"));
        panel.add(new javax.swing.JLabel(
            "Check the card payment before confirming."
        ));
    }

    int choice = JOptionPane.showOptionDialog(
        this,
        panel,
        "Confirm Payment",
        JOptionPane.DEFAULT_OPTION,
        JOptionPane.PLAIN_MESSAGE,
        null,
        new Object[]{"Cancel", "Confirm Payment"},
        "Confirm Payment"
    );

    if (choice != 1) {
        return;
    }

    double received = total;

    if (isCash) {
        try {
            received = Double.parseDouble(
                receivedField.getText().trim()
            );

            if (!Double.isFinite(received) || received < total) {
                JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid amount that covers the total.",
                    "Invalid Payment",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                this,
                "Please enter a valid payment amount.",
                "Invalid Payment",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }
    } else {
        int verify = JOptionPane.showConfirmDialog(
            this,
            "Have you verified that the debit/credit card payment " +
            "was successfully completed for Order #" + orderId +
            " (P " + String.format("%.2f", total) + ")?",
            "Verify Card Payment",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (verify != JOptionPane.YES_OPTION) {
            return;
        }
    }

    int cashierUserId = UserSession.getUserId();

    if (cashierUserId <= 0) {
        JOptionPane.showMessageDialog(
            this,
            "No valid cashier is logged in. Please log in again.",
            "Payment Error",
            JOptionPane.ERROR_MESSAGE
        );
        return;
    }

    // Assign cashier and keep the order Pending.
    // Only update if the order is still Pending and unassigned.
    String updateSql =
    "UPDATE tbl_orders SET user_id = ? " +
    "WHERE order_id = ? AND status = 'Pending'";
    
    try (Connection con = DBConnection.connect()) {

        if (con == null) {
            JOptionPane.showMessageDialog(
                this, "Cannot connect to the database.",
                "Database Error", JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        try (PreparedStatement pst = con.prepareStatement(updateSql)) {
            pst.setInt(1, cashierUserId);
            pst.setInt(2, orderId);

            int updated = pst.executeUpdate();

            if (updated != 1) {
                JOptionPane.showMessageDialog(
                    this,
                    "This order has already been assigned, paid, " +
                    "or is no longer Pending. Please refresh.",
                    "Payment Not Saved",
                    JOptionPane.ERROR_MESSAGE
                );
                loadOrdersFromDatabase();
                loadBaristaOrders();
                return;
            }
        }

        String receiptMessage =
            "PAYMENT CONFIRMED!\n\n" +
            "Order No.: #" + orderId + "\n" +
            "Payment Method: " + paymentMethod + "\n" +
            "Order Total: P " + String.format("%.2f", total) + "\n";

        if (isCash) {
            receiptMessage +=
                "Amount Received: P " +
                String.format("%.2f", received) + "\n" +
                "Change Due: P " +
                String.format("%.2f", received - total) + "\n";
        } else {
            receiptMessage +=
                "Card payment manually verified by cashier.\n";
        }

        receiptMessage += "\nOrder Status: Pending";

        JOptionPane.showMessageDialog(
            this, receiptMessage,
            "Payment Successful", JOptionPane.INFORMATION_MESSAGE
        );

        loadOrdersFromDatabase();
        loadBaristaOrders();
        detailsArea.setText("");

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(
            this,
            "Payment failed:\n" + e.getMessage(),
            "Database Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}
    
    public cashierdashboard() {
        initComponents();
        loadHiddenOrders();
        filterGroup.add(jRadioButton3);   // Pending Only
        filterGroup.add(jRadioButton4);   // Preparation Only

        detailsArea.setFont(
            new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 14)
        );

        detailsArea1.setFont(
            new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 14)
        );

        setupOrdersTable();
        setupBaristaTable();

        loadOrdersFromDatabase();
        loadBaristaOrders();

        jLabel2.setText("ORDERS TO PREPARE");
        preparing.setText("STILL PREPARING");
        btnComplete1.setText("COMPLETED");
        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH); 
    }

    private DefaultTableModel model;
    private DefaultTableModel baristaModel;

    private void setupOrdersTable() {
        model = new DefaultTableModel(
            new Object[]{"Order ID", "User ID", "Total", "Type", "Payment", "Date", "Status"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        ordersTable.setModel(model);
    }

private void setupBaristaTable() {
    baristaModel = new DefaultTableModel(
        new Object[]{
            "Order ID",
            "User ID",
            "Total",
            "Type",
            "Date",
            "Status"
        }, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    ordersTable1.setModel(baristaModel);
}
    
private void loadOrdersFromDatabase() {
    model.setRowCount(0);

    String sql =
        "SELECT order_id, user_id, total_amount, " +
        "order_type, payment_method, order_date, status " +
        "FROM tbl_orders " +
        "WHERE " + (statusFilter == null
            ? "status IN ('Pending', 'Preparing') "
            : "status = ? ") +
        "ORDER BY order_date ASC";

    try (
        Connection con = DBConnection.connect();
        PreparedStatement pst = con.prepareStatement(sql)
    ) {
        if (statusFilter != null) {
            pst.setString(1, statusFilter);
        }

        try (ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                if (hiddenOrderIds.contains(orderId)) {
                    continue;
                }

                int userIdValue = rs.getInt("user_id");
                Object userId = rs.wasNull()
                    ? null : Integer.valueOf(userIdValue);

                model.addRow(new Object[]{
                    orderId,
                    userId,
                    String.format("P%.2f", rs.getDouble("total_amount")),
                    rs.getString("order_type"),
                    rs.getString("payment_method"),
                    rs.getString("order_date"),
                    rs.getString("status")
                });
            }
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(
            this,
            "Unable to load orders:\n" + e.getMessage(),
            "Database Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}
    

private void loadBaristaOrders() {
    baristaModel.setRowCount(0);

    String sql =
        "SELECT order_id, user_id, total_amount, " +
        "order_type, order_date, status " +
        "FROM tbl_orders " +
        "WHERE status IN ('Pending', 'Preparing') " +
        "AND user_id IS NOT NULL " +
        "ORDER BY order_date ASC";

    try (
        Connection conn = DBConnection.connect();
        PreparedStatement pst = conn.prepareStatement(sql);
        ResultSet rs = pst.executeQuery()
    ) {
        while (rs.next()) {
            baristaModel.addRow(new Object[]{
                rs.getInt("order_id"),
                rs.getObject("user_id"),
                rs.getDouble("total_amount"),
                rs.getString("order_type"),
                rs.getTimestamp("order_date"),
                rs.getString("status")
            });
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(
            this,
            "Error loading orders: " + e.getMessage(),
            "Database Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}


private void showBaristaOrderDetails() {
    int row = ordersTable1.getSelectedRow();

    if (row == -1) {
        detailsArea1.setText("");
        return;
    }

    int orderId = ((Number) baristaModel.getValueAt(row, 0)).intValue();

    StringBuilder details = new StringBuilder();
    details.append("ORDER NO: #").append(orderId).append("\n");
    details.append("--------------------------------\n");

    String sql =
        "SELECT mi.item_name, oi.quantity, oi.subtotal " +
        "FROM tbl_order_items oi " +
        "JOIN tbl_menu_items mi ON oi.item_id = mi.item_id " +
        "WHERE oi.order_id = ?";

    try (
        Connection con = DBConnection.connect();
        PreparedStatement pst = con.prepareStatement(sql)
    ) {
        pst.setInt(1, orderId);

        try (ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                details.append(rs.getInt("quantity"))
                    .append(" x ")
                    .append(rs.getString("item_name"))
                    .append(" - P")
                    .append(String.format("%.2f", rs.getDouble("subtotal")))
                    .append("\n");
            }
        }

        detailsArea1.setText(details.toString());
        detailsArea1.setCaretPosition(0);

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(
            this,
            "Unable to load order details:\n" + e.getMessage()
        );
    }
}


  private void showOrderDetails() {
    int selectedRow = ordersTable.getSelectedRow();

    if (selectedRow == -1) {
        detailsArea.setText("");
        return;
    }

    int modelRow = ordersTable.convertRowIndexToModel(selectedRow);
    int orderId = (Integer) model.getValueAt(modelRow, 0);

    StringBuilder details = new StringBuilder();

    details.append("ORDER NO: #").append(orderId).append("\n");
    details.append("--------------------------------\n");

    String sql = "SELECT mi.item_name, oi.quantity, oi.subtotal " +
                 "FROM tbl_order_items oi " +
                 "JOIN tbl_menu_items mi ON oi.item_id = mi.item_id " +
                 "WHERE oi.order_id = ?";

    try (
        Connection con = DBConnection.connect();
        PreparedStatement pst = con.prepareStatement(sql)
    ) {
        pst.setInt(1, orderId);

        try (ResultSet rs = pst.executeQuery()) {
            boolean hasItems = false;

            while (rs.next()) {
                hasItems = true;

                String itemName = rs.getString("item_name");
                int quantity = rs.getInt("quantity");
                double subtotal = rs.getDouble("subtotal");

                details.append(quantity)
                       .append(" x ")
                       .append(itemName)
                       .append(" - P")
                       .append(String.format("%.2f", subtotal))
                       .append("\n");
            }

            if (!hasItems) {
                details.append("No items found for this order.\n");
            }
        }

        detailsArea.setText(details.toString());
        detailsArea.setCaretPosition(0);
        
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                this,
                "Unable to complete order:\n" + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

  
  
    
private void removeSelectedFromDisplay() {
    int selectedRow = ordersTable.getSelectedRow();

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please click an order first.");
        return;
    }

    int modelRow = ordersTable.convertRowIndexToModel(selectedRow);
    int orderId = ((Number) model.getValueAt(modelRow, 0)).intValue();

    int confirm = JOptionPane.showConfirmDialog(
        this,
        "Remove Order #" + orderId + " from this display?",
        "Confirm Remove",
        JOptionPane.YES_NO_OPTION
    );

    if (confirm != JOptionPane.YES_OPTION) return;

    hiddenOrderIds.add(orderId);
    saveHiddenOrders();
    model.removeRow(modelRow);
    detailsArea.setText("");

    JOptionPane.showMessageDialog(
        this,
        "Order removed from the display only. The database record was not deleted."
    );
}


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        jRadioButton4 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        ordersTable = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        detailsArea = new javax.swing.JTextArea();
        btnComplete = new javax.swing.JButton();
        btnRemoveSelected = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        ordersTable1 = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        detailsArea1 = new javax.swing.JTextArea();
        preparing = new javax.swing.JButton();
        btnComplete1 = new javax.swing.JButton();
        btnRefresh1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(245, 243, 238));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jSplitPane1.setBackground(new java.awt.Color(245, 243, 238));

        jPanel2.setBackground(new java.awt.Color(245, 243, 238));

        jRadioButton4.setFont(new java.awt.Font("Georgia", 1, 18)); // NOI18N
        jRadioButton4.setText("Preparation Only");
        jRadioButton4.addActionListener(this::jRadioButton4ActionPerformed);

        jRadioButton3.setFont(new java.awt.Font("Georgia", 1, 18)); // NOI18N
        jRadioButton3.setText("Pending Only");
        jRadioButton3.addActionListener(this::jRadioButton3ActionPerformed);

        jLabel1.setBackground(new java.awt.Color(66, 43, 9));
        jLabel1.setFont(new java.awt.Font("Georgia", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(66, 43, 9));
        jLabel1.setText("CASHIER DASHBOARD");

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

        detailsArea.setColumns(20);
        detailsArea.setRows(5);
        jScrollPane3.setViewportView(detailsArea);

        btnComplete.setBackground(new java.awt.Color(0, 153, 204));
        btnComplete.setFont(new java.awt.Font("Georgia", 1, 18)); // NOI18N
        btnComplete.setForeground(new java.awt.Color(255, 255, 255));
        btnComplete.setText("CONFIRM PAYMENT");
        btnComplete.addActionListener(this::btnCompleteActionPerformed);

        btnRemoveSelected.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        btnRemoveSelected.setForeground(new java.awt.Color(66, 43, 9));
        btnRemoveSelected.setText("REMOVE");
        btnRemoveSelected.addActionListener(this::btnRemoveSelectedActionPerformed);

        jButton1.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(66, 43, 9));
        jButton1.setText("Logout");
        jButton1.addActionListener(this::jButton1ActionPerformed);

        btnRefresh.setFont(new java.awt.Font("Georgia", 1, 12)); // NOI18N
        btnRefresh.setForeground(new java.awt.Color(66, 43, 9));
        btnRefresh.setText("Refresh");
        btnRefresh.addActionListener(this::btnRefreshActionPerformed);

        jButton2.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        jButton2.setText("Order Status");
        jButton2.addActionListener(this::jButton2ActionPerformed);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton2))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(btnRemoveSelected, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jRadioButton4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jRadioButton3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnRefresh))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 558, Short.MAX_VALUE)
                            .addComponent(jScrollPane3)
                            .addComponent(btnComplete, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jRadioButton3)
                        .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jRadioButton4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnComplete, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRemoveSelected, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(38, Short.MAX_VALUE))
        );

        jSplitPane1.setLeftComponent(jPanel2);

        jPanel3.setBackground(new java.awt.Color(245, 243, 238));

        ordersTable1.setFont(new java.awt.Font("Serif", 0, 14)); // NOI18N
        ordersTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Select", "Order ID", "Total", "Type", "Payment", "Date", "Status"
            }
        ));
        ordersTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ordersTable1MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(ordersTable1);

        jLabel2.setBackground(new java.awt.Color(66, 43, 9));
        jLabel2.setFont(new java.awt.Font("Georgia", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(66, 43, 9));
        jLabel2.setText("BARISTA QUEUE");

        detailsArea1.setColumns(20);
        detailsArea1.setRows(5);
        jScrollPane4.setViewportView(detailsArea1);

        preparing.setBackground(new java.awt.Color(200, 100, 0));
        preparing.setFont(new java.awt.Font("Georgia", 1, 18)); // NOI18N
        preparing.setForeground(new java.awt.Color(255, 255, 255));
        preparing.setText("START PREPARING");
        preparing.addActionListener(this::preparingActionPerformed);

        btnComplete1.setBackground(new java.awt.Color(34, 139, 34));
        btnComplete1.setFont(new java.awt.Font("Georgia", 1, 18)); // NOI18N
        btnComplete1.setForeground(new java.awt.Color(255, 255, 255));
        btnComplete1.setText("ORDER COMPLETED(READY TO PICKUP)");
        btnComplete1.addActionListener(this::btnComplete1ActionPerformed);

        btnRefresh1.setFont(new java.awt.Font("Georgia", 1, 12)); // NOI18N
        btnRefresh1.setForeground(new java.awt.Color(66, 43, 9));
        btnRefresh1.setText("Refresh");
        btnRefresh1.addActionListener(this::btnRefresh1ActionPerformed);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnRefresh1))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 615, Short.MAX_VALUE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(preparing, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnComplete1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRefresh1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(preparing, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnComplete1, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        jSplitPane1.setRightComponent(jPanel3);

        jPanel1.add(jSplitPane1, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jRadioButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton4ActionPerformed
    applyStatusFilter("Preparing");           // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton4ActionPerformed

    private void jRadioButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton3ActionPerformed
    applyStatusFilter("Pending");
    }                                             

    private void applyStatusFilter(String status) {
        if (status.equals(statusFilter)) {
            statusFilter = null;
            filterGroup.clearSelection();
        } else {
            statusFilter = status;
        }
        loadOrdersFromDatabase();
        detailsArea.setText("");// TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton3ActionPerformed

    private void ordersTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ordersTableMouseClicked
    showOrderDetails();        // TODO add your handling code here:
    }//GEN-LAST:event_ordersTableMouseClicked

    private void ordersTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ordersTable1MouseClicked
    showBaristaOrderDetails();        // TODO add your handling code here:
    }//GEN-LAST:event_ordersTable1MouseClicked

    private void btnCompleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCompleteActionPerformed
    confirmPayment();                // TODO add your handling code here:
    }//GEN-LAST:event_btnCompleteActionPerformed

    private void btnRemoveSelectedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveSelectedActionPerformed
    removeSelectedFromDisplay();          // TODO add your handling code here:
    }//GEN-LAST:event_btnRemoveSelectedActionPerformed

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
    }            // If NO, stay on this page // TODO add your handling code here:          // If NO, stay on this page // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnComplete1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnComplete1ActionPerformed
    int selectedRow = ordersTable1.getSelectedRow();

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(
            this,
            "Please select an order from the right table first.",
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
        return;
    }

    int modelRow =
        ordersTable1.convertRowIndexToModel(selectedRow);

    int orderId =
        ((Number) baristaModel.getValueAt(modelRow, 0)).intValue();

    int answer = JOptionPane.showConfirmDialog(
        this,
        "Is Order #" + orderId + " complete?",
        "Confirm Order Completion",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE
    );

    // If the user clicks No, do not change the status.
    if (answer != JOptionPane.YES_OPTION) {
        return;
    }

    String sql =
        "UPDATE tbl_orders SET status = 'Completed' " +
        "WHERE order_id = ? AND status = 'Preparing'";

    try (
        Connection con = DBConnection.connect();
        PreparedStatement pst = con.prepareStatement(sql)
    ) {
        pst.setInt(1, orderId);

        int updated = pst.executeUpdate();

        if (updated == 1) {
            JOptionPane.showMessageDialog(
                this,
                "Order #" + orderId +
                " has been completed!"
            );
        } else {
            JOptionPane.showMessageDialog(
                this,
                "This order is no longer Preparing."
            );
        }

        // Reload both tables from MySQL.
        loadOrdersFromDatabase();
        loadBaristaOrders();

        detailsArea.setText("");
        detailsArea1.setText("");

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(
            this,
            "Unable to complete order:\n" + e.getMessage(),
            "Database Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
    }//GEN-LAST:event_btnComplete1ActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
    loadOrdersFromDatabase();
    loadBaristaOrders();     // TODO add your handling code here:
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void preparingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_preparingActionPerformed
    int selectedRow = ordersTable1.getSelectedRow();

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(
            this, 
            "Please select an order from the right table.",
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
        return;
    }


    int modelRow = ordersTable1.convertRowIndexToModel(selectedRow);
    DefaultTableModel model =
            (DefaultTableModel) ordersTable1.getModel();

    int orderId = Integer.parseInt(
            model.getValueAt(modelRow, 0).toString()
    );

    String sql =
    "UPDATE tbl_orders SET status = 'Preparing' " +
    "WHERE order_id = ? " +
    "AND status = 'Pending' " +
    "AND user_id IS NOT NULL";

    try (
        Connection con = DBConnection.connect();
        PreparedStatement pst = con.prepareStatement(sql)
    ) {
        pst.setInt(1, orderId);

        int updated = pst.executeUpdate();

        if (updated == 1) {
            JOptionPane.showMessageDialog(
                this,
                "Order #" + orderId + " is now Preparing!"
            );
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Only pending orders can be changed to Preparing."
            );
        }

        // Reload both tables using database data
        loadOrdersFromDatabase();
        loadBaristaOrders();

        detailsArea.setText("");
        detailsArea1.setText("");

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(
            this,
            "Unable to update order:\n" + e.getMessage(),
            "Database Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
    }//GEN-LAST:event_preparingActionPerformed

    private void btnRefresh1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefresh1ActionPerformed
    loadOrdersFromDatabase();
    loadBaristaOrders();        // TODO add your handling code here:
    }//GEN-LAST:event_btnRefresh1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
    orderstatus orderstatusForm = new orderstatus();

    orderstatusForm.setVisible(true);          // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new cashierdashboard().setVisible(true));
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnComplete;
    private javax.swing.JButton btnComplete1;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnRefresh1;
    private javax.swing.JButton btnRemoveSelected;
    private javax.swing.JTextArea detailsArea;
    private javax.swing.JTextArea detailsArea1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTable ordersTable;
    private javax.swing.JTable ordersTable1;
    private javax.swing.JButton preparing;
    // End of variables declaration//GEN-END:variables
}
