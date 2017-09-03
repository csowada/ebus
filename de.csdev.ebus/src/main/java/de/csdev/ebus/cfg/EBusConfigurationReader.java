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
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import de.csdev.ebus.cfg.datatypes.EBusTypes;
import de.csdev.ebus.cfg.datatypes.IEBusType;
import de.csdev.ebus.cfg.datatypes.ext.EBusTypeBytes;
import de.csdev.ebus.cfg.dto.EBusCollectionDTO;
import de.csdev.ebus.cfg.dto.EBusCommandDTO;
import de.csdev.ebus.cfg.dto.EBusCommandMethodDTO;
import de.csdev.ebus.cfg.dto.EBusValueDTO;
import de.csdev.ebus.command.EBusCommand;
import de.csdev.ebus.command.EBusCommandCollection;
import de.csdev.ebus.command.EBusCommandMethod;
import de.csdev.ebus.command.EBusCommandNestedValue;
import de.csdev.ebus.command.EBusCommandValue;
import de.csdev.ebus.command.IEBusCommand;
import de.csdev.ebus.command.IEBusCommandMethod;
import de.csdev.ebus.utils.EBusUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusConfigurationReader implements IEBusConfigurationReader {

    // private ObjectMapper mapper;
    private EBusTypes registry;

    // public List<IEBusCommand> loadConfiguration(InputStream inputStream)
    // throws IOException, ConfigurationReaderException {
    // EBusCommandCollection collection = loadConfigurationCollection(inputStream);
    // return collection.getCommands();
    // }

    public EBusCommandCollection loadConfigurationCollection(InputStream inputStream)
            throws IOException, EBusConfigurationReaderException {

        if (registry == null) {
            throw new RuntimeException("Unable to load configuration without EBusType set!");
        }

        List<IEBusCommand> commandList = new ArrayList<IEBusCommand>();

        // if (mapper == null) {
        // mapper = new ObjectMapper();
        // mapper.configure(Feature.ALLOW_COMMENTS, true);
        // }

        Gson gson = new Gson();
        EBusCollectionDTO collection = gson.fromJson(new InputStreamReader(inputStream), EBusCollectionDTO.class);
        // EBusCollectionDTO collection = mapper.readValue(inputStream, EBusCollectionDTO.class);

        for (EBusCommandDTO command : collection.getCommands()) {
            commandList.add(parseTelegramConfiguration(command));
        }

        EBusCommandCollection commandCollection = new EBusCommandCollection(collection.getId(), collection.getLabel(),
                collection.getProperties(), commandList);

        commandCollection.setIdentification(collection.getIdentification());

        return commandCollection;
    }

    public void setEBusTypes(EBusTypes ebusTypes) {
        registry = ebusTypes;
    }

    protected EBusCommand parseTelegramConfiguration(EBusCommandDTO commandElement)
            throws EBusConfigurationReaderException {

        LinkedHashMap<String, EBusCommandValue> templateMap = new LinkedHashMap<String, EBusCommandValue>();

        // collect available channels
        List<String> methods = new ArrayList<String>();
        if (commandElement.getGet() != null) {
            methods.add("get");
        }
        if (commandElement.getSet() != null) {
            methods.add("set");
        }
        if (commandElement.getBroadcast() != null) {
            methods.add("broadcast");
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
        for (String channel : methods) {

            EBusCommandMethodDTO commandMethodElement = null;
            IEBusCommandMethod.Method method = null;

            if (channel.equals("get")) {
                commandMethodElement = commandElement.getGet();
                method = IEBusCommandMethod.Method.GET;

            } else if (channel.equals("set")) {
                commandMethodElement = commandElement.getSet();
                method = IEBusCommandMethod.Method.SET;

            } else if (channel.equals("broadcast")) {
                commandMethodElement = commandElement.getBroadcast();
                method = IEBusCommandMethod.Method.BROADCAST;

            }

            if (commandMethodElement != null) {

                // String t = commandMethodElement.getType();
                // IEBusCommandMethod.Type type = t == null ? null
                // : t.equalsIgnoreCase("ms") ? IEBusCommandMethod.Type.MASTER_SLAVE
                // : t.equalsIgnoreCase("mm") ? IEBusCommandMethod.Type.MASTER_MASTER
                // : t.equalsIgnoreCase("ms") ? IEBusCommandMethod.Type.BROADCAST : null;

                // if (type == null) {
                // throw new ConfigurationReaderException("Property \"type\" is missing for command %s !",
                // commandElement.getId());
                // }

                EBusCommandMethod commandMethod = new EBusCommandMethod(cfg, method);

                commandMethod.setCommand(command);
                commandMethod.setDestinationAddress(destination);
                commandMethod.setSourceAddress(source);

                // entry = map.get("master");
                if (commandMethodElement.getMaster() != null) {
                    for (EBusValueDTO template : commandMethodElement.getMaster()) {
                        for (EBusCommandValue ev : parseValueConfiguration(template, templateMap)) {
                            commandMethod.addMasterValue(ev);
                        }
                    }
                }

                if (commandMethodElement.getSlave() != null) {
                    for (EBusValueDTO template : commandMethodElement.getSlave()) {
                        for (EBusCommandValue ev : parseValueConfiguration(template, templateMap)) {
                            commandMethod.addSlaveValue(ev);
                        }
                    }
                }

            }
        }

        return cfg;
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
            final IEBusType<?> typeByte = registry.getType(EBusTypeBytes.BYTES, properties);

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
        IEBusType<?> type = registry.getType(typeStr, map);

        ev.setType(type);

        ev.setName(template.getName());
        ev.setLabel(template.getLabel());

        ev.setFactor(template.getFactor());
        ev.setMin(template.getMin());
        ev.setMax(template.getMax());

        ev.setMapping(template.getMapping());
        ev.setFormat(template.getFormat());

        result.add(ev);
        return result;
    }

}
