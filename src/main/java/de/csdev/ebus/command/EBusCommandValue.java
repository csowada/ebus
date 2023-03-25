/**
 * Copyright (c) 2017-2023 by the respective copyright holders.
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
import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import de.csdev.ebus.command.datatypes.IEBusType;
import de.csdev.ebus.utils.CollectionUtils;
import de.csdev.ebus.utils.EBusUtils;

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

    private @Nullable Object defaultValue;

    private @Nullable BigDecimal factor;

    private @Nullable String format;

    private @Nullable String label;

    private @Nullable Map<String, String> mapping;

    private @Nullable BigDecimal max;

    private @Nullable BigDecimal min;

    private @Nullable String name;

    private EBusCommandMethod parent;

    private Map<String, Object> properties;

    private BigDecimal step;

    private IEBusType<?> type;

    protected EBusCommandValue createInstance() {
        return new EBusCommandValue();
    }

    @Override
    public @NonNull EBusCommandValue clone() {

        EBusCommandValue clone = createInstance();
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
            clone.mapping = new HashMap<>();
            for (Entry<String, String> elem : this.mapping.entrySet()) {
                clone.mapping.put(elem.getKey(), elem.getValue());
            }
        }

        if (this.properties != null) {
            clone.properties = new HashMap<>();
            for (Entry<String, Object> elem : this.properties.entrySet()) {
                clone.properties.put(elem.getKey(), elem.getValue());
            }
        }

        return clone;
    }

    @Override
    public @Nullable Object getDefaultValue() {
        return this.defaultValue;
    }

    @Override
    public @Nullable BigDecimal getFactor() {
        return this.factor;
    }

    @Override
    public @Nullable String getFormat() {
        return this.format;
    }

    @Override
    public @Nullable String getLabel() {
        return this.label;
    }

    @Override
    public @Nullable Map<@NonNull String, @NonNull String> getMapping() {
        return this.mapping;
    }

    @Override
    public @Nullable BigDecimal getMax() {
        return this.max;
    }

    @Override
    public @Nullable BigDecimal getMin() {
        return this.min;
    }

    @Override
    public @Nullable String getName() {
        return this.name;
    }

    @Override
    public @Nullable IEBusCommandMethod getParent() {
        return this.parent;
    }

    @Override
    public @Nullable Map<@NonNull String, @NonNull Object> getProperties() {
        return CollectionUtils.unmodifiableNotNullMap(this.properties);
    }

    @Override
    public @Nullable BigDecimal getStep() {
        return this.step;
    }

    @Override
    public @NonNull IEBusType<?> getType() {
        return Objects.requireNonNull(type);
    }

    public void setDefaultValue(@Nullable Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setFactor(@Nullable BigDecimal factor) {
        this.factor = factor;
    }

    public void setFormat(@Nullable String format) {
        this.format = format;
    }

    public void setLabel(@Nullable String label) {
        this.label = label;
    }

    public void setMapping(@Nullable Map<String, String> mapping) {
        this.mapping = mapping;
    }

    public void setMax(@Nullable BigDecimal max) {
        this.max = max;
    }

    public void setMin(@Nullable BigDecimal min) {
        this.min = min;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }

    public void setParent(@Nullable EBusCommandMethod parent) {
        this.parent = parent;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = new HashMap<>();
        this.properties.putAll(properties);
    }

    public void setProperty(String key, String value) {
        this.properties = CollectionUtils.newMapIfNull(properties);
        this.properties.put(key, value);
    }

    public void setStep(@Nullable BigDecimal step) {
        this.step = step;
    }

    public void setType(@Nullable IEBusType<?> type) {
        this.type = type;
    }

    @Override
    public String toString() {

        // byte hex string for byte[]
        Object defaultValueMod = (defaultValue instanceof byte[]) ? EBusUtils.toHexDumpString((byte[]) defaultValue)
                : defaultValue;

        return "EBusCommandValue [" + (name != null ? "name=" + name + ", " : "")
                + (label != null ? "label=" + label + ", " : "") + (type != null ? "type=" + type + ", " : "")
                + (min != null ? "min=" + min + ", " : "") + (max != null ? "max=" + max + ", " : "")
                + (factor != null ? "factor=" + factor + ", " : "")
                + (mapping != null ? "mapping=" + mapping + ", " : "")
                + (properties != null ? "properties=" + properties + ", " : "")
                + (step != null ? "step=" + step + ", " : "") + (format != null ? "format=" + format + ", " : "")
                + (defaultValueMod != null ? "defaultValue=" + defaultValueMod : "") + "]";
    }

}
