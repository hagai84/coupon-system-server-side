package core.beans;

public class ExceptionBean {
	public int errorCode;
	public String externalMessage;
	public String internalMessage;
	
	
	


	public ExceptionBean(int errorCode, String externalMessage, String internalMessage) {
		super();
		this.errorCode = errorCode;
		this.externalMessage = externalMessage;
		this.internalMessage = internalMessage;
	}

	public ExceptionBean() {
		super();
	}

	public String getInternalMessage() {
		return internalMessage;
	}

	public void setInternalMessage(String internalMessage) {
		this.internalMessage = internalMessage;
	}

	public String getExternalMessage() {
		return externalMessage;
	}

	public void setExternalMessage(String externalMessage) {
		this.externalMessage = externalMessage;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	
	

}
