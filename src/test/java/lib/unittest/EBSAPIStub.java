package lib.unittest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

import javax.naming.NamingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import lib.Config;
import lib.DBTool;
import lib.QueueManager;
import rest.EBSAPI;
import rest.RenameAPI;
import rest.TESTAPI;
import rest.exception.EBSException;
import rest.representation.BucketInfo;
import rest.representation.FolderInfo;
import rest.representation.HeartbeatResponse;
import rest.representation.ObjectList;
import rest.representation.RenameObjectResponse;
import rest.representation.S3Object;
import rest.representation.SystemInfo;
import rest.representation.UserInfo;
import rest.representation.addEventResponse;
import rest.representation.changeSyncIdStatusResponse;
import rest.representation.confirmsyncResponse;
import rest.representation.existingeventsResponse;
import rest.representation.getObjectsMD5Response;
import rest.representation.queryVersionResponse;
import rest.representation.queryeventResponse;
import rest.representation.querylastsyncidResponse;
import rest.representation.syncRequestResponse;
import rest.representation.unLockResponse;

public class EBSAPIStub extends EBSAPI{
	protected String host="http://127.0.0.1/sbx_svr/rest/EBS/",
			         testhost="http://127.0.0.1/sbx_svr/rest/TEST/";
	
	
	
	public void cleanUpQueueMap(){
		
		
		Client c=Client.create();
		WebResource wr=c.resource(testhost+"cleanUpQueuesMap");
		
		MultivaluedMap queryParams = new MultivaluedMapImpl();
		
        wr.queryParams(queryParams).get(String.class);
	}

	public existingeventsResponse existingevents(String account,String instanceid,Integer lastsyncid

	) throws EBSException{
		Client c=Client.create();
		WebResource wr=c.resource(host+"existingevents");
		
		MultivaluedMap queryParams = new MultivaluedMapImpl();
        queryParams.add("account",account);
        queryParams.add("instanceid",instanceid);
        queryParams.add("lastsyncid",new Integer(lastsyncid).toString());
		
        return wr.queryParams(queryParams).get(existingeventsResponse.class);
	}


	public syncRequestResponse insertSyncIdwithoutLock(String account,String instanceid,String status) throws EBSException{

		Client c=Client.create();
		WebResource wr=c.resource(host+"syncIdWithoutLock");
		
		MultivaluedMap queryParams = new MultivaluedMapImpl();
        queryParams.add("account",account);
        queryParams.add("instanceid",instanceid);
        queryParams.add("status",status);
		
        return wr.queryParams(queryParams).put(syncRequestResponse.class);
		
		
		
	}
	public changeSyncIdStatusResponse changeSyncIdStatusInDB( String account,String syncid,String status) throws EBSException{


		Client c=Client.create();
		WebResource wr=c.resource(host+"changeSyncIdStatusInDB");
		
		MultivaluedMap queryParams = new MultivaluedMapImpl();
        queryParams.add("account",account);
        queryParams.add("syncid",syncid);
        queryParams.add("status",status);
		
        return wr.queryParams(queryParams).put(changeSyncIdStatusResponse.class);
        
        
        
	}
	
	
	
	

	public syncRequestResponse syncrequest(String account,String instanceid

	) throws EBSException{

		Client c=Client.create();
		WebResource wr=c.resource(host+"syncrequest");
		
		MultivaluedMap queryParams = new MultivaluedMapImpl();
        queryParams.add("account",account);
        queryParams.add("instanceid",instanceid);
		
        return wr.queryParams(queryParams).put(syncRequestResponse.class);
        
        
	}


	public queryeventResponse queryevent(String account,String instanceid,
			String thissyncid, String lastsyncid,Integer maxevent) throws EBSException{
		


		Client c=Client.create();
		WebResource wr=c.resource(host+"queryevent");
		
		MultivaluedMap queryParams = new MultivaluedMapImpl();
        queryParams.add("account",account);
        queryParams.add("instanceid",instanceid);
		queryParams.add("thissyncid",thissyncid);
		queryParams.add("lastsyncid",lastsyncid);
		if(maxevent!=null){
			queryParams.add("maxevent",maxevent);		
		}
        return wr.queryParams(queryParams).get(queryeventResponse.class);
	}

/*	@Path("addevent")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_PLAIN)
	public addEventResponse addevent(@QueryParam("account") String account,
			@QueryParam("instanceid") String instanceid, @QueryParam("syncid") Integer syncid,
			@DefaultValue("true") @QueryParam("unlock") Boolean unlock, InputStream in) throws EBSException{}

	@Path("confirmsync")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public confirmsyncResponse confirmsync(@QueryParam("account") String account,
			@QueryParam("instanceid") String instanceid, @QueryParam("syncid") String syncid)
			throws EBSException{}*/


	public BucketInfo querybucketinfo(String account,String bucketname) throws EBSException {

		Client c=Client.create();
		WebResource wr=c.resource(host+"querybucketinfo");
		
		MultivaluedMap queryParams = new MultivaluedMapImpl();
        queryParams.add("account",account);
        queryParams.add("bucketname",bucketname);
		
        return wr.queryParams(queryParams).get(BucketInfo.class);
	}


	public FolderInfo queryfolderinfo(String account,String bucketname,String foldername)
			throws EBSException{
		Client c=Client.create();
		WebResource wr=c.resource(host+"queryfolderinfo");
		
		MultivaluedMap queryParams = new MultivaluedMapImpl();
        queryParams.add("account",account);
        queryParams.add("bucketname",bucketname);
        queryParams.add("foldername",foldername);
		
        return wr.queryParams(queryParams).get(FolderInfo.class);
		
		
		
	}

/*	@Path("queryuserinfo")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public UserInfo queryuserinfo(@QueryParam("account") String account) throws EBSException{}*/


	public HeartbeatResponse heartbeat(String account,String instanceid) throws EBSException{
		
		Client c=Client.create();
		WebResource wr=c.resource(host+"heartbeat");
		
		MultivaluedMap queryParams = new MultivaluedMapImpl();
        queryParams.add("account",account);
        queryParams.add("instanceid",instanceid);
		
        return wr.queryParams(queryParams).post(HeartbeatResponse.class);
	}
	
	
/*	@Path("querysystemInfo")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public SystemInfo serverInfo() throws EBSException{}*/


	

	public unLockResponse unlock(String account,String instanceid,String result) throws EBSException{
		
		Client c=Client.create();
		WebResource wr=c.resource(host+"unlock");
		
		MultivaluedMap queryParams = new MultivaluedMapImpl();
        queryParams.add("account",account);
        queryParams.add("instanceid",instanceid);
        queryParams.add("result",result);
        
        
		
        return wr.queryParams(queryParams).put(unLockResponse.class);
	}
	
	
	
	

	public unLockResponse unlockV(String account,String instanceid,String result) throws EBSException{

		
		Client c=Client.create();
		WebResource wr=c.resource(host+"unlockV");
		
		MultivaluedMap queryParams = new MultivaluedMapImpl();
        queryParams.add("account",account);
        queryParams.add("instanceid",instanceid);
        queryParams.add("result",result);
        
        
		
        return wr.queryParams(queryParams).put(unLockResponse.class);
	}
	
	
	
	
	
	
	
	
	
	
	

	public querylastsyncidResponse querylastsyncid(String account,String instanceid) throws EBSException{

		
		Client c=Client.create();
		WebResource wr=c.resource(host+"querylastsyncid");
		
		MultivaluedMap queryParams = new MultivaluedMapImpl();
        queryParams.add("account",account);
        queryParams.add("instanceid",instanceid);
        
        
		
        return wr.queryParams(queryParams).get(querylastsyncidResponse.class);
	}


	public RenameObjectResponse renameObject(String account,String instanceid,
			Integer syncid,String src_object,String dest_object,String bucket_name) throws EBSException{
		
		Client c=Client.create();
		WebResource wr=c.resource(host+"renameobject");
		
		MultivaluedMap queryParams = new MultivaluedMapImpl();
        queryParams.add("account",account);
        queryParams.add("instanceid",instanceid);
        queryParams.add("syncid",syncid);
        queryParams.add("src_object",src_object);
        queryParams.add("dest_object",dest_object);
        queryParams.add("bucket_name",bucket_name);
        
        
		
        return wr.queryParams(queryParams).post(RenameObjectResponse.class);
	}
/*	
	@Path("getObjectsMD5")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public getObjectsMD5Response getObjectMD5(@QueryParam("account") String account) throws EBSException{}
	
	@Path("queryversion")
	@GET
        @Produces(MediaType.APPLICATION_JSON)
        public queryVersionResponse queryVersion() throws EBSException{}*/


	public ObjectList listObjects(String bucket,String folder,int page,int  pagesize) throws EBSException{

		
		Client c=Client.create();
		WebResource wr=c.resource(host+"listobjects");
		
		MultivaluedMap queryParams = new MultivaluedMapImpl();
        queryParams.add("bucket",bucket);
        queryParams.add("folder",folder);
        queryParams.add("page",page);
        queryParams.add("pagesize",pagesize);
        
        
        
		
        return wr.queryParams(queryParams).get(ObjectList.class);
	}





	
	
	

	public querylastsyncidResponse queryreadsyncid(String account,String instanceid) throws EBSException{

		
		Client c=Client.create();
		WebResource wr=c.resource(host+"queryreadsyncid");
		
		MultivaluedMap queryParams = new MultivaluedMapImpl();
        queryParams.add("account",account);
        queryParams.add("instanceid",instanceid);
        
        
        return wr.queryParams(queryParams).get(querylastsyncidResponse.class);
	}
	



	public queryeventResponse queryreadevent(String account,String instanceid,
			String readsyncid,String lastsyncid,Integer maxevent)
			throws EBSException{

		
		Client c=Client.create();
		WebResource wr=c.resource(host+"queryreadevent");
		
		MultivaluedMap queryParams = new MultivaluedMapImpl();
        queryParams.add("account",account);
        queryParams.add("instanceid",instanceid);

        queryParams.add("readsyncid",readsyncid);
        queryParams.add("lastsyncid",lastsyncid);
        queryParams.add("maxevent",maxevent);        
        
        return wr.queryParams(queryParams).get(queryeventResponse.class);
	}
	
	

	public queryeventResponse queryeventV(String account,String instanceid,
			String fromsyncid,String tosyncid) throws EBSException{	
		Client c=Client.create();
		WebResource wr=c.resource(host+"queryeventsV");
		
		MultivaluedMap queryParams = new MultivaluedMapImpl();
        queryParams.add("account",account);
        queryParams.add("instanceid",instanceid);
        queryParams.add("fromsyncid",fromsyncid);
        queryParams.add("tosyncid",tosyncid);   
        
        return wr.queryParams(queryParams).get(queryeventResponse.class);		
		
	}
	
	
	
	
	

	public syncRequestResponse syncrequestV(String account,String instanceid) throws EBSException{
		
		Client c=Client.create();
		WebResource wr=c.resource(host+"syncrequestV");
		
		MultivaluedMap queryParams = new MultivaluedMapImpl();
        queryParams.add("account",account);
        queryParams.add("instanceid",instanceid);
        
        return wr.queryParams(queryParams).put(syncRequestResponse.class);		
		
	
		
		
		
		
		
	}
	
	
	
	
	
	

}
