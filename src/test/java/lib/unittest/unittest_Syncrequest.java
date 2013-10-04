package lib.unittest;

import static org.junit.Assert.*;

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

import rest.exception.EBSException;
import rest.representation.syncRequestResponse;





@RunWith(Parameterized.class)
public class unittest_Syncrequest extends BasicUnitTestClass{

	private String account;

	public unittest_Syncrequest(String account){
		this.account=account;
	}
	
	   @Parameterized.Parameters
	   public static Collection parameters() {
			BufferedReader brf=null;
			List<String[]> l=new LinkedList<String[]>();	
					
			try {
				brf=new BufferedReader(new InputStreamReader(unittest_Existingevents.class.getClassLoader().getResourceAsStream("unittest_Syncrequest.txt")));
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
	 public void test1() throws EBSException{
		String instanceid="test1";
		syncRequestResponse r=ebs.syncrequest(account, instanceid);
		assertTrue(r!=null&&r.syncRequestResult&&r.errors.size()==0);
		
	}
	
	
	
	@Test
	public void test2() throws EBSException{
		

		String instanceid1="test1",instanceid2="test2";
		syncRequestResponse r1=ebs.syncrequest(account, instanceid1);
		assertTrue(r1!=null&&r1.syncRequestResult);
		
		syncRequestResponse r2=ebs.syncrequest(account, instanceid2);
		assertTrue(r2!=null&&!r2.syncRequestResult&&r2.errors.size()==1);
		assertTrue("0101".equals(r2.errors.get(0).code));
		
		
	}
	
	
	
	 
	
}
