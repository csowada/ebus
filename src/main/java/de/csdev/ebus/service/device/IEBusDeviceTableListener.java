/**
 * Copyright (c) 2016-2020 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.service.device;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
@NonNullByDefault
public interface IEBusDeviceTableListener {

    public enum TYPE {

        /** New device */
        NEW,

        /** Device data updated */
        UPDATE,

        /** Last activity updated */
        UPDATE_ACTIVITY,

        /** Device removed */
        REMOVED
    }

    /**
     * An eBUS device has been updated
     *
     * @param type
     * @param device
     */
    public void onEBusDeviceUpdate(TYPE type, IEBusDevice device);

}
