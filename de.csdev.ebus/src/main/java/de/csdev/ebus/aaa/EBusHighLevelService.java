/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.aaa;

import de.csdev.ebus.cfg.EBusConfigurationProvider;
import de.csdev.ebus.core.EBusController;
import de.csdev.ebus.service.device.EBusDeviceTable;
import de.csdev.ebus.service.device.EBusDeviceTableService;
import de.csdev.ebus.service.parser.EBusParserService;

/**
 * @author Christian Sowada
 *
 */
public class EBusHighLevelService {

    private EBusDeviceTable deviceTable;

    private EBusController controller;

    private EBusConfigurationProvider configurationProvider;

    private EBusParserService resolverService;

    private EBusDeviceTableService deviceTableService;

    public EBusHighLevelService(EBusController controller) {
        this.controller = controller;
        init();
    }

    private void init() {

        deviceTable = new EBusDeviceTable((byte) 0x00);
        configurationProvider = new EBusConfigurationProvider();

        resolverService = new EBusParserService(configurationProvider);
        deviceTableService = new EBusDeviceTableService(controller, configurationProvider, deviceTable);

        controller.addEBusEventListener(resolverService);
        resolverService.addEBusParserListener(deviceTableService);

        deviceTable.addEBusDeviceTableListener(deviceTableService);

    }

    public EBusController getController() {
        return controller;
    }

    public EBusConfigurationProvider getConfigurationProvider() {
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
