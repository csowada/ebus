/**
 * Copyright (c) 2016-2020 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.command.EBusCommandRegistry;
import de.csdev.ebus.command.EBusCommandUtils;
import de.csdev.ebus.command.IEBusCommandMethod;
import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.utils.EBusUtils;

public class TestUtils {

    private static final Logger logger = LoggerFactory.getLogger(TestUtils.class);

    @SuppressWarnings("null")
    public static boolean canResolve(EBusCommandRegistry commandRegistry, byte[] data) {

        List<IEBusCommandMethod> list = commandRegistry.find(data);

        if (list.isEmpty()) {
            Assert.fail("Expected an filled array!");
        }

        for (IEBusCommandMethod commandChannel : list) {
            logger.debug(">>> " + commandChannel.toString());
            try {
                Map<String, Object> map = EBusCommandUtils.decodeTelegram(commandChannel, data);
                if (map.isEmpty()) {
                    Assert.fail("Expected a result map!");
                } else {

                    for (Entry<String, Object> entry : map.entrySet()) {
                        if (entry.getValue() instanceof byte[]) {
                            logger.trace(entry.getKey() + " > " + EBusUtils.toHexDumpString((byte[]) entry.getValue()));
                        } else {
                            logger.trace(entry.getKey() + " > " + entry.getValue());
                        }

                    }
                }
            } catch (EBusTypeException e) {
                logger.error("error!", e);
            }
        }

        return true;
    }

}
