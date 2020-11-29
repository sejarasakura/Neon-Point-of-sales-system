package Interface;


import Main.Main;
import User.Staff.Staff;
import SubInterface.SubInterface_Login_Staff;


import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *[1347, 754]
 * @author ITSUKA KOTORI
 */
public class Interface_MenuStaff extends javax.swing.JFrame {

    private Staff duty_staff;
    /**
     * Creates new form PointOfSalesMenu
     */
    /*public Interface_Menu_staff(String staff_id) {
    initComponents();
    initComponents_background();
    // if decesion level manager can stright away use console run all the function they need
    if(new Staff(staff_id).getLevel().level_number() >= 4)
    pointOfSales_Direct_Go_subuse(0);
    }*/
    public Interface_MenuStaff() {
        initComponents();
        initComponents_background();
        initComponents_label();
        initComponents_SetClose();
        pointOfSales_Go();
        this.setIconImage(Main.DEFUALT_ICON.getImage());
    }
    
    private void pointOfSales_Go(){
        SubInterface_Login_Staff login;
        if(!new File("resource/data/staff_id_logedin.txt").isFile()){
            login = new SubInterface_Login_Staff();
            login.setVisible(true);
            this.dispose();
        }
        else {
            // auto login
        }
    }
    
    public String getDataDutyStaff(){
        File temp_f = new File("resource/data/staff_id_logedin.txt");
        if (temp_f.isFile()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(temp_f));
                try {
                    return br.readLine();
                } catch (IOException ex) {
                    Logger.getLogger(Interface_MenuManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }catch (FileNotFoundException ex) {
                Logger.getLogger(Interface_MenuManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return "";
    }
    
    private void initComponents_SetClose() {
        this.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e){
                menu_lable_4MouseClicked(null);
            }
        });
    }
    
    private void initComponents_background(){
        ((JPanel)this.getContentPane()).setOpaque(false);
        ImageIcon img = new ImageIcon("resource/wallpaper-menu.jpg");
        JLabel background = new JLabel(img);
        this.getLayeredPane().add(background, new Integer(Integer.MIN_VALUE));
        img.setImage(getScaledImage(img.getImage(),this.getWidth(), this.getHeight()));
        background.setBounds(0, 0,this.getWidth(), this.getHeight());   
        this.setVisible(true);
    }
    
    private void initComponents_label(){
        // frist one is payment         
        label_today_date.setText("Today Date : " + Main.DATE_FORMAT.format(Main.getToday_date()));
        this.neon.setIcon(new ImageIcon(Interface_Payment.getScaledImage(new ImageIcon("resource/neon.png").getImage(), 524, 160)));
        this.menu_lable_1.setIcon(new ImageIcon(new ImageIcon("resource/menu_button/pos_main_menu.png").getImage().getScaledInstance(485, 60, Image.SCALE_DEFAULT)));
        this.menu_lable_2.setIcon(new ImageIcon(new ImageIcon("resource/menu_button/customer_manager.png").getImage().getScaledInstance(485, 60, Image.SCALE_DEFAULT)));
        this.menu_lable_3.setIcon(new ImageIcon(new ImageIcon("resource/menu_button/order_manager.png").getImage().getScaledInstance(485, 60, Image.SCALE_DEFAULT)));
        this.menu_lable_4.setIcon(new ImageIcon(new ImageIcon("resource/menu_button/exit.png").getImage().getScaledInstance(485, 60, Image.SCALE_DEFAULT)));
        this.menu_lable_1.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.menu_lable_2.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.menu_lable_3.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.menu_lable_4.setCursor(new Cursor(Cursor.HAND_CURSOR));       
    }
    
    
    private Image getScaledImage(Image scrImg, int w, int h){
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(scrImg, 0, 0, w, h, null);
        g2.dispose();
        return resizedImg;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        menu_lable_2 = new javax.swing.JLabel();
        menu_lable_1 = new javax.swing.JLabel();
        menu_lable_4 = new javax.swing.JLabel();
        menu_lable_3 = new javax.swing.JLabel();
        label_today_date = new javax.swing.JLabel();
        neon = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Satff main menu");

        jLabel1.setFont(new java.awt.Font("Garamond", 3, 59)); // NOI18N
        jLabel1.setText("<html><body>Point<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Of<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Sales<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System</body></html>");
        jLabel1.setToolTipText("");

        jButton1.setBackground(new java.awt.Color(255, 204, 204));
        jButton1.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(153, 0, 0));
        jButton1.setText("Logout");
        jButton1.setFocusPainted(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        menu_lable_2.setFont(new java.awt.Font("Consolas", 0, 24)); // NOI18N
        menu_lable_2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                menu_lable_2MouseClicked(evt);
            }
        });

        menu_lable_1.setFont(new java.awt.Font("Consolas", 0, 24)); // NOI18N
        menu_lable_1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                menu_lable_1MouseClicked(evt);
            }
        });

        menu_lable_4.setFont(new java.awt.Font("Consolas", 0, 24)); // NOI18N
        menu_lable_4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                menu_lable_4MouseClicked(evt);
            }
        });

        menu_lable_3.setFont(new java.awt.Font("Consolas", 0, 24)); // NOI18N
        menu_lable_3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                menu_lable_3MouseClicked(evt);
            }
        });

        label_today_date.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label_today_date.setText("Today Date : 12-Feb-2019 12:20:56 pm");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(110, 110, 110)
                        .addComponent(neon, javax.swing.GroupLayout.PREFERRED_SIZE, 528, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(132, 132, 132)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(menu_lable_1, javax.swing.GroupLayout.PREFERRED_SIZE, 485, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(menu_lable_4, javax.swing.GroupLayout.PREFERRED_SIZE, 485, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(menu_lable_3, javax.swing.GroupLayout.PREFERRED_SIZE, 485, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(menu_lable_2, javax.swing.GroupLayout.PREFERRED_SIZE, 485, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 159, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_today_date, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 396, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(366, 366, 366)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(39, 39, 39))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(28, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(39, 39, 39)
                        .addComponent(label_today_date, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(107, 107, 107)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(165, 165, 165))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(neon, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(89, 89, 89)
                        .addComponent(menu_lable_1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(menu_lable_2, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(menu_lable_3, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(menu_lable_4, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(97, 97, 97))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // 
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.log_out();
        new SubInterface_Login_Staff().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed
    // Label 1 pos main menu payment 
    private void menu_lable_1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menu_lable_1MouseClicked
        // TODO add your handling code here:
        new Interface_Payment().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_menu_lable_1MouseClicked
    //  Customer manager customer may apply staff edit something or applycation 
    private void menu_lable_2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menu_lable_2MouseClicked
        // TODO add your handling code here:
        new Interface_Customer_manager().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_menu_lable_2MouseClicked
    //Order manager payment not allow staff change order is not include any credit
    private void menu_lable_3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menu_lable_3MouseClicked
        // TODO add your handling code here:
        new Interface_Order_manager().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_menu_lable_3MouseClicked
    // Label 4 Exit
    private void menu_lable_4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menu_lable_4MouseClicked
        // TODO add your handling code here:
        int x = JOptionPane.showConfirmDialog(this, "You are not yet log out to current account, are yousure want to exit without logout!! and exit the system. Yes exit, No logout !", "Not logged out!!", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        switch (x) {
            case JOptionPane.YES_OPTION:
                this.dispose();
                System.exit(0);
                break;
            case JOptionPane.CANCEL_OPTION:
                break;
            default:
                this.log_out();
                new SubInterface_Login_Staff().setVisible(true);
                this.dispose();
                break;
        }
    }//GEN-LAST:event_menu_lable_4MouseClicked

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel label_today_date;
    private javax.swing.JLabel menu_lable_1;
    private javax.swing.JLabel menu_lable_2;
    private javax.swing.JLabel menu_lable_3;
    private javax.swing.JLabel menu_lable_4;
    private javax.swing.JLabel neon;
    // End of variables declaration//GEN-END:variables

    private void log_out() {
        File xx = new File("resource/data/staff_id_logedin.txt");
        if(xx.isFile()){
            xx.delete();
        }
        this.dispose();
    }
    
}
