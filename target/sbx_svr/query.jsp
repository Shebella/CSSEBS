<%@ page import="org.postgresql.jdbc3.*,java.util.*,java.sql.*,javax.sql.*,javax.naming.*,java.io.*,javax.naming.*,java.math.*,lib.*"%><%@ page import="com.google.gson.stream.*,com.google.gson.*,lib.*"%><%



DataSource source=null;
Connection con = null;
BufferedReader bdr=null;






try {

   if(request.getParameter("queryevent")!=null){
	source=this.getDataSource();
	con = source.getConnection();
	bdr=new BufferedReader(new InputStreamReader(request.getInputStream()));
	
	String[] param=new String[2];
		param[0]=request.getParameter("account");
		param[1]=request.getParameter("lastsynctime");
	String preStat="select rectime,\"time\",hwid,cssact,op,fpth,file_version,isfolder,rslt from opt_log where cssact=? and rslt='succ' and time>to_timestamp(?,'YYYY-MM-DD HH24:MI:ss')";


	query(out,con,bdr,preStat,param);





   }else if(request.getParameter("queryfile")!=null){
	
	source=this.getWalrusDataSource();
	con = source.getConnection();
	con.setAutoCommit(false);
	bdr=new BufferedReader(new InputStreamReader(request.getInputStream()));





	String[] param=new String[1];
		param[0]=request.getParameter("bucketname");
	String preStat="select * from (select coalesce(sum(size),0) total_usage from objects where bucket_name=?) a,(select cast(storage_max_bucket_size_mb as bigint)*1048576 max_size from walrus_info) b limit 1 ";



	query(out,con,bdr,preStat,param);


  }else if(request.getParameter("lockfile")!=null){
	
        source=this.getDataSource();
        con = source.getConnection();
        bdr=new BufferedReader(new InputStreamReader(request.getInputStream()));





        String[] param=new String[4];
                param[0]=request.getParameter("account");
		param[1]=request.getParameter("hwid");
		param[2]=request.getParameter("fpath");
		param[3]=request.getParameter("status");
        String preStat="insert into lockfile values(?,?,?,?)";



        update(out,con,bdr,preStat,param);




  }else if(request.getParameter("addevent")!=null){
		
        source=this.getDataSource();
        con = source.getConnection();
        bdr=new BufferedReader(new InputStreamReader(request.getInputStream()));





        String[] param=new String[4];
                param[0]=request.getParameter("account");
		param[1]=request.getParameter("hwid");
		param[2]=request.getParameter("fpath");
		param[3]=request.getParameter("status");
		


        String preStat="insert into opt_log(id,rectime,time,cmpnm,hwid,\"OS\",osact,cssact,clntipv4,srvipv4,sbxver,op,fpth,fsz,file_version,isfolder,rslt,inst_id)  values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        PreparedStatement stmt=con.prepareStatement(preStat);

	String log=null;
	while((log=bdr.readLine())!=null){
		try{
			String[] item=log.split("\t");   
			stmt.setString(1,UUID.randomUUID().toString());//id
			stmt.setTimestamp(2,this.getUTC0());//rectime
			
			stmt.setTimestamp(3,Timestamp.valueOf(item[0]));//time
			stmt.setString(4,item[1]);//cmpnm
			stmt.setString(5,item[2]);//hwid
			stmt.setString(6,item[3]);//OS
			stmt.setString(7,item[4]);//osact
			stmt.setString(8,item[5]);//cssact
			stmt.setString(9,item[6]);//clnip
			stmt.setString(10,item[7]);//srvip
			stmt.setString(11,item[8]);//sbxver
			stmt.setString(12,item[9]);//op
			stmt.setString(13,item[10]);//fpath
			stmt.setBigDecimal(14,new BigDecimal(item[11]));//fsz
			stmt.setString(15,item[12]);//rslt
			stmt.setString(16,item[13]);//file_version
			stmt.setString(17,item[14]);//is_folder
			stmt.setString(18,"dummy");//inst_id  
			
			
			
			stmt.executeUpdate();
			
			
			
		}catch(Throwable t){}

	}

  }else{



	out.print("Usage:<br/>");
	out.print("<font color=\"blue\">query event:</font>  url?queryevent&account=?&lastsynctime=?<br/>");
	out.print("<font color=\"blue\">query file:</font>  url?queryfile&account=?&bucketname=?<br/>");
out.print("<font color=\"blue\">add event:</font> url?addevent<font color=red>(with event log send in content)</font><br/>");
	out.print("<font color=\"blue\">lock file:</font> url?lockfile&account=?&hwid=?&fpath=?&status=?");
  }

    
   	if(con!=null&&!con.getAutoCommit()){con.commit();}
  
    
} catch(SQLException e) {
	out.print(e);
	e.printStackTrace();
}catch(IOException ioe){
	out.print(ioe); 
	ioe.printStackTrace();
}finally {
	if(bdr!=null){bdr.close();}
	if(con != null) {try {con.close();}catch(SQLException e) {}}
}


%><%!


void update(JspWriter out,Connection con,BufferedReader bdr,String preStat,String[] param)throws SQLException,IOException,Throwable{
        PreparedStatement stmt=con.prepareStatement(preStat);


        for(int i=0;i<param.length;i++){
                if(param[i]==null)throw new Throwable("some argument is null.");
                stmt.setString(i+1,param[i]);
        }
	try{
        	stmt.executeUpdate();
		out.print("succ");
	}catch(Throwable t){out.print(t);}


}



void query(JspWriter out,Connection con,BufferedReader bdr,String preStat,String[] param)throws SQLException,IOException,Throwable{
	PreparedStatement stmt=con.prepareStatement(preStat);
	
	
	for(int i=0;i<param.length;i++){
		if(param[i]==null)throw new Throwable("some argument is null.");
		stmt.setString(i+1,param[i]);
	}

	ResultSet rs=stmt.executeQuery();
	String result=null;
	
	while(rs.next()){
		result="";
		int j=rs.getMetaData().getColumnCount();
		for(int i=0;i<j;i++){
			result=result+rs.getString(i+1)+"\t";
		}
		out.print(result+"\n");
	}


	rs.close();
}





DataSource getWalrusDataSource() throws NamingException{
	return DBTool.getWalrusDatasource();
}




DataSource getDataSource() throws NamingException{
	return DBTool.getWalrusDatasource();
}



Timestamp getUTC0(){
	Calendar c=Calendar.getInstance();
	return new Timestamp(c.getTimeInMillis()-c.getTimeZone().getRawOffset());
}
















%>
