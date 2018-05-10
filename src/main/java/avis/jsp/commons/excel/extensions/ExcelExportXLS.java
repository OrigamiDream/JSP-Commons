package avis.jsp.commons.excel.extensions;

import avis.jsp.commons.excel.ExcelExport;
import com.google.common.collect.Table;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.Map;

/**
 * Created by Avis Network on 2018-05-11.
 */
public class ExcelExportXLS implements ExcelExport {
    
    public Workbook fillWorkbookWithTable(String sheetName, Table<Integer, Integer, String> table) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(sheetName);
    
        for(int rowNum = 0; rowNum < table.rowKeySet().size(); rowNum++) {
            HSSFRow row = sheet.createRow(rowNum);
            Map<Integer, String> colMap = table.row(rowNum);
        
            for(int colNum = 0; colNum < colMap.size(); colNum++) {
                HSSFCell cell = row.createCell(colNum);
            
                cell.setCellValue(colMap.get(colNum));
            }
        }
        return workbook;
    }
    
    public String getExtension() {
        return ".xls";
    }
}
