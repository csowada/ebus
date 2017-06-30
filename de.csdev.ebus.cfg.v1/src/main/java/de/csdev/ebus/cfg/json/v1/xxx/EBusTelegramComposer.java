/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.cfg.json.v1.xxx;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.cfg.json.v1.mapper.EBusConfigurationTelegram;
import de.csdev.ebus.cfg.json.v1.mapper.EBusConfigurationValue;
import de.csdev.ebus.utils.EBusCodecUtils;
import de.csdev.ebus.utils.EBusUtils;
import de.csdev.ebus.utils.NumberUtils;

/**
 * @author Christian Sowada
 *
 */
public class EBusTelegramComposer {

    private static final Logger logger = LoggerFactory.getLogger(EBusTelegramComposer.class);

    /**
     * Composes a byte array from the given telegram configuration including master crc.
     *
     * @param commandCfg
     * @param dst Set slave or broadcast address, if <code>null</code> then use the <code>dst</code> from commandCfg if
     *            set.
     * @param src The sender master address
     * @param values A value map to set/replace the values from commandCfg
     * @return Returns a ready-to-send byte array with master crc as last byte.
     */
    public static byte[] composeEBusTelegram(EBusConfigurationTelegram commandCfg, Byte dst, Byte src,
            Map<String, Object> values) {

        byte[] buffer = internalComposeEBusTelegram(commandCfg, dst, src, values);

        if (buffer == null) {
            return null;
        }

        // encode the data buffer
        buffer = EBusUtils.encodeEBusData(buffer);

        // compute crc and set it as the last byte
        buffer[buffer.length - 1] = EBusUtils.crc8(buffer, buffer.length - 1);

        return buffer;
    }

    /**
     * Composes a byte array from the given telegram configuration without master crc.
     *
     * @param commandCfg
     * @param dst Set slave or broadcast address, if <code>null</code> then use the <code>dst</code> from commandCfg if
     *            set.
     * @param src The sender master address
     * @param values A value map to set/replace the values from commandCfg
     * @return Returns a unescaped byte array with 0x00 byte for master crc as last byte.
     */
    private static byte[] internalComposeEBusTelegram(EBusConfigurationTelegram commandCfg, Byte dst, Byte src,
            Map<String, Object> values) {

        byte[] buffer = null;

        if (commandCfg != null) {

            if (dst == null && StringUtils.isNotEmpty(commandCfg.getDst())) {
                dst = EBusUtils.toByte(commandCfg.getDst());
            }

            if (dst == null) {
                logger.error("Unable to send command, destination address is missing. Set \"dst\" in item.cfg ...");
                return null;
            }

            byte[] bytesData = EBusUtils.toByteArray(commandCfg.getData());
            byte[] bytesCmd = EBusUtils.toByteArray(commandCfg.getCommand());

//            ByteBuffer bff = ByteBuffer.allocate(32);
//            bff.put(src);
//            bff.put(dst);
//            bff.put(bytesCmd);
//            bff.put((byte) bytesData.length);
            
            buffer = new byte[bytesData.length + 6];
            buffer[0] = src;
            buffer[1] = dst;
            buffer[2] = bytesCmd[0];
            buffer[3] = bytesCmd[1];
//            System.arraycopy(bytesCmd, 0, buffer, 2, bytesCmd.length);
            buffer[4] = (byte) bytesData.length;

            if (values == null || values.isEmpty()) {
                logger.trace("No setter-values for eBUS telegram, used default data ...");
                System.arraycopy(bytesData, 0, buffer, 5, bytesData.length);
                return buffer;
            }

            Map<String, EBusConfigurationValue> valuesConfig = commandCfg.getValues();

            if (valuesConfig == null || valuesConfig.isEmpty()) {
                logger.warn("No values configurated in json cfg ...");
                return null;
            }

            for (Entry<String, Object> entry : values.entrySet()) {

                EBusConfigurationValue valueEntry = valuesConfig.get(entry.getKey());

                if (valueEntry == null) {
                    logger.warn("Unable to set value key \"{}\" in command \"{}.{}\", can't compose telegram ...",
                            entry.getKey(), commandCfg.getClazz(), commandCfg.getId());
                    return null;
                }

                String type = valueEntry.getType();
                int pos = valueEntry.getPos() - 1;

                BigDecimal value = NumberUtils.toBigDecimal(entry.getValue());

                if (valueEntry.getMax() != null && value.compareTo(valueEntry.getMax()) == 1) {
                    throw new RuntimeException("Value larger than allowed!");
                }

                if (valueEntry.getMin() != null && value.compareTo(valueEntry.getMin()) == -1) {
                    throw new RuntimeException("Value smaller than allowed!");
                }

                if (value != null && valueEntry.getFactor() != null) {
                    value = value.divide(valueEntry.getFactor());
                }

                byte[] encode = null;

                if (type.equals(EBusCodecUtils.STRING)) {
                    byte[] bytes = ((String) entry.getValue()).getBytes();
                    encode = new byte[valueEntry.getLength()];
                    System.arraycopy(bytes, 0, encode, 0, encode.length);

                } else {
                    encode = EBusCodecUtils.encode(type, value);
                }

                if (encode.length == 0) {
                    logger.warn("eBUS codec encoder returns empty buffer ...");
                    return null;
                }

                // add computed single value to data buffer
                System.arraycopy(encode, 0, bytesData, pos - 5, encode.length);
            }

            for (Entry<String, EBusConfigurationValue> value : valuesConfig.entrySet()) {

                // check if the special value type for kromschöder/wolf crc is availabel
                if (StringUtils.equals(value.getValue().getType(), "crc-kw")) {

                    byte b = 0;
                    int pos = value.getValue().getPos() - 6;

                    for (int i = 0; i < bytesData.length; i++) {
                        // exclude crc pos
                        if (i != pos) {
                            b = EBusUtils.crc8(bytesData[i], b, (byte) 0x5C);
                        }
                    }

                    // set crc to specified position
                    bytesData[pos] = b;
                }
            }

            // bytesData = EBusUtils.encodeEBusData(bytesData);
            System.arraycopy(bytesData, 0, buffer, 5, bytesData.length);

            return buffer;
        }

        return null;
    }
}
