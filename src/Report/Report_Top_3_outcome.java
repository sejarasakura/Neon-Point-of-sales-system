/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Report;

import java.awt.Color;
import java.util.Date;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
/**
 *
 * @author ITSUKA KOTORI
 */
public class Report_Top_3_outcome  extends Report_ExceptionReport{
    
    private ChartPanel panel_of_barchart;
    private JFreeChart histogram;

    public ChartPanel getPanel_of_barchart() {
        return panel_of_barchart;
    }

    public JFreeChart getHistogram() {
        return histogram;
    }
    
    public Report_Top_3_outcome(Date start_date, Date end_date) {
        super(start_date, end_date);
    }
    
    public void generate_exception_report(){
        super.generate_exception_report(3, "total_outcome");
    }
    
    @Override
    public void write_full_report() {
        report = get_table("PRT9430514", "Eception report", "Montly Top 3 Outcome Report", new String[] {"Product ID", "Product name", "Puchase price ( RM )", "Unit Price ( RM )", "Total Sold Price ( RM )", "Quantity sold", "Actural total ( RM )"});
        String content = NEXT_ROW;
        for(int i = 0; i < this.exception_report.length; i++)
            content += super.writeTableRow(i);
        content += get_table_footer();
        report = report.replace("@content@", content);
    }

    @Override
    protected String get_table_footer() {
        String temp = "<tr style='border: 0;border-top: 1px solid #000;'><td colspan=7 style='text-align: right;border: 0;'><i><p>Page 3 of 3</p></i></td></tr>";
        return temp;
    }
    
    public ChartPanel write_chart(){
        DefaultCategoryDataset dcd = new DefaultCategoryDataset();
        for(int i = 0; i < 3; i++)
            dcd.addValue(Double.valueOf(super.exception_report[i][6]), super.exception_report[i][1] + " , " + super.exception_report[i][0], super.exception_report[i][0]);
        
        histogram = ChartFactory.createBarChart("Top Three Outcome Product", "Total Outcome", "Product Name and ID", dcd, PlotOrientation.VERTICAL, false, false, false);
        CategoryPlot plot = histogram.getCategoryPlot();
        plot.setRangeGridlinePaint(Color.GRAY);
        panel_of_barchart = new ChartPanel(histogram);
        panel_of_barchart.setSize(800, 400);
        return panel_of_barchart;
    }
}
