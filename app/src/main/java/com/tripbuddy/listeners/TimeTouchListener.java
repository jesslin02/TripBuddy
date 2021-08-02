package com.tripbuddy.listeners;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import androidx.fragment.app.FragmentManager;

import com.google.android.material.timepicker.MaterialTimePicker;
import com.tripbuddy.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * used as onTouchListener for inputting the start time and end time
 * allows popup time picker
 */
public class TimeTouchListener implements View.OnTouchListener {
    EditText etTime;
    Activity activity;
    Calendar cal;
    FragmentManager fragMgr;

    public TimeTouchListener(EditText etTime, Activity activity, Calendar cal, FragmentManager fragMgr) {
        super();
        this.etTime = etTime;
        this.activity = activity;
        this.cal = cal;
        this.fragMgr = fragMgr;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            Utils.hideKeyboard(activity);
            final Calendar cldr = Calendar.getInstance();
            int hour = cldr.get(Calendar.HOUR_OF_DAY);
            int min = cldr.get(Calendar.MINUTE);

            MaterialTimePicker picker = new MaterialTimePicker.Builder()
                    .setHour(hour)
                    .setMinute(min)
                    .setTitleText("Select time")
                    .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
                    .build();

            picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SimpleDateFormat sdFormat = new SimpleDateFormat("h:mm a");
                    cal.set(Calendar.HOUR_OF_DAY, picker.getHour());
                    cal.set(Calendar.MINUTE, picker.getMinute());
                    etTime.setText(sdFormat.format(cal.getTime()));
                }
            });

            picker.show(fragMgr, "time picker");

            return true;
        }
        return false;
    }
}
