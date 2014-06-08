package de.jpaw.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.DataAndMeta;
import de.jpaw.bonaparte.pojos.meta.FieldDefinition;
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
     * @param path
     * @return
     */
    static public BigDecimal retrieveScaled(BonaPortable root, String path) {
        DataAndMeta<Object,FieldDefinition> value = FieldGetter.getSingleFieldWithMeta(root, path);
        if (value == null || value.data == null || !(value.data instanceof BigDecimal))
            return null;  // wrong type
        BigDecimal numValue = (BigDecimal)value.data; 
        NumericElementaryDataItem meta = (NumericElementaryDataItem)value.meta;
        String props = getFieldPropertyWithDescend(root, path, ".decimals", "min");
        
        if (props.length() == 0)
            props = "min";
        
        if ("min".equals(props)) {
            BigDecimal tmp = numValue.stripTrailingZeros();
            if (tmp.scale() < 0)
                tmp = tmp.setScale(0);
            return tmp;
        }
        if ("max".equals(props)) {
            return numValue.setScale(meta.getDecimalDigits());
        }
        // check for numeric immediate specification
        if (Character.isDigit(props.charAt(0)))
            return numValue.setScale(Integer.valueOf(props));
        // last resort: assume it is another pathname, retrieve that value
        Object precision = FieldGetter.getField(root, props);
        if (precision == null) {
            LOG.warn("Did not find referenced precision field for root object {} and path {}", root.get$PQON(), path);
            return numValue;  // this should not happen, but fall back instead of throwing an NPE
        }        
        if (precision instanceof Integer)
            return numValue.setScale((Integer)precision, RoundingMode.HALF_EVEN);
        // it's not an integer, assume it is a String
        int currencyPrecision = Currency.getInstance((String)precision).getDefaultFractionDigits();
        return numValue.setScale(currencyPrecision, RoundingMode.HALF_EVEN);
    }
    
    static public String getFieldPropertyWithDescend(BonaPortable root, String path, String propertyName, String defaultProperty) {
        String workingPath = path;
        String props = null;
        
        // if the pathname contains no dot, the only containing object is the root
        for (;;) {
            int lastDot = workingPath.lastIndexOf('.');
            if (lastDot < 0) {
                // no more component
                props = root.get$Property(naked(workingPath) + propertyName);
                break;  // must stop here!
            } else {
                // get the meta from the parent object
                String container = workingPath.substring(0, lastDot);
                String fieldname = workingPath.substring(lastDot+1);
                Object parent = FieldGetter.getFieldOrObj(root, container);
                props = ((BonaPortable)parent).get$Property(naked(fieldname) + propertyName);
                if (props != null) {
                    LOG.debug("found property " + props + " at path " + container);
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
