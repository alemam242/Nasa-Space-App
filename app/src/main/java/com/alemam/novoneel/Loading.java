package com.alemam.novoneel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class Loading extends AppCompatActivity {

    TextView appTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        appTitle = findViewById(R.id.appTitle);


        Animation FadeIn = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        appTitle.startAnimation(FadeIn);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = manager.getActiveNetworkInfo();

                if(networkInfo!=null && networkInfo.isConnected()) {
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                }
                else {
                    Intent intent = new Intent(getApplicationContext(),NoInternet.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        },5000);
    }
}