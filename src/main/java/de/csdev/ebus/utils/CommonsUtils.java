/**
 * Copyright (c) 2017-2023 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class CommonsUtils {
    
    private CommonsUtils() {
    }

    public static final void closeQuietly(InputStream stream) {
        try {
            if(stream != null) {
                stream.close();
            }
        } catch (IOException e) {
            // noop
        }
    }
    public static final void closeQuietly(OutputStream stream) {
        try {
            if(stream != null) {
                stream.close();
            }
        } catch (IOException e) {
            // noop
        }
    }

    public static final void closeQuietly(Writer stream) {
        try {
            if(stream != null) {
                stream.close();
            }
        } catch (IOException e) {
            // noop
        }
    }

    public static final void closeQuietly(Reader stream) {
        try {
            if(stream != null) {
                stream.close();
            }
        } catch (IOException e) {
            // noop
        }
    }

    public static final void closeQuietly(Socket stream) {
        try {
            if(stream != null) {
                stream.close();
            }
        } catch (IOException e) {
            // noop
        }
    }
}
