package com.test.busanit.messenger_alpha.handler;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.test.busanit.messenger_alpha.R;
import com.test.busanit.messenger_alpha.activity.RoomActivity;

public class NotificationHandler {

    private Context context;

    private static final int NOTIFICATION_ID = 9999;

    public NotificationHandler(Context context) {
        this.context = context;
    }

    public void notificate(String nick, String msg) {

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, RoomActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context).setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(nick + " 으로부터의 메세지").setWhen(System.currentTimeMillis()).setContentTitle(nick)
                .setContentText(msg).setDefaults(Notification.DEFAULT_ALL).setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
