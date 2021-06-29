package weldingseamlength;

import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class Report {
    void generate(Path filePath, List<Stan> stanStatistics) {
        final StringBuilder stringBuilder = new StringBuilder();
        
        final Comparator<Stan> stanComparator = new Comparator<Stan>() {
            @Override
            public int compare(Stan o1, Stan o2) {
                return Integer.compare(o1.getStanNumber(), o2.getStanNumber());
            }
        };
        
        final List<Stan> stan800Statistics = stanStatistics
                .stream()
                .filter(stan -> stan.getStanDiameter() == StanDiameter.STAN_800)
                .collect(Collectors.toList());
        final List<Stan> stan1000Statistics = stanStatistics
                .stream()
                .filter(stan -> stan.getStanDiameter() == StanDiameter.STAN_1000)
                .collect(Collectors.toList());
        
        final List<Stan> stan800IDStatistics = stan800Statistics
                .stream()
                .filter(stan -> stan.getStanType() == StanType.ID)
                .collect(Collectors.toList());                
        final List<Stan> stan800ODStatistics = stan800Statistics
                .stream()
                .filter(stan -> stan.getStanType() == StanType.OD)
                .collect(Collectors.toList());
        
        final List<Stan> stan1000IDStatistics = stan1000Statistics
                .stream()
                .filter(stan -> stan.getStanType() == StanType.ID)
                .collect(Collectors.toList());
        final List<Stan> stan1000ODStatistics = stan1000Statistics
                .stream()
                .filter(stan -> stan.getStanType() == StanType.OD)
                .collect(Collectors.toList());
        stan800IDStatistics.sort(stanComparator);
        stan800ODStatistics.sort(stanComparator);
        stan1000IDStatistics.sort(stanComparator);
        stan1000ODStatistics.sort(stanComparator);
                
        double summarySeamLength = 0.0;
        for (Stan stan: stanStatistics) {
            summarySeamLength += stan.getSeamLength();
        }
        
        stringBuilder.append(String.format("Суммарная длина: %.1f м\n", summarySeamLength));
        
        final String outputPattern = "%d. %s стан %d: %.1f м\n";
        
        stringBuilder.append(generateReportTextForStans(stan800IDStatistics, outputPattern));
        stringBuilder.append(generateReportTextForStans(stan800ODStatistics, outputPattern));
        stringBuilder.append(generateReportTextForStans(stan1000IDStatistics, outputPattern));
        stringBuilder.append(generateReportTextForStans(stan1000ODStatistics, outputPattern));
        
        final String textReport = stringBuilder.toString();
        
        try (FileOutputStream reportFileStream = new FileOutputStream(filePath.toFile())) {
            reportFileStream.write(textReport.getBytes("cp1251"));
        }
        catch(Exception ex) {            
            System.out.println(ex);
        }        
        
        System.out.println(textReport);
    }    
    
    
    private String generateReportTextForStans(List<Stan> stanStatistics, String pattern) {
        final StringBuilder stringBuilder = new StringBuilder();
        
        stanStatistics.forEach(stan -> {
            String stanTypeStr;
            switch (stan.getStanType()) {
                case ID: stanTypeStr = "Внутренний"; break;
                case OD: stanTypeStr = "Наружный";   break;
                default: stanTypeStr = "";
            }
            stringBuilder.append(String.format(
                pattern, 
                stan.getStanDiameter().getDiameter(),
                stanTypeStr,    
                stan.getStanNumber(),
                stan.getSeamLength()));
        });
        
        return stringBuilder.toString();
    }
}
