/**
 * Copyright (c) 2016-2020 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.core;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.command.EBusCommandUtils;
import de.csdev.ebus.utils.EBusUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusReceiveStateMachine {

    public enum State {
        UNKNOWN,
        SYN,
        SRC_ADDR,
        TGT_ADDR,
        PRIMARY_CMD,
        SECONDARY_CMD,
        LENGTH1,
        DATA1,
        CRC1,
        ACK1,
        LENGTH2,
        DATA2,
        CRC2,
        ACK2
    }

    private static final Logger logger = LoggerFactory.getLogger(EBusReceiveStateMachine.class);

    private ByteBuffer bb = ByteBuffer.allocate(50);

    private byte crc = 0;

    private boolean isEscapedByte = false;

    private int len = 0;

    private State state = State.UNKNOWN;

    private boolean telegramAvailable = false;

    private void fireTelegramAvailable() {
        logger.trace("fireTelegramAvailable ...");
        telegramAvailable = true;
    }

    /**
     * Returns the current state
     *
     * @return
     */
    public State getState() {
        return state;
    }

    /**
     * Rturns a copy of the current received telegram
     *
     * @return
     */
    public byte[] getTelegramData() {
        return EBusUtils.toByteArray(bb);
    }

    /**
     * Returns true if we are waiting for a slave answer
     *
     * @return
     */
    public boolean isWaitingForSlaveAnswer() {
        // after master crc byte
        return state == State.CRC1 && bb.get(1) != EBusConsts.BROADCAST_ADDRESS;
    }

    /**
     * Returns true if we currently receive a telegram
     *
     * @return
     */
    public boolean isReceivingTelegram() {
        return state != State.UNKNOWN && state != State.SYN;
    }

    /**
     * Is current state a SYNC
     *
     * @return
     */
    public boolean isSync() {
        return state == State.SYN;
    }

    /**
     * Returns true if a complete valid telegram is available
     *
     * @return
     */
    public boolean isTelegramAvailable() {
        return telegramAvailable;
    }

    /**
     * Returns true if the next step is waiting for master ACK
     *
     * @return
     */
    public boolean isWaitingForMasterACK() {
        return state.equals(State.CRC2);
    }

    /**
     * Returns true if the next step is waiting for master SYN
     *
     * @return
     */
    public boolean isWaitingForMasterSYN() {

        if (state.equals(State.CRC1) && bb.get(1) == EBusConsts.BROADCAST_ADDRESS) {
            return true;
        }

        if (state.equals(State.ACK1) && EBusUtils.isMasterAddress(bb.get(1))) {
            return true;
        }

        if (state.equals(State.ACK2)) {
            return true;
        }

        return false;
    }

    /**
     * Reset the state machine
     */
    public void reset() {
        reset(false);
    }

    private void reset(boolean ignoreState) {
        len = 0;
        crc = 0;
        isEscapedByte = false;
        telegramAvailable = false;

        bb.clear();

        if (!ignoreState) {
            setState(State.UNKNOWN);
        }
    }

    private void setState(State newState) {
        logger.trace("Update state from " + state.name() + " to " + newState.name());
        state = newState;
    }

    private void throwExceptionIfSYN(byte data) throws EBusDataException {
        if (data == EBusConsts.SYN) {
            bb.put(data);
            throw new EBusDataException("Received SYN byte while receiving telegram!",
                    EBusDataException.EBusError.INVALID_SYN, bb);
        }
    }

    /**
     * Returns the current telegram as hex string
     *
     * @return
     */
    public String toDumpString() {
        return EBusUtils.toHexDumpString(bb).toString();
    }

    /**
     * Update the state machine
     *
     * @param data The next byte
     * @throws EBusDataException throws an exception on any telegram error
     */
    public void update(byte data) throws EBusDataException {

        try {

            if (!bb.hasRemaining()) {
                logger.warn("Input buffer full, reset!");
                throw new EBusDataException("Input buffer full, reset!", EBusDataException.EBusError.BUFFER_FULL, bb);
            }

            // state machine

            switch (state) {

                case UNKNOWN:
                    // unknown > syn

                    // waiting for next sync byte
                    if (data == EBusConsts.SYN) {
                        setState(State.SYN);
                    }

                    break;

                case SYN:
                    // syn > source address
                    if (EBusUtils.isMasterAddress(data)) {

                        // start telegram, reset old data
                        reset(true);

                        // add data to result and crc
                        bb.put(data);
                        crc = EBusUtils.crc8_tab(data, (byte) 0);

                        setState(State.SRC_ADDR);

                    } else if (data == EBusConsts.SYN) {
                        logger.trace("Auto-SYN byte received");
                        // keep in this state

                    } else {
                        // unknown data
                        throw new EBusDataException(
                                "Telegram starts with an invalid source address! " + EBusUtils.toHexDumpString(data),
                                EBusDataException.EBusError.INVALID_SOURCE_ADDRESS, bb);
                    }

                    break;

                case SRC_ADDR:
                    // source address > target address

                    // no syn symbol allowed
                    throwExceptionIfSYN(data);

                    // add data to result and crc
                    bb.put(data);
                    crc = EBusUtils.crc8_tab(data, crc);

                    setState(State.TGT_ADDR);
                    break;

                case TGT_ADDR:
                    // target address > primary command

                    // no syn symbol allowed
                    throwExceptionIfSYN(data);

                    // add data to result and crc
                    bb.put(data);
                    crc = EBusUtils.crc8_tab(data, crc);

                    setState(State.PRIMARY_CMD);
                    break;

                case PRIMARY_CMD:
                    // primary command > secondary command

                    // no syn symbol allowed
                    throwExceptionIfSYN(data);

                    // add data to result and crc
                    bb.put(data);
                    crc = EBusUtils.crc8_tab(data, crc);

                    setState(State.SECONDARY_CMD);
                    break;

                case SECONDARY_CMD:
                    // secondary command > nn1

                    // no syn symbol allowed
                    throwExceptionIfSYN(data);

                    if (data > 16) {
                        throw new EBusDataException("Master Data Length too large!",
                                EBusDataException.EBusError.INVALID_MASTER_LEN, bb);
                    }

                    len = data;
                    logger.trace("Master data length: " + len);

                    // add data to result and crc
                    bb.put(data);
                    crc = EBusUtils.crc8_tab(data, crc);

                    setState(len == 0 ? State.DATA1 : State.LENGTH1);
                    break;

                case LENGTH1:
                    // nn1 > db1 (end)

                    // no syn symbol allowed
                    throwExceptionIfSYN(data);

                    // add data to crc
                    crc = EBusUtils.crc8_tab(data, crc);

                    if (data == EBusConsts.ESCAPE) {
                        isEscapedByte = true;

                    } else {

                        // encode 0xA9 and 0xAA
                        if (isEscapedByte) {
                            data = EBusCommandUtils.unescapeSymbol(data);
                            isEscapedByte = false;
                        }

                        bb.put(data);
                        len--;
                    }

                    // all data symbols received
                    if (len == 0) {
                        setState(State.DATA1);
                    } else {
                        // keep in this state
                        logger.trace("Data " + len);
                    }

                    break;

                case DATA1:
                    // after data

                    // no syn symbol allowed
                    throwExceptionIfSYN(data);

                    // escaped crc value
                    if (!isEscapedByte && data == EBusConsts.ESCAPE) {
                        isEscapedByte = true;
                        break;
                    }

                    // overwrite data with new value
                    if (isEscapedByte) {
                        data = EBusCommandUtils.unescapeSymbol(data);
                        isEscapedByte = false;
                    }

                    bb.put(data);

                    // now check master crc
                    if (data == crc) {
                        logger.trace("Master CRC correct");
                        setState(State.CRC1);
                        break;
                    }

                    throw new EBusDataException(
                            "Master CRC invalid! IS:" + EBusUtils.toHexDumpString(crc) + " SHOULD:"
                                    + EBusUtils.toHexDumpString(data),
                            EBusDataException.EBusError.MASTER_CRC_INVALID, bb);

                case CRC1:
                    // CRC > SYN / NACK / ACK

                    if (data == EBusConsts.SYN) {
                        if (bb.get(1) == EBusConsts.BROADCAST_ADDRESS) {
                            logger.trace("broadcast end");

                            // add last symbol to result
                            bb.put(data);

                            // set to syn and fire event
                            setState(State.SYN);
                            fireTelegramAvailable();

                            return;
                        }

                        // no response from slave
                        throw new EBusDataException("No response from slave! " + EBusUtils.toHexDumpString(data),
                                EBusDataException.EBusError.NO_SLAVE_RESPONSE, bb);
                    }

                    // add data to result
                    bb.put(data);

                    // slave sends ok symbol
                    if (data == EBusConsts.ACK_OK) {
                        setState(State.ACK1);
                        break;
                    }

                    // slave sends a fail symbol
                    if (data == EBusConsts.ACK_FAIL) {
                        throw new EBusDataException("Slave answered with FAIL ACK!",
                                EBusDataException.EBusError.SLAVE_ACK_FAIL, bb);

                    }

                    // wrong answer
                    throw new EBusDataException("Slave answered with " + EBusUtils.toHexDumpString(data),
                            EBusDataException.EBusError.UNEXPECTED_RESPONSE, bb);

                case ACK1:
                    // ACK1 > NN2
                    // ACK1 > SYN (master-master)

                    // check if this is a master-master telegram
                    if (data == EBusConsts.SYN && EBusUtils.isMasterAddress(bb.get(1))) {
                        logger.trace("master-master end");

                        // add last symbol to result
                        bb.put(data);

                        // set to syn and fire event
                        setState(State.SYN);
                        fireTelegramAvailable();

                        return;
                    }

                    // no syn symbol allowed
                    throwExceptionIfSYN(data);

                    // add data to result and crc
                    bb.put(data);
                    crc = EBusUtils.crc8_tab(data, (byte) 0);

                    if (data > 16) {
                        throw new EBusDataException("Slave Data Length too large!",
                                EBusDataException.EBusError.INVALID_SLAVE_LEN, bb);
                    }

                    len = data;
                    logger.trace("Slave data Length: " + len);

                    // if no payload goto CRC2
                    setState(len == 0 ? State.CRC2 : State.LENGTH2);
                    break;

                case LENGTH2:
                    // NN2 > DB2 (end)

                    // no syn symbol allowed
                    throwExceptionIfSYN(data);

                    // add symbol to crc check
                    crc = EBusUtils.crc8_tab(data, crc);

                    // is symbol an escape symbol, then we have to decode the next symbol
                    if (data == EBusConsts.ESCAPE) {
                        isEscapedByte = true;

                    } else {

                        // is the encode flag enabled? then this symbol is a encoded value and we modify data
                        if (isEscapedByte) {
                            data = EBusCommandUtils.unescapeSymbol(data);
                            isEscapedByte = false;
                        }

                        // put the received or modified symbol
                        bb.put(data);

                        // reduce the length pointer until zero
                        len--;
                    }

                    // all data symbols received
                    if (len == 0) {
                        setState(State.DATA2);
                    } else {
                        // keep in this state
                        logger.trace("Data " + len);
                    }

                    break;

                case DATA2:
                    // after data

                    // no syn symbol allowed
                    throwExceptionIfSYN(data);

                    // escaped crc value
                    if (!isEscapedByte && data == EBusConsts.ESCAPE) {
                        isEscapedByte = true;
                        break;
                    }

                    // overwrite data with new value
                    if (isEscapedByte) {
                        data = EBusCommandUtils.unescapeSymbol(data);
                        isEscapedByte = false;
                    }

                    // add data to result
                    bb.put(data);

                    // now check crc
                    if (data == crc) {
                        setState(State.CRC2);
                        break;
                    }

                    throw new EBusDataException(
                            "Slave CRC invalid! IS:" + EBusUtils.toHexDumpString(crc) + " SHOULD:"
                                    + EBusUtils.toHexDumpString(data),
                            EBusDataException.EBusError.SLAVE_CRC_INVALID, bb);

                case CRC2:
                    // CRC > SYN / NACK / ACK

                    // add data to result
                    bb.put(data);

                    if (data == EBusConsts.ACK_OK) {
                        setState(State.ACK2);
                        break;
                    }

                    if (data == EBusConsts.SYN) {
                        throw new EBusDataException("No slave response!", EBusDataException.EBusError.NO_SLAVE_RESPONSE,
                                bb);

                    } else if (data == EBusConsts.ACK_FAIL) {
                        throw new EBusDataException("Master answered with FAIL ACK!",
                                EBusDataException.EBusError.MASTER_ACK_FAIL, bb);

                    }

                    // wrong answer
                    throw new EBusDataException("Master answered with " + EBusUtils.toHexDumpString(data),
                            EBusDataException.EBusError.UNEXPECTED_RESPONSE, bb);

                case ACK2:
                    // ACK1 > SYN

                    // we not add the syn symbol to the result!

                    setState(State.SYN);

                    fireTelegramAvailable();

                    break;

                default:
                    throw new EBusDataException("Unknown state in eBus state machine!");

            }

        } catch (EBusDataException e) {

            // reset machine
            reset();
            throw e;
        }

    }

}
