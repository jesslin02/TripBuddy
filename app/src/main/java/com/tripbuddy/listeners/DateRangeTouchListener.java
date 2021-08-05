package com.tripbuddy.listeners;

import android.app.Activity;
import android.os.Parcel;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import androidx.core.util.Pair;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.tripbuddy.Utils;
import com.tripbuddy.models.Trip;

import java.util.Calendar;

public class DateRangeTouchListener implements View.OnTouchListener{
    static final long HOUR_MILLIS = 3600000;
    /* 7 hours from UTC to PDT */
    static final long TIMEZONE_OFFSET = HOUR_MILLIS * 7;
    static final long DAY_MILLIS = HOUR_MILLIS * 24;
    EditText etDate;
    Activity activity;
    Calendar startCal;
    Calendar endCal;
    FragmentManager fragMgr;
    /* start date of trip, used to set constraints */
    long tripStartMillis;
    /* end date of trip, used to set constraints */
    long tripEndMillis;

    /* for use when creating a new trip */
    public DateRangeTouchListener(EditText etDate, Activity activity, Calendar startCal,
                                  Calendar endCal, FragmentManager fragMgr) {
        super();
        this.etDate = etDate;
        this.activity = activity;
        this.startCal = startCal;
        this.endCal = endCal;
        this.fragMgr = fragMgr;
    }

    /* for use when filtering events by date */
    public DateRangeTouchListener(EditText etDate, Activity activity, Calendar startCal,
                                  Calendar endCal, Trip trip, FragmentManager fragMgr) {
        super();
        this.etDate = etDate;
        this.activity = activity;
        this.startCal = startCal;
        this.endCal = endCal;
        this.fragMgr = fragMgr;

        Calendar tripStart = Calendar.getInstance();
        tripStart.setTime(trip.getStartDate());
        tripStartMillis = tripStart.getTimeInMillis() - TIMEZONE_OFFSET;

        Calendar tripEnd = Calendar.getInstance();
        tripEnd.setTime(trip.getEndDate());
        tripEndMillis = tripEnd.getTimeInMillis() - TIMEZONE_OFFSET;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            Utils.hideKeyboard(activity);

            CalendarConstraints constraints;

            if (tripStartMillis == 0L) {
                constraints = new CalendarConstraints.Builder().build();
            } else {
                constraints = new CalendarConstraints.Builder()
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
            }

            MaterialDatePicker.Builder<Pair<Long,Long>> rangePickerBuilder =
                    MaterialDatePicker.Builder.dateRangePicker()
                            .setTitleText("Select dates")
                            .setCalendarConstraints(constraints)
                            .setSelection(
                                    new Pair<>(
                                            startCal.getTimeInMillis() - TIMEZONE_OFFSET,
                                            endCal.getTimeInMillis() - TIMEZONE_OFFSET
                                    )
                            );
            MaterialDatePicker<Pair<Long,Long>> rangePicker = rangePickerBuilder.build();

            rangePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                @Override
                public void onPositiveButtonClick(Pair<Long, Long> selection) {
                    long startMillis = selection.first + TIMEZONE_OFFSET;
                    startCal.setTimeInMillis(startMillis);
                    long endMillis = selection.second + TIMEZONE_OFFSET + DAY_MILLIS - 1;
                    endCal.setTimeInMillis(endMillis);
                    etDate.setText(Utils.DATE_FORMAT.format(startCal.getTime()) + " - "
                            + Utils.DATE_FORMAT.format(endCal.getTime()));
                }
            });
            rangePicker.show(fragMgr, "range date picker");
            return true;
        }
        return false;
    }
}
