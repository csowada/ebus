/**
 * Copyright (c) 2010-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.cfg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.core.EBusConnectorEventListener;
import de.csdev.ebus.core.EBusController;
import de.csdev.ebus.utils.EBusUtils;

public class EBusNNN implements EBusConnectorEventListener {

    private static final Logger logger = LoggerFactory.getLogger(EBusNNN.class);

    private EBusController controller;

    public EBusNNN(EBusController controller) {
        this.controller = controller;
        this.controller.addEBusEventListener(this);
    }

    public void close() {
        this.controller.removeEBusEventListener(this);
        this.controller = null;
    }

    @Override
    public void onTelegramReceived(byte[] receivedData, Integer sendQueueId) {
        logger.info("DATA: " + EBusUtils.toHexDumpString(receivedData));
    }

}
