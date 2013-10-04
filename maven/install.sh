#!/bin/sh

cp jpackage.repo /etc/yum.repos.d/
rpm -Uvh ./jpackage-utils-compat-el5-0.0.1-1.noarch.rpm
#yum update -y --nogpgcheck
yum install -y --nogpgcheck maven2

if [ "$?" -eq "0" ];then
	exit 0;
else
	echo "install maven fail.";
	exit 1; 
fi
