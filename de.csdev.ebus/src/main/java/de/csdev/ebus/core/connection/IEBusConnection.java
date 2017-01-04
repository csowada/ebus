/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.core.connection;

import java.io.IOException;

/**
 * @author Christian Sowada
 *
 */
public interface IEBusConnection {

    public boolean open() throws IOException;

    public boolean close() throws IOException;

    public boolean isOpen() throws IOException;

    public int readByte(boolean lowLatency) throws IOException;

    public boolean isReceiveBufferEmpty() throws IOException;

    public int readBytes(byte[] buffer) throws IOException;

    public void writeByte(int b) throws IOException;

    public void reset() throws IOException;
}
