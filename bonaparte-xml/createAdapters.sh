#!/bin/bash

# outer loop for Exact and Round variants
rounding=false
for rnd in Exact Round; do

	# create Long adapters
	for digits in 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18; do
		classname=ScaledLongAdapter$digits$rnd
		cat > src/main/java/de/jpaw/xml/jaxb/scaledFp/$classname.java << EOF
package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledLongAdapter;

/** XmlAdapter for fixed-point arithmetic using $digits fractional digits. */
public class $classname extends AbstractScaledLongAdapter {

    public $classname() {
        super($digits, $rounding);
    }
}
EOF
	done

	# create Int adapters
	for digits in 1 2 3 4 5 6 7 8 9; do
		classname=ScaledIntegerAdapter$digits$rnd
		cat > src/main/java/de/jpaw/xml/jaxb/scaledFp/$classname.java << EOF
package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledIntegerAdapter;

/** XmlAdapter for fixed-point arithmetic using $digits fractional digits. */
public class $classname extends AbstractScaledIntegerAdapter {

    public $classname() {
        super($digits, $rounding);
    }
}
EOF
	done

	# create Short adapters
	for digits in 1 2 3 4; do
		classname=ScaledShortAdapter$digits$rnd
		cat > src/main/java/de/jpaw/xml/jaxb/scaledFp/$classname.java << EOF
package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledShortAdapter;

/** XmlAdapter for fixed-point arithmetic using $digits fractional digits. */
public class $classname extends AbstractScaledShortAdapter {

    public $classname() {
        super($digits, $rounding);
    }
}
EOF
	done

	# create Byte adapters
	for digits in 1 2; do
		classname=ScaledByteAdapter$digits$rnd
		cat > src/main/java/de/jpaw/xml/jaxb/scaledFp/$classname.java << EOF
package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledByteAdapter;

/** XmlAdapter for fixed-point arithmetic using $digits fractional digits. */
public class $classname extends AbstractScaledByteAdapter {

    public $classname() {
        super($digits, $rounding);
    }
}
EOF
	done
	# second iteration with rounding
	rounding=true
done

