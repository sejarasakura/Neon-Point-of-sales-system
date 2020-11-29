package Transaction;


import Product.Product;
import Sql.WriteSql;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ITSUKA KOTORI
 */
public class OrderDetail extends WriteSql{

    private String order_id;
    private String product_id;
    private int quantity;
    private double unit_price;
    private double unit_discount;

    //[==========================================================================
    
    public OrderDetail(){
        
    }
    
    
    public OrderDetail(String order_id, String product_id, int quantity){
        this(order_id, product_id, quantity, new Product(product_id).getRetial_price(),  new Product(product_id).getPromotion_persentage());
    }
    

    public OrderDetail(String order_id, String product_id, int quantity, double unit_price, double unit_discount) {
        this.setOrder_id(order_id);
        this.setProduct_id(product_id);
        this.setQuantity(quantity);
        this.setUnit_price(unit_price);
        this.setUnit_discount(unit_discount);
    }
    
    public OrderDetail(String order_id, String product_id)
    { 
        try
        {            
            try (Connection conn = DriverManager.getConnection("jdbc:ucanaccess://resource/data/Point_of_sales.mdb;"); Statement s = conn.createStatement()) {
                s.execute("SELECT * FROM Order_record_detail WHERE product_id='"
                        + product_id + "' AND order_id='" + order_id + "';");
                try{
                    System.out.println("WriteSql > " + s.getWarnings().getMessage());
                }catch(NullPointerException ex){
                    
                }
                try (ResultSet rs = s.getResultSet()) {
                    rs.next();
                    this.split(rs);
                }
                s.close();
                conn.close();
            }
        }
        catch(SQLException ex)
        {
            System.out.println("Order detail : The id not found in the database - Constructor("
                    + order_id + ", " + product_id + ") error !! ");
        }
    }
    
    
    //[==========================================================================
    
    public final String getOrder_id() {
        return order_id;
    }

    public final String getProduct_id() {
        return product_id;
    }

    public final int getQuantity() {
        return quantity;
    }

    public final double getUnit_price() {
        return unit_price;
    }

    public final double getUnit_discount() {
            return unit_discount;
    }

    public final double getSubTotalAfterDiscount() {
        return ((quantity *(1 - unit_discount))*unit_price);
    }
    //===========================================================================
    
    public final boolean setOrder_id(String order_id) {
        if(Pattern.matches("[Oo]{1}[Rr]{1}[0-9]{8}+", order_id)){
            this.order_id = order_id;    
            return true;
        }else
            return false;
    }

    public final boolean setProduct_id(String product_id) {
        if(Pattern.matches("[Cc]{1}[Ww]{1}[0-9]{4}+", product_id)){
            this.product_id = product_id;
            return true;
        }
        else
            return false;
    }

    public final boolean setQuantity(int product_quantity) {
        if(product_quantity >= 1){
            this.quantity = product_quantity;
            return true;
        }
        return false;
    }

    public final boolean setUnit_price(double unit_price) {
        if(unit_price >= 1){
            this.unit_price = unit_price;
            return true;
        }
        return false;
    }
    public final boolean setUnit_discount(double unit_discount) {
        if(0.01 >= unit_discount && unit_discount <= 0.75){
            this.unit_discount = unit_discount;
            return true;
        }else if(1 >= unit_discount && unit_discount <= 75){
            this.unit_discount = (unit_discount/100);
            return true;
        }
        return false;
    }
    
    
    //===========================================================================
    
    
    public boolean giveDiscount(double from_0_to_1){
        this.setUnit_discount(from_0_to_1); 
        this.unit_price = unit_price*(1.0 - unit_discount);
        return this.modify_this();
    }
    
    public double getDiscountPrice(){
        return (this.unit_price - getSubTotalAfterDiscount());
    }
    
    //[==========================================================================

    @Override
    public final void split(ResultSet x) {
        try {
            this.setOrder_id(x.getString("order_id"));
            this.setProduct_id(x.getString("product_id"));
            this.setQuantity(x.getInt("product_quantity"));
            this.setUnit_price(x.getDouble("unit_final_price"));
            this.setUnit_discount((x.getDouble("unit_discount")));
        } catch (SQLException ex) {
               System.out.println("OrderDetail : result is unable to split from database !! ");
        }
    }

    @Override
    public boolean write_this() {
         return update_query(
            "INSERT INTO Order_record_detail (order_id, product_id, product_quantity, unit_final_price, unit_discount) VALUES ('" + 
            this.order_id +"', '" + this.product_id + "', " + this.quantity +
            ", " + this.unit_price + ", " + this.unit_discount*100 + ")") == 1;
    }

    @Override
    public boolean modify_this() {
        return update_query(
             "UPDATE Order_record_detail SET "+
            "product_quantity=" + this.quantity +
            ", unit_final_price=" + this.unit_price +
            ", unit_discount=" + this.unit_discount*100 +
            " WHERE product_id='" + this.product_id  + "' AND order_id='" + this.order_id + "';"
        ) == 1;
    }

    @Override
    public boolean delete_this() {
        return update_query("DELETE FROM Order_record_detail WHERE product_id='" + this.product_id  + "' AND order_id='" + this.order_id + "';")==1;
    }
    
    @Override
    public boolean isNotNull() {
        return (order_id != null && product_id != null && quantity > 0 && unit_price > 0 && unit_discount >= 0 && unit_discount <= 0.75);
    }

    @Override
    public String export_one_oracle(){
        return (
            "INSERT INTO Order_record_detail (order_id, product_id, product_quantity, unit_price, unit_discount) VALUES \n('" + this.order_id + "', '" + this.product_id + "', '" + this.quantity + "', " + this.unit_price + ", " + this.unit_discount + ");"
        );
    }
	
    public static boolean export_oracle(String sql_file) {
        OrderDetail temp = new OrderDetail();
        boolean final_result = true;
        try
        {   FileWriter writer = new FileWriter(sql_file, true); 
            BufferedWriter bfw = new BufferedWriter(writer);
            try (Connection conn = DriverManager.getConnection("jdbc:ucanaccess://resource/data/Point_of_sales.mdb;"); Statement s = conn.createStatement()) {
                final_result &= s.execute("SELECT * FROM Order_record_detail;");
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
            return final_result;
        }
        catch(Exception ex){
            Logger.getLogger(Payment.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public String toString() {
        return "OrderDetail{" + "order_id=" + order_id + ", product_id=" + product_id + ", quantity=" + quantity + ", unit_price=" + unit_price + ", unit_discount=" + unit_discount + '}';
    }
    
}
