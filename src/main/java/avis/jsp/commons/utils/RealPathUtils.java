package avis.jsp.commons.utils;

import javax.servlet.ServletRequest;

/**
 * Created by Avis Network on 2018-05-11.
 */
public class RealPathUtils {
    
    public static String VALID_REAL_PATH = null;
    
    private RealPathUtils() {
    }
    
    public static void setRealPath(String realPath) {
        VALID_REAL_PATH = realPath;
    }
    
    public static void setRealPathValidated(String realPath) {
        if(realPath == null || VALID_REAL_PATH != null) {
            return;
        }
        
        VALID_REAL_PATH = realPath;
    }
    
    public static void setRealPathValidated(ServletRequest request) {
        if(request == null) {
            return;
        }
        setRealPathValidated(request.getServletContext().getRealPath("/"));
    }
    
    public static String getRealPath() {
        return VALID_REAL_PATH;
    }
    
}
