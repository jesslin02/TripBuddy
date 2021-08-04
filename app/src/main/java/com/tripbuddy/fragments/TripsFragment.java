package com.tripbuddy.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.tripbuddy.CreateTripActivity;
import com.tripbuddy.MainActivity;
import com.tripbuddy.R;
import com.tripbuddy.Utils;
import com.tripbuddy.callbacks.SwipeToDeleteCallback;
import com.tripbuddy.TripsAdapter;
import com.tripbuddy.callbacks.SwipeToEditCallback;
import com.tripbuddy.models.Event;
import com.tripbuddy.models.Trip;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TripsFragment#//newInstance} factory method to
 * create an instance of this fragment.
 */
public class TripsFragment extends Fragment {
    public static final String TAG = "TripsFragment";

    RecyclerView rvTrips;
    LinearLayoutManager llManager;
    TripsAdapter adapter;
    List<Trip> allTrips;
    MenuItem addTrip;
    MenuItem search;
    MenuItem filter;
    MainActivity mainActivity;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    boolean ascending;

    public TripsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trips, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        sharedPref = mainActivity.getSharedPreferences("Notifs", MODE_PRIVATE);
        editor = sharedPref.edit();
        rvTrips = view.findViewById(R.id.rvTrips);
        allTrips = new ArrayList<>();
        adapter = new TripsAdapter(getContext(), allTrips);
        ascending = true;

        rvTrips.setAdapter(adapter);
        llManager = new LinearLayoutManager(getContext());
        rvTrips.setLayoutManager(llManager);
        ItemTouchHelper deleteHelper = new ItemTouchHelper(new SwipeToDeleteCallback(adapter));
        deleteHelper.attachToRecyclerView(rvTrips);
        ItemTouchHelper editHelper = new ItemTouchHelper(new SwipeToEditCallback(adapter));
        editHelper.attachToRecyclerView(rvTrips);

        getTrips();
    }

    @Override
    public void onResume() {
        super.onResume();
        getTrips();
    }

    public void getTrips() {
        ParseQuery<Trip> query = ParseQuery.getQuery(Trip.class);
        // include data referred by user key
        query.include(Trip.KEY_USER);
        query.whereEqualTo(Trip.KEY_USER, ParseUser.getCurrentUser());
        // query.orderByAscending(Trip.KEY_START);
        query.setLimit(10);
        query.findInBackground(new FindCallback<Trip>() {
            @Override
            public void done(List<Trip> trips, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with retrieving trips", e);
                    return;
                }

                // for debugging purposes let's print every trip title to logcat
                for (Trip trip : trips) {
                    Log.i(TAG, "Trip: " + trip.getTitle() + ", location: " + trip.getDestination());
                    addNotifs(trip);
                }

                sortTrips(trips);

                // save received posts to list and notify adapter of new data
                allTrips.clear();
                allTrips.addAll(trips);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void sortTrips(List<Trip> trips) {
        if (trips.size() <= 1) {
            return;
        }
        int midpoint = trips.size() / 2;
        List<Trip> front = new ArrayList<>(trips.subList(0, midpoint));
        List<Trip> back = new ArrayList<>(trips.subList(midpoint, trips.size()));

        sortTrips(front);
        sortTrips(back);
        mergeTrips(front, back, trips);
    }

    private void mergeTrips(List<Trip> front, List<Trip> back, List<Trip> trips) {
        int i = 0;
        int j = 0;
        int k = 0;
        while (i < front.size() && j < back.size()) {
            Trip frontTrip = front.get(i);
            Trip backTrip = back.get(j);
            boolean compare;
            if (ascending) {
                compare = frontTrip.compareTo(backTrip) < 0;
            } else {
                compare = frontTrip.compareTo(backTrip) >= 0;
            }
            if (compare) {
                trips.set(k, frontTrip);
                i++;
            } else {
                trips.set(k, backTrip);
                j++;
            }
            k++;
        }

        while (i < front.size()) {
            trips.set(k, front.get(i));
            i++;
            k++;
        }

        while (j < back.size()) {
            trips.set(k, back.get(j));
            j++;
            k++;
        }
    }

    private void addNotifs(Trip trip) {
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
                    Log.e(TAG, "Issue with retrieving events for setting alarms", e);
                    return;
                }

                // for debugging purposes let's print every trip title to logcat
                for (Event event : events) {
                    String id = event.getId();
                    if (sharedPref.getBoolean(id, false)) {
                        Utils.createNotif(mainActivity, event);
                        editor.putBoolean(id, true);
                    }
                    Log.i(TAG, "set notif for event: " + event.getTitle()
                            + ", location: " + event.getLocation());
                }
                editor.commit();
            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        Log.d(TAG, "onPrepareOptionsMenu");
        addTrip = menu.findItem(R.id.add);
        addTrip.setVisible(true);
        addTrip.getIcon().setTint(Color.WHITE);

        search = menu.findItem(R.id.action_search);
        search.setVisible(true);
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
            getTrips();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onAddButton() {
        Intent i = new Intent(mainActivity, CreateTripActivity.class);
        i.putExtra("edit", false);
        startActivity(i);
    }
}