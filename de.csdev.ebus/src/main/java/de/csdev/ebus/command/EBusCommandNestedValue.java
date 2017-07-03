package de.csdev.ebus.command;

import java.util.ArrayList;
import java.util.List;

public class EBusCommandNestedValue extends EBusCommandValue implements IEBusNestedValue {

	private List<IEBusValue> list;
	
	public boolean hasChildren() {
		
		return list != null && !list.isEmpty();
	}

	public List<IEBusValue> getChildren() {
		
		if(list == null)
			list = new ArrayList<IEBusValue>();
		
		return list;
	}

	public boolean add(IEBusValue value) {
		
		if(list == null)
			list = new ArrayList<IEBusValue>();
		
		return list.add(value);
	}
	
}
