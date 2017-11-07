/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.cfg;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.csdev.ebus.cfg.std.EBusConfigurationReader;
import de.csdev.ebus.command.EBusCommandRegistry;
import de.csdev.ebus.command.EBusCommandUtils;
import de.csdev.ebus.command.IEBusCommandMethod;
import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.command.datatypes.EBusTypeRegistry;
import de.csdev.ebus.utils.EBusUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusVaillantVRC430TelegramTest {

    EBusTypeRegistry types;
    EBusCommandRegistry commandRegistry;

    @Before
    public void before() throws IOException, EBusConfigurationReaderException {

        types = new EBusTypeRegistry();

        InputStream inputStream = EBusConfigurationReader.class
                .getResourceAsStream("/commands/vaillant-vrc-configuration.json");

        if (inputStream == null) {
            throw new RuntimeException("Unable to load json file ...");
        }

        EBusConfigurationReader cfg = new EBusConfigurationReader();
        cfg.setEBusTypes(types);

        commandRegistry = new EBusCommandRegistry(EBusConfigurationReader.class);
        commandRegistry.loadCommandCollection(inputStream);
    }

    @Test
    public void testDateTimeBroadcast() {
    	
    	// 22:00:34 06.11.2017 (Monday)
    	byte[] byteArray = EBusUtils.toByteArray("10 FE B5 16 08 00 34 00 22 06 11 01 17 CE AA");
    	
    	List<IEBusCommandMethod> find = commandRegistry.find(byteArray);
    	
    	for (IEBusCommandMethod method : find) {
    		try {
				Map<String, Object> map = EBusCommandUtils.decodeTelegram(method, byteArray);
				Object object = map.get("datetime");
				
				assertNotNull(object);

				
			} catch (EBusTypeException e) {
				e.printStackTrace();
			}
		}
    }

}
