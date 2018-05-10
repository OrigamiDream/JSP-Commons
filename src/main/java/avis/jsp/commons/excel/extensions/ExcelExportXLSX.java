package avis.jsp.commons.excel.extensions;

import avis.jsp.commons.excel.ExcelExport;
import com.google.common.collect.Table;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.Map;

/**
 * Created by Avis Network on 2018-05-11.
 */
public class ExcelExportXLSX implements ExcelExport {
    
    public Workbook fillWorkbookWithTable(String sheetName, Table<Integer, Integer, String> table) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(sheetName);
    
        for(int rowNum = 0; rowNum < table.rowKeySet().size(); rowNum++) {
            XSSFRow row = sheet.createRow(rowNum);
            Map<Integer, String> colMap = table.row(rowNum);
        
            for(int colNum = 0; colNum < colMap.size(); colNum++) {
                XSSFCell cell = row.createCell(colNum);
            
                cell.setCellValue(colMap.get(colNum));
            }
        }
        return workbook;
    }
    
    public String getExtension() {
        return ".xlsx";
    }
}
