/**
 * Copyright (c) 2016-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.wip;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.cfg.EBusConfigurationReaderException;
import de.csdev.ebus.cfg.std.EBusConfigurationReader;
import de.csdev.ebus.client.EBusClient;
import de.csdev.ebus.command.EBusCommandRegistry;
import de.csdev.ebus.command.IEBusCommandMethod;
import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.core.EBusLowLevelController;
import de.csdev.ebus.core.EBusControllerException;
import de.csdev.ebus.core.EBusDataException;
import de.csdev.ebus.core.EBusStateMachineTest;
import de.csdev.ebus.core.IEBusConnectorEventListener;
import de.csdev.ebus.core.connection.EBusEmulatorConnection;
import de.csdev.ebus.service.parser.IEBusParserListener;
import de.csdev.ebus.utils.EBusUtils;

public class ClientTest2 {

    private static final Logger logger = LoggerFactory.getLogger(EBusStateMachineTest.class);

    private EBusEmulatorConnection emulator;

    // @Before
    public void before() throws IOException, EBusConfigurationReaderException {
        emulator = new EBusEmulatorConnection();
    }

    // @Test
    public void testNoSlaveResponse()
            throws EBusTypeException, IOException, InterruptedException, EBusControllerException {

        EBusCommandRegistry commandRegistry = new EBusCommandRegistry(EBusConfigurationReader.class, true);

        EBusClient client = new EBusClient(commandRegistry);

        EBusLowLevelController controller = new EBusLowLevelController(emulator);

        client.connect(controller, (byte) 0xFF);

        client.getController().addEBusEventListener(new IEBusConnectorEventListener() {

            @Override
            public void onTelegramReceived(byte[] receivedData, Integer sendQueueId) {
                // TODO Auto-generated method stub
                logger.error("ClientTest.xxx().new EBusConnectorEventListener() {...}.onTelegramReceived()");
            }

            @Override
            public void onTelegramException(EBusDataException exception, Integer sendQueueId) {
                logger.error(exception.getLocalizedMessage());
                // TODO Auto-generated method stub
                // logger.error("ClientTest.xxx().new EBusConnectorEventListener() {...}.onTelegramException()");
            }

            @Override
            public void onConnectionException(Exception e) {
                // TODO Auto-generated method stub
                logger.error("ClientTest.xxx().new EBusConnectorEventListener() {...}.onConnectionException()");
            }
        });

        client.getResolverService().addEBusParserListener(new IEBusParserListener() {

            @Override
            public void onTelegramResolved(IEBusCommandMethod commandChannel, Map<String, Object> result,
                    byte[] receivedData, Integer sendQueueId) {
                logger.error("ClientTest.xxx().new EBusParserListener() {...}.onTelegramResolved()");
                System.out.println(result);
            }

            @Override
            public void onTelegramResolveFailed(IEBusCommandMethod commandChannel, byte[] receivedData,
                    Integer sendQueueId, String exceptionMessage) {
                // noop
            }
        });

        controller.start();

        controller.addToSendQueue(EBusUtils.toByteArray("FF 08 B5 09 03 0D 2F 00 1D"));

        Thread.sleep(500);
    }
}
