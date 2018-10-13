package com.wolf32.cz3002.howlreal;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.wolf32.cz3002.howlreal.model.News;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationService extends Service {

    private static final int MY_NOTIFICATION_ID = 123;
    Timer timer;
    TimerTask timerTask;
    String TAG = "Timers";
    int Your_X_SECS = 5;


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);

        startTimer();

        return START_STICKY;
    }


    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");


    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        stoptimertask();
        super.onDestroy();


    }

    //we are going to use a handler to be able to run in our TimerTask
    final Handler handler = new Handler();


    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 5000, Your_X_SECS * 1000); //
        //timer.schedule(timerTask, 5000,1000); //
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {

                        //TODO CALL NOTIFICATION FUNC
                        //news_alert();
                        news_notif();
                        stoptimertask();

                    }
                });
            }
        };
    }

    public void news_alert() {

        int uni_notif;

        uni_notif = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_logo)
                .setContentTitle("Breaking News!")
                .setContentText("'Better Day' Is a Much Better Calendar Complication for Your Apple Watch")
                .setAutoCancel(true);

        Intent launchIntent = new Intent();
        launchIntent.setClassName(this, "NotificationService");
        PendingIntent launchPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, launchIntent, PendingIntent.FLAG_ONE_SHOT);
        builder.setContentIntent(launchPendingIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        manager.notify(uni_notif, builder.build());
    }


    public void news_notif(){

        News newsToRead = new News();
        newsToRead.setImageUrl("https://www.straitstimes.com/sites/default/files/styles/article_pictrure_780x520_/public/articles/2018/10/12/colin-hks-12.jpg?itok=CyzzS8-R&timestamp=1539348256");
        newsToRead.setUrl("https://www.straitstimes.com/singapore/outcomes-matter-heng-swee-keat-says-in-response-to-controversial-oxfam-report");
        newsToRead.setSourceName("straitstimes.com");
        newsToRead.setTitle("Outcomes matter, Heng Swee Keat says in response to controversial Oxfam report");
        String url = "https://www.straitstimes.com/singapore/outcomes-matter-heng-swee-keat-says-in-response-to-controversial-oxfam-report";
        newsToRead.setNewsId(url.replace("/", ""));


        Intent myIntent = new Intent(this, ReadNewsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("news", newsToRead);
        myIntent.putExtras(bundle);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                myIntent,
                PendingIntent.FLAG_ONE_SHOT);

        Notification myNotification = new NotificationCompat.Builder(this)
                .setContentTitle("Top News!")
                .setContentText(newsToRead.getTitle())
                .setTicker("Notification!")
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_logo)
                .build();

        NotificationManager notificationManager =
                (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(MY_NOTIFICATION_ID, myNotification);
    }
}