package com.tripbuddy;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tripbuddy.fragments.ProfileFragment;
import com.tripbuddy.fragments.TripsFragment;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    BottomNavigationView bottomNavigation;
    FragmentManager fragmentManager;
    MenuItem addTrip;
    MenuItem search;
    NotificationChannel channel;
    NotificationManager notifManager;

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

        setupNotifs();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(@NonNull Menu menu) {
        Log.d(TAG, "onPrepareOptionsMenu");
        addTrip = menu.findItem(R.id.add);
        addTrip.setVisible(false);
        search = menu.findItem(R.id.action_search);
        search.setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.add) {
//            return false;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    private void setupNotifs() {
        // Configure the channel
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        channel = new NotificationChannel("reminders", "Event Reminders", importance);
        channel.setDescription("Reminders About Upcoming Events");
        // Register the channel with the notifications manager
        notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.createNotificationChannel(channel);
    }




}