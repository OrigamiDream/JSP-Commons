package avis.jsp.commons.parsers;

import com.google.common.util.concurrent.AtomicDouble;
import com.sun.deploy.util.StringUtils;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.logging.Logger;

/**
 * Created by Avis Network on 2018-05-16.
 */
public class Parser {
    
    private static final boolean DEBUG = true;
    private static final Logger LOGGER = Logger.getLogger("Parser");
    
    private final Set<ParsingBuilder.Data> dataSet;
    private final Map<String, ParsingBuilder.Data> dataMap = new HashMap<>();
    
    Parser(Set<ParsingBuilder.Data> dataSet) {
        this.dataSet = dataSet;
        for(ParsingBuilder.Data data : dataSet) {
            if(!data.isValid()) {
                throw new IllegalArgumentException("Invalid dataSet has been found: " + data.toString());
            }
            dataMap.put(data.key, data);
        }
    }
    
    public <T> boolean anyFilterOut(Predicate<T> predicate) {
        return dataSet.stream().anyMatch(data -> predicate.test(get(data.key)));
    }
    
    public <T> boolean allFilterOut(Predicate<T> predicate) {
        return dataSet.stream().allMatch(data -> predicate.test(get(data.key)));
    }
    
    public <T> T get(String key) {
        debug("]=================[KEY:" + key + "]=================[");
        ParsingBuilder.Data data = dataMap.get(key);
        if(data == null) {
            debug("Invalid data by a key: " + key);
            debug("===================================================");
            return null;
        }
        
        List<String> init;
        if(data.plural) {
            String[] values = data.request.getParameterValues(key);
            if(values == null) {
                if(data.defValue) {
                    debug("ParameterValues is invalid. return default value: " + data.def);
                    debug("===================================================");
                    return (T) data.def;
                } else {
                    debug("ParavaterValues and default value are invalid. return empty ArrayList");
                    debug("===================================================");
                    return (T) new ArrayList<>();
                }
            }
            init = new ArrayList<>(Arrays.asList(values));
            debug(init.size() + " plural values are initialized");
        } else {
            init = Collections.singletonList(data.request.getParameter(key));
            debug("singular value [" + init + "] is initialized");
        }
        
        List<T> pluralVal = new ArrayList<>();
        T singularVal = null;
        try {
            for(String val : init) {
                String var = val;
                debug("Accepting a new variable: " + var);
                
                if(data.encoding != null) {
                    var = URLDecoder.decode(var, data.encoding);
                    debug("Decoding variable to " + data.encoding + ", and now: " + var);
                }
                
                T obj = (T) var;
                if(data.mapper != null) {
                    obj = (T) data.mapper.apply(var);
                    debug("Mapping variable to " + obj);
                }
                
                if(data.validator != null) {
                    debug("Testing validator...");
                    if(!data.validator.test(obj)) {
                        debug("Validator returns false");
                        if(data.defValue) {
                            obj = (T) data.def;
                            debug("Return default value because the validator returned false");
                        } else {
                            debug("Loop back because here is no default value");
                            continue;
                        }
                    }
                }
                
                if(data.atomic && obj != null) {
                    debug("Atomicalize enabled");
                    long time = System.currentTimeMillis();
                    if(obj.getClass() == Integer.class) {
                        obj = (T) new AtomicInteger((Integer) obj);
                    } else if(obj.getClass() == Double.class) {
                        obj = (T) new AtomicDouble((Double) obj);
                    } else if(obj.getClass() == Long.class) {
                        obj = (T) new AtomicLong((Long) obj);
                    } else if(obj.getClass() == Boolean.class) {
                        obj = (T) new AtomicBoolean((Boolean) obj);
                    } else {
                        obj = (T) new AtomicReference<T>(obj);
                    }
                    debug("Atomiclized done(" + (System.currentTimeMillis() - time) + "ms): " + obj.getClass().getSimpleName());
                }
                
                if(obj == null && data.defValue) {
                    obj = (T) data.def;
                }
                
                debug("Final value: " + obj);
                pluralVal.add(obj);
                singularVal = obj;
            }
        } catch(Exception e) {
            if(data.defValue) {
                debug("Exception has occurred: " + e.getMessage());
                debug("Returns default value: " + data.def);
                debug("===================================================");
                return (T) data.def;
            } else {
                debug("Exception has occurred, see console.");
                e.printStackTrace();
                debug("===================================================");
                return null;
            }
        }
        
        if(data.plural) {
            debug("The value is plural. values: " + StringUtils.join(pluralVal, ", "));
            debug("===================================================");
            return (T) pluralVal;
        } else {
            debug("The value is singular. value: " + singularVal);
            debug("===================================================");
            return singularVal;
        }
    }
    
    private static void debug(String debug) {
        if(DEBUG) {
            LOGGER.info(debug);
        }
    }
}
