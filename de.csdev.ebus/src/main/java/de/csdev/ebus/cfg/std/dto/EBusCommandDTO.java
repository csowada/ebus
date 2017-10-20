/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.cfg.std.dto;

import java.util.List;

import com.google.gson.annotations.JsonAdapter;

import de.csdev.ebus.cfg.std.EBusValueJsonDeserializer;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusCommandDTO {

    private EBusCommandMethodDTO broadcast;
    private String command;
    private String label;
    private String device;
    private String dst;
    private EBusCommandMethodDTO get;
    private String id;
    private EBusCommandMethodDTO set;

    @JsonAdapter(EBusValueJsonDeserializer.class)
    private List<EBusValueDTO> template;

    private String src;

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public EBusCommandMethodDTO getBroadcast() {
        return broadcast;
    }

    public String getCommand() {
        return command;
    }

    public String getLabel() {
        return label;
    }

    public String getDevice() {
        return device;
    }

    public String getDst() {
        return dst;
    }

    public EBusCommandMethodDTO getGet() {
        return get;
    }

    public String getId() {
        return id;
    }

    public EBusCommandMethodDTO getSet() {
        return set;
    }

    public List<EBusValueDTO> getTemplate() {
        return template;
    }

    public void setBroadcast(EBusCommandMethodDTO broadcast) {
        this.broadcast = broadcast;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public void setDst(String dst) {
        this.dst = dst;
    }

    public void setGet(EBusCommandMethodDTO get) {
        this.get = get;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSet(EBusCommandMethodDTO set) {
        this.set = set;
    }

    public void setTemplate(List<EBusValueDTO> template) {
        this.template = template;
    }

    @Override
    public String toString() {
        return "EBusCommandDTO [broadcast=" + broadcast + ", command=" + command + ", label=" + label + ", device="
                + device + ", dst=" + dst + ", get=" + get + ", id=" + id + ", set=" + set + ", template=" + template
                + ", src=" + src + "]";
    }

}
