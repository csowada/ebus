/**
 * Copyright (c) 2017-2025 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command.datatype.ext;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.command.datatypes.EBusTypeRegistry;
import de.csdev.ebus.command.datatypes.IEBusComplexType;
import de.csdev.ebus.command.datatypes.IEBusType;
import de.csdev.ebus.command.datatypes.ext.EBusTypeKWCrc;
import de.csdev.ebus.utils.EBusUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class CrcKWTest {

    private EBusTypeRegistry types;

    @Before
    public void before() throws EBusTypeException {
        types = new EBusTypeRegistry();
    }

    @Test
    public void test_CrcKW() throws EBusTypeException {

        Map<String, Object> properties = new HashMap<String, Object>();
        IEBusType<Object> type = types.getType(EBusTypeKWCrc.TYPE_KW_CRC, properties);

        assertTrue(type instanceof IEBusComplexType);

        IEBusComplexType<?> crcType = (IEBusComplexType<?>) type;

        byte[] masterData = EBusUtils.toByteArray("30 74 27 00 00 5d 01 00 00");
        byte[] encodeComplex = crcType.encodeComplex(masterData);
        assertEquals(masterData[0], encodeComplex[0]);

        byte[] masterData2 = EBusUtils.toByteArray("a0 74 27 01 00 5d 01 00 00");
        byte[] encodeComplex2 = crcType.encodeComplex(masterData2);
        assertEquals(masterData2[0], encodeComplex2[0]);

        // replace default position for crc with a complete master telegram including address etc.
        properties.put(EBusTypeKWCrc.POS, 5);
        type = types.getType(EBusTypeKWCrc.TYPE_KW_CRC, properties);

        byte[] fullMasterData = EBusUtils.toByteArray("ff 30 50 23 09 a0 74 27 01 00 5d 01 00 00");
        byte[] encodeComplex3 = crcType.encodeComplex(fullMasterData);
        assertEquals(fullMasterData[5], encodeComplex3[0]);

    }
}
