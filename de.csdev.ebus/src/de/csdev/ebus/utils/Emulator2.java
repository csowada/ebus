package de.csdev.ebus.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Emulator2 {

    private static final Logger logger = LoggerFactory.getLogger(Emulator2.class);

    private PipedInputStream in;
    private PipedOutputStream out;

    private ExecutorService pipeThreadExecutor;
    private ExecutorService playThreadExecutor;

    public Emulator2() {

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

    public void write(final byte[] byteArray) {
        pipeThreadExecutor.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    out.write(byteArray);
                    out.flush();
                } catch (IOException e) {
                    logger.error("error!", e);
                }
            }
        });
    }

    public void play(final File inputFile) {
        this.play(inputFile, 1f);
    }

    public void play(final File inputFile, final double replaySpeed) {
        playThreadExecutor.execute(new Runnable() {

            @Override
            public void run() {
                long lastTime = 0;
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

                            long sleepTime = (long) (replaySpeed * (time - lastTime));
                            lastTime = time;

                            if (sleepTime > 0) {
                                logger.debug("Sleep for " + sleepTime + " ms ...");
                                Thread.sleep(sleepTime);
                            }

                            Emulator2.this.write(byteArray);
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
