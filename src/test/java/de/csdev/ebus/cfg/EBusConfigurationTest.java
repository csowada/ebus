/**
 * Copyright (c) 2017-2025 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.cfg;

import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import de.csdev.ebus.cfg.std.EBusConfigurationReader;
import de.csdev.ebus.command.EBusCommandRegistry;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusConfigurationTest {

    EBusCommandRegistry commandRegistry;

    @Before
    public void before() throws IOException, EBusConfigurationReaderException {
        commandRegistry = new EBusCommandRegistry(EBusConfigurationReader.class, true);
    }

    private void load(String resourceFile) throws EBusConfigurationReaderException, IOException {
        commandRegistry.loadCommandCollection(EBusConfigurationTest.class.getResource(resourceFile));
    }

    @Test
    public void testConfigurationWithoutId() {
        try {
            load("/invalid-cfgs/invalid-configuration-empty-id.json");
            fail("The configuration should fail due to missing property 'id' !");
        } catch (EBusConfigurationReaderException e) {
            // expected result

        } catch(Exception e) {
            fail("Unexpected exception occured!");
        }
    }

    @Test
    public void testConfigurationWithoutLabel() {
        try {
            load("/invalid-cfgs/invalid-configuration-empty-label.json");
            fail("The configuration should fail due to missing property 'label' !");
        } catch (EBusConfigurationReaderException e) {
            // expected result

        } catch(Exception e) {
            fail("Unexpected exception occured!");
        }
    }

    @Test
    public void testConfigurationWithoutDescription() {
        try {
            load("/invalid-cfgs/invalid-configuration-empty-description.json");
            fail("The configuration should fail due to missing property 'description' !");
        } catch (EBusConfigurationReaderException e) {
            // expected result

        } catch(Exception e) {
            fail("Unexpected exception occured!");
        }
    }
}
