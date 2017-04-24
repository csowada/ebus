package de.csdev.ebus.command;

import java.math.BigDecimal;

import de.csdev.ebus.cfg.datatypes.IEBusType;

public class EBusNumberValue implements IEBusValue {

    private String name;

    private String label;

    private IEBusType type;

    private BigDecimal min;

    private BigDecimal max;

    private BigDecimal factor;

    public BigDecimal getFactor() {
        return factor;
    }

    public void setFactor(BigDecimal factor) {
        this.factor = factor;
    }

    private Object defaultValue;

    @Override
    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    // public void setDefaultValue(byte[] defaultValue) {
    // this.defaultValue = this.type.decode(defaultValue);
    // // this.defaultValue = defaultValue;
    // }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public IEBusType getType() {
        return type;
    }

    @Override
    public void setType(IEBusType type) {
        this.type = type;
    }

    public BigDecimal getMin() {
        return min;
    }

    public void setMin(BigDecimal min) {
        this.min = min;
    }

    public BigDecimal getMax() {
        return max;
    }

    public void setMax(BigDecimal max) {
        this.max = max;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static EBusNumberValue getInstance(IEBusType type, byte[] data) {
        EBusNumberValue value = new EBusNumberValue();
        value.setType(type);
        value.setDefaultValue(data);
        return value;
    }

}
