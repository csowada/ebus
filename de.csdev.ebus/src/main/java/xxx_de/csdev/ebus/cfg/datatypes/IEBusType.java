package xxx_de.csdev.ebus.cfg.datatypes;

public interface IEBusType {

	public <T> T decode(byte[] data, Object... args);
	
	public byte[] encode(Object data, Object... args);
	
	public String[] getSupportedTypes();

	public void setTypesParent(EBusTypes types);
}
