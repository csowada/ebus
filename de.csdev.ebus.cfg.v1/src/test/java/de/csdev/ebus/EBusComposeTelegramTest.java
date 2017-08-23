package de.csdev.ebus;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.csdev.ebus.cfg.datatypes.EBusTypeException;
import de.csdev.ebus.cfg.datatypes.EBusTypes;
import de.csdev.ebus.cfg.json.v1.OH1ConfigurationReader;
import de.csdev.ebus.command.EBusCommand;
import de.csdev.ebus.command.EBusCommandRegistry;
import de.csdev.ebus.command.EBusCommandUtils;
import de.csdev.ebus.command.IEBusCommand;
import de.csdev.ebus.command.IEBusCommandMethod;
import de.csdev.ebus.command.IEBusCommandMethod.Method;
import de.csdev.ebus.core.EBusConsts;
import de.csdev.ebus.utils.EBusUtils;

public class EBusComposeTelegramTest {

    private EBusCommandRegistry configurationProvider;
    private OH1ConfigurationReader jsonCfgReader;

    @Before
    public void before() {
        configurationProvider = new EBusCommandRegistry();
        jsonCfgReader = new OH1ConfigurationReader();
        jsonCfgReader.setEBusTypes(new EBusTypes());

        // configurationProvider

        try {
            InputStream inputStream = EBusCommand.class.getResourceAsStream("/common-configuration.json");
            // File filex = new File("src/main/resources/common-configuration.json");
            List<IEBusCommand> list = jsonCfgReader.loadConfiguration(inputStream);
            configurationProvider.addTelegramConfigurationList(list);

        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

    @Test
    public void composeTelegram01() throws EBusTypeException {
        IEBusCommandMethod command = configurationProvider.getConfigurationById("common.inquiry_of_existence",
                IEBusCommandMethod.Method.GET);
        assertNotNull("Command common.inquiry_of_existence not found", command);

        ByteBuffer bb = EBusCommandUtils.buildMasterTelegram(command, (byte) 0xFF, (byte) 0x00, null);

        byte[] byteArray = new byte[bb.remaining()];
        bb.get(byteArray);

        assertArrayEquals("Composed byte data wrong!", byteArray, EBusUtils.toByteArray("00 FF 07 FE 00 44"));
    }

    @Test
    public void composeTelegram02() throws EBusTypeException {
        IEBusCommandMethod command = configurationProvider.getConfigurationById("common.error", IEBusCommandMethod.Method.GET);
        assertNotNull("Command common.error not found", command);

        Map<String, Object> values = new HashMap<String, Object>();

        byte[] bytes = "HALLO WELT".getBytes();

        values.put("_error_message1", bytes[0]);
        values.put("_error_message2", bytes[1]);
        values.put("_error_message3", bytes[2]);
        values.put("_error_message4", bytes[3]);
        values.put("_error_message5", bytes[4]);
        values.put("_error_message6", bytes[5]);
        values.put("_error_message7", bytes[6]);
        values.put("_error_message8", bytes[7]);
        values.put("_error_message9", bytes[8]);
        values.put("_error_message10", bytes[9]);

        // add unescaped byte to data!
        values.put("_error_message10", EBusConsts.ESCAPE);

        ByteBuffer bb = EBusCommandUtils.buildMasterTelegram(command, null, (byte) 0xFF, values);

        byte[] byteArray = new byte[bb.remaining()];
        bb.get(byteArray);

        assertArrayEquals("Composed byte data wrong!", byteArray,
                EBusUtils.toByteArray("FF FE FE 01 0A 48 41 4C 4C 4F 20 57 45 4C A9 00 BB"));

        values.clear();

        values.put("error", "Hallo Welt");
        bb = EBusCommandUtils.buildMasterTelegram(command, null, (byte) 0xFF, values);

        byteArray = new byte[bb.remaining()];
        bb.get(byteArray);

        assertArrayEquals("Composed byte data wrong!", byteArray,
                EBusUtils.toByteArray("FF FE FE 01 0A 48 61 6C 6C 6F 20 57 65 6C 74 99"));
    }

}
