package de.jpaw.bonaparte.core;

import java.util.Locale;

public class CSVConfiguration {
    public final static String EMPTY = "";         // used instead of null Strings
    
    public final String separator;         // delimiter between fields
    public final Character quote;          // quote character for strings, null means no quotes used
    public final String quoteReplacement;  // string to insert for quotes inside the string
    public final String ctrlReplacement;   // string to insert for control characters
    public final boolean datesQuoted;      // are date fields quoted or not?
    public final String decimalPoint;      // . or ,
    public final String arrayStart;        // string to output for array start, List<> and Set<>
    public final String arrayEnd;          // string to output for array end
    public final String mapStart;          // string to output for map start
    public final String mapEnd;            // string to output for map end
    public final String objectStart;       // string to output for array start
    public final String objectEnd;         // string to output for array end
    public final String booleanTrue;       // string to output for boolean true value
    public final String booleanFalse;      // string to output for boolean false value
    public final Locale locale;             // used to determine date format
    public final CSVStyle dateStyle;        // verbosity for day output
    public final CSVStyle timeStyle;        // verbosity for time output
    
    public final static CSVConfiguration CSV_DEFAULT_CONFIGURATION = new CSVConfiguration(
            ";", '\"', "'", "?", false, ".", null, null, null, null, null, null, "1", "0", Locale.ROOT, CSVStyle.SHORT, CSVStyle.SHORT);    

    public static final String nvl(String s) {
        return s != null ? s : EMPTY;
    }
    public CSVConfiguration(String separator, Character quote, String quoteReplacement, String ctrlReplacement, boolean datesQuoted, String decimalPoint,
            String mapStart, String mapEnd, String arrayStart, String arrayEnd, String objectStart, String objectEnd, String booleanTrue, String booleanFalse,
            Locale locale, CSVStyle dateStyle, CSVStyle timeStyle) {
        this.separator = nvl(separator);
        this.quote = quote;
        this.quoteReplacement = nvl(quoteReplacement);
        this.ctrlReplacement = nvl(ctrlReplacement);
        this.datesQuoted = datesQuoted;
        this.decimalPoint = nvl(decimalPoint);
        this.arrayStart = nvl(arrayStart);
        this.arrayEnd = nvl(arrayEnd);
        this.mapStart = nvl(mapStart);
        this.mapEnd = nvl(mapEnd);
        this.objectStart = nvl(objectStart);
        this.objectEnd = nvl(objectEnd);
        this.booleanTrue = nvl(booleanTrue);
        this.booleanFalse = nvl(booleanFalse);
        this.locale = locale;
        this.dateStyle = dateStyle;
        this.timeStyle = timeStyle;
    }
    
    /** Creates a new CSVConfiguration.Builder based on the current object. */
    public Builder builder() {
        return new Builder(this);
    }
    
    /** Builder for the configuration */
    public static class Builder {
        private String separator;         // delimiter between fields
        private Character quote;          // quote character for strings
        private String quoteReplacement;  // string to insert for quotes inside the string
        private String ctrlReplacement;   // string to insert for control characters
        private boolean datesQuoted;      // are date fields quoted or not?
        private String decimalPoint;      // Normally "." or ",", but a setting of empty is valid and will result in numbers scaled, as required for some older interfaces.
        private String arrayStart;        // string to output for array start, List<> and Set<>
        private String arrayEnd;          // string to output for array end
        private String mapStart;          // string to output for map start
        private String mapEnd;            // string to output for map end
        private String objectStart;       // string to output for array start
        private String objectEnd;         // string to output for array end
        private String booleanTrue;       // string to output for boolean true value
        private String booleanFalse;      // string to output for boolean false value
        private Locale locale;             // used to determine date format
        private CSVStyle dateStyle;        // verbosity for day output
        private CSVStyle timeStyle;        // verbosity for time output
        
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
            this.decimalPoint = cfg.decimalPoint;
            this.arrayStart = cfg.arrayStart;
            this.arrayEnd = cfg.arrayEnd;
            this.mapStart = cfg.mapStart;
            this.mapEnd = cfg.mapEnd;
            this.objectStart = cfg.objectStart;
            this.objectEnd = cfg.objectEnd;
            this.booleanTrue = cfg.booleanTrue;
            this.booleanFalse = cfg.booleanFalse;
            this.locale = cfg.locale;
            this.dateStyle = cfg.dateStyle;
            this.timeStyle = cfg.timeStyle;
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
            return new CSVConfiguration(separator, quote, quoteReplacement, ctrlReplacement, datesQuoted, decimalPoint,
                    mapStart, mapEnd, arrayStart, arrayEnd, objectStart, objectEnd, booleanTrue, booleanFalse,
                    locale, dateStyle, timeStyle);
        }
        
        // now the individual builder setters follow
        public Builder forLocale(Locale locale) {
            this.locale = locale;
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
        public Builder usingDecimalPoint(String decimalPoint) {
            this.decimalPoint = decimalPoint;
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
    }
}
