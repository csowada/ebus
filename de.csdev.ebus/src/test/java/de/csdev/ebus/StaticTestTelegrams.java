package de.csdev.ebus;

import de.csdev.ebus.utils.EBusUtils;

/**
 * @author Christian
 *
 */
public class StaticTestTelegrams {

    /**
     * Wolf Solar E1 >> Value: 27,3
     */
    public static byte[] WOLF_SOLAR_E1 = EBusUtils.toByteArray("30 76 50 22 03 CC 2B 0A BF 00 02 11 01 84");

    /**
     * Wolf Solar Solar Yield Broadcast >> Value ???
     */
    public static byte[] WOLF_SOLAR_A = EBusUtils
            .toByteArray("71 FE 50 18 0E 00 00 D0 01 05 00 E2 03 0F 01 01 00 00 00 18");

    public static byte[] WOLF_SOLAR_B = EBusUtils
            .toByteArray("71 FE 50 17 10 08 91 05 01 CA 01 00 80 00 80 00 80 00 80 00 80 9B");
    
    /**
     * Wolf Solar Solar Yield Broadcast >> Value 0.0, 7.686, 2.451.675
     */
    public static byte[] WOLF_SOLAR_C = EBusUtils
            .toByteArray("71 FE 50 18 0E 00 00 AE 02 07 00 A3 02 C3 01 02 00 00 00 7E");
    
    /**
     * 2014-10-23 16:11:20 -   >>> Betriebsdaten des Feuerungsautomaten an den Regler - Block 1
2014-10-23 16:11:20 -     >>> auto_stroker.state_valve2          false     Valve2
2014-10-23 16:11:20 -     >>> auto_stroker.state_ldw             false     LDW
2014-10-23 16:11:20 -     >>> auto_stroker.status_auto_stroker   0         Statusanzeige
2014-10-23 16:11:21 -     >>> auto_stroker.temp_return           23        Rücklauftemperatur
2014-10-23 16:11:21 -     >>> auto_stroker.state_uwp             true      UWP
2014-10-23 16:11:21 -     >>> auto_stroker.temp_boiler           48        Boilertemperatur
2014-10-23 16:11:21 -     >>> auto_stroker.temp_vessel           22.0      Kesseltemperatur
2014-10-23 16:11:21 -     >>> auto_stroker.state_alarm           false     Alarm
2014-10-23 16:11:21 -     >>> auto_stroker.state_ws              false     WS
2014-10-23 16:11:21 -     >>> auto_stroker.temp_outdoor          14        Außentemperatur
2014-10-23 16:11:21 -     >>> auto_stroker.state_gdw             false     GDW
2014-10-23 16:11:21 -     >>> auto_stroker.state_flame           false     Flame
2014-10-23 16:11:21 -     >>> auto_stroker.state_valve1          false     Valve1
2014-10-23 16:11:21 -     >>> auto_stroker.performance_burner    null      Stellgrad MIN-MAX Kesselleistung in %
     */
    public static byte[] AUTO_STROKER = EBusUtils
            .toByteArray("03 FE 05 03 08 01 00 40 FF 2C 17 30 0E 96 AA");
    
    
}
