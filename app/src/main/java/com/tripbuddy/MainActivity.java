package com.tripbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tripbuddy.fragments.ProfileFragment;
import com.tripbuddy.fragments.TripsFragment;
import com.tripbuddy.receivers.AlarmReceiver;

import java.util.Calendar;

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

        // createNotification(20, R.drawable.map, "Test", "Notif Body", "reminders");
    }

    //  createNotification(56, R.drawable.ic_launcher, "New Message",
    //      "There is a new message from Bob!");
    private void createNotification(int nId, int iconRes, String title, String body, String channelId) {
        // First let's define the intent to trigger when notification is selected
        // Start out by creating a normal intent (in this case to open an activity)
        Intent intent = new Intent(this, MainActivity.class);
        // Next, let's turn this into a PendingIntent using
        //   public static PendingIntent getActivity(Context context, int requestCode,
        //       Intent intent, int flags)
        int requestID = (int) System.currentTimeMillis(); //unique requestID to differentiate between various notification with same NotifId
        int flags = PendingIntent.FLAG_CANCEL_CURRENT; // cancel old intent and create new one
        PendingIntent pIntent = PendingIntent.getActivity(this, requestID, intent, flags);

        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(
                this, channelId).setSmallIcon(iconRes)
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(pIntent)
                .setAutoCancel(true);

        // mId allows you to update the notification later on.
        notifManager.notify(nId, notifBuilder.build());
    }


}