/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.core;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusConsts {

    /** The Broadcast address */
    public final static byte BROADCAST_ADDRESS = (byte) 0xFE;

    /** The ACK FAIL answer byte */
    public final static byte ACK_FAIL = (byte) 0xFF;

    /** The ACK OK answer byte */
    public final static byte ACK_OK = (byte) 0x00;

    /** The SYN byte */
    public final static byte SYN = (byte) 0xAA;

    /** The escape byte for expanded bytes */
    public final static byte ESCAPE = (byte) 0xA9;

    /** replacement for an excape symbol */
    public final static byte[] ESCAPE_REPLACEMENT = new byte[] { EBusConsts.ESCAPE, 0x00 };

    /** replacement for an sync symbol */
    public final static byte[] SYN_REPLACEMENT = new byte[] { EBusConsts.ESCAPE, 0x01 };

    /** eBUS standard collection id */
    public final static String COLLECTION_STD = "std";

    public final static String COMMAND_INQ_EXISTENCE = "common.inquiry_of_existence";

    public final static String COMMAND_SIGN_OF_LIFE = "common.sign_of_life";

    public final static String COMMAND_IDENTIFICATION = "common.identification";

}
