/**
 * Copyright (c) 2017-2023 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.wip;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.cfg.EBusConfigurationReaderException;
import de.csdev.ebus.cfg.std.EBusConfigurationReader;
import de.csdev.ebus.command.EBusCommandRegistry;
import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.command.datatypes.EBusTypeRegistry;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusVaillantBAI00TelegramTest {

    @SuppressWarnings("unused")
    private final Logger logger = LoggerFactory.getLogger(EBusVaillantBAI00TelegramTest.class);

    EBusTypeRegistry types;
    EBusCommandRegistry commandRegistry;

    // @Before
    public void before() throws IOException, EBusConfigurationReaderException, EBusTypeException {

        types = new EBusTypeRegistry();

        InputStream inputStream = EBusConfigurationReader.class
                .getResourceAsStream("/commands/vaillant-bai00-configuration.json");

        if (inputStream == null) {
            throw new RuntimeException("Unable to load json file ...");
        }

        // EBusConfigurationReader cfg = new EBusConfigurationReader(types);

        commandRegistry = new EBusCommandRegistry(EBusConfigurationReader.class, true);
    }

}
