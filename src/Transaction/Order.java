package Transaction;


import Product.Product;
import Sql.WriteSql;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
public class Order extends WriteSql{
    // transection number 10 number
    private String order_id;
    private String member_id; // ! unique
    private Date order_expiry_date;
    private Date order_date;
    private List<OrderDetail>  product_select = new ArrayList<>();// Use format CW0001#12 after # is number of product

    public Order(String order_num, Date order_expiry_date, Date order_date, String member_id) {
        this.setOrder_num(order_num);
        this.setOrder_expiry_date(order_expiry_date);
        this.setOrder_date(order_date);
        this.member_id = member_id;
    }
    

    public Order(String order_num, Date order_expiry_date, Date order_date) {
        this.setOrder_num(order_num);
        this.setOrder_expiry_date(order_expiry_date);
        this.setOrder_date(order_date);
    }
    
    public Order(Date order_expiry_date, Date order_date, String member_id) {
        this.setOrder_num(generate_id());
        this.setOrder_expiry_date(order_expiry_date);
        this.setOrder_date(order_date);
        this.member_id = member_id;
    }
    
    public Order(String order_id) {
        try
        {   try (Connection conn = DriverManager.getConnection("jdbc:ucanaccess://resource/data/Point_of_sales.mdb;"); Statement s = conn.createStatement()) {
                s.execute("SELECT * FROM Order_record WHERE order_id='" + order_id + "';");
            try (ResultSet rs = s.getResultSet()) {
                rs.next();
                this.split(rs);
                rs.close();
            }
                s.close();
                conn.close();
            }
        }
        catch(SQLException ex){
            System.out.println("Staff : The id not found in the database - Constructor("
                    + order_id + ") error !! ");
        }
        try
        {   try (Connection conn = DriverManager.getConnection("jdbc:ucanaccess://resource/data/Point_of_sales.mdb;"); Statement s = conn.createStatement()) {
                s.execute("SELECT * FROM Order_record_detail WHERE order_id='" + order_id + "';");
                ResultSet rs = s.getResultSet();
                while(rs.next()){
                    OrderDetail pd = new OrderDetail();
                    pd.split(rs);
                    this.add_product(pd);
                }
                rs.close();
                s.close();
                conn.close();
            }
        }
        catch(SQLException ex){
            System.out.println("Order : The detail not found in the database - Constructor("
                    + order_id + ") error !! ");
        }
    }
    
    public Order() {
        
    }
    
    //============================================================================
    
    public static String generate_id(){
        return String.format("OR%08d", Integer.valueOf(WriteSql.getUniqueString("SELECT TOP 1 order_id FROM Order_record ORDER BY order_id DESC;").substring(2)) + 1);
    }

    //============================================================================
    
    public final String getMember_num(){
        return member_id;
    }
    
    public final String getOrder_num() {
        return order_id;
    }

    public final Date getOrder_expiry_date() {
        return order_expiry_date;
    }

    public final Date getOrder_date() {
        return order_date;
    }

    public final List<OrderDetail> getProduct_select() {
        return product_select;
    }

    public final int getProduct_quantity_indexof(int i) {
        return product_select.get(i).getQuantity();
    }

    public final OrderDetail getProduct_id_indexof(int i) {
        return product_select.get(i);
    }

    
    //=============================================================

    public final boolean setOrder_num(String order_num) {
        if(Pattern.matches("[Oo]{1}[Rr]{1}[0-9]{8}+", order_num)){
            this.order_id = order_num;
            return true;
        }else
            return false;
    }

    public final boolean setOrder_date(String order_date) {
        try {
            this.order_expiry_date = WriteSql.DATE_FORMAT.parse(order_date);
            return true;
        } catch (ParseException ex) {
            Logger.getLogger(Order.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public final boolean setOrder_date(Date order_date) {
        this.order_date = order_date;
        return true;
    }

    public final boolean setOrder_expiry_date(Date order_expiry_date) {
        this.order_expiry_date = order_expiry_date;
        return true;
    }

    public final boolean setOrder_expiry_date(String order_expiry_date) {
        try {
            this.order_expiry_date = WriteSql.DATE_FORMAT.parse(order_expiry_date);
            return true;
        } catch (ParseException ex) {
            Logger.getLogger(Order.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public final boolean setProduct_select(String pro_id, int quantity, int i) {
        if(Pattern.matches("[Cc]{1}[Ww]{1}[0-9]{4}+", pro_id) && quantity >= 0 && i >= 0){
            this.product_select.set(i, new OrderDetail(this.order_id, pro_id, quantity));
            return true;
        }else
            return false;
    }
    
    public final boolean setMember_id(String member_id) {
        if(WriteSql.getUniqueInt("SELECT COUNT(customer_id) FROM Customer WHERE customer_id='" + member_id + "';") == 1){
            this.member_id = member_id;
            return true;
        }else{
            member_id = "";
            return false;
        }
    }
    //===================================================================================
    
    public final boolean add_product(OrderDetail unit_order) {
        this.product_select.add(unit_order);
        return true;
    }
    
    public final boolean add_product(String product_id, int quantity) {
        if(WriteSql.getUniqueInt("SELECT COUNT(product_id) FROM Product WHERE product_id='" + product_id + "';") == 1 && quantity >= 0){
            this.product_select.add(new OrderDetail(this.order_id, product_id, quantity));
            return true;
        }else
            return false;
    }
    
    public final boolean add_product(String product_id, int quantity, double unit_discount, double unit_price) {
        if(WriteSql.getUniqueInt("SELECT COUNT(product_id) FROM Product WHERE product_id='" + product_id + "';") == 1  && quantity >= 0){
        this.product_select.add(new OrderDetail(this.order_id, product_id, quantity, unit_price, unit_discount));
            return true;
        }else
            return false;
    }
    
    public final boolean add_product(Product product, int quantity) {
        this.product_select.add(new OrderDetail(this.order_id, product.getProduct_id(), quantity));
        return true;
    }
    
    
    public final boolean add_product(Product product, int quantity, double unit_discount, double unit_price) {
        this.product_select.add(new OrderDetail(this.order_id, product.getProduct_id(), quantity, unit_price, unit_discount));
        return true;
    }
    
    
    @Override
    public boolean write_this() {
        boolean result ;
        if(!(member_id == null || member_id.equals("") || member_id.equals("null")))
            result = update_query("INSERT INTO Order_record (order_id, customer_id, order_date, order_exp_date) VALUES ('" + 
                this.order_id +"', '" + this.member_id + "', #" + WriteSql.DATE_FORMAT_MS.format(this.order_date) +
                "#, #"+WriteSql.DATE_FORMAT_MS.format(this.order_expiry_date)+"#)") == 1;
        else
            result = update_query("INSERT INTO Order_record (order_id, order_date, order_exp_date) VALUES ('" + 
                this.order_id +"', #" + WriteSql.DATE_FORMAT_MS.format(this.order_date) +
                "#, #"+WriteSql.DATE_FORMAT_MS.format(this.order_expiry_date)+"#)") == 1;
        for (int i = 0; i < this.product_select.size(); i++) {
            result &= this.product_select.get(i).write_this();
        }
        return result;
    }

    @Override
    public boolean modify_this() {
        for (int i = 0; i < this.product_select.size(); i++) {
            if(WriteSql.getUniqueInt("SELECT COUNT(product_id) FROM Order_record_detail WHERE product_id='" + product_select.get(i).getProduct_id() + "' AND order_id='" + this.order_id + "';") >= 1){
                product_select.get(i).modify_this();
            }else{
                product_select.get(i).write_this();
            }
        }
        if(!(member_id == null || member_id.equals("") || member_id.equals("null")))
            return update_query("UPDATE Order_record SET customer_id='"+ this.member_id + 
                    "', order_date=#" + WriteSql.DATE_FORMAT_MS.format(this.order_date) +
                    "#, order_exp_date=#" + WriteSql.DATE_FORMAT_MS.format(this.order_expiry_date) +
                    "# WHERE order_id='" + this.order_id + "';") == 1;
        else
            return update_query("UPDATE Order_record SET order_date=#" +
                    WriteSql.DATE_FORMAT_MS.format(this.order_date) +
                    "#, order_exp_date=#" + WriteSql.DATE_FORMAT_MS.format(this.order_expiry_date) +
                    "# WHERE order_id='" + this.order_id + "';") == 1;
    }

    @Override
    public boolean delete_this() {
        for (int i = 0; i < this.product_select.size(); i++) {
            product_select.get(i).delete_this();
        }
        return update_query("DELETE FROM Order_record WHERE '" + this.order_id + "';") == 1;
    }
    
    @Override
    public final void split(ResultSet x){
        try {
            this.setOrder_num(x.getString("order_id"));
            this.setOrder_expiry_date(x.getDate("order_exp_date"));
            this.setOrder_date(x.getDate("order_date"));
            this.setMember_id(x.getString("customer_id"));
        } catch (SQLException ex) {
            System.out.println("Order : result is unable to split from database !! ");
        }
    }
    
    @Override
    public boolean isNotNull() {
        return order_id != null && product_select != null && product_select.toArray().length > 0;
    }

    @Override
    public String export_one_oracle(){
        if(!(member_id != null || !"".equals(member_id) || !"null".equals(member_id)))
            return (
                "INSERT INTO Order_record (order_id, order_date, order_exp_date) VALUES \n('" + 
                this.order_id + "', "  +  WriteSql.getDate_orcl(this.order_date) + ", " +  
                WriteSql.getDate_orcl(this.order_expiry_date) + ");"
            );
        else
            return (
                "INSERT INTO Order_record (order_id, customer_id, order_date, order_exp_date) VALUES \n('" + 
                this.order_id + "', '" + this.member_id + "', "+ WriteSql.getDate_orcl(this.order_date) + 
                ", " +  WriteSql.getDate_orcl(this.order_expiry_date) + ");"
            );
    }
	
    public static boolean export_oracle(String sql_file) {
        Order temp = new Order();
        boolean final_result = true;
        try{                              
            try (FileWriter writer = new FileWriter(sql_file, true); BufferedWriter bfw = new BufferedWriter(writer)) {
                try (Connection conn = DriverManager.getConnection("jdbc:ucanaccess://resource/data/Point_of_sales.mdb;"); Statement s = conn.createStatement()) {
                    final_result &= s.execute("SELECT * FROM Order_record;");
                    try (ResultSet result = s.getResultSet()) {
                        while(result.next()){
                            temp.split(result);
                            bfw.write(temp.export_one_oracle() + "\n");
                        }
                    }
                    s.close();
                    conn.close();
                }
                bfw.flush();
            }
            return final_result;
        }
        catch(IOException | SQLException ex){
            Logger.getLogger(Payment.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public String toString() {
        return "Order{" + "order_id=" + order_id + ", member_id=" + member_id + ", order_expiry_date=" + order_expiry_date + ", order_date=" + order_date + ", product_select=" + product_select + '}';
    }
    
}
