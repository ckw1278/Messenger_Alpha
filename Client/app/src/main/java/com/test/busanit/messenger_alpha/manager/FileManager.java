package com.test.busanit.messenger_alpha.manager;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

public class FileManager {

    public static void writeToFile(Context context, String fileName, byte[] bitmapData) {
        try {
            BufferedOutputStream outputStream = new BufferedOutputStream(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            outputStream.write(bitmapData);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] readFromFile(Context context, String fileName) {
        try {
            BufferedInputStream inputStream = new BufferedInputStream(context.openFileInput(fileName));
            byte[] bitmapData = new byte[inputStream.available()];
            while (inputStream.read(bitmapData) != -1) {
            }
            inputStream.close();
            return bitmapData;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
