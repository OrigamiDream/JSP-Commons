package avis.jsp.commons.utils;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Avis Network on 2018-05-11.
 */
public class ResultSetUtils {
    
    private ResultSetUtils() {
    }
    
    // =====================
    // == Global handlers ==
    // =====================
    
    public static <T extends Number> T getNumber(ResultSet resultSet, String columnName, T def, Class<T> type) throws SQLException {
        T value = (T) resultSet.getObject(columnName);
        return value != null ? value : def;
    }
    
    public static <T extends Number> T getNumber(ResultSet resultSet, int columnIndex, T def, Class<T> type) throws SQLException {
        T value = (T) resultSet.getObject(columnIndex);
        return value != null ? value : def;
    }
}
