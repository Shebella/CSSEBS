package lib.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import lib.Config;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import rest.EBSAPI;
import rest.Initaitor;
import rest.exception.EBSException;
import rest.representation.*;

public class EBSAPITest {
	static EBSAPI ebs=null;  
	static String account,instanceid;
	static Integer syncid,lastsyncid=2;
	

	@BeforeClass
	public static  void pre() throws Throwable{
		String ebsProPath="/opt/apache-tomcat/webapps/sbx_svr/WEB-INF/classes/ebs.properties";
		
		Config.ebsPorperties=Initaitor.loadEbsConfig(ebsProPath); 
		Config.EUCA_IP="127.0.0.1";
		Config.DB_IP="127.0.0.1";
		Config.LDAP="itri.ds";
		Config.ldapUrl="ce.getServletContext().getInitParameter(\"ldapUrl\")";
		Config.ldapAccount="CN=%user%,CN=Users,DC=ITRI,DC=DS";
		
		
		account=UniTestConfig.tester;
		instanceid=UniTestConfig.instance;
		lastsyncid=1;
		ebs=new EBSAPI();
	}
	
	
	@AfterClass
	public static void post() throws Throwable{
		
		
		
	}
	
	private static boolean getSyncId() throws EBSException{

		syncRequestResponse r2=ebs.syncrequest(account, instanceid);
		if(r2.syncRequestResult){
			syncid=(int) r2.syncId;
		}
		return r2.syncRequestResult;
	}
	
	private static boolean unlock() throws EBSException{
		unLockResponse ulp=ebs.unlock(account, instanceid,"SUCCESS");
		return ulp.unlockresult;	
	}
	
	
	
	@Test
	public void Test_existingevents() throws Throwable{
		assertTrue(getSyncId());
		
		existingeventsResponse r1=ebs.existingevents(account,instanceid,lastsyncid);
		if(r1==null){assertFalse(true);}
		
		 assertTrue(unlock());
	}
	
	@Test
	public void Test_getLastSuccessSyncId() throws Throwable{
		assertTrue(getSyncId());
		
		ebs.querylastsyncid(account, instanceid);
		
		 assertTrue(unlock());
	}

	
	@Test
	public void Test_syncRequest() throws Throwable{
		assertTrue(getSyncId());
		
		
		assertTrue(unlock());
	}
	
	
	@Test
	public void Test_queryevents() throws Throwable{
		queryeventResponse r3= ebs.queryevent(account,instanceid,new Long(syncid).toString(),lastsyncid.toString(),null);
		assertTrue(r3!=null);
		
		
		
		
	}
	
	@Test
	public void Test_confirmsync() throws Throwable{
		confirmsyncResponse r3= ebs.confirmsync(account, instanceid,new Long(syncid).toString());
		assertTrue(r3!=null);
	}
	

	
	@Test
	public void Test_heartbeat() throws Throwable{
		HeartbeatResponse r1=ebs.heartbeat(account,instanceid);
		assertTrue(r1!=null);
	}
	
	
	@Test
	public void Test_lastsyncid() throws Throwable{
		querylastsyncidResponse r1=ebs.querylastsyncid(account,instanceid);
		assertTrue(r1!=null);
		
		
	}
	
	boolean unlock_flag=false;
	@Test
	public void Test_unlock() throws Throwable{
		assertTrue(getSyncId());
		
		
		assertTrue(unlock());
	}
	
	
	

	
	

}
