package de.csdev.ebus.aaa;

import de.csdev.ebus.cfg.EBusConfigurationProvider;
import de.csdev.ebus.core.EBusController;
import de.csdev.ebus.meta.EBusDeviceTable;

public class EBusHighLevelService {
	
	private EBusDeviceTable deviceTable;
	private EBusController controller;
	private EBusConfigurationProvider configurationProvider;
	private EBusParserService resolverService;
	private EBusDeviceTableService deviceTableService;

	public EBusHighLevelService(EBusController controller) {
		this.controller = controller;
		init();
	}
	
	private void init() {
		
		deviceTable = new EBusDeviceTable((byte)0xFF);
		configurationProvider = new EBusConfigurationProvider();
		
		resolverService = new EBusParserService(controller, configurationProvider);
		deviceTableService = new EBusDeviceTableService(controller, configurationProvider,	deviceTable);
		
		controller.addEBusEventListener(resolverService);
		resolverService.addEBusParserListener(deviceTableService);

	}

	public EBusController getController() {
		return controller;
	}

	public EBusConfigurationProvider getConfigurationProvider() {
		return configurationProvider;
	}

	public EBusParserService getResolverService() {
		return resolverService;
	}

	public EBusDeviceTableService getDeviceTableService() {
		return deviceTableService;
	}

	public EBusDeviceTable getDeviceTable() {
		return deviceTable;
	}
}
