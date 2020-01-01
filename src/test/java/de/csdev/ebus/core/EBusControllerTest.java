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

import org.junit.Assert;
import org.junit.Test;

import de.csdev.ebus.core.connection.EBusEmulatorConnection;
import de.csdev.ebus.core.connection.EBusTCPConnection;

public class EBusControllerTest {

    @Test
    public void testWatchdogTimeout() throws InterruptedException, IOException, EBusControllerException {

        EBusEmulatorConnection connection = new EBusEmulatorConnection(false);
        EBusLowLevelController controller = new EBusLowLevelController(connection);

        controller.setWatchdogTimerTimeout(1);

        controller.start();

        Thread.sleep(50);
        Assert.assertTrue(controller.getConnection().isOpen());

        Thread.sleep(500);
        Assert.assertTrue(controller.getConnection().isOpen());

        Thread.sleep(1000);
        Assert.assertFalse(controller.getConnection().isOpen());
    }

    @Test
    public void testInterruptEmulator() throws InterruptedException, IOException, EBusControllerException {

        EBusEmulatorConnection connection = new EBusEmulatorConnection(false);
        EBusLowLevelController controller = new EBusLowLevelController(connection);

        controller.setWatchdogTimerTimeout(10);

        Thread.sleep(500);
        controller.start();

        Thread.sleep(1500);

        Assert.assertTrue(controller.isAlive());
        Assert.assertFalse(controller.isInterrupted());

        controller.interrupt();

        Thread.sleep(500);

        Assert.assertFalse(controller.isAlive());
        Assert.assertFalse(controller.isInterrupted());
    }

    @Test
    public void testInterruptTCPRaw() throws InterruptedException, IOException, EBusControllerException {

        EBusTCPConnection connection = new EBusTCPConnection("localhost", 1);
        EBusLowLevelController controller = new EBusLowLevelController(connection);

        controller.setWatchdogTimerTimeout(10);

        Thread.sleep(500);
        controller.start();

        Thread.sleep(1500);

        Assert.assertTrue(controller.isAlive());
        Assert.assertFalse(controller.isInterrupted());

        controller.interrupt();

        Thread.sleep(2500);

        Assert.assertFalse(controller.isAlive());
        Assert.assertFalse(controller.isInterrupted());
    }

    @Test
    public void testInterruptEbusd() throws InterruptedException, IOException, EBusControllerException {

        EBusEbusdController controller = new EBusEbusdController("localhost", 1);

        controller.setWatchdogTimerTimeout(10);

        Thread.sleep(500);
        controller.start();

        Thread.sleep(1500);

        Assert.assertTrue(controller.isAlive());
        Assert.assertFalse(controller.isInterrupted());

        controller.interrupt();

        Thread.sleep(500);

        Assert.assertFalse(controller.isAlive());
        Assert.assertFalse(controller.isInterrupted());
    }
}
