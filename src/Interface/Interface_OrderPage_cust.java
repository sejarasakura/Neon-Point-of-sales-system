package Interface;


import Main.Main;
import Transaction.Order;
import Transaction.OrderDetail;
import Transaction.Payment;
import Product.Product;
import SubInterface.SubInterface_Login_Staff;
import Sql.WriteSql;


import static Interface.Interface_Payment.FORMAT_STRING;
import static Interface.Interface_Payment.FORMAT_STRING_EXTRA_NAME;
import static Interface.Interface_Payment.FORMAT_STRING_EXTRA_PROMOTION;
import com.sun.glass.events.KeyEvent;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ITSUKA KOTORI
 */
public class Interface_OrderPage_cust extends javax.swing.JFrame {

    private DefaultTableModel i_product_model = new DefaultTableModel();
    private DefaultTableModel i_order_model = new DefaultTableModel();
    private boolean onsearch = false;
    private int receipt_index_count = 0;
    /**
     * Creates new form CustomerOrderPage
     */
    public Interface_OrderPage_cust() {
        initComponents();
        initComponents_Interface();
        initComponents_Table();
        initComponents_data_product();
        initComponents_SetClose();
        initComponents_data_order();
        this.setIconImage(Main.DEFUALT_ICON.getImage());
    }
    
    private Payment initComponents_payment_plan(Order order, String ext_data, String ext_label){
        Payment payment = new Payment(order, "CM0001", Main.getToday_date());
        double price = caluculateTotal(order);
        payment.setSSTCharge(0.06);
        this.ext_label.setText(ext_label);
        this.ext_data.setText(ext_data);
        this.label_SSTCharge_true1.setText(String.format("%.2f", payment.getSst_charge()*price));
        this.label_member1.setText(payment.getCustomer_id());
        this.label_order1.setText(order.getOrder_num());
        this.label_payment_id_true1.setText(payment.getPayment_id());
        this.label_productTotal_true1.setText(WriteSql.getUniqueInt("SELECT SUM(product_quantity) FROM ORDER_RECORD_DETAIL WHERE ORDER_ID='" +order.getOrder_num()+ "';") + "");
        this.label_staff1.setText("System : " + payment.getStaff_id());
        this.label_total_price_true1.setText(String.format("%.2f", price));
        this.label_sub_total.setText(String.format("%.2f", price * 1 + payment.getSst_charge()));
        write_receipt(order, payment);
        payment.write_this();
        return payment;
    }
    
    private void write_line_receipt(Product product, int total_buy, double promotion){    
        write_line_receipt(product, total_buy);
        this.receiptArea.append(String.format(FORMAT_STRING_EXTRA_PROMOTION, promotion , "% discount !!"));
        this.receiptArea.append("\n");
    }
    
    private void write_line_receipt(Product product, int total_buy){
        if(product.getProduct_name().length() <= 28)
            this.receiptArea.append(String.format(FORMAT_STRING, receipt_index_count + 1, product.getProduct_name(), total_buy, product.getPriceAfterDiscount() * total_buy));
        else{
            for(int i = 0; i < product.getProduct_name().length(); i += 28)
            {
                if(i == 0)            
                    this.receiptArea.append(String.format(FORMAT_STRING, receipt_index_count + 1, 
                            product.getProduct_name().substring(i, (i + 28)), 
                            total_buy, product.getRetial_price() * total_buy));
                else{
                    if(!((i + 28) >= product.getProduct_name().length())){
                        this.receiptArea.append(String.format(FORMAT_STRING_EXTRA_NAME, 
                                product.getProduct_name().substring(i, (i + 28))));
                    }
                    else{
                        this.receiptArea.append(String.format(FORMAT_STRING_EXTRA_NAME, 
                                product.getProduct_name().substring(i, 
                                        (product.getProduct_name().length()))));
                    }
                }
            }
        }
        if(total_buy <= 0){
            this.receiptArea.append(String.format(FORMAT_STRING_EXTRA_NAME, " ( out of stock ) "));
        }
        this.receiptArea.append("\n");
        receipt_index_count++;
    }
    
    private void write_receipt(Order order_fro_payment, Payment payment){
        Product temp;
        receipt_index_count = 0;
        this.receiptArea.setText(Interface_Payment.HEADER_TITLE + "\n");
        for(int i = 0; i < order_fro_payment.getProduct_select().size(); i++){
            temp = new Product(order_fro_payment.getProduct_id_indexof(i).getProduct_id());
            if(true) {
                if(temp.getPromoting()) {
                    this.write_line_receipt(temp, order_fro_payment.getProduct_select().get(i).getQuantity(), temp.getPromotion_persentage());                    
                }
                else {
                    this.write_line_receipt(temp, order_fro_payment.getProduct_select().get(i).getQuantity());
                }
            }
        }
        payment.setSSTCharge(0.1);
        this.receiptArea.append("\n" + Interface_Payment.MAX_LINE_2 + "\n");
        this.receiptArea.append(String.format(Interface_Payment.FORMAT_STRING_TOTAL, caluculateTotal(order_fro_payment)));
        this.receiptArea.append(String.format(Interface_Payment.FORMAT_STRING_TOTAL_SST, caluculateTotal(order_fro_payment) * 0.06));
        this.receiptArea.append("\n" + Interface_Payment.MAX_LINE);
        this.receiptArea.append("\n" + String.format("  Payment id        : %s \n", payment.getPayment_id()));
        this.receiptArea.append("\n" + String.format("  Order id          : %s \n", order_fro_payment.getOrder_num()));
        this.receiptArea.append("\n" + String.format("  Member id         : %s \n", payment.getCustomer_id()));
        this.receiptArea.append(Interface_Payment.RECEIPT_FOOTER);
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter("result/receipt.txt", true);
            try (BufferedWriter buffer = new BufferedWriter(fileWriter)) {
                buffer.write(this.receiptArea.getText() + "\n\n\n");
                buffer.flush();
                buffer.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(SubInterface_Login_Staff.class.getName()).log(Level.SEVERE, null, ex);
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

        payment_pane = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        label_productTotal_true1 = new javax.swing.JLabel();
        label_SSTCharge_true1 = new javax.swing.JLabel();
        label_total_price_true1 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        label_payment_id_true1 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        label_order1 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        label_member1 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        label_staff1 = new javax.swing.JLabel();
        ext_label = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        ext_data = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        label_sub_total = new javax.swing.JLabel();
        receipt_pane = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        receiptArea = new javax.swing.JTextArea();
        jLabel7 = new javax.swing.JLabel();
        payment_selection = new javax.swing.JPanel();
        payment_method = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        i_product = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        search_box = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        i_order = new javax.swing.JTable();
        input_order_id = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        neon_img_label = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        input_order_date = new datechooser.beans.DateChooserCombo();
        input_order_exp = new datechooser.beans.DateChooserCombo();
        label_today_date = new javax.swing.JLabel();
        member_label = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        error_label = new javax.swing.JLabel();

        jLabel12.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel12.setText("Total Product");

        jLabel15.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel15.setText("Total SST Charge");

        jLabel16.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel16.setText(":");

        jLabel22.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel22.setText(": ( RM )");

        label_productTotal_true1.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label_productTotal_true1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_productTotal_true1.setText("00 ");

        label_SSTCharge_true1.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label_SSTCharge_true1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_SSTCharge_true1.setText("00.00 ");

        label_total_price_true1.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label_total_price_true1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_total_price_true1.setText("00.00 ");

        jLabel26.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel26.setText(": ( RM )");

        jLabel27.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel27.setText("Total Price");

        label_payment_id_true1.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label_payment_id_true1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_payment_id_true1.setText("0000000000 ");

        jLabel28.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel28.setText(": ");

        jLabel29.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel29.setText("Payment ID");

        label_order1.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label_order1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_order1.setText("OR00000000 ");

        jLabel30.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel30.setText("Order ID");

        jLabel31.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel31.setText(": ");

        label_member1.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label_member1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_member1.setText("XX9999 ");

        jLabel32.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel32.setText(": ");

        jLabel33.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel33.setText("Member ID");

        jLabel34.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel34.setText("Handle Staff");

        jLabel35.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel35.setText(": ");

        label_staff1.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label_staff1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_staff1.setText("XX9999 ");

        ext_label.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N

        jLabel39.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel39.setText(": ");

        ext_data.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        ext_data.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel33, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel34, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ext_label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel39, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ext_data, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel35, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_staff1, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel32, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_member1, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel31, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_order1, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_payment_id_true1, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)
                            .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label_SSTCharge_true1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_price_true1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_productTotal_true1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_productTotal_true1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_SSTCharge_true1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_price_true1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_payment_id_true1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_order1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_member1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_staff1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(ext_label, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ext_data, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jLabel36.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel36.setText("Sub-Total ");

        jLabel37.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel37.setText(": ( RM )");

        label_sub_total.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label_sub_total.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label_sub_total.setText("00.00 ");

        javax.swing.GroupLayout payment_paneLayout = new javax.swing.GroupLayout(payment_pane);
        payment_pane.setLayout(payment_paneLayout);
        payment_paneLayout.setHorizontalGroup(
            payment_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(payment_paneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(payment_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(payment_paneLayout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(payment_paneLayout.createSequentialGroup()
                        .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel37, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_sub_total, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        payment_paneLayout.setVerticalGroup(
            payment_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(payment_paneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(payment_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(payment_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_sub_total, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        receiptArea.setColumns(20);
        receiptArea.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        receiptArea.setRows(5);
        receiptArea.setText("\n\n                       WELCOME TO                     \n                _____ _____ _____ _____               \n               |   | |  ___|     |   | |              \n               | | | |  ___|  |  | | | |              \n               |_|___|_____|_____|_|___|              \n                                                      \n\n           NEON - Small (M) Sdn Bhd (242659-T)        \n              Perseteria Wangsa NEON - Small          \n                                                      \n                                                      \n  Today Date :                     12-8-2019 08:39:14 \n  TEL : 0956-26-5310               FAX : 0956-39-5650 \n  URL : http://www.neon-perseteria.co.jp              \n                                                      \n |---------------------------------------------------|\n |                      RECEIPT                      |\n |---------------------------------------------------|\n                                                      \n  * Join neon member to collect more point to change  \n    free gift at special counter                      \n                                                      \n  No.Product                      Qty      Total (RM) \n =====================================================\n\n  1 Oil - Sunflower                 1          10.50  \n\n  2 Bandage - Flexible Neon         1          46.89  \n\n  3 Wine - Chateauneuf Du Pape      1          11.90  \n\n  4 Oil - Sunflower                 1          10.50  \n\n  5 Veal - Brisket, Provimi,bnls    1          22.41  \n\n    Promotion 10.00% discount !!                      \n\n\n -----------------------------------------------------\n\n  Total      :                                  102.20 \n  TAX Charge :                                   10.22 \n\n =====================================================\n\n  Payment id        : 0000000001 \n\n  Order id          : OR00000011 \n\n  Member id         : null \n                                                      \n\n          For returns / canclelation, please          \n    ensure taht the original receipt and vouchers     \n  (if any) are presented, Terms and condition apply   \n\n         Pleases come agian to NEON - SMALL !!        \n                    Thank You ! ! ! !                 \n                                                      ");
        receiptArea.setEnabled(false);
        jScrollPane3.setViewportView(receiptArea);

        jLabel7.setText("<html><body><span color='red'>Your receipt is already generated if did not resive the delivering later please dail to the phone number or email to us, receipt is required to ensure the delivery</span></body></html>");

        javax.swing.GroupLayout receipt_paneLayout = new javax.swing.GroupLayout(receipt_pane);
        receipt_pane.setLayout(receipt_paneLayout);
        receipt_paneLayout.setHorizontalGroup(
            receipt_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 418, Short.MAX_VALUE)
            .addGroup(receipt_paneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        receipt_paneLayout.setVerticalGroup(
            receipt_paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(receipt_paneLayout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 606, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        payment_method.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        payment_method.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "~ Select", "Ali Pay", "Visa Card", "Master Card", "Touch and Go", "Pay later" }));

        jLabel9.setText("<html><body><h1>Please select a payment method to pay to us</h1><span></span></body></html>");

        javax.swing.GroupLayout payment_selectionLayout = new javax.swing.GroupLayout(payment_selection);
        payment_selection.setLayout(payment_selectionLayout);
        payment_selectionLayout.setHorizontalGroup(
            payment_selectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, payment_selectionLayout.createSequentialGroup()
                .addGap(80, 80, 80)
                .addComponent(payment_method, 0, 194, Short.MAX_VALUE)
                .addGap(80, 80, 80))
            .addGroup(payment_selectionLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        payment_selectionLayout.setVerticalGroup(
            payment_selectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, payment_selectionLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(payment_method, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Online ordering system");

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setBorder(null);
        jScrollPane1.setForeground(new java.awt.Color(225, 225, 225));
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setToolTipText("");
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jScrollPane1.setDebugGraphicsOptions(javax.swing.DebugGraphics.NONE_OPTION);
        jScrollPane1.setHorizontalScrollBar(null);
        jScrollPane1.setVerifyInputWhenFocusTarget(false);

        i_product.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "Product ID", "Product Name", "Price", "Stock"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        i_product.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                i_productMousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(i_product);
        if (i_product.getColumnModel().getColumnCount() > 0) {
            i_product.getColumnModel().getColumn(0).setPreferredWidth(10);
            i_product.getColumnModel().getColumn(1).setPreferredWidth(60);
            i_product.getColumnModel().getColumn(2).setPreferredWidth(160);
            i_product.getColumnModel().getColumn(3).setPreferredWidth(40);
            i_product.getColumnModel().getColumn(4).setPreferredWidth(40);
        }

        jButton1.setText("Add");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        search_box.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                search_boxActionPerformed(evt);
            }
        });
        search_box.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                search_boxKeyPressed(evt);
            }
        });

        jScrollPane2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jScrollPane2KeyTyped(evt);
            }
        });

        i_order.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Product ID", "Product Name", "Quantity", "Subtotal"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        i_order.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                i_orderMousePressed(evt);
            }
        });
        i_order.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                i_orderKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                i_orderKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                i_orderKeyTyped(evt);
            }
        });
        jScrollPane2.setViewportView(i_order);
        if (i_order.getColumnModel().getColumnCount() > 0) {
            i_order.getColumnModel().getColumn(0).setPreferredWidth(100);
            i_order.getColumnModel().getColumn(1).setPreferredWidth(300);
            i_order.getColumnModel().getColumn(2).setPreferredWidth(100);
            i_order.getColumnModel().getColumn(3).setPreferredWidth(100);
        }

        input_order_id.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        input_order_id.setText("Generate by system ");
        input_order_id.setEnabled(false);

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel1.setText("Order ID");

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel2.setText("Order Date");

        jLabel3.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel3.setText("Member ID");

        jLabel4.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel4.setText("Expriy Date");

        jLabel5.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel5.setText(" forgot the mistake , remember the lesson");

        jLabel6.setText(" ");
        jLabel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(13, 146, 56), 2));
        jLabel6.setPreferredSize(new java.awt.Dimension(5, 3));

        jLabel8.setText(" ");
        jLabel8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(13, 146, 56), 2));

        input_order_date.setEnabled(false);
        input_order_date.setFieldFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 14));

        input_order_exp.setFieldFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 14));

        label_today_date.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label_today_date.setText("Today Date : 12-Feb-2019 12:20:56 pm");

        member_label.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(32, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(input_order_exp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(input_order_id, javax.swing.GroupLayout.PREFERRED_SIZE, 593, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(input_order_date, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(member_label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(0, 22, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(label_today_date, javax.swing.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
                            .addComponent(neon_img_label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(label_today_date, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(neon_img_label, javax.swing.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(148, 148, 148)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(input_order_id, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                    .addComponent(input_order_date, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(member_label, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(input_order_exp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6))
        );

        jButton2.setText("Add Product ");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Delete Product ");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Back meniu");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText("Commit");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        error_label.setText("Press enter key to direct add product");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(search_box, javax.swing.GroupLayout.DEFAULT_SIZE, 344, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(error_label, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(error_label)
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(search_box, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 600, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton5)
                        .addComponent(jButton4))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton2)
                        .addComponent(jButton3)))
                .addGap(15, 15, 15))
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        new Interface_MenuCustomer().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void i_productMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_i_productMousePressed
        if(evt.getClickCount() == 2){
            int i = i_product.getSelectedRow();
            write_order_row(new OrderDetail(this.input_order_id.getText(),(String)i_product_model.getValueAt(i, 1), 1));
        }
        
    }//GEN-LAST:event_i_productMousePressed

    private void i_orderMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_i_orderMousePressed

        int i = i_order.getSelectedRow();
        Product procduct = new Product((String)i_order_model.getValueAt(i, 0));

    }//GEN-LAST:event_i_orderMousePressed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Product x = new Product(search_box.getText());
        if(x.notNull()){
            this.write_order_row(new OrderDetail(this.input_order_id.getText(),x.getProduct_id(), 1));
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        if(this.i_product.getSelectedRow() >= 0){
            System.out.println((String)i_product_model.getValueAt(i_product.getSelectedRow(), 1));
            this.write_order_row(new OrderDetail(this.input_order_id.getText(), (String)i_product_model.getValueAt(i_product.getSelectedRow(), 1), 1));
        }
        else
            JOptionPane.showConfirmDialog(null, "No row is selected by user, please select a product in product table before add", "No selected row", JOptionPane.WARNING_MESSAGE);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        if(i_order.getSelectedRow() >= 0)
            this.i_order_model.removeRow(i_order.getSelectedRow());
        else
            JOptionPane.showConfirmDialog(null, "No row is selected by user, please select a product in order table before delete", "No selected row", JOptionPane.WARNING_MESSAGE);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void search_boxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_search_boxKeyPressed
        if (evt.getKeyCode() != 10) {
            if(WriteSql.getUniqueInt("SELECT TOP 1 COUNT(product_name) FROM Product WHERE product_id LIKE '" + search_box.getText() + "' OR  product_id LIKE '" + search_box.getText() + "%' OR product_name LIKE '" + search_box.getText() + "' OR product_name LIKE '" + search_box.getText() + "%' ORDER BY product_id;") >= 1)
            error_label.setText(" # " + WriteSql.getUniqueString("SELECT TOP 1 product_name FROM Product WHERE product_id LIKE '" + search_box.getText() + "' OR  product_id LIKE '" + search_box.getText() + "%' OR product_name LIKE '" + search_box.getText() + "' OR product_name LIKE '" + search_box.getText() + "%' ORDER BY product_id;"));
        } else {
            jButton1ActionPerformed(null);
        }
    }//GEN-LAST:event_search_boxKeyPressed

    private void search_boxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_search_boxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_search_boxActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        Order temp = new Order();
        Payment payment = new Payment();
        if (i_order_model.getRowCount() > 0) {
            try {
                temp = new Order(input_order_id.getText(),
                        WriteSql.DATE_FORMAT.parse(input_order_exp.getText()),
                        WriteSql.DATE_FORMAT.parse(input_order_date.getText()),
                        member_label.getText());
                while (this.i_order_model.getRowCount() > 0) {
                    temp.add_product(this.get_order_row(0));
                    this.i_order_model.removeRow(0);
                }
                temp.write_this();
                set_input();
                
            } catch (ParseException ex) {
                Logger.getLogger(Interface_OrderPage_cust.class.getName()).log(Level.SEVERE, null, ex);
            }
            Object[] something = {"Pay now", "Pay Later", "Cancel"}, option_pay = {"Confrim pay", "Pay Later", "Cancel Pay"};
            int result2 = -99, result = JOptionPane.showOptionDialog(null, "Do you want to make the payment now ??", "Make the payment now", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, something, null);
            if (result == JOptionPane.YES_OPTION) {
                if(JOptionPane.showConfirmDialog(null, this.payment_method, "Payment Method", JOptionPane.INFORMATION_MESSAGE) == JOptionPane.OK_OPTION){
                    String s1 = "";
                    switch(this.payment_method.getSelectedIndex()){
                        // 0 .... ~ Select, 1 ....  Ali Pay, 2 .... Visa Card, Master Card, Touch and Go, Pay later
                        case 0:
                            JOptionPane.showConfirmDialog(null, "You are not selected the payment, the payment has canceled please repay agian before " + WriteSql.DATE_FORMAT.format(temp.getOrder_expiry_date()) + " Thank you !!", "No payment selected", JOptionPane.WARNING_MESSAGE);
                            break;
                        case 1:
                            payment = this.initComponents_payment_plan(temp,"AL" + String.format("%04d%04d", Math.round(Math.random() * 10000), Math.round(Math.random() * 10000)) + "", "Alipay Acoount");
                            result2 = JOptionPane.showOptionDialog(null, this.payment_pane, "Comfrim Paid By Ali Pay", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, option_pay, null);
                            break;
                        case 2:
                            payment = this.initComponents_payment_plan(temp, String.format("%04d %04d %04d %04d", Math.round(Math.random() * 10000), Math.round(Math.random() * 10000), Math.round(Math.random() * 10000), Math.round(Math.random() * 10000)), "visacard number");
                            result2 = JOptionPane.showOptionDialog(null, this.payment_pane, "Comfrim Paid By Visacard", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, option_pay, null);
                            break;
                        case 3:
                            payment = this.initComponents_payment_plan(temp, String.format("%04d %04d %04d %04d", Math.round(Math.random() * 10000), Math.round(Math.random() * 10000), Math.round(Math.random() * 10000), Math.round(Math.random() * 10000)), "mastercard number");
                            result2 = JOptionPane.showOptionDialog(null, this.payment_pane, "Comfrim Paid By Mastercard", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, option_pay, null);
                            break;
                        case 4:
                            payment = this.initComponents_payment_plan(temp, String.format("%04d%04d", Math.round(Math.random() * 10000), Math.round(Math.random() * 10000)), "TnG Serial NO");
                            result2 = JOptionPane.showOptionDialog(null, this.payment_pane, "Comfrim Paid By Tng online", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, option_pay, null);
                            break;
                        case 5:
                            JOptionPane.showConfirmDialog(null, "You are not selected the payment, the payment has delayed please repay agian before " + WriteSql.DATE_FORMAT.format(temp.getOrder_expiry_date()) + " Thank you !!", "No payment selected", JOptionPane.WARNING_MESSAGE);
                            break;
                    }
                    if(result2 >= -1){
                        switch (result2) {
                        // Paid display receipt
                            case JOptionPane.YES_OPTION:
                                if (payment.isNotNull()) {              
                                    this.write_receipt(temp, payment);
                                    JOptionPane.showMessageDialog(null, this.receipt_pane, "This Is Your Receipt", JOptionPane.CLOSED_OPTION);
                                } else {
                                    JOptionPane.showConfirmDialog(null, "The payment not yet approved please check agian", "Error payment proccess", JOptionPane.ERROR_MESSAGE);
                                }
                                break;
                            default:
                                temp.delete_this();
                                JOptionPane.showConfirmDialog(null, "You are delayed the payment please repay agian before " + WriteSql.DATE_FORMAT.format(temp.getOrder_expiry_date()) + " Thank you !!", "Payment not approve", JOptionPane.WARNING_MESSAGE);
                                break;
                        }
                    }
                }
            } 
            else{
                JOptionPane.showConfirmDialog(null, "Pleas ebe remind that your payment will be exp by " + WriteSql.DATE_FORMAT.format(temp.getOrder_expiry_date()) + " ,Please pay before that !! Thank you !! ", "Payment delay", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showConfirmDialog(null, "No product choosen, choose at least one product before commit the order than you", "No product selected", JOptionPane.WARNING_MESSAGE);
        }
        
    }//GEN-LAST:event_jButton5ActionPerformed

    private void i_orderKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_i_orderKeyPressed
    }//GEN-LAST:event_i_orderKeyPressed

    private void jScrollPane2KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jScrollPane2KeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_jScrollPane2KeyTyped

    private void i_orderKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_i_orderKeyTyped
    }//GEN-LAST:event_i_orderKeyTyped

    private void i_orderKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_i_orderKeyReleased
        int i = i_order.getSelectedRow();
        Product tempProc = new Product((String)i_order_model.getValueAt(i, 0));
        OrderDetail order = new OrderDetail();
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            if(i_order.getSelectedColumn() == 2){
                if(order.setQuantity(Integer.valueOf((String)i_order_model.getValueAt(i, 2)))){
                    i_order_model.setValueAt(String.format("RM %.2f", tempProc.getPriceAfterDiscount() * order.getQuantity()), i, 3);
                    i_order_model.setValueAt(String.format("%d", order.getQuantity()), i, 2);
                    i_order_model.setValueAt(String.format("%.2f%%",order.getUnit_discount() * 100) , i, 4);
                }else{
                    i_order_model.setValueAt("#Error", i, 3);
                    i_order_model.setValueAt("#Error", i, 2);
                }
            }else if(i_order.getSelectedColumn() == 4){
                if(order.setUnit_discount(Double.valueOf(((String)i_order_model.getValueAt(i, 4)).replace('%', '0')))){
                    i_order_model.setValueAt(String.format("RM %.2f", tempProc.getPriceAfterDiscount() * order.getQuantity()), i, 3);
                    i_order_model.setValueAt(String.format("%.2f%%",order.getUnit_discount() * 100) , i, 4);
                }else{
                    i_order_model.setValueAt("#Error", i, 3);
                    i_order_model.setValueAt("#Error", i, 4);
                }
            }
            System.out.println("EnterKeyPress");
        }
    }//GEN-LAST:event_i_orderKeyReleased

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel error_label;
    private javax.swing.JLabel ext_data;
    private javax.swing.JLabel ext_label;
    private javax.swing.JTable i_order;
    private javax.swing.JTable i_product;
    private datechooser.beans.DateChooserCombo input_order_date;
    private datechooser.beans.DateChooserCombo input_order_exp;
    private javax.swing.JTextField input_order_id;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel label_SSTCharge_true1;
    private javax.swing.JLabel label_member1;
    private javax.swing.JLabel label_order1;
    private javax.swing.JLabel label_payment_id_true1;
    private javax.swing.JLabel label_productTotal_true1;
    private javax.swing.JLabel label_staff1;
    private javax.swing.JLabel label_sub_total;
    private javax.swing.JLabel label_today_date;
    private javax.swing.JLabel label_total_price_true1;
    private javax.swing.JLabel member_label;
    private javax.swing.JLabel neon_img_label;
    private javax.swing.JComboBox<String> payment_method;
    private javax.swing.JPanel payment_pane;
    private javax.swing.JPanel payment_selection;
    private javax.swing.JTextArea receiptArea;
    private javax.swing.JPanel receipt_pane;
    private javax.swing.JTextField search_box;
    // End of variables declaration//GEN-END:variables

    private void initComponents_Interface() {
        this.neon_img_label.setIcon(new ImageIcon(Interface_Payment.getScaledImage(new ImageIcon("resource/neon.png").getImage(), 262, 80)));
        this.input_order_exp.setDateFormat(WriteSql.DATE_FORMAT);
        this.input_order_date.setDateFormat(WriteSql.DATE_FORMAT);
        label_today_date.setText("Today Date : " + Main.DATE_FORMAT.format(Main.getToday_date()));
        this.member_label.setText(Main.getCustomer());
    }

    private void initComponents_Table() {
        Font font = new Font("", 0, 12);
        i_product.setBackground(Color.white);
        i_product.setForeground(Color.DARK_GRAY);
        i_product.setFont(font);
        i_product.setRowHeight(20);
        i_product_model = (DefaultTableModel)i_product.getModel();
        i_order.setBackground(Color.white);
        i_order.setForeground(Color.DARK_GRAY);
        i_order.setFont(font);
        i_order.setRowHeight(20);
        i_order_model = (DefaultTableModel)i_order.getModel();
    }

    private void initComponents_data_order() {
        set_input();
    }
    
    private void initComponents_data_product() {
        Product temp = new Product();
        try{            
            try (Connection conn = DriverManager.getConnection("jdbc:ucanaccess://resource/data/Point_of_sales.mdb;"); Statement s = conn.createStatement()) {
                s.execute("SELECT * FROM Product;");
                try (ResultSet result = s.getResultSet()) {
                    while(result.next()){
                        temp.split(result);
                        this.write_product_row(temp);
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
                jButton4ActionPerformed(null);
            }
        });
    }

    private void write_order_row(OrderDetail x) {
        for(int i = 0; i < i_order.getRowCount(); i++){
            if(((String)this.i_order_model.getValueAt(i, 0)).equals(x.getProduct_id())){
                int newQuantity = Integer.valueOf((String)i_order_model.getValueAt(i, 2)) + 1;
                i_order_model.setValueAt(String.format("%d", newQuantity), i, 2);
                Product tempProc = new Product((String)i_order_model.getValueAt(i, 0));
                i_order_model.setValueAt(String.format("RM %.2f", tempProc.getPriceAfterDiscount() * newQuantity), i, 3);
                return;
            }
        }
        
        String[] data = new String[6];
        data[0] = x.getProduct_id();
        data[1] = new Product(x.getProduct_id()).getProduct_name();
        data[2] = String.format("%d", x.getQuantity());
        data[3] = String.format("RM %.2f", x.getSubTotalAfterDiscount());
        data[4] = String.format("%.2f%%", x.getUnit_discount());
        this.i_order_model.addRow(data);   
    }
    
    private OrderDetail get_order_row(int i){
        OrderDetail x = new OrderDetail();
        x.setOrder_id(this.input_order_id.getText());
        x.setProduct_id((String)i_order_model.getValueAt(i, 0));
        x.setQuantity(Integer.valueOf((String)i_order_model.getValueAt(i, 2)));
        Product x2 = new Product((String)i_order_model.getValueAt(i, 0));
        x.setUnit_price(x2.getPriceAfterDiscount());
        x.setUnit_discount(x2.getPromotion_decimal());
        return x;
    }

    private void write_product_row(Product x) {

        String[] data = new String[5];
        data[0] = String.format("%d", i_product.getRowCount() + 1);
        data[1] = x.getProduct_id();
        data[2] = x.getProduct_name();
        data[3] = String.format("%.2f", x.getRetial_price());
        data[4] = String.format("%d", x.getQuantity());
        this.i_product_model.addRow(data);
    }
    
    private void set_input(){
        input_order_id.setText(Order.generate_id());
        input_order_date.setText("");
	Calendar cal2 = Calendar.getInstance();
        input_order_date.setSelectedDate(cal2);
        input_order_exp.setText("");
	Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 14);  
        input_order_exp.setSelectedDate(cal);
    }

    private double caluculateTotal(Order order) {
        double temp = 0.00;
        temp = order.getProduct_select().stream().map((x) -> 
            x.getUnit_price() * x.getQuantity()).reduce(
                temp, (accumulator, _item) -> accumulator + _item
            );
        return temp;
    }
}
