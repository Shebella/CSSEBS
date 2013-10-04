package lib.unittest;

import static org.junit.Assert.assertTrue;

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
import rest.representation.syncRequestResponse;
import rest.representation.unLockResponse;




@RunWith(Parameterized.class)
public class unittest_unlockV  extends BasicUnitTestClass{


	private String account;

	public unittest_unlockV(String account){
		this.account=account;
		
	}

	
	
	   @Parameterized.Parameters
	   public static Collection parameters() {
			BufferedReader brf=null;
			List<String[]> l=new LinkedList<String[]>();	
					
			try {
				brf=new BufferedReader(new InputStreamReader(unittest_Existingevents.class.getClassLoader().getResourceAsStream("unittest_UnlockV.txt")));
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
	         { "User2"}
	      });
	   }*/
	
	@Test
	public void TestUnlockAfterLock() throws SQLException, NamingException, EBSException{


		 String instanceid1=ek.genInstanceKey(account,"test","test1");

		 
		 //getlock 
		 syncRequestResponse r1=this.ebs.syncrequestV(account,instanceid1);
		 assertTrue(r1!=null&&r1.syncRequestResult);
		 
		 
		 
		 //unlock
		 unLockResponse ur=this.ebs.unlockV(account, instanceid1,"SUCCESS"); 
		 assertTrue(ur!=null&&ur.unlockresult);
		 
		 
	}
	
	
	@Test
	public void TestAfterLockWithFAIL() throws SQLException, NamingException, EBSException{


		 String instanceid1=ek.genInstanceKey(account,"test","test1"),
				instanceid2=ek.genInstanceKey(account,"test","test2");

		 
		 //getlock 
		 syncRequestResponse r1=this.ebs.syncrequestV(account,instanceid1);
		 assertTrue(r1!=null&&r1.syncRequestResult);
		 
		 
		 
		 //unlock
		 unLockResponse ur=this.ebs.unlockV(account, instanceid1,"FAIL"); 
		 assertTrue(ur!=null&&ur.unlockresult);
		
		
		
	}
	
	@Test
	public void TestUnlockBefergetLock() throws SQLException, NamingException, EBSException{


		 String instanceid1=ek.genInstanceKey(account,"test","test1"),
				instanceid2=ek.genInstanceKey(account,"test","test2");
		 
		 
		 
		 //unlock
		 unLockResponse ur=this.ebs.unlockV(account, instanceid1,"SUCCESS"); 
		 assertTrue(ur!=null&&!ur.unlockresult);
		
		
		
	}
	
	
	
	@Test
	public void TestUnlockedwithSUCCStateBefore() throws SQLException, NamingException, EBSException{
		 String instanceid1=ek.genInstanceKey(account,"test","test1");
		 
		 //getlock 
		 syncRequestResponse r1=this.ebs.syncrequestV(account,instanceid1);
		 assertTrue(r1!=null&&r1.syncRequestResult);
		 
		 
		 //unlock with SUCC
		 unLockResponse ur=this.ebs.unlockV(account, instanceid1,"SUCCESS");
		 assertTrue(ur!=null&&ur.unlockresult);
		 
		 
		 //unlock
		 unLockResponse ur2=this.ebs.unlockV(account, instanceid1,"SUCCESS");
		 assertTrue(ur2!=null&&!ur2.unlockresult&&ur2.errors.size()==1);
		 assertTrue("0302".equals(ur2.errors.get(0).code));
		
	}
	
	
	
	@Test
	public void TestUnlockedwithFAILStateBefore() throws SQLException, NamingException, EBSException{
		 String instanceid1=ek.genInstanceKey(account,"test","test1");
		 
		 //getlock 
		 syncRequestResponse r1=this.ebs.syncrequestV(account,instanceid1);
		 assertTrue(r1!=null&&r1.syncRequestResult);
		 
		 //unlock with FAIL
		 unLockResponse ur=this.ebs.unlockV(account, instanceid1,"FAIL");
		 assertTrue(ur!=null&&ur.unlockresult);
		 
		 
		 //unlock
		 unLockResponse ur2=this.ebs.unlockV(account, instanceid1,"SUCCESS");
		 assertTrue(ur2!=null&&!ur2.unlockresult&&ur2.errors.size()==1);
		 assertTrue("0303".equals(ur2.errors.get(0).code));
		
	}
	
	@Test
	public void TestUnlockedwithSYNCUPDStateBefore() throws SQLException, NamingException, EBSException{
		 String instanceid1=ek.genInstanceKey(account,"test","test1");
		 
		 //getlock 
		 syncRequestResponse r1=this.ebs.syncrequestV(account,instanceid1);
		 assertTrue(r1!=null&&r1.syncRequestResult);
		 
		 //ADD EVENT 
		 this.addEvent(account,(int)r1.syncId);
		 
		 
		 //unlock with FAIL
		 unLockResponse ur=this.ebs.unlock(account, instanceid1,"FAIL");
		 assertTrue(ur!=null&&ur.unlockresult);
		 
		 
		 //unlock
		 unLockResponse ur2=this.ebs.unlockV(account, instanceid1,"SUCCESS");
		 assertTrue(ur2!=null&&!ur2.unlockresult&&ur2.errors.size()==1);
		 assertTrue("0304".equals(ur2.errors.get(0).code));
		
	}
	
	
	
	@Test
	public void TestUnlockBeforeLock() throws SQLException, NamingException, EBSException{


		 String instanceid1=ek.genInstanceKey(account,"test","test1");
		 
		 
		 
		 //unlock
		 unLockResponse ur=this.ebs.unlockV(account, instanceid1,"SUCCESS");
		 assertTrue(ur!=null&&!ur.unlockresult&&ur.errors.size()==1);
		 assertTrue("0301".equals(ur.errors.get(0).code));		
	}

	
	
	
	
	
}
