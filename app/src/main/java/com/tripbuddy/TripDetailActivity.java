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

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.tripbuddy.databinding.ActivityTripDetailBinding;
import com.tripbuddy.models.Event;
import com.tripbuddy.models.Trip;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TripDetailActivity extends AppCompatActivity {
    public static final String TAG = "TripDetailActivity";
    ActivityTripDetailBinding binding;
    Trip trip;
    MenuItem editTrip;
    Event nextEvent;

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
            binding.notesLayout.setVisibility(View.VISIBLE);
            binding.tvNotes.setText(trip.getNotes());
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

        getNextEvent();
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

    private void getNextEvent() {
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        // include data referred by user key
        query.include(Event.KEY_USER);
        query.include(Event.KEY_TRIP);
        query.whereEqualTo(Event.KEY_USER, ParseUser.getCurrentUser());
        query.whereEqualTo(Event.KEY_TRIP, trip);
        query.whereGreaterThanOrEqualTo(Event.KEY_START, Calendar.getInstance().getTime());
        query.setLimit(1);
        query.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> events, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with retrieving events", e);
                    return;
                }
                if (events.size() > 0) {
                    nextEvent = events.get(0);
                    binding.btnNext.setText("Next up: " + nextEvent.getTitle());
                    binding.btnNext.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(TripDetailActivity.this, EventDetailActivity.class);
                            i.putExtra(Event.class.getSimpleName(), Parcels.wrap(nextEvent));
                            startActivity(i);
                        }
                    });
                    binding.btnNext.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}