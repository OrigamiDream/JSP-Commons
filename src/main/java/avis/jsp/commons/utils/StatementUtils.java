package avis.jsp.commons.utils;

import com.google.common.collect.ImmutableMap;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.AbstractMap;
import java.util.Map;

/**
 * Created by Avis Network on 2018-05-11.
 */
public class StatementUtils {
    
    @FunctionalInterface
    private interface StatementPredicate<T> {
        
        void run(PreparedStatement statement, int index, T value) throws SQLException;
        
    }
    
    private static final ImmutableMap<Class<? extends Number>, Map.Entry<Integer, StatementPredicate<Number>>> COLUMN_TYPE_REGISTRY = ImmutableMap.<Class<? extends Number>, Map.Entry<Integer, StatementPredicate<Number>>>builder()
            .put(Integer.class, new AbstractMap.SimpleEntry<>(Types.INTEGER, (statement, index, value) -> {
                statement.setInt(index, (Integer) value);
            }))
            .put(Double.class, new AbstractMap.SimpleEntry<>(Types.DOUBLE, (statement, index, value) -> {
                statement.setDouble(index, (Double) value);
            }))
            .put(Float.class, new AbstractMap.SimpleEntry<>(Types.FLOAT, (statement, index, value) -> {
                statement.setFloat(index, (Float) value);
            }))
            .put(Long.class, new AbstractMap.SimpleEntry<>(Types.BIGINT, (statement, index, value) -> {
                statement.setLong(index, (Long) value);
            }))
            .put(Byte.class, new AbstractMap.SimpleEntry<>(Types.TINYINT, (statement, index, value) -> {
                statement.setByte(index, (Byte) value);
            }))
            .put(Short.class, new AbstractMap.SimpleEntry<>(Types.SMALLINT, (statement, index, value) -> {
                statement.setShort(index, (Short) value);
            }))
            .build();
    
    private StatementUtils() {
    }
    
    public static void setInt(PreparedStatement statement, int index, Integer value) throws SQLException {
        setNumber(statement, index, value, Integer.class);
    }
    
    public static void setDouble(PreparedStatement statement, int index, Double value) throws SQLException {
        setNumber(statement, index, value, Double.class);
    }
    
    public static void setFloat(PreparedStatement statement, int index, Float value) throws SQLException {
        setNumber(statement, index, value, Float.class);
    }
    
    public static void setLong(PreparedStatement statement, int index, Long value) throws SQLException {
        setNumber(statement, index, value, Long.class);
    }
    
    public static void setByte(PreparedStatement statement, int index, Byte value) throws SQLException {
        setNumber(statement, index, value, Byte.class);
    }
    
    public static void setShort(PreparedStatement statement, int index, Short value) throws SQLException {
        setNumber(statement, index, value, Short.class);
    }
    
    public static <T extends Number> void setNumber(PreparedStatement statement, int index, T t, Class<T> type) throws SQLException {
        if(!COLUMN_TYPE_REGISTRY.containsKey(type)) {
            throw new IllegalArgumentException("Opposite column type of '" + type.getSimpleName() + "' doesn't exist!");
        }
    
        Map.Entry<Integer, StatementPredicate<Number>> entry = COLUMN_TYPE_REGISTRY.get(type);
        if(t == null) {
            statement.setNull(index, entry.getKey());
        } else {
            entry.getValue().run(statement, index, t);
        }
    }
}
