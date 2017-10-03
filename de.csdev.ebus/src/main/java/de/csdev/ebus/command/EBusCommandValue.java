/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.csdev.ebus.command.datatypes.IEBusType;
import de.csdev.ebus.utils.CollectionUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusCommandValue implements IEBusValue {

    public static EBusCommandValue getInstance(IEBusType<?> type, byte[] data) {
        EBusCommandValue value = new EBusCommandValue();
        value.setType(type);
        value.setDefaultValue(data);
        return value;
    }

    private Object defaultValue;

    private BigDecimal factor;

    private String format;

    private String label;

    private Map<String, String> mapping;

    private BigDecimal max;

    private BigDecimal min;

    private String name;

    private EBusCommandMethod parent;

    private Map<String, Object> properties;

    private BigDecimal step;

    private IEBusType<?> type;

    @Override
    public EBusCommandValue clone() {

        EBusCommandValue clone = new EBusCommandValue();
        clone.defaultValue = this.defaultValue;
        clone.factor = this.factor;
        clone.format = this.format;
        clone.label = this.label;
        clone.max = this.max;
        clone.min = this.min;
        clone.name = this.name;
        clone.step = this.step;
        clone.type = this.type;

        if (this.mapping != null) {
            clone.mapping = new HashMap<String, String>();
            for (Entry<String, String> elem : this.mapping.entrySet()) {
                clone.mapping.put(elem.getKey(), elem.getValue());
            }
        }

        if (this.properties != null) {
            clone.properties = new HashMap<String, Object>();
            for (Entry<String, Object> elem : this.properties.entrySet()) {
                clone.properties.put(elem.getKey(), elem.getValue());
            }
        }

        return clone;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        EBusCommandValue other = (EBusCommandValue) obj;
        if (defaultValue == null) {
            if (other.defaultValue != null) {
                return false;
            }
        } else if (!defaultValue.equals(other.defaultValue)) {
            return false;
        }
        if (factor == null) {
            if (other.factor != null) {
                return false;
            }
        } else if (!factor.equals(other.factor)) {
            return false;
        }
        if (format == null) {
            if (other.format != null) {
                return false;
            }
        } else if (!format.equals(other.format)) {
            return false;
        }
        if (label == null) {
            if (other.label != null) {
                return false;
            }
        } else if (!label.equals(other.label)) {
            return false;
        }
        if (mapping == null) {
            if (other.mapping != null) {
                return false;
            }
        } else if (!mapping.equals(other.mapping)) {
            return false;
        }
        if (max == null) {
            if (other.max != null) {
                return false;
            }
        } else if (!max.equals(other.max)) {
            return false;
        }
        if (min == null) {
            if (other.min != null) {
                return false;
            }
        } else if (!min.equals(other.min)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (properties == null) {
            if (other.properties != null) {
                return false;
            }
        } else if (!properties.equals(other.properties)) {
            return false;
        }
        if (step == null) {
            if (other.step != null) {
                return false;
            }
        } else if (!step.equals(other.step)) {
            return false;
        }
        return true;
    }

    @Override
    public Object getDefaultValue() {
        return defaultValue;
    }

    @Override
    public BigDecimal getFactor() {
        return factor;
    }

    @Override
    public String getFormat() {
        return format;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public Map<String, String> getMapping() {
        return mapping;
    }

    @Override
    public BigDecimal getMax() {
        return max;
    }

    @Override
    public BigDecimal getMin() {
        return min;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public IEBusCommandMethod getParent() {
        return parent;
    }

    @Override
    public Map<String, Object> getProperties() {
        return CollectionUtils.unmodifiableNotNullMap(properties);
    }

    @Override
    public BigDecimal getStep() {
        return step;
    }

    @Override
    public IEBusType<?> getType() {
        return type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((defaultValue == null) ? 0 : defaultValue.hashCode());
        result = prime * result + ((factor == null) ? 0 : factor.hashCode());
        result = prime * result + ((format == null) ? 0 : format.hashCode());
        result = prime * result + ((label == null) ? 0 : label.hashCode());
        result = prime * result + ((mapping == null) ? 0 : mapping.hashCode());
        result = prime * result + ((max == null) ? 0 : max.hashCode());
        result = prime * result + ((min == null) ? 0 : min.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((properties == null) ? 0 : properties.hashCode());
        result = prime * result + ((step == null) ? 0 : step.hashCode());
        return result;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setFactor(BigDecimal factor) {
        this.factor = factor;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setMapping(Map<String, String> mapping) {
        this.mapping = mapping;
    }

    public void setMax(BigDecimal max) {
        this.max = max;
    }

    public void setMin(BigDecimal min) {
        this.min = min;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParent(EBusCommandMethod parent) {
        this.parent = parent;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = new HashMap<String, Object>();
        this.properties.putAll(properties);
    }

    public void setProperty(String key, String value) {
        properties = CollectionUtils.newMapIfNull(properties);
        properties.put(key, value);
    }

    public void setStep(BigDecimal step) {
        this.step = step;
    }

    public void setType(IEBusType<?> type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "EBusCommandValue [" + (name != null ? "name=" + name + ", " : "")
                + (label != null ? "label=" + label + ", " : "") + (type != null ? "type=" + type + ", " : "")
                + (min != null ? "min=" + min + ", " : "") + (max != null ? "max=" + max + ", " : "")
                + (factor != null ? "factor=" + factor + ", " : "")
                + (mapping != null ? "mapping=" + mapping + ", " : "")
                + (properties != null ? "properties=" + properties + ", " : "")
                + (step != null ? "step=" + step + ", " : "") + (format != null ? "format=" + format + ", " : "")
                + (defaultValue != null ? "defaultValue=" + defaultValue : "") + "]";
    }

}
