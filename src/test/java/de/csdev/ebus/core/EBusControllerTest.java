/**
 * Copyright (c) 2016-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.core;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Assert;
import org.junit.Test;

import de.csdev.ebus.core.connection.EBusEmulatorConnection;

public class EBusControllerTest {

    @Test
    public void testWatchdogTimeout() throws InterruptedException, IOException, EBusControllerException {

        EBusEmulatorConnection connection = new EBusEmulatorConnection(false);
        EBusLowLevelController controller = new EBusLowLevelController(connection);

        controller.setWatchdogTimerTimeout(1);

        ExecutorService threadExecutor = Executors
                .newSingleThreadExecutor(new EBusWorkerThreadFactory("ebus-controller", false));
        threadExecutor.execute(controller);

        Thread.sleep(500);
        controller.start();
        Assert.assertTrue(controller.getConnection().isOpen());

        Thread.sleep(1500);

        Assert.assertFalse(controller.getConnection().isOpen());
    }

}
