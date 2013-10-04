#python26 /usr/sbin/euca-add-user $1 >>/dev/null
#if [[ ! $? -eq 0 ]]; then exit $?; fi

psql -h $db_ip -c "update AUTH_USERS set auth_user_is_enabled='true' where auth_user_name='$1'" hippo postgres >>/dev/null
if [[ ! $? -eq 0 ]]; then exit $?; fi




python26 /usr/sbin/euca-describe-users $1|grep USER-KEYS|sed s/USER-KEYS//g
if [[ ! $? -eq 0 ]]; then exit $?; fi
