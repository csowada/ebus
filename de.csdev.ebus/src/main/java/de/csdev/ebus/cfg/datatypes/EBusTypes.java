/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.cfg.datatypes;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.cfg.datatypes.ext.EBusTypeBytes;
import de.csdev.ebus.cfg.datatypes.ext.EBusTypeKWCrc;
import de.csdev.ebus.cfg.datatypes.ext.EBusTypeMultiWord;
import de.csdev.ebus.cfg.datatypes.ext.EBusTypeString;
import de.csdev.ebus.cfg.datatypes.ext.EBusTypeDateTime;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusTypes {

    private static final Logger logger = LoggerFactory.getLogger(EBusTypes.class);

    private Map<String, IEBusType<?>> types = null;

    public EBusTypes() {
        init();
    }

    protected void init() {
        types = new HashMap<String, IEBusType<?>>();

        add(EBusTypeBit.class);
        add(EBusTypeByte.class);
        add(EBusTypeChar.class);
        add(EBusTypeInteger.class);
        add(EBusTypeWord.class);
        add(EBusTypeBCD.class);
        add(EBusTypeData1b.class);
        add(EBusTypeData1c.class);
        add(EBusTypeData2b.class);
        add(EBusTypeData2c.class);

        add(EBusTypeBytes.class);
        add(EBusTypeString.class);

        add(EBusTypeMultiWord.class);
        add(EBusTypeDateTime.class);

        // ext
        add(EBusTypeKWCrc.class);
    }

    // public <T extends IEBusType> T xxx(T xxxy) {
    // return xxxy;
    // }

    public <T> IEBusType<T> getType(String type, Map<String, Object> properties) {
        IEBusType<T> ebusType = getType(type);

        if (ebusType != null) {
            return ebusType.getInstance(properties);
        }

        return null;
    }

    public <T> IEBusType<T> getType(String type) {
        @SuppressWarnings("unchecked")
        IEBusType<T> eBusType = (IEBusType<T>) types.get(type);

        if (eBusType == null) {
            logger.warn("No eBUS data type with name {} !", type);
            return null;
        }

        return eBusType;
    }

    public byte[] encode(String type, Object data, Object... args) throws EBusTypeException {
        IEBusType<?> eBusType = types.get(type);

        if (eBusType == null) {
            logger.warn("No eBUS data type with name {} !", type);
            return null;
        }

        return eBusType.encode(data);
    }

    public <T> T decode(String type, byte[] data) throws EBusTypeException {
        @SuppressWarnings("unchecked")
        IEBusType<T> eBusType = (IEBusType<T>) types.get(type);

        if (eBusType == null) {
            logger.warn("No eBUS data type with name {} !", type);
            return null;
        }

        return eBusType.decode(data);
    }

    // public <T> T decode(String type, byte[] data, int pos, Object... args) throws EBusTypeException {
    // IEBusType eBusType = types.get(type);
    //
    // if (eBusType == null) {
    // logger.warn("No eBUS data type with name {} !", type);
    // return null;
    // }
    //
    // byte[] b = new byte[eBusType.getTypeLenght()];
    // System.arraycopy(data, pos, b, 0, b.length);
    //
    // return eBusType.decode(b);
    // }

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

}
