package de.jpaw.bonaparte.hazelcast.api;

import com.hazelcast.query.EntryObject
import com.hazelcast.query.Predicate
import com.hazelcast.query.PredicateBuilder
import com.hazelcast.query.Predicates
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
import de.jpaw.bonaparte.pojos.api.DecimalFilter
import de.jpaw.dp.Inject
import de.jpaw.dp.Singleton

public interface HzFilter {
    def Predicate<?,?> applyFilter(EntryObject e, FieldFilter f);
}

@Singleton
public class HzCriteriaBuilder {
    @Inject
    var HzFilter hzFilter
    
    def public Predicate<?,?> buildPredicate(SearchFilter filter) {
        if (filter === null)
            return null;
        switch (filter) {
//        BooleanFilter:
//            return if (filter.booleanValue) new PredicateBuilder().getEntryObject().is(filter.fieldName) else new PredicateBuilder().getEntryObject().isNot(filter.fieldName)
        FieldFilter:
            return hzFilter.applyFilter(new PredicateBuilder().getEntryObject().get(filter.fieldName), filter)
        AndFilter:
            return Predicates.and(buildPredicate(filter.filter1), buildPredicate(filter.filter2))
        OrFilter:
            return Predicates.or(buildPredicate(filter.filter1), buildPredicate(filter.filter2))
        NotFilter:
            return Predicates.not(buildPredicate(filter.filter))
        default:
            throw new RuntimeException("Unrecognized filter type: " + filter.ret$PQON)
        }
    }
}

// implemented as a separate injection point to allow adding criteria on additional types via subclassing / specialization
@Singleton
public class HzFilterImpl implements HzFilter {

    override public applyFilter(EntryObject e, FieldFilter filter) {
        switch (filter) {
        NullFilter:
            return e.isNull()
        BooleanFilter:
            return e.equal(Boolean.valueOf(filter.booleanValue))
        AsciiFilter:
            return if (filter.valueList !== null)
                        e.in(filter.valueList.toArray as Comparable<?>[])
                    else if (filter.equalsValue !== null)
                        e.equal(filter.equalsValue)
                    else if (filter.likeValue !== null)
                        Predicates.like(filter.fieldName, filter.likeValue)
                    else if (filter.lowerBound === null)
                        e.lessEqual(filter.upperBound)
                    else if (filter.upperBound === null)
                        e.greaterEqual(filter.lowerBound)
                    else
                        e.between(filter.lowerBound, filter.upperBound)
        UnicodeFilter:
            return if (filter.valueList !== null)
                        e.in(filter.valueList.toArray as Comparable<?>[])
                    else if (filter.equalsValue !== null)
                        e.equal(filter.equalsValue)
                    else if (filter.likeValue !== null)
                        Predicates.like(filter.fieldName, filter.likeValue)
                    else if (filter.lowerBound === null)
                        e.lessEqual(filter.upperBound)
                    else if (filter.upperBound === null)
                        e.greaterEqual(filter.lowerBound)
                    else
                        e.between(filter.lowerBound, filter.upperBound)
        IntFilter:
            return if (filter.valueList !== null)
                        e.in(filter.valueList.toArray as Comparable<?>[])
                    else if (filter.equalsValue !== null)
                        e.equal(filter.equalsValue)
                    else if (filter.lowerBound === null)
                        e.lessEqual(filter.upperBound)
                    else if (filter.upperBound === null)
                        e.greaterEqual(filter.lowerBound)
                    else
                        e.between(filter.lowerBound, filter.upperBound)
        LongFilter:
            return if (filter.valueList !== null)
                        e.in(filter.valueList.toArray as Comparable<?>[])
                    else if (filter.equalsValue !== null)
                        e.equal(filter.equalsValue)
                    else if (filter.lowerBound === null)
                        e.lessEqual(filter.upperBound)
                    else if (filter.upperBound === null)
                        e.greaterEqual(filter.lowerBound)
                    else
                        e.between(filter.lowerBound, filter.upperBound)
        DecimalFilter:
            return if (filter.valueList !== null)
                        e.in(filter.valueList.toArray as Comparable<?>[])
                    else if (filter.equalsValue !== null)
                        e.equal(filter.equalsValue)
                    else if (filter.lowerBound === null)
                        e.lessEqual(filter.upperBound)
                    else if (filter.upperBound === null)
                        e.greaterEqual(filter.lowerBound)
                    else
                        e.between(filter.lowerBound, filter.upperBound)
        DayFilter:
            return if (filter.valueList !== null)
                        e.in(filter.valueList.toArray as Comparable<?>[])
                    else if (filter.equalsValue !== null)
                        e.equal(filter.equalsValue)
                    else if (filter.lowerBound === null)
                        e.lessEqual(filter.upperBound)
                    else if (filter.upperBound === null)
                        e.greaterEqual(filter.lowerBound)
                    else
                        e.between(filter.lowerBound, filter.upperBound)
        TimestampFilter:
            return if (filter.valueList !== null)
                        e.in(filter.valueList.toArray as Comparable<?>[])
                    else if (filter.equalsValue !== null)
                        e.equal(filter.equalsValue)
                    else if (filter.lowerBound === null)
                        e.lessEqual(filter.upperBound)
                    else if (filter.upperBound === null)
                        e.greaterEqual(filter.lowerBound)
                    else
                        e.between(filter.lowerBound, filter.upperBound)
        InstantFilter:
            return if (filter.valueList !== null)
                        e.in(filter.valueList.toArray as Comparable<?>[])
                    else if (filter.equalsValue !== null)
                        e.equal(filter.equalsValue)
                    else if (filter.lowerBound === null)
                        e.lessEqual(filter.upperBound)
                    else if (filter.upperBound === null)
                        e.greaterEqual(filter.lowerBound)
                    else
                        e.between(filter.lowerBound, filter.upperBound)
        TimeFilter:
            return if (filter.valueList !== null)
                        e.in(filter.valueList.toArray as Comparable<?>[])
                    else if (filter.equalsValue !== null)
                        e.equal(filter.equalsValue)
                    else if (filter.lowerBound === null)
                        e.lessEqual(filter.upperBound)
                    else if (filter.upperBound === null)
                        e.greaterEqual(filter.lowerBound)
                    else
                        e.between(filter.lowerBound, filter.upperBound)
        default:
            throw new RuntimeException("Unrecognized field filter type: " + filter.ret$PQON)
        }
    }

}
