/**
 * Copyright (c) 2016-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.core.EBusConsts;
import de.csdev.ebus.core.EBusWorkerThreadFactory;

/**
 * Emulates a virtual connection like a serial connection an replays data from a text file.
 *
 * @author Christian Sowada - Initial contribution
 *
 */
public class Emulator {

    private final Logger logger = LoggerFactory.getLogger(Emulator.class);

    private PipedInputStream in;

    private PipedOutputStream out;

    private ExecutorService pipeThreadExecutor;

    private ScheduledExecutorService playThreadExecutor;

    private Future<?> autoSyncFuture = null;

    private int factor = 10;

    private void stopAutoSync() {
        if (autoSyncFuture != null) {
            autoSyncFuture.cancel(true);
            autoSyncFuture = null;
        }
    }

    private void startAutoSync() {
        autoSyncFuture = playThreadExecutor.schedule(new Runnable() {

            @Override
            public void run() {
                write(EBusConsts.SYN);
            }

        }, 40 * factor, TimeUnit.MILLISECONDS);
    }

    public Emulator() {
        this(1, true);
    }

    public Emulator(int factor, boolean autoSync) {

        this.factor = factor;
        pipeThreadExecutor = Executors.newSingleThreadExecutor(new EBusWorkerThreadFactory("ebus-emu-pipe", false));
        playThreadExecutor = Executors.newScheduledThreadPool(1, new EBusWorkerThreadFactory("ebus-emu-play", false));

        if (autoSync) {
            startAutoSync();
        }

        try {
            in = new PipedInputStream();
            out = new PipedOutputStream(in);
        } catch (IOException e) {
            logger.error("error!", e);
        }
    }

    /**
     * Returns the input stream of the emulator
     *
     * @return
     */
    public InputStream getInputStream() {
        return in;
    }

    /**
     * Writes a byte to the emulator
     *
     * @param b
     */
    public void write(final byte b) {

        pipeThreadExecutor.submit(new Runnable() {

            @Override
            public void run() {
                try {
                    synchronized (out) {

                        stopAutoSync();

                        out.write(b);
                        out.flush();

                        // delay for 2400baud
                        try {
                            Thread.sleep(4 * factor);
                        } catch (InterruptedException e) {
                            // noop, ignore this
                        }

                        startAutoSync();
                    }

                } catch (IOException e) {
                    logger.trace("error!", e);
                }
            }
        });
    }

    /**
     * Blocking write
     *
     * @param byteArray
     */
    public void write(final byte[] byteArray) {

        pipeThreadExecutor.submit(new Runnable() {

            @Override
            public void run() {

                for (byte b : byteArray) {
                    write(b);
                }

            }
        });
    }

    /**
     * Closes the emulator
     */
    public void close() {

        if (autoSyncFuture != null) {
            autoSyncFuture.cancel(false);
        }

        this.playThreadExecutor.shutdownNow();
        this.pipeThreadExecutor.shutdownNow();

        try {
            this.playThreadExecutor.awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.trace("error!", e);
        }

        try {
            this.pipeThreadExecutor.awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.trace("error!", e);
        }

        IOUtils.closeQuietly(in);
        IOUtils.closeQuietly(out);
    }
}
