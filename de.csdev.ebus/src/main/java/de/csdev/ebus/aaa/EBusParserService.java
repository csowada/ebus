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
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptException;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.cfg.EBusConfigurationProvider;
import de.csdev.ebus.cfg.EBusConfigurationTelegram;
import de.csdev.ebus.cfg.EBusConfigurationValue;
import de.csdev.ebus.core.EBusConnectorEventListener;
import de.csdev.ebus.core.EBusController;
import de.csdev.ebus.core.EBusDataException;
import de.csdev.ebus.utils.EBusCodecUtils;
import de.csdev.ebus.utils.EBusUtils;
import de.csdev.ebus.utils.NumberUtils;

/**
 * @author Christian Sowada
 *
 */
public class EBusParserService implements EBusConnectorEventListener {

    private static final Logger logger = LoggerFactory.getLogger(EBusParserService.class);

    private EBusConfigurationProvider configurationProvider;

    /** the list for listeners */
    private final List<EBusParserListener> listeners = new ArrayList<EBusParserListener>();

    public EBusParserService(EBusConfigurationProvider configurationProvider) {
        this.configurationProvider = configurationProvider;
    }

    /**
     * Converts the byte data via the configuration to the value.
     *
     * @param byteBuffer The received telegram byte data
     * @param telegramValue The configuration to encode the value
     * @return Returns the converted value or <code>null</code> on error
     */
    private Object getValue(ByteBuffer byteBuffer, EBusConfigurationValue telegramValue) {

        String type = telegramValue.getType().toLowerCase();
        int pos = telegramValue.getPos() != null ? telegramValue.getPos() : -1;

        Object value = null;
        byte[] bytes = null;

        // requested pos is greater as whole buffer
        if (pos > byteBuffer.limit()) {
            logger.warn("eBUS buffer pos error! Can happen ...");
            return null;
        }

        // replace similar data types
        if (type.equals("uint")) {
            type = "word";
        }

        if (EBusCodecUtils.getDataTypeLen(type) == 2) {
            // low byte first
            bytes = new byte[] { byteBuffer.get(pos), byteBuffer.get(pos - 1) };

        } else {
            bytes = new byte[] { byteBuffer.get(pos - 1) };
        }

        if (type.equals(EBusCodecUtils.BIT)) {
            value = EBusCodecUtils.decodeBit(bytes[0], telegramValue.getBit());

        } else if (type.equals(EBusCodecUtils.STRING)) {
            bytes = new byte[telegramValue.getLength()];
            System.arraycopy(byteBuffer.array(), pos - 1, bytes, 0, bytes.length);
            value = new String(bytes);

        } else {
            value = NumberUtils.toBigDecimal(EBusCodecUtils.decode(type, bytes, telegramValue.getReplaceValue()));
        }

        // if BigDecimal check for min, max and replace value
        if (value instanceof BigDecimal) {
            BigDecimal b = (BigDecimal) value;

            // multiply before check min and max
            if (b != null && telegramValue.getFactor() != null) {
                logger.trace("Value multiplied ...");
                value = b = b.multiply(telegramValue.getFactor());
            }

            // value is below min value, return null
            if (telegramValue.getMin() != null && b != null && b.compareTo(telegramValue.getMin()) == -1) {
                logger.trace("Minimal value reached, skip value ...");
                value = b = null;

                // value is above max value, return null
            } else if (telegramValue.getMax() != null && b != null && b.compareTo(telegramValue.getMax()) == 1) {
                logger.trace("Maximal value reached, skip value ...");
                value = b = null;
            }
        }

        return value;
    }

    /**
     * Parse the received byte data with the load configurations.
     *
     * @param receivedData The received decoded byte data
     * @param sendQueueId The sendQueue id if available
     * @return Returns a map with all converted values
     */
    private Map<String, Object> parse(byte[] receivedData, Integer sendQueueId) {

        // All parsed values
        final Map<String, Object> completeValueRegistry = new HashMap<String, Object>();

        // All parsed values with short keys, used for script evaluation
        final Map<String, Object> valueRegistryShortKeys = new HashMap<String, Object>();

        // Check if a configuration provider is set
        if (configurationProvider == null) {
            logger.error("Configuration not loaded, can't parse telegram!");
            return completeValueRegistry;
        }

        // Get hex string for debugging
        final String dataStr = EBusUtils.toHexDumpString(receivedData).toString();

        // queries the configuration provider for matching registry entries
        final List<EBusConfigurationTelegram> matchedTelegramRegistry = configurationProvider
                .getAllMatchingConfigurations(dataStr);

        // No registry entries found, so this is a unknown telegram
        if (matchedTelegramRegistry.isEmpty()) {
            logger.debug("Telegram unknown ...");
            return completeValueRegistry;
        }

        // Get as byte buffer
        final ByteBuffer byteBuffer = ByteBuffer.wrap(receivedData);

        // loop thru all matching telegrams from registry
        for (EBusConfigurationTelegram registryEntry : matchedTelegramRegistry) {

            // All parsed values
            final Map<String, Object> valueRegistry = new HashMap<String, Object>();

            logger.debug("Found telegram {}", registryEntry.getComment());

            // get id and class key if used
            String idKey = StringUtils.defaultString(registryEntry.getId());
            String classKey = StringUtils.defaultString(registryEntry.getClazz());

            // get values block of configuration
            Map<String, EBusConfigurationValue> values = registryEntry.getValues();

            if (values != null) {

                // loop over all entries
                for (Entry<String, EBusConfigurationValue> entry : values.entrySet()) {

                    EBusConfigurationValue settings = entry.getValue();

                    // String uniqueKey = classKey + "." + idKey + "." + entry.getKey();

                    String uniqueKey = (classKey != "" ? classKey + "." : "") + (idKey != "" ? idKey + "." : "")
                            + entry.getKey();

                    // Extract the value from byte buffer
                    Object value = getValue(byteBuffer, entry.getValue());

                    if (value == null) {
                        // its okay if the value is null, maybe out of range min/max or replace value found
                        logger.trace("Returned value is null, skip ...");
                        continue;
                    }

                    // If compiled script available for this key, execute it now
                    if (settings.getCsript() != null) {
                        try {

                            // Add global variables thisValue and keyName to JavaScript context
                            HashMap<String, Object> bindings = new HashMap<String, Object>();
                            bindings.put(entry.getKey(), value); // short key
                            bindings.put(uniqueKey, value); // full key
                            bindings.put("thisValue", value); // alias thisValue

                            // Evaluates script
                            value = evaluateScript(entry, bindings);

                        } catch (ScriptException e) {
                            logger.error("Error on evaluating JavaScript!", e);
                            break;
                        }
                    }

                    // Add result to registry
                    valueRegistry.put(uniqueKey, value);

                    // Add result to temp. short key registry, used for scripts
                    valueRegistryShortKeys.put(entry.getKey(), value);

                    // also use class.id as key as shortcut if we have only one value
                    if (values.size() == 1) {
                        if (!StringUtils.isEmpty(classKey) && !StringUtils.isEmpty(idKey)) {
                            uniqueKey = classKey + "." + idKey;
                            valueRegistry.put(uniqueKey, value);
                        }
                    }
                }
            }

            // post execute the computes_values block
            Map<String, EBusConfigurationValue> cvalues = registryEntry.getComputedValues();
            if (cvalues != null && !cvalues.isEmpty()) {
                for (Entry<String, EBusConfigurationValue> entry : cvalues.entrySet()) {

                    String uniqueKey = (classKey != "" ? classKey + "." : "") + (idKey != "" ? idKey + "." : "")
                            + entry.getKey();

                    // Add all values to script scope
                    HashMap<String, Object> bindings = new HashMap<String, Object>();
                    bindings.putAll(valueRegistryShortKeys);
                    bindings.putAll(valueRegistry);

                    Object value;
                    try {
                        // Evaluates script
                        value = evaluateScript(entry, bindings);

                        // Add result to registry
                        valueRegistry.put(uniqueKey, value);

                        // if (debugLevel >= 2) {
                        // String label = StringUtils.defaultString(settings.getLabel());
                        // String format = String.format("%-35s%-10s%s", uniqueKey, value, label);
                        // loggerAnalyses.debug(" >>> " + format);
                        // }

                    } catch (ScriptException e) {
                        logger.error("Error on evaluating JavaScript!", e);
                    }
                }
            }

            fireOnTelegramResolved(registryEntry, valueRegistry, receivedData, sendQueueId);
            completeValueRegistry.putAll(valueRegistry);

        }

        return completeValueRegistry;
    }

    /**
     * Evaluates the compiled script of a entry.
     *
     * @param entry The configuration entry to evaluate
     * @param scopeValues All known values for script scope
     * @return The computed value
     * @throws ScriptException
     */
    private Object evaluateScript(Entry<String, EBusConfigurationValue> entry, Map<String, Object> scopeValues)
            throws ScriptException {

        Object value = null;

        // executes compiled script
        if (entry.getValue().getCsript() != null) {
            CompiledScript cscript = entry.getValue().getCsript();

            // Add global variables thisValue and keyName to JavaScript context
            Bindings bindings = cscript.getEngine().createBindings();
            bindings.putAll(scopeValues);
            value = cscript.eval(bindings);
        }

        // try to convert the returned value to BigDecimal
        value = ObjectUtils.defaultIfNull(NumberUtils.toBigDecimal(value), value);

        // round to two digits, maybe not optimal for any result
        if (value instanceof BigDecimal) {
            ((BigDecimal) value).setScale(2, BigDecimal.ROUND_HALF_UP);
        }

        return value;
    }

    /**
     * Fires an event after a telegram was successful parsed .
     *
     * @param registryEntry The used configuration to parse the byte data
     * @param result The result with all values
     * @param receivedData The raw data
     * @param sendQueueId The sendQueue id if available
     */
    private void fireOnTelegramResolved(EBusConfigurationTelegram registryEntry, Map<String, Object> result,
            byte[] receivedData, Integer sendQueueId) {
        for (EBusParserListener listener : listeners) {
            listener.onTelegramResolved(registryEntry, result, receivedData, sendQueueId);
        }
    }

    /**
     * Add an eBus listener to receive parsed eBUS telegram values
     *
     * @param listener
     */
    public void addEBusParserListener(EBusParserListener listener) {
        listeners.add(listener);
    }

    /**
     * Remove an eBus listener
     *
     * @param listener
     * @return
     */
    public boolean removeEBusParserListener(EBusParserListener listener) {
        return listeners.remove(listener);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.core.EBusConnectorEventListener#onTelegramReceived(byte[], java.lang.Integer)
     */
    @Override
    public void onTelegramReceived(byte[] receivedData, Integer sendQueueId) {
        parse(receivedData, sendQueueId);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.core.EBusConnectorEventListener#onTelegramException(de.csdev.ebus.core.EBusDataException,
     * java.lang.Integer)
     */
    @Override
    public void onTelegramException(EBusDataException exception, Integer sendQueueId) {
        logger.debug("ERROR: " + exception.getMessage());
    }
}
