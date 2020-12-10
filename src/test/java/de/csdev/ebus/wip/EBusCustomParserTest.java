/**
 * Copyright (c) 2016-2020 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.wip;

import static org.junit.Assert.*;

import java.net.URL;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.cfg.std.EBusConfigurationReader;
import de.csdev.ebus.command.EBusCommandException;
import de.csdev.ebus.command.EBusCommandRegistry;
import de.csdev.ebus.command.EBusCommandUtils;
import de.csdev.ebus.command.IEBusCommand;
import de.csdev.ebus.command.IEBusCommandCollection;
import de.csdev.ebus.command.IEBusCommandMethod;
import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.utils.EBusUtils;

public class EBusCustomParserTest {

    public void xx() {

    }

    private final Logger logger = LoggerFactory.getLogger(EBusCustomParserTest.class);

    // @Test
    @SuppressWarnings("null")
    public void test_BuildMasterTelegram() {

        URL url = EBusConfigurationReader.class.getResource("/custom.json");
        assertNotNull(url);

        EBusCommandRegistry registry = new EBusCommandRegistry(EBusConfigurationReader.class);
        registry.loadCommandCollection(url);

        for (IEBusCommandCollection collection : registry.getCommandCollections()) {
            for (IEBusCommand command : collection.getCommands()) {
                for (IEBusCommandMethod commandChannel : command.getCommandMethods()) {

                    try {
                        ByteBuffer masterTelegram = EBusCommandUtils.buildMasterTelegram(commandChannel, (byte) 0xFF,
                                (byte) 0x50, null);

                        logger.info(String.format("%-9s| %-40s| %-8s| %s", commandChannel.getMethod(), command.getId(),
                                collection.getId(), EBusUtils.toHexDumpString(masterTelegram)));

                    } catch (EBusTypeException e) {
                        e.printStackTrace();
                        fail();
                    } catch (EBusCommandException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

            }
        }
    }

}
