/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.basic;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.cfg.EBusConfigurationReader;
import de.csdev.ebus.cfg.EBusConfigurationReaderException;
import de.csdev.ebus.cfg.datatypes.EBusTypes;
import de.csdev.ebus.command.EBusCommandRegistry;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusVaillantBAI00TelegramTest {

    private static final Logger logger = LoggerFactory.getLogger(EBusVaillantBAI00TelegramTest.class);

    EBusTypes types;
    EBusCommandRegistry commandRegistry;

    @Before
    public void before() throws IOException, EBusConfigurationReaderException {

        types = new EBusTypes();

        InputStream inputStream = EBusConfigurationReader.class
                .getResourceAsStream("/commands/vaillant-bai00-configuration.json");

        if (inputStream == null) {
            throw new RuntimeException("Unable to load json file ...");
        }

        EBusConfigurationReader cfg = new EBusConfigurationReader();
        cfg.setEBusTypes(types);

        commandRegistry = new EBusCommandRegistry();
        commandRegistry.addCommandCollection(cfg.loadConfigurationCollection(inputStream));
    }

}
