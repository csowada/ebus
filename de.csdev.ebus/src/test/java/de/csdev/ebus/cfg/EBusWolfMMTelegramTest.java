/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.cfg;

import java.io.IOException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import de.csdev.ebus.TestUtils;
import de.csdev.ebus.cfg.std.EBusConfigurationReader;
import de.csdev.ebus.command.EBusCommandRegistry;
import de.csdev.ebus.command.datatypes.EBusTypeRegistry;
import de.csdev.ebus.utils.EBusUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusWolfMMTelegramTest {

    EBusTypeRegistry types;
    EBusCommandRegistry commandRegistry;

    @Before
    public void before() throws IOException, EBusConfigurationReaderException {

        types = new EBusTypeRegistry();

        URL url = EBusConfigurationReader.class.getResource("/commands/wolf-mm-configuration.json");

        if (url == null) {
            throw new RuntimeException("Unable to load json file ...");
        }

        EBusConfigurationReader cfg = new EBusConfigurationReader();
        cfg.setEBusTypes(types);

        commandRegistry = new EBusCommandRegistry(EBusConfigurationReader.class);
        commandRegistry.loadCommandCollection(url);
    }

    @Test
    public void testSolarCommands() {
        TestUtils.canResolve(commandRegistry, EBusUtils
                .toByteArray("70 51 50 14 07 00 00 2C 1A 14 00 14 58 00 09 00 00 E6 12 00 D8 14 64 00 42 00 AA"));

        TestUtils.canResolve(commandRegistry, EBusUtils
                .toByteArray("70 51 50 14 07 41 00 05 00 17 00 5A 0D 00 09 00 40 80 16 00 D8 14 64 00 4B 00 AA"));

    }

}
