package com.tripbuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import com.tripbuddy.databinding.ActivityItineraryFilterBinding;
import com.tripbuddy.listeners.DateRangeTouchListener;
import com.tripbuddy.models.Trip;

import org.parceler.Parcels;

import java.util.Calendar;

public class ItineraryFilterActivity extends AppCompatActivity {
    ActivityItineraryFilterBinding binding;
    Trip trip;
    boolean ascending;
    Calendar startCal;
    Calendar endCal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_itinerary_filter);

        binding = ActivityItineraryFilterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        trip = Parcels.unwrap(getIntent().getParcelableExtra(Trip.class.getSimpleName()));
        ascending = getIntent().getBooleanExtra("ascending", true);
        if (ascending) {
            binding.radioAscending.setChecked(true);
        } else {
            binding.radioDescending.setChecked(true);
        }
        Calendar filterStart = (Calendar) getIntent().getSerializableExtra("filterStart");
        Calendar filterEnd = (Calendar) getIntent().getSerializableExtra("filterEnd");
        if (filterStart != null) {
            startCal = filterStart;
            endCal = filterEnd;
            binding.etDates.setText(Utils.DATE_FORMAT.format(startCal.getTime()) + " - "
                    + Utils.DATE_FORMAT.format(endCal.getTime()));
        } else {
            startCal = Calendar.getInstance();
            startCal.setTime(trip.getStartDate());
            endCal = Calendar.getInstance();
            endCal.setTime(trip.getEndDate());
        }

        setListeners();

        getSupportActionBar().setTitle(trip.getTitle());
    }

    private void setListeners() {
        binding.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View checkedButton = group.findViewById(checkedId);
                int index = group.indexOfChild(checkedButton);
                if (index == 0) {
                    ascending = true;
                } else {
                    ascending = false;
                }
            }
        });

        binding.etDates.setOnTouchListener(new DateRangeTouchListener(binding.etDates, this,
                startCal, endCal, trip, getSupportFragmentManager()));

        binding.btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.radioAscending.setChecked(true);
                binding.radioDescending.setChecked(false);
                binding.etDates.setText("");
                startCal.setTime(trip.getStartDate());
                endCal.setTime(trip.getEndDate());
            }
        });

        binding.btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ItineraryFilterActivity.this, ItineraryActivity.class);
                i.putExtra("ascending", ascending);
                i.putExtra(Trip.class.getSimpleName(), Parcels.wrap(trip));
                if (filtered()) {
                    i.putExtra("filterStart", startCal);
                    i.putExtra("filterEnd", endCal);
                }
                startActivity(i);
                finish();
            }
        });
    }

    /* checks if user has chosen to filter events by date */ 
    private boolean filtered() {
        Calendar tripStart = Calendar.getInstance();
        tripStart.setTime(trip.getStartDate());
        Calendar tripEnd = Calendar.getInstance();
        tripEnd.setTime(trip.getEndDate());

        return startCal.getTimeInMillis() != tripStart.getTimeInMillis()
                || endCal.getTimeInMillis() != tripEnd.getTimeInMillis();
    }
}