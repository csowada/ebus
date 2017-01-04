/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.core;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.utils.EBusUtils;

/**
 * @author Christian Sowada
 *
 */
public class EBusQueue {

    private static final Logger logger = LoggerFactory.getLogger(EBusQueue.class);

    /** the send output queue */
    private final Queue<QueueEntry> outputQueue = new LinkedBlockingQueue<QueueEntry>(20);

    /** eBUS lockout */
    private static int LOCKOUT_COUNTER_MAX = 3;

    /** data to send */
    private QueueEntry sendEntry;

    /** last send try caused a collision */
    private boolean lastSendCollisionDetected = false;

    /** current lockout counter */
    private int lockCounter = 0;

    /** next send try is blocked */
    private boolean blockNextSend;

    /** internal structure to store send attempts */
    public class QueueEntry {
        public byte[] buffer;
        public int id;

        public int sendAttempts = 0;

        public QueueEntry(byte[] buffer) {
            this.buffer = buffer;
        }
    }

    public void checkSendStatus() {

        if (lockCounter > 0) {
            lockCounter--;
        }

        // blocked for this send slot because a collision
        if (blockNextSend) {
            logger.trace("Sender was blocked for this SYN ...");
            blockNextSend = false;
            return;
        }

        // counter not zero, it's not allowed to send yet
        if (lockCounter > 0) {
            logger.trace("No access to ebus because the lock counter ...");
            return;
        }

        // currently no data to send
        if (outputQueue.isEmpty()) {
            return;
        }

        if (sendEntry != null) {
            if (sendEntry.sendAttempts == 10) {
                logger.error("Skip telegram {} after 10 attempts ...", EBusUtils.toHexDumpString(sendEntry.buffer));
                resetSendQueue();
            }

            return;
        }

        // get next entry from stack
        sendEntry = outputQueue.peek();
    }

    public Integer addToSendQueue(byte[] buffer) {

        if (buffer == null) {
            logger.trace("Send data is empty, skip");
            return null;
        }

        QueueEntry entry = new QueueEntry(buffer);

        try {
            outputQueue.add(entry);

        } catch (IllegalStateException e) {
            logger.error("Send queue is full! The eBUS service will reset the queue to ensure proper operation.");

            outputQueue.clear();
            resetSendQueue();

            outputQueue.add(entry);
        }

        //checkSendStatus();
        
        return entry.id;
    }

    public void resetSendQueue() {
        // reset ebus counter
        lockCounter = LOCKOUT_COUNTER_MAX;

        // reset global variables
        lastSendCollisionDetected = false;
        blockNextSend = false;

        // remove entry from sending queue
        sendEntry = null;
        outputQueue.poll();
    }

    public QueueEntry getCurrent() {
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
