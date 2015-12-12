package com.test.busanit.messenger_alpha.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.test.busanit.messenger_alpha.Connector;
import com.test.busanit.messenger_alpha.LoginPreference;
import com.test.busanit.messenger_alpha.handler.BackPressCloseHandler;
import com.test.busanit.messenger_alpha.handler.NotificationHandler;

import static com.test.busanit.messenger_alpha.Constants.AUTO_LOGIN;
import static com.test.busanit.messenger_alpha.Constants.DELIMITER;
import static com.test.busanit.messenger_alpha.Constants.EMPTY;
import static com.test.busanit.messenger_alpha.Constants.ID;
import static com.test.busanit.messenger_alpha.Constants.LOGOUT;
import static com.test.busanit.messenger_alpha.Constants.PASS;
import static com.test.busanit.messenger_alpha.Constants.TERMINATE_APP;

public class IntroActivity extends Activity {

    public static Context context;

    private static final int INTRO_DELAY_TIME = 5000;

    private static final String INTENT_ACTION_MAIN = "android.intent.action.MAIN";

    public Connector connector;

    public NotificationHandler notificationHandler;

    public BackPressCloseHandler backPressCloseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
    }

    @Override
    public void onBackPressed() {
        connector.sendMessage(TERMINATE_APP);
    }

    private void init() {
        context = this;

        notificationHandler = new NotificationHandler(context);
        backPressCloseHandler = new BackPressCloseHandler(this);

        Thread thread = new Thread(connector = new Connector());
        thread.setDaemon(true);
        thread.start();

        String intentAction = getIntent().getAction();

        if (intentAction == INTENT_ACTION_MAIN) {
            try {
                Thread.sleep(INTRO_DELAY_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            processLogin();

        } else if (intentAction == LOGOUT) {
            processLogin();
        }
    }

    private void processLogin() {
        LoginPreference preference = new LoginPreference(context);
        String userId = preference.get(ID, EMPTY);
        String userPass = preference.get(PASS, EMPTY);

        if (userId == EMPTY || userPass == EMPTY) {
            startActivity(new Intent(context, LoginActivity.class));

        } else {
            connector.sendMessage(AUTO_LOGIN + DELIMITER + userId);
        }
        finish();
    }
}