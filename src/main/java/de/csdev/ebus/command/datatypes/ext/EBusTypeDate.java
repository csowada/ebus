/**
 * Copyright (c) 2017-2023 by the respective copyright holders.
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
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import de.csdev.ebus.command.datatypes.EBusAbstractType;
import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.command.datatypes.IEBusType;
import de.csdev.ebus.command.datatypes.std.EBusTypeBCD;
import de.csdev.ebus.command.datatypes.std.EBusTypeChar;
import de.csdev.ebus.command.datatypes.std.EBusTypeWord;
import de.csdev.ebus.utils.EBusDateTime;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
@NonNullByDefault
public class EBusTypeDate extends EBusAbstractType<EBusDateTime> {

    public static String TYPE_DATE = "date";

    public static String DEFAULT = "std"; // BDA - 4

    public static String SHORT = "short"; // BDA:3 - 3

    public static String HEX = "hex"; // BDA:3 - 4

    public static String HEX_SHORT = "hex_short"; // BDA:3 - 3

    public static String DAYS = "days"; // DAY - 2

    private static String[] supportedTypes = new String[] { TYPE_DATE };

    private String variant = DEFAULT;

    @Override
    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @Override
    public int getTypeLength() {
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
    @SuppressWarnings("java:S3776")
    public @Nullable EBusDateTime decodeInt(byte @Nullable [] data) throws EBusTypeException {

        Objects.requireNonNull(data);

        IEBusType<BigDecimal> bcdType = types.getType(EBusTypeBCD.TYPE_BCD);
        IEBusType<BigDecimal> wordType = types.getType(EBusTypeWord.TYPE_WORD);
        IEBusType<BigDecimal> charType = types.getType(EBusTypeChar.TYPE_CHAR);

        if (bcdType == null || wordType == null || charType == null ) {
            throw new EBusTypeException("Unable to get all required EBusTyp's types!");
        }

        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        BigDecimal day = null;
        BigDecimal month = null;
        BigDecimal year = null;

        if (data.length != getTypeLength()) {
            throw new EBusTypeException(
                    String.format("Input byte array must have a length of %d bytes!", getTypeLength()));
        }

        if (StringUtils.equals(variant, SHORT)) {
            day = bcdType.decode(new byte[] { data[0] });
            month = bcdType.decode(new byte[] { data[1] });
            year = bcdType.decode(new byte[] { data[2] });

        } else if (StringUtils.equals(variant, DEFAULT)) {
            day = bcdType.decode(new byte[] { data[0] });
            month = bcdType.decode(new byte[] { data[1] });
            year = bcdType.decode(new byte[] { data[3] });

        } else if (StringUtils.equals(variant, HEX_SHORT)) {
            day = charType.decode(new byte[] { data[0] });
            month = charType.decode(new byte[] { data[1] });
            year = charType.decode(new byte[] { data[2] });

        } else if (StringUtils.equals(variant, HEX)) {
            day = charType.decode(new byte[] { data[0] });
            month = charType.decode(new byte[] { data[1] });
            year = charType.decode(new byte[] { data[3] });

        } else if (StringUtils.equals(variant, DAYS)) {
            BigDecimal daysSince1900 = wordType.decode(data);

            if (daysSince1900 == null) {
                throw new EBusTypeException("Unable to compute days since 1990!");
            }

            calendar.set(1900, 0, 1, 0, 0);
            calendar.add(Calendar.DAY_OF_YEAR, daysSince1900.intValue());
        }

        if (day != null && month != null && year != null) {
            if (day.intValue() < 1 || day.intValue() > 31) {
                throw new EBusTypeException("A valid day must be in a range between 1-31 !");
            }
            if (month.intValue() < 1 || month.intValue() > 12) {
                throw new EBusTypeException("A valid day must be in a range between 1-12 !");
            }
        }

        if (year != null) {
            if (year.intValue() < 70) {
                year = year.add(new BigDecimal(2000));
            } else {
                year = year.add(new BigDecimal(1900));
            }
            calendar.set(Calendar.YEAR, year.intValue());
        }

        if (month != null) {
            calendar.set(Calendar.MONTH, month.intValue() - 1);
        }

        if (day != null && day.intValue() > 0 && day.intValue() < 32) {
            calendar.set(Calendar.DAY_OF_MONTH, day.intValue());
        }

        return new EBusDateTime(calendar, false, true);
    }

    @Override
    public byte @Nullable [] encodeInt(@Nullable Object data) throws EBusTypeException {

        IEBusType<BigDecimal> bcdType = types.getType(EBusTypeBCD.TYPE_BCD);
        IEBusType<BigDecimal> wordType = types.getType(EBusTypeWord.TYPE_WORD);
        IEBusType<BigDecimal> charType = types.getType(EBusTypeChar.TYPE_CHAR);

        if (bcdType == null || wordType == null || charType == null) {
            throw new EBusTypeException("Unable to get all required EBusTyp's type!");
        }

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

        // set date to midnight
        calendar = (Calendar) calendar.clone();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);


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
        

        return result;
    }

    @Override
    public String toString() {
        return "EBusTypeDate [variant=" + variant + "]";
    }

}
