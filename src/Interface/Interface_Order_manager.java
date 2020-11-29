package Interface;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Main.Main;
import Transaction.Order;
import Transaction.OrderDetail;
import Transaction.Payment;
import Product.Product;
import User.Staff.Staff;
import SubInterface.SubInterface_Login_Staff;
import Sql.WriteSql;


import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
public class Interface_Order_manager extends javax.swing.JFrame {
  
    private DefaultTableModel data_order = new DefaultTableModel();
    private DefaultTableModel order_detail = new DefaultTableModel();
    private Product current_product;
    private Order current_order;
    
    /**
     * Creates new form Manager_Frame
     */
    public Interface_Order_manager() {
        initComponents();
        initComponents_Table();
        initComponents_Interface();
        initComponents_data_payment();
        initComponents_SetClose();
        this.setIconImage(Main.DEFUALT_ICON.getImage());
        total_order.setText("Total Order Record : " + this.table_order.getRowCount());
    }

    private void initComponents_Table(){
        this.order_detail = (DefaultTableModel)this.table_detail.getModel();
        this.data_order = (DefaultTableModel)this.table_order.getModel();
    }
    
    private void initComponents_Interface() {
        Vector comboBoxitem2 = new Vector(), comboBoxitem4 = new Vector();
        try
        {          
            comboBoxitem2.add("~ Select");
            comboBoxitem4.add("~ Select");
            try (Connection conn = DriverManager.getConnection("jdbc:ucanaccess://resource/data/Point_of_sales.mdb;"); Statement s = conn.createStatement();) {
                s.execute("SELECT customer_id FROM CUSTOMER ORDER BY SUBSTRING(CUSTOMER_ID,3,7);");
                ResultSet rs = s.getResultSet();
                    while(rs.next()){
                        comboBoxitem2.add(rs.getString("customer_id"));
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
        DefaultComboBoxModel comboBoxModel2 = new DefaultComboBoxModel(comboBoxitem2);
        this.combo_customer.setModel(comboBoxModel2);
        combo_customer.setEditable(false);
        DefaultComboBoxModel comboBoxModel4 = new DefaultComboBoxModel(comboBoxitem4);
        this.combo_product.setModel(comboBoxModel4);
        combo_product.setEditable(false);
        label_today_date.setText("Today Date : " + Main.DATE_FORMAT.format(Main.getToday_date()));
        this.neon_img_label.setIcon(new ImageIcon(Interface_Payment.getScaledImage(new ImageIcon("resource/neon.png").getImage(), 324, 99)));
    }

    private void initComponents_data_payment() {
        
        Order temp;
        try
        {   
            while(data_order.getRowCount() > 0){
                this.data_order.removeRow(0);
            }
            try (Connection conn = DriverManager.getConnection("jdbc:ucanaccess://resource/data/Point_of_sales.mdb;"); Statement s = conn.createStatement();) {
                s.execute("SELECT O.order_id, O.customer_id, O.order_date, O.order_exp_date FROM ORDER_RECORD O WHERE NOT EXISTS ( SELECT P.ORDER_ID FROM PAYMENT P WHERE P.ORDER_ID = O.ORDER_ID );");
                try (ResultSet result = s.getResultSet()) {
                    while(result.next()){
                        temp = new Order();
                        temp.split(result);
                        System.out.println(temp.toString());
                        this.write_order_row(temp);
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
                SubInterface_Login_Staff.validate_login_go(new Staff(SubInterface_Login_Staff.decode_id(getDataDutyStaff())));
                dispose();
            }
        });
    }
    
    private void write_product_row(OrderDetail x){
        String[] data = new String[5];
        data[0] = x.getProduct_id();
        data[1] = new Product(x.getProduct_id()).getProduct_name();
        data[2] = String.format("%d", x.getQuantity());
        data[3] = String.format("%.2f", x.getUnit_discount());
        data[4] = String.format("%.2f", new Product(x.getProduct_id()).getPriceAfterDiscount());
        this.order_detail.addRow(data);
    }
    
    private void write_order_row(Order x){
        String[] data = new String[4];
        data[0] = x.getOrder_num();
        if(WriteSql.getUniqueInt("SELECT COUNT(customer_id) FROM Customer WHERE customer_id='" +x.getMember_num()+ "';") >= 1)
            data[1] = x.getMember_num();
        else
            data[1] = "";
        data[2] = WriteSql.DATE_FORMAT.format(x.getOrder_date());
        data[3] = WriteSql.DATE_FORMAT.format(x.getOrder_expiry_date());
        this.data_order.addRow(data);
    }
    
    
    private void modify_product_row(OrderDetail x, int i){
        String[] data = new String[5];
        data[0] = x.getProduct_id();
        data[1] = new Product(x.getProduct_id()).getProduct_name();
        data[2] = String.format("%d", x.getQuantity());
        data[3] = String.format("%.2f", x.getUnit_discount());
        data[4] = String.format("%.2f", x.getSubTotalAfterDiscount());
        for(int j = 0; j < 5; j++){
            this.order_detail.setValueAt(data[j], i, j);
        }
    }
    
    private void modify_order_row(Order x, int i){
        String[] data = new String[4];
        data[0] = x.getOrder_num();
        if(WriteSql.getUniqueInt("SELECT COUNT(customer_id) FROM Customer WHERE customer_id='" +x.getMember_num()+ "';") >= 1)
            data[1] = x.getMember_num();
        else
            data[1] = "";
        data[2] = WriteSql.DATE_FORMAT.format(x.getOrder_date());
        data[3] = WriteSql.DATE_FORMAT.format(x.getOrder_expiry_date());
        for(int j = 0; j < 4; j++){
            this.data_order.setValueAt(data[j], i, j);
        }
    }
    
    
    private OrderDetail read_table_detail_row(int row){
        OrderDetail temp = new OrderDetail();
        temp.setOrder_id(read_order_row(this.table_order.getSelectedRow()).getOrder_num());
        temp.setProduct_id((String)order_detail.getValueAt(row, 0));
        temp.setQuantity(Integer.valueOf((String)order_detail.getValueAt(row, 2)));
        temp.setUnit_discount(Double.valueOf((String)order_detail.getValueAt(row, 3)));
        temp.setUnit_price(new Product(temp.getProduct_id()).getPriceAfterDiscount());
        return temp;
    }
    
    private Order read_order_row(int row){

        Order temp = new Order();
        temp.setOrder_num((String)this.data_order.getValueAt(row, 0));
        temp.setMember_id((String)this.data_order.getValueAt(row, 1));
        try{
            temp.setOrder_date(WriteSql.DATE_FORMAT.parse((String)this.data_order.getValueAt(row, 2)));
            temp.setOrder_expiry_date(WriteSql.DATE_FORMAT.parse((String)data_order.getValueAt(row, 3)));
        }catch (Exception ex){
            
        }
        return temp;

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
        message_product_detail = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        table_detail = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        label_productTotal_true = new javax.swing.JLabel();
        label_price_true = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        i_quantity = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        btn_update_qnt = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        btn_delete_true = new javax.swing.JButton();
        btn_modifyPayment_true = new javax.swing.JButton();
        btn_refreshPayment_true = new javax.swing.JButton();
        btn_exit_true = new javax.swing.JButton();
        label_today_date = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        btn_addProduct_true = new javax.swing.JButton();
        btn_delete_product_true = new javax.swing.JButton();
        btn_refresh_product = new javax.swing.JButton();
        combo_product = new javax.swing.JComboBox<>();
        combo_customer = new javax.swing.JComboBox<>();
        jLabel35 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        idate_order = new datechooser.beans.DateChooserPanel();
        idate_exp = new datechooser.beans.DateChooserPanel();
        total_order = new javax.swing.JLabel();
        neon_img_label = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Order manager");

        table_order.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Order ID", "Member ID", "Order Date", "Order Exp Date"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
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
            table_order.getColumnModel().getColumn(3).setPreferredWidth(246);
        }

        message_product_detail.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        message_product_detail.setText("Product detail :");

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
        table_detail.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                table_detailMousePressed(evt);
            }
        });
        table_detail.addKeyListener(new java.awt.event.KeyAdapter() {
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

        jLabel6.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel6.setText("Total Product");

        jLabel7.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel7.setText(":");

        label_productTotal_true.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        label_productTotal_true.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_productTotal_true.setText("00 ");

        label_price_true.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        label_price_true.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_price_true.setText("00.00 ");

        jLabel13.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel13.setText(": ( RM )");

        jLabel14.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel14.setText("Total Price");

        i_quantity.setText("0");
        i_quantity.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                i_quantityKeyPressed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel8.setText(":");

        jLabel9.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel9.setText("Quantity");

        btn_update_qnt.setText("Update quantity");
        btn_update_qnt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_update_qntActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(i_quantity, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_productTotal_true, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_price_true, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btn_update_qnt)
                        .addGap(27, 27, 27))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(btn_update_qnt)
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(i_quantity, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_productTotal_true, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_price_true, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15))
        );

        jPanel2.setAutoscrolls(true);
        jPanel2.setDoubleBuffered(false);
        jPanel2.setEnabled(false);
        jPanel2.setRequestFocusEnabled(false);
        jPanel2.setVerifyInputWhenFocusTarget(false);

        btn_delete_true.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        btn_delete_true.setText("Delete");
        btn_delete_true.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_delete_trueActionPerformed(evt);
            }
        });

        btn_modifyPayment_true.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        btn_modifyPayment_true.setText("Modify");
        btn_modifyPayment_true.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_modifyPayment_trueActionPerformed(evt);
            }
        });

        btn_refreshPayment_true.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        btn_refreshPayment_true.setText("Refresh");
        btn_refreshPayment_true.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_refreshPayment_trueActionPerformed(evt);
            }
        });

        btn_exit_true.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        btn_exit_true.setText("Back To Menu");
        btn_exit_true.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_exit_trueActionPerformed(evt);
            }
        });

        label_today_date.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        label_today_date.setText("Today Date : 12-Feb-2015 hh:mm:ss");

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel1.setText("<html>For adding an order, please go to order menu online<html>");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_today_date, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btn_exit_true, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_delete_true, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_modifyPayment_true, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_refreshPayment_true, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_today_date, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(btn_exit_true)
                .addGap(18, 18, 18)
                .addComponent(btn_delete_true)
                .addGap(18, 18, 18)
                .addComponent(btn_modifyPayment_true)
                .addGap(18, 18, 18)
                .addComponent(btn_refreshPayment_true)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
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

        combo_product.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        combo_customer.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        combo_customer.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                combo_customerItemStateChanged(evt);
            }
        });
        combo_customer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_customerActionPerformed(evt);
            }
        });

        jLabel35.setText("Member ID :");

        jLabel23.setText("Order date :");

        jLabel27.setText("Order Exp date :");

        total_order.setText("Total Order Record : ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(total_order, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel35, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel27, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(idate_order, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE)
                                            .addComponent(idate_exp, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(combo_customer, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(17, 17, 17))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(neon_img_label, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(btn_addProduct_true, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(combo_product, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_delete_product_true)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_refresh_product, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(5, 5, 5))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(message_product_detail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(268, 268, 268))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 841, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(message_product_detail, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(neon_img_label, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(total_order, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(4, 4, 4)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btn_addProduct_true)
                            .addComponent(btn_delete_product_true)
                            .addComponent(btn_refresh_product)
                            .addComponent(combo_product, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(combo_customer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 325, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(idate_order, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(idate_exp, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_exit_trueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_exit_trueActionPerformed
        SubInterface_Login_Staff.validate_login_go(new Staff(SubInterface_Login_Staff.decode_id(getDataDutyStaff())));
        dispose();
    }//GEN-LAST:event_btn_exit_trueActionPerformed

    private void table_detailKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_table_detailKeyTyped
    }//GEN-LAST:event_table_detailKeyTyped

    private void table_orderMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table_orderMouseClicked
        
    }//GEN-LAST:event_table_orderMouseClicked

    private void table_orderMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table_orderMousePressed
        int y = this.table_order.getSelectedRow();
        if(y >= 0){
            table_detail.clearSelection();
            while(order_detail.getRowCount() > 0){
                this.order_detail.removeRow(0);
            }
            current_order = new Order(this.read_order_row(y).getOrder_num());
            System.out.println(current_order.toString());
            for (int i = 0; i < current_order.getProduct_select().size(); i++) {
                this.write_product_row(current_order.getProduct_select().get(i));
            }
            if(WriteSql.getUniqueInt("SELECT COUNT(customer_id) FROM Customer WHERE customer_id='" + current_order.getMember_num() + "';") == 1)
                this.combo_customer.setSelectedItem(current_order.getMember_num());
            else                
                this.combo_customer.setSelectedItem("~ Select");
            this.idate_order.setSelectedDate(convertDtoC(current_order.getOrder_date()));
            this.idate_exp.setSelectedDate(convertDtoC(current_order.getOrder_expiry_date()));
            this.label_productTotal_true.setText(String.format("%d",WriteSql.getUniqueInt("SELECT SUM(product_quantity) FROM Order_record_detail WHERE Order_id='" + current_order.getOrder_num() + "';")));
            this.label_price_true.setText(String.format("%.2f", WriteSql.getUniqueDouble("SELECT SUM(product_quantity * unit_final_price) FROM Order_record_detail WHERE Order_id='" + current_order.getOrder_num() + "';")));
        }else{
            table_detail.clearSelection();
            while(order_detail.getRowCount() > 0){
                this.order_detail.removeRow(0);
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
        if(i >= 0){
            current_order = this.read_order_row(i);
            if (JOptionPane.showConfirmDialog(null, "Are you sure want to modify the record of " + current_order.getOrder_num() + ", your changers will not able to recover ? ", "Confrim Modify", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                current_order.setMember_id((String)this.combo_customer.getSelectedItem());
                current_order.setOrder_expiry_date(this.idate_exp.getSelectedDate().getTime());
                current_order.setOrder_date(this.idate_order.getSelectedDate().getTime());
                this.modify_order_row(current_order, i);
                int x = this.table_detail.getRowCount();
                for (int j = 0; j < x; j++) {
                    current_order.add_product(this.read_table_detail_row(j));
                }
                current_order.modify_this();
            }
        }
    }//GEN-LAST:event_btn_modifyPayment_trueActionPerformed

    private void btn_addProduct_trueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_addProduct_trueActionPerformed
        if (table_order.getSelectedRow() >= 0) {
            if (WriteSql.getUniqueInt("SELECT COUNT(PRODUCT_ID) FROM Product WHERE PRODUCT_ID='" + (String) combo_product.getSelectedItem() + "';") >= 1 && table_order.getSelectedRow() >= 0) {
                new OrderDetail(this.read_order_row(table_order.getSelectedRow()).getOrder_num(), (String) combo_product.getSelectedItem(), 1).write_this();
                this.write_product_row(new OrderDetail(current_order.getOrder_num(), current_product.getProduct_id()));
                this.table_detail.clearSelection();
                refresh_total();
            }
        } else {
            JOptionPane.showConfirmDialog(null, "No row is selected in the table !!", "No selection", JOptionPane.OK_OPTION);
        }
    }//GEN-LAST:event_btn_addProduct_trueActionPerformed

    private void btn_delete_product_trueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_delete_product_trueActionPerformed
        int i = table_detail.getSelectedRow();
        int j = table_order.getSelectedRow();
        if (i >= 0 && j >= 0) {
            OrderDetail needDelete = new OrderDetail();
            needDelete.setOrder_id((String)data_order.getValueAt(j, 0));
            needDelete.setProduct_id((String)order_detail.getValueAt(i, 0));
            if (JOptionPane.showConfirmDialog(null, "Are you sure want to delete the product of " + needDelete.getProduct_id() +  " in the order record of " + needDelete.getOrder_id() + " !!", "Confrim Delete", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if (needDelete.delete_this()) {
                    message_product_detail.setText("Product Detial : Delete successful !! ");
                    this.order_detail.removeRow(i);
                } else {
                    message_product_detail.setText("Product Detial : Delete unsuccessful !! ");
                }
                this.table_detail.clearSelection();
                refresh_total();
            }
        }else{
            JOptionPane.showConfirmDialog(null, "No row is selected in the table !!", "No selection", JOptionPane.OK_OPTION);
        }
    }//GEN-LAST:event_btn_delete_product_trueActionPerformed

    private void table_orderKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_table_orderKeyTyped
    }//GEN-LAST:event_table_orderKeyTyped

    private void btn_delete_trueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_delete_trueActionPerformed
        int i = table_order.getSelectedRow();
        if (i >= 0) {
            current_order = new Order((String) data_order.getValueAt(i, 0));
            if (JOptionPane.showConfirmDialog(null, "Are you sure want to delete the record of " + current_order.getOrder_num() + ", you apply will not able to recover ? ", "Confrim Delete", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if (current_order.delete_this()) {
                    table_order.remove(i);
                }
            }
        }
        else{
            JOptionPane.showConfirmDialog(null, "No row is selected in the table !!", "No selection", JOptionPane.OK_OPTION);
        }
        total_order.setText("Total Order Record : " + this.table_order.getRowCount());
    }//GEN-LAST:event_btn_delete_trueActionPerformed

    private void btn_refresh_productActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_refresh_productActionPerformed
        table_orderMousePressed(null);
    }//GEN-LAST:event_btn_refresh_productActionPerformed

    private void btn_refreshPayment_trueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_refreshPayment_trueActionPerformed
        initComponents_data_payment();
        total_order.setText("Total Order Record : " + this.table_order.getRowCount());
    }//GEN-LAST:event_btn_refreshPayment_trueActionPerformed

    private void combo_customerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_customerActionPerformed
        // TODO add your handling code here:
        int i = this.table_order.getSelectedRow();
        if(i >= 0 && combo_customer.getSelectedIndex() != 0){
            this.data_order.setValueAt((String)this.combo_customer.getSelectedItem(), i, 1);
        }
    }//GEN-LAST:event_combo_customerActionPerformed

    private void table_detailMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table_detailMousePressed
        // TODO add your handling code here:
        int i = this.table_detail.getSelectedRow();
        i_quantity.setText((String)this.order_detail.getValueAt(i,2));
    }//GEN-LAST:event_table_detailMousePressed

    private void btn_update_qntActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_update_qntActionPerformed
        // TODO add your handling code here:
        int i = this.table_detail.getSelectedRow();
        if (i > 0) {
            if (isInt(this.i_quantity.getText())) {
                this.order_detail.setValueAt(i_quantity.getText(), i, 2);
                this.read_table_detail_row(i).modify_this();
            }else{
                JOptionPane.showConfirmDialog(null, "Quantity enter nust be integer and more than negetive", "Mumbe rformat eception erorr", JOptionPane.WARNING_MESSAGE);
            }
        }else{
            JOptionPane.showConfirmDialog(null, "No row selected in the order detail list please select a product or order before modify the updated data", "Selection exception", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btn_update_qntActionPerformed

    private void combo_customerItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_combo_customerItemStateChanged
        // TODO add your handling code her
        
    }//GEN-LAST:event_combo_customerItemStateChanged

    private void i_quantityKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_i_quantityKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode() == 10){
            this.btn_update_qntActionPerformed(null);
        }
    }//GEN-LAST:event_i_quantityKeyPressed

    private void table_orderKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_table_orderKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_table_orderKeyPressed

    private void table_orderKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_table_orderKeyReleased
        // TODO add your handling code here:
        if(evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN)
            this.table_orderMousePressed(null);
    }//GEN-LAST:event_table_orderKeyReleased

    private boolean isInt(String string){
        try {
            Integer.parseInt(string);
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
        return Integer.parseInt(string) >= 0;
    }
    
    private void refresh_total(){
        for(int i = 0; i < this.table_detail.getRowCount(); i++){
            this.current_order = new Order();
            current_order.add_product(read_table_detail_row(i));
        }
        int total_product = 0;
        double total_price = 0.00, total_discount = 0.00, total_sst;
        for(int i = 0; i < this.table_detail.getRowCount(); i++){
            total_product += current_order.getProduct_id_indexof(i).getQuantity();
            total_price +=  current_order.getProduct_id_indexof(i).getSubTotalAfterDiscount();
            total_discount +=  current_order.getProduct_id_indexof(i).getDiscountPrice();
        }
        int j = this.table_order.getSelectedRow();
        current_order = new Order((String)table_order.getValueAt(j , 0));
        this.label_productTotal_true.setText(String.format("%02d", total_product));
        this.label_price_true.setText(String.format("%02.2f", total_price));
    }
    
    
    public final String getDataDutyStaff(){
        File temp_f = new File("resource/data/staff_id_logedin.txt");
        if (temp_f.isFile()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(temp_f));
                try {
                    return br.readLine();
                } catch (IOException ex) {
                    Logger.getLogger(Interface_MenuManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }catch (FileNotFoundException ex) {
                Logger.getLogger(Interface_MenuManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return "";
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
    private javax.swing.JButton btn_update_qnt;
    private javax.swing.JComboBox<String> combo_customer;
    private javax.swing.JComboBox<String> combo_product;
    private javax.swing.JTextField i_quantity;
    private datechooser.beans.DateChooserPanel idate_exp;
    private datechooser.beans.DateChooserPanel idate_order;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel label_price_true;
    private javax.swing.JLabel label_productTotal_true;
    private javax.swing.JLabel label_today_date;
    private javax.swing.JLabel message_product_detail;
    private javax.swing.JLabel neon_img_label;
    private javax.swing.JTable table_detail;
    private javax.swing.JTable table_order;
    private javax.swing.JLabel total_order;
    // End of variables declaration//GEN-END:variables


}
