package com.test.busanit.messenger_alpha.manager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.test.busanit.messenger_alpha.ClientHandler;
import com.test.busanit.messenger_alpha.beans.Room;

public class RoomManager {

	private Map<String, Room> roomList;

	public RoomManager(Map<String, Room> roomList) {
		this.roomList = roomList;
	}

	public void addRoom(Room room) {
		roomList.put(room.getRoomId(), room);
	}

	public void addRoomUser(String roomId, ClientHandler client) {
		if (existRoom(roomId)) {
			Room room = roomList.get(roomId);

			room.setUser(client);
			room.setUserId(client.userId);
			room.setUserNick(client.userNick);

			roomList.put(roomId, room);
		}
	}

	public void removeRoom(String roomId) {
		if (existRoom(roomId)) {
			roomList.remove(roomId);
		}
	}

	public void removeRoomUser(String roomId, ClientHandler client) {
		if (existRoom(roomId)) {
			Room room = roomList.get(roomId);

			room.getUser().remove(client);
			room.getUserId().remove(client.userId);
			room.getUserNick().remove(client.userNick);

			roomList.put(roomId, room);
		}
	}

	public Room getRoom(String roomId) {
		if (existRoom(roomId)) {
			return roomList.get(roomId);
		}
		return null;
	}

	public List<ClientHandler> getRoomUser(String roomId) {
		List<ClientHandler> userList = null;
		if (existRoom(roomId)) {
			Room room = roomList.get(roomId);
			userList = room.getUser();
		}
		return userList;
	}

	public List<String> getRoomUserId(String roomId) {
		List<String> userIdList = null;

		if (existRoom(roomId)) {
			Room room = roomList.get(roomId);
			userIdList = room.getUserId();

		}
		return userIdList;
	}

	public List<String> getRoomId() {
		List<String> roomIdList = new ArrayList<>();
		Set<String> keySet = roomList.keySet();
		if (keySet.size() > 0) {
			synchronized (roomList) {
				Iterator<String> it = keySet.iterator();
				while (it.hasNext()) {
					String key = it.next();
					Room room = roomList.get(key);
					roomIdList.add(room.getRoomId());
				}
			}
		}
		return roomIdList;
	}

	public List<Room> getRoomList() {
		List<Room> roomInfo = new ArrayList<>();
		Set<String> keySet = roomList.keySet();
		System.out.println(keySet.size());
		if (keySet.size() > 0) {
			synchronized (roomList) {
				Iterator<String> it = keySet.iterator();
				while (it.hasNext()) {
					String key = it.next();
					Room room = roomList.get(key);

					boolean isTalkRoom = room.isTalkRoom();
					boolean isRandomRoom = room.isRandomRoom();
					if (!isTalkRoom && !isRandomRoom) {
						roomInfo.add(room);
					}
				}
			}
		}
		return roomInfo;
	}

	public List<Room> getRandomRoomList() {
		List<Room> randomRoomList = new ArrayList<>();
		Set<String> keySet = roomList.keySet();
		if (keySet.size() > 0) {
			synchronized (roomList) {
				Iterator<String> it = keySet.iterator();
				while (it.hasNext()) {
					String key = it.next();
					Room room = roomList.get(key);
					boolean isRandomRoom = room.isRandomRoom();
					if (isRandomRoom) {
						randomRoomList.add(room);
					}
				}
			}
		}
		return randomRoomList;
	}

	public boolean existRoom(String roomId) {
		Room room = roomList.get(roomId);
		if (room != null) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isEmptyRoom(String roomId) {
		Room room = roomList.get(roomId);
		if (room.getUser().size() == 0) {
			return true;
		} else {
			return false;
		}
	}
}