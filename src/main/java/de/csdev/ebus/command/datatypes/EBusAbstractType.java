/**
 * Copyright (c) 2016-2020 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command.datatypes;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TreeMap;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.utils.EBusUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public abstract class EBusAbstractType<T> implements IEBusType<T> {

    private static final Logger logger = LoggerFactory.getLogger(EBusAbstractType.class);

    protected Map<Object, EBusAbstractType<T>> otherInstances = new HashMap<Object, EBusAbstractType<T>>();

    protected byte[] replaceValue = null;

    protected boolean reverseByteOrder = false;

    protected EBusTypeRegistry types;

    /**
     * Create a clone of the input array and reverse the byte order if set
     *
     * @param data
     * @return
     */
    protected byte[] applyByteOrder(byte[] data) {

        data = ArrayUtils.clone(data);

        // reverse the byte order immutable
        if (reverseByteOrder) {
            ArrayUtils.reverse(data);
        }

        return data;
    }

    /**
     * Creates a new instance of this type
     *
     * @return
     */
    private EBusAbstractType<T> createNewInstance() {

        try {
            @SuppressWarnings({ "unchecked"})
            EBusAbstractType<T> newInstance = this.getClass().getDeclaredConstructor().newInstance();
            newInstance.types = this.types;
            return newInstance;

        } catch (InstantiationException e) {
            logger.error("error!", e);
        } catch (IllegalAccessException e) {
            logger.error("error!", e);
        } catch (IllegalArgumentException e) {
            logger.error("error!", e);
        } catch (InvocationTargetException e) {
            logger.error("error!", e);
        } catch (NoSuchMethodException e) {
            logger.error("error!", e);
        } catch (SecurityException e) {
            logger.error("error!", e);
        }

        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.datatypes.IEBusType#decode(byte[])
     */
    @Override
    public @Nullable T decode(byte @Nullable [] data) throws EBusTypeException {

        Objects.requireNonNull(data);

        if (data.length != getTypeLength()) {
            throw new EBusTypeException("Input parameter byte-array has size {0}, expected {1} for eBUS type {2}",
                    data.length, getTypeLength(), this.getClass().getSimpleName());
        }

        // apply the right byte order before processing
        data = applyByteOrder(data);

        // return null in case of a replace value
        if (equalsReplaceValue(data)) {
            return null;
        }

        return decodeInt(data);
    }

    /**
     * Post decodes the byte-array extract from the telegram to valid result, replaceValue and byteOrder already applied
     *
     * @param data
     * @return
     * @throws EBusTypeException
     */
    public @Nullable T decodeInt(byte @Nullable [] data) throws EBusTypeException {
        throw new RuntimeException("Must be overwritten by superclass!");
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.datatypes.IEBusType#encode(java.lang.Object)
     */
    @Override
    public byte[] encode(@Nullable Object data) throws EBusTypeException {

        // return the replace value
        if (data == null) {
            return applyByteOrder(getReplaceValue());
        }

        byte[] result = encodeInt(data);

        // apply the right byte order after processing
        result = applyByteOrder(result);

        if (result.length != getTypeLength()) {
            throw new EBusTypeException("Result byte-array has size {0}, expected {1} for eBUS type {2}", result.length,
                    getTypeLength(), this.getClass().getSimpleName());
        }

        return result;
    }

    /**
     * Post encodes the given object to a byte-array value, replaceValue and byteOrder already applied
     *
     * @param data
     * @return
     * @throws EBusTypeException
     */
    public byte[] encodeInt(@Nullable Object data) throws EBusTypeException {
        throw new RuntimeException("Must be overwritten by superclass!");
    }

    /**
     * Check if the input data is equals to the replaceValue
     *
     * @param data
     * @return
     */
    protected boolean equalsReplaceValue(byte[] data) {
        return ArrayUtils.isEquals(data, getReplaceValue());
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.datatypes.IEBusType#getInstance(java.util.Map)
     */
    @Override
    public IEBusType<T> getInstance(@Nullable Map<String, Object> properties) {

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
     * Returns the replace value or <code>null</code>
     *
     * @return
     */
    public byte[] getReplaceValue() {

        int length = getTypeLength();
        if (replaceValue == null || replaceValue.length == 0) {
            replaceValue = new byte[length];
            Arrays.fill(replaceValue, (byte) 0xFF);
        }

        return replaceValue;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.datatypes.IEBusType#getTypeLength()
     */
    @Override
    public int getTypeLength() {
        return 0;
    }

    /**
     * Sets a property to a type instance
     *
     * @param instance
     * @param property
     * @param value
     */
    protected void setInstanceProperty(@Nullable EBusAbstractType<T> instance, @Nullable String property,
            @Nullable Object value) {

        Objects.requireNonNull(property);
        Objects.requireNonNull(instance);

        if (property.equals("replaceValue")) {
            if (value instanceof String) {
                try {
                    instance.setReplaceValue(EBusUtils.toByteArray((String) value));
                } catch (EBusTypeException e) {
                    logger.error("error!", e);
                }
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
     * Set the replace value
     *
     * @param replaceValue
     * @throws EBusTypeException
     */
    public void setReplaceValue(byte[] replaceValue) throws EBusTypeException {
        this.replaceValue = applyByteOrder(replaceValue);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.datatypes.IEBusType#setTypesParent(de.csdev.ebus.command.datatypes.EBusTypeRegistry)
     */
    @Override
    public void setTypesParent(EBusTypeRegistry types) {
        this.types = types;
    }
}