package de.jpaw.bonaparte.api;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.jpaw.bonaparte.pojos.api.AndFilter;
import de.jpaw.bonaparte.pojos.api.DayFilter;
import de.jpaw.bonaparte.pojos.api.FalseFilter;
import de.jpaw.bonaparte.pojos.api.InstantFilter;
import de.jpaw.bonaparte.pojos.api.IntFilter;
import de.jpaw.bonaparte.pojos.api.LongFilter;
import de.jpaw.bonaparte.pojos.api.NotFilter;
import de.jpaw.bonaparte.pojos.api.OrFilter;
import de.jpaw.bonaparte.pojos.api.SearchFilter;
import de.jpaw.bonaparte.pojos.api.TimeFilter;
import de.jpaw.bonaparte.pojos.api.TimestampFilter;
import de.jpaw.bonaparte.pojos.api.TrueFilter;
import de.jpaw.bonaparte.pojos.api.UnicodeFilter;

/** Methods to combine search filters. All methods in this class immediately evaluate logical operations on true or false. */
public final class SearchFilters {
    private SearchFilters() {
    }

    /** Static instance of true and false. */
    public static final SearchFilter TRUE = new TrueFilter();
    public static final SearchFilter FALSE = new FalseFilter();

    /** Combines two optional filters by AND condition. */
    public static SearchFilter and(final SearchFilter filter1, final SearchFilter filter2) {
        if (filter1 == null || filter1 instanceof TrueFilter)
            return filter2;
        if (filter2 == null || filter2 instanceof TrueFilter)
            return filter1;
        if (filter1 instanceof FalseFilter || filter2 instanceof FalseFilter)
            return FALSE;
        return new AndFilter(filter1, filter2);
    }

    /** Combines two optional filters by OR condition. */
    public static SearchFilter or(final SearchFilter filter1, final SearchFilter filter2) {
        if (filter1 == null || filter1 instanceof FalseFilter)
            return filter2;
        if (filter2 == null || filter2 instanceof FalseFilter)
            return filter1;
        if (filter1 instanceof TrueFilter || filter2 instanceof TrueFilter)
            return TRUE;
        return new OrFilter(filter1, filter2);
    }

    /** Negates a filter. */
    public static SearchFilter not(final SearchFilter filter) {
        if (filter == null)
            return null;
        if (filter instanceof FalseFilter)
            return TRUE;
        if (filter instanceof TrueFilter)
            return FALSE;
        return new NotFilter(filter);
    }

    /** Combines any number of optional filters by AND condition. */
    public static SearchFilter and(final Collection<SearchFilter> filters) {
        SearchFilter current = null;
        for (final SearchFilter f : filters)
            current = and(current, f);
        return current;
    }

    /** Combines any number of optional filters by OR condition. */
    public static SearchFilter or(final Collection<SearchFilter> filters) {
        SearchFilter current = null;
        for (final SearchFilter f : filters)
            current = or(current, f);
        return current;
    }


    // some useful shortcuts

    // part 1: equals

    public static IntFilter equalsFilter(final String fieldName, final Integer value) {
        final IntFilter f = new IntFilter(fieldName);
        f.setEqualsValue(value);
        return f;
    }

    public static LongFilter equalsFilter(final String fieldName, final Long value) {
        final LongFilter f = new LongFilter(fieldName);
        f.setEqualsValue(value);
        return f;
    }

    public static UnicodeFilter equalsFilter(final String fieldName, final String value) {
        final UnicodeFilter f = new UnicodeFilter(fieldName);
        f.setEqualsValue(value);
        return f;
    }

    public static InstantFilter equalsFilter(final String fieldName, final Instant value) {
        final InstantFilter f = new InstantFilter(fieldName);
        f.setEqualsValue(value);
        return f;
    }

    public static TimestampFilter equalsFilter(final String fieldName, final LocalDateTime value) {
        final TimestampFilter f = new TimestampFilter(fieldName);
        f.setEqualsValue(value);
        return f;
    }

    public static DayFilter equalsFilter(final String fieldName, final LocalDate value) {
        final DayFilter f = new DayFilter(fieldName);
        f.setEqualsValue(value);
        return f;
    }

    public static TimeFilter equalsFilter(final String fieldName, final LocalTime value) {
        final TimeFilter f = new TimeFilter(fieldName);
        f.setEqualsValue(value);
        return f;
    }

    // part 2: range (or just upper or lower bound)

    public static IntFilter rangeFilter(final String fieldName, final Integer lower, final Integer upper) {
        if (lower == null && upper == null) {
            throw new NullPointerException("Cannot build range filter with both null parameters");
        }
        final IntFilter f = new IntFilter(fieldName);
        if (lower != null && upper != null && lower.intValue() == upper.intValue()) {
            f.setEqualsValue(lower);
        } else {
            f.setLowerBound(lower);
            f.setUpperBound(upper);
        }
        return f;
    }

    public static LongFilter rangeFilter(final String fieldName, final Long lower, final Long upper) {
        if (lower == null && upper == null) {
            throw new NullPointerException("Cannot build range filter with both null parameters");
        }
        final LongFilter f = new LongFilter(fieldName);
        if (lower != null && upper != null && lower.longValue() == upper.longValue()) {
            f.setEqualsValue(lower);
        } else {
            f.setLowerBound(lower);
            f.setUpperBound(upper);
        }
        return f;
    }

    public static UnicodeFilter rangeFilter(final String fieldName, final String lower, final String upper) {
        if (lower == null && upper == null) {
            throw new NullPointerException("Cannot build range filter with both null parameters");
        }
        final UnicodeFilter f = new UnicodeFilter(fieldName);
        if (lower != null && lower.equals(upper)) {
            f.setEqualsValue(lower);
        } else {
            f.setLowerBound(lower);
            f.setUpperBound(upper);
        }
        return f;
    }

    public static InstantFilter rangeFilter(final String fieldName, final Instant lower, final Instant upper) {
        if (lower == null && upper == null) {
            throw new NullPointerException("Cannot build range filter with both null parameters");
        }
        final InstantFilter f = new InstantFilter(fieldName);
        if (lower != null && lower.equals(upper)) {
            f.setEqualsValue(lower);
        } else {
            f.setLowerBound(lower);
            f.setUpperBound(upper);
        }
        return f;
    }

    public static TimestampFilter rangeFilter(final String fieldName, final LocalDateTime lower, final LocalDateTime upper) {
        if (lower == null && upper == null) {
            throw new NullPointerException("Cannot build range filter with both null parameters");
        }
        final TimestampFilter f = new TimestampFilter(fieldName);
        if (lower != null && lower.equals(upper)) {
            f.setEqualsValue(lower);
        } else {
            f.setLowerBound(lower);
            f.setUpperBound(upper);
        }
        return f;
    }

    public static DayFilter rangeFilter(final String fieldName, final LocalDate lower, final LocalDate upper) {
        if (lower == null && upper == null) {
            throw new NullPointerException("Cannot build range filter with both null parameters");
        }
        final DayFilter f = new DayFilter(fieldName);
        if (lower != null && lower.equals(upper)) {
            f.setEqualsValue(lower);
        } else {
            f.setLowerBound(lower);
            f.setUpperBound(upper);
        }
        return f;
    }

    public static TimeFilter rangeFilter(final String fieldName, final LocalTime lower, final LocalTime upper) {
        if (lower == null && upper == null) {
            throw new NullPointerException("Cannot build range filter with both null parameters");
        }
        final TimeFilter f = new TimeFilter(fieldName);
        if (lower != null && lower.equals(upper)) {
            f.setEqualsValue(lower);
        } else {
            f.setLowerBound(lower);
            f.setUpperBound(upper);
        }
        return f;
    }

    // part 3: IN lists (have to use different names due to identical parameter lists after type erasure

    public static IntFilter intFilter(final String fieldName, final Collection<Integer> values) {
        if (values == null || values.isEmpty()) {
            throw new NullPointerException("Need at least one value for IN filter");
        }
        final IntFilter f = new IntFilter(fieldName);
        if (values.size() == 1) {
            f.setEqualsValue(values.iterator().next());
        } else if (values instanceof List) {
            f.setValueList((List<Integer>)values);
        } else {
            f.setValueList(new ArrayList<>(values));
        }
        return f;
    }

    public static LongFilter longFilter(final String fieldName, final Collection<Long> values) {
        if (values == null || values.isEmpty()) {
            throw new NullPointerException("Need at least one value for IN filter");
        }
        final LongFilter f = new LongFilter(fieldName);
        if (values.size() == 1) {
            f.setEqualsValue(values.iterator().next());
        } else if (values instanceof List) {
            f.setValueList((List<Long>)values);
        } else {
            f.setValueList(new ArrayList<>(values));
        }
        return f;
    }

    public static UnicodeFilter unicodeFilter(final String fieldName, final Collection<String> values) {
        if (values == null || values.isEmpty()) {
            throw new NullPointerException("Need at least one value for IN filter");
        }
        final UnicodeFilter f = new UnicodeFilter(fieldName);
        if (values.size() == 1) {
            f.setEqualsValue(values.iterator().next());
        } else if (values instanceof List) {
            f.setValueList((List<String>)values);
        } else {
            f.setValueList(new ArrayList<>(values));
        }
        return f;
    }

    public static InstantFilter instantFilter(final String fieldName, final Collection<Instant> values) {
        if (values == null || values.isEmpty()) {
            throw new NullPointerException("Need at least one value for IN filter");
        }
        final InstantFilter f = new InstantFilter(fieldName);
        if (values.size() == 1) {
            f.setEqualsValue(values.iterator().next());
        } else if (values instanceof List) {
            f.setValueList((List<Instant>)values);
        } else {
            f.setValueList(new ArrayList<>(values));
        }
        return f;
    }

    public static TimestampFilter timestampFilter(final String fieldName, final Collection<LocalDateTime> values) {
        if (values == null || values.isEmpty()) {
            throw new NullPointerException("Need at least one value for IN filter");
        }
        final TimestampFilter f = new TimestampFilter(fieldName);
        if (values.size() == 1) {
            f.setEqualsValue(values.iterator().next());
        } else if (values instanceof List) {
            f.setValueList((List<LocalDateTime>)values);
        } else {
            f.setValueList(new ArrayList<>(values));
        }
        return f;
    }
}
