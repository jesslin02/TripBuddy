package com.tripbuddy.listeners;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import androidx.core.util.Pair;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.tripbuddy.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateRangeTouchListener implements View.OnTouchListener{
    static final long DAY_MILLIS = 86400000;
    EditText etDate;
    Activity activity;
    Calendar startCal;
    Calendar endCal;
    FragmentManager fragMgr;

    public DateRangeTouchListener(EditText etDate, Activity activity, Calendar startCal,
                                  Calendar endCal, FragmentManager fragMgr) {
        super();
        this.etDate = etDate;
        this.activity = activity;
        this.startCal = startCal;
        this.endCal = endCal;
        this.fragMgr = fragMgr;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            Utils.hideKeyboard(activity);
            MaterialDatePicker.Builder<Pair<Long,Long>> rangePickerBuilder =
                    MaterialDatePicker.Builder.dateRangePicker()
                            .setTitleText("Select dates")
                            .setSelection(
                                    new Pair<>(
                                            startCal.getTimeInMillis() - DAY_MILLIS,
                                            endCal.getTimeInMillis() - DAY_MILLIS
                                    )
                            );
            MaterialDatePicker<Pair<Long,Long>> rangePicker = rangePickerBuilder.build();

            rangePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                @Override
                public void onPositiveButtonClick(Pair<Long, Long> selection) {
                    long startMillis = selection.first + DAY_MILLIS;
                    startCal.setTimeInMillis(startMillis);
                    long endMillis = selection.second + DAY_MILLIS;
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
