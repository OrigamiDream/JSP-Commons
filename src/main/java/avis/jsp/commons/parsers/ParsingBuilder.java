package avis.jsp.commons.parsers;

import avis.jsp.commons.utils.ValidatorUtils;
import com.google.common.base.Preconditions;

import javax.servlet.ServletRequest;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by Avis Network on 2018-05-16.
 */
public class ParsingBuilder {
    
    class Data<T> {
        
        ServletRequest request;
        String key = null;
        String encoding = null;
        T def = null;
        boolean defValue = false;
        Predicate<T> validator = null;
        Function<String, T> mapper = null;
        boolean plural = false;
        boolean atomic = false;
        
        boolean isValid() {
            if(request == null) {
                System.out.println("Invalid ServletRequest");
                return false;
            }
            
            if(ValidatorUtils.isInvalid(key)) {
                System.out.println("Invalid key");
                return false;
            }
            
            if(encoding != null && encoding.length() == 0) {
                System.out.println("Invalid encoding");
                return false;
            }
            return true;
        }
    
        @Override
        public String toString() {
            return "Data{key='" + key + "', encoding='" + encoding + "', default='" + def + "', plural='" + plural + "', atomic='" + atomic + "'}";
        }
    }
    
    private final ServletRequest request;
    private final Set<Data> dataSet = new HashSet<>();
    
    private Data node;
    
    public static ParsingBuilder builder(ServletRequest request) {
        return new ParsingBuilder(request);
    }
    
    private ParsingBuilder(ServletRequest request) {
        this.request = request;
    }
    
    // =======================
    // == Handle parameters ==
    // =======================
    
    public ParsingBuilder key(String key) {
        initData();
        
        node.key = key;
        return this;
    }
    
    public ParsingBuilder encoding(String encoding) {
        initData();
        
        node.encoding = encoding;
        return this;
    }
    
    public ParsingBuilder def(Object def) {
        initData();
        
        node.def = def;
        node.defValue = true;
        return this;
    }
    
    // =======================
    // == Handle validators ==
    // =======================
    
    public <T> ParsingBuilder validate(Predicate<T> validator, Class<T> type) {
        Preconditions.checkNotNull(validator, "validator");
        initData();
        
        node.validator = validator;
        return this;
    }
    
    public ParsingBuilder validate(Predicate<Object> validator) {
        Preconditions.checkNotNull(validator, "validator");
        initData();
        
        node.validator = validator;
        return this;
    }
    
    // ===================
    // == Handle mapper ==
    // ===================
    
    public ParsingBuilder map(Function<String, Object> mapper) {
        Preconditions.checkNotNull(mapper, "mapper");
        initData();
        
        node.mapper = mapper;
        return this;
    }
    
    public ParsingBuilder intMap() {
        initData();
        
        node.mapper = s -> Integer.parseInt(s.toString());
        return this;
    }
    
    public ParsingBuilder doubleMap() {
        initData();
        
        node.mapper = s -> Double.parseDouble(s.toString());
        return this;
    }
    
    public ParsingBuilder floatMap() {
        initData();
        
        node.mapper = s -> Float.parseFloat(s.toString());
        return this;
    }
    
    public ParsingBuilder longMap() {
        initData();
        
        node.mapper = s -> Long.parseLong(s.toString());
        return this;
    }
    
    public ParsingBuilder byteMap() {
        initData();
        
        node.mapper = s -> Byte.parseByte(s.toString());
        return this;
    }
    
    public ParsingBuilder booleanMap() {
        initData();
        
        node.mapper = s -> {
            switch(s.toString().toLowerCase()) {
                case "on":
                case "option1":
                case "true":
                    return true;
                    
                case "off":
                case "option2":
                case "false":
                    return false;
            }
            throw new IllegalArgumentException("Unknown boolean mapper key value: " + s.toString());
        };
        return this;
    }
    
    public ParsingBuilder atomic() {
        initData();
        
        node.atomic = true;
        return this;
    }
    
    public ParsingBuilder atomic(boolean atomic) {
        initData();
        
        node.atomic = atomic;
        return this;
    }
    
    public ParsingBuilder pack() {
        initData();
        
        dataSet.add(node);
        newData();
        return this;
    }
    
    public ParsingBuilder plurality() {
        initData();
        
        node.plural = true;
        return this;
    }
    
    public ParsingBuilder plurality(boolean plural) {
        initData();
        
        node.plural = plural;
        return this;
    }
    
    public Parser build() {
        return new Parser(dataSet);
    }
    
    private void initData() {
        if(node == null) {
            node = new Data();
            node.request = request;
        }
    }
    
    private void newData() {
        node = new Data();
        node.request = request;
    }
}
