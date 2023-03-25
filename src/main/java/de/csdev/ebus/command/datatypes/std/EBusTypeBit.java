/**
 * Copyright (c) 2017-2023 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command.datatypes.std;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import de.csdev.ebus.command.datatypes.EBusAbstractType;
import de.csdev.ebus.utils.EBusUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
@NonNullByDefault
public class EBusTypeBit extends EBusAbstractType<Boolean> {

    public static String TYPE_BIT = "bit";

    private static String[] supportedTypes = new String[] { TYPE_BIT };

    @SuppressWarnings("java:S1845")
    public static String POS = "pos";

    private Integer pos = null;

    public EBusTypeBit() {
        // noop
    }

    @Override
    public byte @Nullable [] getReplaceValue() {
        return null;
    }

    @Override
    public String @NonNull [] getSupportedTypes() {
        return supportedTypes;
    }

    @Override
    public int getTypeLength() {
        return 1;
    }

    @Override
    public Boolean decodeInt(byte @Nullable [] data) {
        Objects.requireNonNull(data);
        return (data[0] >> pos & 0x1) == 1;
    }

    @Override
    public byte[] encodeInt(@Nullable Object data) {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    @Override
    public String toString() {
        return "EBusTypeBit [" + (pos != null ? "pos=" + pos + ", " : "")
                + (replaceValue != null ? "replaceValue=" + EBusUtils.toHexDumpString(getReplaceValue()) : "") + "]";
    }

}
