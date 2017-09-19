/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.cfg.std;

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

import de.csdev.ebus.cfg.EBusConfigurationReaderException;
import de.csdev.ebus.cfg.IEBusConfigurationReader;
import de.csdev.ebus.cfg.std.dto.EBusCollectionDTO;
import de.csdev.ebus.cfg.std.dto.EBusCommandDTO;
import de.csdev.ebus.cfg.std.dto.EBusCommandMethodDTO;
import de.csdev.ebus.cfg.std.dto.EBusValueDTO;
import de.csdev.ebus.command.EBusCommand;
import de.csdev.ebus.command.EBusCommandCollection;
import de.csdev.ebus.command.EBusCommandMethod;
import de.csdev.ebus.command.EBusCommandNestedValue;
import de.csdev.ebus.command.EBusCommandValue;
import de.csdev.ebus.command.IEBusCommandCollection;
import de.csdev.ebus.command.IEBusCommandMethod;
import de.csdev.ebus.command.datatypes.EBusTypeRegistry;
import de.csdev.ebus.command.datatypes.IEBusType;
import de.csdev.ebus.command.datatypes.ext.EBusTypeBytes;
import de.csdev.ebus.utils.EBusUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusConfigurationReader implements IEBusConfigurationReader {

    private EBusTypeRegistry registry;

    public IEBusCommandCollection loadConfigurationCollection(InputStream inputStream)
            throws IOException, EBusConfigurationReaderException {

        if (registry == null) {
            throw new RuntimeException("Unable to load configuration without EBusType set!");
        }

        if (inputStream == null) {
            throw new IllegalArgumentException("Required argument inputStream is null!");
        }

        // List<IEBusCommand> commandList = new ArrayList<IEBusCommand>();

        Gson gson = new Gson();
        EBusCollectionDTO collection = gson.fromJson(new InputStreamReader(inputStream), EBusCollectionDTO.class);

        EBusCommandCollection commandCollection = new EBusCommandCollection(collection.getId(), collection.getLabel(),
                collection.getDescription(), collection.getProperties());

        for (EBusCommandDTO commandDto : collection.getCommands()) {
            if (commandDto != null) {
                commandCollection.addCommand(parseTelegramConfiguration(commandCollection, commandDto));
                // commandList.add(parseTelegramConfiguration(commandCollection, commandDto));
            }
        }

        commandCollection.setIdentification(collection.getIdentification());

        return commandCollection;
    }

    public void setEBusTypes(EBusTypeRegistry ebusTypes) {
        registry = ebusTypes;
    }

    protected EBusCommand parseTelegramConfiguration(IEBusCommandCollection commandCollection,
            EBusCommandDTO commandElement) throws EBusConfigurationReaderException {

        if (commandElement == null) {
            throw new IllegalArgumentException("Parameter \"command dto\" not set!");
        }

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
        String label = commandElement.getLabel();
        String device = commandElement.getDevice();
        Byte destination = EBusUtils.toByte(commandElement.getDst());
        Byte source = EBusUtils.toByte(commandElement.getSrc());

        // read in template block
        if (commandElement.getTemplate() != null) {
            for (EBusValueDTO template : commandElement.getTemplate()) {
                for (EBusCommandValue templateCfg : parseValueConfiguration(template, null, null)) {
                    templateMap.put(templateCfg.getName(), templateCfg);
                }
            }
        }

        EBusCommand cfg = new EBusCommand();
        cfg.setId(id);
        cfg.setLabel(label);
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

                EBusCommandMethod commandMethod = new EBusCommandMethod(cfg, method);

                commandMethod.setCommand(command);
                commandMethod.setDestinationAddress(destination);
                commandMethod.setSourceAddress(source);

                if (commandMethodElement.getMaster() != null) {
                    for (EBusValueDTO template : commandMethodElement.getMaster()) {
                        for (EBusCommandValue ev : parseValueConfiguration(template, templateMap, commandMethod)) {
                            commandMethod.addMasterValue(ev);
                        }
                    }
                }

                if (commandMethodElement.getSlave() != null) {
                    for (EBusValueDTO template : commandMethodElement.getSlave()) {
                        for (EBusCommandValue ev : parseValueConfiguration(template, templateMap, commandMethod)) {
                            commandMethod.addSlaveValue(ev);
                        }
                    }
                }

            }
        }

        cfg.setParentCollection(commandCollection);

        return cfg;
    }

    protected Collection<EBusCommandValue> parseValueConfiguration(EBusValueDTO template,
            Map<String, EBusCommandValue> templateMap, EBusCommandMethod commandMethod) {

        Collection<EBusCommandValue> result = new ArrayList<EBusCommandValue>();
        String typeStr = template.getType();

        if (typeStr.equals("template-block")) {
            // return the complete template block as clone
            for (EBusCommandValue commandValue : templateMap.values()) {
                EBusCommandValue clone = commandValue.clone();
                clone.setParent(commandMethod);
                result.add(clone);
            }
            return result;

        } else if (typeStr.equals("template")) {

            // use command value from template map as clone
            EBusCommandValue commandValue = templateMap.get(template.getName());
            EBusCommandValue clone = commandValue.clone();
            clone.setParent(commandMethod);
            result.add(clone);
            return result;

        } else if (typeStr.equals("static")) {
            // convert static content to bytes

            byte[] byteArray = EBusUtils.toByteArray(template.getDefault());
            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put("length", byteArray.length);
            final IEBusType<?> typeByte = registry.getType(EBusTypeBytes.BYTES, properties);

            EBusCommandValue commandValue = EBusCommandValue.getInstance(typeByte, byteArray);
            commandValue.setParent(commandMethod);

            result.add(commandValue);
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
                for (EBusCommandValue childValue : parseValueConfiguration(childElem, templateMap, commandMethod)) {
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

        ev.setParent(commandMethod);

        result.add(ev);
        return result;
    }

}
