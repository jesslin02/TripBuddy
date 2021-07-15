package com.tripbuddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.parse.ParseUser;
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
        Event statue = new Event();
        statue.setTitle("ferry ride");
        statue.setLocation("Statue of Liberty");
        statue.setUser(ParseUser.getCurrentUser());
        statue.setTrip(trip);
        statue.setStart(2021, 6, 2, 8, 0);
        statue.setEnd(2021, 6, 2, 11, 0);
        allEvents.add(statue);
        Event shop = new Event();
        shop.setTitle("shopping spree");
        shop.setLocation("5th Avenue");
        shop.setUser(ParseUser.getCurrentUser());
        shop.setTrip(trip);
        shop.setStart(2021, 6, 2, 15, 0);
        shop.setEnd(2021, 6, 2, 19, 0);
        allEvents.add(shop);
        adapter.notifyDataSetChanged();
    }
}