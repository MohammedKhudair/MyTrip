package com.mohammed.mytrip;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.mohammed.mytrip.callback.CallBack;
import com.mohammed.mytrip.domain.TripManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

    }
    @Override
    protected void onResume() {
        super.onResume();
        TripManager.getInstance().login(new CallBack() {
            @Override
            public void onComplete(boolean isSuccessful) {
                if (isSuccessful) {
                    startActivity(HomeActivity.getStartActivity(SplashActivity.this));
                    finish();
                } else
                    Toast.makeText(SplashActivity.this, R.string.error_loading_data, Toast.LENGTH_SHORT).show();
            }
        });
    }


}