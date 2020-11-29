package User.Staff;

import Transaction.Payment;
import Product.Product;
import User.Gender;
import Sql.WriteSql;
import User.SystemUser;


import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.*;
import java.io.BufferedWriter;
import java.io.FileWriter;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author TARUC
 */
public class Staff extends SystemUser{

    
    private double salary;
    private double salary_rate_hours;
    private int working_hours;
    private StaffLevel level;
    private Department department;
    
    
    //===================================================================================

    public Staff(double salary, double salary_rate_hours, int working_hours, StaffLevel level, Department department, 
            String user_id, String phoneNumber, String fName, String lName, String address, Gender gender, 
            String login_password, Date join_date, String email, String ic_number) {
        super(user_id, phoneNumber, fName, lName, address, gender, login_password, join_date, email, ic_number);
        this.setSalary(salary);
        this.setSalary_rate_hours(salary_rate_hours);
        this.setWorkingHour(working_hours);
        this.setLevel(level);
        this.setDepartment(department);
    }

    
    public Staff(String salary, String salary_rate_hours, String working_hours, String level, String department, 
            String user_id, String phoneNumber, String fName, String lName, String address, String gender, 
            String login_password, String join_date, String email, String ic_number) {
        super(user_id, phoneNumber, fName, lName, address, gender, login_password, join_date, email, ic_number);
        this.setSalary(salary);
        this.setSalary_rate_hours(salary_rate_hours);
        this.setWorkingHour(working_hours);
        this.setLevel(level);
        this.setDepartment(department);
    }

    public Staff(StaffLevel level) {
        this.level = level;
    }

    public Staff() {
        super();
    }

    public Staff(String staff_id) {
        try {
            try (Connection conn = DriverManager.getConnection("jdbc:ucanaccess://resource/data/Point_of_sales.mdb;"); Statement s = conn.createStatement()) {
                s.execute("SELECT * FROM Staff WHERE staff_id='" + staff_id + "';");
                ResultSet rs = s.getResultSet();
                rs.next();
                this.split(rs);
                rs.close();
                s.close();
                conn.close();
            }
        }
        catch(SQLException ex){
            System.out.println("Staff : The id not found in the database - Constructor(" + staff_id +  ") error !! ");
        }
    }
    
    //===================================================================================
    
    public final boolean setLevel(String level){
        this.level = StaffLevel.myValueOf(level);
        return true;
    }
    public final boolean setLevel(StaffLevel level){
        this.level = level;
        return true;
    }

    public final boolean setSalary(double salary) {
        if(salary >= 0 && salary <= 150000 ){
            this.salary = salary;
            return true;
        }
        return false;
    }
    
    public final boolean setWorkingHour(int working_hours) {
        if(working_hours >= 0 && working_hours <= 16 ){
            this.working_hours = working_hours;
            return true;
        }
        return false;
    }

    public final boolean setSalary_rate_hours(double salary_rate_hours) {
        if(salary_rate_hours >= 0 && salary_rate_hours <= 150 ){
            this.salary_rate_hours = salary_rate_hours;
            return true;
        }
        return false;
    }

    public final boolean setSalary(String ssalary) {
        double salary = Double.valueOf(ssalary);
        if(salary >= 0 && salary <= 150000 ){
            this.salary = salary;
            return true;
        }
        return false;
    }
    
    public final boolean setWorkingHour(String sworking_hours) {
        int working_hours = Integer.valueOf(sworking_hours);
        if(working_hours >= 0 && working_hours <= 150000 ){
            this.working_hours = working_hours;
            return true;
        }
        return false;
    }

    public final boolean setSalary_rate_hours(String string) {
        double salary_rate_hours = Double.valueOf(string);
        if(salary_rate_hours >= 0 && salary_rate_hours <= 150 ){
            this.salary_rate_hours = salary_rate_hours;
            return true;
        }
        return false;
    }

    public final boolean setDepartment(Department department) {
        this.department = department;
        return true;
    }
    public final boolean setDepartment(String department) {
        this.department = Department.myValueOf(department);
        return true;
    }
    
    
    //===================================================================================

    public final double getSalary() {
        return salary;
    }

    public final double getSalary_rate_hours() {
        return salary_rate_hours;
    }

    public final Department getDepartment() {
        return department;
    }


    public final StaffLevel getLevel() {
        return level;
    }
    
    public final int getWorkingHours(){
        return this.working_hours;
    }
    
    //===================================================================================

    public void accept_order(SystemUser user, Product[] x){
        
    }
    
    public boolean isManager(){
        return StaffLevel.isStaffLevel(this.level.id_front_code());
    }
    
    //===================================================================================

    @Override
    public void order_product(Product[] product) {
        
        
        
    }

    @Override
    public String generate_id(String user_type)
    {        
        if(this.getLevel() != null){
            return String.format("%2s%04d", StaffLevel.myValueOf(user_type).id_database(), WriteSql.getUniqueInt("SELECT TOP 1 SUBSTRING(STAFF_ID,3,8) AS INT FROM STAFF ORDER BY SUBSTRING(STAFF_ID,3,8) DESC;") + 1);
        }
        return null;
    }


    @Override
    public boolean write_this(){
        return WriteSql.update_query(
            "INSERT INTO Staff (staff_id, ic_number, gender, frist_name, last_name, phone_number, email_address, employment_date, working_hours, salary, salary_rate_hours, department_id, position_id, login_password, address)" +
            " VALUES ('" + this.getUser_id() + "', '" + this.getUser_ic()+ "', '" + this.getGender().toString_database() + "', '" + this.getfName() + "', '" + this.getlName() + "', '" + this.getPhoneNumber()+
            "', '" + this.getEmail()+ "', #" + WriteSql.DATE_FORMAT_MS.format(this.getJoin_date()) + "#, " + this.getWorkingHours()+ ", " + this.getSalary() + ", " + this.salary_rate_hours + ", '" + 
            this.getDepartment().to_idString() + "', '" + this.level.id_database() + "', '" + this.getLogin_password() + "', '" + this.getAddress() + "');"
        ) == 1;
    }

    @Override
    public boolean delete_this(){        
        return WriteSql.update_query("DELETE FROM Staff WHERE staff_id='" + this.getUser_id() + "';") == 1;
    }


    @Override
    public boolean modify_this() {        
        if (this.getLogin_password() != null || !"null".equals(this.getLogin_password())) {
            return WriteSql.update_query(
                    "UPDATE Staff SET ic_number='" + this.getUser_ic()
                    + "', gender='" + this.getGender().toString_database()
                    + "', frist_name='" + this.getfName()
                    + "', last_name='" + this.getlName()
                    + "', phone_number='" + this.getPhoneNumber()
                    + "', email_address='" + this.getEmail()
                    + "', employment_date=#" + WriteSql.DATE_FORMAT_MS.format(this.getJoin_date())
                    + "#, department_id='" + this.getDepartment().to_idString()
                    + "', position_id='" + this.getLevel().id_database()
                    + "', address='" + this.getAddress()
                    + "', login_password='" + this.getLogin_password()
                    + "', working_hours=" + this.getWorkingHours()
                    + " , salary=" + this.getSalary()
                    + " , salary_rate_hours=" + this.salary_rate_hours
                    + " WHERE staff_id='" + this.getUser_id() + "';"
            ) == 1;
        } else {      
            return WriteSql.update_query(
            "UPDATE Staff SET ic_number='"+ this.getUser_ic() + 
                "', gender='" + this.getGender().toString_database() + 
                "', frist_name='" + this.getfName() +
                "', last_name='" + this.getlName() +
                "', phone_number='" + this.getPhoneNumber()+
                "', email_address='" + this.getEmail() +
                "', employment_date=#" + WriteSql.DATE_FORMAT_MS.format(this.getJoin_date()) +
                "#, department_id='" + this.getDepartment().to_idString() +
                "', position_id='" + this.getLevel().id_database()+
                "', address='" + this.getAddress()+
                "', working_hours=" + this.getWorkingHours() + 
                " , salary=" + this.getSalary() + 
                " , salary_rate_hours=" + this.salary_rate_hours + 
                " WHERE staff_id='" + this.getUser_id() + "';"
            ) == 1;
        }
    }
    
    @Override
    public void split(String x){
        String[] data = x.split("&");
        super.split(data[0]);
        this.setLevel(data[1]);
        this.setSalary(data[2]);
        this.setSalary_rate_hours(data[3]);
        this.setWorkingHour(data[4]);
        this.setDepartment(data[5]);
    }
    
    @Override
    public void split(ResultSet x){
        try {
            this.setUser_id(x.getString("staff_id"));
            this.setUser_ic(x.getString("ic_number"));
            this.setGender(x.getString("gender"));
            this.setFName(x.getString("frist_name"));
            this.setLName(x.getString("last_name"));
            this.setPhoneNumber(x.getString("phone_number"));
            this.setAddress(x.getString("address"));
            this.setEmail(x.getString("email_address"));
            this.setJoinDate(x.getDate("employment_date"));
            this.setDepartment(x.getString("department_id"));
            this.setWorkingHour(Integer.valueOf(x.getString("working_hours")));
            this.setSalary(Double.valueOf(x.getString("salary")));
            this.setSalary_rate_hours(Double.valueOf(x.getString("salary_rate_hours")));
            this.setLevel(x.getString("position_id"));
            this.setLogin_password(x.getString("login_password"));
        } catch (SQLException ex) {
            System.out.println("Staff : result is unable to split from database !! ");
        }
    }
    
    @Override
    public String toString() {
        return super.toString() + "&" + level.toString() + "&" + salary + "&" + salary_rate_hours + 
		"&" + working_hours + "&" + department.toString();
    }
    
    @Override
    public boolean isNotNull(){
        System.out.println(this.toString());
        return (salary != 0 || (salary_rate_hours != 0 && working_hours != 0)) && getUser_id() != null && 
                getPhoneNumber() != null && getfName() != null && getlName() != null  && getAddress() != null &&
                getLogin_password() != null && getEmail() != null && getUser_ic() != null;
    }
    
    @Override
    public String export_one_oracle(){
        return (
            "INSERT INTO Staff (staff_id, ic_number, gender, frist_name, last_name, phone_number, email_address, employment_date, working_hours, salary, "
            + "salary_rate_hours, login_password, address, department_id, position_id) VALUES\n('" + this.getUser_id() +"', '"+ this.getUser_ic() +"', '"+ 
            this.getGender().toString_database() +"' ,'"+ this.getfName() +"', '"+ this.getlName() +"', '"+ this.getPhoneNumber() +"', '"+ this.getEmail() +
            "', "+ WriteSql.getDate_orcl(this.getJoin_date()) +", "+ this.working_hours +", "+ this.salary + "," +
             this.salary_rate_hours +", '"+ this.getLogin_password() +"', '"+ this.getAddress() +"', '"+ this.department.to_idString() +"', '"+ this.level.id_front_code() +"');" 			 
        );
    }
	
    public static boolean export_oracle(String sql_file) {
        Staff temp = new Staff();
        boolean final_result;
        try
        {   FileWriter writer = new FileWriter(sql_file, true); 
            BufferedWriter bfw = new BufferedWriter(writer);
            try (Connection conn = DriverManager.getConnection("jdbc:ucanaccess://resource/data/Point_of_sales.mdb;"); Statement s = conn.createStatement()) {
                final_result = s.execute("SELECT * FROM Staff;");
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
