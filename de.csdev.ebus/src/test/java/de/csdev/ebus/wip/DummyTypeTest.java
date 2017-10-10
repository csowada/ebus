/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.wip;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.csdev.ebus.cfg.EBusConfigurationReaderException;
import de.csdev.ebus.command.EBusCommandRegistry;
import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.command.datatypes.EBusTypeRegistry;
import de.csdev.ebus.command.datatypes.IEBusType;
import de.csdev.ebus.command.datatypes.ext.EBusTypeKWCrc;
import de.csdev.ebus.command.datatypes.std.EBusTypeChar3;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class DummyTypeTest {

    EBusCommandRegistry tr;
    EBusTypeRegistry types;

    @Before
    public void before() {
        tr = new EBusCommandRegistry();
        types = new EBusTypeRegistry();
        types.add(EBusTypeKWCrc.class);

    }

    @Test
    public void testIsMasterAddress() throws IOException, EBusTypeException, EBusConfigurationReaderException {

//        DummyTypeChar2 de = new DummyTypeChar2();

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(IEBusType.LENGTH, 2);
        properties.put("variant", 99);
        //IEBusType<BigDecimal> instance = de.getInstance(properties);

        IEBusType<Object> type = types.getType(EBusTypeChar3.CHAR, properties);
        
        
        System.out.println("ConfigurationReaderTest.enclosing_method()");

    }
    
}
