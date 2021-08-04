package com.tripbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
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
import com.tripbuddy.callbacks.SwipeToDeleteCallback;
import com.tripbuddy.callbacks.SwipeToEditCallback;
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
    MenuItem search;
    MenuItem filter;
    boolean ascending;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary);

        trip = Parcels.unwrap(getIntent().getParcelableExtra(Trip.class.getSimpleName()));
        Log.d(TAG, String.format("Showing itinerary for '%s'", trip.getTitle()));

        rvEvents = findViewById(R.id.rvEvents);
        allEvents = new ArrayList<>();
        adapter = new ItineraryAdapter(this, allEvents, trip);
        ascending = true;

        rvEvents.setAdapter(adapter);
        llManager = new LinearLayoutManager(this);
        rvEvents.setLayoutManager(llManager);
        ItemTouchHelper deleteHelper = new ItemTouchHelper(new SwipeToDeleteCallback(adapter));
        deleteHelper.attachToRecyclerView(rvEvents);
        ItemTouchHelper editHelper = new ItemTouchHelper(new SwipeToEditCallback(adapter));
        editHelper.attachToRecyclerView(rvEvents);

        getSupportActionBar().setTitle(trip.getTitle());
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
        // query.orderByAscending(Event.KEY_START);
        query.setLimit(10);
        query.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> events, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with retrieving events", e);
                    return;
                }

                // for debugging purposes let's print every trip title to logcat
                for (Event event : events) {
                    Log.i(TAG, "Event: " + event.getTitle() + ", Location: " + event.getLocation());
                }

                sortEvents(events);

                // save received posts to list and notify adapter of new data
                allEvents.clear();
                allEvents.addAll(events);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void sortEvents(List<Event> events) {
        if (events.size() <= 1) {
            return;
        }
        int midpoint = events.size() / 2;
        List<Event> front = new ArrayList<>(events.subList(0, midpoint));
        List<Event> back = new ArrayList<>(events.subList(midpoint, events.size()));

        sortEvents(front);
        sortEvents(back);
        mergeEvents(front, back, events);
    }

    private void mergeEvents(List<Event> front, List<Event> back, List<Event> events) {
        int i = 0;
        int j = 0;
        int k = 0;
        while (i < front.size() && j < back.size()) {
            Event frontEvent = front.get(i);
            Event backEvent = back.get(j);
            boolean compare;
            if (ascending) {
                compare = frontEvent.compareTo(backEvent) < 0;
            } else {
                compare = frontEvent.compareTo(backEvent) >= 0;
            }
            if (compare) {
                events.set(k, frontEvent);
                i++;
            } else {
                events.set(k, backEvent);
                j++;
            }
            k++;
        }
        // in case front and back lists are not the same size
        while (i < front.size()) {
            events.set(k, front.get(i));
            i++;
            k++;
        }

        while (j < back.size()) {
            events.set(k, back.get(j));
            j++;
            k++;
        }
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

        search = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        filter = menu.findItem(R.id.filter);
        filter.getIcon().setTint(Color.WHITE);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected");
        int id = item.getItemId();
        if (id == R.id.add) {
            onAddButton();
            return true;
        } else if (id == R.id.filter) {
            ascending = !ascending;
            getEvents();
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