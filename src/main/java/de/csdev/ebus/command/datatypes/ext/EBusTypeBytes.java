/**
 * Copyright (c) 2016-2020 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command.datatypes.ext;

import java.util.Map;
import java.util.Objects;

import org.eclipse.jdt.annotation.Nullable;

import de.csdev.ebus.command.datatypes.EBusAbstractType;
import de.csdev.ebus.command.datatypes.IEBusType;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusTypeBytes extends EBusAbstractType<byte[]> {

    public static final String TYPE_BYTES = "bytes";

    private static String[] supportedTypes = new String[] { TYPE_BYTES };

    private Integer length = 1;

    @Override
    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @Override
    public byte @Nullable [] decodeInt(byte @Nullable [] data) {
        return data;
    }

    @Override
    public byte[] encodeInt(@Nullable Object data) {

        byte[] b = new byte[length];

        if (data != null && data instanceof byte[]) {
            System.arraycopy(data, 0, b, 0, b.length);
        }

        return b;
    }

    @Override
    public int getTypeLength() {
        return length;
    }

    @Override
    public IEBusType<byte[]> getInstance(@Nullable Map<String, Object> properties) {
        Objects.requireNonNull(properties);

        if (properties.containsKey(IEBusType.LENGTH)) {
            EBusTypeBytes type = new EBusTypeBytes();
            type.length = (Integer) properties.get(IEBusType.LENGTH);
            type.types = this.types;
            return type;
        }

        return this;
    }

    @Override
    public String toString() {
        return "EBusTypeBytes [length=" + length + "]";
    }
}
