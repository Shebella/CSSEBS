package lib;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Queue;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.naming.NamingException;
import javax.ws.rs.QueryParam;

public class QueueManager {
	
	
	private String account;
	private long syncid=-1;
	private long timestamp=System.currentTimeMillis();
	
	private ConcurrentLinkedQueue<qRecord> q;
	public long getTimeStamp(){
		return this.timestamp;
	}
 	
	public Queue getQueue(){
		return this.q;
	}
	
	
	public void cleanUp(){
		this.q.clear();
	}
	
	
	public QueueManager(String account) throws SQLException, NamingException{
		synchronized(this){
			
			syncIdDB sDb=new syncIdDB();
			this.q=new ConcurrentLinkedQueue<qRecord>();
			this.account=account;
			
			String str=sDb.getCurrentLockerHandler(this.account);
			if(str!=null){
				String[] strA=str.split("\t");
				this.syncid=new Integer(strA[0]).longValue();
				qRecord rsdRc=new qRecord(strA[1]);
				this.q.add(rsdRc);
				this.renewTimestamp();
			}
		
		
		
		}
	}
	

	
	
	
	
	public synchronized boolean heartbeat(String instanceId){
		qRecord r=new qRecord(instanceId),h=q.peek();
		if(h!=null&&h.equals(r)){
			this.renewTimestamp();
			return true;
		}else{
			
			
			
			
			return false;	
		}
	}

	public synchronized boolean isLocked(String inst_id){
		if(this.q.isEmpty())return false;
		return this.q.peek().instanceId.equals(inst_id);
	}
	
	
	
	public synchronized boolean getLock(String instanceKey) throws Throwable{
		
		
		qRecord r=new qRecord(instanceKey);
		if(!q.contains(r)){
			q.add(r);
			if(q.peek().equals(r)){  //caller get Lock after be put into an empty queue.
				try{    //try to getsyncId
					this.syncid=Long.valueOf(QueueManager.syncIdDB.getNewSyncId(account, instanceKey,Config.syncIdStatus.LOCK,DBTool.getUTC0()));
					this.renewTimestamp();
				}catch(Throwable t){
					q.poll();//after q.poll(),queue should be empty.
					throw t;
				}
			}
		
		}
		
		if(q.peek().equals(r)){//caller already get lock early 
			this.renewTimestamp();
			return true;
		}else if(!this.isTimeout()){//caller must wait ,
			return false;
		}else{//timeout another one , next one may be caller or the other one.
			//this.doPoll(Config.syncIdStatus.FAIL);//time out someone  
			this.doPollafterCheck();//auto check and determine status.
			return q.peek().equals(r);
		}
	}
	public synchronized boolean getLockV2(String instanceKey) throws Throwable{
		
		
		qRecord r=new qRecord(instanceKey);
		if(!q.contains(r)){
			q.add(r);
			if(q.peek().equals(r)){  //caller get Lock after be put into an empty queue.
				try{    //try to getsyncId
					this.syncid=Long.valueOf(QueueManager.syncIdDB.getNewSyncId(account, instanceKey,Config.syncIdStatus.LOCK,DBTool.getUTC0()));
					this.renewTimestamp();
				}catch(Throwable t){
					q.poll();//after q.poll(),queue should be empty.
					throw t;
				}
			}
		
		}
		
		if(q.peek().equals(r)){//caller already get lock early 
			this.renewTimestamp();
			return true;
		}else if(!this.isTimeout()){//caller must wait ,
			return false;
		}else{//timeout another one , next one may be caller or the other one.
			//this.doPoll(Config.syncIdStatus.FAIL);//time out someone  
			this.doPollafterCheckV2();//auto check and determine status.
			return q.peek().equals(r);
		}
	}
	
	
	
	public synchronized String confirmsync(long syncid) throws SQLException, NamingException{
		syncIdDB sDb=new syncIdDB();
		try {
			return syncIdDB.checkSyncResult(syncid);
		} catch (SQLException e) {
			throw e;
		}
	}
	
	
	public synchronized boolean unlock(String instanceId,String result) throws Throwable{
		qRecord h=q.peek();
		if(h==null||!h.instanceId.equals(instanceId))return false;
		
		if(Config.syncIdStatus.SUCC.equals(result)){
			this.doPoll(result);
		}else{
			this.doPollafterCheck();//auto check and determine status.
		}
			return true;
	}
	
	
	public synchronized boolean unlockV2(String instanceId,String result) throws Throwable{
		qRecord h=q.peek();
		if(h==null||!h.instanceId.equals(instanceId))return false;
		
		if(Config.syncIdStatus.SUCC.equals(result)){
			this.doPoll(result);
		}else{
			this.doPollafterCheck();//auto check and determine status.
		}
			return true;
	}
	
	
	
	private void renewTimestamp(){this.timestamp=System.currentTimeMillis();}
	
	private void doPollafterCheck() throws SQLException, NamingException{
			String pollStatus="";
		
		
		while(true){
			//set this lock holder this pollStatus(SUCCESS or FAIL) than poll it.
			syncIdDB.setSyncIdStatusAfterCheck(this.account,this.syncid);
			q.poll();
			
			
			if(q.isEmpty()){break;}
			
			qRecord h=q.peek();
			try {
				this.syncid=Long.valueOf(syncIdDB.getNewSyncId(this.account, h.instanceId,Config.syncIdStatus.LOCK,DBTool.getUTC0()));
				this.renewTimestamp();
			}catch(Throwable t){
				continue;
			}
			
			return;
		}
		
	}
	private void doPollafterCheckV2() throws SQLException, NamingException{
		String pollStatus="";
	
	
	while(true){
		//set this lock holder this pollStatus(SUCCESS or FAIL) than poll it.
		syncIdDB.setSyncIdStatusAfterCheckV2(this.account,this.syncid);
		q.poll();
		
		
		if(q.isEmpty()){break;}
		
		qRecord h=q.peek();
		try {
			this.syncid=Long.valueOf(syncIdDB.getNewSyncId(this.account, h.instanceId,Config.syncIdStatus.LOCK,DBTool.getUTC0()));
			this.renewTimestamp();
		}catch(Throwable t){
			continue;
		}
		
		return;
	}
	
}
	
	
	
	private void doPoll(String pollStatus) throws SQLException, NamingException{

		
		
		while(true){
			//set this lock holder this pollStatus(SUCCESS or FAIL) than poll it.
			syncIdDB.setSyncIdStatus(this.account,this.syncid,pollStatus);
			q.poll();
			
			
			if(q.isEmpty()){break;}
			
			qRecord h=q.peek();
			try {
				this.syncid=Long.valueOf(syncIdDB.getNewSyncId(this.account, h.instanceId,Config.syncIdStatus.LOCK,DBTool.getUTC0()));
				this.renewTimestamp();
			}catch(Throwable t){
				continue;
			}
			
			return;
		}
	}
	private boolean isTimeout(){
		return (System.currentTimeMillis()-timestamp>Long.valueOf(lib.Config.ebsPorperties.getProperty("timeoutValueinmillSecond")));
		
	}
	
	
	
	
	public synchronized long getsyncid(){return this.syncid;}
	
	
	public String getQueueAccount(){return this.account;}
	
	
	private static class qRecord{
		String instanceId;
		
		public qRecord(String instanceId){this.instanceId=instanceId;}
		
		public boolean equals(Object obj){
			if(!(obj instanceof qRecord))return false;
			qRecord r=(qRecord) obj;
			if(r.instanceId==null)return false;
			
			
			
			return r.instanceId.equals(this.instanceId);
			
		}
		
		public int hashCode(){
			return this.instanceId.hashCode();
		}
		
		public String toString(){return instanceId;}
		
	}

	
	
	private static class syncIdDB{
		
		public static void setSyncIdStatusAfterCheck(String account,long syncId) throws SQLException, NamingException{
			
			Connection con=DBTool.getConnection();
			con.setAutoCommit(false);
			
			ResultSet rs=null;
			try {
				//to be  in   transaction  
				PreparedStatement stmt=con.prepareStatement("update syncInfo set status="+
						"(select case when(select syncid from opt_log where syncid=syncInfo.syncid limit 1) is NOT NULL then '"+Config.syncIdStatus.SYNC_UPD +"' else '"+Config.syncIdStatus.FAIL+"' end)"+
						" where account=? and syncid=? and status='"+Config.syncIdStatus.LOCK+"'");
				stmt.setString(1,account);
				stmt.setInt(2,(int)syncId);
				
				stmt.executeUpdate();
			} catch (SQLException e) {
				con.rollback();
				throw e;
			}finally{
				try {rs.close();} catch (Throwable e) {}
				DBTool.closeConnection(con);
				
			}
			
			
			
			
		}
		
		public static void setSyncIdStatusAfterCheckV2(String account,long syncId) throws SQLException, NamingException{
			//for EBS API syncrequestV
			
			
			Connection con=DBTool.getConnection();
			con.setAutoCommit(false);
			
			ResultSet rs=null;
			try {
				//to be  in   transaction  
				PreparedStatement stmt=con.prepareStatement("update syncInfo set status="+
						"(select case when(select syncid from opt_log where syncid=syncInfo.syncid limit 1) is NOT NULL then '"+Config.syncIdStatus.SUCC +"' else '"+Config.syncIdStatus.FAIL+"' end)"+
						" where account=? and syncid=? and status='"+Config.syncIdStatus.LOCK+"'");
				stmt.setString(1,account);
				stmt.setInt(2,(int)syncId);
				
				stmt.executeUpdate();
			} catch (SQLException e) {
				con.rollback();
				throw e;
			}finally{
				try {rs.close();} catch (Throwable e) {}
				DBTool.closeConnection(con);
				
			}
			
			
			
			
		}
		
		public static void setSyncIdStatus(String account,long syncId,String status) throws SQLException, NamingException{
			Connection con=DBTool.getConnection();
			con.setAutoCommit(false);
			
			ResultSet rs=null;
			try {
				//to be  in   transaction  
				PreparedStatement stmt=con.prepareStatement("update syncInfo set status=? where account=? and syncid=? and status='"+Config.syncIdStatus.LOCK+"'");
				stmt.setString(1,status);
				stmt.setString(2,account);
				stmt.setInt(3,(int)syncId);
				
				stmt.executeUpdate();
			} catch (SQLException e) {
				con.rollback();
				throw e;
			}finally{
				try {rs.close();} catch (Throwable e) {}
				DBTool.closeConnection(con);
				
			}
			
			
			
		}
		
		
		public static String getCurrentLockerHandler(String account) throws SQLException, NamingException{
			

			Connection con=DBTool.getConnection();
			con.setAutoCommit(false);
			String str=null;
			
			
			ResultSet rs=null;
			try {
				PreparedStatement stmt=con.prepareStatement("select syncId,instancekey from syncInfo where account=? and status='"+Config.syncIdStatus.LOCK+"' order by createTime asc  limit 1");
				stmt.setString(1,account);
				rs=stmt.executeQuery();
				
				while(rs.next()){
					
					str=rs.getString(1)+"\t"+rs.getString(2);
				}
				
				
			} catch (SQLException e) {
				throw e;
			}finally{
				try {rs.close();} catch (Throwable e) {}
				DBTool.closeConnection(con);
				
			}
			
			return str;
			
		}
		
		public static String getNewSyncId(String account,String instanceKey,String status,Timestamp createTime) throws SQLException, NamingException{
			Connection con=DBTool.getConnection();
			con.setAutoCommit(false);
			String syncId="";
			
			
			ResultSet rs=null;
			try {
				//to be  in   transaction  
				PreparedStatement stmt=con.prepareStatement("insert into syncInfo(account,instanceKey,status,createTime) values(?,?,?,?)");
				stmt.setString(1,account);
				stmt.setString(2,instanceKey);
				stmt.setString(3,status);
				stmt.setTimestamp(4,createTime);
				stmt.execute();
				
				stmt=con.prepareStatement("select syncId from syncInfo order by createTime desc limit 1");
				rs=stmt.executeQuery();
				rs.next();
				syncId=rs.getString(1);
				
				
				
				//=====================================
				//take Safebox web's event record.
				stmt=con.prepareStatement("update opt_log set syncid=? where  coalesce(syncid,-100)= -100");//use function index instead of is null condition.
				stmt.setInt(1,new Integer(syncId));
				stmt.execute();
				//===========================================
				
				
			} catch (SQLException e) {
				con.rollback();
				throw e;
			}finally{
				try {rs.close();} catch (Throwable e) {}
				DBTool.closeConnection(con);
				
			}
			
			return syncId;
		}
		
		
		
		public static String checkSyncResult(long syncid) throws SQLException, NamingException{
			String str="";
			
			Connection con=DBTool.getConnection();
			con.setAutoCommit(false);
			ResultSet rs=null;
			try {
				//to be  in   transaction  
				PreparedStatement stmt=con.prepareStatement("select status from syncInfo where syncid=?");
				stmt.setLong(1,syncid);
				
				
				
				rs=stmt.executeQuery();
				if(rs.next()){
					
					
					str=rs.getString(1);
				}
			} catch (SQLException e) {
				
				
				throw e;
			}finally{
				try {rs.close();} catch (Throwable e) {}
				DBTool.closeConnection(con);
				
			}
			
			return str.replace(" ","");
			
			
		}
		
		
		
	}
	
	
}
