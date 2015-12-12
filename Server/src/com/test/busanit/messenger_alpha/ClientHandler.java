package com.test.busanit.messenger_alpha;

import static com.test.busanit.messenger_alpha.Constants.ADD_FRIEND;
import static com.test.busanit.messenger_alpha.Constants.AUTO_LOGIN;
import static com.test.busanit.messenger_alpha.Constants.CHAT_HISTORY;
import static com.test.busanit.messenger_alpha.Constants.DELIMITER;
import static com.test.busanit.messenger_alpha.Constants.ENTER_FRIEND;
import static com.test.busanit.messenger_alpha.Constants.ENTER_ROOM;
import static com.test.busanit.messenger_alpha.Constants.EXIST_FRIEND;
import static com.test.busanit.messenger_alpha.Constants.EXIST_ID;
import static com.test.busanit.messenger_alpha.Constants.EXIT_FRIEND;
import static com.test.busanit.messenger_alpha.Constants.EXIT_RANDOM_ROOM;
import static com.test.busanit.messenger_alpha.Constants.EXIT_ROOM;
import static com.test.busanit.messenger_alpha.Constants.EXIT_TALK;
import static com.test.busanit.messenger_alpha.Constants.FRIEND_LIST;
import static com.test.busanit.messenger_alpha.Constants.JOIN;
import static com.test.busanit.messenger_alpha.Constants.LOGIN;
import static com.test.busanit.messenger_alpha.Constants.LOGIN_FAILURE;
import static com.test.busanit.messenger_alpha.Constants.MEMBER_INFO;
import static com.test.busanit.messenger_alpha.Constants.MEMBER_LIST;
import static com.test.busanit.messenger_alpha.Constants.OPEN_CHAT;
import static com.test.busanit.messenger_alpha.Constants.PROFILE;
import static com.test.busanit.messenger_alpha.Constants.RANDOM_CHAT;
import static com.test.busanit.messenger_alpha.Constants.RANDOM_CHAT_ROOM;
import static com.test.busanit.messenger_alpha.Constants.REMOVE_FRIEND;
import static com.test.busanit.messenger_alpha.Constants.ROOM_LIST;
import static com.test.busanit.messenger_alpha.Constants.ROOM_MEMBER;
import static com.test.busanit.messenger_alpha.Constants.SEARCH_FRIEND;
import static com.test.busanit.messenger_alpha.Constants.TALK;
import static com.test.busanit.messenger_alpha.Constants.TERMINATE_APP;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.test.busanit.messenger_alpha.beans.Member;
import com.test.busanit.messenger_alpha.beans.Message;
import com.test.busanit.messenger_alpha.beans.Profile;
import com.test.busanit.messenger_alpha.beans.Room;
import com.test.busanit.messenger_alpha.manager.DBManager;
import com.test.busanit.messenger_alpha.manager.RoomManager;


public class ClientHandler implements Runnable {

	private Socket socket;

	private List<ClientHandler> clientList;

	private Map<String, Room> roomList;

	private RoomManager roomManager;

	private DBManager dbManager;

	private ObjectInputStream in;

	private ObjectOutputStream out;

	private boolean isRunning;

	public String userId;

	public String userNick;

	public String currentRoomId;

	public ClientHandler(Socket socket, List<ClientHandler> clientList, Map<String, Room> roomList,
			RoomManager roomManager, DBManager dbManager) {
		this.socket = socket;
		this.clientList = clientList;
		this.roomList = roomList;
		this.roomManager = roomManager;
		this.dbManager = dbManager;

		try {
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());

			isRunning = true;

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendMessageToUser(Object msg) {
		try {
			out.writeObject(msg);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendMessageToRoom(String roomId, Object msg) {
		if (roomManager.existRoom(roomId)) {
			List<ClientHandler> userList = roomList.get(roomId).getUser();
			synchronized (userList) {
				Iterator<ClientHandler> it = userList.iterator();
				while (it.hasNext()) {
					ClientHandler client = it.next();
					client.sendMessageToUser(msg);
				}	
			}
		}
	}

	public void broadcast(Object msg) {
		for (ClientHandler client : clientList) {
			client.sendMessageToUser(msg);
		}
	}

	public String[] parseMessage(String msg) {
		return msg.split(DELIMITER);
	}

	public void quit() {
		if (roomManager.existRoom(currentRoomId)) {
			roomManager.removeRoomUser(currentRoomId, this);
			if (roomManager.isEmptyRoom(currentRoomId)) {
				roomManager.removeRoom(currentRoomId);
			}
		}
		clientList.remove(this);

		try {
			if (out != null) {
				out.close();
				out = null;
			}
			if (in != null) {
				in.close();
				in = null;
			}
			if (socket != null) {
				socket.close();
				socket = null;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			Object data = null;

			while (isRunning) {
				data = in.readObject();
				if (data == null) {
					break;
				}
				handleMessage(data);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SocketTimeoutException e) {
			System.out.println("TIME OUT");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			quit();
		}
	}

	private void handleMessage(Object data) {

		if (data instanceof String) {
			String msg = (String) data;

			System.out.println(msg);

			String[] msgs = parseMessage(msg);

			String protocol = msgs[0];

			String roomId;
			String roomName;

			String friendId;
			String friendNick;

			Room room;

			Member member;

			List<Member> friendList;
			List<String> friendIdList;
			Iterator<String> iterator;

			switch (protocol) {
			case LOGIN:
				String loginId = msgs[1];
				String loginPass = msgs[2];

				if (dbManager.checkId(loginId, loginPass)) {
					member = dbManager.getMember(loginId);

					userId = member.getId();
					userNick = member.getNick();

					sendMessageToUser(LOGIN + DELIMITER + loginId + DELIMITER + loginPass);
					sendMessageToUser(member);

				} else {
					sendMessageToUser(LOGIN_FAILURE);
				}
				break;

			case AUTO_LOGIN:
				userId = msgs[1];

				member = dbManager.getMember(userId);

				userId = member.getId();
				userNick = member.getNick();

				sendMessageToUser(AUTO_LOGIN + DELIMITER + userId);
				sendMessageToUser(member);

				break;

			case ROOM_LIST:
				sendMessageToUser(ROOM_LIST);
				sendMessageToUser(roomManager.getRoomList());
				break;

			case FRIEND_LIST:
				userId = msgs[1];

				friendList = new ArrayList<>();

				friendIdList = dbManager.getFriendIdList(userId);

				if (friendIdList.size() > 0) {
					iterator = friendIdList.iterator();

					while (iterator.hasNext()) {
						friendId = iterator.next();
						member = dbManager.getMember(friendId);
						friendList.add(member);
					}
					sendMessageToUser(FRIEND_LIST);
					sendMessageToUser(friendList);
				}
				break;

			case ROOM_MEMBER:
				roomId = msgs[1];

				if (roomManager.existRoom(roomId)) {

					List<String> userIdList = roomManager.getRoomUserId(roomId);

					if (userIdList != null) {

						List<Member> roomMemberList = new ArrayList<>();

						iterator = userIdList.iterator();
						while (iterator.hasNext()) {
							String roomUserId = iterator.next();
							member = dbManager.getMember(roomUserId);
							roomMemberList.add(member);
						}

						sendMessageToRoom(roomId, ROOM_MEMBER);
						sendMessageToRoom(roomId, roomMemberList);
					}
				}
				break;

			case ENTER_ROOM:
				currentRoomId = msgs[1];
				roomName = msgs[2];

				if (roomManager.existRoom(currentRoomId)) {

					sendMessageToRoom(currentRoomId, ENTER_FRIEND + DELIMITER + userNick);

					roomManager.addRoomUser(currentRoomId, this);

					sendMessageToUser(OPEN_CHAT);

					sendMessageToUser(ENTER_ROOM + DELIMITER + currentRoomId + DELIMITER + roomName);
				}

				break;

			case EXIT_ROOM:
				currentRoomId = msgs[1];

				if (roomManager.existRoom(currentRoomId)) {

					roomManager.removeRoomUser(currentRoomId, this);

					sendMessageToRoom(currentRoomId, EXIT_FRIEND + DELIMITER + userNick);

					if (roomManager.isEmptyRoom(currentRoomId)) {
						roomManager.removeRoom(currentRoomId);
					}

					broadcast(ROOM_LIST);
					broadcast(roomManager.getRoomList());

					sendMessageToUser(EXIT_ROOM);
				}
				break;

			case EXIT_RANDOM_ROOM:
				currentRoomId = msgs[1];

				if (roomManager.existRoom(currentRoomId)) {

					roomManager.removeRoomUser(currentRoomId, this);
					if (roomManager.isEmptyRoom(currentRoomId)) {
						roomManager.removeRoom(currentRoomId);
					}
					sendMessageToRoom(currentRoomId, EXIT_FRIEND + DELIMITER + userNick);

					sendMessageToUser(EXIT_ROOM);
				}
				break;

			case EXIT_TALK:
				currentRoomId = msgs[1];

				roomManager.removeRoomUser(currentRoomId, this);
				if (roomManager.isEmptyRoom(currentRoomId)) {
					roomManager.removeRoom(currentRoomId);
				}
				sendMessageToUser(EXIT_ROOM);
				break;

			case MEMBER_INFO:
				friendId = msgs[1];

				member = dbManager.getMember(friendId);

				sendMessageToUser(MEMBER_INFO);
				sendMessageToUser(member);

				break;

			case SEARCH_FRIEND:
				String searchId = msgs[1];

				List<Member> memberList = dbManager.getMemberList(searchId);

				sendMessageToUser(MEMBER_LIST);
				sendMessageToUser(memberList);

				break;

			case ADD_FRIEND:

				friendId = msgs[2];

				if (dbManager.existFriend(userId, friendId)) {
					sendMessageToUser(EXIST_FRIEND);

				} else {
					if (dbManager.insertFriend(userId, friendId)) {

						friendList = new ArrayList<>();

						friendIdList = dbManager.getFriendIdList(userId);

						iterator = friendIdList.iterator();

						while (iterator.hasNext()) {
							friendId = iterator.next();
							member = dbManager.getMember(friendId);
							friendList.add(member);
						}
						sendMessageToUser(FRIEND_LIST);
						sendMessageToUser(friendList);
					}
				}
				break;

			case REMOVE_FRIEND:
				friendId = msgs[2];
				if (dbManager.removeFriend(userId, friendId)) {
					sendMessageToUser(REMOVE_FRIEND);
				}
				break;

			case TALK:
				friendId = msgs[2];
				friendNick = msgs[3];
				boolean isTalkRoom = Boolean.parseBoolean(msgs[4]);

				roomId = userId + friendId;
				String reverseRoomId = friendId + userId;

				if (roomManager.existRoom(roomId)) {
					roomManager.addRoomUser(roomId, this);

					sendMessageToUser(TALK + DELIMITER + roomId + DELIMITER + userId + DELIMITER + friendId + DELIMITER
							+ friendNick);

				} else if (roomManager.existRoom(reverseRoomId)) {
					roomManager.addRoomUser(reverseRoomId, this);

					sendMessageToUser(TALK + DELIMITER + reverseRoomId + DELIMITER + userId + DELIMITER + friendId
							+ DELIMITER + friendNick);

				} else {
					room = new Room();
					room.setRoomId(roomId);
					room.setChiefNick(friendNick);
					room.setUser(this);
					room.setTalkRoom(isTalkRoom);
					room.setRandomRoom(false);

					roomManager.addRoom(room);

					sendMessageToUser(TALK + DELIMITER + roomId + DELIMITER + userId + DELIMITER + friendId + DELIMITER
							+ friendNick);
				}

				break;

			case RANDOM_CHAT:
				userId = msgs[1];
				List<Room> randomRoomList = roomManager.getRandomRoomList();

				while (true) {
					int randomRoomCount = randomRoomList.size();

					if (randomRoomCount == 0) {

						room = new Room();
						room.setRoomId(userId);
						room.setUser(this);
						room.setRoomName(RANDOM_CHAT_ROOM);
						room.setUserId(userId);
						room.setTalkRoom(false);
						room.setRandomRoom(true);

						currentRoomId = userId;

						roomManager.addRoom(room);

						sendMessageToUser(RANDOM_CHAT);
						sendMessageToUser(room);

						break;

					} else {
						List<Room> randomRoomListOneUser = new ArrayList<>();

						Iterator<Room> it = randomRoomList.iterator();
						synchronized (randomRoomList) {
							while (it.hasNext()) {
								Room randomRoom = it.next();
								if (randomRoom.getUser().size() == 1) {
									randomRoomListOneUser.add(randomRoom);
								}
							}
						}

						if (randomRoomListOneUser.size() == 0) {

							room = new Room();
							room.setRoomId(userId);
							room.setUser(this);
							room.setRoomName(RANDOM_CHAT_ROOM);
							room.setUserId(userId);
							room.setTalkRoom(false);
							room.setRandomRoom(true);

							currentRoomId = userId;

							roomManager.addRoom(room);

							sendMessageToUser(RANDOM_CHAT);
							sendMessageToUser(room);

							break;

						} else {

							int randomRoomNumber = (int) (Math.random() * randomRoomListOneUser.size());
							room = randomRoomListOneUser.get(randomRoomNumber);

							roomId = room.getRoomId();

							if (roomManager.existRoom(roomId)) {

								sendMessageToRoom(roomId, ENTER_FRIEND + DELIMITER + userNick);

								roomManager.addRoomUser(roomId, this);

								sendMessageToUser(RANDOM_CHAT);

								sendMessageToUser(ENTER_ROOM + DELIMITER + roomId + DELIMITER + RANDOM_CHAT_ROOM);

								currentRoomId = userId;

							}
							break;
						}
					}
				}

				break;

			case CHAT_HISTORY:

				userId = msgs[1];
				friendId = msgs[2];

				if (!friendId.equals(userId)) {
					List<Message> chatHistory = dbManager.getChatHistory(userId, friendId);

					sendMessageToUser(CHAT_HISTORY);
					sendMessageToUser(chatHistory);
				}
				break;

			case TERMINATE_APP:
				isRunning = false;
			}

		} else if (data instanceof Member) {

			Member member = (Member) data;

			if (dbManager.existId(member.getId())) {
				sendMessageToUser(EXIST_ID);

			} else {
				if (dbManager.insertMember(member)) {
					sendMessageToUser(JOIN);
				}
			}

		} else if (data instanceof Message) {

			Message message = (Message) data;

			String roomId = message.getRoomId();
			boolean isTalkMsg = message.isTalkMsg();

			dbManager.insertMessage(message);

			if (isTalkMsg) {
				// broadcast(message);
				sendMessageToRoom(roomId, message);
			} else {
				sendMessageToRoom(roomId, message);
			}

		} else if (data instanceof Room) {

			Room r = (Room) data;
			Room room = new Room();

			currentRoomId = r.getRoomId();

			room.setRoomId(r.getRoomId());
			room.setRoomName(r.getRoomName());
			room.setChiefNick(r.getChiefNick());
			room.setOpenDate(r.getOpenDate());
			room.setBitmapData(r.getBitmapData());
			room.setTalkRoom(r.isTalkRoom());
			room.setRandomRoom(r.isRandomRoom());
			room.setUser(this);
			room.setUserId(userId);
			room.setUserNick(userNick);

			roomManager.addRoom(room);

			sendMessageToUser(OPEN_CHAT);
			sendMessageToUser(room);

			broadcast(ROOM_LIST);
			broadcast(roomManager.getRoomList());

		} else if (data instanceof Profile) {
			Profile profile = (Profile) data;
			if (dbManager.updateProfile(profile)) {
				sendMessageToUser(PROFILE);
			}
		}
	}
}