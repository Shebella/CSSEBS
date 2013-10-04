package rest;

import java.io.*;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;
import java.util.LinkedList;
import java.util.List;

import javax.naming.NamingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRootElement;

import lib.Config;
import lib.Config.ERRORCODE;
import lib.DBTool;
import lib.QueueManager;
import lib.QueuesMap;
import rest.exception.EBSException;
import rest.representation.*;

import com.sun.jersey.spi.resource.Singleton;

@Path("/EBS")
@Singleton
public class EBSAPI {
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");


	private static QueuesMap qMap = new QueuesMap();
	public static QueuesMap getQueuesMap(){return qMap;}

	public EBSAPI() {

	}

	public void cleanUpQueueMap(){
		EBSAPI.getQueuesMap().cleanUp();
	}
	
	
	
	@Path("existingevents")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public existingeventsResponse existingevents(@QueryParam("account") String account,
			@QueryParam("instanceid") String instanceid, @QueryParam("lastsyncid") Integer lastsyncid

	) throws EBSException{
		//for fake response=====================================
		String fakeset=TESTAPI.fakeValue.get("existingevents");
		if(fakeset!=null){
			try{
				existingeventsResponse r= new existingeventsResponse(); 	
				r.addError(fakeset);
				return r;
			}catch(Throwable t){
				DBTool.recordFakeResponseLog(t.toString());
			}
		}
		//======================================================
		
		
		
		Connection walruscon = null;
try{
		existingeventsResponse r= new existingeventsResponse();
		if (DBTool.isEmpty(account)|| DBTool.isEmpty(instanceid) || lastsyncid == null) {//error 201
//0009
			r.addError("0009");
			return r;
		}
		
		
		walruscon = DBTool.getWalrusConnection();
		PreparedStatement stmt=null;
		ResultSet rs = null;

		try {

			String query = "select is_enabled from client_instances  where  user_id=?  and instance_key=? limit 1";
			stmt = walruscon.prepareStatement(query);
			stmt.setString(1, account);
			stmt.setString(2, instanceid);
			rs = stmt.executeQuery();
			if (!rs.next()) {
				r.isrunaway=false;
				r.hasevent=false;
				return r;
			}
		} finally {
			DBTool.CloseDBResultSet(rs);
			DBTool.CloseDBStatment(stmt);
		}
		
		try {// check if lastsyncid is exist. if not ,it's run away.

			if (!DBTool.isRunAway(walruscon, account, lastsyncid)) {
				r.isrunaway =true;
				r.hasevent=false;
				return r;
			}

			String query = "select syncid from opt_log where cssact = ? and syncid>? and rslt='succ' and not  op in ('GetRemotely','DeleteLocally') limit 1";
			stmt = walruscon.prepareStatement(query);
			stmt.setString(1, account);
			stmt.setInt(2, lastsyncid);

			rs = stmt.executeQuery();
			if (!rs.next()) {
				r.isrunaway=false;
				r.hasevent=false;
				return r;
			}


		} finally {
			DBTool.CloseDBResultSet(rs);
			DBTool.CloseDBStatment(stmt);
		}

		r.isrunaway=false;
		r.hasevent=true;
		return r;
}catch(Throwable t){
	DBTool.recordLog("api_error",t.toString());
	DBTool.RollBackDBConnection(walruscon);
//0008	
	existingeventsResponse r= new existingeventsResponse();
	r.addError("0008",t);
	return r;
}finally{
	DBTool.CloseDBConnection(walruscon);
}
	}

	
	@Path("existingeventsV")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public existingeventsResponse existingeventsV(@QueryParam("account") String account,
			@QueryParam("instanceid") String instanceid, @QueryParam("lastsyncid") Integer lastsyncid

	) throws EBSException{
		//for fake response=====================================
		String fakeset=TESTAPI.fakeValue.get("existingevents");
		if(fakeset!=null){
			try{
				existingeventsResponse r= new existingeventsResponse(); 	
				r.addError(fakeset);
				return r;
			}catch(Throwable t){
				DBTool.recordFakeResponseLog(t.toString());
			}
		}
		//======================================================
		
		
		
		Connection walruscon = null;
try{
		existingeventsResponse r= new existingeventsResponse();
		if (DBTool.isEmpty(account)|| DBTool.isEmpty(instanceid) || lastsyncid == null) {//error 201
//0009
			r.addError("0009");
			return r;
		}
		
		
		walruscon = DBTool.getWalrusConnection();
		PreparedStatement stmt=null;
		ResultSet rs = null;

		try {

			String query = "select is_enabled from client_instances  where  user_id=?  and instance_key=? limit 1";
			stmt = walruscon.prepareStatement(query);
			stmt.setString(1, account);
			stmt.setString(2, instanceid);
			rs = stmt.executeQuery();
			if (!rs.next()) {
				r.isrunaway=false;
				r.hasevent=false;
				return r;
			}
		} finally {
			DBTool.CloseDBResultSet(rs);
			DBTool.CloseDBStatment(stmt);
		}
		
		try {// check if lastsyncid is exist. if not ,it's run away.

			if (!DBTool.isRunAway(walruscon, account, lastsyncid)) {
				r.isrunaway =true;
				r.hasevent=false;
				return r;
			}

			String query = "select p.syncid from opt_log p left outer join syncinfo s on p.syncid=s.syncid where cssact = ? and p.syncid>? and rslt='succ' and not  op in ('GetRemotely','DeleteLocally') and instancekey!=? limit 1";
			stmt = walruscon.prepareStatement(query);
			stmt.setString(1, account);
			stmt.setInt(2, lastsyncid);
			stmt.setString(3,instanceid);

			rs = stmt.executeQuery();
			if (!rs.next()) {
				r.isrunaway=false;
				r.hasevent=false;
				return r;
			}


		} finally {
			DBTool.CloseDBResultSet(rs);
			DBTool.CloseDBStatment(stmt);
		}

		r.isrunaway=false;
		r.hasevent=true;
		return r;
}catch(Throwable t){
	DBTool.recordLog("api_error",t.toString());
	DBTool.RollBackDBConnection(walruscon);
//0008	
	existingeventsResponse r= new existingeventsResponse();
	r.addError("0008",t);
	return r;
}finally{
	DBTool.CloseDBConnection(walruscon);
}
	}
	
	
	
	
	
	
	
	
	
	
	
	@Path("syncIdWithoutLock")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	public syncRequestResponse insertSyncIdwithoutLock(@QueryParam("account") String account,
			@QueryParam("instanceid") String instanceid,@QueryParam("status") String status) throws EBSException{

		//for fake response=====================================
		String fakeset=TESTAPI.fakeValue.get("syncIdWithoutLock");
		if(fakeset!=null){
			try{
				syncRequestResponse r= new syncRequestResponse(); 	
				r.addError(fakeset);
				return r;
			}catch(Throwable t){
				DBTool.recordFakeResponseLog(t.toString());
			}
		}
		//======================================================
		
		
		
		
		Connection con=null;
try{
		syncRequestResponse resp=new syncRequestResponse();
		
		
//error 0009 
		if(DBTool.isEmpty(account)||DBTool.isEmpty(instanceid)||DBTool.isEmpty(status)){
			resp.addError("0009");
			return resp;
			
		}
		
		
//error 0009
		if(Config.syncIdStatus.LOCK.equals(status)){
			resp.addError("0009");
			return resp;
		}
		//resp.syncData = new syncRequestResponse.syncData();
		PreparedStatement stmt=null;
		ResultSet rs=null;
		try {
			con=DBTool.getConnection();
			stmt=con.prepareStatement("insert into syncInfo(account,instanceKey,status,createTime) values(?,?,?,?)  RETURNING syncid");
			stmt.setString(1,account);
			stmt.setString(2,instanceid);
			stmt.setString(3,status);
			stmt.setTimestamp(4,DBTool.getUTC0());
			rs=stmt.executeQuery();
			rs.next();
			resp.syncId=Long.valueOf(rs.getString(1));
			
		}finally{
			DBTool.CloseDBResultSet(rs);
			DBTool.CloseDBStatment(stmt);
		}
		
		resp.syncRequestResult=true;
		
		return resp;
}catch(Throwable t){

	DBTool.recordLog("api_error",t.toString());
	DBTool.RollBackDBConnection(con);
		syncRequestResponse r= new syncRequestResponse();
//0008
		r.addError("0008",t);
		return r;
}finally{
	DBTool.CloseDBConnection(con);
}
	}

	@Path("changeSyncIdStatusInDB")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	public changeSyncIdStatusResponse changeSyncIdStatusInDB(@QueryParam("account") String account,
			@QueryParam("syncid") String syncid,@QueryParam("status") String status) throws EBSException{

		//for fake response=====================================
		String fakeset=TESTAPI.fakeValue.get("changeSyncIdStatusInDB");
		if(fakeset!=null){
			try{
				changeSyncIdStatusResponse r= new changeSyncIdStatusResponse(); 	
				r.addError(fakeset);
				return r;
			}catch(Throwable t){
				DBTool.recordFakeResponseLog(t.toString());
			}
		}
		//======================================================
		
		
		
		Connection con=null;
try{
		changeSyncIdStatusResponse resp=new changeSyncIdStatusResponse();
		
		if(DBTool.isEmpty(account)||DBTool.isEmpty(syncid) || DBTool.isEmpty(status)){
		//0009
			resp.addError("0009");
			return resp;
		}		
		
		
		
		
		if(Config.syncIdStatus.LOCK.equals(status)){
//0009
			resp.addError("0009");
			return resp;
		}

		PreparedStatement stmt=null;
		
		try {
			con=DBTool.getConnection();
			stmt=con.prepareStatement("update syncinfo set status=? where syncid=? and account=? and status not in ('LOCK','SUCC')");
			stmt.setString(1,status);
			stmt.setInt(2,Integer.parseInt(syncid));
			stmt.setString(3,account);
			int updateCount=stmt.executeUpdate();
			
			if(updateCount<=0){resp.result=false;}
		}finally{
			DBTool.CloseDBStatment(stmt);
			DBTool.CloseDBConnection(con);
			
		}
		return resp;
}catch(Throwable t){
	DBTool.recordLog("api_error",t.toString());
	DBTool.RollBackDBConnection(con);
	changeSyncIdStatusResponse r= new changeSyncIdStatusResponse();
//0008
		r.addError("0008",t);
		return r;
}		
	}
	
	
	
	
	@Path("syncrequest")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	public syncRequestResponse syncrequest(@QueryParam("account") String account,
			@QueryParam("instanceid") String instanceid

	) throws EBSException{

		//for fake response=====================================
		String fakeset=TESTAPI.fakeValue.get("syncrequest");
		if(fakeset!=null){
			try{
				syncRequestResponse r= new syncRequestResponse(); 	
				r.addError(fakeset);
				return r;
			}catch(Throwable t){
				DBTool.recordFakeResponseLog(t.toString());
			}
		}
		//======================================================
		
try{
		syncRequestResponse resp =null;
		
		
		resp = new syncRequestResponse();
		if (DBTool.isEmpty(account) || DBTool.isEmpty(instanceid)) {
//0009
			resp.addError("0009");
			return resp;
		}

		QueueManager qm = qMap.getQueueManager(account);
		boolean getlock =false;
		try{
			getlock=qm.getLock(instanceid);
		}catch(Throwable t){
//0008
			resp.addError("0101");
			return resp;
		}		
		// prepare response
		resp.syncRequestResult = getlock;
		if (resp.syncRequestResult) {
			//resp.syncData = new syncRequestResponse.syncData();
			resp.syncId = qm.getsyncid();
		}else{
//0101
			resp.addError("0101");
			
		}
		resp.systeminfo = new SystemInfo();
		resp.systeminfo.serverTime = sdf.format(DBTool.getUTC0());
		// --prepare response

	
	
	
		return resp;
}catch(Throwable t){
		DBTool.recordLog("api_error",t.toString());
		syncRequestResponse r= new syncRequestResponse();
		r.addError("0008",t);
		return r;
}	
		
	}

	@Path("queryevent")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public queryeventResponse queryevent(@QueryParam("account") String account,
			@QueryParam("instanceid") String instanceid, @QueryParam("thissyncid") String thissyncid,
			@QueryParam("lastsyncid") String lastsyncid, @QueryParam("maxevent") Integer maxevent) throws EBSException{
		//for fake response=====================================
		String fakeset=TESTAPI.fakeValue.get("queryevent");
		if(fakeset!=null){
			try{
				queryeventResponse r= new queryeventResponse(); 	
				r.addError(fakeset);
				return r;
			}catch(Throwable t){
				DBTool.recordFakeResponseLog(t.toString());
			}
		}
		//======================================================
		
		
		
		Connection con = null;
		String query = "select id,rectime,time,hwid,cssact,op,fpth,file_version,isfolder,rslt,syncId from opt_log where cssact = ? and rslt='succ' and syncid>?  and syncid<? order by syncid, fpth asc limit ?",
			   countQuery="select count(*) from ("+query+") query";
		
try{
		queryeventResponse resp = new queryeventResponse();
		
		if (DBTool.isEmpty(account)||DBTool.isEmpty(instanceid)|| DBTool.isEmpty(thissyncid)|| DBTool.isEmpty(lastsyncid)) {
//error 0009
			resp.addError("0009");
			return resp;
		}
		
		
		QueueManager qm =qMap.getQueueManager(account);
		if (!qm.isLocked(instanceid)) {
//error 0202
			resp.message= "need to get lock first";
			resp.addError("0202");
			return resp;
		}
		con = DBTool.getConnection();
		PreparedStatement stmt =null;
		ResultSet rs = null;
		

				
		try {
			
			stmt = con.prepareStatement(countQuery);
			stmt.setString(1, account);
			stmt.setInt(2, new Integer(lastsyncid));
			stmt.setInt(3, new Integer(thissyncid));

            if(maxevent!=null){
                stmt.setBigDecimal(4,new BigDecimal(maxevent));
            }else{
                stmt.setBigDecimal(4,null);
            }
            
            
			rs = stmt.executeQuery();
			rs.next();
			resp.size=rs.getInt(1);
			int maxEventsLimit=Integer.valueOf(Config.ebsPorperties.getProperty("maxEventsLimit"));
			if(resp.size>maxEventsLimit&&maxEventsLimit!=-1){//if MaxEventsLImit==-1,it means no limit. 
				//0201
                    			resp.addError("0201");
                    			return resp;
			}
			
		} finally {
			DBTool.CloseDBResultSet(rs);
			DBTool.CloseDBStatment(stmt);
		}
		
		
		try {
			
			stmt = con.prepareStatement(query);
			stmt.setString(1, account);
			stmt.setInt(2, new Integer(lastsyncid));
			stmt.setInt(3, new Integer(thissyncid));

            if(maxevent!=null){
                stmt.setBigDecimal(4,new BigDecimal(maxevent));
            }else{
                stmt.setBigDecimal(4,null);
            }
			rs = stmt.executeQuery();
			while (rs.next()) {
                        	resp.addRecord(rs.getString("id"),rs.getString("rectime"),
                                rs.getString("time"),rs.getString("hwid"),rs.getString("cssact"),
                                rs.getString("op"),rs.getString("fpth"),rs.getString("file_version"),
                               rs.getString("isfolder"),rs.getString("rslt"),rs.getString("syncid"),rs.getString("fsz"));
                       	
			}

		} finally {
			DBTool.CloseDBResultSet(rs);
			DBTool.CloseDBStatment(stmt);
		}
		
		
		
		return resp;
}catch(Throwable t){
	DBTool.recordLog("api_error",t.toString());
	DBTool.RollBackDBConnection(con);
//0008
	queryeventResponse r= new queryeventResponse();
	r.addError("0008",t);
	return r;
}finally{
			DBTool.CloseDBConnection(con);
}


}

	@Path("addevent")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_PLAIN)
	public addEventResponse addevent(@QueryParam("account") String account,
			@QueryParam("instanceid") String instanceid, @QueryParam("syncid") Integer syncid,
			@DefaultValue("true") @QueryParam("unlock") Boolean unlock, InputStream in) throws EBSException{
		//for fake response=====================================
		String fakeset=TESTAPI.fakeValue.get("addevent");
		if(fakeset!=null){
			try{
				addEventResponse r= new addEventResponse(); 	
				r.addError(fakeset);
				return r;
			}catch(Throwable t){
				DBTool.recordFakeResponseLog(t.toString());
			}
		}
		//======================================================
		
		
		
		Connection con = null;
try{

		addEventResponse eventResp = new addEventResponse();		
		if (DBTool.isEmpty(account)|| DBTool.isEmpty(instanceid)|| syncid == null) {
//error 0009
			eventResp.addError("0009");
			return eventResp;
		}
		
		
		
		QueueManager qm =qMap.getQueueManager(account);
		if (!qm.isLocked(instanceid)) {
			eventResp.message = "need to get lock fisrt";
			eventResp.unlockResponse.renewTime();
//error 0901
//need to get lock .
			eventResp.addError("0901");
			return eventResp;
		}
		eventResp.unlockResponse.unlockresult = false;
		PreparedStatement stmt = null;
		BufferedReader bin = null;
		con = DBTool.getConnection();
		
		
		try {

			String query = "insert into opt_log  values(DEFAULT,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			stmt = con.prepareStatement(query);
			bin = new BufferedReader(new InputStreamReader(in));
			String record = null;
			Calendar c = Calendar.getInstance();

			while ((record = bin.readLine()) != null) {

				String[] item = record.split("\t");
				//stmt.setString(1, UUID.randomUUID().toString());// id
				stmt.setTimestamp(1, new Timestamp(c.getTimeInMillis() - c.getTimeZone().getRawOffset()));// rectime
				stmt.setTimestamp(2, Timestamp.valueOf(item[0]));// time
				stmt.setString(3, item[1]);// cmpnm
				stmt.setString(4, item[2]);// hwid
				stmt.setString(5, item[3]);// OS
				stmt.setString(6, item[4]);// osact
				stmt.setString(7, item[5]);// cssact
				stmt.setString(8, item[6]);// clnip
				stmt.setString(9, item[7]);// srvip
				stmt.setString(10, item[8]);// sbxver
				stmt.setString(11, item[9]);// op
				stmt.setString(12, item[10]);// fpath
				stmt.setBigDecimal(13, new BigDecimal(item[11]));// fsz
				stmt.setString(14, item[12]);// file_version
				stmt.setString(15, item[13]);// is_Folder
				stmt.setInt(16, syncid);
				stmt.setString(17, item[14]);// rslt
				stmt.setString(18, "dummy");// inst_id
				stmt.execute();

			}


			if (unlock.booleanValue()) {
				try {
					eventResp.unlockResponse.unlockresult = qm.unlock(instanceid,Config.syncIdStatus.SUCC);
				} catch (Throwable e) {
//0902
//unlock fail.
					DBTool.RollBackDBConnection(con);
					eventResp.addError("0902");
					eventResp.isSuccess=false;
					return eventResp;
				}
			}
			eventResp.unlockResponse.renewTime();
			eventResp.isSuccess = true;
			return eventResp;

		} finally {
			DBTool.CloseReader(bin);
			DBTool.CloseDBStatment(stmt);
			
		}
}catch(Throwable t){
	DBTool.recordLog("api_error",t.toString());
	DBTool.RollBackDBConnection(con);
	addEventResponse r= new addEventResponse();
//0008
		r.addError("0008",t);
		return r;
}finally{
			DBTool.CloseDBConnection(con);
}
	}

	@Path("confirmsync")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public confirmsyncResponse confirmsync(@QueryParam("account") String account,
			@QueryParam("instanceid") String instanceid, @QueryParam("syncid") String syncid)
			throws EBSException{
		//for fake response=====================================
		String fakeset=TESTAPI.fakeValue.get("confirmsync");
		if(fakeset!=null){
			try{
				confirmsyncResponse r= new confirmsyncResponse(); 	
				r.addError(fakeset);
				return r;
			}catch(Throwable t){
				DBTool.recordFakeResponseLog(t.toString());
			}
		}
		//======================================================
		
try{

		confirmsyncResponse r = new confirmsyncResponse();
		if (DBTool.isEmpty(account)|| DBTool.isEmpty(instanceid)|| syncid == null) {
//error 0009	
			r.addError("0009");
			return r;
		}
		QueueManager qm=qMap.getQueueManager(account);
			r.syncResult = qMap.getQueueManager(account).confirmsync(new Long(syncid));
		return r;
		
		
}catch(Throwable t){
	DBTool.recordLog("api_error",t.toString());
	confirmsyncResponse r= new confirmsyncResponse();
//0008 
	r.addError("0008",t);
	return r;
}
	}

	@Path("querybucketinfo")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public BucketInfo querybucketinfo(@QueryParam("account") String account,
			@QueryParam("bucketname") String bucketname) throws EBSException {
		//for fake response=====================================  
		String fakeset=TESTAPI.fakeValue.get("querybucketinfo");
		if(fakeset!=null){
			try{
				BucketInfo  r= new BucketInfo (); 	
				r.addError(fakeset);
				return r;
			}catch(Throwable t){
				DBTool.recordFakeResponseLog(t.toString());
			}
		}
		//======================================================
		
		
		
		BucketInfo binf =null;
		Connection con = null;
		PreparedStatement stmt =null;
		ResultSet rs = null;
try{
		binf = new BucketInfo();
		if (DBTool.isEmpty(account)|| DBTool.isEmpty(bucketname)) {
			bucketname="bkt-"+account;
//error 0009
			//binf.addError("0009");
			//return binf;
		}

		String query = "select * from (select coalesce(sum(size),0)  total_usage from objects where bucket_name=?) a,(select cast(storage_max_bucket_size_mb as bigint)*1048576 max_size from walrus_info) b,(select * from buckets where bucket_name=?  and owner_id=?) c, (select count(*) as objectCount from objects where bucket_name=?  and content_type!='application/x-directory') d,(select count(*) as folderCount from objects where bucket_name=?  and content_type='application/x-directory') e where c.bucket_name is not null limit 1 ";
		String checkPermissionSql="select (allow_read and allow_read_acp and allow_write and allow_write_acp)||'' from ( select * from buckets where bucket_name=? ) b "+
		                           "left outer join bucket_has_grants g on b.bucket_id=g.bucket_id "+
		                           "left outer join grants t on g.grant_id=t.grant_id and t.user_id=?";
		
		
			con = DBTool.getConnection();
			
			try {
				stmt = con.prepareStatement(checkPermissionSql);
				
				stmt.setString(1, bucketname);
				stmt.setString(2,account);
				
				rs = stmt.executeQuery();
				
				
				if(!rs.next()){//counld not find this bucket
					//error 207
					BucketInfo r= new BucketInfo();
					r.addError("0402");
					return r;
				}
				
				
				if(!"true".equals(rs.getString(1))){//found bucket but with no permission.
						//error 208
						BucketInfo r= new BucketInfo();
						r.addError("0401");
						return r;
				}
				
				
				
		} finally {
				DBTool.CloseDBResultSet(rs);
				DBTool.CloseDBStatment(stmt);
		}
		
		
		
		
		try {
				stmt = con.prepareStatement(query);
				stmt.setString(1, bucketname);
				stmt.setString(2, bucketname);
				stmt.setString(3, account);
				stmt.setString(4, bucketname);
				stmt.setString(5, bucketname);

				rs = stmt.executeQuery();
			if (rs.next()) {
				binf.totalUsed = new Long(rs.getString(1));
				binf.maxSize = new Long(rs.getString(2));
				binf.owner_id = rs.getString("owner_id");
				binf.bucket_name=rs.getString("bucket_name");
				binf.objectCount = new Long(rs.getString("objectCount"));
				binf.folderCount=new Long(rs.getString("folderCount"));
				binf.isVersioning = rs.getString("versioning").equals("Disabled") ? false : true;
				if(binf.maxSize==0){
					binf.percentage=0d;
				}else{
					//value a of percentage means a%,and point value is round up to 1. 
					binf.percentage=(Math.ceil((binf.totalUsed*10000d/binf.maxSize)))/100d;
				}
				
			} else {
				binf.totalUsed = new Long(-1);
				binf.maxSize = new Long(-1);
				binf.owner_id = account;
				binf.objectCount = new Long(-1);
				binf.isVersioning = false;
				binf.percentage=-1d;
			}


		} finally {
				DBTool.CloseDBResultSet(rs);
				DBTool.CloseDBStatment(stmt);
		}

		return binf;
}catch(Throwable t){
	DBTool.recordLog("api_error",t.toString());
	DBTool.RollBackDBConnection(con);
//error 0008
	BucketInfo r= new BucketInfo();
	r.addError("0008",t);
	return r;
}finally{	
			DBTool.CloseDBConnection(con);
}

	}

	@Path("queryfolderinfo")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public FolderInfo queryfolderinfo(@QueryParam("account") String account,
			@QueryParam("bucketname") String bucketname, @QueryParam("foldername") String foldername)
			throws EBSException{
		//for fake response=====================================
		String fakeset=TESTAPI.fakeValue.get("queryfolderinfo");
		if(fakeset!=null){
			try{
				FolderInfo  r= new FolderInfo (); 	
				r.addError(fakeset);
				return r;
			}catch(Throwable t){
				DBTool.recordFakeResponseLog(t.toString());
			}
		}
		//======================================================	
		
		
		Connection con=null;
		PreparedStatement stmt=null;
		ResultSet rs=null;
try{
		FolderInfo folderinfo = new FolderInfo();
		if (DBTool.isEmpty(account)|| DBTool.isEmpty(bucketname) || DBTool.isEmpty(foldername)) {
//error 0009
			folderinfo.addError("0009");
			return folderinfo;
		}
		

		
		String subQuery="select buk.owner_id owner_id,buk.bucket_name,buk.bucket_size,(g1.allow_read and g1.allow_read_acp and g1.allow_write and g1.allow_write_acp)||'' bukgrant, "+
				"o.object_key,(g2.allow_read and g2.allow_read_acp and g2.allow_write and g2.allow_write_acp)||'' objgrant "+
				"from (select * from buckets where bucket_name=?) buk "+ 
				"LEFT OUTER JOIN  bucket_has_grants bg on buk.bucket_id=bg.bucket_id "+ 
				"LEFT OUTER JOIN grants g1 on bg.grant_id=g1.grant_id and g1.user_id=? "+

				"LEFT OUTER JOIN (select * from objects where object_key=? and content_type='application/x-directory') o  on o.bucket_name=buk.bucket_name "+

				"LEFT OUTER JOIN  object_has_grants og on o.object_id=og.object_id "+ 
				"LEFT OUTER JOIN grants g2 on og.grant_id=g2.grant_id and g2.user_id=? ";
		
		String folderCount="select count(*) subfoldernum from objects "+
                "where object_key like ? and bucket_name=?  and content_type='application/x-directory'",
       objCount="select count(*) subobjectnum,coalesce(sum(size),0) objsize from objects "+
               "where object_key like ? and bucket_name=?  and content_type<>'application/x-directory'";
		
		String totalQuery="select * from ("+subQuery+") a,("+folderCount+") b,("+objCount+") c";
		
		
		con=DBTool.getConnection();
		
		
		
		try{

			stmt = con.prepareStatement(totalQuery);
			stmt.setString(1,bucketname);
			stmt.setString(2,account);
			stmt.setString(3,foldername);
			stmt.setString(4,account);
			stmt.setString(5,foldername+"%");
			stmt.setString(6, bucketname);   
			stmt.setString(7,foldername+"%");
			stmt.setString(8, bucketname); 
			rs=stmt.executeQuery();
			
			if(rs.next()){
				if(!"true".equals(rs.getString("bukgrant"))){
//0501 no permission to access bucket
					folderinfo.addError("0501");
					return folderinfo;
					
				}else if(rs.getString("object_key")==null){
//0504 folder does not exist
					folderinfo.addError("0504");
					return folderinfo;
				}else if(!"true".equals(rs.getString("objgrant"))){
//0503	no permission to access folder				

					folderinfo.addError("0503");
					return folderinfo;
				}
				
				
				
					folderinfo.folderName = rs.getString("object_key");
					folderinfo.subfoldernum = rs.getLong("subfoldernum");
					folderinfo.fileNum = rs.getLong("subobjectnum");
					folderinfo.size = rs.getLong("objsize");
					folderinfo.buckinfo=new BucketInfo();
					folderinfo.buckinfo.owner_id = rs.getString("owner_id");   
					//folderinfo.buckinfo.maxSize = 50000l;
					//folderinfo.buckinfo.totalUsed = 1000l;
					//folderinfo.buckinfo.isVersioning = true;

					return folderinfo;
			}else{
//error 0502 bucket does not exist .
				folderinfo.addError("0502");
				return folderinfo;
				
			}
			
			
			
			
	
		}finally{
			DBTool.CloseDBResultSet(rs);
			DBTool.CloseDBStatment(stmt);
			
		}	
}catch(Throwable t){
	DBTool.recordLog("api_error",t.toString());
	DBTool.RollBackDBConnection(con);
	FolderInfo r= new FolderInfo();
	r.buckinfo=null;
//error 0008
	r.addError("0008",t);
	return r;
}finally{
	DBTool.CloseDBConnection(con);
}
	}

	@Path("queryuserinfo")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public UserInfo queryuserinfo(@QueryParam("account") String account) throws EBSException{
		//for fake response=====================================
		String fakeset=TESTAPI.fakeValue.get("queryuserinfo");
		if(fakeset!=null){
			try{
				UserInfo  r= new UserInfo (); 	
				r.addError(fakeset);
				return r;
			}catch(Throwable t){
				DBTool.recordFakeResponseLog(t.toString());
			}
		}
		//======================================================	
		
		
		
		Connection con = null;
		PreparedStatement stmt =null;
		ResultSet rs = null;
try{
		UserInfo uif = new UserInfo();
		if (account == null) {
//error 0009
			uif.addError("0009");
			return uif;
		}

		String query = "select a.user_name,b.auth_user_is_admin,a.user_email from users a,auth_users b where a.user_name=b.auth_user_name and a.user_name=?";
		con = DBTool.getConnection();
		
		try {
			stmt = con.prepareStatement(query);
			stmt.setString(1, account);

			rs = stmt.executeQuery();
			if (rs.next()) {
				uif.account = rs.getString(1);
				uif.isAdmin = (rs.getString(2).equals("t")) ? ("true") : ("false");
				uif.email=rs.getString(3);
			}

		} finally {
			DBTool.CloseDBResultSet(rs);
			DBTool.CloseDBStatment(stmt);
		}

		return uif;
}catch(Throwable t){
	DBTool.recordLog("api_error",t.toString());
	DBTool.RollBackDBConnection(con);
	UserInfo r= new UserInfo();
//error 0008
	r.addError("0008",t);
	return r;
}finally{
			DBTool.CloseDBConnection(con);
	
}
	}

	@Path("heartbeat")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public HeartbeatResponse heartbeat(@QueryParam("account") String account,
			@QueryParam("instanceid") String instanceId) throws EBSException{
		//for fake response=====================================
		String fakeset=TESTAPI.fakeValue.get("heartbeat");
		if(fakeset!=null){
			try{
				HeartbeatResponse  r= new HeartbeatResponse(); 	
				r.addError(fakeset);
				return r;
			}catch(Throwable t){
				DBTool.recordFakeResponseLog(t.toString());
			}
		}
		//======================================================	
		
		
try{		
		
		HeartbeatResponse hbr = new HeartbeatResponse();
		if (DBTool.isEmpty(account)|| DBTool.isEmpty(instanceId)) {
//error 0009
			hbr.addError("0009");
			return hbr;
		}
			hbr.heartbeatResult = qMap.getQueueManager(account).heartbeat(instanceId) ? "success" : "fail";


		if(!"success".equals(hbr.heartbeatResult)){
//error 0601			
			hbr.addError("0601");
		}
		
		return hbr;
}catch(Throwable t){
	DBTool.recordLog("api_error",t.toString());
//error 0008
	HeartbeatResponse r= new HeartbeatResponse();
	r.addError("0008",t);
	return r;
}

	}
	
	
	@Path("querysystemInfo")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public SystemInfo serverInfo() throws EBSException{
		//for fake response=====================================
		String fakeset=TESTAPI.fakeValue.get("querysystemInfo");
		if(fakeset!=null){
			try{
				SystemInfo  r= new SystemInfo(); 	
				r.addError(fakeset);
				return r;
			}catch(Throwable t){
				DBTool.recordFakeResponseLog(t.toString());
			}
		}
		//======================================================	
		
		
		
try{
		SystemInfo sysInfo = new SystemInfo();
		
		
		Calendar c = Calendar.getInstance();
		sysInfo.serverTime = new Timestamp(c.getTimeInMillis() - c.getTimeZone().getRawOffset()).toString();

		return sysInfo;
}catch(Throwable t){
	DBTool.recordLog("api_error",t.toString());
//error 0008
	SystemInfo r= new SystemInfo();
	r.addError("0008",t);
	return r;
}
	}

	
	//this private function is for unlock functoin below to identify status of lastsyncid.
	private String checkLastSyncidState(String account,String instanceid) throws SQLException, NamingException{
		String query="select  trim(status) from syncinfo where account=? and instancekey=? order by syncid desc limit 1";
		String state=null;
		Connection con=null;
		PreparedStatement stmt=null;
		ResultSet rs = null;
		try {
			con = DBTool.getWalrusConnection();
			stmt = con.prepareStatement(query);
			stmt.setString(1, account);
			stmt.setString(2, instanceid);
			rs = stmt.executeQuery();
			if (rs.next()) {
				state=rs.getString(1);
			}
		} finally {
			DBTool.CloseDBResultSet(rs);
			DBTool.CloseDBStatment(stmt);
			DBTool.CloseDBConnection(con);
		}
		return state;
	}
	
	@Path("unlock")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	public unLockResponse unlock(@QueryParam("account") String account,
			@QueryParam("instanceid") String instanceid,
			@DefaultValue("SUCCESS")  @QueryParam("result") String result) throws EBSException{

		//for fake response=====================================
		String fakeset=TESTAPI.fakeValue.get("unlock");
		if(fakeset!=null){
			try{
				unLockResponse  r= new unLockResponse(); 	
				r.addError(fakeset);
				return r;
			}catch(Throwable t){
				DBTool.recordFakeResponseLog(t.toString());
			}
		}
		//======================================================	
		
		
		
		
try{

		unLockResponse ulr = new unLockResponse();		
		if (DBTool.isEmpty(account)  || DBTool.isEmpty(instanceid)) {
//0009
			ulr.addError("0009");
			return ulr;
		}
		
		
		if("FAIL".equals(result)){result=Config.syncIdStatus.FAIL;}else{result=Config.syncIdStatus.SUCC;}
		QueueManager qm=qMap.getQueueManager(account);
		ulr.unlockresult = qm.unlock(instanceid,result);
			
			
		if(!ulr.unlockresult){
			String status=checkLastSyncidState(account,instanceid);

			if(Config.syncIdStatus.SUCC.equals(status)){
//error 0302
				ulr.addError("0302");
			}else if(Config.syncIdStatus.FAIL.equals(status)){
//error 0303
				ulr.addError("0303");	
			}else if(Config.syncIdStatus.SYNC_UPD.equals(status)){
//error 0304
				ulr.addError("0304");
			}else if(status==null){
				ulr.addError("0301");
			}
			
			
		}
		ulr.renewTime();
		return ulr;
		
		
}catch(Throwable t){
	DBTool.recordLog("api_error",t.toString());
//error 0008
	unLockResponse r= new unLockResponse();
	r.addError("0008",t);
	return r;
}
	}
	@Path("unlockV")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	public unLockResponse unlockV(@QueryParam("account") String account,
			@QueryParam("instanceid") String instanceid,
			@DefaultValue("SUCCESS")  @QueryParam("result") String result) throws EBSException{

		//for fake response=====================================
		String fakeset=TESTAPI.fakeValue.get("unlockV");
		if(fakeset!=null){
			try{
				unLockResponse  r= new unLockResponse(); 	
				r.addError(fakeset);
				return r;
			}catch(Throwable t){
				DBTool.recordFakeResponseLog(t.toString());
			}
		}
		//======================================================	
		
		
		
try{

		unLockResponse ulr = new unLockResponse();		
		if (DBTool.isEmpty(account) || DBTool.isEmpty(instanceid)) {
//0009
			ulr.addError("0009");
			return ulr;
		}
		
		
		if("FAIL".equals(result)){result=Config.syncIdStatus.FAIL;}else{result=Config.syncIdStatus.SUCC;}
		QueueManager qm=qMap.getQueueManager(account);
		ulr.unlockresult = qm.unlockV2(instanceid,result);
			
			
		if(!ulr.unlockresult){
			String status=checkLastSyncidState(account,instanceid);

			if(Config.syncIdStatus.SUCC.equals(status)){
//error 0302
				ulr.addError("0302");
			}else if(Config.syncIdStatus.FAIL.equals(status)){
//error 0303
				ulr.addError("0303");	
			}else if(Config.syncIdStatus.SYNC_UPD.equals(status)){
//error 0304
				ulr.addError("0304");
			}else if(status==null){
				ulr.addError("0301");
			}
			
			
		}
		ulr.renewTime();
		return ulr;
		
		
}catch(Throwable t){
	DBTool.recordLog("api_error",t.toString());
//error 0008
	unLockResponse r= new unLockResponse();
	r.addError("0008",t);
	return r;
}
}
	
	
	
	
	
	
	
	
	
	
	
	@Path("querylastsyncid")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public querylastsyncidResponse querylastsyncid(@QueryParam("account") String account,
			@QueryParam("instanceid") String instanceid

	) throws EBSException{
		//for fake response=====================================
		String fakeset=TESTAPI.fakeValue.get("querylastsyncid");
		if(fakeset!=null){
			try{
				querylastsyncidResponse  r= new querylastsyncidResponse(); 	
				r.addError(fakeset);
				return r;
			}catch(Throwable t){
				DBTool.recordFakeResponseLog(t.toString());
			}
		}
		//======================================================	
		
		

		Connection con = null;
try{
		querylastsyncidResponse r = new querylastsyncidResponse();
		
		
		if (DBTool.isEmpty(account)|| DBTool.isEmpty(instanceid)) {
//0009
			r.addError("0009");
			return r;
		}
		PreparedStatement stmt=null;
		ResultSet rs = null;

		con = DBTool.getWalrusConnection();
		try {
			String query = "select is_enabled from client_instances  where  user_id=?  and instance_key=? limit 1";
			stmt = con.prepareStatement(query);
			stmt.setString(1, account);
			stmt.setString(2, instanceid);
			rs = stmt.executeQuery();
			if (!rs.next()) {
//0001
//0002
				r.message = querylastsyncidResponse.CONS_INVALID_INSTANCEID;
				r.addError("0001");
				r.addError("0002");
				
				return r;
			}

		} finally {
			DBTool.CloseDBResultSet(rs);
			DBTool.CloseDBStatment(stmt);
		}
		try {// check if lastsyncid is exist. if not ,it's run away.

			// String
			// query="select max(a.syncid)  from opt_log a inner join syncInfo b on a.syncid=b.syncid where   cssact=? and instancekey=? and rslt='succ'";

			Integer lastsyncid = null;
			if ((lastsyncid = DBTool.getLastSuccessSyncId(con, account, instanceid)) == null) {
//
				//r.message = querylastsyncidResponse.CONS_NO_LAST_SYNCID;
				//r.addError("0003");
				r.result=false;
				r.lastsyncid="-1";
				return r;
			}

			r.lastsyncid = lastsyncid.toString();
			r.account = account;
			r.instanceid = instanceid;
			
		} finally {
			DBTool.CloseDBResultSet(rs);
			DBTool.CloseDBStatment(stmt);
		}

		return r;
}catch(Throwable t){
	DBTool.recordLog("api_error",t.toString());
	DBTool.RollBackDBConnection(con);
	querylastsyncidResponse r= new querylastsyncidResponse();
//0008
	r.addError("0008",t);
	return r;
}finally{ 
			DBTool.CloseDBConnection(con);
}
	}

	@Path("renameobject")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_PLAIN)
	public RenameObjectResponse renameObject(@QueryParam("account") String account,
			@QueryParam("instanceid") String instanceid, @QueryParam("syncid") Integer syncid,
			@QueryParam("src_object") String src_object, @QueryParam("dest_object") String dest_object,
			@QueryParam("bucket_name") String bucket_name) throws EBSException{
		//for fake response=====================================
		String fakeset=TESTAPI.fakeValue.get("renameobject");
		
		if(fakeset!=null){
			try{
				RenameObjectResponse  r= new RenameObjectResponse();   
				r.addError(fakeset);
				return r;
			}catch(Throwable t){
				DBTool.recordFakeResponseLog(t.toString());
			}
		}
		//======================================================	
		
		
		

        RenameAPI r = new RenameAPI();
        return r.renameObject(account, instanceid, syncid, src_object, dest_object, bucket_name);
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
	
	@Path("queryversion")
	@GET
        @Produces(MediaType.APPLICATION_JSON)
        public queryVersionResponse queryVersion() throws EBSException{
		//for fake response=====================================
		String fakeset=TESTAPI.fakeValue.get("queryversion");
		
		if(fakeset!=null){
			try{
				queryVersionResponse  r= new queryVersionResponse();   
				r.addError(fakeset);
				return r;
			}catch(Throwable t){
				DBTool.recordFakeResponseLog(t.toString());
			}
		}
		//======================================================
		
		
		
		
		
try{
        		String versionFilePath = System.getProperty("catalina.home")+"/webapps/safebox/downloadKit/version.txt";

                queryVersionResponse r=new queryVersionResponse();
                
                
                
                BufferedReader bfr=null;
                try {
                        bfr=new BufferedReader(new FileReader(new File(versionFilePath)));
                        String str=null;
                        while((str=bfr.readLine())!=null){
                                if(str.contains("safeboxVersion=")){
                                        String[] strA=str.split("=");
                                        if(strA!=null&&strA.length>=2){
                                                r.version=strA[1];
                                        }
                                }
                        }
                        
                        
                        
                }finally{ 
                	DBTool.CloseReader(bfr);
                }
                return r;
}catch(Throwable t){

	DBTool.recordLog("api_error",t.toString());
//0008
	queryVersionResponse r= new queryVersionResponse();
	r.addError("0008",t);
	return r;
}

    }

	@Path("listobjects")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ObjectList listObjects(
			@QueryParam("bucket") String bucket,
			@QueryParam("folder") String folder,
			@DefaultValue("1") @QueryParam("page") int page,
			@DefaultValue("10") @QueryParam("pagesize") int  pagesize
	) throws EBSException{

		//for fake response=====================================   
		String fakeset=TESTAPI.fakeValue.get("listobjects");
		if(fakeset!=null){
			try{
				ObjectList  r= new ObjectList();   
				r.addError(fakeset);
				return r;
			}catch(Throwable t){
				DBTool.recordFakeResponseLog(t.toString());
			}
		}
		//======================================================
		
		
		
		
		
		Connection con=null;
try{		
		ObjectList l=new ObjectList();
		
		l.pagingInfo.bucket=bucket;
		l.pagingInfo.page=page;l.pagingInfo.pagesize=pagesize;
		if(folder==null){folder="";}
		if(!folder.endsWith("/")){folder=folder+"/";}
		l.pagingInfo.folder=folder;
		
		
		
		
		String regularfolder=folder.replace("*","\\*").replace("?","\\?").replace("+","\\+");
		
		
		
		if(page<=0||pagesize<=0){
			l.errorMessage="page or pagesize can not be in negtive.";
//0701
			l.addError("0701");
			return l;
		}else if(bucket==null){
			l.errorMessage="must specify bucket parameter.";
//0702
			l.addError("0702");
			return l;
		}
		PreparedStatement stmt=null;
		ResultSet rs=null;
		String query1="select ceil(count(*)/?),count(*) from objects where bucket_name=? and (object_key ~ ? and object_key !~ ?) ",
			   query2="select * from objects where bucket_name=? and (object_key ~ ? and object_key !~ ?) order by content_type='application/x-directory' desc, object_key asc",
			   countQuery="select count(*) from (" + query2 + ") query";
		
				con=DBTool.getConnection();

			
			
			try{
				stmt = con.prepareStatement(query1);
				stmt.setDouble(1,pagesize);
				stmt.setString(2,bucket);
				stmt.setString(3,"^"+regularfolder+"(.+)");
				stmt.setString(4,"^"+regularfolder+"(.*)/(.+)");
				rs=stmt.executeQuery();
				rs.next();
				l.pagingInfo.totalpages=rs.getInt(1);
				l.pagingInfo.totalobjects=rs.getInt(2);

			
			
				if(l.pagingInfo.totalpages==0){
					l.errorMessage="No object in this bucket.";
					return l;
				
				}else if(page>l.pagingInfo.totalpages){
					l.errorMessage="out of page range,page should from 1 to "+l.pagingInfo.totalpages;
//0703
					l.addError("0703");
					return l;
				} 
				
				
			}finally{
				DBTool.CloseDBResultSet(rs);
				DBTool.CloseDBStatment(stmt);
			}
			
			try{
				
				stmt = con.prepareStatement(query2);
				stmt.setString(1,bucket);
				stmt.setString(2,"^"+regularfolder+"(.+)");
				stmt.setString(3,"^"+regularfolder+"(.*)/(.+)");
				
				
				rs=stmt.executeQuery();
				
				for(int i=0;i<(page-1)*pagesize;i++){
					if(!rs.next())break;
				}
				
				
				for (int i = 0;i < pagesize;i++){
					if (!rs.next()) {
						break;
					}
					
					S3Object obj = new S3Object();
					obj.objectId = rs.getString("object_key").replace("+", " ");
					obj.objectName = obj.objectId.replace(l.pagingInfo.folder,"");
					obj.etagId = rs.getString("etag");      
					obj.contentType = rs.getString("content_type");
					obj.objectSize = rs.getString("size");
					obj.modifiedDate = rs.getString("last_modified");
					obj.bucketName = rs.getString("bucket_name");
					obj.objectUUID = "";

					if (obj.contentType.equals("application/x-directory")) {
						obj.type = "0";
						obj.clickAction = "pages/files?bucket=" + obj.bucketName + "&folder=" + obj.objectId;
					}
					
					else {
						obj.type = "1";
						obj.clickAction = "get/" + obj.bucketName + obj.objectId;
					}
					
					l.objBeanList.add(obj);
				}

			} finally{
				DBTool.CloseDBResultSet(rs);
				DBTool.CloseDBStatment(stmt);
			}
			try{
				
				stmt = con.prepareStatement(countQuery);
				stmt.setString(1,bucket);
				stmt.setString(2,"^"+regularfolder+"(.+)");
				stmt.setString(3,"^"+regularfolder+"(.*)/(.+)");
				
				
				rs=stmt.executeQuery();
				rs.next();
				l.size=rs.getInt(1);
				
				

			} finally{
				DBTool.CloseDBResultSet(rs);
				DBTool.CloseDBStatment(stmt);
			}
			
		return l;

}catch(Throwable t){
	DBTool.recordLog("api_error",t.toString());
	DBTool.RollBackDBConnection(con);
//0008
	ObjectList r= new ObjectList();
	r.addError("0008",t);
	return r;
}finally{
				DBTool.CloseDBConnection(con);
}
	}





	
	
	
	@Path("queryreadsyncid")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public querylastsyncidResponse queryreadsyncid(@QueryParam("account") String account,
			@QueryParam("instanceid") String instanceid

	) throws EBSException{

		//for fake response=====================================  
		String fakeset=TESTAPI.fakeValue.get("queryreadsyncid");
		if(fakeset!=null){
			try{
				querylastsyncidResponse  r= new querylastsyncidResponse();           
				r.addError(fakeset);
				return r;
			}catch(Throwable t){
				DBTool.recordFakeResponseLog(t.toString());
			}
		}
		//======================================================
		
		

		Connection con = null;
try{

		querylastsyncidResponse r = new querylastsyncidResponse();
		if (DBTool.isEmpty(account)|| DBTool.isEmpty(instanceid)) {
//0009
			r.addError("0009");
			return r;
		}
		PreparedStatement stmt =null;
		ResultSet rs = null;
		

			con = DBTool.getWalrusConnection();
		
		try {
			String query = "select is_enabled from client_instances  where  user_id=?  and instance_key=? limit 1";
			stmt = con.prepareStatement(query);
			stmt.setString(1, account);
			stmt.setString(2, instanceid);
			rs = stmt.executeQuery();

			if (!rs.next()) {
//0001 
//0002
				r.message = querylastsyncidResponse.CONS_INVALID_INSTANCEID;
				r.addError("0001");
				r.addError("0002");
				return r;
			}

		} finally {
			DBTool.CloseDBResultSet(rs);
			DBTool.CloseDBStatment(stmt);
		}
		try {// check if lastsyncid is exist. if not ,it's run away.
			// String
			// query="select max(a.syncid)  from opt_log a inner join syncInfo b on a.syncid=b.syncid where   cssact=? and instancekey=? and rslt='succ'";

			Integer lastsyncid = null;
			if ((lastsyncid = DBTool.getLastSuccessSyncId(con, account)) == null) {
//0003			
				r.message = querylastsyncidResponse.CONS_NO_LAST_SYNCID;
				r.addError("0003");
				return r;
			}

			r.lastsyncid = lastsyncid.toString();
			r.account = account;
			r.instanceid = instanceid;

		}finally {
			DBTool.CloseDBResultSet(rs);
			DBTool.CloseDBStatment(stmt);
		}

		return r;
}catch(Throwable t){
	DBTool.recordLog("api_error",t.toString());
	DBTool.RollBackDBConnection(con);
	querylastsyncidResponse r= new querylastsyncidResponse();
//0008
	r.addError("0008",t);
	return r;
}finally{
	DBTool.CloseDBConnection(con);
}
	}
	
	@Path("xxx")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteFolder(){
		
		
		
		return null;
	}


	@Path("queryreadevent")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public queryeventResponse queryreadevent(@QueryParam("account") String account,
			@QueryParam("instanceid") String instanceid, @QueryParam("readsyncid") String readsyncid,
			@QueryParam("lastsyncid") String lastsyncid, @QueryParam("maxevent") Integer maxevent)
			throws EBSException{

		//for fake response=====================================
		String fakeset=TESTAPI.fakeValue.get("queryreadevent");
		
		if(fakeset!=null){
			try{
				queryeventResponse  r= new queryeventResponse();           
				r.addError(fakeset);
				return r;
			}catch(Throwable t){
				DBTool.recordFakeResponseLog(t.toString());
			}
		}
		//======================================================
		
		
		Connection con = null;
try{

		queryeventResponse resp = new queryeventResponse();
		
		
		
		if (DBTool.isEmpty(account)|| DBTool.isEmpty(instanceid)|| DBTool.isEmpty(readsyncid)|| DBTool.isEmpty(lastsyncid) ) {
//0009	

		    resp.addError("0009");
			return resp;
		}

		String query = "select id,rectime,time,hwid,cssact,op,fpth,file_version,isfolder,rslt,syncId from opt_log where cssact = ? and rslt='succ' and syncid>?  and syncid<=? order by syncid, fpth asc limit ?";

		PreparedStatement stmt=null;
		ResultSet rs = null;
		try {
			con = DBTool.getConnection();
			
			stmt = con.prepareStatement(query);
			stmt.setString(1, account);
			stmt.setInt(2, new Integer(lastsyncid));
			stmt.setInt(3, new Integer(readsyncid));

                        if(maxevent!=null){
                                stmt.setBigDecimal(4,new BigDecimal(maxevent));
                        }else{
                                stmt.setBigDecimal(4,null);
                        }



			rs = stmt.executeQuery();
			while (rs.next()) {
                        	resp.addRecord(rs.getString("id"),rs.getString("rectime"),
                                rs.getString("time"),rs.getString("hwid"),rs.getString("cssact"),
                                rs.getString("op"),rs.getString("fpth"),rs.getString("file_version"),
                                rs.getString("isfolder"),rs.getString("rslt"),rs.getString("syncid"),rs.getString("fsz"));
			}


		}finally {
			DBTool.CloseDBResultSet(rs);
			DBTool.CloseDBStatment(stmt);
			DBTool.CloseDBConnection(con);

		}
		return resp;
}catch(Throwable t){
	DBTool.recordLog("api_error",t.toString());
	DBTool.RollBackDBConnection(con);
	queryeventResponse r= new queryeventResponse();
//0008 
	r.addError("0008",t);
	return r;
}
	}
	
	public static void main(String args[]) {
		System.out.println("test");

	}
	
	
	@Path("queryeventsV")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public queryeventResponse queryeventV(@QueryParam("account") String account,
			@QueryParam("instanceid") String instanceid,@DefaultValue("") @QueryParam("fromsyncid") String fromsyncid,
			@DefaultValue("") @QueryParam("tosyncid") String tosyncid) throws EBSException{


		//for fake response=====================================
		String fakeset=TESTAPI.fakeValue.get("queryeventsV");
		
		if(fakeset!=null){
			try{
				queryeventResponse  r= new queryeventResponse();           
				r.addError(fakeset);
				return r;
			}catch(Throwable t){
				DBTool.recordFakeResponseLog(t.toString());
			}
		}
		//======================================================
		
		
		
		queryeventResponse resp = new queryeventResponse();
		
		if (DBTool.isEmpty(account)|| DBTool.isEmpty(instanceid) || DBTool.isEmpty(fromsyncid)&&DBTool.isEmpty(tosyncid)) {
			//error 0009
			resp.addError("0009");
			return resp;
		}
		
		Connection con = null;
		String query = "select id,rectime,time,hwid,cssact,op,fpth,file_version,fsz,isfolder,rslt,e.syncId ,sy.instancekey "+ 
						"from opt_log e Inner join syncinfo sy on e.syncid=sy.syncid Where "+
						"cssact = ? and rslt='succ' and  instancekey<>  ?"+
						"and  (e.syncid> ?  Or ? is null) "+ 
						"and  (e.syncid< ?  Or ? is null) "+
						"order by e.syncid, fpth asc",
			   countQuery="select count(*) from ("+query+") query";
		
try{
		
		
		QueueManager qm =qMap.getQueueManager(account);
		if (!qm.isLocked(instanceid)) {
//error 0202
			resp.message = "need to get lock first";
			resp.addError("0202");
			return resp;
		}
		con = DBTool.getConnection();
		PreparedStatement stmt =null;
		ResultSet rs = null;
		

				
		try {
			
			stmt = con.prepareStatement(countQuery);
			stmt.setString(1, account);
			stmt.setString(2,instanceid);
			if(!DBTool.isEmpty(fromsyncid)){
				stmt.setInt(3, new Integer(fromsyncid));
				stmt.setInt(4, new Integer(fromsyncid));
			}else{
				stmt.setNull(3,java.sql.Types.INTEGER);
				stmt.setNull(4,java.sql.Types.INTEGER);
			}
			

			if(!DBTool.isEmpty(tosyncid)){
				stmt.setInt(5, new Integer(tosyncid));
				stmt.setInt(6,new Integer(tosyncid));
			}else{

				stmt.setNull(5,java.sql.Types.INTEGER);
				stmt.setNull(6,java.sql.Types.INTEGER);
				
			}
			
			
			
            
			rs = stmt.executeQuery();
			rs.next();
			resp.size=rs.getInt(1);
			
			int maxEventsLimit=Integer.valueOf(Config.ebsPorperties.getProperty("maxEventsLimit"));
			if(resp.size>maxEventsLimit&&maxEventsLimit!=-1){//if MaxEventsLImit==-1,it means no limit.
				//0201
                    			resp.addError("0201");
                    			return resp;
			}
			
		} finally {
			DBTool.CloseDBResultSet(rs);
			DBTool.CloseDBStatment(stmt);
		}
		
		
		try {
			
			stmt = con.prepareStatement(query);
			stmt.setString(1, account);
			stmt.setString(2,instanceid);
			
			if(!DBTool.isEmpty(fromsyncid)){
				stmt.setInt(3, new Integer(fromsyncid));
				stmt.setInt(4, new Integer(fromsyncid));
			}else{
				stmt.setNull(3,java.sql.Types.INTEGER);
				stmt.setNull(4,java.sql.Types.INTEGER);
			}
			

			if(!DBTool.isEmpty(tosyncid)){
				stmt.setInt(5, new Integer(tosyncid));
				stmt.setInt(6,new Integer(tosyncid));
			}else{

				stmt.setNull(5,java.sql.Types.INTEGER);
				stmt.setNull(6,java.sql.Types.INTEGER);
				
			}

			
			
			
			
			rs = stmt.executeQuery();
			while (rs.next()) {
                        	resp.addRecord(rs.getString("id"),rs.getString("rectime"),
                                rs.getString("time"),rs.getString("hwid"),rs.getString("cssact"),
                                rs.getString("op"),rs.getString("fpth"),rs.getString("file_version"),
                               rs.getString("isfolder"),rs.getString("rslt"),rs.getString("syncid"),rs.getString("fsz"));
                       	
			}

		} finally {
			DBTool.CloseDBResultSet(rs);
			DBTool.CloseDBStatment(stmt);
		}
		
		
		
		return resp;
}catch(Throwable t){
	DBTool.recordLog("api_error",t.toString());
	DBTool.RollBackDBConnection(con);
//0008
	queryeventResponse r= new queryeventResponse();
	r.addError("0008",t);
	return r;
}finally{
			DBTool.CloseDBConnection(con);
}


}
	
	
	
	
	
	@Path("syncrequestV")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	public syncRequestResponse syncrequestV(@QueryParam("account") String account,
			@QueryParam("instanceid") String instanceid

	) throws EBSException{

		//for fake response=====================================  
		String fakeset=TESTAPI.fakeValue.get("syncrequestV");
		
		if(fakeset!=null){
			try{
				syncRequestResponse  r= new syncRequestResponse();         
				r.addError(fakeset);
				return r;
			}catch(Throwable t){
				DBTool.recordFakeResponseLog(t.toString());
			}
		}
		//======================================================
		
		
		
		
try{
		syncRequestResponse resp =null;
		
		
		resp = new syncRequestResponse();
		if (DBTool.isEmpty(account)|| DBTool.isEmpty(instanceid)) {
//0009
			resp.addError("0009");
			return resp;
		}

		QueueManager qm = qMap.getQueueManager(account);
		boolean getlock =false;
		try{
			getlock=qm.getLockV2(instanceid);
		}catch(Throwable t){
//0008
			resp.addError("0101");
			return resp;
		}		
		// prepare response
		resp.syncRequestResult = getlock;
		if (resp.syncRequestResult) {
			resp.syncId = qm.getsyncid();
		}else{
//0101
			resp.addError("0101");
			
		}
		resp.systeminfo = new SystemInfo();
		resp.systeminfo.serverTime = sdf.format(DBTool.getUTC0());
		// --prepare response

	
	
	
		return resp;
}catch(Throwable t){
		DBTool.recordLog("api_error",t.toString());
		syncRequestResponse r= new syncRequestResponse();
		r.addError("0008",t);
		return r;
}	
		
	}
	
	
	
	
	
	
}
