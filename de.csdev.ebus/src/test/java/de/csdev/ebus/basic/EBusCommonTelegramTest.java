package de.csdev.ebus.basic;

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

import de.csdev.ebus.cfg.ConfigurationReader;
import de.csdev.ebus.cfg.datatypes.EBusTypeException;
import de.csdev.ebus.cfg.datatypes.EBusTypes;
import de.csdev.ebus.command.EBusCommandRegistry;
import de.csdev.ebus.command.EBusCommandUtils;
import de.csdev.ebus.command.IEBusCommand;
import de.csdev.ebus.command.IEBusCommand.Type;
import de.csdev.ebus.command.IEBusCommandChannel;
import de.csdev.ebus.utils.EBusUtils;

public class EBusCommonTelegramTest {

    private static final Logger logger = LoggerFactory.getLogger(EBusCommonTelegramTest.class);

    EBusTypes types;
    EBusCommandRegistry commandRegistry;

    @Before
    public void before() throws IOException {

        types = new EBusTypes();

        InputStream inputStream = ConfigurationReader.class.getResourceAsStream("/commands/common-configuration.json");

        if (inputStream == null) {
            throw new RuntimeException("Unable to load json file ...");
        }

        ConfigurationReader cfg = new ConfigurationReader();
        cfg.setEBusTypes(types);

        commandRegistry = new EBusCommandRegistry();
        commandRegistry.addTelegramConfigurationList(cfg.loadConfiguration(inputStream));
    }

    @Test
    public void yyy() {
        IEBusCommandChannel commandChannel = commandRegistry.getConfigurationById("common.identification", Type.GET);
        try {
            ByteBuffer buffer = EBusCommandUtils.buildMasterTelegram(commandChannel, (byte) 0x00, (byte) 0xFF, null);

            System.out.println("EBusCommonTelegramTest.yyy()" + EBusUtils.toHexDumpString(buffer));
        } catch (EBusTypeException e) {
            logger.error("error!", e);
        }

    }

    @Test
    public void xxx() {
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

    protected void checkMask(String commandId, byte[] data, IEBusCommand.Type type) {

        ByteBuffer wrap = ByteBuffer.wrap(data);
        IEBusCommandChannel commandChannel = commandRegistry.getConfigurationById(commandId, type);

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

        List<IEBusCommandChannel> list = commandRegistry.find(data);

        if (list.isEmpty()) {
            Assert.fail("Expected an filled array!");
        }

        for (IEBusCommandChannel commandChannel : list) {
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

}
