package com.semantic.ecare_android_v2.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.semantic.ecare_android_v2.R;

public class Splash extends AppCompatActivity {

    private final int WAIT_TIMER = 2500;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable(){

            @Override
            public void run() {
                Intent intent = new Intent(Splash.this, LoginActivity.class);
                Splash.this.startActivity(intent);
                Splash.this.finish();
            }
        }, WAIT_TIMER);
    }
}
