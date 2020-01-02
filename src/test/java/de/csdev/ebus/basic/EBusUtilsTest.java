/**
 * Copyright (c) 2016-2020 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.basic;

import static org.junit.Assert.*;

import org.junit.Test;

import de.csdev.ebus.core.EBusConsts;
import de.csdev.ebus.utils.EBusUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
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
