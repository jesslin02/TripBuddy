package com.tripbuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import com.tripbuddy.databinding.ActivityItineraryFilterBinding;
import com.tripbuddy.models.Trip;

import org.parceler.Parcels;

public class ItineraryFilterActivity extends AppCompatActivity {
    ActivityItineraryFilterBinding binding;
    Trip trip;
    boolean ascending;

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

        binding.btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.radioAscending.setChecked(true);
                binding.radioDescending.setChecked(false);
                binding.etDates.setText("");
            }
        });

        binding.btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ItineraryFilterActivity.this, ItineraryActivity.class);
                i.putExtra("ascending", ascending);
                i.putExtra(Trip.class.getSimpleName(), Parcels.wrap(trip));
                startActivity(i);
                finish();
            }
        });

        getSupportActionBar().setTitle(trip.getTitle());
    }
}