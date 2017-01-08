package de.csdev.ebus;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.csdev.ebus.aaa.EBusTelegramComposer;
import de.csdev.ebus.cfg.EBusConfigurationJsonReader;
import de.csdev.ebus.cfg.EBusConfigurationProvider;
import de.csdev.ebus.cfg.EBusConfigurationTelegram;
import de.csdev.ebus.core.EBusConsts;
import de.csdev.ebus.utils.EBusUtils;

public class EBusComposeTelegramTest {

    private EBusConfigurationProvider configurationProvider;
    private EBusConfigurationJsonReader jsonCfgReader;

    @SuppressWarnings("deprecation")
    @Before
    public void before() {
        configurationProvider = new EBusConfigurationProvider();
        jsonCfgReader = new EBusConfigurationJsonReader(configurationProvider);

        try {
            File filex = new File("src/main/resources/common-configuration.json");
            jsonCfgReader.loadConfigurationFile(filex.toURL());
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

    @Test
    public void composeTelegram01() {
        EBusConfigurationTelegram command = configurationProvider.getCommandById("common.inquiry_of_existence");
        assertNotNull("Command common.inquiry_of_existence not found", command);

        byte[] byteArray = EBusTelegramComposer.composeEBusTelegram(command, (byte) 0xFF, (byte) 0x00, null);

        assertArrayEquals("Composed byte data wrong!", byteArray, EBusUtils.toByteArray("00 FF 07 FE 00 44"));
    }

    @Test
    public void composeTelegram02() {
        EBusConfigurationTelegram command = configurationProvider.getCommandById("common.error");
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

        byte[] byteArray = EBusTelegramComposer.composeEBusTelegram(command, null, (byte) 0xFF, values);

        assertArrayEquals("Composed byte data wrong!", byteArray,
                EBusUtils.toByteArray("FF FE FE 01 0A 48 41 4C 4C 4F 20 57 45 4C A9 00 BB"));

        values.clear();

        values.put("error", "Hallo Welt");
        byteArray = EBusTelegramComposer.composeEBusTelegram(command, null, (byte) 0xFF, values);
        assertArrayEquals("Composed byte data wrong!", byteArray,
                EBusUtils.toByteArray("FF FE FE 01 0A 48 61 6C 6C 6F 20 57 65 6C 74 99"));
    }

}
