/**
 * Copyright (c) 2016-2020 by the respective copyright holders.
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
import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.command.IEBusCommandMethod.Type;
import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.command.datatypes.IEBusComplexType;
import de.csdev.ebus.command.datatypes.IEBusType;
import de.csdev.ebus.command.datatypes.ext.EBusTypeBytes;
import de.csdev.ebus.core.EBusConsts;
import de.csdev.ebus.core.EBusDataException;
import de.csdev.ebus.core.EBusReceiveStateMachine;
import de.csdev.ebus.core.EBusReceiveStateMachine.State;
import de.csdev.ebus.utils.EBusUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusCommandUtils {

    private static final Logger logger = LoggerFactory.getLogger(EBusCommandUtils.class);

    private EBusCommandUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Returns a full unique id to a method
     *
     * @param commandMethod
     * @return
     */
    public static @NonNull String getFullId(@NonNull IEBusCommandMethod commandMethod) {
        Objects.requireNonNull(commandMethod);
        return getFullId(commandMethod.getParent()) + ":" + commandMethod.getMethod();
    }

    /**
     * Returns a full unique id to a command
     *
     * @param command
     * @return
     */
    public static @NonNull String getFullId(@NonNull IEBusCommand command) {
        Objects.requireNonNull(command);
        return command.getParentCollection().getId() + "." + command.getId();
    }

    /**
     *
     * @param data
     * @return
     * @throws EBusDataException
     */
    public static byte @NonNull [] prepareSendTelegram(byte @NonNull [] data) throws EBusDataException {

        EBusReceiveStateMachine machine = new EBusReceiveStateMachine();
        machine.updateBytes(data);

        if (machine.getState() == State.CRC1) {
            return data;

        } else if (machine.getState() == State.DATA1 && machine.getRemainDataLength() == 0) {

            // append crc
            byte[] dataExt = new byte[data.length + 1];
            System.arraycopy(data, 0, dataExt, 0, data.length);
            dataExt[dataExt.length - 1] = machine.getCurrentCrc();

            return dataExt;
        }

        throw new EBusDataException("Invalid telegram!");
    }

    /**
     * @param data
     * @return
     * @throws EBusDataException
     */
    public static byte @NonNull [] checkRawTelegram(byte @NonNull [] data) throws EBusDataException {
        EBusReceiveStateMachine machine = new EBusReceiveStateMachine();

        machine.updateBytes(data);

        return machine.getTelegramData();
    }

    /**
     * Reverse an escaped SYN or EXSCAPE back to its decoded value
     *
     * @param reversedByte The byte 0x00 or 0x01 to reverse
     * @return
     */
    public static byte unescapeSymbol(byte reversedByte) {
        if (reversedByte == (byte) 0x00 ) {
            return EBusConsts.ESCAPE;
        }
        return reversedByte == (byte) 0x01 ? EBusConsts.SYN : reversedByte;
    }

    /**
     * Reverse an escaped SYN or EXSCAPE back to its decoded value
     *
     * @param b The byte to escape
     * @return A escaped byte if required or the parameter byte as array
     */
    public static byte @NonNull [] escapeSymbol(byte b) {
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
    public static @NonNull ByteBuffer buildCompleteTelegram(byte source, byte target, byte[] command, byte[] masterData,
            byte[] slaveData) {

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
     * @return
     * @throws EBusTypeException
     */
    public static @NonNull ByteBuffer buildPartMasterTelegram(byte source, byte target, byte[] command,
            byte[] masterData) {

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
    public static @NonNull ByteBuffer buildPartSlave(byte @NonNull [] slaveData) {

        Objects.requireNonNull(slaveData, "slaveData");

        ByteBuffer buf = ByteBuffer.allocate(50);

        buf.put(EBusConsts.ACK_OK); // ACK

        // if payload available
        if (slaveData.length > 0) {
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
    public static @NonNull ByteBuffer composeMasterData(@NonNull IEBusCommandMethod commandMethod,
            @Nullable Map<String, Object> values) throws EBusTypeException {

        Objects.requireNonNull(commandMethod);

        ByteBuffer buf = ByteBuffer.allocate(50);

        Map<Integer, IEBusComplexType<?>> complexTypes = new HashMap<>();

        List<@NonNull IEBusValue> masterTypes = commandMethod.getMasterTypes();
        if (masterTypes != null) {
            for (IEBusValue entry : masterTypes) {

                IEBusType<?> type = entry.getType();
                byte[] b = null;

                // compute byte value from 8 bits
                if (entry instanceof IEBusNestedValue) {
                    IEBusNestedValue nestedValue = (IEBusNestedValue) entry;
                    List<@NonNull IEBusValue> list = nestedValue.getChildren();

                    int n = 0;

                    for (int i = 0; i < list.size(); i++) {
                        IEBusValue childValue = list.get(i);
                        if (values != null && values.containsKey(childValue.getName())) {
                            Boolean object = (Boolean) values.get(childValue.getName());

                            if (object.booleanValue()) {
                                // set bit
                                n = n | (1 << i);
                            }

                        }
                    }

                    b = new byte[] { (byte) n };

                } else if (values != null && values.containsKey(entry.getName())) {
                    // use the value from the values map if set
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
                    throw new EBusTypeException("Encoded value is null! " + type.toString());
                }

                buf.put(b);
            }
        }

        // replace the placeholders with the complex values
        if (!complexTypes.isEmpty()) {
            int orgPos = buf.position();
            buf.limit(buf.position());
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

    public static @NonNull ByteBuffer buildMasterTelegram(@NonNull IEBusCommandMethod commandMethod,
            @Nullable Byte source, @Nullable Byte target, @Nullable Map<String, Object> values)
            throws EBusTypeException, EBusCommandException {
        return buildMasterTelegram(commandMethod, source, target, values, false);
    }

    /**
     * @param commandMethod
     * @param source
     * @param target
     * @param values
     * @param skipAddressChecks
     * @return
     * @throws EBusTypeException
     * @throws EBusCommandException
     */
    public static @NonNull ByteBuffer buildMasterTelegram(@NonNull IEBusCommandMethod commandMethod,
            @Nullable Byte source, @Nullable Byte target, @Nullable Map<String, Object> values,
            boolean skipAddressChecks) throws EBusTypeException, EBusCommandException {

        Objects.requireNonNull(commandMethod, "commandMethod");

        if (source == null && commandMethod.getSourceAddress() != null) {
            source = commandMethod.getSourceAddress();
        }

        if (target == null && commandMethod.getDestinationAddress() != null) {
            target = commandMethod.getDestinationAddress();
        }

        if (source == null) {
            throw new IllegalArgumentException("Parameter source is null!");
        }

        if (target == null) {
            throw new IllegalArgumentException("Parameter target is null!");
        }

        Byte targetChecked = target;

        if (!skipAddressChecks) {

            if (commandMethod.getType().equals(Type.BROADCAST)) {
                if (target != EBusConsts.BROADCAST_ADDRESS) {
                    targetChecked = EBusConsts.BROADCAST_ADDRESS;
                    if (logger.isWarnEnabled()) {
                        logger.warn("Replace target address {} with valid broadcast address 0xFE !",
                            EBusUtils.toHexDumpString(target));
                    }
                }
            } else if (commandMethod.getType().equals(Type.MASTER_MASTER)) {
                if (!EBusUtils.isMasterAddress(target)) {
                    targetChecked = EBusUtils.getMasterAddress(target);

                    if (targetChecked == null) {
                        throw new IllegalArgumentException(String.format(
                                "Cannot replace the slave address 0x%s with a master address because it is a slave address without a master address.",
                                EBusUtils.toHexDumpString(target)));
                    } else {
                        if (logger.isWarnEnabled()) {
                            logger.warn("Replace slave target address {} with valid master address {}!",
                                EBusUtils.toHexDumpString(target), EBusUtils.toHexDumpString(targetChecked));
                        }
                    }
                }

            } else if (commandMethod.getType().equals(Type.MASTER_SLAVE) && EBusUtils.isMasterAddress(target)) {
                targetChecked = EBusUtils.getSlaveAddress(target);
                if (logger.isWarnEnabled()) {
                    logger.warn("Replace master target address {} with valid slave address {}!",
                            EBusUtils.toHexDumpString(target), EBusUtils.toHexDumpString(targetChecked));
                }
            }
        }

        if (targetChecked == null) {
            throw new EBusCommandException("Unable to calculate the correct trarget address!");
        }

        byte[] data = EBusUtils.toByteArray(composeMasterData(commandMethod, values));
        return buildPartMasterTelegram(source, targetChecked, commandMethod.getCommand(), data);
    }

    /**
     * Apply all post number operations like multiply, range check etc.
     *
     * @param decode
     * @param ev
     * @return
     */
    private static @Nullable Object applyNumberOperations(@Nullable Object decode, @Nullable IEBusValue ev) {

        if (ev instanceof EBusCommandValue) {
            EBusCommandValue nev = (EBusCommandValue) ev;

            if (decode instanceof BigDecimal) {

                BigDecimal multiply = (BigDecimal) decode;

                if (nev.getFactor() != null) {
                    multiply = multiply.multiply(nev.getFactor());
                    decode = multiply;
                }

                if (nev.getMin() != null && multiply.compareTo(nev.getMin()) < 0) {
                    logger.debug("Value {} with {} is smaller then allowed {}",
                            ev.getName(), multiply, nev.getMax());
                    decode = null;
                }

                if (nev.getMax() != null && multiply.compareTo(nev.getMax()) > 0) {
                    logger.debug("Value {} with {} is larger then allowed {}",
                            ev.getName(), multiply, nev.getMax());
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
    private static int decodeValueList(@Nullable List<@NonNull IEBusValue> values, byte @NonNull [] data,
            @NonNull HashMap<String, Object> result, int pos) throws EBusTypeException {

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
    public static @NonNull Map<String, Object> decodeTelegram(@NonNull IEBusCommandMethod commandChannel,
            byte @NonNull [] data) throws EBusTypeException {

        Objects.requireNonNull(commandChannel, "commandChannel");
        Objects.requireNonNull(data, "data");

        HashMap<String, Object> result = new HashMap<>();
        int pos = 6;

        pos = decodeValueList(commandChannel.getMasterTypes(), data, result, pos);

        pos += 3;

        decodeValueList(commandChannel.getSlaveTypes(), data, result, pos);

        return result;
    }

    /**
     * @param commandChannel
     * @return
     */
    public static @NonNull ByteBuffer getMasterTelegramMask(@NonNull IEBusCommandMethod commandChannel) {

        Objects.requireNonNull(commandChannel, "Parameter command is null!");

        ByteBuffer buf = ByteBuffer.allocate(50);
        buf.put(commandChannel.getSourceAddress() == null ? (byte) 0x00 : (byte) 0xFF); // QQ - Source
        buf.put(commandChannel.getDestinationAddress() == null ? (byte) 0x00 : (byte) 0xFF); // ZZ - Target
        buf.put(new byte[] { (byte) 0xFF, (byte) 0xFF }); // PB SB - Command
        buf.put((byte) 0xFF); // NN - Length

        List<@NonNull IEBusValue> masterTypes = commandChannel.getMasterTypes();
        if (masterTypes != null) {
            for (IEBusValue entry : masterTypes) {
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

    /**
     *
     * @param command
     * @return
     */
    public static int getSlaveDataLength(@NonNull IEBusCommandMethod command) {

        Objects.requireNonNull(command, "Parameter command is null!");

        if (command.getType() == Type.MASTER_SLAVE) {
            int len = 0;

            List<@NonNull IEBusValue> slaveTypes = command.getSlaveTypes();
            if (slaveTypes != null) {
                for (IEBusValue value : slaveTypes) {
                    if (value.getType() != null) {
                        len += value.getType().getTypeLength();
                    }
                }
            }
            return len;
        }

        return -1;
    }
}
