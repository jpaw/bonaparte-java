package de.jpaw.bonaparte.mfcobol;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.jpaw.bonaparte.pojos.meta.FieldDefinition;

public record PicNumeric(
    int              integralDigits,
    int              fractionalDigits,
    boolean          explicitDecimalPoint,
    PicStorageType   storage,
    PicSignType      sign) {

    private static final int [] SIZES_BINARY_UNSIGNED = {
            0, 1, 1, 2, 2, 3, 3, 3, 4, 4, 5, 5, 5, 6, 6, 7, 7, 8, 8, 8
    };
    private static final int [] SIZES_BINARY_SIGNED = {
            0, 1, 1, 2, 2, 3, 3, 4, 4, 4, 5, 5, 6, 6, 6, 7, 7, 8, 8, 9
    };

    /** Returns the size in bytes to store this data type. */
    public int getSize() {
        final int totalDigits = integralDigits + fractionalDigits;
        if (totalDigits < 1 || totalDigits > 18) {
            throw new InvalidPictureException(InvalidPictureException.UNSUPPORTED_NUMBER_OF_DIGITS);
        }
        switch (storage) {
        case DISPLAY:
            return totalDigits + (explicitDecimalPoint ? 1 : 0) + (sign == PicSignType.IMPLICIT || sign == PicSignType.UNSIGNED ? 0 : 1);
        case BINARY:
            return sign == PicSignType.UNSIGNED ? SIZES_BINARY_UNSIGNED[totalDigits] : SIZES_BINARY_SIGNED[totalDigits];
        case PACKED_DECIMAL:
            return (totalDigits + (sign == PicSignType.UNSIGNED ? 1 : 2)) / 2;
        default:
            throw new InvalidPictureException(InvalidPictureException.UNSUPPORTED_STORAGE_TYPE);
        }
    }

    private static final String PATTERN_STRING = "(S|-)?((9+)(\\x28[0-9]+\\x29)?)((V|.)((9+)(\\x28[0-9]+\\x29)?)?)?(-)?(( )+(COMP|COMP-3|COMP-5|PACKED DECIMAL))?";
    private static final Pattern PATTERN = Pattern.compile(PATTERN_STRING);

    public static PicNumeric forPic(final String pic, final String fieldName, final String className) {
        final Matcher matcher = PATTERN.matcher(pic);
        if (!matcher.matches()) {
            throw new InvalidPictureException(InvalidPictureException.NO_MATCH, fieldName, className);
        }
        final int groupCount = matcher.groupCount();
        if (groupCount != 13) {
            throw new InvalidPictureException(InvalidPictureException.INVALID_GROUP_COUNT);
        }
        final String leadingSign         = matcher.group(1);
        final String integralNines       = matcher.group(3);
        final String integralNineCount   = matcher.group(4);
        final String decimalPoint        = matcher.group(6);
        final String fractionalNines     = matcher.group(8);
        final String fractionalNineCount = matcher.group(9);
        final String trailingSign        = matcher.group(10);
        final String packingInfo         = matcher.group(13);
//                for (int i = 0; i <= groupCount; ++i) {
//                    LOGGER.info("Group {} is {}", i, matcher.group(i));
//                }

        // evaluate pattern
        final PicSignType sign;
        if (leadingSign != null) {
            sign = leadingSign.equals("S") ? PicSignType.IMPLICIT : PicSignType.DISPLAY_LEADING;
        } else if (trailingSign != null) {
            sign = PicSignType.DISPLAY_TRAILING;
        } else {
            sign = PicSignType.UNSIGNED;
        }
        final int integralDigits = integralNines.length() + (integralNineCount == null ? 0 : Integer.parseInt(integralNineCount, 1, integralNineCount.length() - 1, 10) - 1);
        final boolean explicitDecimalPoint = ".".equals(decimalPoint);
        int fractionalDigits = 0;
        if (fractionalNines != null) {
            // have fractional digits
            fractionalDigits = fractionalNines.length();
            if (fractionalNineCount != null) {
                fractionalDigits += Integer.parseInt(fractionalNineCount, 1, fractionalNineCount.length() - 1, 10) - 1;
            }
        }
        final PicStorageType storageType; 
        if (packingInfo == null) {
            storageType = PicStorageType.DISPLAY;
        } else {
            if (explicitDecimalPoint) {
                throw new InvalidPictureException(InvalidPictureException.POINT_WITH_PACKED, fieldName, className);
            }
            if (sign == PicSignType.DISPLAY_LEADING || sign == PicSignType.DISPLAY_TRAILING) {
                throw new InvalidPictureException(InvalidPictureException.SIGN_WITH_PACKED, fieldName, className);
            }
            switch (packingInfo) {
            case "COMP":
            case "COMP-5":
                storageType = PicStorageType.BINARY;
                break;
            case "COMP-3":
            case "PACKED DECIMAL":
                storageType = PicStorageType.PACKED_DECIMAL;
                break;
            default:
                // err storage type
                storageType = null;
                throw new InvalidPictureException(InvalidPictureException.UNSUPPORTED_STORAGE_TYPE, fieldName, className);
            }
        }
        return new PicNumeric(integralDigits, fractionalDigits, explicitDecimalPoint, storageType, sign);
    }

    public static PicNumeric forField(final FieldDefinition fd, final String className, final String defaultPic) {
        final Map<String, String> props = fd.getProperties();
        if (props == null) {
            if (defaultPic != null) {
                return forPic(defaultPic, fd.getName(), className);
            } else {
                throw new InvalidPictureException(InvalidPictureException.MISSING_PIC_PROPERTY, fd.getName(), className);
            }
        }
        final String pic = props.get("pic");
        if (pic == null) {
            if (defaultPic != null) {
                return forPic(defaultPic, fd.getName(), className);
            } else {
                throw new InvalidPictureException(InvalidPictureException.MISSING_PIC_PROPERTY, fd.getName(), className);
            }
        }
        return forPic(pic, fd.getName(), className);
    }
}
