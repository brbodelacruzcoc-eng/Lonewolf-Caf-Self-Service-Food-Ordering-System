/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package lonewolfcafe;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import javax.swing.JFrame;

/**
 *
 * @author ysabel
 */
public class menumanagement extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(menumanagement.class.getName());
    private static final String DB_URL = "jdbc:mysql://localhost:3306/lonewolf_cafe_db";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";
 
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;
    private String activeCategory = "All";
    private final List<String> categories = new ArrayList<>();
    /**
     * Creates new form menumanagement
     */
    public menumanagement() {
        initComponents();
        getContentPane().removeAll();
        getContentPane().setLayout(new java.awt.BorderLayout());
        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        setupTable();
        setupFilters();
        ensureAvailabilityColumn();
        loadItems();
    }
    
    private void setupTable() {
        model = new DefaultTableModel(new Object[]{"ID", "Name", "Category", "Price", "Description", "Availability"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) {
                if (c == 0) return Integer.class;
                if (c == 3) return Double.class;
                return String.class;
            }
        };
        jTable1.setModel(model);
        sorter = new TableRowSorter<>(model);
        jTable1.setRowSorter(sorter);
        jTable1.setRowHeight(28);
        jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTable1.getColumnModel().getColumn(0).setMaxWidth(60);
        jTable1.getColumnModel().getColumn(3).setMaxWidth(100);
        jTable1.getColumnModel().getColumn(5).setMaxWidth(110);
 
        // double-click a row = edit it
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) showItemDialog(true);
            }
        });
        // change the button text when you click a row
        jTable1.getSelectionModel().addListSelectionListener(e -> updateToggleLabel());
    }
 
    private void setupFilters() {
        // every button inside the left category panel filters by its own text
        for (java.awt.Component c : jPanel4.getComponents()) {
            if (c instanceof JButton) {
                JButton b = (JButton) c;
                b.addActionListener(e -> {
                    activeCategory = b.getText();
                    applyFilter();
                });
            }
        }
        // typing in the search box filters the table
        jTextField1.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
        });
    }
 
    private void applyFilter() {
        final String q = jTextField1.getText().trim().toLowerCase();
        sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> e) {
                String cat = e.getStringValue(2);
                if (!activeCategory.equals("All") && !activeCategory.equals(cat)) return false;
                return q.isEmpty()
                        || e.getStringValue(1).toLowerCase().contains(q)
                        || cat.toLowerCase().contains(q)
                        || e.getStringValue(4).toLowerCase().contains(q);
            }
        });
    }
 
    // ---------- database ----------
    private Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }
 
    // adds the "availability" column if you have not run the SQL file yet
    private void ensureAvailabilityColumn() {
        try (Connection con = connect()) {
            boolean exists;
            try (ResultSet rs = con.getMetaData().getColumns(con.getCatalog(), null, "tbl_menu_items", "availability")) {
                exists = rs.next();
            }
            if (!exists) {
                try (Statement st = con.createStatement()) {
                    st.executeUpdate("ALTER TABLE tbl_menu_items ADD COLUMN availability VARCHAR(15) NOT NULL DEFAULT 'Available'");
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Could not prepare the menu table: " + ex.getMessage());
        }
    }
 
    private void loadItems() {
        model.setRowCount(0);
        categories.clear();
        String sql = "SELECT item_id, item_name, price, category, description, availability FROM tbl_menu_items ORDER BY item_id";
        try (Connection con = connect(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String cat = rs.getString("category");
                if (cat == null || cat.trim().isEmpty()) cat = "Others";
                String desc = rs.getString("description");
                String avail = rs.getString("availability");
                model.addRow(new Object[]{
                    rs.getInt("item_id"),
                    rs.getString("item_name"),
                    cat,
                    rs.getDouble("price"),
                    desc == null ? "" : desc,
                    avail == null ? "Available" : avail
                });
                if (!categories.contains(cat)) categories.add(cat);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Could not load the menu: " + ex.getMessage());
        }
        applyFilter();
        updateToggleLabel();
    }
 
    private int selectedModelRow() {
        int viewRow = jTable1.getSelectedRow();
        if (viewRow < 0) return -1;
        return jTable1.convertRowIndexToModel(viewRow);
    }
 
    // ---------- add / edit ----------
    private void showItemDialog(boolean isEdit) {
        int itemId = 0;
        String name = "", price = "", desc = "", avail = "Available";
        String category = activeCategory.equals("All") ? "" : activeCategory;
 
        if (isEdit) {
            int row = selectedModelRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please click an item in the table first.");
                return;
            }
            itemId = (Integer) model.getValueAt(row, 0);
            name = (String) model.getValueAt(row, 1);
            category = (String) model.getValueAt(row, 2);
            price = String.format("%.2f", (Double) model.getValueAt(row, 3));
            desc = (String) model.getValueAt(row, 4);
            avail = (String) model.getValueAt(row, 5);
        }
 
        JTextField nameField = new JTextField(name, 22);
        JComboBox<String> catBox = new JComboBox<>(categories.toArray(new String[0]));
        catBox.setEditable(true);               // you can also type a new category
        catBox.setSelectedItem(category);
        JTextField priceField = new JTextField(price, 22);
        JTextField descField = new JTextField(desc, 22);
        JComboBox<String> availBox = new JComboBox<>(new String[]{"Available", "Unavailable"});
        availBox.setSelectedItem(avail);
 
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Name:"));          form.add(nameField);
        form.add(new JLabel("Category:"));      form.add(catBox);
        form.add(new JLabel("Price (PHP):"));   form.add(priceField);
        form.add(new JLabel("Description:"));   form.add(descField);
        form.add(new JLabel("Availability:"));  form.add(availBox);
 
        while (true) {
            int res = JOptionPane.showConfirmDialog(this, form, isEdit ? "Edit Menu Item" : "Add Menu Item",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (res != JOptionPane.OK_OPTION) return;
 
            String n = nameField.getText().trim();
            Object catObj = catBox.getEditor().getItem();
            String c = catObj == null ? "" : catObj.toString().trim();
            String d = descField.getText().trim();
            String a = (String) availBox.getSelectedItem();
 
            if (n.isEmpty()) { JOptionPane.showMessageDialog(this, "Please enter the item name."); continue; }
            if (n.length() > 100) { JOptionPane.showMessageDialog(this, "Name is too long (max 100 letters)."); continue; }
            if (c.isEmpty()) { JOptionPane.showMessageDialog(this, "Please enter or choose a category."); continue; }
            if (c.length() > 50) { JOptionPane.showMessageDialog(this, "Category is too long (max 50 letters)."); continue; }
            if (d.length() > 255) { JOptionPane.showMessageDialog(this, "Description is too long (max 255 letters)."); continue; }
 
            BigDecimal p;
            try {
                String cleaned = priceField.getText().trim().replace("P", "").replace("p", "").replace(",", "").trim();
                p = new BigDecimal(cleaned).setScale(2, RoundingMode.HALF_UP);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Price must be a number, like 120 or 120.50.");
                continue;
            }
            if (p.signum() <= 0 || p.compareTo(new BigDecimal("99999999.99")) > 0) {
                JOptionPane.showMessageDialog(this, "Price must be more than 0.");
                continue;
            }
 
            String sql = isEdit
                    ? "UPDATE tbl_menu_items SET item_name=?, price=?, category=?, description=?, availability=? WHERE item_id=?"
                    : "INSERT INTO tbl_menu_items (item_name, price, category, description, availability) VALUES (?,?,?,?,?)";
            try (Connection con = connect(); PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, n);
                ps.setBigDecimal(2, p);
                ps.setString(3, c);
                ps.setString(4, d);
                ps.setString(5, a);
                if (isEdit) ps.setInt(6, itemId);
                ps.executeUpdate();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Could not save: " + ex.getMessage());
                return;
            }
            loadItems();
            JOptionPane.showMessageDialog(this, isEdit ? "Item updated." : "Item added.");
            return;
        }
    }
 
    // ---------- delete ----------
    private void deleteSelected() {
        int row = selectedModelRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please click an item in the table first.");
            return;
        }
        int itemId = (Integer) model.getValueAt(row, 0);
        String name = (String) model.getValueAt(row, 1);
 
        int ok = JOptionPane.showConfirmDialog(this,
                "Delete \"" + name + "\" from the menu?\nThis cannot be undone.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (ok != JOptionPane.YES_OPTION) return;
 
        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement("DELETE FROM tbl_menu_items WHERE item_id = ?")) {
            ps.setInt(1, itemId);
            ps.executeUpdate();
            loadItems();
        } catch (SQLIntegrityConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this,
                    "\"" + name + "\" cannot be deleted because it is already in past orders.\n"
                    + "Use \"Set Unavailable\" instead to hide it from customers.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Could not delete: " + ex.getMessage());
        }
    }
 
    // ---------- available / unavailable ----------
    private void updateToggleLabel() {
        int row = selectedModelRow();
        if (row >= 0 && "Unavailable".equals(model.getValueAt(row, 5))) {
            btnToggle.setText("Set Available");
        } else {
            btnToggle.setText("Set Unavailable");
        }
    }
 
    private void toggleAvailability() {
        int row = selectedModelRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please click an item in the table first.");
            return;
        }
        int itemId = (Integer) model.getValueAt(row, 0);
        String newValue = "Unavailable".equals(model.getValueAt(row, 5)) ? "Available" : "Unavailable";
 
        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement("UPDATE tbl_menu_items SET availability = ? WHERE item_id = ?")) {
            ps.setString(1, newValue);
            ps.setInt(2, itemId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Could not update: " + ex.getMessage());
            return;
        }
        model.setValueAt(newValue, row, 5);
        updateToggleLabel();
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
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel8 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButton10 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnToggle = new javax.swing.JToggleButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(245, 243, 238));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Espresso", "coffee", "P 80", "available"},
                {"Americano", "coffee", "P 100", "available"},
                {"Cortado", "coffee", "P 110", "available"},
                {"Flat White", "coffee", "P 110", "available"},
                {"Capuccino", "coffee", "P 110", "available"},
                {"Cafe Latte", "coffee", "P 120", "available"},
                {"Spanish Latte", "coffee", "P 140", "available"},
                {"Rose Latte ", "coffee", "P 150", "unavailable"},
                {"Cafe Mocha", "coffee", "P 150", "available"},
                {"Caramel Macchiato", "coffee", "P 150", "available"},
                {"Salted Caramel", "coffee", "P 150", "available"},
                {"Dirty Macha", "coffee", "P 200", "unavailable"},
                {"Black Eye", "coffee", "P 200", "available"}
            },
            new String [] {
                "Name", "Category", "Price", "Availability"
            }
        ));
        jTable1.setGridColor(new java.awt.Color(255, 255, 255));
        jScrollPane1.setViewportView(jTable1);

        jTextField1.addActionListener(this::jTextField1ActionPerformed);

        jButton1.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        jButton1.setText("Add Item");
        jButton1.addActionListener(this::jButton1ActionPerformed);

        jLabel1.setFont(new java.awt.Font("Georgia", 1, 18)); // NOI18N
        jLabel1.setText("Search:");

        jSeparator1.setBackground(new java.awt.Color(128, 111, 90));
        jSeparator1.setForeground(new java.awt.Color(128, 111, 90));

        jLabel8.setBackground(new java.awt.Color(66, 43, 9));
        jLabel8.setFont(new java.awt.Font("Georgia", 0, 24)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(66, 43, 9));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel8.setText("Menu Management");

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lonewolfcafe/lonewolf-circle.png"))); // NOI18N

        jButton10.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        jButton10.setText("Back");
        jButton10.addActionListener(this::jButton10ActionPerformed);

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jPanel4.setBackground(new java.awt.Color(245, 243, 238));

        jButton2.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        jButton2.setForeground(new java.awt.Color(66, 43, 9));
        jButton2.setText("All");

        jButton3.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        jButton3.setForeground(new java.awt.Color(66, 43, 9));
        jButton3.setText("Coffee");

        jButton4.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        jButton4.setForeground(new java.awt.Color(66, 43, 9));
        jButton4.setText("Signatures");

        jButton5.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        jButton5.setForeground(new java.awt.Color(66, 43, 9));
        jButton5.setText("Tea");

        jButton6.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        jButton6.setForeground(new java.awt.Color(66, 43, 9));
        jButton6.setText("Soda Based");

        jButton7.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        jButton7.setForeground(new java.awt.Color(66, 43, 9));
        jButton7.setText("Iced Tea");

        jButton8.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        jButton8.setForeground(new java.awt.Color(66, 43, 9));
        jButton8.setText("Lemonade");

        jButton9.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        jButton9.setForeground(new java.awt.Color(66, 43, 9));
        jButton9.setText("Frappes");

        jButton11.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        jButton11.setForeground(new java.awt.Color(66, 43, 9));
        jButton11.setText("Food & Snacks");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE)
                    .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jScrollPane2.setViewportView(jPanel3);

        btnEdit.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        btnEdit.setText("Edit");
        btnEdit.addActionListener(this::btnEditActionPerformed);

        btnDelete.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        btnDelete.setText("Delete");
        btnDelete.addActionListener(this::btnDeleteActionPerformed);

        btnToggle.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        btnToggle.setText("Set Unavailable");
        btnToggle.addActionListener(this::btnToggleActionPerformed);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel8))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(96, 96, 96)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 623, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jButton10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnEdit)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnDelete)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnToggle)))))
                .addContainerGap(145, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(18, 18, 18)))
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(79, 79, 79)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnToggle, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 427, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(106, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

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

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
    transaction transactionForm = new transaction();

    transactionForm.setVisible(true);

    this.dispose();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton10ActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
    showItemDialog(true);        // TODO add your handling code here:
    }//GEN-LAST:event_btnEditActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    showItemDialog(false);        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
    deleteSelected();        // TODO add your handling code here:
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnToggleActionPerformed
    toggleAvailability();        // TODO add your handling code here:
    }//GEN-LAST:event_btnToggleActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new menumanagement().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JToggleButton btnToggle;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
