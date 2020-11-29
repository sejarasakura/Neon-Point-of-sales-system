package User;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Product.Product;
import Sql.WriteSql;


import java.util.Date;
import java.text.ParseException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
/**
 *
 * @author ITSUKA KOTORI
 */
public abstract class SystemUser extends WriteSql{
    
    private String email;
    private String address;
    private String phoneNumber;
    private String fName;
    private String lName;
    private String user_id; // Auto generate
    private String user_ic; // Auto generate
    private Gender gender;
    private String login_password;// only higher level can change or singgle method is change
    private Date join_date;
    private final Calendar cal = Calendar.getInstance();
    
    //=========================================================================================================================

//full Constructor orentated to their own object
    public SystemUser(String user_id, String phoneNumber, String fName, String lName, String address, Gender gender, String login_password, Date join_date, String email, String ic_number) {
        this.setEmail(email);
        this.setAddress(address);
        this.setPhoneNumber(phoneNumber);
        this.setName(fName, lName);
        this.setGender(gender);
        this.setUser_id(user_id);
        this.setLogin_password(login_password);
        this.setJoinDate(join_date);
        this.setUser_ic(ic_number);
    }
//full Constructor oreitated to all string
    public SystemUser(String user_id, String phoneNumber, String fName, String lName, String address, String gender, String login_password, String join_date, String email, String ic_number) {
        this.setEmail(email);
        this.setAddress(address);
        this.setPhoneNumber(phoneNumber);
        this.setName(fName, lName);
        this.setGender(gender);
        this.setUser_id(user_id);
        this.setLogin_password(login_password);
        this.setJoinDate(join_date);
        this.setUser_ic(ic_number);
    }

    public SystemUser(){
    
    }
    
    //=========================================================================================================================
    
    public final boolean setEmail(String email){
        if(Pattern.matches("[a-zA-Z0-9]*[@]{1}[a-zA-Z0-9]*[.]{1}[a-zA-Z0-9.]*", email)){
            this.email = email;
            return true;
        }
        return false;
    }
    
    public final boolean setFName(String fName){
        if((fName != null) && (!fName.equals("")) && (Pattern.matches("[a-zA-Z ]*", fName))){
            this.fName = fName; 
            return true;
        }
        else
            return false;
    }
    
    public final boolean setLName(String lName){
        if((lName != null) && (!lName.equals("")) && (Pattern.matches("[a-zA-Z ]*", lName))){
            this.lName = lName; 
            return true;
        }
        else
            return false;
    }
    
    public final boolean setName(String fName, String lName){
        return (this.setFName(fName) && this.setLName(lName));
    }
    
    public final boolean setGender(Gender gender){
        this.gender = gender;
        return true;
    }
    
    public final boolean setGender(String gender){
        this.gender = Gender.myValueOf(gender);
        return true;
    }
    
    public final boolean setJoinDate(String join_date){
        try {
            this.join_date = WriteSql.DATE_FORMAT.parse(join_date);
        } catch (ParseException ex) {
            System.out.println("Here got error");
            Logger.getLogger(Product.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
    
    public final boolean setUser_ic(String user_ic){
        if(user_ic.matches("[0-9]{6}+[-]{1}[0-9]{2}+[-]{1}[0-9]{4}+")){
            this.user_ic = user_ic;
            return true;
        }else{
            
            return false;
        }
    }
    
    public final String getUser_ic(){
        return this.user_ic;
    }
    
    public final boolean setJoinDate(Date join_date){
        this.join_date = join_date;
        cal.setLenient(false);
        cal.setTime(this.join_date);
        try{
            cal.getTime();
            return true;
        }
        catch(Exception e){
            return false;
        }
    }
    protected final boolean setLogin_password(String login_password){
        if (Pattern.matches("[a-zA-Z0-9]*+", login_password) && login_password.length() >= 8){
            this.login_password = login_password;
            return true;
        }
        else
            return false;
    }
    
    public final boolean setFristPassword(String password, String confirm_password) {
        if(password.equals(confirm_password)){
            this.login_password = password;
            return true;
        }
        return false;
    }
    
    public final boolean setLogin_password(String login_password, String old_password){
        if (old_password.equals(this.login_password)) {
            if (Pattern.matches("[a-zA-Z0-9]*+", login_password)) {
                this.login_password = login_password;
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    
    public final boolean setPhoneNumber(String phoneNumber){
        if(phoneNumber.matches("[0-9]{3}+[-]{1}[0-9]{7}+") || phoneNumber.matches("[+]{1}[0-9]{4}+[-]{1}[0-9]{7}+") ){
            this.phoneNumber = phoneNumber;
            return true;
        }
        else
            return false;
    }

    public final boolean setAddress(String address) {
        if(!address.matches("[,]*+")){
            this.address = address;
            return true;
        }
        else
            return false;
    }

    public final boolean setUser_id(String user_id) {
        if(user_id.matches("[A-Z]{2}+[0-9]{4}+")){
            this.user_id = user_id;
            return true;
        }
        else
            return false;
    }
    
    
    
        
    //=========================================================================================================================

    public final String getAddress() {
        return address;
    }

    public final String getPhoneNumber() {
        return phoneNumber;
    }

    public final String getfName() {
        return fName;
    }

    public final String getlName() {
        return lName;
    }

    public final String getFullName() {
        return fName + " " + lName;
    }

    public final String getUser_id() {
        return user_id;
    }

    public final Gender getGender() {
        return gender;
    }

    public final String getLogin_password() {
        return login_password;
    }

    public final Date getJoin_date() {
        return join_date;
    }
    
    public final String getEmail(){
        return this.email;
    }

    //=========================================================================================================================
    
    public abstract void order_product(Product[] product);
    public abstract String generate_id(String user_type);
    

    //=========================================================================================================================

    
    public void split(String x){
        String[] data = x.split(",");
        this.user_id = data[0];
        this.phoneNumber = data[1];
        this.fName = data[2];
        this.lName = data[3];
        this.address = data[4];
        this.gender = Gender.myValueOf(data[5]);
        this.login_password = data[6];
        try {
            this.join_date = WriteSql.DATE_FORMAT.parse(data[7]);
        } catch (ParseException ex) {
            Logger.getLogger(SystemUser.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.email = data[8];
        this.user_ic = data[9];
    }

 
    @Override
    public String toString() {
        return user_id + "," + phoneNumber + ","+ fName + ","+ lName + ","+ address + "," + gender + ","+ login_password + ","+ WriteSql.DATE_FORMAT.format(join_date) + ',' + email + ',' + user_ic;
    }
    
}
