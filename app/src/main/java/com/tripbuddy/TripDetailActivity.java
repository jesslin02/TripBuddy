package com.tripbuddy;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

        binding = ActivityTripDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        trip = Parcels.unwrap(getIntent().getParcelableExtra(Trip.class.getSimpleName()));
        Log.d(TAG, String.format("Showing details for '%s'", trip.getTitle()));

        getSupportActionBar().setTitle(trip.getTitle());
        displayDetails();
    }

    private void displayDetails() {
        binding.tvTitle.setText(trip.getTitle());
        binding.tvDestination.setText(trip.getDestination());
        String fullDate = Utils.DATE_FORMAT.format(trip.getStartDate()) + " - "
                + Utils.DATE_FORMAT.format(trip.getEndDate());
        binding.tvDate.setText(fullDate);

        String notes = trip.getNotes();
        if (notes != null && !notes.isEmpty()) {
            binding.tvNoteTitle.setVisibility(View.VISIBLE);
            binding.tvNotes.setText(trip.getNotes());
            binding.tvNotes.setVisibility(View.VISIBLE);
        }

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
        } else if (item.getItemId() == android.R.id.home) {
            supportFinishAfterTransition();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onEditButton() {
        Intent i = new Intent(this, CreateTripActivity.class);
        i.putExtra(Trip.class.getSimpleName(), Parcels.wrap(trip));
        i.putExtra("edit", true);
        startActivity(i);
        finish();
    }
}