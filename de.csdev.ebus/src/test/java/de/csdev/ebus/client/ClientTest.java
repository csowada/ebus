package de.csdev.ebus.client;

import static org.junit.Assert.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.cfg.EBusConfigurationReaderException;
import de.csdev.ebus.cfg.std.EBusConfigurationReader;
import de.csdev.ebus.command.EBusCommandRegistry;
import de.csdev.ebus.command.IEBusCommandMethod;
import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.core.EBusController;
import de.csdev.ebus.core.EBusDataException;
import de.csdev.ebus.core.EBusStateMachineTest;
import de.csdev.ebus.core.IEBusConnectorEventListener;
import de.csdev.ebus.core.connection.EBusEmulatorConnection;
import de.csdev.ebus.service.parser.IEBusParserListener;
import de.csdev.ebus.utils.EBusUtils;

public class ClientTest {

    private static final Logger logger = LoggerFactory.getLogger(EBusStateMachineTest.class);

    private EBusEmulatorConnection emulator;

    @Before
    public void before() throws IOException, EBusConfigurationReaderException {
        emulator = new EBusEmulatorConnection();
    }

    @Test
    public void xxx() throws EBusTypeException, IOException, InterruptedException {

        EBusCommandRegistry commandRegistry = new EBusCommandRegistry(EBusConfigurationReader.class, true);

        EBusClient client = new EBusClient(commandRegistry);

        EBusController controller = new EBusController(emulator);

        client.connect(controller, (byte) 0xFF);

        // disable auto identification requests for the test!
        client.getDeviceTableService().setDisableIdentificationRequests(true);

        client.getController().addEBusEventListener(new IEBusConnectorEventListener() {

            @Override
            public void onTelegramReceived(byte[] receivedData, Integer sendQueueId) {
                // noop
            }

            @Override
            public void onTelegramException(EBusDataException e, Integer sendQueueId) {
                logger.error("error!", e);
                fail("No TelegramException expected!");
            }

            @Override
            public void onConnectionException(Exception e) {
                logger.error("error!", e);
                fail("No ConnectionException expected!");
            }
        });

        client.getResolverService().addEBusParserListener(new IEBusParserListener() {

            @Override
            public void onTelegramResolved(IEBusCommandMethod commandChannel, Map<String, Object> result,
                    byte[] receivedData, Integer sendQueueId) {

                assertTrue(result.containsKey("pressure"));
                assertEquals(new BigDecimal("1.52"), result.get("pressure"));
                logger.info("Result correct!");
            }

            @Override
            public void onTelegramResolveFailed(byte[] receivedData, Integer sendQueueId) {
                // noop
            }
        });

        controller.start();

        writeTelegramToEmulator("30 08 50 22 03 CC 1A 27 59 00 02 98 00 0C 00");

        // wait a bit for the ebus thread
        Thread.sleep(200);
    }

    private void writeTelegramToEmulator(String telegram) throws IOException {

        // emulator.writeByte(0xAA);
        // emulator.writeByte(0xAA);
        // emulator.writeByte(0xAA);

        byte[] bs = EBusUtils.toByteArray(telegram);
        emulator.writeBytes(bs);
        // for (byte b : bs) {
        // emulator.writeByte(b);
        // }

        // emulator.writeByte(0xAA);
        // emulator.writeByte(0xAA);
    }

}
