/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command.datatypes;

import java.math.BigDecimal;

import org.apache.commons.lang.ArrayUtils;

import de.csdev.ebus.utils.EBusUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public abstract class EBusTypeGenericReplaceValue extends EBusTypeGeneric<BigDecimal> {

    protected byte[] replaceValue = null;

    protected boolean equalsReplaceValue(byte[] data) {
        return ArrayUtils.isEquals(data, applyByteOrder(this.replaceValue));
    }

    @Override
    public BigDecimal decode(byte[] data) throws EBusTypeException {

        if (equalsReplaceValue(data)) {
            return null;
        }

        return decodeInt(data);
    }

    @Override
    public byte[] encode(Object data) throws EBusTypeException {

        if (data == null) {
            return applyByteOrder(replaceValue);
        }

        return encodeInt(data);
    }

    public abstract BigDecimal decodeInt(byte[] data) throws EBusTypeException;

    public abstract byte[] encodeInt(Object data) throws EBusTypeException;

    @Override
    public String toString() {
        return "EBusTypeGenericReplaceValue [replaceValue=" + EBusUtils.toHexDumpString(replaceValue).toString() + "]";
    }
}
