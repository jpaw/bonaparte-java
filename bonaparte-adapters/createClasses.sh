#!/bin/bash
# create subclasses for Milli, Micro, Nano, Pico from the Femto subclass

folder=src/main/java/de/jpaw/adapters/fixedpoint
ext=UnitsAdapter.java
for x in Milli Micro Nano Pico
do
	sed -e s/Femto/$x/g < $folder/Femto$ext > $folder/$x$ext
done
