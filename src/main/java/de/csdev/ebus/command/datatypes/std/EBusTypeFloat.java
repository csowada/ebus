/**
 * Copyright (c) 2017-2023 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command.datatypes.std;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.command.datatypes.EBusAbstractType;
import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.utils.EBusUtils;
import de.csdev.ebus.utils.NumberUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusTypeFloat extends EBusAbstractType<BigDecimal> {

    private static final Logger logger = LoggerFactory.getLogger(EBusTypeFloat.class);

    public static final String TYPE_FLOAT = "float";

    private static String[] supportedTypes = new String[] { TYPE_FLOAT };

    @Override
    public byte[] getReplaceValue() {
        return new byte[] { 0x7F, (byte) 0x80, 0x00, 0x00 };
    }

    @Override
    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @Override
    public int getTypeLength() {
        return 4;
    }

    @Override
    public BigDecimal decodeInt(byte @Nullable [] data) throws EBusTypeException {
        float value = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        if (!Float.isNaN(value)) {
            return BigDecimal.valueOf(value);
        }

        logger.trace("Raw float value {} is NaN!", EBusUtils.toHexDumpString(data));
        return null;
    }

    @Override
    public byte[] encodeInt(@Nullable Object data) throws EBusTypeException {

        BigDecimal b = NumberUtils.toBigDecimal(data == null ? 0 : data);

        if (b != null) {
            ByteBuffer buffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
            return buffer.putFloat(b.floatValue()).array();
        }

        return null;
    }

    @Override
    public String toString() {
        return "EBusTypeFloat [replaceValue=" + EBusUtils.toHexDumpString(getReplaceValue()).toString() + "]";
    }

}
