package com.test.busanit.messenger_alpha.manager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.test.busanit.messenger_alpha.beans.Message;

public class DBManager extends SQLiteOpenHelper {

    private static DBManager dbManager;

    private static final String DB_NAME = "dbMessenger";
    private static final String TABLE_NAME = "tblMyHistory";
    private static final int DB_VERSION = 1;

    private Context context;


    public DBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    public static DBManager getInstance(Context context) {
        if (dbManager == null) {
            dbManager = new DBManager(context, DB_NAME, null, DB_VERSION);
        }
        return dbManager;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists " + TABLE_NAME + "("
                + "_id integer primary key autoincrement, "
                + "msg text, "
                + "img text, "
                + "regDate text not null);");
    }

    public Cursor selectRecord() {
        return getWritableDatabase().rawQuery("select * from " + TABLE_NAME, null);
    }

    public void insertRecord(Message message) {
        byte[] bitmapData = message.getBitmapData();

        if (bitmapData != null) {
            String fileName = System.currentTimeMillis() + "";
            FileManager.writeToFile(context, fileName, bitmapData);
            getWritableDatabase().execSQL("insert into " + TABLE_NAME + " values(null, null, '" + fileName + "', '" + message.getDate() + "');");

        } else {
            getWritableDatabase().execSQL("insert into " + TABLE_NAME + " values(null,'" + message.getContent() + "', null, '" + message.getDate() + "');");
        }

    }

    /*public void deleteRecord(int _id) {
        Cursor cursor = getReadableDatabase().rawQuery("select * from " + TABLE_NAME + " where _id=" + _id + ";", null);
        cursor.moveToFirst();
        String imageId = cursor.getString(cursor.getColumnIndex("imageId"));
        FileManager fileManager = new FileManager(context, imageId);
        fileManager.deleteFile(imageId);

        getWritableDatabase().execSQL("delete from " + TABLE_NAME + " where imageId='" + imageId + "';");
    }*/

    /*public Cursor searchRecord(String searchWord) {
        return database.rawQuery("select * from " + TABLE_NAME + " where name like '%" + searchWord + "%';", null);
    }*/

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            onCreate(db);
        }
    }
}