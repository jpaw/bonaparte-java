#!/bin/bash

folder=src/main/java/de/jpaw/bonaparte/adapters/fixedpoint/bigDecimal

# create subclasses for Milli, Micro, Nano, Pico from the Femto subclass
ext=UnitsAdapter.java
for x in Milli Micro Nano Pico
do
	sed -e s/Femto/$x/g < $folder/BigFemto$ext > $folder/Big$x$ext
done

# create subclasses for Tenths and Hundreds
ext=Adapter.java
for x in Tenths Hundreds
do
	sed -e s/Units/$x/g < $folder/BigUnits$ext > $folder/Big$x$ext
done
