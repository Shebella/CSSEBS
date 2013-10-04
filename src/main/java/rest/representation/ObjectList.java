package rest.representation;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class ObjectList extends BaseResponse{
	public pageInfo pagingInfo=new pageInfo();
	public List<S3Object> objBeanList=new LinkedList<S3Object>();
	public Integer size=0;
	
	
	public String errorMessage="-1";
}




