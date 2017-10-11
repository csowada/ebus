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

    protected EBusTypeRegistry types;

    protected boolean reverseByteOrder = false;

    protected int length;

    public abstract T decodeInt(byte[] data) throws EBusTypeException;

    public abstract byte[] encodeInt(Object data) throws EBusTypeException;

    @Override
    public void setTypesParent(EBusTypeRegistry types) {
        this.types = types;
    }

    @Override
    public int getTypeLenght() {
        return length;
    }

    @Override
    public T decode(byte[] data) throws EBusTypeException {

        // apply the right byte order before processing
        data = applyByteOrder(data);

        return decodeInt(data);
    }

    @Override
    public byte[] encode(Object data) throws EBusTypeException {

        byte[] result = encodeInt(data);

        // apply the right byte order after processing
        result = applyByteOrder(result);

        return result;
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
                setValue(instance, entry.getKey(), entry.getValue());
            }

            // store as shared instance
            otherInstances.put(instanceKey, instance);
        }

        return instance;
    }

    protected byte[] applyByteOrder(byte[] data) {

        // reverse the byte order immutable
        if (reverseByteOrder) {
            data = ArrayUtils.clone(data);
            ArrayUtils.reverse(data);
        }

        return data;
    }

    private void setValue(EBusAbstractType<T> instance, String property, Object value) {
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
}