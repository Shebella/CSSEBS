package lib.unittest;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import rest.representation.existingeventsResponse;
import rest.representation.syncRequestResponse;
import rest.representation.unLockResponse;

@RunWith(Parameterized.class)
public class unittest_ExistingeventsV extends BasicUnitTestClass{
	private String account;
	
	
	public unittest_ExistingeventsV(String account){
		this.account=account;
	}

	

	
	   @Parameterized.Parameters
	   public static Collection parameters() {
			BufferedReader brf=null;
			List<String[]> l=new LinkedList<String[]>();	
					
			try {
				brf=new BufferedReader(new InputStreamReader(unittest_Existingevents.class.getClassLoader().getResourceAsStream("unittest_ExistingeventsV.txt")));
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
	
	
/*	@Parameterized.Parameters
	public static Collection parameters() {
	   return Arrays.asList(new Object[][] {
	         { "User1"},
	         { "User2"}
	      });
	}*/
	
	 @Test
	 public void testRunaway() throws Exception{
		 
		 
		 String instanceid=ek.genInstanceKey(account,"test","test");
		 
		 //getlock 
		 syncRequestResponse r=this.ebs.syncrequestV(account,instanceid);
		 assertTrue(r!=null&&r.syncRequestResult);
	 
		 //insert 1 event
		 assertTrue(r!=null&&this.addEvent(account,(int)r.syncId));
	 
		 //unlock
		 unLockResponse ur=this.ebs.unlockV(account, instanceid,"SUCCESS"); 
		 assertTrue(ur!=null&&ur.unlockresult);

     

    	//create new instanceid
	 	instanceid=ek.genInstanceKey(account,"test","test2");
    	
	 	//TEST
	 	existingeventsResponse er=ebs.existingeventsV(account, instanceid,-1);
    	assertTrue(er!=null&&er.result);
    	assertTrue(er!=null&&er.errors.size()==0);
    	assertTrue(!er.hasevent);
    	assertTrue(er.isrunaway);
	 }
	 
	 @Test
	 public void testRunawayWithIllegalInstanceId() throws Exception{
		 String instanceid="illegalInstanceID";//ek.genInstanceKey(account,"test","test");
		 
		 //getlock 
		 syncRequestResponse r=this.ebs.syncrequestV(account,instanceid);
		 assertTrue(r!=null&&r.syncRequestResult);
	 
		 //insert 1 event
		 assertTrue(r!=null&&this.addEvent(account,(int)r.syncId));
	 
		 //unlock
		 unLockResponse ur=this.ebs.unlockV(account, instanceid,"SUCCESS"); 
		 assertTrue(ur!=null&&ur.unlockresult);

     

	 	instanceid="test2";
    	
	 	//TEST
	 	existingeventsResponse er=ebs.existingeventsV(account, instanceid,-1);
    	assertTrue(er!=null&&er.result);
    	assertTrue(!er.isrunaway);
    	assertTrue(!er.hasevent);
    	assertTrue(er!=null&&er.errors.size()==0);
	 }
	 
	 
	 @Test
	 public void testTrue() throws Exception{

		 String instanceid1=ek.genInstanceKey(account,"test","test1"),
				instanceid2=ek.genInstanceKey(account,"test","test2");

		 
		 //getlock 
		 syncRequestResponse r1=this.ebs.syncrequestV(account,instanceid1);
		 assertTrue(r1!=null&&r1.syncRequestResult);
		 
		 
		 
		 //unlock
		 unLockResponse ur=this.ebs.unlockV(account, instanceid1,"SUCCESS"); 
		 assertTrue(ur!=null&&ur.unlockresult);
		 
		 //getlock 
		 syncRequestResponse r2=this.ebs.syncrequestV(account,instanceid2);
		 assertTrue(r2!=null&&r2.syncRequestResult);
		 
		 //insert 1 event
		 assertTrue(r2!=null&&this.addEvent(account,(int)r2.syncId));
		 
		 //unlock
		 unLockResponse ur2=this.ebs.unlockV(account, instanceid2,"SUCCESS"); 
		 assertTrue(ur2!=null&&ur2.unlockresult);

		 
		 
	 	//TEST
	 	existingeventsResponse er=ebs.existingeventsV(account, instanceid1,(int)r1.syncId);
    	assertTrue(er!=null&&er.result&&er.errors.size()==0);
    	assertTrue(!er.isrunaway);
    	assertTrue(er.hasevent);
    	
    	
    	
    	
	 }
	 
	 @Test
	 public void testFalse() throws Exception{

		 String instanceid1=ek.genInstanceKey(account,"test","test1"),
				instanceid2=ek.genInstanceKey(account,"test","test2");

		 
		 //instance 1 getlock 
		 syncRequestResponse r1=this.ebs.syncrequestV(account,instanceid1);
		 assertTrue(r1!=null&&r1.syncRequestResult);
		 
		 
		 
		 //instance 2 unlock
		 unLockResponse ur=this.ebs.unlockV(account, instanceid1,"SUCCESS"); 
		 assertTrue(ur!=null&&ur.unlockresult);
		 
		 //instance 2 getlock 
		 syncRequestResponse r2=this.ebs.syncrequest(account,instanceid2);
		 assertTrue(r2!=null&&r2.syncRequestResult);
		 unLockResponse ur2=this.ebs.unlockV(account, instanceid2,"SUCCESS"); 
		 assertTrue(ur2!=null&&ur2.unlockresult);
		 
		 
	 	//TEST: instance 1 ask is existing event?
	 	existingeventsResponse er=ebs.existingeventsV(account, instanceid1,(int)r1.syncId);
    	assertTrue(er!=null&&er.result);
    	assertTrue(er.errors.size()==0);
    	assertTrue(!er.isrunaway);
    	assertTrue(!er.hasevent);
	 }
	 

}
