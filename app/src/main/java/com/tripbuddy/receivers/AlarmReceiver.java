package com.tripbuddy.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tripbuddy.NotificationService;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("AlarmReceiver", "onReceive");
        Bundle bundle = intent.getExtras();

        Intent notifIntent = new Intent(context, NotificationService.class);
        notifIntent.putExtras(bundle);
        NotificationService.enqueueWork(context, notifIntent);
    }
}
