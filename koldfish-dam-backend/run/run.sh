#!/usr/bin/env sh
JAVA=java
DAM_JAR=target/koldfish-dam-backend-*-jar-with-dependencies.jar
ACTIVEMQ_URL=$1
LOG4J_CONFIG=run/log4j2.xml

if [ -z $JAVA ]; then
	echo "Variable \$JAVA needs to be set";
	return 1;
fi


if [ -z $DAM_JAR ]; then
	echo "Variable \$DAM_JAR needs to be set";
	return 2;
fi

if [ -z $ACTIVEMQ_URL ]; then
	echo "Variable \$ACTIVEMQ_URL needs to be set";
	return 3;
fi

$JAVA -Dorg.apache.activemq.AMQ_HOST=$ACTIVEMQ_URL `if [ ! -z $LOG4J_CONFIG ]; then echo -Dlog4j.configurationFile=$LOG4J_CONFIG;fi` -jar $DAM_JAR
