package com.test.busanit.messenger_alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.test.busanit.messenger_alpha.beans.Member;
import com.test.busanit.messenger_alpha.R;

import static com.test.busanit.messenger_alpha.Constants.ADD_FRIEND;
import static com.test.busanit.messenger_alpha.Constants.DELIMITER;
import static com.test.busanit.messenger_alpha.Constants.IMAGE_HEIGHT_THUMBNAIL;
import static com.test.busanit.messenger_alpha.Constants.IMAGE_WIDTH_THUMBNAIL;
import static com.test.busanit.messenger_alpha.Constants.MEMBER;

public class FriendAddActivity extends FragmentActivity {

    public static Context context;

    private String friendId;

    private TextView friendMsgTxt;

    private TextView friendNickTxt;

    private ImageView friendImage;

    private Button addBtn;

    private IntroActivity introActivity;

    private MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_add);

        init();
    }

    private void init() {
        context = this;

        friendMsgTxt = (TextView) findViewById(R.id.textView_friendMessage_on_add);
        friendNickTxt = (TextView) findViewById(R.id.textView_friendNickname_on_add);
        friendImage = (ImageView) findViewById(R.id.imageView_friendImage_on_add);

        addBtn = (Button) findViewById(R.id.button_friendAdd);
        addBtn.setOnClickListener(new EventHandler());

        setFriendInfo();
    }

    private void setFriendInfo() {
        Intent intent = getIntent();

        Member member = (Member) intent.getSerializableExtra(MEMBER);

        friendId = member.getId();
        String friendNick = member.getNick();
        String friendMsg = member.getMsg();
        byte[] friendImg = member.getBitmapData();
        Bitmap bitmapImage = getBitmap(friendImg);

        friendNickTxt.setText(friendNick);

        if (friendMsg != null) {
            friendMsgTxt.setText(friendMsg);
        }

        if (bitmapImage == null) {
            bitmapImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
            bitmapImage = Bitmap.createScaledBitmap(bitmapImage, IMAGE_WIDTH_THUMBNAIL, IMAGE_HEIGHT_THUMBNAIL, false);
        }
        friendImage.setImageBitmap(bitmapImage);

        mainActivity = (MainActivity) MainActivity.context;
        if (mainActivity.userId.equals(friendId)) {
            addBtn.setVisibility(View.GONE);
        }
    }

    private Bitmap getBitmap(byte[] bitmapData) {
        if (bitmapData == null)
            return null;
        Bitmap bitmapImage = BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length);
        bitmapImage = Bitmap.createScaledBitmap(bitmapImage, IMAGE_WIDTH_THUMBNAIL, IMAGE_HEIGHT_THUMBNAIL, false);
        return bitmapImage;
    }

    private class EventHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_friendAdd:
                    openDialog();
                    break;
            }
        }
    }

    private void openDialog() {
        new MaterialDialog.Builder(context).title("친구추가").content("친구로 추가하시겠습니까?").backgroundColor(Color.argb(255, 186, 220, 217))
                .titleColor(Color.argb(255, 102, 169, 163)).contentColor(Color.argb(255, 102, 169, 163)).iconRes(R.drawable.gnome_session_logout)
                .positiveColor(Color.argb(255, 102, 169, 163)).dividerColor(Color.argb(255, 102, 169, 163))
                .positiveText("예").negativeText("아니오")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        addFriend();
                    }
                }).show();

    }

    private void addFriend() {
        mainActivity = (MainActivity) MainActivity.context;
        introActivity = (IntroActivity) IntroActivity.context;
        introActivity.connector.sendMessage(ADD_FRIEND + DELIMITER + mainActivity.userId + DELIMITER + friendId);
        finish();
    }
}