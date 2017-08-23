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

import de.csdev.ebus.cfg.datatypes.EBusTypeException;
import de.csdev.ebus.cfg.json.v1.OH1ConfigurationReader;
import de.csdev.ebus.client.EBusClient;
import de.csdev.ebus.command.EBusCommandUtils;
import de.csdev.ebus.command.IEBusCommand;
import de.csdev.ebus.command.IEBusCommandMethod;
import de.csdev.ebus.command.IEBusCommandMethod.Method;
import de.csdev.ebus.core.EBusConsts;
import de.csdev.ebus.core.EBusController;
import de.csdev.ebus.core.connection.EBusEmulatorConnection;
import de.csdev.ebus.core.connection.IEBusConnection;
import de.csdev.ebus.utils.EBusUtils;

public class EBusMain {

    private static final Logger logger = LoggerFactory.getLogger(EBusMain.class);

    public static void main(String[] args) {

        try {
            URL url = EBusMain2.class.getResource("/replay.txt");
            IEBusConnection connection = new EBusEmulatorConnection(url);
            // connection = new EBusTCPConnection("openhab", 8000);

            // EmulatorCapture captureWriter = new EmulatorCapture(new File("src/resources/capture.txt"));
            // connection = new EBusCaptureProxyConnection(connection, captureWriter);

            EBusController controller = new EBusController(connection);
            EBusClient client = new EBusClient(controller, (byte) 0xFF);

            OH1ConfigurationReader jsonCfgReader = new OH1ConfigurationReader();
            jsonCfgReader.setEBusTypes(client.getDataTypes());

            ClassLoader classLoader = controller.getClass().getClassLoader();
            URL resource = classLoader.getResource("common-configuration.json");

            logger.info(">>>>>>>>>>>>>>>>>>" + resource.openStream().read());

            // File filex = new File("src/main/resources/common-configuration.json");
            // jsonCfgReader.loadConfigurationFile(filex.toURL());
            List<IEBusCommand> loadConfiguration = jsonCfgReader.loadConfiguration(resource.openStream());

            client.getConfigurationProvider().addTelegramConfigurationList(loadConfiguration);

            IEBusCommand command = client.getConfigurationProvider().getConfigurationById("common.error", IEBusCommandMethod.Method.GET);
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

            ByteBuffer composeEBusTelegram2 = EBusCommandUtils.buildMasterTelegram(command, (byte) 0x00, (byte) 0xFF,
                    values);

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
        } catch (EBusTypeException e) {
            logger.error("errro1", e);
        }

    }

}
