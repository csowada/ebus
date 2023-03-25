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
public class EBusTypeNumber extends AbstractEBusTypeNumber {

    public static String TYPE_NUMBER = "number";

    private static String[] supportedTypes = new String[] { TYPE_NUMBER };

    private int length = 1;

    @Override
    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @Override
    public int getTypeLength() {
        return length;
    }

    @Override
    public String toString() {
        return "EBusTypeNumber [length=" + length + ", "
                + (replaceValue != null ? "replaceValue=" + EBusUtils.toHexDumpString(getReplaceValue()) + ", " : "")
                + "reverseByteOrder=" + reverseByteOrder + "]";
    }

}
