package core.exception;

import java.io.Serializable;

public class ClientSideException implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String internalMessage;
	public String externalMessage;
	public int errorCode;
	
	public ClientSideException(String internalMessage, String externalMessage, int errorCode) {
		super();
		this.internalMessage = internalMessage;
		this.externalMessage = externalMessage;
		this.errorCode = errorCode;
	}
}
