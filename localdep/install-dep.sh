#!/bin/sh

mvn install:install-file -Dfile=./jodconverter-core-3.0-beta4.jar \
    -DgroupId=org.artofsolving -DartifactId=jodconverter-core \
    -Dversion=3.0-beta4 -Dpackaging=jar

mvn install:install-file -Dfile=./jackson-all-1.7.6.jar \
    -DgroupId=org.codehaus.jackson -DartifactId=jackson-all \
    -Dversion=1.7.6 -Dpackaging=jar

