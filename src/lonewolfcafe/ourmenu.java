/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package lonewolfcafe;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author ysabel
 */
public class ourmenu extends javax.swing.JFrame {
    
    
    private String DB_URL = "jdbc:mysql://localhost:3306/lonewolf_cafe_db";
    private String DB_USER = "root";
    private String DB_PASS = "";
 
    private static class MenuRow {
        int id; String name; double price; String category; String desc;
        MenuRow(int id, String name, double price, String category, String desc) {
            this.id = id; this.name = name; this.price = price;
            this.category = category; this.desc = desc;
        }
    }
 
    private final List<MenuRow> menuRows = new ArrayList<>();
    private final List<String> menuCategories = new ArrayList<>();
    private JPanel menuGrid;
    private Icon cupIcon;
    private String activeCategory = "All";
 
    private static final Color BROWN = new Color(66, 43, 9);
    private static final Color BORDER = new Color(107, 70, 28);
    private static final Color CREAM = new Color(245, 243, 238);
    private static final Color GREY = new Color(128, 111, 90);
    
    private JPanel jPanel1;
    private JSeparator jSeparator1;
    private JButton jButton1, jButton2;
    private JLabel jLabel1, jLabel3, jLabel4, jLabel6, jLabel8;
    private JLabel jLabel7, jLabel10;


    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ourmenu.class.getName());

    /**
     * Creates new form ourmenu
     */
    public ourmenu() {
        initComponentsWithSameNav();
        loadMenu();
        buildMenuBody();
        pack();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
    
    private void initComponentsWithSameNav() {
        jPanel1 = new JPanel();
        jSeparator1 = new JSeparator();
        jButton1 = new JButton();
        jLabel1 = new JLabel();
        jLabel3 = new JLabel();
        jLabel4 = new JLabel();
        jLabel6 = new JLabel();
        jLabel8 = new JLabel();
        jLabel7 = new JLabel();
        jButton2 = new JButton();
        jLabel10 = new JLabel();
 
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jPanel1.setBackground(new Color(245, 243, 238));
 
        jSeparator1.setBackground(new Color(128, 111, 90));
        jSeparator1.setForeground(new Color(128, 111, 90));
 
        jButton1.setBackground(new Color(66, 43, 9));
        jButton1.setFont(new Font("Serif", Font.BOLD, 18));
        jButton1.setForeground(new Color(255, 255, 255));
        jButton1.setText("Login");
        jButton1.addActionListener(e -> {
            new login().setVisible(true);
            this.dispose();
        });
 
        jLabel1.setIcon(new ImageIcon(getClass().getResource("/lonewolfcafe/lonewolf-circle.png")));
 
        jLabel3.setFont(new Font("Georgia", 0, 18));
        jLabel3.setForeground(new Color(66, 43, 9));
        jLabel3.setText("Menu");
        jLabel3.setCursor(new Cursor(Cursor.HAND_CURSOR));
 
        jLabel4.setFont(new Font("Georgia", 0, 18));
        jLabel4.setForeground(new Color(66, 43, 9));
        jLabel4.setText("About");
        jLabel4.setCursor(new Cursor(Cursor.HAND_CURSOR));
        jLabel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new about().setVisible(true);
                dispose();
            }
        });
 
        jButton2.setFont(new Font("Serif", Font.BOLD, 18));
        jButton2.setForeground(new Color(66, 43, 9));
        jButton2.setText("Order Now");
        jButton2.addActionListener(e -> {
            new placingorder().setVisible(true);
            dispose();
        });
 
        jLabel6.setFont(new Font("Georgia", 0, 18));
        jLabel6.setForeground(new Color(66, 43, 9));
        jLabel6.setText("Home");
        jLabel6.setCursor(new Cursor(Cursor.HAND_CURSOR));
        jLabel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new home().setVisible(true);
                dispose();
            }
        });
 
        jLabel8.setFont(new Font("Georgia", 0, 24));
        jLabel8.setForeground(new Color(66, 43, 9));
        jLabel8.setText("The LoneWolf Cafe");
 
        jLabel7.setFont(new Font("Georgia", Font.BOLD, 48));
        jLabel7.setForeground(new Color(66, 43, 9));
        jLabel7.setText("Our Menu");
 
        jLabel10.setFont(new Font("Serif", Font.PLAIN, 14));
        jLabel10.setForeground(new Color(128, 111, 90));
        jLabel10.setText("Browse our full selection - View only");
 
        JLabel tempIcon = new JLabel();
        try {
            tempIcon.setIcon(new ImageIcon(getClass().getResource("/lonewolfcafe/lonewolf-latte-steam-final.png")));
        } catch (Exception ex) {}
        cupIcon = scaleIcon(tempIcon.getIcon(), 100);
 
        // WE WILL ADD GRID LATER IN buildMenuBody()
        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        // placeholder, real layout set in buildMenuBody
    }
 
    private void loadMenu() {
        menuRows.clear();
        menuCategories.clear();
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT item_id, item_name, price, category, description FROM tbl_menu_items WHERE availability = 'Available' ORDER BY item_id")) {
            while (rs.next()) {
                String cat = rs.getString("category");
                if (cat == null) cat = "Others";
                menuRows.add(new MenuRow(rs.getInt("item_id"), rs.getString("item_name"), rs.getDouble("price"), cat, rs.getString("description")));
                if (!menuCategories.contains(cat)) menuCategories.add(cat);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
        }
    }
 
    private Icon scaleIcon(Icon src, int h) {
        if (src instanceof ImageIcon && src.getIconHeight() > 0) {
            int w = src.getIconWidth() * h / src.getIconHeight();
            return new ImageIcon(((ImageIcon) src).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
        }
        return src;
    }
 
    private String defaultDesc(String cat) {
        if ("Coffee".equals(cat)) return "Freshly pulled, bold and smooth.";
        if ("Signatures".equals(cat)) return "A LoneWolf original.";
        if ("Food & Snacks".equals(cat)) return "Freshly made to go with your drink.";
        return "House special blend.";
    }
 
    private JPanel buildCard(MenuRow r) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER), BorderFactory.createEmptyBorder(6,6,6,6)));
        card.setPreferredSize(new Dimension(220, 250));
 
        JPanel imgPanel = new JPanel(new GridBagLayout());
        imgPanel.setBackground(CREAM);
        if (cupIcon != null) imgPanel.add(new JLabel(cupIcon));
 
        JLabel nameLbl = new JLabel(r.name);
        nameLbl.setFont(new Font("Georgia", Font.BOLD, 15));
        nameLbl.setForeground(BROWN);
        JLabel priceLbl = new JLabel(String.format("P %.2f", r.price));
        priceLbl.setFont(new Font("Georgia", Font.BOLD, 15));
        priceLbl.setForeground(BROWN);
        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        titleRow.add(nameLbl, BorderLayout.WEST);
        titleRow.add(priceLbl, BorderLayout.EAST);
 
        String d = (r.desc == null || r.desc.isEmpty()) ? defaultDesc(r.category) : r.desc;
        JLabel descLbl = new JLabel("<html>" + d + "</html>");
        descLbl.setFont(new Font("Serif", Font.PLAIN, 13));
        descLbl.setForeground(GREY);
 
        JLabel catLbl = new JLabel(r.category.toUpperCase());
        catLbl.setFont(new Font("SansSerif", Font.BOLD, 10));
        catLbl.setForeground(GREY);
 
        JPanel info = new JPanel(new BorderLayout(0,4));
        info.setBackground(Color.WHITE);
        info.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        info.add(titleRow, BorderLayout.NORTH);
        info.add(descLbl, BorderLayout.CENTER);
        info.add(catLbl, BorderLayout.SOUTH);
 
        card.add(imgPanel, BorderLayout.CENTER);
        card.add(info, BorderLayout.SOUTH);
        return card;
    }
 
    private JPanel buildFilterBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        bar.setOpaque(false);
        ButtonGroup group = new ButtonGroup();
        List<String> names = new ArrayList<>();
        names.add("All");
        names.addAll(menuCategories);
        List<JToggleButton> buttons = new ArrayList<>();
        for (String n : names) {
            JToggleButton b = new JToggleButton(n, n.equals("All"));
            b.setFont(new Font("Serif", Font.PLAIN, 14));
            b.setFocusPainted(false);
            b.setCursor(new Cursor(Cursor.HAND_CURSOR));
            group.add(b);
            buttons.add(b);
            b.addActionListener(e -> {
                activeCategory = n;
                for (JToggleButton x : buttons) {
                    x.setBackground(x.isSelected() ? BROWN : Color.WHITE);
                    x.setForeground(x.isSelected() ? Color.WHITE : BROWN);
                }
                renderMenu();
            });
            bar.add(b);
        }
        for (JToggleButton x : buttons) {
            x.setBackground(x.isSelected() ? BROWN : Color.WHITE);
            x.setForeground(x.isSelected() ? Color.WHITE : BROWN);
        }
        return bar;
    }
 
    private void renderMenu() {
        menuGrid.removeAll();
        for (MenuRow r : menuRows) {
            if (activeCategory.equals("All") || activeCategory.equals(r.category)) {
                menuGrid.add(buildCard(r));
            }
        }
        menuGrid.revalidate();
        menuGrid.repaint();
    }
 
    private void buildMenuBody() {
        JPanel filterBar = buildFilterBar();
 
        // Menu Grid
        menuGrid = new JPanel(new GridLayout(0, 3, 18, 18));
        menuGrid.setOpaque(false);
        JPanel gridHolder = new JPanel(new BorderLayout());
        gridHolder.setOpaque(false);
        gridHolder.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 10));
        gridHolder.add(menuGrid, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(gridHolder);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(24);
        renderMenu();
 
        // Title block
        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setOpaque(false);
        titleBlock.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        jLabel7.setAlignmentX(Component.LEFT_ALIGNMENT);
        jLabel10.setAlignmentX(Component.LEFT_ALIGNMENT);
        filterBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleBlock.add(jLabel7);
        titleBlock.add(jLabel10);
        titleBlock.add(Box.createVerticalStrut(12));
        titleBlock.add(filterBar);
 
        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 10));
        body.add(titleBlock, BorderLayout.NORTH);
        body.add(scroll, BorderLayout.CENTER);
 
        // === THIS IS THE KEY PART - SAME AS HOME.JAVA ===
        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSeparator1)
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(62, 62, 62)
                .addComponent(jLabel1)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addGap(372, 372, 372)
                .addComponent(jLabel6)
                .addGap(55, 55, 55)
                .addComponent(jLabel3)
                .addGap(58, 58, 58)
                .addComponent(jLabel4)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1, GroupLayout.PREFERRED_SIZE, 101, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addGap(69, 69, 69))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(body, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton2, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton1, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4)
                            .addComponent(jLabel3)
                            .addComponent(jLabel6)
                            .addComponent(jLabel8)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1)))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, GroupLayout.PREFERRED_SIZE, 2, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(body, GroupLayout.DEFAULT_SIZE, 700, Short.MAX_VALUE)
                .addContainerGap())
        );
 
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1136, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 692, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
        java.awt.EventQueue.invokeLater(() -> new ourmenu().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
