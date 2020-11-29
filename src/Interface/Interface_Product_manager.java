package Interface;


import Product.Catergory;
import Main.Main;
import Transaction.Payment;
import Product.Product;
import Sql.WriteSql;


import com.sun.glass.events.KeyEvent;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ITSUKA KOTORI
 */
public class Interface_Product_manager extends javax.swing.JFrame {

    //============================= Propertices ====================================
    
    private JTable productTable;
    private final Object[] columns = {"ID", "Product Name", "Stock", "Category", "Retial price", "Purchase price", "Promotion", "Add date"};
    private  String[] rows = new String[8];
    private boolean input_validate;
    private DefaultTableModel model = new DefaultTableModel();

    /**
     * Creates new form Add_Product_Form
     */
    //============================= Constructor ====================================

    public Interface_Product_manager() {
        initComponents();
        initComponents_Interface();
        initComponents_Table();
        if(WriteSql.get_row_count("Product") > 0)
            initComponents_data();
        initComponents_SetClose();
        total_product.setText("Total Customer Found : " + this.productTable.getRowCount());
    }

    //============================= initComponents ====================================
    
    private void initComponents_SetClose(){
        this.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e){
                new Interface_MenuManager().setVisible(true);
            }
        });
    }
    
    private void initComponents_Interface() {
        setTitle("Product editing manager");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setIconImage(Main.DEFUALT_ICON.getImage());
        productCatergory_input.setModel(new DefaultComboBoxModel<>(new String[] {"Fresh food & meat", "Fresh Vegetable", "Other Foods", "Baby product", "Children & Frozen", "Drinks & ice-cream", "Health care", "Beauty care", "Household product", "Other product"}));
        Date date = new Date();
        today_date.setText(Main.DATE_FORMAT.format(Main.getToday_date()));
        this.productExpTextfield.setDateFormat(WriteSql.DATE_FORMAT);
        close_input();
    }

    private void initComponents_Table() {
        productTable = new JTable(){ @Override public boolean isCellEditable(int rowIndex, int colIndex){ return false; /* disable edit */}};
        this.tablePanel.add(productTable);
        model.setColumnIdentifiers(columns);
        productTable.setModel(model);
        productTable.setBackground(Color.white);
        productTable.setForeground(Color.DARK_GRAY);
        tablePanel.setViewportView(productTable);

        Font font = new Font("", 0, 12);
        productTable.setFont(font);
        productTable.setRowHeight(20);
        productTable.getColumnModel().getColumn(0).setPreferredWidth(10);
        productTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        productTable.getColumnModel().getColumn(2).setPreferredWidth(50);
        productTable.getColumnModel().getColumn(3).setPreferredWidth(70);
        productTable.getColumnModel().getColumn(4).setPreferredWidth(60);
        productTable.getColumnModel().getColumn(5).setPreferredWidth(60);
        productTable.getColumnModel().getColumn(6).setPreferredWidth(60);
        productTable.getColumnModel().getColumn(7).setPreferredWidth(60);
        
        productTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                productTableactive(evt);
            }
        });
        
        productTable.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                if(evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN)
                    productTableactive(null);
            }
        });
    }
    
    private void initComponents_data(){
        Product temp = new Product();
        try{            
            try (Connection conn = DriverManager.getConnection("jdbc:ucanaccess://resource/data/Point_of_sales.mdb;"); Statement s = conn.createStatement()) {
                s.execute("SELECT * FROM Product;");
                try (ResultSet result = s.getResultSet()) {
                    while(result.next()){
                        temp.split(result);
                        this.write_table_row(temp);
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

    //============================= Method ======================================
    
    private void setLabel_readonly(Product product) {
        if(product != null){
            productCatergory_label1.setText("Product Category: " + product.getType().toString());
            productIdLabel1.setText("Product ID: " + product.getProduct_id());
            productNameLabel1.setText("Product  Name: " + product.getProduct_name());
            productPPrice_lable1.setText("Purchase price: " + String.format("RM %.2f", product.getPuchase_price()));
            productQuantitiesLabel1.setText("Product Quantity: " + product.getQuantity() + "");
            productRPrice_lable1.setText(String.format("Retail price: " + "RM %.2f", product.getRetial_price()));
            if (product.getPromoting() == true) {
                product_promotion_lable1.setText(String.format("Promotion: " + "%.2f%%", product.getPromotion_persentage()));
            } else {
                product_promotion_lable1.setText("<html><body>Promotion: <span style='text-decoration: line-through;'>RM " + product.getPromotion_persentage() + "</span></body></html>");
            }
            added_date_label.setText("<html><body><span style='text-align:right;'>Added by " + Main.DATE_FORMAT.format(product.getAdd_date()) + "</span></body></html>");
        }else{
            productCatergory_label1.setText("Product  Category: Select ~ a item");
            productIdLabel1.setText("Product ID: eg. XX0000");
            productNameLabel1.setText("Product name: eg. Lim Sai Keat");
            productPPrice_lable1.setText("Purchase price: eg. 0.00");
            productQuantitiesLabel1.setText("Product Quantity: eg. 0");
            productRPrice_lable1.setText("Retail price: eg. 0.00");
            product_promotion_lable1.setText("Promotion: eg. 0.00% max 75%");
            added_date_label.setText("");
        }
                
    }
    
    private void close_input(){
        this.productCatergory_input.setEditable(false);
        this.productNameTextfield.setEditable(false);
        this.productPPrice_textfield.setEditable(false);
        this.productQuantitiesTextfield.setEditable(false);
        this.productRPrice_textfield.setEditable(false);
        this.product_promotion_textfield.setEditable(false);
        this.productExpTextfield.setEnabled(false);
    }
    
    
    private void open_input(){
        
        this.productIdTextfield.setEditable(false);
        this.productCatergory_input.setEditable(true);
        this.productNameTextfield.setEditable(true);
        this.productPPrice_textfield.setEditable(true);
        this.productQuantitiesTextfield.setEditable(true);
        this.productRPrice_textfield.setEditable(true);
        this.product_promotion_textfield.setEditable(true);
        this.productExpTextfield.setEnabled(true);
    }

    private void resetLabel_readonly() {
        productCatergory_label1.setText("Product Category: Select ~ a item");
        productIdLabel1.setText("Product ID: eg. XX0000");
        productNameLabel1.setText("Product name: eg. Lim Sai Keat");
        productPPrice_lable1.setText("Purchase price: eg. 0.00");
        productQuantitiesLabel1.setText("Product Quantity: eg. 0");
        productRPrice_lable1.setText("Retail price: eg. 0.00");
        product_promotion_lable1.setText("Promotion: eg. 0.00% max 75%");
        added_date_label.setText("");
    }

    private void set_input(Product product) {
        this.productCatergory_input.setSelectedItem(product.getType().toString());
        this.productIdTextfield.setText(product.getProduct_id());
        this.productNameTextfield.setText(product.getProduct_name());
        this.productPPrice_textfield.setText(String.format("%.2f", product.getPuchase_price()));
        this.productQuantitiesTextfield.setText(product.getQuantity() + "");
        this.productRPrice_textfield.setText(String.format("%.2f", product.getRetial_price()));
        this.product_promotion_textfield.setText(product.getPromotion_persentage() + "");
        this.promoting_checkbox.setSelected(product.getPromoting());
        this.productExpTextfield.setText(WriteSql.DATE_FORMAT.format(product.getExp_date()));
        for(int i = 0; i < productTable.getRowCount(); i++){
            if(model.getValueAt(i, 0).equals(product.getProduct_id())){
                productTable.setSelectionMode(i);
                System.out.println("The " + i + " Row is selected with id " + product.getProduct_id());
            }
        }
    }
    
    // change to pass string
    private void set_input() {
        this.productIdTextfield.setText("");
        this.productNameTextfield.setText("");
        this.productQuantitiesTextfield.setText("0");
        this.productCatergory_input.setSelectedItem("Other");
        this.productRPrice_textfield.setText("0.00");
        this.productPPrice_textfield.setText("0.00");
        this.product_promotion_textfield.setText("0.00%");
        this.productExpTextfield.setText("");
        this.promoting_checkbox.setSelected(false);
    }

    private Product get_input() {
        boolean[] validation = new boolean[8];
        Product product = new Product();
        validation[0] = product.setType((String) this.productCatergory_input.getSelectedItem());
        if (!validation[0]) {
            productCatergory_label1.setText("The category is not accept");
            productCatergory_input.setSelectedItem(Catergory.Other.toString());
        }
        validation[1] = product.setProduct_id(this.productIdTextfield.getText());
        if (!validation[1]) {
            productIdLabel1.setText("ID number not valid");
            productIdTextfield.setText("");
        }
        validation[2] = product.setProduct_name(this.productNameTextfield.getText());
        if (!validation[2]) {
            productNameLabel1.setText("The name is wrong format");
            productNameTextfield.setText("");
        }
        validation[3] = product.setQuantity(Integer.parseInt(this.productQuantitiesTextfield.getText()));
        if (!validation[3]) {
            productQuantitiesTextfield.setText("0");
            productQuantitiesLabel1.setText("The quantity is not eccept");
        }
        validation[4] = product.setPuchase_price(Double.valueOf(this.productPPrice_textfield.getText()));
        if (!validation[4]) {
            productPPrice_textfield.setText("0.00");
            productPPrice_lable1.setText("The price is not eccepted");
        }
        validation[5] = product.setRetial_price(Double.valueOf(this.productRPrice_textfield.getText()));
        if (!validation[5]) {
            productRPrice_textfield.setText("0.00");
            productPPrice_lable1.setText("The price is not eccepted");
        }
        validation[6] = product.setPromotion((Double.valueOf(this.product_promotion_textfield.getText().replace('%', '0'))));
        if (!validation[5]) {
            product_promotion_textfield.setText("The promotion is out of range");
            product_promotion_lable1.setText("0.00%");
        }
        validation[7] = product.setPromoting(this.promoting_checkbox.isSelected());
        if (!validation[5]) {
            promoting_checkbox.setText("Promoting value error");
            this.promoting_checkbox.setSelected(false);
        }

        input_validate = validation[0] & validation[1] & validation[2] & validation[3] & validation[4] & validation[5] & validation[6] & validation[7];
        System.out.println("" + validation[0] + validation[1] + validation[2] + validation[3] + validation[4] + validation[5] + validation[6] + validation[7]);
        product.setAdd_Date(Main.getToday_date());
        product.setExp_Date(this.productExpTextfield.getSelectedDate().getTime());
        return product;
    }
    
    private void all_button(boolean setStatus){
        this.addProduct_Button.setEnabled(setStatus);
        this.deleteProduct_Button.setEnabled(setStatus);
        this.modifyProduct_Button.setEnabled(setStatus);
        this.Commit_button.setEnabled(setStatus);
        this.save_change_button.setEnabled(setStatus);
        this.refreshButton.setEnabled(setStatus);
        this.search_id_button.setEnabled(setStatus);
    }

   /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        productIdLabel = new javax.swing.JLabel();
        productIdTextfield = new javax.swing.JTextField();
        productNameLabel = new javax.swing.JLabel();
        productNameTextfield = new javax.swing.JTextField();
        productQuantitiesLabel = new javax.swing.JLabel();
        productQuantitiesTextfield = new javax.swing.JTextField();
        productCatergory_input = new javax.swing.JComboBox<>();
        productCatergory_label = new javax.swing.JLabel();
        productRPrice_lable = new javax.swing.JLabel();
        productRPrice_textfield = new javax.swing.JTextField();
        productPPrice_lable = new javax.swing.JLabel();
        productPPrice_textfield = new javax.swing.JTextField();
        product_promotion_lable = new javax.swing.JLabel();
        product_promotion_textfield = new javax.swing.JTextField();
        promoting_checkbox = new javax.swing.JCheckBox();
        tablePanel = new javax.swing.JScrollPane();
        addProduct_Button = new javax.swing.JButton();
        modifyProduct_Button = new javax.swing.JButton();
        deleteProduct_Button = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        productIdLabel1 = new javax.swing.JLabel();
        product_promotion_lable1 = new javax.swing.JLabel();
        productNameLabel1 = new javax.swing.JLabel();
        productQuantitiesLabel1 = new javax.swing.JLabel();
        productCatergory_label1 = new javax.swing.JLabel();
        productRPrice_lable1 = new javax.swing.JLabel();
        productPPrice_lable1 = new javax.swing.JLabel();
        added_date_label = new javax.swing.JLabel();
        today_date = new javax.swing.JLabel();
        refreshButton = new javax.swing.JButton();
        Commit_button = new javax.swing.JButton();
        search_id_button = new javax.swing.JButton();
        today_date1 = new javax.swing.JLabel();
        save_change_button = new javax.swing.JButton();
        exit_button = new javax.swing.JButton();
        productNameLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        productExpTextfield = new datechooser.beans.DateChooserCombo();
        total_product = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        productIdLabel.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        productIdLabel.setText("Product ID :");

        productIdTextfield.setEditable(false);
        productIdTextfield.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        productIdTextfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                productIdTextfieldActionPerformed(evt);
            }
        });
        productIdTextfield.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                productIdTextfieldKeyReleased(evt);
            }
        });

        productNameLabel.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        productNameLabel.setText("Product Name :");

        productNameTextfield.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        productNameTextfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                productNameTextfieldActionPerformed(evt);
            }
        });

        productQuantitiesLabel.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        productQuantitiesLabel.setText("Product Quantities :");

        productQuantitiesTextfield.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        productQuantitiesTextfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                productQuantitiesTextfieldActionPerformed(evt);
            }
        });

        productCatergory_input.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        productCatergory_input.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        productCatergory_input.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                productCatergory_inputItemStateChanged(evt);
            }
        });
        productCatergory_input.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                productCatergory_inputActionPerformed(evt);
            }
        });

        productCatergory_label.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        productCatergory_label.setText("Product Catergory :");

        productRPrice_lable.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        productRPrice_lable.setText("Retail Price :");

        productRPrice_textfield.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        productRPrice_textfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                productRPrice_textfieldActionPerformed(evt);
            }
        });

        productPPrice_lable.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        productPPrice_lable.setText("Purchase Price :");

        productPPrice_textfield.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        productPPrice_textfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                productPPrice_textfieldActionPerformed(evt);
            }
        });

        product_promotion_lable.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        product_promotion_lable.setText("Promotion :");

        product_promotion_textfield.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        product_promotion_textfield.setToolTipText("xx.xx%");
        product_promotion_textfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                product_promotion_textfieldActionPerformed(evt);
            }
        });

        promoting_checkbox.setText("promotion");
        promoting_checkbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                promoting_checkboxActionPerformed(evt);
            }
        });

        addProduct_Button.setText("Add new");
        addProduct_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addProduct_ButtonActionPerformed(evt);
            }
        });

        modifyProduct_Button.setText("Modify Product");
        modifyProduct_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modifyProduct_ButtonActionPerformed(evt);
            }
        });

        deleteProduct_Button.setText("Delete Product");
        deleteProduct_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteProduct_ButtonActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        productIdLabel1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        productIdLabel1.setText("xxx");

        product_promotion_lable1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        product_promotion_lable1.setText("xxx");

        productNameLabel1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        productNameLabel1.setText("xxx");

        productQuantitiesLabel1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        productQuantitiesLabel1.setText("xxx");

        productCatergory_label1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        productCatergory_label1.setText("xxx");

        productRPrice_lable1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        productRPrice_lable1.setText("xxx");

        productPPrice_lable1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        productPPrice_lable1.setText("xxx");

        added_date_label.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        added_date_label.setText("<html><body><span style='text-align:right;'>Added by xx-xx-xxxx</span></body></html>");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(productPPrice_lable1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 377, Short.MAX_VALUE)
                    .addComponent(product_promotion_lable1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(productRPrice_lable1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(productCatergory_label1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(productQuantitiesLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(productNameLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(productIdLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(added_date_label)
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(productIdLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(productNameLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(productQuantitiesLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(productCatergory_label1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(productRPrice_lable1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(product_promotion_lable1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(productPPrice_lable1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13)
                .addComponent(added_date_label, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
                .addContainerGap())
        );

        today_date.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        today_date.setText("08-12-2019");

        refreshButton.setText("Refresh table");
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });

        Commit_button.setText("Commit");
        Commit_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Commit_buttonActionPerformed(evt);
            }
        });

        search_id_button.setText("Search ID");
        search_id_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                search_id_buttonActionPerformed(evt);
            }
        });

        today_date1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        today_date1.setText("Today Date: ");

        save_change_button.setText("Save changes");
        save_change_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                save_change_buttonActionPerformed(evt);
            }
        });

        exit_button.setText("Back To Menu");
        exit_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exit_buttonActionPerformed(evt);
            }
        });

        productNameLabel2.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        productNameLabel2.setText("Exp Date :");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 60, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 262, Short.MAX_VALUE)
        );

        productExpTextfield.setFormat(2);

        total_product.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        total_product.setText("Total Product Found : ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(product_promotion_lable, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(product_promotion_textfield, javax.swing.GroupLayout.PREFERRED_SIZE, 386, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(productRPrice_lable, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(productPPrice_lable, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(productPPrice_textfield, javax.swing.GroupLayout.PREFERRED_SIZE, 386, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(productRPrice_textfield, javax.swing.GroupLayout.PREFERRED_SIZE, 386, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(productNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(productQuantitiesLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(productCatergory_label, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(productCatergory_input, javax.swing.GroupLayout.PREFERRED_SIZE, 386, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(productNameTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, 386, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(productQuantitiesTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, 386, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(productIdLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(productNameLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(productIdTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, 386, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(productExpTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, 386, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(promoting_checkbox)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(search_id_button)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(2, 2, 2)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(today_date1, javax.swing.GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 6, Short.MAX_VALUE)
                                .addComponent(today_date, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(exit_button, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(deleteProduct_Button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(refreshButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(modifyProduct_Button, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(save_change_button, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(addProduct_Button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Commit_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(total_product, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tablePanel)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(productIdLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(productIdTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(search_id_button, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(productNameLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
                                    .addComponent(productExpTextfield, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(productNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(productNameTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(productQuantitiesTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(productQuantitiesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(productCatergory_input, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(productCatergory_label, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(productRPrice_lable, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(productRPrice_textfield, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(productPPrice_textfield, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(productPPrice_lable, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(product_promotion_textfield, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(product_promotion_lable, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(promoting_checkbox, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(today_date1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(today_date)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(addProduct_Button, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(Commit_button, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(modifyProduct_Button, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(save_change_button, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(deleteProduct_Button, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(refreshButton, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(exit_button, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(total_product, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(tablePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
                .addGap(15, 15, 15))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void productIdTextfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productIdTextfieldActionPerformed

    }//GEN-LAST:event_productIdTextfieldActionPerformed

    private void productNameTextfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productNameTextfieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_productNameTextfieldActionPerformed

    private void productQuantitiesTextfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productQuantitiesTextfieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_productQuantitiesTextfieldActionPerformed

    private void productCatergory_inputItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_productCatergory_inputItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_productCatergory_inputItemStateChanged

    private void productCatergory_inputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productCatergory_inputActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_productCatergory_inputActionPerformed

    private void productRPrice_textfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productRPrice_textfieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_productRPrice_textfieldActionPerformed

    private void productPPrice_textfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productPPrice_textfieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_productPPrice_textfieldActionPerformed

    private void product_promotion_textfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_product_promotion_textfieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_product_promotion_textfieldActionPerformed

    private void productTableactive(java.awt.event.MouseEvent evt)
    {
        int i = productTable.getSelectedRow();
        Product procduct = new Product((String)model.getValueAt(i, 0).toString().substring(1, 7));
        System.out.print(procduct.toString());
        this.setLabel_readonly(procduct);
    }
    
    private void promoting_checkboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_promoting_checkboxActionPerformed
        if (promoting_checkbox.isSelected()) {
            product_promotion_textfield.setEditable(true);
        } else {
            product_promotion_textfield.setEditable(false);
        }
    }//GEN-LAST:event_promoting_checkboxActionPerformed

    private void addProduct_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addProduct_ButtonActionPerformed
        
        this.resetLabel_readonly();
        this.set_input(); 
        this.open_input();
        this.productIdTextfield.setText(Product.generate_Product_id());
        this.all_button(false);
        this.Commit_button.setEnabled(true);
        this.change_refresh_buttonTo_cancle(true);
    }//GEN-LAST:event_addProduct_ButtonActionPerformed

    private void change_refresh_buttonTo_cancle(boolean status_X){
        if(status_X)
        {
            this.refreshButton.setEnabled(true);
            this.refreshButton.setText("Cancle");
        }
        else
        {
            this.refreshButton.setText("Refresh table");
        }
    }
    
    private void write_table_row(Product product)
    {            
        rows[0] = " " + product.getProduct_id();
        rows[1] = " " + product.getProduct_name();
        rows[2] = String.format(" %d", product.getQuantity());
        rows[3] = " " + product.getType().toString();
        rows[4] = String.format(" RM %8.2f", product.getRetial_price());
        rows[5] = String.format(" RM %8.2f", product.getPuchase_price());
        rows[6] = String.format(" %8.2f %%", product.getPromotion_persentage());
        rows[7] = " " + product.getAdd_dateS();
        model.addRow(rows);
    }
    
    private void deleteProduct_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteProduct_ButtonActionPerformed
        int i = productTable.getSelectedRow();
        System.out.println("The selected row is " + i);
        if(i >= 0){
            String id = (String) model.getValueAt(i, 0);
            if (JOptionPane.showConfirmDialog(null, "Do you sure want tpo delete the following product id is " + id, "Confrim Delete", 0) == 0) {
                model.removeRow(i);
                new Product(id.substring(1, 7)).delete_this();
                System.out.println("Deleted fully sucessful sucessful " + id + " is deleted");
            }
        }else{
            System.out.println("Non object selected or selected object less that 0");
        }
        this.total_product.setText("Total Product Found : " + this.productTable.getRowCount());
    }//GEN-LAST:event_deleteProduct_ButtonActionPerformed

    private void modifyProduct_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modifyProduct_ButtonActionPerformed
        // TODO add your handling code here:
        open_input();
        this.productIdTextfield.setEnabled(false);
        int i = productTable.getSelectedRow();
        Product procduct = new Product(((String)model.getValueAt(i, 0)).substring(1,7));
        set_input(procduct);
        this.setLabel_readonly(procduct);
        this.all_button(false);
        this.save_change_button.setEnabled(true);
    }//GEN-LAST:event_modifyProduct_ButtonActionPerformed

    private void Commit_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Commit_buttonActionPerformed
        // TODO add your handling code here:
        this.close_input();
        Product proc = this.get_input();
        if (JOptionPane.showConfirmDialog(null, "Do you sure want to ad the following product(" + proc.getProduct_name() + ") as " + proc.getProduct_id(), "Confrim Add", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            
            if (this.input_validate == true) {
                proc.write_this();
                write_table_row(proc);
                System.out.println("Non-error found in input");
            } else {
                Product.warning_MessageBox("Please enter the correct data");
                System.out.println("The validation is in correct please check back");
            }
            this.all_button(true);
            this.change_refresh_buttonTo_cancle(false);
            this.set_input();
        }
        this.total_product.setText("Total Product Found : " + this.productTable.getRowCount());
    }//GEN-LAST:event_Commit_buttonActionPerformed

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
        // TODO add your handling code here:
        this.set_input();
        this.resetLabel_readonly();
        this.close_input();
        if(this.refreshButton.getText().equals("Cancle")){
            this.all_button(true);
        this.change_refresh_buttonTo_cancle(false);
        }
        else{
            while(this.model.getRowCount() > 0){
                this.model.removeRow(0);
            }
            this.initComponents_data();
            //need to add refreash 
        }
        this.total_product.setText("Total Product Found : " + this.productTable.getRowCount());
    }//GEN-LAST:event_refreshButtonActionPerformed

    private void search_id_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_search_id_buttonActionPerformed
        // TODO add your handling code here:
        if(productIdTextfield.isEditable()){
            this.search_id();
        }
        else{
            set_input();
            close_input();
            productIdTextfield.setEditable(true);
            productIdTextfield.setToolTipText("Keyin id eg.CW001");
            this.search_id_button.setText("Find");
        }
    }//GEN-LAST:event_search_id_buttonActionPerformed

    private void save_change_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_save_change_buttonActionPerformed
        // TODO add your handling code here:
        int i = productTable.getSelectedRow();
        Product data = get_input();
        if(this.input_validate == true)
        {
            if (JOptionPane.showConfirmDialog(null, "Do you sure to modify the product of " + data.getProduct_id() + " : " + data.getProduct_name() , "Confirm Modify", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                close_input();
                data.modify_this();
                System.out.println("The selected item is modified as " + data.toString());
                this.all_button(true);
                rows[0] = " " + data.getProduct_id();
                rows[1] = " " + data.getProduct_name();
                rows[2] = String.format(" %d", data.getQuantity());
                rows[3] = " " + data.getType().toString();
                rows[4] = String.format(" RM %8.2f", data.getRetial_price());
                rows[5] = String.format(" RM %8.2f", data.getPuchase_price());
                rows[6] = String.format(" %8.2f %%", data.getPromotion_persentage());
                rows[7] = " " + data.getAdd_dateS();
                for (int j = 0; j < 8; j++) {
                    model.setValueAt(rows[j], i, j);
                }
                all_button(true);
                
            }
        }
        else{
            open_input();
            Product.warning_MessageBox("Please enter the correct data need to modify");
            System.out.println("The validation is in correct please check back");
            all_button(false);
            this.save_change_button.setEnabled(true);
        }
    }//GEN-LAST:event_save_change_buttonActionPerformed

    private void search_id(){            
        Product current_proc = new Product(this.productIdTextfield.getText());
        if(this.productIdTextfield.getText().contains("CW")){
            this.setLabel_readonly(current_proc);
            this.set_input(current_proc);
            this.all_button(false);
            for(int i = 0; i < model.getRowCount(); i++){
                if(this.model.getValueAt(i, 0).toString().substring(1, 7).equals(current_proc.getProduct_id())){
                    this.productTable.setRowSelectionInterval(i, i);
                }
            }
            this.save_change_button.setEnabled(true);
            this.deleteProduct_Button.setEnabled(true);
            this.modifyProduct_Button.setEnabled(true);
            productIdTextfield.setEditable(false);
            this.search_id_button.setText("Search ID");
        }
    }
    
    private void productIdTextfieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_productIdTextfieldKeyReleased
        // TODO add your handling code here:        // TODO add your handling code here:
        if(evt.getKeyCode() == KeyEvent.VK_ENTER || this.productIdTextfield.getText().contains("CW"))
        {
            this.search_id();
        }
    }//GEN-LAST:event_productIdTextfieldKeyReleased

    private void exit_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exit_buttonActionPerformed
        // TODO add your handling code here
        new Interface_MenuManager().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_exit_buttonActionPerformed
    


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Commit_button;
    private javax.swing.JButton addProduct_Button;
    private javax.swing.JLabel added_date_label;
    private javax.swing.JButton deleteProduct_Button;
    private javax.swing.JButton exit_button;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton modifyProduct_Button;
    private javax.swing.JComboBox<String> productCatergory_input;
    private javax.swing.JLabel productCatergory_label;
    private javax.swing.JLabel productCatergory_label1;
    private datechooser.beans.DateChooserCombo productExpTextfield;
    private javax.swing.JLabel productIdLabel;
    private javax.swing.JLabel productIdLabel1;
    private javax.swing.JTextField productIdTextfield;
    private javax.swing.JLabel productNameLabel;
    private javax.swing.JLabel productNameLabel1;
    private javax.swing.JLabel productNameLabel2;
    private javax.swing.JTextField productNameTextfield;
    private javax.swing.JLabel productPPrice_lable;
    private javax.swing.JLabel productPPrice_lable1;
    private javax.swing.JTextField productPPrice_textfield;
    private javax.swing.JLabel productQuantitiesLabel;
    private javax.swing.JLabel productQuantitiesLabel1;
    private javax.swing.JTextField productQuantitiesTextfield;
    private javax.swing.JLabel productRPrice_lable;
    private javax.swing.JLabel productRPrice_lable1;
    private javax.swing.JTextField productRPrice_textfield;
    private javax.swing.JLabel product_promotion_lable;
    private javax.swing.JLabel product_promotion_lable1;
    private javax.swing.JTextField product_promotion_textfield;
    private javax.swing.JCheckBox promoting_checkbox;
    private javax.swing.JButton refreshButton;
    private javax.swing.JButton save_change_button;
    private javax.swing.JButton search_id_button;
    private javax.swing.JScrollPane tablePanel;
    private javax.swing.JLabel today_date;
    private javax.swing.JLabel today_date1;
    private javax.swing.JLabel total_product;
    // End of variables declaration//GEN-END:variables
}
