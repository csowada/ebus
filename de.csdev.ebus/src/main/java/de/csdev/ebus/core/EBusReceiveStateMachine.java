/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
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

import de.csdev.ebus.utils.EBusUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusReceiveStateMachine {

    public enum State {
        ACK1,
        ACK2,
        CRC1,
        CRC2,
        DATA1,
        DATA2,
        LENGTH1,
        LENGTH2,
        PRIMARY_CMD,
        SECONDARY_CMD,
        SRC_ADDR,
        SYN,
        TGT_ADDR,
        UNKNOWN
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

    public State getState() {
        return state;
    }

    public byte[] getTelegramData() {

        byte[] receivedRawData = new byte[bb.position()];
        bb.position(0);
        bb.get(receivedRawData);

        return receivedRawData;
    }

    public boolean isWaitingForSlaveAnswer() {
        // after master crc byte
        return state == State.CRC1 && bb.get(1) != EBusConsts.BROADCAST_ADDRESS;
    }

    public boolean isReceivingTelegram() {
        return state.compareTo(State.SYN) > 1;
    }

    public boolean isSync() {
        return state == State.SYN;
    }

    public boolean isTelegramAvailable() {
        return telegramAvailable;
    }

    public boolean isWaitingForMasterACK() {
        return state.equals(State.CRC2);
    }

    public boolean isWaitingForMasterSYN() {

        if (state.equals(State.CRC1) && bb.get(1) == EBusConsts.BROADCAST_ADDRESS) {
            return true;
        }

        if (state.equals(State.ACK2)) {
            return true;
        }

        return false;
    }

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

    public String toDumpString() {
        return EBusUtils.toHexDumpString(bb).toString();
    }

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
                    logger.trace("Data Length: " + len);

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
                            data = data == (byte) 0x00 ? EBusConsts.ESCAPE
                                    : data == (byte) 0x01 ? EBusConsts.SYN : data;
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
                    bb.put(data);

                    // escaped crc value
                    if (!isEscapedByte && crc == EBusConsts.ESCAPE) {
                        isEscapedByte = true;
                        break;
                    }

                    // overwrite data with new value
                    if (isEscapedByte) {
                        data = data == (byte) 0x00 ? EBusConsts.ESCAPE : data == (byte) 0x01 ? EBusConsts.SYN : data;
                        isEscapedByte = false;
                    }

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
                    logger.trace("xData Length: " + len);

                    // if no payload goto xxx
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
                            data = data == (byte) 0x00 ? EBusConsts.ESCAPE
                                    : data == (byte) 0x01 ? EBusConsts.SYN : data;
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
