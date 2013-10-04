package rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import lib.CSSRestService;
import lib.DBTool;

import org.jets3t.service.S3ServiceException;
import org.jets3t.service.ServiceException;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;

import rest.exception.EBSException;
import rest.representation.ObjectsNumofUserResponse;
import rest.representation.getObjectsMD5Response;

import com.sun.jersey.spi.resource.Singleton;


@Path("/TEST")
@Singleton
public class TESTAPI {
	
	
	private boolean clearAllObjectInBucket(CSSRestService cssService,String bucket) throws ServiceException{
		boolean r=true;
		
		S3Object[] s3obj=cssService.listObjects(bucket);
		for(int i=0;i<s3obj.length;i++){
			try {
				cssService.deleteObject(bucket,s3obj[i].getName());
			} catch (ServiceException e) {
				e.printStackTrace();
				r=false;
				throw e;
			}
		}		
		return r;
	}
	
	
	
	@Path("cleanAllBucket")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String cleanAllBecket(@QueryParam("account") String account) throws Throwable{
		Runtime r=Runtime.getRuntime();
		Process p=r.exec(new String[]{"sh","/root/packages/purge.sh","-u",account});
		BufferedReader in=null;
		StringBuffer sb=new StringBuffer();
		
		
		try{
			in=new BufferedReader(new InputStreamReader(p.getInputStream()));
			String str="";
			while((str=in.readLine())!=null){
				sb.append(str);
			}
		}finally{
			in.close();
		}
		p.waitFor();
		
		
		return sb.toString();             
		
		
		
	}
	
	
	@Path("getQueueContent")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getQueueContent(@QueryParam("account") String account) throws Exception{
		try{
			Object[] obja= EBSAPI.getQueuesMap().getQueueManager(account).getQueue().toArray();
			String str="time stamep:"+EBSAPI.getQueuesMap().getQueueManager(account).getTimeStamp()+"\n";
			str+=      "now:        "+System.currentTimeMillis()+"\n\n";
			for(int i=0;i<obja.length;i++){
				str+=(i+":"+(obja[i].toString())+"\n");
			}
			
			return str;
			
		}catch(Throwable t){
			return "no instance in queue.";
		
		}
	}
	
	
	@Path("getObjectsMD5")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public getObjectsMD5Response getObjectMD5(@QueryParam("account") String account) throws EBSException{
		//for fake response=====================================
		String fakeset=TESTAPI.fakeValue.get("getObjectMD5");
		if(fakeset!=null){
			try{
				getObjectsMD5Response  r= new getObjectsMD5Response();   
				r.addError(fakeset);
				return r;
			}catch(Throwable t){
				DBTool.recordFakeResponseLog(t.toString());
			}
		}
		//======================================================	
		
		Connection walruscon = null;
		String query = "select object_key||','||etag from objects where owner_id=? order by object_key",
					countQuery="select count(*) from ("+query+") query";
try{
		getObjectsMD5Response resp=new getObjectsMD5Response();
			
			PreparedStatement stmt=null;
			ResultSet rs = null;
			walruscon = DBTool.getWalrusConnection();
			try{
			
				stmt = walruscon.prepareStatement(query);
				stmt.setString(1, account);
				rs = stmt.executeQuery();
				while(rs.next()){
					resp.addItem(rs.getString(1));
				}
				
				
			}finally{
				DBTool.CloseDBResultSet(rs);
				DBTool.CloseDBStatment(stmt);
			}
			
			try{
				stmt = walruscon.prepareStatement(countQuery);
				stmt.setString(1, account);
				rs = stmt.executeQuery();
				rs.next();
				resp.size=rs.getInt(1);
			
			}finally{
				DBTool.CloseDBResultSet(rs);
				DBTool.CloseDBStatment(stmt);
			}
		
		
		return resp;
}catch(Throwable t){
	DBTool.recordLog("api_error",t.toString());
	DBTool.RollBackDBConnection(walruscon);
//0008
	
		getObjectsMD5Response r= new getObjectsMD5Response();
		r.addError("0008",t);
		return r;
}finally{
			DBTool.CloseDBConnection(walruscon);
}
	}
	
	
	
	@Path("getObjectsNumofUser")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ObjectsNumofUserResponse ObjectsNumofUser(@QueryParam("account") String account) throws Throwable {
		ObjectsNumofUserResponse our=new ObjectsNumofUserResponse();
		try{
			File f=new File("/var/lib/eucalyptus/bukkits/bkt-"+account.toLowerCase());
			our.objectNum=f.list().length;
		}catch(Throwable t){
			our.objectNum=0;
		}
		
		return our;
		
	}
	
	public static FakeTest fakeValue=new FakeTest();
	
	@Path("setFakeValue")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String setFakeValue(@QueryParam("key")String key,@QueryParam("value")String value){
		return fakeValue.put(key, value);
	}
	
	@Path("getFakeValue")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getFakeValue(@QueryParam("key")String key){
		String r=fakeValue.get(key);
		if(r==null)r="";
		return r;
	}
	
	
	
	@Path("cleanUpQueuesMap")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String cleanUpQueueMap(){
		try{
			EBSAPI.getQueuesMap().cleanUp();
			return "true";
		}catch(Throwable t){
			return "false";
		}
	}

	
}
