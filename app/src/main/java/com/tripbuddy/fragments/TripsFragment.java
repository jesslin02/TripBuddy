package com.tripbuddy.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import com.tripbuddy.TripsAdapter;
import com.tripbuddy.models.Event;
import com.tripbuddy.models.Trip;

import java.util.ArrayList;
import java.util.List;

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
    MainActivity mainActivity;

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
        rvTrips = view.findViewById(R.id.rvTrips);
        allTrips = new ArrayList<>();
        adapter = new TripsAdapter(getContext(), allTrips);

        rvTrips.setAdapter(adapter);
        llManager = new LinearLayoutManager(getContext());
        rvTrips.setLayoutManager(llManager);

        getTrips();
    }

    public void getTrips() {
        ParseQuery<Trip> query = ParseQuery.getQuery(Trip.class);
        // include data referred by user key
        query.include(Trip.KEY_USER);
        query.whereEqualTo(Trip.KEY_USER, ParseUser.getCurrentUser());
        query.orderByAscending(Trip.KEY_START);
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
                }

                // save received posts to list and notify adapter of new data
                allTrips.clear();
                allTrips.addAll(trips);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        Log.d(TAG, "onPrepareOptionsMenu");
        addTrip = menu.findItem(R.id.add);
        addTrip.setVisible(true);
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
        Intent i = new Intent(mainActivity, CreateTripActivity.class);
        i.putExtra("edit", false);
        startActivity(i);
    }
}