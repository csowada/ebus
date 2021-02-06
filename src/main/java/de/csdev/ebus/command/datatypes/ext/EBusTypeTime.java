/**
 * Copyright (c) 2017-2021 by the respective copyright holders.
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
import org.eclipse.jdt.annotation.Nullable;

import de.csdev.ebus.command.datatypes.EBusAbstractType;
import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.command.datatypes.IEBusType;
import de.csdev.ebus.command.datatypes.std.EBusTypeBCD;
import de.csdev.ebus.command.datatypes.std.EBusTypeChar;
import de.csdev.ebus.command.datatypes.std.EBusTypeUnsignedNumber;
import de.csdev.ebus.command.datatypes.std.EBusTypeWord;
import de.csdev.ebus.utils.EBusDateTime;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusTypeTime extends EBusAbstractType<EBusDateTime> {

    public static final String TYPE_TIME = "time";

    public static final String DEFAULT = "std"; // BTI - 3
    public static final String HEX = "hex"; // HTI - 3

    public static final String SHORT = "short"; // BTM - 2
    public static final String HEX_SHORT = "hex_short"; // HTM - 2

    public static final String MINUTES = "minutes"; // MIN - 2
    public static final String MINUTES_SHORT = "minutes_short"; // MIN - 1

    public static final String MINUTE_MULTIPLIER = "minuteMultiplier";
    
    private static String[] supportedTypes = new String[] { TYPE_TIME };

    private String variant = DEFAULT;

    private BigDecimal minuteMultiplier = BigDecimal.valueOf(1);

    @Override
    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @Override
    public int getTypeLength() {
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
        } else if (variant.equals(MINUTES_SHORT)) {
            return 1;
        }
        return 0;
    }

    @Override
    public EBusDateTime decodeInt(byte @Nullable [] data) throws EBusTypeException {

        if (data == null) {
            throw new IllegalArgumentException();
        }

        Calendar calendar = new GregorianCalendar(1970, 0, 1, 0, 0, 0);

        BigDecimal second = null;
        BigDecimal minute = null;
        BigDecimal hour = null;

        if (data.length != getTypeLength()) {
            throw new EBusTypeException("Input byte array must have a length of %d bytes!", getTypeLength());
        }

        if (StringUtils.equals(variant, SHORT)) {
            IEBusType<BigDecimal> bcdType = types.getType(EBusTypeBCD.TYPE_BCD);
            minute = bcdType.decode(new byte[] { data[0] });
            hour = bcdType.decode(new byte[] { data[1] });

        } else if (StringUtils.equals(variant, DEFAULT)) {
            IEBusType<BigDecimal> bcdType = types.getType(EBusTypeBCD.TYPE_BCD);
            second = bcdType.decode(new byte[] { data[0] });
            minute = bcdType.decode(new byte[] { data[1] });
            hour = bcdType.decode(new byte[] { data[2] });

        } else if (StringUtils.equals(variant, HEX)) {
            IEBusType<BigDecimal> charType = types.getType(EBusTypeChar.TYPE_CHAR);
            second = charType.decode(new byte[] { data[0] });
            minute = charType.decode(new byte[] { data[1] });
            hour = charType.decode(new byte[] { data[2] });

        } else if (StringUtils.equals(variant, HEX_SHORT)) {
            IEBusType<BigDecimal> charType = types.getType(EBusTypeChar.TYPE_CHAR);
            minute = charType.decode(new byte[] { data[0] });
            hour = charType.decode(new byte[] { data[1] });

        } else if (StringUtils.equals(variant, MINUTES) || StringUtils.equals(variant, MINUTES_SHORT)) {

            BigDecimal minutesSinceMidnight = null;
            if (StringUtils.equals(variant, MINUTES_SHORT)) {
                IEBusType<BigDecimal> type = types.getType(EBusTypeUnsignedNumber.TYPE_UNUMBER, IEBusType.LENGTH, 1);
                minutesSinceMidnight = type.decode(data);
            } else {
                IEBusType<BigDecimal> type = types.getType(EBusTypeUnsignedNumber.TYPE_UNUMBER, IEBusType.LENGTH, 2);
                minutesSinceMidnight = type.decode(data);
            }

            if (minutesSinceMidnight == null) {
                throw new EBusTypeException("Unable to computed minutes since midnight!");

            }
            minutesSinceMidnight = minutesSinceMidnight.multiply(minuteMultiplier);

            if (minutesSinceMidnight.intValue() > 1440) {
                throw new EBusTypeException("Value 'minutes since midnight' to large!");
            }

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

    @Override
    public byte[] encodeInt(@Nullable Object data) throws EBusTypeException {

        IEBusType<BigDecimal> bcdType = types.getType(EBusTypeBCD.TYPE_BCD);
        IEBusType<BigDecimal> wordType = types.getType(EBusTypeWord.TYPE_WORD);
        IEBusType<BigDecimal> charType = types.getType(EBusTypeChar.TYPE_CHAR);

        Calendar calendar = null;
        byte[] result = new byte[this.getTypeLength()];

        if (data instanceof EBusDateTime) {
            calendar = ((EBusDateTime) data).getCalendar();

        } else if (data instanceof Calendar) {
            calendar = (Calendar) data;
        }

        if (calendar == null) {
            return applyByteOrder(getReplaceValue());
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

            } else if (StringUtils.equals(variant, MINUTES) || StringUtils.equals(variant, MINUTES_SHORT)) {

                long millis = calendar.getTimeInMillis();

                calendar.clear();
                calendar.set(1970, 0, 1, 0, 0, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                long millisMidnight = calendar.getTimeInMillis();

                BigDecimal minutes = new BigDecimal(millis - millisMidnight);

                // milliseconds to minutes
                minutes = minutes.divide(BigDecimal.valueOf(1000 * 60), 0, RoundingMode.HALF_UP);

                // xxx
                minutes = minutes.divide(minuteMultiplier, 0, RoundingMode.HALF_UP);

                if (StringUtils.equals(variant, MINUTES_SHORT)) {
                    result = charType.encode(minutes);
                } else {
                    result = wordType.encode(minutes);
                }

            }
        }

        return result;
    }

    @Override
    public String toString() {
        return "EBusTypeTime [" + (variant != null ? "variant=" + variant + ", " : "") + "minuteMultiplier="
                + minuteMultiplier + "]";
    }

}
