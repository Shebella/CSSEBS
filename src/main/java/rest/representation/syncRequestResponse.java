package rest.representation;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class syncRequestResponse  extends BaseResponse{
	@XmlElement
	//public syncData syncData;
	public boolean syncRequestResult=false;
	
	public SystemInfo systeminfo=new SystemInfo();
	public long syncId=-1l;
/*	
	public static class syncData{
		public long syncid;
		
		
	}*/
}
