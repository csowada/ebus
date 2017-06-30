package de.csdev.ebus.basic;

import org.junit.Test;

import de.csdev.ebus.core.EBusConsts;
import de.csdev.ebus.utils.EBusUtils;

import static org.junit.Assert.*;

public class EBusUtilsTest {

    @Test
    public void testIsMasterAddress() {
        assertFalse("Broadcast address is not a master address",
                EBusUtils.isMasterAddress(EBusConsts.BROADCAST_ADDRESS));

        assertFalse("0xA9 address is not a master address", EBusUtils.isMasterAddress(EBusConsts.ESCAPE));

        assertFalse("0xAA address is not a master address", EBusUtils.isMasterAddress(EBusConsts.SYN));
        
        assertTrue("0x0 address is a master address", EBusUtils.isMasterAddress((byte) 0x00));
        
        assertTrue("0xFF address is a master address", EBusUtils.isMasterAddress((byte) 0xFF));
        
        assertFalse("0x09 address is not a master address", EBusUtils.isMasterAddress((byte) 0x09));
    }

}
