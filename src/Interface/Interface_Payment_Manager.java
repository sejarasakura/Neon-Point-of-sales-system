package Interface;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import User.Customer.Customer;
import Main.Main;
import Transaction.Order;
import Transaction.OrderDetail;
import Transaction.Payment;
import Product.Product;
import Sql.WriteSql;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author ITSUKA KOTORI
 */
public class Interface_Payment_Manager extends javax.swing.JFrame {
  
    private DefaultTableModel data_payment = new DefaultTableModel();
    private DefaultTableModel data_paymentDetail = new DefaultTableModel();
    private Product current_product;
    private Order current_order;
    private Payment current_payment;
    
    /**
     * Creates new form Manager_Frame
     */
    public Interface_Payment_Manager() {
        initComponents();
        initComponents_Table();
        initComponents_Interface();
        initComponents_data_payment();
        initComponents_SetClose();
        this.setIconImage(Main.DEFUALT_ICON.getImage());
        message_payment.setText("Payment Record : " + this.table_order.getRowCount());
    }

    private void initComponents_Table(){
        this.data_paymentDetail = (DefaultTableModel)this.table_detail.getModel();
        this.data_payment = (DefaultTableModel)this.table_order.getModel();
    }
    
    private void initComponents_Interface() {
        Vector comboBoxitem = new Vector(), comboBoxitem2 = new Vector(), comboBoxitem4 = new Vector();
        try
        {          
            comboBoxitem.add("~ Select");
            comboBoxitem2.add("~ Select");
            try (Connection conn = DriverManager.getConnection("jdbc:ucanaccess://resource/data/Point_of_sales.mdb;"); Statement s = conn.createStatement();) {
                s.execute("SELECT customer_id FROM CUSTOMER ORDER BY SUBSTRING(CUSTOMER_ID,3,7);");
                ResultSet rs = s.getResultSet();
                    while(rs.next()){
                        comboBoxitem2.add(rs.getString("customer_id"));
                    }
                s.execute("SELECT staff_id FROM STAFF ORDER BY SUBSTRING(STAFF_ID,3,8);");
                rs = s.getResultSet();
                    while(rs.next()){
                        comboBoxitem.add(rs.getString("staff_id"));
                    }
                s.execute("SELECT product_id FROM Product ORDER BY product_id;");
                rs = s.getResultSet();
                    while(rs.next()){
                        comboBoxitem4.add(rs.getString("product_id"));
                    }
                rs.close();
                s.close();
                conn.close();
            }
        }
        catch(SQLException ex)
        {
            System.out.println(ex.getMessage());
        }
        DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel(comboBoxitem);
        this.combo_staff.setModel(comboBoxModel);
        combo_staff.setEditable(false);
        DefaultComboBoxModel comboBoxModel2 = new DefaultComboBoxModel(comboBoxitem2);
        this.combo_customer.setModel(comboBoxModel2);
        combo_customer.setEditable(false);
        DefaultComboBoxModel comboBoxModel4 = new DefaultComboBoxModel(comboBoxitem4);
        this.combo_product.setModel(comboBoxModel4);
        combo_product.setEditable(false);
        label_today_date.setText("<html><body><span style='text-align:right;'>Today Date : " 
                + Main.DATE_FORMAT.format(Main.getToday_date()) 
                + "</span></body></html>");
    }

    private void initComponents_data_payment() {
        
        Payment temp = new Payment();
        try
        {   
            while(data_payment.getRowCount() > 0){
                this.data_payment.removeRow(0);
            }
            try (Connection conn = DriverManager.getConnection("jdbc:ucanaccess://resource/data/Point_of_sales.mdb;"); Statement s = conn.createStatement();) {
                s.execute("SELECT * FROM Payment ORDER BY payment_id;");
                try (ResultSet result = s.getResultSet()) {
                    while(result.next()){
                        temp.split(result);
                        System.out.println(temp.toString());
                        this.write_payment_row(temp);
                    }
                }
                s.close();
                conn.close();
            }
        }
        catch(SQLException ex){
            Logger.getLogger(Payment.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initComponents_SetClose() {
        this.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e){
            }
        });
    }
    
    private void write_product_row(OrderDetail x){
        String[] data = new String[5];
        data[0] = x.getProduct_id();
        data[1] = new Product(x.getProduct_id()).getProduct_name();
        data[2] = String.format("%d", x.getQuantity());
        data[3] = String.format("%.2f", x.getUnit_discount());
        data[4] = String.format("%.2f", x.getSubTotalAfterDiscount());
        this.data_paymentDetail.addRow(data);
    }
    
    private void write_payment_row(Payment x){
        String[] data = new String[7];
        data[0] = x.getPayment_id();
        if(WriteSql.getUniqueInt("SELECT COUNT(customer_id) FROM Customer WHERE customer_id='" + x.getCustomer_id() + "';") == 1)
            data[1] = String.format("%.2f%%", new Customer(x.getCustomer_id()).getLevel().discount());
        else
            data[1] = "0.00%";
        data[2] = String.format("%.2f",x.getSst_charge());
        data[3] = x.getCustomer_id();
        data[4] = x.getStaff_id();
        data[5] = x.getOrder_id();
        data[6] = WriteSql.DATE_FORMAT.format(x.getTransaction_date());
        this.data_payment.addRow(data);
    }
    
    
    private void modify_product_row(OrderDetail x, int i){
        String[] data = new String[5];
        data[0] = x.getProduct_id();
        data[1] = new Product(x.getProduct_id()).getProduct_name();
        data[2] = String.format("%d", x.getQuantity());
        data[3] = String.format("%.2f", x.getUnit_discount());
        data[4] = String.format("%.2f", x.getSubTotalAfterDiscount());
        for(int j = 0; j < 5; j++){
            this.data_paymentDetail.setValueAt(data[j], i, j);
        }
    }
    
    private void modify_payment_row(Payment x, int i){
        String[] data = new String[7];
        data[0] = x.getPayment_id();
        if(WriteSql.getUniqueInt("SELECT COUNT(customer_id) FROM Customer WHERE customer_id='" +x.getCustomer_id()+ "';") >= 1)
            data[1] = String.format("%.2f", new Customer(x.getCustomer_id()).getLevel().discount());
        else
            data[1] = "0.00";
        data[2] = String.format("%.2f",x.getSst_charge());
        data[3] = x.getCustomer_id();
        data[4] = x.getStaff_id();
        data[5] = x.getOrder_id();
        data[6] = WriteSql.DATE_FORMAT.format(x.getTransaction_date());
        for(int j = 0; j < 7; j++){
            this.data_payment.setValueAt(data[j], i, j);
        }
    }
    
    
    private OrderDetail read_table_detail_row(int row){
        OrderDetail temp = new OrderDetail();
        temp.setOrder_id(read_payment_row(table_order.getSelectedRow()).getOrder_id());
        temp.setProduct_id((String)table_detail.getValueAt(row, 0));
        temp.setQuantity(Integer.valueOf((String)table_detail.getValueAt(row, 2)));
        temp.setUnit_discount(Double.valueOf((String)table_detail.getValueAt(row, 3)));
        temp.setUnit_price(new Product(temp.getProduct_id()).getPriceAfterDiscount());
        return temp;
    }
    
    private Payment read_payment_row(int row){
        try {
            Payment temp = new Payment();
            temp.setPayment_id((String)this.table_order.getValueAt(row, 0));
            temp.setSSTCharge(Double.valueOf(((String)table_order.getValueAt(row, 2)).replace('%', '0')));
            temp.setCustomer_id((String)table_order.getValueAt(row, 3));
            temp.setStaff_id((String)table_order.getValueAt(row, 4));
            temp.setOrder_id((String)table_order.getValueAt(row, 5));
            temp.setTransaction_date(WriteSql.DATE_FORMAT.parse((String)table_order.getValueAt(row, 6)));
            return temp;
        } catch (ParseException ex) {
            Logger.getLogger(Interface_Payment_Manager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        table_order = new javax.swing.JTable();
        label_today_date = new javax.swing.JLabel();
        message_product_detail = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        table_detail = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        label_productTotal_true = new javax.swing.JLabel();
        label_SSTCharge_true = new javax.swing.JLabel();
        label_price_true = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        label_price_true1 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        label_order = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        label_member = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        label_staff = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        btn_delete_true = new javax.swing.JButton();
        btn_modifyPayment_true = new javax.swing.JButton();
        btn_refreshPayment_true = new javax.swing.JButton();
        btn_exit_true = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        combo_staff = new javax.swing.JComboBox<>();
        combo_customer = new javax.swing.JComboBox<>();
        dateChooserCombo1 = new datechooser.beans.DateChooserCombo();
        jLabel34 = new javax.swing.JLabel();
        sst_charge = new javax.swing.JComboBox<>();
        btn_addProduct_true = new javax.swing.JButton();
        btn_delete_product_true = new javax.swing.JButton();
        btn_refresh_product = new javax.swing.JButton();
        message_payment = new javax.swing.JLabel();
        combo_product = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Payment manager");

        table_order.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Payment ID", "Member discount", "SST Charge", "Member ID", "Duty Staff ID", "Order ID", "Transaction Date"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_order.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                table_orderMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                table_orderMousePressed(evt);
            }
        });
        table_order.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                table_orderKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                table_orderKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                table_orderKeyTyped(evt);
            }
        });
        jScrollPane1.setViewportView(table_order);
        if (table_order.getColumnModel().getColumnCount() > 0) {
            table_order.getColumnModel().getColumn(0).setResizable(false);
            table_order.getColumnModel().getColumn(0).setPreferredWidth(246);
            table_order.getColumnModel().getColumn(1).setResizable(false);
            table_order.getColumnModel().getColumn(1).setPreferredWidth(148);
            table_order.getColumnModel().getColumn(2).setResizable(false);
            table_order.getColumnModel().getColumn(2).setPreferredWidth(148);
            table_order.getColumnModel().getColumn(3).setResizable(false);
            table_order.getColumnModel().getColumn(3).setPreferredWidth(148);
            table_order.getColumnModel().getColumn(4).setResizable(false);
            table_order.getColumnModel().getColumn(4).setPreferredWidth(148);
            table_order.getColumnModel().getColumn(5).setResizable(false);
            table_order.getColumnModel().getColumn(5).setPreferredWidth(246);
            table_order.getColumnModel().getColumn(6).setResizable(false);
            table_order.getColumnModel().getColumn(6).setPreferredWidth(246);
        }

        label_today_date.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        label_today_date.setText("Today Date : 12-Feb-2015 hh:mm:ss");

        message_product_detail.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        message_product_detail.setText("Product detail :");

        jScrollPane3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jScrollPane3KeyPressed(evt);
            }
        });

        table_detail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Product ID", "Product Name", "Quantity", "Discount (%)", "U Price (RM)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_detail.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                table_detailKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                table_detailKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                table_detailKeyTyped(evt);
            }
        });
        jScrollPane3.setViewportView(table_detail);
        if (table_detail.getColumnModel().getColumnCount() > 0) {
            table_detail.getColumnModel().getColumn(0).setResizable(false);
            table_detail.getColumnModel().getColumn(0).setPreferredWidth(68);
            table_detail.getColumnModel().getColumn(1).setResizable(false);
            table_detail.getColumnModel().getColumn(1).setPreferredWidth(268);
            table_detail.getColumnModel().getColumn(2).setResizable(false);
            table_detail.getColumnModel().getColumn(2).setPreferredWidth(68);
            table_detail.getColumnModel().getColumn(3).setResizable(false);
            table_detail.getColumnModel().getColumn(3).setPreferredWidth(78);
            table_detail.getColumnModel().getColumn(4).setResizable(false);
            table_detail.getColumnModel().getColumn(4).setPreferredWidth(68);
        }

        jLabel6.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel6.setText("Total Product");

        jLabel3.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel3.setText("Total SST Charge");

        jLabel7.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel7.setText(":");

        jLabel4.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel4.setText(": ( RM )");

        label_productTotal_true.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        label_productTotal_true.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_productTotal_true.setText("00 ");

        label_SSTCharge_true.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        label_SSTCharge_true.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_SSTCharge_true.setText("00.00 ");

        label_price_true.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        label_price_true.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_price_true.setText("00.00 ");

        jLabel13.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel13.setText(": ( RM )");

        jLabel14.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel14.setText("Total Price");

        label_price_true1.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        label_price_true1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_price_true1.setText("0000000000 ");

        jLabel17.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel17.setText(": ");

        jLabel18.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel18.setText("Payment ID");

        label_order.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        label_order.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_order.setText("OR00000000 ");

        jLabel19.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel19.setText("Order ID");

        jLabel20.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel20.setText(": ");

        label_member.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        label_member.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_member.setText("XX9999 ");

        jLabel21.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel21.setText(": ");

        jLabel23.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel23.setText("Member ID");

        jLabel24.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel24.setText("Handle Staff");

        jLabel25.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel25.setText(": ");

        label_staff.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        label_staff.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_staff.setText("XX9999 ");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_staff, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_member, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_order, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_price_true1, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label_SSTCharge_true, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_price_true, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_productTotal_true, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_productTotal_true, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_SSTCharge_true, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_price_true, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_price_true1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_order, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_member, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_staff, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setAutoscrolls(true);
        jPanel2.setDoubleBuffered(false);
        jPanel2.setEnabled(false);
        jPanel2.setRequestFocusEnabled(false);
        jPanel2.setVerifyInputWhenFocusTarget(false);

        btn_delete_true.setText("Delete");
        btn_delete_true.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_delete_trueActionPerformed(evt);
            }
        });

        btn_modifyPayment_true.setText("Modify");
        btn_modifyPayment_true.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_modifyPayment_trueActionPerformed(evt);
            }
        });

        btn_refreshPayment_true.setText("Refresh");
        btn_refreshPayment_true.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_refreshPayment_trueActionPerformed(evt);
            }
        });

        btn_exit_true.setText("Back To Menu");
        btn_exit_true.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_exit_trueActionPerformed(evt);
            }
        });

        jLabel16.setText("Modify Payment");

        jLabel22.setText("Staff ID :");

        jLabel26.setText("Payment date: ");

        jLabel33.setText("Member ID :");

        combo_staff.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        combo_customer.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        combo_customer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_customerActionPerformed(evt);
            }
        });

        dateChooserCombo1.setFormat(2);

        jLabel34.setText("SST Charge:");

        sst_charge.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0", "3", "6", "10" }));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(btn_modifyPayment_true, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_refreshPayment_true, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btn_exit_true))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(8, 8, 8)
                                .addComponent(combo_staff, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel33, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(combo_customer, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(dateChooserCombo1, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
                            .addComponent(sst_charge, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(btn_delete_true, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btn_exit_true)
                        .addGap(32, 32, 32))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(combo_staff, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(dateChooserCombo1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(combo_customer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sst_charge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btn_delete_true)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_modifyPayment_true)
                    .addComponent(btn_refreshPayment_true))
                .addContainerGap())
        );

        btn_addProduct_true.setText("Add Product");
        btn_addProduct_true.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_addProduct_trueActionPerformed(evt);
            }
        });

        btn_delete_product_true.setText("Delete Product");
        btn_delete_product_true.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_delete_product_trueActionPerformed(evt);
            }
        });

        btn_refresh_product.setText("Refresh");
        btn_refresh_product.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_refresh_productActionPerformed(evt);
            }
        });

        message_payment.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        message_payment.setText("Payment Record : ");

        combo_product.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        combo_product.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_productActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(message_payment, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btn_addProduct_true)
                                .addGap(18, 18, 18)
                                .addComponent(combo_product, javax.swing.GroupLayout.PREFERRED_SIZE, 357, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btn_delete_product_true, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btn_refresh_product, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(message_product_detail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(label_today_date, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(label_today_date, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(message_product_detail, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(btn_delete_product_true)
                                    .addComponent(btn_refresh_product)
                                    .addComponent(combo_product, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(message_payment, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btn_addProduct_true))))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 325, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_exit_trueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_exit_trueActionPerformed
        new Interface_MenuManager().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btn_exit_trueActionPerformed

    private void table_detailKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_table_detailKeyTyped
        if(evt.getKeyCode() == 10){
            boolean save_or_not = false;
            int x = table_detail.getSelectedColumn();
            int y = table_detail.getSelectedRow();
            int y2 = table_order.getSelectedRow();
            OrderDetail temp = new OrderDetail(
                    (String)table_order.getValueAt(y2, 0),
                    (String)table_detail.getValueAt(y,0));
            Object changeResult = table_detail.getValueAt(y, x);
            switch(x){
                case 2: 
                    int quantity = temp.getQuantity();
                    if(temp.setQuantity((Integer) changeResult) == true){
                        save_or_not = true;
                        new Product((String)table_detail.getValueAt(y, 0)).puchase(temp.getQuantity() - quantity);
                        temp.modify_this();
                        this.modify_product_row(temp, y);
                        refresh_total();
                        message_product_detail.setText("<html><body>Product Detail :<span style='color:green'> Saved !!!</span></body></html>");
                    }else{
                        temp.setQuantity(quantity);
                        message_product_detail.setText("<html><body>Product Detail :<span style='color:red'> Not save !!!</span></body></html>");
                    }
                    break;
            }
        }else if (evt.getKeyCode() >= 48 && evt.getKeyCode() <= 57){
            message_product_detail.setText("<html><body>Product Detail :<span style='color:red'> Not save !!!</span></body></html>");
        }       
    }//GEN-LAST:event_table_detailKeyTyped

    private void table_orderMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table_orderMouseClicked
        
    }//GEN-LAST:event_table_orderMouseClicked

    private void table_orderMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table_orderMousePressed
        int y = this.table_order.getSelectedRow();
        if(y >= 0){
            table_detail.clearSelection();
            while(data_paymentDetail.getRowCount() > 0){
                this.data_paymentDetail.removeRow(0);
            }
            current_payment = this.read_payment_row(y);
            System.out.println(current_payment.toString());
            current_order = new Order(current_payment.getOrder_id());
            for (int i = 0; i < current_order.getProduct_select().size(); i++) {
                this.write_product_row(current_order.getProduct_select().get(i));
            }
            if(WriteSql.getUniqueInt("SELECT COUNT(customer_id) FROM Customer WHERE customer_id='" + current_payment.getCustomer_id() + "';") == 1)
                this.combo_customer.setSelectedItem(current_payment.getCustomer_id());
            else                
                this.combo_customer.setSelectedItem("~ Select");
            this.combo_staff.setSelectedItem(current_payment.getStaff_id());
            this.dateChooserCombo1.setSelectedDate(convertDtoC(current_payment.getTransaction_date()));
            this.sst_charge.setSelectedIndex(switch_sst_to_number(current_payment.getSst_charge()));
            this.label_productTotal_true.setText(String.format("%d",WriteSql.getUniqueInt("SELECT SUM(product_quantity) FROM Order_record_detail WHERE Order_id='" + current_order.getOrder_num() + "';")));
            this.label_price_true.setText(String.format("%.2f", WriteSql.getUniqueDouble("SELECT SUM(product_quantity * unit_final_price) FROM Order_record_detail WHERE Order_id='" + current_order.getOrder_num() + "';")));
            this.label_SSTCharge_true.setText(String.format("%.2f",current_payment.getSst_charge() * Double.valueOf(label_price_true.getText())));
            this.label_price_true1.setText(current_payment.getPayment_id());
            this.label_order.setText(current_payment.getOrder_id());
            this.label_staff.setText(current_payment.getStaff_id());
            this.label_member.setText(current_payment.getCustomer_id());
        }else{
            table_detail.clearSelection();
            while(data_paymentDetail.getRowCount() > 0){
                this.data_paymentDetail.removeRow(0);
            }
        }
    }//GEN-LAST:event_table_orderMousePressed

    private Calendar convertDtoC(Date x){
        Calendar cal = Calendar.getInstance();
        cal.setTime(x);
        return cal;
    }
    
    private void btn_modifyPayment_trueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_modifyPayment_trueActionPerformed
        
        int i = this.table_order.getSelectedRow();
        if (i >= 0) {
            current_payment = this.read_payment_row(i);
            if (JOptionPane.showConfirmDialog(null, "Do you sure want to update following payment of " + current_payment.getPayment_id() + " , yours changers may not be make changes", "Confrim Modify Payment", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_NO_OPTION) {
                current_payment.setCustomer_id((String) combo_customer.getSelectedItem());
                current_payment.setStaff_id((String) combo_staff.getSelectedItem());
                current_payment.setTransaction_date(dateChooserCombo1.getSelectedDate().getTime());
                current_payment.setSSTCharge(Double.valueOf((String) this.sst_charge.getSelectedItem()) / 100);
                current_payment.modify_this();
                this.modify_payment_row(current_payment, i);
                int x = this.table_detail.getRowCount();
                for (int j = 0; j < x; j++) {
                    this.read_table_detail_row(j).modify_this();
                }
                this.refresh_total();
                message_product_detail.setText("<html><body>Product Detail :<span style='color:green'> Saved !!!</span></body></html>");
            }else{
                message_product_detail.setText("<html><body>Product Detail :<span style='color:red'> Not save !!!</span></body></html>");
            }
        }else{
            JOptionPane.showConfirmDialog(null, "No row selected ! please select a payment frist", "No row selected", JOptionPane.OK_OPTION);
        }
    }//GEN-LAST:event_btn_modifyPayment_trueActionPerformed

    private void btn_addProduct_trueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_addProduct_trueActionPerformed
        
        if(WriteSql.getUniqueInt("SELECT COUNT(PRODUCT_ID) FROM Product WHERE PRODUCT_ID='" + (String)combo_product.getSelectedItem() + "';") == 1 && table_order.getSelectedRow() >= 0){
            if (WriteSql.getUniqueInt("SELECT COUNT(PRODUCT_ID) FROM Order_record_detail WHERE PRODUCT_ID='" + (String)combo_product.getSelectedItem() + "' AND order_id='" + this.read_payment_row(table_order.getSelectedRow()).getOrder_id() + "';") <= 0) {
                OrderDetail detail = new OrderDetail(this.read_payment_row(table_order.getSelectedRow()).getOrder_id(), (String) combo_product.getSelectedItem(), 1);
                if (JOptionPane.showConfirmDialog(null, "Are you sure want to add the item of " + (String) combo_product.getSelectedItem() + " ? ", "Confrim Add", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    this.write_product_row(detail);
                    detail.write_this();
                }
            } else {
                OrderDetail detail = new OrderDetail(this.read_payment_row(table_order.getSelectedRow()).getOrder_id(), (String) combo_product.getSelectedItem());
                if (JOptionPane.showConfirmDialog(null, "Are you sure want to add the item of " + (String) combo_product.getSelectedItem() + " ? ", "Confrim Add", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    detail.setQuantity(detail.getQuantity() + 1);
                    detail.modify_this();
                    btn_refresh_productActionPerformed(null);
                }
            }
            this.refresh_total();
            this.table_detail.clearSelection();
        }
    }//GEN-LAST:event_btn_addProduct_trueActionPerformed

    private void btn_delete_product_trueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_delete_product_trueActionPerformed
        int i = table_detail.getSelectedRow();
        int j = table_order.getSelectedRow();
        if (i >= 0 && j >= 0) {
            OrderDetail needDelete = new OrderDetail();
            needDelete.setOrder_id((String) table_order.getValueAt(j, 5));
            needDelete.setProduct_id((String) table_detail.getValueAt(i, 0));
            if (JOptionPane.showConfirmDialog(null, "Are you sure want to delete the item of " + needDelete.getProduct_id() + " with is in " + needDelete.getOrder_id() +" ? ", "Confrim delete", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if (needDelete.delete_this()) {
                    message_product_detail.setText("Product Detial : Delete successful !! ");
                    this.data_paymentDetail.removeRow(i);
                } else {
                    message_product_detail.setText("Product Detial : Delete unsuccessful !! ");
                }
                this.table_detail.clearSelection();
                refresh_total();
            } else {
            }
        }
    }//GEN-LAST:event_btn_delete_product_trueActionPerformed

    private void table_orderKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_table_orderKeyTyped
        if(evt.getKeyCode() == 10){
            int i = table_order.getSelectedRow();
            int j = table_order.getSelectedColumn();
            if (i >= 0 && j >= 0) {
                current_payment = this.read_payment_row(i);
                switch (j) {
                    case 1:
                        double data = current_payment.getSst_charge();
                        if(current_payment.setSSTCharge((Double) table_order.getValueAt(i, j))){
                            this.modify_payment_row(current_payment, i);
                            current_payment.modify_this();
                            message_payment.setText("Payment Record : Saved the update drecord !! ");
                        }else{
                            current_payment.setSSTCharge(data);
                            message_payment.setText("Payment Record : The following sst charge is invalid !! ");
                        }
                        break;
                    case 6:
                        try {
                            Date data2 = current_payment.getTransaction_date();
                            if(current_payment.setTransaction_date(WriteSql.DATE_FORMAT.parse((String) table_order.getValueAt(i, j)))){

                                this.modify_payment_row(current_payment, i);
                                current_payment.modify_this();
                                message_payment.setText("Payment Record : Saved the update drecord !! ");   
                            }
                            else{
                                current_payment.setTransaction_date(data2);
                                message_payment.setText("Payment Record : The following sst charge is invalid !! ");
                            }
                        } catch (ParseException ex) {
                            Logger.getLogger(Interface_Payment_Manager.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        break;
                }
            }
        }else{
            message_payment.setText("Payment Record : Enter key to save updated record !!");
        }
        this.table_detail.clearSelection();
    }//GEN-LAST:event_table_orderKeyTyped

    private void btn_delete_trueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_delete_trueActionPerformed
        int i = this.table_order.getSelectedRow();
        current_payment = new Payment((String)data_payment.getValueAt(i, 0));
        if (JOptionPane.showConfirmDialog(null, "Do you sure want to delete the following payment with is " + current_payment.getPayment_id(), "Delete confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (current_payment.delete_this()) {
                this.table_order.remove(i);
                message_payment.setText("Payment Record : Removed record !!");
            } else {
                message_payment.setText("Payment Record : The record is not deleted !!");
            }
        }
        message_payment.setText("Payment Record : " + this.table_order.getRowCount());
        
    }//GEN-LAST:event_btn_delete_trueActionPerformed

    private void btn_refresh_productActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_refresh_productActionPerformed
        table_orderMousePressed(null);
    }//GEN-LAST:event_btn_refresh_productActionPerformed

    private void combo_customerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_customerActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_combo_customerActionPerformed

    private void btn_refreshPayment_trueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_refreshPayment_trueActionPerformed
        initComponents_data_payment();
        message_payment.setText("Payment Record : " + this.table_order.getRowCount());
    }//GEN-LAST:event_btn_refreshPayment_trueActionPerformed

    private void table_orderKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_table_orderKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_table_orderKeyPressed

    private void table_detailKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_table_detailKeyPressed
        if(evt.getKeyCode() >= 48 && evt.getKeyCode() <= 57)
            message_product_detail.setText("<html><body>Product Detail :<span style='color:red'> Not save !!!</span></body></html>");
    }//GEN-LAST:event_table_detailKeyPressed

    private void table_orderKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_table_orderKeyReleased
        // TODO add your handling code here:
        if(evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN)
        table_orderMousePressed(null);
    }//GEN-LAST:event_table_orderKeyReleased

    private void combo_productActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_productActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_combo_productActionPerformed

    private void table_detailKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_table_detailKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_table_detailKeyReleased

    private void jScrollPane3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jScrollPane3KeyPressed
        // TODO add your handling code here: 
        if (evt.getKeyCode() >= 48 && evt.getKeyCode() <= 57){
            message_product_detail.setText("<html><body>Product Detail :<span style='color:red'> Not save !!!</span></body></html>");
        }       
    }//GEN-LAST:event_jScrollPane3KeyPressed

    private int switch_sst_to_number(double x){
        if(x >= 0 && x <=0.001)
            return 0;
        else if(x >= 0.029 && x <= 0.031)
            return 1;
        else if(x >= 0.059 && x <= 0.061)
            return 2;
        else 
            return 3;
    }
    
    private void refresh_total(){
        this.sst_charge.setSelectedIndex(switch_sst_to_number(current_payment.getSst_charge()));
        this.label_productTotal_true.setText(String.format("%d",WriteSql.getUniqueInt("SELECT SUM(product_quantity) FROM Order_record_detail WHERE Order_id='" + current_order.getOrder_num() + "';")));
        this.label_price_true.setText(String.format("%.2f", WriteSql.getUniqueDouble("SELECT SUM(product_quantity * unit_final_price) FROM Order_record_detail WHERE Order_id='" + current_order.getOrder_num() + "';")));
        this.label_SSTCharge_true.setText(String.format("%.2f",current_payment.getSst_charge() * Double.valueOf(label_price_true.getText())));
    }
     /**
        * @param args the command line arguments
        */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_addProduct_true;
    private javax.swing.JButton btn_delete_product_true;
    private javax.swing.JButton btn_delete_true;
    private javax.swing.JButton btn_exit_true;
    private javax.swing.JButton btn_modifyPayment_true;
    private javax.swing.JButton btn_refreshPayment_true;
    private javax.swing.JButton btn_refresh_product;
    private javax.swing.JComboBox<String> combo_customer;
    private javax.swing.JComboBox<String> combo_product;
    private javax.swing.JComboBox<String> combo_staff;
    private datechooser.beans.DateChooserCombo dateChooserCombo1;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel label_SSTCharge_true;
    private javax.swing.JLabel label_member;
    private javax.swing.JLabel label_order;
    private javax.swing.JLabel label_price_true;
    private javax.swing.JLabel label_price_true1;
    private javax.swing.JLabel label_productTotal_true;
    private javax.swing.JLabel label_staff;
    private javax.swing.JLabel label_today_date;
    private javax.swing.JLabel message_payment;
    private javax.swing.JLabel message_product_detail;
    private javax.swing.JComboBox<String> sst_charge;
    private javax.swing.JTable table_detail;
    private javax.swing.JTable table_order;
    // End of variables declaration//GEN-END:variables


}
