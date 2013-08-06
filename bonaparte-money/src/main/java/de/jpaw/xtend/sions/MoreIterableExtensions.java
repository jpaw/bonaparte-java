package de.jpaw.xtend.sions;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.Functions.Function2;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;

public class MoreIterableExtensions {

    /** Just a shorthand for replacement of a value by a default value. */
    static public <T> T nvl(T x, T inCaseOfNull) {
        return x == null ? x : inCaseOfNull;
    }

    /**
     * Returns the maximum element of an iterable.
     * 
     * @param iterable
     *            the items to be evaluated. May not be <code>null</code>.
     * @return the maximum element, or null if there was no not-null element.
     */
    static public <T extends Comparable<? super T>> T max(Iterable<T> iterable) {
        return max(iterable, null);
    }

    static public <T extends Comparable<? super T>> T max(Iterable<T> iterable, T minusInfinity) {
        T maximum = minusInfinity;
        Iterator<T> iterator = iterable.iterator();
        while (iterator.hasNext()) {
            T next = iterator.next();
            if (next != null && (maximum == null || maximum.compareTo(next) < 0))
                maximum = next;
        }
        return maximum;
    }

    /**
     * Returns the minimum element of an iterable.
     * 
     * @param iterable
     *            the items to be evaluated. May not be <code>null</code>.
     * @return the minimum element, or null if there was no not-null element.
     */
    static public <T extends Comparable<? super T>> T min(Iterable<T> iterable) {
        return min(iterable, null);
    }
    
    static public <T extends Comparable<? super T>> T min(Iterable<T> iterable, T infinity) {
        T minimum = infinity;
        Iterator<T> iterator = iterable.iterator();
        while (iterator.hasNext()) {
            T next = iterator.next();
            if (next != null && (minimum == null || minimum.compareTo(next) > 0))
                minimum = next;
        }
        return minimum;
    }

  

    /**
     * Puts the iterable's elements into buckets and applies a reduction method to the elements of each bucket.
     * Similar to SQL's GROUP BY directive.
     * Example of use:
     * lineItems.groupBy([taxPercentage], [taxAmount], [sum, element | sum + element ]).forEach[pct, sum | (output pct and sum here...)] 
     * @param iterable
     *            the items to be grouped. May not be <code>null</code>.
     * @return the minimum element, or null if there was no not-null element.
     */
    static public <T, G, R> Map<G,R> groupBy(Iterable<T> iterable,
            Function1<? super T, ? extends G> grouper,
            Function1<? super T, ? extends R> mapper, Function2 <? super R, ? super R, ? extends R> reducer) {
        Map<G,R> map = new HashMap<G,R>(10);
        Iterator<T> iterator = iterable.iterator();
        while (iterator.hasNext()) {
            T element = iterator.next();
            G group = grouper.apply(element);
            if (group != null) {
                R newElement = mapper.apply(element);
                R oldElement = map.put(group, newElement);
                if (oldElement != null)
                    // store the reduced value
                    map.put(group, reducer.apply(oldElement, newElement));
            }
        }
        return map;
    }

    static public <T> Iterable<T> distinct(Iterable<T> iterable) {
        return IteratorExtensions.toIterable(IterableExtensions.toSet(iterable).iterator());
    }


    static public BigDecimal sum(Iterable<BigDecimal> iterable) {
        BigDecimal sum = BigDecimal.ZERO;
        for (BigDecimal a : iterable) {
            sum = sum.add(a);
        }
        return sum;
    }
    
    /* alternate implementation
    static public BigDecimal sum2(Iterable<BigDecimal> iterable) {
        BigDecimal sum = BigDecimal.ZERO;
        Iterator<BigDecimal> iterator = iterable.iterator();
        while (iterator.hasNext()) {
            BigDecimal next = iterator.next();
            if (next != null)
                sum = sum.add(next);
        }
        return sum;
    } */

}