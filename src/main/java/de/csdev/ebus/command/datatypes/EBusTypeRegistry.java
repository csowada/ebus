/**
 * Copyright (c) 2017-2025 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command.datatypes;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.command.datatypes.ext.EBusTypeBytes;
import de.csdev.ebus.command.datatypes.ext.EBusTypeDate;
import de.csdev.ebus.command.datatypes.ext.EBusTypeDateTime;
import de.csdev.ebus.command.datatypes.ext.EBusTypeKWCrc;
import de.csdev.ebus.command.datatypes.ext.EBusTypeMultiWord;
import de.csdev.ebus.command.datatypes.ext.EBusTypeString;
import de.csdev.ebus.command.datatypes.ext.EBusTypeTime;
import de.csdev.ebus.command.datatypes.ext.EBusTypeVersion;
import de.csdev.ebus.command.datatypes.std.EBusTypeBCD;
import de.csdev.ebus.command.datatypes.std.EBusTypeBit;
import de.csdev.ebus.command.datatypes.std.EBusTypeByte;
import de.csdev.ebus.command.datatypes.std.EBusTypeChar;
import de.csdev.ebus.command.datatypes.std.EBusTypeData1b;
import de.csdev.ebus.command.datatypes.std.EBusTypeData1c;
import de.csdev.ebus.command.datatypes.std.EBusTypeData2b;
import de.csdev.ebus.command.datatypes.std.EBusTypeData2c;
import de.csdev.ebus.command.datatypes.std.EBusTypeFloat;
import de.csdev.ebus.command.datatypes.std.EBusTypeInteger;
import de.csdev.ebus.command.datatypes.std.EBusTypeNumber;
import de.csdev.ebus.command.datatypes.std.EBusTypeUnsignedNumber;
import de.csdev.ebus.command.datatypes.std.EBusTypeWord;
import de.csdev.ebus.utils.CollectionUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusTypeRegistry {

    private static final Logger logger = LoggerFactory.getLogger(EBusTypeRegistry.class);

    private Map<String, IEBusType<?>> types = null;

    /**
     * @throws EBusTypeException
     *
     */
    public EBusTypeRegistry() throws EBusTypeException {
        init();
    }

    /**
     * Loads all internal types
     *
     * @throws EBusTypeException
     */
    protected void init() throws EBusTypeException {
        types = new HashMap<>();

        // primary types
        add(EBusTypeBit.class);
        add(EBusTypeByte.class);
        add(EBusTypeChar.class);
        add(EBusTypeInteger.class);
        add(EBusTypeWord.class);
        add(EBusTypeNumber.class);
        add(EBusTypeUnsignedNumber.class);

        // secondary types
        add(EBusTypeBCD.class);
        add(EBusTypeData1b.class);
        add(EBusTypeData1c.class);
        add(EBusTypeData2b.class);
        add(EBusTypeData2c.class);

        // extended types
        add(EBusTypeBytes.class);
        add(EBusTypeString.class);
        add(EBusTypeMultiWord.class);
        add(EBusTypeDateTime.class);
        add(EBusTypeDate.class);
        add(EBusTypeTime.class);
        add(EBusTypeVersion.class);
        add(EBusTypeFloat.class);
        // vendor specific
        add(EBusTypeKWCrc.class);

        // test
    }

    /**
     * @param type
     * @param properties
     * @return
     */
    public @Nullable <T> IEBusType<T> getType(String type, Map<String, Object> properties) {
        IEBusType<T> ebusType = getType(type);

        if (ebusType != null) {
            return ebusType.getInstance(properties);
        }

        return null;
    }

    /**
     * @param type
     * @param propertiesArguments
     * @return
     */
    public @Nullable <T> IEBusType<T> getType(String type, Object... propertiesArguments) {
        Map<String, Object> properties = CollectionUtils.createProperties(propertiesArguments);
        return this.getType(type, properties);
    }

    /**
     * Returns a data type by id or <code>null</code>
     *
     * @param type
     * @return
     */
    public @Nullable <T> IEBusType<T> getType(String type) {
        @SuppressWarnings("unchecked")
        IEBusType<T> eBusType = (IEBusType<T>) types.get(type);

        if (eBusType == null) {
            logger.warn("No eBUS data type with name {} !", type);
            return null;
        }

        return eBusType;
    }

    /**
     * Returns the type names
     *
     * @return
     */
    public <T> Set<String> getTypesNames() {
        return types.keySet();
    }

    /**
     * Encodes an object with the given data type or <code>null</code>
     *
     * @param type
     * @param data
     * @return
     * @throws EBusTypeException
     */
    public byte @Nullable [] encode(String type, Object data) throws EBusTypeException {

        IEBusType<?> eBusType = types.get(type);

        if (eBusType == null) {
            logger.warn("No eBUS data type with name {} !", type);
            return null;
        }

        return eBusType.encode(data);
    }

    /**
     * Decodes a byte-array data with the data type or <code>null</code>
     *
     * @param type
     * @param data
     * @return
     * @throws EBusTypeException
     */
    public @Nullable <T> T decode(@Nullable String type, byte @Nullable [] data) throws EBusTypeException {

        @SuppressWarnings("unchecked")
        IEBusType<T> eBusType = (IEBusType<T>) types.get(type);

        if (eBusType == null) {
            logger.warn("No eBUS data type with name {} !", type);
            return null;
        }

        return eBusType.decode(data);
    }

    /**
     * Add a new IEBusType to the registry
     *
     * @param clazz
     * @throws EBusTypeException
     */
    public void add(Class<?> clazz) throws EBusTypeException {
        try {
            IEBusType<?> newInstance = (IEBusType<?>) clazz.getDeclaredConstructor().newInstance();

            if (newInstance == null) {
                throw new EBusTypeException(
                        String.format("Unable to create a new instance for class %s", clazz.getName()));
            }

            newInstance.setTypesParent(this);

            for (String typeName : newInstance.getSupportedTypes()) {
                logger.trace("Add eBUS type {}", typeName);
                types.put(typeName, newInstance);
            }

        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            logger.error("error!", e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "EBusTypeRegistry [types=" + types + "]";
    }

}
