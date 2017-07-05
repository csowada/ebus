package de.csdev.ebus.basic;

import org.junit.Test;

import de.csdev.ebus.core.EBusReceiveStateMachine;
import de.csdev.ebus.utils.EBusUtils;

public class EBusStateMachineTest {
	
	@Test
	public void xxx() {
		
		byte[] byteArray = EBusUtils.toByteArray("AA 30 08 50 22 03 CC 1A 27 59 00 02 97 00 E2 00 AA");
		
		EBusReceiveStateMachine machine = new EBusReceiveStateMachine();
		
		for (byte b : byteArray) {
			machine.update(b);
		}
		
	}
	
}
