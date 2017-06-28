package de.csdev.ebus.command;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class EBusCommand implements IEBusCommandWritable {

    private byte[] command;

    private String configurationSource;

    private String description;

    private List<IEBusValue> extendCommandValue;

    private String id;
    
    private List<IEBusValue> masterTypes;

    private List<IEBusValue> slaveTypes;

    private Type type;
    
    private ByteBuffer telegramMask;
    
    public IEBusCommandWritable addExtendedCommand(IEBusValue value) {
        if (extendCommandValue == null) {
            extendCommandValue = new ArrayList<IEBusValue>();
        }

        extendCommandValue.add(value);

        return this;
    }

    public IEBusCommandWritable addMasterValue(IEBusValue value) {
        if (masterTypes == null) {
            masterTypes = new ArrayList<IEBusValue>();
        }

        masterTypes.add(value);

        return this;
    }

    public IEBusCommandWritable addSlaveValue(IEBusValue value) {
        if (slaveTypes == null) {
            slaveTypes = new ArrayList<IEBusValue>();
        }

        slaveTypes.add(value);
        return this;
    }

	public byte[] getCommand() {
		return command;
	}

	public String getConfigurationSource() {
		return configurationSource;
	}

	public String getDescription() {
		return description;
	}

	public List<IEBusValue> getExtendCommandValue() {
		return extendCommandValue;
	}

	public String getId() {
		return id;
	}

    public ByteBuffer getMasterTelegramMask() {
		
		if(telegramMask == null)
			telegramMask = EBusCommandUtils.getMasterTelegramMask(this);
		
		return telegramMask;
		
    }

	public List<IEBusValue> getMasterTypes() {
		return masterTypes;
	}

	public List<IEBusValue> getSlaveTypes() {
		return slaveTypes;
	}

	public Type getType() {
		return type;
	}
    
	public EBusCommand setCommand(byte[] command) {
        this.command = command;
        return this;
    }

	public void setConfigurationSource(String configurationSource) {
        this.configurationSource = configurationSource;
    }
    
	public void setDescription(String description) {
        this.description = description;
    }

    public void setExtendCommandValue(List<IEBusValue> extendCommandValue) {
		this.extendCommandValue = extendCommandValue;
	}

	public EBusCommand setId(String id) {
        this.id = id;
        return this;
    }

    public void setMasterTypes(List<IEBusValue> masterTypes) {
		this.masterTypes = masterTypes;
	}

    public void setSlaveTypes(List<IEBusValue> slaveTypes) {
		this.slaveTypes = slaveTypes;
	}

	public IEBusCommandWritable setType(Type type) {
        this.type = type;
        return this;
    }

}
