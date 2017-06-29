package de.csdev.ebus.cfg;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParser.Feature;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import de.csdev.ebus.cfg.datatypes.EBusTypeBytes;
import de.csdev.ebus.cfg.datatypes.EBusTypes;
import de.csdev.ebus.cfg.datatypes.IEBusType;
import de.csdev.ebus.command.EBusCommand;
import de.csdev.ebus.command.EBusCommandRegistry;
import de.csdev.ebus.command.EBusCommandValue;
import de.csdev.ebus.utils.EBusUtils;
import de.csdev.ebus.utils.NumberUtils;

public class OH2ConfigurationReader {

    private ObjectMapper mapper;
    private EBusTypes registry;
    private EBusCommandRegistry tr;

    public OH2ConfigurationReader(EBusCommandRegistry tr, EBusTypes registry) {
        this.tr = tr;
        this.registry = registry;

    }

    public void aaaa() throws IOException {
        ClassLoader classLoader = this.getClass().getClassLoader();
        URL resource = classLoader.getResource("new-cfg-format.json");

        mapper = new ObjectMapper();

        final InputStream inputStream = resource.openConnection().getInputStream();

        read(inputStream);
    }

    public void read(InputStream inputStream) throws IOException {

        if (mapper == null) {
            mapper = new ObjectMapper();
            mapper.configure(Feature.ALLOW_COMMENTS, true);
        }

        final List<Map<String, Object>> json = mapper.readValue(inputStream,
                new TypeReference<List<Map<String, Object>>>() {
                });

        for (Map<String, Object> element : json) {

            tr.addTelegramConfiguration(parseTelegramConfiguration(element));

            element.get("comment");
            element.get("device");
            element.get("id");
            element.get("dst");
            element.get("command");
            element.get("debug");

        }

    }

    @SuppressWarnings({ "unchecked", "null" })
    public EBusCommand parseTelegramConfiguration(Map<String, Object> element) {

        EBusCommand cfg = new EBusCommand();

        HashMap<String, EBusCommandValue> templateMap = new HashMap<String, EBusCommandValue>();

        cfg.setId((String) element.get("id"));
        cfg.setCommand(EBusUtils.toByteArray((String) element.get("command")));

        Object entry = element.get("templates");
        if (entry != null || entry instanceof List) {
            for (Map<String, Object> template : (List<Map<String, Object>>) entry) {
                EBusCommandValue templateCfg = parseValueConfiguration(template, null);
                templateMap.put(templateCfg.getName(), templateCfg);
            }
        }

        entry = element.get("get");
        if (entry != null || entry instanceof Map) {

            Map<String, Object> map = (Map<String, Object>) entry;

            entry = map.get("master");
            if (entry != null || entry instanceof List) {
                for (Map<String, Object> template : (List<Map<String, Object>>) entry) {
                    EBusCommandValue ev = parseValueConfiguration(template, templateMap);
                    cfg.addMasterValue(ev);
                }
            }

            entry = map.get("slave");
            if (entry != null || entry instanceof List) {
                for (Map<String, Object> template : (List<Map<String, Object>>) entry) {
                    EBusCommandValue ev = parseValueConfiguration(template, templateMap);
                    cfg.addSlaveValue(ev);
                }
            }
        }

        // entry = element.get("set");
        // if (entry != null || entry instanceof Map) {
        //
        // Map<String, Object> map = (Map<String, Object>) entry;
        //
        // entry = map.get("master");
        // if (entry != null || entry instanceof List) {
        // for (Map<String, Object> template : (List<Map<String, Object>>) entry) {
        // EBusValue ev = parseValueConfiguration(template, templateMap);
        // cfg.addMasterValue(ev);
        // }
        // }
        //
        // entry = map.get("slave");
        // if (entry != null || entry instanceof List) {
        // for (Map<String, Object> template : (List<Map<String, Object>>) entry) {
        // EBusValue ev = parseValueConfiguration(template, templateMap);
        // cfg.addSlaveValue(ev);
        // }
        // }
        // }
        // System.out.println("OH2ConfigurationReader.parseTelegramConfiguration()"
        // + cfg.buildMasterTelegram((byte) 0, (byte) 0, null));

        return cfg;
    }

    public EBusCommandValue parseValueConfiguration(Map<String, Object> template,
            Map<String, EBusCommandValue> templateMap) {

        String typeStr = (String) template.get("type");

        if (typeStr.equals("template")) {
            return templateMap.get(template.get("name"));

        } else if (typeStr.equals("static")) {
            byte[] byteArray = EBusUtils.toByteArray((String) template.get("default"));
            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put("length", byteArray.length);
            final IEBusType typeByte = registry.getType(EBusTypeBytes.BYTES, properties);

            return EBusCommandValue.getInstance(typeByte, byteArray);
        }

        EBusCommandValue ev = new EBusCommandValue();

        IEBusType type = registry.getType(typeStr);
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

        template.get("mapping");

        return ev;
    }
}
