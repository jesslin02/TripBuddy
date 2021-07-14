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
import com.tripbuddy.models.Trip;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TripsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TripsFragment extends Fragment {
    public static final String TAG = "TripsFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView rvTrips;
    LinearLayoutManager llManager;
    TripsAdapter adapter;
    List<Trip> allTrips;
    MenuItem addTrip;
    MainActivity mainActivity;

    public TripsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TripsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TripsFragment newInstance(String param1, String param2) {
        TripsFragment fragment = new TripsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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

    private void getTrips() {
        ParseQuery<Trip> query = ParseQuery.getQuery(Trip.class);
        // include data referred by user key
        query.include(Trip.KEY_USER);
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
        startActivity(i);
    }
}