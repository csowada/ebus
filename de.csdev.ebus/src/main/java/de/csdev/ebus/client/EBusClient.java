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

    public void dispose() {
        controller.interrupt();

        if (configuration != null) {
            configuration.clear();
            configuration = null;
        }

        if (deviceTableService != null) {
            deviceTableService.close();
            deviceTableService = null;
        }

        if (deviceTable != null) {
            deviceTable.dispose();
            deviceTable = null;
        }

        if (resolverService != null) {
            resolverService.dispose();
            resolverService = null;
        }
    }

    /**
     *
     */
    public EBusClient() {
        this(new EBusClientConfiguration());
    }

    /**
     * @param configuration
     */
    public EBusClient(EBusClientConfiguration configuration) {
        this.configuration = configuration;

        deviceTable = new EBusDeviceTable();
        resolverService = new EBusParserService(configuration.configurationProvider);
    }

    /**
     * @param controller
     * @param masterAddress
     */
    public void connect(EBusController controller, byte masterAddress) {

        this.controller = controller;

        this.controller.addEBusEventListener(resolverService);

        deviceTable.setOwnAddress(masterAddress);

        deviceTableService = new EBusDeviceTableService(controller, configuration.configurationProvider, deviceTable);

        resolverService.addEBusParserListener(deviceTableService);
        deviceTable.addEBusDeviceTableListener(deviceTableService);
    }

    /**
     * @param commandMethod
     * @param destinationAddress
     * @return
     * @throws EBusTypeException
     */
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

    /**
     * @param commandId
     * @param type
     * @param destinationAddress
     * @return
     * @throws EBusTypeException
     */
    public ByteBuffer buildPollingTelegram(String commandId, IEBusCommandMethod.Method type, Byte destinationAddress)
            throws EBusTypeException {

        final IEBusCommandMethod commandMethod = getConfigurationProvider().getConfigurationById(commandId,
                IEBusCommandMethod.Method.GET);

        return buildPollingTelegram(commandMethod, destinationAddress);
    }

    /**
     * @param commandId
     * @param slaveAddress
     * @return
     */
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

    /**
     * @return
     */
    public EBusTypes getDataTypes() {
        return configuration.dataTypes;
    }

    /**
     * @return
     */
    public EBusController getController() {
        return controller;
    }

    /**
     * @return
     */
    public EBusCommandRegistry getConfigurationProvider() {
        return configuration.configurationProvider;
    }

    /**
     * @return
     */
    public EBusParserService getResolverService() {
        return resolverService;
    }

    /**
     * @return
     */
    public EBusDeviceTableService getDeviceTableService() {
        return deviceTableService;
    }

    /**
     * @return
     */
    public EBusDeviceTable getDeviceTable() {
        return deviceTable;
    }
}
