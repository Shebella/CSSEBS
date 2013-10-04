package rest.representation;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class addEventResponse  extends BaseResponse{
	
	@XmlElement
	public unLockResponse unlockResponse=new unLockResponse();
	public boolean isSuccess=false;
	public String message="-1";
	
	
	
}
