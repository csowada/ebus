package de.csdev.ebus.command;

import java.util.List;

public interface IEBusNestedValue {

	public boolean hasChildren();
	
	public List<IEBusValue> getChildren();
	
}
