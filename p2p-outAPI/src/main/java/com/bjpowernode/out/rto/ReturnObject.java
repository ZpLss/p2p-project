package com.bjpowernode.out.rto;

/**
 * 接口返回对象
 * 返回值对象的设计，可以根据接口的情况，设计符合要求的返回对象，该类是一个示例
 * 
 * @author 郭鑫
 *
 */
public class ReturnObject {

	/**返回错误码，0成功，1失败*/
	private String errorCode;
	
	/**返回错误信息*/
	private String errorMessage;
	
	/**返回数据信息*/
	private Object objects;

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Object getObjects() {
		return objects;
	}

	public void setObjects(Object objects) {
		this.objects = objects;
	}
}
