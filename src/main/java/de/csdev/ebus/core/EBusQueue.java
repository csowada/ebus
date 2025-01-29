/**
 * Copyright (c) 2017-2025 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.core;

import java.util.Objects;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.core.EBusDataException.EBusError;
import de.csdev.ebus.utils.EBusUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusQueue {

    private static final Logger logger = LoggerFactory.getLogger(EBusQueue.class);

    /** eBUS lockout */
    private static final int LOCKOUT_COUNTER_MAX = 3;

    /** the send output queue */
    private final Queue<QueueEntry> outputQueue = new LinkedBlockingQueue<>(20);

    /** data to send */
    private QueueEntry sendEntry;

    /** last send try caused a collision */
    private boolean lastSendCollisionDetected = false;

    /** current lockout counter */
    private int lockCounter = 0;

    /** next send try is blocked */
    private boolean blockNextSend = false;

    /** complete block sending */
    private boolean blockSend = false;

    public boolean isBlockSend() {
        return blockSend;
    }

    public void setBlockSend(boolean blockSend) {
        this.blockSend = blockSend;
    }

    private Random random = new Random();

    /** internal structure to store send attempts */
    public class QueueEntry {
        public byte[] buffer;
        public int id;

        public int maxAttemps = 10;
        public int sendAttempts = 0;
        public boolean secondTry = false;

        public QueueEntry(byte[] buffer) {
            this.buffer = buffer;
            id = random.nextInt();
        }
    }

    public boolean isEmpty() {
        return outputQueue.isEmpty();
    }

    public void checkSendStatus(boolean skipLockCounter) throws EBusDataException {

        if (!skipLockCounter) {
            if (lockCounter > 0) {
                lockCounter--;
            }

            // counter not zero, it's not allowed to send yet
            if (lockCounter > 0) {
                logger.trace("No access to eBUS because the lock counter ...");
                return;
            }
        }

        // blocked for this send slot because a collision
        if (blockNextSend) {
            logger.trace("Sender was blocked for this SYN ...");
            blockNextSend = false;
            return;
        }

        // currently no data to send
        if (outputQueue.isEmpty()) {
            return;
        }

        if (sendEntry != null) {
            if (sendEntry.sendAttempts >= sendEntry.maxAttemps) {

                // store a temp. variable
                QueueEntry tmpEntry = sendEntry;

                resetSendQueue();

                throw new EBusDataException(
                        String.format("Unable to send telegram %s after %d attempts ...",
                                EBusUtils.toHexDumpString(tmpEntry.buffer), tmpEntry.maxAttemps),
                        EBusError.TOO_MANY_ATTEMPS, tmpEntry.buffer, tmpEntry.id);
            }

            return;
        }

        // get next entry from stack
        sendEntry = outputQueue.peek();
    }

    /**
     * Adds a raw telegram to the sending queue.
     *
     * @param buffer
     * @return The unique send id, id is later available on event
     */
    public Integer addToSendQueue(byte @NonNull [] buffer) {
        return addToSendQueue(buffer, 10);
    }

    /**
     * Adds a raw telegram to the sending queue.
     *
     * @param buffer
     * @param maxAttemps
     * @return The unique send id, id is later available on event
     */
    public Integer addToSendQueue(byte @NonNull [] buffer, int maxAttemps) {

        Objects.requireNonNull(buffer, "buffer");

        QueueEntry entry = new QueueEntry(buffer);
        entry.maxAttemps = maxAttemps;

        try {
            outputQueue.add(entry);
            logger.debug("Size of send queue is {} ...", outputQueue.size());

        } catch (IllegalStateException e) {
            logger.error("Send queue is full! The eBUS service will reset the queue to ensure proper operation.");

            // clear the queue, remove all waiting messages
            outputQueue.clear();

            // reset the current state to a defined state
            resetSendQueue();

            // now add the entry as first entry in the emtpy queue
            outputQueue.add(entry);
        }

        return entry.id;
    }

    public void resetSendQueue() {
        // reset ebus counter
        lockCounter = LOCKOUT_COUNTER_MAX;

        // reset global variables
        lastSendCollisionDetected = false;
        blockNextSend = false;
        blockSend = false;

        // remove entry from sending queue
        sendEntry = null;
        outputQueue.poll();
    }

    public @Nullable QueueEntry getCurrent() {
        return sendEntry;
    }

    public boolean isLastSendCollisionDetected() {
        return lastSendCollisionDetected;
    }

    public void setLastSendCollisionDetected(boolean lastSendCollisionDetected) {
        this.lastSendCollisionDetected = lastSendCollisionDetected;
    }

    public int getLockCounter() {
        return lockCounter;
    }

    public void setLockCounter(int lockCounter) {
        this.lockCounter = lockCounter;
    }

    public boolean isBlockNextSend() {
        return blockNextSend;
    }

    public void setBlockNextSend(boolean blockNextSend) {
        this.blockNextSend = blockNextSend;
    }

}
