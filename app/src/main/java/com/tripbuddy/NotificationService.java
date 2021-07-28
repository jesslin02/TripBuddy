package com.tripbuddy;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;

import org.jetbrains.annotations.NotNull;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class NotificationService extends JobIntentService {
    static final String TAG = "NotificationService";
    static final int JOB_ID = 1;
    NotificationManager notifManager;

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, NotificationService.class, JOB_ID, intent);
    }

    @Override
    protected void onHandleWork(@NotNull Intent intent) {
        Log.i(TAG, "onHandleWork");
        Bundle extras = intent.getExtras();
        String eventTitle = extras.getString("event");
        String eventLocation = extras.getString("location");
        int eventTime = extras.getInt("time");

        Context context = getApplicationContext();
        notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // First let's define the intent to trigger when notification is selected
        // Start out by creating a normal intent (in this case to open an activity)
        Intent afterClicked = new Intent(this, MainActivity.class);
        // Bundle bundle = new Bundle();
        // bundle.putString("test", "test");
        // afterClicked.putExtras(bundle);

        // Next, let's turn this into a PendingIntent using
        //   public static PendingIntent getActivity(Context context, int requestCode,
        //       Intent intent, int flags)
        int requestID = (int) System.currentTimeMillis(); //unique requestID to differentiate between various notification with same NotifId
        int flags = PendingIntent.FLAG_CANCEL_CURRENT; // cancel old intent and create new one
        PendingIntent pIntent = PendingIntent.getActivity(this, requestID, afterClicked, flags);

        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(
                this, "reminders").setSmallIcon(R.drawable.map)
                .setContentTitle(eventTitle + " happening now")
                .setContentText(eventLocation)
                .setContentIntent(pIntent)
                .setAutoCancel(true);

        // id allows you to update the notification later on.
        notifManager.notify(eventTime, notifBuilder.build());
    }
}