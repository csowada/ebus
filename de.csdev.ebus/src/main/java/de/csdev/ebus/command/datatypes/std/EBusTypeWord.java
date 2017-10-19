/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command.datatypes.std;

import de.csdev.ebus.utils.EBusUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusTypeWord extends EBusTypeUnsignedNumber {

    public static String TYPE_WORD = "word";
    public static String TYPE_UINT = "uint";

    private static String[] supportedTypes = new String[] { TYPE_WORD, TYPE_UINT };

    @Override
    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @Override
    public int getTypeLength() {
        return 2;
    }

    @Override
    public String toString() {
        return "EBusTypeWord [replaceValue=" + EBusUtils.toHexDumpString(getReplaceValue()).toString() + "]";
    }

}
