/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package lonewolfcafe;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.*;
/**
 *
 * @author ysabel
 */
abstract class Product {
    protected String name;
    protected double price;
    public Product(String name, double price) { this.name = name; this.price = price; }
    public abstract double calculateSubtotal();
    public abstract String getDisplayName();
    public String getName() { return name; }
    public double getPrice() { return price; }
}
class CartItem extends Product {
    private int id; private int qty;
    public CartItem(int id, String name, double price, int qty) { super(name, price); this.id = id; this.qty = qty; }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getQty() { return qty; }
    public void setQty(int qty) { if(qty > 0) this.qty = qty; }
    @Override public double calculateSubtotal() { return price * qty; }
    @Override public String getDisplayName() { return name + " x" + qty; }
    public double sub() { return calculateSubtotal(); }
}
public class placingorder extends javax.swing.JFrame {
    private Map<Integer, CartItem> cart = new LinkedHashMap<>();
    private String DB_URL = "jdbc:mysql://localhost:3306/lonewolf_cafe_db";
    private String DB_USER = "root";
    private String DB_PASS = "";
    private static final int MAX_QTY_PER_ITEM = 20;     // max of one drink per order
    private static final int MAX_ITEMS_PER_ORDER = 50;  // max total drinks per order
    
    private String loggedInCashier = "";
    private int loggedInCashierId = 0;
 
    JPanel orderListPanel = new JPanel();
    JLabel subtotalLabel;
    JRadioButton rbDineIn, rbTakeOut, rbCash, rbCashless;
    String selectedOrderType = "Dine In";
    String selectedPayment = "Cash";
    JPanel yourorder = new JPanel();
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(placingorder.class.getName());
    /**
     * Creates new form placingorder
     */
    public placingorder() {
        initCustomHeader();
        buildPermanentOrderPanel();
        buildMenuLayout();
    }
    private JPanel navi;
    private JSeparator jSeparator1;
    private JLabel jLabel1, jLabel3, jLabel4, jLabel6, jLabel8;
    private JLabel jLabel7, jLabel10;
    private JButton jButtonLogin, jButtonOrderNow;
    private JLabel jLabel43;
 
    private void initCustomHeader() {
        navi = new JPanel();
        navi.setBackground(new Color(245, 243, 238));
        navi.setBorder(BorderFactory.createLineBorder(new Color(107, 70, 28)));
        jSeparator1 = new JSeparator();
        jSeparator1.setBackground(new Color(128, 111, 90));
        jSeparator1.setForeground(new Color(128, 111, 90));
        jLabel1 = new JLabel();
        try { jLabel1.setIcon(new ImageIcon(getClass().getResource("/lonewolfcafe/lonewolf-circle.png"))); } catch(Exception e){ jLabel1.setText("🐺"); }
        jLabel8 = new JLabel("The LoneWolf Cafe");
        jLabel8.setFont(new Font("Georgia", 0, 24));
        jLabel8.setForeground(new Color(66, 43, 9));
        jLabel6 = new JLabel("Home");
        jLabel6.setFont(new Font("Georgia", 0, 18));
        jLabel6.setForeground(new Color(66, 43, 9));
        jLabel6.setCursor(new Cursor(Cursor.HAND_CURSOR));
        jLabel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) { new home().setVisible(true); dispose(); }
        });
        jLabel3 = new JLabel("Menu");
        jLabel3.setFont(new Font("Georgia", 0, 18));
        jLabel3.setForeground(new Color(66, 43, 9));
        jLabel3.setCursor(new Cursor(Cursor.HAND_CURSOR));
        jLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) { new ourmenu().setVisible(true); dispose(); }
        });
        jLabel4 = new JLabel("About");
        jLabel4.setFont(new Font("Georgia", 0, 18));
        jLabel4.setForeground(new Color(66, 43, 9));
        jLabel4.setCursor(new Cursor(Cursor.HAND_CURSOR));
        jLabel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) { new about().setVisible(true); dispose(); }
        });
        jButtonLogin = new JButton("Login");
        jButtonLogin.setBackground(new Color(66, 43, 9));
        jButtonLogin.setFont(new Font("Serif", Font.BOLD, 18));
        jButtonLogin.setForeground(Color.WHITE);
        jButtonLogin.addActionListener(e -> { new login().setVisible(true); dispose(); });
        jButtonOrderNow = new JButton("Order Now");
        jButtonOrderNow.setFont(new Font("Serif", Font.BOLD, 18));
        jButtonOrderNow.setForeground(new Color(66, 43, 9));
        jButtonOrderNow.setText("Order Now");
        jLabel7 = new JLabel("Our Menu");
        jLabel7.setFont(new Font("Georgia", Font.BOLD, 48));
        jLabel7.setForeground(new Color(66, 43, 9));
        jLabel10 = new JLabel("Pick, confirm, pay at counter. Logged-in flow.");
        jLabel10.setFont(new Font("Serif", Font.PLAIN, 14));
        jLabel10.setForeground(new Color(128, 111, 90));
        jLabel43 = new JLabel();
        try { jLabel43.setIcon(new ImageIcon(getClass().getResource("/lonewolfcafe/lonewolf-latte-steam-final.png"))); } catch(Exception ex){}
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Place Order - The LoneWolf Cafe");
    }
 
    private void buildPermanentOrderPanel(){
        yourorder.removeAll();
        yourorder.setLayout(new BorderLayout());
        yourorder.setBackground(Color.WHITE);
        yourorder.setBorder(BorderFactory.createLineBorder(new Color(107,70,28)));
        yourorder.setPreferredSize(new Dimension(340, 600));
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);
        top.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        JLabel title = new JLabel("YOUR ORDER");
        title.setFont(new Font("Serif", Font.BOLD, 16));
        top.add(title, BorderLayout.WEST);
        yourorder.add(top, BorderLayout.NORTH);
        orderListPanel = new JPanel();
        orderListPanel.setLayout(new BoxLayout(orderListPanel, BoxLayout.Y_AXIS));
        orderListPanel.setBackground(Color.WHITE);
        JLabel empty = new JLabel("No items yet. Add a drink!");
        empty.setForeground(new Color(150,150,150));
        empty.setFont(new Font("Serif", Font.ITALIC, 12));
        empty.setBorder(BorderFactory.createEmptyBorder(20,15,0,15));
        orderListPanel.add(empty);
        JScrollPane scroll = new JScrollPane(orderListPanel);
        scroll.setBorder(null);
        yourorder.add(scroll, BorderLayout.CENTER);
        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setBackground(Color.WHITE);
        bottom.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(128,111,90));
        JPanel subPanel = new JPanel(new BorderLayout());
        subPanel.setBackground(Color.WHITE);
        subPanel.setMaximumSize(new Dimension(450, 30));
        subPanel.setBorder(BorderFactory.createEmptyBorder(5,0,5,0));
        JLabel subText = new JLabel("Subtotal");
        subText.setFont(new Font("Serif", Font.PLAIN, 12));
        subtotalLabel = new JLabel("P 000.00");
        subtotalLabel.setFont(new Font("Serif", Font.BOLD, 14));
        subPanel.add(subText, BorderLayout.WEST);
        subPanel.add(subtotalLabel, BorderLayout.EAST);
        JPanel orderTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        orderTypePanel.setBackground(Color.WHITE);
        orderTypePanel.setBorder(BorderFactory.createTitledBorder("Order Type"));
        orderTypePanel.setMaximumSize(new Dimension(450, 55));
        rbDineIn = new JRadioButton("Dine In", true);
        rbTakeOut = new JRadioButton("Take Out");
        ButtonGroup g1 = new ButtonGroup(); g1.add(rbDineIn); g1.add(rbTakeOut);
        orderTypePanel.add(rbDineIn); orderTypePanel.add(rbTakeOut);
        rbDineIn.addActionListener(e -> selectedOrderType = "Dine In");
        rbTakeOut.addActionListener(e -> selectedOrderType = "Take Out");
        JPanel paymentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        paymentPanel.setBackground(Color.WHITE);
        paymentPanel.setBorder(BorderFactory.createTitledBorder("Payment"));
        paymentPanel.setMaximumSize(new Dimension(450, 55));
        rbCash = new JRadioButton("Cash", true);
        rbCashless = new JRadioButton("Cashless");
        ButtonGroup g2 = new ButtonGroup(); g2.add(rbCash); g2.add(rbCashless);
        paymentPanel.add(rbCash); paymentPanel.add(rbCashless);
        rbCash.addActionListener(e -> selectedPayment = "Cash");
        rbCashless.addActionListener(e -> selectedPayment = "Cashless");
        JButton placeBtn = new JButton("Place Order");
        placeBtn.setBackground(new Color(66,43,9));
        placeBtn.setForeground(Color.WHITE);
        placeBtn.setFont(new Font("Serif", Font.BOLD, 14));
        placeBtn.setMaximumSize(new Dimension(400, 40));
        placeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        placeBtn.addActionListener(e -> placeOrder());
        bottom.add(sep);
        bottom.add(Box.createVerticalStrut(8));
        bottom.add(subPanel);
        bottom.add(Box.createVerticalStrut(5));
        bottom.add(orderTypePanel);
        bottom.add(Box.createVerticalStrut(5));
        bottom.add(paymentPanel);
        bottom.add(Box.createVerticalStrut(10));
        bottom.add(placeBtn);
        yourorder.add(bottom, BorderLayout.SOUTH);
        yourorder.revalidate();
        yourorder.repaint();
    }
    
    private int getTotalQty() {
        int t = 0;
        for (CartItem i : cart.values()) t += i.getQty();
        return t;
    }
 
    private boolean canAddOne(int id) {
        if (getTotalQty() >= MAX_ITEMS_PER_ORDER) {
            JOptionPane.showMessageDialog(this,
                    "Maximum of " + MAX_ITEMS_PER_ORDER + " items per order.");
            return false;
        }
        if (cart.containsKey(id) && cart.get(id).getQty() >= MAX_QTY_PER_ITEM) {
            JOptionPane.showMessageDialog(this,
                    "Maximum of " + MAX_QTY_PER_ITEM + " per item.");
            return false;
        }
        return true;
    }
 
    private void addToCart(int id, String name, double price){
        if(!canAddOne(id)) return;
        if(cart.containsKey(id)){ cart.get(id).setQty(cart.get(id).getQty() + 1); }
        else { cart.put(id, new CartItem(id, name, price, 1)); }
        refreshOrderListOnly();
    }
 
    private void refreshOrderListOnly(){
        orderListPanel.removeAll();
        double total = 0;
        if(cart.isEmpty()){
            JLabel empty = new JLabel("No items yet. Add a drink!");
            empty.setForeground(new Color(150,150,150));
            empty.setFont(new Font("Serif", Font.ITALIC, 12));
            empty.setBorder(BorderFactory.createEmptyBorder(20,15,0,15));
            orderListPanel.add(empty);
            subtotalLabel.setText("P 000.00");
        } else {
            for(CartItem item : cart.values()){
                JPanel row = new JPanel(new BorderLayout());
                row.setBackground(Color.WHITE);
                row.setBorder(BorderFactory.createEmptyBorder(6,10,6,10));
                row.setMaximumSize(new Dimension(400, 45));
                JLabel nameLbl = new JLabel("<html><b>"+item.getName()+"</b><br><font size=2>P"+(int)item.getPrice()+" each</font></html>");
                nameLbl.setFont(new Font("Serif", Font.PLAIN, 14));
                JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
                right.setBackground(Color.WHITE);
                JLabel subLbl = new JLabel("P"+(int)item.calculateSubtotal());
                subLbl.setFont(new Font("Serif", Font.BOLD, 14));
                JButton minus = new JButton("-"); JLabel qty = new JLabel(String.valueOf(item.getQty())); JButton plus = new JButton("+");
                minus.setMargin(new Insets(2,5,2,5)); plus.setMargin(new Insets(2,5,2,5));
                int itemId = item.getId();
                minus.addActionListener(e -> { if(cart.get(itemId).getQty() > 1) cart.get(itemId).setQty(cart.get(itemId).getQty() - 1); else cart.remove(itemId); refreshOrderListOnly(); });
                plus.addActionListener(e -> { if(canAddOne(itemId)) { cart.get(itemId).setQty(cart.get(itemId).getQty() + 1); refreshOrderListOnly(); } });
                right.add(subLbl); right.add(minus); right.add(qty); right.add(plus);
                row.add(nameLbl, BorderLayout.WEST); row.add(right, BorderLayout.EAST);
                orderListPanel.add(row); total += item.calculateSubtotal();
            }
            subtotalLabel.setText("P "+(int)total);
        }
        orderListPanel.revalidate(); orderListPanel.repaint();
    }
 
    
    private void placeOrder() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty!");
            return;
        }
 
        double total = 0;
 
        for (CartItem item : cart.values()) {
            total += item.calculateSubtotal();
        }
 
        String sql =
            "INSERT INTO tbl_orders " +
            "(user_id, total_amount, order_type, payment_method, status) " +
            "VALUES (NULL, ?, ?, ?, 'Pending')";
 
        try (Connection con =
                DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = con.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
 
            ps.setDouble(1, total);
            ps.setString(2, selectedOrderType);
            ps.setString(3, selectedPayment);
 
            ps.executeUpdate();
 
            int orderId;
 
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (!rs.next()) {
                    throw new SQLException("Could not get the order ID.");
                }
 
                orderId = rs.getInt(1);
            }
 
            String itemSql =
                "INSERT INTO tbl_order_items " +
                "(order_id, item_id, quantity, subtotal) " +
                "VALUES (?, ?, ?, ?)";
 
            try (PreparedStatement ps2 = con.prepareStatement(itemSql)) {
                for (CartItem item : cart.values()) {
                    ps2.setInt(1, orderId);
                    ps2.setInt(2, item.getId());
                    ps2.setInt(3, item.getQty());
                    ps2.setDouble(4, item.calculateSubtotal());
                    ps2.addBatch();
                }
 
                ps2.executeBatch();
            }
 
            showSimpleReceipt(orderId, total);
 
            cart.clear();
            refreshOrderListOnly();
 
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "DB Error: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
 
    
    // === FIXED RECEIPT - CENTERED + NUMBER ON TOP + NO NEW ORDER BUTTON ===
    private void showSimpleReceipt(int orderId, double total){
        String dateTime = new java.text.SimpleDateFormat("MM/dd/yyyy hh:mm:ss a").format(new java.util.Date());
        String receiptNoStr = String.format("%06d", orderId);
 
        JDialog d = new JDialog(this, "Receipt #" + receiptNoStr, true);
        d.setSize(420, 620);
        d.setLocationRelativeTo(this);
        d.setLayout(new BorderLayout());
        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(25,25,25,25));
 
        // 1. ORDER # ON TOP - CENTERED
        JLabel orderTxt = new JLabel("ORDER #", SwingConstants.CENTER);
        orderTxt.setFont(new Font("SansSerif", Font.BOLD, 14));
        orderTxt.setForeground(new Color(66,43,9));
        orderTxt.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        JLabel bigId = new JLabel(String.valueOf(orderId), SwingConstants.CENTER);
        bigId.setFont(new Font("Georgia", Font.BOLD, 90));
        bigId.setForeground(new Color(66,43,9));
        bigId.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        JLabel cafe = new JLabel("The LoneWolf Cafe", SwingConstants.CENTER);
        cafe.setFont(new Font("Serif", Font.BOLD, 22));
        cafe.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        JLabel address = new JLabel("Cagayan de Oro City", SwingConstants.CENTER);
        address.setFont(new Font("SansSerif", Font.PLAIN, 11));
        address.setForeground(Color.GRAY);
        address.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        JSeparator sepLine = new JSeparator(SwingConstants.HORIZONTAL);
        sepLine.setMaximumSize(new Dimension(300, 1));
        sepLine.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        // 2. INFO - CENTERED
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        JLabel lblReceiptNo = new JLabel("Receipt No: #" + receiptNoStr, SwingConstants.CENTER);
        lblReceiptNo.setFont(new Font("Monospaced", Font.BOLD, 12));
        lblReceiptNo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblDate = new JLabel(dateTime, SwingConstants.CENTER);
        lblDate.setFont(new Font("Monospaced", Font.PLAIN, 11));
        lblDate.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblCashier = new JLabel("Cashier: " + loggedInCashier, SwingConstants.CENTER);
        lblCashier.setFont(new Font("Monospaced", Font.PLAIN, 11));
        lblCashier.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        JLabel lblOrderType = new JLabel(selectedOrderType + " | " + selectedPayment, SwingConstants.CENTER);
        lblOrderType.setFont(new Font("Monospaced", Font.PLAIN, 11));
        lblOrderType.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        infoPanel.add(lblReceiptNo);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(lblDate);
        infoPanel.add(lblOrderType);
 
        // 3. ITEMS - CENTERED
        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBackground(Color.WHITE);
        itemsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        JLabel itemHeader = new JLabel(String.format("%-18s %3s %8s", "ITEM", "QTY", "AMOUNT"));
        itemHeader.setFont(new Font("Monospaced", Font.BOLD, 11));
        itemHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        JSeparator sep2 = new JSeparator(SwingConstants.HORIZONTAL);
        sep2.setMaximumSize(new Dimension(300, 1));
        sep2.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        itemsPanel.add(Box.createVerticalStrut(8));
        itemsPanel.add(itemHeader);
        itemsPanel.add(Box.createVerticalStrut(4));
        itemsPanel.add(sep2);
        itemsPanel.add(Box.createVerticalStrut(4));
 
        for(CartItem item : cart.values()){
            String name = item.getName();
            if(name.length() > 18) name = name.substring(0, 18);
            JLabel row = new JLabel(String.format("%-18s %3d %8s", name, item.getQty(), "P"+(int)item.calculateSubtotal()));
            row.setFont(new Font("Monospaced", Font.PLAIN, 11));
            row.setAlignmentX(Component.CENTER_ALIGNMENT);
            itemsPanel.add(row);
        }
 
        JSeparator sep3 = new JSeparator(SwingConstants.HORIZONTAL);
        sep3.setMaximumSize(new Dimension(300, 1));
        sep3.setAlignmentX(Component.CENTER_ALIGNMENT);
        itemsPanel.add(Box.createVerticalStrut(4));
        itemsPanel.add(sep3);
        itemsPanel.add(Box.createVerticalStrut(6));
 
        JLabel totalLbl = new JLabel(String.format("%-18s %12s", "TOTAL:", "P" + String.format("%.2f", total)));
        totalLbl.setFont(new Font("Monospaced", Font.BOLD, 14));
        totalLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        itemsPanel.add(totalLbl);
 
        JLabel note = new JLabel("Thank you! Wait for your number", SwingConstants.CENTER);
        note.setFont(new Font("SansSerif", Font.ITALIC, 11));
        note.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        // NO BUTTON - just X to close
 
        p.add(orderTxt);
        p.add(bigId);
        p.add(Box.createVerticalStrut(10));
        p.add(cafe);
        p.add(address);
        p.add(Box.createVerticalStrut(10));
        p.add(sepLine);
        p.add(Box.createVerticalStrut(10));
        p.add(infoPanel);
        p.add(Box.createVerticalStrut(10));
        p.add(sepLine);
        p.add(Box.createVerticalStrut(10));
        p.add(itemsPanel);
        p.add(Box.createVerticalStrut(18));
        p.add(note);
 
        JScrollPane scroll = new JScrollPane(p);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        d.add(scroll, BorderLayout.CENTER);

        // ===== BUTTONS =====
        JButton btnNewOrder = new JButton("New Order");
        btnNewOrder.setBackground(new Color(66, 43, 9));
        btnNewOrder.setForeground(Color.WHITE);
        btnNewOrder.setFont(new Font("Serif", Font.BOLD, 14));
        btnNewOrder.setPreferredSize(new Dimension(150, 40));
        btnNewOrder.setFocusPainted(false);
        btnNewOrder.addActionListener(e -> {
            d.dispose();
            placingorder placingorderForm = new placingorder();
            placingorderForm.setVisible(true);
            this.dispose();
        });

        JButton btnOrderStatus = new JButton("Order Status");
        btnOrderStatus.setBackground(Color.WHITE);
        btnOrderStatus.setForeground(new Color(66, 43, 9));
        btnOrderStatus.setFont(new Font("Serif", Font.BOLD, 14));
        btnOrderStatus.setBorder(BorderFactory.createLineBorder(new Color(66, 43, 9), 2));
        btnOrderStatus.setPreferredSize(new Dimension(150, 40));
        btnOrderStatus.setFocusPainted(false);
        btnOrderStatus.addActionListener(e -> {
            d.dispose();
            orderstatus orderstatusForm = new orderstatus();
            orderstatusForm.setVisible(true);
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 12));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(btnNewOrder);
        buttonPanel.add(btnOrderStatus);
        d.add(buttonPanel, BorderLayout.SOUTH);
        d.setVisible(true);
    }
    
    private static class MenuRow { int id; String name; double price; String category; String desc; MenuRow(int id, String name, double price, String category, String desc) { this.id = id; this.name = name; this.price = price; this.category = category; this.desc = desc; } }
    private final java.util.List<MenuRow> menuRows = new java.util.ArrayList<>();
    private final java.util.List<String> menuCategories = new java.util.ArrayList<>();
    private JPanel menuGrid;
    private Icon cupIcon;
    private String activeCategory = "All";
    private static final Color BROWN = new Color(66, 43, 9);
    private static final Color BORDER_BROWN = new Color(107, 70, 28);
    private static final Color CREAM = new Color(245, 243, 238);
    private static final Color GREY_BROWN = new Color(128, 111, 90);
 
    private void loadMenuFromDb() {
        menuRows.clear(); menuCategories.clear();
        String sql = "SELECT item_id, item_name, price, category, description FROM tbl_menu_items WHERE availability = 'Available' ORDER BY item_id";
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) { String cat = rs.getString("category"); if (cat == null) cat = "Others"; menuRows.add(new MenuRow(rs.getInt("item_id"), rs.getString("item_name"), rs.getDouble("price"), cat, rs.getString("description"))); if (!menuCategories.contains(cat)) menuCategories.add(cat); }
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Could not load menu: " + ex.getMessage()); }
    }
    private Icon scaleIcon(Icon src, int targetHeight) { if (src instanceof ImageIcon && src.getIconHeight() > 0) { int w = src.getIconWidth() * targetHeight / src.getIconHeight(); Image img = ((ImageIcon) src).getImage().getScaledInstance(w, targetHeight, Image.SCALE_SMOOTH); return new ImageIcon(img); } return src; }
    private String defaultBlurb(String category) {
        switch (category) { case "Coffee": return "Freshly pulled, bold and smooth."; case "Signatures": return "A LoneWolf original, chilled and creamy."; case "Tea": return "Hot-brewed, soothing and comforting."; case "Soda Based": return "Fizzy, fruity and refreshing."; case "Iced Tea": return "Cold tea over ice, lightly sweet."; case "Lemonade": return "Zesty lemonade with a fruity twist."; case "Frappes": return "Blended, icy and indulgent."; case "Food & Snacks": return "Freshly made to go with your drink."; default: return " "; }
    }
    private JPanel buildCard(MenuRow r) {
        JPanel card = new JPanel(new BorderLayout()); card.setBackground(Color.WHITE); card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_BROWN), BorderFactory.createEmptyBorder(6, 6, 6, 6))); card.setPreferredSize(new Dimension(220, 250));
        JPanel imgPanel = new JPanel(new GridBagLayout()); imgPanel.setBackground(CREAM); imgPanel.add(new JLabel(cupIcon));
        JLabel nameLbl = new JLabel(r.name); nameLbl.setFont(new Font("Georgia", Font.BOLD, 15)); nameLbl.setForeground(BROWN);
        JLabel priceLbl = new JLabel(String.format("P %.2f", r.price)); priceLbl.setFont(new Font("Georgia", Font.BOLD, 15)); priceLbl.setForeground(BROWN);
        JPanel titleRow = new JPanel(new BorderLayout(8, 0)); titleRow.setOpaque(false); titleRow.add(nameLbl, BorderLayout.CENTER); titleRow.add(priceLbl, BorderLayout.EAST);
        String d = (r.desc == null || r.desc.trim().isEmpty()) ? defaultBlurb(r.category) : r.desc; JLabel descLbl = new JLabel("<html>" + d + "</html>"); descLbl.setFont(new Font("Serif", Font.PLAIN, 13)); descLbl.setForeground(GREY_BROWN);
        JButton add = new JButton("ADD"); add.setBackground(BROWN); add.setForeground(Color.WHITE); add.setFont(new Font("Serif", Font.PLAIN, 14)); add.addActionListener(e -> addToCart(r.id, r.name, r.price));
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0)); btnRow.setOpaque(false); btnRow.add(add);
        JPanel info = new JPanel(new BorderLayout(0, 4)); info.setBackground(Color.WHITE); info.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8)); info.add(titleRow, BorderLayout.NORTH); info.add(descLbl, BorderLayout.CENTER); info.add(btnRow, BorderLayout.SOUTH);
        card.add(imgPanel, BorderLayout.CENTER); card.add(info, BorderLayout.SOUTH); return card;
    }
    private void renderMenu() {
        menuGrid.removeAll(); for (MenuRow r : menuRows) { if (activeCategory.equals("All") || activeCategory.equals(r.category)) { menuGrid.add(buildCard(r)); } } menuGrid.revalidate(); menuGrid.repaint();
    }
    private JPanel buildFilterBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0)); bar.setOpaque(false); ButtonGroup group = new ButtonGroup(); java.util.List<String> names = new java.util.ArrayList<>(); names.add("All"); names.addAll(menuCategories); java.util.List<JToggleButton> buttons = new java.util.ArrayList<>();
        for (String n : names) { JToggleButton b = new JToggleButton(n, n.equals("All")); b.setFont(new Font("Serif", Font.PLAIN, 14)); b.setFocusPainted(false); b.setCursor(new Cursor(Cursor.HAND_CURSOR)); group.add(b); buttons.add(b); b.addActionListener(e -> { activeCategory = n; for (JToggleButton x : buttons) { x.setBackground(x.isSelected() ? BROWN : Color.WHITE); x.setForeground(x.isSelected() ? Color.WHITE : BROWN); } renderMenu(); }); bar.add(b); }
        for (JToggleButton x : buttons) { x.setBackground(x.isSelected() ? BROWN : Color.WHITE); x.setForeground(x.isSelected() ? Color.WHITE : BROWN); } return bar;
    }
    private void buildMenuLayout() {
        loadMenuFromDb();
        cupIcon = scaleIcon(jLabel43.getIcon(), 100);
        JPanel titleBlock = new JPanel(); titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS)); titleBlock.setOpaque(false); titleBlock.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        JPanel filterBar = buildFilterBar();
        jLabel7.setAlignmentX(Component.LEFT_ALIGNMENT); jLabel10.setAlignmentX(Component.LEFT_ALIGNMENT); filterBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleBlock.add(jLabel7); titleBlock.add(jLabel10); titleBlock.add(Box.createVerticalStrut(12)); titleBlock.add(filterBar);
        menuGrid = new JPanel(new GridLayout(0, 3, 18, 18)); menuGrid.setOpaque(false);
        JPanel gridHolder = new JPanel(new BorderLayout()); gridHolder.setOpaque(false); gridHolder.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 10)); gridHolder.add(menuGrid, BorderLayout.NORTH);
        JScrollPane sp = new JScrollPane(gridHolder); sp.setBorder(null); sp.setOpaque(false); sp.getViewport().setOpaque(false); sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); sp.getVerticalScrollBar().setUnitIncrement(24);
        renderMenu();
        JPanel bodyCenter = new JPanel(new BorderLayout()); bodyCenter.setOpaque(false); bodyCenter.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 10)); bodyCenter.add(titleBlock, BorderLayout.NORTH); bodyCenter.add(sp, BorderLayout.CENTER);
        JPanel cartWrap = new JPanel(new BorderLayout()); cartWrap.setOpaque(false); cartWrap.setBorder(BorderFactory.createEmptyBorder(10, 10, 15, 20)); cartWrap.add(yourorder, BorderLayout.CENTER);
        JPanel mainBody = new JPanel(new BorderLayout()); mainBody.setOpaque(false); mainBody.add(bodyCenter, BorderLayout.CENTER); mainBody.add(cartWrap, BorderLayout.EAST);
        GroupLayout naviLayout = new GroupLayout(navi);
        navi.setLayout(naviLayout);
        naviLayout.setHorizontalGroup(
            naviLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(naviLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSeparator1)
                .addContainerGap())
            .addGroup(naviLayout.createSequentialGroup()
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
                .addComponent(jButtonLogin, GroupLayout.PREFERRED_SIZE, 101, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonOrderNow)
                .addGap(69, 69, 69))
            .addGroup(naviLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainBody, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        naviLayout.setVerticalGroup(
            naviLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(naviLayout.createSequentialGroup()
                .addGroup(naviLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(naviLayout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addGroup(naviLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(jButtonOrderNow, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonLogin, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4)
                            .addComponent(jLabel3)
                            .addComponent(jLabel6)
                            .addComponent(jLabel8)))
                    .addGroup(naviLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1)))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, GroupLayout.PREFERRED_SIZE, 2, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mainBody, GroupLayout.DEFAULT_SIZE, 700, Short.MAX_VALUE)
                .addContainerGap())
        );
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(navi, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(navi, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pack();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
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
            .addGap(0, 1421, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 947, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try { for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) { if ("Nimbus".equals(info.getName())) { UIManager.setLookAndFeel(info.getClassName()); break; } } } catch (Exception ex) {}
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
        java.awt.EventQueue.invokeLater(() -> new placingorder().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
