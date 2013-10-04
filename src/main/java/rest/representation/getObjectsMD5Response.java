package rest.representation;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class getObjectsMD5Response  extends BaseResponse{
	
	public List<String> record=new LinkedList<String>();
	public Integer size=0;
	
	
	
	
	
	public void addItem(String str){this.record.add(str);}
}
