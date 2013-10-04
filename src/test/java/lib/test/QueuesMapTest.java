package lib.test;

import static org.junit.Assert.assertTrue;

import java.sql.SQLException;

import javax.naming.NamingException;

import lib.QueueManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class QueuesMapTest {
	public String account;
	
	
	@Before
	public void pre(){
		this.account=UniTestConfig.tester;
	}
	
	
	
	@Test
	public void Test_getQueueManager() throws SQLException, NamingException{		
		QueueManager qm=new QueueManager(account);
		
		
		assertTrue(qm!=null);
	}
	

	@After
	public void post(){
	}
	
	
}
