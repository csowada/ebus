/**
 * Copyright (c) 2016-2020 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.wip;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.cfg.EBusConfigurationReaderException;
import de.csdev.ebus.cfg.std.EBusConfigurationReader;
import de.csdev.ebus.client.EBusClient;
import de.csdev.ebus.command.EBusCommandException;
import de.csdev.ebus.command.EBusCommandRegistry;
import de.csdev.ebus.command.IEBusCommand;
import de.csdev.ebus.command.IEBusCommandMethod;
import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.utils.EBusUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusWolfSM1TelegramTest2XXX {

    private static final Logger logger = LoggerFactory.getLogger(EBusWolfSM1TelegramTest2XXX.class);

    private EBusClient client;

    @Before
    public void before() throws IOException, EBusConfigurationReaderException {

        EBusCommandRegistry commandRegistry = new EBusCommandRegistry(EBusConfigurationReader.class, true);

        client = new EBusClient(commandRegistry);
        client.getDeviceTable().setOwnAddress((byte) 0xFF);
    }

    // @Test
    @SuppressWarnings("null")
    public void testSolarCommands2() {

        IEBusCommand command = client.getConfigurationProvider().getCommandById("vrc430", "controller.date");

        if (command != null) {
            for (IEBusCommandMethod method : command.getCommandMethods()) {
                try {

                    ByteBuffer buildTelegram = client.buildTelegram(method, (byte) 0x08, null);
                    logger.info("{} -> {}", method.getMethod(), EBusUtils.toHexDumpString(buildTelegram));

                } catch (EBusTypeException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (EBusCommandException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

    }

}
