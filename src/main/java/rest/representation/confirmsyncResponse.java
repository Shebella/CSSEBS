package rest.representation;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class confirmsyncResponse  extends BaseResponse{
	public static final String CONST_NEED_LOCK="need to get lock first.";
	
	
	public String message="-1",syncResult="-1";
}
