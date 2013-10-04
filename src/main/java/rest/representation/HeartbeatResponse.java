package rest.representation;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class HeartbeatResponse  extends BaseResponse{
	public String heartbeatResult="-1";
}
