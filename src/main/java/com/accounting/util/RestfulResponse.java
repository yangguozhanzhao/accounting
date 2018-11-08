package com.accounting.util;
 
import java.util.List;
 
public class RestfulResponse<T> {
	private String msg="请求成功";
	private List<T> data;
	
	public String getMsg() {
		return msg;
	}
	
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public List<T> getData() {
		return data;
	}
	
	public void setData(List<T> data) {
		this.data = data;
	}
 
}