#!/bin/sh 

mvn >/dev/null
if [ $? -ne 0 ];then 
	cd maven 
	sh install.sh
	cd ..
fi

if [ "$1" == "jar" ];then 
	mvn clean package -Dmaven.test.skip=true compile assembly:single
else 
	mvn clean package -Dmaven.test.skip=true
fi
if [ $? -ne 0 ];then 
	echo "build sbx fail.";
	exit 1;
fi
