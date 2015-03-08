package de.jpaw.bonaparte.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.DataAndMeta;
import de.jpaw.bonaparte.core.MessageParserException;
import de.jpaw.bonaparte.core.ObjectValidationException;
import de.jpaw.bonaparte.pojos.meta.NumericElementaryDataItem;

/** A class which provides some support functions which simplify working with BigDecimals.
 * The key issue we try to solve here is to provide a semantic where 2.5 == 2.50, while
 * the default BigDecimal implementation assumes 2.5 <> 2.50 (due to different scaling).
 * As bonaparte specifies a specific number of fractional digits, we scale to the bonaparte size
 * for hashCode().  The rounding mode is selected so that no Exception occurs.
 * As a consequence, we have to provide an implementation of equals, which is consistent with that (in contrast to using compareTo).
 *
 */
public class BigDecimalTools {
    private static final Logger LOG = LoggerFactory.getLogger(BigDecimalTools.class);
    private static final String DECIMALS_KEYWORD_MIN = "min";
    private static final String DECIMALS_KEYWORD_MAX = "max";


    /** Scales the BigDecimal to some predefined scale */
    static public BigDecimal scale(BigDecimal a, int decimals) {
        if (a != null && a.scale() != decimals)
            a = a.setScale(decimals, RoundingMode.HALF_EVEN);
        return a;
    }

    /** Computes the hashCode of a BigDecimal at a specific scale. */
    static public int hashCode(BigDecimal a, int decimals) {
        if (a == null)
            return 0;
        return scale(a, decimals).hashCode();
    }

    /** Compares to BigDecimal numbers. They can only be the same, if their rounded values are the same. */
    static public boolean equals(BigDecimal a, int aDecimals, BigDecimal b, int bDecimals) {
        if (a == null && b == null)
            return true;
        if (a == null || b == null)
            return false;  // exactly one of them if null, the other not
        return scale(a, aDecimals).compareTo(scale(b, bDecimals)) == 0;
    }

    /** Check a parsed BigDecimal for allowed digits, and perform (if desired) scaling. Use the second form with the metadata parameter instead. */
    static public BigDecimal checkAndScale(BigDecimal r, NumericElementaryDataItem di, int parseIndex, String currentClass) throws MessageParserException {
        String fieldname = di.getName();
        int decimals = di.getDecimalDigits();
        try {
            if (r.scale() > decimals)
                r = r.setScale(decimals, di.getRounding() ? RoundingMode.HALF_EVEN : RoundingMode.UNNECESSARY);
            if (di.getAutoScale() && r.scale() < decimals)  // round for smaller as well!
                r = r.setScale(decimals, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException a) {
            throw new MessageParserException(MessageParserException.TOO_MANY_DECIMALS, fieldname, parseIndex, currentClass);
        }
        if (!di.getIsSigned() && r.signum() < 0)
            throw new MessageParserException(MessageParserException.SUPERFLUOUS_SIGN, fieldname, parseIndex, currentClass);
        // check for overflow
        if (di.getTotalDigits() - decimals < r.precision() - r.scale())
            throw new MessageParserException(MessageParserException.TOO_MANY_DIGITS, fieldname, parseIndex, currentClass);
        return r;
    }

    /** Check a BigDecimal for compliance of the spec. */
    static public void validate(BigDecimal r, NumericElementaryDataItem meta, String classname) throws ObjectValidationException {
        try {
            if (r.scale() > meta.getDecimalDigits())
                r = r.setScale(meta.getDecimalDigits(), meta.getRounding() ? RoundingMode.HALF_EVEN : RoundingMode.UNNECESSARY);
        } catch (ArithmeticException a) {
            throw new ObjectValidationException(ObjectValidationException.TOO_MANY_FRACTIONAL_DIGITS, meta.getName(), classname);
        }
        if (!meta.getIsSigned() && r.signum() < 0)
            throw new ObjectValidationException(ObjectValidationException.NO_NEGATIVE_ALLOWED, meta.getName(), classname);
        // check for overflow
        if (meta.getTotalDigits() - meta.getDecimalDigits() < r.precision() - r.scale())
            throw new ObjectValidationException(ObjectValidationException.TOO_MANY_DIGITS, meta.getName(), classname);
    }

    /** Given an object tree and a pathname within this tree (which should point to some BigDecimal number),
     * retrieve the number and scale it to the desired precision, as indicated by the property "decimals".
     * The possible values are:
     * min: scale the number to the smallest number of decimals
     * max: scale the number to the precision of the underlying field
     * (number): scale to the number of digits provided
     * (pathname): retrieve the object from the tree and use its value (if it is a string, it is interpreted as a currency)
     *
     * If no decimals property is found, the algorithm looks tree upwards (to the root).
     * If no decimals property can be found at any level, "min" is assumed.
     *
     * @param root
     * @param path the path of the field,, in dot notation. At least the field name must be here (indicating a field at the root level)
     * @returns the scaled number
     */
    static public BigDecimal retrieveScaled(BonaPortable root, String path) {
        DataAndMeta value = FieldGetter.getSingleFieldWithMeta(root, path);
        if (value == null || value.data == null || !(value.data instanceof BigDecimal))
            return null;  // wrong type
        BigDecimal numValue = (BigDecimal)value.data;
        NumericElementaryDataItem meta = (NumericElementaryDataItem)value.meta;
        StringRef prefix = new StringRef();
        prefix.prefix = path;
        String props = getFieldPropertyWithDescend(root, path, prefix, "decimals", DECIMALS_KEYWORD_MIN);

        if (props.length() == 0)
            props = DECIMALS_KEYWORD_MIN;

        if (DECIMALS_KEYWORD_MIN.equals(props)) {
            BigDecimal tmp = numValue.stripTrailingZeros();
            if (tmp.scale() < 0)
                tmp = tmp.setScale(0);
            return tmp;
        }
        if (DECIMALS_KEYWORD_MAX.equals(props)) {
            return numValue.setScale(meta.getDecimalDigits());
        }
        // check for numeric immediate specification
        if (Character.isDigit(props.charAt(0)))
            return numValue.setScale(Integer.parseInt(props));
        // last resort: assume it is another pathname, retrieve that value
        Object precision = FieldGetter.getField(root, prefix.prefix + props);
        if (precision == null) {
            LOG.warn("Did not find referenced precision field for root object {} and path {}", root.get$PQON(), path);
            return numValue;  // this should not happen, but fall back instead of throwing an NPE
        }
        if (precision instanceof Integer)
            return numValue.setScale(((Integer)precision).intValue(), RoundingMode.HALF_EVEN);
        // it's not an integer, assume it is a String
        int currencyPrecision = Currency.getInstance((String)precision).getDefaultFractionDigits();
        return numValue.setScale(currencyPrecision, RoundingMode.HALF_EVEN);
    }

    // utility class to pass back a second return value
    static private class StringRef {
        private String prefix;
    }

    static public String getFieldPropertyWithDescend(BonaPortable root, String path, StringRef prefix, String propertyName, String defaultProperty) {
        String workingPath = path;
        String props = null;

        // if the pathname contains no dot, the only containing object is the root
        for (;;) {
            int lastDot = workingPath.lastIndexOf('.');
            if (lastDot < 0) {
                // no more component
                props = root.get$BonaPortableClass().getProperty(naked(workingPath) + "." + propertyName);
                prefix.prefix = "";
                break;  // must stop here!
            } else {
                // get the meta from the parent object
                String container = workingPath.substring(0, lastDot);
                String fieldname = workingPath.substring(lastDot+1);
                Object parent = FieldGetter.getFieldOrObj(root, container);
                props = ((BonaPortable)parent).get$BonaPortableClass().getProperty(naked(fieldname) + "." + propertyName);
                if (props != null) {
                    LOG.debug("found property " + props + " at path " + container);
                    prefix.prefix = container + ".";
                    break;
                }
                // not found here, descend
                workingPath = container;
            }
        }
        return props == null ? defaultProperty : props;
    }

    /** Returns a path fragment without any array / index. */
    static private String naked(String path) {
        int indexBracket = path.indexOf('[');
        if (indexBracket < 0)
            return path;  // contains no array index
        else
            return path.substring(0, indexBracket);
    }
}
