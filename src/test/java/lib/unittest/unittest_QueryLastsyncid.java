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
import rest.representation.querylastsyncidResponse;
import rest.representation.syncRequestResponse;
import rest.representation.unLockResponse;




@RunWith(Parameterized.class)
public class unittest_QueryLastsyncid extends BasicUnitTestClass{
	private String account,account2,bucket_name;

	public unittest_QueryLastsyncid(String account,String account2,String bucket_name){
		this.account=account;
		this.account2=account2;
		this.bucket_name=bucket_name;
		
		
		
	}
	
	
	   @Parameterized.Parameters
	   public static Collection parameters() {
			BufferedReader brf=null;
			List<String[]> l=new LinkedList<String[]>();	
					
			try {
				brf=new BufferedReader(new InputStreamReader(unittest_Existingevents.class.getClassLoader().getResourceAsStream("unittest_QueryLastsyncid.txt")));
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
	         { "User1","User2","testBucket200"},
	         { "User2","User3","testBucket200"}
	      });
	   }*/
	
	
	
	
	@Test
	public void Test1() throws EBSException, SQLException, NamingException{

		String instanceid=ek.genInstanceKey(account,"test","test");
		
		//getSyncid and unlock first
		syncRequestResponse rSync=ebs.syncrequest(account, instanceid);
		assertTrue(rSync!=null&&rSync.syncRequestResult);
		unLockResponse runlock=ebs.unlock(account,instanceid,"SUCCESS");
		assertTrue(runlock!=null&&runlock.unlockresult);
		
		
		querylastsyncidResponse r1=ebs.querylastsyncid(account, instanceid);
		assertTrue(r1!=null);
		assertTrue(r1.result);
		assertTrue(r1.lastsyncid!=null);
		assertTrue(r1.lastsyncid.equals(Long.toString(rSync.syncId)));
		assertTrue(r1.errors.size()==0);
	} 
	
	@Test
	public void Test2() throws EBSException, SQLException, NamingException{

		String instanceid=ek.genInstanceKey(account,"test","test");
		
		querylastsyncidResponse r1=ebs.querylastsyncid(account, instanceid);
		assertTrue(r1!=null);
		assertTrue(!r1.result);
		assertTrue(r1.lastsyncid!=null);
		assertTrue(r1.lastsyncid.equals(Long.toString(-1l)));
		assertTrue(r1.errors.size()==0);
	} 
	

}
