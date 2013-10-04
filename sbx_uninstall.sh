#!/bin/sh
##################################################################
#
#
#
##################################################################
# Export path
LOG=`pwd`/sbx_uninstall.log
[ ! -e $LOG ] || rm -f $LOG
export ARCH=`uname -i`
echo '################################################################' | tee -a $LOG
echo "# Uninstall Safebox Service started at `date`" | tee -a $LOG
echo '################################################################' | tee -a $LOG


echo -n "Stop tomcat..." | tee -a $LOG
/opt/apache-tomcat/bin/shutdown.sh >> $LOG
echo "done." | tee -a $LOG

echo -n "Uninstalling tomcat..." | tee -a $LOG
rpm -e apache-tomcat-7.0.26-1.x86_64           | tee -a $LOG  
rpm -e apache-tomcat-host-manager-7.0.26-1.x86_64 | tee -a $LOG
rpm -e apache-tomcat-debuginfo-7.0.26-1.x86_64   | tee -a $LOG
rpm -e apache-tomcat-manager-7.0.26-1.x86_64 | tee -a $LOG
rpm -e apache-tomcat-docs-7.0.26-1.x86_64        | tee -a $LOG
rpm -e apache-tomcat-ROOT-7.0.26-1.x86_64 | tee -a $LOG
rpm -e apache-tomcat-examples-7.0.26-1.x86_64 | tee -a $LOG

rm -rf /opt/apache-tomcat/webapps/sbx_svr* | tee -a $LOG
rm -rf /opt/apache-tomcat/webapps/Safebox* | tee -a $LOG
rm -rf /opt/apache-tomcat/webapps/safebox \
        /opt/apache-tomcat/webapps/safebox.war | tee -a $LOG
rm -rf /opt/apache-tomcat/tomcatSSLKey


psql -h 127.0.0.1 -c "drop table inst_log" hippo postgres >> $LOG 2>&1
psql -h 127.0.0.1 -c "drop table lockfile" hippo postgres >> $LOG 2>&1
psql -h 127.0.0.1 -c "drop table opt_log" hippo postgres >> $LOG 2>&1
psql -h 127.0.0.1 -c "drop table web_opt_log" hippo postgres >> $LOG 2>&1
psql -h 127.0.0.1 -c "drop table regt_log" hippo postgres >> $LOG 2>&1
psql -h 127.0.0.1 -c "drop table syncinfo" hippo postgres >> $LOG 2>&1
#dropdb -h 127.0.0.1 -U postgres hippo | tee -a $LOG

echo "done." | tee -a $LOG

echo '################################################################' | tee -a $LOG
echo "# Uninstall Safebox Service ended at `date`" | tee -a $LOG
echo '################################################################' | tee -a $LOG
