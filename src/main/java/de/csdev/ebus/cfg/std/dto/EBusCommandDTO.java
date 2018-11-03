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
        return "EBusCommandDTO [" + (broadcast != null ? "broadcast=" + broadcast + ", " : "")
                + (command != null ? "command=" + command + ", " : "") + (label != null ? "label=" + label + ", " : "")
                + (device != null ? "device=" + device + ", " : "") + (dst != null ? "dst=" + dst + ", " : "")
                + (get != null ? "get=" + get + ", " : "") + (id != null ? "id=" + id + ", " : "")
                + (set != null ? "set=" + set + ", " : "") + (template != null ? "template=" + template + ", " : "")
                + (src != null ? "src=" + src : "") + "]";
    }

}
