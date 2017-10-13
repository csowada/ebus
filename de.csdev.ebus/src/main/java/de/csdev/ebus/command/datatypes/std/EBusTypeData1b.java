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
public class EBusTypeData1b extends EBusTypeNumber {

    public static String DATA1B = "data1b";

    private static String[] supportedTypes = new String[] { DATA1B };

    @Override
    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @Override
    public int getTypeLenght() {
        return 1;
    }

    @Override
    public String toString() {
        return "EBusTypeData1b [replaceValue=" + EBusUtils.toHexDumpString(replaceValue).toString() + "]";
    }

}
