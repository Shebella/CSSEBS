<%@ page import="java.io.*,java.util.*,javax.naming.directory.*,javax.naming.ldap.*,javax.naming.*,java.nio.*,eucakit.*,org.apache.log4j.*,org.postgresql.jdbc3.*,lib.*,java.sql.*" %><%
Connection con=null;
ResultSet rs=null;
try{	
		
		con=DBTool.getWalrusConnection() ;
		String query="select distinct auth_user_name from auth_users";
		PreparedStatement stmt = con.prepareStatement(query);
		rs = stmt.executeQuery();
		boolean first_flag=true;
		while(rs.next()){
			if(first_flag){first_flag=false;}else{out.print("\n");}
			out.print(rs.getString("auth_user_name"));
		}




}catch(Throwable t){
	response.setStatus(response.SC_BAD_REQUEST);
}finally{


	rs.close();
	DBTool.closeConnection(con);

}	











%>
