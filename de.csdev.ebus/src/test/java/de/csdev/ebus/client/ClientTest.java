package de.csdev.ebus.client;

import java.io.IOException;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.csdev.ebus.cfg.ConfigurationReaderException;
import de.csdev.ebus.cfg.datatypes.EBusTypeException;
import de.csdev.ebus.command.IEBusCommandMethod;
import de.csdev.ebus.core.EBusConnectorEventListener;
import de.csdev.ebus.core.EBusController;
import de.csdev.ebus.core.EBusDataException;
import de.csdev.ebus.core.connection.EBusEmulatorConnection;
import de.csdev.ebus.service.parser.EBusParserListener;
import de.csdev.ebus.utils.EBusUtils;

public class ClientTest {

    private EBusEmulatorConnection emulator;

    @Before
    public void before() throws IOException, ConfigurationReaderException {
        emulator = new EBusEmulatorConnection(null);
    }

    @Test
    public void xxx() throws EBusTypeException, IOException, InterruptedException {

        EBusClientConfiguration clientConfiguration = new EBusClientConfiguration();

        clientConfiguration.loadInternalConfigurations();

        EBusClient client = new EBusClient(clientConfiguration);

        EBusController controller = new EBusController(emulator);

        client.connect(controller, (byte) 0xFF);

        client.getController().addEBusEventListener(new EBusConnectorEventListener() {

            public void onTelegramReceived(byte[] receivedData, Integer sendQueueId) {
                // TODO Auto-generated method stub
                System.out.println("ClientTest.xxx().new EBusConnectorEventListener() {...}.onTelegramReceived()");
            }

            public void onTelegramException(EBusDataException exception, Integer sendQueueId) {
                // TODO Auto-generated method stub
                System.out.println("ClientTest.xxx().new EBusConnectorEventListener() {...}.onTelegramException()");
            }

            public void onConnectionException(Exception e) {
                // TODO Auto-generated method stub
                System.out.println("ClientTest.xxx().new EBusConnectorEventListener() {...}.onConnectionException()");
            }
        });

        client.getResolverService().addEBusParserListener(new EBusParserListener() {

            public void onTelegramResolved(IEBusCommandMethod commandChannel, Map<String, Object> result,
                    byte[] receivedData, Integer sendQueueId) {
                System.out.println("ClientTest.xxx().new EBusParserListener() {...}.onTelegramResolved()");
                System.out.println(result);
            }
        });

        controller.start();

        writeTelegramToEmulator("30 08 50 22 03 CC 1A 27 59 00 02 98 00 0C 00");

        // wait a bit for the ebus thread
        Thread.sleep(100);
    }

    private void writeTelegramToEmulator(String telegram) throws IOException {

        emulator.writeByte(0xAA);
        emulator.writeByte(0xAA);
        emulator.writeByte(0xAA);

        byte[] bs = EBusUtils.toByteArray(telegram);

        for (byte b : bs) {
            emulator.writeByte(b);
        }

        emulator.writeByte(0xAA);
        emulator.writeByte(0xAA);
    }

}
