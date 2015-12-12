package com.test.busanit.messenger_alpha.beans;

import java.io.Serializable;

public class Profile implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private byte[] bitmapData;
	private String nick;
	private String msg;
	private boolean openId;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public byte[] getBitmapData() {
		return bitmapData;
	}

	public void setBitmapData(byte[] bitmapData) {
		this.bitmapData = bitmapData;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public boolean isOpenId() {
		return openId;
	}

	public void setOpenId(boolean openId) {
		this.openId = openId;
	}
}