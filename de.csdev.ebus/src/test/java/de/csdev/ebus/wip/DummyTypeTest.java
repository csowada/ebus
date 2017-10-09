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
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Before;

import de.csdev.ebus.StaticTestTelegrams;
import de.csdev.ebus.cfg.EBusConfigurationReaderException;
import de.csdev.ebus.cfg.std.EBusConfigurationReader;
import de.csdev.ebus.command.EBusCommandRegistry;
import de.csdev.ebus.command.EBusCommandUtils;
import de.csdev.ebus.command.IEBusCommand;
import de.csdev.ebus.command.IEBusCommandCollection;
import de.csdev.ebus.command.IEBusCommandMethod;
import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.command.datatypes.EBusTypeRegistry;
import de.csdev.ebus.command.datatypes.ext.EBusTypeKWCrc;
import de.csdev.ebus.utils.EBusUtils;

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

    // @Test
    public void testIsMasterAddress() throws IOException, EBusTypeException, EBusConfigurationReaderException {

        InputStream inputStream = EBusConfigurationReader.class
                .getResourceAsStream("/commands/wolf-sm1-configuration.json");

        EBusConfigurationReader reader = new EBusConfigurationReader();
        reader.setEBusTypes(types);

        tr.addCommandCollection(reader.loadConfigurationCollection(inputStream));

        for (IEBusCommandCollection collection : tr.getCommandCollections()) {

            for (IEBusCommand command : collection.getCommands()) {
                for (IEBusCommandMethod commandChannel : command.getCommandMethods()) {
                    ByteBuffer masterTelegram = EBusCommandUtils.buildMasterTelegram(commandChannel, (byte) 0x00,
                            (byte) 0xFF, null);
                    StringBuilder hexDumpString = EBusUtils.toHexDumpString(masterTelegram);
                    System.out.println(hexDumpString);

                    ByteBuffer masterTelegramMask = commandChannel.getMasterTelegramMask();
                    StringBuilder xx = EBusUtils.toHexDumpString(masterTelegramMask);
                    System.out.println(xx);
                }

            }
        }

        // byte[] bs = EBusUtils.toByteArray("71 FE 50 17 10 08 95 F8 00 C3 02 00 80 00 80 00 80 00 80 00 80 DB");
        //
        // byte[] bs2 = EBusUtils.toByteArray("71 FE 50 18 0E 00 00 D0 01 05 00 E2 03 0F 01 01 00 00 00 18");
        //
        // byte[] bs3 = EBusUtils.toByteArray("30 76 50 22 03 CC 2B 0A BF 00 02 11 01 84");

        List<IEBusCommandMethod> find = tr.find(StaticTestTelegrams.WOLF_SOLAR_B);
        for (IEBusCommandMethod eBusCommand : find) {
            System.out.println("ConfigurationReaderTest.testIsMasterAddress()");
            Map<String, Object> encode = EBusCommandUtils.decodeTelegram(eBusCommand, StaticTestTelegrams.WOLF_SOLAR_B);
            for (Entry<String, Object> eBusCommand2 : encode.entrySet()) {
                System.out.println("ConfigurationReaderTest.testIsMasterAddress()" + eBusCommand2.getKey() + " > "
                        + eBusCommand2.getValue());
            }
        }

        Map<String, Object> encode = EBusCommandUtils.decodeTelegram(
                tr.getConfigurationById("wolf-sm1", "solar.solar_data", IEBusCommandMethod.Method.BROADCAST),
                StaticTestTelegrams.WOLF_SOLAR_B);

        for (Entry<String, Object> eBusCommand2 : encode.entrySet()) {
            System.out.println("ConfigurationReaderTest.testIsMasterAddress()" + eBusCommand2.getKey() + " > "
                    + eBusCommand2.getValue());
        }
        //
        // assertFalse("Broadcast address is not a master address",
        // EBusUtils.isMasterAddress(EBusConsts.BROADCAST_ADDRESS));
        //
        // assertFalse("0xA9 address is not a master address", EBusUtils.isMasterAddress(EBusConsts.ESCAPE));
        //
        // assertFalse("0xAA address is not a master address", EBusUtils.isMasterAddress(EBusConsts.SYN));
        //
        // assertTrue("0x0 address is a master address", EBusUtils.isMasterAddress((byte) 0x00));
        //
        // assertTrue("0xFF address is a master address", EBusUtils.isMasterAddress((byte) 0xFF));
        //
        // assertFalse("0x09 address is not a master address", EBusUtils.isMasterAddress((byte) 0x09));
    }
}
