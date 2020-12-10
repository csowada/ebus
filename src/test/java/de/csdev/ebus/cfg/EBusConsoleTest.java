/**
 * Copyright (c) 2016-2020 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.cfg;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.cfg.std.EBusConfigurationReader;
import de.csdev.ebus.command.EBusCommandRegistry;
import de.csdev.ebus.command.EBusCommandUtils;
import de.csdev.ebus.core.EBusDataException;
import de.csdev.ebus.utils.EBusConsoleUtils;
import de.csdev.ebus.utils.EBusUtils;

public class EBusConsoleTest {

    private static final Logger logger = LoggerFactory.getLogger(EBusConsoleTest.class);

    private static EBusCommandRegistry commandRegistry;

    @BeforeClass
    public static void before() {
        commandRegistry = new EBusCommandRegistry(EBusConfigurationReader.class, true);

    }

    @Test
    public void testPrepareSendWithCrc() throws EBusDataException {

        // prepared byte array should be unchanged
        byte[] dataSource1 = EBusUtils.toByteArray("00 08 B5 09 03 0D 02 00 03");
        byte[] dataTarget1 = EBusCommandUtils.prepareSendTelegram(dataSource1);

        assertArrayEquals(dataSource1, dataTarget1);
    }

    @Test
    public void testPrepareSendWithoutCrc() throws EBusDataException {

        // prepared byte array should be unchanged
        byte[] dataSource1 = EBusUtils.toByteArray("00 08 B5 09 03 0D 02 00 03");

        // prepared byte array should be changed - add crc
        byte[] dataSource2 = EBusUtils.toByteArray("00 08 B5 09 03 0D 02 00");
        byte[] dataTarget2 = EBusCommandUtils.prepareSendTelegram(dataSource2);

        assertArrayEquals(dataSource1, dataTarget2);
    }

    @Test
    public void testPrepareSendWithWrongCrc() throws EBusDataException {

        try {
            // prepared byte array should be changed - add crc
            byte[] dataSource3 = EBusUtils.toByteArray("00 08 B5 09 03 0D 02 00 B1");
            EBusCommandUtils.prepareSendTelegram(dataSource3);

            fail();

        } catch (EBusDataException e) {
            // okay, CRC is wrong
        }

    }

    @Test
    public void testPrepareSendInvalidData() throws EBusDataException {

        try {
            // prepared byte array should be changed - add crc
            byte[] dataSource3 = EBusUtils.toByteArray("00 08 B5 09 03 0D");
            EBusCommandUtils.prepareSendTelegram(dataSource3);

            fail();

        } catch (EBusDataException e) {
            // okay,telegram is incomplete
        }

    }

    @Test
    public void testConsoleCommand() {

        EBusCommandRegistry commandRegistry = EBusConsoleTest.commandRegistry;
        assertNotNull(commandRegistry);

        byte[] data = EBusUtils.toByteArray("31 08 07 04 00 d1 00 0a b5 42 41 49 30 30 05 18 74 01 2f 00");
        logger.debug(EBusConsoleUtils.analyzeTelegram(commandRegistry, data));

        data = EBusUtils.toByteArray("71 FE 50 18 0E 00 00 D0 01 05 00 E2 03 0F 01 01 00 00 00 18");
        logger.debug(EBusConsoleUtils.analyzeTelegram(commandRegistry, data));

        data = EBusUtils.toByteArray("30 76 50 22 03 CC 2B 0A BF 00 02 11 01 84 00");
        logger.debug(EBusConsoleUtils.analyzeTelegram(commandRegistry, data));

        data = EBusUtils.toByteArray("FF 50 B5 09 03 0D 15 00 26 00 02 28 00 51 00");
        logger.debug(EBusConsoleUtils.analyzeTelegram(commandRegistry, data));
    }
}
