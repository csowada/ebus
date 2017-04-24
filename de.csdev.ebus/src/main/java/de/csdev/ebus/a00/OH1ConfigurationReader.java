package de.csdev.ebus.a00;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import de.csdev.ebus.cfg.EBusConfigurationTelegram;
import de.csdev.ebus.cfg.EBusConfigurationValue;
import de.csdev.ebus.cfg.datatypes.EBusTypeByte;
import de.csdev.ebus.cfg.datatypes.EBusTypes;
import de.csdev.ebus.cfg.datatypes.IEBusType;
import de.csdev.ebus.command.EBusCommand;
import de.csdev.ebus.command.EBusNumberValue;
import de.csdev.ebus.command.EBusCommand.Type;
import de.csdev.ebus.utils.EBusUtils;

public class OH1ConfigurationReader {

    // filter: (00)
    private static Pattern P_BRACKETS_VALS = Pattern.compile("(\\(([0-9A-Z]{2})\\))");

    private Entry<String, EBusConfigurationValue> findValuebyPos(Map<String, EBusConfigurationValue> map, Integer pos) {
        for (Entry<String, EBusConfigurationValue> entry : map.entrySet()) {
            if (entry.getValue().getPos() == pos) {
                return entry;
            }
        }

        return null;
    }

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

    public List<EBusCommand> b(List<EBusConfigurationTelegram> cfgs, EBusTypes registry) {

        final IEBusType typeByte = registry.getType(EBusTypeByte.BYTE);

        List<EBusCommand> telegramConfiguration = new ArrayList<EBusCommand>();

        for (EBusConfigurationTelegram cfg : cfgs) {

            EBusCommand telegram = new EBusCommand();
            telegram.setId(cfg.getFullId());
            telegram.setType(Type.READ);
            telegram.setCommand(EBusUtils.toByteArray(cfg.getCommand()));

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
            deltaMap.putAll(cfg.getValues());

            final int FIRST_MASTER_DATA_POS = 6;

            int pos = FIRST_MASTER_DATA_POS; // first master data pos
            while (pos <= masterEndPos) {

                EBusNumberValue value = null;

                Entry<String, EBusConfigurationValue> findValuebyPos = findValuebyPos(cfg.getValues(), pos);
                if (findValuebyPos != null) {

                    // remove processed config
                    deltaMap.remove(findValuebyPos.getKey());

                    IEBusType type = registry.getType(findValuebyPos.getValue().getType());

                    if (type != null) {

                        value = new EBusNumberValue();
                        value.setType(type);
                        value.setName(findValuebyPos.getKey());

                        int typeLen = type.getTypeLenght();
                        System.out.println("GGGg.b() JA" + pos);
                        if (typeLen > 1) {
                            pos += typeLen - 1;
                        }
                    } else {
                        System.out.println("GGGg.b() UPS" + pos);
                        value = EBusNumberValue.getInstance(typeByte, new byte[] { byteArray[pos - FIRST_MASTER_DATA_POS] });
                    }

                } else {
                    System.out.println("GGGg.b() NEIN " + pos);
                    value = EBusNumberValue.getInstance(typeByte, new byte[] { byteArray[pos - FIRST_MASTER_DATA_POS] });
                }

                telegram.addExtendedCommand(value);

                // telegram.addMasterValue(value);

                pos++;
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

                        Entry<String, EBusConfigurationValue> findValuebyPos = findValuebyPos(cfg.getValues(), pos);

                        EBusNumberValue value = null;

                        if (findValuebyPos != null) {

                            // remove processed config
                            deltaMap.remove(findValuebyPos.getKey());

                            value = new EBusNumberValue();
                            value.setType(type);
                            value.setName(findValuebyPos.getKey());

                            System.out.println("GGGg.b() JOA" + pos);
                        } else {
                            System.out.println("GGGg.b() NÖ" + pos);
                            value = EBusNumberValue.getInstance(typeByte,
                                    new byte[] { byteArray[pos - FIRST_MASTER_DATA_POS] });
                        }

                        telegram.addSlaveValue(value);

                        pos++;
                    }

                }

            }

            telegramConfiguration.add(telegram);

            ByteBuffer masterTelegram = telegram.buildMasterTelegram((byte) 0x00, (byte) 0xFF, null);
            System.out.println("GGGg.UUUUUU() > " + EBusUtils.toHexDumpString(masterTelegram));

            ByteBuffer masterTelegramMask = telegram.getMasterTelegramMask();
            System.out.println("GGGg.YYYYYY() > " + EBusUtils.toHexDumpString(masterTelegramMask));

        }

        return telegramConfiguration;
    }
}
