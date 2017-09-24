/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command.datatypes.std;

import java.math.BigDecimal;

import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.command.datatypes.EBusTypeGenericReplaceValue;
import de.csdev.ebus.command.datatypes.EBusTypeRegistry;
import de.csdev.ebus.utils.EBusUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusTypeData1b extends EBusTypeGenericReplaceValue {

    public static String DATA1B = "data1b";

    private static String[] supportedTypes = new String[] { DATA1B };

    public EBusTypeData1b() {
        replaceValue = new byte[] { (byte) 0x80 };
    }

    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @Override
    public BigDecimal decodeInt(byte[] data) throws EBusTypeException {
        return types.decode(EBusTypeChar.CHAR, data);
    }

    @Override
    public byte[] encodeInt(Object data) throws EBusTypeException {
        return types.encode(EBusTypeChar.CHAR, data);
    }

    @Override
    public void setTypesParent(EBusTypeRegistry types) {
        this.types = types;
    }

    @Override
    public String toString() {
        return "EBusTypeData1b [replaceValue=" + EBusUtils.toHexDumpString(replaceValue).toString() + "]";
    }

}
