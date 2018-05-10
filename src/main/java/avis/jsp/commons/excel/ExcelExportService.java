package avis.jsp.commons.excel;

import avis.jsp.commons.excel.extensions.ExcelExportXLS;
import avis.jsp.commons.utils.RealPathUtils;
import com.google.common.collect.Table;
import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.server.ExportException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Avis Network on 2018-05-11.
 */
public class ExcelExportService {
    
    private final ServletRequest request;
    private final Map<Integer, ExcelDownloader> downloaders;
    
    private String sheetName;
    private ExcelExport excelExport;
    private String fileName;
    private String path;
    
    public ExcelExportService(ServletRequest request, Map<Integer, ExcelDownloader> downloaders) {
        this.request = request;
        this.downloaders = downloaders;
    }
    
    public void setExcelExport(ExcelExport excelExport) {
        this.excelExport = excelExport;
    }
    
    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public String writeExcel(int dlType) throws ServletException, IOException {
        ExcelDownloader downloader = downloaders.get(dlType);
        if(downloader == null) {
            throw new IllegalArgumentException("Unknown downloader ID: " + downloader);
        }
        
        ExcelDownloader excelDownloader = downloader.retrieveData(request);
        Table<Integer, Integer, String> table = downloader.createTable(excelDownloader);
        
        ExcelExport export = Optional.ofNullable(excelExport).orElse(new ExcelExportXLS());
        String sheet = Optional.ofNullable(sheetName).orElse("Excel1");
        
        Workbook workbook = export.fillWorkbookWithTable(sheet, table);
        
        RealPathUtils.setRealPathValidated(request);
        
        String name = Optional.ofNullable(fileName).orElseGet(() -> {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
            
            String extension = export.getExtension();
            if(!extension.startsWith(".")) {
                extension = "." + extension;
            }
            
            return format.format(new Date()) + extension;
        });
        
        String imPath = RealPathUtils.getRealPath();
        if(imPath == null) {
            throw new IllegalStateException("Failed to export data to " + export.getExtension().toUpperCase());
        }
        
        if(path == null) {
            throw new IllegalArgumentException("Path cannot be null.");
        }
        
        imPath += path;
        File file = new File(imPath);
        if(!file.exists()) {
            file.mkdirs();
        }
        
        try(FileOutputStream outputStream = new FileOutputStream(imPath + name)) {
            workbook.write(outputStream);
            workbook.close();
            
            System.out.println("Excel file has been generated.");
        } catch(ExportException e) {
            e.printStackTrace();
        }
        
        System.out.println("Generated excel file name : " + name);
        return name;
    }
}
