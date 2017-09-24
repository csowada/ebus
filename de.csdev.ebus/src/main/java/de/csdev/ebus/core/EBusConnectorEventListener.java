package de.csdev.ebus.core;

public class EBusConnectorEventListener implements IEBusConnectorEventListener {

    /*
     * (non-Javadoc)
     * 
     * @see de.csdev.ebus.core.IEBusConnectorEventListener#onTelegramReceived(byte[], java.lang.Integer)
     */
    public void onTelegramReceived(byte[] receivedData, Integer sendQueueId) {
        // noop
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.csdev.ebus.core.IEBusConnectorEventListener#onTelegramException(de.csdev.ebus.core.EBusDataException,
     * java.lang.Integer)
     */
    public void onTelegramException(EBusDataException exception, Integer sendQueueId) {
        // noop
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.csdev.ebus.core.IEBusConnectorEventListener#onConnectionException(java.lang.Exception)
     */
    public void onConnectionException(Exception e) {
        // noop
    }
}
