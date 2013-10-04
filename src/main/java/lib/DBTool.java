package lib;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.jets3t.service.Jets3tProperties;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.security.AWSCredentials;
import org.postgresql.jdbc3.Jdbc3PoolingDataSource;
import org.jets3t.service.Jets3tProperties;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.security.AWSCredentials;




public class DBTool {
	

	public static Logger getLogger(String logName) throws IOException{

		PatternLayout p=new PatternLayout();
			p.setConversionPattern("%d{yyMMdd HH:mms,SSS} %-5p (%F:%L) %m%n");
		RollingFileAppender a=null;
			a=new RollingFileAppender(p,System.getProperty("catalina.home")+"/logs/EBS_"+logName+".log");
			a.setMaxBackupIndex(100);
			
		Logger i=Logger.getLogger(logName);
			i.removeAllAppenders();
			i.addAppender(a);
		return i;
	}

	
	
	public static ResultSet query(Connection con,String preStat,String[] param)throws SQLException,Throwable{
		PreparedStatement stmt=con.prepareStatement(preStat);
		
		
		for(int i=0;i<param.length;i++){
			if(param[i]==null)throw new Throwable("some argument is null.");
			stmt.setString(i+1,param[i]);
		}

		ResultSet rs=stmt.executeQuery();


		return rs;
	}

	public static boolean isRunAway(Connection con,String account,int lastsyncid) throws SQLException{
		String query="select syncid from syncinfo where extract(day from (now() -createtime))<=? and account =? and syncid=?";
		PreparedStatement stmt=con.prepareStatement(query);
		stmt.setInt(1,Integer.valueOf(Config.ebsPorperties.getProperty("RUNAWAYDAYS")));
		stmt.setString(2,account);
		stmt.setInt(3,lastsyncid);
		ResultSet rs=stmt.executeQuery();
		if(!rs.next()) {
			return false;
		}		
		return true;
	}

	public static Integer getLastSuccessSyncId(Connection con,String account,String instancekey) throws SQLException{
		String query="select syncid from syncinfo where extract(day from (now() -createtime))<=? and account =? and instancekey=? and status='SUCCESS' order by syncid desc limit 1";
		PreparedStatement stmt=con.prepareStatement(query);
		stmt.setInt(1,Integer.valueOf(Config.ebsPorperties.getProperty("RUNAWAYDAYS")));
		stmt.setString(2,account);
		stmt.setString(3,instancekey);
		ResultSet rs=stmt.executeQuery();
		if(!rs.next()) {
			return null;
		}		
		return rs.getInt(1);
	}

	public static Integer getLastSuccessSyncId(Connection con,String account) throws SQLException{
		String query="select syncid from syncinfo where extract(day from (now() -createtime))<=? and account =? and status='SUCCESS' order by syncid desc limit 1";
		PreparedStatement stmt=con.prepareStatement(query);
		stmt.setInt(1,Integer.valueOf(Config.ebsPorperties.getProperty("RUNAWAYDAYS")));
		stmt.setString(2,account);
		ResultSet rs=stmt.executeQuery();
		if(!rs.next()) {
			return null;
		}		
		return rs.getInt(1);
	}
	
	public static void closeConnection(Connection con) throws SQLException{
		try {
			if(con != null){
				if(!con.getAutoCommit()){con.commit();}
				con.close();
			}
		} catch (SQLException e) {
			throw e;
		}
	}
	
	
	public static Connection getConnection() throws SQLException, NamingException{
			//return DBTool.getDataSource().getConnection();
		Connection con=DBTool.getWalrusDataSource().getConnection();
		con.setAutoCommit(false);
		return con;
	}
	public static Connection getWalrusConnection() throws SQLException, NamingException{
			Connection con=DBTool.getWalrusDataSource().getConnection();
			con.setAutoCommit(false);
			return con;
	}
	
	
	 static  	 	DataSource ds=null;
	public static DataSource getWalrusDataSource() throws NamingException{
		String getDBStr="hippo_db";
		
		
   	 	Context ctx = new InitialContext();//could be singleton if it's thread-safe.

   	 	try{
   	 		
   	 		
   	 		
   	 		//ds = (DataSource)ctx.lookup(getDBStr);
   	 		if(ds==null)throw new NamingException();
   	 	}catch(NamingException nie){
   	 		Jdbc3PoolingDataSource source=new Jdbc3PoolingDataSource();
   	 		source.setDataSourceName(getDBStr);
   	 		source.setServerName(Config.DB_IP);
   	 		source.setDatabaseName("hippo");
   	 		source.setUser("postgres");
   	 		source.setPassword("");
   	 		source.setMaxConnections(Integer.valueOf(Config.ebsPorperties.getProperty("dbMaxCon")));
			source.setApplicationName("EBS_hippo");
			//ctx.bind(getDBStr,source);
			ds=(DataSource)source;
   	 	}
   	 	
   	 	
   	 	return ds;
	}
	
/*
	public static DataSource getDataSource() throws NamingException{
		String getDBStr="logging_db";
		
		
		Context ctx = new InitialContext();//could be singleton if it's thread-safe.
		DataSource ds=null;
		try{
			ds = (DataSource)ctx.lookup(getDBStr);
			
		}catch(NamingException nie){
			Jdbc3PoolingDataSource source=new Jdbc3PoolingDataSource();
			source.setDataSourceName(getDBStr);
			source.setServerName(Config.DB_IP);
			source.setDatabaseName("safebox_logging");
			source.setUser("postgres");
			source.setPassword("");
			source.setMaxConnections(Config.ebsconfig.getDbMaxCon());
			source.setApplicationName("EBS_logging");		
			ctx.bind(getDBStr,source);
			ds=(DataSource)source;
			
		}
		
		
		return ds;
		
	}*/
	private static String MainLogger="MainLog";
	public static boolean recordMainLog(String msg){
		return DBTool.recordLog(DBTool.MainLogger,msg);
	}
	
	private static String FakeResponseLogger="FakeResponseLog";
	public static boolean recordFakeResponseLog(String msg){
		return DBTool.recordLog(DBTool.FakeResponseLogger,msg);
	}
	
	
	
	public static boolean recordLog(String filename,String msg){
		try {
			DBTool.getLogger(filename).debug(msg);
		} catch (IOException e) {
			return false; 
		}
		
		return true;
	}

	public static KeyPair getKeyPair(String account) throws SQLException, NamingException{
		String ak=null,sk=null;
		Connection con=null;
		
		try{
			con=DBTool.getWalrusConnection();
			
			String query="select auth_user_query_id,auth_user_secretkey from auth_users where auth_user_name=?";
			PreparedStatement stmt=con.prepareStatement(query);
			stmt.setString(1,account);
			ResultSet rs=stmt.executeQuery();
			if(rs.next()) {
				ak=rs.getString(1);sk=rs.getString(2);
			}		
		
		}finally{
			if(con!=null)con.close();
			
		}
		

		try {
			DBTool.getLogger("api_error").info("DBTool.getKeyPair(),ak="+ak+",sk="+sk);
		} catch (IOException e) {}
		
		
		return new KeyPair(ak,sk);
	}
	public static CSSRestService getCSSRestService(String account) throws S3ServiceException, SQLException, NamingException{
		CSSRestService s3service=null;
		
		
		KeyPair kp=DBTool.getKeyPair(account);
		AWSCredentials awsCredentials = new AWSCredentials(kp.accessKey,kp.secretKey);
		Jets3tProperties jets3tProperties=Jets3tProperties.getInstance("jets3t.properties");
		jets3tProperties.setProperty("s3service.s3-endpoint",Config.EUCA_IP);
			
		s3service = new CSSRestService(awsCredentials,jets3tProperties);
		s3service.getHttpClient().getParams().setBooleanParameter("http.protocol.expect-continue", false);
		
		return s3service;
	}
	
	public static CSSRestService getCSSRestService() throws S3ServiceException, SQLException, NamingException{
		CSSRestService s3service=null;
		
		
		KeyPair kp=DBTool.getKeyPair("admin");
		AWSCredentials awsCredentials = new AWSCredentials(kp.accessKey,kp.secretKey);
		Jets3tProperties jets3tProperties=Jets3tProperties.getInstance("jets3t.properties");
		jets3tProperties.setProperty("s3service.s3-endpoint",Config.EUCA_IP);
			
		s3service = new CSSRestService(awsCredentials,jets3tProperties);
		s3service.getHttpClient().getParams().setBooleanParameter("http.protocol.expect-continue", false);
		
		return s3service;
	}
	
	
	public static boolean isEmpty(String str){
		return str==null||"".equals(str.trim());
	}
	
	
	public static Timestamp getUTC0(){
		Calendar c=Calendar.getInstance();
		return new Timestamp(c.getTimeInMillis()-c.getTimeZone().getRawOffset());
	}
	
	
	


	public static boolean CloseReader(Reader bin){

		try {
			if (bin != null) {bin.close();}	
		} catch (IOException e) {
			try {DBTool.getLogger("api_error").debug(e);} catch (IOException e1) {}
			return false;
		}
		
		
		return true;
	}
	
	
	public  static boolean RollBackDBConnection(Connection con){
		try {
			if(con!=null)con.rollback();
		} catch (Throwable  e1) {
			try {DBTool.getLogger("api_error").debug(e1);} catch (IOException e2) {}
			return false;
		}
		
		return true;
	}
	
	public static boolean CloseDBStatment(PreparedStatement stmt){
		try {
			if(stmt!=null)stmt.close();
		} catch (Throwable e1) {
			try {DBTool.getLogger("api_error").debug(e1);} catch (IOException e2) {}
			return false;
		}
		
		return true;
	}

	public static boolean CloseDBResultSet(ResultSet rs){
		try {
			if(rs!=null)rs.close();
		} catch (Throwable  e1) {
			try {DBTool.getLogger("api_error").debug(e1);} catch (IOException e2) {}
			return false;
		}
		
		return true;
	}

	public static boolean CloseDBConnection(Connection con){
		try {
			if(con!=null)DBTool.closeConnection(con);
		} catch (Throwable  e1) {
			try {DBTool.getLogger("api_error").debug(e1);} catch (IOException e2) {}
			
			return false;
		}
		
		return true;
	}
}
