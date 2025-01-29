/**
 * Copyright (c) 2017-2025 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.command.IEBusCommandMethod;
import de.csdev.ebus.core.EBusConsts;
import de.csdev.ebus.service.parser.IEBusParserListener;

/**
 *
 * @author Christian Sowada - Initial contribution
 */
@NonNullByDefault
public class EBusTelegramWriter implements IEBusParserListener {

    @NonNullByDefault({})
    private final Logger logger = LoggerFactory.getLogger(EBusTelegramWriter.class);

    private @Nullable BufferedWriter writerResolved;

    private @Nullable BufferedWriter writerUnresolved;

    private File loggingDirectory;

    public EBusTelegramWriter(File loggingDirectory) {
        this.loggingDirectory = loggingDirectory;
    }

    @Override
    public void onTelegramResolved(IEBusCommandMethod commandChannel,
            Map<String, @Nullable Object> result, byte[] receivedData,
            @Nullable Integer sendQueueId) {

        try {
            if (writerResolved == null) {
                writerResolved = open("ebus-resolved.csv");
            }
            String comment = String.format("%s > %s.%s", commandChannel.getMethod(),
                    commandChannel.getParent().getParentCollection().getId(), commandChannel.getParent().getId());

            write(writerResolved, receivedData, comment);

        } catch (IOException e) {
            logger.error(EBusConsts.LOG_ERR_DEF, e);
        }

    }

    @Override
    public void onTelegramResolveFailed(@Nullable IEBusCommandMethod commandChannel, byte @Nullable [] receivedData,
            @Nullable Integer sendQueueId, @Nullable String exceptionMessage) {

        try {
            if (writerUnresolved == null) {
                writerUnresolved = open("ebus-unresolved.csv");
            }

            if (receivedData != null) {
                write(writerUnresolved, receivedData, "");
            }

        } catch (IOException e) {
            logger.error(EBusConsts.LOG_ERR_DEF, e);
        }
    }

    /**
     * Opens a CSV file
     *
     * @param csvFile The file object
     * @throws IOException
     */
    private BufferedWriter open(final String filename) throws IOException {

        File file = new File(loggingDirectory, filename);

        BufferedWriter writer = new BufferedWriter(new FileWriter(file));

        if (!file.exists()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Create new file {}", filename);
            }
            if (!file.createNewFile()) {
                throw new IOException("Unable to create file!");
            }
        }

        writer.write("Date/Time;");
        writer.write("SRC;");
        writer.write("DST;");
        writer.write("CMD;");
        writer.write("REMAIN_DATA;");

        writer.newLine();
        writer.flush();

        return writer;
    }

    /**
     * Close the CSV file
     */
    public void close() {
        try {
            Writer w = this.writerResolved;
            if (w != null) {
                w.flush();
                w.close();
                this.writerResolved = null;
            }

            w = this.writerUnresolved;
            if (w != null) {
                w.flush();
                w.close();
                writerUnresolved = null;
            }

        } catch (IOException e) {
            logger.error("error!", e);
        }
    }

    private void write(final @Nullable BufferedWriter writer, final byte[] receivedData, final @Nullable String comment) throws IOException {

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();

        StringBuilder sb = new StringBuilder();

        sb.append(df.format(date));
        sb.append(";");

        sb.append('"' + EBusUtils.toHexDumpString(receivedData[0]) + '"');
        sb.append(";");

        sb.append('"' + EBusUtils.toHexDumpString(receivedData[1]) + '"');
        sb.append(";");

        byte[] command = Arrays.copyOfRange(receivedData, 2, 4);
        sb.append('"' + EBusUtils.toHexDumpString(command).toString() + '"');
        sb.append(";");

        byte[] rest = Arrays.copyOfRange(receivedData, 4, receivedData.length);
        sb.append('"' + EBusUtils.toHexDumpString(rest).toString() + '"');
        sb.append(";");

        sb.append(comment == null ? "" : comment);

        sb.append("\n");

        if (writer != null) {
            writer.append(sb).flush();
        }
    }
}
