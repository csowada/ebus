/**
 * Copyright (c) 2017-2023 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.core;

import org.eclipse.jdt.annotation.NonNull;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusConsts {

    private EBusConsts() {
        throw new IllegalStateException("Utility class");
    }

    /** The Broadcast address 0xFE */
    public static final  byte BROADCAST_ADDRESS = (byte) 0xFE;

    /** The ACK FAIL answer byte 0xFF */
    public static final byte ACK_FAIL = (byte) 0xFF;

    /** The ACK OK answer byte 0x00 */
    public static final byte ACK_OK = (byte) 0x00;

    /** The SYN byte 0xAA */
    public static final byte SYN = (byte) 0xAA;

    /** The escape byte for expanded bytes 0xA9 */
    public static final byte ESCAPE = (byte) 0xA9;

    /** replacement for an excape symbol */
    @SuppressWarnings("java:S2386")
    public static final byte @NonNull [] ESCAPE_REPLACEMENT = new byte[] { EBusConsts.ESCAPE, 0x00 };

    /** replacement for an sync symbol */
    @SuppressWarnings("java:S2386")
    public static final byte @NonNull [] SYN_REPLACEMENT = new byte[] { EBusConsts.ESCAPE, 0x01 };

    /** eBUS standard collection id */
    public static final String COLLECTION_STD = "std";

    public static final String COMMAND_INQ_EXISTENCE = "common.inquiry_of_existence";

    public static final String COMMAND_SIGN_OF_LIFE = "common.sign_of_life";

    public static final String COMMAND_IDENTIFICATION = "common.identification";

    public static final String LOG_ERR_DEF = "error!";
}
