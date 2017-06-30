/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command;

/**
 * @author Christian Sowada
 *
 */
public interface IEBusCommandWritable extends IEBusCommand {

	public IEBusCommandWritable addSlaveValue(IEBusValue value);
	
	public IEBusCommandWritable addMasterValue(IEBusValue value);
	
	public IEBusCommandWritable addExtendedCommand(IEBusValue value);

	public IEBusCommandWritable setType(Type type);

	public IEBusCommandWritable setId(String id);

	public IEBusCommandWritable setCommand(byte[] command);

	public void setConfigurationSource(String configurationSource);

	public void setDescription(String description);
	
}
