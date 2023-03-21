/**
 * Copyright (c) 2017-2023 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.ByteBuffer;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.command.EBusCommandUtils;
import de.csdev.ebus.core.EBusDataException.EBusError;
import de.csdev.ebus.utils.EBusUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusStateMachineTest {

    EBusReceiveStateMachine machine = new EBusReceiveStateMachine();

    private static final Logger logger = LoggerFactory.getLogger(EBusStateMachineTest.class);

    @Before
    public void init() {
        machine = new EBusReceiveStateMachine();
    }

    @Test
    public void testNoAnswer() {

        logger.info("Master/Slave - No Answer ...");

        try {
            runMachine(EBusUtils.toByteArray("AA 30 08 50 22 03 CC 1A 27 59 AA"));
            fail("expected exception was not occured.");

        } catch (EBusDataException e) {
            assertTrue(e.getErrorCode().equals(EBusError.NO_SLAVE_RESPONSE));
        }

        assertFalse(machine.isReceivingTelegram());
        assertFalse(machine.isSync());
        assertFalse(machine.isTelegramAvailable());
        assertFalse(machine.isWaitingForMasterACK());
        assertFalse(machine.isWaitingForMasterSYN());
        assertFalse(machine.isWaitingForSlaveAnswer());

    }

    @Test
    public void testNACK() throws EBusDataException {

        logger.info("Master/Slave - NACK");

        try {
            runMachine(EBusUtils.toByteArray("AA 30 08 50 22 03 CC 1A 27 59 FF AA"));
        } catch (EBusDataException e) {
            // System.out.println("EBusStateMachineTest.testNACK()" + e);
            assertTrue(e.getErrorCode().equals(EBusError.SLAVE_ACK_FAIL));
        }

    }

    @Test
    public void testACK() throws EBusDataException {

        runMachine(EBusUtils.toByteArray("AA 30 08 50 22 03 CC 1A 27 59 00 02 97 00 E2 00 AA"));

        runMachine(EBusUtils.toByteArray("AA 30 FE 07 00 09 00 80 10 08 16 23 10 04 14 A2 AA"));

        runMachine(EBusUtils.toByteArray("71 FE 50 18 0E 00 00 AE 02 07 00 A3 02 C3 01 02 00 00 00 7E AA"));

    }

    @Test
    public void testMachineMethodsNoResponse() {

        try {

            // AA 30 76 50 22 03 CC 2B 0A BF AA

            // SYN
            machine.update((byte) 0xAA);
            assertTrue(machine.isSync());
            assertFalse(machine.isReceivingTelegram());
            assertFalse(machine.isTelegramAvailable());
            assertFalse(machine.isWaitingForMasterACK());
            assertFalse(machine.isWaitingForMasterSYN());
            assertFalse(machine.isWaitingForSlaveAnswer());

            // SRC
            machine.update((byte) 0x30);
            assertTrue(machine.isReceivingTelegram());
            assertFalse(machine.isTelegramAvailable());
            assertFalse(machine.isWaitingForMasterACK());
            assertFalse(machine.isWaitingForMasterSYN());
            assertFalse(machine.isWaitingForSlaveAnswer());

            // TGT
            machine.update((byte) 0x76);
            assertTrue(machine.isReceivingTelegram());
            assertFalse(machine.isTelegramAvailable());
            assertFalse(machine.isWaitingForMasterACK());
            assertFalse(machine.isWaitingForMasterSYN());
            assertFalse(machine.isWaitingForSlaveAnswer());

            // PRIMARY COMMAND
            machine.update((byte) 0x50);
            assertTrue(machine.isReceivingTelegram());
            assertFalse(machine.isTelegramAvailable());
            assertFalse(machine.isWaitingForMasterACK());
            assertFalse(machine.isWaitingForMasterSYN());
            assertFalse(machine.isWaitingForSlaveAnswer());

            // SECONDARY COMMAND
            machine.update((byte) 0x22);
            assertTrue(machine.isReceivingTelegram());
            assertFalse(machine.isTelegramAvailable());
            assertFalse(machine.isWaitingForMasterACK());
            assertFalse(machine.isWaitingForMasterSYN());
            assertFalse(machine.isWaitingForSlaveAnswer());

            // LEN
            machine.update((byte) 0x03);
            assertTrue(machine.isReceivingTelegram());
            assertFalse(machine.isTelegramAvailable());
            assertFalse(machine.isWaitingForMasterACK());
            assertFalse(machine.isWaitingForMasterSYN());
            assertFalse(machine.isWaitingForSlaveAnswer());

            // DATA 1
            machine.update((byte) 0xCC);
            assertTrue(machine.isReceivingTelegram());
            assertFalse(machine.isTelegramAvailable());
            assertFalse(machine.isWaitingForMasterACK());
            assertFalse(machine.isWaitingForMasterSYN());
            assertFalse(machine.isWaitingForSlaveAnswer());

            // DATA 2
            machine.update((byte) 0x2B);
            assertTrue(machine.isReceivingTelegram());
            assertFalse(machine.isTelegramAvailable());
            assertFalse(machine.isWaitingForMasterACK());
            assertFalse(machine.isWaitingForMasterSYN());
            assertFalse(machine.isWaitingForSlaveAnswer());

            // DATA 3
            machine.update((byte) 0x0A);
            assertTrue(machine.isReceivingTelegram());
            assertFalse(machine.isTelegramAvailable());
            assertFalse(machine.isWaitingForMasterACK());
            assertFalse(machine.isWaitingForMasterSYN());
            assertFalse(machine.isWaitingForSlaveAnswer());

            // MASTER CRC
            machine.update((byte) 0xBF);
            assertTrue(machine.isReceivingTelegram());
            assertFalse(machine.isTelegramAvailable());
            assertFalse(machine.isWaitingForMasterACK());
            assertFalse(machine.isWaitingForMasterSYN());
            assertTrue(machine.isWaitingForSlaveAnswer());

            // SLAVE ACK
            machine.update((byte) 0xAA);

            fail("exception expected!");

        } catch (EBusDataException e) {
            // okay
        }

        assertFalse(machine.isReceivingTelegram());
        assertFalse(machine.isTelegramAvailable());
        assertFalse(machine.isWaitingForMasterACK());
        assertFalse(machine.isWaitingForMasterSYN());
        assertFalse(machine.isWaitingForSlaveAnswer());
    }

    @Test
    public void testMachineMethods() {

        try {

            // AA 30 76 50 22 03 CC 2B 0A BF 00 02 07 01 DA

            // SYN
            machine.update((byte) 0xAA);
            assertTrue(machine.isSync());
            assertFalse(machine.isReceivingTelegram());
            assertFalse(machine.isTelegramAvailable());
            assertFalse(machine.isWaitingForMasterACK());
            assertFalse(machine.isWaitingForMasterSYN());
            assertFalse(machine.isWaitingForSlaveAnswer());

            // SRC
            machine.update((byte) 0x30);
            assertTrue(machine.isReceivingTelegram());
            assertFalse(machine.isTelegramAvailable());
            assertFalse(machine.isWaitingForMasterACK());
            assertFalse(machine.isWaitingForMasterSYN());
            assertFalse(machine.isWaitingForSlaveAnswer());

            // TGT
            machine.update((byte) 0x76);
            assertTrue(machine.isReceivingTelegram());
            assertFalse(machine.isTelegramAvailable());
            assertFalse(machine.isWaitingForMasterACK());
            assertFalse(machine.isWaitingForMasterSYN());
            assertFalse(machine.isWaitingForSlaveAnswer());

            // PRIMARY COMMAND
            machine.update((byte) 0x50);
            assertTrue(machine.isReceivingTelegram());
            assertFalse(machine.isTelegramAvailable());
            assertFalse(machine.isWaitingForMasterACK());
            assertFalse(machine.isWaitingForMasterSYN());
            assertFalse(machine.isWaitingForSlaveAnswer());

            // SECONDARY COMMAND
            machine.update((byte) 0x22);
            assertTrue(machine.isReceivingTelegram());
            assertFalse(machine.isTelegramAvailable());
            assertFalse(machine.isWaitingForMasterACK());
            assertFalse(machine.isWaitingForMasterSYN());
            assertFalse(machine.isWaitingForSlaveAnswer());

            // LEN
            machine.update((byte) 0x03);
            assertTrue(machine.isReceivingTelegram());
            assertFalse(machine.isTelegramAvailable());
            assertFalse(machine.isWaitingForMasterACK());
            assertFalse(machine.isWaitingForMasterSYN());
            assertFalse(machine.isWaitingForSlaveAnswer());

            // DATA 1
            machine.update((byte) 0xCC);
            assertTrue(machine.isReceivingTelegram());
            assertFalse(machine.isTelegramAvailable());
            assertFalse(machine.isWaitingForMasterACK());
            assertFalse(machine.isWaitingForMasterSYN());
            assertFalse(machine.isWaitingForSlaveAnswer());

            // DATA 2
            machine.update((byte) 0x2B);
            assertTrue(machine.isReceivingTelegram());
            assertFalse(machine.isTelegramAvailable());
            assertFalse(machine.isWaitingForMasterACK());
            assertFalse(machine.isWaitingForMasterSYN());
            assertFalse(machine.isWaitingForSlaveAnswer());

            // DATA 3
            machine.update((byte) 0x0A);
            assertTrue(machine.isReceivingTelegram());
            assertFalse(machine.isTelegramAvailable());
            assertFalse(machine.isWaitingForMasterACK());
            assertFalse(machine.isWaitingForMasterSYN());
            assertFalse(machine.isWaitingForSlaveAnswer());

            // MASTER CRC
            machine.update((byte) 0xBF);
            assertTrue(machine.isReceivingTelegram());
            assertFalse(machine.isTelegramAvailable());
            assertFalse(machine.isWaitingForMasterACK());
            assertFalse(machine.isWaitingForMasterSYN());
            assertTrue(machine.isWaitingForSlaveAnswer());

            // SLAVE ACK
            machine.update((byte) 0x00);
            assertTrue(machine.isReceivingTelegram());
            assertFalse(machine.isTelegramAvailable());
            assertFalse(machine.isWaitingForMasterACK());
            assertFalse(machine.isWaitingForMasterSYN());
            assertFalse(machine.isWaitingForSlaveAnswer());

            // LEN
            machine.update((byte) 0x02);
            assertTrue(machine.isReceivingTelegram());
            assertFalse(machine.isTelegramAvailable());
            assertFalse(machine.isWaitingForMasterACK());
            assertFalse(machine.isWaitingForMasterSYN());
            assertFalse(machine.isWaitingForSlaveAnswer());

            // DATA 1
            machine.update((byte) 0x07);
            assertTrue(machine.isReceivingTelegram());
            assertFalse(machine.isTelegramAvailable());
            assertFalse(machine.isWaitingForMasterACK());
            assertFalse(machine.isWaitingForMasterSYN());
            assertFalse(machine.isWaitingForSlaveAnswer());

            // DATA 2
            machine.update((byte) 0x01);
            assertTrue(machine.isReceivingTelegram());
            assertFalse(machine.isTelegramAvailable());
            assertFalse(machine.isWaitingForMasterACK());
            assertFalse(machine.isWaitingForMasterSYN());
            assertFalse(machine.isWaitingForSlaveAnswer());

            // SLAVE CRC
            machine.update((byte) 0xDA);
            assertTrue(machine.isReceivingTelegram());
            assertFalse(machine.isTelegramAvailable());
            assertTrue(machine.isWaitingForMasterACK());
            assertFalse(machine.isWaitingForMasterSYN());
            assertFalse(machine.isWaitingForSlaveAnswer());

            // MASTER ACK
            machine.update((byte) 0x00);
            assertTrue(machine.isReceivingTelegram());
            assertFalse(machine.isTelegramAvailable());
            assertFalse(machine.isWaitingForMasterACK());
            assertTrue(machine.isWaitingForMasterSYN());
            assertFalse(machine.isWaitingForSlaveAnswer());

            // MASTER SYN
            machine.update((byte) 0xAA);
            assertFalse(machine.isReceivingTelegram());
            assertTrue(machine.isTelegramAvailable());
            assertFalse(machine.isWaitingForMasterACK());
            assertFalse(machine.isWaitingForMasterSYN());
            assertFalse(machine.isWaitingForSlaveAnswer());

        } catch (EBusDataException e) {
            fail("No exception expected!");
            e.printStackTrace();
        }

    }

    @Test
    public void testA() {
        try {
            runMachine(EBusUtils.toByteArray("AA 30 76 50 23 05 D4 66 00 00 00 0C 00 00 00 AA"));

            logger.info("Machine state: {}", machine.getState().toString());
            logger.info("Telegram available: {}", machine.isTelegramAvailable());

            assertTrue(machine.isTelegramAvailable());

        } catch (EBusDataException e) {
            fail("No exception expected!");
        }
    }

    @Test
    public void testB() {

        int errors = 0;

        byte[] command = new byte[] { 0x01, 0x02 };
        byte[] data = new byte[2];
        for (short i = 0; i < 256; i++) {
            data[0] = (byte) i;

            for (short j = 0; j < 256; j++) {
                data[1] = (byte) j;

                ByteBuffer bb = ByteBuffer.allocate(50);

                try {
                    ByteBuffer masterTelegramPart = EBusCommandUtils.buildCompleteTelegram((byte) 0xFF, (byte) 0x08,
                            command, data, data);

                    bb.put(masterTelegramPart);

                    runMachine(EBusUtils.toByteArray(masterTelegramPart));

                } catch (EBusDataException e) {

                    logger.info(EBusUtils.toHexDumpString(bb).toString());
                    logger.info(e.getLocalizedMessage());
                    errors++;
                }

            }
        }

        logger.info("Errors {}", errors);
    }

    @Test
    public void testMasterMaster() {

        try {
            runMachine(EBusUtils.toByteArray("AA 00 30 50 23 09 A0 74 27 01 00 5D 01 00 00 65 00"));

            assertTrue(machine.isReceivingTelegram());
            assertFalse(machine.isSync());
            assertFalse(machine.isTelegramAvailable());
            assertFalse(machine.isWaitingForMasterACK());
            assertTrue(machine.isWaitingForMasterSYN());
            assertFalse(machine.isWaitingForSlaveAnswer());

        } catch (EBusDataException e) {
            e.printStackTrace();
        }

    }

    private void runMachine(byte[] byteArray) throws EBusDataException {
        for (byte b : byteArray) {
            machine.update(b);
        }
    }

}
