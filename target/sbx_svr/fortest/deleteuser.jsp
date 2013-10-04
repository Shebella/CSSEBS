<%@ page import="java.io.*,java.util.*,javax.naming.directory.*,javax.naming.ldap.*,javax.naming.*,java.nio.*,eucakit.*,org.apache.log4j.*,org.postgresql.jdbc3.*,lib.*,java.sql.*" %><%
eucaKit uk= new eucaKit(Config.EUCA_IP,Config.DB_IP);
String user=request.getParameter("user");
if(user!=null){
	uk.DeleteUser(user);
}
%>
