package com.test.busanit.messenger_alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.busanit.messenger_alpha.beans.Member;
import com.test.busanit.messenger_alpha.R;

import static com.test.busanit.messenger_alpha.Constants.CHAT_MODE;
import static com.test.busanit.messenger_alpha.Constants.DELIMITER;
import static com.test.busanit.messenger_alpha.Constants.IMAGE_HEIGHT_THUMBNAIL;
import static com.test.busanit.messenger_alpha.Constants.IMAGE_WIDTH_THUMBNAIL;
import static com.test.busanit.messenger_alpha.Constants.MEMBER;
import static com.test.busanit.messenger_alpha.Constants.ROOM_NAME;
import static com.test.busanit.messenger_alpha.Constants.SELF_CHAT;
import static com.test.busanit.messenger_alpha.Constants.TALK;

public class FriendInfoActivity extends FragmentActivity {

    public static Context context;

    private Member member;

    private TextView friendMsgTxt;

    private TextView friendNickTxt;

    private TextView friendNumTxt;

    private ImageView friendImage;

    private Button talkBtn;

    private Button callBtn;

    private String friendId;

    private String friendNick;

    private String friendMsg;

    private String friendNum;

    private byte[] bitmapData;

    private boolean isMyInfo;

    private IntroActivity introActivity;

    private MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_info);

        init();
    }

    private void init() {
        context = this;

        getFriendInfo();

        friendMsgTxt = (TextView) findViewById(R.id.textView_friendMessage_on_info);
        friendNickTxt = (TextView) findViewById(R.id.textView_friendNickname_on_info);
        friendNumTxt = (TextView) findViewById(R.id.textView_friendNumber_on_info);
        friendImage = (ImageView) findViewById(R.id.imageView_friendImage_on_info);
        ButtonEventHandler eventHandler = new ButtonEventHandler();

        talkBtn = (Button) findViewById(R.id.button_talk);
        talkBtn.setOnClickListener(eventHandler);

        callBtn = (Button) findViewById(R.id.button_call);
        callBtn.setOnClickListener(eventHandler);

        if (isMyInfo) {
            talkBtn.setText("나와의채팅");
            callBtn.setVisibility(View.GONE);
        }

        setFriendInfo();
    }

    private void getFriendInfo() {
        Intent intent = getIntent();
        member = (Member) intent.getSerializableExtra(MEMBER);

        friendId = member.getId();
        friendNick = member.getNick();
        friendMsg = member.getMsg();
        friendNum = member.getNum();
        bitmapData = member.getBitmapData();

        mainActivity = (MainActivity) MainActivity.context;
        if (mainActivity.userId.equals(friendId)) {
            isMyInfo = true;
        }
    }

    private void setFriendInfo() {

        Bitmap bitmapImage = null;

        friendMsgTxt.setText(friendMsg);
        friendNickTxt.setText(friendNick);
        friendNumTxt.setText(friendNum);

        if (bitmapData != null) {
            bitmapImage = getBitmap(bitmapData);
        } else {
            bitmapImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
        }

        friendImage.setImageBitmap(bitmapImage);
        if (bitmapImage != null) {
            bitmapImage = null;
        }
    }

    private Bitmap getBitmap(byte[] bitmapData) {
        if (bitmapData == null)
            return null;
        Bitmap bitmapImage = BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length);
        bitmapImage = Bitmap.createScaledBitmap(bitmapImage, IMAGE_WIDTH_THUMBNAIL, IMAGE_HEIGHT_THUMBNAIL, false);
        return bitmapImage;
    }

    private class ButtonEventHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_talk:
                    if (isMyInfo) {
                        startActivity(new Intent(context, RoomActivity.class).putExtra(CHAT_MODE, SELF_CHAT).putExtra(ROOM_NAME, mainActivity.userNick));
                        finish();
                    } else {
                        mainActivity = (MainActivity) MainActivity.context;
                        introActivity = (IntroActivity) IntroActivity.context;
                        introActivity.connector.sendMessage(TALK + DELIMITER + mainActivity.userId + DELIMITER + friendId + DELIMITER + friendNick + DELIMITER + true);
                    }
                    break;

                case R.id.button_call:
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + friendNumTxt.getText().toString())));
                    break;
            }
        }
    }
}