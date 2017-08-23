/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.cfg.datatypes;

import java.util.Map;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusTypeBit extends EBusTypeGeneric {

    public static String BIT = "bit";

    private static String[] supportedTypes = new String[] { BIT };

    private Integer bit = null;

    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @SuppressWarnings("unchecked")
    public <T> T decode(byte[] data) {

        if (data == null) {
            // replace value
            return (T) Boolean.FALSE;
        }

        Boolean isSet = (data[0] >> bit & 0x1) == 1;
        return (T) isSet;
    }

    public byte[] encode(Object data) {
        throw new RuntimeException("Not implemented yet!");
    }

    @Override
    public IEBusType getInstance(Map<String, Object> properties) {

        if (properties.containsKey("pos")) {
            EBusTypeBit x = new EBusTypeBit();
            x.types = types;
            x.bit = (Integer) properties.get("pos");
            return x;
        }

        return this;
    }
}
