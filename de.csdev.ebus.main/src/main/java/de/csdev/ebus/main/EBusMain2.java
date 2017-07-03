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
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
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
import de.csdev.ebus.core.EBusConsts;
import de.csdev.ebus.core.EBusController;
import de.csdev.ebus.core.connection.EBusEmulatorConnection;
import de.csdev.ebus.core.connection.IEBusConnection;
import de.csdev.ebus.utils.EBusUtils;

public class EBusMain2 {

    private static final Logger logger = LoggerFactory.getLogger(EBusMain2.class);

    public static void main(String[] args) {

        try {
        	Reader r = new InputStreamReader(EBusMain2.class.getResourceAsStream("/replay.txt"));
            IEBusConnection connection = new EBusEmulatorConnection(r);

            EBusController controller = new EBusController(connection);
            EBusClient client = new EBusClient(controller);
            
            ConfigurationReader jsonCfgReader = new ConfigurationReader();
            jsonCfgReader.setEBusTypes(client.getDataTypes());

            
            List<EBusCommand> loadConfiguration = jsonCfgReader.loadConfiguration(
            		IEBusConnection.class.getResourceAsStream("/common-configuration2.json"));
            
            client.getConfigurationProvider().addTelegramConfigurationList(loadConfiguration);

            IEBusCommand command = client.getConfigurationProvider().getConfigurationById("common.error");
            Map<String, Object> values = new HashMap<String, Object>();

            byte[] bytes = "HALLO WELT".getBytes();

            values.put("_error_message1", bytes[0]);
            values.put("_error_message2", bytes[1]);
            values.put("_error_message3", bytes[2]);
            values.put("_error_message4", bytes[3]);
            values.put("_error_message5", bytes[4]);
            values.put("_error_message6", bytes[5]);
            values.put("_error_message7", bytes[6]);
            values.put("_error_message8", bytes[7]);
            values.put("_error_message9", bytes[8]);
            values.put("_error_message10", bytes[9]);

            values.put("_error_message10", EBusConsts.ESCAPE);

            ByteBuffer composeEBusTelegram2 = EBusCommandUtils.buildMasterTelegram(command, (byte)0x00, (byte) 0xFF, values);

            logger.info("TEST: Error Command {}", EBusUtils.toHexDumpString(composeEBusTelegram2).toString());

            controller.addToSendQueue(composeEBusTelegram2);

            controller.start();
            
            Thread.sleep(3000);
            client.getDeviceTableService().startDeviceScan();

            // Thread.sleep(3000);

            // service.getDeviceTableService().sendXyyy();

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
