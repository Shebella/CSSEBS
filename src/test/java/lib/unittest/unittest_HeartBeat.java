package lib.unittest;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.naming.NamingException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import rest.exception.EBSException;
import rest.representation.HeartbeatResponse;
import rest.representation.syncRequestResponse;




@RunWith(Parameterized.class)
public class unittest_HeartBeat extends BasicUnitTestClass{
	private String account="User1";
	
	public unittest_HeartBeat(String account){
		this.account=account;
	}

	
	   @Parameterized.Parameters
	   public static Collection parameters() {
			BufferedReader brf=null;
			List<String[]> l=new LinkedList<String[]>();	
					
			try {
				brf=new BufferedReader(new InputStreamReader(unittest_Existingevents.class.getClassLoader().getResourceAsStream("unittest_HeartBeat.txt")));
				String str=null;
				while((str=brf.readLine())!=null){
					l.add(str.split(" "));
				}
				
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				try {
					if(brf!=null)brf.close();
				} catch (IOException e) {}
				
				
			}
			
			return l;
		   
		   
		   
	   }
		
/*	   @Parameterized.Parameters
	   public static Collection parameters() {
	      return Arrays.asList(new Object[][] {
	         { "User1"},
	         { "User2"},
	         { "User3"}
	      });
	   }*/
		

	
	@Test
	public void testHeartbeatAfterLock() throws EBSException, SQLException, NamingException{
		String instanceid=ek.genInstanceKey(account,"test","test");;

		//get lock
		syncRequestResponse r=ebs.syncrequest(account, instanceid);
		assertTrue(r!=null&&r.syncRequestResult&&r.errors.size()==0);
		
		
		//heartbeat
		HeartbeatResponse r2=ebs.heartbeat(account, instanceid); 
		assertTrue(r2!=null&&"success".equals(r2.heartbeatResult));
	}
	

	@Test
	public void testHeartbeatBeforeLock() throws EBSException, SQLException, NamingException{
		String instanceId=ek.genInstanceKey(account,"test","test");;
		
		//heartbeat
		HeartbeatResponse r=ebs.heartbeat(account, instanceId); 
		assertTrue(r!=null&&"fail".equals(r.heartbeatResult));
	}
	
	
}
