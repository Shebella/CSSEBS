package lib;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.naming.NamingException;

public class QueuesMap {
	private Map<String,QueueManager> map=new HashMap<String,QueueManager>();
	
	
	
	public synchronized QueueManager getQueueManager(String account) throws SQLException, NamingException{
		QueueManager qm=map.get(account);
		if(qm==null){
			qm=new QueueManager(account);
			map.put(account,qm);
		}
		
		
		return qm;
	}
	
	public synchronized void cleanUp(){
		Iterator<QueueManager> it=map.values().iterator();
		
		
		while(it.hasNext()){
			QueueManager qm=it.next();
			qm.cleanUp();
			this.map.remove(qm);
		}
		
		
	}

}
