package de.csdev.ebus.command.datatypes;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class EBusAbstractType<T> implements IEBusType<T> {

    private static final Logger logger = LoggerFactory.getLogger(EBusAbstractType.class);

    protected Map<Object, EBusAbstractType<T>> otherInstances = new HashMap<Object, EBusAbstractType<T>>();

    protected byte[] replaceValue = null;

    protected boolean reverseByteOrder = false;

    protected EBusTypeRegistry types;

    protected byte[] applyByteOrder(byte[] data) {

        data = ArrayUtils.clone(data);

        // reverse the byte order immutable
        if (reverseByteOrder) {
            ArrayUtils.reverse(data);
        }

        return data;
    }

    /**
     * @return
     */
    private EBusAbstractType<T> createNewInstance() {

        try {
            @SuppressWarnings("unchecked")
            EBusAbstractType<T> newInstance = this.getClass().newInstance();
            newInstance.types = this.types;
            return newInstance;

        } catch (InstantiationException e) {
            logger.error("error!", e);
        } catch (IllegalAccessException e) {
            logger.error("error!", e);
        }

        return null;
    }

    @Override
    public T decode(byte[] data) throws EBusTypeException {

        if (data.length != getTypeLenght()) {
            throw new EBusTypeException("Input parameter byte-array has size {0}, expected {1} for eBUS type {2}",
                    data.length, getTypeLenght(), this.getClass().getSimpleName());
        }

        // apply the right byte order before processing
        data = applyByteOrder(data);

        // return null in case of a replace value
        if (equalsReplaceValue(data)) {
            return null;
        }

        return decodeInt(data);
    }

    public T decodeInt(byte[] data) throws EBusTypeException {
        throw new RuntimeException("Must be overwritten by superclass!");
    }

    @Override
    public byte[] encode(Object data) throws EBusTypeException {

        // return the replacec value
        if (data == null) {
            return applyByteOrder(getReplaceValue());
        }

        byte[] result = encodeInt(data);

        // apply the right byte order after processing
        result = applyByteOrder(result);

        if (result.length != getTypeLenght()) {
            throw new EBusTypeException("Result byte-array has size {0}, expected {1} for eBUS type {2}", result.length,
                    getTypeLenght(), this.getClass().getSimpleName());
        }

        return result;
    }

    public byte[] encodeInt(Object data) throws EBusTypeException {
        throw new RuntimeException("Must be overwritten by superclass!");
    }

    /**
     * @param data
     * @return
     */
    protected boolean equalsReplaceValue(byte[] data) {
        return ArrayUtils.isEquals(data, getReplaceValue());
    }

    @Override
    public IEBusType<T> getInstance(Map<String, Object> properties) {

        // use default instance if no properties are set
        if (properties == null || properties.isEmpty()) {
            return this;
        }

        // Sort all members to have a reliable key
        TreeMap<String, Object> sortedMap = new TreeMap<String, Object>(properties);
        String instanceKey = sortedMap.toString();

        EBusAbstractType<T> instance = otherInstances.get(instanceKey);
        if (instance == null) {

            // create a new instance
            instance = createNewInstance();

            // apply all properties
            for (Entry<String, Object> entry : properties.entrySet()) {
                setInstanceProperty(instance, entry.getKey(), entry.getValue());
            }

            // store as shared instance
            otherInstances.put(instanceKey, instance);
        }

        return instance;
    }

    /**
     * @return
     */
    public byte[] getReplaceValue() {
        return replaceValue;
    }

    @Override
    public int getTypeLenght() {
        return 0;
    }

    protected void setInstanceProperty(EBusAbstractType<T> instance, String property, Object value) {

        if (property.equals("replaceValue")) {
            try {
                setReplaceValue(value);
            } catch (EBusTypeException e) {
                logger.error("error!", e);
            }
            return;
        }

        try {
            Field field = FieldUtils.getField(instance.getClass(), property, true);

            if (field != null) {
                field.set(instance, value);
            }

        } catch (SecurityException e) {
            logger.error("error!", e);
        } catch (IllegalArgumentException e) {
            logger.error("error!", e);
        } catch (IllegalAccessException e) {
            logger.error("error!", e);
        }
    }

    /**
     * @param replaceValue
     * @throws EBusTypeException
     */
    public void setReplaceValue(byte[] replaceValue) throws EBusTypeException {
        this.replaceValue = applyByteOrder(replaceValue);
    }

    /**
     * @param replaceValue
     * @throws EBusTypeException
     */
    public void setReplaceValue(Object replaceValue) throws EBusTypeException {
        this.replaceValue = encodeInt(replaceValue);
    }

    @Override
    public void setTypesParent(EBusTypeRegistry types) {
        this.types = types;
    }
}