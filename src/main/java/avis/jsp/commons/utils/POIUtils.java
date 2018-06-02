package avis.jsp.commons.utils;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCell;

/**
 * Created by Avis Network on 2018-05-11.
 */
public class POIUtils {
    
    private POIUtils() {
    }
    
    public static String readCell(HSSFCell cell) {
        String value = "";
        if(cell == null) {
            return null;
        } else {
            switch(cell.getCellType()) {
                case HSSFCell.CELL_TYPE_FORMULA:
                    value = cell.getCellFormula();
                    break;
        
                case HSSFCell.CELL_TYPE_NUMERIC:
                    value = String.valueOf(cell.getNumericCellValue());
                    break;
        
                case HSSFCell.CELL_TYPE_STRING:
                    value = cell.getStringCellValue();
                    break;
        
                case HSSFCell.CELL_TYPE_BOOLEAN:
                    value = String.valueOf(cell.getBooleanCellValue());
                    break;
        
                case HSSFCell.CELL_TYPE_ERROR:
                    value = String.valueOf(cell.getErrorCellValue());
                    break;
            }
        }
        return value;
    }
    
    public static String readCell(XSSFCell cell) {
        String value = "";
        if(cell == null) {
            return null;
        } else {
            switch(cell.getCellType()) {
                case XSSFCell.CELL_TYPE_FORMULA:
                    value = cell.getCellFormula();
                    break;
                    
                case XSSFCell.CELL_TYPE_NUMERIC:
                    value = String.valueOf(cell.getNumericCellValue());
                    break;
                    
                case XSSFCell.CELL_TYPE_STRING:
                    value = cell.getStringCellValue();
                    break;
                    
                case XSSFCell.CELL_TYPE_BOOLEAN:
                    value = String.valueOf(cell.getBooleanCellValue());
                    break;
                    
                case XSSFCell.CELL_TYPE_ERROR:
                    value = String.valueOf(cell.getErrorCellValue());
                    break;
            }
        }
        return value;
    }
}
