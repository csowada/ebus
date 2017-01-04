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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.aaa.EBusHighLevelService;
import de.csdev.ebus.cfg.EBusConfigurationJsonReader;
import de.csdev.ebus.core.EBusController;
import de.csdev.ebus.core.connection.EBusEmulatorConnection;
import de.csdev.ebus.core.connection.IEBusConnection;

public class EBusMain {

	private static final Logger logger = LoggerFactory.getLogger(EBusMain.class);
	
    @SuppressWarnings("deprecation")
	public static void main(String[] args) {

        try {
            IEBusConnection connection = new EBusEmulatorConnection(new File("src/resources/replay.txt"));

        	EBusController controller = new EBusController(connection);
        	EBusHighLevelService service = new EBusHighLevelService(controller);
            EBusConfigurationJsonReader jsonCfgReader =  new EBusConfigurationJsonReader(service.getConfigurationProvider());
        	
        	File filex = new File("src/resources/common-configuration.json");
			jsonCfgReader.loadConfigurationFile(filex.toURL());
        	
	        controller.start();
	        service.getDeviceTableService().startDeviceScan();
			
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
