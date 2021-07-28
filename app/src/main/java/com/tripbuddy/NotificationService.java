package com.tripbuddy;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import androidx.core.app.NotificationCompat;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class NotificationService extends IntentService {
    static final String TAG = "NotificationService";
    static int NOTIFICATION_ID = 1;
    NotificationManager notifManager;

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "onHandleIntent");
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
                .setContentTitle("test title")
                .setContentText("test body")
                .setContentIntent(pIntent)
                .setAutoCancel(true);

        // mId allows you to update the notification later on.
        notifManager.notify(NOTIFICATION_ID, notifBuilder.build());
    }
}