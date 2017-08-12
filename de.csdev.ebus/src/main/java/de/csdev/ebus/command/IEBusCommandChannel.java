package de.csdev.ebus.command;

import java.nio.ByteBuffer;
import java.util.List;

import de.csdev.ebus.command.IEBusCommand.Type;

public interface IEBusCommandChannel {

    public IEBusCommand getParent();

    /**
     * Returns the type of this command
     *
     * @return
     */
    public Type getType();

    /**
     * Returns defined destination address or null if not defined
     *
     * @return
     */
    public Byte getDestinationAddress();

    /**
     * Returns defined source address or null if not defined
     *
     * @return
     */
    public Byte getSourceAddress();

    /**
     * Returns the telegram mask
     *
     * @return
     */
    public ByteBuffer getMasterTelegramMask();

    public List<IEBusValue> getExtendCommandValue();

    /**
     * Get ordered list of eBus data types for the master part
     *
     * @return
     */
    public List<IEBusValue> getMasterTypes();

    /**
     * Get ordered list of eBus data types for the slave part
     *
     * @return
     */
    public List<IEBusValue> getSlaveTypes();

    /**
     * Returns the eBus command bytes
     *
     * @return
     */
    public byte[] getCommand();

}
