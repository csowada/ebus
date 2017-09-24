/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command.datatypes.std;

import java.util.Map;

import de.csdev.ebus.command.datatypes.EBusTypeGeneric;
import de.csdev.ebus.command.datatypes.IEBusType;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusTypeBit extends EBusTypeGeneric<Boolean> {

    public static String BIT = "bit";

    private static String[] supportedTypes = new String[] { BIT };

    private Integer bit = null;

    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    public Boolean decode(byte[] data) {

        if (data == null) {
            // replace value
            return Boolean.FALSE;
        }

        Boolean isSet = (data[0] >> bit & 0x1) == 1;
        return isSet;
    }

    public byte[] encode(Object data) {
        throw new RuntimeException("Not implemented yet!");
    }

    @Override
    public IEBusType<Boolean> getInstance(Map<String, Object> properties) {

        if (properties.containsKey("pos")) {
            EBusTypeBit x = new EBusTypeBit();
            x.types = types;
            x.bit = (Integer) properties.get("pos");
            return x;
        }

        return this;
    }

    @Override
    public String toString() {
        return "EBusTypeBit [bit=" + bit + "]";
    }
}
