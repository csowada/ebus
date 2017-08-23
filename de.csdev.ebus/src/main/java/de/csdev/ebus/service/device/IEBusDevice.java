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

    public byte getMasterAddress();

    public byte getSlaveAddress();

    public byte getManufacturer();

    public String getDeviceId();

    public BigDecimal getSoftwareVersion();

    public BigDecimal getHardwareVersion();

    public long getLastActivity();

}
