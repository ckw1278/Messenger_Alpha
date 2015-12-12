package com.test.busanit.messenger_alpha;

public class Server {
	
	public static void main(String[] args) {
		try {
			Thread serverThread = new Thread(new ServerThread());
			serverThread.start();
			serverThread.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}