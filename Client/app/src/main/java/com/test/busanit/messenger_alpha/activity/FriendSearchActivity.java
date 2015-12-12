package com.test.busanit.messenger_alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.test.busanit.messenger_alpha.beans.Member;
import com.test.busanit.messenger_alpha.adapter.MemberListAdapter;
import com.test.busanit.messenger_alpha.R;
import com.test.busanit.messenger_alpha.handler.SnackbarHandler;

import static com.test.busanit.messenger_alpha.Constants.DELIMITER;
import static com.test.busanit.messenger_alpha.Constants.EMPTY;
import static com.test.busanit.messenger_alpha.Constants.MEMBER;
import static com.test.busanit.messenger_alpha.Constants.SEARCH_FRIEND;

public class FriendSearchActivity extends AppCompatActivity {

    public static Context context;

    private ListView memberListView;

    private EditText searchIdEdt;

    private Button searchOkBtn;

    private IntroActivity introActivity;

    public MemberListAdapter memberListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_search);

        init();
    }

    private void init() {

        context = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_on_friendSearch);
        toolbar.setTitle("친구찾기");
        setSupportActionBar(toolbar);

        searchIdEdt = (EditText) findViewById(R.id.edittext_search_id);

        EventHandler eventHandler = new EventHandler();

        searchOkBtn = (Button) findViewById(R.id.button_search_ok);
        searchOkBtn.setOnClickListener(eventHandler);

        memberListAdapter = new MemberListAdapter(context);

        memberListView = (ListView) findViewById(R.id.listView_search_member);
        memberListView.setAdapter(memberListAdapter);

        memberListView.setOnItemClickListener(eventHandler);

    }

    private class EventHandler implements View.OnClickListener, AdapterView.OnItemClickListener {
        @Override
        public void onClick(View view) {
            String searchId = searchIdEdt.getText().toString().trim();

            if (TextUtils.isEmpty(searchId)) {
                SnackbarHandler.showSingleLineSnackBar(context, "검색할 아이디를 입력하세요.");
                return;
            }

            introActivity = (IntroActivity) IntroActivity.context;
            introActivity.connector.sendMessage(SEARCH_FRIEND + DELIMITER + searchId);

            searchIdEdt.setText(EMPTY);
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            Member member = memberListAdapter.memberList.get(position);
            startActivity(new Intent(context, FriendAddActivity.class).putExtra(MEMBER, member));
        }
    }
}