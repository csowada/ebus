/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.cfg;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.TestUtils;
import de.csdev.ebus.cfg.std.EBusConfigurationReader;
import de.csdev.ebus.command.EBusCommandRegistry;
import de.csdev.ebus.command.EBusCommandUtils;
import de.csdev.ebus.command.IEBusCommandMethod;
import de.csdev.ebus.command.IEBusCommandMethod.Method;
import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.command.datatypes.EBusTypeRegistry;
import de.csdev.ebus.core.EBusConsts;
import de.csdev.ebus.utils.EBusUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusCommonTelegramTest {

    private static final Logger logger = LoggerFactory.getLogger(EBusCommonTelegramTest.class);

    EBusTypeRegistry types;
    EBusCommandRegistry commandRegistry;

    @Before
    public void before() throws IOException, EBusConfigurationReaderException {

        types = new EBusTypeRegistry();

        InputStream inputStream = EBusConfigurationReader.class
                .getResourceAsStream("/commands/common-configuration.json");

        if (inputStream == null) {
            throw new RuntimeException("Unable to load json file ...");
        }

        EBusConfigurationReader cfg = new EBusConfigurationReader();
        cfg.setEBusTypes(types);

        commandRegistry = new EBusCommandRegistry();
        commandRegistry.addCommandCollection(cfg.loadConfigurationCollection(inputStream));
    }

    @Test
    public void testIdentification() {
        IEBusCommandMethod commandMethod = commandRegistry.getConfigurationById(EBusConsts.COLLECTION_STD,
                EBusConsts.COMMAND_IDENTIFICATIONE, IEBusCommandMethod.Method.GET);

        assertNotNull("Command common.identification not found!", commandMethod);

        try {
            ByteBuffer buffer = EBusCommandUtils.buildMasterTelegram(commandMethod, (byte) 0x00, (byte) 0xFF, null);
            assertNotNull("Unable to compose byte buffer for command", buffer);

        } catch (EBusTypeException e) {
            logger.error("error!", e);
        }

    }

    @Test
    public void testAutoStroker() {
        IEBusCommandMethod commandMethod = commandRegistry.getConfigurationById(EBusConsts.COLLECTION_STD,
                "auto_stroker.op_data_bc2tc_b1", IEBusCommandMethod.Method.GET);

        assertNotNull("Command auto_stroker not found!", commandMethod);

        try {
            ByteBuffer buffer = EBusCommandUtils.buildMasterTelegram(commandMethod, (byte) 0x00, (byte) 0xFF, null);
            assertNotNull("Unable to compose byte buffer for command", buffer);

        } catch (EBusTypeException e) {
            logger.error("error!", e);
        }

    }

    @Test
    public void testInquiryOfExistence() {
        IEBusCommandMethod commandMethod = commandRegistry.getConfigurationById(EBusConsts.COLLECTION_STD,
                EBusConsts.COMMAND_INQ_EXISTENCE, Method.BROADCAST);
        assertNotNull("Command common.inquiry_of_existence not found!", commandMethod);
    }

    @Test
    public void ResolveCommonTelegrams() {
        byte[] bs = null;

        /*
         * 2014-10-23 16:10:31 - >>> Datum/Zeitmeldung eines eBUS Masters
         * 2014-10-23 16:10:31 - >>> common.time_min 8 Zeit Min
         * 2014-10-23 16:10:31 - >>> common.date_year 14 Zeit Stunde
         * 2014-10-23 16:10:31 - >>> common.date_month 10 Zeit Stunde
         * 2014-10-23 16:10:31 - >>> common.temp_outdoor null Außentemperatur
         * 2014-10-23 16:10:31 - >>> common.date_day 23 Zeit Stunde
         * 2014-10-23 16:10:31 - >>> common.time_sec 10 Zeit Sek.
         * 2014-10-23 16:10:31 - >>> common.time_hour 16 Zeit Stunde
         */
        bs = EBusUtils.toByteArray("30 FE 07 00 09 00 80 10 08 16 23 10 04 14 A2 AA");
        TestUtils.canResolve(commandRegistry, bs);

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

        bs = EBusUtils.toByteArray("03 FE 05 03 08 01 00 40 FF 2D 17 30 0E C8 AA");
        TestUtils.canResolve(commandRegistry, bs);

        /*
         * 2014-10-23 16:10:33 - >>> Sollwertübertragung des Reglers an andere Regler
         * 2014-10-23 16:10:33 - >>> controller2.temp_t_vessel null Kesselsollwert
         * 2014-10-23 16:10:33 - >>> controller2.temp_outdoor 14.597656 Außentemperatur
         * 2014-10-23 16:10:33 - >>> controller2.power_enforcement null Leistungszwang
         * 2014-10-23 16:10:33 - >>> controller2.status_bwr false BWR aktiv (Wärmepumpen?)
         * 2014-10-23 16:10:33 - >>> controller2.status_heat_circuit true Heizkreis aktiv
         * 2014-10-23 16:10:33 - >>> controller2.temp_t_boiler 5.0 Brauchwassersoll
         */
        bs = EBusUtils.toByteArray("03 F1 08 00 08 00 80 99 0E 80 02 00 05 94 AA");
        TestUtils.canResolve(commandRegistry, bs);

        /*
         * 2014-10-23 16:10:39 - >>> Betriebsdaten des Reglers an den Feuerungsautomaten
         * 2014-10-23 16:10:39 - >>> controller.status_warm_req2 3 Statuswärmeanforderung2
         * 2014-10-23 16:10:39 - >>> controller.value_fuel null Brennstoffwert
         * 2014-10-23 16:10:39 - >>> controller.status_warm_req1 187 Statuswärmeanforderung
         * 2014-10-23 16:10:39 - >>> controller.temp_t_vessel 22.0625 Kesselsollwert-Temperatur
         * 2014-10-23 16:10:39 - >>> controller.pressure_t_vessel null Kesselsollwert-Druck
         * 2014-10-23 16:10:39 - >>> controller.performance_burner 0.0 Stellgrad
         * 2014-10-23 16:10:39 - >>> controller.temp_t_boiler 50.0 Brauchwassersollwert
         */
        bs = EBusUtils.toByteArray("30 03 05 07 09 BB 03 61 01 00 80 FF 64 FF D5 00 AA");
        TestUtils.canResolve(commandRegistry, bs);

        /*
         * 2014-10-23 16:11:18 - >>> Identifikation
         * 2014-10-23 16:11:18 - >>> common._software_ver 1 Software Version
         * 2014-10-23 16:11:18 - >>> common._software_rev 0 Software Revision
         * 2014-10-23 16:11:18 - >>> common._hardware_ver 0 Software Version
         * 2014-10-23 16:11:18 - >>> common._hardware_rev 48 Software Revision
         * 2014-10-23 16:11:18 - >>> common.vendor 1 Hersteller
         * 2014-10-23 16:11:18 - >>> common._device_id0 33 Geräte ID 0
         * 2014-10-23 16:11:18 - >>> common._device_id4 96 Geräte ID 4
         * 2014-10-23 16:11:18 - >>> common._device_id3 64 Geräte ID 3
         * 2014-10-23 16:11:18 - >>> common._device_id2 90 Geräte ID 2
         * 2014-10-23 16:11:18 - >>> common._device_id1 0 Geräte ID 1
         */
        bs = EBusUtils.toByteArray("30 08 07 04 00 5E 00 0A 19 01 21 00 5A 40 60 01 00 00 48 00 AA");
        TestUtils.canResolve(commandRegistry, bs);

        bs = EBusUtils.toByteArray("FF 76 07 04 00 43 00 0A 50 01 15 00 00 80 02 27 FF FF 81 00 AA");
        TestUtils.canResolve(commandRegistry, bs);
    }

    @Test
    public void decodeBroadcast() {
        byte[] bs = EBusUtils.toByteArray("30 FE 07 00 09 00 80 10 54 21 16 08 03 17 02 AA");
        TestUtils.canResolve(commandRegistry, bs);

    }

}
