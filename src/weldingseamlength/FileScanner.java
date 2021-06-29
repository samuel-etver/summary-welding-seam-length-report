
package weldingseamlength;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

public class FileScanner {
    private final static Map<StanDiameter, String> s_StanDiametersFolders;
    
    static {
        final HashMap<StanDiameter, String> diametersFolders = new HashMap<>();
        
        final StanDiameter[] allDiameters = {
            StanDiameter.STAN_800,
            StanDiameter.STAN_1000
        };
        for(StanDiameter currDiameter: allDiameters) {
            diametersFolders.put(currDiameter, 
                            Integer.toString(currDiameter.getDiameter()));
        }
        
        s_StanDiametersFolders = diametersFolders;
    }
    
    
    private final static Map<StanType, String> s_StanTypeFolders;
    
    static {
        final HashMap<StanType, String> typeFolders = new HashMap<>();
        
        final StanType[] allTypes = {
            StanType.ID,
            StanType.OD
        };
        for(StanType currType: allTypes) {
            typeFolders.put(currType, 
                            currType.getType());
        }
        
        s_StanTypeFolders = typeFolders;
    }
    
    private final String m_rootPath;
    
    
    public FileScanner(String rootPath) {
        m_rootPath = rootPath;
    }
    
    
    public List<Stan> scan(LocalDateTime dt) {
        final ArrayList<Stan> stanStatistics = new ArrayList<>();
        
        final String yearFolderName = Integer.toString(dt.getYear());
        final String monthFolderName = Integer.toString(dt.getMonthValue());
        
        s_StanDiametersFolders.forEach((stanDiameter, folderName) -> {
            try {
                Path stanDiameterPath = Paths.get(m_rootPath, folderName);
                
                List<String> stanTypeFolders = Files.list(stanDiameterPath)
                        .filter(Files::isDirectory)
                        .filter(path -> {
                            final String fileName = path.getFileName().toString();
                            return s_StanTypeFolders
                                    .values()
                                    .stream()
                                    .anyMatch(prefix -> fileName.startsWith(prefix));
                        })
                        .map(path -> path.getFileName().toString())
                        .collect(Collectors.toList());
                
                stanTypeFolders.forEach(stanTypeFolder -> {
                    Path filePath = Paths.get(
                        stanDiameterPath.toString(),
                        stanTypeFolder,
                        yearFolderName,
                        monthFolderName
                    );
                    List<Path> fileList = null;
                    try {                        
                        fileList = Files.list(filePath)
                                .filter(Files::isRegularFile)
                                .filter(path -> path.toString().toLowerCase().endsWith(".xls"))
                                .collect(Collectors.toList());
                    }
                    catch(Exception ex) {     
                    }
                    if (fileList == null) {
                        return;
                    }
                    
                    final Stan.Builder stanBuilder = new Stan.Builder();
                    stanBuilder.stanDiameter = stanDiameter;
                    stanBuilder.seamLength = getSeamLength(fileList);
                    stanBuilder.stanType = Arrays.asList(StanType.values())
                        .stream()
                        .filter(stanType -> {
                            final String prefix = stanType.getType();
                            return (prefix != null && prefix.length() > 0)
                                    ? stanTypeFolder.startsWith(stanType.getType())
                                    : false;
                         })
                        .findFirst()
                        .get();
                    stanBuilder.stanNumber = parseStanNumber(stanTypeFolder);
                    final Stan stan = stanBuilder.build();
                    if (stan != null) {
                        stanStatistics.add(stan);
                    }
                });
            }
            catch(Exception ex) {     
            }            
        });
        
        return stanStatistics;
    }
    
    
    private double getSeamLength(List<Path> fileList) {        
        class Seam {
            double length;
            int count;
        };
        final Seam seam = new Seam();  
        
        final ExecutorService executor = Executors.newFixedThreadPool(10);
        final ArrayList<Future<Double>> results = new ArrayList<>();
        fileList.forEach(filePath -> {
            Callable<Double> task = () -> {
                final Double seamLength = getSeamLength(filePath);
                println(filePath.toString());
                return seamLength;
            };
            final Future<Double> oneResult = executor.submit(task);
            results.add(oneResult);
        });
        
        results.forEach(currResult -> {
            try {
                seam.length += currResult.get();
                seam.count++;
            }
            catch(Exception ex) {                
            }
        });
        
        executor.shutdown();
        
        return seam.length;
    }
    
    
    private double getSeamLength(Path filePath) {
        double seamLength = 0.0;
        try (FileInputStream inputStream = new FileInputStream(filePath.toFile())) {
            try (final HSSFWorkbook workbook = new HSSFWorkbook(inputStream)) {
                final HSSFSheet sheet = workbook.getSheet("Data");
                seamLength = getSeamLength(sheet);
            }
        }
        catch(Exception ex) {            
        }
        return seamLength;
    }
    
    
    private double getSeamLength(HSSFSheet sheet) {
        int range = 1000;
        int row = range;
        
        if (isCellEmpty(sheet, 0)) {
            return 0;
        }
        
        while (range > 0) {
            if (isCellEmpty(sheet, row)) {
                row -= range;
                range /= 2;
            }
            else {
                row += range;
            }
        }
        
        return row * 0.006;
    }
    
    
    private boolean isCellEmpty(HSSFSheet sheet, int row) {
        return isCellEmpty(sheet, row, 0);
    }
    
    
    private boolean isCellEmpty(HSSFSheet sheet, int row, int col) {
        final String cell = Character.toString((char)(col + 'A')) +
                            Integer.toString(row + 1);
        final CellReference cr = new CellReference(cell);
        final Row rowObj = sheet.getRow(cr.getRow());
        if (rowObj == null) {
            return true;
        }
        final Cell cellObj = rowObj.getCell(cr.getCol());
        if (cellObj == null ||
            cellObj.getCellTypeEnum() == CellType.BLANK) {
            return true;
        }
        
        return false;
    }
    
    
    private Integer parseStanNumber(String txt) {
        Integer value = null;
        try {
            value = Integer.parseInt(txt.replaceAll("[A-Za-z]", ""));
        }
        catch(Exception ex) {            
        }
        return value;
    }
    
    
    private synchronized void println(String text) {
        System.out.println(text);
    }
}
