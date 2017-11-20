/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.service.parser;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.command.EBusCommandRegistry;
import de.csdev.ebus.command.EBusCommandUtils;
import de.csdev.ebus.command.IEBusCommandMethod;
import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.core.EBusConnectorEventListener;
import de.csdev.ebus.utils.EBusUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusParserService extends EBusConnectorEventListener {

    private static final Logger logger = LoggerFactory.getLogger(EBusParserService.class);

    /** the list for listeners */
    private final List<IEBusParserListener> listeners = new CopyOnWriteArrayList<IEBusParserListener>();

    /** */
    private EBusCommandRegistry commandRegistry;

    /**
     * @param configurationProvider
     */
    public EBusParserService(EBusCommandRegistry configurationProvider) {
        this.commandRegistry = configurationProvider;
    }

    /**
     *
     */
    public void dispose() {
        if (listeners != null) {
            listeners.clear();
        }
    }

    /**
     * Add an eBUS listener to receive parsed eBUS telegram values
     *
     * @param listener
     */
    public void addEBusParserListener(IEBusParserListener listener) {
        listeners.add(listener);
    }

    /**
     * Remove an eBUS listener
     *
     * @param listener
     * @return
     */
    public boolean removeEBusParserListener(IEBusParserListener listener) {
        return listeners.remove(listener);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.core.EBusConnectorEventListener#onTelegramReceived(byte[], java.lang.Integer)
     */
    @Override
    public void onTelegramReceived(byte[] receivedData, Integer sendQueueId) {

        final List<IEBusCommandMethod> commandChannelList = commandRegistry.find(receivedData);

        if (commandChannelList == null || commandChannelList.isEmpty()) {
            if (logger.isTraceEnabled()) {
                logger.trace("No command method matches the telegram {} ...", EBusUtils.toHexDumpString(receivedData));
            }
            fireOnTelegramFailed(null, receivedData, sendQueueId, "No command method matches the telegram!");
        }

        for (IEBusCommandMethod commandChannel : commandChannelList) {

            try {
                Map<String, Object> map = EBusCommandUtils.decodeTelegram(commandChannel, receivedData);
                fireOnTelegramResolved(commandChannel, map, receivedData, sendQueueId);
            } catch (EBusTypeException e) {
                fireOnTelegramFailed(commandChannel, receivedData, sendQueueId, e.getMessage());
                logger.error("error!", e);
            }
        }

    }

    /**
     * @param commandChannel
     * @param result
     * @param receivedData
     * @param sendQueueId
     */
    private void fireOnTelegramResolved(IEBusCommandMethod commandChannel, Map<String, Object> result,
            byte[] receivedData, Integer sendQueueId) {

        for (IEBusParserListener listener : listeners) {
            try {
                listener.onTelegramResolved(commandChannel, result, receivedData, sendQueueId);
            } catch (Exception e) {
                logger.error("Error while firing onTelegramResolved events!", e);
            }
        }
    }

    /**
     * @param commandChannel
     * @param result
     * @param receivedData
     * @param sendQueueId
     */
    private void fireOnTelegramFailed(IEBusCommandMethod commandChannel, byte[] receivedData, Integer sendQueueId,
            String exceptionMessage) {

        for (IEBusParserListener listener : listeners) {
            try {
                listener.onTelegramResolveFailed(commandChannel, receivedData, sendQueueId, exceptionMessage);
            } catch (Exception e) {
                logger.error("Error while firing onTelegramResolved events!", e);
            }
        }
    }

}
