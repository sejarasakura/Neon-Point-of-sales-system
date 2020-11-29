package Interface;


import Main.Main;
import User.Staff.Staff;
import User.Staff.StaffLevel;
import SubInterface.SubInterface_Login_Staff;
import Sql.WriteSql;


import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
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
public class Interface_MenuManager extends javax.swing.JFrame {

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
    public Interface_MenuManager() {
        initComponents();
        initComponents_background();
        initComponents_label();
        initComponents_SetClose();
        pointOfSales_Go();
        this.setIconImage(Main.DEFUALT_ICON.getImage());
    }
    
    private void pointOfSales_Go(){
        SubInterface_Login_Staff login;
        if (WriteSql.get_row_count("Staff") == 0) {
            JOptionPane.showConfirmDialog(null, "You are frist time login our system, this system need staff login bafore using please signup frist!! by using console", "Sign up Notices", JOptionPane.DEFAULT_OPTION);
            frist_sign_up_console_version();
        } else if(!new File("resource/data/staff_id_logedin.txt").isFile()){
            login = new SubInterface_Login_Staff();
            login.setVisible(true);
            this.dispose();
        }
        else {
            // auto login
        }
    }
    
    private void setTodayDate(Date data){
        Main.setToday_date(data);
        label_today_date.setText("Today Date : " + Main.DATE_FORMAT.format(data));
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
                menu_lable_9MouseClicked(null);
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
        this.menu_lable_1.setIcon(new ImageIcon(new ImageIcon("resource/menu_button/pos_main_menu.png").getImage().getScaledInstance(485, 60, Image.SCALE_DEFAULT)));
        this.menu_lable_2.setIcon(new ImageIcon(new ImageIcon("resource/menu_button/customer_manager.png").getImage().getScaledInstance(485, 60, Image.SCALE_DEFAULT)));
        this.menu_lable_3.setIcon(new ImageIcon(new ImageIcon("resource/menu_button/staff_manager.png").getImage().getScaledInstance(485, 60, Image.SCALE_DEFAULT)));
        this.menu_lable_4.setIcon(new ImageIcon(new ImageIcon("resource/menu_button/transection_manager.png").getImage().getScaledInstance(485, 60, Image.SCALE_DEFAULT)));
        this.menu_lable_5.setIcon(new ImageIcon(new ImageIcon("resource/menu_button/order_manager.png").getImage().getScaledInstance(485, 60, Image.SCALE_DEFAULT)));
        this.menu_lable_6.setIcon(new ImageIcon(new ImageIcon("resource/menu_button/product_manager.png").getImage().getScaledInstance(485, 60, Image.SCALE_DEFAULT)));
        this.menu_lable_7.setIcon(new ImageIcon(new ImageIcon("resource/menu_button/veiw_report.png").getImage().getScaledInstance(485, 60, Image.SCALE_DEFAULT)));
        this.menu_lable_8.setIcon(new ImageIcon(new ImageIcon("resource/menu_button/insert_sql.png").getImage().getScaledInstance(485, 60, Image.SCALE_DEFAULT)));
        this.menu_lable_9.setIcon(new ImageIcon(new ImageIcon("resource/menu_button/exit.png").getImage().getScaledInstance(485, 60, Image.SCALE_DEFAULT)));
        this.menu_lable_1.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.menu_lable_2.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.menu_lable_3.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.menu_lable_4.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.menu_lable_5.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.menu_lable_6.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.menu_lable_7.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.menu_lable_8.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.menu_lable_9.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
        menu_lable_6 = new javax.swing.JLabel();
        menu_lable_5 = new javax.swing.JLabel();
        menu_lable_7 = new javax.swing.JLabel();
        menu_lable_8 = new javax.swing.JLabel();
        menu_lable_9 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        input_today_date = new datechooser.beans.DateChooserPanel();
        label_today_date = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Manager main menu");

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

        menu_lable_6.setFont(new java.awt.Font("Consolas", 0, 24)); // NOI18N
        menu_lable_6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                menu_lable_6MouseClicked(evt);
            }
        });

        menu_lable_5.setFont(new java.awt.Font("Consolas", 0, 24)); // NOI18N
        menu_lable_5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                menu_lable_5MouseClicked(evt);
            }
        });

        menu_lable_7.setFont(new java.awt.Font("Consolas", 0, 24)); // NOI18N
        menu_lable_7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                menu_lable_7MouseClicked(evt);
            }
        });

        menu_lable_8.setFont(new java.awt.Font("Consolas", 0, 24)); // NOI18N
        menu_lable_8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                menu_lable_8MouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                menu_lable_8MousePressed(evt);
            }
        });

        menu_lable_9.setFont(new java.awt.Font("Consolas", 0, 24)); // NOI18N
        menu_lable_9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                menu_lable_9MouseClicked(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel2.setText("Set Today Date            :");

        input_today_date.addSelectionChangedListener(new datechooser.events.SelectionChangedListener() {
            public void onSelectionChange(datechooser.events.SelectionChangedEvent evt) {
                input_today_dateOnSelectionChange(evt);
            }
        });
        input_today_date.addCommitListener(new datechooser.events.CommitListener() {
            public void onCommit(datechooser.events.CommitEvent evt) {
                input_today_dateOnCommit(evt);
            }
        });

        label_today_date.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        label_today_date.setText("Today Date : 12-Feb-2019 12:20:56 pm");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(142, 142, 142)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(menu_lable_1, javax.swing.GroupLayout.PREFERRED_SIZE, 485, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(menu_lable_9, javax.swing.GroupLayout.PREFERRED_SIZE, 485, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(menu_lable_8, javax.swing.GroupLayout.PREFERRED_SIZE, 485, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(menu_lable_7, javax.swing.GroupLayout.PREFERRED_SIZE, 485, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(menu_lable_5, javax.swing.GroupLayout.PREFERRED_SIZE, 485, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(menu_lable_6, javax.swing.GroupLayout.PREFERRED_SIZE, 485, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(menu_lable_4, javax.swing.GroupLayout.PREFERRED_SIZE, 485, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(menu_lable_3, javax.swing.GroupLayout.PREFERRED_SIZE, 485, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(menu_lable_2, javax.swing.GroupLayout.PREFERRED_SIZE, 485, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(112, 112, 112)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(input_today_date, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(171, 171, 171))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(label_today_date, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(39, 39, 39))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 396, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(menu_lable_1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(menu_lable_2, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(menu_lable_3, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(menu_lable_4, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(menu_lable_5, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(menu_lable_6, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(menu_lable_7, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(menu_lable_8, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(menu_lable_9, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(label_today_date, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(77, 77, 77)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(52, 52, 52)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(input_today_date, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(45, 45, 45))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.log_out();
        new SubInterface_Login_Staff().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    // Label 1 All The Payment methods can inport order record 
    private void menu_lable_1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menu_lable_1MouseClicked
        // TODO add your handling code here:
        new Interface_Payment().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_menu_lable_1MouseClicked
    // Label 2 Customer manager
    private void menu_lable_2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menu_lable_2MouseClicked
        // TODO add your handling code here:
        new Interface_Customer_manager().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_menu_lable_2MouseClicked
    // Label 3 staff manager
    private void menu_lable_3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menu_lable_3MouseClicked
        // TODO add your handling code here:
        new Interface_Staff_manager().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_menu_lable_3MouseClicked
    // Label 4 Transaction manager 
    private void menu_lable_4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menu_lable_4MouseClicked
        new Interface_Payment_Manager().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_menu_lable_4MouseClicked
    // Label 5 Order manager
    private void menu_lable_5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menu_lable_5MouseClicked

        new Interface_Order_manager().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_menu_lable_5MouseClicked
    // Lable 6 Product Manager
    private void menu_lable_6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menu_lable_6MouseClicked
        // TODO add your handling code here:
        new Interface_Product_manager().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_menu_lable_6MouseClicked
    // Label 7 Report and dash board 
    private void menu_lable_7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menu_lable_7MouseClicked
        // TODO add your handling code here:
        new Interface_Report().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_menu_lable_7MouseClicked
    // Label 8 insert insert sql data support multi line
    private void menu_lable_8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menu_lable_8MouseClicked
        if (new Staff(SubInterface_Login_Staff.decode_id(getDataDutyStaff())).getLevel().position() >= StaffLevel.DECESION_MANAGER.position()) {
// TODO add your handling code here:
            new Interface_WriteSql().setVisible(true);
            this.dispose();
        }else{
            JOptionPane.showConfirmDialog(null, "This function is only can use by decision manager, you are not allow to use this function due to the security problem", "Unsupport exception", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_menu_lable_8MouseClicked
    // Last Exit
    private void menu_lable_9MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menu_lable_9MouseClicked
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
        
    }//GEN-LAST:event_menu_lable_9MouseClicked

    private void menu_lable_8MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menu_lable_8MousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_menu_lable_8MousePressed

    private void input_today_dateOnCommit(datechooser.events.CommitEvent evt) {//GEN-FIRST:event_input_today_dateOnCommit
        this.setTodayDate(input_today_date.getSelectedDate().getTime());
    }//GEN-LAST:event_input_today_dateOnCommit

    private void input_today_dateOnSelectionChange(datechooser.events.SelectionChangedEvent evt) {//GEN-FIRST:event_input_today_dateOnSelectionChange
        this.setTodayDate(input_today_date.getSelectedDate().getTime());
    }//GEN-LAST:event_input_today_dateOnSelectionChange

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private datechooser.beans.DateChooserPanel input_today_date;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel label_today_date;
    private javax.swing.JLabel menu_lable_1;
    private javax.swing.JLabel menu_lable_2;
    private javax.swing.JLabel menu_lable_3;
    private javax.swing.JLabel menu_lable_4;
    private javax.swing.JLabel menu_lable_5;
    private javax.swing.JLabel menu_lable_6;
    private javax.swing.JLabel menu_lable_7;
    private javax.swing.JLabel menu_lable_8;
    private javax.swing.JLabel menu_lable_9;
    // End of variables declaration//GEN-END:variables

    private void frist_sign_up_console_version() {
        Staff frist_staff = new Staff();
        Scanner scanner = new Scanner(System.in);
        System.out.print("You request to keyin frist name: ");
        while(frist_staff.setFName(scanner.nextLine()) == false){
            System.out.println("Invalid input data");
            System.out.print("You request to keyin frist name: ");
        }
        System.out.print("You request to keyin last name: ");
        while(frist_staff.setFName(scanner.nextLine()) == false){
            System.out.println("Invalid input data");
            System.out.print("You request to keyin last name: ");
        }
        System.out.println("You position will be defualt as decesion level manager!");
        frist_staff.setLevel(StaffLevel.DECESION_MANAGER);
        System.out.print("You request to keyin gender (FEMALE, MALE): ");
        while(frist_staff.setGender(scanner.nextLine()) == false){
            System.out.println("Invalid input data");
            System.out.print("You request to keyin gender (FEMALE, MALE): ");
        }
        System.out.print("You request to keyin join date dd-mm-yyyy: ");
        while(frist_staff.setJoinDate(scanner.nextLine()) == false){
            System.out.println("Invalid input data");
            System.out.print("You request to keyin join date dd-mm-yyyy: ");
        }
        System.out.print("You request to keyin eamil: ");
        while(frist_staff.setEmail(scanner.nextLine()) == false){
            System.out.println("Invalid input data");
            System.out.print("You request to keyin email: ");
        }
        System.out.print("You request to keyin password: ");
        String temp_password = scanner.nextLine();
        System.out.print("You request to keyin the same password to confirm: ");
        while(frist_staff.setFristPassword(temp_password, scanner.nextLine()) == false){
            System.out.println("Invalid input data");
            System.out.print("You request to keyin password: ");
            temp_password = scanner.nextLine();
            System.out.print("You request to keyin the same password to confirm: ");
        }
        System.out.print("You request to keyin address: ");
        while(frist_staff.setAddress(scanner.nextLine()) == false){
            System.out.println("Invalid input data");
            System.out.print("You request to keyin address: ");
        }
        
        System.out.println("Done the process, you are our system user at now!!");
        System.out.println("Saving cahnging of the updated data, Logined to your account!!");
        
        frist_staff.write_this();
        duty_staff = new Staff(frist_staff.getUser_id());
    }

    private void log_out() {
        File xx = new File("resource/data/staff_id_logedin.txt");
        if(xx.isFile()){
            xx.delete();
        }
        this.dispose();
    }
    
}
