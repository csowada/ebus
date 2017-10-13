/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command.datatype.v2;

import static org.junit.Assert.assertArrayEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.command.datatypes.EBusTypeRegistry;
import de.csdev.ebus.command.datatypes.IEBusType;
import de.csdev.ebus.command.datatypes.std.EBusTypeChar;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class StandardTestDummy {

    EBusTypeRegistry types;
	private byte[] encode;

    @Before
    public void before() {
        types = new EBusTypeRegistry();
    }

    private IEBusType<?> getType(int length, boolean reverseByteOrder) {
        Map<String, Object> properties = new HashMap<String, Object>();

        properties.put(IEBusType.LENGTH, length);

        if (reverseByteOrder) {
            properties.put(IEBusType.REVERSED_BYTE_ORDER, Boolean.TRUE);
        }

        return types.getType(EBusTypeChar.CHAR, properties);
    }

//    @Test
    public void x() throws EBusTypeException {
    	
    	
    	for (String typeName : types.getTypesNames()) {
			System.out.println("StandardTestDummy.x()" + typeName);
			IEBusType<Object> type = types.getType(typeName);
			
			byte[] bs = new byte[type.getTypeLenght()];
			
			
			Object decode = type.decode(bs);
			encode = type.encode(decode);
			
			assertArrayEquals(bs, encode);
		}
    }

    @Test
    public void test_ByteDataImutable() throws EBusTypeException {

        IEBusType<?> type = getType(2, true);

        byte[] data = new byte[] { (byte) 0xFF, (byte) 0xFE };
        type.decode(data);

        assertArrayEquals(new byte[] { (byte) 0xFF, (byte) 0xFE }, data);
    }

}
