/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.main;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.cfg.json.v1.EBusConfigurationJsonReader;
import de.csdev.ebus.cfg.json.v1.EBusConfigurationTelegram;
import de.csdev.ebus.cfg.json.v1.xxx.EBusTelegramComposer;
import de.csdev.ebus.client.EBusClient;
import de.csdev.ebus.core.EBusConsts;
import de.csdev.ebus.core.EBusController;
import de.csdev.ebus.core.connection.EBusEmulatorConnection;
import de.csdev.ebus.core.connection.IEBusConnection;
import de.csdev.ebus.utils.EBusUtils;

public class EBusMain {

    private static final Logger logger = LoggerFactory.getLogger(EBusMain.class);

    @SuppressWarnings("deprecation")
    public static void main(String[] args) {

        try {
            IEBusConnection connection = new EBusEmulatorConnection(new File("src/main/resources/test/replay.txt"));
            // connection = new EBusTCPConnection("openhab", 8000);

            // EmulatorCapture captureWriter = new EmulatorCapture(new File("src/resources/capture.txt"));
            // connection = new EBusCaptureProxyConnection(connection, captureWriter);

            EBusController controller = new EBusController(connection);
            EBusClient service = new EBusClient(controller);
            EBusConfigurationJsonReader jsonCfgReader = new EBusConfigurationJsonReader(
                    service.getConfigurationProvider());

            ClassLoader classLoader = controller.getClass().getClassLoader();
            URL resource = classLoader.getResource("common-configuration.json");

            logger.info(">>>>>>>>>>>>>>>>>>" + resource.openStream().read());

            File filex = new File("src/main/resources/common-configuration.json");
            // jsonCfgReader.loadConfigurationFile(filex.toURL());
            jsonCfgReader.loadConfigurationFile(resource);

            EBusConfigurationTelegram command = service.getConfigurationProvider().getConfigurationById("common.error");
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

            byte[] composeEBusTelegram2 = EBusTelegramComposer.composeEBusTelegram(command, null, (byte) 0xFF, values);

            logger.info("TEST: Error Command {}", EBusUtils.toHexDumpString(composeEBusTelegram2).toString());

            controller.addToSendQueue(composeEBusTelegram2);

            controller.start();
            // service.getDeviceTableService().startDeviceScan();

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
