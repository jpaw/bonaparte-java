package de.jpaw.money

import org.eclipse.xtend.lib.annotations.Data
import static extension de.jpaw.xtend.sions.MoreIterableExtensions.*
import java.math.BigDecimal

@Data
class LineItem {
    String item;
    BigDecimal taxPercentage;
    BigDecimal netAmount;
    BigDecimal taxAmount;
}


class DemoMoreExtensions {
    def static public void main(String [] args) {
        val someStrings = #[ "hello", "world", "to", "every", "one", "and", "to", "me", "as", "well", "to", "the", "world" ]
        
        println('''I got «someStrings.size» words, but only «someStrings.distinct.size» are different''')
        println('''The biggest and smallest words are «someStrings.max» and «someStrings.min»''')
        
        val billLineItems = #[
            new LineItem('The Lord of the Drinks', 7BD, 40BD, 2.80BD),
            new LineItem('A liter of milk',        7BD,  1BD, 0.07BD),
            new LineItem('A game',                19BD, 30BD, 5.70BD),
            new LineItem('A ball',                19BD,  4BD, 0.76BD)
        ]
        
        billLineItems.groupBy([getTaxPercentage], [getTaxAmount], [a, b | a + b ]).forEach[pct, sum | println('''Total tax included of «pct» percent tax is «sum»''')]
        
        // and what do I have to pay?
        println('''My total payment must be «billLineItems.map[getNetAmount + getTaxAmount].sum»''')
    }
}