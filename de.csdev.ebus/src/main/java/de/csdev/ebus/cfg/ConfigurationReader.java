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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.csdev.ebus.cfg.datatypes.EBusTypes;
import de.csdev.ebus.cfg.datatypes.IEBusType;
import de.csdev.ebus.cfg.datatypes.ext.EBusTypeBytes;
import de.csdev.ebus.cfg.dto.EBusCollectionDTO;
import de.csdev.ebus.cfg.dto.EBusCommandDTO;
import de.csdev.ebus.cfg.dto.EBusCommandTypeDTO;
import de.csdev.ebus.cfg.dto.EBusValueDTO;
import de.csdev.ebus.command.EBusCommand;
import de.csdev.ebus.command.EBusCommandChannel;
import de.csdev.ebus.command.EBusCommandCollection;
import de.csdev.ebus.command.EBusCommandNestedValue;
import de.csdev.ebus.command.EBusCommandValue;
import de.csdev.ebus.command.IEBusCommand;
import de.csdev.ebus.command.IEBusCommand.Type;
import de.csdev.ebus.utils.EBusUtils;

/**
 * @author Christian Sowada
 *
 */
public class ConfigurationReader implements IConfigurationReader {

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

        List<IEBusCommand> commandList = new ArrayList<IEBusCommand>();

        if (mapper == null) {
            mapper = new ObjectMapper();
            mapper.configure(Feature.ALLOW_COMMENTS, true);
        }

        EBusCollectionDTO collection = mapper.readValue(inputStream, EBusCollectionDTO.class);

        for (EBusCommandDTO command : collection.getCommands()) {
            commandList.addAll(parseTelegramConfiguration(command));
        }

        return new EBusCommandCollection(collection.getId(), collection.getLabel(), collection.getProperties(),
                commandList);
    }

    public void setEBusTypes(EBusTypes ebusTypes) {
        registry = ebusTypes;
    }

    protected List<EBusCommand> parseTelegramConfiguration(EBusCommandDTO commandElement) {

        final ArrayList<EBusCommand> result = new ArrayList<EBusCommand>();
        LinkedHashMap<String, EBusCommandValue> templateMap = new LinkedHashMap<String, EBusCommandValue>();

        // collect available channels
        List<String> channels = new ArrayList<String>();
        if (commandElement.getGet() != null) {
            channels.add("get");
        }
        if (commandElement.getSet() != null) {
            channels.add("set");
        }
        if (commandElement.getBroadcast() != null) {
            channels.add("broadcast");
        }

        // extract default values
        String id = commandElement.getId();
        byte[] command = EBusUtils.toByteArray(commandElement.getCommand());
        String comment = commandElement.getComment();
        String device = commandElement.getDevice();
        Byte destination = EBusUtils.toByte(commandElement.getDst());
        Byte source = EBusUtils.toByte(commandElement.getSrc());

        // read in template block
        if (commandElement.getTemplate() != null) {
            for (EBusValueDTO template : commandElement.getTemplate()) {
                for (EBusCommandValue templateCfg : parseValueConfiguration(template, null)) {
                    templateMap.put(templateCfg.getName(), templateCfg);
                }
            }
        }

        EBusCommand cfg = new EBusCommand();
        cfg.setId(id);
        cfg.setDescription(comment);
        cfg.setDevice(device);

        // loop all available channnels
        for (String channel : channels) {

            EBusCommandTypeDTO commandChannel = null;

            if (channel.equals("get")) {
                commandChannel = commandElement.getGet();
            } else if (channel.equals("set")) {
                commandChannel = commandElement.getSet();
            } else if (channel.equals("broadcast")) {
                commandChannel = commandElement.getBroadcast();
            }

            if (commandChannel != null) {
                // Map<String, Object> map = (Map<String, Object>) entry;

                EBusCommandChannel c = new EBusCommandChannel(cfg);

                c.setCommand(command);
                c.setDestinationAddress(destination);
                c.setSourceAddress(source);

                // entry = map.get("master");
                if (commandChannel.getMaster() != null) {
                    for (EBusValueDTO template : commandChannel.getMaster()) {
                        for (EBusCommandValue ev : parseValueConfiguration(template, templateMap)) {
                            c.addMasterValue(ev);
                        }
                    }
                }

                if (commandChannel.getSlave() != null) {
                    for (EBusValueDTO template : commandChannel.getSlave()) {
                        for (EBusCommandValue ev : parseValueConfiguration(template, templateMap)) {
                            c.addSlaveValue(ev);
                        }
                    }
                }

                if (channel.equals("get")) {
                    c.setType(Type.GET);

                } else if (channel.equals("set")) {
                    c.setType(Type.SET);

                } else if (channel.equals("broadcast")) {
                    c.setType(Type.BROADCAST);

                }

            }
            // add command to result list
            result.add(cfg);
        }

        return result;
    }

    protected Collection<EBusCommandValue> parseValueConfiguration(EBusValueDTO template,
            Map<String, EBusCommandValue> templateMap) {

        Collection<EBusCommandValue> result = new ArrayList<EBusCommandValue>();
        String typeStr = template.getType();

        if (typeStr.equals("template-block")) {
            // return the complete template block
            return templateMap.values();

        } else if (typeStr.equals("template")) {

            // use command value from template map
            result.add(templateMap.get(template.getName()));
            return result;

        } else if (typeStr.equals("static")) {
            // convert static content to bytes

            byte[] byteArray = EBusUtils.toByteArray(template.getDefault());
            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put("length", byteArray.length);
            final IEBusType typeByte = registry.getType(EBusTypeBytes.BYTES, properties);

            result.add(EBusCommandValue.getInstance(typeByte, byteArray));
            return result;
        }

        EBusCommandValue ev = null;

        // value is a nested value
        if (template.getChildren() != null) {
            EBusCommandNestedValue evc = new EBusCommandNestedValue();
            ev = evc;

            int pos = 0;
            for (EBusValueDTO childElem : template.getChildren()) {

                // add pos information from list
                childElem.setPos(pos);

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

        Map<String, Object> map = template.getAsMap();
        IEBusType type = registry.getType(typeStr, map);

        ev.setType(type);

        ev.setName(template.getName());
        ev.setLabel(template.getLabel());

        ev.setFactor(template.getFactor());
        ev.setMin(template.getMin());
        ev.setMax(template.getMax());

        // TODO missing !!!
        template.getMapping();

        result.add(ev);
        return result;
    }

}
