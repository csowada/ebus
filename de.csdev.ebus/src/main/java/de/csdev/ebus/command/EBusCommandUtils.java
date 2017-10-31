/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.command.datatypes.IEBusComplexType;
import de.csdev.ebus.command.datatypes.IEBusType;
import de.csdev.ebus.command.datatypes.ext.EBusTypeBytes;
import de.csdev.ebus.core.EBusConsts;
import de.csdev.ebus.core.EBusDataException;
import de.csdev.ebus.core.EBusReceiveStateMachine;
import de.csdev.ebus.utils.EBusUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusCommandUtils {

    private final static Logger logger = LoggerFactory.getLogger(EBusCommandUtils.class);

    public static byte[] checkRawTelegram(byte[] data) throws EBusDataException {
        EBusReceiveStateMachine machine = new EBusReceiveStateMachine();

        // init machine
        machine.update(EBusConsts.SYN);

        for (byte b : data) {
            machine.update(b);
        }

        return machine.getTelegramData();
    }

    /**
     * Reverse an escaped SYN or EXSCAPE back to its decoded value
     *
     * @param reversedByte The byte 0x00 or 0x01 to reverse
     * @return
     */
    public static byte unescapeSymbol(byte reversedByte) {
        return reversedByte == (byte) 0x00 ? EBusConsts.ESCAPE
                : reversedByte == (byte) 0x01 ? EBusConsts.SYN : reversedByte;
    }

    /**
     * Reverse an escaped SYN or EXSCAPE back to its decoded value
     *
     * @param b The byte to escape
     * @return A escaped byte if required or the parameter byte as array
     */
    public static byte[] escapeSymbol(byte b) {
        if (b == EBusConsts.ESCAPE) {
            return EBusConsts.ESCAPE_REPLACEMENT;
        } else if (b == EBusConsts.SYN) {
            return EBusConsts.SYN_REPLACEMENT;
        } else {
            return new byte[] { b };
        }
    }

    /**
     * Build a complete telegram for master/slave, master/master and broadcasts
     *
     * @param source
     * @param target
     * @param command
     * @param masterData
     * @param slaveData
     * @return
     * @throws EBusTypeException
     */
    public static ByteBuffer buildCompleteTelegram(byte source, byte target, byte[] command, byte[] masterData,
            byte[] slaveData) throws EBusTypeException {

        boolean isMastereMaster = EBusUtils.isMasterAddress(target);
        boolean isBroadcast = target == EBusConsts.BROADCAST_ADDRESS;
        boolean isMasterSlave = !isMastereMaster && !isBroadcast;

        ByteBuffer buf = ByteBuffer.allocate(50);
        buf.put(buildPartMasterTelegram(source, target, command, masterData));

        // if used compute a complete telegram
        if (isMasterSlave && slaveData != null) {
            ByteBuffer slaveTelegramPart = buildPartSlave(slaveData);
            buf.put(slaveTelegramPart);

            buf.put(EBusConsts.ACK_OK);
            buf.put(EBusConsts.SYN);
        }

        if (isMastereMaster) {
            buf.put(EBusConsts.ACK_OK);
            buf.put(EBusConsts.SYN);
        }

        if (isBroadcast) {
            buf.put(EBusConsts.SYN);
        }

        // set limit and reset position
        buf.limit(buf.position());
        buf.position(0);

        return buf;
    }

    /**
     * Builds an escaped master telegram part or if slaveData is used a complete telegram incl. master ACK and SYN
     *
     * @param source
     * @param target
     * @param command
     * @param masterData
     * @param slaveData
     * @return
     * @throws EBusTypeException
     */
    public static ByteBuffer buildPartMasterTelegram(byte source, byte target, byte[] command, byte[] masterData)
            throws EBusTypeException {

        ByteBuffer buf = ByteBuffer.allocate(50);

        buf.put(source); // QQ - Source
        buf.put(target); // ZZ - Target
        buf.put(command); // PB SB - Command
        buf.put((byte) masterData.length); // NN - Length, will be set later

        // add the escaped bytes
        for (byte b : masterData) {
            buf.put(escapeSymbol(b));
        }

        // calculate crc
        byte crc8 = EBusUtils.crc8(buf.array(), buf.position());

        buf.put(escapeSymbol(crc8));

        // set limit and reset position
        buf.limit(buf.position());
        buf.position(0);

        return buf;
    }

    /**
     * Build an escaped telegram part for a slave answer
     *
     * @param slaveData
     * @return
     * @throws EBusTypeException
     */
    public static ByteBuffer buildPartSlave(byte[] slaveData) throws EBusTypeException {

        ByteBuffer buf = ByteBuffer.allocate(50);

        buf.put(EBusConsts.ACK_OK); // ACK

        // if payload available
        if (slaveData != null && slaveData.length > 0) {
            buf.put((byte) slaveData.length); // NN - Length

            // add the escaped bytes
            for (byte b : slaveData) {
                buf.put(escapeSymbol(b));
            }

            // calculate crc
            byte crc8 = EBusUtils.crc8(buf.array(), buf.position());

            // add the crc, maybe escaped
            buf.put(escapeSymbol(crc8));
        } else {
            // only set len = 0
            buf.put((byte) 0x00); // NN - Length
        }

        // set limit and reset position
        buf.limit(buf.position());
        buf.position(0);

        return buf;
    }

    /**
     * @param commandMethod
     * @param values
     * @return
     * @throws EBusTypeException
     */
    public static ByteBuffer composeMasterData(IEBusCommandMethod commandMethod, Map<String, Object> values)
            throws EBusTypeException {

        ByteBuffer buf = ByteBuffer.allocate(50);

        Map<Integer, IEBusComplexType<?>> complexTypes = new HashMap<Integer, IEBusComplexType<?>>();

        if (commandMethod.getMasterTypes() != null) {
            for (IEBusValue entry : commandMethod.getMasterTypes()) {

                IEBusType<?> type = entry.getType();
                byte[] b = null;

                // use the value from the values map if set
                if (values != null && values.containsKey(entry.getName())) {
                    b = type.encode(values.get(entry.getName()));

                } else {
                    if (type instanceof IEBusComplexType) {

                        // add the complex to the list for post processing
                        complexTypes.put(buf.position(), (IEBusComplexType<?>) type);

                        // add placeholder
                        b = new byte[entry.getType().getTypeLength()];

                    } else {
                        b = type.encode(entry.getDefaultValue());

                    }

                }

                if (b == null) {
                    throw new RuntimeException("Encoded value is null! " + type.toString());
                }
                // buf.p
                buf.put(b);
                // len += type.getTypeLength();
            }
        }

        // replace the placeholders with the complex values
        if (!complexTypes.isEmpty()) {
            int orgPos = buf.position();
            for (Entry<Integer, IEBusComplexType<?>> entry : complexTypes.entrySet()) {
                // jump to position
                buf.position(entry.getKey());
                // put new value
                buf.put(entry.getValue().encodeComplex(buf));

            }
            buf.position(orgPos);

        }

        // reset pos to zero and set the new limit
        buf.limit(buf.position());
        buf.position(0);

        return buf;
    }

    /**
     * @param commandMethod
     * @param source
     * @param target
     * @param values
     * @return
     * @throws EBusTypeException
     */
    public static ByteBuffer buildMasterTelegram(IEBusCommandMethod commandMethod, Byte source, Byte target,
            Map<String, Object> values) throws EBusTypeException {

        if (source == null && commandMethod.getSourceAddress() != null) {
            source = commandMethod.getSourceAddress();
        }

        if (target == null && commandMethod.getDestinationAddress() != null) {
            target = commandMethod.getDestinationAddress();
        }

        if (commandMethod == null) {
            throw new IllegalArgumentException("Parameter command is null!");
        }

        if (source == null) {
            throw new IllegalArgumentException("Parameter source is null!");
        }

        if (target == null) {
            throw new IllegalArgumentException("Parameter target is null!");
        }

        byte[] data = EBusUtils.toByteArray(composeMasterData(commandMethod, values));
        ByteBuffer byteBuffer = buildPartMasterTelegram(source, target, commandMethod.getCommand(), data);

        return byteBuffer;
    }

    /**
     * Apply all post number operations like multiply, range check etc.
     *
     * @param decode
     * @param ev
     * @return
     */
    private static Object applyNumberOperations(Object decode, IEBusValue ev) {

        if (ev instanceof EBusCommandValue) {
            EBusCommandValue nev = (EBusCommandValue) ev;

            if (decode instanceof BigDecimal) {

                BigDecimal multiply = (BigDecimal) decode;

                if (nev.getFactor() != null) {
                    multiply = multiply.multiply(nev.getFactor());
                    decode = multiply;
                }

                if (nev.getMin() != null && multiply.compareTo(nev.getMin()) == -1) {
                    logger.debug("Value {} with {} is smaller then allowed {}", ev.getName(), multiply, nev.getMax());
                    decode = null;
                }

                if (nev.getMax() != null && multiply.compareTo(nev.getMax()) == 1) {
                    logger.debug("Value {} with {} is larger then allowed {}", ev.getName(), multiply, nev.getMax());
                    decode = null;
                }
            }
        }

        return decode;
    }

    /**
     * @param values
     * @param data
     * @param result
     * @param pos
     * @return
     * @throws EBusTypeException
     */
    private static int decodeValueList(List<IEBusValue> values, byte[] data, HashMap<String, Object> result, int pos)
            throws EBusTypeException {

        if (values != null) {
            for (IEBusValue ev : values) {

                byte[] src = null;
                Object decode = null;

                // use the raw buffer up to this position, used for custom crc calculation etc.
                // see kw-crc type
                if (ev.getType() instanceof IEBusComplexType) {
                    decode = ((IEBusComplexType<?>) ev.getType()).decodeComplex(data, pos);

                } else {
                    // default encoding
                    src = new byte[ev.getType().getTypeLength()];
                    System.arraycopy(data, pos - 1, src, 0, src.length);
                    decode = ev.getType().decode(src);
                }

                // not allowed for complex types!
                if (ev instanceof IEBusNestedValue && src != null) {
                    IEBusNestedValue evc = (IEBusNestedValue) ev;
                    if (evc.hasChildren()) {

                        for (IEBusValue child : evc.getChildren()) {

                            Object decode2 = child.getType().decode(src);

                            if (StringUtils.isNotEmpty(child.getName())) {
                                decode2 = applyNumberOperations(decode2, ev);
                                result.put(child.getName(), decode2);
                            }
                        }
                    }
                }

                if (StringUtils.isNotEmpty(ev.getName())) {
                    decode = applyNumberOperations(decode, ev);
                    result.put(ev.getName(), decode);
                }

                pos += ev.getType().getTypeLength();
            }
        }

        return pos;
    }

    /**
     * @param commandChannel
     * @param data
     * @return
     * @throws EBusTypeException
     */
    public static Map<String, Object> decodeTelegram(IEBusCommandMethod commandChannel, byte[] data)
            throws EBusTypeException {

        HashMap<String, Object> result = new HashMap<String, Object>();
        int pos = 6;

        if (commandChannel == null) {
            throw new IllegalArgumentException("Parameter command is null!");
        }

        pos = decodeValueList(commandChannel.getMasterTypes(), data, result, pos);

        pos += 3;

        pos = decodeValueList(commandChannel.getSlaveTypes(), data, result, pos);

        return result;
    }

    /**
     * @param commandChannel
     * @return
     */
    public static ByteBuffer getMasterTelegramMask(IEBusCommandMethod commandChannel) {

        // byte len = 0;
        ByteBuffer buf = ByteBuffer.allocate(50);
        buf.put(commandChannel.getSourceAddress() == null ? (byte) 0x00 : (byte) 0xFF); // QQ - Source
        buf.put(commandChannel.getDestinationAddress() == null ? (byte) 0x00 : (byte) 0xFF); // ZZ - Target
        buf.put(new byte[] { (byte) 0xFF, (byte) 0xFF }); // PB SB - Command
        buf.put((byte) 0xFF); // NN - Length

        if (commandChannel.getMasterTypes() != null) {
            for (IEBusValue entry : commandChannel.getMasterTypes()) {
                IEBusType<?> type = entry.getType();

                if (entry.getName() == null && type instanceof EBusTypeBytes && entry.getDefaultValue() != null) {
                    for (int i = 0; i < type.getTypeLength(); i++) {
                        buf.put((byte) 0xFF);
                    }
                } else {
                    for (int i = 0; i < type.getTypeLength(); i++) {
                        buf.put((byte) 0x00);

                    }
                }
            }
        }

        buf.put((byte) 0x00); // Master CRC

        // set limit and reset position
        buf.limit(buf.position());
        buf.position(0);

        return buf;
    }

}
