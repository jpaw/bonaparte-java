package de.jpaw.bonaparte.batch;

import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

import de.jpaw.batch.api.BatchProcessor;
import de.jpaw.batch.api.BatchProcessorFactory;
import de.jpaw.batch.impl.ContributorNoop;
import de.jpaw.bonaparte.pojos.meta.AlphanumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.ClassDefinition;
import de.jpaw.bonaparte.pojos.meta.FieldDefinition;

/** A worker which analyzes CSV-type text files and outputs an (improved?) interface description.
 * This class can now operate multithreaded, it uses kind of map/reduce to merge the results. */
public class AnalyzerWorkerFactory extends ContributorNoop implements BatchProcessorFactory<String, String> {
    private static final Logger LOG = LoggerFactory.getLogger(AnalyzerWorkerFactory.class);
    private final static int MAX_DIFFERENT_VALUES = 10;    // if reaching this number, we are no longer interested in the specific values
    private final ClassDefinition meta;
    private final String separator;
    private final int [] fieldLengths;

    private final int [] calculateLengths() {
        if (separator != null) {
            return null;
        } else {
            int [] lengths = new int [meta.getNumberOfFields()];
            for (int i = 0; i < meta.getNumberOfFields(); ++i) {
                AlphanumericElementaryDataItem f = (AlphanumericElementaryDataItem)meta.getFields().get(i);
                lengths[i] = f.getLength();
            }
            return lengths;
        }
    }

    // separator must be non-null
    public AnalyzerWorkerFactory(String separator) {
        meta = null;
        this.separator = separator;
        fieldLengths = calculateLengths();
    }

    // if separator is null, meta must be a class definition with only Alphanumeric fields.
    public AnalyzerWorkerFactory(ClassDefinition meta, String separator) {
        this.meta = meta;
        this.separator = separator;
        fieldLengths = calculateLengths();
    }

    @Override
    public BatchProcessor<String, String> getProcessor(int threadNo) {
        return new AnalyzerWorker(this);
    }


    static private class Range {
        public int minVal = 999;
        public int maxVal = 0;
        public void upd(int myVal) {
            if (myVal > maxVal)
                maxVal = myVal;
            if (myVal < minVal)
                minVal = myVal;
        }
        /** Merges a second range, which may have been computed in a parallel thread. */
        public void merge(Range other) {
            if (minVal > other.minVal)
                minVal = other.minVal;
            if (maxVal < other.maxVal)
                maxVal = other.maxVal;
        }
        @Override
        public String toString() {
            return String.format("[%3d,%3d]", minVal, maxVal);
        }
    }

    static private class Statistics {
        public boolean optional = false;
        public boolean nonAscii = false;
        public boolean nonDigit = false;
        public boolean nonUpper = false;
        public boolean nonLower = false;
        public Range len = new Range();
        public Range dots = new Range();
        public Range colons = new Range();
        public Range slashes = new Range();
        public Range minusses = new Range();
        public int maxDigitsBeforeDot = 0;
        public int maxDigitsAfterDot = 0;
        public SortedSet<String> values = new TreeSet<String>();

        /** Merges a second statistics entry, which may have been computed in a parallel thread. */
        public void merge(Statistics other) {
            this.optional = this.optional || other.optional;
            this.nonAscii = this.nonAscii || other.nonAscii;
            this.nonDigit = this.nonDigit || other.nonDigit;
            this.nonUpper = this.nonUpper || other.nonUpper;
            this.nonLower = this.nonLower || other.nonLower;
            this.len.merge(other.len);
            this.dots.merge(other.dots);
            this.colons.merge(other.colons);
            this.slashes.merge(other.slashes);
            this.minusses.merge(other.minusses);
            if (this.maxDigitsBeforeDot < other.maxDigitsBeforeDot)
                this.maxDigitsBeforeDot = other.maxDigitsBeforeDot;
            if (this.maxDigitsAfterDot < other.maxDigitsAfterDot)
                this.maxDigitsAfterDot = other.maxDigitsAfterDot;
            if (this.values.size() < MAX_DIFFERENT_VALUES)  // we don't use it if bigger
                this.values.addAll(other.values);
        }
    }

    static private class AnalyzerWorker implements BatchProcessor<String, String> {
        private final AnalyzerWorkerFactory myFactory;
        private Range numFields = new Range();
        private Statistics [] columnData = new Statistics[500];
        private int warnForstringsLongerThan = 1000;
        private int lastLineWarned = 0;

        public AnalyzerWorker(AnalyzerWorkerFactory myFactory) {
            this.myFactory = myFactory;
            for (int i = 0; i < 500; ++i)
                columnData[i] = new  Statistics();
        }

        private void check(int recordNo, Statistics s, String w) {
            if (w == null) {
                s.optional = true;
                return;
            }
            w = w.trim();
            int len = w.length();
            if (len == 0) {
                s.optional = true;
                return;
            }
            if (len > warnForstringsLongerThan && recordNo > lastLineWarned) {
                LOG.info("Line {} contains a field of length {}", recordNo, len);
                lastLineWarned = recordNo;
            }
            s.len.upd(len);
            if (s.values.size() < MAX_DIFFERENT_VALUES)
                s.values.add(w);

            int dots = 0;
            int colons = 0;
            int slashes = 0;
            int minusses = 0;
            int digsBeforeDot = 0;
            int digsAfterDot = 0;
            for (int i = 0; i < len; ++i) {
                char c = w.charAt(i);
                switch (c) {
                case '-':
                    ++minusses;
                    break;
                case '.':
                    ++dots;
                    break;
                case ':':
                    ++colons;
                    break;
                case '/':
                    ++slashes;
                    break;
                default:
                    if (!Character.isDigit(c))
                        s.nonDigit = true;
                    else {
                        if (dots == 0)
                            ++digsBeforeDot;
                        else
                            ++digsAfterDot;
                    }
                    if (!Character.isLowerCase(c))
                        s.nonLower = true;
                    if (!Character.isUpperCase(c))
                        s.nonUpper = true;
                    if (c > 0x7f)
                        s.nonAscii = true;
                    break;
                }
            }
            s.dots.upd(dots);
            s.colons.upd(colons);
            s.slashes.upd(slashes);
            s.minusses.upd(minusses);
            if (digsBeforeDot > s.maxDigitsBeforeDot)
                s.maxDigitsBeforeDot = digsBeforeDot;
            if (digsAfterDot > s.maxDigitsAfterDot)
                s.maxDigitsAfterDot = digsAfterDot;
        }

        @Override
        public String process(int recordNo, String data) throws Exception {
            // split the line
            String [] cols;
            if (myFactory.separator != null) {
                cols = data.split(myFactory.separator);
            } else {
                cols = new String [myFactory.meta.getNumberOfFields()];
                int currentPos = 0;
                for (int i = 0; i < cols.length; ++i) {
                    int endPos = currentPos + myFactory.fieldLengths[i];
                    if (endPos > data.length())
                        break;   // no more fields
                    cols[i] = data.substring(currentPos, endPos);
                    currentPos = endPos;
                }
            }
            numFields.upd(cols.length);
            for (int i = 0; i < cols.length; ++i)
                check(recordNo, columnData[i], cols[i]);
            return null;
        }

        @Override
        public void close() throws Exception {
            if (numFields.maxVal == 0)
                return;   // no data received
            myFactory.merge(numFields, columnData);
        }
    }

    private Range numFields = null;
    private Statistics [] columnData = null;
    private final Object mergeLock = new Object();

    private void merge(Range numFields, Statistics [] columnData) {
        synchronized(mergeLock) {
            if (this.numFields == null) {
                // assignment
                LOG.info("initial merge");
                this.numFields = numFields;
                this.columnData = columnData;
            } else {
                LOG.info("merging result sets");
                this.numFields.merge(numFields);
                for (int i = 0; i < this.numFields.maxVal; ++i)
                    this.columnData[i].merge(columnData[i]);
            }
        }
    }

    @Override
    public void close() throws Exception {
        // close is invoked after all processors have been closed (and their results been merged),
        // so we can output the overall result now
        System.out.println("Column range is " + numFields);
        System.out.println("Col OPT Uni Dig Upp Low   length      dots  minusses   slashes     colons");
        for (int i = 0; i < numFields.maxVal; ++i) {
            Statistics s = columnData[i];
            System.out.println(String.format("%3d  %s   %s   %s   %s   %s  %s %s %s %s %s",
                    i+1,
                    b2a(s.optional), b2a(s.nonAscii), b2a(!s.nonDigit), b2a(!s.nonUpper), b2a(!s.nonLower),
                    s.len, s.dots, s.minusses, s.slashes, s.colons));
        }

        if (meta != null) {
            guessBetterDescription();
        }
    }


    private String b2a(boolean b) {
        return b ? "Y" : "N";
    }
    private String optionalLengthComment(Range len) {
        if (len.minVal < len.maxVal)
            return String.format("%d .. %d",  len.minVal, len.maxVal);
        return null;
    }

    // return the max of the len found and the original len (if the field was an alphanumeric field)
    private int getLen(FieldDefinition fld, int myLen) {
        if (fld == null || !(fld instanceof AlphanumericElementaryDataItem))
            return myLen;
        AlphanumericElementaryDataItem afld = (AlphanumericElementaryDataItem)fld;
        return afld.getLength() > myLen ? afld.getLength() : myLen;
    }

    private String listValues(SortedSet<String> samples) {
        if (samples.size() >= MAX_DIFFERENT_VALUES || samples.size() == 0)
            return "";  // makes no sense to list
        if (samples.size() == 1)
            return " // constant value " + samples.first();
        // return "  // values = { " + String.join(s.values) + " }";  // needs Java 1.8
        return "  // values = { " + Joiner.on(", ").join(samples) + " }";  // guava
    }

    private void guessBetterDescription() {
        System.out.println("    class " + meta.getName() + " {");
        for (int i = 0; i < numFields.maxVal; ++i) {
            FieldDefinition fld = i < meta.getFields().size() ? meta.getFields().get(i) : null;
            Statistics s = columnData[i];
            int defLen = getLen(fld, s.len.maxVal);
            String type = null;
            String comment = null;
            if (s.len.maxVal == 0) {
                type = String.format("Ascii(%d)", getLen(fld, 1));
                comment = "UNUSED, ALWAYS BLANK!";
            } else if (s.nonAscii) {
                type = String.format("Unicode(%d)", defLen);
                comment = optionalLengthComment(s.len);
            } else if (s.minusses.maxVal > 0 || s.dots.maxVal > 0 || s.slashes.maxVal > 0 || s.colons.maxVal > 0) {
                // signed number or date / timestamp?
                if (!s.nonDigit) {
                    if (s.minusses.maxVal <= 1 && s.dots.maxVal <= 1 && s.slashes.maxVal == 0 && s.colons.maxVal == 0) {
                        // is a number
                        if (s.dots.maxVal > 0) {
                            type = String.format("%sDecimal(%d,%d)", s.minusses.maxVal > 0 ? "signed " : "",
                                    //s.maxDigitsBeforeDot + s.maxDigitsAfterDot,
                                    getLen(fld, s.maxDigitsBeforeDot + s.maxDigitsAfterDot + 1 + s.minusses.maxVal) - 1 - s.minusses.maxVal,  // max possible digits minus decimal minus minus
                                    s.maxDigitsAfterDot);
                        } else {
                            // int digits = s.maxDigitsBeforeDot;
                            int digits = getLen(fld, s.maxDigitsBeforeDot + s.minusses.maxVal) - s.minusses.maxVal;  // max possible digits minus minus
                            type = String.format("%s%s(%d)", s.minusses.maxVal > 0 ? "signed " : "",
                                    digits > 9 ? "Decimal" : "Number",  // use an int if possible, else a (Big)Decimal
                                    digits
                                    );
                        }
                    } else if (s.len.maxVal <= 10) {
                        if (s.minusses.maxVal == 2 && s.minusses.minVal == 2 && s.dots.maxVal == 0 && s.slashes.maxVal == 0 && s.colons.maxVal == 0) {
                            type = "Day";
                            comment = "DIN format?";
                        } else if (s.dots.maxVal == 2 && s.dots.minVal == 2 && s.minusses.maxVal == 0 && s.slashes.maxVal == 0 && s.colons.maxVal == 0) {
                            type = "Day";
                            comment = "German format?";
                        } else if (s.slashes.maxVal == 2 && s.slashes.minVal == 2 && s.minusses.maxVal == 0 && s.dots.maxVal == 0 && s.colons.maxVal == 0) {
                            type = "Day";
                            comment = "US/UK format?";
                        } else if (s.colons.maxVal == 2 && s.colons.minVal == 2 && s.minusses.maxVal == 0 && s.dots.maxVal <= 1 && s.slashes.maxVal == 0) {
                            type = String.format("Time%s;", s.dots.maxVal > 0 ? "(3)" : "");
                        }
                    }
                } // else fall through (ASCII)
            } else if (!s.nonUpper) {
                type = String.format("Uppercase(%d)", defLen);
                comment = optionalLengthComment(s.len);
            } else if (!s.nonLower) {
                type = String.format("Lowercase(%d)", defLen);
                comment = optionalLengthComment(s.len);
            } else if (!s.nonDigit) {
                type = String.format("%s(%d)", defLen > 18 ? "Number" : defLen > 9 ? "Long" : "Integer", defLen);
                comment = "unsigned";
            }
            if (type == null) {
                type = String.format("Ascii(%d)", defLen);
                comment = optionalLengthComment(s.len);
            }
            System.out.println(String.format("        %s %-23s %-23s%s%s",
                    i >= numFields.minVal || s.optional ? "optional" : "required",
                    type,
                    (fld == null ? String.format("extra%03d", i+1) : fld.getName()) + ";",
                    comment == null ? "" : "// " + comment,
                    listValues(s.values)
                    ));
        }
        System.out.println("    }");
    }
}
