package com.tripbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tripbuddy.fragments.ProfileFragment;
import com.tripbuddy.fragments.TripsFragment;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    BottomNavigationView bottomNavigation;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigation = findViewById(R.id.bottom_navigation);
        fragmentManager = getSupportFragmentManager();

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                String fragmentTag;
                switch (item.getItemId()) {
                    case R.id.action_home:
                        fragment = new TripsFragment();
                        fragmentTag = "trips";
                        break;
                    case R.id.action_profile:
                        fragment = new ProfileFragment();
                        fragmentTag = "profile";
                        break;
                    default:
                        fragment = new TripsFragment();
                        fragmentTag = "trips";
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment, fragmentTag).commit();
                return true;
            }
        });

        bottomNavigation.setSelectedItemId(R.id.action_home);
    }
}