package de.jpaw.bonaparte.batch;

import java.util.SortedSet;
import java.util.TreeSet;

import com.google.common.base.Joiner;

import de.jpaw.bonaparte.pojos.meta.AlphanumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.ClassDefinition;
import de.jpaw.bonaparte.pojos.meta.FieldDefinition;

/** A worker which analyzes CSV-type text files and outputs an (improved?) interface description.
 * This is intended for single threaded operation only, as we don't have a combine step so far. */
public class AnalyzerWorkerFactory extends ContributorNoop implements BatchProcessorFactory<String, String> {
    
    private final ClassDefinition meta;
    private final String separator;
    
    public AnalyzerWorkerFactory(String separator) {
        meta = null;
        this.separator = separator;
    }
    
    public AnalyzerWorkerFactory(ClassDefinition meta, String separator) {
        this.meta = meta;
        this.separator = separator;
    }
    
    @Override
    public BatchProcessor<String, String> getProcessor(int threadNo) {
        return new AnalyzerWorker(meta, separator);
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
        public String toString() {
            return String.format("[%3d,%3d]", minVal, maxVal);
        }
    }
    
    static private class AnalyzerWorker implements BatchProcessor<String, String> {
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
        }
        
        private final ClassDefinition meta;
        private final String separator;
        private Range numFields = new Range();
        private Statistics [] columnData = new Statistics[500];
        
        public AnalyzerWorker(ClassDefinition meta, String separator) {
            this.meta = meta;
            this.separator = separator;
            for (int i = 0; i < 500; ++i)
                columnData[i] = new  Statistics();
        }

        private void check(Statistics s, String w) {
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
            s.len.upd(len);
            if (s.values.size() < 10)
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
        private String b2a(boolean b) {
            return b ? "Y" : "N";
        }
        
        @Override
        public String process(int recordNo, String data) throws Exception {
            // split the line
            String [] cols = data.split(separator);
            numFields.upd(cols.length);
            for (int i = 0; i < cols.length; ++i)
                check(columnData[i], cols[i]);
            return null;
        }

        @Override
        public void close() throws Exception {
            if (numFields.maxVal == 0)
                return;   // no data received
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
            if (samples.size() >= 10 || samples.size() == 0)
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
                    type = String.format("%s(%d)", defLen > 9 ? "Decimal" : "Number", defLen);
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
}