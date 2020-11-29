/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Report;
import Main.Main;
import Sql.WriteSql;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ITSUKA KOTORI
 */
public abstract class Report_Abstract {
   
    protected Date start_date,end_date;
    protected String report = "";
    public static String NEXT_ROW = "<tr style='border:0px;margin:0;'><td colspan=7 style='color: white; margin:0; border:0px;'></td></tr>";

    public Report_Abstract(Date start_date,Date end_date){
        this.start_date = start_date;
        this.end_date = end_date;
    }

    public Date getStart_date() {
        return start_date;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }
    
    public abstract void write_full_report();
    protected abstract String get_table_footer();
    
    public String get_table(String report_id, String report_type, String report_title, String[] header){
        String result = "";
        try {
            result = new String(Files.readAllBytes(Paths.get("resource/html/java-sales-report.html")),StandardCharsets.UTF_8);
        } catch (IOException ex) {
            Logger.getLogger(Report_Abstract.class.getName()).log(Level.SEVERE, null, ex);
        }
        result = result.replace("@des_date@", WriteSql.DATE_FORMAT.format(start_date) + " - " + WriteSql.DATE_FORMAT.format(end_date));
        result = result.replace("@report_id@", report_id);
        result = result.replace("@today_date@", Main.DATE_FORMAT.format(new Date()));
        result = result.replace("@report_title@", report_title);
        result = result.replace("@report_type@", report_type);
        for(int i = 1; i <= 7; i++){
            result = result.replace("@heading_" + i + "@", header[i - 1]);
        }
        return result;
    }
}
