/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author Christian Sowada
 *
 */
public interface IEBusCommand {

    public enum Type {
        READ,
        WRITE
    }
	
    public String getDevice();
    
    public Byte getDestinationAddress();
    
    public Byte getSourceAddress();
    
	public ByteBuffer getMasterTelegramMask();
	
	public List<IEBusValue> getExtendCommandValue();
	
	public List<IEBusValue> getMasterTypes();
	
	public List<IEBusValue> getSlaveTypes();
	
	public byte[] getCommand();
	
	public String getConfigurationSource();
	
	public String getDescription();
	
	public String getId();
	
	public Type getType();
	
}
