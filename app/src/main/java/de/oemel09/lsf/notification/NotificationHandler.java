package de.oemel09.lsf.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import de.oemel09.lsf.MainActivity;
import de.oemel09.lsf.R;

public class NotificationHandler {

    private static final String CHANNEL_ID_NEW_GRADES_NOTIFICATION = "LOADED_NEW_GRADES";
    private static final int NOTIFICATION_ID = 1509;

    private final Context context;
    private final NotificationManager notificationManager;

    public NotificationHandler(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void showNotification(String text) {
        Notification notification = createNotification(text);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private Notification createNotification(String text) {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        return new NotificationCompat.Builder(context, createNotificationChannel())
                .setContentTitle(context.getText(R.string.app_name))
                .setContentText(text)
                .setSmallIcon(R.drawable.menu_ic_search)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
    }

    private String createNotificationChannel() {
        if (channelMustBeCreated()) {
            String name = context.getString(R.string.app_name);
            String description = context.getString(R.string.channel_description_info);
            createNotificationChannel(name, description,
                    CHANNEL_ID_NEW_GRADES_NOTIFICATION, NotificationManager.IMPORTANCE_HIGH);
        }
        return CHANNEL_ID_NEW_GRADES_NOTIFICATION;
    }

    private void createNotificationChannel(String name, String description, String id, int importance) {
        if (channelMustBeCreated()) {
            NotificationChannel channel = new NotificationChannel(id, name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private boolean channelMustBeCreated() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }
}
