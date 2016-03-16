#!/usr/bin/env sh
MVN=mvn

if [ -z $MVN ]; then
	echo "Variable \$MVN needs to be set";
	return 1;
fi

$MVN clean compile assembly:single
