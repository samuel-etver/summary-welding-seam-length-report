package weldingseamlength;

import java.io.File;
import java.nio.file.Paths;


public class Common {
    public static final String APP_NAME = "WeldingSeamLength";
    private static final String CFG_FILE_NAME = APP_NAME + ".config";

    private static String s_AppPath;
    private static String s_ArchivePath;    
    private static String s_ReportPath;
    
    public static void init() {
        s_AppPath = new File("").getAbsolutePath();        
    }
    
    
    public static void load() {
        final CfgFile cfg = new CfgFile();
    
        cfg.load(Paths.get(s_AppPath, CFG_FILE_NAME).toAbsolutePath().toString());
        
        s_ArchivePath = cfg.read("ArchivePath", "");
        if(!new File(s_ArchivePath).isDirectory()) {
            System.out.println(String.format("Archive path was not found [%s].", 
                    s_ArchivePath));
            System.exit(0);
        }        
        
        s_ReportPath = cfg.read("ReportPath", "");
        if (!new File(s_ReportPath).isDirectory()) {
            System.out.println(String.format("Report path was not found [%s].",
                    s_ReportPath));
            System.exit(0);
        }
    }
  

    public static String getAppPath() {
        return s_AppPath;
    }
  

    public static String getArchivePath() {
        return s_ArchivePath;
    }    
    
    
    public static String getReportPath () {
        return s_ReportPath;
    }
}
