package com.tripbuddy.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.skydoves.transformationlayout.TransformationCompat;
import com.skydoves.transformationlayout.TransformationLayout;
import com.tripbuddy.Adapter;
import com.tripbuddy.CreateTripActivity;
import com.tripbuddy.MainActivity;
import com.tripbuddy.R;
import com.tripbuddy.TripDetailActivity;
import com.tripbuddy.models.Trip;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class TripsAdapter extends RecyclerView.Adapter<TripsAdapter.ViewHolder> implements Adapter, Filterable {
    public static final String TAG = "TripsAdapter";
    Context context;
    List<Trip> trips;
    List<Trip> tripsFiltered;

    public TripsAdapter(Context c, List<Trip> t) {
        this.context = c;
        this.trips = t;
        this.tripsFiltered = t;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_trip, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripsAdapter.ViewHolder holder, int position) {
        Trip trip = tripsFiltered.get(position);
        holder.bind(trip);
    }

    @Override
    public int getItemCount() {
        return this.tripsFiltered.size();
    }

    @Override
    public void editItem(int position) {
        Intent i = new Intent(context, CreateTripActivity.class);
        Trip trip = tripsFiltered.get(position);
        i.putExtra(Trip.class.getSimpleName(), Parcels.wrap(trip));
        i.putExtra("edit", true);
        context.startActivity(i);
    }

    @Override
    public void deleteItem(int position) {
        Log.i(TAG, "deleteItem at position " + String.valueOf(position));
        Trip recentlyDeletedTrip = tripsFiltered.get(position);
        tripsFiltered.remove(position);
        notifyItemRemoved(position);
        showUndoSnackbar(recentlyDeletedTrip, position);
    }

    private void showUndoSnackbar(Trip deleted, int deletedPos) {
        View view = ((MainActivity) context).findViewById(R.id.coordinator_layout);
        String sbMessage = deleted.getTitle() + " was removed";
        Snackbar sb = Snackbar.make(view, sbMessage, Snackbar.LENGTH_LONG);
        sb.setAnchorView(((MainActivity) context).findViewById(R.id.bottom_navigation));
        sb.setAction(R.string.undo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undoDelete(deleted, deletedPos);
            }
        });
        sb.show();

        sb.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                if (event != BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_ACTION) {
                    deleted.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.e(TAG, "Error deleting event: " + deleted.getTitle(), e);
                                Toast.makeText(context, "Error deleting event!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Log.d(TAG, "recently deleted event: " + deleted.getTitle());
                        }
                    });
                }
                super.onDismissed(transientBottomBar, event);
            }
        });
    }

    private void undoDelete(Trip deleted, int deletedPos) {
        tripsFiltered.add(deletedPos, deleted);
        notifyItemInserted(deletedPos);
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString().toLowerCase();
                if (charString.isEmpty()) {
                    tripsFiltered = trips;
                } else {
                    List<Trip> filteredList = new ArrayList<>();
                    for (Trip trip : trips) {
                        Log.i(TAG, "performFiltering on event " + trip.getTitle()
                                + " with query " + constraint);

                        // name match condition. we are looking for title or location match
                        if (trip.getTitle().toLowerCase().contains(charString)
                                || trip.getDestination().toLowerCase().contains(charString)
                                || trip.getStart().toLowerCase().contains(charString)) {
                            filteredList.add(trip);
                        }
                    }

                    tripsFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = tripsFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                tripsFiltered = (ArrayList<Trip>) results.values;
                notifyDataSetChanged();
            }
        };
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvTitle;
        TextView tvDestination;
        TextView tvDate;
        TransformationLayout transLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDestination = itemView.findViewById(R.id.tvDestination);
            tvDate = itemView.findViewById(R.id.tvDate);
            transLayout = itemView.findViewById(R.id.transLayout);

            itemView.setOnClickListener(this);
        }

        public void bind(Trip trip) {
            tvTitle.setText(trip.getTitle());
            tvDestination.setText(trip.getDestination());
            String fullDate = trip.getStart() + " - " + trip.getEnd();
            tvDate.setText(fullDate);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Log.i(TAG, "item clicked at position " + position);
            if (position != RecyclerView.NO_POSITION) {
                Trip selected = tripsFiltered.get(position);
                Intent i = new Intent(context, TripDetailActivity.class);
                i.putExtra(Trip.class.getSimpleName(), Parcels.wrap(selected));
                TransformationCompat.startActivity(transLayout, i);
            }
        }
    }

}
