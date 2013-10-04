package rest.representation;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Event  extends BaseResponse{
	

	public String id="-1",rectime="-1",time="-1",hwid="-1",cssact="-1",op="-1",fpth="-1",file_version="-1",isfolder="-1",rslt="-1",syncid="-1",fsz="-1";
	public Event(){}

}
