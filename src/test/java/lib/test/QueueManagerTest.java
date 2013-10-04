package lib.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import lib.Config;
import lib.QueueManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import rest.Initaitor;

public class QueueManagerTest {
	@Before
	public void pre() throws JAXBException, IOException{
		String ebsProPath="/opt/apache-tomcat/webapps/sbx_svr/WEB-INF/classes/ebs.properties";

		Config.ebsPorperties=Initaitor.loadEbsConfig(ebsProPath);
		Config.EUCA_IP="127.0.0.1";
		Config.DB_IP="127.0.0.1";
		Config.LDAP="itri.ds";
		Config.ldapUrl="ce.getServletContext().getInitParameter(\"ldapUrl\")";
		Config.ldapAccount="CN=%user%,CN=Users,DC=ITRI,DC=DS";
	}
	
	
	@Test
	public void Test_getQueueAcount() throws SQLException, NamingException{
		QueueManager qm=new QueueManager(UniTestConfig.tester);
		String account=qm.getQueueAccount();
		
		assertTrue(qm!=null&&account.equals(UniTestConfig.tester));
	}
	
	@Test
	public void Test_getsyncid_confirmsync() throws SQLException, NamingException{
		QueueManager qm=new QueueManager(UniTestConfig.tester);
		String flag =qm.confirmsync(qm.getsyncid());
		if(flag==null){assertFalse(true);}
	}
	
	
	@Test
	public void heartbeat() throws SQLException, NamingException{
		QueueManager qm=new QueueManager(UniTestConfig.tester);
		qm.heartbeat(UniTestConfig.instance);
	}
	
	
	
	
	@Test
	public void Test_Lock_unLock() throws Throwable{
		QueueManager qm=new QueueManager(UniTestConfig.tester);
		
		qm.getLock(UniTestConfig.instance);
		if(!qm.isLocked(UniTestConfig.instance)){assertFalse(true);}
		
		qm.unlock(UniTestConfig.instance,"TEST");
		if(qm.isLocked(UniTestConfig.instance)){assertFalse(true);}
	}
	
	
	@After
	public void post(){
		
		
		
	}
}
