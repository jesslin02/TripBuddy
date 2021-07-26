package com.tripbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
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
    MenuItem addEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary);

        trip = Parcels.unwrap(getIntent().getParcelableExtra(Trip.class.getSimpleName()));
        Log.d(TAG, String.format("Showing itinerary for '%s'", trip.getTitle()));

        rvEvents = findViewById(R.id.rvEvents);
        allEvents = new ArrayList<>();
        adapter = new ItineraryAdapter(this, allEvents, trip);

        rvEvents.setAdapter(adapter);
        llManager = new LinearLayoutManager(this);
        rvEvents.setLayoutManager(llManager);
        ItemTouchHelper itHelper = new ItemTouchHelper(new SwipeToDeleteCallback(adapter));
        itHelper.attachToRecyclerView(rvEvents);

        getEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getEvents();
    }

    private void getEvents() {
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        // include data referred by user key
        query.include(Event.KEY_USER);
        query.include(Event.KEY_TRIP);
        query.whereEqualTo(Event.KEY_USER, ParseUser.getCurrentUser());
        query.whereEqualTo(Event.KEY_TRIP, trip);
        query.orderByAscending(Event.KEY_START);
        query.setLimit(10);
        query.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> events, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with retrieving trips", e);
                    return;
                }

                // for debugging purposes let's print every trip title to logcat
                for (Event event : events) {
                    Log.i(TAG, "Event: " + event.getTitle() + ", Location: " + event.getLocation());
                }

                // save received posts to list and notify adapter of new data
                allEvents.clear();
                allEvents.addAll(events);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(@NonNull Menu menu) {
        Log.d(TAG, "onPrepareOptionsMenu");
        addEvent = menu.findItem(R.id.add);
        addEvent.getIcon().setTint(Color.WHITE);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected");
        int id = item.getItemId();
        if (id == R.id.add) {
            onAddButton();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onAddButton() {
        Intent i = new Intent(this, CreateEventActivity.class);
        i.putExtra(Trip.class.getSimpleName(), Parcels.wrap(trip));
        i.putExtra("edit", false);
        startActivity(i);
    }
}