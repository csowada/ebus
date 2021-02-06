/**
 * Copyright (c) 2017-2021 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.wip;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import de.csdev.ebus.cfg.EBusConfigurationReaderException;
import de.csdev.ebus.cfg.std.EBusConfigurationReader;
import de.csdev.ebus.command.EBusCommandRegistry;
import de.csdev.ebus.command.IEBusCommand;
import de.csdev.ebus.command.IEBusCommandMethod;
import de.csdev.ebus.command.IEBusCommandMethod.Method;
import de.csdev.ebus.command.IEBusCommandMethod.Type;
import de.csdev.ebus.command.IEBusValue;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusConfigurationTemplateTest {

    // @Test
    public void test_BuildMasterTelegram() throws EBusConfigurationReaderException, IOException {

        EBusCommandRegistry registry = new EBusCommandRegistry(EBusConfigurationReader.class);
        registry.loadBuildInCommandCollections();

        assertFalse("collection should not be empty!", registry.getCommandCollections().isEmpty());

        registry.clear();
        assertTrue("collection should be empty!", registry.getCommandCollections().isEmpty());

        registry.loadBuildInCommandCollections();
        assertFalse("collection should not be empty!", registry.getCommandCollections().isEmpty());

        IEBusCommand commandById = registry.getCommandById("bai", "boiler.temp_outletx");
        assertNotNull(commandById);

        IEBusCommandMethod commandMethod = commandById.getCommandMethod(Method.GET);
        assertNotNull(commandMethod);

        Type type = commandMethod.getType();
        assertNotNull(type);
        List<@NonNull IEBusValue> slaveTypes = commandMethod.getSlaveTypes();

        // System.out.println(slaveTypes);
        if (slaveTypes != null) {
            for (IEBusValue ieBusValue : slaveTypes) {
                System.out.println(ieBusValue);
            }
        }

        System.out.println("EBusConfigurationTemplateTest.test_BuildMasterTelegram()");
    }

}
