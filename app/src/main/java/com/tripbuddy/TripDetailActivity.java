package com.tripbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.tripbuddy.databinding.ActivityTripDetailBinding;
import com.tripbuddy.models.Trip;

import org.parceler.Parcels;

public class TripDetailActivity extends AppCompatActivity {
    public static final String TAG = "TripDetailActivity";
    ActivityTripDetailBinding binding;
    Trip trip;
    MenuItem editTrip;

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
        binding.tvNotes.setText(trip.getNotes());
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
                i.putExtra("edit", false);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(@NonNull Menu menu) {
        Log.d(TAG, "onPrepareOptionsMenu");
        editTrip = menu.findItem(R.id.edit);
        editTrip.getIcon().setTint(Color.WHITE);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.edit) {
            onEditButton();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onEditButton() {
        Intent i = new Intent(this, CreateTripActivity.class);
        i.putExtra(Trip.class.getSimpleName(), Parcels.wrap(trip));
        i.putExtra("edit", true);
        startActivity(i);
    }
}