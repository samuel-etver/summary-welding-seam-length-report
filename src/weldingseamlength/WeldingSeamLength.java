
package weldingseamlength;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;


public class WeldingSeamLength {
    public static void main(String[] args) {
        LocalDateTime forceDate = null;
        String[] tmpArgs = args;
         // {"2020-10"}
        if (tmpArgs.length > 0) {   
            forceDate = parseArgment(tmpArgs[0]);            
        }
                
        System.out.println("Welding Seam Length");
        System.out.println("Version 1.0.0");
        
        Common.init();
        Common.load();
        
        final LocalDateTime currDate = LocalDateTime.now();
        final FileScanner fileScanner = new FileScanner(Common.getArchivePath());
        final List<Stan> stanStatistics = fileScanner.scan(forceDate == null ? currDate : forceDate);        
        final Report report = new Report();
        final Path reportFilePath = getReportFilePath(currDate);
        report.generate(reportFilePath, stanStatistics);        
    }
    
    private static Path getReportFilePath(LocalDateTime dt) {
        final LocalDateTime currDt = LocalDateTime.now();
        final int currYear = currDt.getHour();
        final int currMonth = currDt.getMonthValue();
        final int currDay = currDt.getDayOfMonth();
        final int currHour = currDt.getHour();
        final int currMinute = currDt.getMinute();
        final int currSecond = currDt.getSecond();
        
        final int year = dt.getYear();
        final int month = dt.getMonthValue();
        
        final String reportFileName = 
          String.format("seam-length-%d-%02d (%d-%02d-%02d %02d-%02d-%02d).txt", 
          year, month,
          currYear, currMonth, currDay,
          currHour, currMinute, currSecond);                
        final Path reportFilePath = Paths.get(Common.getReportPath(), reportFileName);
        return reportFilePath;
    }    
    
    private static LocalDateTime parseArgment(String arg) {
        final String[] splited = arg.split("-");
        if (splited.length != 2) {
            return null;
        }
        
        int year;
        int month;
        
        try {
            year =  Integer.parseInt(splited[0]);
            month = Integer.parseInt(splited[1]);
        }
        catch(Exception ex) {
            return null;
        }
        
        if (year < 2000 || year > 3000) {
            return null;
        }
        if (month < 1 || month > 12) {
            return null;
        }
        
        return LocalDateTime.of(year, Month.of(month), 1, 0, 0);
    }    
}
