package lib.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import rest.EBSAPI;

public class Testsimple {
	
	
	public static void main(String[] args){
		
		Result r=JUnitCore.runClasses(
				Testsimple.class,
				DBToolTest.class,
				QueueManagerTest.class,
				QueuesMapTest.class,
				EBSAPITest.class
		);
		
		System.out.println("RunCount:"+r.getRunCount());
		System.out.println("RunTime:"+r.getRunTime()+" milliseconds");
		System.out.println("Success count:"+(r.getRunCount()-r.getFailureCount()));
		System.out.println("Failure count:"+r.getFailureCount());
		
		
		
		List<Failure> flist=r.getFailures();
		
		for(Failure lf:flist){
			System.out.print(lf.getDescription()+", ");
			System.out.print(lf.getMessage()+", ");
			
		}
	}
	
	@Test
	public void Test_1(){
	}
	
	
	
	
}