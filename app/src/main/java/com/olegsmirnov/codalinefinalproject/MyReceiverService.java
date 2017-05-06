package com.olegsmirnov.codalinefinalproject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

public class MyReceiverService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        Context context = getApplicationContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Intent myIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, myIntent, 0);
        builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Time to wash")
                .setAutoCancel(true)
                .setContentText("You can wash your auto today");
        if (sharedPreferences.getBoolean(getString(R.string.prefs_vibro_notifications), false)) {
            builder.setVibrate(new long[]{100, 500});
        }
        if (sharedPreferences.getBoolean(getString(R.string.prefs_sound_notifications), false)) {
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        }
        Notification notification = builder.build();
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
        Utils.changeSettings(sharedPreferences, getString(R.string.is_notification_alive), false);
        stopService(myIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}