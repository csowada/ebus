package de.csdev.ebus.cfg.datatypes.ext;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.csdev.ebus.cfg.datatypes.EBusTypeBCD;
import de.csdev.ebus.cfg.datatypes.EBusTypeGeneric;
import de.csdev.ebus.cfg.datatypes.IEBusType;
import de.csdev.ebus.utils.NumberUtils;

public class EBusTypeTime extends EBusTypeGeneric {

	public static String TIME = "time";

	private static String[] supportedTypes = new String[] { TIME };

	private String variant = "";

	public String[] getSupportedTypes() {
		return supportedTypes;
	}

	@Override
	public int getTypeLenght() {
		return 3;
	}

	@SuppressWarnings("unchecked")
	public <T> T decode(byte[] data) {

		if(data == null) {
			// TODO replace value
		}

		if(StringUtils.equals(variant, "xxxx")) {
			
			return (T) "";
		} else {
			BigDecimal sec = NumberUtils.toBigDecimal(types.decode(EBusTypeBCD.BCD, new byte[]{data[2]}));
			BigDecimal min = NumberUtils.toBigDecimal(types.decode(EBusTypeBCD.BCD, new byte[]{data[1]}));
			BigDecimal hr = NumberUtils.toBigDecimal(types.decode(EBusTypeBCD.BCD, new byte[]{data[0]}));

			Calendar x = new GregorianCalendar();
			x.set(Calendar.HOUR_OF_DAY, hr.intValue());
			x.set(Calendar.MINUTE, min.intValue());
			x.set(Calendar.SECOND, sec.intValue());
			
			return (T) x;
			
		}
	}

	public byte[] encode(Object data) {
		return new byte[this.getTypeLenght()];
	}

	@Override
	public IEBusType getInstance(Map<String, Object> properties) {
		return super.getInstance(properties);
	}

}
