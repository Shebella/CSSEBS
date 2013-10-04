#!/bin/sh
##################################################################
#
#
#
##################################################################
# Export path
LOG=`pwd`/sbx_deploy.log
[ ! -e $LOG ] || rm -f $LOG
export ARCH=`uname -i`
echo '################################################################' | tee -a $LOG
echo "# Safebox Service deploy started at `date`" | tee -a $LOG
echo '################################################################' | tee -a $LOG

echo -n "Installing tomcat..." | tee -a $LOG
rpm -ivh apache-tomcat-7.0.26-1.x86_64.rpm --nodeps --force >> $LOG 2>&1
rpm -ivh apache-tomcat-host-manager-7.0.26-1.x86_64.rpm --nodeps --force >> $LOG 2>&1
rpm -ivh apache-tomcat-debuginfo-7.0.26-1.x86_64.rpm --nodeps --force >> $LOG 2>&1
rpm -ivh apache-tomcat-manager-7.0.26-1.x86_64.rpm --nodeps --force >> $LOG 2>&1
rpm -ivh apache-tomcat-docs-7.0.26-1.x86_64.rpm --nodeps --force >> $LOG 2>&1
rpm -ivh apache-tomcat-ROOT-7.0.26-1.x86_64.rpm --nodeps --force >> $LOG 2>&1
rpm -ivh apache-tomcat-examples-7.0.26-1.x86_64.rpm --nodeps --force >> $LOG 2>&1

tar -xvzf tomcatSSLKey.tar.gz -C /opt/apache-tomcat/ >> $LOG 2>&1

chkconfig apache-tomcat on

echo "done."

echo -n "Deploy EBS services..." | tee -a $LOG
	cp -rpf sbx_svr.war /opt/apache-tomcat/webapps| tee -a $LOG
	sed -e 's/Connector port="8080"/Connector port="8088"/' /opt/apache-tomcat/conf/server.xml > /tmp/server.xml
	mv /tmp/server.xml /opt/apache-tomcat/conf/server.xml
echo "done."


#su - postgres -c "createdb hippo" |tee -a $LOG
echo -n "Create EBS services database..."
	psql -h 127.0.0.1 hippo postgres < ./safeboxlogging.sql >> $LOG
echo "done."

/opt/apache-tomcat/bin/startup.sh >> $LOG
sleep 10
curl http://127.0.0.1:8088/sbx_svr/rest/EBS/queryuserinfo?account=admin > /dev/null 2>&1

if [ $? -ne 0 ] ; then
	echo '################################################################' | tee -a $LOG
	echo "# Safebox Service deploy failed at `date`" | tee -a $LOG
	echo '################################################################' | tee -a $LOG
	exit 1;
else
	echo '################################################################' | tee -a $LOG
	echo "# Safebox Service deploy ended at `date`" | tee -a $LOG
	echo '################################################################' | tee -a $LOG
	exit 0;
fi

