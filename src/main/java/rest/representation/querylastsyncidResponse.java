package rest.representation;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class querylastsyncidResponse  extends BaseResponse{
	public final static String CONS_INVALID_INSTANCEID="invalid instanceid.",
								CONS_NO_LAST_SYNCID="no last syncid";
	
	
	public String account="-1",instanceid="-1",lastsyncid="-1";
	public String message="-1";
	
	
	
}
