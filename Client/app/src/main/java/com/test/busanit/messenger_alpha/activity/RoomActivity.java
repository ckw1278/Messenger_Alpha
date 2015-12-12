package com.test.busanit.messenger_alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.test.busanit.messenger_alpha.R;
import com.test.busanit.messenger_alpha.adapter.MessageAdapter;
import com.test.busanit.messenger_alpha.adapter.NavigationAdapter;
import com.test.busanit.messenger_alpha.beans.Member;
import com.test.busanit.messenger_alpha.beans.Message;
import com.test.busanit.messenger_alpha.manager.DBManager;
import com.test.busanit.messenger_alpha.manager.FileManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.test.busanit.messenger_alpha.Constants.ANONYMOUS;
import static com.test.busanit.messenger_alpha.Constants.CHAT_HISTORY;
import static com.test.busanit.messenger_alpha.Constants.CHAT_MODE;
import static com.test.busanit.messenger_alpha.Constants.CONTENT_TYPE;
import static com.test.busanit.messenger_alpha.Constants.DELIMITER;
import static com.test.busanit.messenger_alpha.Constants.EXIT_RANDOM_ROOM;
import static com.test.busanit.messenger_alpha.Constants.EXIT_ROOM;
import static com.test.busanit.messenger_alpha.Constants.EXIT_TALK;
import static com.test.busanit.messenger_alpha.Constants.FRIEND_ID;
import static com.test.busanit.messenger_alpha.Constants.FRIEND_NICK;
import static com.test.busanit.messenger_alpha.Constants.IMAGE_HEIGHT;
import static com.test.busanit.messenger_alpha.Constants.IMAGE_WIDTH;
import static com.test.busanit.messenger_alpha.Constants.MEMBER;
import static com.test.busanit.messenger_alpha.Constants.OPEN_CHAT;
import static com.test.busanit.messenger_alpha.Constants.RANDOM_CHAT;
import static com.test.busanit.messenger_alpha.Constants.REQUEST_CODE_OPEN_GALLERY;
import static com.test.busanit.messenger_alpha.Constants.ROOM_ID;
import static com.test.busanit.messenger_alpha.Constants.ROOM_MEMBER;
import static com.test.busanit.messenger_alpha.Constants.ROOM_NAME;
import static com.test.busanit.messenger_alpha.Constants.SELF_CHAT;
import static com.test.busanit.messenger_alpha.Constants.TAG;
import static com.test.busanit.messenger_alpha.Constants.TALK;
import static com.test.busanit.messenger_alpha.Constants.TO_ALL;
import static com.test.busanit.messenger_alpha.Constants.TO_ME;

public class RoomActivity extends AppCompatActivity {

    public static Context context;

    private DrawerLayout drawerLayout;

    private NavigationView navigationView;

    private EditText messageEdit;

    private ImageButton imageBtn;

    private ImageButton sendBtn;

    private String roomId;

    private String roomName;

    private String friendId;

    private String friendNick;

    private String chatMode;

    private boolean navigationFlag;

    byte[] bitmapData;

    private MainActivity mainActivity;

    private IntroActivity introActivity;

    public MessageAdapter messageAdapter;

    public NavigationAdapter navigationAdapter;

    public ListView messageListView;

    public ListView navigationListView;

    private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (chatMode.equals(OPEN_CHAT) || chatMode.equals(RANDOM_CHAT)) {
            getMenuInflater().inflate(R.menu.menu_room, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (chatMode.equals(OPEN_CHAT) || chatMode.equals(RANDOM_CHAT)) {
            switch (item.getItemId()) {
                case R.id.menu_roomMemberList:
                    if (!navigationFlag) {
                        drawerLayout.openDrawer(navigationView);
                        navigationFlag = true;
                    } else {
                        drawerLayout.closeDrawer(navigationView);
                        navigationFlag = false;
                    }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        context = this;

        dbManager = DBManager.getInstance(context);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        navigationView = (NavigationView) findViewById(R.id.navigationView);

        messageEdit = (EditText) findViewById(R.id.editText_message);

        imageBtn = (ImageButton) findViewById(R.id.button_imageSend);
        sendBtn = (ImageButton) findViewById(R.id.button_send);

        EventHandler eventHandler = new EventHandler();

        imageBtn.setOnClickListener(eventHandler);
        sendBtn.setOnClickListener(eventHandler);

        messageAdapter = new MessageAdapter(RoomActivity.this, new ArrayList<Message>());

        messageListView = (ListView) findViewById(R.id.listView_message);
        messageListView.setAdapter(messageAdapter);

        navigationAdapter = new NavigationAdapter(context);

        navigationListView = (ListView) findViewById(R.id.listView_navigator);
        navigationListView.setAdapter(navigationAdapter);
        navigationListView.setOnItemClickListener(eventHandler);

        getIntentData();

        setActionBar();

        if (chatMode.equals(TALK) || chatMode.equals(SELF_CHAT)) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }

        introActivity = (IntroActivity) IntroActivity.context;
        introActivity.connector.sendMessage(ROOM_MEMBER + DELIMITER + roomId);
    }

    private void setActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(roomName);
        actionBar.setIcon(R.drawable.ic_launcher);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        chatMode = intent.getStringExtra(CHAT_MODE);

        if (chatMode.equals(OPEN_CHAT)) {
            roomId = intent.getStringExtra(ROOM_ID);
            roomName = intent.getStringExtra(ROOM_NAME);

        } else if (chatMode.equals(RANDOM_CHAT)) {
            roomId = intent.getStringExtra(ROOM_ID);
            roomName = intent.getStringExtra(ROOM_NAME);

        } else if (chatMode.equals(TALK)) {
            roomId = intent.getStringExtra(ROOM_ID);
            friendId = intent.getStringExtra(FRIEND_ID);
            friendNick = intent.getStringExtra(FRIEND_NICK);
            roomName = friendNick;

            displayChatHistory();

        } else if (chatMode.equals(SELF_CHAT)) {
            roomName = intent.getStringExtra(ROOM_NAME);

            displayMyHistory();
        }
    }

    @Override
    public void onBackPressed() {
        if (chatMode.equals(OPEN_CHAT)) {
            openExitRoomDialog(EXIT_ROOM);

        } else if (chatMode.equals(RANDOM_CHAT)) {
            openExitRoomDialog(EXIT_RANDOM_ROOM);

        } else if (chatMode.equals(TALK)) {
            introActivity = (IntroActivity) IntroActivity.context;
            introActivity.connector.isTalking = false;
            introActivity.connector.sendMessage(EXIT_TALK + DELIMITER + roomId);
            introActivity.connector.sendMessage(ROOM_MEMBER + DELIMITER + roomId);

        } else if (chatMode.equals(SELF_CHAT)) {
            finish();
        }
    }

    private void openExitRoomDialog(final String exitRoomFlag) {
        new MaterialDialog.Builder(context).title("방나가기").content("방에서 나가시겠습니까?").backgroundColor(Color.argb(255, 186, 220, 217))
                .titleColor(Color.argb(255, 102, 169, 163)).contentColor(Color.argb(255, 102, 169, 163)).iconRes(R.drawable.gnome_session_logout)
                .positiveColor(Color.argb(255, 102, 169, 163)).dividerColor(Color.argb(255, 102, 169, 163))
                .positiveText("예").negativeText("아니오")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        introActivity = (IntroActivity) IntroActivity.context;
                        introActivity.connector.sendMessage(exitRoomFlag + DELIMITER + roomId);
                        introActivity.connector.sendMessage(ROOM_MEMBER + DELIMITER + roomId);

                    }
                }).show();
    }

    private class EventHandler implements View.OnClickListener, AdapterView.OnItemClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.button_imageSend:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            openGallery();
                        }
                    }).start();

                    break;

                case R.id.button_send:
                    if (chatMode.equals(SELF_CHAT)) {
                        sendMsgToMe();

                    } else {
                        sendMsg();
                    }
                    break;
            }
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            Member member = navigationAdapter.roomMemberList.get(position);
            startActivity(new Intent(context, FriendAddActivity.class).putExtra(MEMBER, member));
        }
    }

    private void displayChatHistory() {
        introActivity = (IntroActivity) IntroActivity.context;
        mainActivity = (MainActivity) MainActivity.context;
        introActivity.connector.sendMessage(CHAT_HISTORY + DELIMITER + mainActivity.userId + DELIMITER + friendId);
    }

    private void displayMyHistory() {

        Cursor cursor = dbManager.selectRecord();
        mainActivity = (MainActivity) MainActivity.context;

        if (cursor.getCount() > 0) {

            messageAdapter.chatMessages.clear();

            while (cursor.moveToNext()) {
                String msg = cursor.getString(cursor.getColumnIndex("msg"));
                String fileName = cursor.getString(cursor.getColumnIndex("img"));

                Message message = new Message();

                message.setTo(TO_ME);
                message.setFromImgData(mainActivity.userImgData);

                if (fileName != null) {
                    byte[] imgData = FileManager.readFromFile(context, fileName);
                    message.setBitmapData(imgData);
                }

                String regDate = cursor.getString(cursor.getColumnIndex("regDate"));

                mainActivity = (MainActivity) MainActivity.context;

                message.setContent(msg);

                message.setDate(regDate);
                message.setMe(true);

                messageAdapter.chatMessages.add(message);
            }
            cursor.close();
            messageAdapter.notifyDataSetChanged();
        }
    }


    private void sendMsg() {
        String content = messageEdit.getText().toString();
        if (TextUtils.isEmpty(content)) {
            return;
        }
        messageEdit.setText("");

        hideKeyboard();

        introActivity = (IntroActivity) IntroActivity.context;
        mainActivity = (MainActivity) MainActivity.context;

        Message message = new Message();
        message.setRoomId(roomId);
        message.setFrom(mainActivity.userId);
        message.setFromNick(mainActivity.userNick);

        if (chatMode.equals(OPEN_CHAT)) {
            message.setTo(TO_ALL);
            message.setToNick(TO_ALL);
            message.setTalkMsg(false);

        } else if (chatMode.equals(RANDOM_CHAT)) {
            message.setTo(ANONYMOUS);
            message.setToNick(ANONYMOUS);
            message.setTalkMsg(false);

        } else if (chatMode.equals(TALK)) {
            message.setTo(friendId);
            message.setToNick(friendNick);
            message.setTalkMsg(true);
        }
        message.setFromImgData(mainActivity.userImgData);
        message.setContent(content);
        message.setDate(DateFormat.getDateTimeInstance().format(new Date()));

        introActivity.connector.sendMessage(message);
    }

    private void sendMsgToMe() {
        String content = messageEdit.getText().toString();
        if (TextUtils.isEmpty(content)) {
            return;
        }
        messageEdit.setText("");

        hideKeyboard();

        mainActivity = (MainActivity) MainActivity.context;

        Message message = new Message();

        message.setTo(TO_ME);
        message.setFromImgData(mainActivity.userImgData);
        message.setContent(content);
        message.setDate(DateFormat.getDateTimeInstance().format(new Date()));

        dbManager.insertRecord(message);

        messageAdapter.chatMessages.add(message);
        messageAdapter.notifyDataSetChanged();

        scroll();
        messageListView.setSelection(messageListView.getCount() - 1);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(messageEdit.getWindowToken(), 0);
    }

    private void openGallery() {
        startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType(CONTENT_TYPE), REQUEST_CODE_OPEN_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_OPEN_GALLERY) {
            try {
                Uri imgUri = data.getData();

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4;

                AssetFileDescriptor fileDescriptor = null;
                fileDescriptor = getContentResolver().openAssetFileDescriptor(imgUri, "r");

                Bitmap bitmapImage = BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, options);

                bitmapImage = Bitmap.createScaledBitmap(bitmapImage, IMAGE_WIDTH, IMAGE_HEIGHT, false);
                bitmapData = getBitmapData(bitmapImage);

                if (bitmapImage != null) {
                    bitmapImage = null;
                }

                if (chatMode.equals(SELF_CHAT)) {
                    mainActivity = (MainActivity) MainActivity.context;

                    Message message = new Message();

                    message.setTo(TO_ME);
                    message.setBitmapData(bitmapData);
                    message.setFromImgData(mainActivity.userImgData);
                    message.setDate(DateFormat.getDateTimeInstance().format(new Date()));

                    dbManager.insertRecord(message);

                    messageAdapter.chatMessages.add(message);
                    messageAdapter.notifyDataSetChanged();

                    scroll();
                    messageListView.setSelection(messageListView.getCount() - 1);

                    return;
                }

                introActivity = (IntroActivity) IntroActivity.context;
                mainActivity = (MainActivity) MainActivity.context;

                Message message = new Message();
                message.setRoomId(roomId);
                message.setFrom(mainActivity.userId);
                message.setFromNick(mainActivity.userNick);

                if (chatMode.equals(OPEN_CHAT)) {
                    message.setTo(TO_ALL);

                } else if (chatMode.equals(RANDOM_CHAT)) {
                    message.setTo(ANONYMOUS);

                } else if (chatMode.equals(TALK)) {
                    message.setTo(friendId);
                }

                if (friendNick != null) {
                    message.setToNick(friendNick);
                } else {
                    message.setToNick(TO_ALL);
                }

                message.setFromImgData(mainActivity.userImgData);
                message.setBitmapData(bitmapData);
                message.setDate(DateFormat.getDateTimeInstance().format(new Date()));

                introActivity.connector.sendMessage(message);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    private byte[] getBitmapData(Bitmap bitmapImage) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.flush();
            stream.close();
            return stream.toByteArray();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

    public void scroll() {
        messageListView.setSelection(messageListView.getCount() - 1);
    }
}