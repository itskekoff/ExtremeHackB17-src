package org.apache.commons.lang3.time;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.apache.commons.lang3.exception.ExceptionUtils;

class CalendarReflection {
    private static final Method IS_WEEK_DATE_SUPPORTED = CalendarReflection.getCalendarMethod("isWeekDateSupported", new Class[0]);
    private static final Method GET_WEEK_YEAR = CalendarReflection.getCalendarMethod("getWeekYear", new Class[0]);

    CalendarReflection() {
    }

    private static Method getCalendarMethod(String methodName, Class<?> ... argTypes) {
        try {
            Method m2 = Calendar.class.getMethod(methodName, argTypes);
            return m2;
        }
        catch (Exception e2) {
            return null;
        }
    }

    static boolean isWeekDateSupported(Calendar calendar) {
        try {
            return IS_WEEK_DATE_SUPPORTED != null && (Boolean)IS_WEEK_DATE_SUPPORTED.invoke(calendar, new Object[0]) != false;
        }
        catch (Exception e2) {
            return (Boolean)ExceptionUtils.rethrow(e2);
        }
    }

    public static int getWeekYear(Calendar calendar) {
        try {
            if (CalendarReflection.isWeekDateSupported(calendar)) {
                return (Integer)GET_WEEK_YEAR.invoke(calendar, new Object[0]);
            }
        }
        catch (Exception e2) {
            return (Integer)ExceptionUtils.rethrow(e2);
        }
        int year = calendar.get(1);
        if (IS_WEEK_DATE_SUPPORTED == null && calendar instanceof GregorianCalendar) {
            switch (calendar.get(2)) {
                case 0: {
                    if (calendar.get(3) < 52) break;
                    --year;
                    break;
                }
                case 11: {
                    if (calendar.get(3) != 1) break;
                    ++year;
                }
            }
        }
        return year;
    }
}

