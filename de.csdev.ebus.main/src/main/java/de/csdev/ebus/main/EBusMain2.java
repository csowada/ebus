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
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.cfg.ConfigurationReader;
import de.csdev.ebus.client.EBusClient;
import de.csdev.ebus.command.EBusCommand;
import de.csdev.ebus.command.EBusCommandUtils;
import de.csdev.ebus.command.IEBusCommand;
import de.csdev.ebus.core.EBusConnectorEventListener;
import de.csdev.ebus.core.EBusConsts;
import de.csdev.ebus.core.EBusController;
import de.csdev.ebus.core.EBusDataException;
import de.csdev.ebus.core.connection.EBusEmulatorConnection;
import de.csdev.ebus.core.connection.IEBusConnection;
import de.csdev.ebus.service.parser.EBusParserListener;
import de.csdev.ebus.utils.EBusUtils;

public class EBusMain2 {

    private static final Logger logger = LoggerFactory.getLogger(EBusMain2.class);

    public static void main(String[] args) {

        try {
        	URL url = EBusMain2.class.getResource("/replay-common.txt");
            IEBusConnection connection = new EBusEmulatorConnection(url);

            EBusController controller = new EBusController(connection);
            EBusClient client = new EBusClient(controller);
            
            ConfigurationReader jsonCfgReader = new ConfigurationReader();
            jsonCfgReader.setEBusTypes(client.getDataTypes());

            
            List<EBusCommand> loadConfiguration = jsonCfgReader.loadConfiguration(
            		IEBusConnection.class.getResourceAsStream("/common-configuration2.json"));
            
            client.getConfigurationProvider().addTelegramConfigurationList(loadConfiguration);

            IEBusCommand command = client.getConfigurationProvider().getConfigurationById("common.error");
            Map<String, Object> values = new HashMap<String, Object>();
            values.put("error", "1234567890");


            ByteBuffer composeEBusTelegram2 = EBusCommandUtils.buildMasterTelegram(command, (byte)0x00, (byte) 0xFE, values);
            logger.info("TEST: Error Command {}", EBusUtils.toHexDumpString(composeEBusTelegram2).toString());

            
            client.getResolverService().addEBusParserListener(new EBusParserListener() {
				
				public void onTelegramResolved(IEBusCommand command, Map<String, Object> result, byte[] receivedData,
						Integer sendQueueId) {
					// TODO Auto-generated method stub
					System.out.println("Telegram Resolved: " + command.getDescription());
					System.out.println(result.toString());
					
				}
			});
            
            controller.addEBusEventListener(new EBusConnectorEventListener() {
				
				public void onTelegramReceived(byte[] receivedData, Integer sendQueueId) {
					// TODO Auto-generated method stub
					System.out
							.println("EBusMain2.main(...).new EBusConnectorEventListener() {...}.onTelegramReceived()"+EBusUtils.toHexDumpString(receivedData));
				}
				
				public void onTelegramException(EBusDataException exception, Integer sendQueueId) {
					// TODO Auto-generated method stub
					System.out.println(
							"EBusMain2.main(...).new EBusConnectorEventListener() {...}.onTelegramException()" + exception.toString());
				}
			});
            
            
            controller.start();
//            Thread.sleep(3000);
            
            controller.addToSendQueue(composeEBusTelegram2);

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
