package com.mohammed.mytrip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;
import com.mohammed.mytrip.fragment.MapContainerFragment;

public class HomeActivity extends AppCompatActivity {
    private MapContainerFragment mapsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();

        mapsFragment = (MapContainerFragment) fragmentManager.findFragmentById(R.id.map_container_fragment);

    }

    public static Intent getStartActivity(SplashActivity splashActivity) {
        return new Intent(splashActivity, HomeActivity.class);
    }


}