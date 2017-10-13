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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.command.datatypes.EBusTypeGeneric;
import de.csdev.ebus.command.datatypes.IEBusType;
import de.csdev.ebus.command.datatypes.std.EBusTypeBCD;
import de.csdev.ebus.utils.EBusDateTime;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusTypeDateTime extends EBusTypeGeneric<EBusDateTime> {

    public static String TYPE_DATETIME = "datetime";

    public static String DATE = "date"; // BDA - 4

    public static String TIME = "time"; // BTI - 3

    public static String DATE_TIME = "datetime"; // BTI BDA - 7

    public static String DATE_SHORT = "date_short"; // BDA:3 -3

    public static String TIME_SHORT = "time_short"; // BTM -2

    private static String[] supportedTypes = new String[] { TYPE_DATETIME };

    private String variant = DATE_TIME;

    @Override
    public String[] getSupportedTypes() {

        return supportedTypes;
    }

    @Override
    public int getTypeLenght() {
        if (variant.equals(TYPE_DATETIME)) {
            return 7;
        } else if (variant.equals(DATE)) {
            return 4;
        } else if (variant.equals(DATE_SHORT)) {
            return 4;
        } else if (variant.equals(TIME)) {
            return 3;
        } else if (variant.equals(TIME_SHORT)) {
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

        IEBusType<BigDecimal> bcdType = types.getType(EBusTypeBCD.TYPE_BCD);
        Calendar calendar = new GregorianCalendar();

        BigDecimal sec = null;
        BigDecimal min = null;
        BigDecimal hr = null;

        BigDecimal day = null;
        BigDecimal month = null;
        BigDecimal year = null;

        boolean anyDate = false;
        boolean anyTime = false;

        if (data.length != getTypeLenght()) {
            throw new EBusTypeException(
                    String.format("Input byte array must have a length of %d bytes!", getTypeLenght()));
        }

        if (StringUtils.equals(variant, DATE_TIME)) {
            sec = bcdType.decode(new byte[] { data[0] });
            min = bcdType.decode(new byte[] { data[1] });
            hr = bcdType.decode(new byte[] { data[2] });
            day = bcdType.decode(new byte[] { data[3] });
            month = bcdType.decode(new byte[] { data[4] });
            year = bcdType.decode(new byte[] { data[6] });

        } else if (StringUtils.equals(variant, DATE_SHORT)) {
            day = bcdType.decode(new byte[] { data[0] });
            month = bcdType.decode(new byte[] { data[1] });
            year = bcdType.decode(new byte[] { data[2] });
            anyTime = true;

        } else if (StringUtils.equals(variant, DATE)) {
            day = bcdType.decode(new byte[] { data[0] });
            month = bcdType.decode(new byte[] { data[1] });
            year = bcdType.decode(new byte[] { data[3] });
            anyTime = true;

        } else if (StringUtils.equals(variant, TIME)) {
            sec = bcdType.decode(new byte[] { data[0] });
            min = bcdType.decode(new byte[] { data[1] });
            hr = bcdType.decode(new byte[] { data[2] });
            anyDate = true;

        } else if (StringUtils.equals(variant, TIME_SHORT)) {
            min = bcdType.decode(new byte[] { data[0] });
            hr = bcdType.decode(new byte[] { data[1] });
            anyDate = true;
        }

        if (hr != null) {
            calendar.set(Calendar.HOUR_OF_DAY, hr.intValue());
        }
        if (min != null) {
            calendar.set(Calendar.MINUTE, min.intValue());
        }
        if (sec != null) {
            calendar.set(Calendar.SECOND, sec.intValue());
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

        return new EBusDateTime(calendar, anyDate, anyTime);
    }

    @Override
    public byte[] encode(Object data) throws EBusTypeException {

        IEBusType<BigDecimal> bcdType = types.getType(EBusTypeBCD.TYPE_BCD);
        Calendar calendar = null;
        byte[] result = new byte[this.getTypeLenght()];

        if (data instanceof EBusDateTime) {
            calendar = ((EBusDateTime) data).getCalendar();

        } else if (data instanceof Calendar) {
            calendar = (Calendar) data;
        }

        if (calendar != null) {
            if (StringUtils.equals(variant, DATE_TIME)) {

                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                dayOfWeek = dayOfWeek == 1 ? 7 : dayOfWeek - 1;

                result = new byte[] { bcdType.encode(calendar.get(Calendar.SECOND))[0],
                        bcdType.encode(calendar.get(Calendar.MINUTE))[0],
                        bcdType.encode(calendar.get(Calendar.HOUR_OF_DAY))[0],
                        bcdType.encode(calendar.get(Calendar.DAY_OF_MONTH))[0],
                        bcdType.encode(calendar.get(Calendar.MONTH) + 1)[0], bcdType.encode(dayOfWeek)[0],
                        bcdType.encode(calendar.get(Calendar.YEAR) % 100)[0] };

            } else if (StringUtils.equals(variant, DATE)) {

                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                dayOfWeek = dayOfWeek == 1 ? 7 : dayOfWeek - 1;

                result = new byte[] { bcdType.encode(calendar.get(Calendar.DAY_OF_MONTH))[0],
                        bcdType.encode(calendar.get(Calendar.MONTH) + 1)[0], bcdType.encode(dayOfWeek)[0],
                        bcdType.encode(calendar.get(Calendar.YEAR) % 100)[0] };

            } else if (StringUtils.equals(variant, DATE_SHORT)) {

                result = new byte[] { bcdType.encode(calendar.get(Calendar.DAY_OF_MONTH))[0],
                        bcdType.encode(calendar.get(Calendar.MONTH) + 1)[0],
                        bcdType.encode(calendar.get(Calendar.YEAR) % 100)[0] };

            } else if (StringUtils.equals(variant, TIME)) {
                result = new byte[] { bcdType.encode(calendar.get(Calendar.SECOND))[0],
                        bcdType.encode(calendar.get(Calendar.MINUTE))[0],
                        bcdType.encode(calendar.get(Calendar.HOUR_OF_DAY))[0] };

            } else if (StringUtils.equals(variant, TIME_SHORT)) {
                result = new byte[] { bcdType.encode(calendar.get(Calendar.MINUTE))[0],
                        bcdType.encode(calendar.get(Calendar.HOUR_OF_DAY))[0] };
            }
        }

        return result;
    }

    @Override
    public IEBusType<EBusDateTime> getInstance(Map<String, Object> properties) {

        if (properties.containsKey(IEBusType.TYPE)) {
            EBusTypeDateTime type = new EBusTypeDateTime();
            type.variant = (String) properties.get(IEBusType.TYPE);
            type.types = this.types;
            return type;
        }

        return this;
    }

    @Override
    public String toString() {
        return "EBusTypeDateTime [variant=" + variant + "]";
    }

}
