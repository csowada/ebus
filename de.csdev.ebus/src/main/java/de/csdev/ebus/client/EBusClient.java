/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.client;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.cfg.datatypes.EBusTypeException;
import de.csdev.ebus.cfg.datatypes.EBusTypes;
import de.csdev.ebus.command.EBusCommandRegistry;
import de.csdev.ebus.command.EBusCommandUtils;
import de.csdev.ebus.command.IEBusCommandMethod;
import de.csdev.ebus.core.EBusController;
import de.csdev.ebus.service.device.EBusDeviceTable;
import de.csdev.ebus.service.device.EBusDeviceTableService;
import de.csdev.ebus.service.parser.EBusParserService;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusClient {

    private final Logger logger = LoggerFactory.getLogger(EBusClient.class);

    private EBusDeviceTable deviceTable;

    private EBusController controller;

    private EBusParserService resolverService;

    private EBusDeviceTableService deviceTableService;

    private EBusClientConfiguration configuration;

    public EBusClient(EBusController controller, byte masterAddress) {
        this.controller = controller;
        this.configuration = new EBusClientConfiguration();
        init(masterAddress);
    }

    public EBusClient(EBusController controller, EBusClientConfiguration configuration, byte masterAddress) {
        this.controller = controller;
        this.configuration = configuration;
        init(masterAddress);
    }

    private void init(byte masterAddress) {

        deviceTable = new EBusDeviceTable(masterAddress);

        resolverService = new EBusParserService(configuration.configurationProvider);
        deviceTableService = new EBusDeviceTableService(controller, configuration.configurationProvider, deviceTable);

        controller.addEBusEventListener(resolverService);
        resolverService.addEBusParserListener(deviceTableService);

        deviceTable.addEBusDeviceTableListener(deviceTableService);

    }

    public ByteBuffer buildPollingTelegram(IEBusCommandMethod commandMethod, Byte destinationAddress)
            throws EBusTypeException {

        if (destinationAddress == null) {
            logger.warn("No destination address defined!");
            return null;
        }

        if (commandMethod == null) {
            logger.warn("Command method is null!");
            return null;
        }

        final byte masterAddress = getDeviceTable().getOwnDevice().getMasterAddress();
        return EBusCommandUtils.buildMasterTelegram(commandMethod, masterAddress, destinationAddress, null);
    }

    public ByteBuffer buildPollingTelegram(String commandId, IEBusCommandMethod.Method type, Byte destinationAddress)
            throws EBusTypeException {

        final IEBusCommandMethod commandMethod = getConfigurationProvider().getConfigurationById(commandId,
                IEBusCommandMethod.Method.GET);

        return buildPollingTelegram(commandMethod, destinationAddress);
    }

    public boolean pollCommand(String commandId, Byte slaveAddress) {

        try {
            final ByteBuffer buffer = buildPollingTelegram(commandId, IEBusCommandMethod.Method.GET, slaveAddress);
            if (buffer == null) {
                logger.warn("Unable to build polling telegram!");
                return false;
            }

            getController().addToSendQueue(buffer);
            return true;

        } catch (EBusTypeException e) {
            logger.error("error!", e);
        }

        return false;
    }

    public EBusTypes getDataTypes() {
        return configuration.dataTypes;
    }

    public EBusController getController() {
        return controller;
    }

    public EBusCommandRegistry getConfigurationProvider() {
        return configuration.configurationProvider;
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
