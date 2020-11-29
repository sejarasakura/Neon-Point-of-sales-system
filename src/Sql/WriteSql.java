package Sql;


import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ITSUKA KOTORI
 */
public abstract class WriteSql {
    // only work for Point_of_sales database
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    public static final SimpleDateFormat DATE_FORMAT_MS = new SimpleDateFormat("M/d/yyyy");
    private static final SimpleDateFormat DATE_FORMAT_ORCL = new SimpleDateFormat("dd-MMM-yyyy");

   
    public abstract void split(ResultSet result);
    public abstract boolean write_this();
    public abstract boolean modify_this();
    public abstract String export_one_oracle();
    public abstract boolean delete_this();
    public abstract boolean isNotNull();
    
    public static String getDate_orcl(Date x){
        return "TO_DATE('" + DATE_FORMAT_ORCL.format(x) + "','dd-mm-yyyy')";
    }
    
    public static int update_query(String query){        
        int x = 0;
        try
        {
            try (Connection conn = DriverManager.getConnection("jdbc:ucanaccess://resource/data/Point_of_sales.mdb;"); Statement s = conn.createStatement()) {
                x = s.executeUpdate(query);
                try{
                    System.out.println("WriteSql > running query : " + query);
                    System.out.println("WriteSql > update failed " + s.getWarnings().getMessage()+":"+s.getWarnings().getErrorCode());
                }catch(NullPointerException ex){
                    System.out.println("WriteSql > running query successful : " + query);                
                }
                s.close();
                conn.close();
            }
        }
        catch(SQLException ex)
        {
            System.out.println(ex.getMessage()+":"+ex.getErrorCode());
        }
        return x;
    }
    
    public static int get_row_count(String table){        
        return WriteSql.getUniqueInt("SELECT COUNT(*) FROM " + table + ";");
    }
    
    public static int getUniqueInt(String query){        
        try
        {            
            try (Connection conn = DriverManager.getConnection("jdbc:ucanaccess://resource/data/Point_of_sales.mdb;"); Statement s = conn.createStatement()) {
                s.execute(query);
                try{
                    System.out.println("WriteSql > " + s.getWarnings().getMessage());
                }catch(NullPointerException ex){
                    
                }
                ResultSet rs = s.getResultSet();
                rs.next();
                int x = rs.getInt(1);
                rs.close();
                s.close();
                conn.close();
                return x;
            }
        }
        catch(SQLException ex)
        {
            System.out.println(ex.getMessage());
        }
        System.out.println("WriteSql > The following query is not correct: " + query);
        return 0;
    }
    
    public static String getUniqueString(String query){        
        try
        {            
            try (Connection conn = DriverManager.getConnection("jdbc:ucanaccess://resource/data/Point_of_sales.mdb;"); Statement s = conn.createStatement();) {
                s.execute(query);
                try{
                    System.out.println("WriteSql > " + s.getWarnings().getMessage());
                }catch(NullPointerException ex){
                    
                }
                ResultSet rs = s.getResultSet();
                rs.next();
                String x = rs.getString(1);
                rs.close();
                s.close();
                conn.close();
                return x;
            }
        }
        catch(SQLException ex)
        {
            System.out.println(ex.getMessage());
        }
        System.out.println("WriteSql > The following query is not correct: " + query);
        return "";
    }
    
    
    public static double getUniqueDouble(String query){        
        try
        {            
            try (Connection conn = DriverManager.getConnection("jdbc:ucanaccess://resource/data/Point_of_sales.mdb;"); Statement s = conn.createStatement();) {
                s.execute(query);
                try{
                    System.out.println("WriteSql > " + s.getWarnings().getMessage());
                }catch(NullPointerException ex){
                    
                }
                ResultSet rs = s.getResultSet();
                rs.next();
                double x = rs.getDouble(1);
                rs.close();
                s.close();
                conn.close();
                return x;
            }
        }
        catch(SQLException ex)
        {
            System.out.println(ex.getMessage());
        }
        System.out.println("WriteSql > The following query is not correct: " + query);
        return 0;
    }
    
    
    public static Date getUniqueDate(String query){        
        try
        {            
            try (Connection conn = DriverManager.getConnection("jdbc:ucanaccess://resource/data/Point_of_sales.mdb;"); Statement s = conn.createStatement()) {
                s.execute(query);
                try{
                    System.out.println("WriteSql > " + s.getWarnings().getMessage());
                }catch(NullPointerException ex){
                    
                }
                ResultSet rs = s.getResultSet();
                rs.next();
                Date x = rs.getDate(1);
                rs.close();
                s.close();
                conn.close();
                return x;
            }
        }
        catch(SQLException ex)
        {
            System.out.println(ex.getMessage());
        }
        System.out.println("WriteSql > The following query is not correct: " + query);
        return null;
    }

}
