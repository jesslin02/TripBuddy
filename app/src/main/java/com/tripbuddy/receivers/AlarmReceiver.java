package com.tripbuddy.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.tripbuddy.NotificationService;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("AlarmReceiver", "onReceive");
        Toast.makeText(context, "Alarm running", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(context, NotificationService.class);
        context.startService(i);
    }
}
