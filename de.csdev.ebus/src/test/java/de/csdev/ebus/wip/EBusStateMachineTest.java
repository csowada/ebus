/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.wip;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.core.EBusDataException;
import de.csdev.ebus.core.EBusReceiveStateMachine;
import de.csdev.ebus.utils.EBusUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusStateMachineTest {

    private static final Logger logger = LoggerFactory.getLogger(EBusStateMachineTest.class);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testNoAnswer() throws EBusDataException {

        logger.info("Master/Slave - No Answer ...");

        thrown.expect(EBusDataException.class);

        runMachine(EBusUtils.toByteArray("AA 30 08 50 22 03 CC 1A 27 59 AA"));
    }

    @Test
    public void testNACK() throws EBusDataException {

        logger.info("Master/Slave - NACK");

        thrown.expect(EBusDataException.class);

        runMachine(EBusUtils.toByteArray("AA 30 08 50 22 03 CC 1A 27 59 FF AA"));
    }

    @Test
    public void testACK() throws EBusDataException {

        runMachine(EBusUtils.toByteArray("AA 30 08 50 22 03 CC 1A 27 59 00 02 97 00 E2 00 AA"));

        runMachine(EBusUtils.toByteArray("AA 30 FE 07 00 09 00 80 10 08 16 23 10 04 14 A2 AA"));

        runMachine(EBusUtils.toByteArray("71 FE 50 18 0E 00 00 AE 02 07 00 A3 02 C3 01 02 00 00 00 7E AA"));

    }

    @Test
    public void testA() {
        try {
            EBusReceiveStateMachine machine = new EBusReceiveStateMachine();

            byte[] byteArray = EBusUtils.toByteArray("30 76 50 23 05 D4 66 00 00 00 0C 00 00 00 AA");

            /*
             * 30 76 50 23 05 D4 66 00 00 00 0C
             * 00 00 00 AA
             */

            machine.update((byte) 0xAA);

            for (byte b : byteArray) {

                machine.update(b);

            }

            logger.info("Machine state: {}", machine.getState().toString());
            logger.info("Telegram available: {}", machine.isTelegramAvailable());

        } catch (EBusDataException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void runMachine(byte[] byteArray) throws EBusDataException {
        EBusReceiveStateMachine machine = new EBusReceiveStateMachine();

        for (byte b : byteArray) {
            machine.update(b);
        }
    }

}
