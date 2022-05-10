package com.ibm.icu.text;

import com.ibm.icu.text.ChineseDateFormatSymbols;
import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.DisplayContext;
import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.ChineseCalendar;
import com.ibm.icu.util.TimeZone;
import com.ibm.icu.util.ULocale;
import java.io.InvalidObjectException;
import java.text.FieldPosition;
import java.util.Locale;

public class ChineseDateFormat
extends SimpleDateFormat {
    static final long serialVersionUID = -4610300753104099899L;

    public ChineseDateFormat(String pattern, Locale locale) {
        this(pattern, ULocale.forLocale(locale));
    }

    public ChineseDateFormat(String pattern, ULocale locale) {
        this(pattern, (String)null, locale);
    }

    public ChineseDateFormat(String pattern, String override, ULocale locale) {
        super(pattern, new ChineseDateFormatSymbols(locale), new ChineseCalendar(TimeZone.getDefault(), locale), locale, true, override);
    }

    protected void subFormat(StringBuffer buf2, char ch2, int count, int beginOffset, int fieldNum, DisplayContext capitalizationContext, FieldPosition pos, Calendar cal2) {
        super.subFormat(buf2, ch2, count, beginOffset, fieldNum, capitalizationContext, pos, cal2);
    }

    protected int subParse(String text, int start, char ch2, int count, boolean obeyCount, boolean allowNegative, boolean[] ambiguousYear, Calendar cal2) {
        return super.subParse(text, start, ch2, count, obeyCount, allowNegative, ambiguousYear, cal2);
    }

    protected DateFormat.Field patternCharToDateFormatField(char ch2) {
        return super.patternCharToDateFormatField(ch2);
    }

    public static class Field
    extends DateFormat.Field {
        private static final long serialVersionUID = -5102130532751400330L;
        public static final Field IS_LEAP_MONTH = new Field("is leap month", 22);

        protected Field(String name, int calendarField) {
            super(name, calendarField);
        }

        public static DateFormat.Field ofCalendarField(int calendarField) {
            if (calendarField == 22) {
                return IS_LEAP_MONTH;
            }
            return DateFormat.Field.ofCalendarField(calendarField);
        }

        protected Object readResolve() throws InvalidObjectException {
            if (this.getClass() != Field.class) {
                throw new InvalidObjectException("A subclass of ChineseDateFormat.Field must implement readResolve.");
            }
            if (this.getName().equals(IS_LEAP_MONTH.getName())) {
                return IS_LEAP_MONTH;
            }
            throw new InvalidObjectException("Unknown attribute name.");
        }
    }
}

