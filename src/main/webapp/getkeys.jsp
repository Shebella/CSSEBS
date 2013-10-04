<%@ page import="java.io.*,java.util.*,javax.naming.directory.*,javax.naming.ldap.*,javax.naming.*,java.nio.*,eucakit.*,org.apache.log4j.*,org.postgresql.jdbc3.*,lib.*,java.sql.*" %><%
Connection con=null;
try{	
		
		String user=checkStr(getParam(request,"user"));
                        eucaKit uk=new eucaKit(Config.EUCA_IP,Config.DB_IP);
		con=DBTool.getWalrusConnection() ;
		out.print(uk.getKeyPair(con,user));
}catch(Throwable t){
	response.setStatus(response.SC_BAD_REQUEST);
}finally{
	DBTool.closeConnection(con);

}	
	
	
%><%!



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

boolean checkLDAP(String user,String password){
	LdapContext ldap=null;
try{
	if(password==null||password.equals(""))return false;
	String ldapv=Config.LDAP;
	String ldapUrl="ldap://"+ldapv+"/CN="+user+",CN=Users,DC=ITRI,DC=DS";
	String account="CN="+user+",CN=Users,DC=ITRI,DC=DS";
	//"{SSHA}sSYxHpepL7wxLgNiAIUv58DsBhepiTzW";
	
	Hashtable<String, String> env = new Hashtable<String, String>();
	env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
	env.put(Context.PROVIDER_URL, ldapUrl);
	env.put(Context.SECURITY_AUTHENTICATION, "simple");
	env.put(Context.SECURITY_PRINCIPAL, account);
	env.put(Context.SECURITY_CREDENTIALS, password);


	ldap=new InitialLdapContext(env,null);
	ldap.close();
}catch(Throwable t){
	return false;
}


	return true;
}




%>
