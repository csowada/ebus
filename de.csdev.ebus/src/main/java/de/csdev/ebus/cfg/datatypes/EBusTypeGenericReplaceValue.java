/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.cfg.datatypes;

import org.apache.commons.lang.ArrayUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public abstract class EBusTypeGenericReplaceValue extends EBusTypeGeneric {

    protected byte[] replaceValue = null;

    protected boolean equalsReplaceValue(byte[] data) {
        return ArrayUtils.isEquals(data, this.replaceValue);
    }

    public <T> T decode(byte[] data) throws EBusTypeException {

        if (equalsReplaceValue(data)) {
            return null;
        }

        return decodeInt(data);
    }

    public byte[] encode(Object data) throws EBusTypeException {

        if (data == null) {
            return replaceValue;
        }

        return encodeInt(data);
    }

    public abstract <T> T decodeInt(byte[] data) throws EBusTypeException;

    public abstract byte[] encodeInt(Object data) throws EBusTypeException;
}
