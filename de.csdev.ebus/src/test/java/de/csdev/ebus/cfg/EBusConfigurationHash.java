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

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.cfg.std.EBusConfigurationReader;
import de.csdev.ebus.command.EBusCommandRegistry;
import de.csdev.ebus.command.IEBusCommand;
import de.csdev.ebus.command.IEBusCommandCollection;
import de.csdev.ebus.command.IEBusCommandMethod;
import de.csdev.ebus.command.datatypes.EBusTypeRegistry;
import de.csdev.ebus.core.EBusConsts;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusConfigurationHash {

    private static final Logger logger = LoggerFactory.getLogger(EBusConfigurationHash.class);

    private EBusCommandRegistry xx() throws IOException, EBusConfigurationReaderException {

        URL url = EBusConfigurationReader.class.getResource("/commands/common-configuration.json");

        if (url == null) {
            throw new RuntimeException("Unable to load json file ...");
        }

        EBusTypeRegistry types = new EBusTypeRegistry();

        EBusConfigurationReader cfg = new EBusConfigurationReader();
        cfg.setEBusTypes(types);

        EBusCommandRegistry commandRegistry = new EBusCommandRegistry(EBusConfigurationReader.class);
        commandRegistry.loadCommandCollection(url);

        return commandRegistry;
    }

    @Test
    public void testIdentification() throws IOException, EBusConfigurationReaderException {

        EBusCommandRegistry reg1 = xx();
        EBusCommandRegistry reg2 = xx();

        IEBusCommandCollection collection1 = reg1.getCommandCollection(EBusConsts.COLLECTION_STD);
        IEBusCommandCollection collection2 = reg2.getCommandCollection(EBusConsts.COLLECTION_STD);

        for (IEBusCommand command1 : collection1.getCommands()) {
            IEBusCommand command2 = collection2.getCommand(command1.getId());

            for (IEBusCommandMethod method1 : command1.getCommandMethods()) {
                IEBusCommandMethod method2 = command2.getCommandMethod(method1.getMethod());

                logger.debug("Check command {}, H1:{}, H2:{}", method2.getMethod(), method2.hashCode(),
                        method2.hashCode());
                Assert.assertEquals(method2.hashCode(), method2.hashCode());

            }

            logger.debug("Check command {}, H1:{}, H2:{}", command1.getId(), command1.hashCode(), command2.hashCode());
            // Assert.assertEquals(command1.hashCode(), command2.hashCode());
        }

    }
}
