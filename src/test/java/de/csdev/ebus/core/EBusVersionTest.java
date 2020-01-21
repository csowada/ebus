/**
 * Copyright (c) 2016-2020 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.core;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

public class EBusVersionTest {

    // @Test
    public void testVersionInformation() throws InterruptedException, IOException, EBusControllerException {
        // not working in maven build
        assertEquals("1.0.3", EBusVersion.getVersion());
        assertEquals("f120405", EBusVersion.getBuildCommit());
        assertEquals("198", EBusVersion.getBuildNumber());
        assertEquals("20200121192308", EBusVersion.getBuildTimestamp());
    }
}
