package de.csdev.ebus.cfg;

import java.math.BigDecimal;
import java.util.Map;

import javax.script.CompiledScript;

public interface IEBusConfigurationValue {

    /**
     * Returns a bit of
     *
     * @return
     */
    public Integer getBit();

    /**
     * Returns compiled script
     *
     * @return
     */
    public CompiledScript getCsript();

    /**
     * Get debug string
     *
     * @return
     */
    public String getDebug();

    /**
     * Returns factor
     *
     * @return
     */
    public BigDecimal getFactor();

    /**
     * Returns label of value
     *
     * @return
     */
    public String getLabel();

    /**
     * Returns mapping to this value
     *
     * @return
     */
    public Map<String, String> getMapping();

    /**
     * Get max value
     *
     * @return
     */
    public BigDecimal getMax();

    /**
     * Get min value
     *
     * @return
     */
    public BigDecimal getMin();

    /**
     * Get telegram position for this value
     *
     * @return
     */
    public Integer getPos();

    /**
     * Get replace value
     *
     * @return
     */
    public BigDecimal getReplaceValue();

    /**
     * Returns uncompiled script
     *
     * @return
     */
    public String getScript();

    /**
     * Returns the step wide for this value
     *
     * @return
     */
    public String getStep();

    /**
     * Returns the value eBus type
     *
     * @return
     */
    public String getType();

    /**
     * Returns a hint for documentation
     *
     * @return
     */
    public String getTypeHint();

}
