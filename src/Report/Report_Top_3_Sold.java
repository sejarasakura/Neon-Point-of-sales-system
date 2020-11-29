/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Report;
import java.util.Date;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;

/**
 *
 * @author ITSUKA KOTORI
 */
public class Report_Top_3_Sold extends Report_ExceptionReport{
    
    
    private ChartPanel panel_of_piechart;
    private JFreeChart pie_chart;

    public ChartPanel getPanel_of_piechart() {
        return panel_of_piechart;
    }

    public JFreeChart getPie_chart() {
        return pie_chart;
    }
    
    public Report_Top_3_Sold(Date start_date, Date end_date) {
        super(start_date, end_date);
    }
    
    public void generate_exception_report(){
        super.generate_exception_report(3, "sum_of_product");
    }
    
    
    @Override
    public void write_full_report() {
        report = get_table("PRT9430514", "Eception report", "Montly Top 3 Sold Report", new String[] {"Product ID", "Product name", "Puchase price ( RM )", "Unit Price ( RM )", "Total Sold Price ( RM )", "Quantity sold", "Actural total ( RM )"});
        String content = NEXT_ROW;
        for(int i = 0; i < this.exception_report.length; i++)
            content += super.writeTableRow(i);
        content += get_table_footer();
        report = report.replace("@content@", content);
    }

    @Override
    protected String get_table_footer() {
        String temp = "<tr style='border: 0;border-top: 1px solid #000;'><td colspan=7 style='text-align: right;border: 0;'><i><p>Page 2 of 3</p></i></td></tr>";
        return temp;
    }
    
    public ChartPanel write_chart(){
        DefaultPieDataset dcd = new DefaultPieDataset();
        for(int i = 0; i < 3; i++)
            dcd.setValue(super.exception_report[i][1] + " , " + super.exception_report[i][0], Double.valueOf(super.exception_report[i][5]));
        
        pie_chart = ChartFactory.createPieChart3D("Top Three Sold Product", dcd, false, false, false);
        PiePlot3D plot = (PiePlot3D)pie_chart.getPlot();
        plot.setForegroundAlpha(0.4f);
        plot.setCircular(true);
        panel_of_piechart = new ChartPanel(pie_chart);
        panel_of_piechart.setSize(800, 400);
        return panel_of_piechart;
    }
}
