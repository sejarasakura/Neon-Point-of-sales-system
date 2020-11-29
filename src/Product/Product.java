package Product;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ITSUKA KOTORI
 */
 
import Transaction.Payment;
import Sql.WriteSql;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.*;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
 
 
public class Product extends WriteSql{
	
    // decalation
    private String product_id;
    private String product_name;
    private int quantity;
    private Catergory type;
    private double retial_price;
    private double puchase_price;
    private double promotion; // percentage
    private boolean promoting;
    private Date add_date;
    private Date exp_date;
    
//========================================================================================//
    
    public Product() {
       // no change  any thing 
    }
    
    public Product(Product another) {
       // no change  any thing 
       this(another.product_id, another.product_name, another.quantity, another.type, another.retial_price, another.puchase_price, another.promotion, another.promoting, WriteSql.DATE_FORMAT.format(another.add_date));
    }
    
    public Product(String product_id, String product_name, int quantity, Catergory type, double retial_price, 
        double puchase_price, double promotion, boolean promoting, String add_dateS){
        this.setProduct_id(product_id);
        this.setProduct_name(product_name);
        this.setQuantity(quantity);
        this.setType(type);
        this.setRetial_price(retial_price);
        this.setPuchase_price(puchase_price);
        this.setPromotion(promotion);
        this.setPromoting(promoting);
        this.setAdd_Date(add_dateS);
    }
    
    public Product(String product_id, String product_name, String quantity, String type, String retial_price, 
        String puchase_price, String promotion, String promoting, String add_dateS){
        this.setProduct_id(product_id);
        this.setProduct_name(product_name);
        this.setQuantity(Integer.valueOf(quantity));
        this.setType(type);
        this.setRetial_price(Double.valueOf(retial_price));
        this.setPuchase_price(Double.valueOf(puchase_price));
        this.setPromotion(Double.valueOf(promotion));
        this.setPromoting(Boolean.valueOf(promoting));
        this.setAdd_Date(add_dateS);
    }
        
    public Product(String product_id){
        try
        {            
            try (Connection conn = DriverManager.getConnection("jdbc:ucanaccess://resource/data/Point_of_sales.mdb;"); Statement s = conn.createStatement()) {
                s.execute("SELECT * FROM Product WHERE product_id='" + product_id + "';");
                try (ResultSet rs = s.getResultSet()) {
                    rs.next();
                    this.split(rs);
                }
                s.close();
                conn.close();
            }
        }
        catch(SQLException ex){
            System.out.println("Product : The id not found in the database - Constructor("
                    + product_id + ") error !! ");
        }
    }
    
    
//=================================== GETTER =====================================
    
    public String getProduct_id(){
        return this.product_id;
    }
    
    public String getProduct_name(){
        return this.product_name;
    }
    
    public int getQuantity(){
        return this.quantity;
    }
    
    public Catergory getType(){
        return this.type;
    }
    
    public double getRetial_price(){
        return this.retial_price;
    }
    
    public double getPuchase_price(){
        return this.puchase_price;
    }
    
    public boolean getPromoting(){
        return this.promoting;
    }
    
    public double getPromotion_decimal(){
        if(this.promotion <= 0.75) 
            return this.promotion; 
        else 
            return this.promotion/100;
    }
    
    public double getPromotion_persentage(){
        if(this.promotion <= 0.75) 
            return this.promotion * 100; 
        else 
            return this.promotion;
    }
    
    public Date getAdd_date(){
        return this.add_date;
    }
    
    public String getAdd_dateS(){
        return WriteSql.DATE_FORMAT.format(add_date);
    }
    
    public double getPriceAfterDiscount(){
        if(this.promoting == true)
            return this.retial_price - (this.getPromotion_persentage() * this.retial_price/100);
        else
            return this.retial_price;
    }
    
    public double getCheckPromotion(){
        if(this.promoting == true) 
            return getPromotion_persentage(); 
        else 
            return 0;
    }
    
    public Date getExp_date(){
        return this.exp_date;
    }

//===================================== SETTER =================================
    public final boolean setProduct_id(String product_id){
        if(Pattern.matches("[Cc]{1}[Ww]{1}[0-9]{4}+", product_id)){
            this.product_id = product_id;
            return true;
        }
        return false;
    }
        
    public final boolean setProduct_name(String product_name){
        this.product_name = product_name;
        return true;
    }
    
    public final boolean setQuantity(int quantity){
        if(quantity >= 0){
            this.quantity = quantity;
            return true;
        }
        return false;
    }
    
    public final boolean setType(String type){
        this.type = Catergory.myValueOf(type);
        return true;
    }

    public final boolean setType(Catergory type){
        this.type = type;
        return true;
    }
    
    public final boolean setRetial_price(double retial_price){
        if(retial_price >= 0){
            this.retial_price = retial_price;
            return true;
        }
        return false;
    }
    
    public final boolean setPuchase_price(double puchase_price){
        if(puchase_price >= 0){
            this.puchase_price = puchase_price;
            return true;
        }
        return false;
    }
    
    public final boolean setPrice(double retial_price, double puchase_price){
        boolean result = true;
        result &= this.setRetial_price(retial_price);
        result &= this.setPuchase_price(puchase_price);
        return result;
    }
    
    public final boolean setExp_Date(String exp_date){
        try {
            this.exp_date = WriteSql.DATE_FORMAT.parse(exp_date);
        } catch (ParseException ex) {
            Logger.getLogger(Product.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
    
    public final boolean setExp_Date(Date exp_date){
        this.exp_date = exp_date;
        return true;
    }
    
    public final boolean setAdd_Date(String add_date){
        try {
            this.add_date = WriteSql.DATE_FORMAT.parse(add_date);
            return true;
        } catch (ParseException ex) {
            Logger.getLogger(Product.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public final boolean setAdd_Date(Date add_date){
        this.add_date = add_date;
        return true;
    }
    
    public final boolean setPromotion(double promotion){
        if(promotion >= 0.999 & promotion <= 75){
            this.promotion = (promotion/100);
            return true;
        }
        else if(promotion >= 0.01 && promotion <= 0.75) {
            this.promotion = promotion;
            return true;
        }
        return false;
    }
    
    public final boolean setPromoting(boolean promoting){
        this.promoting = promoting;
        return true;
    }
    
        
    public static String generate_Product_id(){
        return String.format("CW%04d" ,WriteSql.getUniqueInt("SELECT TOP 1 SUBSTRING(PRODUCT_ID, 3, 6) AS INT FROM PRODUCT ORDER BY PRODUCT_ID DESC;") + 1);
    }
    public static void warning_MessageBox (String output)
    {
        JOptionPane.showMessageDialog(null, output, "Warning !!", JOptionPane.INFORMATION_MESSAGE);
    }
    public boolean notNull()
    {
        return this.product_id != null && this.product_name != null && this.quantity >= 0 && puchase_price >= 0.5;
    }
    
    public boolean puchase(int quantity) {
        if(this.quantity >= quantity){
            this.quantity -= quantity;
            this.modify_this();
            return true;
        }
        else{
            return false;
        }
    }
    

//=================================== METHOD ===================================//
    
    @Override
    public boolean write_this() 
    {
        return WriteSql.update_query(
            "INSERT INTO Product (product_id, product_name, unit_price, puchase_price, exp_date,"+
            " stock_remain, max_discount, discounting, catergory_id, add_date) VALUES ('"+
            this.product_id + "', '" + this.product_name + "', "+
            this.retial_price + ", " + this.puchase_price + ", #"+
            WriteSql.DATE_FORMAT_MS.format(this.exp_date) + "#, "+
            this.quantity + ", " + this.promotion + ", "+
            this.promoting + ", '" + this.type.toString_id() + "', #"+
            WriteSql.DATE_FORMAT_MS.format(this.add_date) + "#);"
        ) == 1;
    }
    
    @Override
    public boolean delete_this()
    {        
        return WriteSql.update_query("DELETE FROM Product WHERE product_id='" + this.product_id + "';" ) == 1;
    }
    
    @Override
    public boolean modify_this(){
        if(this.exp_date != null)
            return WriteSql.update_query(
                "UPDATE Product SET product_name='" + this.product_name
                + "', unit_price=" + this.retial_price
                + ", puchase_price=" + this.puchase_price
                + ", exp_date=#" + WriteSql.DATE_FORMAT_MS.format(this.exp_date)
                + "#, stock_remain=" + this.quantity
                + ", max_discount=" + this.promotion
                + ", discounting=" + this.promoting 
                + ", catergory_id='" +  this.type.toString_id()
                + "', add_date=#" + WriteSql.DATE_FORMAT_MS.format(this.add_date) + "# WHERE "
                + "product_id='" + this.product_id + "';"
            ) == 1;
        else
            return WriteSql.update_query(
                "UPDATE Product SET product_name='" + this.product_name
                + "', unit_price=" + this.retial_price
                + ", puchase_price=" + this.puchase_price
                + ", stock_remain=" + this.quantity
                + ", max_discount=" + this.promotion
                + ", discounting=" + this.promoting 
                + ", catergory_id='" +  this.type.toString_id()
                + "', add_date=#" + WriteSql.DATE_FORMAT_MS.format(this.add_date) + "# WHERE "
                + "product_id='" + this.product_id + "';"
            ) == 1;
            
    }
    
    @Override
    public boolean isNotNull() {
        return (product_id != null && product_name != null && retial_price != 0 && puchase_price != 0);
    }
    
    @Override
    public final void split(ResultSet row){
        
        try {
            //csvReader.close();
            this.setProduct_id(row.getString("product_id"));
            this.setProduct_name(row.getString("product_name"));
            this.setQuantity(row.getInt("stock_remain"));
            this.setType(row.getString("catergory_id"));
            this.setRetial_price(row.getDouble("unit_price"));
            this.setPuchase_price(row.getDouble("puchase_price"));
            this.setPromotion(row.getDouble("max_discount"));
            this.setPromoting(row.getBoolean("discounting"));
            this.setAdd_Date(row.getDate("add_date"));
            this.setExp_Date(row.getDate("exp_date"));
        } catch (SQLException ex) {
            System.out.println("Product : result is unable to split from database !! ");
        }
    }

    public void split(String row){
        
        String[] data = row.split(",");
        //csvReader.close();
        this.product_id  = data[0];
        this.product_name = data[1];
        this.quantity = Integer.valueOf(data[2]);
        this.type = Catergory.myValueOf(data[3]);
        this.retial_price = Double.valueOf(data[4]);
        this.puchase_price = Double.valueOf(data[5]);
        this.promotion = Double.valueOf(data[6]);
        this.promoting = Boolean.valueOf(data[7]);
        this.setAdd_Date(data[8]);
        this.setExp_Date(data[9]);
    }
    
    //========================== Override Method ==================================
    
    @Override
    public String toString(){
        if(exp_date != null)
        return (this.product_id + "," + this.product_name + "," + this.quantity +
                "," + this.type.toString() + "," + this.retial_price + "," + this.puchase_price +
                "," + this.promotion + "," + this.promoting + "," + WriteSql.DATE_FORMAT.format(this.add_date) + 
                "," + WriteSql.DATE_FORMAT.format(this.exp_date) + "\n") ;  
        else
        return (this.product_id + "," + this.product_name + "," + this.quantity +
                "," + this.type.toString() + "," + this.retial_price + "," + this.puchase_price +
                "," + this.promotion + "," + this.promoting + "," + WriteSql.DATE_FORMAT.format(this.add_date) + 
                "," + "\n") ;       
    }

    
    @Override
    public String export_one_oracle() {
        if(exp_date != null)
        return (
            "INSERT INTO Product (product_id, product_name, unit_price, purchase_price, stock_remain, exp_date, maximum_discount, discounting,"
            + " category_id, add_date) VALUES \n('" + this.product_id +"', '"+ 
            this.product_name+"', "+ this.retial_price +" ,"+ this.puchase_price +", "+ this.quantity +
            ", "+ WriteSql.getDate_orcl(this.exp_date) +", "+ this.promotion +", "+ getOrcl_boolean() + ",'" +
             this.type.toString_id() +"', "+ WriteSql.getDate_orcl(this.add_date)+");" 			 
        );   
        else
        return (
            "INSERT INTO Product (product_id, product_name, unit_price, purchase_price, stock_remain, maximum_discount, discounting,"
            + " category_id, add_date) VALUES \n('" + this.product_id +"', '"+ this.product_name+"', "+ 
            this.retial_price +" ,"+ this.puchase_price +", "+ this.quantity + ", " + this.promotion +", "+ 
            getOrcl_boolean() + ",'" + this.type.toString_id() +"', "+ WriteSql.getDate_orcl(this.add_date)+");" 			 
        );   
    }
    
    private String getOrcl_boolean(){
        if(promoting)
            return "'1'";
        else
            return "'0'";
    }

    public static boolean export_oracle(String sql_file) {
        Product temp = new Product();
        try
        {   FileWriter writer = new FileWriter(sql_file, true); 
            BufferedWriter bfw = new BufferedWriter(writer);
            try (Connection conn = DriverManager.getConnection("jdbc:ucanaccess://resource/data/Point_of_sales.mdb;"); Statement s = conn.createStatement()) {
                s.execute("SELECT * FROM Product;");
                ResultSet result = s.getResultSet();
                while(result.next()){
                    temp.split(result);
                    bfw.write(temp.export_one_oracle() + "\n");
                }
                result.close();
                s.close();
                conn.close();
            }
            bfw.flush();
            bfw.close();
            writer.close();
            return true;
        }
        catch(IOException | SQLException ex){
            Logger.getLogger(Payment.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }    
}

