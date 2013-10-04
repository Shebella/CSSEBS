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
echo "# sbx_deploy.sh started at `date`" | tee -a $LOG
echo '################################################################' | tee -a $LOG

echo -n "Installing tomcat..." | tee -a $LOG
rpm -ivh apache-tomcat-7.0.26-1.x86_64.rpm --nodeps --force | tee -a $LOG
rpm -ivh apache-tomcat-host-manager-7.0.26-1.x86_64.rpm --nodeps --force | tee -a $LOG
rpm -ivh apache-tomcat-debuginfo-7.0.26-1.x86_64.rpm --nodeps --force | tee -a $LOG
rpm -ivh apache-tomcat-manager-7.0.26-1.x86_64.rpm --nodeps --force | tee -a $LOG
rpm -ivh apache-tomcat-docs-7.0.26-1.x86_64.rpm --nodeps --force | tee -a $LOG
rpm -ivh apache-tomcat-ROOT-7.0.26-1.x86_64.rpm --nodeps --force | tee -a $LOG
rpm -ivh apache-tomcat-examples-7.0.26-1.x86_64.rpm --nodeps --force| tee -a $LOG

echo "done."

cp -rpf target/sbx_svr.war /opt/apache-tomcat/webapps| tee -a $LOG
sed -e 's/Connector port="8080"/Connector port="8088"/' /opt/apache-tomcat/conf/server.xml > /tmp/server.xml
mv /tmp/server.xml /opt/apache-tomcat/conf/server.xml

#su - postgres -c "createdb safebox_logging" |tee -a $LOG
psql -h 127.0.0.1 safebox_logging postgres < ./safeboxlogging.sql | tee -a $LOG


/opt/apache-tomcat/bin/startup.sh | tee -a $LOG
sleep 10
curl http://127.0.0.1:8088/sbx_svr/rest/EBS/queryuserinfo?account=admin > /dev/null 2>&1

if [ $? -ne 0 ] ; then
	echo '################################################################' | tee -a $LOG
	echo "# sbx_deploy.sh failed at `date`" | tee -a $LOG
	echo '################################################################' | tee -a $LOG
	
else
	echo '################################################################' | tee -a $LOG
	echo "# sbx_deploy.sh ended at `date`" | tee -a $LOG
	echo '################################################################' | tee -a $LOG
fi

