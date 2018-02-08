/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Writes byte data to capture text file. It is used to replay the data later on.
 *
 * @author Christian Sowada - Initial contribution
 *
 */
public class EmulatorCapture {

    private static final Logger logger = LoggerFactory.getLogger(EmulatorCapture.class);

    protected long referenceTime;

    protected PrintWriter writer;

    public EmulatorCapture(File outputFile) {
        referenceTime = System.currentTimeMillis();
        try {
            writer = new PrintWriter(outputFile, "UTF-8");
        } catch (FileNotFoundException e) {
            logger.error("error!", e);
        } catch (UnsupportedEncodingException e) {
            logger.error("error!", e);
        }
    }

    public void write(byte[] buffer, int len) {
        byte[] copyOf = Arrays.copyOf(buffer, len);
        write(copyOf);
    }

    public void write(byte[] buffer, int from, int len) {
        byte[] copyOf = Arrays.copyOfRange(buffer, from, len);
        write(copyOf);
    }

    public void write(byte[] buffer) {
        writer.printf("%09d", System.currentTimeMillis() - referenceTime);
        writer.print(" - ");
        writer.println(EBusUtils.toHexDumpString(buffer).toString());
        writer.flush();

        referenceTime = System.currentTimeMillis();
    }

    public void close() {
        writer.flush();
        writer.close();
    }

}
