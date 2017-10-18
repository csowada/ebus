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

import de.csdev.ebus.command.datatypes.EBusAbstractType;
import de.csdev.ebus.command.datatypes.IEBusType;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusTypeBit extends EBusAbstractType<Boolean>  {

    public static String TYPE_BIT = "bit";

    private static String[] supportedTypes = new String[] { TYPE_BIT };

    private Integer bit = null;


	@Override
	public String[] getSupportedTypes() {
		return supportedTypes;
	}

	@Override
	public int getTypeLength() {
		return 1;
	}

    @Override
    public Boolean decode(byte[] data) {

        if (data == null) {
            // replace value
            return Boolean.FALSE;
        }

        Boolean isSet = (data[0] >> bit & 0x1) == 1;
        return isSet;
    }

    @Override
    public byte[] encode(Object data) {
        throw new RuntimeException("Not implemented yet!");
    }

    @Override
    public IEBusType<Boolean> getInstance(Map<String, Object> properties) {

        if (properties.containsKey("pos")) {
            EBusTypeBit x = new EBusTypeBit();
//            x.types = types;
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
