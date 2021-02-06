/**
 * Copyright (c) 2017-2021 by the respective copyright holders.
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
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.cfg.std.EBusConfigurationReader;
import de.csdev.ebus.command.EBusCommandException;
import de.csdev.ebus.command.EBusCommandRegistry;
import de.csdev.ebus.command.EBusCommandUtils;
import de.csdev.ebus.command.IEBusCommandMethod;
import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.utils.EBusUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusBitTypeTest {

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(EBusBitTypeTest.class);

    static EBusCommandRegistry commandRegistry;

    @BeforeClass
    public static void before() throws IOException, EBusConfigurationReaderException {
        commandRegistry = new EBusCommandRegistry(EBusConfigurationReader.class);

        URL url = EBusBitTypeTest.class.getResource("/common-configuration.json");
        assertNotNull(url);

        commandRegistry.loadCommandCollection(url);
    }

    @Test
    public void testBits() throws EBusTypeException {
        /*
         * 2014-10-23 16:10:30 - >>> Betriebsdaten des Feuerungsautomaten an den Regler - Block 1
         * 2014-10-23 16:10:30 - >>> auto_stroker.state_valve2 false Valve2
         * 2014-10-23 16:10:30 - >>> auto_stroker.state_ldw false LDW
         * 2014-10-23 16:10:30 - >>> auto_stroker.status_auto_stroker 0 Statusanzeige
         * 2014-10-23 16:10:30 - >>> auto_stroker.temp_return 23 Rücklauftemperatur
         * 2014-10-23 16:10:30 - >>> auto_stroker.state_uwp true UWP
         * 2014-10-23 16:10:30 - >>> auto_stroker.temp_boiler 48 Boilertemperatur
         * 2014-10-23 16:10:30 - >>> auto_stroker.temp_vessel 22.0 Kesseltemperatur
         * 2014-10-23 16:10:30 - >>> auto_stroker.state_alarm false Alarm
         * 2014-10-23 16:10:30 - >>> auto_stroker.state_ws false WS
         * 2014-10-23 16:10:30 - >>> auto_stroker.temp_outdoor 14 Außentemperatur
         * 2014-10-23 16:10:30 - >>> auto_stroker.state_gdw false GDW
         * 2014-10-23 16:10:30 - >>> auto_stroker.state_flame false Flame
         * 2014-10-23 16:10:30 - >>> auto_stroker.state_valve1 false Valve1
         * 2014-10-23 16:10:30 - >>> auto_stroker.performance_burner null Stellgrad MIN-MAX Kesselleistung in %
         */

        String sourceTelegram = "03 FE 05 03 08 01 00 40 FF 2D 17 30 0E C8";
        byte[] bs = EBusUtils.toByteArray(sourceTelegram);

        List<IEBusCommandMethod> find = commandRegistry.find(bs);

        IEBusCommandMethod method = find.get(0);

        assertNotNull(method);

        Map<String, Object> values = EBusCommandUtils.decodeTelegram(method, bs);

        assertFalse(values.isEmpty());

        try {
            ByteBuffer buildMasterTelegram = EBusCommandUtils.buildMasterTelegram(method, (byte) 0x03, (byte) 0xFE,
                    values);

            String hexDumpString = EBusUtils.toHexDumpString(buildMasterTelegram).toString();

            assertEquals(sourceTelegram, hexDumpString);

        } catch (EBusTypeException e) {
            e.printStackTrace();
            fail();
        } catch (EBusCommandException e) {
            e.printStackTrace();
            fail();
        }
    }
}
