package avis.jsp.commons.excel;

import com.google.common.collect.Table;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Created by Avis Network on 2018-05-11.
 */
public interface ExcelExport {
    
    Workbook fillWorkbookWithTable(String sheetName, Table<Integer, Integer, String> table);
    
    String getExtension();
    
}
