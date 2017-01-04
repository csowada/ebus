/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.aaa;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.cfg.EBusConfigurationTelegram;
import de.csdev.ebus.cfg.EBusConfigurationValue;
import de.csdev.ebus.utils.EBusCodecUtils;
import de.csdev.ebus.utils.EBusUtils;
import de.csdev.ebus.utils.NumberUtils;

/**
 * @author Christian Sowada
 *
 */
public class EBusTelegramComposer {
	
	private static final Logger logger = LoggerFactory.getLogger(EBusTelegramComposer.class);

	public static byte[] composeEBusTelegram2(EBusConfigurationTelegram commandCfg, Byte dst, Byte src, Map<String, Object> values) {
		byte[] buffer = composeEBusTelegram(commandCfg, dst, src, values);
		
		 buffer[buffer.length-1] = EBusUtils.crc8(buffer, buffer.length-1);
		
		return buffer;
	}
	
    /**
     * @param commandId
     * @param commandClass
     * @param dst
     * @param src
     * @param values
     * @return
     */
    private static byte[] composeEBusTelegram(EBusConfigurationTelegram commandCfg, Byte dst, Byte src, Map<String, Object> values) {

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

            buffer = new byte[bytesData.length + 6];
            buffer[0] = src;
            buffer[1] = dst;
            buffer[4] = (byte) bytesData.length;
            System.arraycopy(bytesCmd, 0, buffer, 2, bytesCmd.length);

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

                byte[] encode = EBusCodecUtils.encode(type, value);

                if (encode.length == 0) {
                    logger.warn("eBUS codec encoder returns empty buffer ...");
                    return null;
                }

                // add computed single value to data buffer
                System.arraycopy(encode, 0, bytesData, pos - 5, encode.length);
            }

            for (Entry<String, EBusConfigurationValue> value : valuesConfig.entrySet()) {

                // check if the special value type for kromsch�der/wolf crc is availabel
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

            bytesData = EBusUtils.encodeEBusData(bytesData);
            System.arraycopy(bytesData, 0, buffer, 5, bytesData.length);

            return buffer;
        }

        return null;
    }
}
