/**
 * Copyright (c) 2017-2025 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.wip;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import de.csdev.ebus.command.EBusCommandUtils;
import de.csdev.ebus.core.EBusDataException;
import de.csdev.ebus.utils.EBusUtils;

public class SynEscapeTest {

    public void QuickCheck(byte source, byte target, String command, String masterData, String assertEscaped,
            String assertUnescaped) throws EBusDataException {

        byte[] masterTelegram = EBusUtils.toByteArray(EBusCommandUtils.buildPartMasterTelegram(
                source, target,
                EBusUtils.toByteArray(command),
                EBusUtils.toByteArray(masterData)));

        StringBuilder escaped = EBusUtils.toHexDumpString(masterTelegram);
        assertEquals(assertEscaped, escaped.toString());

        byte[] v = EBusCommandUtils.checkRawTelegram(masterTelegram);
        StringBuilder unescaped = EBusUtils.toHexDumpString(v);
        assertEquals(assertUnescaped, unescaped.toString());
    }

    @Test
    public void SynByteInCrc() {
        try {
            QuickCheck((byte) 0x00, (byte) 0x00, "B5 13", "04 15 00",
                    "00 00 B5 13 03 04 15 00 A9 01",
                    "00 00 B5 13 03 04 15 00 AA");

        } catch (NumberFormatException | EBusDataException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void SynByteInMasterData() {
        try {

            QuickCheck((byte) 0xFF, (byte) 0x08, "00 00", "AA 26 02",
                    "FF 08 00 00 03 A9 01 26 02 78",
                    "FF 08 00 00 03 AA 26 02 78");

            QuickCheck((byte) 0x01, (byte) 0xFE, "20 20", "62 73 AA 00",
                    "01 FE 20 20 04 62 73 A9 01 00 78",
                    "01 FE 20 20 04 62 73 AA 00 78");


            QuickCheck((byte) 0x00, (byte) 0x00, "B5 13", "04 15 00",
                    "00 00 B5 13 03 04 15 00 A9 01",
                    "00 00 B5 13 03 04 15 00 AA");

        } catch (NumberFormatException | EBusDataException e) {
            fail(e.getMessage());
        }
    }

}
