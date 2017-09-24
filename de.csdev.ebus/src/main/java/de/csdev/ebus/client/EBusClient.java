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
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.command.EBusCommandRegistry;
import de.csdev.ebus.command.EBusCommandUtils;
import de.csdev.ebus.command.IEBusCommandMethod;
import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.command.datatypes.EBusTypeRegistry;
import de.csdev.ebus.core.IEBusConnectorEventListener;
import de.csdev.ebus.core.EBusController;
import de.csdev.ebus.service.device.EBusDeviceTable;
import de.csdev.ebus.service.device.IEBusDeviceTableListener;
import de.csdev.ebus.service.device.EBusDeviceTableService;
import de.csdev.ebus.service.parser.IEBusParserListener;
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

    /**
     *
     */
    public void dispose() {
        controller.interrupt();

        if (configuration != null) {
            configuration.clear();
            configuration = null;
        }

        if (deviceTableService != null) {
            deviceTableService.dispose();
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
    public ByteBuffer buildTelegram(IEBusCommandMethod commandMethod, Byte destinationAddress,
            Map<String, Object> values) throws EBusTypeException {

        if (destinationAddress == null) {
            logger.warn("No destination address defined!");
            return null;
        }

        if (commandMethod == null) {
            logger.warn("Command method is null!");
            return null;
        }

        final byte masterAddress = getDeviceTable().getOwnDevice().getMasterAddress();
        return EBusCommandUtils.buildMasterTelegram(commandMethod, masterAddress, destinationAddress, values);
    }

    /**
     * @return
     */
    public EBusTypeRegistry getDataTypes() {
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

    /**
     * Add a listener
     *
     * @param listener
     */
    public void addEBusDeviceTableListener(IEBusDeviceTableListener listener) {
        getDeviceTable().addEBusDeviceTableListener(listener);
    }

    /**
     * Remove a listener
     *
     * @param listener
     * @return
     */
    public boolean removeEBusDeviceTableListener(IEBusDeviceTableListener listener) {
        return getDeviceTable().removeEBusDeviceTableListener(listener);
    }

    /**
     * Add an eBus listener to receive valid eBus telegrams
     *
     * @param listener
     */
    public void addEBusEventListener(IEBusConnectorEventListener listener) {
        getController().addEBusEventListener(listener);
    }

    /**
     * Remove an eBus listener
     *
     * @param listener
     * @return
     */
    public boolean removeEBusEventListener(IEBusConnectorEventListener listener) {
        return getController().removeEBusEventListener(listener);
    }

    /**
     * Add an eBus listener to receive parsed eBUS telegram values
     *
     * @param listener
     */
    public void addEBusParserListener(IEBusParserListener listener) {
        getResolverService().addEBusParserListener(listener);
    }

    /**
     * Remove an eBus listener
     *
     * @param listener
     * @return
     */
    public boolean removeEBusParserListener(IEBusParserListener listener) {
        return getResolverService().removeEBusParserListener(listener);
    }

    /**
     * @param buffer
     * @param maxAttemps
     * @return
     */
    public Integer addToSendQueue(byte[] buffer, int maxAttemps) {
        return getController().addToSendQueue(buffer, maxAttemps);
    }

    /**
     * @param buffer
     * @return
     */
    /**
     * @param buffer
     * @return
     */
    public Integer addToSendQueue(byte[] buffer) {
        return getController().addToSendQueue(buffer);
    }
}
