package com.test.busanit.messenger_alpha.beans;

import java.io.Serializable;

public class Message implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private String roomId;
	private String from;
    private String to;
    private String fromNick;
    private String toNick;
    private byte[] fromImgData;
    private String content;
    private String date;
    private transient boolean isMe;
    private byte[] bitmapData;
    private boolean isTalkMsg;
    
    public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFromNick() {
        return fromNick;
    }

    public void setFromNick(String fromNick) {
        this.fromNick = fromNick;
    }

    public String getToNick() {
        return toNick;
    }

    public void setToNick(String toNick) {
        this.toNick = toNick;
    }
    
    public byte[] getFromImgData() {
        return fromImgData;
    }

    public void setFromImgData(byte[] fromImgData) {
        this.fromImgData = fromImgData;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isMe() {
        return isMe;
    }

    public void setMe(boolean isMe) {
        this.isMe = isMe;
    }

    public byte[] getBitmapData() {
        return bitmapData;
    }

    public void setBitmapImage(byte[] bitmapData) {
        this.bitmapData = bitmapData;
    }

	public boolean isTalkMsg() {
		return isTalkMsg;
	}

	public void setTalkMsg(boolean isTalkMsg) {
		this.isTalkMsg = isTalkMsg;
	}
}