package com.tripbuddy;

import androidx.appcompat.app.AppCompatActivity;

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
        // TODO: update Trip.java to return date as string
        binding.tvDate.setText("June 1 - June 8");
    }
}