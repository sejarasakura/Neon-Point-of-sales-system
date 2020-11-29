package Main;


import SubInterface.SubInterface_Login_Customer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
//page_top_icon.png
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ITSUKA KOTORI
 */
public class Main {
    private static Date today_date;
    private static String customer = null;
    private static String look = "";
    public final static ImageIcon DEFUALT_ICON = new ImageIcon("resource/page_top_icon.png");
    public final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");

    public static String getLook() {
        return look;
        
    }

    public static void setLook(String look) {
        Main.look = look;
    }
    
    public static void main(String[] args){
        
        look = selectLookAndFeel();
        today_date = new Date();
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                javax.swing.UIManager.setLookAndFeel(look);
            }
            System.out.println("The look and felling is approved to the system : " + look );
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            System.out.println("The look and felling is not able to display now will reset to defualt look and feel.");
        }
        //new Interface_OrderPage_cust().setVisible(true);
        //====================================================
        new SubInterface_Login_Customer().setVisible(true);
        //====================================================
    }

    public static Date getToday_date() {
        return today_date;
    }

    public static void setToday_date(Date today_date) {
        Main.today_date = today_date;
    }

    public static String getCustomer() {
        return customer;
    }

    public static void setCustomer(String customer) {
        Main.customer = customer;
    }
    
    private static String selectLookAndFeel(){
        String[] selectionList = {
            "1.  Texture (Light, nostalgis)", 
            "2.  Aluminium (Light, Silver)", 
            "3.  Mc Window (Light, Apple style)", 
            "4.  Noire (Dark, contract) ", 
            "5.  Acryl (Light)", 
            "6.  HiFi (Black)", 
            "7.  Bernstein (Gold)", 
            "8.  Graphite (Light)", 
            "9.  Fast (Light, Simple, Classic)", 
            "10. Luna ()", 
            "11. Mint ()"};
        String input_look = (String)JOptionPane.showInputDialog(null, 
                "Please select a Look and Feel in the program", 
                "Selection of Look and Feel", JOptionPane.QUESTION_MESSAGE,
                Main.DEFUALT_ICON, selectionList, selectionList[0]);
        System.out.println(input_look);
        String tempScan[] = input_look.split("\\.");
        int selection = Integer.valueOf(tempScan[0]);
        switch(selection){
            case 1:
                return "com.jtattoo.plaf.texture.TextureLookAndFeel";
            case 2:
                return "com.jtattoo.plaf.aluminium.AluminiumLookAndFeel";
            case 3:
                return "com.jtattoo.plaf.mcwin.McWinLookAndFeel";
            case 4:
                return "com.jtattoo.plaf.noire.NoireLookAndFeel";
            case 5:
                return "com.jtattoo.plaf.acryl.AcrylLookAndFeel";
            case 6:
                return "com.jtattoo.plaf.hifi.HiFiLookAndFeel";
            case 7:
                return "com.jtattoo.plaf.bernstein.BernsteinLookAndFeel";
            case 8:
                return "com.jtattoo.plaf.graphite.GraphiteLookAndFeel";
            case 9:
                return "com.jtattoo.plaf.fast.FastLookAndFeel";
            case 10:
                return "com.jtattoo.plaf.luna.LunaLookAndFeel";
            case 11:
                return "com.jtattoo.plaf.mint.MintLookAndFeel";
            default:
                System.exit(0);
                break;
        }
        return "";
    }
}
