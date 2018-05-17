package avis.jsp.commons.utils;

/**
 * Created by Avis Network on 2018-04-17.
 */
public interface Validator<T extends ValidationResult> {
    
    T validate();
    
}
