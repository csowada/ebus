/**
 * Copyright (c) 2017-2023 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.cfg;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URL;

import org.junit.Test;

import de.csdev.ebus.cfg.std.EBusConfigurationReader;
import de.csdev.ebus.command.EBusCommandRegistry;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusConfigurationBundleTest {

    @Test
    public void test_BuildMasterTelegram() throws EBusConfigurationReaderException, IOException {

        URL url = EBusCommandRegistry.class.getResource("/index-configuration.json");
        assertNotNull(url);

        EBusCommandRegistry registry = new EBusCommandRegistry(EBusConfigurationReader.class);

        registry.loadCommandCollectionBundle(url);
        assertFalse("collection should not be empty!", registry.getCommandCollections().isEmpty());

        registry.clear();
        assertTrue("collection should be empty!", registry.getCommandCollections().isEmpty());

        registry.loadBuildInCommandCollections();
        assertFalse("collection should not be empty!", registry.getCommandCollections().isEmpty());

    }

}
