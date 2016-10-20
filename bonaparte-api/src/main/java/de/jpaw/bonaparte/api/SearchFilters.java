package de.jpaw.bonaparte.api;

import java.util.List;

import de.jpaw.bonaparte.pojos.api.AndFilter;
import de.jpaw.bonaparte.pojos.api.FalseFilter;
import de.jpaw.bonaparte.pojos.api.NotFilter;
import de.jpaw.bonaparte.pojos.api.OrFilter;
import de.jpaw.bonaparte.pojos.api.SearchFilter;
import de.jpaw.bonaparte.pojos.api.TrueFilter;

/** Methods to combine search filters. All methods in this class immediately evaluate logical operations on true or false. */
public class SearchFilters {
    /** Static instance of true and false. */
    public static final SearchFilter TRUE = new TrueFilter();
    public static final SearchFilter FALSE = new FalseFilter();

    /** Combines two optional filters by AND condition. */
    public static SearchFilter and(SearchFilter filter1, SearchFilter filter2) {
        if (filter1 == null || filter1 instanceof TrueFilter)
            return filter2;
        if (filter2 == null || filter2 instanceof TrueFilter)
            return filter1;
        if (filter1 instanceof FalseFilter || filter2 instanceof FalseFilter)
            return FALSE;
        return new AndFilter(filter1, filter2);
    }

    /** Combines two optional filters by OR condition. */
    public static SearchFilter or(SearchFilter filter1, SearchFilter filter2) {
        if (filter1 == null || filter1 instanceof FalseFilter)
            return filter2;
        if (filter2 == null || filter2 instanceof FalseFilter)
            return filter1;
        if (filter1 instanceof TrueFilter || filter2 instanceof TrueFilter)
            return TRUE;
        return new OrFilter(filter1, filter2);
    }

    /** Negates a filter. */
    public static SearchFilter not(SearchFilter filter) {
        if (filter == null)
            return null;
        if (filter instanceof FalseFilter)
            return TRUE;
        if (filter instanceof TrueFilter)
            return FALSE;
        return new NotFilter(filter);
    }

    /** Combines any number of optional filters by AND condition. */
    public static SearchFilter and(List<SearchFilter> filters) {
        SearchFilter current = null;
        for (SearchFilter f : filters)
            current = and(current, f);
        return current;
    }

    /** Combines any number of optional filters by OR condition. */
    public static SearchFilter or(List<SearchFilter> filters) {
        SearchFilter current = null;
        for (SearchFilter f : filters)
            current = or(current, f);
        return current;
    }
}
