/**
 * Copyright (c) 2016-2020 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.cfg.std.dto;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.annotations.SerializedName;

import de.csdev.ebus.utils.CollectionUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusValueDTO {

    @SerializedName("default")
    private String _default;

    private List<EBusValueDTO> children;
    private BigDecimal divider;
    private BigDecimal factor;
    private String format;
    private String label;
    private Integer length;
    private final transient Logger logger = LoggerFactory.getLogger(EBusValueDTO.class);
    private Map<String, String> mapping;
    private BigDecimal max;
    private BigDecimal min;
    private String name;
    private Integer pos;

    private Map<String, Object> properties;
    private String replaceValue;
    private boolean reverseByteOrder = false;
    private BigDecimal step;
    private String type;

    public Map<String, Object> getAsMap() {

        HashMap<String, Object> map = new HashMap<String, Object>();
        for (Field field : getClass().getDeclaredFields()) {
            try {
                Object value = field.get(this);

                // skip logger and empty values
                if (!field.getName().equals("logger") && value != null) {
                    map.put(field.getName(), value);
                }

            } catch (IllegalArgumentException e) {
                logger.error("error!", e);

            } catch (IllegalAccessException e) {
                logger.error("error!", e);
            }
        }

        if (properties != null) {
            map.putAll(properties);
        }

        return map;
    }

    public List<EBusValueDTO> getChildren() {
        return children;
    }

    public String getDefault() {
        return _default;
    }

    public BigDecimal getDivider() {
        return divider;
    }

    public BigDecimal getFactor() {
        return factor;
    }

    public String getFormat() {
        return format;
    }

    public String getLabel() {
        return label;
    }

    public Integer getLength() {
        return length;
    }

    public Map<String, String> getMapping() {
        return mapping;
    }

    public BigDecimal getMax() {
        return max;
    }

    public BigDecimal getMin() {
        return min;
    }

    public String getName() {
        return name;
    }

    public Integer getPos() {
        return pos;
    }

    public Map<String, Object> getProperties() {
        properties = CollectionUtils.newMapIfNull(properties);
        return CollectionUtils.unmodifiableNotNullMap(properties);
    }

    public Object getProperty(String key) {
        return CollectionUtils.get(properties, key);
    }

    public String getReplaceValue() {
        return replaceValue;
    }

    public BigDecimal getStep() {
        return step;
    }

    public String getType() {
        return type;
    }

    public boolean isReverseByteOrder() {
        return reverseByteOrder;
    }

    public void setChildren(List<EBusValueDTO> children) {
        this.children = children;
    }

    public void setDefault(String _default) {
        this._default = _default;
    }

    public void setDivider(BigDecimal divider) {
        this.divider = divider;
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

    public void setLength(Integer length) {
        this.length = length;
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

    public void setPos(Integer pos) {
        this.pos = pos;
    }

    public void setProperty(String key, Object value) {
        properties = CollectionUtils.newMapIfNull(properties);
        properties.put(key, value);
    }

    public void setReplaceValue(String replaceValue) {
        this.replaceValue = replaceValue;
    }

    public void setReverseByteOrder(boolean reverseByteOrder) {
        this.reverseByteOrder = reverseByteOrder;
    }

    public void setStep(BigDecimal step) {
        this.step = step;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "EBusValueDTO [" + (children != null ? "children=" + children + ", " : "")
                + (factor != null ? "factor=" + factor + ", " : "")
                + (divider != null ? "divider=" + divider + ", " : "") + (label != null ? "label=" + label + ", " : "")
                + (length != null ? "length=" + length + ", " : "")
                + (mapping != null ? "mapping=" + mapping + ", " : "") + (max != null ? "max=" + max + ", " : "")
                + (min != null ? "min=" + min + ", " : "") + (name != null ? "name=" + name + ", " : "")
                + (replaceValue != null ? "replaceValue=" + replaceValue + ", " : "")
                + (step != null ? "step=" + step + ", " : "") + (type != null ? "type=" + type + ", " : "")
                + (_default != null ? "_default=" + _default + ", " : "") + (pos != null ? "pos=" + pos + ", " : "")
                + (format != null ? "format=" + format + ", " : "") + "reverseByteOrder=" + reverseByteOrder + ", "
                + (properties != null ? "properties=" + properties : "") + "]";
    }

}
