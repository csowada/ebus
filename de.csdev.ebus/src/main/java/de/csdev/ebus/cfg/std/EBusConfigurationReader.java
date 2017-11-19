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
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

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

    private final Logger logger = LoggerFactory.getLogger(EBusConfigurationReader.class);

    private EBusTypeRegistry registry;

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.cfg.IEBusConfigurationReader#loadBuildInConfigurations()
     */
    @Override
    public List<IEBusCommandCollection> loadBuildInConfigurationCollections() {
        return loadConfigurationCollectionBundle(
                EBusConfigurationReader.class.getResource("/index-configuration.json"));
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.cfg.IEBusConfigurationReader#loadConfigurationCollection(java.io.InputStream)
     */
    @Override
    public IEBusCommandCollection loadConfigurationCollection(URL url)
            throws IOException, EBusConfigurationReaderException {

        if (registry == null) {
            throw new RuntimeException("Unable to load configuration without EBusType set!");
        }

        if (url == null) {
            throw new IllegalArgumentException("Required argument url is null!");
        }

        Type merchantListType = new TypeToken<List<EBusValueDTO>>() {
        }.getType();

        Gson gson = new Gson();
        gson = new GsonBuilder().registerTypeAdapter(merchantListType, new EBusValueJsonDeserializer()).create();

        MessageDigest md = null;

        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IOException(e);
        }

        // collect md5 hash while reading file
        DigestInputStream dis = new DigestInputStream(url.openStream(), md);

        EBusCollectionDTO collection = gson.fromJson(new InputStreamReader(dis), EBusCollectionDTO.class);

        EBusCommandCollection commandCollection = new EBusCommandCollection(collection.getId(), collection.getLabel(),
                collection.getDescription(), collection.getProperties());

        // add md5 hash
        commandCollection.setSourceHash(md.digest());

        for (EBusCommandDTO commandDto : collection.getCommands()) {
            if (commandDto != null) {
                commandCollection.addCommand(parseTelegramConfiguration(commandCollection, commandDto));
            }
        }

        commandCollection.setIdentification(collection.getIdentification());

        return commandCollection;
    }

    /**
     * @param commandCollection
     * @param commandElement
     * @return
     * @throws EBusConfigurationReaderException
     */
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

                // overwrite with local command
                if (StringUtils.isNotEmpty(commandMethodElement.getCommand())) {
                    commandMethod.setCommand(EBusUtils.toByteArray(commandMethodElement.getCommand()));
                } else {
                    commandMethod.setCommand(command);
                }

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

    /**
     * @param template
     * @param templateMap
     * @param commandMethod
     * @return
     */
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
            final IEBusType<?> typeByte = registry.getType(EBusTypeBytes.TYPE_BYTES, properties);

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

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.cfg.IEBusConfigurationReader#setEBusTypes(de.csdev.ebus.command.datatypes.EBusTypeRegistry)
     */
    @Override
    public void setEBusTypes(EBusTypeRegistry ebusTypes) {
        registry = ebusTypes;
    }

    @Override
    public List<IEBusCommandCollection> loadConfigurationCollectionBundle(URL url) {
        List<IEBusCommandCollection> result = new ArrayList<IEBusCommandCollection>();

        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, ?>>() {
        }.getType();

        try {

            Map<String, ?> mapping = gson.fromJson(new InputStreamReader(url.openStream()), type);

            if (mapping.containsKey("files")) {

                @SuppressWarnings("unchecked")
                List<Map<String, String>> files = (List<Map<String, String>>) mapping.get("files");

                for (Map<String, String> file : files) {
                    URL fileUrl = new URL(url, file.get("url"));

                    try {
                        logger.debug("Load configuration from url {} ...", fileUrl);
                        IEBusCommandCollection collection = loadConfigurationCollection(fileUrl);
                        if (collection != null) {
                            result.add(collection);
                        }

                    } catch (EBusConfigurationReaderException e) {
                        logger.error("error!", e);
                    } catch (IOException e) {
                        logger.error("error!", e);
                    }

                }
            }

        } catch (JsonSyntaxException e) {
            logger.error("error!", e);
        } catch (JsonIOException e) {
            logger.error("error!", e);
        } catch (IOException e) {
            logger.error("error!", e);
        }

        return result;
    }

}
