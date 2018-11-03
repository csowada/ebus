/**
 * Copyright (c) 2016-2018 by the respective copyright holders.
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
import de.csdev.ebus.cfg.std.dto.EBusCommandTemplatesDTO;
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

    private Map<String, Collection<EBusCommandValue>> templateValueRegistry = new HashMap<String, Collection<EBusCommandValue>>();
    private Map<String, Collection<EBusCommandValue>> templateBlockRegistry = new HashMap<String, Collection<EBusCommandValue>>();

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

        EBusCommandCollection commandCollection = (EBusCommandCollection) loadConfigurationCollection(collection);

        // add md5 hash
        commandCollection.setSourceHash(md.digest());

        return commandCollection;
    }

    public IEBusCommandCollection loadConfigurationCollection(EBusCollectionDTO collection)
            throws EBusConfigurationReaderException {
        // EBusCollectionDTO collection = gson.fromJson(new InputStreamReader(dis), EBusCollectionDTO.class);

        EBusCommandCollection commandCollection = new EBusCommandCollection(collection.getId(), collection.getLabel(),
                collection.getDescription(), collection.getProperties());

        // add md5 hash
        // commandCollection.setSourceHash(md.digest());
        commandCollection.setIdentification(collection.getIdentification());

        // parse the template block
        parseTemplateConfiguration(collection);

        if (collection.getCommands() != null) {
            for (EBusCommandDTO commandDto : collection.getCommands()) {
                if (commandDto != null) {
                    commandCollection.addCommand(parseTelegramConfiguration(commandCollection, commandDto));
                }
            }
        }

        return commandCollection;
    }

    protected void parseTemplateConfiguration(EBusCollectionDTO collection) throws EBusConfigurationReaderException {

        // extract templates
        List<EBusCommandTemplatesDTO> templateSection = collection.getTemplates();
        if (templateSection != null) {
            for (EBusCommandTemplatesDTO templates : templateSection) {
                List<EBusValueDTO> templateValues = templates.getTemplate();
                if (templateValues != null) {

                    Collection<EBusCommandValue> blockList = new ArrayList<EBusCommandValue>();

                    for (EBusValueDTO value : templateValues) {

                        Collection<EBusCommandValue> pv = parseValueConfiguration(value, null, null);
                        blockList.addAll(pv);

                        // global id
                        String id = collection.getId() + "." + templates.getName() + "." + value.getName();
                        logger.trace("Add template with global id {} to registry ...", id);
                        templateValueRegistry.put(id, pv);
                    }

                    String id = collection.getId() + "." + templates.getName();

                    // global id
                    logger.trace("Add template block with global id {} to registry ...", id);
                    templateBlockRegistry.put(id, blockList);
                }
            }
        }
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
        cfg.setParentCollection(commandCollection);

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

        return cfg;
    }

    /**
     * @param template
     * @param templateMap
     * @param commandMethod
     * @return
     * @throws EBusConfigurationReaderException
     */
    protected Collection<EBusCommandValue> parseValueConfiguration(EBusValueDTO template,
            Map<String, EBusCommandValue> templateMap, EBusCommandMethod commandMethod)
            throws EBusConfigurationReaderException {

        Collection<EBusCommandValue> result = new ArrayList<EBusCommandValue>();
        String typeStr = template.getType();
        String collectionId = null;

        // check if really set
        if (commandMethod != null && commandMethod.getParent() != null
                && commandMethod.getParent().getParentCollection() != null) {
            collectionId = commandMethod.getParent().getParentCollection().getId();
        }

        if (typeStr.equals("template-block")) {

            Collection<EBusCommandValue> templateCollection = null;

            if (StringUtils.isNotEmpty(template.getName())) {
                logger.warn("Property 'name' is not allowed for type 'template-block', ignore property !");
            }

            // use the global or local id as template block, new with alpha 15
            String id = (String) template.getProperty("id");
            String globalId = collectionId + "." + id;

            if (StringUtils.isNotEmpty(id)) {

                templateCollection = templateBlockRegistry.get(id);

                if (templateCollection == null) {

                    // try to convert the local id to a global id
                    logger.trace("Unable to find a template with id {}, second try with {} ...", id, globalId);

                    templateCollection = templateBlockRegistry.get(globalId);

                    if (templateCollection == null) {
                        throw new EBusConfigurationReaderException("Unable to find a template-block with id {0}!", id);
                    }
                }

            } else if (templateMap != null) {
                // return the complete template block from within command block
                templateCollection = templateMap.values();

            } else {
                throw new EBusConfigurationReaderException(
                        "No additional information for type 'template-block' defined!");
            }

            if (templateCollection != null) {
                for (EBusCommandValue commandValue : templateCollection) {

                    // clone the original value
                    EBusCommandValue clone = commandValue.clone();
                    clone.setParent(commandMethod);

                    overwritePropertiesFromTemplate(clone, template);

                    result.add(clone);
                }
            }

            return result;

        } else if (typeStr.equals("template")) {

            String id = (String) template.getProperty("id");
            String globalId = collectionId + "." + id;
            Collection<EBusCommandValue> templateCollection = null;

            if (StringUtils.isEmpty(id)) {
                throw new EBusConfigurationReaderException("No additional information for type 'template' defined!");
            }

            if (templateValueRegistry.containsKey(id)) {
                templateCollection = templateValueRegistry.get(id);

            } else if (templateValueRegistry.containsKey(globalId)) {
                templateCollection = templateValueRegistry.get(globalId);

            } else if (templateMap != null && templateMap.containsKey(id)) {
                // return the complete template block from within command block
                templateCollection = new ArrayList<EBusCommandValue>();
                templateCollection.add(templateMap.get(id));

            } else {
                throw new EBusConfigurationReaderException("Unable to find a template for id {0}!", id);

            }

            if (templateCollection != null && !templateCollection.isEmpty()) {
                for (EBusCommandValue commandValue : templateCollection) {

                    EBusCommandValue clone = commandValue.clone();
                    clone.setParent(commandMethod);

                    overwritePropertiesFromTemplate(clone, template);

                    // allow owerwrite for single names
                    clone.setName(StringUtils.defaultIfEmpty(template.getName(), clone.getName()));

                    result.add(clone);
                }
            } else {
                throw new EBusConfigurationReaderException("Internal template collection is empty!");
            }

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

    private void overwritePropertiesFromTemplate(EBusCommandValue clone, EBusValueDTO template) {

        // allow placeholders in template-block mode
        if (StringUtils.isNotEmpty(template.getLabel())) {
            if (StringUtils.isNotEmpty(clone.getLabel()) && clone.getLabel().contains("%s")) {
                clone.setLabel(String.format(clone.getLabel(), template.getLabel()));
            } else {
                clone.setLabel(template.getLabel());
            }
        }

        // clone.setName(StringUtils.defaultIfEmpty(template.g, clone.getName()));

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
                        logger.error(e.getMessage() + " (Url: " + fileUrl + ")");
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

    @Override
    public void clear() {
        templateBlockRegistry.clear();
        templateValueRegistry.clear();
    }

    public Map<String, Collection<EBusCommandValue>> getTemplateValueRegistry() {
        return templateValueRegistry;
    }

    public Map<String, Collection<EBusCommandValue>> getTemplateBlockRegistry() {
        return templateBlockRegistry;
    }
}
