package rest.representation;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class queryeventResponse  extends BaseResponse{
	public List<Event> record=new LinkedList<Event>();
	public Integer size=0;
	public String message="-1";

	
	
	
	public void addRecord(String id,String rectime,String time,String hwid,String cssact,String op,String fpth,String file_version,String isfolder,String rslt,String syncid,String fsz){
		Event e=new Event();
		if(id!=null)e.id=id;
		if(rectime!=null)e.rectime=rectime;
		if(time!=null)e.time=time;
		if(hwid!=null)e.hwid=hwid;
		if(cssact!=null)e.cssact=cssact;
		if(op!=null)e.op=op;
		if(fpth!=null)e.fpth=fpth;
		if(file_version!=null)e.file_version=file_version;
		if(isfolder!=null)e.isfolder=isfolder;
		if(rslt!=null)e.rslt=rslt;
		if(syncid!=null)e.syncid=syncid;
		if(fsz!=null)e.fsz=fsz;
		
		this.record.add(e);	
	}
	
	
	
	
}
