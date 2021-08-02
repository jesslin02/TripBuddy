package com.tripbuddy.listeners;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import androidx.fragment.app.FragmentManager;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.tripbuddy.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * used as onTouchListener for inputting the start date and end date
 * allows popup calendar date picker
 * https://www.tutlane.com/tutorial/android/android-datepicker-with-examples
 * CreateEventActivity
 */
public class DateTouchListener implements View.OnTouchListener {
    static final long DAY_MILLIS = 86400000;
    EditText etDate;
    Activity activity;
    Calendar cal;
    FragmentManager fragMgr;

    public DateTouchListener(EditText etDate, Activity activity, Calendar cal, FragmentManager fragMgr) {
        super();
        this.etDate = etDate;
        this.activity = activity;
        this.cal = cal;
        this.fragMgr = fragMgr;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            Utils.hideKeyboard(activity);

            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build();

            datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                @Override
                public void onPositiveButtonClick(Long selection) {
                    cal.setTimeInMillis(selection + DAY_MILLIS);
                    SimpleDateFormat sdFormat = new SimpleDateFormat("MMM d, yyyy");
                    etDate.setText(sdFormat.format(cal.getTime()));
                }
            });

            datePicker.show(fragMgr, "date picker");

            return true;
        }
        return false;
    }
}
