/**
 * Copyright (c) 2017-2025 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.wip;

import static org.junit.Assert.fail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.utils.Emulator;

public class EmulatorTest {

    private static final Logger logger = LoggerFactory.getLogger(EmulatorTest.class);

    // @Test
    public void xxx() {

        Emulator emu = new Emulator();

        try {
            Thread.sleep(3000);

            emu.write(new byte[] { 0x00, 0x01, 0x02, 0x00, 0x01, 0x02, 0x00, 0x01, 0x02, 0x00, 0x01, 0x02, 0x00, 0x01,
                    0x02, 0x00, 0x01, 0x02, 0x00, 0x01, 0x02, 0x00, 0x01, 0x02, 0x00, 0x01, 0x02 });

            Thread.sleep(5000);
        } catch (InterruptedException e) {
            logger.error("error!", e);
            fail();
        }

    }

}
