package de.csdev.ebus.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EBusDateTime {

    private Calendar calendar;
    private boolean anyDate = false;
    private boolean anyTime = false;

    public EBusDateTime(Calendar calendar, boolean anyDate, boolean anyTime) {
        this.calendar = calendar;
        this.anyDate = anyDate;
        this.anyTime = anyTime;

    }

    public Calendar getCalendar() {
        return calendar;
    }

    public boolean isAnyDate() {
        return anyDate;
    }

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
            format = new SimpleDateFormat("DD.MM.YYYY");
        } else {
            format = new SimpleDateFormat("DD.MM.YYYY HH:mm:ss");
        }

        return format.format(calendar.getTime());
    }

}
