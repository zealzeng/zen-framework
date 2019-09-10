package org.zenframework.util.json;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import static org.zenframework.util.TypeUtils.*;

/**
 * Similar to JSONArray of fast json
 */
public class JSONArray implements List<Object> {

    private List<Object> list = null;

    public JSONArray() {
        this.list = new ArrayList<>();
    }

    public JSONArray(List<Object> list) {
        this.list = list;
    }

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.list.contains(o);
    }

    @Override
    public Iterator<Object> iterator() {
        return this.list.iterator();
    }

    @Override
    public Object[] toArray() {
        return this.list.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return null;
    }

    @Override
    public boolean add(Object o) {
        return this.list.add(o);
    }

    @Override
    public boolean remove(Object o) {
        return this.list.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.list.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<?> c) {
        return this.list.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<?> c) {
        return this.list.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return this.list.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return this.list.retainAll(c);
    }

    @Override
    public void clear() {
        this.list.clear();
    }

    @Override
    public Object get(int index) {
        return this.list.get(index);
    }

    @Override
    public Object set(int index, Object element) {
        return this.list.set(index, element);
    }

    @Override
    public void add(int index, Object element) {
        this.list.add(index, element);
    }

    @Override
    public Object remove(int index) {
        return this.list.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return this.list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.list.lastIndexOf(o);
    }

    @Override
    public ListIterator<Object> listIterator() {
        return this.list.listIterator();
    }

    @Override
    public ListIterator<Object> listIterator(int index) {
        return null;
    }

    @Override
    public List<Object> subList(int fromIndex, int toIndex) {
        return this.list.subList(fromIndex, toIndex);
    }

    //========================================================

    public Boolean getBoolean(int index) {
        Object value = get(index);

        if (value == null) {
            return null;
        }

        return castToBoolean(value);
    }

    public boolean getBooleanValue(int index) {
        Object value = get(index);

        if (value == null) {
            return false;
        }

        return castToBoolean(value).booleanValue();
    }

    public Byte getByte(int index) {
        Object value = get(index);

        return castToByte(value);
    }

    public byte getByteValue(int index) {
        Object value = get(index);

        if (value == null) {
            return 0;
        }

        return castToByte(value).byteValue();
    }

    public Short getShort(int index) {
        Object value = get(index);

        return castToShort(value);
    }

    public short getShortValue(int index) {
        Object value = get(index);

        if (value == null) {
            return 0;
        }

        return castToShort(value).shortValue();
    }

    public Integer getInteger(int index) {
        Object value = get(index);

        return castToInt(value);
    }

    public int getIntValue(int index) {
        Object value = get(index);

        if (value == null) {
            return 0;
        }

        return castToInt(value).intValue();
    }

    public Long getLong(int index) {
        Object value = get(index);

        return castToLong(value);
    }

    public long getLongValue(int index) {
        Object value = get(index);

        if (value == null) {
            return 0L;
        }

        return castToLong(value).longValue();
    }

    public Float getFloat(int index) {
        Object value = get(index);

        return castToFloat(value);
    }

    public float getFloatValue(int index) {
        Object value = get(index);

        if (value == null) {
            return 0F;
        }

        return castToFloat(value).floatValue();
    }

    public Double getDouble(int index) {
        Object value = get(index);

        return castToDouble(value);
    }

    public double getDoubleValue(int index) {
        Object value = get(index);

        if (value == null) {
            return 0D;
        }

        return castToDouble(value);
    }

    public BigDecimal getBigDecimal(int index) {
        Object value = get(index);

        return castToBigDecimal(value);
    }

    public BigInteger getBigInteger(int index) {
        Object value = get(index);

        return castToBigInteger(value);
    }

    public String getString(int index) {
        Object value = get(index);

        return castToString(value);
    }

    public JSONObject getJSONObject(int index) {
        Object value = list.get(index);
        if (value == null) {
            return null;
        }
        if (value instanceof Map) {
            Map<String,Object> valueMap = (Map<String,Object>) value;
            return new JSONObject(valueMap);
        }
        else if (value instanceof List) {
            throw new RuntimeException("Not JSONObject, call getJSONArray instead");
        }
        else {
            throw new RuntimeException("Not JSONObject");
        }
    }

}
