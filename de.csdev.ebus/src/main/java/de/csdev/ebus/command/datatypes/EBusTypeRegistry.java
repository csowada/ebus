/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command.datatypes;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
     *
     */
    public EBusTypeRegistry() {
        init();
    }

    /**
     * Loads all internal types
     */
    protected void init() {
        types = new HashMap<String, IEBusType<?>>();

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
    public <T> IEBusType<T> getType(String type, Map<String, Object> properties) {
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
    public <T> IEBusType<T> getType(String type, Object... propertiesArguments) {
        Map<String, Object> properties = CollectionUtils.createProperties(propertiesArguments);
        return this.getType(type, properties);
    }

    /**
     * Returns a data type by id or <code>null</code>
     *
     * @param type
     * @return
     */
    public <T> IEBusType<T> getType(String type) {
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
    public byte[] encode(String type, Object data) throws EBusTypeException {

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
    public <T> T decode(String type, byte[] data) throws EBusTypeException {
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
     */
    public void add(Class<?> clazz) {
        try {
            IEBusType<?> newInstance = (IEBusType<?>) clazz.newInstance();
            newInstance.setTypesParent(this);

            for (String typeName : newInstance.getSupportedTypes()) {
                logger.trace("Add eBUS type {}", typeName);
                types.put(typeName, newInstance);
            }

        } catch (InstantiationException e) {
            logger.error("error!", e);

        } catch (IllegalAccessException e) {
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
