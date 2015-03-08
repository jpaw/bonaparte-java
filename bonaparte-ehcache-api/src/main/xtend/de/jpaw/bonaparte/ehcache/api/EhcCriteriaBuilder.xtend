package de.jpaw.bonaparte.ehcache.api;

import de.jpaw.bonaparte.pojos.api.AndFilter
import de.jpaw.bonaparte.pojos.api.AsciiFilter
import de.jpaw.bonaparte.pojos.api.BooleanFilter
import de.jpaw.bonaparte.pojos.api.DayFilter
import de.jpaw.bonaparte.pojos.api.FieldFilter
import de.jpaw.bonaparte.pojos.api.InstantFilter
import de.jpaw.bonaparte.pojos.api.IntFilter
import de.jpaw.bonaparte.pojos.api.LongFilter
import de.jpaw.bonaparte.pojos.api.NotFilter
import de.jpaw.bonaparte.pojos.api.NullFilter
import de.jpaw.bonaparte.pojos.api.OrFilter
import de.jpaw.bonaparte.pojos.api.SearchFilter
import de.jpaw.bonaparte.pojos.api.TimeFilter
import de.jpaw.bonaparte.pojos.api.TimestampFilter
import de.jpaw.bonaparte.pojos.api.UnicodeFilter
import de.jpaw.dp.Jdp
import de.jpaw.dp.Singleton
import net.sf.ehcache.search.expression.Criteria
import net.sf.ehcache.Cache
import net.sf.ehcache.search.Attribute

public interface EhcFilter {
    def Criteria applyFilter(Cache cache, Attribute<?> field, FieldFilter f);
}

public class EhcCriteriaBuilder {
    def public static Criteria buildPredicate(Cache cache, SearchFilter filter) {
        if (filter === null)
            return null;
        switch (filter) {
//        BooleanFilter:
//            return if (filter.booleanValue) new PredicateBuilder().getEntryObject().is(filter.fieldName) else new PredicateBuilder().getEntryObject().isNot(filter.fieldName)
        FieldFilter:
            return Jdp.getRequired(EhcFilter).applyFilter(cache, cache.getSearchAttribute(filter.fieldName), filter)
        AndFilter:
            return buildPredicate(cache, filter.filter1).and(buildPredicate(cache, filter.filter2))
        OrFilter:
            return buildPredicate(cache, filter.filter1).or(buildPredicate(cache, filter.filter2))
        NotFilter:
            return buildPredicate(cache, filter.filter).not
        default:
            throw new RuntimeException("Unrecognized filter type: " + filter.get$PQON)
        }
    }
}

@Singleton
public class EhcFilterImpl implements EhcFilter {

    override public applyFilter(Cache cache, Attribute<?> field, FieldFilter filter) {
        switch (filter) {
        NullFilter:
            return field.isNull()
        BooleanFilter:
            return (field as Attribute<Boolean>).eq(Boolean.valueOf(filter.booleanValue))
        AsciiFilter:
            return if (filter.valueList !== null)
                        (field as Attribute<String>).in(filter.valueList)
                    else if (filter.likeValue !== null)
                        throw new RuntimeException("Ehcache supports case insensitive like only")
                    else if (filter.lowerBound === null)
                        (field as Attribute<String>).le(filter.upperBound)
                    else if (filter.upperBound === null)
                        (field as Attribute<String>).ge(filter.lowerBound)
                    else if (filter.lowerBound.equals(filter.upperBound))
                        (field as Attribute<String>).eq(filter.lowerBound)
                    else
                        (field as Attribute<String>).between(filter.lowerBound, filter.upperBound)
        UnicodeFilter:
            return if (filter.valueList !== null)
                        (field as Attribute<String>).in(filter.valueList)
                    else if (filter.likeValue !== null)
                        throw new RuntimeException("Ehcache supports case insensitive like only")
                    else if (filter.lowerBound === null)
                        (field as Attribute<String>).le(filter.upperBound)
                    else if (filter.upperBound === null)
                        (field as Attribute<String>).ge(filter.lowerBound)
                    else if (filter.lowerBound.equals(filter.upperBound))
                        (field as Attribute<String>).eq(filter.lowerBound)
                    else
                        (field as Attribute<String>).between(filter.lowerBound, filter.upperBound)
        IntFilter:
            return if (filter.valueList !== null)
                        (field as Attribute<Integer>).in(filter.valueList)
                    else if (filter.lowerBound === null)
                        (field as Attribute<Integer>).le(filter.upperBound)
                    else if (filter.upperBound === null)
                        (field as Attribute<Integer>).ge(filter.lowerBound)
                    else if (filter.lowerBound.equals(filter.upperBound))
                        (field as Attribute<Integer>).eq(filter.lowerBound)
                    else
                        (field as Attribute<Integer>).between(filter.lowerBound, filter.upperBound)
        LongFilter:
            return if (filter.valueList !== null)
                        (field as Attribute<Long>).in(filter.valueList)
                    else if (filter.lowerBound === null)
                        (field as Attribute<Long>).le(filter.upperBound)
                    else if (filter.upperBound === null)
                        (field as Attribute<Long>).ge(filter.lowerBound)
                    else if (filter.lowerBound.equals(filter.upperBound))
                        (field as Attribute<Long>).eq(filter.lowerBound)
                    else
                        (field as Attribute<Long>).between(filter.lowerBound, filter.upperBound)
        DayFilter:
            throw new RuntimeException("joda comparison not supported by Ehcache")   // TODO: write attributeExtractor and convert to integral number
        TimestampFilter:
            throw new RuntimeException("joda comparison not supported by Ehcache")
        InstantFilter:
            throw new RuntimeException("joda comparison not supported by Ehcache")
        TimeFilter:
            throw new RuntimeException("joda comparison not supported by Ehcache")
        default:
            throw new RuntimeException("Unrecognized field filter type: " + filter.get$PQON)
        }
    }

}
