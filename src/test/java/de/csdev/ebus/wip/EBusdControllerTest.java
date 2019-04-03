/**
 * Copyright (c) 2016-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.wip;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.cfg.std.EBusConfigurationReader;
import de.csdev.ebus.client.EBusClient;
import de.csdev.ebus.command.EBusCommandRegistry;
import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.core.EBusControllerException;
import de.csdev.ebus.core.EBusDataException;
import de.csdev.ebus.core.EBusEbusdController;
import de.csdev.ebus.core.EBusStateMachineTest;
import de.csdev.ebus.core.IEBusConnectorEventListener;
import de.csdev.ebus.utils.EBusUtils;

public class EBusdControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(EBusStateMachineTest.class);

    // private IEBusConnection connection;

    // @Test
    public void testEbusdSendString() {
        String sendString = EBusEbusdController
                .buildEbusdSendString(EBusUtils.toByteArray("FF 35 50 22 03 CC 74 27 BE"));
        assertEquals(" -s FF 35502203CC7427", sendString);
    }

    // @Test
    public void testNoSlaveResponse()
            throws EBusTypeException, IOException, InterruptedException, EBusControllerException {

        EBusCommandRegistry commandRegistry = new EBusCommandRegistry(EBusConfigurationReader.class, true);

        EBusClient client = new EBusClient(commandRegistry);

        EBusEbusdController controller = new EBusEbusdController("openhab", 8888);

        client.connect(controller, (byte) 0xFF);

        client.getController().addEBusEventListener(new IEBusConnectorEventListener() {

            @Override
            public void onTelegramReceived(byte[] receivedData, Integer sendQueueId) {
                // TODO Auto-generated method stub
                logger.info("Received: " + EBusUtils.toHexDumpString(receivedData).toString());
            }

            @Override
            public void onTelegramException(EBusDataException exception, Integer sendQueueId) {
                System.err.println(exception.getLocalizedMessage());
                // logger.error(exception.getLocalizedMessage());
                // TODO Auto-gen1erated method stub
                // logger.error("ClientTest.xxx().new EBusConnectorEventListener() {...}.onTelegramException()");
            }

            @Override
            public void onConnectionException(Exception e) {
                // TODO Auto-generated method stub
                logger.info("ClientTest.xxx().new EBusConnectorEventListener() {...}.onConnectionException()");
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
        logger.info("Round trip time: {}", client.getController().getLastSendReceiveRoundtripTime());
    }
}
