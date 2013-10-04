package rest.representation;

import javax.xml.bind.annotation.XmlRootElement;



@XmlRootElement
public class BucketInfo  extends BaseResponse{
	public String owner_id="-1",bucket_name="-1";
	public Long totalUsed=-1l,maxSize=-1l,objectCount=-1l,folderCount=-1l;
	public Boolean isVersioning=false;
	public Double percentage=-1d;
}
