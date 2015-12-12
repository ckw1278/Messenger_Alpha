package com.test.busanit.messenger_alpha.beans;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

public class Member implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String pass;
    private String nick;
    private String num;
    private String msg;
    private boolean openId;
    private byte[] bitmapData;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
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

    public byte[] getBitmapData(Bitmap bitmap) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.flush();
            stream.close();
            bitmapData = stream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmapData;
    }

    public byte[] getBitmapData() {
        return bitmapData;
    }

    public void setBitmapData(byte[] bitmapData) {
        this.bitmapData = bitmapData;
    }
}