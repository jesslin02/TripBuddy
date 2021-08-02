package com.tripbuddy.listeners;

import android.app.Activity;
import android.os.Parcel;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import androidx.fragment.app.FragmentManager;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.tripbuddy.Utils;
import com.tripbuddy.models.Event;
import com.tripbuddy.models.Trip;

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
    /* calendar object representing date (start or end) being chosen for event */
    Calendar cal;
    /* start date of trip, used to set constraints */
    long tripStartMillis;
    /* end date of trip, used to set constraints */
    long tripEndMillis;
    FragmentManager fragMgr;

    public DateTouchListener(EditText etDate, Activity activity, Calendar cal, Trip trip,
                             FragmentManager fragMgr) {
        super();
        this.etDate = etDate;
        this.activity = activity;
        this.cal = cal;
        this.fragMgr = fragMgr;

        Calendar tripStart = Calendar.getInstance();
        tripStart.setTime(trip.getStartDate());
        tripStartMillis = tripStart.getTimeInMillis() - DAY_MILLIS;
        Calendar tripEnd = Calendar.getInstance();
        tripEnd.setTime(trip.getEndDate());
        tripEndMillis = tripEnd.getTimeInMillis() - DAY_MILLIS;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            Utils.hideKeyboard(activity);

            CalendarConstraints constraints = new CalendarConstraints.Builder()
                    // limits which months users can see
                    .setStart(tripStartMillis)
                    .setEnd(tripEndMillis)
                    // limits which dates that users can select
                    .setValidator(new CalendarConstraints.DateValidator() {
                        @Override
                        public boolean isValid(long date) {
                            return date >= tripStartMillis && date <= tripEndMillis;
                        }

                        @Override
                        public int describeContents() {
                            return 0;
                        }

                        @Override
                        public void writeToParcel(Parcel dest, int flags) {
                            // empty
                        }
                    })
                    .build();

            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select date")
                    .setSelection(cal.getTimeInMillis() - DAY_MILLIS)
                    .setCalendarConstraints(constraints)
                    .build();

            datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                @Override
                public void onPositiveButtonClick(Long selection) {
                    cal.setTimeInMillis(selection + DAY_MILLIS);
                    etDate.setText(Utils.DATE_FORMAT.format(cal.getTime()));
                }
            });

            datePicker.show(fragMgr, "date picker");

            return true;
        }
        return false;
    }
}
