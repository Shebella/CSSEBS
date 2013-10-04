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
import rest.representation.queryeventResponse;
import rest.representation.syncRequestResponse;
import rest.representation.unLockResponse;




@RunWith(Parameterized.class)
public class unittest_QueryeventV extends BasicUnitTestClass{
	private String account;


	public unittest_QueryeventV(String account){
		this.account=account;
		
	}
	
	
	
	
	   @Parameterized.Parameters
	   public static Collection parameters() {
			BufferedReader brf=null;
			List<String[]> l=new LinkedList<String[]>();	
					
			try {
				brf=new BufferedReader(new InputStreamReader(unittest_Existingevents.class.getClassLoader().getResourceAsStream("unittest_QueryeventV.txt")));
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
	public void testQueryEventBeforeInsertEvent() throws EBSException, SQLException, NamingException{
		String instanceid=ek.genInstanceKey(account,"test","test1");
		
		syncRequestResponse rSync=ebs.syncrequest(account, instanceid);
		assertTrue(rSync!=null&&rSync.syncRequestResult);
		unLockResponse rulock=ebs.unlock(account,instanceid,"SUCCESS");
		assertTrue(rulock!=null&&rulock.unlockresult);
		
		
		
		syncRequestResponse rSync2=ebs.syncrequest(account, instanceid);
		assertTrue(rSync2!=null&&rSync2.syncRequestResult);
		
		
		
		queryeventResponse rQuerye=ebs.queryeventV(account, instanceid,new Long(rSync.syncId).toString(),new Long(rSync2.syncId).toString());
		assertTrue(rQuerye!=null);
		assertTrue(rQuerye.result);
		assertTrue(rQuerye.size==0&&rQuerye.errors.size()==0);
		
	}
	
	
	
	
	@Test
	public void testQueryEventAfertInert1Event() throws EBSException, SQLException, NamingException{	
		String instanceid=ek.genInstanceKey(account,"test","test1"),
		       instanceid2=ek.genInstanceKey(account,"test","test2");
		
		//instance 1 get syncid and unlcok 
		syncRequestResponse rSync=ebs.syncrequest(account, instanceid);
		assertTrue(rSync!=null&&rSync.syncRequestResult);
		unLockResponse rulock=ebs.unlock(account,instanceid,"SUCCESS");
		assertTrue(rulock!=null);
		assertTrue(rulock.unlockresult);
		
		//instance 2 get syncid ,insert 2 event and unlcok 
		syncRequestResponse rSync2=ebs.syncrequest(account, instanceid2);
		assertTrue(rSync2!=null);
		assertTrue(rSync2.syncRequestResult);
		
		
		
		this.addEvent(account,(int)rSync2.syncId);
		
		unLockResponse rulock2=ebs.unlock(account,instanceid2,"SUCCESS");
		assertTrue(rulock2!=null);
		assertTrue(rulock2.unlockresult);
		
		
		
		//instance 1 get syncid once again.
		syncRequestResponse rSync3=ebs.syncrequest(account, instanceid);
		assertTrue(rSync3!=null);
		assertTrue(rSync3.syncRequestResult);
		
		
		
		queryeventResponse rQuerye=ebs.queryeventV(account, instanceid,new Long(rSync.syncId).toString(),new Long(rSync3.syncId).toString());
		assertTrue(rQuerye!=null);
		assertTrue(rQuerye.result);
		assertTrue(rQuerye.size==1);
		assertTrue(rQuerye.errors.size()==0);
	}
	
	
	
	@Test
	public void testQueryEventAfertInert100Event() throws EBSException, SQLException, NamingException{	
		String instanceid=ek.genInstanceKey(account,"test","test1"),
	           instanceid2=ek.genInstanceKey(account,"test","test2");
		
		//instance 1 get syncid and unlcok 
		syncRequestResponse rSync=ebs.syncrequest(account, instanceid);
		assertTrue(rSync!=null&&rSync.syncRequestResult);
		unLockResponse rulock=ebs.unlock(account,instanceid,"SUCCESS");
		assertTrue(rulock!=null);
		assertTrue(rulock.unlockresult);
		
		//instance 2 get syncid ,insert 2 event and unlcok 
		syncRequestResponse rSync2=ebs.syncrequest(account, instanceid2);
		assertTrue(rSync2!=null);
		assertTrue(rSync2.syncRequestResult);
		

			this.addEvent(account,(int)rSync2.syncId,100);
		
		unLockResponse rulock2=ebs.unlock(account,instanceid2,"SUCCESS");
		assertTrue(rulock2!=null);
		assertTrue(rulock2.unlockresult);
		
		
		
		//instance 1 get syncid once again.
		syncRequestResponse rSync3=ebs.syncrequest(account, instanceid);
		assertTrue(rSync3!=null);
		assertTrue(rSync3.syncRequestResult);
		
		
		
		queryeventResponse rQuerye=ebs.queryeventV(account, instanceid,new Long(rSync.syncId).toString(),new Long(rSync3.syncId).toString());
		assertTrue(rQuerye!=null);
		assertTrue(rQuerye.result);
		assertTrue(rQuerye.size==100);
		assertTrue(rQuerye.errors.size()==0);
	}
	
	
	
	
	
	
	@Test
	public void testInsertMoreThen10000evevt() throws EBSException, SQLException, NamingException{	
		String instanceid=ek.genInstanceKey(account,"test","test1"),
	           instanceid2=ek.genInstanceKey(account,"test","test2");
		
		//instance 1 get syncid and unlcok 
		syncRequestResponse rSync=ebs.syncrequest(account, instanceid);
		assertTrue(rSync!=null&&rSync.syncRequestResult);
		unLockResponse rulock=ebs.unlock(account,instanceid,"SUCCESS");
		assertTrue(rulock!=null);
		assertTrue(rulock.unlockresult);
		
		//instance 2 get syncid ,insert 2 event and unlcok 
		syncRequestResponse rSync2=ebs.syncrequest(account, instanceid2);
		assertTrue(rSync2!=null);
		assertTrue(rSync2.syncRequestResult);
		
		
			this.addEvent(account,(int)rSync2.syncId,10010);
		
		unLockResponse rulock2=ebs.unlock(account,instanceid2,"SUCCESS");
		assertTrue(rulock2!=null);
		assertTrue(rulock2.unlockresult);
		
		
		
		//instance 1 get syncid once again.
		syncRequestResponse rSync3=ebs.syncrequest(account, instanceid);
		assertTrue(rSync3!=null);
		assertTrue(rSync3.syncRequestResult);
		
		
		
		queryeventResponse rQuerye=ebs.queryeventV(account, instanceid,new Long(rSync.syncId).toString(),new Long(rSync3.syncId).toString());
		assertTrue(rQuerye!=null);
		assertTrue(!rQuerye.result);
		assertTrue(rQuerye.errors.size()==1);
		assertTrue("0201".equals(rQuerye.errors.get(0).code));
	}
	
	
	@Test
	public void testQueryEventBeforeGetlock() throws EBSException, SQLException, NamingException{
		String instanceid=ek.genInstanceKey(account,"test","test1");
		
		syncRequestResponse rSync=ebs.syncrequest(account, instanceid);
		assertTrue(rSync!=null&&rSync.syncRequestResult);
		unLockResponse rulock=ebs.unlock(account,instanceid,"SUCCESS");
		assertTrue(rulock!=null&&rulock.unlockresult);
		
		queryeventResponse rQuerye=ebs.queryeventV(account, instanceid,new Long(rSync.syncId).toString(),new Long(rSync.syncId+1).toString());
		assertTrue(rQuerye!=null);
		
		assertTrue(!rQuerye.result);
		assertTrue(rQuerye.size==0);
		assertTrue(rQuerye.errors.size()==1);
		assertTrue("0202".equals(rQuerye.errors.get(0).code));
		
	}
	
	
	
	@Test
	public void testQueryEventAfertInert100EventWithOutSyncid() throws EBSException, SQLException, NamingException{	
		String instanceid=ek.genInstanceKey(account,"test","test1"),
	           instanceid2=ek.genInstanceKey(account,"test","test2");
		
		//instance 1 get syncid and unlcok 
		syncRequestResponse rSync=ebs.syncrequest(account, instanceid);
		assertTrue(rSync!=null&&rSync.syncRequestResult);
		unLockResponse rulock=ebs.unlock(account,instanceid,"SUCCESS");
		assertTrue(rulock!=null);
		assertTrue(rulock.unlockresult);
		
		//instance 2 get syncid ,insert 2 event and unlcok 
		syncRequestResponse rSync2=ebs.syncrequest(account, instanceid2);
		assertTrue(rSync2!=null);
		assertTrue(rSync2.syncRequestResult);
		

			this.addEvent(account,(int)rSync2.syncId,100);
		
		unLockResponse rulock2=ebs.unlock(account,instanceid2,"SUCCESS");
		assertTrue(rulock2!=null);
		assertTrue(rulock2.unlockresult);
		
		
		
		//instance 1 get syncid once again.
		syncRequestResponse rSync3=ebs.syncrequest(account, instanceid);
		assertTrue(rSync3!=null);
		assertTrue(rSync3.syncRequestResult);
		
		
		
		queryeventResponse rQuerye=ebs.queryeventV(account, instanceid,null,null);
		assertTrue(rQuerye!=null);
		assertTrue(!rQuerye.result);
		assertTrue(rQuerye.errors.size()==1);
		assertTrue("0009".equals(rQuerye.errors.get(0).code));
	}
	
	
	
	
	@Test
	public void testQueryEventAfertInert100EventWithOutSyncid2() throws EBSException, SQLException, NamingException{	
		String instanceid=ek.genInstanceKey(account,"test","test1"),
	           instanceid2=ek.genInstanceKey(account,"test","test2");
		
		//instance 1 get syncid and unlcok 
		syncRequestResponse rSync=ebs.syncrequest(account, instanceid);
		assertTrue(rSync!=null&&rSync.syncRequestResult);
		unLockResponse rulock=ebs.unlock(account,instanceid,"SUCCESS");
		assertTrue(rulock!=null);
		assertTrue(rulock.unlockresult);
		
		//instance 2 get syncid ,insert 2 event and unlcok 
		syncRequestResponse rSync2=ebs.syncrequest(account, instanceid2);
		assertTrue(rSync2!=null);
		assertTrue(rSync2.syncRequestResult);
		

			this.addEvent(account,(int)rSync2.syncId,100);
		
		unLockResponse rulock2=ebs.unlock(account,instanceid2,"SUCCESS");
		assertTrue(rulock2!=null);
		assertTrue(rulock2.unlockresult);
		
		
		
		//instance 1 get syncid once again.
		syncRequestResponse rSync3=ebs.syncrequest(account, instanceid);
		assertTrue(rSync3!=null);
		assertTrue(rSync3.syncRequestResult);
		
		
		
		queryeventResponse rQuerye=ebs.queryeventV(account,instanceid,new Long(rSync.syncId).toString(),null);
		assertTrue(rQuerye!=null);
		assertTrue(rQuerye.result);
		assertTrue(rQuerye.size==100);
		assertTrue(rQuerye.errors.size()==0);
	}
	

	@Test
	public void testQueryEventAfertInert100EventWithOutSyncid3() throws EBSException, SQLException, NamingException{	
		String instanceid=ek.genInstanceKey(account,"test","test1"),
	           instanceid2=ek.genInstanceKey(account,"test","test2");
		
		//instance 1 get syncid and unlcok 
		syncRequestResponse rSync=ebs.syncrequest(account, instanceid);
		assertTrue(rSync!=null&&rSync.syncRequestResult);
		unLockResponse rulock=ebs.unlock(account,instanceid,"SUCCESS");
		assertTrue(rulock!=null);
		assertTrue(rulock.unlockresult);
		
		//instance 2 get syncid ,insert 2 event and unlcok 
		syncRequestResponse rSync2=ebs.syncrequest(account, instanceid2);
		assertTrue(rSync2!=null);
		assertTrue(rSync2.syncRequestResult);
		

			this.addEvent(account,(int)rSync2.syncId,100);
		
		unLockResponse rulock2=ebs.unlock(account,instanceid2,"SUCCESS");
		assertTrue(rulock2!=null);
		assertTrue(rulock2.unlockresult);
		
		
		
		//instance 1 get syncid once again.
		syncRequestResponse rSync3=ebs.syncrequest(account, instanceid);
		assertTrue(rSync3!=null);
		assertTrue(rSync3.syncRequestResult);
		
		
		
		queryeventResponse rQuerye=ebs.queryeventV(account,instanceid,null,new Long(rSync3.syncId).toString());
		assertTrue(rQuerye!=null);
		assertTrue(rQuerye.result);
		assertTrue(rQuerye.size==100);
		assertTrue(rQuerye.errors.size()==0);
	}
	
	
}
