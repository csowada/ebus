package de.csdev.ebus.command.datatypes.ext;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.command.datatypes.EBusTypeRegistry;
import de.csdev.ebus.command.datatypes.IEBusType;

public abstract class DummyTypeGeneric<T> implements IEBusType<T> {

    private static final Logger logger = LoggerFactory.getLogger(DummyTypeGeneric.class);

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
        applyByteOrder(data);
        return decodeInt(data);
    }

    @Override
    public byte[] encode(Object data) throws EBusTypeException {
        byte[] result = encodeInt(data);
        applyByteOrder(result);
        return result;
    }

    @Override
    public IEBusType<T> getInstance(Map<String, Object> properties) {

        DummyTypeGeneric<T> instance = createNewInstance();

        for (Entry<String, Object> entry : properties.entrySet()) {
            setValue(instance, entry.getKey(), entry.getValue());
        }

        return instance;
    }

    protected void applyByteOrder(byte[] data) {
        if (reverseByteOrder) {
            ArrayUtils.reverse(data);
        }
    }

    private void setValue(DummyTypeGeneric<T> instance, String property, Object value) {
        try {

            Field declaredField = instance.getClass().getDeclaredField(property);

            if (declaredField != null) {
                declaredField.set(instance, value);
            }

        } catch (NoSuchFieldException e) {
            logger.error("error!", e);
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
    private DummyTypeGeneric<T> createNewInstance() {

        try {
            @SuppressWarnings("unchecked")
            DummyTypeGeneric<T> newInstance = this.getClass().newInstance();
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