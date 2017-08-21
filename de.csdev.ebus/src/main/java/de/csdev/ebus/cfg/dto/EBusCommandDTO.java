package de.csdev.ebus.cfg.dto;

import java.util.List;

public class EBusCommandDTO {

    private EBusCommandMethodDTO broadcast;
    private String command;
    private String comment;
    private String device;
    private String dst;
    private EBusCommandMethodDTO get;
    private String id;
    private EBusCommandMethodDTO set;
    private List<EBusValueDTO> template;
    private String type;
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

    public String getComment() {
        return comment;
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

    public String getType() {
        return type;
    }

    public void setBroadcast(EBusCommandMethodDTO broadcast) {
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

    public void setType(String type) {
        this.type = type;
    }

}
