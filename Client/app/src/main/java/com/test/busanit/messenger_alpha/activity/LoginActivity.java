package com.test.busanit.messenger_alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.test.busanit.messenger_alpha.handler.BackPressCloseHandler;
import com.test.busanit.messenger_alpha.handler.NotificationHandler;
import com.test.busanit.messenger_alpha.R;
import com.test.busanit.messenger_alpha.handler.SnackbarHandler;

import static com.test.busanit.messenger_alpha.Constants.DELIMITER;
import static com.test.busanit.messenger_alpha.Constants.EMPTY;
import static com.test.busanit.messenger_alpha.Constants.LOGIN;

public class LoginActivity extends FragmentActivity {

    public static Context context;

    private EditText idEdt;

    private EditText passEdt;

    private Button loginBtn;

    private Button joinBtn;

    private IntroActivity introActivity;

    private BackPressCloseHandler backPressCloseHandler;

    NotificationHandler notificationHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
    }

    private void init() {
        context = this;

        backPressCloseHandler = new BackPressCloseHandler(this);

        notificationHandler = new NotificationHandler(context);

        idEdt = (EditText) findViewById(R.id.editText_id);
        passEdt = (EditText) findViewById(R.id.editText_password);

        loginBtn = (Button) findViewById(R.id.button_login);
        joinBtn = (Button) findViewById(R.id.button_join);

        EventHandler eventHandler = new EventHandler();
        loginBtn.setOnClickListener(eventHandler);
        joinBtn.setOnClickListener(eventHandler);
    }

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

    private class EventHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.button_login:
                    String userId = idEdt.getText().toString();
                    String userPass = passEdt.getText().toString();

                    if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(userPass)) {
                        SnackbarHandler.showSingleLineSnackBar(context, "아이디와 패스워드를 확인하세요.");
                        return;
                    }
                    introActivity = (IntroActivity) IntroActivity.context;

                    introActivity.connector.sendMessage(LOGIN + DELIMITER + userId + DELIMITER + userPass);

                    idEdt.setText(EMPTY);
                    passEdt.setText(EMPTY);

                    break;

                case R.id.button_join:
                    startActivity(new Intent(context, JoinActivity.class));

                    idEdt.setText(EMPTY);
                    passEdt.setText(EMPTY);
                    break;
            }
        }
    }
}