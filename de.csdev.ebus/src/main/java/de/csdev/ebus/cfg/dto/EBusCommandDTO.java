package de.csdev.ebus.cfg.dto;

import java.util.List;

public class EBusCommandDTO {

    private EBusCommandTypeDTO broadcast;
    private String command;
    private String comment;
    private String device;
    private String dst;
    private EBusCommandTypeDTO get;
    private String id;
    private EBusCommandTypeDTO set;
    private List<EBusValueDTO> template;
    private String type;
    private String src;

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public EBusCommandTypeDTO getBroadcast() {
        return broadcast;
    }

    public String getCommand() {
        return command;
    }

    public String getComment() {
        return comment;
    }

    public String getDevice() {
        return device;
    }

    public String getDst() {
        return dst;
    }

    public EBusCommandTypeDTO getGet() {
        return get;
    }

    public String getId() {
        return id;
    }

    public EBusCommandTypeDTO getSet() {
        return set;
    }

    public List<EBusValueDTO> getTemplate() {
        return template;
    }

    public String getType() {
        return type;
    }

    public void setBroadcast(EBusCommandTypeDTO broadcast) {
        this.broadcast = broadcast;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public void setDst(String dst) {
        this.dst = dst;
    }

    public void setGet(EBusCommandTypeDTO get) {
        this.get = get;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSet(EBusCommandTypeDTO set) {
        this.set = set;
    }

    public void setTemplate(List<EBusValueDTO> template) {
        this.template = template;
    }

    public void setType(String type) {
        this.type = type;
    }

}
