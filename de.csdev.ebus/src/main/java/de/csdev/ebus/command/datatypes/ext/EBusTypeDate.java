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
public class EBusTypeDate extends EBusTypeGenericVariant<EBusDateTime> {

    public static String DATE = "date";

    public static String DEFAULT = "std"; // BDA - 4

    public static String SHORT = "short"; // BDA:3 - 3

    public static String HEX = "hex"; // BDA:3 - 4

    public static String HEX_SHORT = "hex_short"; // BDA:3 - 3

    public static String DAYS = "days"; // DAY - 2

    private static String[] supportedTypes = new String[] { DATE };

    // private String variant = STD;

    @Override
    public String[] getSupportedTypes() {

        return supportedTypes;
    }

    @Override
    public int getTypeLenght() {
        if (variant.equals(DEFAULT)) {
            return 4;
        } else if (variant.equals(HEX)) {
            return 4;
        } else if (variant.equals(SHORT)) {
            return 3;
        } else if (variant.equals(HEX_SHORT)) {
            return 3;
        } else if (variant.equals(DAYS)) {
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

        IEBusType<BigDecimal> bcdType = types.getType(EBusTypeBCD.BCD);
        IEBusType<BigDecimal> wordType = types.getType(EBusTypeWord.WORD);
        IEBusType<BigDecimal> charType = types.getType(EBusTypeChar.CHAR);

        Calendar calendar = new GregorianCalendar();

        BigDecimal day = null;
        BigDecimal month = null;
        BigDecimal year = null;

        if (data.length != getTypeLenght()) {
            throw new EBusTypeException(
                    String.format("Input byte array must have a length of %d bytes!", getTypeLenght()));
        }

        if (StringUtils.equals(variant, SHORT)) {
            day = bcdType.decode(new byte[] { data[0] });
            month = bcdType.decode(new byte[] { data[1] });
            year = bcdType.decode(new byte[] { data[2] });

        } else if (StringUtils.equals(variant, DEFAULT)) {
            day = bcdType.decode(new byte[] { data[0] });
            month = bcdType.decode(new byte[] { data[1] });
            year = bcdType.decode(new byte[] { data[3] });

        } else if (StringUtils.equals(variant, HEX)) {
            day = charType.decode(new byte[] { data[0] });
            month = charType.decode(new byte[] { data[1] });
            year = charType.decode(new byte[] { data[2] });

        } else if (StringUtils.equals(variant, HEX_SHORT)) {
            day = charType.decode(new byte[] { data[0] });
            month = charType.decode(new byte[] { data[1] });
            year = charType.decode(new byte[] { data[3] });

        } else if (StringUtils.equals(variant, DAYS)) {
            BigDecimal daysSince1900 = wordType.decode(data);
            calendar.set(1900, 0, 1, 0, 0);
            calendar.add(Calendar.DAY_OF_YEAR, daysSince1900.intValue());
        }

        if (day != null) {
            calendar.set(Calendar.DAY_OF_MONTH, day.intValue());
        }
        if (month != null) {
            calendar.set(Calendar.MONTH, month.intValue() - 1);
        }
        if (year != null) {
            if (year.intValue() < 70) {
                year = year.add(new BigDecimal(2000));
            } else {
                year = year.add(new BigDecimal(1900));
            }
            calendar.set(Calendar.YEAR, year.intValue());
        }

        return new EBusDateTime(calendar, false, true);
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

        // set date to midnight
        calendar = (Calendar) calendar.clone();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (calendar != null) {
            if (StringUtils.equals(variant, DEFAULT)) {

                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                dayOfWeek = dayOfWeek == 1 ? 7 : dayOfWeek - 1;

                result = new byte[] { bcdType.encode(calendar.get(Calendar.DAY_OF_MONTH))[0],
                        bcdType.encode(calendar.get(Calendar.MONTH) + 1)[0], bcdType.encode(dayOfWeek)[0],
                        bcdType.encode(calendar.get(Calendar.YEAR) % 100)[0] };

            } else if (StringUtils.equals(variant, SHORT)) {

                result = new byte[] { bcdType.encode(calendar.get(Calendar.DAY_OF_MONTH))[0],
                        bcdType.encode(calendar.get(Calendar.MONTH) + 1)[0],
                        bcdType.encode(calendar.get(Calendar.YEAR) % 100)[0] };

            } else if (StringUtils.equals(variant, HEX)) {

                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                dayOfWeek = dayOfWeek == 1 ? 7 : dayOfWeek - 1;

                result = new byte[] { charType.encode(calendar.get(Calendar.DAY_OF_MONTH))[0],
                        charType.encode(calendar.get(Calendar.MONTH) + 1)[0], charType.encode(dayOfWeek)[0],
                        charType.encode(calendar.get(Calendar.YEAR) % 100)[0] };

            } else if (StringUtils.equals(variant, HEX_SHORT)) {

                result = new byte[] { charType.encode(calendar.get(Calendar.DAY_OF_MONTH))[0],
                        charType.encode(calendar.get(Calendar.MONTH) + 1)[0],
                        charType.encode(calendar.get(Calendar.YEAR) % 100)[0] };

            } else if (StringUtils.equals(variant, DAYS)) {

                long millis = calendar.getTimeInMillis();

                calendar.clear();
                calendar.set(1900, 0, 1, 0, 0);
                long millis1900 = calendar.getTimeInMillis();

                BigDecimal days = new BigDecimal(millis - millis1900);
                days = days.divide(BigDecimal.valueOf(86400000), 0, RoundingMode.HALF_UP);

                result = wordType.encode(days);
            }
        }

        return result;
    }

    // @Override
    // public IEBusType<EBusDateTime> getInstance(Map<String, Object> properties) {
    //
    // if (properties.containsKey(IEBusType.TYPE)) {
    // EBusTypeDate type = new EBusTypeDate();
    // type.variant = (String) properties.get(IEBusType.TYPE);
    // type.types = this.types;
    // return type;
    // }
    //
    // return this;
    // }

    @Override
    public String toString() {
        return "EBusTypeDateTime [variant=" + variant + "]";
    }

}
