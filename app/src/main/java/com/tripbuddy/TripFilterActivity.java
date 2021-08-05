package com.tripbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.tripbuddy.databinding.ActivityTripFilterBinding;
import com.tripbuddy.listeners.DateRangeTouchListener;

import java.util.Calendar;

public class TripFilterActivity extends AppCompatActivity {
    ActivityTripFilterBinding binding;
    boolean ascending;
    Calendar startCal;
    Calendar endCal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTripFilterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

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
            endCal = Calendar.getInstance();
        }

        setListeners();

        getSupportActionBar().setTitle("Filter trips");
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
                startCal, endCal, getSupportFragmentManager()));

        binding.btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.radioAscending.setChecked(true);
                binding.radioDescending.setChecked(false);
                binding.etDates.setText("");
                startCal = Calendar.getInstance();
                endCal = Calendar.getInstance();
            }
        });

        binding.btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TripFilterActivity.this, MainActivity.class);
                i.putExtra("ascending", ascending);
                if (startCal.getTimeInMillis() != endCal.getTimeInMillis()) {
                    i.putExtra("filterStart", startCal);
                    i.putExtra("filterEnd", endCal);
                }
                startActivity(i);
                finish();
            }
        });
    }
}