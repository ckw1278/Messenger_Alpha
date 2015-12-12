package com.test.busanit.messenger_alpha.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.astuetz.PagerSlidingTabStrip;
import com.github.clans.fab.FloatingActionButton;
import com.test.busanit.messenger_alpha.handler.BackPressCloseHandler;
import com.test.busanit.messenger_alpha.fragment.FriendListFragment;
import com.test.busanit.messenger_alpha.LoginPreference;
import com.test.busanit.messenger_alpha.adapter.MainPagerAdapter;
import com.test.busanit.messenger_alpha.beans.Member;
import com.test.busanit.messenger_alpha.fragment.ProfileFragment;
import com.test.busanit.messenger_alpha.R;
import com.test.busanit.messenger_alpha.beans.Room;
import com.test.busanit.messenger_alpha.fragment.RoomListFragment;
import com.test.busanit.messenger_alpha.handler.SnackbarHandler;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.test.busanit.messenger_alpha.Constants.CHAT_MODE;
import static com.test.busanit.messenger_alpha.Constants.DELIMITER;
import static com.test.busanit.messenger_alpha.Constants.EMPTY;
import static com.test.busanit.messenger_alpha.Constants.ID;
import static com.test.busanit.messenger_alpha.Constants.LOGOUT;
import static com.test.busanit.messenger_alpha.Constants.MEMBER;
import static com.test.busanit.messenger_alpha.Constants.PASS;
import static com.test.busanit.messenger_alpha.Constants.RANDOM_CHAT;
import static com.test.busanit.messenger_alpha.Constants.ROOM_NAME;
import static com.test.busanit.messenger_alpha.Constants.SELF_CHAT;
import static com.test.busanit.messenger_alpha.Constants.TERMINATE_APP;

public class MainActivity extends AppCompatActivity {

    public static Context context;

    private BackPressCloseHandler backPressCloseHandler;

    private List<Fragment> fragments = new ArrayList<>();

    private Toolbar toolbar;

    private PagerSlidingTabStrip tab;

    private ViewPager viewPager;

    private MainPagerAdapter pagerAdapter;

    private IntroActivity introActivity;

    private FloatingActionButton searchFriendBtn;

    private FloatingActionButton openChatBtn;

    private FloatingActionButton randomChatBtn;

    private FloatingActionButton selfChatBtn;

    private EventHandler eventHandler;

    public RoomListFragment roomListFragment;

    public FriendListFragment friendListFragment;

    public ProfileFragment profileFragment;

    public String userId;

    public String userNick;

    public byte[] userImgData;

    public String userMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        getIntentData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:
                openLogoutDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        context = this;

        setToolbar();

        setSupportActionBar(toolbar);

        searchFriendBtn = (FloatingActionButton) findViewById(R.id.floatingbutton_search_friend);
        openChatBtn = (FloatingActionButton) findViewById(R.id.floatingbutton_open_chat);
        randomChatBtn = (FloatingActionButton) findViewById(R.id.floatingbutton_random_chat);
        selfChatBtn = (FloatingActionButton) findViewById(R.id.floatingbutton_self_chat);

        eventHandler = new EventHandler();

        searchFriendBtn.setOnClickListener(eventHandler);
        openChatBtn.setOnClickListener(eventHandler);
        randomChatBtn.setOnClickListener(eventHandler);
        selfChatBtn.setOnClickListener(eventHandler);

        backPressCloseHandler = new BackPressCloseHandler(this);

        addFragment();

        setViewPager();

        setTab();
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("메신저");
        toolbar.setNavigationIcon(R.drawable.chat_black);
        toolbar.setSubtitle("공개채팅");
    }

    private void addFragment() {
        fragments.add(roomListFragment = new RoomListFragment());
        fragments.add(friendListFragment = new FriendListFragment());
        fragments.add(profileFragment = new ProfileFragment());
    }

    private void setViewPager() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        viewPager.setAdapter(pagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), fragments));
    }

    private void setTab() {
        tab = (PagerSlidingTabStrip) findViewById(R.id.tab);
        tab.setTextSize(30);
        tab.setBackgroundColor(Color.argb(255, 186, 220, 217));
        tab.setUnderlineColor(Color.argb(255, 102, 169, 163));
        tab.setIndicatorColor(Color.argb(255, 102, 169, 163));
        tab.setUnderlineHeight(1);

        tab.setOnPageChangeListener(eventHandler);

        tab.setViewPager(viewPager);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        Member member = (Member) intent.getSerializableExtra(MEMBER);

        userId = member.getId();
        userNick = member.getNick();
        userImgData = member.getBitmapData();
        userMsg = member.getMsg();

        Bundle bundle = new Bundle();
        bundle.putSerializable(MEMBER, member);

        roomListFragment.setArguments(bundle);
        profileFragment.setArguments(bundle);
    }

    private class EventHandler implements View.OnClickListener, ViewPager.OnPageChangeListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.floatingbutton_search_friend:
                    searchFriend();
                    break;

                case R.id.floatingbutton_random_chat:
                    openRandomChatDialog();
                    break;

                case R.id.floatingbutton_open_chat:
                    openMakeRoomDialog();
                    break;

                case R.id.floatingbutton_self_chat:
                    openSelfChatRoom();
                    break;
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            if (position == 0) {
                pagerAdapter.tabIcons[0] = R.drawable.chat_black;
                pagerAdapter.tabIcons[1] = R.drawable.friend;
                pagerAdapter.tabIcons[2] = R.drawable.config;

                toolbar.setSubtitle("공개채팅");
                toolbar.setNavigationIcon(R.drawable.chat_black);

            } else if (position == 1) {
                pagerAdapter.tabIcons[0] = R.drawable.chat;
                pagerAdapter.tabIcons[1] = R.drawable.friend_black;
                pagerAdapter.tabIcons[2] = R.drawable.config;

                toolbar.setSubtitle("친구");
                toolbar.setNavigationIcon(R.drawable.friend_black);

            } else if (position == 2) {
                pagerAdapter.tabIcons[0] = R.drawable.chat;
                pagerAdapter.tabIcons[1] = R.drawable.friend;
                pagerAdapter.tabIcons[2] = R.drawable.config_black;

                toolbar.setSubtitle("프로필");
                toolbar.setNavigationIcon(R.drawable.config_black);

            }
            tab.notifyDataSetChanged();
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        introActivity = (IntroActivity) IntroActivity.context;
        introActivity.connector.sendMessage(TERMINATE_APP);
    }

    private void openRandomChatDialog() {
        new MaterialDialog.Builder(context).title("랜덤채팅").content("랜덤방에 연결됩니다. ").backgroundColor(Color.argb(255, 186, 220, 217))
                .titleColor(Color.argb(255, 102, 169, 163)).contentColor(Color.argb(255, 102, 169, 163)).iconRes(R.drawable.gnome_session_logout)
                .positiveColor(Color.argb(255, 102, 169, 163)).dividerColor(Color.argb(255, 102, 169, 163))
                .positiveText("예").negativeText("아니오")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        requestRandomChat();
                    }
                }).show();
    }

    private void openLogoutDialog() {
        new MaterialDialog.Builder(context).title("다른 아이디로 로그인").content("로그아웃 하시겠습니까?").backgroundColor(Color.argb(255, 186, 220, 217))
                .titleColor(Color.argb(255, 102, 169, 163)).contentColor(Color.argb(255, 102, 169, 163)).iconRes(R.drawable.gnome_session_logout)
                .positiveColor(Color.argb(255, 102, 169, 163)).dividerColor(Color.argb(255, 102, 169, 163))
                .positiveText("예").negativeText("아니오")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        logout();
                    }
                }).show();
    }

    private void searchFriend() {
        startActivity(new Intent(context, FriendSearchActivity.class));
    }

    private void openMakeRoomDialog() {
        new MaterialDialog.Builder(context).title("방만들기").backgroundColor(Color.argb(255, 186, 220, 217))
                .widgetColor(Color.argb(255, 186, 220, 217)).positiveColor(Color.argb(255, 102, 169, 163)).dividerColor(Color.argb(255, 102, 169, 163))
                .titleColor(Color.argb(255, 102, 169, 163)).iconRes(R.drawable.chatting_room)
                .inputType(InputType.TYPE_CLASS_TEXT).input("방제목을 입력하세요.", null, new MaterialDialog.InputCallback() {
            @Override
            public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {

                if (TextUtils.isEmpty(charSequence)) {
                    SnackbarHandler.showSingleLineSnackBar(context, "방제목을 입력하세요.");
                    return;
                }
                makeRoom(charSequence.toString());
            }
        }).show();

    }

    private void requestRandomChat() {
        introActivity = (IntroActivity) IntroActivity.context;
        introActivity.connector.sendMessage(RANDOM_CHAT + DELIMITER + userId);
    }

    private void openSelfChatRoom() {
        startActivity(new Intent(context, RoomActivity.class).putExtra(CHAT_MODE, SELF_CHAT).putExtra(ROOM_NAME, userNick));
    }

    private void logout() {
        PendingIntent pendingIntent = PendingIntent.getActivity
                (context, 0, new Intent(MainActivity.this, IntroActivity.class).setAction(LOGOUT), PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis(), pendingIntent);

        LoginPreference preference = new LoginPreference(context);

        preference.put(ID, EMPTY);
        preference.put(PASS, EMPTY);

        //finish();
        System.exit(0);
    }

    private void makeRoom(String roomName) {
        String openDate = DateFormat.getDateTimeInstance().format(new Date());

        introActivity = (IntroActivity) IntroActivity.context;

        Room room = new Room();

        room.setRoomId(userId);
        room.setRoomName(roomName);
        room.setChiefNick(userNick);
        room.setBitmapData(userImgData);
        room.setOpenDate(openDate);
        room.setTalkRoom(false);
        room.setRandomRoom(false);

        introActivity.connector.sendMessage(room);
    }
}