/**
 * Copyright (c) 2017-2023 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.wip;

import java.io.IOException;

import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.cfg.EBusConfigurationReaderException;
import de.csdev.ebus.cfg.std.EBusConfigurationReader;
import de.csdev.ebus.client.EBusClient;
import de.csdev.ebus.command.EBusCommandRegistry;
import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.core.EBusControllerException;
import de.csdev.ebus.core.EBusDataException;
import de.csdev.ebus.core.EBusLowLevelController;
import de.csdev.ebus.core.EBusStateMachineTest;
import de.csdev.ebus.core.IEBusConnectorEventListener;
import de.csdev.ebus.core.IEBusController.ConnectionStatus;
import de.csdev.ebus.core.connection.EBusTCPConnection;
import de.csdev.ebus.core.connection.IEBusConnection;
import de.csdev.ebus.utils.EBusUtils;

public class ClientTest3 {

    private static final Logger logger = LoggerFactory.getLogger(EBusStateMachineTest.class);

    private IEBusConnection connection;

    // @Before
    public void before() throws IOException, EBusConfigurationReaderException {
        connection = new EBusTCPConnection("openhab", 8881);
        // connection = new EBusUDPConnection("localhost", 8881);
    }

    // @Test
    public void testNoSlaveResponse()
            throws EBusTypeException, IOException, InterruptedException, EBusControllerException {

        EBusCommandRegistry commandRegistry = new EBusCommandRegistry(EBusConfigurationReader.class, true);

        EBusLowLevelController controller = new EBusLowLevelController(connection);

        EBusClient client = new EBusClient(commandRegistry);

        client.connect(controller, (byte) 0xFF);

        controller.addEBusEventListener(new IEBusConnectorEventListener() {

            @Override
            public void onTelegramReceived(byte[] receivedData, @Nullable Integer sendQueueId) {
                logger.info("Received: " + EBusUtils.toHexDumpString(receivedData).toString());
            }

            @Override
            public void onTelegramException(EBusDataException exception, @Nullable Integer sendQueueId) {
                logger.error(exception.getLocalizedMessage());
            }

            @Override
            public void onConnectionException(Exception e) {
                logger.info("ClientTest.xxx().new EBusConnectorEventListener() {...}.onConnectionException()");
            }

            @Override
            public void onConnectionStatusChanged(ConnectionStatus status) {
                logger.info(
                        "ClientTest3.testNoSlaveResponse().new IEBusConnectorEventListener() {...}.onConnectionStatusChanged()");
            }
        });

        // client.getResolverService().addEBusParserListener(new IEBusParserListener() {
        //
        // @Override
        // public void onTelegramResolved(IEBusCommandMethod commandChannel, Map<String, Object> result,
        // byte[] receivedData, Integer sendQueueId) {
        // logger.info("ClientTest.xxx().new EBusParserListener() {...}.onTelegramResolved()");
        // System.out.println(result);
        // }
        //
        // @Override
        // public void onTelegramResolveFailed(IEBusCommandMethod commandChannel, byte[] receivedData,
        // Integer sendQueueId, String exceptionMessage) {
        // // noop
        // }
        // });

        controller.start();

        Thread.sleep(1000);

        controller.addToSendQueue(EBusUtils.toByteArray("FF 35 50 22 03 CC 74 27 BE"));

        // Thread.sleep(10 * 60 * 1000);
        Thread.sleep(5 * 1000);

        controller.interrupt();

        Thread.sleep(1000);

        logger.info("Failure ratio: {}%", client.getMetricsService().getFailureRatio());
        logger.info("Failed: {}", client.getMetricsService().getFailed());
        logger.info("Received: {}", client.getMetricsService().getReceived());
        logger.info("ReceivedAmount: {}", client.getMetricsService().getReceivedAmount());
        logger.info("Round trip time: {}", controller.getLastSendReceiveRoundtripTime());
    }
}
