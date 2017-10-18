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
import de.csdev.ebus.utils.EBusUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusCommandUtils {

    private final static Logger logger = LoggerFactory.getLogger(EBusCommandUtils.class);

    public static ByteBuffer buildMasterTelegram(IEBusCommandMethod commandMethod, Byte source, Byte target,
            Map<String, Object> values) throws EBusTypeException {

        byte len = 0;
        ByteBuffer buf = ByteBuffer.allocate(50);

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

        buf.put(source); // QQ - Source
        buf.put(target); // ZZ - Target
        buf.put(commandMethod.getCommand()); // PB SB - Command
        buf.put((byte) 0x00); // NN - Length, will be set later

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

                    } else if (entry.getDefaultValue() == null) {
                        b = type.encode(null);

                    } else {
                        b = type.encode(entry.getDefaultValue());

                    }

                }

                if (b == null) {
                    throw new RuntimeException("Encoded value is null!");
                }
                // buf.p
                buf.put(b);
                len += type.getTypeLength();
            }
        }

        // set len
        buf.put(4, len);

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

        // calculate crc
        byte crc8 = EBusUtils.crc8(buf.array(), buf.position());

        buf.put(crc8);

        return buf;
    }

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

    private static int decodeValueList(List<IEBusValue> values, byte[] data, HashMap<String, Object> result, int pos)
            throws EBusTypeException {

        // HashMap<String, Object> result = new HashMap<String, Object>();

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

                // boolean x = type instanceof EBusTypeBytes;

                if (entry.getName() == null && type instanceof EBusTypeBytes && entry.getDefaultValue() != null) {
                    for (int i = 0; i < type.getTypeLength(); i++) {
                        buf.put((byte) 0xFF);
                    }
                } else {
                    for (int i = 0; i < type.getTypeLength(); i++) {
                        buf.put((byte) 0x00);

                    }
                }

                // buf.put(type.encode(entry.getDefaultValue()));

            }
        }

        buf.put((byte) 0x00); // Master CRC

        return buf;
    }

}
