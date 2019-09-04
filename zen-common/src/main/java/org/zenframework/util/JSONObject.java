package org.zenframework.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * Extract a generic json object like fast json
 * @author Zeal
 */
public class JSONObject implements Map<String,Object> {

    private Map<String,Object> map = null;

    public JSONObject() {
        this.map = new LinkedHashMap<>();
    }

    public JSONObject(Map<String,Object> map) {
        this.map = map;
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.map.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return map.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        return this.map.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return this.map.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        this.map.putAll(m);
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    @Override
    public Set<String> keySet() {
        return this.map.keySet();
    }

    @Override
    public Collection<Object> values() {
        return this.map.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return this.map.entrySet();
    }

    //=============================================
    public BigDecimal getBigDecimal(String key) {
        Object value = get(key);
        return TypeUtils.castToBigDecimal(value);
    }

    public BigInteger getBigInteger(String key) {
        Object value = get(key);
        return TypeUtils.castToBigInteger(value);
    }

    public Boolean getBoolean(String key) {
        Object value = get(key);

        if (value == null) {
            return null;
        }
        return TypeUtils.castToBoolean(value);
    }

    public boolean getBooleanValue(String key) {
        Object value = get(key);

        Boolean booleanVal = TypeUtils.castToBoolean(value);
        if (booleanVal == null) {
            return false;
        }
        return booleanVal.booleanValue();
    }

    public Byte getByte(String key) {
        Object value = get(key);
        return TypeUtils.castToByte(value);
    }

    public byte getByteValue(String key) {
        Object value = get(key);

        Byte byteVal = TypeUtils.castToByte(value);
        if (byteVal == null) {
            return 0;
        }

        return byteVal.byteValue();
    }

    public Double getDouble(String key) {
        Object value = get(key);

        return TypeUtils.castToDouble(value);
    }

    public double getDoubleValue(String key) {
        Object value = get(key);

        Double doubleValue = TypeUtils.castToDouble(value);
        if (doubleValue == null) {
            return 0D;
        }

        return doubleValue.doubleValue();
    }

    public Float getFloat(String key) {
        Object value = get(key);

        return TypeUtils.castToFloat(value);
    }

    public float getFloatValue(String key) {
        Object value = get(key);

        Float floatValue = TypeUtils.castToFloat(value);
        if (floatValue == null) {
            return 0F;
        }

        return floatValue.floatValue();
    }

    public Integer getInteger(String key) {
        Object value = get(key);

        return TypeUtils.castToInt(value);
    }

    public int getIntValue(String key) {
        Object value = get(key);

        Integer intVal = TypeUtils.castToInt(value);
        if (intVal == null) {
            return 0;
        }

        return intVal.intValue();
    }

    public Long getLong(String key) {
        Object value = get(key);

        return TypeUtils.castToLong(value);
    }

    public long getLongValue(String key) {
        Object value = get(key);

        Long longVal = TypeUtils.castToLong(value);
        if (longVal == null) {
            return 0L;
        }

        return longVal.longValue();
    }

    public JSONObject getJSONObject(String key) {
        Object value = get(key);
        if (value == null) {
            return null;
        }
        else if (value instanceof Map) {
            Map<String,Object> valueMap = (Map<String,Object>) value;
            return new JSONObject(valueMap);
        }
        else if (value instanceof String) {
            return JSONUtils.parseObject((String) value);
        }
        else if (value instanceof List) {
            throw new RuntimeException("Not JSONObject, call getJSONArray instead");
        }
        else {
            throw new RuntimeException("Not JSONObject");
        }

    }

    public JSONArray getJSONArray(String key) {
        Object value = get(key);
        if (value == null) {
            return null;
        }
        else if (value instanceof List) {
            List<Object> list = (List<Object>) value;
            return new JSONArray(list);
        }
        else if (value instanceof Map) {
            throw new RuntimeException("Not JSONArray, call getJSONObject instead");
        }
        else {
            throw new RuntimeException("Not JSONArray");
        }
    }

    public String getString(String key) {
         Object value = get(key);

        if (value == null) {
            return null;
        }

        return value.toString();
    }





}
