package avis.jsp.commons.utils;

import java.util.function.Predicate;

/**
 * Created by Avis Network on 2018-04-17.
 */
public class ValidatorUtils {
    
    public static final Predicate<String> STRING_VALIDATOR = s -> s != null && s.length() > 0;
    
    private ValidatorUtils() {
    }
    
    public static boolean isInvalid(ValidationResult result) {
        return result != null && !(result instanceof SuccessResult);
    }
    
    public static boolean isValid(ValidationResult result) {
        return !isInvalid(result);
    }
    
    public static boolean isValid(String str) {
        return STRING_VALIDATOR.test(str);
    }
    
    public static boolean isInvalid(String str) {
        return !isValid(str);
    }
    
}
