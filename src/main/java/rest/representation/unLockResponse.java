package rest.representation;

import java.sql.Timestamp;
import java.util.Calendar;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class unLockResponse  extends BaseResponse{
	public boolean unlockresult=false;
	public String time="-1";
	
	
	
	public void renewTime(){
		
		Calendar c=Calendar.getInstance();
		this.time=new Timestamp(c.getTimeInMillis()-c.getTimeZone().getRawOffset()).toString();
	}
	
}
