<%@ page import="java.io.*,java.util.*,javax.naming.directory.*,javax.naming.ldap.*,javax.naming.*,java.nio.*,eucakit.*,org.apache.log4j.*,org.postgresql.jdbc3.*,lib.*,java.sql.*" %><%
Connection con=null;
ResultSet rs=null;

try{
	String user=request.getParameter("user");
		if(user==null)user="admin";

		con=DBTool.getWalrusConnection() ;
		String query="select * from "+
"(select auth_user_name, auth_user_query_id,auth_user_secretkey from auth_users where auth_user_name=?) a,"+
"(select DISTINCT system_info_cloud_host, ns_address from system_info where dns_domain='localhost' and ns_address='127.0.0.1') b";
		PreparedStatement stmt = con.prepareStatement(query);
		stmt.setString(1,user);

		rs = stmt.executeQuery();
		while(rs.next()){
			out.print("export HOST="+rs.getString(4)+"\n");
			out.print("export EC2_ACCESS_KEY="+rs.getString(2)+"\n");
			out.print("export EC2_SECRET_KEY="+rs.getString(3)+"\n");

		}





}catch(Throwable t){

	response.setStatus(response.SC_BAD_REQUEST);

}finally{


	rs.close();

	DBTool.closeConnection(con);



}  
%>
