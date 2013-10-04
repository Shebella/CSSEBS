package rest.representation;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SystemInfo extends BaseResponse{
	public String serverTime="-1";
}
