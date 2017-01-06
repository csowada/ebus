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
 * @author Christian Sowada
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
}
