package de.jpaw.bonaparte.i18n;

import java.time.format.FormatStyle;
import java.util.Locale;

import com.ibm.icu.util.ULocale;

import de.jpaw.bonaparte.core.CSVConfiguration;

/** An example how to extend the CSVConfiguration builder pattern. Is not actually used. */
public class ICUCSVConfiguration extends CSVConfiguration {

    public final ULocale ulocale; // the ICU locale replacement (597 instead of 159 available Locales)

    public ICUCSVConfiguration(String separator, Character quote, String quoteReplacement, String ctrlReplacement, boolean datesQuoted, boolean removePoint4BD,
            String mapStart, String mapEnd, String arrayStart, String arrayEnd, String objectStart, String objectEnd, String booleanTrue, String booleanFalse,
            Locale locale, FormatStyle dateStyle, FormatStyle timeStyle,
            String customDayFormat, String customTimeFormat, String customTimeWithMsFormat, String customTimestampFormat, String customTimestampWithMsFormat,
            ULocale ulocale) {
        super(separator, quote, quoteReplacement, ctrlReplacement, datesQuoted, removePoint4BD, mapStart, mapEnd, arrayStart, arrayEnd, objectStart, objectEnd,
                booleanTrue, booleanFalse, locale, null, dateStyle, timeStyle,
                customDayFormat, customTimeFormat, customTimeWithMsFormat, customTimestampFormat, customTimestampWithMsFormat,
                false, false, false);
        this.ulocale = ulocale;
    }

    /** Creates a new CSVConfiguration.Builder based on the current object. */
    @Override
    public Builder builder() {
        return new Builder(this);
    }

    /** Builder for the configuration */
    public static class Builder extends CSVConfiguration.Builder {
        private ULocale ulocale; // used to determine date format

        protected final void xferBaseClass(ICUCSVConfiguration cfg) {
            super.xferBaseClass(cfg);
            this.ulocale = cfg.ulocale;
        }

        /** Creates a new CSVConfiguration.Builder with default settings. */
        public Builder() {
            xferBaseClass(CSV_DEFAULT_CONFIGURATION);
            this.ulocale = ULocale.US;
        }

        /** Creates a new CSVConfiguration.Builder from some existing configuration. */
        public Builder(ICUCSVConfiguration cfg) {
            xferBaseClass(cfg);
        }

        /** Creates a new CSVConfiguration.Builder (as a factory method). */
        public static Builder from(ICUCSVConfiguration cfg) {
            return new Builder(cfg);
        }

        /** Constructs the CSVConfiguration from the data collected so far */
        @Override
        public ICUCSVConfiguration build() {
            return new ICUCSVConfiguration(separator, quote, quoteReplacement, ctrlReplacement, datesQuoted, removePoint4BD, mapStart, mapEnd, arrayStart,
                    arrayEnd, objectStart, objectEnd, booleanTrue, booleanFalse, locale, dateStyle, timeStyle,
                    customDayFormat, customTimeFormat, customTimeWithMsFormat, customTimestampFormat, customTimestampWithMsFormat, ulocale);
        }

        // now the individual builder setters follow
        public Builder forULocale(ULocale ulocale) {
            this.ulocale = ulocale;
            return this;
        }

    }

}
