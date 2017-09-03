/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.basic;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.cfg.EBusConfigurationReader;
import de.csdev.ebus.cfg.EBusConfigurationReaderException;
import de.csdev.ebus.cfg.datatypes.EBusTypeException;
import de.csdev.ebus.cfg.datatypes.EBusTypes;
import de.csdev.ebus.command.EBusCommandRegistry;
import de.csdev.ebus.command.EBusCommandUtils;
import de.csdev.ebus.command.IEBusCommandMethod;
import de.csdev.ebus.utils.EBusUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusWolfSM1TelegramTest {

    private static final Logger logger = LoggerFactory.getLogger(EBusWolfSM1TelegramTest.class);

    EBusTypes types;
    EBusCommandRegistry commandRegistry;

    @Before
    public void before() throws IOException, EBusConfigurationReaderException {

        types = new EBusTypes();

        InputStream inputStream = EBusConfigurationReader.class
                .getResourceAsStream("/commands/wolf-sm1-configuration.json");

        if (inputStream == null) {
            throw new RuntimeException("Unable to load json file ...");
        }

        EBusConfigurationReader cfg = new EBusConfigurationReader();
        cfg.setEBusTypes(types);

        commandRegistry = new EBusCommandRegistry();
        commandRegistry.addCommandCollection(cfg.loadConfigurationCollection(inputStream));
    }

    @Test
    public void xxx() {
        byte[] bs = null;

        bs = EBusUtils.toByteArray("71 FE 50 18 0E 00 00 F9 00 07 00 3D 02 88 01 05 00 00 00 B8 AA");

        List<IEBusCommandMethod> commandMethods = commandRegistry.find(bs);

        IEBusCommandMethod commandMethod = commandRegistry.getConfigurationById("solar.solar_yield",
                IEBusCommandMethod.Method.BROADCAST);

        commandMethod = commandMethods.get(0);

        try {
            Map<String, Object> decodeTelegram = EBusCommandUtils.decodeTelegram(commandMethod, bs);

            ByteBuffer buildMasterTelegram = EBusCommandUtils.buildMasterTelegram(commandMethod, bs[0], bs[1],
                    decodeTelegram);

            System.out.println("EBusWolfSM1TelegramTest.xxx2()");

        } catch (EBusTypeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        checkMask("solar.solar_yield", bs, IEBusCommandMethod.Method.BROADCAST);
        xxx("solar.solar_yield", bs, IEBusCommandMethod.Method.BROADCAST);
        canResolve(bs);

    }

    @Test
    public void xxx2() {
        byte[] bs = null;

        bs = EBusUtils.toByteArray("71 FE 50 17 10 08 91 F0 01 0A 04 00 80 00 80 00 80 00 80 00 80 F7 AA");

        checkMask("solar.solar_data", bs, IEBusCommandMethod.Method.BROADCAST);
        xxx("solar.solar_data", bs, IEBusCommandMethod.Method.BROADCAST);
        canResolve(bs);

    }

    @Test
    public void xxx3() {
        byte[] bs = null;

        bs = EBusUtils.toByteArray("30 76 50 22 03 CC 2B 0A BF 00 02 07 01 DA");
        checkMask("solar.e1", bs, IEBusCommandMethod.Method.GET);
        xxx("solar.e1", bs, IEBusCommandMethod.Method.GET);
        canResolve(bs);

    }

    protected void xxx(String commandId, byte[] data, IEBusCommandMethod.Method type) {

        IEBusCommandMethod commandChannel = commandRegistry.getConfigurationById(commandId, type);

        try {

            Map<String, Object> map = EBusCommandUtils.decodeTelegram(commandChannel, data);
            if (map != null) {
                for (Entry<String, Object> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " -> " + entry.getValue());
                }
            }

        } catch (EBusTypeException e) {
            logger.error("error!", e);
        }
    }

    protected void checkMask(String commandId, byte[] data, IEBusCommandMethod.Method type) {

        ByteBuffer wrap = ByteBuffer.wrap(data);
        IEBusCommandMethod commandChannel = commandRegistry.getConfigurationById(commandId, type);

        try {
            ByteBuffer masterTelegram = EBusCommandUtils.buildMasterTelegram(commandChannel, (byte) 0x00, (byte) 0xFF,
                    null);
            ByteBuffer mask = commandChannel.getMasterTelegramMask();

            System.out.println("MASK:     " + EBusUtils.toHexDumpString(mask));
            System.out.println("DATA:     " + EBusUtils.toHexDumpString(data));
            System.out.println("COMPOSED: " + EBusUtils.toHexDumpString(masterTelegram));
            System.out.println("MATCHS?   " + commandRegistry.matchesCommand(commandChannel, wrap));

            // Map<String, Object> map = EBusCommandUtils.decodeTelegram(commandChannel, data);
            // if (map != null) {
            // for (byte b : data) {
            //
            // }
            // }

        } catch (EBusTypeException e) {
            logger.error("error!", e);
        }
    }

    private boolean canResolve(byte[] data) {

        List<IEBusCommandMethod> list = commandRegistry.find(data);

        if (list.isEmpty()) {
            Assert.fail("Expected an filled array!");
        }

        for (IEBusCommandMethod commandChannel : list) {
            logger.info(">>> " + commandChannel.toString());
            try {
                Map<String, Object> map = EBusCommandUtils.decodeTelegram(commandChannel, data);
                if (map.isEmpty()) {
                    Assert.fail("Expected a result map!");
                } else {

                    for (Entry<String, Object> entry : map.entrySet()) {
                        logger.info(entry.getKey() + " > " + entry.getValue());
                    }
                }
            } catch (EBusTypeException e) {
                logger.error("error!", e);
            }
        }

        return true;
    }

}
