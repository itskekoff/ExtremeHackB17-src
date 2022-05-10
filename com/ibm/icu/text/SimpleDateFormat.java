package com.ibm.icu.text;

import com.ibm.icu.impl.CalendarData;
import com.ibm.icu.impl.DateNumberFormat;
import com.ibm.icu.impl.ICUCache;
import com.ibm.icu.impl.PatternProps;
import com.ibm.icu.impl.SimpleCache;
import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.DateFormatSymbols;
import com.ibm.icu.text.DecimalFormat;
import com.ibm.icu.text.DecimalFormatSymbols;
import com.ibm.icu.text.DisplayContext;
import com.ibm.icu.text.MessageFormat;
import com.ibm.icu.text.NumberFormat;
import com.ibm.icu.text.NumberingSystem;
import com.ibm.icu.text.TimeZoneFormat;
import com.ibm.icu.text.UTF16;
import com.ibm.icu.text.UnicodeSet;
import com.ibm.icu.util.BasicTimeZone;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.HebrewCalendar;
import com.ibm.icu.util.Output;
import com.ibm.icu.util.TimeZone;
import com.ibm.icu.util.ULocale;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SimpleDateFormat
extends DateFormat {
    private static final long serialVersionUID = 4774881970558875024L;
    static final int currentSerialVersion = 2;
    static boolean DelayedHebrewMonthCheck = false;
    private static final int[] CALENDAR_FIELD_TO_LEVEL = new int[]{0, 10, 20, 20, 30, 30, 20, 30, 30, 40, 50, 50, 60, 70, 80, 0, 0, 10, 30, 10, 0, 40};
    private static final int[] PATTERN_CHAR_TO_LEVEL = new int[]{-1, 40, -1, -1, 20, 30, 30, 0, 50, -1, -1, 50, 20, 20, -1, 0, -1, 20, -1, 80, -1, 10, 0, 30, 0, 10, 0, -1, -1, -1, -1, -1, -1, 40, -1, 30, 30, 30, -1, 0, 50, -1, -1, 50, -1, 60, -1, -1, -1, 20, -1, 70, -1, 10, 0, 20, 0, 10, 0, -1, -1, -1, -1, -1};
    private static final int HEBREW_CAL_CUR_MILLENIUM_START_YEAR = 5000;
    private static final int HEBREW_CAL_CUR_MILLENIUM_END_YEAR = 6000;
    private int serialVersionOnStream = 2;
    private String pattern;
    private String override;
    private HashMap<String, NumberFormat> numberFormatters;
    private HashMap<Character, String> overrideMap;
    private DateFormatSymbols formatData;
    private transient ULocale locale;
    private Date defaultCenturyStart;
    private transient int defaultCenturyStartYear;
    private transient long defaultCenturyBase;
    private transient TimeZoneFormat.TimeType tztype = TimeZoneFormat.TimeType.UNKNOWN;
    private static final int millisPerHour = 3600000;
    private static final int ISOSpecialEra = -32000;
    private static final String SUPPRESS_NEGATIVE_PREFIX = "\uab00";
    private transient boolean useFastFormat;
    private volatile TimeZoneFormat tzFormat;
    private transient DisplayContext capitalizationSetting;
    private static ULocale cachedDefaultLocale = null;
    private static String cachedDefaultPattern = null;
    private static final String FALLBACKPATTERN = "yy/MM/dd HH:mm";
    private static final int PATTERN_CHAR_BASE = 64;
    private static final int[] PATTERN_CHAR_TO_INDEX = new int[]{-1, 22, -1, -1, 10, 9, 11, 0, 5, -1, -1, 16, 26, 2, -1, 31, -1, 27, -1, 8, -1, 30, 29, 13, 32, 18, 23, -1, -1, -1, -1, -1, -1, 14, -1, 25, 3, 19, -1, 21, 15, -1, -1, 4, -1, 6, -1, -1, -1, 28, -1, 7, -1, 20, 24, 12, 33, 1, 17, -1, -1, -1, -1, -1};
    private static final int[] PATTERN_INDEX_TO_CALENDAR_FIELD = new int[]{0, 1, 2, 5, 11, 11, 12, 13, 14, 7, 6, 8, 3, 4, 9, 10, 10, 15, 17, 18, 19, 20, 21, 15, 15, 18, 2, 2, 2, 15, 1, 15, 15, 15};
    private static final int[] PATTERN_INDEX_TO_DATE_FORMAT_FIELD = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33};
    private static final DateFormat.Field[] PATTERN_INDEX_TO_DATE_FORMAT_ATTRIBUTE = new DateFormat.Field[]{DateFormat.Field.ERA, DateFormat.Field.YEAR, DateFormat.Field.MONTH, DateFormat.Field.DAY_OF_MONTH, DateFormat.Field.HOUR_OF_DAY1, DateFormat.Field.HOUR_OF_DAY0, DateFormat.Field.MINUTE, DateFormat.Field.SECOND, DateFormat.Field.MILLISECOND, DateFormat.Field.DAY_OF_WEEK, DateFormat.Field.DAY_OF_YEAR, DateFormat.Field.DAY_OF_WEEK_IN_MONTH, DateFormat.Field.WEEK_OF_YEAR, DateFormat.Field.WEEK_OF_MONTH, DateFormat.Field.AM_PM, DateFormat.Field.HOUR1, DateFormat.Field.HOUR0, DateFormat.Field.TIME_ZONE, DateFormat.Field.YEAR_WOY, DateFormat.Field.DOW_LOCAL, DateFormat.Field.EXTENDED_YEAR, DateFormat.Field.JULIAN_DAY, DateFormat.Field.MILLISECONDS_IN_DAY, DateFormat.Field.TIME_ZONE, DateFormat.Field.TIME_ZONE, DateFormat.Field.DAY_OF_WEEK, DateFormat.Field.MONTH, DateFormat.Field.QUARTER, DateFormat.Field.QUARTER, DateFormat.Field.TIME_ZONE, DateFormat.Field.YEAR, DateFormat.Field.TIME_ZONE, DateFormat.Field.TIME_ZONE, DateFormat.Field.TIME_ZONE};
    private static ICUCache<String, Object[]> PARSED_PATTERN_CACHE = new SimpleCache<String, Object[]>();
    private transient Object[] patternItems;
    private transient boolean useLocalZeroPaddingNumberFormat;
    private transient char[] decDigits;
    private transient char[] decimalBuf;
    private static final String NUMERIC_FORMAT_CHARS = "MYyudehHmsSDFwWkK";
    static final UnicodeSet DATE_PATTERN_TYPE = new UnicodeSet("[GyYuUQqMLlwWd]").freeze();

    public SimpleDateFormat() {
        this(SimpleDateFormat.getDefaultPattern(), null, null, null, null, true, null);
    }

    public SimpleDateFormat(String pattern) {
        this(pattern, null, null, null, null, true, null);
    }

    public SimpleDateFormat(String pattern, Locale loc) {
        this(pattern, null, null, null, ULocale.forLocale(loc), true, null);
    }

    public SimpleDateFormat(String pattern, ULocale loc) {
        this(pattern, null, null, null, loc, true, null);
    }

    public SimpleDateFormat(String pattern, String override, ULocale loc) {
        this(pattern, null, null, null, loc, false, override);
    }

    public SimpleDateFormat(String pattern, DateFormatSymbols formatData) {
        this(pattern, (DateFormatSymbols)formatData.clone(), null, null, null, true, null);
    }

    public SimpleDateFormat(String pattern, DateFormatSymbols formatData, ULocale loc) {
        this(pattern, (DateFormatSymbols)formatData.clone(), null, null, loc, true, null);
    }

    SimpleDateFormat(String pattern, DateFormatSymbols formatData, Calendar calendar, ULocale locale, boolean useFastFormat, String override) {
        this(pattern, (DateFormatSymbols)formatData.clone(), (Calendar)calendar.clone(), null, locale, useFastFormat, override);
    }

    private SimpleDateFormat(String pattern, DateFormatSymbols formatData, Calendar calendar, NumberFormat numberFormat, ULocale locale, boolean useFastFormat, String override) {
        this.pattern = pattern;
        this.formatData = formatData;
        this.calendar = calendar;
        this.numberFormat = numberFormat;
        this.locale = locale;
        this.useFastFormat = useFastFormat;
        this.override = override;
        this.initialize();
    }

    public static SimpleDateFormat getInstance(Calendar.FormatConfiguration formatConfig) {
        String ostr = formatConfig.getOverrideString();
        boolean useFast = ostr != null && ostr.length() > 0;
        return new SimpleDateFormat(formatConfig.getPatternString(), formatConfig.getDateFormatSymbols(), formatConfig.getCalendar(), null, formatConfig.getLocale(), useFast, formatConfig.getOverrideString());
    }

    private void initialize() {
        if (this.locale == null) {
            this.locale = ULocale.getDefault(ULocale.Category.FORMAT);
        }
        if (this.formatData == null) {
            this.formatData = new DateFormatSymbols(this.locale);
        }
        if (this.calendar == null) {
            this.calendar = Calendar.getInstance(this.locale);
        }
        if (this.numberFormat == null) {
            NumberingSystem ns2 = NumberingSystem.getInstance(this.locale);
            if (ns2.isAlgorithmic()) {
                this.numberFormat = NumberFormat.getInstance(this.locale);
            } else {
                String digitString = ns2.getDescription();
                String nsName = ns2.getName();
                this.numberFormat = new DateNumberFormat(this.locale, digitString, nsName);
            }
        }
        this.defaultCenturyBase = System.currentTimeMillis();
        this.setLocale(this.calendar.getLocale(ULocale.VALID_LOCALE), this.calendar.getLocale(ULocale.ACTUAL_LOCALE));
        this.initLocalZeroPaddingNumberFormat();
        if (this.override != null) {
            this.initNumberFormatters(this.locale);
        }
        this.capitalizationSetting = DisplayContext.CAPITALIZATION_NONE;
    }

    private synchronized void initializeTimeZoneFormat(boolean bForceUpdate) {
        if (bForceUpdate || this.tzFormat == null) {
            this.tzFormat = TimeZoneFormat.getInstance(this.locale);
            String digits = null;
            if (this.numberFormat instanceof DecimalFormat) {
                DecimalFormatSymbols decsym = ((DecimalFormat)this.numberFormat).getDecimalFormatSymbols();
                digits = new String(decsym.getDigits());
            } else if (this.numberFormat instanceof DateNumberFormat) {
                digits = new String(((DateNumberFormat)this.numberFormat).getDigits());
            }
            if (digits != null && !this.tzFormat.getGMTOffsetDigits().equals(digits)) {
                if (this.tzFormat.isFrozen()) {
                    this.tzFormat = this.tzFormat.cloneAsThawed();
                }
                this.tzFormat.setGMTOffsetDigits(digits);
            }
        }
    }

    private TimeZoneFormat tzFormat() {
        if (this.tzFormat == null) {
            this.initializeTimeZoneFormat(false);
        }
        return this.tzFormat;
    }

    private static synchronized String getDefaultPattern() {
        ULocale defaultLocale = ULocale.getDefault(ULocale.Category.FORMAT);
        if (!defaultLocale.equals(cachedDefaultLocale)) {
            cachedDefaultLocale = defaultLocale;
            Calendar cal2 = Calendar.getInstance(cachedDefaultLocale);
            try {
                CalendarData calData = new CalendarData(cachedDefaultLocale, cal2.getType());
                String[] dateTimePatterns = calData.getDateTimePatterns();
                int glueIndex = 8;
                if (dateTimePatterns.length >= 13) {
                    glueIndex += 4;
                }
                cachedDefaultPattern = MessageFormat.format(dateTimePatterns[glueIndex], dateTimePatterns[3], dateTimePatterns[7]);
            }
            catch (MissingResourceException e2) {
                cachedDefaultPattern = FALLBACKPATTERN;
            }
        }
        return cachedDefaultPattern;
    }

    private void parseAmbiguousDatesAsAfter(Date startDate) {
        this.defaultCenturyStart = startDate;
        this.calendar.setTime(startDate);
        this.defaultCenturyStartYear = this.calendar.get(1);
    }

    private void initializeDefaultCenturyStart(long baseTime) {
        this.defaultCenturyBase = baseTime;
        Calendar tmpCal = (Calendar)this.calendar.clone();
        tmpCal.setTimeInMillis(baseTime);
        tmpCal.add(1, -80);
        this.defaultCenturyStart = tmpCal.getTime();
        this.defaultCenturyStartYear = tmpCal.get(1);
    }

    private Date getDefaultCenturyStart() {
        if (this.defaultCenturyStart == null) {
            this.initializeDefaultCenturyStart(this.defaultCenturyBase);
        }
        return this.defaultCenturyStart;
    }

    private int getDefaultCenturyStartYear() {
        if (this.defaultCenturyStart == null) {
            this.initializeDefaultCenturyStart(this.defaultCenturyBase);
        }
        return this.defaultCenturyStartYear;
    }

    public void set2DigitYearStart(Date startDate) {
        this.parseAmbiguousDatesAsAfter(startDate);
    }

    public Date get2DigitYearStart() {
        return this.getDefaultCenturyStart();
    }

    @Override
    public StringBuffer format(Calendar cal2, StringBuffer toAppendTo, FieldPosition pos) {
        TimeZone backupTZ = null;
        if (cal2 != this.calendar && !cal2.getType().equals(this.calendar.getType())) {
            this.calendar.setTimeInMillis(cal2.getTimeInMillis());
            backupTZ = this.calendar.getTimeZone();
            this.calendar.setTimeZone(cal2.getTimeZone());
            cal2 = this.calendar;
        }
        StringBuffer result = this.format(cal2, this.capitalizationSetting, toAppendTo, pos, null);
        if (backupTZ != null) {
            this.calendar.setTimeZone(backupTZ);
        }
        return result;
    }

    private StringBuffer format(Calendar cal2, DisplayContext capitalizationContext, StringBuffer toAppendTo, FieldPosition pos, List<FieldPosition> attributes) {
        pos.setBeginIndex(0);
        pos.setEndIndex(0);
        Object[] items = this.getPatternItems();
        for (int i2 = 0; i2 < items.length; ++i2) {
            int end;
            if (items[i2] instanceof String) {
                toAppendTo.append((String)items[i2]);
                continue;
            }
            PatternItem item = (PatternItem)items[i2];
            int start = 0;
            if (attributes != null) {
                start = toAppendTo.length();
            }
            if (this.useFastFormat) {
                this.subFormat(toAppendTo, item.type, item.length, toAppendTo.length(), i2, capitalizationContext, pos, cal2);
            } else {
                toAppendTo.append(this.subFormat(item.type, item.length, toAppendTo.length(), i2, capitalizationContext, pos, cal2));
            }
            if (attributes == null || (end = toAppendTo.length()) - start <= 0) continue;
            DateFormat.Field attr = this.patternCharToDateFormatField(item.type);
            FieldPosition fp = new FieldPosition(attr);
            fp.setBeginIndex(start);
            fp.setEndIndex(end);
            attributes.add(fp);
        }
        return toAppendTo;
    }

    protected DateFormat.Field patternCharToDateFormatField(char ch2) {
        int patternCharIndex = -1;
        if ('A' <= ch2 && ch2 <= 'z') {
            patternCharIndex = PATTERN_CHAR_TO_INDEX[ch2 - 64];
        }
        if (patternCharIndex != -1) {
            return PATTERN_INDEX_TO_DATE_FORMAT_ATTRIBUTE[patternCharIndex];
        }
        return null;
    }

    protected String subFormat(char ch2, int count, int beginOffset, FieldPosition pos, DateFormatSymbols fmtData, Calendar cal2) throws IllegalArgumentException {
        return this.subFormat(ch2, count, beginOffset, 0, DisplayContext.CAPITALIZATION_NONE, pos, cal2);
    }

    protected String subFormat(char ch2, int count, int beginOffset, int fieldNum, DisplayContext capitalizationContext, FieldPosition pos, Calendar cal2) {
        StringBuffer buf2 = new StringBuffer();
        this.subFormat(buf2, ch2, count, beginOffset, fieldNum, capitalizationContext, pos, cal2);
        return buf2.toString();
    }

    protected void subFormat(StringBuffer buf2, char ch2, int count, int beginOffset, int fieldNum, DisplayContext capitalizationContext, FieldPosition pos, Calendar cal2) {
        int maxIntCount = Integer.MAX_VALUE;
        int bufstart = buf2.length();
        TimeZone tz2 = cal2.getTimeZone();
        long date = cal2.getTimeInMillis();
        String result = null;
        int patternCharIndex = -1;
        if ('A' <= ch2 && ch2 <= 'z') {
            patternCharIndex = PATTERN_CHAR_TO_INDEX[ch2 - 64];
        }
        if (patternCharIndex == -1) {
            if (ch2 == 'l') {
                return;
            }
            throw new IllegalArgumentException("Illegal pattern character '" + ch2 + "' in \"" + this.pattern + '\"');
        }
        int field = PATTERN_INDEX_TO_CALENDAR_FIELD[patternCharIndex];
        int value = cal2.get(field);
        NumberFormat currentNumberFormat = this.getNumberFormat(ch2);
        DateFormatSymbols.CapitalizationContextUsage capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.OTHER;
        switch (patternCharIndex) {
            case 0: {
                if (cal2.getType().equals("chinese")) {
                    this.zeroPaddingNumber(currentNumberFormat, buf2, value, 1, 9);
                    break;
                }
                if (count == 5) {
                    SimpleDateFormat.safeAppend(this.formatData.narrowEras, value, buf2);
                    capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.ERA_NARROW;
                    break;
                }
                if (count == 4) {
                    SimpleDateFormat.safeAppend(this.formatData.eraNames, value, buf2);
                    capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.ERA_WIDE;
                    break;
                }
                SimpleDateFormat.safeAppend(this.formatData.eras, value, buf2);
                capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.ERA_ABBREV;
                break;
            }
            case 30: {
                if (this.formatData.shortYearNames != null && value <= this.formatData.shortYearNames.length) {
                    SimpleDateFormat.safeAppend(this.formatData.shortYearNames, value - 1, buf2);
                    break;
                }
            }
            case 1: 
            case 18: {
                if (this.override != null && (this.override.compareTo("hebr") == 0 || this.override.indexOf("y=hebr") >= 0) && value > 5000 && value < 6000) {
                    value -= 5000;
                }
                if (count == 2) {
                    this.zeroPaddingNumber(currentNumberFormat, buf2, value, 2, 2);
                    break;
                }
                this.zeroPaddingNumber(currentNumberFormat, buf2, value, count, Integer.MAX_VALUE);
                break;
            }
            case 2: 
            case 26: {
                int isLeapMonth;
                if (cal2.getType().equals("hebrew")) {
                    boolean isLeap = HebrewCalendar.isLeapYear(cal2.get(1));
                    if (isLeap && value == 6 && count >= 3) {
                        value = 13;
                    }
                    if (!isLeap && value >= 6 && count < 3) {
                        --value;
                    }
                }
                int n2 = isLeapMonth = this.formatData.leapMonthPatterns != null && this.formatData.leapMonthPatterns.length >= 7 ? cal2.get(22) : 0;
                if (count == 5) {
                    if (patternCharIndex == 2) {
                        SimpleDateFormat.safeAppendWithMonthPattern(this.formatData.narrowMonths, value, buf2, isLeapMonth != 0 ? this.formatData.leapMonthPatterns[2] : null);
                    } else {
                        SimpleDateFormat.safeAppendWithMonthPattern(this.formatData.standaloneNarrowMonths, value, buf2, isLeapMonth != 0 ? this.formatData.leapMonthPatterns[5] : null);
                    }
                    capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.MONTH_NARROW;
                    break;
                }
                if (count == 4) {
                    if (patternCharIndex == 2) {
                        SimpleDateFormat.safeAppendWithMonthPattern(this.formatData.months, value, buf2, isLeapMonth != 0 ? this.formatData.leapMonthPatterns[0] : null);
                        capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.MONTH_FORMAT;
                        break;
                    }
                    SimpleDateFormat.safeAppendWithMonthPattern(this.formatData.standaloneMonths, value, buf2, isLeapMonth != 0 ? this.formatData.leapMonthPatterns[3] : null);
                    capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.MONTH_STANDALONE;
                    break;
                }
                if (count == 3) {
                    if (patternCharIndex == 2) {
                        SimpleDateFormat.safeAppendWithMonthPattern(this.formatData.shortMonths, value, buf2, isLeapMonth != 0 ? this.formatData.leapMonthPatterns[1] : null);
                        capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.MONTH_FORMAT;
                        break;
                    }
                    SimpleDateFormat.safeAppendWithMonthPattern(this.formatData.standaloneShortMonths, value, buf2, isLeapMonth != 0 ? this.formatData.leapMonthPatterns[4] : null);
                    capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.MONTH_STANDALONE;
                    break;
                }
                StringBuffer monthNumber = new StringBuffer();
                this.zeroPaddingNumber(currentNumberFormat, monthNumber, value + 1, count, Integer.MAX_VALUE);
                String[] monthNumberStrings = new String[]{monthNumber.toString()};
                SimpleDateFormat.safeAppendWithMonthPattern(monthNumberStrings, 0, buf2, isLeapMonth != 0 ? this.formatData.leapMonthPatterns[6] : null);
                break;
            }
            case 4: {
                if (value == 0) {
                    this.zeroPaddingNumber(currentNumberFormat, buf2, cal2.getMaximum(11) + 1, count, Integer.MAX_VALUE);
                    break;
                }
                this.zeroPaddingNumber(currentNumberFormat, buf2, value, count, Integer.MAX_VALUE);
                break;
            }
            case 8: {
                this.numberFormat.setMinimumIntegerDigits(Math.min(3, count));
                this.numberFormat.setMaximumIntegerDigits(Integer.MAX_VALUE);
                if (count == 1) {
                    value /= 100;
                } else if (count == 2) {
                    value /= 10;
                }
                FieldPosition p2 = new FieldPosition(-1);
                this.numberFormat.format(value, buf2, p2);
                if (count <= 3) break;
                this.numberFormat.setMinimumIntegerDigits(count - 3);
                this.numberFormat.format(0L, buf2, p2);
                break;
            }
            case 19: {
                if (count < 3) {
                    this.zeroPaddingNumber(currentNumberFormat, buf2, value, count, Integer.MAX_VALUE);
                    break;
                }
                value = cal2.get(7);
            }
            case 9: {
                if (count == 5) {
                    SimpleDateFormat.safeAppend(this.formatData.narrowWeekdays, value, buf2);
                    capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.DAY_NARROW;
                    break;
                }
                if (count == 4) {
                    SimpleDateFormat.safeAppend(this.formatData.weekdays, value, buf2);
                    capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.DAY_FORMAT;
                    break;
                }
                if (count == 6 && this.formatData.shorterWeekdays != null) {
                    SimpleDateFormat.safeAppend(this.formatData.shorterWeekdays, value, buf2);
                    capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.DAY_FORMAT;
                    break;
                }
                SimpleDateFormat.safeAppend(this.formatData.shortWeekdays, value, buf2);
                capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.DAY_FORMAT;
                break;
            }
            case 14: {
                SimpleDateFormat.safeAppend(this.formatData.ampms, value, buf2);
                break;
            }
            case 15: {
                if (value == 0) {
                    this.zeroPaddingNumber(currentNumberFormat, buf2, cal2.getLeastMaximum(10) + 1, count, Integer.MAX_VALUE);
                    break;
                }
                this.zeroPaddingNumber(currentNumberFormat, buf2, value, count, Integer.MAX_VALUE);
                break;
            }
            case 17: {
                if (count < 4) {
                    result = this.tzFormat().format(TimeZoneFormat.Style.SPECIFIC_SHORT, tz2, date);
                    capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.METAZONE_SHORT;
                } else {
                    result = this.tzFormat().format(TimeZoneFormat.Style.SPECIFIC_LONG, tz2, date);
                    capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.METAZONE_LONG;
                }
                buf2.append(result);
                break;
            }
            case 23: {
                result = count < 4 ? this.tzFormat().format(TimeZoneFormat.Style.ISO_BASIC_LOCAL_FULL, tz2, date) : (count == 5 ? this.tzFormat().format(TimeZoneFormat.Style.ISO_EXTENDED_FULL, tz2, date) : this.tzFormat().format(TimeZoneFormat.Style.LOCALIZED_GMT, tz2, date));
                buf2.append(result);
                break;
            }
            case 24: {
                if (count == 1) {
                    result = this.tzFormat().format(TimeZoneFormat.Style.GENERIC_SHORT, tz2, date);
                    capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.METAZONE_SHORT;
                } else if (count == 4) {
                    result = this.tzFormat().format(TimeZoneFormat.Style.GENERIC_LONG, tz2, date);
                    capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.METAZONE_LONG;
                }
                buf2.append(result);
                break;
            }
            case 29: {
                if (count == 1) {
                    result = this.tzFormat().format(TimeZoneFormat.Style.ZONE_ID_SHORT, tz2, date);
                } else if (count == 2) {
                    result = this.tzFormat().format(TimeZoneFormat.Style.ZONE_ID, tz2, date);
                } else if (count == 3) {
                    result = this.tzFormat().format(TimeZoneFormat.Style.EXEMPLAR_LOCATION, tz2, date);
                } else if (count == 4) {
                    result = this.tzFormat().format(TimeZoneFormat.Style.GENERIC_LOCATION, tz2, date);
                    capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.ZONE_LONG;
                }
                buf2.append(result);
                break;
            }
            case 31: {
                if (count == 1) {
                    result = this.tzFormat().format(TimeZoneFormat.Style.LOCALIZED_GMT_SHORT, tz2, date);
                } else if (count == 4) {
                    result = this.tzFormat().format(TimeZoneFormat.Style.LOCALIZED_GMT, tz2, date);
                }
                buf2.append(result);
                break;
            }
            case 32: {
                if (count == 1) {
                    result = this.tzFormat().format(TimeZoneFormat.Style.ISO_BASIC_SHORT, tz2, date);
                } else if (count == 2) {
                    result = this.tzFormat().format(TimeZoneFormat.Style.ISO_BASIC_FIXED, tz2, date);
                } else if (count == 3) {
                    result = this.tzFormat().format(TimeZoneFormat.Style.ISO_EXTENDED_FIXED, tz2, date);
                } else if (count == 4) {
                    result = this.tzFormat().format(TimeZoneFormat.Style.ISO_BASIC_FULL, tz2, date);
                } else if (count == 5) {
                    result = this.tzFormat().format(TimeZoneFormat.Style.ISO_EXTENDED_FULL, tz2, date);
                }
                buf2.append(result);
                break;
            }
            case 33: {
                if (count == 1) {
                    result = this.tzFormat().format(TimeZoneFormat.Style.ISO_BASIC_LOCAL_SHORT, tz2, date);
                } else if (count == 2) {
                    result = this.tzFormat().format(TimeZoneFormat.Style.ISO_BASIC_LOCAL_FIXED, tz2, date);
                } else if (count == 3) {
                    result = this.tzFormat().format(TimeZoneFormat.Style.ISO_EXTENDED_LOCAL_FIXED, tz2, date);
                } else if (count == 4) {
                    result = this.tzFormat().format(TimeZoneFormat.Style.ISO_BASIC_LOCAL_FULL, tz2, date);
                } else if (count == 5) {
                    result = this.tzFormat().format(TimeZoneFormat.Style.ISO_EXTENDED_LOCAL_FULL, tz2, date);
                }
                buf2.append(result);
                break;
            }
            case 25: {
                if (count < 3) {
                    this.zeroPaddingNumber(currentNumberFormat, buf2, value, 1, Integer.MAX_VALUE);
                    break;
                }
                value = cal2.get(7);
                if (count == 5) {
                    SimpleDateFormat.safeAppend(this.formatData.standaloneNarrowWeekdays, value, buf2);
                    capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.DAY_NARROW;
                    break;
                }
                if (count == 4) {
                    SimpleDateFormat.safeAppend(this.formatData.standaloneWeekdays, value, buf2);
                    capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.DAY_STANDALONE;
                    break;
                }
                if (count == 6 && this.formatData.standaloneShorterWeekdays != null) {
                    SimpleDateFormat.safeAppend(this.formatData.standaloneShorterWeekdays, value, buf2);
                    capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.DAY_STANDALONE;
                    break;
                }
                SimpleDateFormat.safeAppend(this.formatData.standaloneShortWeekdays, value, buf2);
                capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.DAY_STANDALONE;
                break;
            }
            case 27: {
                if (count >= 4) {
                    SimpleDateFormat.safeAppend(this.formatData.quarters, value / 3, buf2);
                    break;
                }
                if (count == 3) {
                    SimpleDateFormat.safeAppend(this.formatData.shortQuarters, value / 3, buf2);
                    break;
                }
                this.zeroPaddingNumber(currentNumberFormat, buf2, value / 3 + 1, count, Integer.MAX_VALUE);
                break;
            }
            case 28: {
                if (count >= 4) {
                    SimpleDateFormat.safeAppend(this.formatData.standaloneQuarters, value / 3, buf2);
                    break;
                }
                if (count == 3) {
                    SimpleDateFormat.safeAppend(this.formatData.standaloneShortQuarters, value / 3, buf2);
                    break;
                }
                this.zeroPaddingNumber(currentNumberFormat, buf2, value / 3 + 1, count, Integer.MAX_VALUE);
                break;
            }
            default: {
                this.zeroPaddingNumber(currentNumberFormat, buf2, value, count, Integer.MAX_VALUE);
            }
        }
        if (fieldNum == 0) {
            boolean titlecase = false;
            if (capitalizationContext != null) {
                switch (capitalizationContext) {
                    case CAPITALIZATION_FOR_BEGINNING_OF_SENTENCE: {
                        titlecase = true;
                        break;
                    }
                    case CAPITALIZATION_FOR_UI_LIST_OR_MENU: 
                    case CAPITALIZATION_FOR_STANDALONE: {
                        if (this.formatData.capitalization == null) break;
                        boolean[] transforms = this.formatData.capitalization.get((Object)capContextUsageType);
                        titlecase = capitalizationContext == DisplayContext.CAPITALIZATION_FOR_UI_LIST_OR_MENU ? transforms[0] : transforms[1];
                        break;
                    }
                }
            }
            if (titlecase) {
                String firstField = buf2.substring(bufstart);
                String firstFieldTitleCase = UCharacter.toTitleCase(this.locale, firstField, null, 768);
                buf2.replace(bufstart, buf2.length(), firstFieldTitleCase);
            }
        }
        if (pos.getBeginIndex() == pos.getEndIndex()) {
            if (pos.getField() == PATTERN_INDEX_TO_DATE_FORMAT_FIELD[patternCharIndex]) {
                pos.setBeginIndex(beginOffset);
                pos.setEndIndex(beginOffset + buf2.length() - bufstart);
            } else if (pos.getFieldAttribute() == PATTERN_INDEX_TO_DATE_FORMAT_ATTRIBUTE[patternCharIndex]) {
                pos.setBeginIndex(beginOffset);
                pos.setEndIndex(beginOffset + buf2.length() - bufstart);
            }
        }
    }

    private static void safeAppend(String[] array, int value, StringBuffer appendTo) {
        if (array != null && value >= 0 && value < array.length) {
            appendTo.append(array[value]);
        }
    }

    private static void safeAppendWithMonthPattern(String[] array, int value, StringBuffer appendTo, String monthPattern) {
        if (array != null && value >= 0 && value < array.length) {
            if (monthPattern == null) {
                appendTo.append(array[value]);
            } else {
                appendTo.append(MessageFormat.format(monthPattern, array[value]));
            }
        }
    }

    private Object[] getPatternItems() {
        if (this.patternItems != null) {
            return this.patternItems;
        }
        this.patternItems = PARSED_PATTERN_CACHE.get(this.pattern);
        if (this.patternItems != null) {
            return this.patternItems;
        }
        boolean isPrevQuote = false;
        boolean inQuote = false;
        StringBuilder text = new StringBuilder();
        char itemType = '\u0000';
        int itemLength = 1;
        ArrayList<Object> items = new ArrayList<Object>();
        for (int i2 = 0; i2 < this.pattern.length(); ++i2) {
            char ch2 = this.pattern.charAt(i2);
            if (ch2 == '\'') {
                if (isPrevQuote) {
                    text.append('\'');
                    isPrevQuote = false;
                } else {
                    isPrevQuote = true;
                    if (itemType != '\u0000') {
                        items.add(new PatternItem(itemType, itemLength));
                        itemType = '\u0000';
                    }
                }
                inQuote = !inQuote;
                continue;
            }
            isPrevQuote = false;
            if (inQuote) {
                text.append(ch2);
                continue;
            }
            if (ch2 >= 'a' && ch2 <= 'z' || ch2 >= 'A' && ch2 <= 'Z') {
                if (ch2 == itemType) {
                    ++itemLength;
                    continue;
                }
                if (itemType == '\u0000') {
                    if (text.length() > 0) {
                        items.add(text.toString());
                        text.setLength(0);
                    }
                } else {
                    items.add(new PatternItem(itemType, itemLength));
                }
                itemType = ch2;
                itemLength = 1;
                continue;
            }
            if (itemType != '\u0000') {
                items.add(new PatternItem(itemType, itemLength));
                itemType = '\u0000';
            }
            text.append(ch2);
        }
        if (itemType == '\u0000') {
            if (text.length() > 0) {
                items.add(text.toString());
                text.setLength(0);
            }
        } else {
            items.add(new PatternItem(itemType, itemLength));
        }
        this.patternItems = items.toArray(new Object[items.size()]);
        PARSED_PATTERN_CACHE.put(this.pattern, this.patternItems);
        return this.patternItems;
    }

    protected void zeroPaddingNumber(NumberFormat nf2, StringBuffer buf2, int value, int minDigits, int maxDigits) {
        if (this.useLocalZeroPaddingNumberFormat && value >= 0) {
            this.fastZeroPaddingNumber(buf2, value, minDigits, maxDigits);
        } else {
            nf2.setMinimumIntegerDigits(minDigits);
            nf2.setMaximumIntegerDigits(maxDigits);
            nf2.format(value, buf2, new FieldPosition(-1));
        }
    }

    @Override
    public void setNumberFormat(NumberFormat newNumberFormat) {
        super.setNumberFormat(newNumberFormat);
        this.initLocalZeroPaddingNumberFormat();
        this.initializeTimeZoneFormat(true);
    }

    private void initLocalZeroPaddingNumberFormat() {
        if (this.numberFormat instanceof DecimalFormat) {
            this.decDigits = ((DecimalFormat)this.numberFormat).getDecimalFormatSymbols().getDigits();
            this.useLocalZeroPaddingNumberFormat = true;
        } else if (this.numberFormat instanceof DateNumberFormat) {
            this.decDigits = ((DateNumberFormat)this.numberFormat).getDigits();
            this.useLocalZeroPaddingNumberFormat = true;
        } else {
            this.useLocalZeroPaddingNumberFormat = false;
        }
        if (this.useLocalZeroPaddingNumberFormat) {
            this.decimalBuf = new char[10];
        }
    }

    private void fastZeroPaddingNumber(StringBuffer buf2, int value, int minDigits, int maxDigits) {
        int padding;
        int limit = this.decimalBuf.length < maxDigits ? this.decimalBuf.length : maxDigits;
        int index = limit - 1;
        while (true) {
            this.decimalBuf[index] = this.decDigits[value % 10];
            if (index == 0 || (value /= 10) == 0) break;
            --index;
        }
        for (padding = minDigits - (limit - index); padding > 0 && index > 0; --padding) {
            this.decimalBuf[--index] = this.decDigits[0];
        }
        while (padding > 0) {
            buf2.append(this.decDigits[0]);
            --padding;
        }
        buf2.append(this.decimalBuf, index, limit - index);
    }

    protected String zeroPaddingNumber(long value, int minDigits, int maxDigits) {
        this.numberFormat.setMinimumIntegerDigits(minDigits);
        this.numberFormat.setMaximumIntegerDigits(maxDigits);
        return this.numberFormat.format(value);
    }

    private static final boolean isNumeric(char formatChar, int count) {
        int i2 = NUMERIC_FORMAT_CHARS.indexOf(formatChar);
        return i2 > 0 || i2 == 0 && count < 3;
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    @Override
    public void parse(String text, Calendar cal, ParsePosition parsePos) {
        block44: {
            block43: {
                block41: {
                    block42: {
                        block39: {
                            block38: {
                                backupTZ = null;
                                resultCal = null;
                                if (cal != this.calendar && !cal.getType().equals(this.calendar.getType())) {
                                    this.calendar.setTimeInMillis(cal.getTimeInMillis());
                                    backupTZ = this.calendar.getTimeZone();
                                    this.calendar.setTimeZone(cal.getTimeZone());
                                    resultCal = cal;
                                    cal = this.calendar;
                                }
                                start = pos = parsePos.getIndex();
                                this.tztype = TimeZoneFormat.TimeType.UNKNOWN;
                                ambiguousYear = new boolean[]{false};
                                numericFieldStart = -1;
                                numericFieldLength = 0;
                                numericStartPos = 0;
                                numericLeapMonthFormatter = null;
                                if (this.formatData.leapMonthPatterns != null && this.formatData.leapMonthPatterns.length >= 7) {
                                    numericLeapMonthFormatter = new MessageFormat(this.formatData.leapMonthPatterns[6], this.locale);
                                }
                                items = this.getPatternItems();
                                i = 0;
                                while (i < items.length) {
                                    if (items[i] instanceof PatternItem) {
                                        field = (PatternItem)items[i];
                                        if (field.isNumeric && numericFieldStart == -1 && i + 1 < items.length && items[i + 1] instanceof PatternItem && ((PatternItem)items[i + 1]).isNumeric) {
                                            numericFieldStart = i;
                                            numericFieldLength = field.length;
                                            numericStartPos = pos;
                                        }
                                        if (numericFieldStart != -1) {
                                            len = field.length;
                                            if (numericFieldStart == i) {
                                                len = numericFieldLength;
                                            }
                                            if ((pos = this.subParse(text, pos, field.type, len, true, false, ambiguousYear, cal, numericLeapMonthFormatter)) < 0) {
                                                if (--numericFieldLength == 0) {
                                                    parsePos.setIndex(start);
                                                    parsePos.setErrorIndex(pos);
                                                    if (backupTZ == null) return;
                                                    this.calendar.setTimeZone(backupTZ);
                                                    return;
                                                }
                                                i = numericFieldStart;
                                                pos = numericStartPos;
                                                continue;
                                            }
                                        } else if (field.type != 'l') {
                                            numericFieldStart = -1;
                                            s = pos;
                                            if ((pos = this.subParse(text, pos, field.type, field.length, false, true, ambiguousYear, cal, numericLeapMonthFormatter)) < 0) {
                                                if (pos != -32000) {
                                                    parsePos.setIndex(start);
                                                    parsePos.setErrorIndex(s);
                                                    if (backupTZ == null) return;
                                                    this.calendar.setTimeZone(backupTZ);
                                                    return;
                                                }
                                                pos = s;
                                                if (i + 1 < items.length) {
                                                    patl = null;
                                                    try {
                                                        patl = (String)items[i + 1];
                                                    }
                                                    catch (ClassCastException cce) {
                                                        parsePos.setIndex(start);
                                                        parsePos.setErrorIndex(s);
                                                        if (backupTZ == null) return;
                                                        this.calendar.setTimeZone(backupTZ);
                                                        return;
                                                    }
                                                    if (patl == null) {
                                                        patl = (String)items[i + 1];
                                                    }
                                                    plen = patl.length();
                                                    for (idx = 0; idx < plen && PatternProps.isWhiteSpace(pch = patl.charAt(idx)); ++idx) {
                                                    }
                                                    if (idx == plen) {
                                                        ++i;
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        numericFieldStart = -1;
                                        complete = new boolean[1];
                                        pos = this.matchLiteral(text, pos, items, i, complete);
                                        if (!complete[0]) {
                                            parsePos.setIndex(start);
                                            parsePos.setErrorIndex(pos);
                                            if (backupTZ == null) return;
                                            this.calendar.setTimeZone(backupTZ);
                                            return;
                                        }
                                    }
                                    ++i;
                                }
                                if (pos < text.length() && (extra = text.charAt(pos)) == '.' && this.isLenient() && items.length != 0 && (lastItem = items[items.length - 1]) instanceof PatternItem && !((PatternItem)lastItem).isNumeric) {
                                    ++pos;
                                }
                                parsePos.setIndex(pos);
                                try {
                                    block40: {
                                        if (!ambiguousYear[0] && this.tztype == TimeZoneFormat.TimeType.UNKNOWN) break block38;
                                        if (ambiguousYear[0] && (parsedDate = (copy = (Calendar)cal.clone()).getTime()).before(this.getDefaultCenturyStart())) {
                                            cal.set(1, this.getDefaultCenturyStartYear() + 100);
                                        }
                                        if (this.tztype == TimeZoneFormat.TimeType.UNKNOWN) break block38;
                                        copy = (Calendar)cal.clone();
                                        tz = copy.getTimeZone();
                                        btz = null;
                                        if (tz instanceof BasicTimeZone) {
                                            btz = (BasicTimeZone)tz;
                                        }
                                        copy.set(15, 0);
                                        copy.set(16, 0);
                                        localMillis = copy.getTimeInMillis();
                                        offsets = new int[2];
                                        if (btz != null) {
                                            if (this.tztype == TimeZoneFormat.TimeType.STANDARD) {
                                                btz.getOffsetFromLocal(localMillis, 1, 1, offsets);
                                            } else {
                                                btz.getOffsetFromLocal(localMillis, 3, 3, offsets);
                                            }
                                        } else {
                                            tz.getOffset(localMillis, true, offsets);
                                            if (this.tztype == TimeZoneFormat.TimeType.STANDARD && offsets[1] != 0 || this.tztype == TimeZoneFormat.TimeType.DAYLIGHT && offsets[1] == 0) {
                                                tz.getOffset(localMillis - 86400000L, true, offsets);
                                            }
                                        }
                                        resolvedSavings = offsets[1];
                                        if (this.tztype != TimeZoneFormat.TimeType.STANDARD) break block40;
                                        if (offsets[1] != 0) {
                                            resolvedSavings = 0;
                                        }
                                        ** GOTO lbl129
                                    }
                                    if (offsets[1] != 0) ** GOTO lbl129
                                    if (btz != null) {
                                        beforeT = time = localMillis + (long)offsets[0];
                                        afterT = time;
                                        beforeSav = 0;
                                        afterSav = 0;
                                        break block39;
                                    }
                                    resolvedSavings = tz.getDSTSavings();
lbl126:
                                    // 6 sources

                                    while (true) {
                                        if (resolvedSavings == 0) {
                                            resolvedSavings = 3600000;
                                        }
lbl129:
                                        // 5 sources

                                        cal.set(15, offsets[0]);
                                        cal.set(16, resolvedSavings);
                                        break;
                                    }
                                }
                                catch (IllegalArgumentException e) {
                                    parsePos.setErrorIndex(pos);
                                    parsePos.setIndex(start);
                                    if (backupTZ == null) return;
                                    this.calendar.setTimeZone(backupTZ);
                                    return;
                                }
                            }
                            if (resultCal != null) {
                                resultCal.setTimeZone(cal.getTimeZone());
                                resultCal.setTimeInMillis(cal.getTimeInMillis());
                            }
                            if (backupTZ == null) return;
                            this.calendar.setTimeZone(backupTZ);
                            return;
                        }
                        while ((beforeTrs = btz.getPreviousTransition(beforeT, true)) != null) {
                            beforeT = beforeTrs.getTime() - 1L;
                            beforeSav = beforeTrs.getFrom().getDSTSavings();
                            if (beforeSav == 0) continue;
                        }
                        while ((afterTrs = btz.getNextTransition(afterT, false)) != null) {
                            afterT = afterTrs.getTime();
                            afterSav = afterTrs.getTo().getDSTSavings();
                            if (afterSav == 0) continue;
                        }
                        if (beforeTrs == null || afterTrs == null) break block41;
                        if (time - beforeT <= afterT - time) break block42;
                        resolvedSavings = afterSav;
                        ** GOTO lbl126
                    }
                    resolvedSavings = beforeSav;
                    ** GOTO lbl126
                }
                if (beforeTrs == null || beforeSav == 0) break block43;
                resolvedSavings = beforeSav;
                ** GOTO lbl126
            }
            if (afterTrs == null || afterSav == 0) break block44;
            resolvedSavings = afterSav;
            ** GOTO lbl126
        }
        resolvedSavings = btz.getDSTSavings();
        ** while (true)
    }

    private int matchLiteral(String text, int pos, Object[] items, int itemIndex, boolean[] complete) {
        int originalPos = pos;
        String patternLiteral = (String)items[itemIndex];
        int plen = patternLiteral.length();
        int tlen = text.length();
        int idx = 0;
        while (idx < plen && pos < tlen) {
            char pch = patternLiteral.charAt(idx);
            char ich = text.charAt(pos);
            if (PatternProps.isWhiteSpace(pch) && PatternProps.isWhiteSpace(ich)) {
                while (idx + 1 < plen && PatternProps.isWhiteSpace(patternLiteral.charAt(idx + 1))) {
                    ++idx;
                }
                while (pos + 1 < tlen && PatternProps.isWhiteSpace(text.charAt(pos + 1))) {
                    ++pos;
                }
            } else if (pch != ich) {
                boolean isNumeric;
                Object before;
                if (ich != '.' || pos != originalPos || 0 >= itemIndex || !this.isLenient() || !((before = items[itemIndex - 1]) instanceof PatternItem) || (isNumeric = ((PatternItem)before).isNumeric)) break;
                ++pos;
                continue;
            }
            ++idx;
            ++pos;
        }
        boolean bl2 = complete[0] = idx == plen;
        if (!complete[0] && this.isLenient() && 0 < itemIndex && itemIndex < items.length - 1 && originalPos < tlen) {
            Object before = items[itemIndex - 1];
            Object after = items[itemIndex + 1];
            if (before instanceof PatternItem && after instanceof PatternItem) {
                char beforeType = ((PatternItem)before).type;
                char afterType = ((PatternItem)after).type;
                if (DATE_PATTERN_TYPE.contains(beforeType) != DATE_PATTERN_TYPE.contains(afterType)) {
                    char ich;
                    int newPos = originalPos;
                    while (PatternProps.isWhiteSpace(ich = text.charAt(newPos))) {
                        ++newPos;
                    }
                    complete[0] = newPos > originalPos;
                    pos = newPos;
                }
            }
        }
        return pos;
    }

    protected int matchString(String text, int start, int field, String[] data, Calendar cal2) {
        return this.matchString(text, start, field, data, null, cal2);
    }

    protected int matchString(String text, int start, int field, String[] data, String monthPattern, Calendar cal2) {
        int i2 = 0;
        int count = data.length;
        if (field == 7) {
            i2 = 1;
        }
        int bestMatchLength = 0;
        int bestMatch = -1;
        int isLeapMonth = 0;
        int matchLength = 0;
        while (i2 < count) {
            int length = data[i2].length();
            if (length > bestMatchLength && (matchLength = this.regionMatchesWithOptionalDot(text, start, data[i2], length)) >= 0) {
                bestMatch = i2;
                bestMatchLength = matchLength;
                isLeapMonth = 0;
            }
            if (monthPattern != null) {
                String leapMonthName = MessageFormat.format(monthPattern, data[i2]);
                length = leapMonthName.length();
                if (length > bestMatchLength && (matchLength = this.regionMatchesWithOptionalDot(text, start, leapMonthName, length)) >= 0) {
                    bestMatch = i2;
                    bestMatchLength = matchLength;
                    isLeapMonth = 1;
                }
            }
            ++i2;
        }
        if (bestMatch >= 0) {
            if (field == 1) {
                ++bestMatch;
            }
            cal2.set(field, bestMatch);
            if (monthPattern != null) {
                cal2.set(22, isLeapMonth);
            }
            return start + bestMatchLength;
        }
        return -start;
    }

    private int regionMatchesWithOptionalDot(String text, int start, String data, int length) {
        boolean matches = text.regionMatches(true, start, data, 0, length);
        if (matches) {
            return length;
        }
        if (data.length() > 0 && data.charAt(data.length() - 1) == '.' && text.regionMatches(true, start, data, 0, length - 1)) {
            return length - 1;
        }
        return -1;
    }

    protected int matchQuarterString(String text, int start, int field, String[] data, Calendar cal2) {
        int count = data.length;
        int bestMatchLength = 0;
        int bestMatch = -1;
        int matchLength = 0;
        for (int i2 = 0; i2 < count; ++i2) {
            int length = data[i2].length();
            if (length <= bestMatchLength || (matchLength = this.regionMatchesWithOptionalDot(text, start, data[i2], length)) < 0) continue;
            bestMatch = i2;
            bestMatchLength = matchLength;
        }
        if (bestMatch >= 0) {
            cal2.set(field, bestMatch * 3);
            return start + bestMatchLength;
        }
        return -start;
    }

    protected int subParse(String text, int start, char ch2, int count, boolean obeyCount, boolean allowNegative, boolean[] ambiguousYear, Calendar cal2) {
        return this.subParse(text, start, ch2, count, obeyCount, allowNegative, ambiguousYear, cal2, null);
    }

    protected int subParse(String text, int start, char ch2, int count, boolean obeyCount, boolean allowNegative, boolean[] ambiguousYear, Calendar cal2, MessageFormat numericLeapMonthFormatter) {
        Number number = null;
        NumberFormat currentNumberFormat = null;
        int value = 0;
        ParsePosition pos = new ParsePosition(0);
        boolean lenient = this.isLenient();
        int patternCharIndex = -1;
        if ('A' <= ch2 && ch2 <= 'z') {
            patternCharIndex = PATTERN_CHAR_TO_INDEX[ch2 - 64];
        }
        if (patternCharIndex == -1) {
            return -start;
        }
        currentNumberFormat = this.getNumberFormat(ch2);
        int field = PATTERN_INDEX_TO_CALENDAR_FIELD[patternCharIndex];
        if (numericLeapMonthFormatter != null) {
            numericLeapMonthFormatter.setFormatByArgumentIndex(0, currentNumberFormat);
        }
        while (true) {
            if (start >= text.length()) {
                return -start;
            }
            int c2 = UTF16.charAt(text, start);
            if (!UCharacter.isUWhiteSpace(c2) || !PatternProps.isWhiteSpace(c2)) break;
            start += UTF16.getCharCount(c2);
        }
        pos.setIndex(start);
        if (patternCharIndex == 4 || patternCharIndex == 15 || patternCharIndex == 2 && count <= 2 || patternCharIndex == 26 && count <= 2 || patternCharIndex == 1 || patternCharIndex == 18 || patternCharIndex == 30 || patternCharIndex == 0 && cal2.getType().equals("chinese") || patternCharIndex == 8) {
            boolean parsedNumericLeapMonth = false;
            if (numericLeapMonthFormatter != null && (patternCharIndex == 2 || patternCharIndex == 26)) {
                Object[] args = numericLeapMonthFormatter.parse(text, pos);
                if (args != null && pos.getIndex() > start && args[0] instanceof Number) {
                    parsedNumericLeapMonth = true;
                    number = (Number)args[0];
                    cal2.set(22, 1);
                } else {
                    pos.setIndex(start);
                    cal2.set(22, 0);
                }
            }
            if (!parsedNumericLeapMonth) {
                if (obeyCount) {
                    if (start + count > text.length()) {
                        return -start;
                    }
                    number = this.parseInt(text, count, pos, allowNegative, currentNumberFormat);
                } else {
                    number = this.parseInt(text, pos, allowNegative, currentNumberFormat);
                }
                if (number == null && patternCharIndex != 30) {
                    return -start;
                }
            }
            if (number != null) {
                value = number.intValue();
            }
        }
        switch (patternCharIndex) {
            case 0: {
                if (cal2.getType().equals("chinese")) {
                    cal2.set(0, value);
                    return pos.getIndex();
                }
                int ps2 = 0;
                ps2 = count == 5 ? this.matchString(text, start, 0, this.formatData.narrowEras, null, cal2) : (count == 4 ? this.matchString(text, start, 0, this.formatData.eraNames, null, cal2) : this.matchString(text, start, 0, this.formatData.eras, null, cal2));
                if (ps2 == -start) {
                    ps2 = -32000;
                }
                return ps2;
            }
            case 1: 
            case 18: {
                if (this.override != null && (this.override.compareTo("hebr") == 0 || this.override.indexOf("y=hebr") >= 0) && value < 1000) {
                    value += 5000;
                } else if (count == 2 && pos.getIndex() - start == 2 && !cal2.getType().equals("chinese") && UCharacter.isDigit(text.charAt(start)) && UCharacter.isDigit(text.charAt(start + 1))) {
                    int ambiguousTwoDigitYear = this.getDefaultCenturyStartYear() % 100;
                    ambiguousYear[0] = value == ambiguousTwoDigitYear;
                    value += this.getDefaultCenturyStartYear() / 100 * 100 + (value < ambiguousTwoDigitYear ? 100 : 0);
                }
                cal2.set(field, value);
                if (DelayedHebrewMonthCheck) {
                    if (!HebrewCalendar.isLeapYear(value)) {
                        cal2.add(2, 1);
                    }
                    DelayedHebrewMonthCheck = false;
                }
                return pos.getIndex();
            }
            case 30: {
                int newStart;
                if (this.formatData.shortYearNames != null && (newStart = this.matchString(text, start, 1, this.formatData.shortYearNames, null, cal2)) > 0) {
                    return newStart;
                }
                if (number != null && (lenient || this.formatData.shortYearNames == null || value > this.formatData.shortYearNames.length)) {
                    cal2.set(1, value);
                    return pos.getIndex();
                }
                return -start;
            }
            case 2: 
            case 26: {
                int newStart;
                boolean haveMonthPat;
                if (count <= 2) {
                    cal2.set(2, value - 1);
                    if (cal2.getType().equals("hebrew") && value >= 6) {
                        if (cal2.isSet(1)) {
                            if (!HebrewCalendar.isLeapYear(cal2.get(1))) {
                                cal2.set(2, value);
                            }
                        } else {
                            DelayedHebrewMonthCheck = true;
                        }
                    }
                    return pos.getIndex();
                }
                boolean bl2 = haveMonthPat = this.formatData.leapMonthPatterns != null && this.formatData.leapMonthPatterns.length >= 7;
                int n2 = patternCharIndex == 2 ? this.matchString(text, start, 2, this.formatData.months, haveMonthPat ? this.formatData.leapMonthPatterns[0] : null, cal2) : (newStart = this.matchString(text, start, 2, this.formatData.standaloneMonths, haveMonthPat ? this.formatData.leapMonthPatterns[3] : null, cal2));
                if (newStart > 0) {
                    return newStart;
                }
                return patternCharIndex == 2 ? this.matchString(text, start, 2, this.formatData.shortMonths, haveMonthPat ? this.formatData.leapMonthPatterns[1] : null, cal2) : this.matchString(text, start, 2, this.formatData.standaloneShortMonths, haveMonthPat ? this.formatData.leapMonthPatterns[4] : null, cal2);
            }
            case 4: {
                if (value == cal2.getMaximum(11) + 1) {
                    value = 0;
                }
                cal2.set(11, value);
                return pos.getIndex();
            }
            case 8: {
                int i2;
                if (i2 < 3) {
                    for (i2 = pos.getIndex() - start; i2 < 3; ++i2) {
                        value *= 10;
                    }
                } else {
                    int a2 = 1;
                    while (i2 > 3) {
                        a2 *= 10;
                        --i2;
                    }
                    value /= a2;
                }
                cal2.set(14, value);
                return pos.getIndex();
            }
            case 9: {
                int newStart = this.matchString(text, start, 7, this.formatData.weekdays, null, cal2);
                if (newStart > 0) {
                    return newStart;
                }
                newStart = this.matchString(text, start, 7, this.formatData.shortWeekdays, null, cal2);
                if (newStart > 0) {
                    return newStart;
                }
                if (this.formatData.shorterWeekdays != null) {
                    return this.matchString(text, start, 7, this.formatData.shorterWeekdays, null, cal2);
                }
                return newStart;
            }
            case 25: {
                int newStart = this.matchString(text, start, 7, this.formatData.standaloneWeekdays, null, cal2);
                if (newStart > 0) {
                    return newStart;
                }
                newStart = this.matchString(text, start, 7, this.formatData.standaloneShortWeekdays, null, cal2);
                if (newStart > 0) {
                    return newStart;
                }
                if (this.formatData.standaloneShorterWeekdays != null) {
                    return this.matchString(text, start, 7, this.formatData.standaloneShorterWeekdays, null, cal2);
                }
                return newStart;
            }
            case 14: {
                return this.matchString(text, start, 9, this.formatData.ampms, null, cal2);
            }
            case 15: {
                if (value == cal2.getLeastMaximum(10) + 1) {
                    value = 0;
                }
                cal2.set(10, value);
                return pos.getIndex();
            }
            case 17: {
                Output<TimeZoneFormat.TimeType> tzTimeType = new Output<TimeZoneFormat.TimeType>();
                TimeZoneFormat.Style style = count < 4 ? TimeZoneFormat.Style.SPECIFIC_SHORT : TimeZoneFormat.Style.SPECIFIC_LONG;
                TimeZone tz2 = this.tzFormat().parse(style, text, pos, tzTimeType);
                if (tz2 != null) {
                    this.tztype = (TimeZoneFormat.TimeType)((Object)tzTimeType.value);
                    cal2.setTimeZone(tz2);
                    return pos.getIndex();
                }
                return -start;
            }
            case 23: {
                Output<TimeZoneFormat.TimeType> tzTimeType = new Output();
                TimeZoneFormat.Style style = count < 4 ? TimeZoneFormat.Style.ISO_BASIC_LOCAL_FULL : (count == 5 ? TimeZoneFormat.Style.ISO_EXTENDED_FULL : TimeZoneFormat.Style.LOCALIZED_GMT);
                TimeZone tz3 = this.tzFormat().parse(style, text, pos, tzTimeType);
                if (tz3 != null) {
                    this.tztype = (TimeZoneFormat.TimeType)((Object)tzTimeType.value);
                    cal2.setTimeZone(tz3);
                    return pos.getIndex();
                }
                return -start;
            }
            case 24: {
                Output<TimeZoneFormat.TimeType> tzTimeType = new Output();
                TimeZoneFormat.Style style = count < 4 ? TimeZoneFormat.Style.GENERIC_SHORT : TimeZoneFormat.Style.GENERIC_LONG;
                TimeZone tz4 = this.tzFormat().parse(style, text, pos, tzTimeType);
                if (tz4 != null) {
                    this.tztype = (TimeZoneFormat.TimeType)((Object)tzTimeType.value);
                    cal2.setTimeZone(tz4);
                    return pos.getIndex();
                }
                return -start;
            }
            case 29: {
                Output<TimeZoneFormat.TimeType> tzTimeType = new Output();
                TimeZoneFormat.Style style = null;
                switch (count) {
                    case 1: {
                        style = TimeZoneFormat.Style.ZONE_ID_SHORT;
                        break;
                    }
                    case 2: {
                        style = TimeZoneFormat.Style.ZONE_ID;
                        break;
                    }
                    case 3: {
                        style = TimeZoneFormat.Style.EXEMPLAR_LOCATION;
                        break;
                    }
                    default: {
                        style = TimeZoneFormat.Style.GENERIC_LOCATION;
                    }
                }
                TimeZone tz5 = this.tzFormat().parse(style, text, pos, tzTimeType);
                if (tz5 != null) {
                    this.tztype = (TimeZoneFormat.TimeType)((Object)tzTimeType.value);
                    cal2.setTimeZone(tz5);
                    return pos.getIndex();
                }
                return -start;
            }
            case 31: {
                Output<TimeZoneFormat.TimeType> tzTimeType = new Output();
                TimeZoneFormat.Style style = count < 4 ? TimeZoneFormat.Style.LOCALIZED_GMT_SHORT : TimeZoneFormat.Style.LOCALIZED_GMT;
                TimeZone tz6 = this.tzFormat().parse(style, text, pos, tzTimeType);
                if (tz6 != null) {
                    this.tztype = (TimeZoneFormat.TimeType)((Object)tzTimeType.value);
                    cal2.setTimeZone(tz6);
                    return pos.getIndex();
                }
                return -start;
            }
            case 32: {
                TimeZoneFormat.Style style;
                Output<TimeZoneFormat.TimeType> tzTimeType = new Output();
                switch (count) {
                    case 1: {
                        style = TimeZoneFormat.Style.ISO_BASIC_SHORT;
                        break;
                    }
                    case 2: {
                        style = TimeZoneFormat.Style.ISO_BASIC_FIXED;
                        break;
                    }
                    case 3: {
                        style = TimeZoneFormat.Style.ISO_EXTENDED_FIXED;
                        break;
                    }
                    case 4: {
                        style = TimeZoneFormat.Style.ISO_BASIC_FULL;
                        break;
                    }
                    default: {
                        style = TimeZoneFormat.Style.ISO_EXTENDED_FULL;
                    }
                }
                TimeZone tz7 = this.tzFormat().parse(style, text, pos, tzTimeType);
                if (tz7 != null) {
                    this.tztype = (TimeZoneFormat.TimeType)((Object)tzTimeType.value);
                    cal2.setTimeZone(tz7);
                    return pos.getIndex();
                }
                return -start;
            }
            case 33: {
                TimeZoneFormat.Style style;
                Output<TimeZoneFormat.TimeType> tzTimeType = new Output();
                switch (count) {
                    case 1: {
                        style = TimeZoneFormat.Style.ISO_BASIC_LOCAL_SHORT;
                        break;
                    }
                    case 2: {
                        style = TimeZoneFormat.Style.ISO_BASIC_LOCAL_FIXED;
                        break;
                    }
                    case 3: {
                        style = TimeZoneFormat.Style.ISO_EXTENDED_LOCAL_FIXED;
                        break;
                    }
                    case 4: {
                        style = TimeZoneFormat.Style.ISO_BASIC_LOCAL_FULL;
                        break;
                    }
                    default: {
                        style = TimeZoneFormat.Style.ISO_EXTENDED_LOCAL_FULL;
                    }
                }
                TimeZone tz8 = this.tzFormat().parse(style, text, pos, tzTimeType);
                if (tz8 != null) {
                    this.tztype = (TimeZoneFormat.TimeType)((Object)tzTimeType.value);
                    cal2.setTimeZone(tz8);
                    return pos.getIndex();
                }
                return -start;
            }
            case 27: {
                if (count <= 2) {
                    cal2.set(2, (value - 1) * 3);
                    return pos.getIndex();
                }
                int newStart = this.matchQuarterString(text, start, 2, this.formatData.quarters, cal2);
                if (newStart > 0) {
                    return newStart;
                }
                return this.matchQuarterString(text, start, 2, this.formatData.shortQuarters, cal2);
            }
            case 28: {
                if (count <= 2) {
                    cal2.set(2, (value - 1) * 3);
                    return pos.getIndex();
                }
                int newStart = this.matchQuarterString(text, start, 2, this.formatData.standaloneQuarters, cal2);
                if (newStart > 0) {
                    return newStart;
                }
                return this.matchQuarterString(text, start, 2, this.formatData.standaloneShortQuarters, cal2);
            }
        }
        if (obeyCount) {
            if (start + count > text.length()) {
                return -start;
            }
            number = this.parseInt(text, count, pos, allowNegative, currentNumberFormat);
        } else {
            number = this.parseInt(text, pos, allowNegative, currentNumberFormat);
        }
        if (number != null) {
            cal2.set(field, number.intValue());
            return pos.getIndex();
        }
        return -start;
    }

    private Number parseInt(String text, ParsePosition pos, boolean allowNegative, NumberFormat fmt) {
        return this.parseInt(text, -1, pos, allowNegative, fmt);
    }

    private Number parseInt(String text, int maxDigits, ParsePosition pos, boolean allowNegative, NumberFormat fmt) {
        int nDigits;
        Number number;
        int oldPos = pos.getIndex();
        if (allowNegative) {
            number = fmt.parse(text, pos);
        } else if (fmt instanceof DecimalFormat) {
            String oldPrefix = ((DecimalFormat)fmt).getNegativePrefix();
            ((DecimalFormat)fmt).setNegativePrefix(SUPPRESS_NEGATIVE_PREFIX);
            number = fmt.parse(text, pos);
            ((DecimalFormat)fmt).setNegativePrefix(oldPrefix);
        } else {
            boolean dateNumberFormat = fmt instanceof DateNumberFormat;
            if (dateNumberFormat) {
                ((DateNumberFormat)fmt).setParsePositiveOnly(true);
            }
            number = fmt.parse(text, pos);
            if (dateNumberFormat) {
                ((DateNumberFormat)fmt).setParsePositiveOnly(false);
            }
        }
        if (maxDigits > 0 && (nDigits = pos.getIndex() - oldPos) > maxDigits) {
            double val = number.doubleValue();
            nDigits -= maxDigits;
            while (nDigits > 0) {
                val /= 10.0;
                --nDigits;
            }
            pos.setIndex(oldPos + maxDigits);
            number = (int)val;
        }
        return number;
    }

    private String translatePattern(String pat, String from, String to2) {
        StringBuilder result = new StringBuilder();
        boolean inQuote = false;
        for (int i2 = 0; i2 < pat.length(); ++i2) {
            int ci2;
            char c2 = pat.charAt(i2);
            if (inQuote) {
                if (c2 == '\'') {
                    inQuote = false;
                }
            } else if (c2 == '\'') {
                inQuote = true;
            } else if ((c2 >= 'a' && c2 <= 'z' || c2 >= 'A' && c2 <= 'Z') && (ci2 = from.indexOf(c2)) != -1) {
                c2 = to2.charAt(ci2);
            }
            result.append(c2);
        }
        if (inQuote) {
            throw new IllegalArgumentException("Unfinished quote in pattern");
        }
        return result.toString();
    }

    public String toPattern() {
        return this.pattern;
    }

    public String toLocalizedPattern() {
        return this.translatePattern(this.pattern, "GyMdkHmsSEDFwWahKzYeugAZvcLQqVUOXx", this.formatData.localPatternChars);
    }

    public void applyPattern(String pat) {
        this.pattern = pat;
        this.setLocale(null, null);
        this.patternItems = null;
    }

    public void applyLocalizedPattern(String pat) {
        this.pattern = this.translatePattern(pat, this.formatData.localPatternChars, "GyMdkHmsSEDFwWahKzYeugAZvcLQqVUOXx");
        this.setLocale(null, null);
    }

    public DateFormatSymbols getDateFormatSymbols() {
        return (DateFormatSymbols)this.formatData.clone();
    }

    public void setDateFormatSymbols(DateFormatSymbols newFormatSymbols) {
        this.formatData = (DateFormatSymbols)newFormatSymbols.clone();
    }

    protected DateFormatSymbols getSymbols() {
        return this.formatData;
    }

    public TimeZoneFormat getTimeZoneFormat() {
        return this.tzFormat().freeze();
    }

    public void setTimeZoneFormat(TimeZoneFormat tzfmt) {
        this.tzFormat = tzfmt.isFrozen() ? tzfmt : tzfmt.cloneAsThawed().freeze();
    }

    public void setContext(DisplayContext context) {
        if (context.type() == DisplayContext.Type.CAPITALIZATION) {
            this.capitalizationSetting = context;
        }
    }

    public DisplayContext getContext(DisplayContext.Type type) {
        return type == DisplayContext.Type.CAPITALIZATION && this.capitalizationSetting != null ? this.capitalizationSetting : DisplayContext.CAPITALIZATION_NONE;
    }

    @Override
    public Object clone() {
        SimpleDateFormat other = (SimpleDateFormat)super.clone();
        other.formatData = (DateFormatSymbols)this.formatData.clone();
        return other;
    }

    @Override
    public int hashCode() {
        return this.pattern.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        SimpleDateFormat that = (SimpleDateFormat)obj;
        return this.pattern.equals(that.pattern) && this.formatData.equals(that.formatData);
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        if (this.defaultCenturyStart == null) {
            this.initializeDefaultCenturyStart(this.defaultCenturyBase);
        }
        this.initializeTimeZoneFormat(false);
        stream.defaultWriteObject();
        stream.writeInt(this.capitalizationSetting.value());
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        int capitalizationSettingValue;
        stream.defaultReadObject();
        int n2 = capitalizationSettingValue = this.serialVersionOnStream > 1 ? stream.readInt() : -1;
        if (this.serialVersionOnStream < 1) {
            this.defaultCenturyBase = System.currentTimeMillis();
        } else {
            this.parseAmbiguousDatesAsAfter(this.defaultCenturyStart);
        }
        this.serialVersionOnStream = 2;
        this.locale = this.getLocale(ULocale.VALID_LOCALE);
        if (this.locale == null) {
            this.locale = ULocale.getDefault(ULocale.Category.FORMAT);
        }
        this.initLocalZeroPaddingNumberFormat();
        this.capitalizationSetting = DisplayContext.CAPITALIZATION_NONE;
        if (capitalizationSettingValue >= 0) {
            for (DisplayContext context : DisplayContext.values()) {
                if (context.value() != capitalizationSettingValue) continue;
                this.capitalizationSetting = context;
                break;
            }
        }
    }

    @Override
    public AttributedCharacterIterator formatToCharacterIterator(Object obj) {
        Calendar cal2 = this.calendar;
        if (obj instanceof Calendar) {
            cal2 = (Calendar)obj;
        } else if (obj instanceof Date) {
            this.calendar.setTime((Date)obj);
        } else if (obj instanceof Number) {
            this.calendar.setTimeInMillis(((Number)obj).longValue());
        } else {
            throw new IllegalArgumentException("Cannot format given Object as a Date");
        }
        StringBuffer toAppendTo = new StringBuffer();
        FieldPosition pos = new FieldPosition(0);
        ArrayList<FieldPosition> attributes = new ArrayList<FieldPosition>();
        this.format(cal2, this.capitalizationSetting, toAppendTo, pos, attributes);
        AttributedString as2 = new AttributedString(toAppendTo.toString());
        for (int i2 = 0; i2 < attributes.size(); ++i2) {
            FieldPosition fp = (FieldPosition)attributes.get(i2);
            Format.Field attribute = fp.getFieldAttribute();
            as2.addAttribute(attribute, attribute, fp.getBeginIndex(), fp.getEndIndex());
        }
        return as2.getIterator();
    }

    ULocale getLocale() {
        return this.locale;
    }

    boolean isFieldUnitIgnored(int field) {
        return SimpleDateFormat.isFieldUnitIgnored(this.pattern, field);
    }

    static boolean isFieldUnitIgnored(String pattern, int field) {
        int level;
        int fieldLevel = CALENDAR_FIELD_TO_LEVEL[field];
        boolean inQuote = false;
        char prevCh = '\u0000';
        int count = 0;
        for (int i2 = 0; i2 < pattern.length(); ++i2) {
            char ch2 = pattern.charAt(i2);
            if (ch2 != prevCh && count > 0) {
                level = PATTERN_CHAR_TO_LEVEL[prevCh - 64];
                if (fieldLevel <= level) {
                    return false;
                }
                count = 0;
            }
            if (ch2 == '\'') {
                if (i2 + 1 < pattern.length() && pattern.charAt(i2 + 1) == '\'') {
                    ++i2;
                    continue;
                }
                inQuote = !inQuote;
                continue;
            }
            if (inQuote || (ch2 < 'a' || ch2 > 'z') && (ch2 < 'A' || ch2 > 'Z')) continue;
            prevCh = ch2;
            ++count;
        }
        return count <= 0 || fieldLevel > (level = PATTERN_CHAR_TO_LEVEL[prevCh - 64]);
    }

    public final StringBuffer intervalFormatByAlgorithm(Calendar fromCalendar, Calendar toCalendar, StringBuffer appendTo, FieldPosition pos) throws IllegalArgumentException {
        PatternItem item;
        int i2;
        if (!fromCalendar.isEquivalentTo(toCalendar)) {
            throw new IllegalArgumentException("can not format on two different calendars");
        }
        Object[] items = this.getPatternItems();
        int diffBegin = -1;
        int diffEnd = -1;
        try {
            int i3;
            for (i3 = 0; i3 < items.length; ++i3) {
                if (!this.diffCalFieldValue(fromCalendar, toCalendar, items, i3)) continue;
                diffBegin = i3;
                break;
            }
            if (diffBegin == -1) {
                return this.format(fromCalendar, appendTo, pos);
            }
            for (i3 = items.length - 1; i3 >= diffBegin; --i3) {
                if (!this.diffCalFieldValue(fromCalendar, toCalendar, items, i3)) continue;
                diffEnd = i3;
                break;
            }
        }
        catch (IllegalArgumentException e2) {
            throw new IllegalArgumentException(e2.toString());
        }
        if (diffBegin == 0 && diffEnd == items.length - 1) {
            this.format(fromCalendar, appendTo, pos);
            appendTo.append(" \u2013 ");
            this.format(toCalendar, appendTo, pos);
            return appendTo;
        }
        int highestLevel = 1000;
        for (i2 = diffBegin; i2 <= diffEnd; ++i2) {
            if (items[i2] instanceof String) continue;
            item = (PatternItem)items[i2];
            char ch2 = item.type;
            int patternCharIndex = -1;
            if ('A' <= ch2 && ch2 <= 'z') {
                patternCharIndex = PATTERN_CHAR_TO_LEVEL[ch2 - 64];
            }
            if (patternCharIndex == -1) {
                throw new IllegalArgumentException("Illegal pattern character '" + ch2 + "' in \"" + this.pattern + '\"');
            }
            if (patternCharIndex >= highestLevel) continue;
            highestLevel = patternCharIndex;
        }
        try {
            for (i2 = 0; i2 < diffBegin; ++i2) {
                if (!this.lowerLevel(items, i2, highestLevel)) continue;
                diffBegin = i2;
                break;
            }
            for (i2 = items.length - 1; i2 > diffEnd; --i2) {
                if (!this.lowerLevel(items, i2, highestLevel)) continue;
                diffEnd = i2;
                break;
            }
        }
        catch (IllegalArgumentException e3) {
            throw new IllegalArgumentException(e3.toString());
        }
        if (diffBegin == 0 && diffEnd == items.length - 1) {
            this.format(fromCalendar, appendTo, pos);
            appendTo.append(" \u2013 ");
            this.format(toCalendar, appendTo, pos);
            return appendTo;
        }
        pos.setBeginIndex(0);
        pos.setEndIndex(0);
        for (i2 = 0; i2 <= diffEnd; ++i2) {
            if (items[i2] instanceof String) {
                appendTo.append((String)items[i2]);
                continue;
            }
            item = (PatternItem)items[i2];
            if (this.useFastFormat) {
                this.subFormat(appendTo, item.type, item.length, appendTo.length(), i2, this.capitalizationSetting, pos, fromCalendar);
                continue;
            }
            appendTo.append(this.subFormat(item.type, item.length, appendTo.length(), i2, this.capitalizationSetting, pos, fromCalendar));
        }
        appendTo.append(" \u2013 ");
        for (i2 = diffBegin; i2 < items.length; ++i2) {
            if (items[i2] instanceof String) {
                appendTo.append((String)items[i2]);
                continue;
            }
            item = (PatternItem)items[i2];
            if (this.useFastFormat) {
                this.subFormat(appendTo, item.type, item.length, appendTo.length(), i2, this.capitalizationSetting, pos, toCalendar);
                continue;
            }
            appendTo.append(this.subFormat(item.type, item.length, appendTo.length(), i2, this.capitalizationSetting, pos, toCalendar));
        }
        return appendTo;
    }

    private boolean diffCalFieldValue(Calendar fromCalendar, Calendar toCalendar, Object[] items, int i2) throws IllegalArgumentException {
        int value_2;
        if (items[i2] instanceof String) {
            return false;
        }
        PatternItem item = (PatternItem)items[i2];
        char ch2 = item.type;
        int patternCharIndex = -1;
        if ('A' <= ch2 && ch2 <= 'z') {
            patternCharIndex = PATTERN_CHAR_TO_INDEX[ch2 - 64];
        }
        if (patternCharIndex == -1) {
            throw new IllegalArgumentException("Illegal pattern character '" + ch2 + "' in \"" + this.pattern + '\"');
        }
        int field = PATTERN_INDEX_TO_CALENDAR_FIELD[patternCharIndex];
        int value = fromCalendar.get(field);
        return value != (value_2 = toCalendar.get(field));
    }

    private boolean lowerLevel(Object[] items, int i2, int level) throws IllegalArgumentException {
        if (items[i2] instanceof String) {
            return false;
        }
        PatternItem item = (PatternItem)items[i2];
        char ch2 = item.type;
        int patternCharIndex = -1;
        if ('A' <= ch2 && ch2 <= 'z') {
            patternCharIndex = PATTERN_CHAR_TO_LEVEL[ch2 - 64];
        }
        if (patternCharIndex == -1) {
            throw new IllegalArgumentException("Illegal pattern character '" + ch2 + "' in \"" + this.pattern + '\"');
        }
        return patternCharIndex >= level;
    }

    protected NumberFormat getNumberFormat(char ch2) {
        Character ovrField = Character.valueOf(ch2);
        if (this.overrideMap != null && this.overrideMap.containsKey(ovrField)) {
            String nsName = this.overrideMap.get(ovrField).toString();
            NumberFormat nf2 = this.numberFormatters.get(nsName);
            return nf2;
        }
        return this.numberFormat;
    }

    private void initNumberFormatters(ULocale loc) {
        this.numberFormatters = new HashMap();
        this.overrideMap = new HashMap();
        this.processOverrideString(loc, this.override);
    }

    private void processOverrideString(ULocale loc, String str) {
        if (str == null || str.length() == 0) {
            return;
        }
        int start = 0;
        boolean moreToProcess = true;
        while (moreToProcess) {
            boolean fullOverride;
            String nsName;
            int end;
            int delimiterPosition = str.indexOf(";", start);
            if (delimiterPosition == -1) {
                moreToProcess = false;
                end = str.length();
            } else {
                end = delimiterPosition;
            }
            String currentString = str.substring(start, end);
            int equalSignPosition = currentString.indexOf("=");
            if (equalSignPosition == -1) {
                nsName = currentString;
                fullOverride = true;
            } else {
                nsName = currentString.substring(equalSignPosition + 1);
                Character ovrField = Character.valueOf(currentString.charAt(0));
                this.overrideMap.put(ovrField, nsName);
                fullOverride = false;
            }
            ULocale ovrLoc = new ULocale(loc.getBaseName() + "@numbers=" + nsName);
            NumberFormat nf2 = NumberFormat.createInstance(ovrLoc, 0);
            nf2.setGroupingUsed(false);
            if (fullOverride) {
                this.setNumberFormat(nf2);
            } else {
                this.useLocalZeroPaddingNumberFormat = false;
            }
            if (!this.numberFormatters.containsKey(nsName)) {
                this.numberFormatters.put(nsName, nf2);
            }
            start = delimiterPosition + 1;
        }
    }

    private static class PatternItem {
        final char type;
        final int length;
        final boolean isNumeric;

        PatternItem(char type, int length) {
            this.type = type;
            this.length = length;
            this.isNumeric = SimpleDateFormat.isNumeric(type, length);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static enum ContextValue {
        UNKNOWN,
        CAPITALIZATION_FOR_MIDDLE_OF_SENTENCE,
        CAPITALIZATION_FOR_BEGINNING_OF_SENTENCE,
        CAPITALIZATION_FOR_UI_LIST_OR_MENU,
        CAPITALIZATION_FOR_STANDALONE;

    }
}

