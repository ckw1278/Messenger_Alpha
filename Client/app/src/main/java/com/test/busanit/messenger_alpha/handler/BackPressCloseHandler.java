package com.test.busanit.messenger_alpha.handler;

import android.app.Activity;

public class BackPressCloseHandler {

    private long backKeyPressedTime = 0;

    private Activity activity;

    public BackPressCloseHandler(Activity activity) {
        this.activity = activity;
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2500) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2500) {
            activity.finish();
        }
    }

    private void showGuide() {
        SnackbarHandler.showMultiLineSnackBar(activity, "뒤로가기를 한번 더 클릭하면 앱이 종료됩니다.");
    }
}