package de.csdev.ebus.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.command.EBusCommandRegistry;
import de.csdev.ebus.command.EBusCommandUtils;
import de.csdev.ebus.command.IEBusCommandCollection;
import de.csdev.ebus.command.IEBusCommandMethod;
import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.core.EBusConsts;
import de.csdev.ebus.core.EBusDataException;
import de.csdev.ebus.service.device.EBusDevice;
import de.csdev.ebus.service.device.EBusDeviceTable;

public class EBusConsoleUtils {

    private static final Logger logger = LoggerFactory.getLogger(EBusConsoleUtils.class);

    /**
     * @return
     */
    public static String getDeviceTableInformation(Collection<IEBusCommandCollection> collections,
            EBusDeviceTable deviceTable) {

        StringBuilder sb = new StringBuilder();

        Map<String, String> mapping = new HashMap<String, String>();

        for (IEBusCommandCollection collection : collections) {
            for (String identification : collection.getIdentification()) {
                mapping.put(identification, collection.getId());
            }
        }

        EBusDevice ownDevice = deviceTable.getOwnDevice();

        sb.append(String.format("%-2s | %-2s | %-14s | %-14s | %-25s | %-2s | %-10s | %-10s | %-20s\n", "MA", "SA",
                "Identifier", "Device", "Manufacture", "ID", "Firmware", "Hardware", "Last Activity"));

        sb.append(String.format("%-2s-+-%-2s-+-%-14s-+-%-14s-+-%-20s-+-%-2s-+-%-10s-+-%-10s-+-%-20s\n",
                StringUtils.repeat("-", 2), StringUtils.repeat("-", 2), StringUtils.repeat("-", 14),
                StringUtils.repeat("-", 14), StringUtils.repeat("-", 20), StringUtils.repeat("-", 2),
                StringUtils.repeat("-", 10), StringUtils.repeat("-", 10), StringUtils.repeat("-", 20)));

        for (EBusDevice device : deviceTable.getDeviceTable()) {

            boolean isBridge = device.equals(ownDevice);
            String masterAddress = EBusUtils.toHexDumpString(device.getMasterAddress());
            String slaveAddress = EBusUtils.toHexDumpString(device.getSlaveAddress());

            String activity = device.getLastActivity() == 0 ? "---" : new Date(device.getLastActivity()).toString();
            String id = EBusUtils.toHexDumpString(device.getDeviceId()).toString();
            String deviceName = isBridge ? "<interface>" : mapping.getOrDefault(id, "---");
            String manufacture = isBridge ? "eBUS Library" : device.getManufacturerName();

            sb.append(String.format("%-2s | %-2s | %-14s | %-14s | %-25s | %-2s | %-10s | %-10s | %-20s\n",
                    masterAddress, slaveAddress, id, deviceName, manufacture,
                    EBusUtils.toHexDumpString(device.getManufacturer()), device.getSoftwareVersion(),
                    device.getHardwareVersion(), activity));

        }

        sb.append(StringUtils.repeat("-", 118) + "\n");
        sb.append("MA = Master Address / SA = Slave Address / ID = Manufacture ID\n");

        return sb.toString();
    }

    public static String analyzeTelegram(EBusCommandRegistry registry, byte[] data) {

        StringBuilder sb = new StringBuilder();
        byte[] edata = null;

        sb.append("\n");

        try {
            edata = EBusCommandUtils.checkRawTelegram(data);
        } catch (EBusDataException e) {

            String msg = String.format("** Error on checking telegram: %s **", e.getMessage());
            int len = msg.length();

            sb.append("\n");
            sb.append(StringUtils.repeat("*", len) + "\n");
            sb.append(msg + "\n");

            msg = "**     !!! Warning: All following results are wrong and only displayed for information purpose !!!";
            msg += StringUtils.repeat(" ", len - msg.length() - 2) + "**";

            sb.append(msg + "\n");
            sb.append(StringUtils.repeat("*", len) + "\n");
            sb.append("\n");
        }

        sb.append("\n");
        sb.append("Check and unescape telegram\n");
        sb.append("***************************\n");
        sb.append("\n");

        sb.append(String.format("Original data : %s\n", EBusUtils.toHexDumpString(data)));
        sb.append(String.format("Unescaped data: %s\n", EBusUtils.toHexDumpString(edata)));

        byte[] command = Arrays.copyOfRange(edata, 2, 4);

        boolean isMasterMaster = EBusUtils.isMasterAddress(edata[1]);
        boolean isBroadcast = edata[1] == EBusConsts.BROADCAST_ADDRESS;
        boolean isMasterSlave = !isMasterMaster && !isBroadcast;

        int masterDataLenPos = 4;
        int masterDataLen = edata[masterDataLenPos];
        byte[] masterData = Arrays.copyOfRange(edata, 5, 5 + masterDataLen);

        int masterCrcPos = 5 + masterDataLen;
        int slaveACKPos = masterCrcPos + 1;

        String dataString = EBusUtils.toHexDumpString(edata).toString();
        int dataLen = dataString.length();

        sb.append("\n");
        sb.append("Analyse the telegram\n");
        sb.append("********************\n");

        sb.append("\n");
        sb.append(dataString + "\n");

        final String FORMAT = "%-20s | %-20s | %s";

        sb.append(createTelegramResoverRow(0, 1, dataLen,
                String.format(FORMAT, "Source address", "Type: " + addressType(edata[0]), hex(edata[0]))));

        sb.append(createTelegramResoverRow(1, 1, dataLen,
                String.format(FORMAT, "Destination address", "Type: " + addressType(edata[1]), hex(edata[1]))));

        sb.append(createTelegramResoverRow(2, 2, dataLen,
                String.format(FORMAT, "Command", addressType(edata[1]), hex(command))));

        sb.append(createTelegramResoverRow(4, 1, dataLen,
                String.format(FORMAT, "Master Data Length", "Length: " + edata[4], hex(edata[4]))));

        sb.append(createTelegramResoverRow(5, masterDataLen, dataLen,
                String.format(FORMAT, "Master Data", "", hex(masterData))));

        sb.append(createTelegramResoverRow(masterCrcPos, 1, dataLen,
                String.format(FORMAT, "Master CRC", "", hex(edata[masterCrcPos]))));

        if (isMasterMaster) {
            sb.append(createTelegramResoverRow(slaveACKPos, 1, dataLen, hex(edata[slaveACKPos]) + " Slave ACK"));
            // SYN

        } else if (isBroadcast) {
            // SYN

        } else if (isMasterSlave) {

            int slaveDataLenPos = slaveACKPos + 1;
            int slaveDataLen = edata[slaveDataLenPos];

            int slaveDataPos = slaveDataLenPos + 1;
            int slaveCRCPos = slaveDataPos + slaveDataLen;

            int masterACKPos = slaveCRCPos + 1;

            byte[] slaveData = Arrays.copyOfRange(edata, slaveDataPos, slaveDataPos + slaveDataLen);

            sb.append(createTelegramResoverRow(slaveACKPos, 1, dataLen,
                    String.format(FORMAT, "Slave ACK", "", hex(edata[slaveACKPos]))));

            sb.append(createTelegramResoverRow(slaveDataLenPos, 1, dataLen, String.format(FORMAT, "Slave Data Length",
                    "Length: " + edata[slaveDataLenPos], hex(edata[slaveDataLenPos]))));

            sb.append(createTelegramResoverRow(slaveDataPos, slaveDataLen, dataLen,
                    String.format(FORMAT, "Slave Data", "", hex(slaveData))));

            sb.append(createTelegramResoverRow(slaveCRCPos, 1, dataLen,
                    String.format(FORMAT, "Slave CRC", "", hex(edata[slaveCRCPos]))));

            sb.append(createTelegramResoverRow(masterACKPos, 1, dataLen,
                    String.format(FORMAT, "Master ACK", "", hex(edata[masterACKPos]))));

            // SYN
        }

        List<IEBusCommandMethod> methods = registry.find(edata);

        sb.append("\n");
        sb.append("Resolve the telegram\n");
        sb.append("********************\n");
        sb.append("\n");
        sb.append(String.format("Found %s command method(s) for this telegram.\n", methods.size()));
        sb.append("\n");

        for (IEBusCommandMethod method : methods) {
            try {
                Map<String, Object> result = EBusCommandUtils.decodeTelegram(method, data);

                sb.append(String.format("Values from command '%s' with method '%s' from collection '%s'\n",
                        method.getParent().getId(), method.getMethod(),
                        method.getParent().getParentCollection().getId()));

                for (Entry<String, Object> entry : result.entrySet()) {
                    Object value = entry.getValue();

                    if (value instanceof byte[]) {
                        value = EBusUtils.toHexDumpString((byte[]) value);
                    }

                    sb.append(String.format("  %-20s = %s\n", entry.getKey(), value.toString()));
                }

            } catch (EBusTypeException e) {
                logger.error("error!", e);
            }
        }
        sb.append("\n");

        return sb.toString();

    }

    private static String addressType(byte b) {

        if (EBusUtils.isMasterAddress(b)) {
            return "Master";
        } else if (b == EBusConsts.BROADCAST_ADDRESS) {
            return "Broadcast";
        }

        return "Slave";
    }

    private static String hex(byte[] b) {
        return EBusUtils.toHexDumpString(b).toString();

    }

    private static String hex(byte b) {
        return EBusUtils.toHexDumpString(b);
    }

    private static String createTelegramResoverRow(int pos, int length, int textStart, String text) {

        StringBuilder sb = new StringBuilder();
        String repeat = StringUtils.repeat("^^ ", length);

        if (repeat.length() > 0) {
            repeat = repeat.substring(0, repeat.length() - 1);
        }

        sb.append(StringUtils.repeat(" ", pos * 3));
        sb.append(repeat);

        sb.append(StringUtils.repeat("-", textStart - sb.length()));
        sb.append(" ");
        sb.append(text);
        sb.append("\n");

        return sb.toString();
    }
}
