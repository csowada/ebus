package de.csdev.ebus.basic;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.cfg.ConfigurationReader;
import de.csdev.ebus.cfg.datatypes.EBusTypeException;
import de.csdev.ebus.cfg.datatypes.EBusTypes;
import de.csdev.ebus.command.EBusCommandRegistry;
import de.csdev.ebus.command.EBusCommandUtils;
import de.csdev.ebus.command.IEBusCommand;
import de.csdev.ebus.command.IEBusCommandChannel;
import de.csdev.ebus.utils.EBusUtils;

public class EBusWolfSM1TelegramTest {

    private static final Logger logger = LoggerFactory.getLogger(EBusWolfSM1TelegramTest.class);

    EBusTypes types;
    EBusCommandRegistry commandRegistry;

    @Before
    public void before() throws IOException {

        types = new EBusTypes();

        InputStream inputStream = ConfigurationReader.class
                .getResourceAsStream("/commands/wolf-sm1-configuration.json");

        if (inputStream == null) {
            throw new RuntimeException("Unable to load json file ...");
        }

        ConfigurationReader cfg = new ConfigurationReader();
        cfg.setEBusTypes(types);

        commandRegistry = new EBusCommandRegistry();
        commandRegistry.addTelegramConfigurationList(cfg.loadConfiguration(inputStream));
    }

    public void xxx() {
        byte[] bs = null;

        bs = EBusUtils.toByteArray("71 FE 50 18 0E 00 00 F9 00 07 00 3D 02 88 01 05 00 00 00 B8 AA");
        checkMask("solar.solar_yield", bs, IEBusCommand.Type.BROADCAST);
        xxx("solar.solar_yield", bs, IEBusCommand.Type.BROADCAST);
        canResolve(bs);

    }

    @Test
    public void xxx2() {
        byte[] bs = null;

        bs = EBusUtils.toByteArray("71 FE 50 17 10 08 91 F0 01 0A 04 00 80 00 80 00 80 00 80 00 80 F7 AA");
        checkMask("solar.solar_data", bs, IEBusCommand.Type.BROADCAST);
        xxx("solar.solar_data", bs, IEBusCommand.Type.BROADCAST);
        canResolve(bs);

    }

    public void xxx3() {
        byte[] bs = null;

        bs = EBusUtils.toByteArray("30 76 50 22 03 CC 2B 0A BF 00 02 07 01 DA");
        checkMask("solar.e1", bs, IEBusCommand.Type.GET);
        xxx("solar.e1", bs, IEBusCommand.Type.GET);
        canResolve(bs);

    }

    protected void xxx(String commandId, byte[] data, IEBusCommand.Type type) {

        IEBusCommandChannel commandChannel = commandRegistry.getConfigurationById(commandId, type);

        try {

            Map<String, Object> map = EBusCommandUtils.decodeTelegram(commandChannel, data);
            if (map != null) {
                for (Entry<String, Object> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " -> " + entry.getValue());
                }
            }

        } catch (EBusTypeException e) {
            logger.error("error!", e);
        }
    }

    protected void checkMask(String commandId, byte[] data, IEBusCommand.Type type) {

        ByteBuffer wrap = ByteBuffer.wrap(data);
        IEBusCommandChannel commandChannel = commandRegistry.getConfigurationById(commandId, type);

        try {
            ByteBuffer masterTelegram = EBusCommandUtils.buildMasterTelegram(commandChannel, (byte) 0x00, (byte) 0xFF,
                    null);
            ByteBuffer mask = commandChannel.getMasterTelegramMask();

            System.out.println("MASK:     " + EBusUtils.toHexDumpString(mask));
            System.out.println("DATA:     " + EBusUtils.toHexDumpString(data));
            System.out.println("COMPOSED: " + EBusUtils.toHexDumpString(masterTelegram));
            System.out.println("MATCHS?   " + commandRegistry.matchesCommand(commandChannel, wrap));

            Map<String, Object> map = EBusCommandUtils.decodeTelegram(commandChannel, data);
            if (map != null) {
                for (byte b : data) {

                }
            }

        } catch (EBusTypeException e) {
            logger.error("error!", e);
        }
    }

    private boolean canResolve(byte[] data) {

        List<IEBusCommandChannel> list = commandRegistry.find(data);

        if (list.isEmpty()) {
            Assert.fail("Expected an filled array!");
        }

        for (IEBusCommandChannel commandChannel : list) {
            logger.info(">>> " + commandChannel.toString());
            try {
                Map<String, Object> map = EBusCommandUtils.decodeTelegram(commandChannel, data);
                if (map.isEmpty()) {
                    Assert.fail("Expected a result map!");
                } else {

                    for (Entry<String, Object> entry : map.entrySet()) {
                        logger.info(entry.getKey() + " > " + entry.getValue());
                    }
                }
            } catch (EBusTypeException e) {
                logger.error("error!", e);
            }
        }

        return true;
    }

}
