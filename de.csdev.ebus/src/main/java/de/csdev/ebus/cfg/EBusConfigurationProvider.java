/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.cfg;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The configuration provider reads the vendors specific ebus protocol
 * information from the json configuration files. All placeholders (regex)
 * and javascript snippets will be compiled after loading to improve
 * runtime performance.
 *
 * @author Christian Sowada
 */
public class EBusConfigurationProvider {

    private static final Logger logger = LoggerFactory.getLogger(EBusConfigurationProvider.class);

    // The registry with all loaded configuration entries
    private ArrayList<EBusConfigurationTelegram> telegramRegistry = new ArrayList<EBusConfigurationTelegram>();

    // The script engine if available
    private Compilable compEngine;

    /**
     * Return if the provider is empty.
     * 
     * @return
     */
    public boolean isEmpty() {
        return telegramRegistry.isEmpty();
    }

    /**
     * Constructor
     */
    public EBusConfigurationProvider() {
        final ScriptEngineManager mgr = new ScriptEngineManager();

        // load script engine if available
        if (mgr != null) {
            final ScriptEngine engine = mgr.getEngineByName("JavaScript");

            if (engine == null) {
                logger.warn("Unable to load \"JavaScript\" engine! Skip every eBus value calculated by JavaScript.");

            } else if (engine instanceof Compilable) {
                compEngine = (Compilable) engine;

            }
        }
    }

    protected boolean add(List<EBusConfigurationTelegram> loadedTelegramRegistry) {

        for (Iterator<EBusConfigurationTelegram> iterator = loadedTelegramRegistry.iterator(); iterator.hasNext();) {
            EBusConfigurationTelegram configurationEntry = iterator.next();

            // compile scipt's if available also once
            if (configurationEntry.getValues() != null && !configurationEntry.getValues().isEmpty()) {
                Map<String, EBusConfigurationValue> values = configurationEntry.getValues();
                for (Entry<String, EBusConfigurationValue> entry : values.entrySet()) {
                    if (StringUtils.isNotEmpty(entry.getValue().getScript())) {
                        String script = entry.getValue().getScript();

                        // check if engine is available
                        if (StringUtils.isNotEmpty(script) && compEngine != null) {
                            try {
                                CompiledScript compile = compEngine.compile(script);
                                entry.getValue().setCsript(compile);
                            } catch (ScriptException e) {
                                logger.error("Error while compiling JavaScript!", e);
                            }
                        }
                    }
                }
            }

            // compile scipt's if available
            if (configurationEntry.getComputedValues() != null && !configurationEntry.getComputedValues().isEmpty()) {
                Map<String, EBusConfigurationValue> cvalues = configurationEntry.getComputedValues();
                for (Entry<String, EBusConfigurationValue> entry : cvalues.entrySet()) {
                    if (StringUtils.isNotEmpty(entry.getValue().getScript())) {
                        String script = entry.getValue().getScript();

                        // check if engine is available
                        if (StringUtils.isNotEmpty(script) && compEngine != null) {
                            try {
                                CompiledScript compile = compEngine.compile(script);
                                entry.getValue().setCsript(compile);
                            } catch (ScriptException e) {
                                logger.error("Error while compiling JavaScript!", e);
                            }
                        }
                    }
                }
            }
        }

        return telegramRegistry.addAll(loadedTelegramRegistry);
    }

    /**
     * Clears all loaded configurations
     */
    public void clear() {
        if (telegramRegistry != null) {
            telegramRegistry.clear();
        }
    }

    /**
     * Return all configuration which filter match the bufferString paramter
     * 
     * @param bufferString The byte string to check against all loaded filters
     * @return All configurations with matching filter
     */
    public List<EBusConfigurationTelegram> getCommandsByFilter(String bufferString) {

        final List<EBusConfigurationTelegram> matchedTelegramRegistry = new ArrayList<EBusConfigurationTelegram>();

        /** select matching telegram registry entries */
        for (EBusConfigurationTelegram registryEntry : telegramRegistry) {
            Pattern pattern = registryEntry.getFilterPattern();
            Matcher matcher = pattern.matcher(bufferString);
            if (matcher.matches()) {
                matchedTelegramRegistry.add(registryEntry);
            }
        }

        return matchedTelegramRegistry;
    }

    /**
     * Return all configurations by command id and class
     * 
     * @param commandId The command id
     * @return All matching configurations
     */
    public EBusConfigurationTelegram getCommandById(String commandId) {

        String[] idElements = StringUtils.split(commandId, ".");
        String commandClass = null;
        commandId = null;

        if (idElements.length > 1) {
            commandClass = idElements[0];
            commandId = idElements[1];
        }

        for (EBusConfigurationTelegram entry : telegramRegistry) {
            if (StringUtils.equals(entry.getId(), commandId) && StringUtils.equals(entry.getClazz(), commandClass)) {
                return entry;
            }
        }

        return null;
    }
}
