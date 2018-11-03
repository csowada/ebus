/**
 * Copyright (c) 2016-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.cfg;

import org.junit.Before;
import org.junit.Test;

import de.csdev.ebus.cfg.std.EBusConfigurationReader;
import de.csdev.ebus.command.EBusCommandRegistry;
import de.csdev.ebus.utils.EBusConsoleUtils;
import de.csdev.ebus.utils.EBusUtils;

public class EBusConsoleTest {

    private EBusCommandRegistry commandRegistry;

    @Before
    public void xx() {
        commandRegistry = new EBusCommandRegistry(EBusConfigurationReader.class, true);

    }

    @Test
    public void x() {

        byte[] data = EBusUtils.toByteArray("31 08 07 04 00 d1 00 0a b5 42 41 49 30 30 05 18 74 01 2f 00");
        System.out.print(EBusConsoleUtils.analyzeTelegram(commandRegistry, data));

        data = EBusUtils.toByteArray("71 FE 50 18 0E 00 00 D0 01 05 00 E2 03 0F 01 01 00 00 00 18");
        System.out.print(EBusConsoleUtils.analyzeTelegram(commandRegistry, data));

        data = EBusUtils.toByteArray("30 76 50 22 03 CC 2B 0A BF 00 02 11 01 84 00");
        System.out.print(EBusConsoleUtils.analyzeTelegram(commandRegistry, data));

        data = EBusUtils.toByteArray("FF 50 B5 09 03 0D 15 00 26 00 02 28 00 51 00");
        System.out.print(EBusConsoleUtils.analyzeTelegram(commandRegistry, data));
    }
}
