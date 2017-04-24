package de.csdev.ebus.client;

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
            .toByteArray("71 FE 50 17 10 08 95 F8 00 C3 02 00 80 00 80 00 80 00 80 00 80 DB");

}
