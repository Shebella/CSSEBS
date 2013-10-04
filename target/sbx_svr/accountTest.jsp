<%@ page import="java.io.*,java.util.*,javax.naming.directory.*,javax.naming.ldap.*,javax.naming.*,java.nio.*,eucakit.*,lib.*,java.sql.*" %>
<%@ page import="lib.Config" %>
<%

	
		Connection con=null;	
	try{		
		eucaKit uk=new eucaKit(Config.EUCA_IP,Config.DB_IP);
		uk.AddUser("testUser");
		
		con=DBTool.getWalrusConnection() ;
		uk.getKeyPair(con,"testUser");
		out.print("OK");

	}catch(Throwable t){
		out.print("Account Service Error");
	}finally{
		DBTool.closeConnection(con);
	}
		



	
	
%>


<%!



String getParam(HttpServletRequest request,String key){
	String param=request.getParameter(key);
	if(param==null||param.equals("")){
		param=request.getHeader(key);
	}
	
	return param;
}


String checkStr(String str){
	if(str==null)return "";
	return str.replace(" ","").replace("\"","").replace("\'","");
}



String getBody(HttpServletRequest request)throws Throwable{
	
	InputStreamReader inr=null;
	CharBuffer charbuf=CharBuffer.allocate(300);
	String content=null;
	
	try{
		inr=new InputStreamReader(request.getInputStream());
		
		while(inr.read(charbuf)>-1){Thread.yield();}
		
	
	}catch(Throwable t){
		throw t;
	}finally{
		content=charbuf.toString();
		charbuf.clear();
		inr.close();
	}
	return content;
}



%>
