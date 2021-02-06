/**
 * Copyright (c) 2017-2021 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusDateTime {

    private Calendar calendar;
    private boolean anyDate = false;
    private boolean anyTime = false;

    /**
     * Constructor
     *
     * @param calendar
     * @param anyDate Set date part to any date
     * @param anyTime Set time part to any time
     */
    public EBusDateTime(final Calendar calendar, boolean anyDate, boolean anyTime) {
        this.calendar = calendar;
        this.anyDate = anyDate;
        this.anyTime = anyTime;
    }

    /**
     * Returns a calendar object, take notice of the any date flags.
     *
     * @return
     */
    public Calendar getCalendar() {
        return calendar;
    }

    /**
     * Ignore the date in the calendar object
     *
     * @return
     */
    public boolean isAnyDate() {
        return anyDate;
    }

    /**
     * Ignore the time in the calendar object
     *
     * @return
     */
    public boolean isAnyTime() {
        return anyTime;
    }

    @Override
    public String toString() {

        SimpleDateFormat format = null;
        if (calendar == null) {
            return "<null>";
        }

        if (anyDate && anyTime) {
            return "<any>";
        }

        if (anyDate) {
            format = new SimpleDateFormat("HH:mm:ss");
        } else if (anyTime) {
            format = new SimpleDateFormat("dd.MM.yyyy");
        } else {
            format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        }

        return format.format(calendar.getTime());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (anyDate ? 1231 : 1237);
        result = prime * result + (anyTime ? 1231 : 1237);
        result = prime * result + ((calendar == null) ? 0 : calendar.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        EBusDateTime other = (EBusDateTime) obj;
        if (anyDate != other.anyDate) {
            return false;
        }
        if (anyTime != other.anyTime) {
            return false;
        }
        if (calendar == null) {
            if (other.calendar != null) {
                return false;
            }
        } else if (!calendar.equals(other.calendar)) {
            return false;
        }
        return true;
    }

}
