package de.csdev.ebus.cfg.dto;

import java.util.List;

public class EBusCommandTypeDTO {

    private String command;
    private List<EBusValueDTO> master;
    private List<EBusValueDTO> slave;

    public String getCommand() {
        return command;
    }

    public List<EBusValueDTO> getMaster() {
        return master;
    }

    public List<EBusValueDTO> getSlave() {
        return slave;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setMaster(List<EBusValueDTO> master) {
        this.master = master;
    }

    public void setSlave(List<EBusValueDTO> slave) {
        this.slave = slave;
    }

}
