package Report;

import Transaction.OrderDetail;
import Product.Product;
import Sql.WriteSql;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ITSUKA KOTORI
 */
public class Report_ExceptionReport extends Report_Abstract{

    // frist create new object
    // call generate_exception_report
    // call write_full_report()
    // get your result at getReport(); in html format
    protected String[][] exception_report;
    private double actual_profit = 0.00;
    private double sold_profit = 0.00;
    private double total_lost = 0.00;
    private double net_profit = 0.00;
    private double total_purchase_price = 0.00;

    public Report_ExceptionReport(Date start_date, Date end_date) {
        super(start_date, end_date);
    }

    public double getActual_profit() {
        return actual_profit;
    }

    public double getSold_profit() {
        return sold_profit;
    }

    public double getTotal_lost() {
        return total_lost;
    }

    public double getNet_profit() {
        return net_profit;
    }

    public double getTotal_purchase_price() {
        return total_purchase_price;
    }
            
    public void generate_exception_report(int top_n_product, String order_by){
        Product product;
        OrderDetail[] product_sold;
        int product_count, count;
        try{            
            try (Connection conn = DriverManager.getConnection("jdbc:ucanaccess://resource/data/Point_of_sales.mdb;"); Statement s = conn.createStatement()) {
                ResultSet result = s.getResultSet(), order_result;
                product_sold = new OrderDetail[top_n_product];
                s.execute("SELECT TOP " + top_n_product + " DISTINCT O.product_id,SUM(O.UNIT_FINAL_PRICE * O.PRODUCT_QUANTITY) as total_outcome,"
                        + "SUM(O.PRODUCT_QUANTITY) as sum_of_product "
                        + "FROM Order_record_detail O, Payment Pay, Order_record OD "
                        + "WHERE OD.order_id = Pay.order_id "
                        + "AND OD.order_id = O.order_id "
                        + "AND payment_date "
                        + "BETWEEN #" + WriteSql.DATE_FORMAT_MS.format(start_date) 
                        + "# AND #" + WriteSql.DATE_FORMAT_MS.format(end_date) + "# "
                        + "GROUP BY product_id "
                        + "ORDER BY " + order_by + " DESC;");
                result = s.getResultSet();
                count = 0;
                while(result.next()){
                    product_sold[count] = new OrderDetail();
                    product_sold[count].setQuantity(result.getInt("sum_of_product"));
                    product_sold[count].setUnit_price(result.getDouble("total_outcome"));
                    product_sold[count].setProduct_id(result.getString("product_id"));
                    count++;
                }
                exception_report = new String[top_n_product][7];
                for(int i = 0; i < count; i++){
                    product = new Product();
                    s.execute("SELECT * FROM Product WHERE product_id='" + product_sold[i].getProduct_id() + "';");
                    result = s.getResultSet();
                    result.next();
                    product.split(result);
                    exception_report[i][0] = product_sold[i].getProduct_id();
                    exception_report[i][1] = product.getProduct_name();
                    exception_report[i][2] = String.format("%.2f", product.getPuchase_price());
                    exception_report[i][3] = String.format("%.2f", product.getRetial_price());
                    exception_report[i][4] = String.format("%.2f", product_sold[i].getUnit_price());
                    exception_report[i][5] = String.format("%d", product_sold[i].getQuantity());
                    exception_report[i][6] = String.format("%.2f", product.getRetial_price()*product_sold[i].getQuantity());
                    this.actual_profit +=  (double)product_sold[i].getQuantity() * product.getRetial_price();
                    this.sold_profit += product_sold[i].getUnit_price();
                    this.total_purchase_price += product.getPuchase_price() * product_sold[i].getQuantity();
                }
                result.close();
                s.close();
                conn.close();
            }
            total_lost = actual_profit - sold_profit;
            net_profit = sold_profit - total_purchase_price;
        }
        catch(SQLException ex){
            Logger.getLogger(Report_ExceptionReport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String[][] getException_report() {
        return exception_report;
    }

    public void setException_report(String[][] exception_report) {
        this.exception_report = exception_report;
    }
    
    public String[] getTableRow(int rowNum){
        return this.exception_report[rowNum];
    }
    
    public void setTableRow(int rowNum, String[] row){
        this.exception_report[rowNum] = row;
    }
    
    public String getCell(int row, int col){
        return this.exception_report[row][col];
    }
    
    public void setCell(int row, int col, String cellData){
        this.exception_report[row][col] = cellData;
    }

    protected String writeTableRow(int row){
        if(exception_report[row][0] != null)
        return(
            "<tr style='border:0px;'>"
		+ "<td style='border:0px;'>" + exception_report[row][0] + "</td>"
                + "<td style='border:0px;'>" + exception_report[row][1] + "</td>"
                + "<td style='border:0px;'>" + exception_report[row][2] + "</td>"
                + "<td style='border:0px;'>" + exception_report[row][3] + "</td>"
                + "<td style='border:0px;'>" + exception_report[row][4] + "</td>"
                + "<td style='border:0px;'>" + exception_report[row][5] + "</td>"
                + "<td style='border:0px;text-align: right;'>" + exception_report[row][6] + "</td>"
          + "</tr>" + Report_Abstract.NEXT_ROW);
        else
            return "";
    }
    
    @Override
    public void write_full_report() {
        report = get_table("PRT9430514", "Eception report", "Montly Top Sales Report", new String[] {"Product ID", "Product name", "Puchase price ( RM )", "Unit Price ( RM )", "Total Sold Price ( RM )", "Quantity sold", "Actural total ( RM )"});
        String content = NEXT_ROW;
        for(int i = 0; i < this.exception_report.length; i++)
            content += writeTableRow(i);
        content += get_table_footer();
        report = report.replace("@content@", content);
    }

    @Override
    protected String get_table_footer() {
        String temp = "<tr style='border: 0;'><td style='border: 0;' colspan=5></td><td colspan=1 style='border: 0;border-top: 3px solid #000;text-align: left'>Total Actual profit : </td><td colspan=1 style='border: 0;border-top: 3px solid #000;text-align: right'>" + String.format("%.2f", this.actual_profit) + "</td></tr>"
        + "<tr style='border: 0;'><td style='border: 0;' colspan=5></td><td colspan=1 style='border: 0;text-align: left'>Total Sold profit : </td><td colspan=1 style='border: 0;text-align: right'>" + String.format("%.2f", this.sold_profit) + "</td></tr>"
        + "<tr style='border: 0;'><td style='border: 0;' colspan=5></td><td colspan=1 style='border: 0;text-align: left'>Total lost : </td><td colspan=1 style='border: 0;text-align: right'>" + String.format("%.2f", this.total_lost) + "</td></tr>"
        + "<tr style='border: 0;'><td style='border: 0;' colspan=5></td><td colspan=1 style='border: 0;border-bottom: 3px solid #000;text-align: left'>Total net profit : </td><td colspan=1 style='border: 0;border-bottom: 3px solid #000;text-align: right;'>" + String.format("%.2f", this.net_profit) + "</td></tr>"
        + NEXT_ROW + "<tr style='border: 0;border-top: 1px solid #000;'><td colspan=7 style='text-align: right;border: 0;'><i><p>Page 1 of 3</p></i></td></tr>";
        return temp;
    }
    
}
