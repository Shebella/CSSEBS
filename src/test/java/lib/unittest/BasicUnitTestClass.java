package lib.unittest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.naming.NamingException;
import javax.ws.rs.QueryParam;
import javax.xml.bind.JAXBContext;

import lib.CSSRestService;
import lib.Config;
import lib.DBTool;
import lib.test.UniTestConfig;

import org.jets3t.service.S3ServiceException;
import org.jets3t.service.ServiceException;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runners.Parameterized;

import eucakit.eucaKit;

import rest.EBSAPI;
import rest.Initaitor;
import rest.exception.EBSException;
import rest.representation.syncRequestResponse;
import rest.representation.unLockResponse;

public class BasicUnitTestClass {
	

	 	static eucaKit ek=null;
		static EBSAPI ebs=null;
		
		
		
		protected boolean addEvent(String account,int syncid ){
			return this.addEvent(account,syncid,1);
		 }

		
		protected boolean addEvent(String account,int syncid,int eventCount){
			 Connection con=null;
			 PreparedStatement stmt=null;
			 ResultSet rs=null;
			 Calendar c = Calendar.getInstance();
			 
			 
			 try {
				con=DBTool.getConnection();
				
				for(int i=0;i<eventCount;i++){

					String query = "insert into opt_log  values(DEFAULT,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
					stmt = con.prepareStatement(query);

					stmt.setTimestamp(1, new Timestamp(c.getTimeInMillis() - c.getTimeZone().getRawOffset()));// rectime
					stmt.setTimestamp(2, new Timestamp(c.getTimeInMillis() - c.getTimeZone().getRawOffset()));// time
					stmt.setString(3,"fortest");// cmpnm
					stmt.setString(4,"fortest");// hwid
					stmt.setString(5,"fortest");// OS
					stmt.setString(6,"fortest");// osact
					stmt.setString(7,account);// cssact
					stmt.setString(8,"fortest");// clnip
					stmt.setString(9, "fortest");// srvip
					stmt.setString(10,"fortest");// sbxver
					stmt.setString(11,"fortest");// op
					stmt.setString(12,"fortest");// fpath
					stmt.setBigDecimal(13, new BigDecimal(5));// fsz
					stmt.setString(14,"fortest");// file_version
					stmt.setString(15,"fortest");// is_Folder
					stmt.setInt(16, syncid);
					stmt.setString(17,"succ");// rslt
					stmt.setString(18, "dummy");// inst_id
					stmt.executeUpdate();
				}
			}catch(Throwable t){
				t.printStackTrace();
				try {con.rollback();} catch (SQLException e) {}
				return false;
				
			}finally{
				try {DBTool.closeConnection(con);} catch(SQLException e) {}
			}
			 
			return true;
		 }
		
		
		

		@BeforeClass
		public static  void pre() throws Throwable{
			String ebsProPath="/opt/apache-tomcat/webapps/sbx_svr/WEB-INF/classes/ebs.properties";

			Config.ebsPorperties=Initaitor.loadEbsConfig(ebsProPath);
			Config.EUCA_IP="127.0.0.1";
			Config.DB_IP="127.0.0.1";
			Config.LDAP="itri.ds";
			Config.ldapUrl="ldap://%ip%/CN=%user%,CN=Users,DC=ITRI,DC=DS";
			Config.ldapAccount="CN=%user%,CN=Users,DC=ITRI,DC=DS";
			if(System.getenv("EBSAPISTUB")!=null){
				ebs=new EBSAPIStub();
			}else{
				ebs=new EBSAPI();
			}
			ek=new eucaKit("127.0.0.1","127.0.0.1");
			 
			
		}
		
		@AfterClass
		public static void post() throws Throwable{
		}
		
		@Before
		public void beforeeachTest(){
			
		}
		
		
		@After
		public void aftereachTest() throws Throwable{
			this.ebs.cleanUpQueueMap();
			this.truncateAllEBSTable();
			this.resetAllSequence();
		}
		
		
		
		
		
		protected void truncateAllEBSTable() throws Throwable{
			 Connection con=null;
			 PreparedStatement stmt=null;
			 ResultSet rs=null;
			 Calendar c = Calendar.getInstance();
			 
			 
			 try {
				con=DBTool.getConnection();
				
				String query = "truncate table opt_log,syncinfo,client_instances;";
				stmt = con.prepareStatement(query);
				stmt.executeUpdate();
				
				
			}catch(Throwable t){
				try {con.rollback();} catch (SQLException e) {}
				throw t;
			}finally{
				try {DBTool.closeConnection(con);} catch(SQLException e) {}
			}
			 
		}
		
		
		protected void resetAllSequence(){

			 Connection con=null;
			 PreparedStatement stmt=null;
			 ResultSet rs=null;
			 Calendar c = Calendar.getInstance();
			 
			 
			 try {
				con=DBTool.getConnection();
				


				String query = "ALTER SEQUENCE syncinfo_syncid_seq RESTART WITH 1;"+
							   "ALTER SEQUENCE syncinfo_syncid_seq RESTART WITH 1;"+ 
						       "ALTER SEQUENCE client_instances_instance_id_seq RESTART WITH 1;";
				stmt = con.prepareStatement(query);
				stmt.executeUpdate();
				
				
			}catch(Throwable t){
				t.printStackTrace();
				try {con.rollback();} catch (SQLException e) {}
				
			}finally{
				try {DBTool.closeConnection(con);} catch(SQLException e) {}
			}
			
		}
		
		protected void createFolder(String account,String bucket_name,String object_name) throws Throwable{

			S3Bucket buk=new S3Bucket(bucket_name);
			S3Object obj=new S3Object(object_name);
			obj.setContentType("application/x-directory");
			S3Object robj=DBTool.getCSSRestService(account).putObject(buk,obj);
			InputStream inp=robj.getDataInputStream();
			if(inp!=null){inp.close();}
		}
		
		
		
		
		protected void createObject(String account,String bucket_name,String object_name) throws Throwable{

			S3Bucket buk=new S3Bucket(bucket_name);
			S3Object obj=new S3Object(object_name);
			S3Object robj=DBTool.getCSSRestService(account).putObject(buk,obj);
			InputStream inp=robj.getDataInputStream();
			if(inp!=null){inp.close();}
		}
		
		
		protected void createBucket(String account,String bucket_name) throws Throwable{
			S3Bucket buk=new S3Bucket(bucket_name);
			DBTool.getCSSRestService(account).createBucket(buk);
		}
		
		
		
		protected String cleanAllBecket(@QueryParam("account") String account){
			try{
				boolean delFlag=true;
				CSSRestService cssService=DBTool.getCSSRestService(account);
				if(cssService==null){return "can not init RestS3Service.";}
				
				
				S3Bucket[] bucket=cssService.listAllBuckets();
				
				for(int i=0;i<bucket.length;i++){
					this.clearAllObjectInBucket(cssService,bucket[i].getName());
					try{
						cssService.deleteBucket(bucket[i].getName());
					}catch(Throwable t){
						delFlag=false;
					}
				}
				
				if(delFlag){
					return "success";
				}else{
					return "some bucket has not been deleted.";
				}
			}catch(Throwable t){
				return "error:"+t.getMessage();
			}
		}
		

		
		
		protected boolean clearAllObjectInBucket(CSSRestService cssService,String bucket) throws S3ServiceException{
			boolean r=true;
			
			S3Object[] s3obj=cssService.listObjects(bucket);
			for(int i=0;i<s3obj.length;i++){
				try {
					cssService.deleteObject(bucket,s3obj[i].getName());
				} catch (ServiceException e) {
					e.printStackTrace();
					r=false;
				}
			}		
			return r;
		}
		
		
		

}
