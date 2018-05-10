package avis.jsp.commons.excel;

import com.google.common.collect.Table;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import java.io.IOException;

/**
 * Created by Avis Network on 2018-05-11.
 */
public interface ExcelDownloader<T extends ExcelDownloader> {

    Table<Integer, Integer, String> createTable(T downloader);
    
    T retrieveData(ServletRequest request) throws ServletException, IOException;

}
