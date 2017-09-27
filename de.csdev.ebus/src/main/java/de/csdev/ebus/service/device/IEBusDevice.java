/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.service.device;

import java.math.BigDecimal;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public interface IEBusDevice {

    /**
     * The eBUS Master Address
     *
     * @return
     */
    public Byte getMasterAddress();

    /**
     * The eBUS Slave Address
     *
     * @return
     */
    public Byte getSlaveAddress();

    /**
     * Returns the identifier of the manufacturer of the eBUS device
     *
     * @return
     */
    public Byte getManufacturer();

    /**
     * Returns the identifier of the manufacturer of the eBUS device
     *
     * @return
     */
    public String getManufacturerName();

    /**
     * Returns the device ID
     *
     * @return
     */
    public byte[] getDeviceId();

    /**
     * Returns the software version of the eBUS device
     *
     * @return
     */
    public BigDecimal getSoftwareVersion();

    /**
     * Returns the hardware version of the eBUS device
     *
     * @return
     */
    public BigDecimal getHardwareVersion();

    /**
     * Returns the last activity of the device as 1970 millis
     *
     * @return
     */
    public long getLastActivity();

}
