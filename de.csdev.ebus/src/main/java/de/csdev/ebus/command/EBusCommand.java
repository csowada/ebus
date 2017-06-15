package de.csdev.ebus.command;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.csdev.ebus.cfg.datatypes.EBusTypeBytes;
import de.csdev.ebus.cfg.datatypes.IEBusType;
import de.csdev.ebus.utils.EBusUtils;

public class EBusCommand {

    private byte[] command;

    private List<IEBusValue> extendCommandValue;

    private List<IEBusValue> masterTypes;

    private List<IEBusValue> slaveTypes;

    private String description;
    private String configurationSource;

    public void setDescription(String description) {
        this.description = description;
    }

    public void setConfigurationSource(String configurationSource) {
        this.configurationSource = configurationSource;
    }

    private Type type;
    private String id;

    public enum Type {
        READ,
        WRITE
    }

    public EBusCommand setCommand(byte[] command) {
        this.command = command;
        return this;
    }

    public EBusCommand setId(String id) {
        this.id = id;
        return this;
    }

    public EBusCommand setType(Type type) {
        this.type = type;
        return this;
    }

    public EBusCommand addExtendedCommand(IEBusValue value) {
        if (extendCommandValue == null) {
            extendCommandValue = new ArrayList<IEBusValue>();
        }

        extendCommandValue.add(value);

        return this;
    }

    public EBusCommand addMasterValue(IEBusValue value) {

        if (masterTypes == null) {
            masterTypes = new ArrayList<IEBusValue>();
        }

        masterTypes.add(value);

        return this;
    }

    public EBusCommand addSlaveValue(IEBusValue value) {
        if (slaveTypes == null) {
            slaveTypes = new ArrayList<IEBusValue>();
        }

        slaveTypes.add(value);
        return this;
    }

    public ByteBuffer getMasterTelegramMask() {
        // byte len = 0;
        ByteBuffer buf = ByteBuffer.allocate(50);
        buf.put((byte) 0x00); // QQ - Source
        buf.put((byte) 0x00); // ZZ - Target
        buf.put(new byte[] { (byte) 0xFF, (byte) 0xFF }); // PB SB - Command
        buf.put((byte) 0xFF); // NN - Length

        if (extendCommandValue != null) {

            for (IEBusValue entry : extendCommandValue) {
                IEBusType type = entry.getType();

                if (entry instanceof KWCrcMValue) {
                    buf.put(new byte[type.getTypeLenght()]);
                } else {
                    for (int i = 0; i < type.getTypeLenght(); i++) {
                        buf.put((byte) 0xFF);

                    }
                }

                // buf.put(new byte[type.getTypeLenght()]);

            }
        }

        if (masterTypes != null) {
            for (IEBusValue entry : masterTypes) {
                IEBusType type = entry.getType();

                boolean x = type instanceof EBusTypeBytes;

                if (entry.getName() == null && type instanceof EBusTypeBytes && entry.getDefaultValue() != null) {
                    for (int i = 0; i < type.getTypeLenght(); i++) {
                        buf.put((byte) 0xFF);

                    }
                } else {
                    for (int i = 0; i < type.getTypeLenght(); i++) {
                        buf.put((byte) 0x00);

                    }
                }

                // buf.put(type.encode(entry.getDefaultValue()));

            }
        }

        buf.put((byte) 0x00); // Master CRC

        return buf;
    }

    public ByteBuffer buildMasterTelegram(Byte source, Byte target, Map<String, Object> values) {

        byte len = 0;
        ByteBuffer buf = ByteBuffer.allocate(50);

        buf.put(source); // QQ - Source
        buf.put(target); // ZZ - Target
        buf.put(command); // PB SB - Command
        buf.put((byte) 0x00); // NN - Length, will be set later

        if (extendCommandValue != null) {
            for (IEBusValue entry : extendCommandValue) {
                IEBusType type = entry.getType();
                buf.put(type.encode(entry.getDefaultValue()));
                len += type.getTypeLenght();
            }
        }

        if (masterTypes != null) {
            for (IEBusValue entry : masterTypes) {
                IEBusType type = entry.getType();

                // use the value from the values map if set
                if (values != null && values.containsKey(entry.getName())) {
                    buf.put(type.encode(values.get(entry.getName())));
                } else {
                    if (entry.getDefaultValue() == null) {
                        buf.put(type.encode(0));
                    } else {
                        byte[] encode = type.encode(entry.getDefaultValue());
                        buf.put(type.encode(entry.getDefaultValue()));
                    }

                }

                len += type.getTypeLenght();
            }
        }

        byte crc8 = EBusUtils.crc8(buf.array(), len);

        // set len
        buf.put(4, len);

        buf.put(crc8);

        return buf;
    }

    public Map<String, Object> encode(byte[] data) {

        HashMap<String, Object> result = new HashMap<String, Object>();
        int pos = 6;

        if (extendCommandValue != null) {
            for (IEBusValue ev : extendCommandValue) {
                pos += ev.getType().getTypeLenght();
            }
        }

        if (masterTypes != null) {
            for (IEBusValue ev : masterTypes) {
                byte[] src = new byte[ev.getType().getTypeLenght()];
                System.arraycopy(data, pos - 1, src, 0, src.length);
                Object decode = ev.getType().decode(src);
                System.out.println("EBusTelegram.encode()" + decode);
                result.put(ev.getName(), decode);
                pos += ev.getType().getTypeLenght();
            }
        }

        pos += 3;

        if (slaveTypes != null) {

            for (IEBusValue ev : slaveTypes) {
                byte[] src = new byte[ev.getType().getTypeLenght()];
                System.arraycopy(data, pos - 1, src, 0, src.length);
                Object decode = ev.getType().decode(src);

                if (ev instanceof EBusCommandValue) {
                    EBusCommandValue nev = (EBusCommandValue) ev;
                    BigDecimal multiply = (BigDecimal) decode;

                    if (nev.getFactor() != null) {
                        multiply = multiply.multiply(nev.getFactor());
                    }

                    if (nev.getMin() != null && multiply.compareTo(nev.getMin()) == -1) {
                        System.out.println("EBusCommand.encode() >> MIN");
                    }

                    if (nev.getMax() != null && multiply.compareTo(nev.getMax()) == 1) {
                        System.out.println("EBusCommand.encode() >> MAX");
                    }

                    decode = multiply;
                    // nev.getMax()
                }

                System.out.println("EBusTelegram.encode()" + decode);
                result.put(ev.getName(), decode);
                pos += ev.getType().getTypeLenght();
            }
        }

        return result;
    }

}
