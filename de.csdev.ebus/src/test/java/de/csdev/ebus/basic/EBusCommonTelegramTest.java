/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.basic;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.cfg.EBusConfigurationReader;
import de.csdev.ebus.cfg.EBusConfigurationReaderException;
import de.csdev.ebus.cfg.datatypes.EBusTypeException;
import de.csdev.ebus.cfg.datatypes.EBusTypes;
import de.csdev.ebus.command.EBusCommandRegistry;
import de.csdev.ebus.command.EBusCommandUtils;
import de.csdev.ebus.command.IEBusCommandMethod;
import de.csdev.ebus.command.IEBusCommandMethod.Method;
import de.csdev.ebus.utils.EBusUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusCommonTelegramTest {

    private static final Logger logger = LoggerFactory.getLogger(EBusCommonTelegramTest.class);

    EBusTypes types;
    EBusCommandRegistry commandRegistry;

    @Before
    public void before() throws IOException, EBusConfigurationReaderException {

        types = new EBusTypes();

        InputStream inputStream = EBusConfigurationReader.class.getResourceAsStream("/commands/common-configuration.json");

        if (inputStream == null) {
            throw new RuntimeException("Unable to load json file ...");
        }

        EBusConfigurationReader cfg = new EBusConfigurationReader();
        cfg.setEBusTypes(types);

        commandRegistry = new EBusCommandRegistry();
        commandRegistry.addCommandCollection(cfg.loadConfigurationCollection(inputStream));
    }

    @Test
    public void Identification() {
        IEBusCommandMethod commandMethod = commandRegistry.getConfigurationById("common.identification",
                IEBusCommandMethod.Method.GET);

        assertNotNull("Command common.identification not found!", commandMethod);

        try {
            ByteBuffer buffer = EBusCommandUtils.buildMasterTelegram(commandMethod, (byte) 0x00, (byte) 0xFF, null);
            assertNotNull("Unable to compose byte buffer for command", buffer);

        } catch (EBusTypeException e) {
            logger.error("error!", e);
        }

    }

    @Test
    public void AutoStroker() {
        IEBusCommandMethod commandMethod = commandRegistry.getConfigurationById("auto_stroker",
                IEBusCommandMethod.Method.GET);

        assertNotNull("Command auto_stroker not found!", commandMethod);

        try {
            ByteBuffer buffer = EBusCommandUtils.buildMasterTelegram(commandMethod, (byte) 0x00, (byte) 0xFF, null);
            assertNotNull("Unable to compose byte buffer for command", buffer);
            logger.info(EBusUtils.toHexDumpString(buffer).toString());

        } catch (EBusTypeException e) {
            logger.error("error!", e);
        }

    }

    @Test
    public void InquiryOfExistence() {
        // common.inquiry_of_existence
        IEBusCommandMethod commandMethod = commandRegistry.getConfigurationById("common.inquiry_of_existence",
                Method.BROADCAST);
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
        canResolve(bs);

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
        canResolve(bs);

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
        canResolve(bs);

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
        canResolve(bs);

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
        canResolve(bs);
    }

    @Test
    public void decodeBroadcast() {
        byte[] bs = EBusUtils.toByteArray("30 FE 07 00 09 00 80 10 54 21 16 08 03 17 02 AA");
        xxx("common", bs, IEBusCommandMethod.Method.BROADCAST);
        canResolve(bs);

    }

    protected void checkMask(String commandId, byte[] data, IEBusCommandMethod.Method type) {

        ByteBuffer wrap = ByteBuffer.wrap(data);
        IEBusCommandMethod commandChannel = commandRegistry.getConfigurationById(commandId, type);

        try {
            ByteBuffer masterTelegram = EBusCommandUtils.buildMasterTelegram(commandChannel, (byte) 0x00, (byte) 0xFF,
                    null);
            ByteBuffer mask = commandChannel.getMasterTelegramMask();

            System.out.println("MASK:     " + EBusUtils.toHexDumpString(mask));
            System.out.println("DATA:     " + EBusUtils.toHexDumpString(data));
            System.out.println("COMPOSED: " + EBusUtils.toHexDumpString(masterTelegram));
            System.out.println("MATCHS?   " + commandRegistry.matchesCommand(commandChannel, wrap));

        } catch (EBusTypeException e) {
            logger.error("error!", e);
        }
    }

    private boolean canResolve(byte[] data) {

        List<IEBusCommandMethod> list = commandRegistry.find(data);

        if (list.isEmpty()) {
            Assert.fail("Expected an filled array!");
        }

        for (IEBusCommandMethod commandChannel : list) {
            logger.info(">>> " + commandChannel.toString());
            try {
                Map<String, Object> map = EBusCommandUtils.decodeTelegram(commandChannel, data);
                if (map.isEmpty()) {
                    Assert.fail("Expected a result map!");
                } else {

                    for (Entry<String, Object> entry : map.entrySet()) {
                        logger.info(entry.getKey() + " > " + entry.getValue());
                    }
                }
            } catch (EBusTypeException e) {
                logger.error("error!", e);
            }
        }

        return true;
    }

    protected void xxx(String commandId, byte[] data, IEBusCommandMethod.Method type) {

        IEBusCommandMethod commandChannel = commandRegistry.getConfigurationById(commandId, type);

        try {

            Map<String, Object> map = EBusCommandUtils.decodeTelegram(commandChannel, data);
            if (map != null) {
                for (Entry<String, Object> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " -> " + entry.getValue());
                }
            }

        } catch (EBusTypeException e) {
            logger.error("error!", e);
        }
    }

}
