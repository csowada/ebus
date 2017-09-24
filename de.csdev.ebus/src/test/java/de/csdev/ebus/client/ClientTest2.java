package de.csdev.ebus.client;

import java.io.IOException;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.cfg.EBusConfigurationReaderException;
import de.csdev.ebus.command.IEBusCommandMethod;
import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.core.IEBusConnectorEventListener;
import de.csdev.ebus.core.EBusController;
import de.csdev.ebus.core.EBusDataException;
import de.csdev.ebus.core.connection.EBusEmulatorConnection;
import de.csdev.ebus.service.parser.IEBusParserListener;
import de.csdev.ebus.utils.EBusUtils;
import de.csdev.ebus.wip.EBusStateMachineTest;

public class ClientTest2 {

    private static final Logger logger = LoggerFactory.getLogger(EBusStateMachineTest.class);

    private EBusEmulatorConnection emulator;

    @Before
    public void before() throws IOException, EBusConfigurationReaderException {
        emulator = new EBusEmulatorConnection(null);
    }

    @Test
    public void testNoSlaveResponse() throws EBusTypeException, IOException, InterruptedException {
        EBusClientConfiguration clientConfiguration = new EBusClientConfiguration();

        clientConfiguration.loadInternalConfigurations();

        EBusClient client = new EBusClient(clientConfiguration);

        EBusController controller = new EBusController(emulator);

        client.connect(controller, (byte) 0xFF);

        client.getController().addEBusEventListener(new IEBusConnectorEventListener() {

            public void onTelegramReceived(byte[] receivedData, Integer sendQueueId) {
                // TODO Auto-generated method stub
                logger.error("ClientTest.xxx().new EBusConnectorEventListener() {...}.onTelegramReceived()");
            }

            public void onTelegramException(EBusDataException exception, Integer sendQueueId) {
                logger.error(exception.getLocalizedMessage());
                // TODO Auto-generated method stub
                // logger.error("ClientTest.xxx().new EBusConnectorEventListener() {...}.onTelegramException()");
            }

            public void onConnectionException(Exception e) {
                // TODO Auto-generated method stub
                logger.error("ClientTest.xxx().new EBusConnectorEventListener() {...}.onConnectionException()");
            }
        });

        client.getResolverService().addEBusParserListener(new IEBusParserListener() {

            public void onTelegramResolved(IEBusCommandMethod commandChannel, Map<String, Object> result,
                    byte[] receivedData, Integer sendQueueId) {
                logger.error("ClientTest.xxx().new EBusParserListener() {...}.onTelegramResolved()");
                System.out.println(result);
            }
        });

        controller.start();

        controller.addToSendQueue(EBusUtils.toByteArray("FF 08 B5 09 03 0D 2F 00 1D"));

        sendAutoSYN(10);
    }

    private void sendAutoSYN(int loopCount) throws IOException {

        for (int i = 0; i < loopCount; i++) {
            emulator.writeByte(0xAA);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }

        }
    }

}
