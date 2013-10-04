package rest.representation;

import java.util.HashMap;
import java.util.Map;

public class ErrorMessage {
	private static Map<String,String> map=new HashMap<String,String>();
	static{
		
		//generic error 
		map.put("0001","Invalid Instance Id");
		map.put("0002","Invalid User");
		map.put("0003","lastsyncid does not exist");
		map.put("0004","lastsyncid is not valid (not equal to lastsyncid in db)");
		map.put("0005","Invalid thissyncid (thissyncid is not equal to the thissyncid in db)");
		map.put("0006","Invalid thissyncid (thissyncid is less than lastsyncid)");
		map.put("0007","Server Too Busy");
		map.put("0008","EBS Internal Error");
		map.put("0009","Request parameter is null");
		//syncreqeust
		map.put("0101","Waiting to get lock");
		//queryevent 
		map.put("0201","Number of delta events exceed limit of EBS API.");
		map.put("0202","need to get lock first.");
		//unlock
		map.put("0301","instance unlocks before getting syncid.");
		map.put("0302","instance has unlocked with SUCC status");
		map.put("0303","instance has been unlocked with FAIL status");
		map.put("0304","instance has unlocked with SYNC-UPD status");
		//querybucketinfo
		map.put("0401","No permission to access bucket");
		map.put("0402","bucket does not exist");
		//queryfolderinfo 
		map.put("0501","No permission to access bucket");
		map.put("0502","bucket does not exist");		
		map.put("0503","No permission to access folder");
		map.put("0504","folder does not exist");
		//heartbeat 
		map.put("0601","Instance does not own current lock");		
		
		//paging 
		map.put("0701","page or pagesize can not be in negtive.");
		map.put("0702","must specify bucket parameter.");
		map.put("07030","out of page range");
		
		//rename 
		map.put("0801","need to get lock first."); 
		
		//addevent  
		map.put("0901","ndde to get lock first.");
		map.put("0902","unlock fail");
		
		
		
	} 
	
	public String code="-1",requestId="-1",message="-1";
	public 	ErrorMessage(){}
	
	public ErrorMessage(String code,String requestId){
		this.code=code;this.requestId=requestId;
		this.message=ErrorMessage.map.get(code);
	}
	
	public String toString(){
		return "code="+this.code+",messgae="+this.message+",reqeustId="+this.requestId;
		
	}

}

