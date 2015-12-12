
package com.test.busanit.messenger_alpha.manager;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class FileManager {

	private static final String FILE_STORAGE_PATH = "src/com/test/busanit/messenger_alpha/manager/storage/";

	public void saveToFile(String fileName, byte[] bitmapData) {
		ByteBuffer buf = ByteBuffer.allocate(bitmapData.length);
		buf.put(bitmapData).clear();

		FileOutputStream out = null;
		try {
			out = new FileOutputStream(FILE_STORAGE_PATH + fileName);
			out.getChannel().write(buf);

		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			if (out != null) {
				try {
					out.close();
					out = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public byte[] loadFromFile(String fileName) {
		FileInputStream in = null;
		try {
			in = new FileInputStream(FILE_STORAGE_PATH + fileName);
			ByteBuffer buf = ByteBuffer.allocate(in.available());
			in.getChannel().read(buf);

			return buf.array();

		} catch (IOException e) {
			return null;

		} finally {
			if (in != null) {
				try {
					in.close();
					in = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}