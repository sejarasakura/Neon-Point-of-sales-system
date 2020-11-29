package User.Customer;


import Transaction.Payment;
import Product.Product;
import User.Gender;
import Sql.WriteSql;
import User.SystemUser;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ITSUKA KOTORI
 */
public class Customer extends SystemUser{
    

    private int total_point;
    private MemberLevel level;
    private Date birthdayDate;


    public Customer(String user_id, String phoneNumber, String fName, String lName, String address, Gender gender, String login_password, Date join_date, String email, MemberLevel level, int total_point, Date birthdayDate, String ic_number) {
        super(user_id, phoneNumber, fName, lName, address, gender, login_password, join_date, email, ic_number);
        this.total_point = total_point;
        this.level = level;
        this.birthdayDate = birthdayDate;
    }
    
    public final boolean notNull(){
        return getUser_id() != null &&  getPhoneNumber() != null &&  getLogin_password() != null &&  getJoin_date() != null &&  getUser_ic() != null;
    }

    public Customer() {
        
    }
    
    public Customer(String user_id) {
        try {
            try (Connection conn = DriverManager.getConnection("jdbc:ucanaccess://resource/data/Point_of_sales.mdb;"); Statement s = conn.createStatement()) {
                s.execute("SELECT * FROM Customer WHERE customer_id='" + user_id + "';");
                ResultSet rs = s.getResultSet();
                rs.next();
                this.split(rs);
                rs.close();
                s.close();
                conn.close();
            }
        }
        catch(SQLException ex){
            System.out.println("Customer : The id not found in the database - Constructor("
                    + user_id + ") error !! ");
        }
    }

    //===================================================================================

    public void setTotal_point(int total_puchase) {
        this.total_point = total_puchase;
    }

    public void setLevel(MemberLevel level) {
        this.level = level;
    }

    public void setLevel(String level) {
        this.level = MemberLevel.myValueOf(level);
    }
    
    public boolean setBrithdayDate(Date date){
        this.birthdayDate = date;
        return true;
    }
    
    public boolean setBrithdayDate(String date){
        try {
            this.birthdayDate = DATE_FORMAT.parse(date);
            return true;
        } catch (ParseException ex) {
            Logger.getLogger(Customer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    //===================================================================================

    public int getTotal_point() {
        return total_point;
    }

    public MemberLevel getLevel() {
        return level;
    }
    
    public Date getBrithdayDate(){
        return this.birthdayDate;
    }

    //=====================================================================================
    
    public double useMemberPoint(double original_price, int use_point_amount){
        if(use_point_amount > this.total_point){
            return original_price;
        }else{
            this.total_point -= use_point_amount;
            this.modify_this();
            return (original_price - (int)(use_point_amount/10));
        }
    }
    
    //===================================================================================

    @Override
    public void order_product(Product[] product) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    @Override
    public String generate_id(String user_type)
    {        
        return String.format("%2s%04d", MemberLevel.myValueOf(user_type).id_front_code(), 
        1 + WriteSql.getUniqueInt("SELECT TOP 1 SUBSTRING(CUSTOMER_ID,3,8) AS INT FROM Customer ORDER BY SUBSTRING(CUSTOMER_ID,3,8) DESC;"));
    }


    @Override
    public boolean write_this(){
        
        if((WriteSql.getUniqueInt("SELECT COUNT(email_address) FROM Staff WHERE email_address='" +getEmail()+"';") + 
                WriteSql.getUniqueInt("SELECT COUNT(email_address) FROM Customer WHERE email_address='" +getEmail()+"';")) == 0){
            return WriteSql.update_query(
                "INSERT INTO Customer (customer_id, ic_number, gender, frist_name, "+
                        "last_name, phone_number, address, brithday_date, join_date, member_level, email_address, login_password)" +
                " VALUES ('" + this.getUser_id() + "', '" + this.getUser_ic()+ "', '" + 
                        this.getGender().toString_database() + "', '" + this.getfName() + 
                        "', '" + this.getlName() + "', '" + this.getPhoneNumber() + "', '" + this.getAddress()+ "', #" + 
                        WriteSql.DATE_FORMAT_MS.format(this.getBrithdayDate()) + "#, #" + 
                        WriteSql.DATE_FORMAT_MS.format(this.getJoin_date()) + "#, '" + 
                        this.level.id_front_code() + "', '" + this.getEmail()+  "', '" + this.getLogin_password() + "');"
            ) == 1;
        }else{
            JOptionPane.showMessageDialog(null, "The email address is arealdy regiser in neon online shop or staff", "Email address is used", JOptionPane.OK_OPTION);
            return false;
        }
    }

    @Override
    public boolean delete_this()
    {        
        return WriteSql.update_query("DELETE FROM Customer WHERE customer_id='" + this.getUser_id() + "';") == 1;
    }


    @Override
    public boolean modify_this()
    {   
        return WriteSql.update_query("UPDATE Customer SET ic_number='" + this.getUser_ic() + 
                "', gender='" + this.getGender().toString_database() + 
                "', frist_name='" + this.getfName() +
                "', last_name='" + this.getlName() +
                "', phone_number='" + this.getPhoneNumber()+
                "', email_address='" + this.getEmail() +
                "', join_date=#" + WriteSql.DATE_FORMAT_MS.format(this.getJoin_date()) +
                "#, brithday_date=#" + WriteSql.DATE_FORMAT_MS.format(this.getBrithdayDate()) +
                "#, login_password='" + this.getLogin_password() +
                "', member_level='" + this.getLevel().id_front_code()+
                "' WHERE customer_id='" + this.getUser_id() + "';") == 1;
    }
    
    @Override
    public void split(String x){
        String[] data = x.split("&");
        super.split(data[0]);
        this.setLevel(data[1]);
        this.setTotal_point(Integer.valueOf(data[2]));
        this.setBrithdayDate(data[3]);
    }
    
    @Override
    public final void split(ResultSet x){
        try {
            this.setUser_id(x.getString("customer_id"));
            this.setUser_ic(x.getString("ic_number"));
            this.setGender(x.getString("gender"));
            this.setFName(x.getString("frist_name"));
            this.setLName(x.getString("last_name"));
            this.setPhoneNumber(x.getString("phone_number"));
            this.setEmail(x.getString("email_address"));
            this.setJoinDate(x.getDate("join_date"));
            this.setBrithdayDate(x.getDate("join_date"));
            this.setLevel(x.getString("member_level"));
            this.setLogin_password(x.getString("login_password"));
            this.setAddress(x.getString("address"));
        } catch (SQLException ex) {
            System.out.println("Customer : result is unable to split from database !! ");
        }
    }
    
    @Override
    public String toString() {
        return super.toString() + "&" + level.toString() + "&" + total_point + DATE_FORMAT.format(birthdayDate);
    }

    @Override
    public boolean isNotNull() {
        return (getUser_id() != null && 
                getPhoneNumber() != null && getfName() != null && getlName() != null  && getAddress() != null &&
                getLogin_password() != null && getEmail() != null && getUser_ic() != null);
    }
    @Override
    public String export_one_oracle(){
            return (
                    "INSERT INTO Customer (customer_id, ic_number, gender, frist_name, last_name, phone_number, address, birthday_date, join_date, membership_id, "
                    + "email, login_password) VALUES \n('" + this.getUser_id() +"', '"+ this.getUser_ic() +"', '"+ 
                    this.getGender().toString_database() +"' ,'"+ this.getfName() +"', '"+ this.getlName() +"', '"+ this.getPhoneNumber() +"', '"+ this.getAddress()+
                    "', "+ WriteSql.getDate_orcl(this.birthdayDate) +",  "+ WriteSql.getDate_orcl(this.getJoin_date()) + ", '"+ 
                    this.level.id_front_code() +"', '"+ this.getEmail()+"', '"+ this.getLogin_password() +"');" 			 
            );
    }
	
    public static boolean export_oracle(String sql_file) {
        Customer temp = new Customer();
        boolean final_result = true;
        try
        {   FileWriter writer = new FileWriter(sql_file, true); 
            BufferedWriter bfw = new BufferedWriter(writer);
            try (Connection conn = DriverManager.getConnection("jdbc:ucanaccess://resource/data/Point_of_sales.mdb;"); Statement s = conn.createStatement()) {
                final_result = s.execute("SELECT * FROM Customer;");
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
}
