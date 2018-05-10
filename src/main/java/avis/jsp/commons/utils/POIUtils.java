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
            switch(cell.getCellTypeEnum()) {
                case FORMULA:
                    value = cell.getCellFormula();
                    break;
                
                case NUMERIC:
                    value = String.valueOf(cell.getNumericCellValue());
                    break;
                
                case STRING:
                    value = cell.getStringCellValue();
                    break;
                
                case BOOLEAN:
                    value = String.valueOf(cell.getBooleanCellValue());
                    break;
                
                case ERROR:
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
            switch(cell.getCellTypeEnum()) {
                case FORMULA:
                    value = cell.getCellFormula();
                    break;
        
                case NUMERIC:
                    value = String.valueOf(cell.getNumericCellValue());
                    break;
        
                case STRING:
                    value = cell.getStringCellValue();
                    break;
        
                case BOOLEAN:
                    value = String.valueOf(cell.getBooleanCellValue());
                    break;
        
                case ERROR:
                    value = String.valueOf(cell.getErrorCellValue());
                    break;
            }
        }
        return value;
    }
}
