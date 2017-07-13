package de.csdev.ebus;

import static org.junit.Assert.assertNotNull;

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
import de.csdev.ebus.command.IEBusCommand.Type;
import de.csdev.ebus.service.parser.EBusParserListener;
import de.csdev.ebus.service.parser.EBusParserService;
import de.csdev.ebus.utils.EBusUtils;

public class EBusComposeTelegramTest2 {

    private EBusCommandRegistry configurationProvider;
    private OH1ConfigurationReader jsonCfgReader;

    @Before
    public void before() {

        configurationProvider = new EBusCommandRegistry();
        jsonCfgReader = new OH1ConfigurationReader();
        jsonCfgReader.setEBusTypes(new EBusTypes());

        try {
        	InputStream inputStream = this.getClass().getResourceAsStream("/junit-configuration.json");
//        	int read = inputStream.read();
            List<EBusCommand> list = jsonCfgReader.loadConfiguration(inputStream);
            configurationProvider.addTelegramConfigurationList(list);
            
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    @Test
    public void composeTelegram01() throws EBusTypeException {
        IEBusCommand command = configurationProvider.getConfigurationById("fbh.set_heizkurve", Type.GET);
        assertNotNull("Command fbh.set_heizkurve not found", command);

        Map<String, Object> values = new HashMap<String, Object>();
        values.put("value", 50);
        
        ByteBuffer bb = EBusCommandUtils.buildMasterTelegram(command, null, (byte) 0x00, values);
//        byte[] byteArray = EBusTelegramComposer.composeEBusTelegram(command, null, (byte) 0x00, values);
        byte[] byteArray = new byte[bb.remaining()];
        bb.get(byteArray);
        
        System.out.println(EBusUtils.toHexDumpString(byteArray));
        
        
        
        
        //EBusConfigurationTelegram command2 = configurationProvider.getConfigurationById("fbh.heizkurve");
        //assertNotNull("Command fbh.heizkurve not found", command);
        
        EBusParserService parserService = new EBusParserService(configurationProvider);
        parserService.addEBusParserListener(new EBusParserListener() {

			public void onTelegramResolved(IEBusCommand command, Map<String, Object> result, byte[] receivedData,
					Integer sendQueueId) {
	               System.out.println(
	                       "EBusComposeTelegramTest2.composeTelegram01().new EBusParserListener() {...}.onTelegramResolved()");
	                  
	                  
	                  
			}
        });
        
        parserService.onTelegramReceived(byteArray, null);
        
        //assertArrayEquals("Composed byte data wrong!", byteArray, EBusUtils.toByteArray("00 FF 07 FE 00 44"));
    }
}
