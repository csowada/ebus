/**
 * Copyright (c) 2016-2020 by the respective copyright holders.
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.command.IEBusCommandMethod;
import de.csdev.ebus.service.parser.IEBusParserListener;

/**
 *
 * @author Christian Sowada - Initial contribution
 */
public class EBusTelegramWriter implements IEBusParserListener {

    private final Logger logger = LoggerFactory.getLogger(EBusTelegramWriter.class);

    private BufferedWriter writerResolved;

    private BufferedWriter writerUnresolved;

    private File loggingDirectory;

    public EBusTelegramWriter(File loggingDirectory) {
        this.loggingDirectory = loggingDirectory;
    }

    @Override
    public void onTelegramResolved(@NonNull IEBusCommandMethod commandChannel,
            @NonNull Map<@NonNull String, @NonNull Object> result, byte @NonNull [] receivedData,
            @Nullable Integer sendQueueId) {

        try {
            if (writerResolved == null) {
                writerResolved = open("ebus-resolved.csv");
            }
            String comment = String.format("%s > %s.%s", commandChannel.getMethod(),
                    commandChannel.getParent().getParentCollection().getId(), commandChannel.getParent().getId());

            write(writerResolved, receivedData, comment);

        } catch (IOException e) {
            logger.error("error!", e);
        }

    }

    @Override
    public void onTelegramResolveFailed(@Nullable IEBusCommandMethod commandChannel, byte @Nullable [] receivedData,
            @Nullable Integer sendQueueId, @Nullable String exceptionMessage) {

        try {
            if (writerUnresolved == null) {
                writerUnresolved = open("ebus-unresolved.csv");
            }
            write(writerUnresolved, receivedData, "");

        } catch (IOException e) {
            logger.error("error!", e);
        }
    }

    /**
     * Opens a CSV file
     *
     * @param csvFile The file object
     * @throws IOException
     */
    private BufferedWriter open(String filename) throws IOException {

        // File loggingFolder = new File(System.getProperty("openhab.logdir"));
        File file = new File(loggingDirectory, filename);

        BufferedWriter writer = new BufferedWriter(new FileWriter(file));

        if (!file.exists()) {
            file.createNewFile();
        }

        logger.warn(file.getAbsolutePath());

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
            if (writerResolved != null) {
                writerResolved.flush();
                writerResolved.close();
                writerResolved = null;
            }

            if (writerUnresolved != null) {
                writerUnresolved.flush();
                writerUnresolved.close();
                writerUnresolved = null;
            }

        } catch (IOException e) {
            logger.error("error!", e);
        }
    }

    private void write(BufferedWriter writer, byte[] receivedData, String comment) throws IOException {

        synchronized (writer) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();

            writer.write(df.format(date));
            writer.write(";");

            writer.write('"' + EBusUtils.toHexDumpString(receivedData[0]) + '"');
            writer.write(";");

            writer.write('"' + EBusUtils.toHexDumpString(receivedData[1]) + '"');
            writer.write(";");

            byte[] command = Arrays.copyOfRange(receivedData, 2, 4);
            writer.write('"' + EBusUtils.toHexDumpString(command).toString() + '"');
            writer.write(";");

            byte[] rest = Arrays.copyOfRange(receivedData, 4, receivedData.length);
            writer.write('"' + EBusUtils.toHexDumpString(rest).toString() + '"');
            writer.write(";");

            writer.write(comment);

            writer.newLine();
            writer.flush();
        }

    }
}
