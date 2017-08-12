/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.cfg;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.csdev.ebus.cfg.datatypes.EBusTypes;
import de.csdev.ebus.cfg.datatypes.IEBusType;
import de.csdev.ebus.cfg.datatypes.ext.EBusTypeBytes;
import de.csdev.ebus.cfg.dto.EBusCollectionDTO;
import de.csdev.ebus.command.EBusCommand;
import de.csdev.ebus.command.EBusCommandCollection;
import de.csdev.ebus.command.EBusCommandNestedValue;
import de.csdev.ebus.command.EBusCommandValue;
import de.csdev.ebus.command.IEBusCommand;
import de.csdev.ebus.command.IEBusCommand.Type;
import de.csdev.ebus.utils.EBusUtils;
import de.csdev.ebus.utils.NumberUtils;

/**
 * @author Christian Sowada
 *
 */
public class ConfigurationReaderOLD implements IConfigurationReader {

    private ObjectMapper mapper;
    private EBusTypes registry;

    public List<IEBusCommand> loadConfiguration(InputStream inputStream) throws IOException {

        // if (registry == null) {
        // throw new RuntimeException("Unable to load configuration without EBusType set!");
        // }
        //
        // List<EBusCommand> list = new ArrayList<EBusCommand>();
        //
        // if (mapper == null) {
        // mapper = new ObjectMapper();
        // mapper.configure(Feature.ALLOW_COMMENTS, true);
        // }
        //
        // final Map<String, Object> json = mapper.readValue(inputStream, new TypeReference<Map<String, Object>>() {
        // });
        //
        // @SuppressWarnings("unchecked")
        // List<Map<String, Object>> commands = (List<Map<String, Object>>) json.get("commands");
        //
        // for (Map<String, Object> element : commands) {
        // list.addAll(parseTelegramConfiguration(element));
        // }
        EBusCommandCollection collection = loadConfigurationCollection(inputStream);
        return collection.getCommands();
    }

    public EBusCommandCollection loadConfigurationCollection(InputStream inputStream) throws IOException {

        if (registry == null) {
            throw new RuntimeException("Unable to load configuration without EBusType set!");
        }

        List<IEBusCommand> list = new ArrayList<IEBusCommand>();

        if (mapper == null) {
            mapper = new ObjectMapper();
            mapper.configure(Feature.ALLOW_COMMENTS, true);
        }

        EBusCollectionDTO readValue = mapper.readValue(inputStream, EBusCollectionDTO.class);
        final Map<String, Object> json = mapper.readValue(inputStream, new TypeReference<Map<String, Object>>() {
        });

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> commands = (List<Map<String, Object>>) json.get("commands");
        for (Map<String, Object> element : commands) {
            list.addAll(parseTelegramConfiguration(element));
        }

        return new EBusCommandCollection(json, list);
    }

    public void setEBusTypes(EBusTypes ebusTypes) {
        registry = ebusTypes;
    }

    @SuppressWarnings({ "unchecked" })
    protected List<EBusCommand> parseTelegramConfiguration(Map<String, Object> commandElement) {

        final ArrayList<EBusCommand> result = new ArrayList<EBusCommand>();
        LinkedHashMap<String, EBusCommandValue> templateMap = new LinkedHashMap<String, EBusCommandValue>();

        // collect available channels
        List<String> channels = new ArrayList<String>();
        if (commandElement.containsKey("get")) {
            channels.add("get");
        }
        if (commandElement.containsKey("set")) {
            channels.add("set");
        }
        if (commandElement.containsKey("broadcast")) {
            channels.add("broadcast");
        }

        // extract default values
        String id = (String) commandElement.get("id");
        byte[] command = EBusUtils.toByteArray((String) commandElement.get("command"));
        String comment = (String) commandElement.get("comment");
        String device = (String) commandElement.get("device");
        Byte destination = EBusUtils.toByte((String) commandElement.get("dst"));
        Byte source = EBusUtils.toByte((String) commandElement.get("src"));

        // read in template block
        Object entry = commandElement.get("template");
        if (entry instanceof List) {
            for (Map<String, Object> template : (List<Map<String, Object>>) entry) {
                for (EBusCommandValue templateCfg : parseValueConfiguration(template, null)) {
                    templateMap.put(templateCfg.getName(), templateCfg);
                }
            }
        }

        // loop all available channnels
        for (String channel : channels) {

            entry = commandElement.get(channel);

            if (entry instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) entry;

                EBusCommand cfg = new EBusCommand();

                cfg.setId(id);
                cfg.setCommand(command);
                cfg.setDescription(comment);
                cfg.setDevice(device);

                cfg.setDestinationAddress(destination);
                cfg.setSourceAddress(source);

                entry = map.get("master");
                if (entry instanceof List) {
                    for (Map<String, Object> template : (List<Map<String, Object>>) entry) {
                        for (EBusCommandValue ev : parseValueConfiguration(template, templateMap)) {
                            cfg.addMasterValue(ev);
                        }
                    }
                }

                entry = map.get("slave");
                if (entry instanceof List) {
                    for (Map<String, Object> template : (List<Map<String, Object>>) entry) {
                        for (EBusCommandValue ev : parseValueConfiguration(template, templateMap)) {
                            cfg.addSlaveValue(ev);
                        }
                    }
                }

                if (channel.equals("get")) {
                    cfg.setType(Type.GET);

                } else if (channel.equals("set")) {
                    cfg.setType(Type.SET);

                } else if (channel.equals("broadcast")) {
                    cfg.setType(Type.BROADCAST);

                }

                result.add(cfg);
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    protected Collection<EBusCommandValue> parseValueConfiguration(Map<String, Object> template,
            Map<String, EBusCommandValue> templateMap) {

        Collection<EBusCommandValue> result = new ArrayList<EBusCommandValue>();
        String typeStr = (String) template.get("type");

        if (typeStr.equals("template-block")) {
            // return the complete template block
            return templateMap.values();

        } else if (typeStr.equals("template")) {

            // use command value from template map
            result.add(templateMap.get(template.get("name")));
            return result;

        } else if (typeStr.equals("static")) {
            // convert static content to bytes

            byte[] byteArray = EBusUtils.toByteArray((String) template.get("default"));
            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put("length", byteArray.length);
            final IEBusType typeByte = registry.getType(EBusTypeBytes.BYTES, properties);

            result.add(EBusCommandValue.getInstance(typeByte, byteArray));
            return result;
        }

        EBusCommandValue ev = null;

        // value is a nested value
        if (template.containsKey("children")) {
            EBusCommandNestedValue evc = new EBusCommandNestedValue();
            ev = evc;

            int pos = 0;
            for (Map<String, Object> childElem : (List<Map<String, Object>>) template.get("children")) {

                // add pos information from list
                childElem.put("pos", pos);

                // parse child value
                for (EBusCommandValue childValue : parseValueConfiguration(childElem, templateMap)) {
                    evc.add(childValue);
                }

                pos++;
            }

        } else {
            // default value
            ev = new EBusCommandValue();
        }

        IEBusType type = registry.getType(typeStr, template);

        ev.setType(type);

        ev.setName((String) template.get("name"));
        ev.setLabel((String) template.get("label"));

        Double factor = (Double) template.get("factor");
        if (factor != null) {
            ev.setFactor(BigDecimal.valueOf(factor));
        }

        if (template.containsKey("min")) {
            ev.setMin(NumberUtils.toBigDecimal(template.get("min")));
        }

        if (template.containsKey("max")) {
            ev.setMax(NumberUtils.toBigDecimal(template.get("max")));
        }

        // TODO missing !!!
        template.get("mapping");

        result.add(ev);
        return result;
    }

}
