package de.csdev.ebus.a00;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.cfg.datatypes.EBusTypeByte;
import de.csdev.ebus.cfg.datatypes.EBusTypeException;
import de.csdev.ebus.cfg.datatypes.EBusTypeWord;
import de.csdev.ebus.cfg.datatypes.EBusTypes;
import de.csdev.ebus.cfg.datatypes.IEBusType;
import de.csdev.ebus.cfg.json.v1.mapper.EBusConfigurationTelegram;
import de.csdev.ebus.command.EBusCommand;
import de.csdev.ebus.command.EBusCommandRegistry;
import de.csdev.ebus.command.EBusCommandUtils;
import de.csdev.ebus.command.EBusCommandValue;
import de.csdev.ebus.command.IEBusCommand;
import de.csdev.ebus.command.IEBusCommand.Type;
import de.csdev.ebus.command.IEBusCommandWritable;
import de.csdev.ebus.command.EBusKWCrcMValue;
import de.csdev.ebus.utils.EBusUtils;

public class GGGg {

    private static final Logger logger = LoggerFactory.getLogger(GGGg.class);

    private EBusTypes registry;

    public static void main(String[] args) {
        try {
			new GGGg().x();
		} catch (EBusTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public List<EBusConfigurationTelegram> loadConfiguration() throws IOException {

        ClassLoader classLoader = this.getClass().getClassLoader();
        URL resource = classLoader.getResource("other.json");

        final ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
        final InputStream inputStream = resource.openConnection().getInputStream();

        final List<EBusConfigurationTelegram> loadedTelegramRegistry = mapper.readValue(inputStream,
                new TypeReference<List<EBusConfigurationTelegram>>() {
                });

        return loadedTelegramRegistry;
    }

    public void x() throws EBusTypeException {

        registry = new EBusTypes();

        EBusCommandRegistry tregistry = new EBusCommandRegistry();

        // OH1ConfigurationReader ohreader = new OH1ConfigurationReader();
        //
        // try {
        //// List<EBusCommand> list = ohreader.b(loadConfiguration(), registry);
        //// tregistry.addTelegramConfigurationList(list);
        //
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }

        /*
         * {
         * "comment": "<Heating> Set Heating program",
         * "device": "Wolf CSZ-2 > CGB-2 > HCM-2/GBC-e, FW: 1.6",
         * "id": "set_program_heating_circuit",
         * "class": "heating",
         * "command": "50 23",
         * "data": "00 74 27 00 00 5D 01 00 00",
         * "dst": "35",
         *
         * "values": {
         * "_crc": {"type": "crc-kw", "pos": 6},
         * "program": {"type": "byte", "pos": 9, "label": "Heating program", "min": 0, "max": 3,
         * "mapping": {"0": "standby", "1": "auto", "2": "heating mode", "3":"economy mode"}}
         * }
         * },
         *
         * {
         * "comment": "<Heating> Heating program",
         * "device": "Wolf CSZ-2 > CGB-2 > HCM-2/GBC-e, FW: 1.6",
         * "id": "program_heating_circuit",
         * "class": "heating",
         * "command": "50 22",
         * "data": "(FF) 74 27",
         * "dst": "35",
         *
         * "values": {
         * "program": {"type": "byte", "pos": 12, "label": "Heating program", "min": 0, "max": 3,
         * "mapping": {"0": "standby", "1": "auto", "2": "heating mode", "3":"economy mode"}}
         * }
         * },
         */

        // m.addExtendedCommand(new byte[] { 0x01 })
        // .addMasterXxxx("status_auto_stroker", registry.getType(EBusTypeByte.BYTE))
        // .addMasterXxxx("state_air_pressure", registry.getType(EBusTypeBit.BIT))
        // .addMasterXxxx("state_gas_pressure", registry.getType(EBusTypeBit.BIT));

        IEBusType typeWord = registry.getType(EBusTypeWord.WORD);
        IEBusType typeByte = registry.getType(EBusTypeByte.BYTE);

        EBusCommandValue value = new EBusCommandValue();
        value.setName("program");
        value.setLabel("Heating program");
        value.setType(registry.getType(EBusTypeWord.WORD));
        value.setMin(BigDecimal.valueOf(0));
        value.setMax(BigDecimal.valueOf(3));
        value.setDefaultValue(new byte[] { 0x00, 0x01 });

        // Mmm m = new Mmm();
        // m.setId("heating.program_heating_circuit");
        // m.setType(Type.READ);
        // m.setCommand(new byte[] { 0x50, 0x22 });
        // // m.addExtendedCommand(new byte[] { (byte) 0xFF, 0x74, 0x27 });
        // // m.addMasterValue(new KWCrc());
        // // m.addMasterValue({0x74, 0x27});
        // m.addSlaveValue(value);

        EBusCommand telegram = new EBusCommand();
        telegram.setId("heating.program_heating_circuit");
        telegram.setType(Type.SET);
        telegram.setCommand(new byte[] { 0x50, 0x22 });
        // m.addExtendedCommand();
        // m.addExtendedCommand(new byte[] { (byte) 0xFF, 0x74, 0x27 });
        // m.addMasterValue(new KWCrc());
        // m.addMasterValue({0x74, 0x27});

//        telegram.addExtendedCommand(new KWCrcMValue(typeByte));
//        telegram.addExtendedCommand(EBusCommandValue.getInstance(typeWord, new byte[] { 0x74, 0x27 }));

        telegram.addMasterValue(value);
        telegram.addMasterValue(EBusCommandValue.getInstance(typeWord, new byte[] { 0x5D, 0x01 }));
        telegram.addMasterValue(EBusCommandValue.getInstance(typeWord, new byte[] { 0x00, 0x00 }));

        tregistry.addTelegramConfiguration(telegram);

        ByteBuffer masterTelegram = EBusCommandUtils.buildMasterTelegram(telegram, (byte) 0x00, (byte) 0xFF, null);
        System.out.println("GGGg.x() > " + EBusUtils.toHexDumpString(masterTelegram));

        ByteBuffer mask = telegram.getMasterTelegramMask();
        System.out.println("GGGg.x() > " + EBusUtils.toHexDumpString(mask));

        byte[] byteArray = EBusUtils.toByteArray("08 FE 50 22 09 00 74 27 74 27 5D 01 00 00");
        List<IEBusCommand> find = tregistry.find(byteArray);

        for (IEBusCommand eBusTelegram : find) {

            Map<String, Object> encode = EBusCommandUtils.decodeTelegram(eBusTelegram, byteArray);
            System.out.println("GGGg.x()");
        }

        // boolean match = false;
        // for (int i = 0; i < mask.position(); i++) {
        // byte b = mask.get(i);
        //
        // if (b == (byte) 0xFF) {
        // if (masterTelegram.get(i) != byteArray[i]) {
        // // match = true;
        // break;
        // }
        // }
        // if (i == mask.position() - 1) {
        // match = true;
        // }
        // }

        // System.out.println("GGGg.x()" + match);
        System.out.println("GGGg.x()");
    }

}
