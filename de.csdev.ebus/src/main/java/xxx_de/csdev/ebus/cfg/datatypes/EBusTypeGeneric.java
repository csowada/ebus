package xxx_de.csdev.ebus.cfg.datatypes;

public abstract class EBusTypeGeneric implements IEBusType {

	protected EBusTypes types;

	@Override
	public void setTypesParent(EBusTypes types) {
		this.types = types;
	}

}
