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
public class EBusTypeBytes extends EBusTypeGeneric<byte[]> {

    public static String BYTES = "bytes";

    private static String[] supportedTypes = new String[] { BYTES };

    private Integer length = 1;

    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    public byte[] decode(byte[] data) {
        return data;
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
    public IEBusType<byte[]> getInstance(Map<String, Object> properties) {

        if (properties.containsKey(IEBusType.LENGTH)) {
            EBusTypeBytes type = new EBusTypeBytes();
            type.length = (Integer) properties.get(IEBusType.LENGTH);
            type.types = this.types;
            return type;
        }

        return this;
    }
}
