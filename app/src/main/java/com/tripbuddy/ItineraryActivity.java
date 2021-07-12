package com.tripbuddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.tripbuddy.models.Event;
import com.tripbuddy.models.Trip;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class ItineraryActivity extends AppCompatActivity {
    public static final String TAG = "AppCompatActivity";
    RecyclerView rvEvents;
    LinearLayoutManager llManager;
    ItineraryAdapter adapter;
    List<Event> allEvents;
    Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary);

        trip = Parcels.unwrap(getIntent().getParcelableExtra(Trip.class.getSimpleName()));
        Log.d(TAG, String.format("Showing itinerary for '%s'", trip.getTitle()));

        rvEvents = findViewById(R.id.rvEvents);
        allEvents = new ArrayList<>();
        adapter = new ItineraryAdapter(this, allEvents);

        rvEvents.setAdapter(adapter);
        llManager = new LinearLayoutManager(this);
        rvEvents.setLayoutManager(llManager);

        getEvents();
    }

    private void getEvents() {
        // TODO: add trips

        // sample data to verify recycler view
        allEvents.add(new Event("ferry ride", "Statue of Liberty"));
        allEvents.add(new Event("shopping spree", "5th Avenue"));
        adapter.notifyDataSetChanged();
    }
}