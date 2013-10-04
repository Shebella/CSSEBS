package rest.representation;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FolderInfo  extends BaseResponse{
	@XmlElement
	public BucketInfo buckinfo=new BucketInfo();
	public String folderName="-1";
	public Long size=-1l,subfoldernum=-1l,fileNum=-1l;
	
	
	
	public FolderInfo(){
		
	}
}
