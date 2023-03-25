/**
 * Copyright (c) 2017-2023 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command.datatype;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.command.datatypes.EBusTypeRegistry;
import de.csdev.ebus.command.datatypes.IEBusType;
import de.csdev.ebus.command.datatypes.std.EBusTypeBit;
import de.csdev.ebus.utils.EBusUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class ReplaceValueTest {

    private final Logger logger = LoggerFactory.getLogger(ReplaceValueTest.class);

    EBusTypeRegistry types;
    private byte[] encode;

    @Before
    public void before() throws EBusTypeException {
        types = new EBusTypeRegistry();
    }

    @Test
    public void test_CheckReplaceValues() {

        for (String typeName : types.getTypesNames()) {

            if (!typeName.equals(EBusTypeBit.TYPE_BIT)) {

                IEBusType<Object> type = types.getType(typeName);
                byte[] encodedData = null;
                String error = null;

                try {
                    encodedData = type.encode(null);
                } catch (RuntimeException e) {
                    error = "Exception: " + e.getMessage();
                } catch (EBusTypeException e) {
                    error = "Exception: " + e.getMessage();
                }

                if (error != null) {
                    logger.warn(String.format("%-10s | %s", typeName, error));
                    fail(String.format("Data type '%s' returns with error %s", typeName, error));

                } else if (encodedData == null) {

                } else if (encodedData.length > 0) {
                    logger.debug(String.format("%-10s | %s", typeName, EBusUtils.toHexDumpString(encodedData)));

                } else {
                    fail(String.format("Encoded data invalid for data type '%s' !", typeName));
                }

                try {
                    Object decode = type.decode(encodedData);
                    assertNull("Datatype " + typeName + ": ", decode);

                } catch (EBusTypeException e) {
                    logger.error("error!", e);
                }

            }
        }
    }

    @Test
    public void test_EncodeDecodeEqualsTest() throws EBusTypeException {

        for (String typeName : types.getTypesNames()) {

            IEBusType<Object> type = types.getType(typeName);

            byte[] bs = new byte[type.getTypeLength()];
            logger.debug(type.toString());

            try {
                Object decode = type.decode(bs);
                encode = type.encode(decode);

                if (Arrays.equals(bs, encode)) {
                    logger.debug(String.format("%-10s | %s", typeName, "OK"));
                } else {
                    logger.warn(String.format("%-10s | %s - %s", typeName, EBusUtils.toHexDumpString(encode), decode));
                }

            } catch (Exception e) {
                logger.warn(String.format("%-10s | %s", typeName, e.getMessage()));
            }
        }
    }

}
