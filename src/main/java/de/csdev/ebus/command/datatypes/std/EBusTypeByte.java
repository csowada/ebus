/**
 * Copyright (c) 2017-2023 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command.datatypes.std;

import org.eclipse.jdt.annotation.NonNullByDefault;

import de.csdev.ebus.utils.EBusUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
@NonNullByDefault
public class EBusTypeByte extends AbstractEBusTypeUnsignedNumber {

    public static String TYPE_UCHAR = "uchar";
    public static String TYPE_BYTE = "byte";

    private static String[] supportedTypes = new String[] { TYPE_BYTE, TYPE_UCHAR };

    @Override
    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @Override
    public int getTypeLength() {
        return 1;
    }

    @Override
    public String toString() {
        return "EBusTypeByte [replaceValue=" + EBusUtils.toHexDumpString(getReplaceValue()).toString() + "]";
    }

}
