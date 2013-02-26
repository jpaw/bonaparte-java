#!/bin/bash
echo "Usage: set-others (oldversion) (newversion)"
if [ x$2 = x ]; then
    exit
fi
# find .. -name pom.xml -exec ./gres $1 $2 \{\} \;
./gres $1 $2 ../*/modules/*/pom.xml ../*/pom.xml
./gres $1 $2 ../*/modules/generator/META-INF/MANIFEST.MF
