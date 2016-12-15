package de.csdev.ebus.cfg;

import java.util.Map;
import java.util.regex.Pattern;

public interface IEBusConfiguration {

    public Map<String, IEBusConfigurationValue> getValues();

    /**
     * The class of the eBus telegram
     *
     * @return
     */
    public String getClazz();

    /**
     * The command bytes of the eBus telegram
     *
     * @return
     */
    public String getCommand();

    /**
     * The comment of the eBus telegram
     *
     * @return
     */
    public String getComment();

    /**
     * The computed values of the eBus telegram (optional)
     *
     * @return
     */
    public Map<String, IEBusConfigurationValue> getComputedValues();

    /**
     * The data bytes of the eBus telegram
     *
     * @return
     */
    public String getData();

    /**
     * A debug flag for this telegram
     *
     * @return
     */
    public Integer getDebug();

    /**
     * The device that should work with this telegram
     *
     * @return
     */
    public String getDevice();

    /**
     * The destination byte of the eBus telegram
     *
     * @return
     */
    public String getDst();

    /**
     * The filter string (regex)
     *
     * @return
     */
    public String getFilter();

    /**
     * The compiled filter pattern
     *
     * @return
     */
    public Pattern getFilterPattern();

    /**
     * The ID of the eBus telegram
     *
     * @return
     */
    public String getId();

}
