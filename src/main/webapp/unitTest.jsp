<%@ page import="org.postgresql.jdbc3.*,java.util.*,java.sql.*,javax.sql.*,javax.naming.*,java.io.*,javax.naming.*,java.math.*,lib.*"%>
<%@ page import="com.google.gson.stream.*,com.google.gson.*"%>






<script>
function sendRegistrationLog_test(){
	
	var req = new XMLHttpRequest();
	req.open('POST','/sbx_svr/test.jsp', false);
	req.setRequestHeader('Log-type','registration-json');
	var data='{\"registration-records\":{\"time\":\"2012-05-21 05:42:08\",\"cmpnm\":\"test\",\"hwid\":\"xxxx\",\"os\":\"xxx\",\"osact\":\"test\",\"cssact\":\"test\",\"clntipv4\":\"test\",\"srvipv4\":\"test\",\"sbxver\":\"test\",\"op\":\"test\",\"rslt\":\"test\"}}';

	
	req.send(data);
	window.location.reload();
}

function sendFileOperationLog_test(){
	
	var req = new XMLHttpRequest();
	req.open('POST','/sbx_svr/test.jsp', false);
	req.setRequestHeader('Log-type','file-operation-json');
	var data='{\"file-operation-records\":[{\"time\":\"2012-05-21 05:42:08\",\"cmpnm\":\"test\",\"hwid\":\"xxxx\",\"os\":\"xxx\",\"osact\":\"test\",\"cssact\":\"test\",\"clntipv4\":\"test\",\"srvipv4\":\"test\",\"sbxver\":\"test\",\"op\":\"test\",\"fpth\":\"test\",\"fsz\":1000,\"rslt\":\"test\"}]}';

	
	req.send(data);
	window.location.reload();
}


//sendRegistrationLog_test();
//sendFileOperationLog_test();


</script>

<form >
	<input type=button value="Send file-operatoin msg" onClick="sendFileOperationLog_test();return false;">
	<input type=button value="Send registration msg" onClick="sendRegistrationLog_test();return false;">

</form>



<%
DataSource source=this.getDataSource();
Connection con=source.getConnection();
Statement  st=con.createStatement();
ResultSet rs=null;

try{
	rs=st.executeQuery("select * from regt_log where op='test'");
	while(rs.next()){
		out.print(rs.getString(1)+" "+rs.getString(2)+" "+rs.getString(3)+" "+rs.getString(4)+" "+rs.getString(5)+" "+rs.getString(6)+" "+rs.getString(7)+" "+rs.getString(8)+" "+rs.getString(9)+" "+rs.getString(10)+" "+rs.getString(11)+" "+rs.getString(12)+" "+rs.getString(13)+"<br>");

	}
	
	out.print("<br><br><br>");	

	rs=st.executeQuery("select * from opt_log where op='test'");
	while(rs.next()){
		out.print(rs.getString(1)+" "+rs.getString(2)+" "+rs.getString(3)+" "+rs.getString(4)+" "+rs.getString(5)+" "+rs.getString(6)+" "+rs.getString(7)+" "+rs.getString(8)+" "+rs.getString(9)+" "+rs.getString(10)+" "+rs.getString(11)+" "+rs.getString(12)+" "+rs.getString(13)+"<br>");

	}


} catch(Throwable e) {
	out.print("test"+e);
}finally{
	if(con != null) {        try {con.close();}catch(SQLException e) {}          }
}
%>


<%!


DataSource getDataSource() throws NamingException{
	Context ctx = new InitialContext();//could be singleton if it's thread-safe.
	DataSource ds=null;
	try{
		ds = (DataSource)ctx.lookup("postgres");
	
	
	
	}catch(NamingException nie){
		Jdbc3PoolingDataSource source=new Jdbc3PoolingDataSource();
		source.setDataSourceName("A Data Source");
		source.setServerName(Config.DB_IP);
		source.setDatabaseName("safebox_logging");
		source.setUser("postgres");
		source.setPassword("");
		source.setMaxConnections(10);
				
		ctx.bind("postgres",source);
		ds=(DataSource)source;
		
	}
	
	
	return ds;
	
}










%>

