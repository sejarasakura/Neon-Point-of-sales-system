package Transaction;


import Sql.WriteSql;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
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
public class Payment extends WriteSql{
    
    // transection number 10 number
    private String payment_id;
    private String staff_id;
    private double sst_charge;
    private String customer_id;
    private String order_id;
    private Date transaction_date;


    
    //==========================================================================

    
    public Payment(String payment_id, String staff_id, double sst_charge, String customer_id, String order_id, Date transaction_date) {
        this.payment_id = payment_id;
        this.staff_id = staff_id;
        this.sst_charge = sst_charge;
        this.customer_id = customer_id;
        this.order_id = order_id;
        this.transaction_date = transaction_date;
    }
    
    public Payment(String staff_id, Date transaction_date, double sst_charge, String customer_id, String order_id){
        this(generateID(), staff_id, sst_charge, customer_id, order_id, transaction_date);
    }
    
    public Payment(String transaction_id){        
        try
        {   try (Connection conn = DriverManager.getConnection("jdbc:ucanaccess://resource/data/Point_of_sales.mdb;"); Statement s = conn.createStatement()) {
                s.execute("SELECT * FROM Payment WHERE payment_id='" + transaction_id + "';");
                ResultSet rs = s.getResultSet();
                rs.next();
                this.split(rs);
                rs.close();
                conn.close();
                s.close();
            }
        }
        catch(SQLException ex){
            System.out.println("Payment : The id not found in the database - Constructor("
                    + transaction_id + ") error !! ");
        }
    }
    
    public Payment(Order order, String staff_id, Date transaction_date){        
        this(staff_id, transaction_date, 0,order.getMember_num(), order.getOrder_num());
    }
    
    public Payment() {

    }
    
    //==========================================================================

    
    public double getSst_charge() {
        return sst_charge;
    }

    public String getCustomer_id() {
        if(customer_id == null || "null".equals(customer_id) || " ".equals(customer_id))
            return "";
        else
            return customer_id;
    }

    public String getOrder_id() {
        return order_id;
    }
    public String getPayment_id() {
        return payment_id;
    }

    public String getStaff_id() {
        return staff_id;
    }

    public Date getTransaction_date() {
        return transaction_date;
    }

    
    //==========================================================================

    
    
    public boolean setOrder_id(String order_id) {
        if(Pattern.matches("[OR]{2}[0-9]{8}+", order_id)){
            this.order_id = order_id;
            return true;
        }else
            return false;
    }
    
    public boolean setPayment_id(String payment_id) {
        if(Pattern.matches("[0-9]{10}+", payment_id)){
            this.payment_id = payment_id;
            return true;
        }else
            return false;
    }
    
    
    public boolean setCustomer_id(String cust_id) {
        if(cust_id == null || cust_id == "null" || cust_id == "")
            return true;
        if(Pattern.matches("[A-Z]{2}+[0-9]{4}+", cust_id)){
            this.customer_id = cust_id;
            return true;
        }else
            return false;
    }
    
    public boolean setStaff_id(String staff_id) {
        if(Pattern.matches("[A-Za-z]{2}+[0-9]{4}+", staff_id)){
            this.staff_id = staff_id;
            return true;
        }else
            return false;
    }
    
    public boolean setSSTCharge(double sst_charge){
        if(sst_charge >= 0 && sst_charge <= 0.1){
            this.sst_charge = sst_charge;
                    return true;
        }
        else
            return false;
    }

    public boolean setTransaction_date(Date transaction_date) {
        this.transaction_date = transaction_date;
        return true;
    }
    
    
    public static String generateID()
    {   
        return String.format("%010d" ,Integer.valueOf(WriteSql.getUniqueString("SELECT TOP 1 Payment_id AS INT FROM Payment ORDER BY Payment_id DESC")) + 1);
    }

    @Override
    public boolean write_this(){
        boolean result = true;
        if(this.isNotNull()){
            result = WriteSql.update_query(
                "INSERT INTO Payment (payment_id, sst_charge, customer_id, " + 
                "staff_id, order_id, payment_date) VALUES ('" +
                this.payment_id + "', " + this.sst_charge + ", '" + 
                this.customer_id + "', '" + this.staff_id + "', '" + 
                this.order_id + "', #" + WriteSql.DATE_FORMAT_MS.format(this.transaction_date) + "#);"
            ) == 1;
            
            return result;
            
        } else {
            System.out.println("The payment record is null value consider not abel write in data base");
            return false;
        }
    }
    
    @Override
    public boolean modify_this()
    {        
        // not yet done 
        if(this.isNotNull()){
            if(customer_id != null)
                return WriteSql.update_query(
                    "UPDATE Payment SET payment_id='"+ this.payment_id +
                        "', staff_id='"+ this.staff_id + 
                        "', order_id='" + this.order_id + 
                        "', customer_id='" + this.customer_id +
                        "', payment_date=#" + WriteSql.DATE_FORMAT_MS.format(this.transaction_date)+
                        "#, sst_charge=" + this.sst_charge + 
                        " WHERE payment_id='" + this.payment_id + "';"
                ) == 1;
            else
                return WriteSql.update_query(
                    "UPDATE Payment SET payment_id='"+ this.payment_id +
                        "', staff_id='"+ this.staff_id + 
                        "', order_id='" + this.order_id + 
                        "', payment_date=#" + WriteSql.DATE_FORMAT_MS.format(this.transaction_date)+
                        "#, sst_charge=" + this.sst_charge + 
                        " WHERE payment_id='" + this.payment_id + "';"
                ) == 1;
        }else{
            System.out.println("The payment record is null value consider not abel modify in database");
            return false;
        }
    }
    
    @Override
    public boolean  delete_this(){
        // not yet done       
        return WriteSql.update_query("DELETE FROM Payment WHERE payment_id='" + 
            this.payment_id + "';" ) == 1;
    }
    
    @Override
    public final void split(ResultSet rs){
        
        try {
            this.payment_id = rs.getString("payment_id");
            this.transaction_date = rs.getDate("payment_date");
            this.sst_charge = rs.getDouble("sst_charge");
            this.customer_id = rs.getString("customer_id");
            this.staff_id = rs.getString("staff_id");
            this.order_id = rs.getString("order_id");    
        } catch (SQLException ex) {
            System.out.println("Payments : result is unable to split from database !! ");
        }
    }
    
    @Override
    public boolean isNotNull() {
        return (staff_id != null && payment_id != null && order_id != null && sst_charge >= 0 && sst_charge <= 1);
    }
    
    @Override
    public String export_one_oracle(){
        if("".equals(customer_id) || "null".equals(customer_id) || " ".equals(customer_id) || customer_id == null)
            return (
                "INSERT INTO Payment (payment_id, payment_date, sst_charge, staff_id, order_id) VALUES \n('" + 
                this.payment_id + "', " + WriteSql.getDate_orcl(transaction_date) + ", '" + this.sst_charge + 
                 "', '" + this.staff_id + "', '" + this.order_id + "');"
            );
        else
            return (
                "INSERT INTO Payment (payment_id, payment_date, sst_charge, staff_id, order_id, customer_id) VALUES \n('" + 
                this.payment_id + "', " + WriteSql.getDate_orcl(transaction_date) + ", " + this.sst_charge + 
                 ", '" + this.staff_id + "', '" + this.order_id + "', '" +customer_id+ "');"
            );
    }
	
    public static boolean export_oracle(String sql_file) {
        Payment temp = new Payment();
        boolean final_result;
        try
        {   FileWriter writer = new FileWriter(sql_file, true); 
            BufferedWriter bfw = new BufferedWriter(writer);
            try (Connection conn = DriverManager.getConnection("jdbc:ucanaccess://resource/data/Point_of_sales.mdb;"); Statement s = conn.createStatement()) {
                final_result = s.execute("SELECT * FROM Payment;");
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
        return "Payment{" + "payment_id=" + payment_id + ", staff_id=" + staff_id + ", sst_charge=" + sst_charge + ", customer_id=" + customer_id + ", order_id=" + order_id + ", transaction_date=" + transaction_date + '}';
    }
    
    
}
