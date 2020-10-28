package com.inclass.app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;
import com.rvalerio.fgchecker.AppChecker;


import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class ForegroundService extends Service {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private AppChecker appChecker;
    public static int count=0;
    public static final String SHARED_PREFS="new";
    public static final String active="active";


    @Override
    public void onCreate() {
        super.onCreate();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("inputExtra");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("App in running")
               // .setContentText(input)
                .setSmallIcon(R.drawable.ic_notify)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
        startChecker();

        //stopSelf();
        return START_NOT_STICKY;
    }
    @Override
    public void onDestroy() {
        appChecker.stop();
        super.onDestroy();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
    private void startChecker() {
        appChecker = new AppChecker();
        appChecker.when("us.zoom.videomeetings", new AppChecker.Listener() {
            @Override
            public void onForeground(String packageName) {
                count+=1;
                Integer c=count/12;
                saveData(c.toString());
            }
        }).when("com.gotomeeting", new AppChecker.Listener() {
            @Override
            public void onForeground(String packageName) {
                count+=1;
                Integer c=count/12;
                saveData(c.toString());
            }
        }).when("com.google.android.apps.meeting", new AppChecker.Listener() {
            @Override
            public void onForeground(String packageName) {
                count+=1;
                Integer c=count/12;
                saveData(c.toString());
            }
        }).when("com.microsoft.teams", new AppChecker.Listener() {
            @Override
            public void onForeground(String packageName) {
                count+=1;
                Integer c=count/12;
                saveData(c.toString());
            }
        })
                .whenOther(new AppChecker.Listener() {
                    @Override
                    public void onForeground(String packageName) {
                        Toast.makeText(getBaseContext(),"Get back to the class", Toast.LENGTH_SHORT).show();
                    }
                })
                .timeout(5000)
                .start(this);
    }
    public void saveData(String s1){
        String s2="Active: "+s1+"m";
        SharedPreferences sharedPreferences= getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editer = sharedPreferences.edit();
        editer.putString(active, s2);
        editer.apply();
    }
}
