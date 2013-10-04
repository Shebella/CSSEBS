package lib.unittest;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.QueryParam;

import lib.CSSRestService;
import lib.DBTool;

import org.jets3t.service.S3ServiceException;
import org.jets3t.service.ServiceException;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import rest.exception.EBSException;
import rest.representation.BucketInfo;


@RunWith(Parameterized.class)
public class unittest_QueryBucketInfo extends BasicUnitTestClass{
	private String account="User1",account2="User2",bucket_name="testBucket";
	
	
	
	public unittest_QueryBucketInfo(String account,String account2,String bucket_name){
		this.account=account;
		this.account2=account2;
		this.bucket_name=bucket_name;
		
	}
	
	
	
	   @Parameterized.Parameters
	   public static Collection parameters() {
			BufferedReader brf=null;
			List<String[]> l=new LinkedList<String[]>();	
					
			try {
				brf=new BufferedReader(new InputStreamReader(unittest_Existingevents.class.getClassLoader().getResourceAsStream("unittest_QueryBucketInfo.txt")));
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
	         { "User1","User2","testBucket"},
	         { "User1","User3","testBucket"}
	      });
	   }*/
	
	
	
	
	@Test
	public void testBucketAreadybeCreated() throws Throwable{
		this.createBucket(this.account,this.bucket_name);
		
		
		BucketInfo r=ebs.querybucketinfo(account, bucket_name);
		assertTrue(r!=null);
		assertTrue(r.result);
		assertTrue(this.account.equals(r.owner_id));
		
		
	}
	
	
	@Test
	public void testBucketHasnotbeCreated() throws Throwable{
		
		
		BucketInfo r=ebs.querybucketinfo(account, bucket_name);
		assertTrue(r!=null);
		assertTrue(!r.result);
		assertTrue(r.errors.size()>0);
		assertTrue("0402".equals(r.errors.get(0).code));
		
	}
	
	@Test
	public void testBucketHasnotpermission() throws Throwable{
		this.createBucket(account2,bucket_name);
		
		
		BucketInfo r=ebs.querybucketinfo(account, bucket_name);
		assertTrue(r!=null);
		assertTrue(!r.result);
		assertTrue(r.errors.size()>0);
		assertTrue("0401".equals(r.errors.get(0).code));
		
		ek.DeleteUser(account2);
	}
	
	
	
	
	

	@Before
	public void beforeBucketTest() throws Throwable{
		ek.AddUser(account);
		ek.AddUser(account2);
		
		this.cleanAllBecket(this.account);
		this.cleanAllBecket(this.account2);
	}
	
	@After
	public void afterBucketTest() throws Throwable{
		
		this.cleanAllBecket(this.account);
		this.cleanAllBecket(this.account2);
		ek.DeleteUser(account);
		ek.DeleteUser(account2);
	}
	
	
	

	
	
}
