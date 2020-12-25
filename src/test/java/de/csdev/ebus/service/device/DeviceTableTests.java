
/**
 * Copyright (c) 2016-2020 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.service.device;

import static org.junit.Assert.fail;

import java.util.HashMap;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.Test;

public class DeviceTableTests {

    @Test
    public void testMetrics() {

        EBusDeviceTable table = new EBusDeviceTable();
        table.setOwnAddress((byte) 0xFF);

        HashMap<@NonNull String, @Nullable Object> data = new HashMap<>();
        data.put("device_id", null);
        data.put("hardware_version", null);
        data.put("software_version", null);
        data.put("vendor", null);

        table.updateDevice((byte) 0x00, data);

        data.put("device_id", "wrong");
        data.put("hardware_version", "wrong");
        data.put("software_version", "wrong");
        data.put("vendor", "wrong");
        table.updateDevice((byte) 0x00, data);

        data.put("device_id", new byte[] {0x00, 0x00});
        try {
            table.updateDevice((byte) 0x00, data);
            fail("Should cause an IllegalArgumentException !");
        } catch(IllegalArgumentException e) {
            // expected
        }

        data.put("device_id", new byte[] {0x00, 0x00, 0x00, 0x00, 0x00, 0x00});
        try {
            table.updateDevice((byte) 0x00, data);
            fail("Should cause an IllegalArgumentException !");
        } catch(IllegalArgumentException e) {
            // expected
        }
        
        data.put("device_id", new byte[] {0x00, 0x00, 0x00, 0x00, 0x00});
        try {
            table.updateDevice((byte) 0x00, data);
        } catch(IllegalArgumentException e) {
            fail("Should NOT cause an IllegalArgumentException !");
        }
    }
}
