package rest.representation;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class S3Object  extends BaseResponse{
	public String objectId = "-1",
				  etagId = "-1",
				  objectName = "-1",
				  objectSize = "-1",
				  contentType = "-1",
				  modifiedDate = "-1",
				  bucketName = "-1",
				  objectUUID = "-1",
				  type = "-1",
				  clickAction = "-1";
}
