/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command.datatypes.ext;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
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
public class EBusTypeDateTime extends EBusAbstractType<EBusDateTime> {

    private static final Logger logger = LoggerFactory.getLogger(EBusTypeDateTime.class);

    public static String TYPE_DATETIME = "datetime";

    private static String[] supportedTypes = new String[] { TYPE_DATETIME };

    public static String TIME_FIRST = "timeFirst";

    public static String VARIANT_DATE = "variantDate";

    public static String VARIANT_TIME = "variantTime";

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
    public EBusDateTime decodeInt(byte[] data) throws EBusTypeException {

        IEBusType<Object> dateType = getDateType();
        IEBusType<Object> timeType = getTimeType();

        byte[] timeData = null;
        byte[] dateData = null;

        if (timeFirst) {
            timeData = Arrays.copyOf(data, timeType.getTypeLength());
            dateData = Arrays.copyOfRange(data, timeData.length, timeData.length + dateType.getTypeLength());
        } else {
            dateData = Arrays.copyOf(data, dateType.getTypeLength());
            timeData = Arrays.copyOfRange(data, dateData.length, dateData.length + timeType.getTypeLength());
        }

        EBusDateTime time = (EBusDateTime) timeType.decode(timeData);
        EBusDateTime date = (EBusDateTime) dateType.decode(dateData);

        Calendar calendar = date.getCalendar();

        calendar.set(Calendar.HOUR_OF_DAY, time.getCalendar().get(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, time.getCalendar().get(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, time.getCalendar().get(Calendar.SECOND));

        return new EBusDateTime(calendar, false, false);
    }

    @Override
    public byte[] encodeInt(Object data) throws EBusTypeException {

        IEBusType<Object> dateType = getDateType();
        IEBusType<Object> timeType = getTimeType();

        Calendar calendar = null;
        byte[] result = new byte[this.getTypeLength()];

        if (data instanceof EBusDateTime) {
            calendar = ((EBusDateTime) data).getCalendar();

        } else if (data instanceof Calendar) {
            calendar = (Calendar) data;
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

    private IEBusType<Object> getDateType() {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(IEBusType.VARIANT, variantDate);
        return types.getType(EBusTypeDate.TYPE_DATE, properties);
    }

    @Override
    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    private IEBusType<Object> getTimeType() {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(IEBusType.VARIANT, variantTime);
        return types.getType(EBusTypeTime.TYPE_TIME, properties);
    }

    @Override
    public int getTypeLength() {
        IEBusType<Object> dateType = getDateType();
        IEBusType<Object> timeType = getTimeType();

        return dateType.getTypeLength() + timeType.getTypeLength();
    }

}
