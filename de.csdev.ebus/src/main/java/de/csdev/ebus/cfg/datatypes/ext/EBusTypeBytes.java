/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.cfg.datatypes.ext;

import java.util.Map;

import de.csdev.ebus.cfg.datatypes.EBusTypeGeneric;
import de.csdev.ebus.cfg.datatypes.IEBusType;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusTypeBytes extends EBusTypeGeneric {

    public static String BYTES = "bytes";

    private static String[] supportedTypes = new String[] { BYTES };

    private Integer length = 1;

    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @SuppressWarnings("unchecked")
    public <T> T decode(byte[] data) {
        return (T) data;
    }

    public byte[] encode(Object data) {

        byte[] b = new byte[length];

        if (data != null && data instanceof byte[]) {
            System.arraycopy(data, 0, b, 0, b.length);
        }

        return b;
    }

    @Override
    public int getTypeLenght() {
        return length;
    }

    @Override
    public IEBusType getInstance(Map<String, Object> properties) {

        if (properties.containsKey("length")) {
            EBusTypeBytes type = new EBusTypeBytes();
            type.length = (Integer) properties.get("length");
            type.types = this.types;
            return type;
        }

        return this;
    }
}
