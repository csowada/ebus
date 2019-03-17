package de.csdev.ebus.core;

public interface IEBusController {

    Integer addToSendQueue(byte[] buffer, int maxAttemps) throws EBusControllerException;

    /**
     * @param buffer
     * @return
     * @throws EBusControllerException
     */
    Integer addToSendQueue(byte[] buffer) throws EBusControllerException;

    /**
     * Add an eBUS listener to receive valid eBus telegrams
     *
     * @param listener
     */
    void addEBusEventListener(IEBusConnectorEventListener listener);

    /**
     * Remove an eBUS listener
     *
     * @param listener
     * @return
     */
    boolean removeEBusEventListener(IEBusConnectorEventListener listener);

    boolean isRunning();

    void setWatchdogTimerTimeout(int seconds);

    long getLastSendReceiveRoundtripTime();

    void start();

    boolean isInterrupted();

    void interrupt();
}