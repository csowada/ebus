package xxx_de.csdev.ebus.cfg.datatypes;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EBusTypes {

	private static final Logger logger = LoggerFactory.getLogger(EBusTypes.class);
	
	private Map<String, IEBusType> types = null;
	
	public EBusTypes() {
		init();
	}
	
	protected void init() {
		types = new HashMap<String, IEBusType>();
		
		add(EBusTypeBit.class);
		add(EBusTypeByte.class);
		add(EBusTypeInteger.class);
		add(EBusTypeWord.class);
		add(EBusTypeBCD.class);
		add(EBusTypeData1b.class);
		add(EBusTypeData2b.class);
		add(EBusTypeData2c.class);
	}
	
	public byte[] encode(String type, Object data, Object... args) {
		IEBusType eBusType = types.get(type);
		
		if(eBusType == null) {
			logger.warn("No eBUS data type with name {} !", type);
			return null;
		}
		
		return eBusType.encode(data, args);
	}
	
	public <T> T decode(String type, byte[] data, Object... args) {
		IEBusType eBusType = types.get(type);
		
		if(eBusType == null) {
			logger.warn("No eBUS data type with name {} !", type);
			return null;
		}
		
		return eBusType.decode(data, args);
	}
	
	protected void add(Class<?> clazz) {
		try {
			IEBusType newInstance = (IEBusType) clazz.newInstance();
			newInstance.setTypesParent(this);
			
			for (String typeName : newInstance.getSupportedTypes()) {
				logger.info("Add eBUS type {}", typeName);
				types.put(typeName, newInstance);
			}
			
		} catch (InstantiationException e) {
			logger.error("error!", e);

		} catch (IllegalAccessException e) {
			logger.error("error!", e);
		}
	}
	
}
