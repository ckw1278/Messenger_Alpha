package com.test.busanit.messenger_alpha;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.test.busanit.messenger_alpha.beans.Room;
import com.test.busanit.messenger_alpha.manager.DBManager;
import com.test.busanit.messenger_alpha.manager.RoomManager;

public class ServerThread implements Runnable {

	private List<ClientHandler> clientList = new ArrayList<>();

	private Map<String, Room> roomList = new HashMap<>();

	private RoomManager roomManager;

	private DBManager dbManager;

	private ServerSocket serverSocket;

	private boolean isRunning;

	private static final int PORT = 9999;

	//private static final int CONNECTION_TIMEOUT = 60000;

	public ServerThread() throws IOException {

		Collections.synchronizedList(clientList);
		Collections.synchronizedMap(roomList);

		roomManager = new RoomManager(roomList);

		dbManager = new DBManager();

		serverSocket = new ServerSocket(PORT);
		//serverSocket.setSoTimeout(CONNECTION_TIMEOUT);

		isRunning = true;
		System.out.println("Server Start");
	}

	@Override
	public void run() {
		while (isRunning) {
			try {
				Socket socket = serverSocket.accept();

				System.out.println("Client Connected : " + socket.getInetAddress().toString());

				ClientHandler clientHandler = new ClientHandler(socket, clientList, roomList, roomManager, dbManager);

				Thread clientThread = new Thread(clientHandler);

				clientThread.start();

				clientList.add(clientHandler);

				System.out.println("Client : " + clientList.size());
				System.out.println("Room : " + roomList.size());
 
			} catch (InterruptedIOException e) {
				System.out.println("SocketTimeout");

			} catch (IOException e) {
				break;
			}
		}
		try {
			if (serverSocket != null) {
				serverSocket.close();
				serverSocket = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}