/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command.datatypes.std;

import java.math.BigDecimal;

import de.csdev.ebus.command.datatypes.EBusAbtstractReplaceValueType;
import de.csdev.ebus.utils.EBusUtils;
import de.csdev.ebus.utils.NumberUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusTypeBCD extends EBusAbtstractReplaceValueType<BigDecimal>  {

	public static String TYPE_BCD = "bcd";

	private static String[] supportedTypes = new String[] { TYPE_BCD };

	public EBusTypeBCD() {
		replaceValue = new byte[] {(byte) 0xFF};	
	}

	@Override
	public String[] getSupportedTypes() {
		return supportedTypes;
	}

	@Override
	public int getTypeLenght() {
		return 1;
	}

	@Override
	public BigDecimal decodeInt(byte[] data) {

		byte high = (byte) (data[0] >> 4 & 0x0F);
		byte low = (byte) (data[0] & 0x0F);

		// nibbles out of rang 0-9
		if(high > 9 || low > 9) {
			return null;
		}

		return BigDecimal.valueOf((byte) ((data[0] >> 4 & 0x0F) * 10 + (data[0] & 0x0F)));
	}

	@Override
	public byte[] encodeInt(Object data) {
		BigDecimal b = NumberUtils.toBigDecimal(data);
		return new byte[] { (byte) (((b.intValue() / 10) << 4) | b.intValue() % 10) };
	}

	@Override
	public String toString() {
		return "EBusTypeBCD [replaceValue=" + EBusUtils.toHexDumpString(replaceValue).toString() + "]";
	}

}
