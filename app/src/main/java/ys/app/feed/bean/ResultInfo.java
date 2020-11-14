package ys.app.feed.bean;

import java.io.Serializable;

public class ResultInfo implements Serializable{

	private static final long serialVersionUID = 1L;
	private Object data;
	private Integer code;
	private String message;

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}