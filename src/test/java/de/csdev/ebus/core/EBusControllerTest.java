package de.csdev.ebus.core;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Assert;
import org.junit.Test;

import de.csdev.ebus.core.connection.EBusEmulatorConnection;

public class EBusControllerTest {

    @Test
    public void testWatchdogTimeout() throws InterruptedException, IOException {

        EBusEmulatorConnection connection = new EBusEmulatorConnection(false);
        EBusController controller = new EBusController(connection);

        controller.setWatchdogTimerTimeout(1);

        ExecutorService threadExecutor = Executors
                .newSingleThreadExecutor(new EBusWorkerThreadFactory("ebus-controller", false));
        threadExecutor.execute(controller);

        Thread.sleep(500);

        Assert.assertTrue(controller.getConnection().isOpen());

        Thread.sleep(1000);

        Assert.assertFalse(controller.getConnection().isOpen());
    }

}
