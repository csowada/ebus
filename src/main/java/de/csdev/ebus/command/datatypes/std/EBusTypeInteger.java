/**
 * Copyright (c) 2017-2025 by the respective copyright holders.
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
public class EBusTypeInteger extends AbstractEBusTypeNumber {

    public static String TYPE_INTEGER = "int";

    private static String[] supportedTypes = new String[] { TYPE_INTEGER };

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
        return "EBusTypeInteger [replaceValue=" + EBusUtils.toHexDumpString(getReplaceValue()).toString() + "]";
    }

}
