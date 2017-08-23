/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.service.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.cfg.datatypes.EBusTypeException;
import de.csdev.ebus.command.EBusCommandRegistry;
import de.csdev.ebus.command.EBusCommandUtils;
import de.csdev.ebus.command.IEBusCommandMethod;
import de.csdev.ebus.core.EBusConnectorEventListener;
import de.csdev.ebus.core.EBusDataException;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusParserService implements EBusConnectorEventListener {

    private static final Logger logger = LoggerFactory.getLogger(EBusParserService.class);

    /** the list for listeners */
    private final List<EBusParserListener> listeners = new ArrayList<EBusParserListener>();

    private EBusCommandRegistry commandRegistry;

    public EBusParserService(EBusCommandRegistry configurationProvider) {
        this.commandRegistry = configurationProvider;
    }

    /**
     * Add an eBus listener to receive parsed eBUS telegram values
     *
     * @param listener
     */
    public void addEBusParserListener(EBusParserListener listener) {
        listeners.add(listener);
    }

    /**
     * Remove an eBus listener
     *
     * @param listener
     * @return
     */
    public boolean removeEBusParserListener(EBusParserListener listener) {
        return listeners.remove(listener);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.core.EBusConnectorEventListener#onTelegramReceived(byte[], java.lang.Integer)
     */
    public void onTelegramReceived(byte[] receivedData, Integer sendQueueId) {

        final List<IEBusCommandMethod> commandChannelList = commandRegistry.find(receivedData);
        for (IEBusCommandMethod commandChannel : commandChannelList) {

            try {
                Map<String, Object> map = EBusCommandUtils.decodeTelegram(commandChannel, receivedData);
                fireOnTelegramResolved(commandChannel, map, receivedData, sendQueueId);
            } catch (EBusTypeException e) {
                logger.error("error!", e);
            }
        }

    }

    private void fireOnTelegramResolved(IEBusCommandMethod commandChannel, Map<String, Object> result,
            byte[] receivedData, Integer sendQueueId) {
        for (EBusParserListener listener : listeners) {
            listener.onTelegramResolved(commandChannel, result, receivedData, sendQueueId);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.core.EBusConnectorEventListener#onTelegramException(de.csdev.ebus.core.EBusDataException,
     * java.lang.Integer)
     */
    public void onTelegramException(EBusDataException exception, Integer sendQueueId) {
        logger.debug("ERROR: " + exception.getMessage());
    }

    public void onConnectionException(Exception e) {
        // noop
    }

}
