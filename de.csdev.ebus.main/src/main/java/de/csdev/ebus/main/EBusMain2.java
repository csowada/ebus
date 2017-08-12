/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.main;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.cfg.ConfigurationReader;
import de.csdev.ebus.client.EBusClient;
import de.csdev.ebus.command.IEBusCommand;
import de.csdev.ebus.core.EBusConnectorEventListener;
import de.csdev.ebus.core.EBusController;
import de.csdev.ebus.core.EBusDataException;
import de.csdev.ebus.core.connection.EBusEmulatorConnection;
import de.csdev.ebus.core.connection.EBusTCPConnection;
import de.csdev.ebus.core.connection.IEBusConnection;
import de.csdev.ebus.service.parser.EBusParserListener;
import de.csdev.ebus.utils.EBusUtils;

public class EBusMain2 {

    private static final Logger logger = LoggerFactory.getLogger(EBusMain2.class);

    public static void main(String[] args) {

        try {
            URL url = EBusMain2.class.getResource("/replay-common.txt");
            IEBusConnection connection = new EBusEmulatorConnection(url);
            connection = new EBusTCPConnection("openhab", 8000);
            EBusController controller = new EBusController(connection);
            EBusClient client = new EBusClient(controller, (byte) 0xFF);

            ConfigurationReader jsonCfgReader = new ConfigurationReader();
            jsonCfgReader.setEBusTypes(client.getDataTypes());

            List<IEBusCommand> loadConfiguration = jsonCfgReader.loadConfiguration(
                    IEBusConnection.class.getResourceAsStream("/commands/common-configuration.json"));

            client.getConfigurationProvider().addTelegramConfigurationList(loadConfiguration);

            loadConfiguration = jsonCfgReader.loadConfiguration(
                    IEBusConnection.class.getResourceAsStream("/commands/wolf-cgb2-configuration.json"));

            client.getConfigurationProvider().addTelegramConfigurationList(loadConfiguration);

            loadConfiguration = jsonCfgReader.loadConfiguration(
                    IEBusConnection.class.getResourceAsStream("/commands/wolf-sm1-configuration.json"));

            client.getConfigurationProvider().addTelegramConfigurationList(loadConfiguration);

            client.getResolverService().addEBusParserListener(new EBusParserListener() {

                public void onTelegramResolved(IEBusCommand command, Map<String, Object> result, byte[] receivedData,
                        Integer sendQueueId) {

                    logger.info("Telegram Resolved: " + command.getDescription());
                    logger.info(result.toString());

                }
            });

            controller.addEBusEventListener(new EBusConnectorEventListener() {

                public void onTelegramReceived(byte[] receivedData, Integer sendQueueId) {
                    logger.trace("onTelegramReceived > " + EBusUtils.toHexDumpString(receivedData));
                }

                public void onTelegramException(EBusDataException exception, Integer sendQueueId) {
                    logger.trace("onTelegramException > " + exception.toString());
                }

                public void onConnectionException(Exception e) {

                }
            });

            // byte[] byteArray = EBusUtils.toByteArray("71 FE 50 17 10 08 91 05 01 C8 01 00 80 00 80 00 80 00 80 00 80
            // A0 AA");
            // IEBusCommand command = client.getConfigurationProvider().getConfigurationById("solar.solar_data",
            // Type.BROADCAST);
            // ByteBuffer mask = EBusCommandUtils.getMasterTelegramMask(command);
            //
            // logger.warn(EBusUtils.toHexDumpString(mask).toString());

            // if(true)
            // return;

            controller.start();
            Thread.sleep(3000);

            // main thread wait
            controller.join();

        } catch (InterruptedException e) {
            logger.error("errro1", e);

        } catch (MalformedURLException e) {
            logger.error("errro1", e);

        } catch (IOException e) {
            logger.error("errro1", e);
        }

    }

}
