package de.csdev.ebus.cfg.json.v1;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import de.csdev.ebus.cfg.IConfigurationProvider;
import de.csdev.ebus.cfg.datatypes.EBusTypeBytes;
import de.csdev.ebus.cfg.datatypes.EBusTypes;
import de.csdev.ebus.cfg.datatypes.IEBusType;
import de.csdev.ebus.command.EBusCommand;
import de.csdev.ebus.command.EBusCommandUtils;
import de.csdev.ebus.command.EBusCommandValue;
import de.csdev.ebus.command.IEBusCommandWritable;
import de.csdev.ebus.utils.EBusUtils;

public class OH1ConfigurationReader implements IConfigurationProvider {

    // filter: (00)
    private static Pattern P_BRACKETS_VALS = Pattern.compile("(\\(([0-9A-Z]{2})\\))");

    private EBusTypes ebusTypes;

    /**
     * @param map
     * @param pos
     * @return
     */
    private Entry<String, EBusConfigurationValue> findValuebyPos(Map<String, EBusConfigurationValue> map, Integer pos) {
        for (Entry<String, EBusConfigurationValue> entry : map.entrySet()) {
            if (entry.getValue().getPos() == pos) {
                return entry;
            }
        }

        return null;
    }

    /**
     * @param map
     * @return
     */
    private Entry<String, EBusConfigurationValue> findMaxPos(Map<String, EBusConfigurationValue> map) {

        Entry<String, EBusConfigurationValue> maxEntry = null;

        for (Entry<String, EBusConfigurationValue> entry : map.entrySet()) {

            if (maxEntry == null) {
                maxEntry = entry;
            } else if (entry.getValue().getPos() > maxEntry.getValue().getPos()) {
                maxEntry = entry;
            }

        }

        return maxEntry;
    }

    /**
     * @param command
     * @param cfg
     * @return
     */
    private EBusCommand loadSingleEbusCommandByFilter(IEBusCommandWritable command, EBusConfigurationTelegram cfg) {
        System.err.println("Skip filter command " + cfg.getFullId());

        String f = cfg.getFilter();

        if (!f.startsWith(".*")) {

            System.err.println("Skip filter command " + cfg.getFullId());
            return null;

            // String[] split = f.split(" ");
            // System.out.println("OH1ConfigurationReader.b()");
            //
            // // source
            // if (!split[0].equals("??")) {
            // // telegram.setSource();
            // }
            //
            // // destination
            // if (!split[1].equals("??")) {
            // // telegram.setDest();
            // }
            //
            // // command
            // telegram.setCommand(EBusUtils.toByteArray(split[2] + " " + split[3]));
            //
            // // len
            // if (!split[4].equals("??")) {
            //
            // }

        }

        return null;
    }

    /**
     * @param jsonValue
     * @param value
     */
    private void transferEBusValue(EBusConfigurationValue jsonValue, EBusCommandValue value) {
        if (jsonValue.getFactor() != null) {
            value.setFactor(jsonValue.getFactor());
        }

        if (jsonValue.getMin() != null) {
            value.setMin(jsonValue.getMin());
        }

        if (jsonValue.getMax() != null) {
            value.setMax(jsonValue.getMax());
        }

        if (jsonValue.getLabel() != null) {
            value.setLabel(jsonValue.getLabel());
        }

        if (jsonValue.getMapping() != null) {
            value.setMapping(jsonValue.getMapping());
        }

        if (jsonValue.getStep() != null) {
            value.setStep(jsonValue.getStep());
        }

    }

    /**
     * @param pos
     * @param cfg
     * @param deltaMap
     * @param byteArray
     * @return
     */
    private EBusCommandValue computeValueForPos(int pos, EBusConfigurationTelegram cfg,
            Map<String, EBusConfigurationValue> deltaMap, byte[] byteArray) {

        final int FIRST_MASTER_DATA_POS = 6;

        final EBusTypes registry = getEBusTypes();
        final IEBusType typeBytes = registry.getType(EBusTypeBytes.BYTES);

        EBusCommandValue value = null;

        Entry<String, EBusConfigurationValue> findValuebyPos = findValuebyPos(cfg.getValues(), pos);
        if (findValuebyPos != null) {

            EBusConfigurationValue jsonValue = findValuebyPos.getValue();

            // remove processed config
            deltaMap.remove(findValuebyPos.getKey());

            IEBusType type = registry.getType(findValuebyPos.getValue().getType());

            if (type != null) {

                value = new EBusCommandValue();

                value.setType(type);
                value.setName(findValuebyPos.getKey());

                transferEBusValue(jsonValue, value);

                int typeLen = type.getTypeLenght();
                System.out.println("GGGg.b() JA" + pos);
                if (typeLen > 1) {
                    pos += typeLen - 1;
                }
            } else {
                System.out.println("Unknown value type" + pos);
                value = EBusCommandValue.getInstance(typeBytes, new byte[] { byteArray[pos - FIRST_MASTER_DATA_POS] });
            }

        } else {
            System.out.println("GGGg.b() NEIN " + pos);
            value = EBusCommandValue.getInstance(typeBytes, new byte[] { byteArray[pos - FIRST_MASTER_DATA_POS] });
        }

        // SLAVE
        //
        // Entry<String, EBusConfigurationValue> findValuebyPos = findValuebyPos(cfg.getValues(), pos);
        //
        // EBusNumberValue value = null;
        //
        // if (findValuebyPos != null) {
        //
        // // remove processed config
        // deltaMap.remove(findValuebyPos.getKey());
        //
        // value = new EBusNumberValue();
        // value.setType(type);
        // value.setName(findValuebyPos.getKey());
        //
        // int typeLen = type.getTypeLenght();
        // System.out.println("GGGg.b() JOA" + pos);
        // if (typeLen > 1) {
        // pos += typeLen - 1;
        // }
        // } else {
        // System.out.println("GGGg.b() NÖ" + pos);
        // value = EBusNumberValue.getInstance(typeBytes,
        // new byte[] { byteArray[pos - FIRST_MASTER_DATA_POS] });
        // }

        return value;

    }

    private EBusCommand loadSingleEbusCommandByData(EBusCommand command, EBusConfigurationTelegram cfg) {

        final EBusTypes registry = getEBusTypes();

        command.setCommand(EBusUtils.toByteArray(cfg.getCommand()));

        // remove brackets () from data strings
        String data = cfg.getData();
        if (StringUtils.isNoneEmpty(data)) {
            Matcher matcher = P_BRACKETS_VALS.matcher(data);
            if (matcher.find()) {
                data = matcher.replaceAll(matcher.group(2));
            }
        }

        byte[] byteArray = EBusUtils.toByteArray(data);
        int masterEndPos = 5 + byteArray.length;

        Map<String, EBusConfigurationValue> deltaMap = new HashMap<String, EBusConfigurationValue>();
        if (cfg.getValues() != null) {
            deltaMap.putAll(cfg.getValues());
        }

        final int FIRST_MASTER_DATA_POS = 6;

        int pos = FIRST_MASTER_DATA_POS; // first master data pos
        while (pos <= masterEndPos) {

            EBusCommandValue value = computeValueForPos(pos, cfg, deltaMap, byteArray);
            command.addMasterValue(value);
            pos += value.getType().getTypeLenght();
        }

        int slaveDataStartPos = masterEndPos + 4; // CRC, ACK, NN, DB1

        Entry<String, EBusConfigurationValue> findMaxPos = findMaxPos(deltaMap);

        if (findMaxPos != null) {
            pos = slaveDataStartPos;

            IEBusType type = registry.getType(findMaxPos.getValue().getType());

            if (type != null) {

                int slaveEndPos = findMaxPos.getValue().getPos();
                slaveEndPos += (type.getTypeLenght() - 1);

                while (pos <= slaveEndPos) {
                    EBusCommandValue value = computeValueForPos(pos, cfg, deltaMap, byteArray);
                    command.addSlaveValue(value);
                    pos += value.getType().getTypeLenght();
                }

            }

        }

        return command;
    }

    /**
     * @param cfg
     * @return
     */
    private EBusCommand loadSingleEbusCommand(EBusConfigurationTelegram cfg) {

        System.out.println("Load OH1 command " + cfg.getFullId());

        EBusCommand command = new EBusCommand();
        command.setId(cfg.getFullId());
        command.setType(de.csdev.ebus.command.IEBusCommand.Type.READ);
        command.setDescription(cfg.getComment());
        command.setConfigurationSource(cfg.getConfigurationSource());

        if (cfg.getFilter() != null) {
            command = loadSingleEbusCommandByFilter(command, cfg);

        } else {
            command = loadSingleEbusCommandByData(command, cfg);
        }

        return command;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.cfg.IConfigurationX#loadConfiguration(java.net.URL)
     */
    public List<EBusCommand> loadConfiguration(URL url) throws IOException {

        final ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
        final InputStream inputStream = url.openConnection().getInputStream();

        final List<EBusConfigurationTelegram> cfgs = mapper.readValue(inputStream,
                new TypeReference<List<EBusConfigurationTelegram>>() {
                });

        List<EBusCommand> telegramConfiguration = new ArrayList<EBusCommand>();

        for (EBusConfigurationTelegram cfg : cfgs) {

            EBusCommand telegram = loadSingleEbusCommand(cfg);

            if (telegram != null) {
                telegramConfiguration.add(telegram);

                ByteBuffer masterTelegram = EBusCommandUtils.buildMasterTelegram(telegram, (byte) 0x00, (byte) 0xFF, null);
                System.out.println("GGGg.UUUUUU() > " + EBusUtils.toHexDumpString(masterTelegram));

                ByteBuffer masterTelegramMask = telegram.getMasterTelegramMask();
                System.out.println("GGGg.YYYYYY() > " + EBusUtils.toHexDumpString(masterTelegramMask));
            }

        } // loop

        return telegramConfiguration;
    }


    public EBusTypes getEBusTypes() {
        return ebusTypes;
    }


    public void setEBusTypes(EBusTypes ebusTypes) {
        this.ebusTypes = ebusTypes;
    }
}
