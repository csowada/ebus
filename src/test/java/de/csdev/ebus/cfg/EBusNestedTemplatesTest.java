/**
 * Copyright (c) 2016-2020 by the respective copyright holders.
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
import java.nio.ByteBuffer;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.cfg.std.EBusConfigurationReader;
import de.csdev.ebus.command.EBusCommandException;
import de.csdev.ebus.command.EBusCommandRegistry;
import de.csdev.ebus.command.EBusCommandUtils;
import de.csdev.ebus.command.IEBusCommandMethod;
import de.csdev.ebus.command.IEBusValue;
import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.command.datatypes.IEBusType;
import de.csdev.ebus.utils.EBusUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusNestedTemplatesTest {

    private static final Logger logger = LoggerFactory.getLogger(EBusNestedTemplatesTest.class);

    static EBusCommandRegistry commandRegistry;

    @BeforeClass
    public static void before() throws IOException, EBusConfigurationReaderException {
        commandRegistry = new EBusCommandRegistry(EBusConfigurationReader.class);

        URL url = EBusNestedTemplatesTest.class.getResource("/nested-templates/FirstTemplate.json");
        assertNotNull(url);

        commandRegistry.loadCommandCollection(url);

        url = EBusNestedTemplatesTest.class.getResource("/nested-templates/SecondTemplate.json");
        assertNotNull(url);

        commandRegistry.loadCommandCollection(url);

        url = EBusNestedTemplatesTest.class.getResource("/nested-templates/ThirdCommand.json");
        assertNotNull(url);

        commandRegistry.loadCommandCollection(url);
    }

    @Test
    public void test1() {

        IEBusCommandMethod commandMethod = commandRegistry.getCommandMethodById("et", "test.nextest-block",
                IEBusCommandMethod.Method.SET);

        assertNotNull("Command et.test.nextest-blockn not found!", commandMethod);

        try {
            ByteBuffer buffer = EBusCommandUtils.buildMasterTelegram(commandMethod, (byte) 0x00, (byte) 0xFF, null);
            assertNotNull("Unable to compose byte buffer for command", buffer);

            logger.debug(EBusUtils.toHexDumpString(buffer).toString());

        } catch (EBusTypeException e) {
            logger.error("error!", e);
            fail();
        } catch (EBusCommandException e) {
            logger.error("error!", e);
            fail();
        }

    }

    @Test
    public void test2() throws EBusTypeException {

        IEBusCommandMethod commandMethod = commandRegistry.getCommandMethodById("et", "test.tth",
                IEBusCommandMethod.Method.SET);

        assertNotNull("Command et.test.tth not found!", commandMethod);

        List<@NonNull IEBusValue> masterTypes = commandMethod.getMasterTypes();
        assertNotNull(masterTypes);

        IEBusValue value = masterTypes.get(0);
        IEBusType<?> type = value.getType();

        @SuppressWarnings("unused")
        Object decode = type.decode(new byte[] { (byte) 0x90 });

        Map<String, Object> values = new HashMap<String, Object>();
        values.put("tth", new GregorianCalendar());

        try {
            ByteBuffer buffer = EBusCommandUtils.buildMasterTelegram(commandMethod, (byte) 0x00, (byte) 0xFF, values);
            assertNotNull("Unable to compose byte buffer for command", buffer);

            logger.debug(EBusUtils.toHexDumpString(buffer).toString());

        } catch (EBusTypeException e) {
            logger.error("error!", e);
            fail();
        } catch (EBusCommandException e) {
            logger.error("error!", e);
            fail();
        }

    }

    @Test
    public void test3() throws EBusTypeException {

        IEBusCommandMethod commandMethod = commandRegistry.getCommandMethodById("et", "test.to",
                IEBusCommandMethod.Method.SET);

        assertNotNull("Command et.test.tth not found!", commandMethod);

        List<@NonNull IEBusValue> masterTypes = commandMethod.getMasterTypes();
        assertNotNull(masterTypes);

        IEBusValue value = masterTypes.get(0);
        IEBusType<?> type = value.getType();

        @SuppressWarnings("unused")
        Object decode = type.decode(new byte[] { (byte) 0x90 });

        Map<String, Object> values = new HashMap<String, Object>();
        values.put("value", new GregorianCalendar());

        try {
            ByteBuffer buffer = EBusCommandUtils.buildMasterTelegram(commandMethod, (byte) 0x00, (byte) 0xFF, values);
            assertNotNull("Unable to compose byte buffer for command", buffer);

            logger.debug(EBusUtils.toHexDumpString(buffer).toString());

        } catch (EBusTypeException e) {
            logger.error("error!", e);
            fail();
        } catch (EBusCommandException e) {
            logger.error("error!", e);
            fail();
        }

    }
}
