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
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Emulates a virtual connection like a serial connection an replays data from a text file.
 * 
 * @author csowada
 * 
 */
public class Emulator {

    private static final Logger logger = LoggerFactory.getLogger(Emulator.class);

    private PipedInputStream in;
    private PipedOutputStream out;

    private ExecutorService pipeThreadExecutor;
    private ExecutorService playThreadExecutor;

    public Emulator() {

        pipeThreadExecutor = Executors.newSingleThreadExecutor();
        playThreadExecutor = Executors.newSingleThreadExecutor();

        try {
            in = new PipedInputStream();
            out = new PipedOutputStream(in);
        } catch (IOException e) {
            logger.error("error!", e);
        }
    }

    public InputStream getInputStream() {
        return in;
    }

    /**
     * Blocking write
     * 
     * @param byteArray
     */
    public void write(final byte[] byteArray) {

        Future<?> submit = pipeThreadExecutor.submit(new Runnable() {

            @Override
            public void run() {
                try {
                	synchronized (out) {
                		if(logger.isTraceEnabled()) {
                			logger.trace("Emulator WRITE: {}", EBusUtils.toHexDumpString(byteArray).toString());
                		}

                        out.write(byteArray);
                        out.flush();
                        
                        // delay for 2400baud
                        Thread.sleep(4);
					}

                } catch (IOException e) {
                    logger.error("error!", e);
                } catch (InterruptedException e) {
                	logger.error("error!", e);
				}
            }
        });
        
        
        try {
        	// block here !!!
			submit.get(30, TimeUnit.SECONDS);
			
		} catch (InterruptedException e) {
			logger.error("error!", e);
		} catch (ExecutionException e) {
			logger.error("error!", e);
		} catch (TimeoutException e) {
			logger.error("error!", e);
		}
    }

    public void play(final File inputFile) {
        this.play(inputFile, 1f);
    }

    public void play(final File inputFile, final double replaySpeed) {
        playThreadExecutor.execute(new Runnable() {

            @Override
            public void run() {
                //long lastTime = 0;
                LineNumberReader reader = null;
                String line = "";

                try {
                    reader = new LineNumberReader(new FileReader(inputFile));

                    while (line != null) {

                        line = reader.readLine();

                        if (line != null) {

                            int timeSepPos = line.indexOf(" - ");
                            long time = Long.parseLong(line.substring(0, timeSepPos));
                            byte[] byteArray = EBusUtils.toByteArray(line.substring(timeSepPos + 2));

                            //long sleepTime = (long) (replaySpeed * (time - lastTime));
                            long sleepTime = (long) (replaySpeed * time);
                            //lastTime = time;

                            if (sleepTime > 0) {
                                logger.debug("Sleep for " + sleepTime + " ms ...");
                                Thread.sleep(sleepTime);
                            }
                            
                            Emulator.this.write(byteArray);
                        }
                    }

                } catch (InterruptedException e) {
                    logger.error("error!", e);

                } catch (FileNotFoundException e) {
                    logger.error("error!", e);

                } catch (IOException e) {
                    logger.error("error!", e);

                } finally {

                    try {

                        if (reader != null) {
                            reader.close();
                        }

                    } catch (IOException e) {
                        logger.error("error!", e);
                    }
                }
            }
        });
    }

    public void close() {
        this.playThreadExecutor.shutdownNow();
        this.pipeThreadExecutor.shutdownNow();

        try {
            this.playThreadExecutor.awaitTermination(3, TimeUnit.SECONDS);
            this.pipeThreadExecutor.awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error("error!", e);
        }

    }
}
