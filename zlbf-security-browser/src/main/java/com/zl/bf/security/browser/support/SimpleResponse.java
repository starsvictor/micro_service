package com.zl.bf.security.browser.support;

/**
 * 返回类型封装
 * @author star
 * @date 2018年12月6日 
 *
 */
public class SimpleResponse {
	
	private Object content;

	/**
	 * 构造函数
	 * @param content
	 */
	public SimpleResponse(Object content) {
		super();
		this.content = content;
	}

	
	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		this.content = content;
	}
	
	

}
