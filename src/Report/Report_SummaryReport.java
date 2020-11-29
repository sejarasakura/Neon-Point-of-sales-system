package Report;


import Product.Product;
import Sql.WriteSql;


import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ITSUKA KOTORI
 */
public class Report_SummaryReport extends Report_Abstract{

    private String[] unsalesProduct;
    private Product mostPopular, lowerPurchase;
    private Product higher_outcome,lower_outcome;
    private Report_ExceptionReport exception_stagment;
    private ChartPanel panel_of_barchart;
    private JFreeChart histogram;
    
    public Report_SummaryReport(Date start_date, Date end_date) {
        super(start_date, end_date);
    }
   
    public String get_table(String report_id, String report_type, String report_title) {
        return super.get_table(report_id, report_type, report_title, new String[] {"", "", "", "", "", "", ""}).
                replace("<tr class='foo' style=\"border-bottom: 1px solid #000;border-top: 1px solid #000;\">", "<tr style=\"border-top: 1px solid #000;\">"); 
    }
    
    public void generateSummaryReport(){
        int count = 0;
        exception_stagment = new Report_ExceptionReport(start_date, end_date);
        exception_stagment.generate_exception_report(1000, "sum_of_product");
        unsalesProduct = new String[5];
        try{            
            try (Connection conn = DriverManager.getConnection("jdbc:ucanaccess://resource/data/Point_of_sales.mdb;"); Statement s = conn.createStatement()) {
                // get the zero sales product
                s.execute("SELECT TOP 5 DISTINCT O.product_id,SUM(O.UNIT_FINAL_PRICE * O.PRODUCT_QUANTITY) as total_outcome,"
                        + "SUM(O.PRODUCT_QUANTITY) as sum_of_product "
                        + "FROM Order_record_detail O, Payment Pay, Order_record OD "
                        + "WHERE OD.order_id = Pay.order_id "
                        + "AND OD.order_id = O.order_id "
                        + "AND payment_date "
                        + "BETWEEN #" + WriteSql.DATE_FORMAT_MS.format(start_date) 
                        + "# AND #" + WriteSql.DATE_FORMAT_MS.format(end_date) + "# "
                        + "GROUP BY product_id "
                        + "HAVING SUM(O.PRODUCT_QUANTITY) = 0 "
                        + "ORDER BY product_id DESC;");
                ResultSet result = s.getResultSet();
                while(result.next()){
                    unsalesProduct[count] = result.getString("product_id");
                    count++;
                }
                // get the top sold product
                s.execute("SELECT TOP 1 DISTINCT O.product_id,SUM(O.UNIT_FINAL_PRICE * O.PRODUCT_QUANTITY) as total_outcome,"
                        + "SUM(O.PRODUCT_QUANTITY) as sum_of_product "
                        + "FROM Order_record_detail O, Payment Pay, Order_record OD "
                        + "WHERE OD.order_id = Pay.order_id "
                        + "AND OD.order_id = O.order_id "
                        + "AND payment_date "
                        + "BETWEEN #" + WriteSql.DATE_FORMAT_MS.format(start_date) 
                        + "# AND #" + WriteSql.DATE_FORMAT_MS.format(end_date) + "# "
                        + "GROUP BY O.product_id "
                        + "ORDER BY sum_of_product DESC;");
                result = s.getResultSet();
                if(result.next()){
                    mostPopular = new Product(result.getString("product_id"));
                    mostPopular.setQuantity(result.getInt("sum_of_product"));
                }
                s.execute("SELECT TOP 1 DISTINCT O.product_id,SUM(O.UNIT_FINAL_PRICE * O.PRODUCT_QUANTITY) as total_outcome,"
                        + "SUM(O.PRODUCT_QUANTITY) as sum_of_product "
                        + "FROM Order_record_detail O, Payment Pay, Order_record OD "
                        + "WHERE OD.order_id = Pay.order_id "
                        + "AND OD.order_id = O.order_id "
                        + "AND payment_date "
                        + "BETWEEN #" + WriteSql.DATE_FORMAT_MS.format(start_date) 
                        + "# AND #" + WriteSql.DATE_FORMAT_MS.format(end_date) + "# "
                        + "GROUP BY O.product_id "
                        + "HAVING SUM(O.PRODUCT_QUANTITY) > 0 "
                        + "ORDER BY sum_of_product;");
                result = s.getResultSet();
                if(result.next()){
                    lowerPurchase = new Product(result.getString("product_id"));
                    lowerPurchase.setQuantity(result.getInt("sum_of_product"));
                }
                // get the top sold product
                s.execute("SELECT TOP 1 DISTINCT O.product_id,SUM(O.UNIT_FINAL_PRICE * O.PRODUCT_QUANTITY) as total_outcome,"
                        + "SUM(O.PRODUCT_QUANTITY) as sum_of_product "
                        + "FROM Order_record_detail O, Payment Pay, Order_record OD "
                        + "WHERE OD.order_id = Pay.order_id "
                        + "AND OD.order_id = O.order_id "
                        + "AND payment_date "
                        + "BETWEEN #" + WriteSql.DATE_FORMAT_MS.format(start_date) 
                        + "# AND #" + WriteSql.DATE_FORMAT_MS.format(end_date) + "# "
                        + "GROUP BY O.product_id "
                        + "ORDER BY total_outcome DESC;");
                result = s.getResultSet();
                if(result.next()){
                    higher_outcome = new Product(result.getString("product_id"));
                    higher_outcome.setRetial_price(result.getDouble("total_outcome"));
                }
                s.execute("SELECT TOP 1 DISTINCT O.product_id,SUM(O.UNIT_FINAL_PRICE * O.PRODUCT_QUANTITY) as total_outcome,"
                        + "SUM(O.PRODUCT_QUANTITY) as sum_of_product "
                        + "FROM Order_record_detail O, Payment Pay, Order_record OD "
                        + "WHERE OD.order_id = Pay.order_id "
                        + "AND OD.order_id = O.order_id "
                        + "AND payment_date "
                        + "BETWEEN #" + WriteSql.DATE_FORMAT_MS.format(start_date) 
                        + "# AND #" + WriteSql.DATE_FORMAT_MS.format(end_date) + "# "
                        + "GROUP BY O.product_id "
                        + "HAVING SUM(O.PRODUCT_QUANTITY) > 0 "
                        + "ORDER BY total_outcome;");
                result = s.getResultSet();
                if(result.next()){
                    lower_outcome = new Product(result.getString("product_id"));
                    lower_outcome.setRetial_price(result.getDouble("total_outcome"));
                }
                result.close();
                s.close();
                conn.close();
            }
        }
        catch(SQLException ex){
            Logger.getLogger(Report_ExceptionReport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private String writeRow(String name_title, String value_list){
        return "<tr style='border: 0;'>"
                + "<td colspan=2 style='border: 0;'>"+ name_title +"</td>"
                + "<td colspan=1 style='border: 0;'>:</td>"
                + "<td colspan=4 style='border: 0;'>" + value_list + "</td>"
            + "</tr>";
    }
    
    @Override
    protected String get_table_footer() {
        String temp = "<tr style='border: 0;border-top: 1px solid #000;'><td colspan=7 style='text-align: right;border: 0;'><i><p>Page 1 of 1</p></i></td></tr>";
        return temp;
    }
    
    @Override
    public void write_full_report() {
        report = get_table("PRT9430515", "Summary report", "Monthly report Analysis");
        String porduct_list="";
        for(String temp: unsalesProduct){
            if(temp != null)
                porduct_list += temp + ", ";
        }
        if(porduct_list.length() >= 2)
            porduct_list = porduct_list.substring(0, porduct_list.length() - 2);
        else
            porduct_list = "none";
        String content = writeRow("Unsales Product", porduct_list)
                + writeRow("Most popular product", this.mostPopular.getProduct_id() + ":" + mostPopular.getProduct_name())
                + writeRow("Lower purchase product", this.lowerPurchase.getProduct_id() + ":" + this.lowerPurchase.getProduct_name())
                + writeRow("Higher outcomeproduct", this.higher_outcome.getProduct_id() + ":" + this.higher_outcome.getProduct_name())
                + writeRow("Lower outcome product", this.lower_outcome.getProduct_id() + ":" + this.lower_outcome.getProduct_name())
                + writeRow("Total acture profit", String.format("%.2f", this.exception_stagment.getActual_profit()))
                + writeRow("Total sold profit", String.format("%.2f", this.exception_stagment.getSold_profit()))
                + writeRow("Total loss", String.format("%.2f", this.exception_stagment.getTotal_lost()))
                + writeRow("Total net profit", String.format("%.2f", this.exception_stagment.getNet_profit()));
        content += get_table_footer();
        report = report.replace("@content@", content);
    }
    
    public ChartPanel write_chart(){
        DefaultCategoryDataset dcd = new DefaultCategoryDataset();
        Report_ExceptionReport report = new Report_ExceptionReport(start_date, end_date);
        report.generate_exception_report(10, "sum_of_product");
        for(int i = 0; i < 10; i++)
            dcd.addValue(Integer.valueOf(report.exception_report[i][5]), report.exception_report[i][1] + " , " + report.exception_report[i][0], report.exception_report[i][0]);
        
        histogram = ChartFactory.createBarChart("Top 10 higher sales product", "Product ID", "Total Product Sold", dcd, PlotOrientation.HORIZONTAL, false, false, false);
        CategoryPlot plot = histogram.getCategoryPlot();
        plot.setRangeGridlinePaint(Color.GRAY);
        panel_of_barchart = new ChartPanel(histogram);
        panel_of_barchart.setSize(800, 400);
        return panel_of_barchart;
    }
}
