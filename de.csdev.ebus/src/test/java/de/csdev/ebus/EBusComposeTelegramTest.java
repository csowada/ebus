package de.csdev.ebus;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import de.csdev.ebus.aaa.EBusTelegramComposer;
import de.csdev.ebus.cfg.EBusConfigurationJsonReader;
import de.csdev.ebus.cfg.EBusConfigurationProvider;
import de.csdev.ebus.cfg.EBusConfigurationTelegram;
import de.csdev.ebus.utils.EBusUtils;

public class EBusComposeTelegramTest {

	private EBusConfigurationProvider configurationProvider;
	private EBusConfigurationJsonReader jsonCfgReader;

	@SuppressWarnings("deprecation")
	@Before
	public void xxx() {
		configurationProvider = new EBusConfigurationProvider();
		jsonCfgReader = new EBusConfigurationJsonReader(configurationProvider);
		
        try {
        	File filex = new File("src/resources/common-configuration.json");
			jsonCfgReader.loadConfigurationFile(filex.toURL());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}
	
	@Test
	public void xxxx() {
		EBusConfigurationTelegram command = configurationProvider.getCommandById("common.inquiry_of_existence");
		assertNotNull("Command common.inquiry_of_existence not found", command);
		
		byte[] composeEBusTelegram2 = EBusTelegramComposer.composeEBusTelegram2(
				command, (byte)0xFF, (byte) 0x00, null);
		
		assertArrayEquals("Composed byte data wrong!", composeEBusTelegram2, 
				EBusUtils.toByteArray("00 FF 07 FE 00 44"));
	}
	
	
}


