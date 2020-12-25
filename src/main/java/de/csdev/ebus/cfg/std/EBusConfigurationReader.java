/**
 * Copyright (c) 2016-2020 by the respective copyright holders.
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
import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
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
import de.csdev.ebus.command.EBusCommandUtils;
import de.csdev.ebus.command.EBusCommandValue;
import de.csdev.ebus.command.IEBusCommandCollection;
import de.csdev.ebus.command.IEBusCommandMethod;
import de.csdev.ebus.command.IEBusCommandMethod.Method;
import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.command.datatypes.EBusTypeRegistry;
import de.csdev.ebus.command.datatypes.IEBusType;
import de.csdev.ebus.command.datatypes.ext.EBusTypeBytes;
import de.csdev.ebus.command.datatypes.std.EBusTypeByte;
import de.csdev.ebus.core.EBusConsts;
import de.csdev.ebus.utils.EBusUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusConfigurationReader implements IEBusConfigurationReader {

    private final Logger logger = LoggerFactory.getLogger(EBusConfigurationReader.class);

    private @NonNull EBusTypeRegistry registry;

    private @NonNull Map<@NonNull String, @Nullable Collection<@NonNull EBusCommandValue>> templateValueRegistry = new HashMap<>();
    private @NonNull Map<@NonNull String, @Nullable Collection<@NonNull EBusCommandValue>> templateBlockRegistry = new HashMap<>();

    public EBusConfigurationReader() {
        try {
            this.registry = new EBusTypeRegistry();
        } catch (EBusTypeException e) {
            throw new IllegalStateException("Unable to create a new eBus type registry!");
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.cfg.IEBusConfigurationReader#loadBuildInConfigurations()
     */
    @Override
    public @NonNull List<@NonNull IEBusCommandCollection> loadBuildInConfigurationCollections() {

        URL url = EBusConfigurationReader.class.getResource("/index-configuration.json");
        Objects.requireNonNull(url);

        return loadConfigurationCollectionBundle(url);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.cfg.IEBusConfigurationReader#loadConfigurationCollection(java.io.InputStream)
     */
    @Override
    public @NonNull IEBusCommandCollection loadConfigurationCollection(@NonNull URL url)
            throws IOException, EBusConfigurationReaderException {

        Objects.requireNonNull(url, "url");

        Type merchantListType = new TypeToken<List<EBusValueDTO>>() {
        }.getType();

        Gson gson = new GsonBuilder().registerTypeAdapter(merchantListType, new EBusValueJsonDeserializer()).create();

        MessageDigest md = null;

        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IOException(e);
        }

        // collect md5 hash while reading file
        DigestInputStream dis = new DigestInputStream(url.openStream(), md);

        EBusCollectionDTO collection = Objects
                .requireNonNull(gson.fromJson(new InputStreamReader(dis), EBusCollectionDTO.class));

        EBusCommandCollection commandCollection = (EBusCommandCollection) loadConfigurationCollection(collection);

        // add md5 hash
        commandCollection.setSourceHash(md.digest());

        return commandCollection;
    }

    public @NonNull IEBusCommandCollection loadConfigurationCollection(@NonNull EBusCollectionDTO collection)
            throws EBusConfigurationReaderException {

        Objects.requireNonNull(collection, "collection");

        EBusCommandCollection commandCollection = new EBusCommandCollection(collection.getId(), collection.getLabel(),
                collection.getDescription(), collection.getProperties());

        // add md5 hash
        commandCollection.setIdentification(collection.getIdentification());

        // parse the template block
        parseTemplateConfiguration(collection);

        List<EBusCommandDTO> commands = collection.getCommands();
        if (commands != null) {
            for (EBusCommandDTO commandDto : commands) {
                if (commandDto != null) {
                    commandCollection.addCommand(parseTelegramConfiguration(commandCollection, commandDto));
                }
            }
        }

        return commandCollection;
    }

    protected void parseTemplateConfiguration(@NonNull EBusCollectionDTO collection)
            throws EBusConfigurationReaderException {

        Objects.requireNonNull(collection, "collection");

        // extract templates
        List<EBusCommandTemplatesDTO> templateSection = collection.getTemplates();
        if (templateSection != null) {
            for (EBusCommandTemplatesDTO templates : templateSection) {
                List<EBusValueDTO> templateValues = templates.getTemplate();
                if (templateValues != null) {

                    Collection<EBusCommandValue> blockList = new ArrayList<>();

                    for (EBusValueDTO value : templateValues) {
                        if (value != null) {
                            Collection<@NonNull EBusCommandValue> pv = parseValueConfiguration(value, null, null, null);

                            if (pv != null && !pv.isEmpty()) {
                                blockList.addAll(pv);

                                // global id
                                String id = collection.getId() + "." + templates.getName() + "." + value.getName();
                                logger.trace("Add template with global id {} to registry ...", id);
                                templateValueRegistry.put(id, pv);
                            }
                        }
                    }

                    if (!blockList.isEmpty()) {
                        String id = collection.getId() + "." + templates.getName();

                        // global id
                        logger.trace("Add template block with global id {} to registry ...", id);
                        templateBlockRegistry.put(id, blockList);
                    }
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
    protected EBusCommand parseTelegramConfiguration(@NonNull IEBusCommandCollection commandCollection,
            @NonNull EBusCommandDTO commandElement) throws EBusConfigurationReaderException {

        Objects.requireNonNull(commandCollection, "commandCollection");

        LinkedHashMap<String, EBusCommandValue> templateMap = new LinkedHashMap<>();
        ArrayList<EBusCommandValue> templateList = new ArrayList<>();

        // collect available channels
        List<String> methods = new ArrayList<>();
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
        List<EBusValueDTO> templates = commandElement.getTemplate();
        if (templates != null) {
            for (EBusValueDTO template : templates) {
                if (template != null) {
                    for (EBusCommandValue templateCfg : parseValueConfiguration(template, null, null, null)) {
                        if (StringUtils.isEmpty(templateCfg.getName())) {
                            templateMap.put(templateCfg.getName(), templateCfg);
                        }

                        templateList.add(templateCfg);
                    }
                }
            }
        }

        if (id == null) {
            throw new EBusConfigurationReaderException("Property 'id' is missing for command ! {0}",
                    commandElement != null ? commandElement.toString() : "<NULL>");
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

                List<EBusValueDTO> master = commandMethodElement.getMaster();
                if (master != null) {
                    for (EBusValueDTO template : master) {
                        if (template != null) {
                            for (EBusCommandValue ev : parseValueConfiguration(template, templateMap, templateList,
                                    commandMethod)) {
                                commandMethod.addMasterValue(ev);
                            }
                        }
                    }
                }

                List<EBusValueDTO> slave = commandMethodElement.getSlave();
                if (slave != null) {
                    for (EBusValueDTO template : slave) {
                        if (template != null) {
                            for (EBusCommandValue ev : parseValueConfiguration(template, templateMap, templateList,
                                    commandMethod)) {
                                commandMethod.addSlaveValue(ev);
                            }
                        }
                    }
                }

                // default type is always master-slave if not explicit set or a broadcast
                if (StringUtils.equals(commandMethodElement.getType(), "master-master")) {
                    commandMethod.setType(IEBusCommandMethod.Type.MASTER_MASTER);

                } else if (method == Method.BROADCAST) {
                    commandMethod.setDestinationAddress(EBusConsts.BROADCAST_ADDRESS);
                    commandMethod.setType(IEBusCommandMethod.Type.BROADCAST);

                } else {
                    commandMethod.setType(IEBusCommandMethod.Type.MASTER_SLAVE);
                }

                if (commandMethod.getType() == IEBusCommandMethod.Type.MASTER_SLAVE) {
                    if (commandMethod.getSlaveTypes() == null) {
                        logger.debug("Warning: Master-Slave command \"{}\" has no slave configuration defined!",
                                EBusCommandUtils.getFullId(commandMethod));
                    }
                }
            }
        }

        return cfg;
    }

    /**
     * @param valueDto
     * @param templateMap
     * @param commandMethod
     * @return
     * @throws EBusConfigurationReaderException
     */
    protected @NonNull Collection<@NonNull EBusCommandValue> parseValueConfiguration(@NonNull EBusValueDTO valueDto,
            @Nullable Map<@NonNull String, @NonNull EBusCommandValue> templateMap,
            @Nullable List<@NonNull EBusCommandValue> templateList, @Nullable EBusCommandMethod commandMethod)
            throws EBusConfigurationReaderException {

        Objects.requireNonNull(valueDto, "valueDto");

        Collection<@NonNull EBusCommandValue> result = new ArrayList<>();
        String typeStr = valueDto.getType();
        String collectionId = null;

        // check if really set
        if (commandMethod != null && commandMethod.getParent() != null
                && commandMethod.getParent().getParentCollection() != null) {
            collectionId = commandMethod.getParent().getParentCollection().getId();
        }

        if (StringUtils.isEmpty(typeStr)) {
            throw new EBusConfigurationReaderException("Property 'type' is missing for command ! {0}",
                    commandMethod != null ? commandMethod.getParent() : "<NULL>");
        }

        if (typeStr != null && typeStr.equals("template-block")) {

            Collection<@NonNull EBusCommandValue> templateCollection = null;

            if (StringUtils.isNotEmpty(valueDto.getName())) {
                logger.warn("Property 'name' is not allowed for type 'template-block', ignore property !");
            }

            // use the global or local id as template block, new with alpha 15
            String id = (String) valueDto.getProperty("id");
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
                templateCollection = new ArrayList<>(templateList);

            } else {
                throw new EBusConfigurationReaderException(
                        "No additional information for type 'template-block' defined!");
            }

            if (templateCollection != null) {
                for (EBusCommandValue commandValue : templateCollection) {

                    // clone the original value
                    EBusCommandValue clone = commandValue.clone();
                    clone.setParent(commandMethod);

                    overwritePropertiesFromTemplate(clone, valueDto);

                    result.add(clone);
                }
            }

            return result;

        } else if (typeStr != null && typeStr.equals("template")) {

            String id = (String) valueDto.getProperty("id");
            String globalId = collectionId + "." + id;
            Collection<@NonNull EBusCommandValue> templateCollection = null;

            if (StringUtils.isEmpty(id)) {
                throw new EBusConfigurationReaderException("No additional information for type 'template' defined!");
            }

            if (templateValueRegistry.containsKey(id)) {
                templateCollection = templateValueRegistry.get(id);

            } else if (templateValueRegistry.containsKey(globalId)) {
                templateCollection = templateValueRegistry.get(globalId);

            } else if (templateMap != null && templateMap.containsKey(id)) {
                // return the complete template block from within command block
                templateCollection = new ArrayList<>();
                templateCollection.add(templateMap.get(id));

            } else {
                throw new EBusConfigurationReaderException("Unable to find a template for id {0}!", id);

            }

            if (templateCollection != null && !templateCollection.isEmpty()) {
                for (EBusCommandValue commandValue : templateCollection) {

                    EBusCommandValue clone = commandValue.clone();
                    clone.setParent(commandMethod);

                    overwritePropertiesFromTemplate(clone, valueDto);

                    // allow owerwrite for single names
                    clone.setName(StringUtils.defaultIfEmpty(valueDto.getName(), clone.getName()));

                    result.add(clone);
                }
            } else {
                throw new EBusConfigurationReaderException("Internal template collection is empty!");
            }

            return result;

        } else if (typeStr != null && typeStr.equals("static")) {
            // convert static content to bytes

            byte[] byteArray = EBusUtils.toByteArray(valueDto.getDefault());
            Map<String, Object> properties = new HashMap<>();
            properties.put("length", byteArray.length);
            final IEBusType<?> typeByte = registry.getType(EBusTypeBytes.TYPE_BYTES, properties);

            EBusCommandValue commandValue = EBusCommandValue.getInstance(typeByte, byteArray);
            commandValue.setParent(commandMethod);

            result.add(commandValue);
            return result;
        }

        EBusCommandValue ev = null;

        // value is a nested value
        if (valueDto.getChildren() != null) {
            EBusCommandNestedValue evc = new EBusCommandNestedValue();

            // default for nested type for now
            final IEBusType<?> typeByte = registry.getType(EBusTypeByte.TYPE_BYTE);
            evc.setDefaultValue((byte) 0xff);
            evc.setType(typeByte);

            ev = evc;

            int pos = 0;
            List<EBusValueDTO> children = valueDto.getChildren();
            if (children != null) {
                for (EBusValueDTO childElem : children) {

                    // add pos information from list
                    childElem.setPos(pos);

                    // parse child value
                    for (EBusCommandValue childValue : parseValueConfiguration(childElem, templateMap, templateList,
                            commandMethod)) {
                        evc.add(childValue);
                    }

                    pos++;
                }
            }

        } else {
            // default value
            ev = new EBusCommandValue();
        }

        Map<String, Object> map = valueDto.getAsMap();
        IEBusType<?> type = registry.getType(typeStr, map);

        ev.setType(type);

        ev.setName(valueDto.getName());
        ev.setLabel(valueDto.getLabel());

        ev.setFactor(valueDto.getFactor());
        ev.setMin(valueDto.getMin());
        ev.setMax(valueDto.getMax());

        ev.setMapping(valueDto.getMapping());
        ev.setFormat(valueDto.getFormat());

        if (commandMethod != null) {
            ev.setParent(commandMethod);
        }

        result.add(ev);
        return result;
    }

    private void overwritePropertiesFromTemplate(@NonNull EBusCommandValue clone, @NonNull EBusValueDTO template) {
        // allow placeholders in template-block mode
        if (StringUtils.isNotEmpty(template.getLabel())) {
            if (StringUtils.isNotEmpty(clone.getLabel()) && clone.getLabel().contains("%s")) {
                clone.setLabel(String.format(clone.getLabel(), template.getLabel()));
            } else {
                clone.setLabel(template.getLabel());
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.cfg.IEBusConfigurationReader#setEBusTypes(de.csdev.ebus.command.datatypes.EBusTypeRegistry)
     */
    @Override
    public void setEBusTypes(@NonNull EBusTypeRegistry ebusTypes) {
        Objects.requireNonNull(ebusTypes, "ebusTypes");
        registry = ebusTypes;
    }

    @Override
    public @NonNull List<@NonNull IEBusCommandCollection> loadConfigurationCollectionBundle(@NonNull URL url) {

        Objects.requireNonNull(url, "url");

        List<@NonNull IEBusCommandCollection> result = new ArrayList<>();

        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, ?>>() {
        }.getType();

        try {

            Map<String, ?> mapping = gson.fromJson(new InputStreamReader(url.openStream()), type);

            if (mapping.containsKey("files")) {

                @SuppressWarnings("unchecked")
                List<Map<String, String>> files = (List<Map<String, String>>) mapping.get("files");

                if (files != null && !files.isEmpty()) {
                    for (Map<String, String> file : files) {
                        URL fileUrl = new URL(url, file.get("url"));

                        try {
                            logger.debug("Load configuration from url {} ...", fileUrl);
                            IEBusCommandCollection collection = loadConfigurationCollection(fileUrl);
                            result.add(collection);

                        } catch (EBusConfigurationReaderException e) {
                            logger.error("{} (url: {})", e.getMessage(), fileUrl);
                        } catch (IOException e) {
                            logger.error("error!", e);
                        }

                    }
                }
            }

        } catch (JsonSyntaxException | JsonIOException | IOException e) {
            logger.error("error!", e);
        }

        return result;
    }

    @Override
    public void clear() {
        templateBlockRegistry.clear();
        templateValueRegistry.clear();
    }

    public @NonNull Map<@NonNull String, @Nullable Collection<@NonNull EBusCommandValue>> getTemplateValueRegistry() {
        return templateValueRegistry;
    }

    public @NonNull Map<@NonNull String, @Nullable Collection<@NonNull EBusCommandValue>> getTemplateBlockRegistry() {
        return templateBlockRegistry;
    }
}
