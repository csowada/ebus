package de.csdev.ebus.main;

import java.io.File;
import java.io.IOException;

import de.csdev.ebus.cfg.EBusNNN;
import de.csdev.ebus.core.EBusController;
import de.csdev.ebus.core.connection.EBusEmulatorConnection;
import de.csdev.ebus.core.connection.IEBusConnection;

public class EBusMain {

    public static void main(String[] args) {

        File file = new File("resource/replay.txt");

        IEBusConnection connection = null;
        // connection = new EBusTCPConnection("", 80);
        connection = new EBusEmulatorConnection(file);

        try {
            connection.open();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        EBusController controller = new EBusController(connection);
        controller.start();

        @SuppressWarnings("unused")
        EBusNNN x = new EBusNNN(controller);

        try {
            controller.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
