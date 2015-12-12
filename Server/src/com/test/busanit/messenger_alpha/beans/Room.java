package com.test.busanit.messenger_alpha.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.test.busanit.messenger_alpha.ClientHandler;

public class Room implements Serializable {

	private static final long serialVersionUID = 1L;

	private String roomId;
	private String roomName;
	private String chiefNick;
	private String openDate;
	private byte[] bitmapData;
	private boolean isTalkRoom;
	private boolean isRandomRoom;
	private transient List<ClientHandler> user = new ArrayList<>();
	private transient List<String> userId = new ArrayList<>();
	private transient List<String> userNick = new ArrayList<>();

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public String getChiefNick() {
		return chiefNick;
	}

	public void setChiefNick(String chiefNick) {
		this.chiefNick = chiefNick;
	}

	public String getOpenDate() {
		return openDate;
	}

	public void setOpenDate(String openDate) {
		this.openDate = openDate;
	}

	public List<ClientHandler> getUser() {
		return user;
	}

	public void setUser(ClientHandler user) {
		this.user.add(user);
	}

	public List<String> getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId.add(userId);
	}

	public List<String> getUserNick() {
		return userNick;
	}

	public void setUserNick(String userNick) {
		this.userNick.add(userNick);
	}

	public byte[] getBitmapData() {
		return bitmapData;
	}

	public void setBitmapData(byte[] bitmapData) {
		this.bitmapData = bitmapData;
	}
	

	public boolean isTalkRoom() {
		return isTalkRoom;
	}

	public void setTalkRoom(boolean isTalkRoom) {
		this.isTalkRoom = isTalkRoom;
	}

	public boolean isRandomRoom() {
		return isRandomRoom;
	}

	public void setRandomRoom(boolean isRandomRoom) {
		this.isRandomRoom = isRandomRoom;
	}
}