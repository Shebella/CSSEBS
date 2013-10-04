<%@ page import="java.io.*,java.util.*,javax.naming.directory.*,javax.naming.ldap.*,javax.naming.*,java.nio.*,eucakit.*,org.apache.log4j.*,org.postgresql.jdbc3.*,lib.*,java.io.*,lib.Config" %><%!
static Logger log=null;

%><%
try{
		if(log==null){log=DBTool.getLogger("AccountService");}
	
		String user=checkStr(getParam(request,"user"));//transfer parameter "user" in lowercase in checkStr(...).
		String password=getParam(request,"password");
		
		String hardwareId = getParam(request,"hardware_id");
		String instanceName = getParam(request,"instance_name");
		String newEmail=getParam(request,"new_email");//optional

		if("".equals(user)){
			log.info("someone is  signing on without username.");
			throw new AuthenticationException("username is empty.");
		}log.info(user+" is  signing on.");

		
			checkLDAP(user,password);
			log.info(user+" success to pass LDAP auth"); 

			
			
			
			eucaKit uk=new eucaKit(Config.EUCA_IP,Config.DB_IP);
			String key=uk.queryKey(user, hardwareId, instanceName,newEmail);
                        if(key!=null && !key.equals("")){
                               out.print(key);
                               log.info("succeed in quering key.");
                               return;
                        }

			uk.AddUser(user);
			log.info("success to add user");
			
			uk.EnableUser(user);
			log.info("success to enable user");
			
			String instanceKey = uk.genInstanceKey(user, "", "");
			out.print(uk.queryKey(user,hardwareId, instanceKey,newEmail));
			log.info(" success to query key");



}catch(javax.naming.CommunicationException ce){	
	log.info("can not connect to LDAP Server.");
	response.setStatus(response.SC_INTERNAL_SERVER_ERROR);out.println("connect to LDAP fail");return ;
}catch(javax.naming.AuthenticationException ae){
	log.info(" fail to pass LDAP auth.");
        response.setStatus(response.SC_UNAUTHORIZED);out.println("pass LDAP auth fial");return;
}catch(Throwable t){
	log.info("user fail to sign on,due to some uncheckable problem: \n"+t);
	response.setStatus(response.SC_BAD_REQUEST);out.println("Throw exception:"+t.getMessage()+",please see log for more detail.");return ;
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
	str=str.replace(" ","").replace("\"","").replace("\'","");
	

	if(Config.ebsPorperties.getProperty("AccountCaseSensitive")==null||
	   !Config.ebsPorperties.getProperty("AccountCaseSensitive").toLowerCase().equals("true")){
		str=str.toLowerCase();
	}
	
	
	return str;
}



String getBody(HttpServletRequest request)throws Throwable{
	
	InputStreamReader inr=null;
	CharBuffer charbuf=CharBuffer.allocate(300);
	String content=null;
	
	try{
		inr=new InputStreamReader(request.getInputStream());
		while(inr.read(charbuf)>-1){Thread.yield();}
	}finally{
		content=charbuf.toString();
		charbuf.clear();
		inr.close();
	}
	return content;
}

void checkLDAP(String user,String password)throws Throwable{
	LdapContext ldap=null;
	if(password==null||password.equals(""))throw new javax.naming.AuthenticationException ();
	String ldapv=Config.LDAP;
	//String ldapUrl="ldap://"+ldapv+"/CN="+user+",CN=Users,DC=ITRI,DC=DS";
	String ldapUrl=Config.getLDAPUrl(ldapv,user);
	//String account="CN="+user+",CN=Users,DC=ITRI,DC=DS";
	String account=Config.getLDAPAccount(user);
	
	//"{SSHA}sSYxHpepL7wxLgNiAIUv58DsBhepiTzW";
	
	Hashtable<String, String> env = new Hashtable<String, String>();
	env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
	env.put(Context.PROVIDER_URL, ldapUrl);
	env.put(Context.SECURITY_AUTHENTICATION, "simple");
	env.put(Context.SECURITY_PRINCIPAL, account);
	env.put(Context.SECURITY_CREDENTIALS, password);

	try{
		ldap=new InitialLdapContext(env,null);
	}finally{
		if(ldap!=null)ldap.close();
	}
}




%>
