/**
 * Copyright (c) 2016-2020 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.core.connection;

import java.io.IOException;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public interface IEBusConnection {

    /**
     * Open the connection
     *
     * @return
     * @throws IOException
     */
    public boolean open() throws IOException;

    /**
     * Close the connection
     *
     * @return
     * @throws IOException
     */
    public boolean close() throws IOException;

    /**
     * Returns <code>true</code> if the connection is open and working
     *
     * @return
     * @throws IOException
     */
    public boolean isOpen() throws IOException;

    /**
     * Reads on byte (blocking) from the connection.
     *
     * @param lowLatency
     * @return
     * @throws IOException
     */
    public int readByte(final boolean lowLatency) throws IOException;

    /**
     * Returns <code>true</code> if not data is available in the buffer
     *
     * @return
     * @throws IOException
     */
    public boolean isReceiveBufferEmpty() throws IOException;

    /**
     * Reads some number of bytes from the connection. Same as InputStream.
     *
     * @param buffer
     * @return
     * @throws IOException
     */
    public int readBytes(final byte[] buffer) throws IOException;

    /**
     * Writes a byte to the connection
     *
     * @param b
     * @throws IOException
     */
    public void writeByte(final int b) throws IOException;

    /**
     * Resets the connection and flushes all buffers
     *
     * @throws IOException
     */
    public void reset() throws IOException;
}
