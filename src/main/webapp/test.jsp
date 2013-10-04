<%@ page import="org.postgresql.jdbc3.*,java.util.*,java.sql.*,javax.sql.*,javax.naming.*,java.io.*,javax.naming.*,java.math.*"%>
<%@ page import="com.google.gson.stream.*,com.google.gson.*,lib.*"%><%




String ak=request.getHeader("AccessKey"),
		client_ip=request.getHeader("Address"),
		logType=request.getHeader("Log-type"),
		lenght=request.getHeader("Content-length"),
		etag=request.getHeader("ETag"),
		signature=request.getHeader("Signatue"); 

DataSource source=this.getDataSource();
Connection con = null;
PreparedStatement stmt=null;
ResultSet result=null;
BufferedReader bdr=new BufferedReader(new InputStreamReader(request.getInputStream()));

try {
    con = source.getConnection();
    if(logType!=null&&logType.equalsIgnoreCase("installation")){
    	inst_logging(con,bdr);	
    }else if(logType!=null&&logType.equalsIgnoreCase("registration")){
    	rgst_logging(con,bdr);
    }else if(logType!=null&&logType.equalsIgnoreCase("file-operation")){
    	opt_logging(con,bdr);
    }else if(logType!=null&&logType.equalsIgnoreCase("file-operation-json")){
	opt_logging_json(con,bdr);
    }else if(logType!=null&&logType.equalsIgnoreCase("registration-json")){
	rgst_logging_json(con,bdr);
    }else{



    	//debug
    	
    	
    	
    	
    }
    
   	if(!con.getAutoCommit()){con.commit();}
  
    
} catch(SQLException e) {
	out.print(e);
	e.printStackTrace();
}catch(IOException ioe){
	out.print(ioe); 
	ioe.printStackTrace();
}finally {
	if(bdr!=null){bdr.close();}
	if(result!=null&&!result.isClosed()){result.close();}
	if(stmt!=null&&!stmt.isClosed()){stmt.close();}
	if(con != null) {try {con.close();}catch(SQLException e) {}}
}



%><%!







void inst_logging(Connection con,BufferedReader bdr)throws SQLException,IOException {
	PreparedStatement stmt=con.prepareStatement("insert into inst_log  values(?,?,?,?,?,?,?,?,?,?,?,?)");
	
	String log=bdr.readLine();
	
	String[] item=log.split("\t");
	stmt.setString(1,UUID.randomUUID().toString());//id
	stmt.setTimestamp(2,this.getUTC0());//rectime
	stmt.setTimestamp(3,Timestamp.valueOf(item[0]));//time
	stmt.setString(4,item[1]);//computer name
	stmt.setString(5,item[2]);//hwid
	stmt.setString(6,item[3]);//OS
	stmt.setString(7,item[4]);//osacc 
	stmt.setString(8,item[5]);//clnip 
	stmt.setString(9,item[6]);//srvip
	stmt.setString(10,item[7]);//sbxver
	stmt.setString(11,item[8]);//op
	stmt.setString(12,item[9]);//rslt
	stmt.executeUpdate();
	
}


static class rgst_record{String time,cmpnm,hwid,os,osact,cssact,clntipv4,srvipv4,sbxver,op,rslt;}
void rgst_logging_json(Connection con,BufferedReader bdr)throws SQLException,IOException {

	PreparedStatement stmt=con.prepareStatement("insert into regt_log  values(?,?,?,?,?,?,?,?,?,?,?,?,?)");
	
	Gson j=new Gson();
	JsonReader reader = new JsonReader(bdr);


	reader.beginObject();
	while(reader.hasNext()){
		reader.nextName();

		rgst_record record=j.fromJson(reader,rgst_record.class);
		
	
		stmt.setString(1,UUID.randomUUID().toString());//id
		stmt.setTimestamp(2,this.getUTC0());//rectime
		
		stmt.setTimestamp(3,Timestamp.valueOf(record.time));//time
		stmt.setString(4,record.cmpnm);//computer name  
		stmt.setString(5,record.hwid);//hwid
		stmt.setString(6,record.os);//OS
		stmt.setString(7,record.osact);//osact  
		stmt.setString(8,record.cssact);//cssaccount 
		stmt.setString(9,record.clntipv4);//clntip
		stmt.setString(10,record.srvipv4);//srvip
		stmt.setString(11,record.sbxver);//sbxver	
		stmt.setString(12,record.op);//op
		stmt.setString(13,record.rslt);//rslt
		stmt.executeUpdate();


	}
	reader.endObject();
	
	reader.close();
	
}



void rgst_logging(Connection con,BufferedReader bdr)throws SQLException,IOException {

	PreparedStatement stmt=con.prepareStatement("insert into regt_log  values(?,?,?,?,?,?,?,?,?,?,?,?,?)");
	String log=bdr.readLine();
	String[] item=log.split("\t");
	
	
	
	stmt.setString(1,UUID.randomUUID().toString());//id
	stmt.setTimestamp(2,this.getUTC0());//rectime
	
	
	stmt.setTimestamp(3,Timestamp.valueOf(item[0]));//time
	stmt.setString(4,item[1]);//computer name  
	stmt.setString(5,item[2]);//hwid
	stmt.setString(6,item[3]);//OS
	stmt.setString(7,item[4]);//osact  
	stmt.setString(8,item[5]);//cssaccount 
	stmt.setString(9,item[6]);//clntip
	stmt.setString(10,item[7]);//srvip
	stmt.setString(11,item[8]);//sbxver	
	stmt.setString(12,item[9]);//op
	stmt.setString(13,item[10]);//rslt  
	
	
	
	stmt.executeUpdate();
	
	
	
}


static class opt_record{String time,cmpnm,hwid,os,osact,cssact,clntipv4,srvipv4,sbxver,op,fpth,rslt;Integer fsz;}
void opt_logging_json(Connection con,BufferedReader bdr)throws SQLException,IOException {
	

	PreparedStatement stmt=con.prepareStatement("insert into opt_log(id,rectime,time,cmpnm,hwid,\"OS\",osact,cssact,clntipv4,srvipv4,sbxver,op,fpth,fsz,rslt,inst_id)  values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");


	
	Gson j=new Gson();
	JsonReader reader = new JsonReader(bdr);
	
	reader.beginObject();

	while(reader.hasNext()){
		reader.nextName();
		
		reader.beginArray();
		while(reader.hasNext()){
			opt_record record=j.fromJson(reader,opt_record.class);


			try{  
				stmt.setString(1,UUID.randomUUID().toString());//id
				stmt.setTimestamp(2,this.getUTC0());//rectime
			
				stmt.setTimestamp(3,Timestamp.valueOf(record.time));//time
				stmt.setString(4,record.cmpnm);//cmpnm
				stmt.setString(5,record.hwid);//hwid
				stmt.setString(6,record.os);//OS
				stmt.setString(7,record.osact);//osact
				stmt.setString(8,record.cssact);//cssact
				stmt.setString(9,record.clntipv4);//clntip
				stmt.setString(10,record.srvipv4);//srvip
				stmt.setString(11,record.sbxver);//sbxver
				stmt.setString(12,record.op);//op
				stmt.setString(13,record.fpth);//fpath
				stmt.setBigDecimal(14,new BigDecimal(record.fsz));//fsz
				stmt.setString(15,record.rslt);//rslt
				stmt.setString(16,"dummy");//inst_id
			
			
				stmt.executeUpdate();
			
			
			
			}catch(Throwable t){}
		}
		reader.endArray();
	}
	reader.endObject();
	reader.close();

}



void opt_logging(Connection con,BufferedReader bdr)throws SQLException,IOException {

	PreparedStatement stmt=con.prepareStatement("insert into opt_log(id,rectime,time,cmpnm,hwid,\"OS\",osact,cssact,clntipv4,srvipv4,sbxver,op,fpth,fsz,rslt,inst_id)  values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");


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
			stmt.setString(16,"dummy");//inst_id  
			
			
			
			stmt.executeUpdate();
			
			
			
		}catch(Throwable t){}
	}
	
	
	
}


Timestamp getUTC0(){
	Calendar c=Calendar.getInstance();
	return new Timestamp(c.getTimeInMillis()-c.getTimeZone().getRawOffset());
}


DataSource getDataSource() throws NamingException{
	return DBTool.getWalrusDataSource();
	
}






%>