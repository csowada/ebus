/**
 * Copyright (c) 2017-2025 by the respective copyright holders.
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import de.csdev.ebus.utils.StringUtil;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
/**
 * A configuration reader for eBus configurations that loads and parses
 * configuration files
 * in JSON format. This reader handles templates, command methods, and value
 * configurations
 * while maintaining type safety and null safety.
 *
 * @author Christian Sowada - Initial contribution
 */
public class EBusConfigurationReader implements IEBusConfigurationReader {

    private static final int INITIAL_MAP_CAPACITY = 32;
    private static final String ERROR_REGISTRY_INIT = "Unable to create a new eBus type registry!";

    private final Logger logger = LoggerFactory.getLogger(EBusConfigurationReader.class);

    private @NonNull EBusTypeRegistry registry;

    // Initialize maps with a reasonable initial capacity to avoid resizing
    private @NonNull Map<@NonNull String, @Nullable Collection<@NonNull EBusCommandValue>> templateValueRegistry = new HashMap<>(
            INITIAL_MAP_CAPACITY);
    private @NonNull Map<@NonNull String, @Nullable Collection<@NonNull EBusCommandValue>> templateBlockRegistry = new HashMap<>(
            INITIAL_MAP_CAPACITY);

    /**
     * Creates a new EBusConfigurationReader instance.
     * Initializes the type registry and template registries.
     *
     * @throws IllegalStateException if the type registry cannot be created
     */
    public EBusConfigurationReader() {
        try {
            this.registry = new EBusTypeRegistry();
        } catch (EBusTypeException e) {
            throw new IllegalStateException(ERROR_REGISTRY_INIT, e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.cfg.IEBusConfigurationReader#loadBuildInConfigurations()
     */
    @Override
    public @NonNull List<@NonNull IEBusCommandCollection> loadBuildInConfigurationCollections()
            throws EBusConfigurationReaderException, IOException {

        URL url = EBusConfigurationReader.class.getResource("/index-configuration.json");
        Objects.requireNonNull(url);

        return loadConfigurationCollectionBundle(url);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.csdev.ebus.cfg.IEBusConfigurationReader#loadConfigurationCollection(java.
     * io.InputStream)
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

        try {
            EBusCommandCollection commandCollection = (EBusCommandCollection) loadConfigurationCollection(collection);
            // add md5 hash
            commandCollection.setSourceHash(md.digest());

            return commandCollection;

        } catch (EBusConfigurationReaderException e) {
            throw new EBusConfigurationReaderException("%s [ URL: %s ]", e.getMessage(), url);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.csdev.ebus.cfg.IEBusConfigurationReader#loadConfigurationCollection(de.
     * csdev.
     * ebus.cfg.std.dto.EBusCollectionDTO)
     */
    public @NonNull IEBusCommandCollection loadConfigurationCollection(@NonNull EBusCollectionDTO collection)
            throws EBusConfigurationReaderException {

        Objects.requireNonNull(collection, "collection");

        if (StringUtil.isEmpty(collection.getId())) {
            throw new EBusConfigurationReaderException("The property 'id' is missing for the configuration!");
        }

        if (StringUtil.isEmpty(collection.getLabel())) {
            throw new EBusConfigurationReaderException("The property 'label' is missing for the configuration!");
        }

        if (StringUtil.isEmpty(collection.getDescription())) {
            throw new EBusConfigurationReaderException("The property 'description' is missing for the configuration!");
        }

        if (collection.getProperties() == null) {
            throw new EBusConfigurationReaderException("The property 'properties' is missing for the configuration!");
        }

        String id = Objects.requireNonNull(collection.getId(), "Collection ID must not be null");
        String label = Objects.requireNonNull(collection.getLabel(), "Collection label must not be null");
        String description = Objects.requireNonNull(collection.getDescription(),
                "Collection description must not be null");
        Map<String, Object> properties = Objects.requireNonNull(collection.getProperties(),
                "Collection properties must not be null");

        EBusCommandCollection commandCollection = new EBusCommandCollection(id, label, description, properties);

        // add md5 hash
        commandCollection.setIdentification(collection.getIdentification());

        // parse the template list block
        parseTemplateListConfiguration(collection);

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

    /**
     * Parses the template list configuration from the given collection.
     * This method processes all template blocks defined in the collection
     * and adds them to the template registries for later use.
     *
     * @param collection The eBus collection containing template definitions
     * @throws EBusConfigurationReaderException if there's an error parsing
     *                                          templates
     */
    protected void parseTemplateListConfiguration(@NonNull EBusCollectionDTO collection)
            throws EBusConfigurationReaderException {

        Objects.requireNonNull(collection, "collection");

        // extract templates
        List<EBusCommandTemplatesDTO> templateSection = collection.getTemplates();
        if (templateSection != null) {
            for (EBusCommandTemplatesDTO templates : templateSection) {
                parseTemplateBlockConfiguration(templates.getTemplate(), collection, templates);
            }
        }
    }

    /**
     * Parses template block configuration and adds templates to the registry.
     * This method processes template values and creates corresponding command
     * values
     * that can be reused throughout the configuration.
     *
     * @param templateValues The list of template values to parse, may be null
     * @param collection     The parent collection containing these templates
     * @param templates      The template configuration containing name and other
     *                       properties
     * @throws EBusConfigurationReaderException if there's an error parsing the
     *                                          templates
     */
    protected void parseTemplateBlockConfiguration(@Nullable List<EBusValueDTO> templateValues,
            @NonNull EBusCollectionDTO collection, @NonNull EBusCommandTemplatesDTO templates)
            throws EBusConfigurationReaderException {

        Objects.requireNonNull(collection, "collection cannot be null");
        Objects.requireNonNull(templates, "templates cannot be null");

        if (templateValues == null || templateValues.isEmpty()) {
            return;
        }

        Collection<EBusCommandValue> blockList = new ArrayList<>();

        for (EBusValueDTO value : templateValues) {
            if (value != null) {
                Collection<@NonNull EBusCommandValue> parsedValues = parseValueConfiguration(value, null, null, null);

                if (!parsedValues.isEmpty()) {
                    blockList.addAll(parsedValues);

                    // Build global id with StringBuilder for better performance
                    String id = new StringBuilder(50)
                            .append(collection.getId())
                            .append('.')
                            .append(templates.getName())
                            .append('.')
                            .append(value.getName())
                            .toString();

                    logger.trace("Add template with global id {} to registry ...", id);
                    templateValueRegistry.put(id, parsedValues);
                }
            }
        }

        if (!blockList.isEmpty()) {
            String id = collection.getId() + "." + templates.getName();
            logger.trace("Add template block with global id {} to registry ...", id);
            templateBlockRegistry.put(id, blockList);
        }
    }

    /**
     * Parses the command configuration from the given collection.
     * 
     * @param commandCollection The eBus command collection
     * @param commandElement    The eBus command element to parse
     * @return The parsed eBus command
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
        for (EBusValueDTO template : checkedList(commandElement.getTemplate())) {
            for (EBusCommandValue templateCfg : parseValueConfiguration(template, null, null, null)) {
                String name = templateCfg.getName();
                if (StringUtil.isEmpty(name)) {
                    templateMap.put(name, templateCfg);
                }

                templateList.add(templateCfg);
            }
        }

        if (id == null) {
            throw new EBusConfigurationReaderException("Property 'id' is missing for command ! %s",
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
                String methodCommand = commandMethodElement.getCommand();
                if (StringUtil.isNotEmpty(methodCommand)) {
                    commandMethod.setCommand(EBusUtils.toByteArray(methodCommand));
                } else {
                    commandMethod.setCommand(command);
                }

                commandMethod.setDestinationAddress(destination);
                commandMethod.setSourceAddress(source);

                for (EBusValueDTO template : checkedList(commandMethodElement.getMaster())) {
                    for (EBusCommandValue ev : parseValueConfiguration(template, templateMap, templateList,
                            commandMethod)) {
                        commandMethod.addMasterValue(ev);
                    }
                }

                for (EBusValueDTO template : checkedList(commandMethodElement.getSlave())) {
                    for (EBusCommandValue ev : parseValueConfiguration(template, templateMap, templateList,
                            commandMethod)) {
                        commandMethod.addSlaveValue(ev);
                    }
                }

                // default type is always master-slave if not explicit set or a broadcast
                String methodType = commandMethodElement.getType();
                if ("master-master".equals(methodType)) {
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
     * Creates a new list containing only non-null elements from the input list.
     * This helper function ensures type safety and null safety when working with
     * lists
     * of EBusValueDTO objects.
     *
     * @param source The source list that may contain null elements
     * @return A new list containing only non-null elements, never null itself
     */
    protected @NonNull List<@NonNull EBusValueDTO> checkedList(@Nullable List<EBusValueDTO> source) {
        if (source == null || source.isEmpty()) {
            return new ArrayList<>();
        }

        // Pre-size the list to avoid resizing
        List<@NonNull EBusValueDTO> templates = new ArrayList<>(source.size());
        for (EBusValueDTO template : source) {
            if (template != null) {
                templates.add(template);
            }
        }
        return templates;
    }

    /**
     * Parses the value configuration from the given DTO.
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

        if (StringUtil.isEmpty(typeStr)) {
            throw new EBusConfigurationReaderException("Property 'type' is missing for command ! %s",
                    commandMethod != null ? commandMethod.getParent() : "<NULL>");
        }

        if (typeStr != null && typeStr.equals("template-block")) {

            Collection<@NonNull EBusCommandValue> templateCollection = null;

            String valueName = valueDto.getName();
            if (StringUtil.isNotEmpty(valueName)) {
                logger.warn("Property 'name' is not allowed for type 'template-block', ignore property !");
            }

            // use the global or local id as template block, new with alpha 15
            String id = (String) valueDto.getProperty("id");
            String globalId = collectionId + "." + id;

            if (StringUtil.isNotEmpty(id)) {
                templateCollection = templateBlockRegistry.get(id);

                if (templateCollection == null) {
                    // try to convert the local id to a global id
                    logger.trace("Unable to find a template with id {}, second try with {} ...", id, globalId);

                    templateCollection = templateBlockRegistry.get(globalId);

                    if (templateCollection == null) {
                        throw new EBusConfigurationReaderException("Unable to find a template-block with id %s!", id);
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
                    EBusCommandValue clone = commandValue.getClonedInstance();
                    clone.setParent(commandMethod);

                    overwritePropertiesFromTemplate(clone, valueDto);

                    result.add(clone);
                }
            }

            return result;

        } else if (typeStr != null && typeStr.equals("template")) {

            String id = (String) valueDto.getProperty("id");
            String globalId = collectionId != null ? collectionId + "." + id : null;
            Collection<@NonNull EBusCommandValue> templateCollection = null;

            if (StringUtil.isEmpty(id)) {
                throw new EBusConfigurationReaderException("No additional information for type 'template' defined!");
            }

            if (templateValueRegistry.containsKey(id)) {
                templateCollection = templateValueRegistry.get(id);

            } else if (globalId != null && templateValueRegistry.containsKey(globalId)) {
                templateCollection = templateValueRegistry.get(globalId);

            } else if (templateMap != null && templateMap.containsKey(id)) {
                // return the complete template block from within command block
                templateCollection = new ArrayList<>();
                templateCollection.add(templateMap.get(id));

            } else {
                throw new EBusConfigurationReaderException("Unable to find a template for id %s!", id);

            }

            if (templateCollection != null && !templateCollection.isEmpty()) {
                for (EBusCommandValue commandValue : templateCollection) {

                    EBusCommandValue clone = commandValue.getClonedInstance();
                    clone.setParent(commandMethod);

                    overwritePropertiesFromTemplate(clone, valueDto);

                    // allow owerwrite for single names
                    clone.setName(
                            StringUtil.defaultIfEmpty(valueDto.getName(), Objects.requireNonNull(clone.getName())));

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
        String templateLabel = template.getLabel();
        String cloneLabel = clone.getLabel();

        // allow placeholders in template-block mode
        if (StringUtil.isNotEmpty(templateLabel)) {
            if (cloneLabel != null && cloneLabel.contains("%s")) {
                clone.setLabel(String.format(cloneLabel, templateLabel));
            } else {
                clone.setLabel(templateLabel);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.csdev.ebus.cfg.IEBusConfigurationReader#setEBusTypes(de.csdev.ebus.command
     * .datatypes.EBusTypeRegistry)
     */
    @Override
    public void setEBusTypes(@NonNull EBusTypeRegistry ebusTypes) {
        Objects.requireNonNull(ebusTypes, "ebusTypes");
        registry = ebusTypes;
    }

    /**
     * Loads a bundle of configuration collections from a JSON index file.
     * The index file should contain a "files" array with URLs to individual
     * configuration files.
     *
     * @param url The URL to the index file
     * @return A list of loaded command collections
     * @throws EBusConfigurationReaderException if there's an error parsing
     *                                          configurations
     * @throws IOException                      if there's an error reading the
     *                                          files
     * @throws NullPointerException             if url is null
     */
    @Override
    public @NonNull List<@NonNull IEBusCommandCollection> loadConfigurationCollectionBundle(@NonNull URL url)
            throws EBusConfigurationReaderException, IOException {

        Objects.requireNonNull(url, "url cannot be null");

        List<@NonNull IEBusCommandCollection> result = new ArrayList<>();
        InputStreamReader reader = null;

        try {
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, ?>>() {
            }.getType();

            reader = new InputStreamReader(url.openStream());
            Map<String, ?> mapping = gson.fromJson(reader, type);

            if (mapping != null && mapping.containsKey("files")) {
                @SuppressWarnings("unchecked")
                List<Map<String, String>> files = (List<Map<String, String>>) mapping.get("files");

                if (files != null && !files.isEmpty()) {
                    // Pre-size the result list
                    result = new ArrayList<>(files.size());

                    for (Map<String, String> file : files) {
                        String fileUrlStr = file.get("url");
                        if (fileUrlStr != null) {
                            URL fileUrl = new URL(url, fileUrlStr);
                            logger.debug("Loading configuration from url {} ...", fileUrl);

                            IEBusCommandCollection collection = loadConfigurationCollection(fileUrl);
                            result.add(collection);
                        }
                    }
                }
            }
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    logger.warn("Failed to close reader", e);
                }
            }
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
