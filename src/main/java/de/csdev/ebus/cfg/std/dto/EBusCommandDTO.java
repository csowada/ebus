/**
 * Copyright (c) 2016-2020 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.cfg.std.dto;

import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusCommandDTO {

    private @Nullable EBusCommandMethodDTO broadcast;
    private @Nullable String command;
    private @Nullable String label;
    private @Nullable String device;
    private @Nullable String dst;
    private @Nullable EBusCommandMethodDTO get;
    private @Nullable String id;
    private @Nullable EBusCommandMethodDTO set;

    private @Nullable List<EBusValueDTO> template;

    private @Nullable String src;

    public @Nullable String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public @Nullable EBusCommandMethodDTO getBroadcast() {
        return broadcast;
    }

    public @Nullable String getCommand() {
        return command;
    }

    public @Nullable String getLabel() {
        return label;
    }

    public @Nullable String getDevice() {
        return device;
    }

    public @Nullable String getDst() {
        return dst;
    }

    public @Nullable EBusCommandMethodDTO getGet() {
        return get;
    }

    public @Nullable String getId() {
        return id;
    }

    public @Nullable EBusCommandMethodDTO getSet() {
        return set;
    }

    public @Nullable List<EBusValueDTO> getTemplate() {
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
