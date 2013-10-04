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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import rest.representation.FolderInfo;





@RunWith(Parameterized.class)
public class unittest_QueryFolderInfo extends BasicUnitTestClass{

	private String account="User1",account2="User2",bucket_name="testBucket200";
	

	public unittest_QueryFolderInfo(String account,String account2,String bucket_name){
		this.account=account;
		this.account2=account2;
		this.bucket_name=bucket_name;
		
		
		
	}
	
	   @Parameterized.Parameters
	   public static Collection parameters() {
			BufferedReader brf=null;
			List<String[]> l=new LinkedList<String[]>();	
					
			try {
				brf=new BufferedReader(new InputStreamReader(unittest_Existingevents.class.getClassLoader().getResourceAsStream("unittest_QueryFolderInfo.txt")));
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
	public void test1() throws Throwable{
		ek.AddUser(account);
		this.createBucket(account,bucket_name);
		this.createFolder(account,bucket_name,"/testabc");

		this.createFolder(account,bucket_name,"/testabc/testFolder1");
		this.createObject(account,bucket_name,"/testabc/testObject1");
		
		
		FolderInfo r1=ebs.queryfolderinfo(account, bucket_name,"/testabc");
		assertTrue(r1!=null);
		assertTrue(r1.buckinfo!=null);
		assertTrue(r1.buckinfo.owner_id.equals(account));
		assertTrue(r1.fileNum==1);
		
	}

	@Test
	public void testBucketDoesnotExist() throws Throwable{
		ek.AddUser(account);
		
		
		FolderInfo r1=ebs.queryfolderinfo(account, bucket_name,"/testabc");
		assertTrue(r1!=null);
		assertTrue(r1.errors.size()==1);
		assertTrue("0502".equals(r1.errors.get(0).code));
		
	}
	
	@Test
	public void testObjectDoesnotExist() throws Throwable{
		ek.AddUser(account);
		this.createBucket(account,bucket_name);
		
		FolderInfo r1=ebs.queryfolderinfo(account, bucket_name,"/testabc");
		assertTrue(r1!=null);
		assertTrue(r1.errors.size()==1);
		assertTrue("0504".equals(r1.errors.get(0).code));
		
	}
	
	
	
	
	
	
	@Before
	public void beforeFolderTest() throws Throwable{
		//to avoid last time's fault on test cause some objects still on server,
		//we alos do function "afterFolder" to clean object,bucket and user before eacho test,

	
		
		
		this.afterFolderTest();
		ek.AddUser(account);
		ek.AddUser(account2);	
		this.cleanAllBecket(this.account);
		this.cleanAllBecket(this.account2);
	}
	
	@After
	public void afterFolderTest() throws Throwable{
		
		this.cleanAllBecket(this.account);
		this.cleanAllBecket(this.account2);
		ek.DeleteUser(account);
		ek.DeleteUser(account2);
	}
	
	

}
