/**
 * Copyright (c) 2017-2021 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.cfg;

import static org.junit.Assert.fail;

import java.nio.ByteBuffer;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.cfg.std.EBusConfigurationReader;
import de.csdev.ebus.command.EBusCommandException;
import de.csdev.ebus.command.EBusCommandRegistry;
import de.csdev.ebus.command.EBusCommandUtils;
import de.csdev.ebus.command.IEBusCommand;
import de.csdev.ebus.command.IEBusCommandCollection;
import de.csdev.ebus.command.IEBusCommandMethod;
import de.csdev.ebus.command.IEBusCommandMethod.Type;
import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.core.EBusConsts;
import de.csdev.ebus.utils.EBusUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class BuildTelegramTest {

    private final Logger logger = LoggerFactory.getLogger(BuildTelegramTest.class);

    @Test
    public void test_BuildMasterTelegram() {

        EBusCommandRegistry registry = new EBusCommandRegistry(EBusConfigurationReader.class, true);

        for (IEBusCommandCollection collection : registry.getCommandCollections()) {
            for (IEBusCommand command : collection.getCommands()) {
                for (IEBusCommandMethod commandChannel : command.getCommandMethods()) {

                    try {
                        byte targetAddress = commandChannel.getType() == Type.MASTER_MASTER ? (byte) 0xFF
                                : commandChannel.getType() == Type.BROADCAST ? EBusConsts.BROADCAST_ADDRESS
                                        : (byte) 0x04;

                        ByteBuffer masterTelegram = EBusCommandUtils.buildMasterTelegram(commandChannel, (byte) 0xFF,
                                targetAddress, null);

                        logger.debug(String.format("%-9s| %-40s| %-8s| %s", commandChannel.getMethod(), command.getId(),
                                collection.getId(), EBusUtils.toHexDumpString(masterTelegram)));

                    } catch (EBusTypeException e) {
                        e.printStackTrace();
                        fail();
                    } catch (EBusCommandException e) {
                        e.printStackTrace();
                        fail();
                    }
                }

            }
        }
    }

    @Test
    public void test_BuildMasterTelegramMask() {

        EBusCommandRegistry registry = new EBusCommandRegistry(EBusConfigurationReader.class, true);

        for (IEBusCommandCollection collection : registry.getCommandCollections()) {
            for (IEBusCommand command : collection.getCommands()) {
                for (IEBusCommandMethod commandChannel : command.getCommandMethods()) {

                    ByteBuffer mask = commandChannel.getMasterTelegramMask();

                    logger.debug(String.format("%-9s| %-40s| %-8s| %s", commandChannel.getMethod(), command.getId(),
                            collection.getId(), EBusUtils.toHexDumpString(mask)));

                }

            }
        }
    }
}
