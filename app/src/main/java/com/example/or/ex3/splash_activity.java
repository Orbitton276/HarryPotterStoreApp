package com.example.or.ex3;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.or.ex3.Analytics.AnalyticsManager;

public class splash_activity extends AppCompatActivity {

    public static final int SPLASH_TIME = 4000;
    public static final String TAG = "inSplash >>";
    MediaPlayer mySong ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);


        //AnalyticsManager.getInstance().init(getApplicationContext());

        mySong = MediaPlayer.create(splash_activity.this,R.raw.harry_potter_theme);
        mySong.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(splash_activity.this, signIn_activity.class);
                startActivity(intent);
                finish();

            }

        }, SPLASH_TIME);

    }

}
