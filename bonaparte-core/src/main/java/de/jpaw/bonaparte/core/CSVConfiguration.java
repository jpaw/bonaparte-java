package de.jpaw.bonaparte.core;

import java.util.Locale;

import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSVConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(CSVConfiguration.class);
    
    public final static String EMPTY_STRING = "";                           // used instead of null Strings
    public final static String DEFAULT_DAY_FORMAT = "yyyyMMdd";             // default pattern for LocalDate (bonaparte Day) outputs
    public final static String DEFAULT_TIME_FORMAT = "HHmmss";              // default pattern for LocalTime (bonaparte Time(0)) outputs
    public final static String DEFAULT_TIME_WITH_MS_FORMAT = "HHmmssSSS";   // default pattern for LocalTime (bonaparte Time(3)) outputs
    public final static String DEFAULT_TIMESTAMP_FORMAT = "yyyyMMddHHmmss"; // default pattern for LocalDatetime (bonaparte Timestamp(0)) outputs
    public final static String DEFAULT_TS_WITH_MS_FORMAT = "yyyyMMddHHmmssSSS"; // default pattern for LocalDatetime (bonaparte Timestamp(3)) outputs

    public final String separator;          // delimiter between fields, if null or empty, then a special composer will generate fixed-width formats (for example SAP IDOC)
    public final Character quote;           // quote character for strings, null means no quotes used
    public final String quoteReplacement;   // string to insert for quotes inside the string
    public final String ctrlReplacement;    // string to insert for control characters
    public final boolean datesQuoted;       // are date fields quoted or not?
    public final boolean removePoint4BD;    // SPECIAL: remove decimal point for BigDecimal output, to support some specific interfaces
    public final boolean zeroPadNumbers;    // SPECIAL: (only if separator is null or empty (= fixed width format): left-pad numeric values with zeroes instead of spaces
    public final boolean rightPadNumbers;   // SPECIAL: (only if separator is null or empty (= fixed width format): right-pad numeric values (Decimal or Number)
    public final boolean useGrouping;       // SPECIAL: use decimal grouping
    public final String arrayStart;         // string to output for array start, List<> and Set<>
    public final String arrayEnd;           // string to output for array end
    public final String mapStart;           // string to output for map start
    public final String mapEnd;             // string to output for map end
    public final String objectStart;        // string to output for array start
    public final String objectEnd;          // string to output for array end
    public final String booleanTrue;        // string to output for boolean true value
    public final String booleanFalse;       // string to output for boolean false value
    public final Locale locale;             // used to determine date format
    public final DateTimeZone timeZone;     // the timezone to use (for special output formatters). Will use UTC if null.
    public final CSVStyle dateStyle;        // verbosity for day output
    public final CSVStyle timeStyle;        // verbosity for time output
    public final String customDayFormat;             // set to override locale specific LocalDate formatter
    public final String customTimeFormat;            // set to override locale specific LocalTime formatter
    public final String customTimeWithMsFormat;      // set to override locale specific LocalTime formatter if milliseconds should be printed
    public final String customTimestampFormat;       // set to override locale specific LocalDateTime formatter
    public final String customTimestampWithMsFormat; // set to override locale specific LocalDateTime formatter if milliseconds should be printed

    public final static CSVConfiguration CSV_DEFAULT_CONFIGURATION = new CSVConfiguration(
            ";", '\"', "\"\"", "?", false, false, null, null, null, null, null, null, "1", "0", Locale.ROOT, null, CSVStyle.SHORT, CSVStyle.SHORT,
            DEFAULT_DAY_FORMAT, DEFAULT_TIME_FORMAT, DEFAULT_TIME_WITH_MS_FORMAT, DEFAULT_TIMESTAMP_FORMAT, DEFAULT_TS_WITH_MS_FORMAT, false, false, false);

    public static final String nvl(String s) {
        return s != null ? s : EMPTY_STRING;
    }
    public CSVConfiguration(String separator, Character quote, String quoteReplacement, String ctrlReplacement, boolean datesQuoted, boolean removePoint4BD,
            String mapStart, String mapEnd, String arrayStart, String arrayEnd, String objectStart, String objectEnd, String booleanTrue, String booleanFalse,
            Locale locale, DateTimeZone timeZone, CSVStyle dateStyle, CSVStyle timeStyle,
            String customDayFormat,
            String customTimeFormat, String customTimeWithMsFormat,
            String customTimestampFormat, String customTimestampWithMsFormat,
            boolean zeroPadNumbers, boolean rightPadNumbers, boolean useGrouping) {
        this.separator = nvl(separator);
        this.quote = quote;
        this.quoteReplacement = nvl(quoteReplacement);
        this.ctrlReplacement = nvl(ctrlReplacement);
        this.datesQuoted = datesQuoted;
        this.removePoint4BD = removePoint4BD;
        this.zeroPadNumbers = zeroPadNumbers;
        this.rightPadNumbers = rightPadNumbers;
        this.useGrouping = useGrouping;
        this.arrayStart = nvl(arrayStart);
        this.arrayEnd = nvl(arrayEnd);
        this.mapStart = nvl(mapStart);
        this.mapEnd = nvl(mapEnd);
        this.objectStart = nvl(objectStart);
        this.objectEnd = nvl(objectEnd);
        this.booleanTrue = nvl(booleanTrue);
        this.booleanFalse = nvl(booleanFalse);
        this.locale = locale;
        this.timeZone = timeZone;
        this.dateStyle = dateStyle;
        this.timeStyle = timeStyle;
        this.customDayFormat = customDayFormat;
        this.customTimeFormat = customTimeFormat;
        this.customTimeWithMsFormat = customTimeWithMsFormat;
        this.customTimestampFormat = customTimestampFormat;
        this.customTimestampWithMsFormat = customTimestampWithMsFormat;
    }

    /** Creates a new CSVConfiguration.Builder based on the current object. */
    public Builder builder() {
        return new Builder(this);
    }

    /** Builder for the configuration */
    public static class Builder {
        protected String separator;           // delimiter between fields
        protected Character quote;            // quote character for strings
        protected String quoteReplacement;    // string to insert for quotes inside the string
        protected String ctrlReplacement;     // string to insert for control characters
        protected boolean datesQuoted;        // are date fields quoted or not?
        protected boolean removePoint4BD;     // SPECIAL: remove decimal point for BigDecimal output, to support some specific interfaces
        protected boolean zeroPadNumbers;     // SPECIAL: (only if separator is null or empty (= fixed width format): left-pad numeric values with zeroes instead of spaces
        protected boolean rightPadNumbers;    // SPECIAL: (only if separator is null or empty (= fixed width format): right-pad numeric values (Decimal or Number)
        protected boolean useGrouping;        // SPECIAL: use decimal grouping
        protected String arrayStart;          // string to output for array start, List<> and Set<>
        protected String arrayEnd;            // string to output for array end
        protected String mapStart;            // string to output for map start
        protected String mapEnd;              // string to output for map end
        protected String objectStart;         // string to output for array start
        protected String objectEnd;           // string to output for array end
        protected String booleanTrue;         // string to output for boolean true value
        protected String booleanFalse;        // string to output for boolean false value
        protected Locale locale;              // used to determine date format
        protected DateTimeZone timeZone;      // the timezone to use (for special output formatters). Will use UTC if null.
        protected CSVStyle dateStyle;         // verbosity for day output
        protected CSVStyle timeStyle;         // verbosity for time output
        protected String customDayFormat;             // set to override locale specific LocalDate formatter
        protected String customTimeFormat;            // set to override locale specific LocalTime formatter
        protected String customTimeWithMsFormat;      // set to override locale specific LocalTime formatter if milliseconds should be printed
        protected String customTimestampFormat;       // set to override locale specific LocalDateTime formatter
        protected String customTimestampWithMsFormat; // set to override locale specific LocalDateTime formatter if milliseconds should be printed

        /** Transfers the parameter as passed by the argument into this builder.
         * Must be final because called from a constructor.
         * @param cfg
         */
        protected final void xferBaseClass(CSVConfiguration cfg) {
            this.separator = cfg.separator;
            this.quote = cfg.quote;
            this.quoteReplacement = cfg.quoteReplacement;
            this.ctrlReplacement = cfg.ctrlReplacement;
            this.datesQuoted = cfg.datesQuoted;
            this.removePoint4BD = cfg.removePoint4BD;
            this.zeroPadNumbers = cfg.zeroPadNumbers;
            this.rightPadNumbers = cfg.rightPadNumbers;
            this.useGrouping = cfg.useGrouping;
            this.arrayStart = cfg.arrayStart;
            this.arrayEnd = cfg.arrayEnd;
            this.mapStart = cfg.mapStart;
            this.mapEnd = cfg.mapEnd;
            this.objectStart = cfg.objectStart;
            this.objectEnd = cfg.objectEnd;
            this.booleanTrue = cfg.booleanTrue;
            this.booleanFalse = cfg.booleanFalse;
            this.locale = cfg.locale;
            this.timeZone = cfg.timeZone;
            this.dateStyle = cfg.dateStyle;
            this.timeStyle = cfg.timeStyle;
            this.customDayFormat = cfg.customDayFormat;
            this.customTimeFormat = cfg.customTimeFormat;
            this.customTimeWithMsFormat = cfg.customTimeWithMsFormat;
            this.customTimestampFormat = cfg.customTimestampFormat;
            this.customTimestampWithMsFormat = cfg.customTimestampWithMsFormat;
        }
        /** Creates a new CSVConfiguration.Builder with default settings. */
        public Builder() {
            xferBaseClass(CSV_DEFAULT_CONFIGURATION);
        }

        /** Creates a new CSVConfiguration.Builder from some existing configuration. */
        public Builder(CSVConfiguration cfg) {
            xferBaseClass(cfg);
        }

        /** Creates a new CSVConfiguration.Builder (as a factory method). */
        public static Builder from(CSVConfiguration cfg) {
            return new Builder(cfg);
        }

        /** Constructs the CSVConfiguration from the data collected so far */
        public CSVConfiguration build() {
            return new CSVConfiguration(separator, quote, quoteReplacement, ctrlReplacement, datesQuoted, removePoint4BD,
                    mapStart, mapEnd, arrayStart, arrayEnd, objectStart, objectEnd, booleanTrue, booleanFalse,
                    locale, timeZone, dateStyle, timeStyle,
                    customDayFormat, customTimeFormat, customTimeWithMsFormat, customTimestampFormat, customTimestampWithMsFormat,
                    zeroPadNumbers, rightPadNumbers, useGrouping);
        }

        // now the individual builder setters follow
        public Builder forLocale(Locale locale) {
            this.locale = locale;
            return this;
        }
        public Builder forTimeZone(DateTimeZone timeZone) {
            this.timeZone = timeZone;
            return this;
        }
        public Builder arrayDelimiters(String arrayStart, String arrayEnd) {
            this.arrayStart = arrayStart;
            this.arrayEnd = arrayEnd;
            return this;
        }
        public Builder mapDelimiters(String mapStart, String mapEnd) {
            this.mapStart = mapStart;
            this.mapEnd = mapEnd;
            return this;
        }
        public Builder objectDelimiters(String objectStart, String objectEnd) {
            this.objectStart = objectStart;
            this.objectEnd = objectEnd;
            return this;
        }
        public Builder booleanTokens(String booleanTrue, String booleanFalse) {
            this.booleanTrue = booleanTrue;
            this.booleanFalse = booleanFalse;
            return this;
        }
        public Builder usingSeparator(String separator) {
            this.separator = separator;
            return this;
        }
        public Builder removeDecimalPoint(boolean removePoint4BD) {
            this.removePoint4BD = removePoint4BD;
            return this;
        }
        public Builder usingZeroPadding(boolean zeroPadNumbers) {
            this.zeroPadNumbers = zeroPadNumbers;
            return this;
        }
        public Builder usingRightPadding(boolean rightPadNumbers) {
            this.rightPadNumbers = rightPadNumbers;
            return this;
        }
        public Builder usingGrouping(boolean useGrouping) {
            this.useGrouping = useGrouping;
            return this;
        }
        public Builder usingQuoteCharacter(Character quote) {
            this.quote = quote;
            return this;
        }
        public Builder quoteReplacement(String quoteReplacement) {
            this.quoteReplacement = quoteReplacement;
            return this;
        }
        public Builder ctrlReplacement(String ctrlReplacement) {
            this.ctrlReplacement = ctrlReplacement;
            return this;
        }
        public Builder quoteDateFields(boolean datesQuoted) {
            this.datesQuoted = datesQuoted;
            return this;
        }
        public Builder dateTimeStyle(CSVStyle dateStyle, CSVStyle timeStyle) {
            this.dateStyle = dateStyle;
            this.timeStyle = timeStyle;
            return this;
        }
        /** Custom format setting. See http://joda-time.sourceforge.net/apidocs/org/joda/time/format/DateTimeFormat.html for format description. */
        public Builder setCustomDayFormat(String customDayFormat) {
            this.customDayFormat = customDayFormat;
            return this;
        }
        /** Custom format setting. See http://joda-time.sourceforge.net/apidocs/org/joda/time/format/DateTimeFormat.html for format description. */
        public Builder setCustomTimeFormats(String customTimeFormat, String customTimeWithMsFormat) {
            this.customTimeFormat = customTimeFormat;
            this.customTimeWithMsFormat = customTimeWithMsFormat;
            return this;
        }
        /** Custom format setting. See http://joda-time.sourceforge.net/apidocs/org/joda/time/format/DateTimeFormat.html for format description. */
        public Builder setCustomDayTimeFormats(String customTimestampFormat, String customTimestampWithMsFormat) {
            this.customTimestampFormat = customTimestampFormat;
            this.customTimestampWithMsFormat = customTimestampWithMsFormat;
            return this;
        }
        /** Custom format setting. See http://joda-time.sourceforge.net/apidocs/org/joda/time/format/DateTimeFormat.html for format description. */
        public Builder setCustomDayTimeFormats(String customDayFormat,
                String customTimeFormat, String customTimeWithMsFormat,
                String customTimestampFormat, String customTimestampWithMsFormat) {
            this.customDayFormat = customDayFormat;
            this.customTimeFormat = customTimeFormat;
            this.customTimeWithMsFormat = customTimeWithMsFormat;
            this.customTimestampFormat = customTimestampFormat;
            this.customTimestampWithMsFormat = customTimestampWithMsFormat;
            return this;
        }
        public Builder resetCustomDayTimeFormats() {
            this.customDayFormat = null;
            this.customTimeFormat = null;
            this.customTimeWithMsFormat = null;
            this.customTimestampFormat = null;
            this.customTimestampWithMsFormat = null;
            return this;
        }
    }
    
    // certain utility methods used by CSV parser / composers

    public DateTimeFormatter determineDayFormatter() {
        try {
            return customTimestampFormat == null
                    ? DateTimeFormat.forStyle(dateStyle.getToken() + "-")
                    : DateTimeFormat.forPattern(customDayFormat);
        } catch (IllegalArgumentException e) {
            // could occur if the user provided format is invalid
            LOGGER.error("Provided format is not valid: " + customDayFormat, e);
            return DateTimeFormat.forPattern(DEFAULT_DAY_FORMAT);
        }
    }

    public DateTimeFormatter determineTimeFormatter() {
        try {
            return customTimeFormat == null
                    ? DateTimeFormat.forStyle(timeStyle.getToken())
                    : DateTimeFormat.forPattern(customTimeFormat);
        } catch (IllegalArgumentException e) {
            // could occur if the user provided format is invalid
            LOGGER.error("Provided format is not valid: " + customTimeFormat, e);
            return DateTimeFormat.forPattern(DEFAULT_TIME_FORMAT);
        }
    }

    public DateTimeFormatter determineTime3Formatter() {
        try {
            return customTimeWithMsFormat == null
                    ? DateTimeFormat.forStyle(timeStyle.getToken())
                    : DateTimeFormat.forPattern(customTimeWithMsFormat);
        } catch (IllegalArgumentException e) {
            // could occur if the user provided format is invalid
            LOGGER.error("Provided format is not valid: " + customTimeWithMsFormat, e);
            return DateTimeFormat.forPattern(DEFAULT_TIME_WITH_MS_FORMAT);
        }
    }

    public DateTimeFormatter determineTimestampFormatter() {
        try {
            return customTimestampFormat == null
                    ? DateTimeFormat.forStyle(dateStyle.getToken() + timeStyle.getToken())
                    : DateTimeFormat.forPattern(customTimestampFormat);
        } catch (IllegalArgumentException e) {
            // could occur if the user provided format is invalid
            LOGGER.error("Provided format is not valid: " + customTimestampFormat, e);
            return DateTimeFormat.forPattern(DEFAULT_TIMESTAMP_FORMAT);
        }
    }

    public DateTimeFormatter determineTimestamp3Formatter() {
        try {
            return customTimestampWithMsFormat == null
                    ? DateTimeFormat.forStyle(dateStyle.getToken() + timeStyle.getToken())
                    : DateTimeFormat.forPattern(customTimestampWithMsFormat);
        } catch (IllegalArgumentException e) {
            // could occur if the user provided format is invalid
            LOGGER.error("Provided format is not valid: " + customTimestampWithMsFormat, e);
            return DateTimeFormat.forPattern(DEFAULT_TS_WITH_MS_FORMAT);
        }
    }
}
