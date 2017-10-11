/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command.datatypes.ext;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.commons.lang.StringUtils;

import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.command.datatypes.EBusTypeGenericVariant;
import de.csdev.ebus.command.datatypes.IEBusType;
import de.csdev.ebus.command.datatypes.std.EBusTypeBCD;
import de.csdev.ebus.command.datatypes.std.EBusTypeChar;
import de.csdev.ebus.command.datatypes.std.EBusTypeWord;
import de.csdev.ebus.utils.EBusDateTime;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusTypeTime extends EBusTypeGenericVariant<EBusDateTime> {

    public static String TIME = "time";

    public static String DEFAULT = "std"; // BTI - 3
    public static String HEX = "hex"; // HTI - 3

    public static String SHORT = "short"; // BTM - 2
    public static String HEX_SHORT = "hex_short"; // HTM - 2

    public static String MINUTES = "minutes"; // MIN - 2

    private static String[] supportedTypes = new String[] { TIME };

    // private String variant = STD;

    @Override
    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @Override
    public int getTypeLenght() {
        if (variant.equals(DEFAULT)) {
            return 3;
        } else if (variant.equals(HEX)) {
            return 3;
        } else if (variant.equals(SHORT)) {
            return 2;
        } else if (variant.equals(HEX_SHORT)) {
            return 2;
        } else if (variant.equals(MINUTES)) {
            return 2;
        }
        return 0;
    }

    @Override
    public EBusDateTime decode(byte[] data) throws EBusTypeException {

        if (data == null) {
            // TODO replace value
            return null;
        }

        data = applyByteOrder(data);

        IEBusType<BigDecimal> bcdType = types.getType(EBusTypeBCD.BCD);
        IEBusType<BigDecimal> wordType = types.getType(EBusTypeWord.WORD);
        IEBusType<BigDecimal> charType = types.getType(EBusTypeChar.CHAR);

        Calendar calendar = new GregorianCalendar(1970, 0, 1, 0, 0, 0);

        BigDecimal second = null;
        BigDecimal minute = null;
        BigDecimal hour = null;

        if (data.length != getTypeLenght()) {
            throw new EBusTypeException(
                    String.format("Input byte array must have a length of %d bytes!", getTypeLenght()));
        }

        if (StringUtils.equals(variant, SHORT)) {
            minute = bcdType.decode(new byte[] { data[0] });
            hour = bcdType.decode(new byte[] { data[1] });

        } else if (StringUtils.equals(variant, DEFAULT)) {
            second = bcdType.decode(new byte[] { data[0] });
            minute = bcdType.decode(new byte[] { data[1] });
            hour = bcdType.decode(new byte[] { data[2] });

        } else if (StringUtils.equals(variant, HEX)) {
            second = charType.decode(new byte[] { data[0] });
            minute = charType.decode(new byte[] { data[1] });
            hour = charType.decode(new byte[] { data[2] });

        } else if (StringUtils.equals(variant, HEX_SHORT)) {
            minute = charType.decode(new byte[] { data[0] });
            hour = charType.decode(new byte[] { data[1] });

        } else if (StringUtils.equals(variant, MINUTES)) {
            BigDecimal minutesSinceMidnight = wordType.decode(data);
            // calendar.set(1970, 0, 1, 0, 0, 0);
            // calendar.set(Calendar.MILLISECOND, 0);
            calendar.add(Calendar.MINUTE, minutesSinceMidnight.intValue());
        }

        if (second != null) {
            calendar.set(Calendar.SECOND, second.intValue());
        }
        if (minute != null) {
            calendar.set(Calendar.MINUTE, minute.intValue());
        }
        if (hour != null) {
            calendar.set(Calendar.HOUR_OF_DAY, hour.intValue());
        }

        return new EBusDateTime(calendar, true, false);
    }

    @SuppressWarnings("null")
    @Override
    public byte[] encode(Object data) throws EBusTypeException {

        IEBusType<BigDecimal> bcdType = types.getType(EBusTypeBCD.BCD);
        IEBusType<BigDecimal> wordType = types.getType(EBusTypeWord.WORD);
        IEBusType<BigDecimal> charType = types.getType(EBusTypeChar.CHAR);

        Calendar calendar = null;
        byte[] result = new byte[this.getTypeLenght()];

        if (data instanceof EBusDateTime) {
            calendar = ((EBusDateTime) data).getCalendar();

        } else if (data instanceof Calendar) {
            calendar = (Calendar) data;
        }

        // set date to 01.01.1970
        calendar = (Calendar) calendar.clone();
        calendar.set(1970, 0, 1);

        if (calendar != null) {
            if (StringUtils.equals(variant, DEFAULT)) {

                result = new byte[] { bcdType.encode(calendar.get(Calendar.SECOND))[0],
                        bcdType.encode(calendar.get(Calendar.MINUTE))[0],
                        bcdType.encode(calendar.get(Calendar.HOUR_OF_DAY))[0] };

            } else if (StringUtils.equals(variant, SHORT)) {

                result = new byte[] { bcdType.encode(calendar.get(Calendar.MINUTE))[0],
                        bcdType.encode(calendar.get(Calendar.HOUR_OF_DAY))[0] };

            } else if (StringUtils.equals(variant, HEX)) {

                result = new byte[] { charType.encode(calendar.get(Calendar.SECOND))[0],
                        charType.encode(calendar.get(Calendar.MINUTE))[0],
                        charType.encode(calendar.get(Calendar.HOUR_OF_DAY))[0] };

            } else if (StringUtils.equals(variant, HEX_SHORT)) {

                result = new byte[] { charType.encode(calendar.get(Calendar.MINUTE))[0],
                        charType.encode(calendar.get(Calendar.HOUR_OF_DAY))[0] };

            } else if (StringUtils.equals(variant, MINUTES)) {

                long millis = calendar.getTimeInMillis();

                calendar.clear();
                calendar.set(1970, 0, 1, 0, 0, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                long millisMidnight = calendar.getTimeInMillis();

                BigDecimal minutes = new BigDecimal(millis - millisMidnight);
                minutes = minutes.divide(BigDecimal.valueOf(1000), 0, RoundingMode.HALF_UP);

                result = wordType.encode(minutes);
            }
        }

        return applyByteOrder(result);
    }

    // @Override
    // public IEBusType<EBusDateTime> getInstance(Map<String, Object> properties) {
    //
    // if (properties == null || !properties.containsKey(IEBusType.TYPE)
    // || ObjectUtils.equals(properties.get(IEBusType.TYPE), STD)) {
    // return super.getInstance(properties);
    // }
    //
    // String key = (String) properties.get(IEBusType.TYPE) + ":" + isReverseByteOrderSet(properties);
    // EBusTypeTime type = (EBusTypeTime) otherInstances.get(key);
    // if (type == null) {
    // type = (EBusTypeTime) createNewInstance();
    // type.variant = (String) properties.get(IEBusType.TYPE);
    // applyNewInstanceProperties(type, properties);
    //
    // otherInstances.put(key, type);
    // }
    //
    // return type;
    // }

    @Override
    public String toString() {
        return "EBusTypeDateTime [variant=" + variant + "]";
    }

}
