/**
 * Copyright (c) 2016-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.cfg;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.cfg.std.EBusConfigurationReader;
import de.csdev.ebus.command.EBusCommandRegistry;
import de.csdev.ebus.command.EBusCommandUtils;
import de.csdev.ebus.command.IEBusCommandMethod;
import de.csdev.ebus.command.IEBusCommandMethod.Method;
import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.core.EBusConsts;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusCommonTelegramTest {

    private static final Logger logger = LoggerFactory.getLogger(EBusCommonTelegramTest.class);

    EBusCommandRegistry commandRegistry;

    @Before
    public void before() throws IOException, EBusConfigurationReaderException {
        commandRegistry = new EBusCommandRegistry(EBusConfigurationReader.class, true);
    }

    @Test
    public void testIdentification() {
        IEBusCommandMethod commandMethod = commandRegistry.getCommandMethodById(EBusConsts.COLLECTION_STD,
                EBusConsts.COMMAND_IDENTIFICATION, IEBusCommandMethod.Method.GET);

        assertNotNull("Command common.identification not found!", commandMethod);

        try {
            ByteBuffer buffer = EBusCommandUtils.buildMasterTelegram(commandMethod, (byte) 0x00, (byte) 0xFF, null);
            assertNotNull("Unable to compose byte buffer for command", buffer);

        } catch (EBusTypeException e) {
            logger.error("error!", e);
        }

    }

    @Test
    public void testInquiryOfExistence() {
        IEBusCommandMethod commandMethod = commandRegistry.getCommandMethodById(EBusConsts.COLLECTION_STD,
                EBusConsts.COMMAND_INQ_EXISTENCE, Method.BROADCAST);
        assertNotNull("Command common.inquiry_of_existence not found!", commandMethod);
    }
}
