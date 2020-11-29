/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Report;
import Transaction.OrderDetail;
import User.Customer.Customer;
import Sql.WriteSql;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author ITSUKA KOTORI
 */

public class Report_DetailReport extends Report_Abstract{
	// cerete constructor 
        //  call write full report
        // get your reslut at getReport(); in html format
	public final static String ROW_OF_TABLE = 
                "<tr style='border: 0;'>"
                    + "<td style='border: 0;'>@order_id@</td>"
                    + "<td style='border: 0;'>@cust_name@</td>"
                    + "<td style='border: 0;'>@payment_date@</td>"
                    + "<td style='border: 0;'>@product_id@</td>"
                    + "<td style='border: 0;'>@unit_price@</td>"
                    + "<td style='border: 0;'>@quantity@</td>"
                    + "<td style='border: 0; text-align:right;'>@sub_total@</td>"
                + "</tr>";

	public final static String ROW_OF_TABLE_OTHER = 
                "<tr style='border: 0;'>"
                + "<td style='border: 0;' colspan=3></td>"
                + "<td style='border: 0;'>@product_id@</td>"
                + "<td style='border: 0;'>@unit_price@</td>"
                + "<td style='border: 0;'>@quantity@</td>"
                + "<td style='border: 0; text-align:right;'>@sub_total@</td>"
                + "</tr>";

	private String[][] order_record;
	private double grand_total = 0;

	public Report_DetailReport(Date start_date,Date end_date){
		super(start_date, end_date);
	}
		
	@Override
	public void write_full_report(){
		report = super.get_table("PRT9430513", "Detail Report", "Sales transaction report at August", new String[] {"Order ID", "Customer name", "Payment Date", "Product ID", "Unit price (RM) ", "Quantity", "Sub total (RM)"});
		String content = "";
		int count = 0;
		order_record = new String[WriteSql.getUniqueInt("SELECT COUNT(payment_id) FROM Payment WHERE payment_date "
						+ "BETWEEN #" + WriteSql.DATE_FORMAT_MS.format(start_date) 
						+ "# AND #" + WriteSql.DATE_FORMAT_MS.format(end_date) +"#;")][3];
		try	{
			try (Connection conn = DriverManager.getConnection("jdbc:ucanaccess://resource/data/Point_of_sales.mdb;"); Statement s = conn.createStatement()) {
				s.execute("SELECT PAY.payment_date,ORD.order_id,PAY.customer_id "
						+ "FROM Order_record ORD, Payment PAY "
						+ "WHERE PAY.order_id = ORD.order_id "
						+ "AND PAY.payment_date "
						+ "BETWEEN #" + WriteSql.DATE_FORMAT_MS.format(start_date) 
						+ "# AND #" + WriteSql.DATE_FORMAT_MS.format(end_date) + "# "
						+ "ORDER BY ORD.order_id;");
				ResultSet rs = s.getResultSet();
				while(rs.next()){
					order_record[count][0] = rs.getString("order_id");
					order_record[count][1] = rs.getString("customer_id");
					order_record[count][2] = WriteSql.DATE_FORMAT.format(rs.getDate("payment_date"));
					count++;
				}
				for(int i = 0; i < count; i++){
					s.execute("SELECT * FROM Order_record_detail WHERE Order_id='" + order_record[i][0] + "';");
					rs = s.getResultSet();
					content += get_table_row(i, rs);
				}
				rs.close();
				s.close();
				conn.close();
			}
		}
		catch(SQLException ex)
		{
                    System.out.println("SELECT PAY.payment_date, ORD.order_id, PAY.customer_id "
						+ "FROM Order_record ORD, Payment PAY "
						+ "WHERE PAY.order_id = ORD.order_id "
						+ "AND PAY.payment_date "
						+ "BETWEEN #" + WriteSql.DATE_FORMAT_MS.format(start_date) 
						+ "# AND #" + WriteSql.DATE_FORMAT_MS.format(end_date) + "# "
						+ "ORDER BY ORD.order_id;");
                    System.out.println(ex.getMessage()+":"+ex.getErrorCode());
		}
		content += get_table_footer();
		report = report.replace("@content@", content);
	}

	private String get_table_frist_row(OrderDetail order_detail, String payment_date, String customer_name){
		String temp = ROW_OF_TABLE;
		temp = temp.replace("@order_id@", order_detail.getOrder_id());
                if(customer_name == null || (customer_name).contains("null"))
                    temp = temp.replace("@cust_name@", "");
                else
                    temp = temp.replace("@cust_name@", customer_name);
		temp = temp.replace("@payment_date@", payment_date);
		temp = temp.replace("@product_id@", order_detail.getProduct_id());
		temp = temp.replace("@unit_price@", String.format("%.2f",order_detail.getUnit_price()));
		temp = temp.replace("@quantity@", String.format("%d",order_detail.getQuantity()));
		temp = temp.replace("@sub_total@", String.format("%.2f",order_detail.getUnit_price()));
		return temp;
	}

	private String get_table_other_row(OrderDetail order_detail){
		String temp = ROW_OF_TABLE_OTHER;
		temp = temp.replace("@product_id@", order_detail.getProduct_id());
		temp = temp.replace("@unit_price@", String.format("%.2f",order_detail.getUnit_price()));
		temp = temp.replace("@quantity@", String.format("%d",order_detail.getQuantity()));
		temp = temp.replace("@sub_total@", String.format("%.2f",order_detail.getUnit_price()));
		return temp;
	}

	private String get_table_sub_footer_row(Double total_price){
		String temp = "<tr style='border: 0;'>"
                        + "<td style='border-width:1px 0px 0px 0px;' colspan=5></td>"
                        + "<td style='border-width:1px 0 3px 0;'>Total (RM) : </td>"
                        + "<td style='border-width:1px 0 3px 0; width:140px; text-align:right;'>" + String.format("%.2f",total_price) + "</td>"
                        + "</tr><tr style='border: 0;'>"
                            + "<td colspan=7 style='color: white;border: 0;'></td>"
                        + "</tr>";
		return temp;
	}

	private String get_table_row(int index, ResultSet rs){
		String temp = "";
		OrderDetail order_detail = new OrderDetail();
		double total_price = 0;
            try {
                if(rs.next()){
                    order_detail.split(rs);
                    temp += get_table_frist_row(order_detail, order_record[index][2], new Customer(order_record[index][1]).getFullName());
                    total_price += order_detail.getUnit_price() * order_detail.getQuantity();	
                }
		while(rs.next()){
			order_detail.split(rs);
			temp += get_table_other_row(order_detail);
			total_price += order_detail.getUnit_price() * order_detail.getQuantity();
		}
		grand_total += total_price;
		temp += get_table_sub_footer_row(total_price);
            } catch (SQLException ex) {
                Logger.getLogger(Report_DetailReport.class.getName()).log(Level.SEVERE, null, ex);
            }
            return temp;
	}
	@Override
	protected String get_table_footer(){
		
		String temp = 
                        "<tr style='border: 0;'>"
                        + "<td style='border: 0;' colspan=5></td>"
                        + "<td colspan=1 style='border: 0;border-bottom: 3px solid #000;border-top: 3px solid #000;text-align: left'>Grand Total : </td>"
                        + "<td colspan=1 style='border: 0;border-bottom: 3px solid #000;border-top: 3px solid #000;text-align:right;'>@grand_total@</td>"
                        + "</tr><tr style='border: 0;'>"
                        + "<td style='border: 0;' colspan=7></td></tr>"
                        + "<tr style='border: 0;border-top: 1px solid #000;'>"
                        + "<td colspan=7 style='text-align: right;border: 0;'>"
                        + "<i><p>Page 1 of 1</p></i>"
                        + "</td></tr>";
		temp = temp.replace("@grand_total@", String.format("%.2f",grand_total));
		return temp;
	}

}