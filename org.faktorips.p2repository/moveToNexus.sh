#!/bin/bash
REPO_ID=releases
REPO_URL=$1
VERSION_ORIG=$2
VERSION=$3
JAR_DIRECTORY=$4
RUNTIME_LIB=org.faktorips.runtime.java5
RUNTIME_LIB_JAR=JAR_DIRECTORY/$RUNTIME_LIB_$VERSION_ORIG.jar
RUNTIME_LIB_SOURCE_JAR=JAR_DIRECTORY/$RUNTIME_LIB.source_$VERSION_ORIG.jar
RUNTIME_LIB_JAVADOC_JAR=JAR_DIRECTORY/$RUNTIME_LIB.javadoc_$VERSION_ORIG.jar
RUNTIME_LIB_POM=../$RUNTIME_LIB/nexus/pom.xml
VALUETYPES_LIB=org.faktorips.valuetypes.java5
VALUETYPES_LIB_JAR=JAR_DIRECTORY/$VALUETYPES_LIB_$VERSION_ORIG.jar
VALUETYPES_LIB_SOURCE_JAR=JAR_DIRECTORY/$VALUETYPES_LIB.source_$VERSION_ORIG.jar
VALUETYPES_LIB_JAVADOC_JAR=JAR_DIRECTORY/$VALUETYPES_LIB.javadoc_$VERSION_ORIG.jar
VALUETYPES_POM=../$VALUETYPES_LIB/nexus/pom.xml

mvn deploy:deploy-file -Durl=$REPO_URL -DrepositoryId=$REPO_ID -Dfile=$RUNTIME_LIB_JAR -Dsources=$RUNTIME_LIB_SOURCE_JAR -DgroupId=org.faktorips -DartifactId=$RUNTIME_LIB -Dversion=$VERSION -Dpackaging=jar -DpomFile=$RUNTIME_LIB_POM
mvn deploy:deploy-file -Durl=$REPO_URL -DrepositoryId=$REPO_ID -Dfile=$VALUETYPES_LIB_JAR -Dsources=$VALUETYPES_LIB_SOURCE_JAR -DgroupId=org.faktorips -DartifactId=$VALUETYPES_LIB -Dversion=$VERSION -Dpackaging=jar -DpomFile=$VALUETYPES_LIB_POM
