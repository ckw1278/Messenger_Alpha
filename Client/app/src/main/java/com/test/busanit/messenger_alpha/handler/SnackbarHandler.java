package com.test.busanit.messenger_alpha.handler;


import android.content.Context;
import android.graphics.Color;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;

public class SnackbarHandler {

    public static void showSingleLineSnackBar(Context context, String text) {
        SnackbarManager.show(Snackbar.with(context).text(text).color(Color.argb(255, 186, 220, 217)).textColor(Color.parseColor("#FF116D65"))
                .margin(50, 100).actionLabel("확인"));
    }

    public static void showMultiLineSnackBar(Context context, String text) {
        SnackbarManager.show(Snackbar.with(context).text(text).color(Color.argb(255, 186, 220, 217)).textColor(Color.parseColor("#FF116D65"))
                .margin(50, 100).type(SnackbarType.MULTI_LINE).actionLabel("확인"));
    }
}
