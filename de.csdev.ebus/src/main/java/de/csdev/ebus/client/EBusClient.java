/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.client;

import de.csdev.ebus.cfg.datatypes.EBusTypes;
import de.csdev.ebus.command.EBusCommandRegistry;
import de.csdev.ebus.core.EBusController;
import de.csdev.ebus.service.device.EBusDeviceTable;
import de.csdev.ebus.service.device.EBusDeviceTableService;
import de.csdev.ebus.service.parser.EBusParserService;

/**
 * @author Christian Sowada
 *
 */
public class EBusClient {

    private EBusDeviceTable deviceTable;

    private EBusController controller;

    private EBusCommandRegistry configurationProvider;

    private EBusParserService resolverService;

    private EBusDeviceTableService deviceTableService;

    private EBusTypes dataTypes;
    
    public EBusClient(EBusController controller) {
        this.controller = controller;
        init();
    }

    private void init() {

    	dataTypes = new EBusTypes();
        deviceTable = new EBusDeviceTable((byte) 0x00);
        configurationProvider = new EBusCommandRegistry();
        
        resolverService = new EBusParserService(configurationProvider);
        deviceTableService = new EBusDeviceTableService(controller, configurationProvider, deviceTable);

        controller.addEBusEventListener(resolverService);
        resolverService.addEBusParserListener(deviceTableService);

        deviceTable.addEBusDeviceTableListener(deviceTableService);

    }

    public EBusTypes getDataTypes() {
		return dataTypes;
	}

	public EBusController getController() {
        return controller;
    }

    public EBusCommandRegistry getConfigurationProvider() {
        return configurationProvider;
    }

    public EBusParserService getResolverService() {
        return resolverService;
    }

    public EBusDeviceTableService getDeviceTableService() {
        return deviceTableService;
    }

    public EBusDeviceTable getDeviceTable() {
        return deviceTable;
    }
}
