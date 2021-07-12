package com.tripbuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.tripbuddy.databinding.ActivityEventDetailBinding;
import com.tripbuddy.databinding.ActivityTripDetailBinding;
import com.tripbuddy.models.Event;
import com.tripbuddy.models.Trip;

import org.parceler.Parcels;

public class EventDetailActivity extends AppCompatActivity {
    public static final String TAG = "EventDetailActivity";
    ActivityEventDetailBinding binding;
    Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_event_detail);

        binding = ActivityEventDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        event = Parcels.unwrap(getIntent().getParcelableExtra(Event.class.getSimpleName()));
        Log.d(TAG, String.format("Showing details for '%s'", event.getTitle()));

        displayDetails();
    }

    private void displayDetails() {
        binding.tvTitle.setText(event.getTitle());
        binding.tvLocation.setText(event.getLocation());
        // TODO: update Event.java to return date as string
        binding.tvDate.setText("June 2");
        binding.tvTime.setText("8:00 AM - 11:00 AM");
    }
}