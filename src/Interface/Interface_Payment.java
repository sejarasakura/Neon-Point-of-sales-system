package Interface;


import Main.Main;
import Transaction.Order;
import Transaction.Payment;
import Product.Product;
import User.Staff.Staff;
import SubInterface.SubInterface_Login_Staff;
import SubInterface.SubInterface_Product_List;
import Sql.WriteSql;


import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.table.*;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ITSUKA KOTORI
 */
public class Interface_Payment extends javax.swing.JFrame {

    final public static String MAX_LINE =   " =====================================================\n";
    final public static String MAX_LINE_2 = " -----------------------------------------------------\n";
    //                                                               12345678901234567890123456789012345678901234567890123
    final public static String HEADER_TITLE = "                                                      \n\n\n" + 
                                        "                       WELCOME TO                     \n" + 
                                        "                _____ _____ _____ _____               \n" + 
                                        "               |   | |  ___|     |   | |              \n" + 
                                        "               | | | |  ___|  |  | | | |              \n" +  
                                        "               |_|___|_____|_____|_|___|              \n" + 
                                        "                                                      \n\n" + 
                                        "           NEON - Small (M) Sdn Bhd (242659-T)        \n" + 
                                        "              Perseteria Wangsa NEON - Small          \n" + 
                                        "                                                      \n" + 
                                        "                                                      \n" + 
                          String.format("  Today Date :         %30s \n", Main.DATE_FORMAT.format(Main.getToday_date())) + 
                                        "  TEL : 0956-26-5310               FAX : 0956-39-5650 \n" +
                                        "  URL : http://www.neon-perseteria.co.jp              \n" +
                                        "                                                      \n" + 
                                        " |---------------------------------------------------|\n" +
                                        " |                      RECEIPT                      |\n" +
                                        " |---------------------------------------------------|\n" +
                                        "                                                      \n" + 
                                        "  * Join neon member to collect more point to change  \n" + 
                                        "    free gift at special counter                      \n" + 
                                        "                                                      \n" + 
                                        "  No.Product                      Qty      Total (RM) \n" +
                                        " =====================================================\n";
    
    final public static String RECEIPT_FOOTER = "                                                      \n\n" +
                                          "          For returns / cancellation, please          \n" +
                                          "    ensure that the original receipt and vouchers     \n" +
                                          "  (if any) are presented, Terms and condition apply   \n\n" +
                                          "         Pleases come again to NEON - SMALL !!        \n" +
                                          "                    Thank You ! ! ! !                 \n" +
                                          "                                                      \n" +
                                          " \n"; 
    final public static String FORMAT_STRING            = " %2d %-28s  %3d %14.2f  \n";
    final public static String FORMAT_STRING_EXTRA_NAME = "    %-28s                      \n";
    final public static String FORMAT_STRING_EXTRA_PROMOTION = "    Promotion %5.2f%-13s                      \n";
    final public static String FORMAT_STRING_TOTAL     = "  Total      :                              %10.2f \n";
    final public static String FORMAT_STRING_TOTAL_SST = "  TAX Charge :                              %10.2f \n";
    final private Object[] columns = {"ID", "Product Name", "Price", "Quantity", "Total Price","Pay"};
    private int receipt_index_count = 0;
    private JTable productTable;
    private DefaultTableModel model;
    private SubInterface_Product_List runProductList; 
    private Order order_fro_payment;
    private Payment payment;

    /**
     * Creates new form POSInterface
     */
    public Interface_Payment() {
        
        initComponents();
        initComponents_Interface();
        initComponents_ProductPanel();
        initComponents_SetClose();
        runProductList = new SubInterface_Product_List(); 
        System.out.println("Transaction module build completly success");
    }
    
    private void reset(){ 
        receipt_index_count = 0;
    }
    
    private void initComponents_SetClose(){
        this.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e){
                SubInterface_Login_Staff.validate_login_go(new Staff(SubInterface_Login_Staff.decode_id(getDataDutyStaff())));
                if(runProductList != null)
                    runProductList.dispose();
            }
        });
        
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
    
    
    private void write_table_row(Product product, int quantity, boolean select_x)
    {   
        boolean added = false;
        for(int i = 0; i < this.model.getRowCount(); i++){
            if((" " + product.getProduct_id()).equals((String)model.getValueAt(i, 0))){
                model.setValueAt(String.format("%d", Integer.valueOf((String)model.getValueAt(i, 3)) + 1), i, 3);
                model.setValueAt(
                        String.format("%.2f",((double)Integer.valueOf((String)model.getValueAt(i, 3)))*product.getPriceAfterDiscount()), i, 4);
                added = true;
            }
        }
        if (!added) {
            String[] rows = new String[6];
            rows[0] = " " + product.getProduct_id();
            rows[1] = " " + product.getProduct_name();
            rows[2] = String.format("%.2f", product.getRetial_price());
            rows[3] = String.format("%d", quantity);
            rows[4] = String.format("%.2f", quantity * product.getPriceAfterDiscount());
            rows[5] = "" + this.getCode_checked_or_not(select_x);
            model.addRow(rows);
        }
    }
    
    private String getCode_checked_or_not(boolean select_x){
        if(select_x)
            return '\u2714' + "";
        else
            return '\u2718' + "";
    }
    
    private boolean getBool_checked_or_not(String data){
        System.out.println(data.contains("\u2714"));
        return (data.contains("\u2714"));
    }
    
    private void initComponents_Interface() {
        Vector comboBoxitem = new Vector(), comboBoxitem2 = new Vector();
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
                s.execute("SELECT O.ORDER_ID FROM ORDER_RECORD O WHERE NOT EXISTS ( SELECT P.ORDER_ID FROM PAYMENT P WHERE P.ORDER_ID = O.ORDER_ID );");
                rs = s.getResultSet();
                    while(rs.next()){
                        comboBoxitem.add(rs.getString("order_id"));
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
        this.combo_order.setModel(comboBoxModel);
        combo_order.setEditable(false);
        DefaultComboBoxModel comboBoxModel1 = new DefaultComboBoxModel(comboBoxitem2);
        this.combo_customer.setModel(comboBoxModel1);
        combo_customer.setEditable(false);
        setTitle("Point of sales system");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setIconImage(Main.DEFUALT_ICON.getImage());
        this.neon_img_label.setIcon(new ImageIcon(getScaledImage(new ImageIcon("resource/neon.png").getImage(), 262, 80)));
        this.save_change_table.setEnabled(false);
        this.add_product_button_table.setEnabled(false);
        this.delete_product_button.setEnabled(false);
        this.quantity_input.setEditable(false);
        dateTimeLable.setText("Today Date : " + Main.DATE_FORMAT.format(Main.getToday_date()));
    }
    
    public static Image getScaledImage(Image scrImg, int w, int h){
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(scrImg, 0, 0, w, h, null);
        g2.dispose();
        return resizedImg;
    }
    
    private void refresh_table_detail(){
        if(runProductList.getSelected_id() != null)
        {
            this.write_table_row(new Product(runProductList.getSelected_id()), runProductList.getQuantity(), false);
        }
        runProductList.refresh();
    }
    
    private void initComponents_ProductPanel(){
        
        productTable = new JTable(){ 
            @Override 
            public boolean isCellEditable(int rowIndex, int colIndex){
                switch(colIndex){
                    case 3: 
                        return true;
                    default: 
                        return false;
                }
            }
        };
        Font font = new Font("", 0, 12);
        productTable.setFont(font);
        productTable.setRowHeight(26);
        this.product_list_panel.add(productTable);
        model = new DefaultTableModel(0,6);
        model.setColumnIdentifiers(columns);
        productTable.setModel(model);
        productTable.setBackground(Color.white);
        productTable.setForeground(Color.DARK_GRAY);
        product_list_panel.setViewportView(productTable);

        productTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        productTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        productTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        productTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        productTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        productTable.getColumnModel().getColumn(5).setPreferredWidth(42);
        
        productTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                int y = productTable.rowAtPoint(evt.getPoint());
                int x = productTable.columnAtPoint(evt.getPoint());
                System.out.println("Now we at table point [x,y] = [" + x + "," + y + "]");
                if(x == 5 && (x >= 0 && y >= 0))
                    productTableactive(evt, y);
                write_label_of_detial(new Product((String)model.getValueAt(y, 0).toString().substring(1, 7)));
                shift_add_notsave(false);
                quantity_input.setText((String)model.getValueAt(y, 3).toString());
            }
        });
        
        
        productTable.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                if(evt.getKeyCode() == KeyEvent.VK_ENTER){
                    int i = productTable.getSelectedRow();
                    quantity_input.setText((String)productTable.getValueAt(i, 3));
                    change_add_not_save_full_set();
                }
                else{
                    
                }
            }
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if(evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_UP){
                    int i = productTable.getSelectedRow();
                    quantity_input.setText((String)productTable.getValueAt(i, 3));
                    write_label_of_detial(new Product((String)model.getValueAt(i, 0).toString().substring(1, 7)));
                    shift_add_notsave(false);
                    quantity_input.setText((String)model.getValueAt(i, 3).toString());
                }
                else{
                    
                }
            }
        });

    }
    
    private void shift_add_notsave(boolean x){
        this.add_product_button_table.setEnabled(x);
        this.save_change_table.setEnabled(!x);
    }
    
    private void write_label_of_detial(Product temp){
        String writeIn = "<html><body><br/><br/>"; 
        writeIn += 
                String.format("Product ID &nbsp;&nbsp;: %s<br/><br/>", temp.getProduct_id()) + 
                String.format("Product name : %s<br/><br/>", temp.getProduct_name()) + 
                String.format("Catergory &nbsp;&nbsp; : %s<br/><br/>", temp.getType().toString());
        if(temp.getPromoting())
            writeIn += String.format("Promotion &nbsp;&nbsp; : %5.2f<br/><br/>", temp.getCheckPromotion());
        else
            writeIn += String.format("Promotion &nbsp;&nbsp; : <span style='text-decoration: line-through;'>%5.2f</span><br/><br/>", temp.getPromotion_persentage());
        writeIn += 
                String.format("Discount &nbsp;&nbsp;&nbsp; : %.2f<br/><br/>", temp.getRetial_price() - temp.getPriceAfterDiscount()) + 
                String.format("Retail Price : <b>%.2f</b><br/><br/>", temp.getRetial_price()) + 
                String.format("Stock Remain : %d<br/>", temp.getQuantity());
        if(temp.getQuantity() <= 0){
            writeIn += "<strong> ( The product is out of stock ) </strong><br/><br/>";   
        }
        writeIn += "</body></html>";
        detials_label.setText(writeIn);
    }

    private void productTableactive(MouseEvent e, int y){
        this.model.setValueAt(this.getCode_checked_or_not(!this.getBool_checked_or_not(this.model.getValueAt(y, 5).toString())), y, 5);
        System.out.println("Changed the checked value to oposite !!");
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
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        add_product_button_table = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        receiptArea = new javax.swing.JTextArea();
        dateTimeLable = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        totalButton = new javax.swing.JButton();
        receiptButton = new javax.swing.JButton();
        resetButton = new javax.swing.JButton();
        exitButton = new javax.swing.JButton();
        selection_plan = new javax.swing.JPanel();
        productManupulation_panel = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        delete_product_button = new javax.swing.JButton();
        add_product_button = new javax.swing.JButton();
        save_change_table = new javax.swing.JButton();
        detials_label = new javax.swing.JLabel();
        search_product_button = new javax.swing.JButton();
        search_input = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        quantity_input = new javax.swing.JTextField();
        prossecing = new javax.swing.JProgressBar();
        product_list_panel = new javax.swing.JScrollPane();
        select_all_check = new javax.swing.JCheckBox();
        neon_img_label = new javax.swing.JLabel();
        refresh_product_button = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        total_label = new javax.swing.JLabel();
        insert_order_button = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        combo_customer = new javax.swing.JComboBox<>();
        combo_order = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        add_product_button_table.setText("Add to table");
        add_product_button_table.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                add_product_button_tableActionPerformed(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Point of sales system");
        setMinimumSize(new java.awt.Dimension(1347, 754));

        receiptArea.setEditable(false);
        receiptArea.setColumns(20);
        receiptArea.setFont(new java.awt.Font("Courier New", 0, 11)); // NOI18N
        receiptArea.setRows(5);
        jScrollPane1.setViewportView(receiptArea);

        dateTimeLable.setText(" Today Date : 2016/11/16 12:08:43");

        totalButton.setText("Total");
        totalButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                totalButtonActionPerformed(evt);
            }
        });

        receiptButton.setText("Submit");
        receiptButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                receiptButtonActionPerformed(evt);
            }
        });

        resetButton.setText("Reset");
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });

        exitButton.setText("Back Menu");
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(totalButton, javax.swing.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(receiptButton, javax.swing.GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(resetButton, javax.swing.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(exitButton, javax.swing.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
                .addGap(3, 3, 3))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(totalButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(receiptButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(resetButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(exitButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(dateTimeLable, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1))
                        .addGap(5, 5, 5))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(dateTimeLable, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 628, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        delete_product_button.setText("Delete ");
        delete_product_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delete_product_buttonActionPerformed(evt);
            }
        });

        add_product_button.setText("Catalog item list");
        add_product_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                add_product_buttonActionPerformed(evt);
            }
        });

        save_change_table.setText("Save Changes");
        save_change_table.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                save_change_tableActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(delete_product_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(save_change_table, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(23, 23, 23)
                .addComponent(add_product_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addComponent(save_change_table)
                .addGap(7, 7, 7)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(add_product_button)
                    .addComponent(delete_product_button)))
        );

        detials_label.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        detials_label.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        search_product_button.setText("search");
        search_product_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                search_product_buttonActionPerformed(evt);
            }
        });

        search_input.setText("Product ID");
        search_input.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                search_inputActionPerformed(evt);
            }
        });
        search_input.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                search_inputKeyReleased(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel1.setText("Quantity :");

        quantity_input.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        quantity_input.setText("1");
        quantity_input.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quantity_inputActionPerformed(evt);
            }
        });
        quantity_input.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                quantity_inputKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(quantity_input)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(quantity_input, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout productManupulation_panelLayout = new javax.swing.GroupLayout(productManupulation_panel);
        productManupulation_panel.setLayout(productManupulation_panelLayout);
        productManupulation_panelLayout.setHorizontalGroup(
            productManupulation_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(productManupulation_panelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(productManupulation_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(prossecing, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(productManupulation_panelLayout.createSequentialGroup()
                        .addComponent(search_input, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(search_product_button))
                    .addGroup(productManupulation_panelLayout.createSequentialGroup()
                        .addComponent(detials_label, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        productManupulation_panelLayout.setVerticalGroup(
            productManupulation_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, productManupulation_panelLayout.createSequentialGroup()
                .addGroup(productManupulation_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(search_product_button)
                    .addComponent(search_input, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(detials_label, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(prossecing, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );

        select_all_check.setText("Select All");
        select_all_check.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                select_all_checkMousePressed(evt);
            }
        });
        select_all_check.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                select_all_checkActionPerformed(evt);
            }
        });

        refresh_product_button.setText("refresh");
        refresh_product_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refresh_product_buttonActionPerformed(evt);
            }
        });

        jLabel2.setText("Total :");

        total_label.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        total_label.setText("RM 0.00 ");

        insert_order_button.setText("Insert Order");
        insert_order_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insert_order_buttonActionPerformed(evt);
            }
        });

        jLabel3.setText("Member ID          :");

        combo_customer.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        combo_order.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel4.setText("Order ID            :");

        javax.swing.GroupLayout selection_planLayout = new javax.swing.GroupLayout(selection_plan);
        selection_plan.setLayout(selection_planLayout);
        selection_planLayout.setHorizontalGroup(
            selection_planLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(selection_planLayout.createSequentialGroup()
                .addGroup(selection_planLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(productManupulation_panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(selection_planLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(neon_img_label, javax.swing.GroupLayout.PREFERRED_SIZE, 277, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, 0)
                .addGroup(selection_planLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(selection_planLayout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(combo_customer, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(combo_order, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(insert_order_button)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(select_all_check)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, selection_planLayout.createSequentialGroup()
                        .addComponent(refresh_product_button)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(total_label, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(product_list_panel, javax.swing.GroupLayout.Alignment.TRAILING)))
        );
        selection_planLayout.setVerticalGroup(
            selection_planLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(selection_planLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(selection_planLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(selection_planLayout.createSequentialGroup()
                        .addGroup(selection_planLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(select_all_check, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(selection_planLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(combo_order, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(insert_order_button, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(combo_customer, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                        .addComponent(product_list_panel, javax.swing.GroupLayout.PREFERRED_SIZE, 637, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addGroup(selection_planLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(total_label)
                            .addComponent(refresh_product_button, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(selection_planLayout.createSequentialGroup()
                        .addComponent(neon_img_label, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(11, 11, 11)
                        .addComponent(productManupulation_panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(selection_plan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(selection_plan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void select_all_checkMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_select_all_checkMousePressed
        // TODO add your handling code here
        refresh_table_detail();
        if(!select_all_check.isSelected())
        {
            for(int i = 0;i < model.getRowCount(); i ++){
                model.setValueAt(this.getCode_checked_or_not(true), i, 5);
            }
        }
        else
        {
            for(int i = 0;i < model.getRowCount(); i ++){
                model.setValueAt(this.getCode_checked_or_not(false), i, 5);
            }
        }
    }//GEN-LAST:event_select_all_checkMousePressed

    private void select_all_checkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_select_all_checkActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_select_all_checkActionPerformed

    private void search_inputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_search_inputActionPerformed
        // TODO add your handling code here:
        search_product_buttonActionPerformed(evt);
    }//GEN-LAST:event_search_inputActionPerformed

    private void search_product_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_search_product_buttonActionPerformed
        // TODO add your handling code here:
        Product temp;
        this.refresh_table_detail();
        if(this.search_input.getText() != null) {
            temp = new Product(this.search_input.getText());
            this.write_label_of_detial(temp);
        }
        shift_add_notsave(true);
    }//GEN-LAST:event_search_product_buttonActionPerformed

    private void add_product_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_add_product_buttonActionPerformed
        // TODO add your handling code here:
        prossecing.setValue(3);
        refresh_table_detail();
        prossecing.setValue(50);
        runProductList.setVisible(true);
        prossecing.setValue(100);
        runProductList.setAutoRequestFocus(true);
        prossecing.setValue(0);
        this.runProductList.refresh_table();
    }//GEN-LAST:event_add_product_buttonActionPerformed

    private void refresh_product_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refresh_product_buttonActionPerformed
        // TODO add your handling code here:
        refresh_table_detail();
    }//GEN-LAST:event_refresh_product_buttonActionPerformed

    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitButtonActionPerformed
        // TODO add your handling code here:                
        SubInterface_Login_Staff.validate_login_go(new Staff(SubInterface_Login_Staff.decode_id(getDataDutyStaff())));
        this.runProductList.dispose();
        this.dispose();
    }//GEN-LAST:event_exitButtonActionPerformed

    private void search_inputKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_search_inputKeyReleased
        // TODO add your handling code here:
        if(evt.getKeyCode() == KeyEvent.VK_ENTER
                ||evt.getKeyCode() == KeyEvent.getExtendedKeyCodeForChar('0')
                ||evt.getKeyCode() == KeyEvent.getExtendedKeyCodeForChar('1')
                ||evt.getKeyCode() == KeyEvent.getExtendedKeyCodeForChar('2')
                ||evt.getKeyCode() == KeyEvent.getExtendedKeyCodeForChar('3')
                ||evt.getKeyCode() == KeyEvent.getExtendedKeyCodeForChar('4')
                ||evt.getKeyCode() == KeyEvent.getExtendedKeyCodeForChar('5')
                ||evt.getKeyCode() == KeyEvent.getExtendedKeyCodeForChar('6')
                ||evt.getKeyCode() == KeyEvent.getExtendedKeyCodeForChar('7')
                ||evt.getKeyCode() == KeyEvent.getExtendedKeyCodeForChar('9')
                ||evt.getKeyCode() == KeyEvent.getExtendedKeyCodeForChar('8'))
        {
            Product temp;
            this.refresh_table_detail();
            if(WriteSql.getUniqueInt("SELECT COUNT(product_id) FROM Product WHERE product_id='" + this.search_input.getText() + "';") == 1) {
                temp = new Product(this.search_input.getText());
                if(temp.getProduct_id() != null){
                    this.write_label_of_detial(temp);
                    this.quantity_input.setEditable(true);
                this.add_product_button_table.setEnabled(true);
                }
                else{
                    this.detials_label.setText(" No result found !! in record");
                    this.quantity_input.setEditable(false);
                this.add_product_button_table.setEnabled(false);
                }
            }
            else{
                this.detials_label.setText(" No result found !! in record");
                this.quantity_input.setEditable(false);
                this.add_product_button_table.setEnabled(false);
            }
            shift_add_notsave(true);
        }
        else{
                this.detials_label.setText(" No result found !! in record");
                this.quantity_input.setEditable(false);
                this.add_product_button_table.setEnabled(false);
        }
    }//GEN-LAST:event_search_inputKeyReleased

    private void add_product_button_tableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_add_product_button_tableActionPerformed
        // TODO add your handling code here:
        if(receiptButton.isEnabled() == false){
            resetButtonActionPerformed(null);
        }
        if(Pattern.matches("[Cc]{1}[Ww]{1}[0-9]{4}", this.search_input.getText())){
            this.write_table_row(new Product(this.search_input.getText()), Integer.valueOf(this.quantity_input.getText()), false);
        }else
            JOptionPane.showConfirmDialog(null, "The Product ID not in the correct format. Correct format is eg.CW0001, CW0020", "Invalid product ID", JOptionPane.DEFAULT_OPTION);
        this.search_input.setText("");
        this.quantity_input.setText("1");
        this.detials_label.setText("");
    }//GEN-LAST:event_add_product_button_tableActionPerformed

    private void quantity_inputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quantity_inputActionPerformed
        // TODO add your handling code here:
        productTable.setRowSelectionAllowed(false);
    }//GEN-LAST:event_quantity_inputActionPerformed

    private void save_change_tableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_save_change_tableActionPerformed
        // TODO add your handling code here:
        change_add_not_save_full_set();
    }//GEN-LAST:event_save_change_tableActionPerformed

    private void quantity_inputKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_quantity_inputKeyReleased
        // TODO add your handling code here
        if(evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            if(save_change_table.isEnabled())
                change_add_not_save_full_set();
            else
                add_product_button_tableActionPerformed(null);
        }
    }//GEN-LAST:event_quantity_inputKeyReleased

    private void delete_product_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delete_product_buttonActionPerformed
        // TODO add your handling code here:
        int i = productTable.getSelectedRow();
        if(i >= 0){
            model.removeRow(i);
        }
    }//GEN-LAST:event_delete_product_buttonActionPerformed

    private double caluculateTotal(){
        double total = 0;
        for(int i = 0; i < model.getRowCount(); i++){
            if(getBool_checked_or_not((String)model.getValueAt(i, 5)))
                total += Double.valueOf((String)model.getValueAt(i, 4));
        }
        this.total_label.setText(String.format("RM %.2f ", total));
        return total;
    }
    
    private void totalButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_totalButtonActionPerformed
        // TODO add your handling code here:
        refresh_table_detail();
        caluculateTotal();
    }//GEN-LAST:event_totalButtonActionPerformed

    private void receiptButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_receiptButtonActionPerformed
        // TODO add your handling code here:
        this.totalButton.setEnabled(false);
        this.receiptButton.setEnabled(false);
        this.add_product_button.setEnabled(false);
        totalButtonActionPerformed(null);
        prossecing.setValue(10);
        if(!"~ Select".equals((String)combo_order.getSelectedItem())){
            JOptionPane.showConfirmDialog(null, "You are not abel to insert the order when generate receipt at the same time", "Not support dialog", JOptionPane.DEFAULT_OPTION);
            return;
        }else{
            this.order_fro_payment = new Order(Order.generate_id(), Main.getToday_date(), Main.getToday_date(),"");
            System.out.println(order_fro_payment.toString());
        }
        int quantity;
        Product temp;
        prossecing.setValue(20);
        for(int i = 0; i < model.getRowCount(); i++){
            if(getBool_checked_or_not((String)model.getValueAt(i, 5))) {
                temp = new Product(this.model.getValueAt(i, 0).toString().substring(1, 7));
                quantity = Integer.valueOf(this.model.getValueAt(i, 3).toString());
                if (temp.puchase(quantity) == false) {
                    order_fro_payment.add_product(temp.getProduct_id(), 0);
                } else if(temp.getPromoting()) {
                    order_fro_payment.add_product(temp.getProduct_id(), quantity, temp.getPromotion_decimal(), temp.getPriceAfterDiscount());
                }
                else {
                    order_fro_payment.add_product(temp.getProduct_id(), quantity);
                }
            }
            prossecing.setValue(20 + i);
        }
        prossecing.setValue(80);
        if(!"~ Select".equals((String)combo_customer.getSelectedItem())){
            payment = new Payment(SubInterface_Login_Staff.decode_id(getDataDutyStaff()), Main.getToday_date(), 0.00, (String)combo_customer.getSelectedItem(), order_fro_payment.getOrder_num());
        }
        else{
            payment = new Payment(SubInterface_Login_Staff.decode_id(getDataDutyStaff()), Main.getToday_date(), 0.00, null, order_fro_payment.getOrder_num());
        }
        prossecing.setValue(90);
        payment.write_this();
        prossecing.setValue(95);
        order_fro_payment.write_this();
        prossecing.setValue(99);
        write_receipt(order_fro_payment,payment,true);
        prossecing.setValue(0);
    }//GEN-LAST:event_receiptButtonActionPerformed
    private void write_receipt(Order order_fro_payment, Payment payment, boolean need_promotion){
        Product temp;
        this.receiptArea.setText(HEADER_TITLE + "\n");
        for(int i = 0; i < model.getRowCount(); i++){
            temp = new Product(order_fro_payment.getProduct_id_indexof(i).getProduct_id());
            if(getBool_checked_or_not((String)model.getValueAt(i, 5))) {
                if(temp.getPromoting() && need_promotion) {
                    this.write_line_receipt(temp, order_fro_payment.getProduct_select().get(i).getQuantity(), temp.getPromotion_persentage());                    
                }
                else {
                    this.write_line_receipt(temp, order_fro_payment.getProduct_select().get(i).getQuantity());
                }
            }
        }
        payment.setSSTCharge(0.1);
        this.receiptArea.append("\n" + Interface_Payment.MAX_LINE_2 + "\n");
        this.receiptArea.append(String.format(Interface_Payment.FORMAT_STRING_TOTAL, caluculateTotal()));
        this.receiptArea.append(String.format(Interface_Payment.FORMAT_STRING_TOTAL_SST, caluculateTotal() * 0.1));
        this.receiptArea.append("\n" + MAX_LINE);
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
        prossecing.setValue(100);
    }
    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        // TODO add your handling code here:
        this.totalButton.setEnabled(true);
        this.receiptButton.setEnabled(true);
        this.add_product_button.setEnabled(true);
        while(this.model.getRowCount() > 0){
            this.model.removeRow(0);
        }
        quantity_input.setText("1");
        this.total_label.setText("RM 0.00 ");
        receiptArea.setText("");
        select_all_check.setSelected(false);
        search_input.setText("");
        detials_label.setText("");
        this.runProductList.refresh_table();
        reset();
    }//GEN-LAST:event_resetButtonActionPerformed

    private void insert_order_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insert_order_buttonActionPerformed
        // TODO add your handling code here:
        refresh_table_detail();
        int option = JOptionPane.showConfirmDialog(null, "Do you sure want to insert the order, when insert an order the all the product in the particular order will be approve and generate receipt directly !!", "Confrim payment", JOptionPane.YES_NO_CANCEL_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            if (!"~ Select".equals((String) this.combo_order.getSelectedItem())) {
                if (model.getRowCount() <= 0) {                    
                    Order order_x = new Order((String) combo_order.getSelectedItem());
                    this.payment = new Payment(order_x, SubInterface_Login_Staff.decode_id(getDataDutyStaff()), Main.getToday_date());
                    for (int i = 0; i < order_x.getProduct_select().size(); i++) {
                        this.write_table_row(new Product(order_x.getProduct_id_indexof(i).getProduct_id()), order_x.getProduct_id_indexof(i).getQuantity(), true);
                    }
                    if (WriteSql.getUniqueInt("SELECT COUNT(*) FROM CUSTOMER WHERE customer_id='" + order_x.getMember_num() + "';") >= 1) {
                        combo_customer.setSelectedItem((String) order_x.getMember_num());
                    } else {
                        JOptionPane.showConfirmDialog(null, "The customer id is not found for the particular order is not found, this is an invalid order", "Not found user", JOptionPane.DEFAULT_OPTION);
                    }
                    write_receipt(order_x, payment, false);
                    payment.write_this();
                } else {
                    combo_order.setSelectedIndex(0);
                    JOptionPane.showConfirmDialog(null, "Please done the current transaction frist before adding for order", "Current payment proccess still active", JOptionPane.DEFAULT_OPTION);
                }
            } else {
                JOptionPane.showConfirmDialog(null, "Non - order is selected, please select another before submit", "No order selected", JOptionPane.DEFAULT_OPTION);
            }
        }else if(option == JOptionPane.NO_OPTION){
            combo_customer.setSelectedIndex(0);
        }
    }//GEN-LAST:event_insert_order_buttonActionPerformed

    private void change_add_not_save_full_set(){
        this.shift_add_notsave(true);
        int i = productTable.getSelectedRow();
        model.setValueAt(Integer.valueOf(this.quantity_input.getText()).toString(), i, 3);
        double price = (double)( Integer.valueOf(this.quantity_input.getText()) * Double.valueOf((String)productTable.getValueAt(i, 2)));
        model.setValueAt(String.format("%.2f", price), i, 4);
        productTable.setRowSelectionAllowed(true);
    }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton add_product_button;
    private javax.swing.JButton add_product_button_table;
    private javax.swing.JComboBox<String> combo_customer;
    private javax.swing.JComboBox<String> combo_order;
    private javax.swing.JLabel dateTimeLable;
    private javax.swing.JButton delete_product_button;
    private javax.swing.JLabel detials_label;
    private javax.swing.JButton exitButton;
    private javax.swing.JButton insert_order_button;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel neon_img_label;
    private javax.swing.JPanel productManupulation_panel;
    private javax.swing.JScrollPane product_list_panel;
    private javax.swing.JProgressBar prossecing;
    private javax.swing.JTextField quantity_input;
    private javax.swing.JTextArea receiptArea;
    private javax.swing.JButton receiptButton;
    private javax.swing.JButton refresh_product_button;
    private javax.swing.JButton resetButton;
    private javax.swing.JButton save_change_table;
    private javax.swing.JTextField search_input;
    private javax.swing.JButton search_product_button;
    private javax.swing.JCheckBox select_all_check;
    private javax.swing.JPanel selection_plan;
    private javax.swing.JButton totalButton;
    private javax.swing.JLabel total_label;
    // End of variables declaration//GEN-END:variables
}
