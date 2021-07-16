package com.tripbuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.tripbuddy.databinding.ActivityTripDetailBinding;
import com.tripbuddy.models.Trip;

import org.parceler.Parcels;

public class TripDetailActivity extends AppCompatActivity {
    public static final String TAG = "TripDetailActivity";
    ActivityTripDetailBinding binding;
    Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_trip_detail);

        binding = ActivityTripDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        trip = Parcels.unwrap(getIntent().getParcelableExtra(Trip.class.getSimpleName()));
        Log.d(TAG, String.format("Showing details for '%s'", trip.getTitle()));

        displayDetails();
    }

    private void displayDetails() {
        binding.tvTitle.setText(trip.getTitle());
        binding.tvDestination.setText(trip.getDestination());
        String fullDate = trip.getStart() + " - " + trip.getEnd();
        binding.tvDate.setText(fullDate);

        binding.btnItinerary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.goItineraryActivity(TripDetailActivity.this, trip);
            }
        });

        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TripDetailActivity.this, CreateEventActivity.class);
                i.putExtra(Trip.class.getSimpleName(), Parcels.wrap(trip));
                startActivity(i);
            }
        });
    }
}