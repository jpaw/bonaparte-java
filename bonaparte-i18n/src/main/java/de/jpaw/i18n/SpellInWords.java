package de.jpaw.i18n;

import java.util.Locale;

import com.ibm.icu.text.NumberFormat;
import com.ibm.icu.text.RuleBasedNumberFormat;

public class SpellInWords {
    public static void main(String[] args) throws Exception {
        int number = 42;
        String locale = "de_DE";

        if (args.length == 0) {
            System.out.println("Usage: Spell (locale) (number)");
        }
        if (args.length >= 1) {
            locale = args[0];
        }
        if (args.length >= 2) {
            number = Integer.valueOf(args[1]);
        }

        Locale loc = new Locale(locale);
        NumberFormat formatter =
            new RuleBasedNumberFormat(loc, RuleBasedNumberFormat.SPELLOUT);
        String result = formatter.format(number);
        System.out.println(number + " spells as <" + result + "> in " + locale);
    }

}
