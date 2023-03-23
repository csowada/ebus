/**
 * Copyright (c) 2017-2023 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command.datatypes.ext;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.command.datatypes.EBusAbstractType;
import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.command.datatypes.IEBusType;
import de.csdev.ebus.utils.EBusDateTime;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
@NonNullByDefault
public class EBusTypeDateTime extends EBusAbstractType<EBusDateTime> {

    private static final Logger logger = LoggerFactory.getLogger(EBusTypeDateTime.class);

    public static final String TYPE_DATETIME = "datetime";

    public static final String TIME_FIRST = "timeFirst";

    public static final String VARIANT_DATE = "variantDate";

    public static final String VARIANT_TIME = "variantTime";

    private static String[] supportedTypes = new String[] { TYPE_DATETIME };

    private boolean timeFirst = true;

    private String variantDate = EBusTypeDate.DEFAULT;

    private String variantTime = EBusTypeTime.DEFAULT;

    @Override
    protected byte[] applyByteOrder(byte[] data) {
        if (reverseByteOrder) {
            logger.warn("Parameter 'reverseByteOrder' not supported for EBusTypeDateTime yet!");
        }
        return ArrayUtils.clone(data);
    }

    @Override
    public EBusDateTime decodeInt(byte @Nullable [] data) throws EBusTypeException {

        IEBusType<Object> dateType = getDateType();
        IEBusType<Object> timeType = getTimeType();

        if (dateType == null || timeType == null) {
            throw new EBusTypeException("Unable to get all required EBusTyp's type!");
        }

        byte[] timeData = null;
        byte[] dateData = null;

        boolean anyDate = false;
        boolean anyTime = false;

        if (timeFirst) {
            timeData = Arrays.copyOf(data, timeType.getTypeLength());
            dateData = Arrays.copyOfRange(data, timeData.length, timeData.length + dateType.getTypeLength());
        } else {
            dateData = Arrays.copyOf(data, dateType.getTypeLength());
            timeData = Arrays.copyOfRange(data, dateData.length, dateData.length + timeType.getTypeLength());
        }

        EBusDateTime time = (EBusDateTime) timeType.decode(timeData);
        EBusDateTime date = (EBusDateTime) dateType.decode(dateData);

        if (time == null) {
            logger.trace("The decoded time part of datetime is null!");
        }

        if (date == null) {
            logger.trace("The decoded date part of datetime is null!");
        }

        Calendar calendar = null;

        if (date == null) {
            anyDate = true;
            calendar = new GregorianCalendar(1970, 0, 1, 0, 0, 0);
        } else {
            calendar = date.getCalendar();
        }

        if (time == null) {
            anyTime = true;
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, time.getCalendar().get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, time.getCalendar().get(Calendar.MINUTE));
            calendar.set(Calendar.SECOND, time.getCalendar().get(Calendar.SECOND));
        }

        return new EBusDateTime(calendar, anyDate, anyTime);
    }

    @Override
    public byte[] encodeInt(@Nullable Object data) throws EBusTypeException {

        IEBusType<Object> dateType = getDateType();
        IEBusType<Object> timeType = getTimeType();

        if (dateType == null || timeType == null) {
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

        byte[] timeData = timeType.encode(calendar);
        byte[] dateData = dateType.encode(calendar);

        if (timeFirst) {
            System.arraycopy(timeData, 0, result, 0, timeData.length);
            System.arraycopy(dateData, 0, result, timeData.length, dateData.length);
        } else {
            System.arraycopy(dateData, 0, result, 0, dateData.length);
            System.arraycopy(timeData, 0, result, dateData.length, timeData.length);
        }

        return result;
    }

    private @Nullable IEBusType<Object> getDateType() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(IEBusType.VARIANT, variantDate);
        return types.getType(EBusTypeDate.TYPE_DATE, properties);
    }

    @Override
    public String @NonNull [] getSupportedTypes() {
        return supportedTypes;
    }

    private @Nullable IEBusType<Object> getTimeType() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(IEBusType.VARIANT, variantTime);
        return types.getType(EBusTypeTime.TYPE_TIME, properties);
    }

    @Override
    public int getTypeLength() {
        IEBusType<Object> dateType = getDateType();
        IEBusType<Object> timeType = getTimeType();

        if (dateType == null || timeType == null) {
            throw new IllegalStateException("Unable to get all required EBusTyp's type!");
        }

        return dateType.getTypeLength() + timeType.getTypeLength();
    }

    @Override
    public String toString() {
        return "EBusTypeDateTime [timeFirst=" + timeFirst + ", "
                + (variantDate != null ? "variantDate=" + variantDate + ", " : "")
                + (variantTime != null ? "variantTime=" + variantTime : "") + "]";
    }

}
