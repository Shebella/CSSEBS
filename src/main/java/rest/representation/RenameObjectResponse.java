package rest.representation;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RenameObjectResponse  extends BaseResponse{

	private boolean isSuccess = false;
	private String message="-1";

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
