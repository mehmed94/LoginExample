package com.beginners.loginexample;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Runnable runnable3secs = new Runnable() {
            @Override
            public void run() {
                //nextActivity();
                finish();
            }
        };

        Handler myHandler = new Handler();
        myHandler.postDelayed(runnable3secs,3000);

        //same as previous code
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                nextActivity();
//                finish();
//            }
//        },3000);

    }

}
