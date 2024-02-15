package de.jpaw.bonaparte.jpa.api;

import de.jpaw.bonaparte.pojos.api.AndFilter
import de.jpaw.bonaparte.pojos.api.AsciiFilter
import de.jpaw.bonaparte.pojos.api.BooleanFilter
import de.jpaw.bonaparte.pojos.api.ByteArrayFilter
import de.jpaw.bonaparte.pojos.api.ByteFilter
import de.jpaw.bonaparte.pojos.api.BytesFilter
import de.jpaw.bonaparte.pojos.api.DayFilter
import de.jpaw.bonaparte.pojos.api.DecimalFilter
import de.jpaw.bonaparte.pojos.api.DoubleFilter
import de.jpaw.bonaparte.pojos.api.FieldFilter
import de.jpaw.bonaparte.pojos.api.FloatFilter
import de.jpaw.bonaparte.pojos.api.InstantFilter
import de.jpaw.bonaparte.pojos.api.IntFilter
import de.jpaw.bonaparte.pojos.api.LongFilter
import de.jpaw.bonaparte.pojos.api.NotFilter
import de.jpaw.bonaparte.pojos.api.NullFilter
import de.jpaw.bonaparte.pojos.api.OrFilter
import de.jpaw.bonaparte.pojos.api.SearchFilter
import de.jpaw.bonaparte.pojos.api.ShortFilter
import de.jpaw.bonaparte.pojos.api.TimeFilter
import de.jpaw.bonaparte.pojos.api.TimestampFilter
import de.jpaw.bonaparte.pojos.api.UnicodeFilter
import de.jpaw.bonaparte.pojos.api.UuidFilter
import de.jpaw.dp.Inject
import de.jpaw.dp.Singleton
import java.math.BigDecimal
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.Path
import jakarta.persistence.criteria.Predicate
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

interface JpaFilter {
    def Predicate applyFilter(CriteriaBuilder cb, Path<?> from, FieldFilter f);
}

interface JpaPathResolver {
    def Path<?> getPath(String fieldName);
}

class JpaCriteriaBuilder {
    @Inject
    var JpaFilter jpaFilter

    val JpaPathResolver pathResolver
    val CriteriaBuilder cb

    new(JpaPathResolver pathResolver, CriteriaBuilder cb) {
        this.pathResolver = pathResolver
        this.cb = cb
    }

    def Predicate buildPredicate(SearchFilter filter) {
        if (filter === null)
            return null;
        switch (filter) {
//        BooleanFilter:
//            return if (filter.booleanValue) new PredicateBuilder().getEntryObject().is(filter.fieldName) else new PredicateBuilder().getEntryObject().isNot(filter.fieldName)
        FieldFilter:
            return jpaFilter.applyFilter(cb, pathResolver.getPath(filter.fieldName), filter)
        AndFilter:
            return cb.and(buildPredicate(filter.filter1), buildPredicate(filter.filter2))
        OrFilter:
            return cb.or(buildPredicate(filter.filter1), buildPredicate(filter.filter2))
        NotFilter:
            return buildPredicate(filter.filter).not
        default:
            throw new RuntimeException("Unrecognized filter type: " + filter.ret$PQON)
        }
    }
}

@Singleton
class JpaFilterImpl implements JpaFilter {

    override applyFilter(CriteriaBuilder cb, Path<?> path, FieldFilter filter) {
        switch (filter) {
        NullFilter:
            return cb.isNull(path)
        BooleanFilter:
            return cb.equal(path, Boolean.valueOf(filter.booleanValue))
        AsciiFilter:
            return if (filter.valueList !== null)
                        path.in(filter.valueList)
                    else if (filter.equalsValue !== null)
                        cb.equal(path, filter.equalsValue)
                    else if (filter.likeValue !== null)
                        cb.like(path as Path<String>, filter.likeValue)
                    else if (filter.lowerBound === null)
                        cb.lessThanOrEqualTo(path as Path<String>, filter.upperBound)
                    else if (filter.upperBound === null)
                        cb.greaterThanOrEqualTo(path as Path<String>, filter.lowerBound)
                    else
                        cb.between(path as Path<String>, filter.lowerBound, filter.upperBound)
        UnicodeFilter:
            return if (filter.valueList !== null)
                        path.in(filter.valueList)
                    else if (filter.equalsValue !== null)
                        cb.equal(path, filter.equalsValue)
                    else if (filter.likeValue !== null)
                        cb.like(path as Path<String>, filter.likeValue)
                    else if (filter.lowerBound === null)
                        cb.lessThanOrEqualTo(path as Path<String>, filter.upperBound)
                    else if (filter.upperBound === null)
                        cb.greaterThanOrEqualTo(path as Path<String>, filter.lowerBound)
                    else
                        cb.between(path as Path<String>, filter.lowerBound, filter.upperBound)
        IntFilter:
            return if (filter.valueList !== null)
                        path.in(filter.valueList)
                    else if (filter.equalsValue !== null)
                        cb.equal(path, filter.equalsValue)
                    else if (filter.lowerBound === null)
                        cb.lessThanOrEqualTo(path as Path<Integer>, filter.upperBound)
                    else if (filter.upperBound === null)
                        cb.greaterThanOrEqualTo(path as Path<Integer>, filter.lowerBound)
                    else
                        cb.between(path as Path<Integer>, filter.lowerBound, filter.upperBound)
        LongFilter:
            return if (filter.valueList !== null)
                        path.in(filter.valueList)
                    else if (filter.equalsValue !== null)
                        cb.equal(path, filter.equalsValue)
                    else if (filter.lowerBound === null)
                        cb.lessThanOrEqualTo(path as Path<Long>, filter.upperBound)
                    else if (filter.upperBound === null)
                        cb.greaterThanOrEqualTo(path as Path<Long>, filter.lowerBound)
                    else
                        cb.between(path as Path<Long>, filter.lowerBound, filter.upperBound)
        DecimalFilter:
            return if (filter.valueList !== null)
                        path.in(filter.valueList)
                    else if (filter.equalsValue !== null)
                        cb.equal(path, filter.equalsValue)
                    else if (filter.lowerBound === null)
                        cb.lessThanOrEqualTo(path as Path<BigDecimal>, filter.upperBound)
                    else if (filter.upperBound === null)
                        cb.greaterThanOrEqualTo(path as Path<BigDecimal>, filter.lowerBound)
                    else
                        cb.between(path as Path<BigDecimal>, filter.lowerBound, filter.upperBound)
        DayFilter:
            return if (filter.valueList !== null)
                        path.in(filter.valueList)
                    else if (filter.equalsValue !== null)
                        cb.equal(path, filter.equalsValue)
                    else if (filter.lowerBound === null)
                        cb.lessThanOrEqualTo(path as Path<LocalDate>, filter.upperBound)
                    else if (filter.upperBound === null)
                        cb.greaterThanOrEqualTo(path as Path<LocalDate>, filter.lowerBound)
                    else
                        cb.between(path as Path<LocalDate>, filter.lowerBound, filter.upperBound)
        TimestampFilter:
            return if (filter.valueList !== null)
                        path.in(filter.valueList)
                    else if (filter.equalsValue !== null)
                        cb.equal(path, filter.equalsValue)
                    else if (filter.lowerBound === null)
                        cb.lessThanOrEqualTo(path as Path<LocalDateTime>, filter.upperBound)
                    else if (filter.upperBound === null)
                        cb.greaterThanOrEqualTo(path as Path<LocalDateTime>, filter.lowerBound)
                    else
                        cb.between(path as Path<LocalDateTime>, filter.lowerBound, filter.upperBound)
        InstantFilter:
            return if (filter.valueList !== null)
                        path.in(filter.valueList)
                    else if (filter.equalsValue !== null)
                        cb.equal(path, filter.equalsValue)
                    else if (filter.lowerBound === null)
                        cb.lessThanOrEqualTo(path as Path<Instant>, filter.upperBound)
                    else if (filter.upperBound === null)
                        cb.greaterThanOrEqualTo(path as Path<Instant>, filter.lowerBound)
                    else
                        cb.between(path as Path<Instant>, filter.lowerBound, filter.upperBound)
        TimeFilter:
            return if (filter.valueList !== null)
                        path.in(filter.valueList)
                    else if (filter.equalsValue !== null)
                        cb.equal(path, filter.equalsValue)
                    else if (filter.lowerBound === null)
                        cb.lessThanOrEqualTo(path as Path<LocalTime>, filter.upperBound)
                    else if (filter.upperBound === null)
                        cb.greaterThanOrEqualTo(path as Path<LocalTime>, filter.lowerBound)
                    else
                        cb.between(path as Path<LocalTime>, filter.lowerBound, filter.upperBound)
        ByteFilter:
            return if (filter.valueList !== null)
                        path.in(filter.valueList)
                    else if (filter.equalsValue !== null)
                        cb.equal(path, filter.equalsValue)
                    else if (filter.lowerBound === null)
                        cb.lessThanOrEqualTo(path as Path<Byte>, filter.upperBound)
                    else if (filter.upperBound === null)
                        cb.greaterThanOrEqualTo(path as Path<Byte>, filter.lowerBound)
                    else
                        cb.between(path as Path<Byte>, filter.lowerBound, filter.upperBound)
        ShortFilter:
            return if (filter.valueList !== null)
                        path.in(filter.valueList)
                    else if (filter.equalsValue !== null)
                        cb.equal(path, filter.equalsValue)
                    else if (filter.lowerBound === null)
                        cb.lessThanOrEqualTo(path as Path<Short>, filter.upperBound)
                    else if (filter.upperBound === null)
                        cb.greaterThanOrEqualTo(path as Path<Short>, filter.lowerBound)
                    else
                        cb.between(path as Path<Short>, filter.lowerBound, filter.upperBound)
        DoubleFilter:
            return if (filter.valueList !== null)
                        path.in(filter.valueList)
                    else if (filter.equalsValue !== null)
                        cb.equal(path, filter.equalsValue)
                    else if (filter.lowerBound === null)
                        cb.lessThanOrEqualTo(path as Path<Double>, filter.upperBound)
                    else if (filter.upperBound === null)
                        cb.greaterThanOrEqualTo(path as Path<Double>, filter.lowerBound)
                    else
                        cb.between(path as Path<Double>, filter.lowerBound, filter.upperBound)
        FloatFilter:
            return if (filter.valueList !== null)
                        path.in(filter.valueList)
                    else if (filter.equalsValue !== null)
                        cb.equal(path, filter.equalsValue)
                    else if (filter.lowerBound === null)
                        cb.lessThanOrEqualTo(path as Path<Float>, filter.upperBound)
                    else if (filter.upperBound === null)
                        cb.greaterThanOrEqualTo(path as Path<Float>, filter.lowerBound)
                    else
                        cb.between(path as Path<Float>, filter.lowerBound, filter.upperBound)
        UuidFilter:
            return if (filter.valueList !== null)
                        path.in(filter.valueList)
                    else if (filter.equalsValue !== null)
                        cb.equal(path, filter.equalsValue)
        ByteArrayFilter:
            return if (filter.valueList !== null)
                        path.in(filter.valueList)
                    else if (filter.equalsValue !== null)
                        cb.equal(path, filter.equalsValue)
        BytesFilter:
            return if (filter.valueList !== null)
                        path.in(filter.valueList)
                    else if (filter.equalsValue !== null)
                        cb.equal(path, filter.equalsValue)
        default:
            throw new RuntimeException("Unrecognized field filter type: " + filter.ret$PQON)
        }
    }

}
