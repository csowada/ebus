package de.csdev.ebus;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.csdev.ebus.aaa.EBusParserListener;
import de.csdev.ebus.aaa.EBusParserService;
import de.csdev.ebus.aaa.EBusTelegramComposer;
import de.csdev.ebus.cfg.EBusConfigurationJsonReader;
import de.csdev.ebus.cfg.EBusConfigurationProvider;
import de.csdev.ebus.cfg.EBusConfigurationTelegram;
import de.csdev.ebus.core.EBusConsts;
import de.csdev.ebus.utils.EBusUtils;

public class EBusComposeTelegramTest2 {

    private EBusConfigurationProvider configurationProvider;
    private EBusConfigurationJsonReader jsonCfgReader;

    @SuppressWarnings("deprecation")
    @Before
    public void before() {
        configurationProvider = new EBusConfigurationProvider();
        jsonCfgReader = new EBusConfigurationJsonReader(configurationProvider);

        try {
            File filex = new File("src/test/resources/junit-configuration.json");
            jsonCfgReader.loadConfigurationFile(filex.toURL());
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

    @Test
    public void composeTelegram01() {
        EBusConfigurationTelegram command = configurationProvider.getConfigurationById("fbh.set_heizkurve");
        assertNotNull("Command fbh.set_heizkurve not found", command);

        Map<String, Object> values = new HashMap<String, Object>();
        values.put("value", 50);
        
        byte[] byteArray = EBusTelegramComposer.composeEBusTelegram(command, null, (byte) 0x00, values);

        System.out.println(EBusUtils.toHexDumpString(byteArray));
        
        
        
        
        //EBusConfigurationTelegram command2 = configurationProvider.getConfigurationById("fbh.heizkurve");
        //assertNotNull("Command fbh.heizkurve not found", command);
        
        EBusParserService parserService = new EBusParserService(configurationProvider);
        parserService.addEBusParserListener(new EBusParserListener() {
            @Override
            public void onTelegramResolved(EBusConfigurationTelegram registryEntry, Map<String, Object> result,
                    byte[] receivedData, Integer sendQueueId) {
               System.out.println(
                    "EBusComposeTelegramTest2.composeTelegram01().new EBusParserListener() {...}.onTelegramResolved()");
               
               
               
            }
        });
        
        parserService.onTelegramReceived(byteArray, null);
        
        //assertArrayEquals("Composed byte data wrong!", byteArray, EBusUtils.toByteArray("00 FF 07 FE 00 44"));
    }
}
