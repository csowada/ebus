package de.csdev.ebus.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Emulator {

    private static final Logger logger = LoggerFactory.getLogger(Emulator.class);

    class ReaderThread extends Thread {

        File inputFile;
        double replaySpeed = 1f;
        OutputStream os;
        InputStream is;

        @Override
        public void run() {

            long lastTime = 0;
            LineNumberReader reader = null;
            String line = "";

            try {

                reader = new LineNumberReader(new FileReader(inputFile));

                while (!isInterrupted() && line != null) {

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

                        os.write(byteArray);
                        os.flush();
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

                    if (os != null) {
                        os.flush();
                        os.close();
                    }

                    if (is != null) {
                        // is.flush();
                        is.close();
                    }

                } catch (IOException e) {
                    logger.error("error!", e);
                }
            }
        }
    }

    public static InputStream getEmulatorInputStream(File inputFile) throws IOException {
        return getEmulatorInputStream(inputFile, 1f);
    }

    @SuppressWarnings("resource")
    public static InputStream getEmulatorInputStream(File inputFile, double replaySpeed) throws IOException {

        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out = new PipedOutputStream(in);

        ReaderThread thread = new Emulator().new ReaderThread();
        thread.inputFile = inputFile;
        thread.os = out;
        thread.is = in;

        thread.setDaemon(true);
        thread.setName("Emulator Thread");
        thread.start();

        return in;
    }

}